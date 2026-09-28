package de.bootko.graylist;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CommandListener implements Listener {
    private final Graylist plugin;

    public CommandListener(Graylist plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onCommand(PlayerCommandPreprocessEvent event) {
        if (this.plugin.isApproved(event.getPlayer())) {
            return;
        }
        List<String> allowed = this.plugin.getConfig().getStringList("gated.allowed-commands");
        String base = event.getMessage().split(" ")[0].toLowerCase();
        if (base.startsWith("/")) {
            base = base.substring(1);
        }
        for (String a : allowed) {
            if (base.equalsIgnoreCase(a)) {
                return;
            }
        }
        event.setCancelled(true);
        event.getPlayer().sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.GRAY + "Graylist" + ChatColor.DARK_GRAY + "] "
                + ChatColor.RED + this.plugin.getConfig().getString("message"));
    }
}
