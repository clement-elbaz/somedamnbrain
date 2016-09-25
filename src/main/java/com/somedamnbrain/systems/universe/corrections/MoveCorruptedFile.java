package com.somedamnbrain.systems.universe.corrections;

import com.google.inject.Inject;
import com.somedamnbrain.diagnostic.CorrectiveAction;
import com.somedamnbrain.exceptions.NoResultException;
import com.somedamnbrain.exceptions.UnexplainableException;
import com.somedamnbrain.services.filesystem.FilesystemService;
import com.somedamnbrain.systems.universe.LocalUniverseSystem;

public class MoveCorruptedFile implements CorrectiveAction {

	private final FilesystemService filesystem;

	@Inject
	public MoveCorruptedFile(final FilesystemService filesystem) {
		this.filesystem = filesystem;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.somedamnbrain.diagnostic.CorrectiveAction#attemptCorrection()
	 */
	@Override
	public void attemptCorrection() throws UnexplainableException {
		try {
			filesystem.moveFile(LocalUniverseSystem.UNIVERSE_FILE_PATH,
					LocalUniverseSystem.UNIVERSE_FILE_PATH + ".old." + System.currentTimeMillis());
		} catch (NoResultException e) {
			// If no universe file is present when this correction is called,
			// something very wrong happened.
			throw new UnexplainableException(e);
		}

	}

	@Override
	public String getUniqueID() {
		return "Moving corrupted Universe file";
	}

}
