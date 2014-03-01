package org.mitratul.watchit.menu.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;


public abstract class CliMenu extends Menu {

	private static final TextIO OPTION_EXIT = new TextIO("Exit");

	/**
	 * Returns the list of options available under a menu.
	 * 
	 * @return
	 */
	abstract protected List<TextIO> getOptionList();

	public void render(String arguments) {
		// * perform the default action of this menu
		ActionOutcome defaultOutcome = performAction(arguments);
		System.out.println(defaultOutcome);

		if (defaultOutcome.isError()
				|| (subMenuList.isEmpty() && getOptionList().isEmpty())) {
			return;
		}
		// * then display the sub-menus
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		for (;;) {
			// * display the options
			System.out.println("\n\n" + "  ---==| Options for menu " + this
					+ " |==---");
			for (Menu subMenu : subMenuList) {
				System.out.println("         [+] " + subMenu);
			}
			for (TextIO option : getOptionList()) {
				System.out.println("         [-] " + option);
			}
			System.out.println("         [-] " + OPTION_EXIT);

			// * ask for the input then
			System.out.print("         [?] Your option: ");
			String input = null;
			try {
				input = br.readLine();
			} catch (IOException e) {
				System.err.println("         [X] My bad! come again.");
			}

			String inOpnStr = input.split(" ")[0];
			TextIO inOption = new TextIO(inOpnStr);

			// * act accordingly
			// * if exit selected, get out of this menu.
			if (OPTION_EXIT.equals(inOption)) {
				return;
			}
			// * if within this menu options, execute that and continue
			if (getOptionList().contains(inOption)) {
				System.out.println(performAction(input));
				continue;
			}
			// * if a sub-menu is selected, open prompt of that
			for (Menu subMenu : subMenuList) {
				if (subMenu.getTextIO().equals(inOption)) {
					// * exclude the sub-menu name, the following white spaces,
					// and send only the options
					subMenu.render(input.substring(inOpnStr.length()).trim());
					break;
				}
			}
		}
	}

	@Override
	public String toString() {
		return getTextIO().toString();
	}

}
