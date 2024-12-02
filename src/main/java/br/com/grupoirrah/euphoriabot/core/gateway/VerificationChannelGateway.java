package br.com.grupoirrah.euphoriabot.core.gateway;

import net.dv8tion.jda.api.entities.Guild;

public interface VerificationChannelGateway {

    void configureVerificationChannel(Guild guild);

}
