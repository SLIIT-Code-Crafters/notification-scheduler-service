package com.sep.notificationschedulerservice.configuration.dto.accountactivation;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccountActivationRequest {

	private String recipientName;

	private String recipientEmail;

	private String activationCode;

}
