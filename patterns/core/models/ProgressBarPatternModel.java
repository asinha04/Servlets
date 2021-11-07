package org.kp.patterns.core.models;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Via;
import org.kp.foundation.core.constants.GlobalConstants;
import org.kp.foundation.core.models.BaseModel;
import org.kp.foundation.core.models.MultiFieldLinkModel;
import org.kp.foundation.core.utils.WCMUseUtil;

import com.day.cq.wcm.api.WCMMode;

/**
 * Model class for Progress Bar
 * 
 * @author Ravish
 *
 */
@Model(adaptables = {Resource.class, SlingHttpServletRequest.class},  defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ProgressBarPatternModel  extends BaseModel{

  @Inject
  @Via("resource")
  private String id;
  
  @Inject
  @Via("resource")
  private String variationStyle;

  @Inject
  @Via("resource")
  private String barColor;
  
  @Inject
  @Via("resource")
  private String descriptiveText;
   
  @Inject
  @Default(values = "")
  @Via("resource")
  @Named("headerText")
  private String titleText;
  
  private String headerId;
  
  private String WIDTH_VALUE="width:50%";
  


  private List<MultiFieldLinkModel> panelLinksList = new ArrayList<>(); 
  private int minimum = 0;
  private int maximum = 100;
  private int current = 0;

  public String getId() {
    return id;
  }

  public String getBarColor() {
    return barColor;
  }
  
  public String getVariationStyle() {
    return variationStyle;
  }

  public List<MultiFieldLinkModel> getPanelLinksList() {
    panelLinksList = new ArrayList<MultiFieldLinkModel>();
    panelLinksList = WCMUseUtil.getLists(request,"links",GlobalConstants.EMPTY_STRING);
    int count = 1;
    String pbId = id +"_link";
    for (MultiFieldLinkModel linkModel : panelLinksList) {
      if(linkModel.getLinkLabel() != null) {
        linkModel.setId(pbId+ count); 
        if(null != titleText && !titleText.equals("")) {
          linkModel.setAriaLabelledBy(pbId + count + " "  + getHeaderId());
        }
        count++;
      }
    }
    return new ArrayList<>(panelLinksList);
  }
  
  public int getMinimum() {
    return minimum;
  }
  
  public int getMaximum() {
    return maximum;
  }
  
  public int getCurrent() {
    return current;
  }

  public String getDescriptiveText() {
    return descriptiveText;
  }

  public String getHeaderId() {
    headerId = id+"_label" ;
    return headerId;
  }
  
  public String getStyle() {
	  String style = "";
		if (WCMMode.fromRequest(request) == WCMMode.EDIT) {
			style = WIDTH_VALUE;
		}
		return style;
	  
  }
}