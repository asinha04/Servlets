package org.kp.patterns.core.models;

import java.util.List;

import javax.inject.Inject;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Via;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.via.ResourceSuperType;
import org.jetbrains.annotations.NotNull;

import com.adobe.cq.wcm.core.components.models.Carousel;
import com.adobe.cq.wcm.core.components.models.ListItem;

/**
 * Sling Model class for Show More Less Component version DS2. 
 *  @author Abinaya Muthiyalram
 */
@Model(adaptables = { Resource.class, SlingHttpServletRequest.class }, resourceType = {
		ShowMoreLessPatternModelV2.RESOURCE_TYPE }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ShowMoreLessPatternModelV2 extends ShowMoreLessPatternModel implements Carousel {

	public static final String RESOURCE_TYPE = "kporg/kp-foundation/components/patterns/show-more-less-pattern/v2/show-more-less-pattern";
    
	@Self
	@Via(type = ResourceSuperType.class)
	private Carousel delegate;

	@Inject
	@Via("resource")
	private Boolean showCount;

	@Inject
	@Via("resource")
	private Boolean showDivider;

	@Inject
	@Via("resource")
	private Boolean showIcon;

	@Inject
	@Via("resource")
	private Boolean revealAnimation;

	@Inject
	@Via("resource")
	private Boolean displayInline;

	@Inject
	@Via("resource")
	@Default(values = "")
	private String disableBreakpoint;

	@Override
	public @NotNull List<ListItem> getItems() {
		return delegate.getItems();
	}

	public Boolean getShowCount() {
		return showCount;
	}

	public Boolean getShowDivider() {
		return showDivider;
	}

	public Boolean getShowIcon() {
		return showIcon;
	}

	public Boolean getRevealAnimation() {
		return revealAnimation;
	}

	public Boolean getDisplayInline() {
		return displayInline;
	}

	public String getDisableBreakpoint() {
		return disableBreakpoint;
	}

}