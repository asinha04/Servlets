
package org.kp.patterns.core.models;

import java.util.List;

import javax.inject.Inject;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Via;
import org.apache.sling.models.annotations.Default;
import org.kp.foundation.core.models.HeaderStylesModel;
import org.kp.foundation.core.models.MultiFieldLinkModel;
import org.kp.foundation.core.utils.WCMUseUtil;

import com.day.cq.wcm.api.WCMMode;


/**
 * IconLinkModel is Sling Model class, This class provides the data for the icon link component node
 * that is generated through authoring dialog . 
 * Tirumala Malladi
 */
@Model(adaptables = {SlingHttpServletRequest.class},
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)

public class IconLinkPatternModel extends HeaderStylesModel {

  @Inject
  @Via("resource")
  private boolean hideOnLoad;

  @Inject
  @Via("resource")
  private String iconLinkId;

  @Inject
  @Via("resource")
  private String title;

  @Inject
  @Via("resource")
  private String icon;

  @Inject
  @Via("resource")
  @Default(values = "icon")
  private String iconType;
  
  @Inject
  @Via("resource")
  private String svgPath;

  @Inject
  @Via("resource")
  private String subTitle;

  @Inject
  @Via("resource")
  private String description;

  @Inject
  @Via("resource")
  private String alt;
    
  @Inject
  private String linkClasses;
  
  @Inject
  private String iconClasses;
  
  private String headerStyleTitleTextStyle = "icon-link-title";
  
  private String toggleHide;
  private static final String CLASS_NAME = "toggle-hide";
  

  public boolean isHideOnLoad() {
    return hideOnLoad;
  }
  
  
  public String getIconLinkId() {
    return iconLinkId;
  }

  public String getTitle() {
    return title;
  }

  public String getIcon() {
    return icon;
  }

  public String getSubTitle() {
    return subTitle;
  }

  public String getDescription() {
    return description;
  }

  public List<MultiFieldLinkModel> getLinkList() {
    List<MultiFieldLinkModel> linkList;
    linkList = WCMUseUtil.getLists(request, "links", linkClasses);
    return linkList;
  }

  public String getToggleHide() {
    toggleHide = "";
    if(hideOnLoad && WCMMode.fromRequest(request) != WCMMode.EDIT) {
      toggleHide = CLASS_NAME;
    }
    return toggleHide;
  }

  public String getIconClasses() {
    return iconClasses;
  }
  
  public String getHeaderStyleTitleText() {
	if (null != getHeaderStyle() && !getHeaderStyle().isEmpty()) {
		headerStyleTitleTextStyle = getHeaderStyle();
	}
	return headerStyleTitleTextStyle;
  }
  
  public String getAlt() {
		return alt;
	}
  
  public String getIconType() {
	return iconType;
  }


  public String getSvgPath() {
	return svgPath;
  }
}
