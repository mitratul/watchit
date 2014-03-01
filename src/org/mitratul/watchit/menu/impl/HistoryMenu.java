package org.mitratul.watchit.menu.impl;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.mitratul.watchit.core.Controller;
import org.mitratul.watchit.core.FileChange;
import org.mitratul.watchit.menu.core.ActionOutcome;
import org.mitratul.watchit.menu.core.CliMenu;
import org.mitratul.watchit.menu.core.TextIO;

public class HistoryMenu extends CliMenu {

	private final TextIO menuIO;
	private final TextIO filter;
	private final List<TextIO> optionList;

	public HistoryMenu() {
		menuIO = new TextIO("History", "Full history of watched folder(s)");
		filter = new TextIO("Filter",
				"Filter based on file selected by serial number");
		optionList = Arrays.asList(filter);
	}

	@Override
	public ActionOutcome performAction(String filterOption) {
		if (filterOption == null || filterOption.length() == 0) {
			return performDefaultAction();
		}
		ActionOutcome outcome = null;
		String[] optionWithArgs = filterOption.split(" ");
		TextIO option = new TextIO(optionWithArgs[0]);
		// String[] arguments = Arrays.copyOfRange(optionWithArgs, 1,
		// optionWithArgs.length);

		if (filter.equals(option)) {
			if (optionWithArgs.length > 1) {
				outcome = filter(optionWithArgs[1]);
			}
		} // * else cannot come; validated in prompt()

		return outcome;
	}

	public ActionOutcome performDefaultAction() {
		ActionOutcome outcome = new ActionOutcome(
				"\nFull history of changes in all watched directory:");
		List<FileChange> history = Controller.getInstance().getMainHistory();
		for (FileChange fileChange : history) {
			outcome.appendOutput("\n\t" + fileChange.toString());
		}
		// * display message for empty list
		if (history.size() == 0) {
			outcome = new ActionOutcome("\nDidn't see nothing.", true);
		}

		return outcome;
	}

	private ActionOutcome filter(String id) {
		ActionOutcome outcome = new ActionOutcome(
				"\nHistory of changes in history item: " + id);

		List<FileChange> history = Controller.getInstance().getMainHistory();
		String filteredFileName = history.get(Integer.valueOf(id))
				.getFileName();

		boolean found = false;
		for (FileChange fileChange : history) {
			if (filteredFileName.equals(fileChange.getFileName())) {
				found = true;
				outcome.appendOutput("\n\t" + fileChange.toString());
			}
		}
		// * display message for empty list
		if (!found) {
			outcome = new ActionOutcome("\nBetter luck next time", true);
		}

		return outcome;
	}

	@Override
	protected List<TextIO> getOptionList() {
		return Collections.unmodifiableList(optionList);
	}

	@Override
	protected TextIO getTextIO() {
		return menuIO;
	}

}
