package com.rubim.pcpBackEnd.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientOAuth2Config {

    @Bean
    WebClient webclient(ReactiveClientRegistrationRepository clientRegistration, 
                        ReactiveOAuth2AuthorizedClientService authorizedClientService) {
        ServerOAuth2AuthorizedClientExchangeFilterFunction oauth = 
                new ServerOAuth2AuthorizedClientExchangeFilterFunction(
                        new AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager(
                            clientRegistration, authorizedClientService)
                );
                oauth.setDefaultClientRegistrationId("teste");
        return WebClient.builder()
                .filter(oauth)
                .build();
    }
}
