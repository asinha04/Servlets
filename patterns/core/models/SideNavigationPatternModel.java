package org.kp.patterns.core.models;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Via;
import org.kp.foundation.core.enums.LanguageLocaleEnum;
import org.kp.foundation.core.exception.GenericRuntimeException;
import org.kp.foundation.core.models.HeaderStylesModel;
import org.kp.foundation.core.models.MultiFieldLinkModel;
import org.kp.foundation.core.utils.LinkUtil;
import org.kp.foundation.core.utils.WCMUseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.day.cq.dam.api.Asset;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

/**
 * SideNavigationPatternModel class is responsible for processing the links in
 * sidenav content.
 * 
 * * @author Mohan Joshi
 */
@Model(adaptables = { SlingHttpServletRequest.class,
		Resource.class }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class SideNavigationPatternModel extends HeaderStylesModel {

	private static final Logger LOGGER = LoggerFactory.getLogger(SideNavigationPatternModel.class);
	private static final String DISPLAY_STYLE = "displayStyle";
	private static final String BASIC_HIERARCHY = "heirarchy";
	private static final String HIERARCHICAL_NAVIGATION = "hierarchical-navigation";
	private static final String PARENT_PAGE = "parentPage";
	private static final String CHILDREN_LEVEL = "childLevels";
	private static final String HIDE_NAV_SIBLINGS = "hideSiblings";
	private static final String TAB_NAV = "tab-nav";
	private static final String SIDE_NAV_CONFIGURATIONS = "sideNavConfiguration";
	private static final String DISABLE_DROPDOWN_MOBILE = "disableDropdown";
	private static final String SURROUNDING_CHARS = "surroundingChars";
	private static final String DATA_FEED_SOURCE = "dataFeedSource";
	private static final String DEFAULT_BRACKET_PREFIX = "(";
	private static final String DEFAULT_BRACKET_SUFFIX = ")";
	private static final String DEFAULT_SIDENAV = "static";
	private static final String CATEGORY_ZERO_COUNT = "0";
	private static final String ARIA_CURRENT_VALUE = "page";
	private static final String DYNAMIC_PROP = "dynamic";
	private static final String STATIC_PROP = "static";
	private static final String BASIC_HIERARCHY_ID = "side-nav-list1";
	private static final String BAR_INDICATOR_ID = "side-nav-list2";
	private static final String NON_INDENT_HIERARCHY_ID = "side-nav-list3";
	private static final String HIERARCHICAL_NAVIGATION_ID = "side-nav-list4";
	private static final String NON_INDENT_HIERARCHY = "non-indent-heirarchy";
	private static final String PATH_SEPARATOR = "-";
	private String sideNavComponentNodeName;
	private String bracketPrefix;
	private String bracketSuffix;
	private int parentLevels = 1;
	Node currentNode;
	private boolean nonIndentVariation = false;
	private boolean isNonIndentOrHierarchicalNav = false;
	private String variationClass = StringUtils.EMPTY;
	private String navLevelClass = StringUtils.EMPTY;
	private static final String BAR_NAV_TOP_LEVEL_CLASS = "bar-nav";
	private static final String HIERARCHICAL_NAVIGATION_CLASS = "heirarchy";
	private static final String VARIATION_1 = "variation1";
    private static final String VARIATION_2 = "variation2";
	private static final String VARIATION_3 = "variation3";
	private static final String VARIATION_4 = "variation4";
    private static final String SIDE_NAV_LIST1 = "side-nav-list1";
    private static final String SIDE_NAV_LIST2 = "side-nav-list2";
    private static final String SIDE_NAV_LIST3 = "side-nav-list3";
    private static final String SIDE_NAV_LIST4 = "side-nav-list4";
	private static final String NAV_NO_LINK = "nav-no-link";
	private static final String LEVEL_1 = "-level1";
	private static final String CHOOSE_CATEGORY_EN = "Choose a category";
	private static final String CHOOSE_CATEGORY_ES = "Elige una categoría";
	private String ariaLabel = StringUtils.EMPTY;
	private String ariaControls = StringUtils.EMPTY;
	private String navNoLink = StringUtils.EMPTY;
	private String level1 = StringUtils.EMPTY;
	private String SIDENAV_TITLE_CLASS_VARIATION3 = "styling-4 -book -dolphin";
	private int index=-1;
	private Page rootPage;
	private Page parentPage;

	@Inject
	@Via("resource")
	@Default(values = BASIC_HIERARCHY)
	@Named(DISPLAY_STYLE)
	private String displayStyle;

	@Inject
	@Via("resource")
	@Named(PARENT_PAGE)
	private String rootPathField;

	@Inject
	@Via("resource")
	@Default(values = "0")
	@Named(CHILDREN_LEVEL)
	private Integer childLevels;

	@Inject
	@Via("resource")
	@Default(values = DEFAULT_SIDENAV)
	@Named(SIDE_NAV_CONFIGURATIONS)
	private String sideNavConfiguration;

	@Inject
	@Via("resource")
	@Default(values = "")
	@Named(DISABLE_DROPDOWN_MOBILE)
	private String disableDropdown;

	@Inject
	@Via("resource")
	@Default(values = "")
	@Named(SURROUNDING_CHARS)
	private String surroundingChars;

	@Inject
	@Via("resource")
	@Default(values = "")
	@Named(DATA_FEED_SOURCE)
	private String dataFeedSource;

	@Inject
	@Via("resource")
	@Default(values = "false")
	@Named(HIDE_NAV_SIBLINGS)
	private boolean hideSiblings;
	
	@Inject
	@Default(values = "")
	@Via("resource")
	private String mobileDropdownDefaultText;

	@Inject
	SlingHttpServletRequest request;

	@Inject
	private Page currentPage;

	@Inject
	private Resource resource;

	@Inject
	PageManager pageManager;

	/**
	 * Init method to set the initial values.
	 * @throws RepositoryException 
	 * 
	 */
	@PostConstruct
	public void init() throws RepositoryException {
		LOGGER.info("Activating Side Nav Use");
		currentNode = resource.adaptTo(Node.class);
		sideNavComponentNodeName = currentNode.getName();
		sideNavComponentNodeName = StringUtils.isBlank(sideNavComponentNodeName) ? StringUtils.EMPTY
				: PATH_SEPARATOR + sideNavComponentNodeName;
		rootPathField = StringUtils.isBlank(rootPathField) ? currentPage.getPath() : rootPathField;
		if (!StringUtils.isBlank(surroundingChars) && surroundingChars.indexOf("#")!=index) {
			int hashIndex = surroundingChars.indexOf("#");
			bracketPrefix = surroundingChars.substring(0, hashIndex);
			bracketSuffix = surroundingChars.substring(hashIndex + 1);
		}
		
		if (sideNavConfiguration.equals(DYNAMIC_PROP)) {
			displayStyle = TAB_NAV;
		}

		if (BASIC_HIERARCHY.equals(displayStyle)) {
			if (childLevels.equals(1)) {
				ariaLabel = VARIATION_4;
				ariaControls = SIDE_NAV_LIST4;
				variationClass = HIERARCHICAL_NAVIGATION_ID;
				navLevelClass = HIERARCHICAL_NAVIGATION_CLASS;
				isNonIndentOrHierarchicalNav = true;
			} else {
				ariaLabel = VARIATION_1;
				ariaControls = SIDE_NAV_LIST1;
				childLevels = 0;
				variationClass = BASIC_HIERARCHY_ID;
				navLevelClass = StringUtils.EMPTY;
			}
		} else if (TAB_NAV.equals(displayStyle)) {
			ariaLabel = VARIATION_2;
			ariaControls = SIDE_NAV_LIST2;
			childLevels = 0;
			variationClass = BAR_INDICATOR_ID;
			navLevelClass = BAR_NAV_TOP_LEVEL_CLASS;
		} else if (NON_INDENT_HIERARCHY.equals(displayStyle)) {
			ariaLabel = VARIATION_3;
			ariaControls = SIDE_NAV_LIST3;
			nonIndentVariation = true;
			variationClass = NON_INDENT_HIERARCHY_ID;
			navLevelClass = NON_INDENT_HIERARCHY;
			isNonIndentOrHierarchicalNav = true;
		} else if (HIERARCHICAL_NAVIGATION.equals(displayStyle)) {
			ariaLabel = VARIATION_4;
        		ariaControls = SIDE_NAV_LIST4;
			childLevels = 1;
			variationClass = HIERARCHICAL_NAVIGATION_ID;
			navLevelClass = HIERARCHICAL_NAVIGATION_CLASS;
			isNonIndentOrHierarchicalNav = true;
		}
		if (isNonIndentOrHierarchicalNav) {
			navNoLink = NAV_NO_LINK;
			level1 = LEVEL_1;
		}
	}

	/**
	 * This method builds the list of links.
	 * 
	 * @returns the list of links(MultiFieldLinkModel)
	 */
	public List<MultiFieldLinkModel> getLinks() {
		rootPage = pageManager.getPage(rootPathField);
		parentPage = rootPage.getParent();
		List<MultiFieldLinkModel> siblings;
		if (!hideSiblings) {
			siblings = getChildLinks(parentPage, parentLevels);
		} else {
			siblings = new ArrayList<MultiFieldLinkModel>();
			MultiFieldLinkModel rootModel = buildModel(rootPage);
			if (rootModel != null) {
				siblings.add(rootModel);
			}
		}
		for (MultiFieldLinkModel listModel : siblings) {
			Page pageObj = pageManager.getPage(listModel.getLinkPath());
			List<MultiFieldLinkModel> childLinks = getChildLinks(pageObj, childLevels);
			listModel.setChildLinks(buildLinks(childLinks));
		}
		siblings = buildLinks(siblings);
		return siblings;
	}

	/**
	 * This method builds the list of child links.
	 * 
	 * @returns the childlinks for each sibling or current page.
	 */
	private List<MultiFieldLinkModel> getChildLinks(Page oPageObj, int level) {
		LOGGER.info("build Links");
		List<MultiFieldLinkModel> childLinks = new ArrayList<MultiFieldLinkModel>();
		if (level > 0) {
			Iterator<Page> children = oPageObj.listChildren();
			while (children.hasNext()) {
				LOGGER.debug("build Links - hasNext :: ", childLevels);
				Page childPage = children.next();
				MultiFieldLinkModel childModel = buildModel(childPage);
				if (childModel != null) {
					childLinks.add(childModel);
				}
			}
		}
		return childLinks;
	}

	/**
	 * Creating model object from the page object.
	 * 
	 * @param childPage
	 * @return LinkListModel
	 */
	private MultiFieldLinkModel buildModel(Page childPage) {
		LOGGER.debug("build Model - childPage ", childPage.getPath());
		MultiFieldLinkModel objLinkModel = null;
		if (!childPage.isHideInNav()) {
			objLinkModel = new MultiFieldLinkModel();
			objLinkModel.setLinkPath(childPage.getPath());
			String title = StringUtils.isNotBlank(childPage.getNavigationTitle()) ? childPage.getNavigationTitle() : childPage.getTitle();
			objLinkModel.setLinkLabel(title);
			
			currentPage = StringUtils.isBlank(rootPathField) ? currentPage : rootPage;
			if (childPage.getPath().equals(currentPage.getPath())) {
				objLinkModel.setSelectedPage("selected");
				objLinkModel.setAriaCurrent(ARIA_CURRENT_VALUE);
			}
			if (childPage.getPath().equals(currentPage.getPath())
					|| childPage.getPath().equals(currentPage.getParent().getPath())) {
				objLinkModel.setMobileSelected(Boolean.TRUE);
			}
			if (childLevels > 0 && childPage.listChildren().hasNext()) {
				objLinkModel.setChildLinks(getChildLinks(childPage, childLevels - 1));
			}

		}
		return objLinkModel;
	}

	/**
	 * This method sets the link properties.
	 * 
	 * @returns the list of links(MultiFieldLinkModel)
	 */
	private List<MultiFieldLinkModel> buildLinks(List<MultiFieldLinkModel> linkModelList) {
		if (!linkModelList.isEmpty()) {
			for (MultiFieldLinkModel listModel : linkModelList) {
				String path = LinkUtil.getRelativeURL(request, listModel.getLinkPath());
				listModel.setLinkPath(path);
				if (listModel.getChildLinks() != null && listModel.getChildLinks().size() > 0) {
					LOGGER.debug("child level 2 links");
					buildLinks(listModel.getChildLinks());
				}
			}
		}
		return linkModelList;
	}

	/**
	 * 
	 * This method reads the JSON which is authored in Side Nav component in
	 * dataFeedSource field. Then creates the java mapping object for each of the
	 * items in Json. And then creates the list of MultiFieldLinkModel which is
	 * returned back to sightly html.
	 * 
	 * @returns the list of MultiFieldLinkModel
	 */
	public List<MultiFieldLinkModel> getJsonFeed() throws Exception {
		List<MultiFieldLinkModel> jsonLinks = null;
		try {
			if (!StringUtils.isBlank(dataFeedSource)) {
				Resource assetResource = resource.getResourceResolver().getResource(dataFeedSource);
				if (assetResource != null) {
					Asset asset = assetResource.adaptTo(Asset.class);
					InputStream assetData = asset.getOriginal().getStream();
					JsonReader reader = new JsonReader(new InputStreamReader(assetData, "UTF-8"));
					JsonElement element = new JsonParser().parse(new InputStreamReader(assetData));
					String jsonData = element.getAsJsonObject().toString();

					Gson gson = new GsonBuilder().enableComplexMapKeySerialization()
							.excludeFieldsWithoutExposeAnnotation().serializeNulls().create();
					if (!StringUtils.isBlank(jsonData)) {
						SideNavJsonData sideNavJsonResult = gson.fromJson(jsonData, SideNavJsonData.class);

						List<SideNavLinksData> linksList = sideNavJsonResult.getLinks();
						jsonLinks = new ArrayList<MultiFieldLinkModel>();

						getmultiFieldLinkModel(linksList, jsonLinks);
					}
					assetData.close();
					reader.close();
				}
			}
		} catch (UnsupportedEncodingException e) {
			throw new GenericRuntimeException("SideNavUse :: UnsupportedEncodingException:: getJsonFeed method {}.", e);
		} catch (IOException e) {
			throw new GenericRuntimeException("SideNavUse :: IOException:: getJsonFeed method {}.", e);
		}
		return jsonLinks;
	}

	/**
	 * This method creates list of MultiFieldLinkModel.
	 * 
	 */
	private void getmultiFieldLinkModel(List<SideNavLinksData> linksList, List<MultiFieldLinkModel> jsonLinks) {
		int trackFirstItem=0;
		for (SideNavLinksData links : linksList) {
			trackFirstItem++;
			MultiFieldLinkModel objLinkModel = new MultiFieldLinkModel();
			if (trackFirstItem ==1 ) {
				objLinkModel.setMobileSelected(Boolean.TRUE);
			}
			objLinkModel.setLinkLabel(links.getLabel());
			objLinkModel.setLinkPath(links.getUrl());
			objLinkModel.setAmount(StringUtils.isBlank(links.getAmount()) ? CATEGORY_ZERO_COUNT : links.getAmount());
			objLinkModel.setId(links.getId());
			jsonLinks.add(objLinkModel);
		}
	}

	/**
	 * Getter for root Path.
	 * 
	 * @return the rootPathField
	 */
	public String getRootPathField() {
		return rootPathField;
	}

	/**
	 * Getter for Display Style.
	 * 
	 * @return the displayStyle
	 */
	public String getDisplayStyle() {

		return displayStyle;
	}

	/**
	 * Getter for childLevels.
	 * 
	 * @return the childLevels
	 */
	public int getChildLevels() {
		return childLevels;
	}

	/**
	 * @return the sideNavConfiguration
	 */
	public String getSideNavConfiguration() {
		return sideNavConfiguration;
	}

	/**
	 * @return the value of sideNavConfiguration
	 */
	public boolean getIsDynamic() {
		return getSideNavConfiguration().equals(DYNAMIC_PROP);
	}

	/**
	 * @return the value sideNavConfiguration
	 */
	public boolean getIsStatic() {
		return getSideNavConfiguration().equals(STATIC_PROP);
	}

	/**
	 * @return the surroundingChars
	 */
	public String getSurroundingChars() {
		return surroundingChars;
	}

	/**
	 * @return the disableDropdown
	 */
	public String getDisableDropdown() {
		return disableDropdown;
	}

	/**
	 * @return the bracketPrefix
	 */
	public String getBracketPrefix() {
		String openingBracket;
		if (!StringUtils.isBlank(bracketPrefix)) {
			openingBracket = bracketPrefix.trim();
		} else {
			openingBracket = DEFAULT_BRACKET_PREFIX;
		}
		return openingBracket;
	}

	/**
	 * @return the bracketSuffix
	 */
	public String getBracketSuffix() {
		String closingBracket;
		if (!StringUtils.isBlank(bracketSuffix)) {
			closingBracket = bracketSuffix.trim();
		} else {
			closingBracket = DEFAULT_BRACKET_SUFFIX;
		}
		return closingBracket;
	}

	/**
	 * @return the dataFeedSource
	 */
	public String getDataFeedSource() {
		return dataFeedSource;
	}

	/**
	 * @return the dataFeedSource
	 */
	public String getVariationClass() {
		return variationClass + sideNavComponentNodeName;
	}

	/**
	 * @return the nonIndentVariation
	 */
	public boolean isNonIndentVariation() {
		return nonIndentVariation;
	}

	/**
	 * @return the navLevelClass
	 */
	public String getNavLevelClass() {
		return navLevelClass;
	}

	/**
	 * @return the isNonIndentOrHierarchicalNav
	 */
	public boolean isNonIndentOrHierarchicalNav() {
		return isNonIndentOrHierarchicalNav;
	}

	/**
	 * @return the ariaLabelledby
	 */
	public String getAriaLabelledBy() {
		String ariaLabelledby = StringUtils.EMPTY;
		if (NON_INDENT_HIERARCHY.equals(displayStyle) && StringUtils.isNotBlank(getHeaderText())) {
			ariaLabelledby = ariaLabel + sideNavComponentNodeName;
		}
		return ariaLabelledby;
	}

	/**
	 * @return the ariaLabel
	 */
	public String getAriaLabel() {
		String ariaLabel = StringUtils.EMPTY;
		if (!NON_INDENT_HIERARCHY.equals(displayStyle)) {
			ariaLabel = getHeaderText();
		}
		return ariaLabel;
	}
	
	/**
	 * @return the navNoLink
	 */
	public String getNavNoLink() {
		return navNoLink;
	}

	/**
	 * @return the level1
	 */
	public String getLevel1() {
		return level1;
	}
	
	/**
	 * @return the ariaControls
	 */
	public String getAriaControls() {
		return ariaControls;
	}
	
	/**
	 * @return title class based on variations
	 */
	public String getClasses() {
		String titleClass = StringUtils.EMPTY;
		if (NON_INDENT_HIERARCHY.equals(displayStyle)) {
			titleClass = SIDENAV_TITLE_CLASS_VARIATION3;
		}
		return titleClass;
	}
	
	/**
	 * @return id
	 */
	public String getId() {
		return ariaLabel + sideNavComponentNodeName;
	}
	
	/**
	 * @return headerTag
	 */
	public String getHeaderTag() {
		return StringUtils.EMPTY;
	}
		
	/**
	 * @return the mobile dropdown default value
	 */
	public String getMobileDropdownDefaultText() {
		String mobileDropdownDefaultValue;
		if (StringUtils.isNotEmpty(mobileDropdownDefaultText)) {
			mobileDropdownDefaultValue = mobileDropdownDefaultText;
		} else {
			mobileDropdownDefaultValue = CHOOSE_CATEGORY_EN;
			String language = WCMUseUtil.getCurrentLanguage(currentPage);
			if (StringUtils.isNotEmpty(language)) {
				if (language.equalsIgnoreCase(LanguageLocaleEnum.EN.name())) {
					mobileDropdownDefaultValue = CHOOSE_CATEGORY_EN;
				} else if (language.equalsIgnoreCase(LanguageLocaleEnum.ES.name())) {
					mobileDropdownDefaultValue = CHOOSE_CATEGORY_ES;
				}
			}
		}
		return mobileDropdownDefaultValue;
	}
}    