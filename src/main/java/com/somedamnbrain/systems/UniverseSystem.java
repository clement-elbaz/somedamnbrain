package com.somedamnbrain.systems;

import com.somedamnbrain.entities.Entities.DiagnosticResult;
import com.somedamnbrain.exceptions.ExplainableException;
import com.somedamnbrain.services.AlertService;

public class UniverseSystem implements Runnable {

	private final AlertService alertService;

	public UniverseSystem(AlertService alertService) {
		this.alertService = alertService;
	}

	public void run() {
		try {
			this.loadUniverse();
		} catch (NoUniverseException e) {
			DiagnosticResult.Builder result = DiagnosticResult.newBuilder();
			result.setHumanMessage("No universe could be found.");

			alertService.alert(result.build());
		}

	}

	private void loadUniverse() throws NoUniverseException {
		// TODO
		throw new NoUniverseException();

	}

	static class NoUniverseException extends ExplainableException {

		/**
		 * 
		 */
		private static final long serialVersionUID = -6536736252986896901L;

	}

}
