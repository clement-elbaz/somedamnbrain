package com.somedamnbrain.systems.mail.diagnostics;

import com.google.inject.Inject;
import com.somedamnbrain.diagnostic.CorrectiveAction;
import com.somedamnbrain.diagnostic.Diagnostic;
import com.somedamnbrain.entities.Entities.DiagnosticResult;
import com.somedamnbrain.exceptions.NoResultException;
import com.somedamnbrain.exceptions.UnexplainableException;
import com.somedamnbrain.services.universe.UniverseService;

public class MailConfigPresent implements Diagnostic {

	private final UniverseService universeService;

	@Inject
	public MailConfigPresent(final UniverseService universeService) {
		this.universeService = universeService;
	}

	@Override
	public String getUniqueID() {
		return "Checking existence of mail configuration";
	}

	@Override
	public DiagnosticResult attemptDiagnostic() throws UnexplainableException {
		try {
			this.universeService.getConfig("mail");
			return this.newResult(true, "mail-config-OK", "Mail config is present", universeService);
		} catch (final NoResultException e) {
			return this.newResult(false, "mail-config-missing", "Mail config is missing", universeService);
		}

	}

	@Override
	public CorrectiveAction getCorrection(final DiagnosticResult diagnosticResult) throws NoResultException {
		throw new NoResultException();
	}

}
