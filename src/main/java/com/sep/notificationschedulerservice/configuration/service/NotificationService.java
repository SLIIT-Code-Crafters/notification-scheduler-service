package com.sep.notificationschedulerservice.configuration.service;

import com.sep.notificationschedulerservice.configuration.exception.TSMSException;

public interface NotificationService {

	public Boolean sendNotification(String subject, String to, String[] cc, String body, String requestId)
			throws TSMSException;

	public Boolean sendAccountActivationEmail(String recipientName, String recipientEmail, String activationCode,
			String requestId) throws TSMSException;
}
