package com.somedamnbrain.systems.mail;

import java.util.Arrays;
import java.util.List;

import com.google.inject.Inject;
import com.somedamnbrain.diagnostic.Diagnostic;
import com.somedamnbrain.exceptions.UnexplainableException;
import com.somedamnbrain.systems.AbstractSystem;
import com.somedamnbrain.systems.SDBSystem;
import com.somedamnbrain.systems.mail.diagnostics.MailConfigPresent;
import com.somedamnbrain.systems.universe.LocalUniverseSystem;

public class MailSystem extends AbstractSystem {
	private final LocalUniverseSystem universeSystem;

	private final MailConfigPresent mailConfigPresentDiagnostic;

	@Inject
	public MailSystem(final LocalUniverseSystem universeSystem, final MailConfigPresent mailConfigPresentDiagnostic) {
		this.universeSystem = universeSystem;
		this.mailConfigPresentDiagnostic = mailConfigPresentDiagnostic;
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
		return Arrays.asList(mailConfigPresentDiagnostic);
	}

	@Override
	public void executeIfOperational() throws UnexplainableException {
		// TODO

	}

}
