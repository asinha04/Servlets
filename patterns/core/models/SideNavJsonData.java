package org.kp.patterns.core.models;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;

/**
 * This Java class is used to map the Side Nav JSON Data with the 
 * Java properties.
 * 
 * @author Mohan Joshi
 *
 */

public class SideNavJsonData {

	@Expose
	private List<SideNavLinksData> links = new ArrayList<SideNavLinksData>();

	/**
	 * @return the links
	 */
	public List<SideNavLinksData> getLinks() {
		return new ArrayList<SideNavLinksData>(links);
	}

	/**
	 * @param links
	 *            the links to set
	 */
	public void setLinks(List<SideNavLinksData> links) {
	  this.links.addAll(links);
	}
}
