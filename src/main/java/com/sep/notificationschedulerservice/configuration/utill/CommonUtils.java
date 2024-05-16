package com.sep.notificationschedulerservice.configuration.utill;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sep.notificationschedulerservice.configuration.dto.commonemail.CommonEmailRequest;

public class CommonUtils {

	public static String convertToString(Object object) {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.registerModule(new JavaTimeModule());
			return objectMapper.writeValueAsString(object);
		} catch (Exception e) {
			return null;
		}
	}

	public static String getExecutionTime(long startTime) {
		long endTime = System.currentTimeMillis();
		return String.format("%d ms", (endTime - startTime));
	}

	public static boolean haveEmptySpace(String string) {
		Pattern whitespace = Pattern.compile("\\s");
		Matcher matcher = whitespace.matcher(string);
		if (matcher.find()) {
			return true;
		}
		return false;
	}

	public static boolean checkAccountActivationMandtoryFieldsNullOrEmpty(CommonEmailRequest request) {
		return !((request.getRecipientName() == null || request.getRecipientName().isEmpty()
				|| request.getRecipientName().isBlank())
				|| (request.getActivationCode() == null || request.getActivationCode().isEmpty()
						|| request.getActivationCode().isBlank())
				|| (request.getRecipientEmail() == null || request.getRecipientEmail().isEmpty()
						|| request.getRecipientEmail().isBlank()));
	}

	public static boolean checkAccountApprovalMandtoryFieldsNullOrEmpty(CommonEmailRequest request) {
		return !((request.getRecipientName() == null || request.getRecipientName().isEmpty()
				|| request.getRecipientName().isBlank())
				|| (request.getRecipientEmail() == null || request.getRecipientEmail().isEmpty()
						|| request.getRecipientEmail().isBlank())
				|| (request.getApprovalStatus() == null));
	}

	public static boolean checkSendOTPMandtoryFieldsNullOrEmpty(CommonEmailRequest request) {
		return !((request.getRecipientName() == null || request.getRecipientName().isEmpty()
				|| request.getRecipientName().isBlank())
				|| (request.getRecipientEmail() == null || request.getRecipientEmail().isEmpty()
						|| request.getRecipientEmail().isBlank())
				|| (request.getOtp() == null || request.getOtp().isEmpty() || request.getOtp().isBlank()));
	}

	public static boolean checkSendPwdResetAndWelcomeMandtoryFieldsNullOrEmpty(CommonEmailRequest request) {
		return !((request.getRecipientName() == null || request.getRecipientName().isEmpty()
				|| request.getRecipientName().isBlank())
				|| (request.getRecipientEmail() == null || request.getRecipientEmail().isEmpty()
						|| request.getRecipientEmail().isBlank()));
	}

}
