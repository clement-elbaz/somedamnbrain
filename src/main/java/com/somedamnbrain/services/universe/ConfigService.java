package com.somedamnbrain.services.universe;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.somedamnbrain.entities.Entities.Configuration;
import com.somedamnbrain.exceptions.NoResultException;

@Singleton
public class ConfigService {

	private final Map<String, Configuration> currentConfigs;

	@Inject
	public ConfigService() {
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

}
