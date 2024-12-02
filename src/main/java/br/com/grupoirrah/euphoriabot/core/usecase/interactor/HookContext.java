package br.com.grupoirrah.euphoriabot.core.usecase.interactor;

import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

public class HookContext {
    private final InteractionHook hook;

    public HookContext(InteractionHook hook) {
        this.hook = hook;
    }

    public void sendMessage(String message, boolean ephemeral) {
        hook.sendMessage(message)
            .setEphemeral(ephemeral)
            .queue();
    }

    public void sendActionRow(String message, Button button, boolean ephemeral) {
        hook.sendMessage(message)
            .addActionRow(button)
            .setEphemeral(ephemeral)
            .queue();
    }

}
