package org.kp.foundation.core.use;

import java.util.LinkedList;
import java.util.List;

import javax.jcr.Node;

import org.apache.sling.api.resource.Resource;
import org.kp.foundation.core.constants.GlobalConstants;
import org.kp.foundation.core.exception.GenericRuntimeException;
import org.kp.foundation.core.models.LinkModel;
import org.kp.foundation.core.models.TitleTextLinkModel;
import org.kp.foundation.core.models.TitleTextModel;
import org.kp.foundation.core.utils.LinkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.sightly.WCMUsePojo;

/**
 * The Class TitleTextLinksUse.
 */

//Waiver details on GSC-3618
@SuppressWarnings({ "squid:S3776" })
public class TitleTextLinkUse extends WCMUsePojo {

	private static final Logger LOG = LoggerFactory.getLogger(TitleTextLinkUse.class);
	private static final String PROPERTY_LINKS = "links";
	TitleTextLinkModel titleTextLink;

	@Override
	public void activate() throws Exception {
		// Implement this method to perform post initialization tasks. This is called
		// from the WCMUse#init
	}

	/**
	 * Gets the title text link.
	 *
	 * @return the title text link
	 */
	public TitleTextLinkModel getTitleTextLink() {
		LOG.debug("Inside getTitleTextLink()");
		titleTextLink = new TitleTextLinkModel();
		String style = "Horizontal List";
		String title = "";
		List<LinkModel> links = new LinkedList<LinkModel>();
		try {
			Node currentNode = getResource().adaptTo(Node.class);
			if (currentNode != null) {
				if (currentNode.hasProperty(GlobalConstants.JCR_TITLE)) {
					title = currentNode.getProperty(GlobalConstants.JCR_TITLE).getString();
				}

				if (currentNode.hasProperty(GlobalConstants.JCR_STYLE)) {
					style = currentNode.getProperty(GlobalConstants.JCR_STYLE).getString();
				}

				Resource linkRootRes = getResource().getChild(PROPERTY_LINKS);
				if (linkRootRes != null) {
					Iterable<Resource> linkResItr = linkRootRes.getChildren();
					for (Resource res : linkResItr) {
						TitleTextModel linkModel2 = res.adaptTo(TitleTextModel.class);
						LinkModel linkObj = convertLinkModel(linkModel2);
						links.add(linkObj);
					}
				}
			}
			titleTextLink.setTitle(title);
			titleTextLink.setStyle(style);
			titleTextLink.setLinks(links);
			LOG.debug("TitleTextLink Links Data: {} ", titleTextLink.toString());
		} catch (Exception e) {
			throw new GenericRuntimeException("TitleTextLinkUse :: getTitleTextLink:: Exception while loading links {}",
					e);
		}
		return titleTextLink;
	}

	private LinkModel convertLinkModel(TitleTextModel linkModel2) {
		LinkModel linkModel = new LinkModel();
		linkModel.setDisplayText(linkModel2.getLinkText());
		if (linkModel2.getLink() != null) {
			linkModel.setResourceLink(LinkUtil.getPathfieldURL(linkModel2.getLink()));
		}

		String target = linkModel2.isOpenWindow() ? "_blank" : "_self";
		linkModel.setTarget(target);
		return linkModel;
	}
}