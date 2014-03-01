package org.mitratul.watchit;

import java.io.IOException;
import java.util.Date;

import org.mitratul.watchit.core.Controller;
import org.mitratul.watchit.core.DirectoryWatcher;

public class Main {

	public static final String OPTION_EXIT = "Exit";

	public static void usage() {
		System.err
				.println("usage: java org.mitratul.watchit.Main <dir to watch>");
		System.exit(-1);
	}

	public static void main(String[] args) {
		// * parse arguments
		if (args.length != 1) {
			usage();
		}

		// * initialize the controller
		Controller controller = Controller.getInstance();

		// * register directory and process its events
		final DirectoryWatcher dw = controller.getWatcher();
		try {
			dw.setDirectory(args[0]);
			System.out.println("Starting watcher on " + args[0]);
		} catch (IOException e) {
			System.err.println("Ouch! Failed to start watcher on " + args[0]);
		}

		// * start the watcher in background thread.
		Thread watcherThread = new Thread() {
			@Override
			public void run() {
				try {
					dw.start();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}

			@Override
			public void interrupt() {
				try {
					dw.stop();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				super.interrupt();
			}
		};
		watcherThread.start();
		System.out.println("Started watcher at: " + new Date());

		// * render the main menu
		controller.getUI().render("");

		System.out.println("Stopping watcher at: " + new Date());
		watcherThread.interrupt();

	}
}
