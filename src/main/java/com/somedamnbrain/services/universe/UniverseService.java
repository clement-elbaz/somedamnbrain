package com.somedamnbrain.services.universe;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.protobuf.InvalidProtocolBufferException;
import com.somedamnbrain.entities.Entities.Configuration;
import com.somedamnbrain.entities.Entities.Universe;
import com.somedamnbrain.exceptions.NoResultException;
import com.somedamnbrain.exceptions.SystemNotAvailableException;
import com.somedamnbrain.exceptions.UnexplainableException;
import com.somedamnbrain.services.filesystem.FilesystemService;
import com.somedamnbrain.systems.universe.LocalUniverseSystem;

@Singleton
public class UniverseService {

	private final FilesystemService filesystem;
	private final ConfigService configService;
	private final DiagnosticStateService diagnosticStateService;
	private final SystemStateService systemStateService;

	private boolean configured;
	private Universe universePreviousIteration;

	@Inject
	public UniverseService(final FilesystemService filesystem, final ConfigService configService,
			final DiagnosticStateService diagnosticStateService, final SystemStateService systemStateService) {
		this.filesystem = filesystem;
		this.configService = configService;
		this.diagnosticStateService = diagnosticStateService;
		this.systemStateService = systemStateService;

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

			// Load previous diagnostics
			this.diagnosticStateService.configure(universePreviousIteration);

			// Load previous systems state
			this.systemStateService.configure(universePreviousIteration);

			// Load previous configurations
			for (final Configuration config : universePreviousIteration.getConfigurationsList()) {
				this.configService.publishConfiguration(config);
			}
			// Review configurations
			this.configService.reviewAllConfigs();

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

	public int getCurrentExecutionNumber() throws SystemNotAvailableException {
		if (!this.configured) {
			throw new SystemNotAvailableException();
		}
		return this.universePreviousIteration.getPreviousExecutionNumber() + 1;
	}

	public void closeAndSaveUniverse() throws SystemNotAvailableException, UnexplainableException {
		if (!this.configured) {
			throw new SystemNotAvailableException();
		}
		final Universe.Builder modifiedUniverse = Universe.newBuilder();

		modifiedUniverse.setName(universePreviousIteration.getName());
		modifiedUniverse.addAllDiagnostics(diagnosticStateService.getAllDiagnostics());
		modifiedUniverse.addAllConfigurations(this.configService.getAllConfigurations());
		modifiedUniverse.addAllSystemStates(this.systemStateService.getAllSystemStates());
		modifiedUniverse.setPreviousExecutionNumber(universePreviousIteration.getPreviousExecutionNumber() + 1);

		final Universe finalizedUniverse = modifiedUniverse.build();

		this.filesystem.writeFile(LocalUniverseSystem.UNIVERSE_FILE_PATH, finalizedUniverse.toByteArray());
		this.configured = false;
	}

	public boolean isConfigured() {
		return configured;
	}

	public String getUniverseName() throws SystemNotAvailableException {
		if (!this.configured) {
			throw new SystemNotAvailableException();
		}
		return this.universePreviousIteration.getName();
	}

}
