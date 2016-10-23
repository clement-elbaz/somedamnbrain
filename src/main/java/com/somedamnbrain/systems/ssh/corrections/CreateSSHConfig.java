package com.somedamnbrain.systems.ssh.corrections;

import com.google.inject.Inject;
import com.somedamnbrain.diagnostic.CorrectiveAction;
import com.somedamnbrain.entities.Entities.Configuration;
import com.somedamnbrain.exceptions.SystemNotAvailableException;
import com.somedamnbrain.exceptions.UnexplainableException;
import com.somedamnbrain.services.universe.ConfigService;

public class CreateSSHConfig implements CorrectiveAction {

	private final ConfigService configService;

	@Inject
	public CreateSSHConfig(final ConfigService configService) {
		this.configService = configService;
	}

	@Override
	public String getUniqueID() {
		return "Creating SSH config";
	}

	@Override
	public void attemptCorrection() throws UnexplainableException {
		try {
			final Configuration.Builder newConfig = Configuration.newBuilder();

			newConfig.setConfigName("ssh");

			this.configService.askConfig(newConfig, "ip", "What is the IP address of the machine ?");
			this.configService.askConfig(newConfig, "user", "What is the SSH user I should log into ?");
			this.configService.askConfig(newConfig, "password", "What is the SSH password I should use ?");

			final Configuration finalizedConfig = newConfig.build();

			this.configService.publishConfiguration(finalizedConfig);

		} catch (final SystemNotAvailableException e) {
			// If no humain minion is available, there is nothing we can do.
			System.out.println("Skipping correction - no human minion is available");
		}

	}

}
