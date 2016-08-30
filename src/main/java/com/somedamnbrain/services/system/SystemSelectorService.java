package com.somedamnbrain.services.system;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import javax.inject.Singleton;

import com.somedamnbrain.systems.SDBSystem;

@Singleton
public class SystemSelectorService {
	/**
	 * Once a system is fully diagnosticated, it goes here so it can't be
	 * skipped the next time. The structure can be accessed in constant time.
	 */
	private final Set<SDBSystem> diagnosticatedSystems = new HashSet<SDBSystem>();

	/**
	 * Compute an order for a full system diagnostic. A root system is given, an
	 * iterator is returned. By running diagnostics on every systems provided by
	 * the iterator in the correct order, the root system and its dependencies
	 * will al be checked in the right order.
	 * 
	 * @param rootSystem
	 *            system to compute dependencies on
	 * @return an ordered iterator of systems
	 */
	public Iterator<SDBSystem> computeDependenciesResolution(SDBSystem rootSystem) {
		// If the full system is already diagnosticated, we fully skip it.
		if (!diagnosticatedSystems.contains(rootSystem)) {
			// Seen systems is a (usually) constant time way to quickly detect a
			// system that is already in the list
			final Set<SDBSystem> seenSystems = new HashSet<SDBSystem>();
			// We do not refer to this LinkedList as a List because we care
			// about
			// algorithm complexity and want to use a few specific LinkedList
			// methods.
			final LinkedList<SDBSystem> orderedSystems = new LinkedList<SDBSystem>();

			this.addSystemToList(rootSystem, orderedSystems, seenSystems);
			this.recursiveManageSystem(rootSystem, orderedSystems, seenSystems);

			// The systems which need to be diagnosticated first are now at the
			// end of the list : the list can be diagnosticated in reversed
			// order.
			return orderedSystems.descendingIterator();
		} else {
			return Collections.<SDBSystem>emptyList().iterator();
		}

	}

	/**
	 * Mark the system as diagnosticated.
	 * 
	 * @param system
	 *            system
	 */
	public void markSystemAsDiagnosticated(SDBSystem system) {
		this.diagnosticatedSystems.add(system);
	}

	private void recursiveManageSystem(SDBSystem system, LinkedList<SDBSystem> orderedSystems,
			Set<SDBSystem> seenSystems) {

		// There are two loops here instead of one to get a BFS behavior
		// instead of a DFS behavior.

		for (SDBSystem childSystem : system.getDependencies()) {
			if (!diagnosticatedSystems.contains(system)) {
				this.addSystemToList(childSystem, orderedSystems, seenSystems);
			}

		}

		for (SDBSystem childSystem : system.getDependencies()) {
			if (!diagnosticatedSystems.contains(system)) {
				this.recursiveManageSystem(childSystem, orderedSystems, seenSystems);
			}
		}

	}

	private void addSystemToList(SDBSystem system, LinkedList<SDBSystem> orderedSystems, Set<SDBSystem> seenSystems) {
		if (seenSystems.contains(system)) { // Constant time.
			// We remove the seen system from the ordered list. Linear time.
			orderedSystems.remove(system);
		} else {
			// We mark the system as seen. Constant time.
			seenSystems.add(system);
		}
		// We add the system at the end of the ordered systems list. Constant
		// time.
		orderedSystems.add(system);
	}

}
