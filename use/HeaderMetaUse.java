package org.kp.foundation.core.use;

import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.kp.foundation.core.constants.GlobalConstants;
import org.kp.foundation.core.constants.RegionPicker;
import org.kp.foundation.core.enums.LanguageLocaleEnum;
import org.kp.foundation.core.utils.LinkUtil;
import org.kp.foundation.core.utils.PropertyInheritedUtil;
import org.kp.foundation.core.utils.WCMUseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.day.cq.wcm.api.Page;

/**
 * The HeaderMetaUse class provides the logic to generate meta tags for each
 * page.
 * 
 * @author Krishan Rathi
 *
 */

public class HeaderMetaUse extends BaseWCMUse {

    private static final Logger LOGGER = LoggerFactory.getLogger(HeaderMetaUse.class);

    private static final String DATA_REGION_HAWAII_NMP = "hawaii/nmp";
    private static final String DATA_REGION_HAWAII_MAUI = "hawaii/maui";
    private static final String DATA_REGION_EMPTY = "";
    private static final String SECURE_PAGE = "/secure/";
    public static final int CUR_SECURE_ROOT_LEVEL = 5;
    public static final int CUR_NON_SECURE_ROOT_LEVEL = 4;
    private static final String OTHER = "other";
    private static final String CANONICAL_LINK = "canonicalLink";
    private static final String REGION = "region";
    private static final String CATEGORY = "category";
    private static final String PUBLICATION_TYPE = "publicationtype";
    private static final String ARTICLE_TYPE = "articletype";
    private static final String TOPIC = "topic";
    private static final String CPT_CODE = "cptCode";
    private static final String ICD10_CODE = "icd10Code";
    private static final String MMR_REMINDERS = "mmrReminders";
    private static final String MMR_ALLERGIES = "mmrAllergies";
    private static final String MMR_IMMUNIZATIONS = "mmrImmunizations";
    private static final String LINE_OF_BUSINESS = "lob";
    private static final String ORG_ENTITIES = "org_entities";
    private static final String IMC_PROGRAMS = "imc_programs";
    private static final String AGE_RANGE = "agerange";
    private static final String SEX = "sex";
    private static final String STATE = "state";
    private static final String ISLAND = "island";
    private static final String KEYWORDS = "keywords";
    private Map<String,String> singlePagePropertiesMap = new LinkedHashMap<String, String>();
    private Map<String, String[]> mutliPagePropertiesMap = new LinkedHashMap<String, String[]>();
    private static final String KP_REGION = "kp:region";
    private static final String KP_CATEGORY = "kp:kp-category";
    private static final String KP_PUBLICATION_TYPE = "kp:publication-type";
    private static final String KP_CPT_CODE = "kp:cpt-code";
    private static final String KP_ICD10_CODE = "kp:icd10-code";
    private static final String KP_MMR_ALLERGIES = "kp:mmr-allergies";
    private static final String KP_MMR_IMMUNIZATIONS = "kp:mmr-immunizations";
    private static final String KP_SEX = "kp:sex";
    public static final String CANONICAL_URL = "canonicalUrl";
    public static final String KP_HEALTH_ARTICLE_TYPE = "kp:health-article-type";
    public static final String KP_HEALTH_TOPIC = "kp:health-topic";
    public static final String KP_MMR_REMINDERS = "kp:mmr-reminders";
    public static final String KP_LINE_OF_BUSINESS = "kp:line-of-business";
    public static final String KP_ORGANIZATIONAL_ENTITIES = "kp:organizational-entities";
    public static final String KP_IMC_PROGRAMS = "kp:imc-programs";
    public static final String KP_AGE_RANGE = "kp:age-range";
    public static final String KP_STATE = "kp:state";
    public static final String KP_ISLAND = "kp:island";
    public static final String JCR_DESCRIPTION = "jcr:description";
    Map<String, String> singleValueMap = new LinkedHashMap<String, String>(); 
    Map<String, String> multiValueMap = new LinkedHashMap<String, String>();
    private String canonicalLink;
    ValueMap contentValueMap;
    private String colorTheme;

	/**
	 * Activate method that Sightly executes
	 */
	@Override
	public void activate() throws Exception {
		contentValueMap = getResource().adaptTo(ValueMap.class);
		canonicalLink = getProperties().get(CANONICAL_LINK, "");
		singlePropertiesMap();
		multiPropertiesMap();
		createSinglePagePropertiesMap();
		createMultiPagePropertiesMap();
		colorTheme = PropertyInheritedUtil. getProperty (getCurrentPage().getContentResource(),GlobalConstants.COLOR_THEME);
	}

	/**
	 * This method iterates over the single-valued property map which is
	 * initialized in singlePropertiesMap method and creates a map of page
	 * properties. HTL iterates over this map and displays meta tag.
	 */
	public void createSinglePagePropertiesMap() {
		for (Map.Entry<String, String> entry : singleValueMap.entrySet()) {
			if (StringUtils.isNotBlank(getSinglePageProperty(entry.getValue()))) {
				singlePagePropertiesMap.put(entry.getKey(), getSinglePageProperty(entry.getValue()));
			}
		}
	}

	/**
	 * This method iterates over the multi-valued property map which is
	 * initialized in multiPropertiesMap method and creates a map of page
	 * properties. HTL iterates over this map and displays meta tag.
	 */
	public void createMultiPagePropertiesMap() {
		for (Map.Entry<String, String> entry : multiValueMap.entrySet()) {
			String[] multiPageProperties = getMultiPageProperty(entry.getValue());
			if (multiPageProperties != null && multiPageProperties.length > 0) {
				for (String multiPageProperty : multiPageProperties) {
					if (StringUtils.isNotBlank(multiPageProperty))
						mutliPagePropertiesMap.put(entry.getKey(), getMultiPageProperty(entry.getValue()));
				}
			}
		}
	}

	/**
	 * @return the node values of the property.
	 */
	private String getSinglePageProperty(String singlePageProperty) {
		String singleProperty = null;
		singleProperty = contentValueMap.get(singlePageProperty, StringUtils.EMPTY);
		return singleProperty;
	}

	/**
	 * @return the node value of multi valued property.
	 */
	private String[] getMultiPageProperty(String multiPageProperty) {
		String[] multiValueProperty = getPageProperties().get(multiPageProperty, String[].class);
		return multiValueProperty;
	}

	/**
	 * @return the singlePagePropertiesMap to the HTL.
	 */
	public Map<String, String> getSinglePagePropertiesMap() {
		return singlePagePropertiesMap;
	}

	/**
	 * @return the mutliPagePropertiesMap to the HTL.
	 */
	public Map<String, String[]> getMutliPagePropertiesMap() {
		return mutliPagePropertiesMap;
	}

	/**
	 * Creates map of single valued properties which would be shown in meta
	 * tags. If new property has to be shown in meta tag, then just add a
	 * property here.
	 */
	public void singlePropertiesMap() {
		singleValueMap.put(KP_REGION, REGION);
		singleValueMap.put(KEYWORDS, KEYWORDS);
		singleValueMap.put(KP_CATEGORY, CATEGORY);
		singleValueMap.put(KP_PUBLICATION_TYPE, PUBLICATION_TYPE);
		singleValueMap.put(KP_CPT_CODE, CPT_CODE);
		singleValueMap.put(KP_ICD10_CODE, ICD10_CODE);
		singleValueMap.put(KP_MMR_ALLERGIES, MMR_ALLERGIES);
		singleValueMap.put(KP_SEX, SEX);
	}

	/**
	 * Creates map of multi valued properties which would be shown in meta tags.
	 * If new property has to be shown in meta tag, then just add a property
	 * here.
	 */
	public void multiPropertiesMap() {
		multiValueMap.put(KP_HEALTH_ARTICLE_TYPE, ARTICLE_TYPE);
		multiValueMap.put(KP_HEALTH_TOPIC, TOPIC);
		multiValueMap.put(KP_MMR_REMINDERS, MMR_REMINDERS);
		multiValueMap.put(KP_MMR_IMMUNIZATIONS, MMR_IMMUNIZATIONS);
		multiValueMap.put(KP_LINE_OF_BUSINESS, LINE_OF_BUSINESS);
		multiValueMap.put(KP_ORGANIZATIONAL_ENTITIES, ORG_ENTITIES);
		multiValueMap.put(KP_IMC_PROGRAMS, IMC_PROGRAMS);
		multiValueMap.put(KP_AGE_RANGE, AGE_RANGE);
		multiValueMap.put(KP_STATE, STATE);
		multiValueMap.put(KP_ISLAND, ISLAND);
	}

    public String getCannonicalURL() {
        return adjustUrlForRegionSEO(contentValueMap.get(GlobalConstants.CANONICAL_URL, StringUtils.EMPTY),
                LinkUtil.getAbsoluteURL(getRequest(), LinkUtil.adjustFDLProviderPath(getCurrentPage().getPath()), getCurrentPage()));
    }

    /**
     * Check if the page property CanonicalURL is set to national or regional (at the SEO tab) and adjust the URL.
     * 
     * @param externalURL
     * @param canonicalURL
     * @return adjusted canonicalURL
     */
	private String adjustUrlForRegionSEO(String externalURL, String canonicalURL) {
		if (GlobalConstants.NATIONAL.equalsIgnoreCase(externalURL) || GlobalConstants.REGIONAL.equalsIgnoreCase(externalURL)) {
			if ((StringUtils.isNotEmpty(externalURL) && GlobalConstants.NATIONAL.equalsIgnoreCase(externalURL))
					|| (!StringUtils.isNotEmpty(externalURL) && GlobalConstants.NATIONAL.equalsIgnoreCase(
							getCurrentPage().getAbsoluteParent(RegionPicker.CUR_REGION_ROOT_LEVEL).getName()))) {
				String currentRegion = getCurrentPage().getAbsoluteParent(RegionPicker.CUR_REGION_ROOT_LEVEL).getName();
				if (StringUtils.isNotEmpty(currentRegion) && StringUtils.isNotEmpty(canonicalURL)) {
					LOGGER.debug("Removing the region from the URL.");
					canonicalURL = canonicalURL.replaceAll('/' + currentRegion, "");
				}
			}
		} else if (OTHER.equalsIgnoreCase(externalURL)) {
			canonicalURL = LinkUtil.getAbsoluteURL(getRequest(), canonicalLink, getCurrentPage());
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("canonicalURL {}.", canonicalURL);
		}
		return canonicalURL;
	}

    public String getLocale() {
        return WCMUseUtil.getHeaderLocaleOverride(getResource(), getCurrentPage());
    }

    public String getLocaleForLang() {
        String language = WCMUseUtil.getCurrentLanguage(getCurrentPage());
        if (StringUtils.isNotEmpty(language) && language.equalsIgnoreCase(LanguageLocaleEnum.EN.name())) {
            return LanguageLocaleEnum.EN.getLabel();
        } else if (StringUtils.isNotEmpty(language) && language.equalsIgnoreCase(LanguageLocaleEnum.ES.name())) {
            return LanguageLocaleEnum.ES.getLabel();
        } else {
            return LanguageLocaleEnum.EN.getLabel();
        }
    }

    public String getLocaleForLanguage() {
        String language = WCMUseUtil.getCurrentLanguage(getCurrentPage());
        if (StringUtils.isNotEmpty(language) && language.equalsIgnoreCase(LanguageLocaleEnum.EN.name())) {
            return LanguageLocaleEnum.EN.getLabel();
        } else if (StringUtils.isNotEmpty(language) && language.equalsIgnoreCase(LanguageLocaleEnum.ES.name())) {
            return LanguageLocaleEnum.ES.getLabel();
        } else {
            return LanguageLocaleEnum.EN.getLabel();
        }
    }

    public String getAssetType() {
        Resource res = getResource();
        return WCMUseUtil.getAssetType(res);
    }

    public String getPageTitle(){
    	Resource res = getCurrentPage().getContentResource();
        String jcrTitle = getCurrentPage().getTitle();
        String pageTitleSuffix = PropertyInheritedUtil. getProperty (res,GlobalConstants.PAGE_TITLE_SUFFIX);
        //QC17429 - For September release, it has been decided to show the page title for mobile using jcr:title property
        // instead of pageTitle property.
        //Later this may change based on new content structure.
        String pageTitle = contentValueMap.get(GlobalConstants.PAGE_TITLE, StringUtils.EMPTY);
        if(StringUtils.isEmpty(pageTitle)){
            pageTitle = jcrTitle;
        }
        if(StringUtils.isNotEmpty(pageTitleSuffix) && StringUtils.isNotEmpty(pageTitle) ){
            pageTitle = pageTitle + pageTitleSuffix;
        }
        return pageTitle;     
    }
    /**
     * Return the page title for Mobile view.
     * @return
     */
	
   public String getMobilePageTitle(){
      Page rootPage;
      if(getCurrentPage().getPath().contains(SECURE_PAGE))  {
            rootPage = getCurrentPage().getAbsoluteParent(CUR_SECURE_ROOT_LEVEL);
      } else {
          rootPage = getCurrentPage().getAbsoluteParent(CUR_NON_SECURE_ROOT_LEVEL);
      }
       if(null == rootPage) {
           rootPage =getCurrentPage();
       }
      return rootPage.getTitle();
  }
   
	/**
	 * @return page description
	 */
	public String getPageDescription() {
		String pageDescription = contentValueMap.get(JCR_DESCRIPTION, StringUtils.EMPTY);
		return pageDescription;
	}

    public String getSearchable() {
        Resource res = getResource();
        return WCMUseUtil.getSearchable(res);
    }


    /**
     * @return string to identify the NMP region, i.e. "hawaii/maui"
     */
    public String getDataRegion() {
        String canonicalURL = getCannonicalURL();
        if (StringUtils.isNotEmpty(canonicalURL) && canonicalURL.contains(DATA_REGION_HAWAII_NMP)) {
            return DATA_REGION_HAWAII_MAUI;
        }
        return DATA_REGION_EMPTY;
    }
    
    /**
     * 
     * @return String array of Prefetch links
     */
    public String[] getPrefetchLinks() {
        String[] prefetchLinks = getPageProperties().get(GlobalConstants.PREFETCHLINKS, String[].class);
        return prefetchLinks;
    }

	/**
	 * @return the colorTheme
	 */
	public String getColorTheme() {
		return colorTheme;
	}
}