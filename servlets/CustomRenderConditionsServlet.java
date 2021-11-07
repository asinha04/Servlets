package org.kp.foundation.core.servlets;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;

import com.adobe.granite.ui.components.rendercondition.RenderCondition;
import com.adobe.granite.ui.components.rendercondition.SimpleRenderCondition;

/**
 * This servlet is responsible to show/hide the page properties based on the
 * template. It gets called from granite renderer condition node for the
 * resource-type.
 * 
 * @author Mohan Joshi
 *
 */
@Component(service = Servlet.class, property = {
		Constants.SERVICE_DESCRIPTION + "= Show/hide page properties based on template",
		"sling.servlet.methods=" + HttpConstants.METHOD_GET,
		"sling.servlet.resourceTypes=" + "kporg/granite/rendercondition/templateBasedProperties" })
public class CustomRenderConditionsServlet extends SlingSafeMethodsServlet {

	private static final long serialVersionUID = 1L;
	private boolean templateRenderCondition = true;
	private String en_nmp = "/content/kporg/en/hawaii/nmp";
	private String es_nmp = "/content/kporg/es/hawaii/nmp";
	private static final String JCR_CONTENT ="/jcr:content";
	private static final String CONTENT_KPORG="/content/";
	private static final String TEMPLATE_KPORG="/conf";
	private static final String ITEM ="item";
	private static final String TEMPLATE_PATH="templatePath";
	private static final String TEMPLATE_RENDER_CONDITION="templateRenderCondition";
	private static final String TRUE = "true";
	private static final String APPS ="apps";

	@Override
	protected void doGet(final SlingHttpServletRequest req, final SlingHttpServletResponse res)
			throws ServletException, IOException {
		String pagePath;
		String pageResourceType = null;
		boolean render = false;
		String editableTemplatePath = null;
		ValueMap cfg = ResourceUtil.getValueMap(req.getResource());
		String templatePaths = cfg.get(TEMPLATE_PATH, new String());
		String templateRenderConditionFromDialog = cfg.get(TEMPLATE_RENDER_CONDITION, new String());
		if(templateRenderConditionFromDialog!= null) {
			templateRenderCondition = templateRenderConditionFromDialog.equalsIgnoreCase(TRUE)? true : false;
		} else {
			templateRenderCondition =true;
		}
		pagePath = req.getParameter(ITEM);
		if (null == pagePath || StringUtils.isBlank(pagePath)) {
			String pathInfo = req.getPathInfo(); 
			editableTemplatePath = pathInfo.contains(TEMPLATE_KPORG) ? req.getPathInfo().substring(pathInfo.indexOf(TEMPLATE_KPORG)) : "";
			pagePath = pathInfo.contains(CONTENT_KPORG)?req.getPathInfo().substring(pathInfo.indexOf(CONTENT_KPORG),
					pathInfo.lastIndexOf(JCR_CONTENT)):""; 
		}
		if ((StringUtils.isNotBlank(pagePath) && (pagePath.equals(en_nmp) || pagePath.equals(es_nmp))) || StringUtils.isNotBlank(editableTemplatePath)) {
			render = true;
		} else {
			String[] contentPaths = templatePaths.split("[,]", 0);
			if(contentPaths.length >= 1) {
				for (String templatePath : contentPaths) {
						if (StringUtils.isNotBlank(templatePath) && StringUtils.isNotBlank(pagePath)) {
							Resource pageResource = req.getResourceResolver().getResource(pagePath + JCR_CONTENT);
							if (pageResource != null) {
								pageResourceType = pageResource.getResourceType();
							}
							if (StringUtils.isNotBlank(pageResourceType)) {
								if (pageResourceType.equals(templatePath) || pageResourceType.equals(APPS + templatePath)) {
									if(templateRenderCondition) {
										render = true;
									}  else  {
										render = false;
									}
								} else if(!templateRenderCondition) {
									render = true;
								} 
							}
						}
					}
				}
			}
		req.setAttribute(RenderCondition.class.getName(), new SimpleRenderCondition(render));
	}
}