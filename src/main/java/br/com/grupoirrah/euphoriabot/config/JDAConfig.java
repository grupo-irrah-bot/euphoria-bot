package br.com.grupoirrah.euphoriabot.config;

import br.com.grupoirrah.euphoriabot.core.domain.exception.JDAConfigurationException;
import br.com.grupoirrah.euphoriabot.core.util.LogUtil;
import br.com.grupoirrah.euphoriabot.dataprovider.discord.TeamRoleChannelManager;
import br.com.grupoirrah.euphoriabot.entrypoint.listener.ReactionListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class JDAConfig {

    private final DiscordConfig discordConfig;

    @Bean
    public Map<String, String> teamEmojiToRoleMap() {
        return new HashMap<>();
    }

    @Bean
    public Map<String, String> roleEmojiToRoleMap() {
        return new HashMap<>();
    }

    @Bean
    public JDA jda(ReactionListener reactionListener, TeamRoleChannelManager teamRoleChannelManager) {
        try {
            String botToken = discordConfig.getBotToken();
            if (botToken == null || botToken.isBlank()) {
                throw new JDAConfigurationException("O token do bot do Discord não está configurado.");
            }

            return JDABuilder.createDefault(botToken)
                .enableIntents(
                    GatewayIntent.GUILD_MEMBERS,
                    GatewayIntent.GUILD_MESSAGES,
                    GatewayIntent.MESSAGE_CONTENT
                )
                .addEventListeners(reactionListener, teamRoleChannelManager)
                .build().awaitReady();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            String errorMessage = "A inicialização do JDA foi interrompida.";
            LogUtil.logException(log, errorMessage, e);
            throw new JDAConfigurationException(errorMessage, e);
        } catch (Exception e) {
            String errorMessage = "Erro ao configurar o JDA com o token fornecido.";
            LogUtil.logException(log, errorMessage, e);
            throw new JDAConfigurationException(errorMessage, e);
        }
    }

}
