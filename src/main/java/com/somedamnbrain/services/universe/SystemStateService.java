package com.somedamnbrain.services.universe;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.somedamnbrain.diagnostic.Diagnostic;
import com.somedamnbrain.entities.Entities.DiagnosticResult;
import com.somedamnbrain.entities.Entities.SystemState;
import com.somedamnbrain.entities.Entities.Universe;
import com.somedamnbrain.exceptions.NoResultException;
import com.somedamnbrain.exceptions.UnexplainableException;
import com.somedamnbrain.systems.SDBSystem;

@Singleton
public class SystemStateService {

	private final DiagnosticStateService diagnosticStateService;

	private boolean configured;
	private final Map<String, SystemState> previousSystemStates;
	private final Map<String, SystemState> currentSystemStates;

	@Inject
	public SystemStateService(final DiagnosticStateService diagnosticStateService) {
		this.configured = false;
		this.previousSystemStates = new HashMap<String, SystemState>();
		this.currentSystemStates = new HashMap<String, SystemState>();
		this.diagnosticStateService = diagnosticStateService;
	}

	public void configure(final Universe previousUniverseIteration) {
		for (final SystemState systemState : previousUniverseIteration.getSystemStatesList()) {
			this.previousSystemStates.put(systemState.getUniqueId(), systemState);
		}
		this.configured = true;
	}

	public List<SDBSystem> getFailedDependencies(final SDBSystem currentSystem) {
		final List<SDBSystem> result = new ArrayList<SDBSystem>();

		for (final SDBSystem dependency : currentSystem.getDependencies()) {
			final SystemState state = this.currentSystemStates.get(dependency.getUniqueID());
			if (!state.getUp()) {
				result.add(dependency);
			}
		}

		return result;
	}

	public SystemState computeAndStoreSystemState(final SDBSystem system) throws UnexplainableException {
		final SystemState.Builder state = SystemState.newBuilder();

		state.setUniqueId(system.getUniqueID());

		final boolean systemUp = this.isSystemUp(system);
		state.setUp(systemUp);

		state.setStability(this.computeSystemStability(system, systemUp));

		final SystemState finalizedState = state.build();
		this.currentSystemStates.put(finalizedState.getUniqueId(), finalizedState);
		return finalizedState;

	}

	private boolean isSystemUp(final SDBSystem system) {
		final boolean allDependenciesUp = this.allSystemsAvailable(system.getDependencies());
		final boolean allDiagnosticsOk = this.diagnosticStateService.getFailedDiagnostics(system).isEmpty();

		return allDependenciesUp && allDiagnosticsOk;
	}

	private int computeSystemStability(final SDBSystem system, final boolean systemUp) {
		if (!this.configured) {
			return 0;
		}
		final SystemState previousState = this.previousSystemStates.get(system.getUniqueID());

		if (systemUp != previousState.getUp()) {
			return 0;
		}

		return previousState.getStability() + 1;
	}

	private boolean allSystemsAvailable(final List<SDBSystem> systems) {
		for (final SDBSystem system : systems) {
			final SystemState state = this.currentSystemStates.get(system.getUniqueID());
			if (!state.getUp()) {
				return false;
			}
		}

		return true;
	}

	public SystemState recomputeCompleteSystemStability(final SDBSystem currentSystem) throws UnexplainableException {
		try {
			for (final Diagnostic diagnostic : currentSystem.getDiagnostics()) {
				final DiagnosticResult resultWithWrongStability = this.diagnosticStateService
						.getResultForDiagnostic(diagnostic);
				final DiagnosticResult resultWithCorrectStability = diagnostic.newResult(
						resultWithWrongStability.getSuccess(), resultWithWrongStability.getMachineMessage(),
						resultWithWrongStability.getHumanMessage(), this.diagnosticStateService);

				this.diagnosticStateService.storeDiagnosticResult(diagnostic, resultWithCorrectStability);

			}

			final int correctSystemStability = this.computeSystemStability(currentSystem,
					this.isSystemUp(currentSystem));

			final SystemState.Builder correctedSystemState = this.currentSystemStates.get(currentSystem.getUniqueID())
					.toBuilder();

			correctedSystemState.setStability(correctSystemStability);

			final SystemState finalState = correctedSystemState.build();

			this.currentSystemStates.put(correctedSystemState.getUniqueId(), finalState);

			return finalState;
		} catch (final NoResultException e) {
			throw new UnexplainableException(e);
		}

	}

	public List<SystemState> getFailedSystems() {
		final List<SystemState> result = new ArrayList<SystemState>();

		for (final SystemState state : this.currentSystemStates.values()) {
			if (!state.getUp()) {
				result.add(state);
			}
		}

		return result;
	}

	public Collection<? extends SystemState> getAllSystemStates() {
		return this.currentSystemStates.values();
	}

}
