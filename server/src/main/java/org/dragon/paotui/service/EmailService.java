package org.dragon.paotui.service;

public interface EmailService {
    void send(String toEmail, String subject, String content);
    void sendHtml(String toEmail, String subject, String content);
}
