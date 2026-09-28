package de.bootko.graylist;

import java.util.List;

import de.bootko.graylist.commands.GraylistCmd;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Graylist extends JavaPlugin {
    private static Graylist instance;

    public final java.util.logging.Logger logger = java.util.logging.Logger.getLogger("Minecraft");
    private PluginManager pm;
    JoinListener jl;
    ChatListener cl;
    CommandListener cmdl;
    public List<String> glist;
    public List<String> opq;

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
        if (baseCommand != null) {
            baseCommand.setExecutor(new GraylistCmd(this));
        }

        this.logger.info("Graylist " + pluginVersion + " has been enabled.");
    }

    @Override
    public void onDisable() {
        this.logger.info("Graylist " + pluginVersion + " has been disabled.");
    }

    public void loadLists() {
        glist = getConfig().getStringList("graylist");
        opq = getConfig().getStringList("offlinePlayerQueue");
    }

    public boolean isApproved(Player p) {
        return glist.contains(p.getUniqueId().toString()) || p.hasPermission("graylist.bypass");
    }

    public boolean listPlayer(String pname) {
        OfflinePlayer op = getServer().getOfflinePlayer(pname);
        if (op.getUniqueId() == null) {
            return false;
        }
        if (!this.glist.contains(op.getUniqueId().toString())) {
            this.glist.add(op.getUniqueId().toString());
            saveToConfig("graylist", this.glist);

            Player online = op.getPlayer();
            if (online != null) {
                applyGateMode(online);
                online.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.GRAY + "Graylist" + ChatColor.DARK_GRAY + "] "
                        + ChatColor.GREEN + "You have been approved. Welcome!");
            } else if (!this.opq.contains(op.getUniqueId().toString())) {
                this.opq.add(op.getUniqueId().toString());
                saveToConfig("offlinePlayerQueue", this.opq);
            }
            return true;
        }
        return false;
    }

    public boolean unlistPlayer(String pname) {
        OfflinePlayer op = getServer().getOfflinePlayer(pname);
        if (op.getUniqueId() == null) {
            return false;
        }
        if (this.glist.contains(op.getUniqueId().toString())) {
            this.glist.remove(op.getUniqueId().toString());
            saveToConfig("graylist", this.glist);
            this.opq.remove(op.getUniqueId().toString());
            saveToConfig("offlinePlayerQueue", this.opq);

            Player online = op.getPlayer();
            if (online != null && !online.hasPermission("graylist.bypass")) {
                online.setGameMode(GameMode.ADVENTURE);
                online.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.GRAY + "Graylist" + ChatColor.DARK_GRAY + "] "
                        + ChatColor.RED + "You have been removed from the graylist.");
            }
            return true;
        }
        return false;
    }

    public void applyGateMode(Player p) {
        String gm = getConfig().getString("gamemode", "adventure");
        try {
            p.setGameMode(GameMode.valueOf(gm.toUpperCase()));
        } catch (IllegalArgumentException ignored) {
            p.setGameMode(GameMode.ADVENTURE);
        }
        String cmd = getConfig().getString("command", "NULL");
        if (cmd != null && !cmd.equalsIgnoreCase("NULL") && !cmd.isEmpty()) {
            p.performCommand(cmd);
        }
    }

    public void saveToConfig(String key, Object s) {
        getConfig().set(key, s);
        saveConfig();
    }

    public static Graylist getPlugin() {
        return instance;
    }
}
