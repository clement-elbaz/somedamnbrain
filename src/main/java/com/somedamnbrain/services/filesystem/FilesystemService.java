package com.somedamnbrain.services.filesystem;

import com.somedamnbrain.exceptions.NoResultException;
import com.somedamnbrain.exceptions.UnexplainableException;

public interface FilesystemService {

	byte[] readFile(final String path) throws NoResultException, UnexplainableException;

	void writeFile(final String path, final byte[] content) throws UnexplainableException;

	void moveFile(String universeFilePath, String string) throws UnexplainableException;

}
