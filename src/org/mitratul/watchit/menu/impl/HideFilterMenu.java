package org.mitratul.watchit.menu.impl;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.mitratul.watchit.core.Controller;
import org.mitratul.watchit.core.FileChange;
import org.mitratul.watchit.menu.core.ActionOutcome;
import org.mitratul.watchit.menu.core.CliMenu;
import org.mitratul.watchit.menu.core.TextIO;

public class HideFilterMenu extends CliMenu {

	private final TextIO menuIO;
	// private final TextIO filter;
	private final List<TextIO> optionList;

	public HideFilterMenu() {
		menuIO = new TextIO("Hide", "Filter - exclude files");
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
				"\nFull history of changes in files name of which excludes:");

		List<FileChange> history = Controller.getInstance().getMainHistory();
		Set<Integer> indicesToExclude = new HashSet<Integer>();

		// * record the indices to be excluded
		int indexToExclude = -1;
		for (String criterion : criteria) {
			outcome.appendOutput(" " + criterion);
			String criterionTxt = criterion.replaceAll("\\*", "");
			if (criterion.startsWith("*") && criterion.endsWith("*")) {
				for (FileChange fileChange : history) {
					++indexToExclude;
					if (fileChange.getFileName().contains(criterionTxt)) {
						indicesToExclude.add(indexToExclude);
					}
				}
			} else if (criterion.startsWith("*")) {
				for (FileChange fileChange : history) {
					++indexToExclude;
					if (fileChange.getFileName().endsWith(criterionTxt)) {
						indicesToExclude.add(indexToExclude);
					}
				}
			} else if (criterion.endsWith("*")) {
				for (FileChange fileChange : history) {
					++indexToExclude;
					if (fileChange.getFileName().startsWith(criterionTxt)) {
						indicesToExclude.add(indexToExclude);
					}
				}
			}
		}

		if (history.size() > indicesToExclude.size()) {
			// * exclude the recorded items, put rest in the buffer
			int curIndex = -1;
			for (FileChange filteredItem : history) {
				++curIndex;
				if (!indicesToExclude.contains(curIndex)) {
					outcome.appendOutput("\n\t" + filteredItem.toString());
				}
			}
		} else {
			// * display message for empty list
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
