package org.kp.foundation.core.use;

import org.kp.foundation.core.service.PropertyConfigService;
import org.osgi.service.component.annotations.Reference;

/**
 * Read properties from configuration file.
 * More properties can be added as required.
 * 
 */
public class PropertyConfigurationUse extends BaseWCMUse {

	@Reference
	PropertyConfigService propService;

	@Override
	public void activate() throws Exception {
		// Implement this method to perform post initialization tasks. This is called from the WCMUse#init
	}

	/*
	 * Get DTM JS file from PropertyConfiguration Service 
	 * 
	 * @ return
	 */
	public String getDtmJsFile() {
		return getSlingScriptHelper().getService(PropertyConfigService.class).getDTMJsfile();
	}
}
