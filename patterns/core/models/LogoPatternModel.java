package org.kp.patterns.core.models;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Via;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.kp.foundation.core.constants.RegionPicker;
import org.kp.foundation.core.models.GenericPageModel;
import org.kp.foundation.core.utils.GenericUtil;
import org.kp.foundation.core.models.BaseModel;

import com.day.cq.wcm.api.Page;

@Model(adaptables = { SlingHttpServletRequest.class,Resource.class}, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class LogoPatternModel extends BaseModel {

	// Default Logo Path
	private static final String DEFAULT_LOGO_PATH = "/etc.clientlibs/settings/wcm/designs/kporg/kp-foundation/clientlib-modules/styleguide/resources/assets/images/logo.svg";
	// Default Logo Width
	private static final String DEFAULT_LOGO_WIDTH = "300";
	// Default Logo Height
	private static final String DEFAULT_LOGO_HEIGHT = "34";
	// Default Mobile Logo Path
	private static final String MOBILE_LOGO_PATH = "/etc.clientlibs/settings/wcm/designs/kporg/kp-foundation/clientlib-modules/styleguide/resources/assets/images/kp-icon-mini.svg";
    // Mobile Logo Width
	private static final String MOBILE_LOGO_WIDTH = "40";
	// Mobile Logo Height
	private static final String MOBILE_LOGO_HEIGHT = "37";
	// Small Desktop Logo Path
	private static final String SMALL_DESKTOP_LOGO_PATH = "/etc.clientlibs/settings/wcm/designs/kporg/kp-foundation/clientlib-modules/styleguide/resources/assets/images/KPLogoIconBlue.svg";
	// Small Desktop Logo Width
	private static final String SMALL_DESKTOP_LOGO_WIDTH = "79";
	// Small Desktop Logo Height
	private static final String SMALL_DESKTOP_LOGO_HEIGHT = "72";
	// Default Alt Text
	private static final String DEFAULT_ALT_TEXT = "KP Logo";
	
	@Inject
	private Page currentPage;

	@Inject
	@Via("resource")
	@Default(booleanValues = false)
	private boolean disableLogo;

	@Inject
	@Via("resource")
	@Default(booleanValues = false)
	private boolean disableLink;
	
	@Self
	GenericPageModel pageModel;
	
	@Inject
	@Via("resource")
	@Default(values = DEFAULT_LOGO_PATH)
	@Named("fileReference")
	private String logoPath;
	
	@Inject
	@Via("resource")
	@Default(values = MOBILE_LOGO_PATH)
	@Named("mobileFileReference")
	private String mobileLogoPath;
	
	@Inject
	@Via("resource")
	@Default(values = SMALL_DESKTOP_LOGO_PATH)
	@Named("smallDesktopFileReference")
	private String smallDesktopLogoPath;
	
	@Inject
	@Via("resource")
	@Default(values = DEFAULT_ALT_TEXT)
	private String alt;
	
	@Inject
	SlingHttpServletRequest request;
	
	private Map<String, String> srcAttributeMap = new HashMap<>();
	private String homePageUrl;


	/**
	 *
	 * @return the {@link #logoPath} built from
	 *         {@link #buildLogoPath(Resource, String)}
	 */
	public Map<String, String> getLogoPath() {
		srcAttributeMap = GenericUtil.createSourceAttribute(logoPath, pageModel, request, DEFAULT_LOGO_WIDTH, DEFAULT_LOGO_HEIGHT);
		return srcAttributeMap;
	}

	/**
	 *
	 * @return the {@link #logoPath} built from
	 *         {@link #buildLogoPath(Resource, String)}
	 */
	public Map<String, String> getSmallDesktopLogoPath() {
		srcAttributeMap = GenericUtil.createSourceAttribute(smallDesktopLogoPath, pageModel, request, SMALL_DESKTOP_LOGO_WIDTH, SMALL_DESKTOP_LOGO_HEIGHT);
		return srcAttributeMap;
	}

	/**
	 *
	 * @return the {@link #mobileLogoPath} built from
	 *         {@link #buildLogoPath(Resource, String)}
	 */
	public Map<String, String> getMobileLogoPath() {
		srcAttributeMap = GenericUtil.createSourceAttribute(mobileLogoPath, pageModel, request, MOBILE_LOGO_WIDTH, MOBILE_LOGO_HEIGHT) ;
		return srcAttributeMap;
	}
	
	/**
	 *
	 * @return {@link #homePageUrl} built from method {@link #buildHomePageUrl()}
	 */
	public String getHomePageUrl() {
		buildHomePageUrl();
		if (disableLink) {
			homePageUrl = new String();
		} 
		return homePageUrl;
	}
	
	/**
	 * {@link #homePageUrl} will be the respective region in which they're browsing.
	 * If current page absolute level is less than region level then default to
	 * current page
	 */
	private void buildHomePageUrl() {
		if (null != currentPage && null != currentPage.getAbsoluteParent(RegionPicker.CUR_REGION_ROOT_LEVEL)) {
			Page rootPage = currentPage.getAbsoluteParent(RegionPicker.CUR_REGION_ROOT_LEVEL);
			homePageUrl = rootPage.getPath();
		} else {
			homePageUrl = currentPage.getPath();
		}
	}

	public boolean isDisableLink() {
		return disableLink;
	}

	public boolean isDisableLogo() {
		return disableLogo;
	}

	public String getAlt() {
		if(null == alt || StringUtils.isEmpty(alt)) {
            alt = DEFAULT_ALT_TEXT;
        }
		return alt;
	}
}