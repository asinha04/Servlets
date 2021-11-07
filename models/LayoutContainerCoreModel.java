package org.kp.foundation.core.models;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.ExporterOption;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Via;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.via.ResourceSuperType;
import org.kp.foundation.core.utils.SlingModelUtil;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.adobe.cq.wcm.core.components.models.LayoutContainer;
import com.adobe.cq.wcm.core.components.models.ListItem;

/**
 * Sling Model class for Customizing Core Layout container Component. Added sling exporter
 * annotations to expose content as json.
 * 
 * * @author Ravish Sehgal
 */
@Model(adaptables = SlingHttpServletRequest.class, adapters = { LayoutContainer.class,
		ComponentExporter.class }, resourceType = {
				"kporg/kp-foundation/components/core/container" }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME, extensions = ExporterConstants.SLING_MODEL_EXTENSION, options = {
		@ExporterOption(name = "MapperFeature.SORT_PROPERTIES_ALPHABETICALLY", value = "true") })
public class LayoutContainerCoreModel extends BaseModel implements LayoutContainer {

	@Inject
	SlingHttpServletRequest request;

	private static final String PROPERTY_NAME = "attributesList";
	private static final String ATTRIBUTE_NAME = "role";
	private static final String ATTRIBUTE_CONTENTINFO = "contentinfo";
	private Map<String, String> attrMap = new HashMap<>();
	
	@Inject
	@Via("resource")
	@Named("type")
	private String htmlTag;
	
	@Inject
	@Default(booleanValues = false)
	@Via("resource")
	@Named("cardClickable")
	private boolean containerClickable;

	@Self
	@Via(type = ResourceSuperType.class)
	private LayoutContainer delegate;

	@PostConstruct
	protected void init() {
		Resource linkRootRes = request.getResource().getChild(PROPERTY_NAME);
		if (linkRootRes != null) {
			Iterable<Resource> linkResItr = linkRootRes.getChildren();
			for (Resource res : linkResItr) {
				MultiFieldLinkModel containerModel = res.adaptTo(MultiFieldLinkModel.class);
				createContainerModel(containerModel);
			}
		}
	}

	public void createContainerModel(MultiFieldLinkModel containerModel) {
		if (containerModel != null) {
			String attributeValues="";
			if (null != containerModel.getAttributeName() && null != containerModel.getAttributeValue()
					&& !(containerModel.getAttributeName().equalsIgnoreCase(ATTRIBUTE_NAME)
							&& containerModel.getAttributeValue().equalsIgnoreCase(ATTRIBUTE_CONTENTINFO))) {

				attributeValues = containerModel.getAttributeName() + "=" + containerModel.getAttributeValue();
				Map<String, String> map = new HashMap<>();
				map = SlingModelUtil.parseAttributeString(attributeValues);
				attrMap.putAll(map);
			}
		}
	}
	
//	method inherited from the parent class
	public String getId() {
		return delegate.getId();
	}
//	method inherited from the parent class
	public String getBackgroundStyle() {
		return delegate.getBackgroundStyle();
	}
//	method inherited from the parent class
    public List<ListItem> getItems() {
    		return delegate.getItems();
    }
//	method inherited from the parent class
	public String[] getExportedItemsOrder() {
		return delegate.getExportedItemsOrder();
	}
	public Map<String, String> getAttrMap() {
		return attrMap;
	}
	public String getHtmlTag() {
		return htmlTag;
	}
	
	public boolean isContainerClickable() {
		return containerClickable;
	}
}