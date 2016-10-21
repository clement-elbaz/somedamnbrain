package com.somedamnbrain.systems.ssh.diagnostics;

import com.google.inject.Inject;
import com.somedamnbrain.diagnostic.CorrectiveAction;
import com.somedamnbrain.diagnostic.Diagnostic;
import com.somedamnbrain.entities.Entities.DiagnosticResult;
import com.somedamnbrain.exceptions.NoResultException;
import com.somedamnbrain.exceptions.SystemNotAvailableException;
import com.somedamnbrain.exceptions.UnexplainableException;
import com.somedamnbrain.services.ssh.SSHService;
import com.somedamnbrain.services.universe.UniverseService;

public class SSHTestConnection implements Diagnostic {

	private final UniverseService universeService;
	private final SSHService sshService;

	@Inject
	public SSHTestConnection(final UniverseService universeService, final SSHService sshService) {
		this.universeService = universeService;
		this.sshService = sshService;
	}

	@Override
	public String getUniqueID() {
		return "Testing SSH connection";
	}

	@Override
	public DiagnosticResult attemptDiagnostic() throws UnexplainableException {
		try {
			if (sshService.testConnection()) {
				return this.newResult(true, "ssh-connection-OK", "Machine can be reached through SSH", universeService);
			} else {
				return this.newResult(false, "ssh-connection-KO-error", "Machine can not be reached through SSH",
						universeService);
			}
		} catch (final SystemNotAvailableException e) {
			return this.newResult(false, "ssh-connection-KO-not-configured", "SSH Connection is not configured",
					universeService);
		}
	}

	@Override
	public CorrectiveAction getCorrection(final DiagnosticResult diagnosticResult) throws NoResultException {
		throw new NoResultException();
	}

}
