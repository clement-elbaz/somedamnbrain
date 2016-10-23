package com.somedamnbrain.systems.mail.corrections;

import com.google.inject.Inject;
import com.somedamnbrain.diagnostic.CorrectiveAction;
import com.somedamnbrain.entities.Entities.Configuration;
import com.somedamnbrain.exceptions.SystemNotAvailableException;
import com.somedamnbrain.exceptions.UnexplainableException;
import com.somedamnbrain.services.ask.AskService;
import com.somedamnbrain.services.mail.MailService;
import com.somedamnbrain.services.universe.ConfigService;

public class CreateMailConfig implements CorrectiveAction {

	private final MailService mailService;
	private final ConfigService configService;
	private final AskService askService;

	@Inject
	public CreateMailConfig(final MailService mailService, final ConfigService configService,
			final AskService askService) {
		this.mailService = mailService;
		this.configService = configService;
		this.askService = askService;
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

			this.configService.askConfig(newConfig, "user", "What is the mail user ?");
			this.configService.askConfig(newConfig, "password", "What is the mail user password ?");
			this.configService.askConfig(newConfig, "smtp.host", "What is the SMTP host ?");
			this.configService.askConfig(newConfig, "from", "What is the from address ?");
			this.configService.askConfig(newConfig, "ssl",
					"Should I use SSL to connect to the SMTP host (true/false) ?");

			this.configService.askConfig(newConfig, "admin.address", "What is YOUR email address, human minion ?");

			final Configuration finalizedConfig = newConfig.build();

			// Mail config is tested here before being published, because we
			// only want to test the config when publishing it.
			// If we created a separate diagnostic for testing mail config, a
			// mail would be sent at every execution !
			if (this.mailService.testConfig(finalizedConfig)) {
				this.configService.publishConfiguration(finalizedConfig);
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

}
