package com.somedamnbrain.systems;

import java.util.List;

import com.somedamnbrain.diagnostic.Diagnostic;

/**
 * In somedamnbrain, a system describes a piece of infrastructure. A system
 * depends on other systems and you can run diagnostics on a given system.
 * 
 * @author clement
 *
 */
public interface SDBSystem {

	/**
	 * A name for the system.
	 * 
	 * @return
	 */
	String getUniqueID();

	/**
	 * Return the dependencies of the system. Ideally run the diagnostics of
	 * every dependencies of a system before running the ones of a system. I
	 * 
	 * @return a list of system this system depends on
	 */
	List<SDBSystem> getDependencies();

	/**
	 * Return a list of diagnostics to be run on the system. If a dependency
	 * system (see getDependencies()) is in an unrecoverable error, you can run
	 * the diagnostic of this system, but should not attempt any correction as
	 * the result would be undefined.
	 * 
	 * @return a list of diagnostic to run on this system.
	 */
	List<Diagnostic> getDiagnostics();

	/**
	 * This method is called after the full diagnostic of the system, if and
	 * only if the system is operational. That if, no unrecoverable failures
	 * were encountered during the diagnostic run.
	 */
	void executeIfOperational();

}
