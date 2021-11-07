package org.kp.patterns.core.models;

import java.util.List;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Via;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.via.ResourceSuperType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.adobe.cq.wcm.core.components.models.Accordion;
import com.adobe.cq.wcm.core.components.models.ListItem;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Model(adaptables = SlingHttpServletRequest.class, adapters = { Accordion.class,
		ComponentExporter.class }, resourceType = {
				ContentToggleModel.RESOURCE_TYPE }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME, extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class ContentToggleModel extends ContentTogglePatternModel implements Accordion {

	public static final String RESOURCE_TYPE = "kporg/kp-foundation/components/patterns/content-toggle/v2/content-toggle";

	@Self
	@Via(type = ResourceSuperType.class)
	Accordion delegate;

	public @Nullable String getId() {
		return delegate.getId();
	}

	public @NotNull String getExportedType() {
		return delegate.getExportedType();
	}
	
	@Override
	@JsonIgnore
	public @NotNull List<ListItem> getItems() {
		return delegate.getItems();
	}

}
