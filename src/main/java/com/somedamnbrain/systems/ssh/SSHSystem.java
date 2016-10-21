package com.somedamnbrain.systems.ssh;

import java.util.Arrays;
import java.util.List;

import com.google.inject.Inject;
import com.somedamnbrain.diagnostic.Diagnostic;
import com.somedamnbrain.exceptions.UnexplainableException;
import com.somedamnbrain.systems.AbstractSystem;
import com.somedamnbrain.systems.SDBSystem;
import com.somedamnbrain.systems.mail.MailSystem;
import com.somedamnbrain.systems.ssh.diagnostics.SSHConfigPresent;
import com.somedamnbrain.systems.ssh.diagnostics.SSHTestConnection;

public class SSHSystem extends AbstractSystem {

	private final MailSystem mailSystem;

	private final SSHConfigPresent sshConfigPresent;
	private final SSHTestConnection sshTestConnection;

	@Inject
	public SSHSystem(final MailSystem mailSystem, final SSHConfigPresent sshConfigPresent,
			final SSHTestConnection sshTestConnection) {
		this.mailSystem = mailSystem;
		this.sshConfigPresent = sshConfigPresent;
		this.sshTestConnection = sshTestConnection;
	}

	@Override
	public String getUniqueID() {
		return "SSH";
	}

	@Override
	public List<SDBSystem> getDependencies() {
		return Arrays.asList(mailSystem);
	}

	@Override
	public List<Diagnostic> getDiagnostics() {
		return Arrays.asList(sshConfigPresent, sshTestConnection);
	}

	@Override
	public void executeIfOperational() throws UnexplainableException {
		// TODO

	}

}
