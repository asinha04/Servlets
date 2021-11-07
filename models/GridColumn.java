package org.kp.foundation.core.models;

public class GridColumn {
	private String htmlClass;
	private String analyticsLocation;
	private String resourceType;
	private String resourcePath;
	
	public GridColumn(String htmlClass, String analyticsLocation, String resourceType, String resourcePath) {
		this.htmlClass = htmlClass;
		this.analyticsLocation = analyticsLocation;
		this.resourceType = resourceType;
		this.resourcePath = resourcePath;
	}
	/**
	 * @return the htmlClass
	 */
	public String getHtmlClass() {
		return htmlClass;
	}
	/**
	 * @param htmlClass the htmlClass to set
	 */
	public void setHtmlClass(String htmlClass) {
		this.htmlClass = htmlClass;
	}
	/**
	 * @return the analyticsLocation
	 */
	public String getAnalyticsLocation() {
		return analyticsLocation;
	}
	/**
	 * @param analyticsLocation the analyticsLocation to set
	 */
	public void setAnalyticsLocation(String analyticsLocation) {
		this.analyticsLocation = analyticsLocation;
	}
	/**
	 * @return the resourceType
	 */
	public String getResourceType() {
		return resourceType;
	}
	/**
	 * @param resourceType the resourceType to set
	 */
	public void setResourceType(String resourceType) {
		this.resourceType = resourceType;
	}
	/**
	 * @return the resourcePath
	 */
	public String getResourcePath() {
		return resourcePath;
	}
	/**
	 * @param resourcePath the resourcePath to set
	 */
	public void setResourcePath(String resourcePath) {
		this.resourcePath = resourcePath;
	} 
}
