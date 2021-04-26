package com.pulse.mst.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.thymeleaf.spring5.SpringTemplateEngine;


@Service
public class DefaultEmailService implements EmailService {


    @Autowired
    public JavaMailSender javaMailSender;
    @Autowired
    private SpringTemplateEngine templateEngine;


    @Override
    public void sentApprovedEmail(String toAddress, String subject, String message) {
//        System.out.println("email id =="+toAddress);
//        System.out.println("email subject =="+subject);
//        System.out.println("email message =="+message);

        SimpleMailMessage sentApprovedEmail = new SimpleMailMessage();
        sentApprovedEmail.setTo(toAddress);
        sentApprovedEmail.setSubject(subject);
        sentApprovedEmail.setText(message);
        javaMailSender.send(sentApprovedEmail);
        System.out.println("sent approved  email done...");



    }
    @Override
    public void sendSubmitEmail(String toAddress, String subject, String message) {
//        System.out.println("email id =="+toAddress);
//        System.out.println("email subject =="+subject);
//        System.out.println("email message =="+message);

        SimpleMailMessage sentSubmitEmail = new SimpleMailMessage();
        sentSubmitEmail.setTo(toAddress);
        sentSubmitEmail.setSubject(subject);
        sentSubmitEmail.setText(message);
        javaMailSender.send(sentSubmitEmail);
        System.out.println("sentSubmitEmail email done...");



    }

}