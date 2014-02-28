package org.mitratul.watchit.core;

public class FileChange {
	
	private String fileChanged; 
	private ChangeType type; 
	private long changeTime;
	
	private boolean isEmpty;
	
	public FileChange() {
		isEmpty = true;
	}
	
	public FileChange(String fileChanged, ChangeType type, long changeTime) {
		this.fileChanged = fileChanged;
		this.type = type;
		this.changeTime = changeTime;
		
		isEmpty = false;
	}

	public String getFileName() {
		return isEmpty ? null : fileChanged;
	}

	public ChangeType getType() {
		return isEmpty ? null : type;
	}

	public long getChangeTime() {
		return isEmpty ? -1 : changeTime;
	}

	@Override 
	public String toString() {
		return isEmpty ? null : changeTime + ": " + type + ": " + fileChanged;
	}
}
