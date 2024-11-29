package br.com.grupoirrah.euphoriabot.dataprovider.discord;

import br.com.grupoirrah.euphoriabot.core.gateway.TeamRoleGateway;
import br.com.grupoirrah.euphoriabot.core.util.LogUtil;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TeamRoleManager implements TeamRoleGateway {

    private static final Logger logger = LoggerFactory.getLogger(TeamRoleManager.class);

    @Override
    public List<Role> findRolesByIds(Guild guild, List<String> roleIds) {
        List<Role> roles = new ArrayList<>();

        for (String roleId : roleIds) {
            Role role = guild.getRoleById(roleId);
            if (role != null) {
                roles.add(role);
            } else {
                LogUtil.logWarn(logger, "Cargo com ID '{}' não encontrado", roleId);
            }
        }

        return roles;
    }

    @Override
    public void assignRolesToMember(Guild guild, Member member, List<Role> roles) {
        for (Role role : roles) {
            guild.addRoleToMember(member, role).queue(
                success -> LogUtil.logInfo(logger, "Cargo '{}' adicionado ao membro {}",
                    role.getName(), member.getEffectiveName()),

                error -> LogUtil.logError(logger, "Erro ao adicionar cargo '{}' ao membro {}",
                    role.getName(), member.getEffectiveName())
            );
        }
    }

}
