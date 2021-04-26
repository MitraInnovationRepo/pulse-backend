package com.pulse.mst.Services;

public interface EmailService {

    void sentApprovedEmail(final String toAddress, final String subject, final String message);
    void sendSubmitEmail(final String toAddress, final String subject, final String message);
//    void sendEmailWithAttachment(final String toAddress, final String subject, final String message, final String attachment) throws MessagingException, FileNotFoundException;
}