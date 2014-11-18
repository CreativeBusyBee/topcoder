package com.utils.mail.receive;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import javax.mail.BodyPart;
import javax.mail.FetchProfile;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.URLName;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import com.mail.config.GmailProperties;

public class ReceivefromGmail {

    /**
     * �ִ�����
     * 
     * @param text
     * @return
     * @throws UnsupportedEncodingException
     */
    protected static String decodeText(String text) throws UnsupportedEncodingException {
        if (text == null)
            return null;
        if (text.startsWith("=?GB") || text.startsWith("=?gb")) {
            text = MimeUtility.decodeText(text);
        } else {
            text = new String(text.getBytes("ISO8859_1"));
        }
        return text;
    }

    /**
     * �����ʼ�
     * 
     * @param mPart
     */
    public static void parseMailContent(Object content) {
        try {
            if (content instanceof Multipart) {
                Multipart mPart = (MimeMultipart) content;
                for (int i = 0; i < mPart.getCount(); i++) {
                    extractPart((MimeBodyPart) mPart.getBodyPart(i));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * ��ȡ����
     * 
     * @param part
     */
    public static void extractPart(MimeBodyPart part) {
        try {
            String disposition = part.getDisposition();

            if (disposition != null
                    && (disposition.equalsIgnoreCase(Part.ATTACHMENT) || disposition.equalsIgnoreCase(Part.INLINE))) {// ����
                String fileName = decodeText(part.getFileName());
                System.out.println(fileName);
                saveAttachFile(part);// ���渽��
            } else {// ����
                if (part.getContent() instanceof String) {// ���յ��Ĵ��ı�
                    System.out.println(part.getContent());
                }
                if (part.getContent() instanceof MimeMultipart) {// ���յ��ʼ��и���ʱ
                    BodyPart bodyPart = ((MimeMultipart) part.getContent()).getBodyPart(0);
                    System.out.println(bodyPart.getContent());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * ���渽��
     * 
     * @param part
     */
    public static void saveAttachFile(Part part) {
        try {
            if (part.getDisposition() == null)
                return;

            String dir = "D:/mail/";
            String filename = decodeText(part.getFileName());

            InputStream in = part.getInputStream();
            OutputStream out = new FileOutputStream(new File(dir + filename));

            byte[] buffer = new byte[8192];
            while (in.read(buffer) != -1) {
                out.write(buffer);
            }

            in.close();
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * ȡ�ʼ���Ϣ
     * 
     * @throws Exception
     */
    public static void fetchMail() throws Exception {
        // Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
        Session session = Session.getDefaultInstance(GmailProperties.getProperties(), null);
        // ��pop3Э�飺new URLName("pop3", "pop.gmail.com", 995, null,"[�����ʺ�]",
        // "[��������]");
        // ��IMAPЭ��
        URLName urln = new URLName("imap",
                                   "imap.gmail.com",
                                   995,
                                   null,
                                   "dongpf.lab",
                                   "19860306");
        Store store = null;
        Folder inbox = null;
        try {
            // Store��������,Store��ʵ���ض��ʼ�Э���ϵĶ���д�����ӡ����ҵȲ�����
            store = session.getStore(urln);
            store.connect();
            inbox = store.getFolder("INBOX");// �ռ���
            inbox.open(Folder.READ_ONLY);
            FetchProfile profile = new FetchProfile();
            profile.add(FetchProfile.Item.ENVELOPE);
            Message[] messages = inbox.getMessages();
            inbox.fetch(messages, profile);
            System.out.println("�ռ�����ʼ���" + messages.length);
            System.out.println("δ���ʼ���" + inbox.getUnreadMessageCount());
            System.out.println("���ʼ���" + inbox.getNewMessageCount());

            for (int i = 0; i < messages.length; i++) {
                // �ʼ�������
                String from = decodeText(messages[i].getFrom()[0].toString());
                InternetAddress ia = new InternetAddress(from);
                System.out.println("FROM:" + ia.getPersonal() + '('
                        + ia.getAddress() + ')');
                // �ʼ�����
                System.out.println("TITLE:" + messages[i].getSubject());
                // �ʼ�����
                parseMailContent(messages[i].getContent());

                // �ʼ���С
                System.out.println("SIZE:" + messages[i].getSize());
                // �ʼ�����ʱ��
                System.out.println("DATE:" + messages[i].getSentDate());
            }
        } finally {
            try {
                inbox.close(false);
            } catch (Exception e) {
            }
            try {
                store.close();
            } catch (Exception e) {
            }
        }
    }

}
