package org.kp.foundation.core.filters;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.engine.EngineConstants;
import org.kp.web.envconfig.core.util.JsonSettingsOsgiUtil;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;

/**
 * Filter Class to add custom MDC properties to Log Message.
 * 
 * * @author Mohan Joshi
 */
@Component(service = javax.servlet.Filter.class,
name ="KPLogger MDC Filter",
configurationPid = "org.kp.foundation.core.filters.KPLoggerFilter",
property = { "process.label= KPLogger MDC Filter", EngineConstants.SLING_FILTER_SCOPE + "=" + EngineConstants.FILTER_SCOPE_REQUEST,
		Constants.SERVICE_RANKING+ ":Integer=0"})
@Designate(ocd = KPLoggerFilter.Config.class)
public class KPLoggerFilter implements javax.servlet.Filter {
	public static final String APPLICATION_ID = "kp.appId";
	public static final String COOKIE_IDENTIFIER_PREFIX = "cookie.";
	public static final String HEADER_IDENTIFIER_PREFIX = "header.";
	private Set<String> headerNames = new CopyOnWriteArraySet<String>();
	private Set<String> cookieNames = new CopyOnWriteArraySet<String>();
	private static final String APP_ID = "x-appname";
	List<String> keyNames;

	@ObjectClassDefinition(name="KPLogger MDC Filter",
	        description = "Filter Class to add custom MDC properties to Log Message")
	public static @interface Config {
		@AttributeDefinition(name = "headers", description = "Request header Properties")
		String[] headers();
	
		@AttributeDefinition(name = "cookies", description = "Cookie Properties")
		String[] cookies();
	}
	
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
			throws IOException, ServletException {
		final SlingHttpServletRequest request = (SlingHttpServletRequest) servletRequest;
		try {
			insertIntoMDC(request);
			filterChain.doFilter(request, servletResponse);
		} finally {
			clearMDC();
		}
	}

	@Activate
	public void activate(final Config config) {
		modified(config);
	}

	private void clearMDC() {
		for (String key : keyNames) {
			MDC.remove(key);
		}
	}

	/**
	 * This method populates MDC map with the appId & request headers and
	 * cookies properties (configured in KPLoggerFilter OSGI). Keys in the MDC map should
	 * match with the message pattern configured in Apache
	 * Sling Logging Configuration. For instance, here is message pattern -
	 * [KP.AppId: %X{kp.appId:-NULL}]
	 * 
	 */
	public void insertIntoMDC(SlingHttpServletRequest request) {
		String appName = null;
		Resource resource = request.getResource();
		PageManager pMgr = resource.getResourceResolver().adaptTo(PageManager.class);
		Page page = pMgr.getContainingPage(resource);

		// This if-condition is to set appId for content pages
		if (null != page) {
			appName = JsonSettingsOsgiUtil.getAppNameFromPage(page);
			MDC.put(APPLICATION_ID, appName);
		}
		// This else-if condition is to set appId for non-content pages like servlets
		else {
			MDC.put(APPLICATION_ID, request.getHeader(APP_ID));
		}

		// Populate MDC map with request header properties configured in KPLoggerFilter OSGI
		for (String headerName : headerNames) {
			MDC.put(HEADER_IDENTIFIER_PREFIX + headerName, request.getHeader(headerName));
		}

		// Populate MDC map with cookie properties configured in KPLoggerFilter OSGI
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie c : cookies) {
				if (cookieNames.contains(c.getName())) {
					MDC.put(COOKIE_IDENTIFIER_PREFIX + c.getName(), c.getValue());
				}
			}
		}
	}

	/**
	 * This method reads the KPLoggerFilter OSGI and creates the individual sets
	 * of request header and cookie properties. These sets are then iterated to
	 * read properties from cookies and request headers.
	 */
	@Modified
	private void modified(final Config config) {
		Set<String> headers = toTrimmedValues(config.headers());
		headerNames.clear();
		headerNames.addAll(headers);

		Set<String> cookies = toTrimmedValues(config.cookies());
		cookieNames.clear();
		cookieNames.addAll(cookies);

		keyNames = new ArrayList<String>();
		keyNames.addAll(headerNames);
		keyNames.addAll(cookieNames);
		keyNames.add(APPLICATION_ID);
	}

	/**
	 * This method create sets of request header and cookie properties.
	 * 
	 */
	private static Set<String> toTrimmedValues(String[] values) {
		Set<String> result = new HashSet<String>(values.length);
		for (String value : values) {
			if (value != null && value.trim().length() > 0) {
				result.add(value.trim());
			}
		}
		return result;
	}

	public void destroy() {
	}
}