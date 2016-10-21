package com.somedamnbrain.services.ssh;

import com.google.inject.Inject;
import com.somedamnbrain.exceptions.NoResultException;
import com.somedamnbrain.exceptions.SystemNotAvailableException;
import com.somedamnbrain.services.universe.UniverseService;

public class SSHService {

	private final UniverseService universeService;

	@Inject
	public SSHService(final UniverseService universeService) {
		this.universeService = universeService;
	}

	public boolean testConnection() throws SystemNotAvailableException {
		try {
			this.universeService.getConfig("ssh");
		} catch (final NoResultException e) {
			throw new SystemNotAvailableException(e);
		}

		// TODO
		throw new RuntimeException("not implemented");
	}

}
