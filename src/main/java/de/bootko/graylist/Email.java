package de.bootko.graylist;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;

public class Email implements CommandExecutor {
    Main plugin;

    public Email (Main instance) {
        plugin = instance;
    }

    String api_key = plugin.getConfig().getString("email.api_key");
    String sendfrom = plugin.getConfig().getString("email.sendfrom");
    String replyto = plugin.getConfig().getString("email.replyto");
    String name = plugin.getConfig().getString("email.name");
    Integer groupId = plugin.getConfig().getInt("email.groupId");


    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
            if (cmd.getName().equalsIgnoreCase("sendwelcome")) {
                if (sender instanceof Player) {
                    String email = args[0];
                    String username = args[1];

                    if (args.length != 2) {
                        sender.sendMessage(ChatColor.RED + "Usage: /greylist sendwelcome <email> <username>");
                } else {
                        if (args.length == 2) {
                            if (sender.hasPermission("graylist.email")){
                                try {
                                    SendGrid sg = new SendGrid(System.getenv(api_key));
                                    Request request = new Request();
                                    request.setMethod(Method.POST);
                                    request.setEndpoint("mail/send");
                                    request.setBody("{\"personalizations\":[{\"to\":[{\"email\":\"" + email + "\"}],\"dynamic_template_data\":{\"minecraft_user\":\"" + username + "\"}}],\"from\":{\"email\":\"" + sendfrom + "\",\"name\":\""+ name + "\"},\"reply_to\":{\"email\":\"" + replyto + "\",\"asm\":{\"group_id\":" + groupId + "},\"template_id\":\"d-c3e6ee5686864ef1a2d8ee44fbd32d48\"}");
                                    Response response = sg.api(request);
                                    System.out.println(response.getStatusCode());
                                    System.out.println(response.getBody());
                                    System.out.println(response.getHeaders());

                                } catch (IOException ex) {
                                    System.out.println(ex);
                                }
                            }
                        }
                    }
                }
            } return false;
    }
}

