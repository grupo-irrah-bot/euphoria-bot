package br.com.grupoirrah.euphoriabot.core.usecase.interactor;

import br.com.grupoirrah.euphoriabot.core.gateway.*;
import br.com.grupoirrah.euphoriabot.core.usecase.boundary.output.AuthStateOutput;
import br.com.grupoirrah.euphoriabot.core.usecase.boundary.output.UserProviderOutput;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthUseCase {

    private final UserButtonInteractionGateway userButtonInteractionGateway;
    private final OAuthTokenGateway oAuthTokenGateway;
    private final UserGateway userGateway;
    private final GuildGateway guildGateway;
    private final MailboxValidationProviderGateway mailboxValidationProviderGateway;

    public Mono<Optional<String>> execute(String code, String state) throws Exception {
        String decodedState = URLDecoder.decode(state, StandardCharsets.UTF_8);
        AuthStateOutput authStateOutput = oAuthTokenGateway.parseState(decodedState);

        String interactionId = authStateOutput.interactionId();

        return userButtonInteractionGateway.removeInteraction(interactionId)
            .flatMap(optionalHookContext -> optionalHookContext
                .map(hookContext -> processOAuthFlow(code, authStateOutput, hookContext))
                .orElseGet(() -> Mono.just(Optional.of("❌ Interação não encontrada.")))
            );
    }

    private Mono<Optional<String>> processOAuthFlow(String code,
                                                    AuthStateOutput authStateOutput,
                                                    HookContext hookContext) {
        return oAuthTokenGateway.retrieveAccessToken(code)
            .flatMap(accessToken -> userGateway.fetchUserProvider(accessToken)
                .flatMap(userInfo -> processUserInfo(authStateOutput, userInfo, hookContext))
            );
    }

    private Mono<Optional<String>> processUserInfo(AuthStateOutput authStateOutput, 
                                                   UserProviderOutput userInfo,
                                                   HookContext hookContext) {
        return guildGateway.getGuild(authStateOutput.guildId())
            .flatMap(guild -> guildGateway.getMember(guild, userInfo.id())
                .flatMap(member -> validateEmailAndAssignRole(guild, member, userInfo, hookContext))
            )
            .switchIfEmpty(Mono.just(Optional.of("❌ Servidor ou membro não encontrado.")));
    }

    private Mono<Optional<String>> validateEmailAndAssignRole(Guild guild,
                                                              Member member,
                                                              UserProviderOutput userInfo,
                                                              HookContext hookContext) {
        String email = userInfo.email();

        if (!email.endsWith("@grupoirrah.com")) {
            String invalidEmailMessage = "❌ Apenas e-mails com o domínio **@grupoirrah.com** são permitidos.";
            return guildGateway.sendDirectMessageOrFallback(member, invalidEmailMessage, hookContext, guild);
        }

        return mailboxValidationProviderGateway.validateEmail(email)
            .flatMap(isValid -> {
                if (!isValid) {
                    String invalidEmailMessage = "Olá! Detectamos que o e-mail informado não pertence ao domínio " +
                        "do **Grupo Irrah**.";

                    return guildGateway.sendDirectMessageOrFallback(member, invalidEmailMessage, hookContext, guild);
                }

                guildGateway.assignIrradianteRoleToMember(guild, member);

                return guildGateway.sendRedirectChannelTeamSelectionMessage(hookContext, guild);
            });
    }

}
