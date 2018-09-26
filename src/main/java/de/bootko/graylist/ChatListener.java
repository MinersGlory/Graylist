package de.bootko.graylist;

import java.util.List;
import java.util.UUID;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener
        implements Listener
{
    private Main plugin;

    public ChatListener(Main plugin)
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
