package de.bootko.graylist.commands;

import de.bootko.graylist.Graylist;
import de.bootko.graylist.mail.MailService;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GraylistCmd implements CommandExecutor {
    private final Graylist plugin;

    public GraylistCmd(Graylist plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!cmd.getName().equalsIgnoreCase("graylist")) {
            return true;
        }
        if (args.length == 0) {
            help(sender);
            return true;
        }
        String sub = args[0].toLowerCase();
        switch (sub) {
            case "add":
                if (check(sender, "graylist.admin")) { add(sender, args); }
                return true;
            case "remove":
                if (check(sender, "graylist.admin")) { remove(sender, args); }
                return true;
            case "list":
                if (check(sender, "graylist.admin")) { list(sender); }
                return true;
            case "reload":
                if (check(sender, "graylist.admin")) {
                    plugin.reloadConfig();
                    plugin.loadLists();
                    msg(sender, ChatColor.GREEN + "Config and lists reloaded.");
                }
                return true;
            case "sendwelcome":
                if (check(sender, "graylist.email")) { sendWelcome(sender, args); }
                return true;
            default:
                help(sender);
                return true;
        }
    }

    private void add(CommandSender sender, String[] args) {
        if (args.length < 2) {
            msg(sender, ChatColor.RED + "Usage: /graylist add <player>");
            return;
        }
        String name = args[1];
        if (plugin.listPlayer(name)) {
            msg(sender, ChatColor.GREEN + "Added " + name + " to the graylist.");
        } else {
            msg(sender, ChatColor.RED + name + " is already on the graylist or could not be resolved.");
        }
    }

    private void remove(CommandSender sender, String[] args) {
        if (args.length < 2) {
            msg(sender, ChatColor.RED + "Usage: /graylist remove <player>");
            return;
        }
        String name = args[1];
        if (plugin.unlistPlayer(name)) {
            msg(sender, ChatColor.GREEN + "Removed " + name + " from the graylist.");
        } else {
            msg(sender, ChatColor.RED + name + " is not on the graylist.");
        }
    }

    private void list(CommandSender sender) {
        msg(sender, ChatColor.GRAY + "Graylist (" + plugin.glist.size() + " players):");
        int shown = 0;
        for (String uid : plugin.glist) {
            if (shown >= 20) {
                msg(sender, ChatColor.GRAY + "... and " + (plugin.glist.size() - shown) + " more");
                break;
            }
            msg(sender, ChatColor.GRAY + " - " + Bukkit.getOfflinePlayer(UUID.fromString(uid)).getName());
            shown++;
        }
    }

    private void sendWelcome(CommandSender sender, String[] args) {
        if (args.length < 3) {
            msg(sender, ChatColor.RED + "Usage: /graylist sendwelcome <player> <email>");
            return;
        }
        String playerName = args[1];
        String email = args[2];
        if (!plugin.getConfig().getBoolean("email.enabled", false)) {
            msg(sender, ChatColor.RED + "Emails are disabled in config (email.enabled: false).");
            return;
        }
        Map<String, String> vars = new HashMap<>();
        vars.put("playerName", playerName);
        vars.put("serverName", plugin.getConfig().getString("email.serverName", "Aftermath SMP"));
        vars.put("link", plugin.getConfig().getString("email.websiteLink", ""));
        vars.put("supportEmail", plugin.getConfig().getString("email.supportEmail", ""));
        boolean ok = MailService.send(plugin, email, "Welcome to " + vars.get("serverName") + "!", vars);
        if (ok) {
            msg(sender, ChatColor.GREEN + "Welcome email sent to " + email + ".");
        } else {
            msg(sender, ChatColor.RED + "Failed to send email - check the console and email config.");
        }
    }

    private void help(CommandSender sender) {
        msg(sender, ChatColor.GRAY + "Graylist commands:");
        msg(sender, ChatColor.GRAY + " /graylist add <player> - approve a player");
        msg(sender, ChatColor.GRAY + " /graylist remove <player> - revoke approval");
        msg(sender, ChatColor.GRAY + " /graylist list - list approved players");
        msg(sender, ChatColor.GRAY + " /graylist reload - reload config");
        msg(sender, ChatColor.GRAY + " /graylist sendwelcome <player> <email> - send welcome email");
    }

    private boolean check(CommandSender sender, String perm) {
        if (sender.hasPermission(perm) || sender.isOp()) {
            return true;
        }
        msg(sender, ChatColor.RED + "Insufficient permissions.");
        return false;
    }

    private void msg(CommandSender sender, String text) {
        String prefix = ChatColor.DARK_GRAY + "[" + ChatColor.GRAY + "Graylist" + ChatColor.DARK_GRAY + "] ";
        if (sender instanceof Player) {
            sender.sendMessage(prefix + text);
        } else {
            plugin.logger.info(ChatColor.stripColor(text));
        }
    }
}
