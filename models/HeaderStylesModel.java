package org.kp.foundation.core.models;

import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Via;
import org.kp.foundation.core.constants.GlobalConstants;

/**
 * Model class for HeaderStylesModel.
 * 
 * @author Ravish Sehgal
 *
 */
@Model(adaptables = {Resource.class, SlingHttpServletRequest.class }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class HeaderStylesModel extends BaseModel{
  
  @Inject
  @Default(values = "")
  @Via("resource")
  private String headerText;

  @Inject
  @Default(values = "")
  @Via("resource")
  private String headerStyle;

  @Inject
  @Default(values = "")
  @Via("resource")
  private String headerWeight;

  @Inject
  @Default(values = "")
  @Via("resource")
  private String headerColor;

  @Inject
  @Default(values = "")
  @Via("resource")
  private String headerTag;

  @Inject
  @Default(values = "")
  private String addComponentClasses;
  
  private String analyticsTitle;
  private static final String COLON = ":";
  
  public String getHeaderText() {
    return headerText;
  }

  public String getHeaderStyle() {
    return headerStyle;
  }

  public String getHeaderWeight() {
    return headerWeight;
  }

  public String getHeaderColor() {
    return headerColor;
  }

  public String getHeaderTag() {
    return headerTag;
  }


  public String getAddComponentClasses() {
    return addComponentClasses;
  }
  
  /**
   * 
   * @return - componentClasses
   */
  public String getComponentClasses() {
      return getHeaderStyle() + GlobalConstants.WHITE_SPACE + getHeaderWeight() + GlobalConstants.WHITE_SPACE + getHeaderColor()+ GlobalConstants.WHITE_SPACE + getAddComponentClasses();
  }

  /**
  * @return the analyticsTitle
  */
  public String getAnalyticsTitle() {
    analyticsTitle = StringUtils.isNotBlank(headerText) ? COLON + headerText : "";
    return analyticsTitle;
  }
}
