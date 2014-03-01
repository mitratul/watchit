package org.mitratul.watchit.menu.core;

public class ActionOutcome {

	private StringBuilder output;
	private StringBuilder errorOutput;
	private boolean empty;

	public ActionOutcome() {
		output = new StringBuilder("");
		errorOutput = new StringBuilder("");
		empty = false;
	}

	public ActionOutcome(String output) {
		this(output, false);
	}

	public ActionOutcome(String output, boolean error) {
		this();
		if (error) {
			appendErrorOutput(output);
		} else {
			appendOutput(output);
		}
	}

	public String getOutput() {
		return output.toString();
	}

	public String getErrorOutput() {
		return errorOutput.toString();
	}

	public void appendOutput(String output) {
		this.output.append(output);
	}

	public void appendErrorOutput(String output) {
		empty = true;
		errorOutput.append(output);
	}

	public boolean isError() {
		return empty;
	}

	public String toString() {
		return isError() ? getErrorOutput() : getOutput();
	}

}
