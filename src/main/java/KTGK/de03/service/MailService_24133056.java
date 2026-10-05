package KTGK.de03.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

public class MailService_24133056 {
    private final Properties settings = new Properties();

    public MailService_24133056() {
        try (InputStream input = MailService_24133056.class.getClassLoader().getResourceAsStream("mail.properties")) {
            if (input == null) throw new IllegalStateException("Không tìm thấy mail.properties");
            settings.load(input);
        } catch (IOException e) {
            throw new IllegalStateException("Không thể đọc cấu hình email", e);
        }
    }

    public void sendOtp(String recipient, String otp) {
        String username = required("mail.username");
        String password = required("mail.password");
        Properties smtp = new Properties();
        smtp.put("mail.smtp.host", required("mail.smtp.host"));
        smtp.put("mail.smtp.port", required("mail.smtp.port"));
        smtp.put("mail.smtp.auth", required("mail.smtp.auth"));
        smtp.put("mail.smtp.starttls.enable", required("mail.smtp.starttls.enable"));
        smtp.put("mail.smtp.starttls.required", required("mail.smtp.starttls.required"));
        Session session = Session.getInstance(smtp, new Authenticator() {
            @Override protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username, settings.getProperty("mail.from.name"), StandardCharsets.UTF_8.name()));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
            message.setSubject("Mã OTP kích hoạt tài khoản", StandardCharsets.UTF_8.name());
            message.setText("Mã OTP của bạn là: " + otp + "\nMã có hiệu lực trong 5 phút. Không cung cấp mã này cho người khác.", StandardCharsets.UTF_8.name());
            Transport.send(message);
        } catch (MessagingException | IOException e) {
            throw new IllegalStateException("Không thể gửi OTP. Vui lòng kiểm tra cấu hình Gmail.", e);
        }
    }

    private String required(String key) {
        String value = settings.getProperty(key);
        if (value == null || value.isBlank()) throw new IllegalStateException("Thiếu cấu hình: " + key);
        return value.trim();
    }
}
