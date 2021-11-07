package org.kp.patterns.core.panels;

/**
 * Content Toggle Panel component Model class. This POJO class is intended to populate each property of the
 * multi field node.
 */
public class ContentTogglePanel {
  private String accTitle;
  private String sectionAnchor;
  private boolean openByDefault;
  private String areaExpand;
  private String analyticsData;
  private String showClass;

  private boolean parsysNode;

  public String getAccTitle() {
    return accTitle;
  }

  public void setAccTitle(String accTitle) {
    this.accTitle = accTitle;
  }

  public String getSectionAnchor() {
    return sectionAnchor;
  }

  public void setSectionAnchor(String sectionAnchor) {
    this.sectionAnchor = sectionAnchor;
  }

  public boolean getOpenByDefault() {
    return openByDefault;
  }

  public void setOpenByDefault(boolean openByDefault) {
    this.openByDefault = openByDefault;
  }

  public Boolean getParsysNode() {
    return parsysNode;
  }

  public void setParsysNode(Boolean parsysNode) {
    this.parsysNode = parsysNode;
  }

  public String getAreaExpand() {
    return areaExpand;
  }

  public void setAreaExpand(String areaExpand) {
    this.areaExpand = areaExpand;
  }

  public String getAnalyticsData() {
    return analyticsData;
  }

  public void setAnalyticsData(String analyticsData) {
    this.analyticsData = analyticsData;
  }

  public String getShowClass() {
    return showClass;
  }

  public void setShowClass(String showClass) {
    this.showClass = showClass;
  }
}
