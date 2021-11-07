package org.kp.foundation.core.service;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
/**
 * This class defines the interface for error handling logic.
 * @author Krishan Rathi
 *
 */
public interface KPErrorHandlerService {


	String getDefaultPath(SlingHttpServletRequest request, Resource errorResource);

	void includeUsingGET(SlingHttpServletRequest request, SlingHttpServletResponse response, String path);
}
