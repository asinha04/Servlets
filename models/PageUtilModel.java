package org.kp.foundation.core.models;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.Page;

/**
 * Sling model to handle logic required on KP Right Rail Template Page. This
 * will be explicitly used for pages created with Facility Template
 * 
 * @author Utkarsh
 */
@Model(adaptables = { SlingHttpServletRequest.class })
public class PageUtilModel {

	/**
	 * Defines the interface of a CQ WCM Page.
	 */
	@Inject
	private Page currentPage;

	@Inject
	private SlingHttpServletRequest request;

	@Inject
	@Optional
	private String nodeName;

	private ResourceResolver resourceResolver;
	private String nodePath;
	
	@PostConstruct
	protected void init() {
		nodePath = String.format("%s/%s/%s", currentPage.getPath(),JcrConstants.JCR_CONTENT,nodeName);
		resourceResolver = request.getResourceResolver();
	}
	
	public boolean hasChildren(){
		Resource resource = resourceResolver.resolve(nodePath);
		if(resource != null ){
			return resource.hasChildren();
		}else{
			return false;
		}
	}
	
	public boolean hasNode(){
		Resource resource = resourceResolver.resolve(nodePath);
		return resource != null;
	}

}