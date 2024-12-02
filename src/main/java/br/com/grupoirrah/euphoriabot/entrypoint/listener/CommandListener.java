package br.com.grupoirrah.euphoriabot.entrypoint.listener;

import br.com.grupoirrah.euphoriabot.core.usecase.interactor.RolePermissionUseCase;
import br.com.grupoirrah.euphoriabot.core.usecase.interactor.TeamRoleChannelUseCase;
import br.com.grupoirrah.euphoriabot.core.usecase.interactor.VerificationChannelUseCase;
import br.com.grupoirrah.euphoriabot.core.util.LogUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommandListener extends ListenerAdapter {

    private final VerificationChannelUseCase verificationChannelUseCase;
    private final TeamRoleChannelUseCase teamRoleChannelUseCase;
    private final RolePermissionUseCase rolePermissionUseCase;

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        String message = event.getMessage().getContentRaw();
        if (message.equalsIgnoreCase("!ativarBotEuphoria")) {
            if (!event.getMember().isOwner()) {
                event.getChannel().sendMessage("Apenas o administrador do servidor pode ativar o bot.").queue();
                return;
            }

            event.getChannel().sendMessage("🔧 Configurando o bot...").queue();

            try {
                verificationChannelUseCase.configureVerificationChannel(event.getGuild());
                teamRoleChannelUseCase.configureTeamRoleChannel(event.getGuild());
                rolePermissionUseCase.removeRolesBelowBot(event.getGuild());
                rolePermissionUseCase.updatePermissionsForCategoryRoleMapping(event.getGuild());

                event.getChannel().sendMessage("✅ Bot ativado com sucesso! Configurações aplicadas.").queue();
            } catch (Exception e) {
                LogUtil.logException(log, String.format("Erro ao ativar o bot no servidor %s",
                        event.getGuild().getName()), e);

                event.getChannel().sendMessage("❌ Erro ao ativar o bot. Verifique os logs para " +
                        "mais detalhes.").queue();
            }
        }
    }

}
