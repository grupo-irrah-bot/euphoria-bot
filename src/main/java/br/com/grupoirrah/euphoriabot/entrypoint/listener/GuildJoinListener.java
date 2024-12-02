package br.com.grupoirrah.euphoriabot.entrypoint.listener;

import br.com.grupoirrah.euphoriabot.core.gateway.RolePermissionGateway;
import br.com.grupoirrah.euphoriabot.core.usecase.interactor.TeamRoleChannelUseCase;
import br.com.grupoirrah.euphoriabot.core.usecase.interactor.VerificationChannelUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class GuildJoinListener extends ListenerAdapter {

    private final VerificationChannelUseCase verificationChannelUseCase;
    private final TeamRoleChannelUseCase teamRoleChannelUseCase;
    private final RolePermissionGateway rolePermissionGateway;

    @Override
    public void onGuildJoin(GuildJoinEvent event) {
        verificationChannelUseCase.configureVerificationChannel(event);
        teamRoleChannelUseCase.configureTeamRoleChannel(event);
        rolePermissionGateway.updatePermissionsForCategoryRoleMapping(event.getGuild());
    }

}
