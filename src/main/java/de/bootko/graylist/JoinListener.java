package de.bootko.graylist;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {
    private final Graylist plugin;

    public JoinListener(Graylist plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void join(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        String uid = p.getUniqueId().toString();

        if (this.plugin.opq.contains(uid)) {
            if (!p.hasPermission("graylist.bypass")) {
                this.plugin.applyGateMode(p);
            }
            this.plugin.opq.remove(uid);
            this.plugin.saveToConfig("offlinePlayerQueue", this.plugin.opq);
        }

        if (!this.plugin.isApproved(p)) {
            p.setGameMode(GameMode.ADVENTURE);
            p.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.GRAY + "Graylist" + ChatColor.DARK_GRAY + "] "
                    + ChatColor.RED + this.plugin.getConfig().getString("message"));
        }
    }
}
