package com.somedamnbrain.services.filesystem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.io.IOUtils;

import com.somedamnbrain.exceptions.NoResultException;
import com.somedamnbrain.exceptions.UnexplainableException;

public class LocalFileSystemImpl implements FilesystemService {

	@Override
	public byte[] readFile(final String path) throws NoResultException, UnexplainableException {
		final File file = new File(path);

		try {
			final FileInputStream fis = new FileInputStream(file);

			return IOUtils.toByteArray(fis);
		} catch (final FileNotFoundException e) {
			throw new NoResultException(e);
		} catch (final IOException e) {
			throw new UnexplainableException(e);
		}
	}

	@Override
	public void writeFile(final String path, final byte[] content) throws UnexplainableException {
		final File file = new File(path);

		try {
			final FileOutputStream fos = new FileOutputStream(file);
			IOUtils.write(content, fos);
		} catch (final FileNotFoundException e) {
			// if the file exists but is a directory rather than a regular file,
			// does not exist but cannot be created, or cannot be opened for any
			// other reason
			// @ see
			// https://docs.oracle.com/javase/8/docs/api/java/io/FileOutputStream.html#FileOutputStream(java.io.File)
			throw new UnexplainableException(e);
		} catch (final IOException e) {
			throw new UnexplainableException(e);
		}

	}

	@Override
	public void moveFile(final String originalPath, final String newPath)
			throws NoResultException, UnexplainableException {
		final File originalFile = new File(originalPath);

		if (!originalFile.exists() || originalFile.isDirectory()) {
			throw new NoResultException();
		}

		final File newFile = new File(newPath);

		if (!originalFile.renameTo(newFile)) {
			throw new UnexplainableException("Could not move file from path " + originalPath + " to path " + newPath);
		}

	}

}
