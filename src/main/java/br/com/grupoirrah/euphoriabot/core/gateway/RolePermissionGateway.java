package br.com.grupoirrah.euphoriabot.core.gateway;

import net.dv8tion.jda.api.entities.Guild;

public interface RolePermissionGateway {

    void removeRolesBelowBot(Guild guild);
    void updatePermissionsForCategoryRoleMapping(Guild guild);

}