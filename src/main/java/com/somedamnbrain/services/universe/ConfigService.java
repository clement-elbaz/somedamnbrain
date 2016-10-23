package com.somedamnbrain.services.universe;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.somedamnbrain.entities.Entities.ConfigItem;
import com.somedamnbrain.entities.Entities.Configuration;
import com.somedamnbrain.exceptions.NoResultException;
import com.somedamnbrain.exceptions.SystemNotAvailableException;
import com.somedamnbrain.exceptions.UnexplainableException;
import com.somedamnbrain.services.ask.AskService;

@Singleton
public class ConfigService {

	private final AskService askService;

	private final Map<String, Configuration> currentConfigs;

	@Inject
	public ConfigService(final AskService askService) {
		this.askService = askService;
		this.currentConfigs = new HashMap<String, Configuration>();
	}

	public Configuration getConfig(final String configName) throws NoResultException {
		if (!this.currentConfigs.containsKey(configName)) {
			throw new NoResultException();
		}

		return this.currentConfigs.get(configName);
	}

	public void publishConfiguration(final Configuration config) {
		this.currentConfigs.remove(config.getConfigName());
		this.currentConfigs.put(config.getConfigName(), config);

	}

	public Collection<Configuration> getAllConfigurations() {
		return this.currentConfigs.values();
	}

	public void askConfig(final Configuration.Builder newConfig, final String key, final String humanQuestion)
			throws SystemNotAvailableException, UnexplainableException {
		final ConfigItem.Builder configItem = ConfigItem.newBuilder();
		configItem.setKey(key);

		configItem.setValue(askService.askHumanMinion(humanQuestion));

		newConfig.addConfigItems(configItem.build());
	}

}
