package org.kp.foundation.core.models;

import javax.inject.Inject;
import javax.inject.Named;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.ExporterOption;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Via;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.via.ResourceSuperType;
import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.adobe.cq.wcm.core.components.models.*;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;

/**
 * Sling Model class for Customizing Core Title Component.
 * Added sling exporter annotations to expose content as json.
 * 
 * * @author Mohan Joshi
 */
@Model(adaptables = SlingHttpServletRequest.class, 
	   adapters = { Title.class, ComponentExporter.class }, 
	   resourceType = {"kporg/kp-foundation/components/core/title" },
	   defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME, 
		  extensions = ExporterConstants.SLING_MODEL_EXTENSION,
		  options = { @ExporterOption(name = "MapperFeature.SORT_PROPERTIES_ALPHABETICALLY", value = "true") })
public class TitleCoreModel implements Title {

	private static final String NO_FOLLOW_TAG = "nofollow";
	private static final String NO_FOLLOW_EMPTY = "";

	@Self
	@Via(type = ResourceSuperType.class)
	private Title delegate;

	@Inject
	@Named("sling:resourceType")
	String slingResourceType;

	@ScriptVariable
	private Resource resource;

	@Inject
	@Via("resource")
	private boolean noFollow;

	/**
	 * returns the header size
	 */
	public String getType() {
		return delegate.getType();
	}

	/**
	 * returns the title text
	 */
	public String getText() {
		return delegate.getText();
	}

	/**
	 * returns the link url
	 */
	public String getLinkURL() {
		return delegate.getLinkURL();
	}

	/**
	 * check if link is disabled
	 */
	public boolean isLinkDisabled() {
		return delegate.isLinkDisabled();
	}

	/**
	 * check if search engine should follow this link or not
	 */
	public String getNoFollow() {
		return noFollow ? NO_FOLLOW_TAG : NO_FOLLOW_EMPTY;
	}

	/**
	 * returns slingResourceType to be used while exporting content as json
	 */
	public String getSlingResourceType() {
		return slingResourceType;
	}
}