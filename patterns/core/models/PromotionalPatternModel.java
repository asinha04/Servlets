package org.kp.patterns.core.models;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Via;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.kp.foundation.core.constants.GlobalConstants;
import org.kp.foundation.core.models.BaseModel;
import org.kp.foundation.core.utils.WCMUseUtil;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.policies.ContentPolicy;
import com.day.cq.wcm.api.policies.ContentPolicyManager;

/**
 * This class is responsible to read the data from the repository for
 * Promotional Component
 * 
 * @author Tirumala Malladi
 */

@Model(adaptables = { SlingHttpServletRequest.class }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)

public class PromotionalPatternModel extends BaseModel {

	@Inject
	@Via("resource")
	@Default(values="-announcement")
	private String displayVariation;

	@Inject
	@Default(booleanValues = false)
	@Via("resource")
	private boolean showCloseButton;

	@Inject
	@ValueMapValue(name = "sling:resourceType")
	private String slingResourceType;
	
	@Inject
	private Resource resource;
	
	@Inject
	@ValueMapValue(name = "jcr:title")
	private String title;

	@Inject
	private boolean editableTemplate;
	
	@Inject
	private Page page;
	
	
	private String iconClass ="promotional-component__icon";
	
	private String promoAnalyticsLocation;
	
	private static final String CQ_STYLE_IDS="cq:styleIds";
	private static final String CQ_STYLE_ID="cq:styleId";
	private static final String CQ_STYLE_CLASSES="cq:styleClasses";
	private static final String CQ_STYLE_GROUPS="/cq:styleGroups";
	private static final String LIFE_STYLE="-lifestyle";
	private static final String PROMO_COMPONENT_PHOTO="promotional-component__photo";
	private static final String CQ_DEFAULT_STYLE_CLASSES="cq:styleDefaultClasses";
	private static final String BANNER="banner";
	private static final String ARIA_LABEL_FOR_ADA_CLOSE_ICON_ES="Cerrar Bandera";
	private static final String ARIA_LABEL_FOR_ADA_CLOSE_ICON_EN="Close Banner";
	/**
	 * Init method sets the values for display variation which we are using it for analytics
	 * @throws RepositoryException 
	 * 
	 */
	@PostConstruct
	public void init() throws RepositoryException {
		if(editableTemplate) {
			ResourceResolver resourceResolver = this.request.getResourceResolver();
			  ContentPolicyManager policyManager = (ContentPolicyManager)resourceResolver.adaptTo(ContentPolicyManager.class);
			  if (policyManager != null) {
				ContentPolicy contentPolicy = policyManager.getPolicy(this.resource);
			        if (contentPolicy != null) {
			        	if(contentPolicy.getProperties()!= null && contentPolicy.getProperties().get(CQ_DEFAULT_STYLE_CLASSES)!= null) {
			        		displayVariation = contentPolicy.getProperties().get(CQ_DEFAULT_STYLE_CLASSES).toString();
			        	}
			        	String path =contentPolicy.getPath()+CQ_STYLE_GROUPS;
			        	Resource stylesGroupsRes = resourceResolver.getResource(path);
			        	if(stylesGroupsRes!= null && stylesGroupsRes.hasChildren()) {
			        		 Iterable<Resource> stylesItemsRes =  stylesGroupsRes.getChildren();
			        		 for (Resource stylesItemRes : stylesItemsRes) {
								if(stylesItemRes!= null && stylesItemRes.hasChildren()) {
									Iterable<Resource>  stylesResource = stylesItemRes.getChildren();
									for (Resource styleResource : stylesResource) {
										Iterable<Resource> itemsResource = styleResource.getChildren();
										for (Resource itemResource : itemsResource) {
											String policyStyleId = itemResource.getValueMap().get(CQ_STYLE_ID).toString();
											String[] resourceStyleIds = (String[]) resource.getValueMap().get(CQ_STYLE_IDS);
											if(resourceStyleIds!= null && resourceStyleIds.length >0) {
							                	for (String resourceStyleId : resourceStyleIds) {
							                		if(resourceStyleId.equalsIgnoreCase(policyStyleId)) {
							                			displayVariation = itemResource.getValueMap().get(CQ_STYLE_CLASSES).toString();
								                		break;
								                	}
												}
											}
						                	
										}
									}
								}
							}
			        		
			        	}
			        }
			  }
		  }
		if(displayVariation!= null && displayVariation.equalsIgnoreCase(LIFE_STYLE)) {
			showCloseButton = false;
			iconClass = PROMO_COMPONENT_PHOTO;
		}
		Node currentNode = resource.adaptTo(Node.class);
		promoAnalyticsLocation =	displayVariation!= null ? displayVariation.substring(1) :"";
		if(title != null) {
			promoAnalyticsLocation = promoAnalyticsLocation + ":" + title;
		} else if(currentNode != null) {
			promoAnalyticsLocation = promoAnalyticsLocation + ":" + currentNode.getName();
		}
	}

	public String getCloseButtonAdaText() {
		String currentLanguage =  WCMUseUtil.getCurrentLanguage(page);
		return currentLanguage.equals(GlobalConstants.EN_LANGUAGE)? ARIA_LABEL_FOR_ADA_CLOSE_ICON_EN : ARIA_LABEL_FOR_ADA_CLOSE_ICON_ES;
	}

	public String getAriaLabel() {
		return title!=null ? title : displayVariation.substring(1) + " " +  BANNER;
	}
	
	public boolean isEditableTemplate() {
		return editableTemplate;
	}
	
	public String getIconClass() {
		return iconClass;
	}
	
	public String getPromoAnalyticsLocation() throws RepositoryException {
		return promoAnalyticsLocation;
	}
	
	public String getDisplayVariation() {
		return displayVariation;
	}

	public boolean isShowCloseButton() {
		return showCloseButton;
	}
}
