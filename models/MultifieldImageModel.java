package org.kp.foundation.core.models;

import java.io.Serializable;

import javax.inject.Inject;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.kp.foundation.core.constants.GlobalConstants;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class MultifieldImageModel implements Serializable {
  private static final long serialVersionUID = 1L;
  
  @Inject
  private String imagePath;
  
  @Inject
  private String alt;
  
  @Inject
  private String title;
  
  @Inject
  private String bodyText;
  
  @Inject
  private String linkType;
  
  @Inject
  private String linkPath;
  
  @Inject
  private String linkLabel;
  
  @Inject
  private String linkTarget;
  
  @Inject
  private String noLinkFollow;
  
  @Inject
  private String hrLine;
  
  @Inject
  private String headerText;
  
  @Inject
  private String headerTag;
  
  @Inject
  private String headerStyle;
  
  @Inject
  private String headerWeight;
  
  @Inject
  private String headerColor;

  @Override
  public String toString() {
    return "MultiFieldLinkModel -->[Image path: " + getImagePath() + "; alt: " + getAlt()
        + "; Title: " + getTitle() + "; Body Text: " + getBodyText() + "; Link Type: "
        + getLinkType() + "; Link Path: " + getLinkPath() + "; Link Label: " + getLinkLabel()
        + "; Link Target: " + getLinkTarget() + "; No Follow: " + getNoLinkFollow() + ";Hr Line: "
        + getHrLine() + ";Header Text : " + this.getHeaderText() + ";Header Tag : " + this.getHeaderTag() + "; Header Style: "
        + getHeaderStyle() + "; Header Weight: " + getHeaderWeight() + ";Header Color: "
        + getHeaderColor() + "]";
  }

  /**
   * Getter for imagePath.
   * 
   * @return the imagePath
   */
  public String getImagePath() {
    return imagePath;
  }

  /**
   * Setter for imagePath.
   * 
   * @param imagePath the imagePath to set
   */
  public void setImagePath(String imagePath) {
    this.imagePath = imagePath;
  }

  /**
   * Getter for alt.
   * 
   * @return the alt
   */
  public String getAlt() {
    return alt;
  }

  /**
   * Setter for alt.
   * 
   * @param alt the alt to set
   */
  public void setAlt(String alt) {
    this.alt = alt;
  }

  /**
   * Getter for title.
   * 
   * @return the title
   */
  public String getTitle() {
    return title;
  }

  /**
   * Setter for title.
   * 
   * @param title the title to set
   */
  public void setTitle(String title) {
    this.title = title;
  }

  /**
   * Getter for bodyText.
   * 
   * @return the bodyText
   */
  public String getBodyText() {
    return bodyText;
  }

  /**
   * Setter for bodyText.
   * 
   * @param bodyText the bodyText to set
   */
  public void setBodyText(String bodyText) {
    this.bodyText = bodyText;
  }

  /**
   * Getter for linkType.
   * 
   * @return the linkType
   */
  public String getLinkType() {
    return linkType;
  }

  /**
   * Setter for linkType.
   * 
   * @param linkType the linkType to set
   */
  public void setLinkType(String linkType) {
    this.linkType = linkType;
  }

  /**
   * Getter for linkPath.
   * 
   * @return the linkPath
   */
  public String getLinkPath() {
    return linkPath;
  }

  /**
   * Setter for linkPath.
   * 
   * @param linkPath the linkPath to set
   */
  public void setLinkPath(String linkPath) {
    this.linkPath = linkPath;
  }

  /**
   * Getter for linkLabel.
   * 
   * @return the linkLabel
   */
  public String getLinkLabel() {
    return linkLabel;
  }

  /**
   * Setter for linkLabel.
   * 
   * @param linkLabel the linkLabel to set
   */
  public void setLinkLabel(String linkLabel) {
    this.linkLabel = linkLabel;
  }

  /**
   * Getter for linkTarget.
   * 
   * @return the linkTarget
   */
  public String getLinkTarget() {
    return linkTarget;
  }

  /**
   * Setter for linkTarget.
   * 
   * @param linkTarget the linkTarget to set
   */
  public void setLinkTarget(String linkTarget) {
    this.linkTarget = linkTarget;
  }

  /**
   * Getter for noFollow.
   * 
   * @return the noFollow
   */
  public String getNoLinkFollow() {
    return noLinkFollow;
  }

  /**
   * Setter for noFollow.
   * 
   * @param noFollow the noFollow to set
   */
  public void setNoLinkFollow(String noLinkFollow) {
    this.noLinkFollow = noLinkFollow;
  }
  
 /**
   * Getter for hrLine.
   * 
   * @return the hrLine
   */
  public String getHrLine() {
    return hrLine;
  }

  /**
   * Setter for hrLine.
   * 
   * @param hrLine the hrLine to set
   */
  public void setHrLine(String hrLine) {
    this.hrLine = hrLine;
  }

  /**
   * Getter for headerText.
   * 
   * @return the headerText
   */
  public String getHeaderText() {
    return headerText;
  }

  /**
   * Setter for headerText.
   * 
   * @param headerText the headerText to set
   */
  public void setHeaderText(String headerText) {
    this.headerText = headerText;
  }

  /**
   * Getter for headerTag.
   * 
   * @return the headerTag
   */
  public String getHeaderTag() {
    return headerTag;
  }

  /**
   * Setter for headerTag.
   * 
   * @param headerTag the headerTag to set
   */
  public void setHeaderTag(String headerTag) {
    this.headerTag = headerTag;
  }

  /**
   * Getter for headerStyle.
   * 
   * @return the headerStyle
   */
  public String getHeaderStyle() {
    return headerStyle;
  }

  /**
   * Setter for headerStyle.
   * 
   * @param headerStyle the headerStyle to set
   */
  public void setHeaderStyle(String headerStyle) {
    this.headerStyle = headerStyle;
  }

  /**
   * Getter for headerWeight.
   * 
   * @return the headerWeight
   */
  public String getHeaderWeight() {
    return headerWeight;
  }

  /**
   * Setter for headerWeight.
   * 
   * @param headerWeight the headerWeight to set
   */
  public void setHeaderWeight(String headerWeight) {
    this.headerWeight = headerWeight;
  }

  /**
   * Getter for headerColor.
   * 
   * @return the headerColor
   */
  public String getHeaderColor() {
    return headerColor;
  }

  /**
   * Setter for headerColor.
   * 
   * @param headerColor the headerColor to set
   */
  public void setHeaderColor(String headerColor) {
    this.headerColor = headerColor;
  }
  
  /**
   * 
   * @return - componentClasses
   */
  public String getComponentClasses() {
      return "-title "+getHrLine()+ GlobalConstants.WHITE_SPACE +getHeaderStyle()+ GlobalConstants.WHITE_SPACE +getHeaderColor()+ GlobalConstants.WHITE_SPACE +getHeaderWeight();
  }
}
