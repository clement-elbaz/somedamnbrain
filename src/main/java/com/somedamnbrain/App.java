package com.somedamnbrain;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.somedamnbrain.exceptions.SystemNotAvailableException;
import com.somedamnbrain.exceptions.UnexplainableException;
import com.somedamnbrain.services.ask.AskService;
import com.somedamnbrain.services.ask.AskServiceImpl;
import com.somedamnbrain.services.filesystem.FilesystemService;
import com.somedamnbrain.services.filesystem.LocalFileSystemImpl;
import com.somedamnbrain.services.system.SystemDiagnosticService;
import com.somedamnbrain.services.universe.SelfService;
import com.somedamnbrain.services.universe.UniverseService;
import com.somedamnbrain.systems.self.SelfSystem;

/**
 * Hello world!
 *
 */
public class App {
	public static void main(final String[] args) throws UnexplainableException {
		final Injector injector = configureAndProvideInjector();

		final AskService askService = injector.getInstance(AskService.class);
		final SystemDiagnosticService diagnosticService = injector.getInstance(SystemDiagnosticService.class);
		final UniverseService universeService = injector.getInstance(UniverseService.class);

		final SelfSystem selfSystem = injector.getInstance(SelfSystem.class);
		final SelfService selfService = injector.getInstance(SelfService.class);

		askService.initialize();

		diagnosticService.diagnosticFullSystem(selfSystem);

		selfService.reportSelf();

		try {
			universeService.closeAndSaveUniverse();
		} catch (final SystemNotAvailableException e) {
			System.out.println("Universe was not saved because Universe System is not initialized.");
		}

		askService.close();

	}

	private static Injector configureAndProvideInjector() {
		final AbstractModule module = new AbstractModule() {

			@Override
			protected void configure() {
				bind(AskService.class).to(AskServiceImpl.class);
				bind(FilesystemService.class).to(LocalFileSystemImpl.class);
			}

		};

		return Guice.createInjector(module);
	}
}
