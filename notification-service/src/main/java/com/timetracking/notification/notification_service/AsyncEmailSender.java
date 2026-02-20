package com.timetracking.notification.notification_service;

import com.timetracking.notification.domain.EmailNotification;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
@Slf4j
public class AsyncEmailSender {

  private final JavaMailSender mailSender;
  private final TemplateEngine templateEngine;

  @Value("${notification.from-email}")
  private String fromEmail;

  @Value("${notification.from-name}")
  private String fromName;

  @Async
  public void sendEmail(EmailNotification notification) {
    try {
      MimeMessage message = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

      helper.setFrom(new InternetAddress(fromEmail, fromName));
      helper.setTo(notification.getTo());
      helper.setSubject(notification.getSubject());

      Context context = new Context();
      context.setVariables(notification.getVariables());
      String htmlContent = templateEngine.process(notification.getTemplateName(), context);

      helper.setText(htmlContent, true);

      if (notification.getCc() != null && !notification.getCc().isEmpty()) {
        helper.setCc(notification.getCc().toArray(new String[0]));
      }

      mailSender.send(message);
      log.info("Email sent successfully to: {}", notification.getTo());

    } catch (Exception e) {
      log.error("Failed to send email to: {}", notification.getTo(), e);
      throw new RuntimeException("Failed to send email", e);
    }
  }
}
