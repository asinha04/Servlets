package org.kp.patterns.core.panels;

/**
 * Tabbed content component Model class.
 * 
 * @author Manjunath Gurmitkal.
 *
 */
public class TabPanel {

	private String tabTitle;
	private String tabListItemActive;
	private boolean areaSelected;

	/**
	 * @return the tabTitle
	 */
	public String getTabTitle() {
		return tabTitle;
	}

	/**
	 * @param tabTitle
	 *            the tabTitle to set
	 */
	public void setTabTitle(String tabTitle) {
		this.tabTitle = tabTitle;
	}

	/**
	 * @return the tabListItemActive
	 */
	public String getTabListItemActive() {
		return tabListItemActive;
	}

	/**
	 * @param tabListItemActive
	 *            the tabListItemActive to set
	 */
	public void setTabListItemActive(String tabListItemActive) {
		this.tabListItemActive = tabListItemActive;
	}

	/**
	 * @return the areaSelected
	 */
	public boolean isAreaSelected() {
		return areaSelected;
	}

	/**
	 * @param areaSelected
	 *            the areaSelected to set
	 */
	public void setAreaSelected(boolean areaSelected) {
		this.areaSelected = areaSelected;
	}

	@Override
	public String toString() {
		return "TabbedModel [tabTitle=" + tabTitle + ", tabListItemActive="
				+ tabListItemActive + ", areaSelected=" + areaSelected + "]";
	}

}
