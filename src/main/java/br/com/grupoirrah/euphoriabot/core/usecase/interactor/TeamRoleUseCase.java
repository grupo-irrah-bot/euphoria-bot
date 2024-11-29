package br.com.grupoirrah.euphoriabot.core.usecase.interactor;

import br.com.grupoirrah.euphoriabot.core.gateway.TeamRoleGateway;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TeamRoleUseCase {

    private final TeamRoleGateway teamRoleGateway;

    public List<Role> assignRolesToMember(Guild guild, Member member, List<String> roleIds) {
        List<Role> roles = teamRoleGateway.findRolesByIds(guild, roleIds);
        teamRoleGateway.assignRolesToMember(guild, member, roles);
        return roles;
    }

    public String generateSuccessMessage(List<Role> assignedRoles) {
        if (assignedRoles.isEmpty()) {
            return "Nenhum cargo foi atribuído. Verifique as opções selecionadas.";
        }

        StringBuilder message = new StringBuilder("Os seguintes cargos foram atribuídos com sucesso:\n");
        assignedRoles.forEach(role -> message.append("- ").append(role.getName()).append("\n"));
        return message.toString();
    }

}
