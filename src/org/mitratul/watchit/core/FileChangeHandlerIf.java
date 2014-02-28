package org.mitratul.watchit.core;

public interface FileChangeHandlerIf {

	/**
	 * Automatically triggered by DirectoryWatcher when it intercepts 
	 * an file change event.
	 * 
	 * @param fileChanged
	 * @param changeType
	 * @param changeTime
	 */
	public void onChange(FileChange fileChange);
}
