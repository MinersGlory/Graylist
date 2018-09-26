package de.bootko.graylist;

import java.util.List;
import java.util.UUID;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener
        implements Listener
{
    private Main plugin;

    public JoinListener(Main plugin)
    {
        this.plugin = plugin;
    }

    @EventHandler
    public void join(PlayerJoinEvent event)
    {
        if (this.plugin.opq.contains(event.getPlayer().getUniqueId().toString()))
        {
            if (!event.getPlayer().hasPermission("graylist.bypass"))
            {
                if (this.plugin.getConfig().getString("command") != "NULL") {
                    event.getPlayer().performCommand(this.plugin.getConfig().getString("command"));
                }
                if (this.plugin.getConfig().getString("gamemode").equalsIgnoreCase("survival")) {
                    event.getPlayer().setGameMode(GameMode.SURVIVAL);
                } else if (this.plugin.getConfig().getString("gamemode").equalsIgnoreCase("creative")) {
                    event.getPlayer().setGameMode(GameMode.CREATIVE);
                } else if (this.plugin.getConfig().getString("gamemode").equalsIgnoreCase("adventure")) {
                    event.getPlayer().setGameMode(GameMode.ADVENTURE);
                }
            }
            this.plugin.opq.remove(event.getPlayer().getUniqueId().toString());
            this.plugin.saveToConfig("offlinePlayerQueue", this.plugin.opq);
        }
        if ((!this.plugin.glist.contains(event.getPlayer().getUniqueId().toString())) && (!event.getPlayer().hasPermission("graylist.bypass")))
        {
            event.getPlayer().setGameMode(GameMode.ADVENTURE);
            event.getPlayer().sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.GRAY + "Graylist" + ChatColor.DARK_GRAY + "] " + ChatColor.RED + this.plugin.getConfig().getString("message"));
        }
    }
}
