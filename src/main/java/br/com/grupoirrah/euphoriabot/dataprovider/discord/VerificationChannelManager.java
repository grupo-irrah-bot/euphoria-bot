package br.com.grupoirrah.euphoriabot.dataprovider.discord;

import br.com.grupoirrah.euphoriabot.config.DiscordConfig;
import br.com.grupoirrah.euphoriabot.core.gateway.VerificationChannelGateway;
import br.com.grupoirrah.euphoriabot.core.util.LogUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.springframework.stereotype.Component;

import java.awt.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class VerificationChannelManager implements VerificationChannelGateway {

    private final DiscordConfig discordConfig;

    @Override
    public void configureVerificationChannel(Guild guild) {
        TextChannel channel = guild.getTextChannelById(discordConfig.getVerificationChannelId());

        if (channel != null) {
            Role everyoneRole = guild.getPublicRole();

            channel.getPermissionContainer()
                .upsertPermissionOverride(everyoneRole)
                .grant(Permission.VIEW_CHANNEL)
                .deny(Permission.MESSAGE_SEND)
                .queue();

            guild.getRoles().stream()
                .filter(role -> !role.isPublicRole())
                .forEach(role -> channel.getPermissionContainer()
                    .upsertPermissionOverride(role)
                    .deny(Permission.VIEW_CHANNEL)
                    .queue());

            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle("🛡️ Ativação de Conta Necessária");
            embed.setDescription("Para garantir a segurança do servidor, precisamos que você ative sua conta " +
                "antes de começar a interagir conosco.");

            embed.addField("📢 Como ativar?", "Clique no botão abaixo para prosseguir com a " +
                "ativação da sua conta.", false);

            embed.setColor(new Color(0x5865F2));
            embed.setFooter("Estamos ansiosos para te receber na comunidade!", null);

            channel.sendMessageEmbeds(embed.build())
                .setActionRow(Button.primary("activate-account", "Ativar Conta")
                    .withEmoji(Emoji.fromUnicode("🛡️")))
                .queue();
        } else {
            LogUtil.logError(log, "Canal de verificação não encontrado com o ID: {}",
                discordConfig.getVerificationChannelId());
        }
    }

}
