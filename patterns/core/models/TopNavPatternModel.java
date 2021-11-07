package org.kp.patterns.core.models;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.kp.foundation.core.models.BaseModel;
import org.kp.foundation.core.models.MultiFieldLinkModel;
import org.kp.foundation.core.utils.WCMUseUtil;
import com.day.cq.wcm.api.Page;

/**
 * Sling Model class for new Top Nav component.
 * 
 * * @author Mohan Joshi
 */
@Model(adaptables = { Resource.class,
		SlingHttpServletRequest.class }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class TopNavPatternModel extends BaseModel {

	private List<MultiFieldLinkModel> navLinks = new ArrayList<MultiFieldLinkModel>();
	public static final String SLASH = "/";
	public static final String ACTIVE_CLASS = "active";
	public static final String ARIA_CURRENT_VALUE = "page";

	@Inject
	@Optional
	private String navType;

	@Inject
	SlingHttpServletRequest request;

	@Inject
	private String linkClasses;

	@Inject
	private Page currentPage;
	
	private static final String HYPERLINK = "hyperlink";

	/**
	 * Init method to set the initial values and set active class for current page.
	 * 
	 */
	@PostConstruct
	public void init() {
		String currentPagePath = currentPage.getPath();
		String pageLang = currentPage.getLanguage().toString();
		int currentPagePathFifthIndex = StringUtils.ordinalIndexOf(currentPagePath, SLASH, 5);
		if (currentPagePathFifthIndex > 0 && StringUtils.isNotBlank(pageLang)) {
			String contentAfterRegionForCurrentPage = currentPagePath.substring(currentPagePathFifthIndex);
			navLinks = WCMUseUtil.getLists(request, navType, linkClasses);

			if (StringUtils.isNotBlank(contentAfterRegionForCurrentPage)) {
				for (MultiFieldLinkModel listObj : navLinks) {
					listObj.setLinkType(HYPERLINK);
					String authoredPath = listObj.getQualifiedLink();
					if (StringUtils.isNotBlank(authoredPath)) {
						int authoredPathFifthIndex = StringUtils.ordinalIndexOf(authoredPath, SLASH, 5);
						if (authoredPathFifthIndex > 0) {
							String contentAfterRegionForAuthoredPages = authoredPath.substring(authoredPathFifthIndex);
							int authoredPathStartIndex = StringUtils.ordinalIndexOf(authoredPath, SLASH, 3);
							if (authoredPathStartIndex > 0) {
								authoredPathStartIndex = authoredPathStartIndex + 1;
							}
							int authoredPathEndIndex = StringUtils.ordinalIndexOf(authoredPath, SLASH, 4);

							if (authoredPathStartIndex > 0 && authoredPathEndIndex > 0) {
								String authoredPageLang = authoredPath.substring(authoredPathStartIndex,
										authoredPathEndIndex);
								if (StringUtils.isNotBlank(authoredPageLang)
										&& StringUtils.isNotBlank(contentAfterRegionForAuthoredPages)) {
									if (authoredPageLang.equals(pageLang) && contentAfterRegionForCurrentPage
											.contains(contentAfterRegionForAuthoredPages)) {
										listObj.setAttributeValue(ACTIVE_CLASS);
										listObj.setAriaCurrent(ARIA_CURRENT_VALUE);
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

	/**
	 * Get navigation links.
	 * 
	 * @return List
	 */
	public List<MultiFieldLinkModel> getNavLinks() {
		return new ArrayList<>(navLinks);
	}
}