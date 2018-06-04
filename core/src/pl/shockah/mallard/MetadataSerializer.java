package pl.shockah.mallard;

import javax.annotation.Nonnull;

public abstract class MetadataSerializer<T, JsonType> {
	@Nonnull
	public final String key;

	public MetadataSerializer(@Nonnull String key) {
		this.key = key;
	}

	@Nonnull
	public abstract JsonType serialize(@Nonnull T value);

	@Nonnull
	public abstract T deserialize(@Nonnull JsonType input);
}