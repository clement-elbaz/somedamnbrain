package com.somedamnbrain.services.ask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.google.inject.Singleton;
import com.somedamnbrain.exceptions.SystemNotAvailableException;
import com.somedamnbrain.exceptions.UnexplainableException;

@Singleton
public class AskServiceImpl implements AskService {

	private boolean isAnyoneThere;
	private final InputStreamReader simpleIn;
	private final BufferedReader bufferedIn;

	public AskServiceImpl() {
		this.isAnyoneThere = false;
		this.simpleIn = new InputStreamReader(System.in);
		this.bufferedIn = new BufferedReader(simpleIn);
	}

	@Override
	public boolean initialize() throws UnexplainableException {
		System.out.println("You have 5 seconds to press ENTER to notify your presence, human minion !");

		try {

			for (int i = 0; i < 5; i++) {
				Thread.sleep(1000);
				if (simpleIn.ready()) {
					this.isAnyoneThere = true;
					System.out.println("Great to know you're there !");
					this.bufferedIn.readLine();
					break;
				}
			}
		} catch (final Exception e) {
			this.isAnyoneThere = false;
			throw new UnexplainableException(e);
		}

		return this.isAnyoneThere;
	}

	@Override
	public String askHumanMinion(final String question) throws SystemNotAvailableException, UnexplainableException {
		if (!this.isAnyoneThere) {
			throw new SystemNotAvailableException();
		}
		System.out.println("I have a question for you, human minion ! " + question);
		try {
			return bufferedIn.readLine();
		} catch (final IOException e) {
			throw new UnexplainableException(e);
		}
	}

	@Override
	public void close() throws UnexplainableException {
		try {
			this.bufferedIn.close();
			this.simpleIn.close();
		} catch (final Exception e) {
			throw new UnexplainableException(e);
		}

	}

}
