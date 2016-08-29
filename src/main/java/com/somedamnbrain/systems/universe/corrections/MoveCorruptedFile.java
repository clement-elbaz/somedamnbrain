package com.somedamnbrain.systems.universe.corrections;

import com.somedamnbrain.diagnostic.CorrectiveAction;
import com.somedamnbrain.exceptions.UnexplainableException;
import com.somedamnbrain.services.filesystem.FilesystemService;
import com.somedamnbrain.systems.universe.UniverseSystem;

public class MoveCorruptedFile implements CorrectiveAction {

	private final FilesystemService filesystem;

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
		filesystem.moveFile(UniverseSystem.UNIVERSE_FILE_PATH,
				UniverseSystem.UNIVERSE_FILE_PATH + ".old." + System.currentTimeMillis());

	}

}
