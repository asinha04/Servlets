package org.kp.foundation.core.servlets;

import java.io.IOException;
import java.io.Writer;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;
import org.kp.foundation.core.constants.GlobalConstants;
import org.kp.foundation.core.exception.GenericRuntimeException;
import org.kp.foundation.core.utils.WCMUseUtil;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.crx.JcrConstants;

/**
 * This servlet helps in providing JSON for Header Meta Data which could be used
 * in applications for encapsulating the business logic.
 * 
 * @author krishan rathi
 *
 */
@Component(service = Servlet.class, immediate = true,
name = "KP Foundation Header Meta Data Servlet",
property = { 
	"process.label= KP Foundation Header Meta Data Servlet", 
	"sling.servlet.resourceTypes"+"=sling/servlet/default",
	"sling.servlet.methods"+"="+HttpConstants.METHOD_GET, 
	"sling.servlet.selectors"+"=headerMetaData",
	"sling.servlet.extensions=json" })
public class HeaderMetaDataServlet extends SlingAllMethodsServlet {
	private static final String APPLICATION_JSON = "application/json";
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LoggerFactory.getLogger(HeaderMetaDataServlet.class);

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

	protected void processRequest(final SlingHttpServletRequest slingRequest,
			final SlingHttpServletResponse slingResponse) throws ServletException, IOException {
		LOGGER.debug("HeaderMetaDataServlet - processRequest begin");
		com.day.cq.wcm.api.Page page = slingRequest.getResource().adaptTo(com.day.cq.wcm.api.Page.class);
		Resource res = slingRequest.getResourceResolver().getResource(page.getPath() + "/"+  JcrConstants.JCR_CONTENT);
		String header = WCMUseUtil.getHeaderDefaultRegion(res, page);
		String locale = WCMUseUtil.getHeaderLocaleOverride(res, page);
		slingResponse.setContentType(APPLICATION_JSON);
		try {
			JSONObject obj = new JSONObject();
			obj.put(GlobalConstants.CQ_JCR_PREFIX + GlobalConstants.CANONICAL_URL, header);
			obj.put(GlobalConstants.CQ_JCR_PREFIX + GlobalConstants.LOCALE_OVERRIDE, locale);
			Writer out = slingResponse.getWriter();
			out.write(obj.toString());
		} catch (JSONException e) {
			throw new GenericRuntimeException("HeaderMetaDataServlet :: processRequest :: JSON parsing or processing error {} :: ", e);
		}

	}

}
