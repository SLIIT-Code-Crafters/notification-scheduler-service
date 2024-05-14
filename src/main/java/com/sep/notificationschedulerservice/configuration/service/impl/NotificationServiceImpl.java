package com.sep.notificationschedulerservice.configuration.service.impl;

import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.sep.notificationschedulerservice.configuration.enums.ApprovalStatus;
import com.sep.notificationschedulerservice.configuration.exception.TSMSError;
import com.sep.notificationschedulerservice.configuration.exception.TSMSException;
import com.sep.notificationschedulerservice.configuration.service.NotificationService;
import com.sep.notificationschedulerservice.configuration.utill.CommonUtils;

@Service
public class NotificationServiceImpl implements NotificationService {

	@Value("${spring.mail.username}")
	private String fromMailAddress;

	@Value("${supportEmail}")
	private String supportEmail;

	@Autowired
	private JavaMailSender javaMailSender;

	@Autowired
	private TemplateEngine templateEngine;

	private static final Logger LOGGER = LoggerFactory.getLogger(NotificationServiceImpl.class);

	// TODO Need to reactor this method in sprint 3-4
	@Override
	public Boolean sendNotification(String subject, String to, String[] cc, String body, String requestId)
			throws TSMSException {

		long startTime = System.currentTimeMillis();
		LOGGER.info("START [SERVICE-LAYER] [RequestId={}] sendNotification: subject={}|to={}|cc={}|body={}", requestId,
				subject, to, CommonUtils.convertToString(cc), CommonUtils.convertToString(body));

		String recipientName = "Notification Service";
		Boolean notificationSendStatus = Boolean.FALSE;

		try {
			MimeMessage mimeMessage = javaMailSender.createMimeMessage();

			MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, "UTF-8");

			mimeMessageHelper.setFrom(fromMailAddress);
			mimeMessageHelper.setTo(to);
			mimeMessageHelper.setCc(cc);
			mimeMessageHelper.setSubject(subject);

			String htmlContent = generateBasicNotificationBody(recipientName, body, supportEmail);
			mimeMessageHelper.setText(htmlContent, true);

			javaMailSender.send(mimeMessage);
			notificationSendStatus = Boolean.TRUE;

		} catch (Exception e) {
			notificationSendStatus = Boolean.FALSE;
			// TODO: handle exception
			LOGGER.error("ERROR [SERVICE-LAYER] [RequestId={}]  sendNotification : exception={}", requestId,
					e.getMessage());
			e.printStackTrace();
//			throw new TSMSException(TSMSError.REGISTRATION_FAILED);
		}

		LOGGER.info("END [SERVICE-LAYER] [RequestId={}] sendNotification: timeTaken={}|response={}", requestId,
				CommonUtils.getExecutionTime(startTime), CommonUtils.convertToString(notificationSendStatus));
		return notificationSendStatus;

	}

	@Override
	public Boolean sendAccountActivationEmail(String recipientName, String recipientEmail, String activationCode,
			String requestId) throws TSMSException {

		long startTime = System.currentTimeMillis();
		LOGGER.info(
				"START [SERVICE-LAYER] [RequestId={}] sendAccountActivationEmail: recipientName={}|recipientEmail={}|activationCode={}",
				requestId, recipientName, recipientEmail, activationCode);

		Boolean emailSendStatus = Boolean.FALSE;
		String subject = "Activate Your Travel Trek Account Now!";

		try {
			MimeMessage mimeMessage = javaMailSender.createMimeMessage();

			MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, "UTF-8");

			mimeMessageHelper.setFrom(fromMailAddress);
			mimeMessageHelper.setTo(recipientEmail);
			mimeMessageHelper.setSubject(subject);

			String htmlContent = generateAccountActivationEmailBody(recipientName, activationCode, supportEmail);
			mimeMessageHelper.setText(htmlContent, true);

			javaMailSender.send(mimeMessage);
			emailSendStatus = Boolean.TRUE;

		} catch (Exception e) {
			LOGGER.error("ERROR [SERVICE-LAYER] [RequestId={}]  sendAccountActivationEmail : exception={}", requestId,
					e.getMessage());
			e.printStackTrace();
			throw new TSMSException(TSMSError.ACCOUNT_ACTIVATION_EMAIL_SENDING_FAILED);

		}

		LOGGER.info("END [SERVICE-LAYER] [RequestId={}] sendAccountActivationEmail: timeTaken={}|response={}",
				requestId, CommonUtils.getExecutionTime(startTime), CommonUtils.convertToString(emailSendStatus));
		return emailSendStatus;
	}

	@Override
	public Boolean sendAccountApprovalEmail(String recipientName, String recipientEmail, ApprovalStatus approvalStatus,
			String requestId) throws TSMSException {

		long startTime = System.currentTimeMillis();
		LOGGER.info("START [SERVICE-LAYER] [RequestId={}] sendAccountApprovalEmail: recipientName={}|recipientEmail={}",
				requestId, recipientName, recipientEmail);

		Boolean emailSendStatus = Boolean.FALSE;
		String subject = "Travel Trek Account Approval Status!";

		try {
			MimeMessage mimeMessage = javaMailSender.createMimeMessage();

			MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, "UTF-8");

			mimeMessageHelper.setFrom(fromMailAddress);
			mimeMessageHelper.setTo(recipientEmail);

			mimeMessageHelper.setSubject(subject);

			String htmlContent = generateAccountApprovalEmailBody(recipientName, supportEmail, approvalStatus);
			if (htmlContent != null) {
				mimeMessageHelper.setText(htmlContent, true);
			}

			javaMailSender.send(mimeMessage);
			emailSendStatus = Boolean.TRUE;

		} catch (Exception e) {
			LOGGER.error("ERROR [SERVICE-LAYER] [RequestId={}]  sendAccountApprovalEmail : exception={}", requestId,
					e.getMessage());
			e.printStackTrace();
			throw new TSMSException(TSMSError.ACCOUNT_ACTIVATION_EMAIL_SENDING_FAILED);

		}

		LOGGER.info("END [SERVICE-LAYER] [RequestId={}] sendAccountApprovalEmail: timeTaken={}|response={}", requestId,
				CommonUtils.getExecutionTime(startTime), CommonUtils.convertToString(emailSendStatus));
		return emailSendStatus;
	}

	private String generateAccountActivationEmailBody(String recipientName, String activationCode,
			String supportEmail) {
		Context context = new Context();
		context.setVariable("recipientName", recipientName);
		context.setVariable("activationCode", activationCode);
		context.setVariable("supportEmail", supportEmail);

		return templateEngine.process("account-activation-email-template", context);
	}

	private String generateAccountApprovalEmailBody(String recipientName, String supportEmail,
			ApprovalStatus approvalStatus) {

		Context context = new Context();
		context.setVariable("recipientName", recipientName);
		context.setVariable("supportEmail", supportEmail);

		if (approvalStatus.equals(ApprovalStatus.APPROVED)) {
			return templateEngine.process("account-approval-email-template", context);
		} else if (approvalStatus.equals(ApprovalStatus.REJECTED)) {
			return templateEngine.process("account-reject-email-template", context);
		} else {
			return null;
		}

	}

	private String generateBasicNotificationBody(String recipientName, String message, String supportEmail) {
		Context context = new Context();
		context.setVariable("recipientName", recipientName);
		context.setVariable("message", message);
		context.setVariable("supportEmail", supportEmail);

		return templateEngine.process("basic-notification-template", context);
	}

}
