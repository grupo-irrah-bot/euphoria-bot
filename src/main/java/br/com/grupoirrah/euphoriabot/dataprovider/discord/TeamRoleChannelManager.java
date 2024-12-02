package br.com.grupoirrah.euphoriabot.dataprovider.discord;

import br.com.grupoirrah.euphoriabot.config.DiscordConfig;
import br.com.grupoirrah.euphoriabot.core.gateway.TeamRoleChannelGateway;
import br.com.grupoirrah.euphoriabot.core.util.LogUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class TeamRoleChannelManager extends ListenerAdapter implements TeamRoleChannelGateway {

    private final DiscordConfig discordConfig;
    private final Map<String, String> teamEmojiToRoleMap;
    private final Map<String, String> roleEmojiToRoleMap;

    @Override
    public void configureTeamRoleChannel(GuildJoinEvent event) {
        TextChannel channel = event.getGuild().getTextChannelById(discordConfig.getTeamRoleChannelId());

        if (channel != null) {
            Role everyoneRole = event.getGuild().getPublicRole();
            Role irradianteRole = event.getGuild().getRoleById(discordConfig.getRoleIrradianteId());

            if (irradianteRole == null) {
                LogUtil.logError(log, "Role 'irradiante' não encontrada");
                return;
            }

            channel.getPermissionContainer()
                .upsertPermissionOverride(everyoneRole)
                .deny(Permission.VIEW_CHANNEL, Permission.MESSAGE_SEND)
                .queue();

            channel.getPermissionContainer()
                .upsertPermissionOverride(irradianteRole)
                .grant(Permission.VIEW_CHANNEL)
                .deny(Permission.MESSAGE_SEND)
                .queue();

            sendTeamReactions(channel);
            channel.sendMessage("⠀").queue();
            sendRoleReactions(channel);
        } else {
            LogUtil.logError(log, "Canal de times e funções não encontrado com o ID: {}",
                discordConfig.getTeamRoleChannelId());
        }
    }

    public void sendTeamReactions(TextChannel channel) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("🏆 Escolha seu Time");
        embed.setDescription("""
                Reaja com os emojis abaixo para selecionar/desselecionar os times:
                
                🟢 - Z-API
                🟠 - KIGI
                🔵 - DISPARA AI
                🟣 - PLUG CHAT
                ⚪ - GPT-MARKER
                📣 - MARKETING
                📊 - FINANCEIRO
                💼 - RH
                """);
        embed.setColor(new Color(0xFFD700));
        embed.setFooter("Você pode reagir com mais de um emoji para escolher múltiplos times.", null);

        channel.sendMessageEmbeds(embed.build()).queue(message -> {
            addReactionAndMap(message, "🟢", discordConfig.getTeams().get("z-api"), teamEmojiToRoleMap);
            addReactionAndMap(message, "🟠", discordConfig.getTeams().get("kigi"), teamEmojiToRoleMap);
            addReactionAndMap(message, "🔵", discordConfig.getTeams().get("dispara-ai"), teamEmojiToRoleMap);
            addReactionAndMap(message, "🟣", discordConfig.getTeams().get("plug-chat"), teamEmojiToRoleMap);
            addReactionAndMap(message, "⚪", discordConfig.getTeams().get("gpt-marker"), teamEmojiToRoleMap);
            addReactionAndMap(message, "📣", discordConfig.getTeams().get("marketing"), teamEmojiToRoleMap);
            addReactionAndMap(message, "📊", discordConfig.getTeams().get("financeiro"), teamEmojiToRoleMap);
            addReactionAndMap(message, "💼", discordConfig.getTeams().get("rh-people"), teamEmojiToRoleMap);
        });
    }

    public void sendRoleReactions(TextChannel channel) {
        EmbedBuilder rolesEmbed = new EmbedBuilder();
        rolesEmbed.setTitle("📋 Escolha sua Função");
        rolesEmbed.setDescription("""
                Reaja com os emojis abaixo para selecionar/desselecionar as funções:
                
                💬 - CUSTOMER EXPERIENCE
                🎯 - PRODUTO
                💻 - DEVELOPER
                🎧 - SUPORTE
                """);
        rolesEmbed.setColor(new Color(0x00BFFF));
        rolesEmbed.setFooter("Você pode reagir com mais de um emoji para escolher múltiplas funções!",
            null);

        channel.sendMessageEmbeds(rolesEmbed.build()).queue(message -> {
            addReactionAndMap(message, "💬", discordConfig.getRoles().get("customer-experience"),
                roleEmojiToRoleMap);

            addReactionAndMap(message, "🎯", discordConfig.getRoles().get("product"), roleEmojiToRoleMap);
            addReactionAndMap(message, "💻", discordConfig.getRoles().get("developer"), roleEmojiToRoleMap);
            addReactionAndMap(message, "🎧", discordConfig.getRoles().get("support"), roleEmojiToRoleMap);
        });
    }

    private void addReactionAndMap(Message message, String emoji, String roleId, Map<String, String> map) {
        message.addReaction(Emoji.fromUnicode(emoji)).queue();
        map.put(emoji, roleId);
    }

}
