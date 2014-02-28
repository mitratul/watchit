package org.mitratul.menu.impl;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.mitratul.menu.core.CliMenu;
import org.mitratul.watchit.core.Controller;
import org.mitratul.watchit.core.FileChange;

public class HistoryMenu extends CliMenu {

	private final String name;
	private final String filter;
	private final List<String> optionList;
	
	public HistoryMenu() {
		name = "history";
		
		filter = "Filter";
		
		optionList = Arrays.asList(filter);
	}

	@Override
	public String performAction() {
		StringBuffer buffer = new StringBuffer("");
		buffer.append("\nFull history of changes in all watched directory:");
		try {
			List<FileChange> history = Controller.getInstance().getMainHistory();
			for (FileChange fileChange : history) {
				buffer.append("\n\t" + fileChange.toString());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return buffer.toString();
	}

	@Override
	public String performAction(String input) {
		String retValue = null;
		String option = input.split(" ")[0];
		String[] arguments = Arrays.copyOfRange(input.split(" "), 1, input.split(" ").length);
		
		if(filter.equalsIgnoreCase(option)) {
			if(arguments.length > 0) {
				retValue = filter(arguments[0]);
			}
		} //* else cannot come; validated in prompt()

		return retValue;
	}
	
	private String filter(String id) {
		StringBuffer buffer = new StringBuffer("");
		buffer.append("\nFull history of changes in history item: " + id);
		try {
			List<FileChange> history = Controller.getInstance().getMainHistory();
			String filteredFileName = history.get(Integer.valueOf(id)).getFileName();
			for (FileChange fileChange : history) {
				if(filteredFileName.equals(fileChange.getFileName())) {
					buffer.append("\n\t" + fileChange.toString());
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return buffer.toString();
	}

	@Override
	protected String getName() {
		return name;
	}

	@Override
	protected List<String> getOptionList() {
		return Collections.unmodifiableList(optionList);
	}

}
