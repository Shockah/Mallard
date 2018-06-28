package pl.shockah.mallard.ui;

import javax.annotation.Nonnull;

import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import pl.shockah.mallard.project.SpriteProject;

public class SpriteFramePreviewView extends Region {
	@Nonnull
	public final SpriteProject.Frame frame;

	@Nonnull
	protected final ImageView imageView;

	public SpriteFramePreviewView(@Nonnull SpriteProject.Frame frame) {
		this.frame = frame;

		imageView = new PixelatedImageView();
		imageView.setSmooth(false);
		imageView.setPreserveRatio(true);
		imageView.setImage(frame.image);
		getChildren().add(imageView);

		imageView.fitWidthProperty().bind(widthProperty());
		imageView.fitHeightProperty().bind(heightProperty());
	}

	@Override
	protected void layoutChildren() {
		super.layoutChildren();
		imageView.setLayoutX(0);
		imageView.setLayoutY(0);
	}
}