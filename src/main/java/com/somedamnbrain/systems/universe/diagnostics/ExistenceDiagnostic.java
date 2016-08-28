package com.somedamnbrain.systems.universe.diagnostics;

import org.apache.commons.lang3.StringUtils;

import com.google.protobuf.InvalidProtocolBufferException;
import com.somedamnbrain.diagnostic.CorrectiveAction;
import com.somedamnbrain.diagnostic.Diagnostic;
import com.somedamnbrain.entities.Entities.DiagnosticResult;
import com.somedamnbrain.entities.Entities.Universe;
import com.somedamnbrain.exceptions.NoResultException;
import com.somedamnbrain.exceptions.UnexplainableException;
import com.somedamnbrain.services.filesystem.FilesystemService;
import com.somedamnbrain.systems.universe.UniverseSystem;

public class ExistenceDiagnostic implements Diagnostic {

	private final FilesystemService filesystem;

	private final CorrectiveAction initUniverseFile;

	/**
	 * Constructor.
	 * 
	 * @param filesystem
	 *            filesystem
	 * @param initUniverseFile
	 *            init universe file correction
	 */
	public ExistenceDiagnostic(final FilesystemService filesystem, final CorrectiveAction initUniverseFile) {
		this.filesystem = filesystem;
		this.initUniverseFile = initUniverseFile;
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
		DiagnosticResult.Builder result = DiagnosticResult.newBuilder();
		try {
			Universe universe = Universe.parseFrom(filesystem.readFile(UniverseSystem.UNIVERSE_FILE_PATH));

			result.setSuccess(true);
			result.setMachineMessage("universe-existence-OK");
			result.setHumanMessage("Universe file " + universe.getName() + " is present and properly formatted");

			return result.build();
		} catch (InvalidProtocolBufferException e) {
			result.setSuccess(false);
			result.setMachineMessage("universe-existence-unreadable");
			result.setHumanMessage("Universe file is not readable");

			return result.build();
		} catch (NoResultException e) {
			result.setSuccess(false);
			result.setMachineMessage("universe-existence-missing");
			result.setHumanMessage("Universe file is not present");

			return result.build();
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
		throw new NoResultException();
	}

}
