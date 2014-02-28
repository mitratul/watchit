package org.mitratul.menu.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;


public abstract class CliMenu extends Menu {
	
	private static final String OPTION_EXIT = "Exit";
	
	abstract protected List<String> getOptionList(); 
	
	public void prompt() {
		//* perform the default action of this menu
		System.out.println(performAction());
		
		if(subMenuList.isEmpty() && getOptionList().isEmpty()) {
			return;
		}
		//* then display the sub-menus
		BufferedReader br = new BufferedReader(
				new InputStreamReader(System.in));
		for(;;){
			//* display the options
			System.out.println("\n\n" +
					    "  ---==| Options for menu " + this + " |==---");
			for (Menu subMenu : subMenuList) {
				System.out.println(
						"         [+] " + subMenu);
			}
			for (String option : getOptionList()) {
				System.out.println(
						"         [-] " + option);
			}
			System.out.println(
					    "         [-] " + OPTION_EXIT);
			
			//* ask for the input then
			System.out.print(
					    "         [?] Your option: ");
			String input = null;
			try {
				input = br.readLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.err.println(
						"         [X] Oops! Could not read input.");
			}
			
			String inOption = input.split(" ")[0];
			
			//* act accordingly
			//* if exit selected, get out of this menu.
			if (OPTION_EXIT.equalsIgnoreCase(inOption)) {
				return;
			}
			//* if within this menu options, execute that and continue 
			if (getOptionList().contains(inOption) || 
					getOptionList().contains(inOption.toUpperCase())) {
				System.out.println(performAction(input));
				continue;
			}
			//* if a sub-menu is selected, open prompt of that 
			for (Menu subMenu : subMenuList) {
				if(subMenu.toString().equalsIgnoreCase(inOption)){
					subMenu.prompt();
					break;
				}
			}
		}
	}
	
	@Override 
	public String toString() {
		return getName().replaceAll(" ", "_");
	}

}
