package com.sep.notificationschedulerservice.configuration.dto.notification;

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
public class NotificationRequest {

	private String subject;

	private String to;

	private String[] cc;

	private String body;

}
