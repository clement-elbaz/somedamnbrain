package com.somedamnbrain.services.ssh;

import com.google.inject.Inject;
import com.somedamnbrain.exceptions.NoResultException;
import com.somedamnbrain.exceptions.SystemNotAvailableException;
import com.somedamnbrain.services.universe.ConfigService;

public class SSHService {

	private final ConfigService configService;

	@Inject
	public SSHService(final ConfigService configService) {
		this.configService = configService;
	}

	public boolean testConnection() throws SystemNotAvailableException {
		try {
			this.configService.getConfig("ssh");
		} catch (final NoResultException e) {
			throw new SystemNotAvailableException(e);
		}

		// TODO
		throw new RuntimeException("not implemented");
	}

}
