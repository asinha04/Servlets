package org.kp.foundation.core.use;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.sling.api.resource.Resource;
import org.kp.foundation.core.constants.GlobalConstants;
import org.kp.foundation.core.models.MultiFieldLinkModel;
import org.kp.foundation.core.utils.PropertyInheritedUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UtilityLinkUse extends BaseWCMUse {
	private static final Logger LOGGER = LoggerFactory.getLogger(UtilityLinkUse.class);
	private List<MultiFieldLinkModel> utilityLinks = new ArrayList<MultiFieldLinkModel>();

	@Override
	public void activate() throws Exception {
		Resource linkRootRes = PropertyInheritedUtil.getChildNodeResource(getResource(), GlobalConstants.UTILITY_LINKS);
		if (linkRootRes == null) {
			LOGGER.error("utility Links for header are not set!");
			return;
		}
		Iterator<Resource> itr = linkRootRes.listChildren();
		while (itr.hasNext()) {
			Resource linkRes = itr.next();
			MultiFieldLinkModel utilityLinkModel = linkRes.adaptTo(MultiFieldLinkModel.class);
			utilityLinks.add(utilityLinkModel);
		}
	}

	public List<MultiFieldLinkModel> getUtilityLinks() {
		return new ArrayList<>(utilityLinks);
	}

}
