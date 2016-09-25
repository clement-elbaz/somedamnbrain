package com.somedamnbrain.systems.mail;

import java.util.Arrays;
import java.util.List;

import com.google.inject.Inject;
import com.somedamnbrain.diagnostic.Diagnostic;
import com.somedamnbrain.exceptions.UnexplainableException;
import com.somedamnbrain.services.universe.UniverseService;
import com.somedamnbrain.systems.AbstractSystem;
import com.somedamnbrain.systems.SDBSystem;
import com.somedamnbrain.systems.universe.LocalUniverseSystem;

public class MailSystem extends AbstractSystem {

	private final UniverseService universeService;

	private final LocalUniverseSystem universeSystem;

	@Inject
	public MailSystem(final UniverseService universeService, final LocalUniverseSystem universeSystem) {
		this.universeService = universeService;
		this.universeSystem = universeSystem;
	}

	@Override
	public String getUniqueID() {
		return "Mail";
	}

	@Override
	public List<SDBSystem> getDependencies() {
		return Arrays.asList(universeSystem);
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
