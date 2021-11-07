package org.kp.foundation.core.servlets;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceMetadata;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.api.wrappers.ValueMapDecorator;
import org.apache.sling.caconfig.ConfigurationBuilder;
import org.jetbrains.annotations.NotNull;
import org.kp.foundation.core.configs.EntitlementsConfig;
import org.kp.foundation.core.constants.GlobalConstants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.granite.ui.components.Value;
import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.SimpleDataSource;
import com.adobe.granite.ui.components.ds.ValueMapResource;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

@Component(service = { Servlet.class }, property = {
		"sling.servlet.resourceTypes=" + EntitlementsDataSourceServlet.RESOURCE_TYPE, "sling.servlet.methods=GET",
		"sling.servlet.extensions=html" })

public class EntitlementsDataSourceServlet extends SlingSafeMethodsServlet {

	@Reference
	private transient ResourceResolverFactory resourceResolverFactory;

	private static final String CHILDNODE = "datasource";
	private static final String ENTITLEMENTTYPE = "entitlementType";
	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger(EntitlementsDataSourceServlet.class);
	public final static String RESOURCE_TYPE = "kp/core/components/datasource/entitlements";

	@Override
	protected void doGet(@NotNull SlingHttpServletRequest request, @NotNull SlingHttpServletResponse response)
			throws ServletException, IOException {
		SimpleDataSource entitlementsDataSource = null;
		try {
			entitlementsDataSource = new SimpleDataSource(getContextTypes(request).iterator());
		} catch (Exception e) {
			log.error("Exception :: EntitlementsDataSourceServlet :: ", e);
		} 
		request.setAttribute(DataSource.class.getName(), entitlementsDataSource);
	}

	private List<Resource> getContextTypes(@NotNull SlingHttpServletRequest request)
			throws JsonParseException, JsonMappingException, IOException, LoginException, PathNotFoundException,
			RepositoryException, NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {

		Map<String, Object> map = new HashMap<String, Object>();
		map.put(ResourceResolverFactory.SUBSERVICE, GlobalConstants.KP_COMPILER_SERVICE);
		ResourceResolver resolver = resourceResolverFactory.getServiceResourceResolver(map);

		Resource pathResource = request.getResource();
		String entitlementType = pathResource.getChild(CHILDNODE).getValueMap().get(ENTITLEMENTTYPE, String.class);

		@SuppressWarnings("deprecation")
		Resource contentResource = resolver.getResource((String) request.getAttribute(Value.CONTENTPATH_ATTRIBUTE));
		ConfigurationBuilder configurationBuilder = contentResource.adaptTo(ConfigurationBuilder.class);
		List<Resource> entitlementList = new ArrayList<>();
		if (configurationBuilder != null) {
			EntitlementsConfig entitlementsConfig = configurationBuilder.as(EntitlementsConfig.class);
			Object[] entitlements = {};

//			dynamically create method name from entitlementType property saved in crx via java reflection
			Method method = entitlementsConfig.getClass().getMethod(entitlementType);
			entitlements = (Object[]) method.invoke(entitlementsConfig);
			for (Object entitlement : entitlements) {
				if(entitlement.toString().contains("::")) {
					ValueMap vm = new ValueMapDecorator(new HashMap<String, Object>());
					String[] entitlementTokens = entitlement.toString().split("::");
					if(null != entitlementTokens && entitlementTokens.length > 0) {
						vm.put("text", entitlementTokens[0].trim());
						if(!entitlementTokens[1].equalsIgnoreCase("empty") && !entitlementTokens[1].equalsIgnoreCase("")) {
							vm.put("value", entitlementTokens[1].trim());
						}else {
							vm.put("value", "");
						}
					}
					entitlementList.add(new ValueMapResource(request.getResourceResolver(), new ResourceMetadata(),
							"nt:unstructured", vm));
				}
			}
		}
		return entitlementList;
	}
}
