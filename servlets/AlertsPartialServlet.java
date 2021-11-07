package org.kp.foundation.core.servlets;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.engine.SlingRequestProcessor;
import org.kp.foundation.core.constants.AlertsComponent;
import org.kp.foundation.core.constants.GlobalConstants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.contentsync.handler.util.RequestResponseFactory;

/**
 * This servlet is responsible for rendering alerts partial for its ajax
 * implementation, It renders two different partials based upon selectors
 * 1)notification alerts and 2)bulletin alerts.
 * 
 * @author krishan rathi
 *
 */
@Component(service = Servlet.class, 
		name = "KP Foundation Alerts Partial Servlet",
		property = { 
			"sling.servlet.resourceTypes"+"="+GlobalConstants.ALERTS_PAGE_RES_TYPE,
			"sling.servlet.methods"+"="+HttpConstants.METHOD_GET, 
			"sling.servlet.selectors"+"="+AlertsComponent.ALERT_NOTIFICATION_PARTIAL,
			"sling.servlet.selectors"+"="+AlertsComponent.ALERT_BULLETIN_PARTIAL,
			"sling.servlet.extensions=html" })
public class AlertsPartialServlet extends SlingAllMethodsServlet implements Serializable {

	/** Service to create HTTP Servlet requests and responses */
	@Reference
	private transient RequestResponseFactory requestResponseFactory;

	/** Service to process requests through Sling */
	@Reference
	private transient SlingRequestProcessor requestProcessor;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LoggerFactory.getLogger(AlertsPartialServlet.class);

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

	/**
	 * The main method which does all processing for rendering the alerts
	 * partial.
	 * 
	 * @param slingRequest
	 * @param slingResponse
	 * @throws ServletException
	 * @throws IOException
	 */
	protected void processRequest(final SlingHttpServletRequest slingRequest,
			final SlingHttpServletResponse slingResponse) throws ServletException, IOException {
		slingResponse.setContentType(slingRequest.getResponseContentType());
		/* The resource path to resolve. Use any selectors or extension. */
		String path = slingRequest.getResource().getPath();
		if (AlertsComponent.ALERT_NOTIFICATION_PARTIAL
				.equalsIgnoreCase(slingRequest.getRequestPathInfo().getSelectorString())) {

			path = path + AlertsComponent.ALERTS_NOTIFICATION_PARTIAL_PATH;
		} else if ((AlertsComponent.ALERT_BULLETIN_PARTIAL
				.equalsIgnoreCase(slingRequest.getRequestPathInfo().getSelectorString()))) {

			path = path + AlertsComponent.ALERTS_BULLETIN_PARTIAL_PATH;
		}
		LOGGER.info("alerts partial path is ::" , path);
		/* Setup request */
		HttpServletRequest req = requestResponseFactory.createRequest("GET", path);

		/* Setup response */
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		HttpServletResponse resp = requestResponseFactory.createResponse(out);

		/* Process request through Sling */
		requestProcessor.processRequest(req, resp, slingRequest.getResourceResolver());
		String html = out.toString();
		slingResponse.getWriter().write(html);
	}

}
