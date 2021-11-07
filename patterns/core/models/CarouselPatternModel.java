package org.kp.patterns.core.models;

import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.ExporterOption;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Via;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.via.ResourceSuperType;
import org.kp.foundation.core.models.BaseModel;
import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.adobe.cq.wcm.core.components.models.Carousel;
import com.adobe.cq.wcm.core.components.models.ListItem;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Sling Model class for customizing core carousel Component. This class uses
 * the delegate pattern to access the AEM's OOTB methods from core carousel. We
 * have added additional methods for custom properties. This class also inherits
 * the BaseModel to extend id, classes and link properties.
 * 
 * * @author Mohan Joshi
 */
@Model(adaptables = SlingHttpServletRequest.class, adapters = { Carousel.class,
		ComponentExporter.class }, resourceType = {
				CarouselPatternModel.RESOURCE_TYPE }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME, extensions = ExporterConstants.SLING_MODEL_EXTENSION, options = {
		@ExporterOption(name = "MapperFeature.SORT_PROPERTIES_ALPHABETICALLY", value = "true") })
public class CarouselPatternModel extends BaseModel implements Carousel {

	public static final String RESOURCE_TYPE = "kporg/kp-foundation/components/patterns/carousel/carousel";
	private static final String PREVIOUS_ICON_PATH = "/etc.clientlibs/settings/wcm/designs/kporg/kp-foundation/clientlib-modules/styleguide/resources/assets/images/prev.svg";
	private static final String NEXT_ICON_PATH = "/etc.clientlibs/settings/wcm/designs/kporg/kp-foundation/clientlib-modules/styleguide/resources/assets/images/next.svg";
	private static final String CAROUSEL_ARIA_LABEL = "Carousel";

	@Self
	@Via(type = ResourceSuperType.class)
	private Carousel delegate;

	@Inject
	@Named("sling:resourceType")
	String slingResourceType;

	@Inject
	@Via("resource")
	private String previousClass;

	@Inject
	@Via("resource")
	private String nextClass;

	@Inject
	@Via("resource")
	private String ofLabel;

	@Inject
	@Via("resource")
	private String showing;

	@Override
	public boolean getAutoplay() {
		return delegate.getAutoplay();
	}

	@Override
	public Long getDelay() {
		return delegate.getDelay();
	}

	@Override
	public boolean getAutopauseDisabled() {
		return delegate.getAutopauseDisabled();
	}

	@Override
	public String getAccessibilityLabel() {
		return StringUtils.isNotBlank(delegate.getAccessibilityLabel()) ? delegate.getAccessibilityLabel()
				: CAROUSEL_ARIA_LABEL;
	}

	@Override
	@JsonIgnore
	public List<ListItem> getItems() {
		return delegate.getItems();
	}

	@Override
	@JsonIgnore
	public String getId() {
		return delegate.getId();
	}

	/**
	 * returns slingResourceType to be used while exporting content as json
	 */
	public String getSlingResourceType() {
		return slingResourceType;
	}

	/**
	 * @return the previousClass
	 */
	public String getPreviousClass() {
		return previousClass;
	}

	/**
	 * @return the nextClass
	 */
	public String getNextClass() {
		return nextClass;
	}

	/**
	 * @return the ofLabel
	 */
	public String getOfLabel() {
		return ofLabel;
	}

	/**
	 * @return the showing
	 */
	public String getShowing() {
		return showing;
	}

	/**
	 * @return the previousIconPath
	 */
	public String getPreviousIconPath() {
		return PREVIOUS_ICON_PATH;
	}

	/**
	 * @return the nextIconPath
	 */
	public String getNextIconPath() {
		return NEXT_ICON_PATH;
	}
}