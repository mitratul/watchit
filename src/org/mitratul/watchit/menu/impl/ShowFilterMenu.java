package org.mitratul.watchit.menu.impl;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.mitratul.watchit.core.Controller;
import org.mitratul.watchit.core.FileChange;
import org.mitratul.watchit.menu.core.ActionOutcome;
import org.mitratul.watchit.menu.core.CliMenu;
import org.mitratul.watchit.menu.core.TextIO;

public class ShowFilterMenu extends CliMenu {

	private final TextIO menuIO;
	// private final TextIO filter;
	private final List<TextIO> optionList;

	public ShowFilterMenu() {
		menuIO = new TextIO("Show", "Filter - show files");
		// filter = new TextIO("Filter",
		// "Filter based on file selected by serial number");
		optionList = Arrays.asList();
	}

	@Override
	public ActionOutcome performAction(String filterOption) {
		if (filterOption == null || filterOption.length() == 0) {
			return new ActionOutcome("\nDidn't see nothing.", true);
		}
		return filter(filterOption.split(" "));
	}

	private ActionOutcome filter(String... criteria) {
		ActionOutcome outcome = new ActionOutcome(
				"\nFull history of changes in files which includes: ");
		List<FileChange> history = Controller.getInstance().getMainHistory();

		boolean found = false;
		for (String criterion : criteria) {
			String criterionTxt = criterion.replaceAll("\\*", "");
			if (criterion.startsWith("*") && criterion.endsWith("*")) {
				for (FileChange fileChange : history) {
					if (fileChange.getFileName().contains(criterionTxt)) {
						found = true;
						outcome.appendOutput("\n\t" + fileChange.toString());
					}
				}
			} else if (criterion.startsWith("*")) {
				for (FileChange fileChange : history) {
					if (fileChange.getFileName().endsWith(criterionTxt)) {
						found = true;
						outcome.appendOutput("\n\t" + fileChange.toString());
					}
				}
			} else if (criterion.endsWith("*")) {
				for (FileChange fileChange : history) {
					if (fileChange.getFileName().startsWith(criterionTxt)) {
						found = true;
						outcome.appendOutput("\n\t" + fileChange.toString());
					}
				}
			}
		}
		// * display message for empty list
		if (!found) {
			outcome = new ActionOutcome("\nBetter luck next time.", true);
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
