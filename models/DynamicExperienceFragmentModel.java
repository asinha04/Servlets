package org.kp.foundation.core.models;

import com.adobe.cq.wcm.core.components.models.ExperienceFragment;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.WCMMode;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Source;
import org.apache.sling.models.annotations.Via;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.via.ResourceSuperType;
import org.kp.foundation.core.enums.RegionXFEnum;
import org.kp.foundation.core.utils.GenericUtil;
import org.kp.foundation.core.utils.LinkUtil;
import org.kp.foundation.core.utils.WCMUseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.kp.foundation.core.constants.GlobalConstants.JCR_CONTENT;

@Model(adaptables = { SlingHttpServletRequest.class,
		Resource.class }, adapters = ExperienceFragment.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL, resourceType = DynamicExperienceFragmentModel.RESOURCE_TYPE)
public class DynamicExperienceFragmentModel extends BaseModel implements ExperienceFragment {

	private static final Logger log = LoggerFactory.getLogger(DynamicExperienceFragmentModel.class);
	public static final String RESOURCE_TYPE = "kporg/kp-foundation/components/content/dynamic-experience-fragment";
	private final static String PROD_RUNMODE_VALUE = "prod";
	private static final String EDITABLE_TEMPLATE = "structure";
	private static final String HYPHEN = "-";
	private String relativeLocalizedFragmentVariationPath;
	private String authoredFragmentPath;
	private String finalXFVariationPath;
	private String currentRegionName;
	private String currentPageLanguage;
	private String xfPathWithoutJcrContent;
	private String xfPathWithoutVariation;

	@Inject
	@Named("view")
	@Via("resource")
	private String view;

	@ScriptVariable
	@Via("request")
	private Page currentPage;

	@Inject
	SlingHttpServletRequest request;

	@Inject
	@Source("sling-object")
	private ResourceResolver resourceResolver;

	@Self
	@Via(type = ResourceSuperType.class)
	private ExperienceFragment experienceFragment;
	
	private boolean editableTemplateAndEditMode = false;

	/**
	 * Initialize experience fragment.
	 */
	@PostConstruct
	public void init() {
		try {
			authoredFragmentPath = experienceFragment == null ? null
					: experienceFragment.getLocalizedFragmentVariationPath();
			if (StringUtils.isNotBlank(authoredFragmentPath)) {
				if (isEditableTemplate()) {
					finalXFVariationPath = authoredFragmentPath;
				} else {
					finalXFVariationPath = authoredFragmentPath;
					currentRegionName = WCMUseUtil.getCurrentRegion(currentPage);
					currentPageLanguage = WCMUseUtil.getCurrentLanguage(currentPage);
					xfPathWithoutJcrContent = StringUtils.replaceIgnoreCase(authoredFragmentPath, JCR_CONTENT, "");
					xfPathWithoutVariation = xfPathWithoutJcrContent.substring(0,
							xfPathWithoutJcrContent.lastIndexOf("/"));
					String authoredXFVariation = xfPathWithoutJcrContent
							.substring(xfPathWithoutJcrContent.lastIndexOf("/") + 1);
					if (StringUtils.isNotBlank(authoredXFVariation)) {
						if (authoredXFVariation.equalsIgnoreCase("master")
								|| authoredXFVariation.contains(experienceFragment.getName())) {
							if (StringUtils.isNotBlank(currentRegionName) && StringUtils.isNotBlank(currentPageLanguage)
									&& StringUtils.isNotBlank(xfPathWithoutJcrContent)
									&& StringUtils.isNotBlank(xfPathWithoutVariation))
								buildXFFinalPath();
						}
					}
				}
			}
		} catch (Exception e) {
			log.error("Error while getting variation of the Experience Fragment", e);
		}
	}

	@Override
	public String getLocalizedFragmentVariationPath() {
		return finalXFVariationPath;
	}

	@Override
	public String getName() {
		return experienceFragment.getName();
	}

	public boolean isEmbed() {
		return StringUtils.containsIgnoreCase(view, "embed");
	}

	public boolean isOnView() {
		return StringUtils.containsIgnoreCase(view, "onview");
	}

	public boolean isOnload() {
		return StringUtils.containsIgnoreCase(view, "onload");
	}

	public boolean isSDITag() {
		return StringUtils.containsIgnoreCase(view, "sdiTag");
	}

	public String getView() {
		return view;
	}

	public boolean isProdRunMode() {
		return GenericUtil.isRunModeAvailable(PROD_RUNMODE_VALUE);
	}

	public String getRelativeLocalizedFragmentVariationPath() {
		relativeLocalizedFragmentVariationPath = StringUtils.replaceIgnoreCase(finalXFVariationPath, JCR_CONTENT, "");
		String relativeLocalizedFragmentShortendUrl = LinkUtil.getRelativeURL(request, relativeLocalizedFragmentVariationPath);
        return relativeLocalizedFragmentShortendUrl;
	}

	/**
	 * Checks if the resource exists at the given path. Basically, this method
	 * checks if language and regional variation xf exists.
	 *
	 * @param path the resource path
	 * @return {true} if the resource exists, {false} otherwise
	 */
	private boolean checkXFExists(String xfPath) throws Exception {
		final Resource resource = resourceResolver.getResource(xfPath);
		if (resource != null) {
			return true;
		}
		return false;
	}

	/**
	 *
	 * @return {true} if current page is an editable template; {false} otherwise
	 *         Basically it returns {true} for editable template and {false} for
	 *         pages/xf
	 */
	private boolean isEditableTemplate() throws Exception {
		final Resource currentPageRes = this.resourceResolver == null ? null
				: this.resourceResolver.getResource(this.currentPage.getPath());
		final String pageName = currentPageRes == null ? "" : currentPageRes.getName();
		return EDITABLE_TEMPLATE.equals(pageName);
	}
		
	/**
	 * This method creates the final xf path taking region and language
	 * into consideration. If xf variation doesn't exist, then it would fall back to
	 * authored xf path in the dynamic XF component.
	 * 
	 * @return the final XF variation path. This would be w.r.t region and language
	 *         variation.
	 */
	private String buildXFFinalPath() throws Exception {
		String xfVariation;
		String regionAbbrevation = RegionXFEnum.getRegionCodeByRegionName(currentRegionName).getCode();
		if (StringUtils.isNotBlank(regionAbbrevation) && regionAbbrevation.equals("master")) {
			xfVariation = "master";
		} else {
			xfVariation = experienceFragment.getName() + HYPHEN + regionAbbrevation;
		}
		String fullXFvariationXFPath = xfPathWithoutVariation + "/" + xfVariation;

		if (currentPageLanguage.equalsIgnoreCase("ES")) {
			fullXFvariationXFPath = fullXFvariationXFPath.replace("/en/", "/es/").concat("-es");
		}

		if (checkXFExists(fullXFvariationXFPath)) {
			finalXFVariationPath = fullXFvariationXFPath + JCR_CONTENT;
		}
		
		return finalXFVariationPath;
	}
	
	/**
	 * @return {true} if current page is an editable template and wcmmode is edit.
	 * @throws Exception
	 */
	public boolean isEditableTemplateAndEditMode() throws Exception {
		if (isEditableTemplate()) {
			String currentWcmMode = WCMMode.fromRequest(request).toString();
			if (StringUtils.isNotBlank(currentWcmMode) && currentWcmMode.equals("EDIT")) {
				editableTemplateAndEditMode = true;
			}
		}
		return editableTemplateAndEditMode;
	}
}