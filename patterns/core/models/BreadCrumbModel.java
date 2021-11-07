package org.kp.patterns.core.models;

public class BreadCrumbModel {
	private String pagePath;
	private String pageTitle;

	/**
	 * @return the pagePath
	 */
	public String getPagePath() {
		return pagePath;
	}
	/**
	 * @param pagePath
	 *            the pagePath to set
	 */
	public void setPagePath(String pagePath) {
		this.pagePath = pagePath;
	}

	/**
	 * @return the pageTitle
	 */
	public String getPageTitle() {
		return pageTitle;
	}
	/**
	 * @param pageTitle
	 *            the pageTitle to set
	 */
	public void setPageTitle(String pageTitle) {
		this.pageTitle = pageTitle;
	}

	
	@Override
	public String toString() {
		return "BreadCrumbModel [pagePath=" + pagePath + ", pageTitle=" + pageTitle + "]";
	}
}
