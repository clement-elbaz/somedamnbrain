package com.somedamnbrain.systems.universe.diagnostics;

import org.apache.commons.lang3.StringUtils;

import com.google.inject.Inject;
import com.google.protobuf.InvalidProtocolBufferException;
import com.somedamnbrain.diagnostic.CorrectiveAction;
import com.somedamnbrain.diagnostic.Diagnostic;
import com.somedamnbrain.entities.Entities.DiagnosticResult;
import com.somedamnbrain.entities.Entities.Universe;
import com.somedamnbrain.exceptions.NoResultException;
import com.somedamnbrain.exceptions.UnexplainableException;
import com.somedamnbrain.services.filesystem.FilesystemService;
import com.somedamnbrain.services.universe.UniverseService;
import com.somedamnbrain.systems.universe.UniverseSystem;
import com.somedamnbrain.systems.universe.corrections.InitUniverseFile;
import com.somedamnbrain.systems.universe.corrections.MoveCorruptedFile;

public class ExistenceDiagnostic implements Diagnostic {

	private final FilesystemService filesystem;
	private final UniverseService universeService;

	private final InitUniverseFile initUniverseFile;
	private final MoveCorruptedFile moveCorruptedFile;

	/**
	 * Constructor.
	 * 
	 * @param filesystem
	 *            filesystem
	 * @param initUniverseFile
	 *            init universe file correction
	 */
	@Inject
	public ExistenceDiagnostic(final FilesystemService filesystem, final UniverseService universeService,
			final InitUniverseFile initUniverseFile, final MoveCorruptedFile moveCorruptedFile) {
		this.filesystem = filesystem;
		this.universeService = universeService;
		this.initUniverseFile = initUniverseFile;
		this.moveCorruptedFile = moveCorruptedFile;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.somedamnbrain.diagnostic.Diagnostic#getUniqueID()
	 */
	@Override
	public String getUniqueID() {
		return "Checking universe file existence on the system";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.somedamnbrain.diagnostic.Diagnostic#attemptDiagnostic()
	 */
	@Override
	public DiagnosticResult attemptDiagnostic() throws UnexplainableException {
		try {
			Universe universe = Universe.parseFrom(filesystem.readFile(UniverseSystem.UNIVERSE_FILE_PATH));

			return this.newResult(true, "universe-existence-OK",
					"Universe file " + universe.getName() + " is present and properly formatted", universeService);
		} catch (InvalidProtocolBufferException e) {
			return this.newResult(false, "universe-existence-unreadable", "Universe file is not readable",
					universeService);

		} catch (NoResultException e) {
			return this.newResult(false, "universe-existence-missing", "Universe file is not present", universeService);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.somedamnbrain.diagnostic.Diagnostic#getCorrection(com.somedamnbrain.
	 * entities.Entities.DiagnosticResult)
	 */
	@Override
	public CorrectiveAction getCorrection(DiagnosticResult diagnosticResult) throws NoResultException {
		if (StringUtils.equals("universe-existence-missing", diagnosticResult.getMachineMessage())) {
			return this.initUniverseFile;
		}
		if (StringUtils.equals("universe-existence-unreadable", diagnosticResult.getMachineMessage())) {
			return this.moveCorruptedFile;
		}
		throw new NoResultException();
	}

}
