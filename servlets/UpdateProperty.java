package org.kp.foundation.core.servlets;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.kp.foundation.core.exception.GenericRuntimeException;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.crx.JcrConstants;

/**
 * This servlet helps in updating a property for existing pages / and children
 * 
 * 
 * @author Abdul
 *
 */

@Component(service = Servlet.class, immediate = true, configurationPid = "org.kp.foundation.core.servlets.UpdateProperty",
	name = "KP Foundation Update Property Servlet", property = {
		"process.label= KP Foundation Update Property Servlet", 
		"sling.servlet.resourceTypes" + "=cq:Page", "sling.servlet.methods" + "=" + HttpConstants.METHOD_GET,
		"sling.servlet.methods" + "=" + HttpConstants.METHOD_POST, "sling.servlet.selectors" + "=updateproperty",
		"sling.servlet.extensions=html" })
@Designate(ocd = UpdateProperty.Config.class)
public class UpdateProperty extends SlingAllMethodsServlet implements Serializable{

	private Boolean updateChildren;
	private String updateProperty;
	private String updateValue;
	private String pagePath;

	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LoggerFactory.getLogger(UpdateProperty.class);
	
	@ObjectClassDefinition(name = "Configuration Update Property Servlet", description = "Configuration Update Property Servlet")
	public static @interface Config {
		@AttributeDefinition(name = "updateProperty", type = AttributeType.STRING)
		String updateProperty() default "";

		@AttributeDefinition(name = "updateValue", type = AttributeType.STRING)
		String updateValue() default "";

		@AttributeDefinition(name = "updateChildren", type = AttributeType.BOOLEAN)
		boolean updateChildren() default true;
	}

	@Reference
	private transient ResourceResolverFactory resolverFactory;

	@Activate
	protected void activate(final Config config) {
		LOGGER.info("Reading the OSGI configuration values");
		this.updateChildren = config.updateChildren();
		LOGGER.debug("Should children be updated: " + this.updateChildren);
		this.updateProperty = config.updateProperty();
		LOGGER.debug("updateProperty: " + this.updateProperty);
		this.updateValue = config.updateValue();
		LOGGER.debug("updateValue: " + this.updateValue);
	}

	@Override
	protected void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response)
			throws ServletException, IOException {
		processRequest(request, response);
	}

	@Override
	protected void doPost(final SlingHttpServletRequest request, final SlingHttpServletResponse response)
			throws ServletException, IOException {
		processRequest(request, response);
	}

	protected void processRequest(SlingHttpServletRequest request, SlingHttpServletResponse response)
			throws ServletException, IOException {

		LOGGER.debug("Update property - processRequest begin");
		ResourceResolver resourceResolver = null;

		try {
			

			Map<String, Object> paramMap = new HashMap<String, Object>();
			// Add the subServiceName used in the Apache Sling Service User Mapping Config
			paramMap.put(ResourceResolverFactory.SUBSERVICE, "updateProp");
			LOGGER.info("After setting the subservice param in map");
			resourceResolver = resolverFactory.getServiceResourceResolver(paramMap);

			pagePath = request.getResource().getPath();
			LOGGER.debug("Path-->" + pagePath);

			LOGGER.debug("Initializing Page Manager");
			PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
			Page page = pageManager.getPage(pagePath);

			LOGGER.info("Parent Page-->" + page.getName());
			updatePageProperty(page, resourceResolver);

			if (updateChildren) {
				Iterator<Page> pageIter = page.listChildren(null, true);

				while (pageIter.hasNext()) {
					Page childPage = pageIter.next();
					LOGGER.debug("Child page is-->" + childPage.getPath());
					updatePageProperty(childPage, resourceResolver);
				}
			}

			resourceResolver.commit();

		} catch (Exception e) {
			LOGGER.error("exception" + e);
		}finally {
			if (resourceResolver != null && resourceResolver.isLive()) {
				resourceResolver.close();
			}
		}

	}

	private void updatePageProperty(Page page, ResourceResolver resourceResolver) throws GenericRuntimeException {

		Resource resource = page.adaptTo(Resource.class).getChild(JcrConstants.JCR_CONTENT);

		if (resource == null) {
			LOGGER.debug("Resource is null for the path-->" + page.getPath());
			throw new GenericRuntimeException("Resource is Empty");
		}

		LOGGER.debug("PAGE-->" + page.getName());
		ModifiableValueMap map = resource.adaptTo(ModifiableValueMap.class);
		map.put(updateProperty, updateValue);
	}

}
