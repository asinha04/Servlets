package org.kp.patterns.core.models;

import javax.inject.Inject;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Via;
import org.kp.foundation.core.models.BaseModel;

/**
 * Sling model for Area of Care Proxy Picker pattern
 * 
 * @author anirudh.satchitanand
 */
@Model(adaptables = { Resource.class,
		SlingHttpServletRequest.class }, resourceType = ProxyPickerPatternModel.RESOURCE_TYPE, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ProxyPickerPatternModel extends BaseModel {

	public static final String RESOURCE_TYPE = "kporg/kp-foundation/components/patterns/proxy-picker-pattern";

	@Inject
	@Via("resource")
	private String areaOfCareText;

	@Inject
	@Via("resource")
	private String selectAreaOfCareText;

	public String getAreaOfCareText() {
		return areaOfCareText;
	}

	public String getSelectAreaOfCareText() {
		return selectAreaOfCareText;
	}

}
