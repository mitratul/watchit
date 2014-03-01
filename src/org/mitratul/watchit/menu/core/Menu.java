package org.mitratul.watchit.menu.core;

import java.util.ArrayList;
import java.util.List;


public abstract class Menu {

	protected Menu parent;
	protected List<Menu> subMenuList;

	public Menu() {
		subMenuList = new ArrayList<Menu>();
	}

	/**
	 * Perform the menu action with the provided input. <br>
	 * It should take care of exposing the sub-menus if available.
	 * 
	 * @param input
	 * @return
	 */
	abstract protected ActionOutcome performAction(String filterOption);

	abstract public void render(String optionWithArgs);

	abstract protected TextIO getTextIO();

	/**
	 * Adds the sub-menu to this menu.
	 * 
	 * @param subMenu
	 */
	public void addSubMenu(Menu... subMenus) {
		for (Menu menu : subMenus) {
			subMenuList.add(menu);
			menu.setParent(this);
		}
	}

	public Menu getParent() {
		return parent;
	}

	public void setParent(Menu parent) {
		this.parent = parent;
	}

	// abstract public String getShortName();
}
