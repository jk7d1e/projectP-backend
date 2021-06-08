package com.jk7d.projectpbackend.service.mail;

import com.jk7d.projectpbackend.store.user.User;
import com.jk7d.projectpbackend.store.user.confirmation.ConfirmationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;

@Service
public class MailService {

    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine springTemplateEngine;

    @Autowired
    public MailService(final JavaMailSender javaMailSender, final SpringTemplateEngine springTemplateEngine) {
        this.javaMailSender = javaMailSender;
        this.springTemplateEngine = springTemplateEngine;
    }

    /**
     *
     * @param user
     * @param confirmationToken
     * @throws MessagingException
     */
    @Async
    public void sendConfirmationEmail(final User user, final ConfirmationToken confirmationToken) throws MessagingException {
        // setup
        final MimeMessage message = this.javaMailSender.createMimeMessage();
        final MimeMessageHelper helper = new MimeMessageHelper(message,
                MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                StandardCharsets.UTF_8.name());

        // set context
        final String link = "http://127.0.0.1:8080/auth/register/confirm?ct=" + confirmationToken.getToken();
        final Context context = new Context();
        context.setVariable("username", user.getUsername());
        context.setVariable("link", link);

        // get html
        final String html = this.springTemplateEngine.process("confirmation_mail", context);

        // construct mail
        helper.setTo(user.getEmail());
        helper.setFrom("no-reply@example.com");
        helper.setSubject("Complete the registration of your account");
        helper.setText(html, true);

        this.javaMailSender.send(message);
    }
}
