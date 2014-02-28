package org.mitratul.watchit.watcher;

import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mitratul.watchit.core.ChangeType;
import org.mitratul.watchit.core.DirectoryWatcher;
import org.mitratul.watchit.core.FileChange;
import org.mitratul.watchit.core.FileChangeHandlerIf;


public class NioDirectoryWatcher implements DirectoryWatcher {

    private final WatchService watcher;
    private final Map<WatchKey,Path> keys;
    //TODO: allow list of directories to watch
    private Path dirToWatch;
    private List<FileChangeHandlerIf> handlerList;
    
    private boolean recursive;
    private boolean trace = false;
    

    public NioDirectoryWatcher()  throws IOException {
        this.watcher = FileSystems.getDefault().newWatchService();
        this.keys = new HashMap<WatchKey, Path>();
        this.handlerList = new ArrayList<FileChangeHandlerIf>();
        this.recursive = true;
    }

	@Override
	public void setDirectory(String dirPath) throws IOException {
		//* First validate that the input path exists.
		if( ! new File(dirPath).isDirectory() ) {
			throw new IOException("Directory " + dirPath + "does not exist.");
		}
		dirToWatch = Paths.get(dirPath);
	}

	@Override
	public void setRecursive(boolean recursive) {
		this.recursive = recursive;
	}
	
	@Override
	public boolean isDirectorySet() {
		return dirToWatch != null;
	}
    
    @SuppressWarnings("unchecked")
    private static <T> WatchEvent<T> cast(WatchEvent<?> event) {
        return (WatchEvent<T>)event;
    }

    /**
     * Register the given directory with the WatchService
     */
    private void register(Path dir) throws IOException {
        WatchKey key = dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
        if (trace) {
            Path prev = keys.get(key);
            if (prev == null) {
                System.out.format("register: %s\n", dir);
            } else {
                if (!dir.equals(prev)) {
                    System.out.format("update: %s -> %s\n", prev, dir);
                }
            }
        }
        keys.put(key, dir);
    }

    /**
     * Register the given directory, and all its sub-directories, with the
     * WatchService.
     */
    private void registerAll(final Path start) throws IOException {
        // register directory and sub-directories
        Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
            @Override public FileVisitResult preVisitDirectory(
            		Path dir, BasicFileAttributes attrs) throws IOException {
                register(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    /**
     * Process all events for keys queued to the watcher
     * @throws IOException 
     */
    public void processEvents() throws IOException {
    	if (recursive) {
            registerAll(dirToWatch);
        } else {
            register(dirToWatch);
        }

        for (;;) {

            // wait for key to be signalled
            WatchKey key;
            try {
                key = watcher.take();
            } catch (InterruptedException x) {
                return;
            }

            Path dir = keys.get(key);
            if (dir == null) {
                System.err.println("WatchKey not recognized!!");
                continue;
            }

            for (WatchEvent<?> event: key.pollEvents()) {
                Kind<?> kind = event.kind();

                // TBD - provide example of how OVERFLOW event is handled
                if (kind == OVERFLOW) {
                    System.err.println("Oops!! Some events might have been lost or discarded");
                    continue;
                }

                // Context for directory entry event is the file name of entry
                WatchEvent<Path> ev = cast(event);
                Path name = ev.context();
                Path child = dir.resolve(name);

                // print out event
                notifyAllHandlers(event.kind().name(), child.toFile().toString());

                // if directory is created, and watching recursively, then
                // register it and its sub-directories
                if (recursive && (kind == ENTRY_CREATE)) {
                    try {
                        if (Files.isDirectory(child, NOFOLLOW_LINKS)) {
                            registerAll(child);
                        }
                    } catch (IOException x) {
                        // ignore to keep sample readable
                    }
                }
            }

            // reset key and remove from set if directory no longer accessible
            boolean valid = key.reset();
            if (!valid) {
                keys.remove(key);

                // all directories are inaccessible
                if (keys.isEmpty()) {
                    break;
                }
            }
        }
    }

	private void notifyAllHandlers(String type, String name) {
		long time = System.currentTimeMillis();
		for (FileChangeHandlerIf handler : handlerList) {
			handler.onChange(new FileChange(name, mapType(type), time));
		}
	}

	private ChangeType mapType(String type) {
		ChangeType changeType = null;
		switch (type) {
		case "ENTRY_CREATE":
			changeType = ChangeType.ENTRY_CREATE;
			break;
		case "ENTRY_MODIFY":
			changeType = ChangeType.ENTRY_MODIFY;
			break;
		case "ENTRY_DELETE":
			changeType = ChangeType.ENTRY_DELETE;
			break;
		default:
			// already checked, cannot occur.
			break;
		}
		
		return changeType;
	}

	@Override
	public void addChangeHandler(FileChangeHandlerIf handler) {
		handlerList.add(handler);
	}

	@Override
	public void removeChangeHandler(FileChangeHandlerIf handler) {
		handlerList.remove(handler);
	}
}
