package com.somedamnbrain.services.alert;

public class Alert {

	private final String subject;

	private final String content;

	public Alert(final String subject, final String content) {
		this.subject = subject;
		this.content = content;

	}

	public String getSubject() {
		return subject;
	}

	public String getContent() {
		return content;
	}

}
