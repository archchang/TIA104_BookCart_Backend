package com.mail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Component
public class MailServiceImpl implements MailService {
	
	@Autowired
	private JavaMailSender mailSender;
	
	@Override
	public void sendOrderConfirmationEmail(String to, Integer ordersNo, Integer ordersTotal) {
		try {
			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true);
			
			helper.setTo(to);
			helper.setSubject("訂單成立通知 - 訂單編號: " + ordersNo);
			
			String content = String.format(
				"親愛的顧客您好，\n\n" +
			    "您的訂單已成功建立！\n" +
			    "訂單編號: %d\n" +
			    "訂單金額: %d 元\n\n" +
			    "感謝您的購買！",
			    ordersNo, ordersTotal
			);
			
			helper.setText(content);
//			mailSender.send(message);
			new Thread(()->mailSender.send(message)).start();
			
		} catch (MessagingException e) {
			throw new RuntimeException("發送郵件失敗", e);
		}
	}
	
	@Override
    public void sendPasswordResetEmail(String to, String resetUrl) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            
            helper.setTo(to);
            helper.setSubject("重設密碼通知");
            
            String content = String.format(
                "親愛的會員您好，\n\n" +
                "請點擊以下連結重設密碼：\n%s\n\n" +
                "此連結將在5分鐘後失效。\n\n" +
                "如果您沒有要求重設密碼，請忽略此郵件。",
                resetUrl
            );
            
            helper.setText(content);
//            mailSender.send(message);
            new Thread(()->mailSender.send(message)).start();
            
        } catch (MessagingException e) {
            throw new RuntimeException("發送重設密碼郵件失敗", e);
        }
    }
}