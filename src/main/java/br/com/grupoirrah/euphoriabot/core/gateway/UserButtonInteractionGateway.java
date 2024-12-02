package br.com.grupoirrah.euphoriabot.core.gateway;

import br.com.grupoirrah.euphoriabot.core.usecase.interactor.HookContext;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import reactor.core.publisher.Mono;

import java.util.Optional;

public interface UserButtonInteractionGateway {

    void execute(ButtonInteractionEvent event);
    Mono<Optional<HookContext>> removeInteraction(String interactionId);

}