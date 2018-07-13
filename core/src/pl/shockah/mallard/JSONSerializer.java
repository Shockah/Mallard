package pl.shockah.mallard;

import javax.annotation.Nonnull;

import pl.shockah.jay.JSONObject;

public abstract class JSONSerializer<T> {
	@Nonnull
	public final String type;

	public JSONSerializer(@Nonnull String type) {
		this.type = type;
	}

	@Nonnull
	public abstract JSONObject serialize(@Nonnull T object);

	@Nonnull
	public abstract T deserialize(@Nonnull JSONObject json);
}