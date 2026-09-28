package de.bootko.graylist.mail;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;

import org.bukkit.configuration.file.FileConfiguration;

public class SendGridProvider implements MailProvider {
    private final FileConfiguration cfg;

    public SendGridProvider(FileConfiguration cfg) {
        this.cfg = cfg;
    }

    @Override
    public boolean send(String to, String subject, String htmlBody) {
        String apiKey = cfg.getString("email.sendgrid.apiKey", "");
        if (apiKey.isEmpty()) {
            return false;
        }
        try {
            Email from = new Email(cfg.getString("email.from"), cfg.getString("email.fromName"));
            Mail mail = new Mail();
            mail.setFrom(from);
            Personalization p = new Personalization();
            p.addTo(new Email(to));
            mail.addPersonalization(p);
            mail.setSubject(subject);
            mail.addContent(new Content("text/html", htmlBody));
            SendGrid sg = new SendGrid(apiKey);
            Request req = new Request();
            req.setMethod(Method.POST);
            req.setEndpoint("mail/send");
            req.setBody(mail.build());
            Response res = sg.api(req);
            return res.getStatusCode() >= 200 && res.getStatusCode() < 300;
        } catch (Exception ex) {
            return false;
        }
    }
}
