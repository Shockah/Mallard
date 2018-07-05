package pl.shockah.mallard.ui.controller.sprite;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.TitledPane;
import javafx.scene.image.PixelReader;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import lombok.experimental.var;
import pl.shockah.godwit.geom.Shape;
import pl.shockah.mallard.Mallard;
import pl.shockah.mallard.project.SpriteProject;
import pl.shockah.mallard.ui.BindUtilities;
import pl.shockah.mallard.ui.controller.Controller;
import pl.shockah.mallard.ui.controller.sprite.editor.OriginEditor;
import pl.shockah.mallard.ui.controller.sprite.editor.ShapeEditor;
import pl.shockah.mallard.ui.controller.sprite.editor.SpriteFrameEditor;
import pl.shockah.mallard.ui.view.ResizableCanvas;
import pl.shockah.unicorn.Math2;
import pl.shockah.unicorn.func.Action2;
import pl.shockah.unicorn.func.Func2;

public class SpriteFramePreviewController extends Controller {
	@Nonnull
	public final SpriteController spriteController;

	@Nonnull
	public final SpriteProject project;

	@Nonnull
	public final SpriteProject.Frame frame;

	@Nonnull
	public final ResizableCanvas canvas;

	protected boolean additionalEditingPrecision = false;

	@Nonnull
	private final ChangeListener<? super SpriteFrameEditor> currentEditorListener;

	@Nonnull
	private final ListChangeListener<SpriteProject.Frame.ShapeEntry<? extends Shape.Filled>> shapesListListener;

	@Nonnull
	private final Map<SpriteProject.Frame.ShapeEntry<? extends Shape.Filled>, ShapeEditor<? extends Shape.Filled>> shapeToEditorMap = new HashMap<>();

	@Nonnull
	public final ObservableList<SpriteFrameEditor> editors = FXCollections.observableArrayList();

	@Nonnull
	public final OriginEditor originEditor;

	public SpriteFramePreviewController(@Nonnull SpriteController spriteController, @Nonnull SpriteProject project, @Nonnull SpriteProject.Frame frame) {
		this.spriteController = spriteController;
		this.project = project;
		this.frame = frame;

		canvas = new ResizableCanvas() {
			{
				parentProperty().addListener((observable, oldValue, newValue) -> {
					if (newValue != null) {
						BindUtilities.bind(((Region)newValue).widthProperty(), width -> setWidth(width.doubleValue()));
						BindUtilities.bind(((Region)newValue).heightProperty(), height -> setHeight(height.doubleValue()));
					}
				});
			}

			@Override
			protected void draw(@Nonnull GraphicsContext context) {
				super.draw(context);
				SpriteFramePreviewController.this.draw(context);
			}
		};

		setView(new TitledPane("Frame Preview", new VBox() {{
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

		shapesListListener = c -> {
			while (c.next()) {
				for (SpriteProject.Frame.ShapeEntry<? extends Shape.Filled> shapeEntry : c.getRemoved()) {
					editors.remove(shapeToEditorMap.get(shapeEntry));
				}
				for (SpriteProject.Frame.ShapeEntry<? extends Shape.Filled> shapeEntry : c.getAddedSubList()) {
					editors.add(createEditor(shapeEntry));
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
			}
		});

		editors.add(originEditor = new OriginEditor(frame));
	}

	@Override
	protected void onAddedToScene(@Nonnull Scene scene) {
		super.onAddedToScene(scene);
		frame.currentEditor.addListener(currentEditorListener);

		for (SpriteProject.Frame.ShapeEntry<? extends Shape.Filled> shapeEntry : frame.shapes) {
			editors.add(createEditor(shapeEntry));
		}
		frame.shapes.addListener(shapesListListener);
	}

	@Override
	protected void onRemovedFromScene(@Nonnull Scene scene) {
		super.onRemovedFromScene(scene);
		frame.currentEditor.removeListener(currentEditorListener);
		frame.shapes.removeListener(shapesListListener);
	}

	@Nonnull
	@SuppressWarnings("unchecked")
	private ShapeEditor<? extends Shape.Filled> createEditor(@Nonnull SpriteProject.Frame.ShapeEntry<? extends Shape.Filled> shapeEntry) {
		return shapeToEditorMap.computeIfAbsent(shapeEntry, key -> {
			Func2<?, ?, ?> wildcardFactory = key.shapeManagerEntry.editorFactory;
			var rawFactory = (Func2<SpriteProject.Frame, SpriteProject.Frame.ShapeEntry<? extends Shape.Filled>, ShapeEditor<Shape.Filled>>) wildcardFactory;
			return rawFactory.call(frame, key);
		});
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

		double newX = fx * frame.image.getValue().getWidth();
		double newY = fy * frame.image.getValue().getHeight();

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

	public double getImageScale() {
		double padding = 16;

		double scale = (canvas.getWidth() - padding) / frame.image.getValue().getWidth();
		if (frame.image.getValue().getHeight() * scale > (canvas.getHeight() - padding))
			scale = (canvas.getHeight() - padding) / frame.image.getValue().getHeight();
		return scale;
	}

	public double getLeft() {
		double scale = getImageScale();
		double width = frame.image.getValue().getWidth() * scale;
		return canvas.getWidth() * 0.5 - width * 0.5;
	}

	public double getRight() {
		double scale = getImageScale();
		double width = frame.image.getValue().getWidth() * scale;
		return canvas.getWidth() * 0.5 + width * 0.5;
	}

	public double getTop() {
		double scale = getImageScale();
		double height = frame.image.getValue().getHeight() * scale;
		return canvas.getHeight() * 0.5 - height * 0.5;
	}

	public double getBottom() {
		double scale = getImageScale();
		double height = frame.image.getValue().getHeight() * scale;
		return canvas.getHeight() * 0.5 + height * 0.5;
	}

	private void draw(@Nonnull GraphicsContext context) {
		context.clearRect(0, 0, context.getCanvas().getWidth(), context.getCanvas().getHeight());
		drawGrid(context);
		drawImage(context);

		for (SpriteFrameEditor editor : editors) {
			editor.draw(this, context);
		}
	}

	private void drawGrid(@Nonnull GraphicsContext context) {
		double scale = getImageScale();
		double width = frame.image.getValue().getWidth() * scale;
		double height = frame.image.getValue().getHeight() * scale;
		double x1 = canvas.getWidth() * 0.5 - width * 0.5;
		double y1 = canvas.getHeight() * 0.5 - height * 0.5;

		context.setFill(Color.WHITE);
		context.fillRect(x1, y1, width, height);

		context.setFill(Color.gray(0.925));
		for (int y = 0; y < frame.image.getValue().getHeight() * 2; y++) {
			for (int x = 0; x < frame.image.getValue().getWidth() * 2; x++) {
				if ((x + y) % 2 == 0)
					continue;

				double px1 = x1 + x * scale * 0.5;
				double py1 = y1 + y * scale * 0.5;
				double px2 = px1 + scale * 0.5;
				double py2 = py1 + scale * 0.5;

				px1 = Math.round(px1);
				py1 = Math.round(py1);
				px2 = Math.round(px2);
				py2 = Math.round(py2);

				if (px2 - px1 < 1 || py2 - py1 < 1)
					continue;
				context.fillRect(px1, py1, px2 - px1, py2 - py1);
			}
		}
	}

	private void drawImage(@Nonnull GraphicsContext context) {
		double scale = getImageScale();
		double width = frame.image.getValue().getWidth() * scale;
		double height = frame.image.getValue().getHeight() * scale;
		double x1 = canvas.getWidth() * 0.5 - width * 0.5;
		double y1 = canvas.getHeight() * 0.5 - height * 0.5;

		PixelReader pixels = frame.image.getValue().getPixelReader();
		for (int y = 0; y < frame.image.getValue().getHeight(); y++) {
			for (int x = 0; x < frame.image.getValue().getWidth(); x++) {
				Color color = pixels.getColor(x, y);
				context.setFill(color);

				double px1 = x1 + x * scale;
				double py1 = y1 + y * scale;
				double px2 = px1 + scale;
				double py2 = py1 + scale;

				px1 = Math.round(px1);
				py1 = Math.round(py1);
				px2 = Math.round(px2);
				py2 = Math.round(py2);

				if (px2 - px1 < 1 || py2 - py1 < 1)
					continue;
				context.fillRect(px1, py1, px2 - px1, py2 - py1);
			}
		}
	}
}