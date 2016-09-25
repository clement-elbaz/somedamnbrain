package com.somedamnbrain.systems.universe;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.somedamnbrain.diagnostic.Diagnostic;
import com.somedamnbrain.exceptions.UnexplainableException;
import com.somedamnbrain.services.universe.UniverseService;
import com.somedamnbrain.systems.AbstractSystem;
import com.somedamnbrain.systems.SDBSystem;
import com.somedamnbrain.systems.universe.diagnostics.ExistenceDiagnostic;

/**
 * In somedamnbrain, the Universe system take care of the Universe file and its
 * content.
 * 
 * The Universe file contains the configuration of the somedamnbrain instance,
 * such as : the name of the universe (QUALIF, PROD, etc.), the SSH informations
 * to connect to the host machine, etc.
 * 
 * @author clement
 *
 */
@Singleton
public class LocalUniverseSystem extends AbstractSystem {
	/** Path to the universe file. */
	public static final String UNIVERSE_FILE_PATH = "~/.somedamnbrain-universe";

	private final ExistenceDiagnostic existenceDiagnostic;

	private final UniverseService universeService;

	@Inject
	public LocalUniverseSystem(final ExistenceDiagnostic existenceDiagnostic, final UniverseService universeService) {
		this.existenceDiagnostic = existenceDiagnostic;
		this.universeService = universeService;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.somedamnbrain.systems.SDBSystem#getUniqueID()
	 */
	@Override
	public String getUniqueID() {
		return "LocalUniverse";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.somedamnbrain.systems.SDBSystem#getDependencies()
	 */
	@Override
	public List<SDBSystem> getDependencies() {
		// No dependencies.
		return Collections.<SDBSystem>emptyList();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.somedamnbrain.systems.SDBSystem#getDiagnostics()
	 */
	@Override
	public List<Diagnostic> getDiagnostics() {
		return Arrays.asList(existenceDiagnostic);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.somedamnbrain.systems.SDBSystem#executeIfOperational()
	 */
	@Override
	public void executeIfOperational() throws UnexplainableException {
		this.universeService.configureUniverse();

	}

}
