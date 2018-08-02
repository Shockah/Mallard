package pl.shockah.mallard.ui.controller.sprite;

import javax.annotation.Nonnull;

import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.TitledPane;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import pl.shockah.godwit.geom.Shape;
import pl.shockah.mallard.Mallard;
import pl.shockah.mallard.project.SpriteProject;
import pl.shockah.mallard.ui.controller.sprite.editor.OriginEditor;
import pl.shockah.mallard.ui.controller.sprite.editor.SpriteFrameEditor;
import pl.shockah.unicorn.Math2;
import pl.shockah.unicorn.func.Action2;

public class SpriteFramePreviewController extends AbstractSpritePreviewController {
	@Nonnull
	public final SpriteController spriteController;

	@Nonnull
	public final SpriteProject project;

	protected boolean additionalEditingPrecision = false;

	@Nonnull
	private final ChangeListener<? super SpriteFrameEditor> currentEditorListener;

	@Nonnull
	private final ListChangeListener<SpriteProject.Frame.ShapeEntry<? extends Shape.Filled>> shapesListListener;

	@Nonnull
	public final ObservableList<SpriteFrameEditor> editors = FXCollections.observableArrayList();

	@Nonnull
	public final OriginEditor originEditor;

	public SpriteFramePreviewController(@Nonnull SpriteController spriteController, @Nonnull SpriteProject project, @Nonnull SpriteProject.Frame frame) {
		this.spriteController = spriteController;
		this.project = project;
		this.frame.setValue(frame);

		setRoot(new TitledPane("Frame Preview", new VBox() {{
			getChildren().add(canvas);
			VBox.setVgrow(canvas, Priority.ALWAYS);
		}}) {{
			setPadding(new Insets(8));
			setCollapsible(false);
			setMaxHeight(Double.MAX_VALUE);
		}});

		frame.image.addListener((observable, oldValue, newValue) -> {
			canvas.redraw();
		});

		canvas.setOnMouseMoved(event -> {
			SpriteFrameEditor editor = frame.currentEditor.getValue();
			if (editor == null)
				return;

			calculateMousePositionAndProceed(event.getX(), event.getY(), (x, y) -> editor.onMouseMove(SpriteFramePreviewController.this, x, y));
		});

		canvas.setOnMouseClicked(event -> {
			SpriteFrameEditor editor = frame.currentEditor.getValue();
			if (editor == null)
				return;

			calculateMousePositionAndProceed(event.getX(), event.getY(), (x, y) -> editor.onMouseClick(SpriteFramePreviewController.this, x, y, event.getButton()));
		});

		canvas.setOnKeyPressed(event -> {
			additionalEditingPrecision = event.isAltDown();
		});

		canvas.setOnKeyReleased(event -> {
			additionalEditingPrecision = event.isAltDown();

			SpriteFrameEditor editor = frame.currentEditor.getValue();
			if (editor == null)
				return;

			if (event.getCode() == KeyCode.ESCAPE) {
				frame.currentEditor.setValue(null);
				editor.onCancel(SpriteFramePreviewController.this);
			}
		});

		currentEditorListener = (observable, oldValue, newValue) -> {
			if (oldValue == null && newValue != null) {
				canvas.setFocusTraversable(true);
				canvas.requestFocus();
			} else {
				Mallard.getStage().requestFocus();
				canvas.setFocusTraversable(false);
			}
		};

		frame.shapes.forEach(this::onNewShape);

		shapesListListener = c -> {
			while (c.next()) {
				for (SpriteProject.Frame.ShapeEntry<? extends Shape.Filled> shapeEntry : c.getRemoved()) {
					editors.remove(shapeEntry.editor);
				}
				for (SpriteProject.Frame.ShapeEntry<? extends Shape.Filled> shapeEntry : c.getAddedSubList()) {
					editors.add(shapeEntry.editor);
					onNewShape(shapeEntry);
				}
			}
		};

		editors.addListener((ListChangeListener<SpriteFrameEditor>) c -> {
			while (c.next()) {
				for (SpriteFrameEditor editor : c.getRemoved()) {
					frame.currentEditor.removeListener(editor.changeListener);
				}
				for (SpriteFrameEditor editor : c.getAddedSubList()) {
					frame.currentEditor.addListener(editor.changeListener);
				}
				canvas.redraw();
			}
		});

		editors.add(originEditor = new OriginEditor(frame));
	}

	private void onNewShape(@Nonnull SpriteProject.Frame.ShapeEntry<? extends Shape.Filled> shapeEntry) {
		shapeEntry.visible.addListener((observable, oldValue, newValue) -> {
			canvas.redraw();
		});
		shapeEntry.color.addListener((observable, oldValue, newValue) -> {
			canvas.redraw();
		});
	}

	@Override
	protected void onAddedToScene(@Nonnull Scene scene) {
		super.onAddedToScene(scene);
		frame.getValue().currentEditor.addListener(currentEditorListener);

		for (SpriteProject.Frame.ShapeEntry<? extends Shape.Filled> shapeEntry : frame.getValue().shapes) {
			editors.add(shapeEntry.editor);
		}
		frame.getValue().shapes.addListener(shapesListListener);
	}

	@Override
	protected void onRemovedFromScene(@Nonnull Scene scene) {
		super.onRemovedFromScene(scene);
		frame.getValue().currentEditor.removeListener(currentEditorListener);
		frame.getValue().shapes.removeListener(shapesListListener);
	}

	private void calculateMousePositionAndProceed(double eventX, double eventY, @Nonnull Action2<Double, Double> func) {
		double scale = getImageScale();
		double x1 = getLeft();
		double y1 = getTop();
		double x2 = getRight();
		double y2 = getBottom();

		double extra = scale * 0.5;
		if (additionalEditingPrecision)
			extra /= 4;

		double mx = Math2.clamp(eventX + extra, x1, x2);
		double my = Math2.clamp(eventY + extra, y1, y2);
		double fx = (mx - x1) / (x2 - x1);
		double fy = (my - y1) / (y2 - y1);

		double newX = fx * frame.getValue().image.getValue().getWidth();
		double newY = fy * frame.getValue().image.getValue().getHeight();

		if (additionalEditingPrecision) {
			newX *= 4;
			newY *= 4;
		}

		newX = (int)newX;
		newY = (int)newY;

		if (additionalEditingPrecision) {
			newX /= 4;
			newY /= 4;
		}

		func.call(newX, newY);
	}

	@Override
	protected void draw(@Nonnull GraphicsContext context) {
		super.draw(context);

		for (SpriteFrameEditor editor : editors) {
			editor.draw(this, context);
		}
	}
}