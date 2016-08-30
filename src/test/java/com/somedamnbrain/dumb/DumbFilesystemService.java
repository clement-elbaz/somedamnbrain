package com.somedamnbrain.dumb;

import java.util.HashMap;
import java.util.Map;

import com.google.inject.Singleton;
import com.somedamnbrain.exceptions.NoResultException;
import com.somedamnbrain.exceptions.UnexplainableException;
import com.somedamnbrain.services.filesystem.FilesystemService;

@Singleton
public class DumbFilesystemService implements FilesystemService {

	private final Map<String, ByteArray> dumbFilesystem = new HashMap<String, ByteArray>();

	@Override
	public byte[] readFile(String path) throws NoResultException, UnexplainableException {
		if (dumbFilesystem.containsKey(path)) {
			return dumbFilesystem.get(path).getByteArray();
		}

		throw new NoResultException();
	}

	@Override
	public void writeFile(String path, byte[] content) throws UnexplainableException {
		dumbFilesystem.put(path, new ByteArray(content));

	}

	@Override
	public void moveFile(String originalPath, String newPath) throws NoResultException, UnexplainableException {
		ByteArray content = dumbFilesystem.get(originalPath);

		if (content != null) {
			dumbFilesystem.remove(originalPath);
			dumbFilesystem.put(newPath, content);
		} else {
			throw new NoResultException();
		}

	}

	static class ByteArray {
		private final byte[] byteArray;

		public ByteArray(byte[] byteArray) {
			this.byteArray = byteArray;
		}

		public byte[] getByteArray() {
			return byteArray;
		}
	}

}
