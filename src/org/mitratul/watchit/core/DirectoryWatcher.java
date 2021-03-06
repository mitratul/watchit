package org.mitratul.watchit.core;

import java.io.IOException;

public interface DirectoryWatcher {

	/**
	 * Set the directory to watch. 
	 * <br>Throws IOException if the directory doesn't already exist.
	 * @param dirPath
	 * @throws IOException
	 */
	public void setDirectory(String dirPath) throws IOException;
	
	/**
	 * Checks if the directory to watch is already set or not.
	 * @return
	 */
	public boolean isDirectorySet();
	
	/**
	 * Processes the file change events in the watched directory(s). 
	 * For each change events, notifies all registered handlers. 
	 * <br>
	 * Throws IOException if the watch service registration fails 
	 * for any directory under watch.
	 * @throws Exception 
	 */
	public void start() throws Exception;
	
	public void stop() throws Exception;
	/**
	 * Registers the handler with the watcher.
	 * @param handler
	 */
	public void addChangeHandler(FileChangeHandlerIf handler);
	
	/**
	 * Removes the registration of the handler with the watcher.
	 * @param handler
	 */
	public void removeChangeHandler(FileChangeHandlerIf handler);
}
