// ---------------------------------------------------------------------------
/*
 CreatedOn       : Aug 02, 2016
 CreatedBy       : Irene Appraem
 ProjectName     : kp-foundation.core
 CompilationUnit : compactable for Java 1.6 or higher

 */
// ---------------------------------------------------------------------------
package org.kp.foundation.core.use;

import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.kp.foundation.core.exception.GenericRuntimeException;

public class ImageUtilUse extends BaseWCMUse {

	private static final String PATH = "path";
	private static final String ID = "id";

	private String mobileImagePath;
	private String adaptiveImageStyleString;
	private String adaptiveImageId;

	@Override
	public void activate() {

		ResourceResolver resourceResolver = getResourceResolver();

		try {
			String desktopImagePath = get(PATH, String.class);
			String id = get(ID, String.class);
			Resource resource = getResource();
			if (StringUtils.isNotEmpty(desktopImagePath)) {
				int i = desktopImagePath.lastIndexOf('.');
				mobileImagePath = desktopImagePath.substring(0, i) + "-320"
						+ desktopImagePath.substring(i);
				Resource mobileImageResource = resourceResolver
						.getResource(mobileImagePath);

				if (mobileImageResource == null) {
					mobileImagePath = desktopImagePath;
				}
				if (resource != null && StringUtils.isNotEmpty(id)) {
					adaptiveImageId = generateAdapativeImageId(id);
					adaptiveImageStyleString =  "<style> @media screen and (max-device-width: 786px){#"+ adaptiveImageId
							+" { content: url("+ mobileImagePath +");}}@media screen and (min-device-width: 1024px){#"
							+ adaptiveImageId +"{ content: url("+ desktopImagePath +");}}</style>";
				}
			}

		} catch (Exception e) {
			throw new GenericRuntimeException("ImageUtilUse :: activate :: Exception while trying to get the mobile image path", e);
		}

	}

	public String generateAdapativeImageId(String id) {
		return id + "_" + UUID.randomUUID().toString();
	}

	public String getMobileImagePath() {
		return mobileImagePath;
	}

	public String getAdaptiveImageStyleString() {
		return adaptiveImageStyleString;
	}

	public String getAdaptiveImageId() {
		return adaptiveImageId;
	}
}
