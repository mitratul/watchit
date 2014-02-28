package org.mitratul.watchit.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.mitratul.menu.core.CliMenu;
import org.mitratul.menu.impl.HistoryMenu;
import org.mitratul.watchit.watcher.NioDirectoryWatcher;

public class Controller {

	private static Controller instance;

	private DirectoryWatcher watcher;
	private List<FileChange> mainHistory;
	private CliMenu mainMenu;

	private Controller() throws IOException {
		initWatcher();
		initMenu();
		
		mainHistory = new ArrayList<FileChange>();


	}

	public static Controller getInstance() throws IOException {
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
	
	public CliMenu getMainMenu() {
		return mainMenu;
	}
	
	private void initWatcher() throws IOException {
		watcher = new NioDirectoryWatcher();
		//* add the main history listener
		watcher.addChangeHandler(new FileChangeHandlerIf() {
			@Override public void onChange(FileChange fileChange) {
				mainHistory.add(fileChange);
			}
		});
	}

	private void initMenu() {
		mainMenu = new CliMenu() {
			@Override public String performAction(String input) {
				return "";
			}
			@Override public String getName() {
				return "Main";
			}
			@Override protected List<String> getOptionList() {
				return new ArrayList<String>(0);
			}
		};
		
		mainMenu.addSubMenu(new HistoryMenu());
	}
}
