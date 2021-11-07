
package org.kp.foundation.core.use;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.apache.sling.api.resource.Resource;
import org.kp.foundation.core.models.MultiFieldLinkModel;
import org.kp.foundation.core.utils.LinkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.wcm.api.Page;

/**
 * LinkListUse class is responsible for processing the link list authored
 * content.
 * 
 * @author Mohan Joshi,Mallika
 *
 */
//Waiver details on GSC-3616
@SuppressWarnings({"squid:S3776"})
public class LinkListUse extends WCMUsePojo {
	private static final Logger LOGGER = LoggerFactory.getLogger(LinkListUse.class);
	List<HashMap<String, Object>> multiFieldValues = new ArrayList<HashMap<String, Object>>();
	private static final String NO_FOLLOW_TAG = "noFollow";
	private static final String PROPERTY_NAME = "options";
	private static final String TITLE_TEXT = "titleText";
	private static final String TITLE_STYLE = "titleStyle";
	private static final String HIDE_TITLE = "hideTitle";
	private static final String LIST_STYLE = "listStyle";
	private static final String LIST_FROM = "listFrom";
	private static final String PARENT_PAGE = "parentPage";
	private String titleText;
	private String titleStyle;
	private boolean hideTitle;
	private String listStyle;
	private String listFrom;
	private String rootPathField;
	private static final String CHILDREN="children";
	private static final String FIXED_LINKED_LIST="fixed link-list";
	private static final String STATIC="static";
	private static final String CHILDREN_LINKED_LIST="children link-list";

	private List<MultiFieldLinkModel> linkModelsList = new ArrayList<>();
	Node currentNode;

	@Override
	public void activate() throws Exception {
		LOGGER.info("Activating Link List Use");
		currentNode = getResource().adaptTo(Node.class);
		titleText = getProperties().get(TITLE_TEXT, "");
		titleStyle = getProperties().get(TITLE_STYLE, "");
		listStyle = getProperties().get(LIST_STYLE, "");
		hideTitle = getProperties().get(HIDE_TITLE, false);
		listFrom = getProperties().get(LIST_FROM, "");
		rootPathField = getProperties().get(PARENT_PAGE, "");
		
		linkModelsList = new ArrayList<MultiFieldLinkModel>();
		Resource linkRootRes = getResource().getChild(PROPERTY_NAME);
		
		if(linkRootRes!=null) {
			Iterable<Resource> linkResItr = linkRootRes.getChildren();	
			for(Resource res : linkResItr) {
				MultiFieldLinkModel linkModel = res.adaptTo(MultiFieldLinkModel.class);
				if(linkModel!=null) {
					if (linkModel.getLinkPath() != null) {
						String path = LinkUtil.getRelativeURL(getRequest(), linkModel.getLinkPath());
						linkModel.setLinkPath(path);
					}
					if (linkModel.getNoLinkFollow() != null) {
						boolean noFollow = Boolean.parseBoolean(linkModel.getNoLinkFollow());
						linkModel.setNoLinkFollow(noFollow ? NO_FOLLOW_TAG : "");
					}
					linkModelsList.add(linkModel);
				}
			}
		}
	}

	/**
	 * . This method is responsible to get the authored multifield list values
	 * and return a list to the html.
	 * 
	 * @return List
	 * @throws RepositoryException
	 *             when multifield has error
	 */
	public List<MultiFieldLinkModel> getListOfLinks() throws RepositoryException {
		return new ArrayList<>(linkModelsList);
	}

	/**
	 * .
	 * 
	 * @returns the childlinks for rootPath selected
	 */
	public List<MultiFieldLinkModel> getChildLinks() {
		Page rootpage = getPageManager().getPage(rootPathField);
		Iterator<Page> children = rootpage.listChildren();
		List<MultiFieldLinkModel> childLinks = new ArrayList<MultiFieldLinkModel>();
		while (children.hasNext()) {
			Page childPage = children.next();
			if (!childPage.isHideInNav()) {
				MultiFieldLinkModel objLinkModel = new MultiFieldLinkModel();
				String path = LinkUtil.getRelativeURL(getRequest(), childPage.getPath());
				objLinkModel.setLinkPath(path);
				objLinkModel.setLinkLabel(childPage.getTitle());
				childLinks.add(objLinkModel);
			}
		}
		return childLinks;
	}

	/**
	 * Getter for root Path.
	 * 
	 * @return the rootPathField
	 */
	public String getRootPathField() {
		return rootPathField;
	}

	/**
	 * Getter for Title text.
	 * 
	 * @return the titleText
	 */
	public String getTitleText() {
		return titleText;
	}

	/**
	 * Getter for Title Style.
	 * 
	 * @return the titleStyle
	 */
	public String getTitleStyle() {
		return titleStyle;
	}

	/**
	 * Getter for Hide Title flag.
	 * 
	 * @return the hideTitle
	 */
	public boolean getHideTitle() {
		return hideTitle;
	}

	/**
	 * .
	 * 
	 * @return the listStyle
	 */
	public String getListStyle() {
		return listStyle;
	}

	/**
	 * Getter for List From.
	 * 
	 * @return the listFrom
	 */
	public String getListFrom() {
		return listFrom;
	}
	/**
	 * Getter for List From.
	 * 
	 * @return the listFrom
	 */
	public String getLinkListTitleForAnalytics() {
		return listFrom.equalsIgnoreCase(STATIC) ? getCurrentPage().getTitle() : CHILDREN;
	}
	
	/**
	 * Getter for List From.
	 * 
	 * @return the listFrom
	 */
	public String getLinkListAnalyticsLocation() {
		if(listFrom.equalsIgnoreCase(STATIC) &&  titleText.isEmpty()) {
			return FIXED_LINKED_LIST; 
		} else if(listFrom.equalsIgnoreCase(STATIC) &&  !titleText.isEmpty()) {
			return FIXED_LINKED_LIST +":" + titleText;
		} else  if(listFrom.equalsIgnoreCase(CHILDREN) &&  titleText.isEmpty()) {
			return CHILDREN_LINKED_LIST; 
		}  else {
			return CHILDREN_LINKED_LIST +":" + titleText;
		}
	}
}