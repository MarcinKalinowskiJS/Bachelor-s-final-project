/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beans;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 *
 * @author Marcin
 */
@ManagedBean
@RequestScoped
public class SendMailSSLBean {

    static final String username = "youremail@gmail.com";
    static final String password = "yourpassword";


    /**
     * Creates a new instance of SendMailSSLBean
     */
    public SendMailSSLBean() {
    }

    public static boolean sendMailSSL(String subject, String message, String emailAddressTo) {
        System.out.println("Beggining send");
        boolean errors = false;
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class",
                "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");

        //Session session = Session.getDefaultInstance(props,
        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {

            Message mes = new MimeMessage(session);
            mes.setFrom(new InternetAddress("tt349047@gmail.com"));
            mes.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(emailAddressTo));
            mes.setSubject(subject);
            mes.setText(message);

            Transport.send(mes);

            System.out.println("Done sending email");

        } catch (MessagingException e) {
            e.printStackTrace();
            System.out.println("Send email error");
            //throw new RuntimeException(e);
            errors = true;
        }
        return errors;
    }
}
