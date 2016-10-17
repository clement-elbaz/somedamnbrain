package com.somedamnbrain.systems.mail.corrections;

import com.google.inject.Inject;
import com.somedamnbrain.diagnostic.CorrectiveAction;
import com.somedamnbrain.entities.Entities.ConfigItem;
import com.somedamnbrain.entities.Entities.Configuration;
import com.somedamnbrain.entities.Entities.Configuration.Builder;
import com.somedamnbrain.exceptions.SystemNotAvailableException;
import com.somedamnbrain.exceptions.UnexplainableException;
import com.somedamnbrain.services.MailService;
import com.somedamnbrain.services.ask.AskService;
import com.somedamnbrain.services.universe.UniverseService;

public class CreateMailConfig implements CorrectiveAction {

	private final AskService askService;
	private final MailService mailService;
	private final UniverseService universeService;

	@Inject
	public CreateMailConfig(final AskService askService, final MailService mailService,
			final UniverseService universeService) {
		this.askService = askService;
		this.mailService = mailService;
		this.universeService = universeService;
	}

	@Override
	public String getUniqueID() {
		return "Creating mail config";
	}

	@Override
	public void attemptCorrection() throws UnexplainableException {
		try {
			final Configuration.Builder newConfig = Configuration.newBuilder();

			newConfig.setConfigName("mail");

			this.askConfig(newConfig, "user", "What is the mail user ?");
			this.askConfig(newConfig, "password", "What is the mail user password ?");
			this.askConfig(newConfig, "smtp.host", "What is the SMTP host ?");
			this.askConfig(newConfig, "from", "What is the from address ?");
			this.askConfig(newConfig, "ssl", "Should I use SSL to connect to the SMTP host (true/false) ?");

			this.askConfig(newConfig, "admin.address", "What is YOUR email address, human minion ?");

			final Configuration finalizedConfig = newConfig.build();

			if (this.mailService.testConfig(finalizedConfig)) {
				universeService.publishConfiguration(finalizedConfig);
			} else {
				if ("true".equals(askService
						.askHumanMinion("Mail configuration seems incorrect ! Would you like to try again ?"))) {
					this.attemptCorrection();
				}
			}

		} catch (final SystemNotAvailableException e) {
			// If no humain minion is available, there is nothing we can do.
			System.out.println("Skipping correction - no human minion is available");
		}

	}

	private void askConfig(final Builder newConfig, final String key, final String humanQuestion)
			throws SystemNotAvailableException, UnexplainableException {
		final ConfigItem.Builder configItem = ConfigItem.newBuilder();
		configItem.setKey(key);

		configItem.setValue(askService.askHumanMinion(humanQuestion));

		newConfig.addConfigItems(configItem.build());
	}

}
