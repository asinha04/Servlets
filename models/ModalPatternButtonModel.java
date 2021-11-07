package org.kp.foundation.core.models;

import javax.inject.Inject;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;

@Model(adaptables = { Resource.class, SlingHttpServletRequest.class }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)

public class ModalPatternButtonModel extends BaseModel {
	
	@Inject 
    private String btnText;
	@Inject 
	private String btnPath;
	@Inject
    private String btnTarget;
	@Inject
    private String btnType;
	@Inject
	private Boolean disabled;
	
	
	public String getBtnText() {
		return btnText;
	}
	public void setBtnText(String btnText) {
		this.btnText = btnText;
	}
	public String getBtnPath() {
		return btnPath;
	}
	public void setBtnPath(String btnPath) {
		this.btnPath = btnPath;
	}
	public String getBtnTarget() {
		return btnTarget;
	}
	public void setBtnTarget(String btnTarget) {
		this.btnTarget = btnTarget;
	}
	public String getBtnType() {
		return btnType;
	}
	public void setBtnType(String btnType) {
		this.btnType = btnType;
	}
	public Boolean getDisabled() {
      return disabled;
    }
    public void setDisabled(Boolean disabled) {
      this.disabled = disabled;
    }
}
