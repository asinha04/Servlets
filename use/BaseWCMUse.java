package org.kp.foundation.core.use;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.sightly.WCMUsePojo;


/**
 * The BaseComponent class is base Use bean class for all components
 * 
 * this class defined for future purpose which can be defined all common functionality
 *
 * @author Tirumala Malladi
 *
 */

public class BaseWCMUse extends WCMUsePojo {
	protected Logger log = LoggerFactory.getLogger(this.getClass());
	private boolean hasContent;

	public void activate() throws Exception {
		// Implement this method to perform post initialization tasks. This is called from the WCMUse#init.
	}

	public boolean hasContent() {
		return hasContent;
	}

	public void setHasContent(boolean hasContent) {
		this.hasContent = hasContent;
	}

}
