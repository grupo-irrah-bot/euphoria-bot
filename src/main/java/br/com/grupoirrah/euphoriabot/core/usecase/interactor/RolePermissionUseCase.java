package br.com.grupoirrah.euphoriabot.core.usecase.interactor;

import br.com.grupoirrah.euphoriabot.core.gateway.RolePermissionGateway;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Guild;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RolePermissionUseCase {

    private final RolePermissionGateway rolePermissionGateway;

    public void removeRolesBelowBot(Guild guild) {
        rolePermissionGateway.removeRolesBelowBot(guild);
    }

    public void updatePermissionsForCategoryRoleMapping(Guild guild) {
        rolePermissionGateway.updatePermissionsForCategoryRoleMapping(guild);
    }

}
