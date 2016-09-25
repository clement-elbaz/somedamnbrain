package com.somedamnbrain.systems.ssh;

import java.util.Arrays;
import java.util.List;

import com.google.inject.Inject;
import com.somedamnbrain.diagnostic.Diagnostic;
import com.somedamnbrain.exceptions.UnexplainableException;
import com.somedamnbrain.services.universe.UniverseService;
import com.somedamnbrain.systems.SDBSystem;
import com.somedamnbrain.systems.mail.MailSystem;

public class SSHSystem implements SDBSystem {

	private final UniverseService universeService;

	private final MailSystem mailSystem;

	@Inject
	public SSHSystem(final UniverseService universeService, final MailSystem mailSystem) {
		this.universeService = universeService;
		this.mailSystem = mailSystem;
	}

	@Override
	public String getUniqueID() {
		return "SSH";
	}

	@Override
	public List<SDBSystem> getDependencies() {
		return Arrays.asList(mailSystem);
	}

	@Override
	public List<Diagnostic> getDiagnostics() {
		return Diagnostic.notImplemented(this, universeService);
	}

	@Override
	public void executeIfOperational() throws UnexplainableException {
		// TODO

	}

}
