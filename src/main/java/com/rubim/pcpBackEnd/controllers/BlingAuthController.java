package com.rubim.pcpBackEnd.controllers;

import com.rubim.pcpBackEnd.Services.BlingAuthService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Map;

@RestController
public class BlingAuthController {

    private final BlingAuthService blingAuthService;

    @Value("${bling.client-id}")
    private String clientId;

    @Value("${bling.redirect-uri}")
    private String redirectUri;

    public BlingAuthController(BlingAuthService blingAuthService) {
        this.blingAuthService = blingAuthService;
    }

    @GetMapping("/authorize")
    public Mono<String> initiateAuthorization() {
        // Gerar state único para evitar CSRF
        String state = generateState();
        
        // Construir URL de autorização
        String authorizationUrl = UriComponentsBuilder
            .fromHttpUrl("https://bling.com.br/Api/v3/oauth/authorize")
            .queryParam("response_type", "code")
            .queryParam("client_id", clientId)
            .queryParam("state", state)
            .build()
            .toUriString();

        // Salvar state (pode ser em sessão, banco ou cache para validação posterior)
        // Para simplificar, apenas retornamos a URL
        return Mono.just("Redirecione o usuário para: " + authorizationUrl);
    }

    @GetMapping("/callback")
    public Mono<String> handleCallback(@RequestParam("code") String code, @RequestParam("state") String state) {
        // Validar state (comparar com o valor salvo anteriormente)
        // Para este exemplo, apenas logamos o state
        // Em produção, valide contra o state salvo
        if (state == null || state.isEmpty()) {
            return Mono.error(new IllegalStateException("State inválido ou ausente."));
        }

        // Trocar code por tokens
        return blingAuthService.trocarCodePorToken(code)
            .map(token -> "Token salvo com sucesso: " + token.getAccessToken() + ", State: " + state);
    }

    private String generateState() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[24];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    @GetMapping("/produtos")
    public Mono<Map> getProdutos() {
        return blingAuthService.getProdutos();
    }

    public Mono<Map> getPedidos() {
        return blingAuthService.getPedidos();
    }
}