package com.sep.notificationschedulerservice.configuration.utill;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sep.notificationschedulerservice.configuration.dto.accountactivation.AccountActivationRequest;

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

	public static boolean checkAccountActivationMandtoryFieldsNullOrEmpty(AccountActivationRequest request) {
		return !((request.getRecipientName() == null || request.getRecipientName().isEmpty()
				|| request.getRecipientName().isBlank() || request.getRecipientName().equals(""))
				|| (request.getActivationCode() == null || request.getActivationCode().isEmpty()
						|| request.getActivationCode().isBlank() || request.getActivationCode().equals(""))
				|| (request.getRecipientEmail() == null || request.getRecipientEmail().isEmpty()
						|| request.getRecipientEmail().isBlank() || request.getRecipientEmail().equals("")));
	}

}
