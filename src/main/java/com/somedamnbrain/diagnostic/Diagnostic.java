package com.somedamnbrain.diagnostic;

import java.util.ArrayList;
import java.util.List;

import com.somedamnbrain.entities.Entities.DiagnosticResult;
import com.somedamnbrain.exceptions.NoResultException;
import com.somedamnbrain.exceptions.UnexplainableException;
import com.somedamnbrain.services.universe.UniverseService;
import com.somedamnbrain.systems.SDBSystem;

/**
 * In somedamnbrain, a diagnostic is a monitoring action on a system. Diagnostic
 * should be idempotent and leave the situation unchanged.
 * 
 * @author clement
 *
 */
public interface Diagnostic {

	/**
	 * Get a unique id (description) of the diagnostic. Please stay alphanumeric
	 * + space there. Ex : "Checking universe file existence on the system"
	 * 
	 * @return
	 */
	String getUniqueID();

	/**
	 * Start the diagnostic and return a DiagnosticResult. Diagnostic should be
	 * idempotent and leave the situation unchanged.
	 * 
	 * @throws UnexplainableException
	 *             if something unexpected occured during the correction
	 */
	DiagnosticResult attemptDiagnostic() throws UnexplainableException;

	/**
	 * Return a correction for the diagnostic.
	 * 
	 * @param diagnosticResult
	 *            diagnostic result.
	 * @return a correction
	 */
	CorrectiveAction getCorrection(DiagnosticResult diagnosticResult) throws NoResultException;

	/**
	 * Initiate a new diagnostic result.
	 * 
	 * @param success
	 *            true if the diagnostic is in success
	 * @param machineMessage
	 *            the machine-readable message.
	 * @param humanMessage
	 *            the human readable message.
	 * @param universeService
	 *            the universe service.
	 * @return a Diagnosticresult object
	 */
	public default DiagnosticResult newResult(final boolean success, final String machineMessage,
			final String humanMessage, final UniverseService universeService) {
		DiagnosticResult.Builder result = DiagnosticResult.newBuilder();
		result.setSuccess(success);
		result.setHumanMessage(humanMessage);
		result.setMachineMessage(machineMessage);
		result.setStability(universeService.computeStability(this, machineMessage));
		result.setDiagnosticId(this.getUniqueID());
		return result.build();
	}

	/**
	 * A generic static method allowing to quickly provide a failing "not
	 * implemented" diagnostic for a system.
	 * 
	 * @param system
	 *            system
	 * @param universeService
	 *            universe service
	 * @return a list of diagnostic containing one failing "not implemented"
	 *         diagnostic.
	 */
	public static List<Diagnostic> notImplemented(SDBSystem system, UniverseService universeService) {
		List<Diagnostic> result = new ArrayList<Diagnostic>();

		result.add(new Diagnostic() {

			@Override
			public String getUniqueID() {
				return system.getUniqueID() + "-diagnostic-not-implemented";
			}

			@Override
			public DiagnosticResult attemptDiagnostic() throws UnexplainableException {
				return this.newResult(false, "not-implemented", "No diagnostic implemented for this system",
						universeService);
			}

			@Override
			public CorrectiveAction getCorrection(DiagnosticResult diagnosticResult) throws NoResultException {
				throw new NoResultException();
			}

		});

		return result;
	}

}
