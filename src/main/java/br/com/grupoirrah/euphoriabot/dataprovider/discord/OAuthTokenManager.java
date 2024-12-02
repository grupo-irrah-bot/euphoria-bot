package br.com.grupoirrah.euphoriabot.dataprovider.discord;

import br.com.grupoirrah.euphoriabot.config.DiscordConfig;
import br.com.grupoirrah.euphoriabot.core.domain.exception.AuthProcessingException;
import br.com.grupoirrah.euphoriabot.core.gateway.OAuthTokenGateway;
import br.com.grupoirrah.euphoriabot.core.usecase.boundary.output.AuthStateOutput;
import br.com.grupoirrah.euphoriabot.core.util.LogUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuthTokenManager implements OAuthTokenGateway {

    private final WebClient.Builder webClientBuilder;
    private final ObjectMapper objectMapper;
    private final DiscordConfig discordConfig;

    @Override
    public Mono<String> retrieveAccessToken(String code) {
        return webClientBuilder.baseUrl("https://discord.com/api").build()
            .post()
            .uri("/oauth2/token")
            .header("Content-Type", "application/x-www-form-urlencoded")
            .bodyValue(buildTokenRequestBody(code))
            .retrieve()
            .bodyToMono(String.class)
            .map(this::extractAccessToken)
            .doOnSuccess(token -> LogUtil.logInfo(log, "Token OAuth recebido com sucesso."))
            .doOnError(e -> LogUtil.logError(log, "Erro ao recuperar token OAuth.", e));
    }

    @Override
    public AuthStateOutput parseState(String state) {
        try {
            JsonNode node = objectMapper.readTree(state);
            return new AuthStateOutput(
                node.get("guildId").asText(),
                node.get("interactionId").asText()
            );
        } catch (Exception e) {
            String errorMessage = "Erro ao processar o estado OAuth: " + state;
            LogUtil.logException(log, errorMessage, e);
            throw new AuthProcessingException(errorMessage, e);
        }
    }

    private String extractAccessToken(String tokenResponse) {
        try {
            JsonNode node = objectMapper.readTree(tokenResponse);
            return node.get("access_token").asText();
        } catch (Exception e) {
            String errorMessage = "Erro ao processar a resposta do token OAuth: " + tokenResponse;
            LogUtil.logException(log, errorMessage, e);
            throw new AuthProcessingException(errorMessage, e);
        }
    }

    private String buildTokenRequestBody(String code) {
        return "client_id=" + discordConfig.getClientId() +
            "&client_secret=" + discordConfig.getClientSecret() +
            "&grant_type=authorization_code" +
            "&code=" + code +
            "&redirect_uri=" + discordConfig.getRedirectUri();
    }

}
