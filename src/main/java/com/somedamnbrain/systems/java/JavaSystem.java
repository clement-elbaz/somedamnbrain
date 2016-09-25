package com.somedamnbrain.systems.java;

import java.util.Arrays;
import java.util.List;

import com.google.inject.Inject;
import com.somedamnbrain.diagnostic.Diagnostic;
import com.somedamnbrain.exceptions.UnexplainableException;
import com.somedamnbrain.services.universe.UniverseService;
import com.somedamnbrain.systems.SDBSystem;
import com.somedamnbrain.systems.machine.MachineSystem;

public class JavaSystem implements SDBSystem {

	private final MachineSystem machineSystem;

	private final UniverseService universeService;

	@Inject
	public JavaSystem(final MachineSystem machineSystem, final UniverseService universeService) {
		this.machineSystem = machineSystem;
		this.universeService = universeService;
	}

	@Override
	public String getUniqueID() {
		return "Java";
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
