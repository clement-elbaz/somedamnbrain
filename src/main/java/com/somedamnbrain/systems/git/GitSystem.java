package com.somedamnbrain.systems.git;

import java.util.Arrays;
import java.util.List;

import com.google.inject.Inject;
import com.somedamnbrain.diagnostic.Diagnostic;
import com.somedamnbrain.exceptions.UnexplainableException;
import com.somedamnbrain.services.universe.UniverseService;
import com.somedamnbrain.systems.AbstractSystem;
import com.somedamnbrain.systems.SDBSystem;
import com.somedamnbrain.systems.machine.MachineSystem;

public class GitSystem extends AbstractSystem {

	private final MachineSystem machineSystem;

	private final UniverseService universeService;

	@Inject
	public GitSystem(final MachineSystem machineSystem, final UniverseService universeService) {
		this.machineSystem = machineSystem;
		this.universeService = universeService;
	}

	@Override
	public String getUniqueID() {
		return "Git";
	}

	@Override
	public List<SDBSystem> getDependencies() {
		return Arrays.asList(machineSystem);
	}

	@Override
	public List<Diagnostic> getDiagnostics() {
		return Diagnostic.notImplemented(this, universeService);
	}

	@Override
	public void executeIfOperational() throws UnexplainableException {
		// TODO

	}

}
