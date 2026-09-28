package de.bootko.graylist.mail;

public interface MailProvider {
    boolean send(String to, String subject, String htmlBody);
}
