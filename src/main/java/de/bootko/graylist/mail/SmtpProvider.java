package de.bootko.graylist.mail;

import org.bukkit.configuration.file.FileConfiguration;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

public class SmtpProvider implements MailProvider {
    private final FileConfiguration cfg;

    public SmtpProvider(FileConfiguration cfg) {
        this.cfg = cfg;
    }

    @Override
    public boolean send(String to, String subject, String htmlBody) {
        String host = cfg.getString("email.smtp.host", "localhost");
        int port = cfg.getInt("email.smtp.port", 587);
        String user = cfg.getString("email.smtp.user", "");
        String password = cfg.getString("email.smtp.password", "");
        boolean starttls = cfg.getBoolean("email.smtp.starttls", true);
        boolean ssl = cfg.getBoolean("email.smtp.ssl", false);

        Properties props = new Properties();
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", String.valueOf(port));
        props.put("mail.smtp.auth", user.isEmpty() ? "false" : "true");
        props.put("mail.smtp.starttls.enable", String.valueOf(starttls));
        if (ssl) {
            props.put("mail.smtp.ssl.enable", "true");
        }

        Session session = Session.getInstance(props, user.isEmpty() ? null : new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(user, password);
            }
        });

        try {
            MimeMessage msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(cfg.getString("email.from"), cfg.getString("email.fromName", ""), "UTF-8"));
            msg.setReplyTo(new InternetAddress[] { new InternetAddress(cfg.getString("email.replyTo", cfg.getString("email.from"))) });
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            msg.setSubject(subject, "UTF-8");
            msg.setContent(htmlBody, "text/html; charset=UTF-8");
            Transport.send(msg);
            return true;
        } catch (MessagingException | UnsupportedEncodingException ex) {
            return false;
        }
    }
}
