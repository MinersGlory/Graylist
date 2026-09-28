package de.bootko.graylist.mail;

import de.bootko.graylist.Graylist;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public final class MailService {
    private static MailProvider provider;
    private static String cachedBody;

    private MailService() {}

    public static boolean send(Graylist plugin, String to, String subject, Map<String, String> vars) {
        FileConfiguration cfg = plugin.getConfig();
        String body = render(plugin, vars);
        if (body == null) {
            plugin.getLogger().warning("MailService: template email/welcome.html not found");
            return false;
        }
        MailProvider p = getProvider(plugin, cfg);
        if (p == null) {
            plugin.getLogger().warning("MailService: provider not configured");
            return false;
        }
        try {
            return p.send(to, subject, body);
        } catch (Exception ex) {
            plugin.getLogger().warning("MailService: " + ex.getMessage());
            return false;
        }
    }

    private static MailProvider getProvider(Graylist plugin, FileConfiguration cfg) {
        if (provider != null) {
            return provider;
        }
        String type = cfg.getString("email.provider", "smtp");
        switch (type.toLowerCase()) {
            case "sendgrid":
                provider = new SendGridProvider(cfg);
                break;
            case "mailgun":
                provider = new MailgunProvider(cfg);
                break;
            case "smtp":
            default:
                provider = new SmtpProvider(cfg);
                break;
        }
        return provider;
    }

    private static String render(Graylist plugin, Map<String, String> vars) {
        if (cachedBody == null) {
            try (InputStream in = plugin.getResource("email/welcome.html")) {
                if (in == null) {
                    return null;
                }
                cachedBody = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            } catch (Exception ex) {
                plugin.getLogger().warning("MailService: failed to read template: " + ex.getMessage());
                return null;
            }
        }
        String out = cachedBody;
        for (Map.Entry<String, String> e : vars.entrySet()) {
            out = out.replace("{" + e.getKey() + "}", e.getValue() == null ? "" : e.getValue());
        }
        return out;
    }
}
