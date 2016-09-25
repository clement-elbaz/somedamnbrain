package com.somedamnbrain;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.somedamnbrain.exceptions.SystemNotAvailableException;
import com.somedamnbrain.exceptions.UnexplainableException;
import com.somedamnbrain.services.ask.AskService;
import com.somedamnbrain.services.ask.AskServiceImpl;

/**
 * Hello world!
 *
 */
public class App {
	public static void main(String[] args) throws UnexplainableException, SystemNotAvailableException {
		Injector injector = configureAndProvideInjector();

		AskService askService = injector.getInstance(AskService.class);

		if (askService.initialize()) {
			String answer = askService.askHumanMinion("How are you today ?");

			System.out.println("You said to me : " + answer);
		} else {
			System.out.println("I'm so alone :(");
		}

	}

	private static Injector configureAndProvideInjector() {
		AbstractModule module = new AbstractModule() {

			@Override
			protected void configure() {
				bind(AskService.class).to(AskServiceImpl.class);
			}

		};

		return Guice.createInjector(module);
	}
}
