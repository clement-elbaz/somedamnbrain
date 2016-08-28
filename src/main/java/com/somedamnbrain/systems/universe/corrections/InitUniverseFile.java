package com.somedamnbrain.systems.universe.corrections;

import com.somedamnbrain.diagnostic.CorrectiveAction;
import com.somedamnbrain.entities.Entities.Universe;
import com.somedamnbrain.exceptions.UnexplainableException;
import com.somedamnbrain.services.filesystem.FilesystemService;
import com.somedamnbrain.systems.universe.UniverseSystem;

public class InitUniverseFile implements CorrectiveAction {

	private final FilesystemService filesystem;

	public InitUniverseFile(final FilesystemService filesystem) {
		this.filesystem = filesystem;
	}

	@Override
	public void attemptCorrection() throws UnexplainableException {
		Universe blankUniverse = this.generateBlankUniverse();
		this.filesystem.writeFile(UniverseSystem.UNIVERSE_FILE_PATH, blankUniverse.toByteArray());

	}

	private Universe generateBlankUniverse() {
		// TODO
		throw new RuntimeException("not implemented");
	}

}
