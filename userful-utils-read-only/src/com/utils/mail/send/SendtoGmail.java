package com.utils.mail.send;

import java.util.Date;
import java.util.Enumeration;
import java.util.Vector;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.mail.config.GmailProperties;

public class SendtoGmail {

    private static String messageContentMimeType = "text/html; charset=gb2312";



    /**
     * �����ʼ�
     * 
     * @param username
     * @param password
     * @param receiver
     * @return
     * @throws AddressException
     * @throws MessagingException
     */
    @SuppressWarnings( { "unchecked", "serial" })
    public static Message buildMimeMessage(final String username,
                                           final String password,
                                           String receiver) throws AddressException,
        MessagingException {
        Session session = Session.getDefaultInstance(GmailProperties.getProperties(),
                                                     new Authenticator() {
                                                         protected PasswordAuthentication getPasswordAuthentication() {
                                                             return new PasswordAuthentication(username,
                                                                                               password);
                                                         }
                                                     });

        Message msg = new MimeMessage(session);

        // ������ߣ������ʹ��setFrom()��setReplyTo()������
        // msg.setFrom(new InternetAddress("[������]"));
        msg.addFrom(InternetAddress.parse("[������]"));// ��ַ��Դ,û����?
        msg.setReplyTo(InternetAddress.parse("[�ظ�ʱ�ռ���]"));// �ظ�ʱ�õĵ�ַ
        // ��Ϣ������
        msg.setRecipients(Message.RecipientType.TO,
                          InternetAddress.parse(receiver, false));
        msg.setSubject("JavaMail�ʼ�����");
        msg.setSentDate(new Date());

        String content = "How are you!����һ������!";
        // �ʼ�������ݣ�Content��
        msg.setContent(buildMimeMultipart(content, new Vector() {
            {
                add("D:/test.txt");
            }
        }));

        return msg;
    }

    /**
     * �����ʼ������ĺ͸���
     * 
     * @param msgContent
     * @param attachedFileList
     * @return
     * @throws MessagingException
     */
    public static Multipart buildMimeMultipart(String msgContent,
                                               Vector attachedFileList) throws MessagingException {
        Multipart mPart = new MimeMultipart();// �ಿ��ʵ��

        // �ʼ�����
        MimeBodyPart mBodyContent = new MimeBodyPart();// MIME�ʼ�����
        if (msgContent != null) {
            mBodyContent.setContent(msgContent, messageContentMimeType);
        } else {
            mBodyContent.setContent("", messageContentMimeType);
        }
        mPart.addBodyPart(mBodyContent);

        // ����
        String file;
        String fileName;
        if (attachedFileList != null) {
            for (Enumeration fileList = attachedFileList.elements(); fileList.hasMoreElements();) {
                file = (String) fileList.nextElement();
                fileName = file.substring(file.lastIndexOf("/") + 1);
                MimeBodyPart mBodyPart = new MimeBodyPart();
                // Զ����Դ
                // URLDataSource uds=new
                // URLDataSource(http://www.javaeye.com/logo.gif);
                FileDataSource fds = new FileDataSource(file);
                mBodyPart.setDataHandler(new DataHandler(fds));
                mBodyPart.setFileName(fileName);
                mPart.addBodyPart(mBodyPart);
            }
        }

        return mPart;
    }

    /**
     * �����ʼ�
     * 
     * @throws AddressException
     * @throws MessagingException
     */
    public static void sendMail() throws AddressException, MessagingException {
        // Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
        // Transport ������������Ϣ��
        Transport.send(buildMimeMessage("dongpf.lab", "19860306", "dongpf.lab@gmail.com"));

        System.out.println("Message send...");

    }
}
