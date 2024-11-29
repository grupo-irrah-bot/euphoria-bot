package br.com.grupoirrah.euphoriabot.dataprovider.discord;

import br.com.grupoirrah.euphoriabot.core.gateway.TeamRoleChannelGateway;
import br.com.grupoirrah.euphoriabot.core.util.LogUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.awt.*;

@Component
public class TeamRoleChannelManager implements TeamRoleChannelGateway {

    private static final Logger logger = LoggerFactory.getLogger(TeamRoleChannelManager.class);

    @Value("${channel.teams-roles.id}")
    private String teamRoleChannelId;

    @Override
    public void configureTeamRoleChannel(GuildJoinEvent event) {
        TextChannel channel = event.getGuild().getTextChannelById(teamRoleChannelId);

        if (channel != null) {
            Role everyoneRole = event.getGuild().getPublicRole();
            Role irradianteRole = event.getGuild().getRolesByName("irradiante", true).stream()
                .findFirst().orElse(null);

            if (irradianteRole == null) {
                LogUtil.logError(logger, "Role 'irradiante' não encontrada");
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

            EmbedBuilder teamsEmbed = new EmbedBuilder();
            teamsEmbed.setTitle("🏆 Escolha seu Time");
            teamsEmbed.setColor(new Color(0xFFD700));
            teamsEmbed.setFooter("Você pode selecionar mais de um time!", null);

            StringSelectMenu teamsMenu = StringSelectMenu.create("select-teams")
                .setPlaceholder("Selecione seus times...")
                .setMinValues(1)
                .addOption("Z-API", "z-api")
                .addOption("KIGI", "kigi")
                .addOption("DISPARA AI", "dispara-ai-team")
                .addOption("PLUG CHAT", "plug-chat-team")
                .addOption("GPT-MARKER", "gpt-marker-team")
                .addOption("MARKETING", "marketing-team")
                .addOption("FINANCEIRO", "financeiro-team")
                .addOption("RH & PEOPLE", "rh-people-team")
                .build();

            channel.sendMessageEmbeds(teamsEmbed.build())
                .setActionRow(teamsMenu)
                .queue();

            EmbedBuilder rolesEmbed = new EmbedBuilder();
            rolesEmbed.setTitle("🎭 Escolha sua Função");
            rolesEmbed.setColor(new Color(0x00BFFF));
            rolesEmbed.setFooter("Você pode selecionar mais de uma função!", null);

            StringSelectMenu rolesMenu = StringSelectMenu.create("select-roles")
                .setPlaceholder("Selecione suas funções...")
                .setMinValues(1)
                .addOption("CUSTOMER EXPERIENCE", "customer-experience-role")
                .addOption("PRODUTO", "product-role")
                .addOption("DEVELOPER", "developer-role")
                .addOption("SUPORTE", "support-role")
                .build();

            channel.sendMessageEmbeds(rolesEmbed.build())
                .setActionRow(rolesMenu)
                .queue();
        } else {
            LogUtil.logError(logger, "Canal de times e funções não encontrado com o ID: {}",
                teamRoleChannelId);
        }
    }

}
