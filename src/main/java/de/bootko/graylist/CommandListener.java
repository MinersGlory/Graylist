package de.bootko.graylist;

import java.util.List;
import java.util.UUID;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CommandListener
        implements Listener
{
    private Main plugin;

    public CommandListener(Main plugin)
    {
        this.plugin = plugin;
    }

    @EventHandler
    public void join(PlayerCommandPreprocessEvent event)
    {
        if ((!this.plugin.glist.contains(event.getPlayer().getUniqueId().toString())) && (!event.getPlayer().hasPermission("graylist.bypass")))
        {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.GRAY + "Graylist" + ChatColor.DARK_GRAY + "] " + ChatColor.RED + this.plugin.getConfig().getString("message"));
        }
    }
}
