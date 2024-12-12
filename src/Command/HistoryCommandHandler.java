package Command;

import java.util.LinkedList;
import java.util.function.Consumer;

public class HistoryCommandHandler implements CommandHandler {
	private int maxHistoryLength = 100;

	private final LinkedList<Command> history = new LinkedList<>();
	private final LinkedList<Command> redoList = new LinkedList<>();
	private Consumer<Void> onStateChanged; // Callback per aggiornare la grafica

	public HistoryCommandHandler() {
		this(100);
	}

	public HistoryCommandHandler(int maxHistoryLength) {
		if (maxHistoryLength < 0)
			throw new IllegalArgumentException();
		this.maxHistoryLength = maxHistoryLength;
	}

	public void setOnStateChanged(Consumer<Void> callback) {
		this.onStateChanged = callback;
	}

	public void handle(Command cmd) {
		if (cmd.doIt()) {
			addToHistory(cmd);
		} else {
			history.clear();
		}
		redoList.clear();
		notifyStateChanged();
	}

	public void redo() {
		if (!redoList.isEmpty()) {
			Command redoCmd = redoList.removeFirst();
			redoCmd.doIt();
			history.addFirst(redoCmd);
			notifyStateChanged();
		}
	}

	public void undo() {
		if (!history.isEmpty()) {
			Command undoCmd = history.removeFirst();
			undoCmd.undoIt();
			redoList.addFirst(undoCmd);
			notifyStateChanged();
		}
	}

	private void notifyStateChanged() {
		if (onStateChanged != null) {
			onStateChanged.accept(null);
		}
	}

	private void addToHistory(Command cmd) {
		history.addFirst(cmd);
		if (history.size() > maxHistoryLength) {
			history.removeLast();
		}
	}

	public boolean isUndoVuoto() {
		return history.isEmpty();
	}

	public boolean isRedoVuoto() {
		return redoList.isEmpty();
	}
}
