package com.somedamnbrain.systems.universe.corrections;

import com.somedamnbrain.diagnostic.CorrectiveAction;
import com.somedamnbrain.entities.Entities.Universe;
import com.somedamnbrain.exceptions.UnexplainableException;
import com.somedamnbrain.services.ask.AskService;
import com.somedamnbrain.services.filesystem.FilesystemService;
import com.somedamnbrain.systems.universe.UniverseSystem;

public class InitUniverseFile implements CorrectiveAction {

	private final FilesystemService filesystem;
	private final AskService askService;

	public InitUniverseFile(final FilesystemService filesystem, final AskService askService) {
		this.filesystem = filesystem;
		this.askService = askService;
	}

	@Override
	public void attemptCorrection() throws UnexplainableException {
		Universe blankUniverse = this.generateBlankUniverse();
		this.filesystem.writeFile(UniverseSystem.UNIVERSE_FILE_PATH, blankUniverse.toByteArray());

	}

	private Universe generateBlankUniverse() {
		Universe.Builder blankUniverse = Universe.newBuilder();

		blankUniverse.setName(askService.askHumanMinion("What is the name of this new Universe ?"));

		return blankUniverse.build();
	}

}
