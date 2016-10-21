package com.somedamnbrain.systems.mail.diagnostics;

import com.google.inject.Inject;
import com.somedamnbrain.diagnostic.CorrectiveAction;
import com.somedamnbrain.diagnostic.Diagnostic;
import com.somedamnbrain.entities.Entities.DiagnosticResult;
import com.somedamnbrain.exceptions.NoResultException;
import com.somedamnbrain.exceptions.UnexplainableException;
import com.somedamnbrain.services.universe.ConfigService;
import com.somedamnbrain.services.universe.DiagnosticStateService;
import com.somedamnbrain.systems.mail.corrections.CreateMailConfig;

public class MailConfigPresent implements Diagnostic {

	private final DiagnosticStateService diagnosticStateService;
	private final ConfigService configService;
	private final CreateMailConfig createMailConfig;

	@Inject
	public MailConfigPresent(final DiagnosticStateService diagnosticStateService, final ConfigService configService,
			final CreateMailConfig createMailConfig) {
		this.diagnosticStateService = diagnosticStateService;
		this.configService = configService;
		this.createMailConfig = createMailConfig;
	}

	@Override
	public String getUniqueID() {
		return "Checking existence of mail configuration";
	}

	@Override
	public DiagnosticResult attemptDiagnostic() throws UnexplainableException {
		try {
			this.configService.getConfig("mail");
			return this.newResult(true, "mail-config-OK", "Mail config is present", diagnosticStateService);
		} catch (final NoResultException e) {
			return this.newResult(false, "mail-config-missing", "Mail config is missing", diagnosticStateService);
		}

	}

	@Override
	public CorrectiveAction getCorrection(final DiagnosticResult diagnosticResult) throws NoResultException {
		return this.createMailConfig;
	}

}
