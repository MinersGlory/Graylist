package de.bootko.graylist;

import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.EventHandler;

public class ChatListener implements Listener {
    private final Graylist plugin;

    public ChatListener(Graylist plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void chat(AsyncPlayerChatEvent event) {
        // Chat is allowed for everyone; gating is handled by gamemode and CommandListener.
    }
}
