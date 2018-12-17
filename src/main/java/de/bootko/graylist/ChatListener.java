package de.bootko.graylist;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener
        implements Listener
{
    private Graylist plugin;

    public ChatListener(Graylist plugin)
    {
        this.plugin = plugin;
    }

    @EventHandler
    public void chat(AsyncPlayerChatEvent event)
    {
        if ((!this.plugin.glist.contains(event.getPlayer().getUniqueId().toString())) && (!event.getPlayer().hasPermission("graylist.bypass")))
        {
            // Players will no longer be muted as of v1.1
            //event.setCancelled(true);
            //event.getPlayer().sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.GRAY + "Graylist" + ChatColor.DARK_GRAY + "] " + ChatColor.RED + this.plugin.getConfig().getString("message"));
        }
    }
}
