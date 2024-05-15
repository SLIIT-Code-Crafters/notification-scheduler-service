package com.sep.notificationschedulerservice.configuration.controller;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sep.notificationschedulerservice.configuration.dto.commonemail.CommonEmailRequest;
import com.sep.notificationschedulerservice.configuration.dto.commonemail.CommonEmailResponse;
import com.sep.notificationschedulerservice.configuration.dto.notification.NotificationRequest;
import com.sep.notificationschedulerservice.configuration.dto.response.TSMSResponse;
import com.sep.notificationschedulerservice.configuration.enums.EmailType;
import com.sep.notificationschedulerservice.configuration.exception.TSMSError;
import com.sep.notificationschedulerservice.configuration.exception.TSMSException;
import com.sep.notificationschedulerservice.configuration.service.NotificationService;
import com.sep.notificationschedulerservice.configuration.utill.CommonUtils;

@RestController
@RequestMapping("/api/v1/private/notification")
public class NotificationController {

	@Autowired
	private NotificationService service;

	private static final Logger LOGGER = LoggerFactory.getLogger(NotificationController.class);

	@PostMapping("/send")
	public ResponseEntity<TSMSResponse> sendNotification(@RequestParam("requestId") String requestId,
			@RequestBody NotificationRequest notificationDto) throws TSMSException {

		long startTime = System.currentTimeMillis();
		LOGGER.info("START [REST-LAYER] [RequestId={}] sendNotification: request={}", requestId,
				CommonUtils.convertToString(notificationDto));

		TSMSResponse response = new TSMSResponse();
		response.setRequestId(requestId);

		// Service Call.
		Boolean success = service.sendNotification(notificationDto.getSubject(), notificationDto.getTo(),
				notificationDto.getCc(), notificationDto.getBody(), requestId);

		if (success) {
			response.setSuccess(true);
			response.setData(success);
			response.setMessage("Notification Sent Successfully");
			response.setStatus(TSMSError.OK.getStatus());

		} else {

			response.setSuccess(false);
			response.setData(success);
			response.setMessage("Notification Sending Failed");
			response.setStatus(TSMSError.FAILED.getStatus());

		}

		response.setTimestamp(LocalDateTime.now().toString());

		LOGGER.info("END [REST-LAYER] [RequestId={}] sendNotification: timeTaken={}|response={}", requestId,
				CommonUtils.getExecutionTime(startTime), CommonUtils.convertToString(response));
		return ResponseEntity.ok(response);
	}

	@PostMapping("/send/email")
	public ResponseEntity<TSMSResponse> sendEmail(@RequestParam("requestId") String requestId,
			@RequestParam("emailType") EmailType emailType, @RequestBody CommonEmailRequest commonEmailRequest)
			throws TSMSException {

		long startTime = System.currentTimeMillis();
		LOGGER.info("START [REST-LAYER] [RequestId={}] sendEmail: request={}|emailType={}", requestId,
				CommonUtils.convertToString(commonEmailRequest), emailType);

		TSMSResponse response = new TSMSResponse();
		response.setRequestId(requestId);

		Boolean success = Boolean.FALSE;

		CommonEmailResponse commonEmailResponse = new CommonEmailResponse();

		if (emailType.equals(EmailType.ACCOUNT_ACTIVATION)) {

			if (!CommonUtils.checkAccountActivationMandtoryFieldsNullOrEmpty(commonEmailRequest)) {
				LOGGER.error(
						"ERROR [REST-LAYER] [RequestId={}] sendEmail : Mandatory fields are null. Please ensure all required fields are provided",
						requestId);
				throw new TSMSException(TSMSError.MANDOTORY_FIELDS_EMPTY);

			} else {
				// Service Call.
				success = service.sendEmail(commonEmailRequest, emailType, requestId);

				commonEmailResponse.setEmailSendStatus(success);

				if (success.equals(Boolean.TRUE)) {
					response.setMessage("Account Activation Email Sent Successfully");
				} else {
					response.setMessage("Account Activation Email Sending Failed");
				}

			}

		} else if (emailType.equals(EmailType.ACCOUNT_APPROVAL)) {

			if (!CommonUtils.checkAccountApprovalMandtoryFieldsNullOrEmpty(commonEmailRequest)) {
				LOGGER.error(
						"ERROR [REST-LAYER] [RequestId={}] sendEmail : Mandatory fields are null. Please ensure all required fields are provided",
						requestId);
				throw new TSMSException(TSMSError.MANDOTORY_FIELDS_EMPTY);

			} else {
				// Service Call.
				success = service.sendEmail(commonEmailRequest, emailType, requestId);

				commonEmailResponse.setEmailSendStatus(success);

				if (success.equals(Boolean.TRUE)) {
					response.setMessage("Account Approval Email Sent Successfully");
				} else {
					response.setMessage("Account Approval Email Sending Failed");
				}

			}
		} else if (emailType.equals(EmailType.SEND_OTP)) {
			// TODO

		}

		if (success.equals(Boolean.TRUE)) {
			response.setSuccess(true);
			response.setData(commonEmailResponse);
			response.setStatus(TSMSError.OK.getStatus());

		} else {
			response.setSuccess(false);
			response.setStatus(TSMSError.FAILED.getStatus());

		}

		response.setTimestamp(LocalDateTime.now().toString());

		LOGGER.info("END [REST-LAYER] [RequestId={}] sendEmail: timeTaken={}|response={}", requestId,
				CommonUtils.getExecutionTime(startTime), CommonUtils.convertToString(response));
		return ResponseEntity.ok(response);
	}

}
