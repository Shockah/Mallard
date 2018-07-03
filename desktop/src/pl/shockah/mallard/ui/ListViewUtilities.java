package pl.shockah.mallard.ui;

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

import javax.annotation.Nonnull;

import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import lombok.experimental.UtilityClass;

@UtilityClass
public final class ListViewUtilities {
	@Nonnull
	private static final Map<ListView<?>, DataFormat> dragAndDropDataFormats = new WeakHashMap<>();

	private static <T> DataFormat getDragAndDropFormat(@Nonnull ListView<T> listView) {
		return dragAndDropDataFormats.computeIfAbsent(listView, key -> {
			return new DataFormat(String.format("%s/dnd", key.toString()));
		});
	}

	public static <T> void setupDragAndDropReorder(@Nonnull ListCell<T> cell) {
		cell.setOnDragDetected(event -> {
			if (cell.getItem() == null)
				return;

			Dragboard dragboard = cell.startDragAndDrop(TransferMode.MOVE);
			dragboard.setDragView(cell.snapshot(new SnapshotParameters(), null));
			dragboard.setContent(new HashMap<DataFormat, Object>() {{
				put(getDragAndDropFormat(cell.getListView()), cell.getIndex());
			}});
			event.consume();
		});

		cell.setOnDragOver(event -> {
			if (event.getGestureSource() != cell && event.getDragboard().hasContent(getDragAndDropFormat(cell.getListView())))
				event.acceptTransferModes(TransferMode.MOVE);
			event.consume();
		});

		cell.setOnDragEntered(event -> {
			if (event.getGestureSource() != cell && event.getDragboard().hasContent(getDragAndDropFormat(cell.getListView())))
				cell.setOpacity(0.3);
		});

		cell.setOnDragExited(event -> {
			if (event.getGestureSource() != cell && event.getDragboard().hasContent(getDragAndDropFormat(cell.getListView())))
				cell.setOpacity(1.0);
		});

		cell.setOnDragDropped(event -> {
			if (cell.getItem() == null)
				return;


			DataFormat dragAndDropDataFormat = getDragAndDropFormat(cell.getListView());
			Dragboard dragboard = event.getDragboard();
			if (dragboard.hasContent(dragAndDropDataFormat)) {
				ObservableList<T> items = cell.getListView().getItems();
				int draggedIndex = (int)dragboard.getContent(dragAndDropDataFormat);

				T draggedItem = items.get(draggedIndex);
				items.set(draggedIndex, cell.getItem());
				items.set(cell.getIndex(), draggedItem);

				event.setDropCompleted(true);
			} else {
				event.setDropCompleted(false);
			}

			event.consume();
		});

		cell.setOnDragDone(Event::consume);
	}
}