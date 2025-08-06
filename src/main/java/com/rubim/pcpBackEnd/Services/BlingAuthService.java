package com.rubim.pcpBackEnd.Services;
import com.rubim.pcpBackEnd.Entity.BlingTokenEntity;
import com.rubim.pcpBackEnd.repository.BlingTokenRepository;

import reactor.core.publisher.Mono;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Map;

@Service
public class BlingAuthService {

    private final WebClient webClient;
    private final BlingTokenRepository repository;

    @Value("${bling.client-id}")
    private String clientId;

    @Value("${bling.client-secret}")
    private String clientSecret;

    @Value("${bling.redirect-uri}")
    private String redirectUri;

    @Value("${bling.token-url}")
    private String tokenUrl;

    public BlingAuthService(BlingTokenRepository repository, WebClient.Builder webClientBuilder) {
        this.repository = repository;
        this.webClient = webClientBuilder.baseUrl(tokenUrl).build();
    }

    public Mono<BlingTokenEntity> trocarCodePorToken(String code) {
        String authHeader = gerarBasicAuth();
        String body = "grant_type=authorization_code" +
                "&code=" + code +
                "&redirect_uri=" + redirectUri;

        return webClient.post()
                .uri(tokenUrl)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Basic " + authHeader)
                .bodyValue(body)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> response.bodyToMono(String.class)
                                .flatMap(error -> Mono.error(new RuntimeException("Falha na requisição de token: " + error))))
                .bodyToMono(Map.class)
                .map(this::mapToBlingTokenEntity)
                .flatMap(blingToken -> Mono.fromCallable(() -> repository.save(blingToken)));
    }

    public Mono<BlingTokenEntity> renovarToken(BlingTokenEntity antigo) {
        String authHeader = gerarBasicAuth();
        String body = "grant_type=refresh_token" +
                "&refresh_token=" + antigo.getRefreshToken();

        return webClient.post()
                .uri(tokenUrl)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Basic " + authHeader)
                .bodyValue(body)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> response.bodyToMono(String.class)
                                .flatMap(error -> Mono.error(new RuntimeException("Falha na renovação de token: " + error))))
                .bodyToMono(Map.class)
                .map(this::mapToBlingTokenEntity)
                .flatMap(blingToken -> Mono.fromCallable(() -> repository.save(blingToken)));
    }

    public Mono<String> obterAccessTokenValido() {
        return Mono.justOrEmpty(repository.findTopByOrderByCreatedAtDesc())
                .switchIfEmpty(Mono.error(new IllegalStateException("Nenhum token encontrado.")))
                .flatMap(token -> {
                    LocalDateTime expiraEm = token.getCreatedAt().plusSeconds(token.getExpiresIn());
                    if (LocalDateTime.now().isAfter(expiraEm.minusMinutes(1))) {
                        return renovarToken(token).map(BlingTokenEntity::getAccessToken);
                    }
                    return Mono.just(token.getAccessToken());
                });
    }

    private BlingTokenEntity mapToBlingTokenEntity(Map<String, Object> response) {
        BlingTokenEntity token = new BlingTokenEntity();
        token.setAccessToken((String) response.get("access_token"));
        token.setRefreshToken((String) response.get("refresh_token"));
        token.setExpiresIn((Integer) response.get("expires_in"));
        token.setTokenType((String) response.get("token_type"));
        token.setCreatedAt(LocalDateTime.now());
        return token;
    }

    private String gerarBasicAuth() {
        if (clientId == null || clientSecret == null) {
            throw new IllegalStateException("clientId ou clientSecret não configurados.");
        }
        String credenciais = clientId + ":" + clientSecret;
        return Base64.getEncoder().encodeToString(credenciais.getBytes(StandardCharsets.UTF_8));
    }

    public Mono<Map> getProdutos() {
    return obterAccessTokenValido()
        .flatMap(token -> webClient.get()
            .uri("https://api.bling.com.br/Api/v3/produtos")
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
            .retrieve()
            .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                response -> response.bodyToMono(String.class)
                    .flatMap(error -> Mono.error(new RuntimeException("Falha na requisição à API: " + error))))
            .bodyToMono(Map.class));
    }
}