package pl.shockah.mallard;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

import pl.shockah.godwit.asset.Asset;

public class MetadataAsset<T> extends Asset<T> {
	@Nonnull
	public final Asset<T> asset;

	@Nonnull
	public final Map<String, Object> metadata = new HashMap<>();

	public MetadataAsset(@Nonnull Asset<T> asset) {
		this.asset = asset;
	}

	@Override
	public void load() {
		asset.load();
	}

	@Override
	public void unload() {
		asset.unload();
	}

	@Override
	public void finishLoading() {
		asset.finishLoading();
	}

	@Override
	public T get() {
		return asset.get();
	}

	@SuppressWarnings("unchecked")
	@Nonnull
	public <M> M getMetadata(@Nonnull Class<M> clazz, @Nonnull String key) {
		Object value = metadata.get(key);
		if (clazz.isInstance(value))
			return (M)value;
		throw new IllegalArgumentException();
	}
}