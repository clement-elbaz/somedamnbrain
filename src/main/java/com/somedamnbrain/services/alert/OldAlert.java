package com.somedamnbrain.services.alert;

public class OldAlert {

	private final String subject;

	private final String content;

	public OldAlert(final String subject, final String content) {
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
