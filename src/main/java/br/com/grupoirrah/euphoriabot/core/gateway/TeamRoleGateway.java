package br.com.grupoirrah.euphoriabot.core.gateway;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

import java.util.List;

public interface TeamRoleGateway {
    List<Role> findRolesByIds(Guild guild, List<String> roleIds);
    void assignRolesToMember(Guild guild, Member member, List<Role> roles);
}
