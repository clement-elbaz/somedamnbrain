package com.somedamnbrain;

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
		AskService askService = new AskServiceImpl();

		if (askService.initialize()) {
			String answer = askService.askHumanMinion("How are you today ?");

			System.out.println("You said to me : " + answer);
		} else {
			System.out.println("I'm so alone :(");
		}

	}
}
