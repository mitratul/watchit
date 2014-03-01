package org.mitratul.watchit.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.mitratul.watchit.menu.core.ActionOutcome;
import org.mitratul.watchit.menu.core.CliMenu;
import org.mitratul.watchit.menu.core.TextIO;
import org.mitratul.watchit.menu.impl.HideFilterMenu;
import org.mitratul.watchit.menu.impl.HistoryMenu;
import org.mitratul.watchit.menu.impl.ShowFilterMenu;
import org.mitratul.watchit.watcher.CioDirectoryWatcher;

public class Controller {

	private static Controller instance;

	private DirectoryWatcher watcher;
	private List<FileChange> mainHistory;
	private CliMenu mainMenu;

	private Controller() {
		initWatcher();
		initMenu();
		
		mainHistory = new ArrayList<FileChange>();


	}

	public static Controller getInstance() {
		if (instance == null) {
			synchronized (Controller.class) {
				if (instance == null) {
					instance = new Controller();
				}
			}
		}
		return instance;
	}

	public DirectoryWatcher getWatcher(){
		return watcher;
	}

	public List<FileChange> getMainHistory() {
		return Collections.unmodifiableList(mainHistory);
	}
	
	public CliMenu getUI() {
		return mainMenu;
	}
	
	private void initWatcher() {
		watcher = new CioDirectoryWatcher();
		//* add the main history listener
		watcher.addChangeHandler(new FileChangeHandlerIf() {
			@Override public void onChange(FileChange fileChange) {
				mainHistory.add(fileChange);
			}
		});
	}

	private void initMenu() {
		mainMenu = new CliMenu() {
			@Override protected List<TextIO> getOptionList() {
				return new ArrayList<TextIO>(0);
			}
			@Override protected ActionOutcome performAction(String filterOption) {
				return new ActionOutcome("");
			}
			@Override protected TextIO getTextIO() {
				return new TextIO("Main");
			}
			
		};
		// * read and load from properties file
		mainMenu.addSubMenu(new HistoryMenu());
		mainMenu.addSubMenu(new ShowFilterMenu());
		mainMenu.addSubMenu(new HideFilterMenu());
	}
}
