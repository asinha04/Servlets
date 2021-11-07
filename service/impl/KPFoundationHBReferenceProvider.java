package org.kp.foundation.core.service.impl;

import com.day.cq.wcm.api.NameConstants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.reference.Reference;
import com.day.cq.wcm.api.reference.ReferenceProvider;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.kp.foundation.core.service.SystemUserResources;
import org.kp.web.envconfig.core.util.JsonSettingsOsgiUtil;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.List;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import java.util.Calendar;
import java.util.Date;

import static com.day.cq.commons.jcr.JcrConstants.JCR_CONTENT;
import static com.day.cq.commons.jcr.JcrConstants.JCR_LASTMODIFIED;
import org.osgi.framework.Constants;

/**
 * @author Mohan Joshi
 * 
 * This class finds the references for a given resource.Basically, while
 * activating the page, it would show the list of all the referenced
 * content so that required content can be activated together.
 * 
 */
@Component(property=Constants.SERVICE_RANKING+":Integer=5000", immediate = true, enabled = true, service = ReferenceProvider.class)
public class KPFoundationHBReferenceProvider implements ReferenceProvider {

	private static final Logger LOGGER = LoggerFactory.getLogger(KPFoundationHBReferenceProvider.class);

	private static final String HANDLEBAR_CLIENTLIB_BASEPATH = "settings/wcm/designs/kporg/generated/handlebars/";
	private static final String CLIENT_LIB_ID = "clientlib_id";
	private static final String SLASH = "/";
	private static final String JS = "js";
	private static final String JS_DOT_TXT = "js.txt";
	private static final String ASSET = "asset";
	private String featureName;
	private String appClientlibPath;
	private long jsTxtlastModified;
	private Date jsLastUpdated;
	
	@org.osgi.service.component.annotations.Reference
	private SystemUserResources systemUserResources;

	/**
	 * Logic for retrieving referenced Handlebars resource and creation of reference
	 * object for the same, and adding each reference object to the
	 * hbsReferenceList.
	 */
	@Override
	public List<Reference> findReferences(Resource resource) {

		List<Reference> hbsReferenceList = new ArrayList<>();
		String primaryNodeType = null;
		try {			
			PageManager pMgr = resource.getResourceResolver().adaptTo(PageManager.class);
			Page page = pMgr.getContainingPage(resource);	
			LOGGER.debug("KPFoundationHBReferenceProvider called to find page references -----> "+ page);
			if (null != page) {
				LOGGER.debug("KPFoundationHBReferenceProvider page path -----> "+ page.getPath());
				featureName = JsonSettingsOsgiUtil.getAppNameFromPage(page);
				LOGGER.debug("KPFoundationHBReferenceProvider feature name -----> "+ featureName);
				primaryNodeType = page.getProperties().get("jcr:primaryType", String.class);
				if (!NameConstants.NT_PAGE.equals(primaryNodeType)) {
					ValueMap valueMap = resource.getValueMap();
					String clientlibID = (String) valueMap.get(CLIENT_LIB_ID);
					LOGGER.debug("KPFoundationHBReferenceProvider page clientlibID -----> "+ clientlibID);
					if (StringUtils.isNotBlank(featureName) && !isNull(clientlibID)) {
						ResourceResolver resourceResolver = resource.getResourceResolver();						
						appClientlibPath = HANDLEBAR_CLIENTLIB_BASEPATH + featureName;
						
						Resource resourceRoot = resourceResolver.getResource(appClientlibPath + SLASH + clientlibID);
						
						LOGGER.debug("KPFoundationHBReferenceProvider resourceRoot -----> "+ resourceRoot);
						
						Resource resourceJsTxt = resourceResolver
								.getResource(appClientlibPath + SLASH + clientlibID + SLASH + JS_DOT_TXT);
						
						LOGGER.debug("KPFoundationHBReferenceProvider resourceJsTxt -----> "+ resourceJsTxt);
						
						Resource resourceJS = resourceResolver
								.getResource(appClientlibPath + SLASH + clientlibID + SLASH + JS);
						
						LOGGER.debug("KPFoundationHBReferenceProvider resourceJS -----> "+ resourceJS);

						if (nonNull(resourceRoot) && nonNull(resourceJsTxt) && nonNull(resourceJS)
								&& nonNull(resourceJS.getChildren())) {
							
							// root node
							Reference referenceRoot = new Reference(ASSET, resourceRoot.getPath(), resourceRoot,
									getLastModifiedTimeOfResource(resourceRoot));
							hbsReferenceList.add(referenceRoot);
							
							LOGGER.debug("KPFoundationHBReferenceProvider referenceRoot -----> "+ referenceRoot.getResource().getPath());

							// js.txt
							Reference referenceJsTxt = new Reference(ASSET, resourceJsTxt.getPath(), resourceJsTxt,
									getLastModifiedTimeOfResource(resourceJsTxt));
							hbsReferenceList.add(referenceJsTxt);
							
							LOGGER.debug("KPFoundationHBReferenceProvider referenceJsTxt -----> "+ referenceJsTxt.getResource().getPath());
							
							LOGGER.debug("KPFoundationHBReferenceProvider jsTxt lastModified -----> "+ jsLastUpdated);	
							
							// js folder with all JS files
							Iterable<Resource> hbsResources = resourceJS.getChildren();
							hbsResources.forEach(hbsResource -> {
								if (nonNull(hbsResource)) {
									hbsReferenceList.add(new Reference(ASSET, hbsResource.getPath(), hbsResource,
											jsTxtlastModified));
								LOGGER.debug("KPFoundationHBReferenceProvider Add JS files in reference list -----> "+ hbsResource.getPath());	
								}
							});
						}
					}
				}
			}
		} catch (Exception e) {
			LOGGER.error("Exception {}", e);
		}
		return hbsReferenceList;
	}
	
    private long getLastModifiedTimeOfResource(Resource hbsResource) {
        final Calendar mod = hbsResource.getValueMap().get(JCR_CONTENT + "/" + JCR_LASTMODIFIED, Calendar.class);
        long lastModified = mod != null ? mod.getTimeInMillis() : -1;
        if (hbsResource.getPath().contains(JS_DOT_TXT)) {
        	jsTxtlastModified = lastModified;
        	jsLastUpdated = mod.getTime(); 
        }
        return lastModified;
    }
}