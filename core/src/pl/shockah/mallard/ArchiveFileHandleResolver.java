package pl.shockah.mallard;

import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;

import java.io.IOException;
import java.util.zip.ZipFile;

import javax.annotation.Nonnull;

public class ArchiveFileHandleResolver implements FileHandleResolver {
	@Nonnull
	private final ZipFile zip;

	public ArchiveFileHandleResolver(@Nonnull FileHandle zipHandle) {
		try {
			zip = new ZipFile(zipHandle.file());
		} catch (IOException e) {
			throw new GdxRuntimeException(String.format("Couldn't open ZIP file %s.", zipHandle.path()), e);
		}
	}

	@Override
	public FileHandle resolve(String fileName) {
		return new ArchiveFileHandle(zip, fileName);
	}
}