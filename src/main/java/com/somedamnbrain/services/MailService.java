package com.somedamnbrain.services;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;

import com.google.inject.Inject;
import com.somedamnbrain.entities.Entities.ConfigItem;
import com.somedamnbrain.entities.Entities.Configuration;
import com.somedamnbrain.exceptions.NoResultException;
import com.somedamnbrain.exceptions.SystemNotAvailableException;
import com.somedamnbrain.exceptions.UnexplainableException;
import com.somedamnbrain.services.ask.AskService;

public class MailService {

	private final AskService askService;

	@Inject
	public MailService(final AskService askService) {
		this.askService = askService;
	}

	public boolean testConfig(final Configuration configuration)
			throws SystemNotAvailableException, UnexplainableException {
		final String secret = RandomStringUtils.randomAlphanumeric(8);

		try {
			this.sendMail(configuration, "Testing mail configuration",
					"Here is my secret : " + secret + "\n\nSomedamnbrain");
		} catch (final UnexplainableException e) {
			// rare case of catching UnexplainableException early. Since we are
			// testing the configuration, it makes sense to consider any strange
			// failure as a configuration problem.
			e.printStackTrace();
			return false;
		}

		return secret.equals(askService.askHumanMinion("I just sent you a secret by email ! What is it ?"));
	}

	private void sendMail(final Configuration config, final String subject, final String content)
			throws UnexplainableException {
		try {
			final SimpleEmail simpleEmail = new SimpleEmail();
			simpleEmail.setAuthentication(this.getConfig(config, "user"), this.getConfig(config, "password"));

			simpleEmail.setHostName(this.getConfig(config, "smtp.host"));
			simpleEmail.setFrom(this.getConfig(config, "from"));
			simpleEmail.setSSLOnConnect(this.getConfigAsBoolean(config, "ssl"));

			simpleEmail.setSubject(subject);
			simpleEmail.setMsg(content);
			simpleEmail.addTo(this.getConfig(config, "admin.address"));

			simpleEmail.send();
		} catch (final EmailException | NoResultException e) {
			throw new UnexplainableException(e);
		}
	}

	private boolean getConfigAsBoolean(final Configuration config, final String key) throws NoResultException {
		return StringUtils.equalsIgnoreCase("true", this.getConfig(config, key));
	}

	private String getConfig(final Configuration config, final String key) throws NoResultException {
		for (final ConfigItem item : config.getConfigItemsList()) {
			if (StringUtils.equals(key, item.getKey())) {
				return item.getValue();
			}
		}
		throw new NoResultException();
	}

}
