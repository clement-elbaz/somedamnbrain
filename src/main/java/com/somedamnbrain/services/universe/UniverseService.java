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
import com.somedamnbrain.systems.universe.UniverseSystem;

@Singleton
public class UniverseService {

	private final FilesystemService filesystem;

	private boolean configured;
	private Universe universePreviousIteration;

	private Map<String, DiagnosticResult> previousDiagnostics;

	@Inject
	public UniverseService(final FilesystemService filesystem) {
		this.filesystem = filesystem;
		this.previousDiagnostics = new HashMap<String, DiagnosticResult>();
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

			this.universePreviousIteration = Universe.parseFrom(filesystem.readFile(UniverseSystem.UNIVERSE_FILE_PATH));
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
		throw new RuntimeException("not implemented");
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

	public boolean isConfigured() {
		return configured;
	}

}
