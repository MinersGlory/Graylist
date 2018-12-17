package de.bootko.graylist;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CommandListener
        implements Listener
{
    private Graylist plugin;

    public CommandListener(Graylist plugin)
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
