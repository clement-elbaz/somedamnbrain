package com.somedamnbrain.services.universe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.protobuf.InvalidProtocolBufferException;
import com.somedamnbrain.diagnostic.Diagnostic;
import com.somedamnbrain.entities.Entities.DiagnosticResult;
import com.somedamnbrain.entities.Entities.SystemState;
import com.somedamnbrain.entities.Entities.Universe;
import com.somedamnbrain.exceptions.NoResultException;
import com.somedamnbrain.exceptions.SystemNotAvailableException;
import com.somedamnbrain.exceptions.UnexplainableException;
import com.somedamnbrain.services.filesystem.FilesystemService;
import com.somedamnbrain.systems.SDBSystem;
import com.somedamnbrain.systems.universe.LocalUniverseSystem;

@Singleton
public class UniverseService {

	private final FilesystemService filesystem;

	private boolean configured;
	private Universe universePreviousIteration;

	private final Map<String, DiagnosticResult> previousDiagnostics;

	private final Map<String, DiagnosticResult> currentDiagnostics;

	private final Map<String, SystemState> currentSystemStates;

	@Inject
	public UniverseService(final FilesystemService filesystem) {
		this.filesystem = filesystem;
		this.previousDiagnostics = new HashMap<String, DiagnosticResult>();
		this.currentDiagnostics = new HashMap<String, DiagnosticResult>();
		this.currentSystemStates = new HashMap<String, SystemState>();
	}

	/**
	 * This method should only be called by UniverseSystem when all Universe
	 * file checks are green. Any error at this point is unexplainable.
	 * 
	 * @throws UnexplainableException
	 *             if something unexpected happened
	 */
	public void configureUniverse() throws UnexplainableException {
		this.configured = true;
		try {

			this.universePreviousIteration = Universe
					.parseFrom(filesystem.readFile(LocalUniverseSystem.UNIVERSE_FILE_PATH));
			for (final DiagnosticResult result : universePreviousIteration.getDiagnosticsList()) {
				this.previousDiagnostics.put(result.getDiagnosticId(), result);
			}
		} catch (final InvalidProtocolBufferException e) {
			this.configured = false;
			throw new UnexplainableException(e);
		} catch (final NoResultException e) {
			this.configured = false;
			throw new UnexplainableException(e);
		} catch (final UnexplainableException e) {
			this.configured = false;
			throw new UnexplainableException(e);
		}
	}

	public void storeDiagnosticResult(final Diagnostic diagnostic, final DiagnosticResult result)
			throws SystemNotAvailableException {
		this.currentDiagnostics.put(diagnostic.getUniqueID(), result);
	}

	public int getCurrentExecutionNumber() throws SystemNotAvailableException {
		if (!this.configured) {
			throw new SystemNotAvailableException();
		}
		return this.universePreviousIteration.getPreviousExecutionNumber() + 1;
	}

	public int computeStability(final Diagnostic diagnostic, final String machineMessage) {
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

	public void closeAndSaveUniverse() throws SystemNotAvailableException, UnexplainableException {
		if (!this.configured) {
			throw new SystemNotAvailableException();
		}
		final Universe.Builder modifiedUniverse = Universe.newBuilder();

		modifiedUniverse.setName(universePreviousIteration.getName());
		modifiedUniverse.addAllDiagnostics(this.currentDiagnostics.values());
		modifiedUniverse.setPreviousExecutionNumber(universePreviousIteration.getPreviousExecutionNumber() + 1);

		final Universe finalizedUniverse = modifiedUniverse.build();

		// TODO report global stability and failed systems

		// TODO report sustainability of universe

		this.filesystem.writeFile(LocalUniverseSystem.UNIVERSE_FILE_PATH, finalizedUniverse.toByteArray());
		this.configured = false;
	}

	public boolean isConfigured() {
		return configured;
	}

	public boolean diagnosticAlreadyRan(final Diagnostic diagnostic) {
		return this.currentDiagnostics.containsKey(diagnostic.getUniqueID());
	}

	public SystemState computeAndStoreSystemState(final SDBSystem system) {
		final SystemState.Builder state = SystemState.newBuilder();

		state.setUniqueId(system.getUniqueID());
		state.setStability(this.computeSystemStability(system));

		final boolean allDependenciesUp = this.allSystemsAvailable(system.getDependencies());
		final boolean allDiagnosticsOk = this.allDiagnosticsOk(system.getDiagnostics());

		final boolean systemUp = allDependenciesUp && allDiagnosticsOk;
		state.setUp(systemUp);

		final SystemState finalizedState = state.build();
		this.currentSystemStates.put(finalizedState.getUniqueId(), finalizedState);
		return finalizedState;

	}

	private int computeSystemStability(final SDBSystem system) {
		if (!this.configured) {
			return 0;
		}
		int minimumStability = Integer.MAX_VALUE;

		for (final Diagnostic diagnostic : system.getDiagnostics()) {
			final DiagnosticResult result = this.currentDiagnostics.get(diagnostic.getUniqueID());
			minimumStability = Math.min(minimumStability, result.getStability());
		}

		for (final SDBSystem dependency : system.getDependencies()) {
			final SystemState dependencyState = this.currentSystemStates.get(dependency.getUniqueID());
			minimumStability = Math.min(minimumStability, dependencyState.getStability());
		}

		return minimumStability;
	}

	private boolean allDiagnosticsOk(final List<Diagnostic> diagnostics) {
		for (final Diagnostic diagnostic : diagnostics) {
			final DiagnosticResult result = this.currentDiagnostics.get(diagnostic.getUniqueID());
			if (!result.getSuccess()) {
				return false;
			}
		}

		return true;
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

}
