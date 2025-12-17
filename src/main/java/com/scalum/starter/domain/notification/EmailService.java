package com.scalum.starter.domain.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Async
    public void sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }

    @Async
    public void sendVerificationSuccessEmail(String to, String name) {
        String subject = "Email Verified Successfully!";
        String body =
                "Dear "
                        + name
                        + ",\n\nYour email address has been successfully verified. You can now fully access all features of our application.\n\nThank you!";
        sendEmail(to, subject, body);
    }
}
