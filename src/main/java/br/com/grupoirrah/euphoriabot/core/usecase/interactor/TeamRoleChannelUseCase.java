package br.com.grupoirrah.euphoriabot.core.usecase.interactor;

import br.com.grupoirrah.euphoriabot.core.gateway.TeamRoleChannelGateway;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TeamRoleChannelUseCase {

    private final TeamRoleChannelGateway teamRoleChannelGateway;

    public void configureTeamRoleChannel(GuildJoinEvent event) {
        teamRoleChannelGateway.configureTeamRoleChannel(event);
    }

}
