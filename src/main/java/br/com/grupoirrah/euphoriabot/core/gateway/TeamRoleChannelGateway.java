package br.com.grupoirrah.euphoriabot.core.gateway;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;

public interface TeamRoleChannelGateway {

    void configureTeamRoleChannel(Guild guild);

}
