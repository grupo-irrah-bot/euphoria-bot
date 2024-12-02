package br.com.grupoirrah.euphoriabot.entrypoint.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.react.GenericMessageReactionEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReactionListener extends ListenerAdapter {

    private final Map<String, String> teamEmojiToRoleMap;
    private final Map<String, String> roleEmojiToRoleMap;

    @Override
    public void onMessageReactionAdd(@NotNull MessageReactionAddEvent event) {
        if (isBot(event)) {
            return;
        }

        String emoji = event.getReaction().getEmoji().getName();
        String roleId = null;
        String type = null;

        if (teamEmojiToRoleMap.containsKey(emoji)) {
            roleId = teamEmojiToRoleMap.get(emoji);
            type = "time";
        } else if (roleEmojiToRoleMap.containsKey(emoji)) {
            roleId = roleEmojiToRoleMap.get(emoji);
            type = "função";
        }

        if (roleId != null) {
            Role role = event.getGuild().getRoleById(roleId);
            Member member = event.retrieveMember().complete();

            if (role != null && member != null) {
                String message;
                if ("time".equals(type)) {
                    message = "Você foi adicionado ao time";
                } else {
                    message = "Você foi adicionado à função";
                }

                event.getGuild().addRoleToMember(member, role).queue(
                    success -> {
                        if (event.getUser() != null) {
                            event.getUser().openPrivateChannel().queue(privateChannel ->
                                privateChannel.sendMessage(message + ": **" + role.getName() +
                                    "** do **Grupo Irrah**").queue()
                            );
                        }
                    },
                    error -> log.error("Erro ao adicionar role {} ao usuário {}: {}", role.getName(),
                        member.getEffectiveName(), error.getMessage())
                );
            }
        }
    }

    @Override
    public void onMessageReactionRemove(@NotNull MessageReactionRemoveEvent event) {
        if (isBot(event)) {
            return;
        }

        String emoji = event.getReaction().getEmoji().getName();
        String roleId = null;
        String type = null;

        if (teamEmojiToRoleMap.containsKey(emoji)) {
            roleId = teamEmojiToRoleMap.get(emoji);
            type = "time";
        } else if (roleEmojiToRoleMap.containsKey(emoji)) {
            roleId = roleEmojiToRoleMap.get(emoji);
            type = "função";
        }

        if (roleId != null) {
            Role role = event.getGuild().getRoleById(roleId);
            Member member = event.retrieveMember().complete();

            if (role != null && member != null) {
                String message;
                if ("time".equals(type)) {
                    message = "Você foi removido do time";
                } else {
                    message = "Você foi removido da função";
                }

                event.getGuild().removeRoleFromMember(member, role).queue(
                    success -> {
                        if (event.getUser() != null) {
                            event.getUser().openPrivateChannel().queue(privateChannel ->
                                privateChannel.sendMessage(message + ": **" + role.getName() +
                                    "** do **Grupo Irrah**").queue()
                            );
                        }
                    },
                    error -> log.error("Erro ao remover role {} do usuário {}: {}", role.getName(),
                        member.getEffectiveName(), error.getMessage())
                );
            }
        }
    }

    private static boolean isBot(@NotNull GenericMessageReactionEvent event) {
        if (event.getMember() == null) {
            return false;
        }

        return event.getMember().getUser().isBot();
    }

}
