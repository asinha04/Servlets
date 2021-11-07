package org.kp.patterns.core.models;

import java.util.List;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Via;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.via.ResourceSuperType;
import org.kp.foundation.core.models.BaseModel;
import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.adobe.cq.wcm.core.components.models.ListItem;
import com.adobe.cq.wcm.core.components.models.Tabs;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Sling Model class for customizing core Tabs Component. This class uses the
 * delegate pattern to access the AEM's OOTB methods from core Tabs component.
 * * @author Sathyaprakash Ashokan
 */

@Model(adaptables = SlingHttpServletRequest.class, adapters = { Tabs.class, ComponentExporter.class }, resourceType = {
		TabsCorePatternModel.RESOURCE_TYPE }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME, extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class TabsCorePatternModel extends BaseModel implements Tabs {

	public static final String RESOURCE_TYPE = "kporg/kp-foundation/components/patterns/tabs/tabs";

	@Self
	@Via(type = ResourceSuperType.class)
	private Tabs delegate;

	@Override
	public String getActiveItem() {
		return delegate.getActiveItem();
	}

	@Override
	public String getAccessibilityLabel() {
		return delegate.getAccessibilityLabel();
	}

	@Override
	@JsonIgnore
	public String getId() {
		return delegate.getId();
	}

	@Override
	@JsonIgnore
	public List<ListItem> getItems() {
		return delegate.getItems();
	}

}