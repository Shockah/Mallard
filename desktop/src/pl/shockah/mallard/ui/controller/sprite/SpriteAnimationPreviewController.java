package pl.shockah.mallard.ui.controller.sprite;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import pl.shockah.godwit.geom.MutableVec2;
import pl.shockah.godwit.geom.Rectangle;
import pl.shockah.godwit.geom.Vec2;
import pl.shockah.mallard.project.SpriteProject;

public class SpriteAnimationPreviewController extends AbstractSpritePreviewController {
	@Nonnull
	public final SpriteController spriteController;

	@Nonnull
	public final SpriteProject project;

	@Nonnull
	public final SpriteProject.Animation.Entry animationEntry;

	@Nonnull
	public final DoubleProperty animationSpeed = new SimpleDoubleProperty(this, "animationSpeed", 1.0);

	@Nonnull
	public final Property<Timeline> animation = new SimpleObjectProperty<>(this, "timeline");

	@Nonnull
	private final ListChangeListener<SpriteProject.Animation.Frame> animationFrameListListener;

	@Nonnull
	private final ChangeListener<Number> animationDurationListener;

	public SpriteAnimationPreviewController(@Nonnull SpriteController spriteController, @Nonnull SpriteProject project, @Nonnull SpriteProject.Animation.Entry animationEntry) {
		this.spriteController = spriteController;
		this.project = project;
		this.animationEntry = animationEntry;

		if (!animationEntry.animation.frames.isEmpty())
			frame.setValue(animationEntry.animation.frames.get(0).frame);

		setView(new VBox(8) {{
			setMaxHeight(Double.MAX_VALUE);

			getChildren().addAll(
					new AnchorPane() {{
						VBox.setVgrow(this, Priority.ALWAYS);
						getChildren().add(new TitledPane("Animation Preview", canvas) {{
							setPadding(new Insets(8));
							setCollapsible(false);
							AnchorPane.setTopAnchor(this, 0.0);
							AnchorPane.setBottomAnchor(this, 0.0);
							AnchorPane.setLeftAnchor(this, 0.0);
							AnchorPane.setRightAnchor(this, 0.0);
						}});
					}},
					new TitledPane("Controls", new HBox(4) {{
						setPadding(new Insets(4));
						setAlignment(Pos.CENTER);

						getChildren().addAll(
								new Label("Speed modifier:"),
								new Spinner<Double>(0.1, 10.0, animationSpeed.get(), 0.1) {{
									setEditable(true);
									animationSpeed.bind(valueProperty());
								}},
								new Pane() {{
									prefWidth(16);
								}},
								new Button("Play") {{
									setOnAction(event -> {
										playAnimation();
									});
								}}
						);
					}}) {{
						VBox.setVgrow(this, Priority.NEVER);
						setPadding(new Insets(8));
						setCollapsible(false);
						setMaxHeight(Double.MAX_VALUE);
					}}
			);
		}});

		animationFrameListListener = c -> {
			boolean changed = false;

			while (c.next()) {
				changed = true;
				for (SpriteProject.Animation.Frame frame : c.getAddedSubList()) {
					handleNewFrame(frame);
				}
			}

			if (changed)
				playAnimation();
		};

		for (SpriteProject.Animation.Frame frame : animationEntry.animation.frames) {
			handleNewFrame(frame);
		}

		animationDurationListener = (observable, oldValue, newValue) -> {
			playAnimation();
		};
	}

	@Override
	protected void onAddedToScene(@Nonnull Scene scene) {
		super.onAddedToScene(scene);
		animationEntry.animation.frames.addListener(animationFrameListListener);
		animationEntry.animation.duration.addListener(animationDurationListener);
		playAnimation();
	}

	@Override
	protected void onRemovedFromScene(@Nonnull Scene scene) {
		super.onRemovedFromScene(scene);
		animationEntry.animation.frames.removeListener(animationFrameListListener);
		animationEntry.animation.duration.removeListener(animationDurationListener);
	}

	@Nonnull
	private Timeline createTimeline() {
		double totalWeight = animationEntry.animation.frames.stream()
				.mapToDouble(frame -> frame.relativeDuration.get())
				.sum();

		Timeline timeline = new Timeline();
		double seconds = 0.0;

		MutableVec2 currentOffset = new MutableVec2();
		for (SpriteProject.Animation.Frame frame : animationEntry.animation.frames) {
			currentOffset.x += frame.offset.getValue().x;
			currentOffset.y += frame.offset.getValue().y;
			Vec2 immutableCurrentOffset = currentOffset.asImmutable();
			timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(seconds), event -> {
				SpriteAnimationPreviewController.this.frameOffset.setValue(immutableCurrentOffset);
				SpriteAnimationPreviewController.this.frame.setValue(frame.frame);
			}));
			seconds += (frame.relativeDuration.get() / totalWeight * animationEntry.animation.duration.get()) / animationSpeed.get();
		}
		timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(seconds), event -> {
			stopAnimation();
		}));

		return timeline;
	}

	@Nullable
	private Rectangle createSpriteBounds() {
		if (animationEntry.animation.frames.isEmpty())
			return null;

		double x1 = Integer.MIN_VALUE;
		double x2 = Integer.MIN_VALUE;
		double y1 = Integer.MIN_VALUE;
		double y2 = Integer.MIN_VALUE;

		MutableVec2 currentOffset = new MutableVec2();
		for (SpriteProject.Animation.Frame frame : animationEntry.animation.frames) {
			Image image = frame.frame.image.getValue();
			currentOffset.x += frame.offset.getValue().x;
			currentOffset.y += frame.offset.getValue().y;

			double nx1 = -frame.frame.origin.getValue().x + currentOffset.x;
			double ny1 = -frame.frame.origin.getValue().y + currentOffset.y;
			double nx2 = nx1 + image.getWidth();
			double ny2 = ny1 + image.getHeight();

			if (x1 == Integer.MIN_VALUE || nx1 < x1)
				x1 = nx1;
			if (y1 == Integer.MIN_VALUE || ny1 < y1)
				y1 = ny1;
			if (x2 == Integer.MIN_VALUE || nx2 > x2)
				x2 = nx2;
			if (y2 == Integer.MIN_VALUE || ny2 > y2)
				y2 = ny2;
		}

		return new Rectangle((float)x1, (float)y1, (float)(x2 - x1), (float)(y2 - y1));
	}

	private void handleNewFrame(@Nonnull SpriteProject.Animation.Frame frame) {
		frame.relativeDuration.addListener((observable, oldValue, newValue) -> {
			playAnimation();
		});
	}

	public void stopAnimation() {
		Timeline animation = this.animation.getValue();

		if (animation == null)
			return;

		animation.stop();
		this.animation.setValue(null);
	}

	public void playAnimation() {
		stopAnimation();
		animation.setValue(createTimeline());
		spriteBounds.setValue(createSpriteBounds());
		animation.getValue().play();
	}
}