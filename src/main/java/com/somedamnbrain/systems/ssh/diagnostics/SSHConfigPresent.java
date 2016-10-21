package com.somedamnbrain.systems.ssh.diagnostics;

import com.google.inject.Inject;
import com.somedamnbrain.diagnostic.CorrectiveAction;
import com.somedamnbrain.diagnostic.Diagnostic;
import com.somedamnbrain.entities.Entities.DiagnosticResult;
import com.somedamnbrain.exceptions.NoResultException;
import com.somedamnbrain.exceptions.UnexplainableException;
import com.somedamnbrain.services.universe.ConfigService;
import com.somedamnbrain.services.universe.UniverseService;

public class SSHConfigPresent implements Diagnostic {

	private final UniverseService universeService;
	private final ConfigService configService;

	@Inject
	public SSHConfigPresent(final UniverseService universeService, final ConfigService configService) {
		this.universeService = universeService;
		this.configService = configService;
	}

	@Override
	public String getUniqueID() {
		return "Checking existence of SSH configuration";
	}

	@Override
	public DiagnosticResult attemptDiagnostic() throws UnexplainableException {
		try {
			this.configService.getConfig("ssh");
			return this.newResult(true, "ssh-config-OK", "SSH config is present", universeService);
		} catch (final NoResultException e) {
			return this.newResult(false, "ssh-config-missing", "SSH config is missing", universeService);
		}
	}

	@Override
	public CorrectiveAction getCorrection(final DiagnosticResult diagnosticResult) throws NoResultException {
		// TODO
		throw new NoResultException();
	}

}
