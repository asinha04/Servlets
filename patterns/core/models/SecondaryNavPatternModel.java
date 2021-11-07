package org.kp.patterns.core.models;

import static org.kp.foundation.core.constants.Constants.EMPTY_STRING;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.Via;
import org.kp.foundation.core.models.BaseModel;
import org.kp.foundation.core.models.NavigationItem;
import org.kp.foundation.core.utils.LinkUtil;
import org.kp.foundation.core.utils.WCMUseUtil;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.commons.inherit.HierarchyNodeInheritanceValueMap;
import com.day.cq.commons.inherit.InheritanceValueMap;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;

/**
 * This is a Secondary Navigation Use Class. This will provide link models for both scenarios, one
 * for single level children and next
 * 
 * @author Utkarsh Thakkar
 */
@Model(adaptables = { SlingHttpServletRequest.class,
        Resource.class }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class SecondaryNavPatternModel extends BaseModel {

  private static final Logger LOGGER = LoggerFactory.getLogger(SecondaryNavPatternModel.class);
  private static final String SECNAV_ANALYTICS_TAG = "Link";
  private static final String SELECTED_TAG = "selected";
  private static final String SEC_NAV_INCLUDE_ROOT = "includeRoot";
  private static final String SEC_NAV_DISPLAY_CHILD_ONLY = "onlyRootPages";
  private static final String SEC_NAV_ROOT_PATH = "rootPage";
  private static final String SEC_NAV_ROOT_PAGE_TEXT_OVERRIDE = "rootPageTextOverride";
  private static final String SEC_NAV_OVERVIEW_LABEL = "overviewLabel";
  private static final String HIGH_LIGHTING_CSS = "this-page";
  private static final String HIGH_LIGHTING_SUB_CSS = "this-page-sub";
  private static final String FORMAT_PATTERN = "%s %s";
  private static final String ONE_LEVEL_LIST_LEVEL_ONE = "one-level list-level-1";
  private static final String TWO_LEVEL_LIST_LEVEL_ONE = "two-level list-level-1";
  private static final String SUB_MENU_CLASS= "has-submenu";
  private static final String TOGGLE_CLASS= "level-2-toggle";
  private static final String DISPLAY_VARIATION_PROPERTY = "displayVariation";
  private static final String DEFAULT_DISPLAY_VARIATION_VALUE = "variation1";
  private static final String SECOND_DISPLAY_VARIATION_VALUE = "variation2";
  private static final String ARIA_CURRENT= "page";
  private static final String ARIA_LABEL_EN = "Secondary Navigation";
  private static final String ARIA_LABEL_ES = "Navegación Secundaria";
  private static final String ENGLISH = "en";
  private static final String ESPANOL = "es";
  private String ariaLabel="";
  private boolean exisitingFlag = false;

  @Inject
  private Resource resource;

  @Inject
  @Named(SEC_NAV_INCLUDE_ROOT)
  @Default(booleanValues = true)
  private boolean includeRoot;

  private boolean onlyRootPages = false;

  @Inject
  @Named(SEC_NAV_ROOT_PATH)
  @Optional
  private String rootPagePath;

  @Inject
  @Named(SEC_NAV_ROOT_PAGE_TEXT_OVERRIDE)
  @Optional
  private String rootPageTextOverride;

  @Inject
  @Named(SEC_NAV_OVERVIEW_LABEL)
  @Optional
  @Default(values = "")
  private String overViewLabel;
  
  @Inject
  @Default(values = DEFAULT_DISPLAY_VARIATION_VALUE)
  @Via("resource")
  private String displayVariation;
  
  @Inject
  Page currentPage;
  
  
  private String styleClass;
  private String subMenuClass="";
  private String toggleClass="";


  private int maxLimit;

  private List<NavigationItem> navLinks = new ArrayList<>();
  private String selectedPagePath;
  private String selectedPageTitle;
  private String selectedPageMobileTitle;
  private int iterationLevel = 2;
  
  
  @Reference
  private ResourceResolverFactory resolverFactory;

  @PostConstruct
  public void init() {

    LOGGER.debug("Entering SecondaryNavModel ---- >");

    PageManager manager = resource.getResourceResolver().adaptTo(PageManager.class);
    if (manager == null) {
      return;
    }
    Page currentPage = manager.getContainingPage(resource);

    selectedPagePath = LinkUtil.getPathfieldURL(currentPage.getPath());
    selectedPageTitle = (null == currentPage.getNavigationTitle()) ? currentPage.getTitle()
        : currentPage.getNavigationTitle();
    selectedPageMobileTitle = selectedPageTitle;

    setProperties(resource);
    
//  this is to cater backward compatibility
    Node currentNode;
    currentNode = resource.adaptTo(Node.class);
    try {
      if( null!= currentNode && currentNode.hasProperty(SEC_NAV_DISPLAY_CHILD_ONLY)) {
        boolean value = currentNode.getProperty(SEC_NAV_DISPLAY_CHILD_ONLY).getBoolean();
        if(value) {
          currentNode.setProperty(DISPLAY_VARIATION_PROPERTY, DEFAULT_DISPLAY_VARIATION_VALUE);
          onlyRootPages = true;
        }else {
          currentNode.setProperty(DISPLAY_VARIATION_PROPERTY, SECOND_DISPLAY_VARIATION_VALUE);
        }
        exisitingFlag = true;
        currentNode.getProperty(SEC_NAV_DISPLAY_CHILD_ONLY).remove();
        currentNode.getSession().save();
      }
    } catch (RepositoryException e) {
      LOGGER.error("SecondaryNavPatternModel :: RepositoryException    "+e.toString());
    }

    if (rootPagePath == null) {
      rootPagePath = currentPage.getPath();
    }
    
    if (displayVariation.equalsIgnoreCase(DEFAULT_DISPLAY_VARIATION_VALUE) && exisitingFlag != true) {
      onlyRootPages = true;
    }

//  if onlyRootPages is true set values according to variation 1 otherwise variation 2
    if (onlyRootPages ) {
      iterationLevel = 1; /* fetch only first level pages */
      styleClass = ONE_LEVEL_LIST_LEVEL_ONE;
    } else {
         styleClass = TWO_LEVEL_LIST_LEVEL_ONE;
         subMenuClass =SUB_MENU_CLASS;
         toggleClass = TOGGLE_CLASS;
    }

    Page rootPage = manager.getPage(rootPagePath);
    maxLimit = rootPage.getDepth() + iterationLevel;
    processRootPage(currentPage, rootPage);
    navLinks.addAll(processPages(currentPage, rootPage, new NavigationItem()));
    LOGGER.debug(navLinks.toString());

  }


  /**
   * Method to set the properties in each navItem
   * 
   * @param child
   * @param childNav
   * @param selectedNavItem
   * @param currentPage
   */
  private void setProperties(Page child, NavigationItem childNav, NavigationItem selectedNavItem,
      Page currentPage) {
    childNav.setPath(LinkUtil.getPathfieldURL(child.getPath()));
    childNav.setTitle(
        (null == child.getNavigationTitle()) ? child.getTitle() : child.getNavigationTitle());
    if (includeRoot) {
      childNav.setOverriddenTitle(EMPTY_STRING);
    } else {
      childNav.setOverriddenTitle(
          (null == child.getNavigationTitle()) ? child.getTitle() : child.getNavigationTitle());
    }
    setSelectedPathAndPage(currentPage, child, childNav);
    if (childNav.getIsSelectedPath()) {
      selectedNavItem.setSelectedParent(true);
      childNav.setHighlightingSubCss(HIGH_LIGHTING_SUB_CSS);
      childNav.setAriaCurrent(ARIA_CURRENT);
      ariaLabel = getAriaLabel();
    }
  }

  /**
   * Recursive method to build the naviagtion items.
   * 
   * @param currentPage
   * @param processingPage
   * @param parentNavItem
   * @return
   */
  private List<NavigationItem> processPages(Page currentPage, Page processingPage,
      NavigationItem parentNavItem) {

    List<NavigationItem> list = new ArrayList<>();
    NavigationItem selectedNavItem = new NavigationItem();/*
                                                           * Based on child property parent flag is
                                                           * updated. This object is used for that
                                                           */
    Iterator<Page> pages = processingPage.listChildren();
    while (pages.hasNext()) {
      NavigationItem childNavItem = new NavigationItem();
      Page childPage = pages.next();
      recursiveFunctionCall(childPage, selectedNavItem, currentPage, childNavItem);
      if (!childPage.isHideInNav()) {
        setProperties(childPage, childNavItem, selectedNavItem, currentPage);
        list.add(childNavItem);
      }
    }
    /*
     * Processing the page and its siblings page. If one of the page's is the selected page then
     * parent object is updated
     */
    parentNavItem.setSelectedParent(selectedNavItem.isSelectedParent());

    return list;
  }

  /**
   * Recursive call function.
   * 
   * @param childPage
   * @param selectedNavItem
   * @param currentPage
   * @param childNavItem
   */
  private void recursiveFunctionCall(Page childPage, NavigationItem selectedNavItem,
      Page currentPage, NavigationItem childNavItem) {
    List<NavigationItem> finalChildList = new ArrayList<>();
    if (childPage.listChildren().hasNext() && maxLimit > childPage.getDepth()
        && !childPage.isHideInNav()) {
      /* Add the parent as first item in list */
      NavigationItem parentAsFirstNavItem = new NavigationItem();
      setProperties(childPage, parentAsFirstNavItem, selectedNavItem, currentPage);
      parentAsFirstNavItem
          .setTitle(String.format(FORMAT_PATTERN, parentAsFirstNavItem.getTitle(), overViewLabel));
      finalChildList.add(parentAsFirstNavItem);
      /*
       * returned recurisve call value is set below. Once reached the leaf the leafs siblings are
       * returned by the below recursive call and is set.
       */
      finalChildList.addAll(processPages(currentPage, childPage, selectedNavItem));
      childNavItem.setChildNavs(finalChildList);
      /*
       * Through reference flag is set based on child property and that flag is set to parent below
       */
      childNavItem.setSelectedParent(selectedNavItem.isSelectedParent());
    } else if (!childPage.isHideInNav() && maxLimit > childPage.getDepth()) {
      /* Add the parent as first item in list */
      NavigationItem parentAsFirstNavItem = new NavigationItem();
      setProperties(childPage, parentAsFirstNavItem, selectedNavItem, currentPage);
      parentAsFirstNavItem
          .setTitle(String.format(FORMAT_PATTERN, parentAsFirstNavItem.getTitle(), overViewLabel));
      finalChildList.add(parentAsFirstNavItem);
      childNavItem.setChildNavs(finalChildList);
    }
  }

  /**
   * @param currentPage
   * @param rootPage
   */
  private void processRootPage(Page currentPage, Page rootPage) {
    if (includeRoot && !rootPage.isHideInNav()) {
      boolean isLabelOverridden =
          (null != rootPageTextOverride && rootPageTextOverride.trim().length() > 0) ? true : false;
      NavigationItem rootPageNavItem = new NavigationItem();
      rootPageNavItem.setPath(LinkUtil.getPathfieldURL(rootPage.getPath()));
      rootPageNavItem.setTitle((null == rootPage.getNavigationTitle()) ? rootPage.getTitle()
          : rootPage.getNavigationTitle());
      if (isLabelOverridden) {
        rootPageNavItem.setOverriddenTitle(rootPageTextOverride);
        rootPageNavItem.setTitle(rootPageTextOverride);
      } else {
        rootPageNavItem
            .setOverriddenTitle((null == rootPage.getNavigationTitle()) ? rootPage.getTitle()
                : rootPage.getNavigationTitle());
      }
      setSelectedPathAndPage(currentPage, rootPage, rootPageNavItem);
      buildChildNavRootItem(rootPageNavItem);
      navLinks.add(rootPageNavItem);
    }
  }


  /**
   * @param rootPageNavItem
   */
  private void buildChildNavRootItem(NavigationItem rootPageNavItem) {

    if (!onlyRootPages) {
      /* Append the overview along with title and add the object one more time */
      NavigationItem rootPageChildNavItem = new NavigationItem();
      rootPageChildNavItem.setAnalytics(rootPageNavItem.getAnalytics());
      rootPageChildNavItem.setHref(rootPageNavItem.getHref());
      rootPageChildNavItem.setIsSelectedPath(rootPageNavItem.getIsSelectedPath());
      rootPageChildNavItem.setHighlightingSubCss(rootPageNavItem.getHighlightingSubCss());
      rootPageChildNavItem.setHighlightingCss(rootPageNavItem.getHighlightingCss());
      rootPageChildNavItem.setPath(rootPageNavItem.getPath());
      rootPageChildNavItem
          .setTitle(String.format(FORMAT_PATTERN, rootPageNavItem.getTitle(), overViewLabel));
      rootPageChildNavItem.setOverriddenTitle(null);
      rootPageChildNavItem.setSelected(rootPageNavItem.getSelected());
      rootPageChildNavItem.setSelectedParent(rootPageNavItem.isSelectedParent());
      List<NavigationItem> childNavItemList = new ArrayList<>();
      childNavItemList.add(rootPageChildNavItem);
      rootPageNavItem.setChildNavs(childNavItemList);
    }
  }



  /**
   * @param currentPage
   * @param processingPage
   * @param rootPageNavItem
   */
  private void setSelectedPathAndPage(Page currentPage, Page processingPage,
      NavigationItem navItem) {
    if (currentPage.getPath().equals(processingPage.getPath())) {
      selectedPagePath = navItem.getPath();
      selectedPageTitle = (null != rootPageTextOverride && rootPageTextOverride.trim().length() > 0)
          ? navItem.getOverriddenTitle()
          : navItem.getTitle();
      selectedPageMobileTitle = navItem.getTitle();
      navItem.setIsSelectedPath(true);
      navItem.setSelected(SELECTED_TAG);
      navItem.setAnalytics(String.format("%1$s-%2$s", SECNAV_ANALYTICS_TAG, SELECTED_TAG));
      navItem.setHighlightingSubCss(HIGH_LIGHTING_SUB_CSS);
      navItem.setHighlightingCss(HIGH_LIGHTING_CSS);
      navItem.setHref("#");
      navItem.setAriaCurrent(ARIA_CURRENT);
      ariaLabel = getAriaLabel();
    } else {
      navItem.setHref(navItem.getPath());
      navItem.setAnalytics(SECNAV_ANALYTICS_TAG);
    }
  }

  /**
   * Getter to navigation links for single level Secondary Navigation
   * 
   * @return navLinks : List of LinkModel
   */
  public List<NavigationItem> getNavLinks() {
    return new ArrayList<>(navLinks);
  }

  /**
   * Getter for Resource for currentPage
   * 
   * @return resource
   */
  public Resource getResource() {
    return resource;
  }

  /**
   * Getter for includeRoot Boolean
   * 
   * @return includeRoot
   */
  public boolean getIncludeRoot() {
    return includeRoot;
  }

  /**
   * Getter o to only include child and not subchild
   * 
   * @return
   */
  public boolean getOnlyRootPages() {
    return onlyRootPages;
  }

  /**
   * Getter for the Root Page path.
   * 
   * @return
   */
  public String getRootPagePath() {
    return rootPagePath;
  }

  /**
   * Getter to mark the path of page to highlight as selected
   * 
   * @return
   */
  public String getSelectedPagePath() {
    return selectedPagePath;
  }

  /**
   * Getter to receive pagetitle of selected page
   * 
   * @return
   */
  public String getSelectedPageTitle() {
    return selectedPageTitle;
  }

  /**
   * Method to set the properties from current page if secondary nav is configured if not take it
   * from parent page.
   */
  private void setProperties(Resource resource) {

    ValueMap properties = resource.adaptTo(ValueMap.class);
    InheritanceValueMap iProperties = new HierarchyNodeInheritanceValueMap(resource);

    boolean hasProperties = properties == null ? false : true;

    setIncludeRoot(properties, iProperties, hasProperties);
    
    setRootPath(properties, iProperties, hasProperties);

    setRootOverrideText(properties, iProperties, hasProperties);

    setOverviewLabel(properties, iProperties, hasProperties);
  }

  /**
   * Method to set the root path
   * 
   * @param properties
   * @param iProperties
   * @param hasProperties
   */
  private void setRootPath(ValueMap properties, InheritanceValueMap iProperties,
      boolean hasProperties) {
    if (hasProperties && properties.get(SEC_NAV_ROOT_PATH) != null) {
      rootPagePath = properties.get(SEC_NAV_ROOT_PATH, String.class);
    } else if (iProperties.getInherited(SEC_NAV_ROOT_PATH, String.class) != null) {
      rootPagePath = iProperties.getInherited(SEC_NAV_ROOT_PATH, String.class);
    }
  }
  
  /**
   * Method to set the property which determines whether root should be included or not
   * 
   * @param properties
   * @param iProperties
   * @param hasProperties
   */
  private void setIncludeRoot(ValueMap properties, InheritanceValueMap iProperties,
      boolean hasProperties) {
    if (hasProperties && properties.get(SEC_NAV_INCLUDE_ROOT) != null) {
      includeRoot = properties.get(SEC_NAV_INCLUDE_ROOT, Boolean.class);
    } else if (iProperties.getInherited(SEC_NAV_INCLUDE_ROOT, Boolean.class) != null) {
      includeRoot = iProperties.getInherited(SEC_NAV_INCLUDE_ROOT, Boolean.class);
    }
  }


  /**
   * Method to set the property which overrides the root page title
   * 
   * @param properties
   * @param iProperties
   * @param hasProperties
   */
  private void setRootOverrideText(ValueMap properties, InheritanceValueMap iProperties,
      boolean hasProperties) {
    if (hasProperties && properties.get(SEC_NAV_ROOT_PAGE_TEXT_OVERRIDE) != null) {
      rootPageTextOverride = properties.get(SEC_NAV_ROOT_PAGE_TEXT_OVERRIDE, String.class);
    } else if (iProperties.getInherited(SEC_NAV_ROOT_PAGE_TEXT_OVERRIDE, String.class) != null) {
      rootPageTextOverride =
          iProperties.getInherited(SEC_NAV_ROOT_PAGE_TEXT_OVERRIDE, String.class);
    }
  }

  /**
   * Method to set the overview label which will be appended along with the first item.
   * 
   * @param properties
   * @param iProperties
   * @param hasProperties
   */
  private void setOverviewLabel(ValueMap properties, InheritanceValueMap iProperties,
      boolean hasProperties) {
    if (hasProperties && properties.get(SEC_NAV_OVERVIEW_LABEL) != null) {
      overViewLabel = properties.get(SEC_NAV_OVERVIEW_LABEL, String.class);
    } else if (iProperties.getInherited(SEC_NAV_OVERVIEW_LABEL, String.class) != null) {
      overViewLabel = iProperties.getInherited(SEC_NAV_OVERVIEW_LABEL, String.class);
    }
  }

  /**
   * @return the selectedPageMobileTitle
   */
  public String getSelectedPageMobileTitle() {
    return selectedPageMobileTitle;
  }

  /**
   * @param selectedPageMobileTitle the selectedPageMobileTitle to set
   */
  public void setSelectedPageMobileTitle(String selectedPageMobileTitle) {
    this.selectedPageMobileTitle = selectedPageMobileTitle;
  }

  /**
   * @return the selectedTitle
   */
  public String getShowDevice() {
    if (selectedPageMobileTitle != null) {
      return selectedPageMobileTitle;
    }
    return selectedPageTitle;
  }

  public String getStyleClass() {
    return styleClass;
  }
  
  public String getSubMenuClass() {
    return subMenuClass;
  }
  
  public String getToggleClass() {
    return toggleClass;
  }
  
  public String getDisplayVariation() {
    return displayVariation;
  }
  
  public void setDisplayVariation(String displayVariation) {
    this.displayVariation = displayVariation;
  }

  public String getAriaLabel() {
    String language = WCMUseUtil.getCurrentLanguage(currentPage);
    if(language!= "" && language.equalsIgnoreCase(ENGLISH)){
      ariaLabel =  ARIA_LABEL_EN;
    }else if(language!= "" && language.equalsIgnoreCase(ESPANOL)){
      ariaLabel =  ARIA_LABEL_ES;
    }
    return ariaLabel;
  }
}