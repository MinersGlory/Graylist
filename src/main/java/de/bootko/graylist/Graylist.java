package de.bootko.graylist;

import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import de.bootko.graylist.de.bootko.graylist.commands.EmailCmd;
import de.bootko.graylist.de.bootko.graylist.commands.GraylistCmd;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Graylist extends JavaPlugin {
    private static Graylist instance;

    public final Logger logger = Logger.getLogger("Minecraft");
    private PluginManager pm;
    JoinListener jl;
    ChatListener cl;
    CommandListener cmdl;
    public List<String> glist;
    public List<String> opq;

    // String to easily grab plugin version
    String pluginVersion = this.getDescription().getVersion();

    @Override
    public void onEnable() {

        instance = this;

        getConfig().options().copyDefaults(true);
        saveConfig();

        loadLists();

        this.jl = new JoinListener(this);
        this.cl = new ChatListener(this);
        this.cmdl = new CommandListener(this);

        this.pm = getServer().getPluginManager();
        this.pm.registerEvents(this.jl, this);
        this.pm.registerEvents(this.cl, this);
        this.pm.registerEvents(this.cmdl, this);

        PluginCommand baseCommand = getCommand("graylist");
        baseCommand.setExecutor(new GraylistCmd(this));

        this.logger.info("Graylist " + pluginVersion + " has been enabled.");
    }

    public void onDisable()
    {
        this.logger.info("Graylist " + pluginVersion + " has been disabled.");
    }



    public void loadLists()
    {
        glist = getConfig().getStringList("graylist");
        opq = getConfig().getStringList("offlinePlayerQueue");
    }

    public boolean listPlayer(String pname)
    {
        OfflinePlayer op = getServer().getOfflinePlayer(pname);
        if (!this.glist.contains(op.getUniqueId().toString()))
        {
            this.glist.add(op.getUniqueId().toString());
            saveToConfig("graylist", this.glist);

            if (getServer().getOnlinePlayers().contains(op))
            {
                Player p = getServer().getPlayer(pname);
                if ((getConfig().getString("gamemode").equalsIgnoreCase("survival")) && (!p.hasPermission("graylist.bypass"))) {
                    p.setGameMode(GameMode.SURVIVAL);
                } else if ((getConfig().getString("gamemode").equalsIgnoreCase("creative")) && (!p.hasPermission("graylist.bypass"))) {
                    p.setGameMode(GameMode.CREATIVE);
                } else if ((getConfig().getString("gamemode").equalsIgnoreCase("adventure")) && (!p.hasPermission("graylist.bypass"))) {
                    p.setGameMode(GameMode.ADVENTURE);
                }
                p.performCommand(getConfig().getString("command"));

                p.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.GRAY + "Graylist" + ChatColor.DARK_GRAY + "] " + ChatColor.GREEN + "You have been graylisted.");
            }
            else if (!this.opq.contains(op.getUniqueId().toString()))
            {
                this.opq.add(op.getUniqueId().toString());
                saveToConfig("offlinePlayerQueue", this.opq);
            }
            return true;
        }
        return false;
    }

    public boolean unlistPlayer(String pname)
    {
        OfflinePlayer op = getServer().getOfflinePlayer(pname);
        if (this.glist.contains(op.getUniqueId().toString()))
        {
            this.glist.remove(op.getUniqueId().toString());
            saveToConfig("graylist", this.glist);
            if (getServer().getOnlinePlayers().contains(op))
            {
                Player p = getServer().getPlayer(pname);
                if (!p.hasPermission("graylist.bypass")) {
                    p.setGameMode(GameMode.ADVENTURE);
                }
                p.performCommand(getConfig().getString("command"));

                p.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.GRAY + "Graylist" + ChatColor.DARK_GRAY + "] " + ChatColor.RED + "You have been un-graylisted.");
            }
            else if (!this.opq.contains(op.getUniqueId().toString()))
            {
                this.opq.add(op.getUniqueId().toString());
                saveToConfig("offlinePlayerQueue", this.opq);
            }
            return true;
        }
        return false;
    }

    public void saveToConfig(String key, Object s)
    {
        getConfig().set(key, s);
        saveConfig();
    }

    public static Graylist getPlugin() {
        return instance;
    }
}
