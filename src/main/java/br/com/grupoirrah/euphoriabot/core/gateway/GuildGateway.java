package br.com.grupoirrah.euphoriabot.core.gateway;

import br.com.grupoirrah.euphoriabot.core.usecase.interactor.HookContext;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import reactor.core.publisher.Mono;

import java.util.Optional;

public interface GuildGateway {

    Mono<Guild> getGuild(String guildId);
    Mono<Member> getMember(Guild guild, String userId);
    void assignIrradianteRoleToMember(Guild guild, Member member);

    Mono<Optional<String>> sendDirectMessageOrFallback(Member member,
                                                       String message,
                                                       HookContext hookContext,
                                                       Guild guild);

    Mono<Optional<String>> sendRedirectChannelTeamSelectionMessage(HookContext hookContext, Guild guild);
}
