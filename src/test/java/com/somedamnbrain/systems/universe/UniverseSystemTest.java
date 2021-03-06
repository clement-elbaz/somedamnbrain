package com.somedamnbrain.systems.universe;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.somedamnbrain.dumb.DumbFilesystemService;
import com.somedamnbrain.entities.Entities.Universe;
import com.somedamnbrain.exceptions.UnexplainableException;
import com.somedamnbrain.services.ask.AskService;
import com.somedamnbrain.services.filesystem.FilesystemService;
import com.somedamnbrain.services.system.SystemDiagnosticService;
import com.somedamnbrain.services.universe.UniverseService;

public class UniverseSystemTest {

	private Injector configureAndProvideInjector() {
		AbstractModule module = new AbstractModule() {

			@Override
			protected void configure() {
				bind(FilesystemService.class).to(DumbFilesystemService.class);
				bind(AskService.class).to(DumbAskService.class);

			}

		};

		return Guice.createInjector(module);
	}

	@Test
	public void testUniverseFileMissing() throws UnexplainableException {
		Injector injector = this.configureAndProvideInjector();

		SystemDiagnosticService diagnosticService = injector.getInstance(SystemDiagnosticService.class);
		LocalUniverseSystem system = injector.getInstance(LocalUniverseSystem.class);
		UniverseService service = injector.getInstance(UniverseService.class);

		diagnosticService.diagnosticFullSystem(system);
		Assert.assertTrue(service.isConfigured());
	}

	@Test
	public void testUniverseFileCorrupted() throws UnexplainableException {
		Injector injector = this.configureAndProvideInjector();

		FilesystemService filesystem = injector.getInstance(FilesystemService.class);
		SystemDiagnosticService diagnosticService = injector.getInstance(SystemDiagnosticService.class);
		LocalUniverseSystem system = injector.getInstance(LocalUniverseSystem.class);
		UniverseService service = injector.getInstance(UniverseService.class);

		filesystem.writeFile(LocalUniverseSystem.UNIVERSE_FILE_PATH, "some corrupted data".getBytes());

		diagnosticService.diagnosticFullSystem(system);
		Assert.assertTrue(service.isConfigured());
	}

	@Test
	public void testUniverseFilePresent() throws UnexplainableException {
		Injector injector = this.configureAndProvideInjector();

		FilesystemService filesystem = injector.getInstance(FilesystemService.class);
		SystemDiagnosticService diagnosticService = injector.getInstance(SystemDiagnosticService.class);
		LocalUniverseSystem system = injector.getInstance(LocalUniverseSystem.class);
		UniverseService service = injector.getInstance(UniverseService.class);

		Universe.Builder universe = Universe.newBuilder();

		universe.setName("NEWUNIVERSE");

		filesystem.writeFile(LocalUniverseSystem.UNIVERSE_FILE_PATH, universe.build().toByteArray());

		diagnosticService.diagnosticFullSystem(system);
		Assert.assertTrue(service.isConfigured());
	}

	static class DumbAskService implements AskService {

		@Override
		public String askHumanMinion(String question) {
			if (StringUtils.equals("What is the name of this new Universe ?", question)) {
				return "TESTU";
			}

			return "I don't know !";
		}

		@Override
		public boolean initialize() {
			return true;
		}

		@Override
		public void close() {
			// do nothing

		}

	}
}
