package com.somedamnbrain.systems.ssh.diagnostics;

import com.google.inject.Inject;
import com.somedamnbrain.diagnostic.CorrectiveAction;
import com.somedamnbrain.diagnostic.Diagnostic;
import com.somedamnbrain.entities.Entities.DiagnosticResult;
import com.somedamnbrain.exceptions.NoResultException;
import com.somedamnbrain.exceptions.UnexplainableException;
import com.somedamnbrain.services.universe.ConfigService;
import com.somedamnbrain.services.universe.DiagnosticStateService;
import com.somedamnbrain.systems.ssh.corrections.CreateSSHConfig;

public class SSHConfigPresent implements Diagnostic {

	private final DiagnosticStateService diagnosticStateService;
	private final ConfigService configService;
	private final CreateSSHConfig createSSHConfig;

	@Inject
	public SSHConfigPresent(final DiagnosticStateService diagnosticStateService, final ConfigService configService,
			final CreateSSHConfig createSSHConfig) {
		this.diagnosticStateService = diagnosticStateService;
		this.configService = configService;
		this.createSSHConfig = createSSHConfig;
	}

	@Override
	public String getUniqueID() {
		return "Checking existence of SSH configuration";
	}

	@Override
	public DiagnosticResult attemptDiagnostic() throws UnexplainableException {
		try {
			this.configService.getConfig("ssh");
			return this.newResult(true, "ssh-config-OK", "SSH config is present", diagnosticStateService);
		} catch (final NoResultException e) {
			return this.newResult(false, "ssh-config-missing", "SSH config is missing", diagnosticStateService);
		}
	}

	@Override
	public CorrectiveAction getCorrection(final DiagnosticResult diagnosticResult) throws NoResultException {
		return this.createSSHConfig;
	}

}
