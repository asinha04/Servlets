package org.kp.foundation.core.use;
import java.util.Map;

import javax.jcr.Node;

import org.kp.foundation.core.utils.LinkUtil;
import org.kp.foundation.core.utils.SlingModelUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.sightly.WCMUsePojo;


/**
 * ButtonUse class is responsible to read the authored content for the button component.
 * 
 * @author V.Malladi
 *
 */

public class ButtonUse extends WCMUsePojo {
  private static final Logger LOGGER = LoggerFactory.getLogger(ButtonUse.class);
  private static final String BUTTON_TEXT = "btnText";
  private static final String BUTTON_PATH = "btnPath";
  private static final String BUTTON_TARGET = "btnTarget";
  private static final String BUTTON_TYPE = "btnType";
  private static final String ATTR_STRING = "attrString";

  private String text;
  private String path;
  private String target;
  private String type;
  private String attrString;
  private Map<String,String> attrMap;

  @Override
  public void activate() throws Exception {
    Node currentNode;
    LOGGER.info("Activating Button Use");
    currentNode = getResource().adaptTo(Node.class);

    if(currentNode!= null) {
	    if (currentNode.hasProperty(BUTTON_TEXT)) {
	      text = currentNode.getProperty(BUTTON_TEXT).getString();
	    } else {
	      text = "SAMPLE BUTTON";
	    }
	    if (currentNode.hasProperty(BUTTON_PATH)) {
	      path = currentNode.getProperty(BUTTON_PATH).getString();
	    }
	    if (currentNode.hasProperty(BUTTON_TARGET)) {
	      target = currentNode.getProperty(BUTTON_TARGET).getString();
	    } else {
	      target = "_self";
	    }
	    if (currentNode.hasProperty(BUTTON_TYPE)) {
	      type = currentNode.getProperty(BUTTON_TYPE).getString();
	    } else {
	      type = "";
	    }
      attrString = get(ATTR_STRING, String.class);
      attrMap = SlingModelUtil.parseAttributeString(attrString);
    }
  }
    
  /**
   * @return the passed in attribute string
   */
  public Map<String,String> getAttrMap(){
    return attrMap;
  }

  /**
   * @return the passed in attribute string
   */
  public String getAttrString(){
    return attrString;
  }


  public String getText() {
    return text;
  }

  /**
   * This method is responsible for to get the authored path.
   * 
   * @return String
   */

  public String getPath() {
    String url;
    url = LinkUtil.getRelativeURL(getRequest(), path);
    // If the URL is with in the aem then it will be resolved using LinkUtil.getRelativeURL method
    // and If the url is outside of the AEM then we are returning the url which is configured by
    // authors either it's portal url or external url.
    if (null == url || "".equalsIgnoreCase(url) || url.length() == 0) {
      return path;
    }
    return url;
  }

  public String getTarget() {
    return target;
  }

  public String getType() {
    return type;
  }

}