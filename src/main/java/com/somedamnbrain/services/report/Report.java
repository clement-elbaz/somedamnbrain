package com.somedamnbrain.services.report;

public class Report {

	private final String subject;

	private final String content;

	public Report(final String subject, final String content) {
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
