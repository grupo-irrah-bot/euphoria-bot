package br.com.grupoirrah.euphoriabot.dataprovider.discord;

import br.com.grupoirrah.euphoriabot.config.DiscordConfig;
import br.com.grupoirrah.euphoriabot.core.gateway.GuildGateway;
import br.com.grupoirrah.euphoriabot.core.usecase.interactor.HookContext;
import br.com.grupoirrah.euphoriabot.core.util.LogUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class GuildManager implements GuildGateway {

    private final JDA jda;
    private final DiscordConfig discordConfig;

    @Override
    public Mono<Guild> getGuild(String guildId) {
        return Mono.justOrEmpty(jda.getGuildById(guildId));
    }

    @Override
    public Mono<Member> getMember(Guild guild, String userId) {
        return Mono.justOrEmpty(guild.retrieveMemberById(userId).complete());
    }

    @Override
    public void assignIrradianteRoleToMember(Guild guild, Member member) {
        Role role = guild.getRoleById(discordConfig.getRoleIrradianteId());

        if (role == null) {
            throw new RuntimeException("Cargo com o ID '" + discordConfig.getRoleIrradianteId() + "' não " +
                "encontrado no servidor.");
        }

        guild.addRoleToMember(member, role).queue();

    }

    @Override
    public Mono<Optional<String>> sendDirectMessageOrFallback(Member member,
                                                              String message,
                                                              HookContext hookContext,
                                                              Guild guild) {
        String guildName = guild.getName();
        String fullMessage = message + " no servidor **" + guildName + "**.";

        return Mono.create(sink -> member.getUser().openPrivateChannel().queue(
            privateChannel -> privateChannel.sendMessage(fullMessage).queue(
                success -> {
                    LogUtil.logInfo(log, "✅ Mensagem enviada com sucesso para o usuário: {}",
                        member.getUser().getId());
                    guild.kick(member).queue();
                    sink.success(Optional.empty());
                },
                error -> {
                    handleDmError(error, member, fullMessage, hookContext);
                    sink.success(Optional.of("❌ Não foi possível enviar a mensagem via DM. Mensagem enviada " +
                        "no canal."));
                }
            ),
            error -> {
                handlePrivateChannelError(error, member, fullMessage, hookContext);
                sink.success(Optional.of("❌ Não foi possível abrir o canal privado. Mensagem enviada " +
                    "no canal."));
            }
        ));
    }

    @Override
    public Mono<Optional<String>> sendRedirectChannelTeamSelectionMessage(HookContext hookContext, Guild guild) {
        String link = "https://discord.com/channels/" + guild.getId() + "/" + discordConfig.getTeamRoleChannelId();
        Button teamSelectionButton = Button.link(link, "Acesse o canal de boas-vindas 🎉");

        String teamSelectionMessage = """
            ✅ **Seu e-mail foi verificado com sucesso!**
            Agora você já pode começar a explorar o servidor do **Grupo Irrah**.
            
            👉 **Próximo passo:**
            Clique no botão abaixo para acessar o canal de seleção de equipes e funções.
            
            _Estamos animados em ter você conosco! 🚀_
            """;

        return Mono.fromRunnable(() -> hookContext.sendActionRow(teamSelectionMessage, teamSelectionButton,
            true)).then(Mono.just(Optional.empty()));
    }

    private void handleDmError(Throwable error, Member member, String message, HookContext hookContext) {
        if (error.getMessage().contains("50007")) {
            LogUtil.logError(log, "❌ Não foi possível enviar DM para o usuário: {}, pois o usuário " +
                "desativou mensagens diretas.", member.getUser().getId());

            hookContext.sendMessage(message, true);
        } else {
            LogUtil.logError(log, "❌ Falha ao enviar a mensagem para o usuário: {}. Erro: {}",
                member.getUser().getId(), error.getMessage());
        }
    }

    private void handlePrivateChannelError(Throwable error, Member member, String message, HookContext hookContext) {
        LogUtil.logError(log, "❌ Não foi possível abrir o canal privado para o usuário: {}. Erro: {}",
            member.getUser().getId(), error.getMessage());

        hookContext.sendMessage(message, true);
    }

}
