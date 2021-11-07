package org.kp.foundation.core.models;

import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Via;
import org.kp.foundation.core.utils.LinkUtil;
import org.kp.foundation.core.utils.SlingModelUtil;

@Model(adaptables = {SlingHttpServletRequest.class}, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ButtonModel {

	  private static final String BUTTON_TEXT = "btnText";
	  private static final String BUTTON_PATH = "btnPath";
	  private static final String BUTTON_TARGET = "btnTarget";
	  private static final String BUTTON_TYPE = "btnType";
	  private static final String ATTR_STRING = "attrString";
	  
	  private static final String EMPTY_BUTTON_TEXT = "SAMPLE BUTTON";
	  private static final String EMPTY_TARGET_TEXT = "_self";
	  
	  @Inject
	  SlingHttpServletRequest request;
	  
	  @Inject
	  @Named(BUTTON_TEXT)
	  @Via("resource")
	  private String text;
	  
	  @Inject
	  @Named(BUTTON_PATH)
	  @Via("resource")
	  private String path;
	  
	  @Inject
	  @Named(BUTTON_TARGET)
	  @Via("resource")
	  private String target;
	  
	  @Inject
	  @Named(BUTTON_TYPE)
	  @Via("resource")
	  private String type;
	  
	  @Inject
	  @Named(ATTR_STRING)
	  @Via("resource")
	  private String attrString;
	  
	  private Map<String,String> attrMap;
	  
	  @PostConstruct
	  protected void init() {
		  if(StringUtils.isBlank(text)) {
			  text = EMPTY_BUTTON_TEXT;
		  }
		  
		  if(StringUtils.isBlank(target)) {
			  target = EMPTY_TARGET_TEXT;
		  }
		  
		  attrMap = SlingModelUtil.parseAttributeString(attrString);
	  }
	  
	 public String getPath() {
		     String url = LinkUtil.getRelativeURL(request, path);
		    if (StringUtils.isBlank(url)) {
		      return path;
		    }
		    return url;
	}
	  
	public Map<String,String> getAttrMap(){
		    return attrMap;
	}

	public String getText() {
		return text;
	}

	public String getTarget() {
		return target;
	}

	public String getType() {
		return type;
	}

	public String getAttrString() {
		return attrString;
	}
	  

}
