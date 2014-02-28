package org.mitratul.watchit;

import java.io.IOException;
import java.util.Date;

import org.mitratul.watchit.core.Controller;
import org.mitratul.watchit.core.DirectoryWatcher;

public class Main {
	
	public static final String OPTION_EXIT = "Exit"; 
	
	private static final String OPTION_NON_RECURSIVE = "nr";

    public static void usage() {
        System.err.println("usage: java org.mitratul.watchit.Main [-nr] <dir to watch>");
        System.err.println("  options: -nr: (optional) non-recursive watch (be default recursive)");
        System.exit(-1);
    }

    public static void main(String[] args) throws IOException {
        //* parse arguments
        if (args.length == 0 || args.length > 2)
            usage();
        boolean recursive = true;
        int dirArg = 0;
        if (OPTION_NON_RECURSIVE.equals(args[0])) {
            if (args.length < 2)
                usage();
            recursive = false;
            dirArg++;
        }
        
        //* initialize the controller
        Controller controller = Controller.getInstance();

        //* register directory and process its events
        final DirectoryWatcher dw = controller.getWatcher();
        dw.setDirectory(args[dirArg]);
        dw.setRecursive(recursive);
        
        Thread watcherThread = new Thread(new Runnable() {
			@Override public void run() {
				try{
					dw.processEvents();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		});
        watcherThread.start();
        System.out.println("Started watcher at: " + new Date());
        
        controller.getMainMenu().prompt();

        System.out.println("Stopping watcher at: " + new Date());
		watcherThread.interrupt();
        
    }
}
