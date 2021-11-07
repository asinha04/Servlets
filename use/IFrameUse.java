package org.kp.foundation.core.use;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.jcr.RepositoryException;
import org.kp.foundation.core.utils.PropertyInheritedUtil;

/**
 * The IFrameComponent class is Use bean class for IFrameComponent
 * 
 * display the content from Iframe URL
 *
 * @author Prakash Sankuratri
 *
 */

public class IFrameUse extends BaseWCMUse {

  private static final Logger log = LoggerFactory.getLogger(IFrameUse.class);
  public static final String IFRAME_TARGET = "target";
  public static final String IFRAME_PASSPARAMS = "passparams";
  public static final String IFRAME_WIDTH = "width";
  public static final String IFRAME_HEIGHT = "height";
  public static final String IFRAME_MODULE = "module";
  public static final String IFRAME_ID = "iframeID";
  public static final String IFRAME_TITLE = "iframeTitle";
  private String target;
  private String width;
  private String height;
  private String iframeTitle;
  ValueMap properties;

  @Override
  public void activate() throws Exception {

    log.debug("Inside IFrameComponent activator");

    properties = getProperties();
    target = StringUtils.trimToEmpty(PropertyInheritedUtil.getProperty(getResource(), IFRAME_TARGET));
    width = StringUtils.trimToEmpty(PropertyInheritedUtil.getProperty(getResource(), IFRAME_WIDTH));
    height = StringUtils.trimToEmpty(PropertyInheritedUtil.getProperty(getResource(), IFRAME_HEIGHT));
    iframeTitle = StringUtils.trimToEmpty(PropertyInheritedUtil.getProperty(getResource(), IFRAME_TITLE));
  }

  public String getTarget() {
    String params = getRequest().getQueryString();
    if (passParameters() && params != null) {
      if (target.indexOf("?") == -1) {
        target = target + "?" + params;
      } else {
        target = target + "&" + params;
      }

    }
    if (target.length() == 0) {
      target = "about:blank";
    }
    return target;
  }

  public boolean moduleEnabled() {
    return !("".equals(getStringProperty(IFRAME_MODULE, "")));
  }

  public boolean passParameters() {
    return "true".equals(getStringProperty(IFRAME_PASSPARAMS, "false"));
  }

  private String getStringProperty(String name, String defaultValue) {
    return properties.get(name, defaultValue);
  }

  public String getWidth() {
    return width;
  }

  public String getHeight() {
    return height;
  }

  public String getIframeTitle() {
    return iframeTitle;
  }
}