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

import com.sep.notificationschedulerservice.configuration.dto.commonemail.CommonEmailRequest;
import com.sep.notificationschedulerservice.configuration.enums.ApprovalStatus;
import com.sep.notificationschedulerservice.configuration.enums.EmailType;
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
		Boolean notificationSendStatus;

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
			LOGGER.error("ERROR [SERVICE-LAYER] [RequestId={}]  sendNotification : exception={}", requestId,
					e.getMessage());
			e.printStackTrace();
		}

		LOGGER.info("END [SERVICE-LAYER] [RequestId={}] sendNotification: timeTaken={}|response={}", requestId,
				CommonUtils.getExecutionTime(startTime), CommonUtils.convertToString(notificationSendStatus));
		return notificationSendStatus;

	}

	@Override
	public Boolean sendEmail(CommonEmailRequest commonEmailRequest, EmailType emailType, String requestId)
			throws TSMSException {

		long startTime = System.currentTimeMillis();
		LOGGER.info("START [SERVICE-LAYER] [RequestId={}] sendEmail: request={}|emailType={}", requestId,
				CommonUtils.convertToString(commonEmailRequest), emailType);

		Boolean emailSendStatus;
		String subject = null;
		String htmlContent = null;

		try {

			if (emailType.equals(EmailType.ACCOUNT_ACTIVATION)) {

				subject = "Activate Your Travel Trek Account Now!";
				htmlContent = generateAccountActivationEmailBody(commonEmailRequest.getRecipientName(),
						commonEmailRequest.getActivationCode(), supportEmail);

			} else if (emailType.equals(EmailType.ACCOUNT_APPROVAL)) {

				subject = "Travel Trek Account Approval Status!";
				htmlContent = generateAccountApprovalEmailBody(commonEmailRequest.getRecipientName(), supportEmail,
						commonEmailRequest.getApprovalStatus());

			} else if (emailType.equals(EmailType.SEND_OTP)) {
				// TODO

			} else {
				emailSendStatus = Boolean.FALSE;
			}

		} catch (Exception e) {

			LOGGER.error("ERROR [SERVICE-LAYER] [RequestId={}]  sendEmail : exception={}", requestId, e.getMessage());
			e.printStackTrace();
			throw new TSMSException(TSMSError.EMAIL_SENDING_FAILED);

		}

		sendEmail(commonEmailRequest, subject, htmlContent, requestId);
		emailSendStatus = Boolean.TRUE;

		LOGGER.info("END [SERVICE-LAYER] [RequestId={}] sendEmail: timeTaken={}|response={}", requestId,
				CommonUtils.getExecutionTime(startTime), CommonUtils.convertToString(emailSendStatus));
		return emailSendStatus;
	}

//	@Override
//	public Boolean sendAccountApprovalEmail(CommonEmailRequest commonEmailRequest, String requestId)
//			throws TSMSException {
//
//		long startTime = System.currentTimeMillis();
//		LOGGER.info("START [SERVICE-LAYER] [RequestId={}] sendAccountApprovalEmail: recipientName={}|recipientEmail={}",
//				requestId, commonEmailRequest.getRecipientName(), commonEmailRequest.getRecipientEmail());
//
//		Boolean emailSendStatus;
//
//		try {
//
//		} catch (Exception e) {
//			emailSendStatus = Boolean.FALSE;
//			LOGGER.error("ERROR [SERVICE-LAYER] [RequestId={}]  sendAccountApprovalEmail : exception={}", requestId,
//					e.getMessage());
//			e.printStackTrace();
//			throw new TSMSException(TSMSError.ACCOUNT_ACTIVATION_EMAIL_SENDING_FAILED);
//
//		}
//
//		LOGGER.info("END [SERVICE-LAYER] [RequestId={}] sendAccountApprovalEmail: timeTaken={}|response={}", requestId,
//				CommonUtils.getExecutionTime(startTime), CommonUtils.convertToString(emailSendStatus));
//		return emailSendStatus;
//	}

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

	private void sendEmail(CommonEmailRequest commonEmailRequest, String subject, String htmlContent, String requestId)
			throws TSMSException {

		MimeMessage mimeMessage = javaMailSender.createMimeMessage();

		MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, "UTF-8");

		try {
			mimeMessageHelper.setFrom(fromMailAddress);
			mimeMessageHelper.setTo(commonEmailRequest.getRecipientEmail());
			mimeMessageHelper.setSubject(subject);
			if (htmlContent != null) {
				mimeMessageHelper.setText(htmlContent, true);
			}

			javaMailSender.send(mimeMessage);

		} catch (Exception e) {

			LOGGER.error("ERROR [SERVICE-LAYER] [RequestId={}]  sendAccountActivationEmail : exception={}", requestId,
					e.getMessage());
			e.printStackTrace();
			throw new TSMSException(TSMSError.EMAIL_SENDING_FAILED);

		}

	}

}
