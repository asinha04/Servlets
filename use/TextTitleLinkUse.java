package org.kp.foundation.core.use;


import java.util.ArrayList;
import java.util.List;

import org.apache.sling.api.SlingHttpServletRequest;
import org.kp.foundation.core.constants.GlobalConstants;
import org.kp.foundation.core.models.MultiFieldLinkModel;
import org.kp.foundation.core.utils.WCMUseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.sightly.WCMUsePojo;




/**
 * TextTitleLinkUse class is responsible to read the authored content for the TextTitleLinkUse.
 * 
 * @author Tirumala Malladi
 *
 */

public class TextTitleLinkUse extends WCMUsePojo {
  private static final Logger LOGGER = LoggerFactory.getLogger(TitleTextLinkUse.class);
  private static final String HEADER_ALGINMENT = "headerAlignment";
  private static final String LINKS = "links";
  private static final String LINK_DISPLAY = "linkDisplay";
  private static final String LINK_ALIGNMENT = "linkAlignment";
  private static final String LINK_ALIGNMENT_LEFT = "flex-start";
  private static final String LINK_ALIGNMENT_RIGHT = "flex-end";
  private static final String LEFT = "left";
  private static final String CENTER = "center";

  private String alignment;
  private String linkDisplay;
  private String linkAlignment;
  private String contentAlignment;

  @Override
  public void activate() throws Exception {
    LOGGER.info("Activating Title Text Link Use Class");
    alignment = getProperties().get(HEADER_ALGINMENT, "");
    linkDisplay = getProperties().get(LINK_DISPLAY,"");
    linkAlignment = getProperties().get(LINK_ALIGNMENT,"");
  }
  
  
  /**
   * This method retuns the list of LinkModel objects.
   *
   * @return ArrayList.
   */
  public List<MultiFieldLinkModel> getLists() {
      List<MultiFieldLinkModel> linkModelList = new ArrayList<MultiFieldLinkModel>();
      SlingHttpServletRequest req = getRequest();
      linkModelList = WCMUseUtil.getLists(req,LINKS,GlobalConstants.EMPTY_STRING);
      return linkModelList;
  }

  public String getAlignment() {
    return alignment;
  }
  
  public String getContentAlignment() {
    if(alignment.equalsIgnoreCase(LEFT)) {
      contentAlignment = LINK_ALIGNMENT_LEFT;
    }else if(alignment.equalsIgnoreCase(CENTER)) {
      contentAlignment = CENTER;
    }else {
      contentAlignment = LINK_ALIGNMENT_RIGHT;
    }
    return contentAlignment;
  }

  public String getLinkDisplay() {
    return linkDisplay;
  }

  public String getLinkAlignment() {
    return linkAlignment;
  }
}