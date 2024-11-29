package br.com.grupoirrah.euphoriabot.entrypoint.listener;

import br.com.grupoirrah.euphoriabot.core.usecase.interactor.TeamRoleUseCase;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TeamRoleSelectionListener extends ListenerAdapter {

    private final TeamRoleUseCase teamRoleUseCase;

    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event) {
        String componentId = event.getComponentId();

        if (componentId.equals("select-teams") || componentId.equals("select-roles")) {
            handleSelection(event);
        }
    }


    private void handleSelection(StringSelectInteractionEvent event) {
        List<String> selectedIds = event.getValues();

        if (selectedIds.isEmpty()) {
            event.reply("Você precisa selecionar pelo menos uma opção!").setEphemeral(true).queue();
            return;
        }

        var assignedRoles = teamRoleUseCase.assignRolesToMember(event.getGuild(), event.getMember(), selectedIds);
        String response = teamRoleUseCase.generateSuccessMessage(assignedRoles);

        event.reply(response).setEphemeral(true).queue();
    }

}
