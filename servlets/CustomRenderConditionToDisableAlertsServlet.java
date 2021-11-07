package org.kp.foundation.core.servlets;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.kp.foundation.core.constants.GlobalConstants;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.granite.ui.components.rendercondition.RenderCondition;
import com.adobe.granite.ui.components.rendercondition.SimpleRenderCondition;

/**
 * This servlet is responsible to show/hide the page properties based jcr property.
 *  It gets called from granite renderer condition node for the
 * resource-type.
 * 
 * @author Ravish Sehgal
 *
 */
@Component(service = Servlet.class, property = {
		Constants.SERVICE_DESCRIPTION + "= Show/hide page properties based on template",
		"sling.servlet.methods=" + HttpConstants.METHOD_GET,
		"sling.servlet.resourceTypes=" + "kporg/granite/rendercondition/disableAlertProperties" })
public class CustomRenderConditionToDisableAlertsServlet extends SlingSafeMethodsServlet {

	private static final long serialVersionUID = 1L;
	
	private static final String RESOURCE_PATH_EN = GlobalConstants.CONTENT_KPORG_EN+GlobalConstants.JCR_CONTENT;
	private static final String RESOURCE_PATH_NATIONAL = GlobalConstants.CONTENT_KPORG_EN_NATIONAL+GlobalConstants.JCR_CONTENT;;
	private static final String ALERT_PROPERTY_NAME = "enableAlertsExperienceFragment";

	@Override
	protected void doGet(final SlingHttpServletRequest req, final SlingHttpServletResponse res)
			throws ServletException, IOException {
		boolean render = false;
		ResourceResolver resourceResolver = req.getResourceResolver();
		Resource resource = resourceResolver.getResource(RESOURCE_PATH_NATIONAL);
		String alertPropertyValue = resource.getValueMap().get(ALERT_PROPERTY_NAME, String.class);
		
		if(null == alertPropertyValue || !Boolean.parseBoolean(alertPropertyValue)) {
			resource = resourceResolver.getResource(RESOURCE_PATH_EN);
			alertPropertyValue = resource.getValueMap().get(ALERT_PROPERTY_NAME, String.class);
		}
		if(null != alertPropertyValue) {
			render = Boolean.parseBoolean(alertPropertyValue);
		}
		req.setAttribute(RenderCondition.class.getName(), new SimpleRenderCondition(render));
	}
}