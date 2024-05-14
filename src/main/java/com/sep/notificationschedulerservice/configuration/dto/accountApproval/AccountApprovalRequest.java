package com.sep.notificationschedulerservice.configuration.dto.accountApproval;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.sep.notificationschedulerservice.configuration.enums.ApprovalStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccountApprovalRequest {

	private String recipientName;

	private String recipientEmail;

	private ApprovalStatus approvalStatus;
}
