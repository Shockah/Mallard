package pl.shockah.mallard.ui;

import java.util.function.Consumer;

import javax.annotation.Nonnull;

import javafx.beans.value.ObservableValue;
import lombok.experimental.UtilityClass;

@UtilityClass
public final class BindUtilities {
	public static <T> void bind(@Nonnull ObservableValue<T> value, @Nonnull Consumer<T> consumer) {
		value.addListener((observable, oldValue, newValue) -> {
			consumer.accept(newValue);
		});
		consumer.accept(value.getValue());
	}
}