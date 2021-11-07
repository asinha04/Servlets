package org.kp.patterns.core.models;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.jcr.RepositoryException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Via;
import org.kp.foundation.core.models.BaseModel;

import com.adobe.cq.wcm.core.components.models.LayoutContainer;

/**
 * Sling Model class for Show More Less Component.
 * 
 * * @author Venkata Malladi
 */
@Model(adaptables = { Resource.class,
		SlingHttpServletRequest.class }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ShowMoreLessPatternModel extends BaseModel implements LayoutContainer {
	
	private static final String CLASS_NAME = "show-more-less";
	
	
	@Inject
	@Via("resource")
	@Default(values = "1")
	private Integer defaultView;

	@Inject
	@Via("resource")
	private String showMoreLabel;

	@Inject
	@Via("resource")
	private String showLessLabel;

	@Inject
	private Resource resource;
	
	
	private List<ShowMoreLessModel> componentResources = new ArrayList<ShowMoreLessModel>();
	
	/**
	 * Init method to set the initial values.
	 * @throws RepositoryException 
	 * 
	 */
	@PostConstruct
	public void init() throws RepositoryException {
	 if(resource != null && resource.hasChildren()) {
		Resource childResource =  resource.getChild(CLASS_NAME);
		if(childResource!= null && childResource.hasChildren()) {
			 for (Iterator<Resource> it = childResource.listChildren(); it.hasNext();) {
			        Resource currentResource = it.next();
			        ShowMoreLessModel model = new ShowMoreLessModel();
			        model.setResourcePath(childResource.getName()+"/" +currentResource.getName());
			        model.setResourceType(currentResource.getResourceType());
			        componentResources.add(model);
			 }
		}
	 }

	}

	
	public Integer getDefaultView() {
		return defaultView;
	}

	public String getShowMoreLabel() {
		return showMoreLabel;
	}

	public String getShowLessLabel() {
		return showLessLabel;
	}

	public String getClassName() {
		return CLASS_NAME;
	}

	public List<ShowMoreLessModel> getComponentResources() {
		return new ArrayList<>(componentResources);
	}
	
	
}