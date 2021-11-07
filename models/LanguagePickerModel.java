package org.kp.foundation.core.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.*;
import org.kp.foundation.core.constants.GlobalConstants;
import org.kp.foundation.core.constants.LanguagePicker;
import org.kp.foundation.core.service.GetJcrSession;
import org.kp.foundation.core.utils.LinkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.commons.inherit.HierarchyNodeInheritanceValueMap;
import com.day.cq.commons.inherit.InheritanceValueMap;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;

/**
 * Sling Model class for Language Picker Component.
 * 
 * * @author Mohan Joshi
 */
@Model(adaptables = { Resource.class,
		SlingHttpServletRequest.class }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class LanguagePickerModel extends BaseModel {

	private static final Logger LOGGER = LoggerFactory.getLogger(LanguagePickerModel.class);
	private Page siteRootPage;
	private Locale curPageLocale;
	private boolean disableFooterUpperFourColumns;
	private Page languagePage;
	public static final String CONSTANT_CONTENT = "content";
	public static final String CONSTANT_SINGLE_SLASH = "/";
	private String currentPagePath;
	private Page esEquivalentpage;
	private ValueMap pageProperties;
	private String enableLanguageModal = Boolean.FALSE.toString();
	
	@Inject
	SlingHttpServletRequest request;

	@Inject
	PageManager pageManager;

	@Inject
	private Page curPage;

	@Inject
	private Resource resource;
	
	@Inject
	private Resource currentResource;
		 
	@Inject
	private GetJcrSession getJcrSession;
	
	@Inject
	@Optional
	@Via("resource")
	@Default(values = LanguagePicker.LANGUAGE_PICKER_LABEL_EN)
	private String languagePickerLabel;
	
	@Inject
	@Optional
	@Via("resource")
	@Default(values = LanguagePicker.OTHER_LANGUAGES_PICKER_LABEL_EN)
	private String otherLanguagesPickerLabel;
	
	@Inject
	@Optional
	@Via("resource")
	@Default(values = LanguagePicker.OTHER_LANGUAGES_LINK)
	private String otherLanguagesLinkURL;
	
	private ResourceResolver resourceResolver;

	/**
	 * Init method sets the values for language modal and session time out modal
	 * based on parameters.
	 * 
	 */
	
	@PostConstruct
	public void init() {
		try {
			InheritanceValueMap inheritedProp = new HierarchyNodeInheritanceValueMap(resource);
			resourceResolver = getJcrSession.getSystemUserResourceResolver(GlobalConstants.KP_COMPILER_SERVICE);
			pageProperties = curPage.getProperties();
			currentPagePath = curPage.getPath();
			setEnableLanguageModelProperty();
			if (null != pageManager && StringUtils.isNotBlank(currentPagePath)) {
				esEquivalentpage = pageManager.getPage(
						currentPagePath.replace(GlobalConstants.CONTENT_KPORG_EN, GlobalConstants.CONTENT_KPORG_ES));
			}
			siteRootPage = curPage.getAbsoluteParent(GlobalConstants.SITE_ROOT_LEVEL);
			languagePage = curPage.getAbsoluteParent(LanguagePicker.SITE_LANGUAGE_PAGE_LEVEL);
			disableFooterUpperFourColumns = Boolean
					.valueOf(inheritedProp.get(GlobalConstants.DISABLE_FOOTER_UPPER_FOUR_COLUMNS, "false"));
			if (languagePage != null) {
				curPageLocale = languagePage.getLanguage(false);
			}
		} catch (Exception e) {
			LOGGER.error("Exception --- ", e.getMessage());
		} finally {
			if (null != resourceResolver) {
				resourceResolver.close();
			}
		}
	}

	/**
	 * Provides language list and corresponding page path.
	 * 
	 * @return languageList
	 */
	public List<LanguageModel> getLanguageList() {
		// Iterate over all site locales and set into LanguageModel properties.
		// Then return he language list to the caller.
		List<LanguageModel> languageList = new ArrayList<LanguageModel>();
		if (siteRootPage == null || curPage == null) {
			LOGGER.error("siteRootPage or curPage are null!");
			return languageList;
		}
		java.util.Iterator<Page> pageIterator = siteRootPage.listChildren();
		while (pageIterator != null && pageIterator.hasNext()) {
			final Page curChildPage = (Page) pageIterator.next();
			Locale curLocale = curChildPage.getLanguage(false);
			LanguageModel langModel = new LanguageModel();
			langModel.setLocale(curLocale.toString());
			langModel.setLocaleCookie(LanguagePicker.getLanguageCode().get(curLocale.toString()));
			langModel.setLanguageTitle(curChildPage.getTitle());
			String finalPath = StringUtils.replace(request.getPathInfo(),
					siteRootPage.getPath() + "/" + curPageLocale.toString(),
					siteRootPage.getPath() + "/" + curLocale.toString());
			finalPath = LinkUtil.removeHtmlExtn(finalPath);
			finalPath = LinkUtil.getExternalURL(request, finalPath, curChildPage);
			LOGGER.debug("finalPath(before FDL adjustment) {}", finalPath);
			finalPath = LinkUtil.adjustFDLProviderPath(finalPath);
			LOGGER.debug("finalPath(after FDL adjustment) {}", finalPath);
			finalPath = LinkUtil.adjustFDLFacilityPath(finalPath, request);
			langModel.setLanguagePath(finalPath);
			languageList.add(langModel);
		}
		return languageList;
	}

	public String getLanguagePickerLabel() { return languagePickerLabel; }
	
	public String getOtherLanguagesPickerLabel() { return otherLanguagesPickerLabel; }

	public String getOtherLanguagesLinkURL() { return otherLanguagesLinkURL; }

	public String getSelectedLanguage() {
		return curPageLocale.toString();
	}

	public boolean isDisableFooterUpperFourColumns() {
		return disableFooterUpperFourColumns;
	}

	/**
	 * This method returns true  if all the below mentioned conditions are met. Otherwise returns false.
	 * 	- Current page is not null and contains /content/kporg/en
	 *  - Spanish equivalent page exists
	 *  - enableLanguageModal property is to true/or page is loading first time
	 *  
	 * @return String
	 */
	public String getEnableLanguageModelProperty() {
		if (StringUtils.isNotBlank(currentPagePath) && currentPagePath.contains(GlobalConstants.CONTENT_KPORG_EN)) {
			enableLanguageModal = pageProperties.get(GlobalConstants.ENABLE_LANGUAGE_MODAL_PROPERTY,
					Boolean.TRUE.toString());
		}
		return enableLanguageModal;
	}

	/**
	 * This method returns true if Spanish equivalent page exists, otherwise returns false.
	 * @return true/false
	 */
	public boolean isSpanishEquivalentPage() {
		if (null != esEquivalentpage) {
			return true;
		}
		return false;
	}
	
	/**
	 * This method adds the enableLanguageModal true property to page jcr:content if it doesn't exist.
	 * And it's added for backward compatibility.
	 * 
	 */
	private void setEnableLanguageModelProperty() throws Exception {
		if (StringUtils.isNotBlank(currentPagePath) && currentPagePath.startsWith(GlobalConstants.CONTENT_KPORG_EN)) {
			Resource currentPageResource = resourceResolver.getResource(curPage.getPath() + "/jcr:content");
			if (null != currentPageResource) {
				ValueMap writeProps = (ValueMap) currentPageResource.adaptTo(ModifiableValueMap.class);
				if (null != writeProps) {
					if (!writeProps.containsKey(GlobalConstants.ENABLE_LANGUAGE_MODAL_PROPERTY)) {
						writeProps.put(GlobalConstants.ENABLE_LANGUAGE_MODAL_PROPERTY, "true");
						resourceResolver.commit();
					}
				}	
			}
		}
	}
}