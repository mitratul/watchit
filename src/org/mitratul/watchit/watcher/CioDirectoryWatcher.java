package org.mitratul.watchit.watcher;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.mitratul.watchit.core.ChangeType;
import org.mitratul.watchit.core.DirectoryWatcher;
import org.mitratul.watchit.core.FileChange;
import org.mitratul.watchit.core.FileChangeHandlerIf;

public class CioDirectoryWatcher implements DirectoryWatcher {

	private final long monitorInterval;

	// TODO: allow list of directories to watch
	private File dirToWatch;
	private List<FileChangeHandlerIf> handlerList;

	private FileAlterationObserver observer;
	private FileAlterationMonitor monitor;

	public CioDirectoryWatcher() {
		handlerList = new ArrayList<FileChangeHandlerIf>();
		monitorInterval = 3000L;
	}

	@Override
	public void setDirectory(String dirPath) throws IOException {
		// * First validate that the input path exists.
		File tmpFile = new File(dirPath);
		if (!tmpFile.isDirectory()) {
			throw new IOException("Directory " + dirPath + " does not exist.");
		}
		dirToWatch = tmpFile;
	}

	@Override
	public boolean isDirectorySet() {
		return dirToWatch != null;
	}

	/**
	 * Register the given directory with the WatchService
	 */
	private void register(File dir) {
		observer = new FileAlterationObserver(dir);
		monitor = new FileAlterationMonitor(monitorInterval, observer);
	}

	/**
	 * Process all events for keys queued to the watcher
	 * 
	 * @throws Exception
	 */
	public void start() throws Exception {
		// * register the watcher
		register(dirToWatch);

		observer.addListener(new FileAlterationListenerAdaptor() {
			@Override
			public void onFileCreate(File fileCreated) {
				notifyCreate(fileCreated.getAbsolutePath());
			}

			@Override
			public void onDirectoryCreate(File dirCreated) {
				notifyCreate(dirCreated.getAbsolutePath());
			}

			@Override
			public void onFileChange(File fileChanged) {
				notifyModify(fileChanged.getAbsolutePath());
			}

			@Override
			public void onDirectoryChange(File dirChanged) {
				notifyModify(dirChanged.getAbsolutePath());
			}

			@Override
			public void onFileDelete(File fileDeleted) {
				notifyDelete(fileDeleted.getAbsolutePath());
			}

			@Override
			public void onDirectoryDelete(File dirDeleted) {
				notifyDelete(dirDeleted.getAbsolutePath());
			}
		});

		try {
			monitor.start();
		} catch (Exception ex) {
			throw new Exception("Could not initialize file the observer.", ex);
		}
	}

	public void stop() throws Exception {
		monitor.stop();
	}

	private void notifyCreate(String name) {
		notifyAllHandlers(ChangeType.ENTRY_CREATE, name);
	}

	private void notifyModify(String name) {
		notifyAllHandlers(ChangeType.ENTRY_MODIFY, name);
	}

	private void notifyDelete(String name) {
		notifyAllHandlers(ChangeType.ENTRY_DELETE, name);
	}

	private void notifyAllHandlers(ChangeType type, String name) {
		long time = System.currentTimeMillis();
		for (FileChangeHandlerIf handler : handlerList) {
			handler.onChange(new FileChange(name, type, time));
		}
	}

	@Override
	public void addChangeHandler(FileChangeHandlerIf handler) {
		handlerList.add(handler);
	}

	@Override
	public void removeChangeHandler(FileChangeHandlerIf handler) {
		handlerList.remove(handler);
	}

	public static void main(String[] args) throws Exception {
		File directory = new File(args[0]);
		FileAlterationObserver observer = new FileAlterationObserver(directory);
		observer.addListener(new FileAlterationListenerAdaptor() {

			@Override
			public void onFileDelete(File arg0) {
				System.out.println("onFileDelete: " + arg0);
			}

			@Override
			public void onFileCreate(File arg0) {
				System.out.println("onFileCreate: " + arg0);
			}

			@Override
			public void onFileChange(File arg0) {
				System.out.println("onFileChange: " + arg0);
			}

			@Override
			public void onDirectoryDelete(File arg0) {
				System.out.println("onDirectoryDelete: " + arg0);
			}

			@Override
			public void onDirectoryCreate(File arg0) {
				System.out.println("onDirectoryCreate: " + arg0);
			}

			@Override
			public void onDirectoryChange(File arg0) {
				System.out.println("onDirectoryChange: " + arg0);
			}
		});

		long interval = 1000;
		FileAlterationMonitor monitor = new FileAlterationMonitor(interval,
				observer);
		monitor.start();
		System.out.println("Staring monitor on: " + args[0]);
	}

}
