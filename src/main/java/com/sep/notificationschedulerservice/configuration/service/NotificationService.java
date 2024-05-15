package com.sep.notificationschedulerservice.configuration.service;

import com.sep.notificationschedulerservice.configuration.dto.commonemail.CommonEmailRequest;
import com.sep.notificationschedulerservice.configuration.enums.EmailType;
import com.sep.notificationschedulerservice.configuration.exception.TSMSException;

public interface NotificationService {

	public Boolean sendNotification(String subject, String to, String[] cc, String body, String requestId)
			throws TSMSException;

	public Boolean sendEmail(CommonEmailRequest commonEmailRequest, EmailType emailType, String requestId)
			throws TSMSException;

//	public Boolean sendAccountApprovalEmail(CommonEmailRequest commonEmailRequest, String requestId)
//			throws TSMSException;
}
