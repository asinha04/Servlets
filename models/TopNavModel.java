package org.kp.foundation.core.models;

import javax.inject.Inject;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class TopNavModel {
	@Inject
	private String pagePath;
	
	@Inject
	private String pageName;
	
	private boolean active = false;    //used to identify which link we are currently on, so that UI code can act accordingly
	
	@Inject
	private String color;
	
	@Inject
	private String hideFromTopNav;
	
	public String getPagePath() {
		return pagePath;
	}
	public void setPagePath(String pagePath) {
		this.pagePath = pagePath;
	}
	
	public String getPageName() {
		return pageName;
	}
	public void setPageName(String pageName) {
		this.pageName = pageName;
	}
	
	public boolean isActive() {
        return active;
    }
    public void setActive(boolean active) {
        this.active = active;
    }
    
    public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	@Override
    public String toString() {
        return "TopNavModel [pagePath=" + pagePath + ", pageName=" + pageName + ", active=" + active +", color=" + color+", hideFromTopNav=" + hideFromTopNav +"]";
    }
	public String getHideFromTopNav() {
		return hideFromTopNav;
	}
	public void setHideFromTopNav(String hideFromTopNav) {
		this.hideFromTopNav = hideFromTopNav;
	}
	
}