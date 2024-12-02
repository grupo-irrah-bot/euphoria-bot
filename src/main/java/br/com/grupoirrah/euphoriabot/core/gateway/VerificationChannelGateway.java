package br.com.grupoirrah.euphoriabot.core.gateway;

import net.dv8tion.jda.api.events.guild.GuildJoinEvent;

public interface VerificationChannelGateway {

    void configureVerificationChannel(GuildJoinEvent event);

}
