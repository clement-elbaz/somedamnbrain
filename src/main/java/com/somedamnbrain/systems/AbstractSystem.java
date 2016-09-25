package com.somedamnbrain.systems;

public abstract class AbstractSystem implements SDBSystem {

	@Override
	public boolean equals(final Object o) {
		if (o == null) {
			return false;
		}

		if (!(o instanceof SDBSystem)) {
			return false;
		}

		final SDBSystem s = (SDBSystem) o;

		return s.getUniqueID().equals(this.getUniqueID());
	}

	@Override
	public int hashCode() {
		return this.getUniqueID().hashCode();
	}

}
