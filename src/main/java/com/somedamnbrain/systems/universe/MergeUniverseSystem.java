package com.somedamnbrain.systems.universe;

import java.util.Arrays;
import java.util.List;

import com.google.inject.Inject;
import com.somedamnbrain.diagnostic.Diagnostic;
import com.somedamnbrain.exceptions.UnexplainableException;
import com.somedamnbrain.services.universe.DiagnosticStateService;
import com.somedamnbrain.systems.AbstractSystem;
import com.somedamnbrain.systems.SDBSystem;
import com.somedamnbrain.systems.ssh.SSHSystem;

public class MergeUniverseSystem extends AbstractSystem {

	private final LocalUniverseSystem universeSystem;

	private final SSHSystem sshSystem;

	private final DiagnosticStateService diagnosticStateService;

	@Inject
	public MergeUniverseSystem(final LocalUniverseSystem universeSystem, final SSHSystem sshSystem,
			final DiagnosticStateService diagnosticStateService) {
		this.universeSystem = universeSystem;
		this.sshSystem = sshSystem;
		this.diagnosticStateService = diagnosticStateService;
	}

	@Override
	public String getUniqueID() {
		return "MergeUniverse";
	}

	@Override
	public List<SDBSystem> getDependencies() {
		return Arrays.asList(universeSystem, sshSystem);
	}

	@Override
	public List<Diagnostic> getDiagnostics() {
		return Diagnostic.notImplemented(this, diagnosticStateService);
	}

	@Override
	public void executeIfOperational() throws UnexplainableException {
		// TODO

	}

}
