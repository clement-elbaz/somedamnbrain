package com.somedamnbrain.systems.universe.corrections;

import com.google.inject.Inject;
import com.somedamnbrain.diagnostic.CorrectiveAction;
import com.somedamnbrain.entities.Entities.Universe;
import com.somedamnbrain.exceptions.SystemNotAvailableException;
import com.somedamnbrain.exceptions.UnexplainableException;
import com.somedamnbrain.services.ask.AskService;
import com.somedamnbrain.services.filesystem.FilesystemService;
import com.somedamnbrain.systems.universe.LocalUniverseSystem;

public class InitUniverseFile implements CorrectiveAction {

	private final FilesystemService filesystem;
	private final AskService askService;

	@Inject
	public InitUniverseFile(final FilesystemService filesystem, final AskService askService) {
		this.filesystem = filesystem;
		this.askService = askService;
	}

	@Override
	public void attemptCorrection() throws UnexplainableException {
		try {
			Universe blankUniverse = this.generateBlankUniverse();
			this.filesystem.writeFile(LocalUniverseSystem.UNIVERSE_FILE_PATH, blankUniverse.toByteArray());
		} catch (SystemNotAvailableException e) {
			// do nothing, there is nothing we can do.
		}

	}

	private Universe generateBlankUniverse() throws SystemNotAvailableException, UnexplainableException {
		Universe.Builder blankUniverse = Universe.newBuilder();

		blankUniverse.setName(askService.askHumanMinion("What is the name of this new Universe ?"));

		return blankUniverse.build();
	}

	@Override
	public String getUniqueID() {
		return "Creating new Universe file";
	}

}
