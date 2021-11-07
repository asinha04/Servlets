package org.kp.patterns.core.models;

import javax.inject.Inject;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Via;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.via.ResourceSuperType;
import org.kp.foundation.core.models.BaseModel;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.wcm.core.components.models.Button;

@Model(adaptables = { SlingHttpServletRequest.class }, adapters = { Button.class,
		ComponentExporter.class }, resourceType = ButtonPatternModel.RESOURCE_TYPE, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ButtonPatternModel extends BaseModel implements Button {

	public static final String RESOURCE_TYPE = "kporg/kp-foundation/components/patterns/button/button";

	@Self
	@Via(type = ResourceSuperType.class)
	private Button delegate;

	@Inject
	@Via("resource")
	private String iconPath;

	@Inject
	@Via("resource")
	private String altText;

	public String getIconPath() {
		return iconPath;
	}

	public String getAltText() {
		return altText;
	}

}
