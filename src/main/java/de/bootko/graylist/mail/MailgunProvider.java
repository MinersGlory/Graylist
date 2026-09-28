package de.bootko.graylist.mail;

import org.bukkit.configuration.file.FileConfiguration;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class MailgunProvider implements MailProvider {
    private final FileConfiguration cfg;

    public MailgunProvider(FileConfiguration cfg) {
        this.cfg = cfg;
    }

    @Override
    public boolean send(String to, String subject, String htmlBody) {
        String apiKey = cfg.getString("email.mailgun.apiKey", "");
        String domain = cfg.getString("email.mailgun.domain", "");
        if (apiKey.isEmpty() || domain.isEmpty()) {
            return false;
        }
        try {
            String body = "from=" + enc(cfg.getString("email.fromName", "") + " <" + cfg.getString("email.from") + ">")
                    + "&to=" + enc(to)
                    + "&subject=" + enc(subject)
                    + "&html=" + enc(htmlBody);
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.mailgun.net/v3/" + domain + "/messages"))
                    .header("Authorization", "Basic " + Base64.getEncoder().encodeToString(("api:" + apiKey).getBytes(StandardCharsets.UTF_8)))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                    .build();
            HttpResponse<String> res = HttpClient.newHttpClient().send(req, HttpResponse.BodyHandlers.ofString());
            return res.statusCode() >= 200 && res.statusCode() < 300;
        } catch (Exception ex) {
            return false;
        }
    }

    private static String enc(String s) throws Exception {
        return URLEncoder.encode(s == null ? "" : s, StandardCharsets.UTF_8);
    }
}
