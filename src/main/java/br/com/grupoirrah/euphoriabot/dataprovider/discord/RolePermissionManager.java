package br.com.grupoirrah.euphoriabot.dataprovider.discord;

import br.com.grupoirrah.euphoriabot.config.DiscordConfig;
import br.com.grupoirrah.euphoriabot.core.gateway.RolePermissionGateway;
import br.com.grupoirrah.euphoriabot.core.util.LogUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class RolePermissionManager implements RolePermissionGateway {

    private final DiscordConfig discordConfig;

    @Override
    public void removeRolesBelowBot(Guild guild) {
        try {
            Member botMember = guild.getSelfMember();

            Role botRole = botMember.getRoles().isEmpty() ? null : botMember.getRoles().get(0);
            if (botRole == null) {
                LogUtil.logWarn(log, "Bot não possui um cargo atribuído no servidor {}", guild.getName());
                return;
            }

            performRoleRemoval(guild, botRole);
        } catch (Exception e) {
            LogUtil.logException(log, "Erro ao processar a remoção de cargos no servidor " + guild.getName(), e);
        }
    }

    @Override
    public void updatePermissionsForCategoryRoleMapping(Guild guild) {
        Map<String, String> categoryRoleMapping = discordConfig.getParsedCategoryRoleMapping();

        for (Map.Entry<String, String> entry : categoryRoleMapping.entrySet()) {
            String categoryId = entry.getKey();
            String roleId = entry.getValue();

            Category category = guild.getCategoryById(categoryId);
            Role role = guild.getRoleById(roleId);
            Role everyoneRole = guild.getPublicRole();

            if (category != null && role != null) {
                category.getChannels().forEach(channel -> {
                    channel.getPermissionContainer()
                        .upsertPermissionOverride(everyoneRole)
                        .deny(Permission.VIEW_CHANNEL)
                        .queue(
                            success -> LogUtil.logInfo(log, "Acesso removido para " +
                                    "'everyone' no canal: {}", channel.getName()),

                            error -> LogUtil.logError(log, "Erro ao remover acesso para " +
                                    "'everyone' no canal: {}", channel.getName())
                        );

                    if (channel.getType().isMessage()) {
                        channel.getPermissionContainer()
                            .upsertPermissionOverride(role)
                            .grant(Permission.VIEW_CHANNEL, Permission.MESSAGE_SEND)
                            .queue(
                                success -> LogUtil.logInfo(log, "Permissões aplicadas " +
                                        "ao canal de texto: {}", channel.getName()),

                                error -> LogUtil.logError(log, "Erro ao configurar permissões " +
                                        "para o canal de texto: {}", channel.getName())
                            );
                    } else if (channel.getType().isAudio()) {
                        channel.getPermissionContainer()
                            .upsertPermissionOverride(role)
                            .grant(Permission.VIEW_CHANNEL, Permission.VOICE_CONNECT, Permission.VOICE_SPEAK)
                            .queue(
                                success -> LogUtil.logInfo(log, "Permissões aplicadas " +
                                        "ao canal de voz: {}", channel.getName()),

                                error -> LogUtil.logError(log, "Erro ao configurar permissões " +
                                        "para o canal de voz: {}", channel.getName())
                            );
                    } else {
                        LogUtil.logWarn(log, "Tipo de canal não suportado: {}", channel.getName());
                    }
                });
            } else {
                LogUtil.logError(log, "Categoria ou Cargo não encontrado para ID: Categoria={}, Cargo={}",
                    categoryId, roleId);
            }
        }
    }

    private void performRoleRemoval(Guild guild, Role botRole) {
        try {
            guild.getMembers().stream()
                    .filter(member -> !member.getUser().isBot()) // Ignora outros bots
                    .forEach(member -> {
                        member.getRoles().stream()
                                .filter(role -> role.getPosition() < botRole.getPosition()) // Seleciona apenas cargos abaixo do cargo do bot
                                .forEach(role -> guild.removeRoleFromMember(member, role).queue(
                                        success -> LogUtil.logInfo(log, "Cargo {} removido do membro {} no servidor {}",
                                                role.getName(), member.getEffectiveName(), guild.getName()),
                                        error -> LogUtil.logError(log, "Erro ao remover cargo {} do membro {} no servidor {}",
                                                role.getName(), member.getEffectiveName(), guild.getName())
                                ));
                    });
        } catch (Exception e) {
            LogUtil.logException(log, "Erro ao remover cargos abaixo do bot no servidor " + guild.getName(), e);
        }
    }

}
