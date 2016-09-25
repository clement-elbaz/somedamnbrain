package com.somedamnbrain.services.universe;

import java.util.HashMap;
import java.util.Map;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.protobuf.InvalidProtocolBufferException;
import com.somedamnbrain.diagnostic.Diagnostic;
import com.somedamnbrain.entities.Entities.DiagnosticResult;
import com.somedamnbrain.entities.Entities.Universe;
import com.somedamnbrain.exceptions.NoResultException;
import com.somedamnbrain.exceptions.SystemNotAvailableException;
import com.somedamnbrain.exceptions.UnexplainableException;
import com.somedamnbrain.services.filesystem.FilesystemService;
import com.somedamnbrain.systems.universe.LocalUniverseSystem;

@Singleton
public class UniverseService {

	private final FilesystemService filesystem;

	private boolean configured;
	private Universe universePreviousIteration;

	private final Map<String, DiagnosticResult> previousDiagnostics;

	private final Map<String, DiagnosticResult> currentDiagnostics;

	@Inject
	public UniverseService(final FilesystemService filesystem) {
		this.filesystem = filesystem;
		this.previousDiagnostics = new HashMap<String, DiagnosticResult>();
		this.currentDiagnostics = new HashMap<String, DiagnosticResult>();
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

			this.universePreviousIteration = Universe.parseFrom(filesystem.readFile(LocalUniverseSystem.UNIVERSE_FILE_PATH));
			for (DiagnosticResult result : universePreviousIteration.getDiagnosticsList()) {
				this.previousDiagnostics.put(result.getDiagnosticId(), result);
			}
		} catch (InvalidProtocolBufferException e) {
			this.configured = false;
			throw new UnexplainableException(e);
		} catch (NoResultException e) {
			this.configured = false;
			throw new UnexplainableException(e);
		} catch (UnexplainableException e) {
			this.configured = false;
			throw new UnexplainableException(e);
		}
	}

	public void storeDiagnosticResult(Diagnostic diagnostic, DiagnosticResult result)
			throws SystemNotAvailableException {
		if (!this.configured) {
			throw new SystemNotAvailableException();
		}
		this.currentDiagnostics.put(diagnostic.getUniqueID(), result);
	}

	public int getCurrentExecutionNumber() throws SystemNotAvailableException {
		if (!this.configured) {
			throw new SystemNotAvailableException();
		}
		return this.universePreviousIteration.getPreviousExecutionNumber() + 1;
	}

	public int computeStability(Diagnostic diagnostic, String machineMessage) {
		if (!this.configured) {
			// If universe is not configured, then everything is new !
			return 0;
		} else {
			DiagnosticResult previousResult = this.previousDiagnostics.get(diagnostic.getUniqueID());
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
		Universe.Builder modifiedUniverse = Universe.newBuilder();

		modifiedUniverse.setName(universePreviousIteration.getName());
		modifiedUniverse.addAllDiagnostics(this.currentDiagnostics.values());
		modifiedUniverse.setPreviousExecutionNumber(universePreviousIteration.getPreviousExecutionNumber() + 1);

		Universe finalizedUniverse = modifiedUniverse.build();

		// TODO report global stability and failed systems

		// TODO report sustainability of universe

		this.filesystem.writeFile(LocalUniverseSystem.UNIVERSE_FILE_PATH, finalizedUniverse.toByteArray());
		this.configured = false;
	}

	public boolean isConfigured() {
		return configured;
	}

}
