package br.com.grupoirrah.euphoriabot.core.usecase.interactor;

import br.com.grupoirrah.euphoriabot.core.gateway.VerificationChannelGateway;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VerificationChannelUseCase {

    private final VerificationChannelGateway verificationChannelGateway;

    public void configureVerificationChannel(Guild guild) {
        verificationChannelGateway.configureVerificationChannel(guild);
    }
    
}
