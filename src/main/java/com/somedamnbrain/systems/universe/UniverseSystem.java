package com.somedamnbrain.systems.universe;

import java.util.Collections;
import java.util.List;

import com.somedamnbrain.diagnostic.Diagnostic;
import com.somedamnbrain.systems.SDBSystem;

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
public class UniverseSystem implements SDBSystem {
	/** Path to the universe file. */
	public static final String UNIVERSE_FILE_PATH = "~/.somedamnbrain-universe";

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.somedamnbrain.systems.SDBSystem#getUniqueID()
	 */
	@Override
	public String getUniqueID() {
		return "Universe";
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
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.somedamnbrain.systems.SDBSystem#executeIfOperational()
	 */
	@Override
	public void executeIfOperational() {
		// TODO Auto-generated method stub

	}

}
