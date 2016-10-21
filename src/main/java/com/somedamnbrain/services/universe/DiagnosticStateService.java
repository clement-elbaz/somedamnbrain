package com.somedamnbrain.services.universe;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.inject.Singleton;
import com.somedamnbrain.diagnostic.Diagnostic;
import com.somedamnbrain.entities.Entities.DiagnosticResult;
import com.somedamnbrain.entities.Entities.Universe;
import com.somedamnbrain.exceptions.NoResultException;
import com.somedamnbrain.systems.SDBSystem;

@Singleton
public class DiagnosticStateService {

	private boolean configured;
	private final Map<String, DiagnosticResult> previousDiagnostics;
	private final Map<String, DiagnosticResult> currentDiagnostics;

	public DiagnosticStateService() {
		this.configured = false;
		this.previousDiagnostics = new HashMap<String, DiagnosticResult>();
		this.currentDiagnostics = new HashMap<String, DiagnosticResult>();
	}

	public void configure(final Universe universePreviousIteration) {
		for (final DiagnosticResult result : universePreviousIteration.getDiagnosticsList()) {
			this.previousDiagnostics.put(result.getDiagnosticId(), result);
		}
		this.configured = true;
	}

	public void storeDiagnosticResult(final Diagnostic diagnostic, final DiagnosticResult result) {
		this.currentDiagnostics.put(diagnostic.getUniqueID(), result);
	}

	public int computeDiagnosticStability(final Diagnostic diagnostic, final String machineMessage) {
		if (!this.configured) {
			// If universe is not configured, then everything is new !
			return 0;
		} else {
			final DiagnosticResult previousResult = this.previousDiagnostics.get(diagnostic.getUniqueID());
			if (previousResult == null) {
				return 0;
			}
			if (!machineMessage.equals(previousResult.getMachineMessage())) {
				return 0;
			}

			return previousResult.getStability() + 1;
		}

	}

	public boolean diagnosticAlreadyRan(final Diagnostic diagnostic) {
		return this.currentDiagnostics.containsKey(diagnostic.getUniqueID());
	}

	public List<DiagnosticResult> getAllFailedDiagnostics() {
		final List<DiagnosticResult> result = new ArrayList<DiagnosticResult>();

		for (final DiagnosticResult diagnosticResult : this.currentDiagnostics.values()) {
			if (!diagnosticResult.getSuccess()) {
				result.add(diagnosticResult);
			}
		}

		return result;
	}

	public List<Diagnostic> getFailedDiagnostics(final SDBSystem currentSystem) {
		final List<Diagnostic> result = new ArrayList<Diagnostic>();

		for (final Diagnostic diagnostic : currentSystem.getDiagnostics()) {
			final DiagnosticResult diagnosticResult = this.currentDiagnostics.get(diagnostic.getUniqueID());
			if (!diagnosticResult.getSuccess()) {
				result.add(diagnostic);
			}
		}

		return result;
	}

	public Collection<DiagnosticResult> getAllDiagnostics() {
		return this.currentDiagnostics.values();
	}

	public int computeGlobalStability() {
		int result = Integer.MAX_VALUE;
		for (final DiagnosticResult diagnosticResult : this.currentDiagnostics.values()) {
			result = Math.min(result, diagnosticResult.getStability());
		}

		return result;
	}

	public DiagnosticResult getResultForDiagnostic(final Diagnostic diagnostic) throws NoResultException {
		final DiagnosticResult result = this.currentDiagnostics.get(diagnostic.getUniqueID());
		if (result == null) {
			throw new NoResultException();
		}
		return result;
	}

}
