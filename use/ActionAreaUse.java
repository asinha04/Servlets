package org.kp.foundation.core.use;

import com.adobe.cq.sightly.WCMUsePojo;
import javax.jcr.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ActionAreaUse class is responsible to read the authored content for the Action Area component.
 * 
 * @author Jai Parkash
 *
 */

public class ActionAreaUse extends WCMUsePojo {
  private static final Logger LOGGER = LoggerFactory.getLogger(ActionAreaUse.class);
  
  private static final String AA_TITLE = "title";
  private static final String AA_TITLE_TAG = "titletag";
  private static final String AA_TITLE_STYLE = "titlestyle";
  private static final String AA_HIDE_SUBTITLE = "hidesubtitle";
  private static final String AA_HIDE_CTA = "hidecta";

  private String title;
  private String titletag;
  private String titlestyle;
  private String hidesubtitle;
  private String hidecta;

  @Override
  public void activate() throws Exception {
    
    LOGGER.info("Activating");
    
    Node currentNode;
    currentNode = getResource().adaptTo(Node.class);
    title = "";
    if(currentNode!= null) {
	    if (currentNode.hasProperty(AA_TITLE)) {
	    	title = currentNode.getProperty(AA_TITLE).getString();
	    } else {
	    	// else get page title
	    	title = getCurrentPage().getTitle();
	    }
	    titletag="";
	    if (currentNode.hasProperty(AA_TITLE_TAG)) {
	    	titletag = currentNode.getProperty(AA_TITLE_TAG).getString();
	    }
	    titlestyle="";
	    if (currentNode.hasProperty(AA_TITLE_STYLE)) {
	    	titlestyle = currentNode.getProperty(AA_TITLE_STYLE).getString();
	    }
	
	    hidesubtitle="";
	    if (currentNode.hasProperty(AA_HIDE_SUBTITLE)) {
	    	hidesubtitle = currentNode.getProperty(AA_HIDE_SUBTITLE).getString();
	   
	    }
	    hidecta="";
	    if (currentNode.hasProperty(AA_HIDE_CTA)) {
	    	hidecta = currentNode.getProperty(AA_HIDE_CTA).getString();
	    }
    }
  }

  /**
   * This method is responsible for to get the title.
   * 
   * @return String
   */
  public String getTitle() {
    return title;
  }

  /**
   * This method is responsible for to get the title tag.
   * 
   * @return String
   */
  public String getTitleTag() {
	    return titletag;
  }
 
  /**
   * This method is responsible for to get the title style.
   * 
   * @return String
   */
  public String getTitleStyle() {
	  return titlestyle;
  }
  
  /**
   * This method is responsible for to get the  Hide Sub Title Configuration.
   * 
   * @return String
   */
  public boolean getHideSubTitle() {
	  return Boolean.parseBoolean(hidesubtitle) ; 
  }
  
  /**
   * This method is responsible for to get the  Hide CTA Configuration.
   * 
   * @return String
   */
  public boolean getHideCTA() {
	  return Boolean.parseBoolean(hidecta) ; 
  }
}
