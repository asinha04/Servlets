package org.kp.patterns.core.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.jcr.Node;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.Via;
import org.kp.foundation.core.constants.GlobalConstants;
import org.kp.foundation.core.models.BaseModel;
import org.kp.foundation.core.service.GetJcrSession;
import org.kp.foundation.core.utils.WCMUseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.acs.commons.genericlists.GenericList;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.WCMMode;

/**
 * This class is responsible to read the data from the repository and set in the
 * arrayList based on that icon list will be displayed in UI.
 * 
 * @author Tirumala Malladi
 */

@Model(adaptables = { Resource.class,
		SlingHttpServletRequest.class }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class SocialSharePatternModel extends BaseModel {

	@Inject
	@Via("resource")
	@Optional
	@Default(values = "Share")
	private String title;

	@Inject
	private SlingHttpServletRequest request;
	
	@Inject
	private Resource resource;
	
	
	@Inject
	private ResourceResolver resourceResolver;
	
	
	@Inject
	private Page page;
	
	@Inject
	private GetJcrSession getJcrSession;

	private List<SocialShareModel> socialIconsList = new ArrayList<>();
	
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SocialSharePatternModel.class);
	private static final String GOOGLE_PLUS_OLD_ICON_CLASS = "fa fa-google-plus";

	private static final String ICON_TYPE = "iconType";
	private static final String SHARE_WITH = "Share with ";
	private static final String SHARE_WITH_ES = "Compartir con ";
	private static final String SOCIAL_MEDIA = "socialmedia:";
	private static final String SOCIAL_ICONS_PATH="/etc/acs-commons/lists/social-icons";
	private static final String SOCIAL_ICONS_LIST="socialIconsList";
	private static final String ITEM="item";
	private static final String REG_EXP="::";
	private static final String SOCIAL_NETWORK="socialNetwork";
	/**
	 * Init method populates the socilaIconList and consumed in presentation layer
	 * Reading the acs-commons social-icons list from "/etc/acs-commons/lists/social-icons"  and prepopulating the socilaIconList for the edit mode.
	 *
	 * @throws Exception
	 */
	@PostConstruct
	protected void init() {
		
		try {
			String currentLanguage =  WCMUseUtil.getCurrentLanguage(page);
			String ariaLabel = currentLanguage.equals(GlobalConstants.ES_LANGUAGE) ?  SHARE_WITH_ES : SHARE_WITH;
			resourceResolver = getJcrSession.getSystemUserResourceResolver(GlobalConstants.KP_COMPILER_SERVICE);
			// Reading acs-commons social icons list
			PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
			Page listPage = pageManager.getPage(SOCIAL_ICONS_PATH);
			if(listPage!= null) {
				GenericList genericList = listPage.adaptTo(GenericList.class);
				List<GenericList.Item> list = genericList.getItems();
				if(null != resourceResolver.getResource(resource,SOCIAL_ICONS_LIST)) {
					Iterable<Resource> linkResItr  = resourceResolver.getResource(resource,SOCIAL_ICONS_LIST).getChildren();
					if (linkResItr != null) {
						for (Resource res : linkResItr) {
							SocialShareModel socialModel = res.adaptTo(SocialShareModel.class);
							if (StringUtils.isNotEmpty(socialModel.getSocialNetwork())
									|| StringUtils.isNotEmpty(socialModel.getIconType())) {
								ListIterator<GenericList.Item> iterator = list.listIterator();
								while (iterator.hasNext()) {
									GenericList.Item item = iterator.next();
									String[] classUrl = item.getValue().split(REG_EXP);
									String[] title = item.getTitle().split(REG_EXP);
									if ((null !=  socialModel.getSocialNetwork() && title[0].equalsIgnoreCase(socialModel.getSocialNetwork()) ||
										(classUrl[0].equalsIgnoreCase(socialModel.getIconType())))) {
										socialModel.setIconType(classUrl[0]);
										socialModel.setResourceUrl(classUrl[1]);
										socialModel.setAnalyticsClick(SOCIAL_MEDIA + title[0].toLowerCase());
										if(currentLanguage.equals(GlobalConstants.EN_LANGUAGE)) {
											socialModel.setAriaLabel(ariaLabel+ title[0]);
										} else {
											socialModel.setAriaLabel(ariaLabel+ title[1]);
										}
										break;
									} 
								}
								//Updating old nodes with new icon type and removing social share property and google plus node.
								if (null !=  socialModel.getSocialNetwork()) {
									Resource currentNodeResource = resourceResolver.getResource(res.getPath());
									if (null != currentNodeResource) {
										ValueMap nodeProps = (ValueMap) currentNodeResource.adaptTo(ModifiableValueMap.class);
										if (null != nodeProps) {
											if (nodeProps.containsKey(ICON_TYPE)) {
												if (nodeProps.get(ICON_TYPE).toString().equalsIgnoreCase(GOOGLE_PLUS_OLD_ICON_CLASS)) {
													currentNodeResource.adaptTo(Node.class).remove();
												} else  {
													nodeProps.put(ICON_TYPE, socialModel.getIconType());
													nodeProps.remove(SOCIAL_NETWORK);
												}
												resourceResolver.commit();
											}
										}	
									}
								}
								socialIconsList.add(socialModel);
							}
							
						}
					} 
			   } else {
				   	if(WCMMode.fromRequest(request) == WCMMode.EDIT) 
				   		prePopulateSocialIcons(list);
			   }
			} else {
				LOGGER.error("Unable to read the social icons list from acs-commons"+SOCIAL_ICONS_PATH );
			}

		} catch (Exception e) {
			LOGGER.error("Exception from SocialSharePatternModel ", e.getMessage());
		} finally {
			if (null != resourceResolver && resourceResolver.isLive()) {
				resourceResolver.close();
			}
		}

	}
	/**
	 *  Pre Populate the default social icons as soon as the component dragged on the page.
	 * @param list
	 * @throws Exception
     */
	private void prePopulateSocialIcons(List<GenericList.Item> list) throws Exception {
		Map<String, Object> nodeProps = new HashMap<>();
		nodeProps.put(JcrConstants.JCR_PRIMARYTYPE, JcrConstants.NT_UNSTRUCTURED);
		Resource socialLinks = resourceResolver.create(request.getResource(), SOCIAL_ICONS_LIST,nodeProps);
		ListIterator<GenericList.Item> iterator = list.listIterator();
		int i = 0;
		while (iterator.hasNext()) {
			GenericList.Item item = iterator.next();
			Map<String, Object> linkProps = nodeProps;
			linkProps.put(ICON_TYPE, item.getValue().split(REG_EXP)[0]);
			resourceResolver.create(socialLinks, ITEM + i, linkProps);
			i++;
		}
		resourceResolver.commit();
	}

	public String getTitle() {
		return title;
	}

	/**
	 * @return the socialIconsList
	 */
	public List<SocialShareModel> getSocialIconsList() {
		return new ArrayList<>(socialIconsList);
	}

}
