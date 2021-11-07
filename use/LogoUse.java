package org.kp.foundation.core.use;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.kp.foundation.core.constants.GlobalConstants;
import org.kp.foundation.core.constants.RegionPicker;
import org.kp.foundation.core.utils.PropertyInheritedUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.wcm.api.Page;

public class LogoUse extends WCMUsePojo {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogoUse.class);

    private static final String ALT_TEXT = "alt";
    private static final String FILE_REFERENCE = "fileReference";
    private static final String MOBILE_REFERENCE = "mobileFileReference";
    private static final String SMALL_DESKTOP_FILE_REFERENCE = "smallDesktopFileReference";
    private static final String DISABLE_LINK = "disableLink";
    private static final String DISABLE_LOGO = "disableLogo";

    private String logoPath;
    private String altText;
    private String homePageUrl = null;
    private String mobileLogoPath;
    private String smallDesktopLogoPath;
    private boolean disableLink;
    private String disableLogo;

    // Default Logo Path
    private static final String DEFAULT_LOGO_PATH = "/etc.clientlibs/settings/wcm/designs/kporg/kp-foundation/clientlib-modules/styleguide/resources/assets/images/logo.svg";
    // Default Alt Text
    private static final String DEFAULT_ALT_TEXT = "KP Logo";
    // Default Logo Path
    private static final String MOBILE_LOGO_PATH = "/etc.clientlibs/settings/wcm/designs/kporg/kp-foundation/clientlib-modules/styleguide/resources/assets/images/kp-icon-mini.svg";
    // Small Desktop Logo Path
    private static final String SMALL_DESKTOP_LOGO_PATH = "/etc.clientlibs/settings/wcm/designs/kporg/kp-foundation/clientlib-modules/styleguide/resources/assets/images/KPLogoIconBlue.svg";

    /**
     * Activate method that Sightly executes
     */

    public void activate() throws Exception {

        Resource resource = getResource();
        LOGGER.debug("Node being looked at---> {}", resource.getPath());
        String propertyName = PropertyInheritedUtil.getProperty(resource, FILE_REFERENCE);
        String mobilePropertyName = PropertyInheritedUtil.getProperty(resource, MOBILE_REFERENCE);
        String smallDesktopPropertyName = PropertyInheritedUtil.getProperty(resource, SMALL_DESKTOP_FILE_REFERENCE);
        buildLogoPath(resource, propertyName, mobilePropertyName,smallDesktopPropertyName);
        buildAltText(resource);
        findDisableLink(resource);
        disableLogo = getProperties().get(DISABLE_LOGO,GlobalConstants.FALSE);
        buildHomePageUrl();
    }

    /**
     * {@link #homePageUrl} will be the respective region in which they're browsing. If current page absolute level is less than region level then default to current page
     */
    private void buildHomePageUrl() {
        Page rootPage = getCurrentPage().getAbsoluteParent(RegionPicker.CUR_REGION_ROOT_LEVEL);
        if(rootPage!=null){
            homePageUrl =  rootPage.getPath();
        } else {
            homePageUrl = getCurrentPage().getPath();
        }
    }

    /**
     * Find {@link #disableLink Disable Logo link} from dialog or Default value
     *
     * @param resource
     */

    private void findDisableLink(Resource resource) {
        disableLink = "true".equals(PropertyInheritedUtil.getProperty(resource, DISABLE_LINK));
    }
   

    /**
     * Build {@link #altText} from dialog or default value
     *
     * @param resource
     */
    private void buildAltText(Resource resource) {
        altText = PropertyInheritedUtil.getProperty(resource, ALT_TEXT);
        if (StringUtils.isEmpty(altText)) {
            altText = DEFAULT_ALT_TEXT;
        }
    }

    /**
     * Build {@link #logoPath} from Dialog properties or default value
     *
     * @param resource
     * @param propertyName
     */
    private void buildLogoPath(Resource resource, String propertyName, String mobilePropertyName, String smallDesktopPropertyName) {
        LOGGER.debug("Logo Path {}.",propertyName);
        if (StringUtils.isEmpty(propertyName)) {
            logoPath = DEFAULT_LOGO_PATH;
        }
        else {
            logoPath = propertyName;
        }
        if (StringUtils.isEmpty(mobilePropertyName)) {
            mobileLogoPath = MOBILE_LOGO_PATH;
        }
        else {
            mobileLogoPath = mobilePropertyName;
        }
        if (StringUtils.isEmpty(smallDesktopPropertyName)) {
        	smallDesktopLogoPath = SMALL_DESKTOP_LOGO_PATH;
        }
        else {
        	smallDesktopLogoPath = smallDesktopPropertyName;
        }
       
    }

    /**
     *
     * @return the {@link #logoPath} built from {@link #buildLogoPath(Resource, String)}
     */
    public String getLogoPath() {
        return logoPath;
    }
    
    /**
    *
    * @return the {@link #logoPath} built from {@link #buildLogoPath(Resource, String)}
    */
   public String getSmallDesktopLogoPath() {
       return smallDesktopLogoPath;
   }

    /**
     *
     * @return the {@link #mobileLogoPath} built from {@link #buildLogoPath(Resource, String)}
     */
    public String getMobileLogoPath() {
        return mobileLogoPath;
    }

    /**
     *
     * @return the {@link #altText} built from {@link #buildAltText(Resource)}
     */
    public String getAltText() {
        return altText;
    }

    /**
     *
     * @return {@link #homePageUrl} built from method {@link #buildHomePageUrl()}
     */
    public String getHomePageUrl() {
        return homePageUrl;

    }

    public boolean isDisableLink(){
        return disableLink;
    }

    public String isDisableLogo(){
        return disableLogo;
    }
}