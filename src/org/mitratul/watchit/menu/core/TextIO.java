package org.mitratul.watchit.menu.core;

public class TextIO {

	private String fullText;
	private String triggerText;

	protected TextIO() {
	}

	public TextIO(String triggerText) {
		this(triggerText, null);
	}

	public TextIO(String triggerText, String fullText) {
		this.triggerText = triggerText;
		this.fullText = fullText;
	}

	public String getFullText() {
		return fullText;
	}

	public String getTriggerText() {
		return triggerText;
	}

	public boolean equals(Object o) {
		return o instanceof TextIO
				&& getTriggerText().equalsIgnoreCase(
						((TextIO) o).getTriggerText());
	}
	
	public int hashCode() {
		return getTriggerText().toLowerCase().hashCode();
	}

	public String toString() {
		return fullText == null 
				? triggerText 
				: triggerText + " (" + fullText + ")";
	}
}
