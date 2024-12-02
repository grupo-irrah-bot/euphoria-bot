package br.com.grupoirrah.euphoriabot.dataprovider.discord;

import br.com.grupoirrah.euphoriabot.config.DiscordConfig;
import br.com.grupoirrah.euphoriabot.core.gateway.UserButtonInteractionGateway;
import br.com.grupoirrah.euphoriabot.core.usecase.interactor.HookContext;
import br.com.grupoirrah.euphoriabot.core.util.LogUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserButtonInteraction implements UserButtonInteractionGateway {

    private final ConcurrentHashMap<String, InteractionHook> interactionCache = new ConcurrentHashMap<>();

    private final DiscordConfig discordConfig;

    @Override
    public void execute(ButtonInteractionEvent event) {
        event.deferReply(true).queue();

        String buttonId = event.getButton().getId();

        if ("activate-account".equals(buttonId)) {
            handleAccountActivation(event);
        } else {
            LogUtil.logWarn(log, "ID do botão desconhecido: '{}'", buttonId);

            event.getHook().sendMessage("Ação desconhecida. Entre em contato com o suporte.")
                .setEphemeral(true).queue();
        }
    }

    @Override
    public Mono<Optional<HookContext>> removeInteraction(String interactionId) {
        InteractionHook hook = interactionCache.remove(interactionId);
        if (hook != null) {
            HookContext hookContext = new HookContext(hook);
            return Mono.just(Optional.of(hookContext));
        }
        return Mono.just(Optional.empty());
    }

    private void handleAccountActivation(ButtonInteractionEvent event) {
        try {
            Long guildId = Objects.requireNonNull(event.getGuild()).getIdLong();
            event.deferReply(true).queue();

            InteractionHook hook = event.getHook();
            interactionCache.put(event.getId(), hook);

            String stateJson = new ObjectMapper().writeValueAsString(Map.of(
                "guildId", guildId,
                "interactionId", event.getId()
            ));

            String redirectUrl = "https://discord.com/oauth2/authorize"
                + "?client_id=" + discordConfig.getClientId()
                + "&redirect_uri=" + URLEncoder.encode(discordConfig.getRedirectUri(), StandardCharsets.UTF_8)
                + "&response_type=code"
                + "&scope=email%20identify"
                + "&state=" + URLEncoder.encode(stateJson, StandardCharsets.UTF_8);

            hook.sendMessage("Por favor, clique no link abaixo para validar seu e-mail:")
                .addActionRow(Button.link(redirectUrl, "Validar e-mail").withEmoji(Emoji.fromUnicode("✉️")))
                .setEphemeral(true)
                .queue();

        } catch (Exception e) {
            LogUtil.logException(log, "Erro ao gerar o link de ativação: ", e);
            event.getHook().sendMessage("❌ Ocorreu um erro ao gerar o link de ativação.")
                .setEphemeral(true)
                .queue();
        }
    }

}
