package com.mail;

public interface MailService {
	void sendOrderConfirmationEmail(String to, Integer ordersNo, Integer ordersTotal);
	
	 // 新增發送重設密碼郵件方法
    void sendPasswordResetEmail(String to, String resetUrl);
}