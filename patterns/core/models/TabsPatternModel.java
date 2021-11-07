package org.kp.patterns.core.models;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.RepositoryException;
import javax.jcr.ValueFormatException;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.models.annotations.Model;
import org.kp.foundation.core.exception.GenericRuntimeException;
import org.kp.patterns.core.panels.TabPanel;
import org.osgi.framework.InvalidSyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TabbedContentModel is use class, This class provides the data for the tabs
 * that are created in dialog authoring.
 * 
 * @author Manjunath Gurmitkal
 *
 */
@SuppressWarnings("deprecation")
@Model(adaptables = { Resource.class })
public class TabsPatternModel {

	private static final String TAB_TITLE = "tabTitle";
	private static final String DEFAULT_DISPLAY = "defaultDisplay";
	private static final String TAB_LIST_NODE_NAME = "tabList";
	private static final String PAR_NODE_NAME = "par";
	private static final String TAB_LIST_ITEM_ACTIVE = "-active";
	private static final String RESOURCE_TYPE = "wcm/foundation/components/parsys";

	@Inject
	Resource resource;

	private static final Logger LOGGER = LoggerFactory.getLogger(TabsPatternModel.class);
	private List<TabPanel> tabbedContentList = new ArrayList<>();

	/**
	 * Init method populates the tabbed content list for tabs that are created
	 * while authoring in dialog..
	 * 
	 * @throws InvalidSyntaxException,
	 *             JSONException
	 * 
	 */
	@PostConstruct
	public void init() throws InvalidSyntaxException, JSONException {
		LOGGER.debug("TabbedContentModel :: init");
		tabbedContentList = new ArrayList<TabPanel>();
		setTabbedContentList();
	}

	public List<TabPanel> getTabbedContentList() {
		return new ArrayList<>(tabbedContentList);
	}

	/**
	 * populate the tab List.
	 * 
	 * tabbedContentList is populated by iterating over the node named tabList
	 * from the content. The children nodes of tabList node are iterated based
	 * on counter and tab properties extracted are set to the tabbedContentList.
	 * 
	 * @throws RepositoryException
	 * @throws ValueFormatException
	 * @throws PathNotFoundException
	 */
	private void setTabbedContentList() {
		Node currentNode = resource.adaptTo(Node.class);
		try {
			if (currentNode.hasNode(TAB_LIST_NODE_NAME)) {
				setTabList(TAB_LIST_NODE_NAME, currentNode);
			}
		} catch (PathNotFoundException e) {
			throw new GenericRuntimeException("TabbedContentModel :: PathNotFoundException", e);
		} catch (ValueFormatException e) {
			throw new GenericRuntimeException("TabbedContentModel :: ValueFormatException", e);
		} catch (RepositoryException e) {
			throw new GenericRuntimeException("TabbedContentModel :: RepositoryException", e);
		}
	}

	/**
	 * setTabList.
	 * 
	 * List is populated by iterating over the node named tabList from the
	 * content. The children nodes of tabList node are iterated based on counter
	 * and tab properties extracted are set to the tabbedContentList.
	 * 
	 * @param nodeVal
	 *            nodeVal.
	 * @param currentNode
	 *            currentNode.
	 * @throws PathNotFoundException
	 *             PathNotFoundException.
	 * @throws RepositoryException
	 *             RepositoryException.
	 * @throws ValueFormatException
	 *             ValueFormatException.
	 */
	private void setTabList(String nodeVal, Node currentNode)
			throws PathNotFoundException, RepositoryException, ValueFormatException {
		Node mapNode = currentNode.getNode(nodeVal);
		int counter = 1;
		boolean defaultDisplayChecker = false;
		NodeIterator nodeItr = mapNode.getNodes();
		while (nodeItr.hasNext()) {
			PropertyIterator itr = nodeItr.nextNode().getProperties();
			TabPanel tabbedModel = new TabPanel();
			defaultDisplayChecker |= setTabData(counter++, itr, tabbedModel);
			tabbedContentList.add(tabbedModel);
		}
		LOGGER.debug("TabbedContentModel :: getTableListValue :: tabbedContentList.size()={}", tabbedContentList.size());
		setDefaultDisplayValue(defaultDisplayChecker);
	}

	/**
	 * setDefaultDisplayValue.
	 * 
	 * By default if author has not set any of the tab as default tab, then
	 * first tab is marked as default tab.
	 * 
	 * @param defaultDisplayChecker
	 *            defaultDisplayChecker.
	 */
	private void setDefaultDisplayValue(boolean defaultDisplayChecker) {
		if (!defaultDisplayChecker && !tabbedContentList.isEmpty()) {
			tabbedContentList.get(0).setAreaSelected(true);
			tabbedContentList.get(0).setTabListItemActive(TAB_LIST_ITEM_ACTIVE);
		}
	}

	/**
	 * setTabData.
	 * 
	 * The children nodes of tabList node are iterated based on counter and tab
	 * properties extracted are set to the tabbedContentList
	 * 
	 * @param counter
	 *            counter.
	 * @param itr
	 *            itr.
	 * @param defaultDisplayChecker
	 *            defaultDisplayChecker.
	 * @param tabbedModel
	 *            tabbedModel.
	 * @return boolean flag to set the default value used for selected tab
	 *         focus.
	 * @throws RepositoryException
	 *             RepositoryException.
	 * @throws ValueFormatException
	 *             ValueFormatException.
	 */
	private boolean setTabData(int counter, PropertyIterator itr, TabPanel tabbedModel)
			throws RepositoryException, ValueFormatException {

		boolean defaultDisplayCheck = false;
		LOGGER.debug("TabbedContentModel :: setTableContentData :: counter={}", counter);
		while (itr.hasNext()) {
			Property property = itr.nextProperty();
			String propName = property.getName();
			if (propName==null) {
				continue;
			}
			switch (propName) {
			case TAB_TITLE:
				tabbedModel.setTabTitle(property.getString());
				break;
				
			case DEFAULT_DISPLAY:
				tabbedModel.setTabListItemActive(TAB_LIST_ITEM_ACTIVE);
				tabbedModel.setAreaSelected(true);
				defaultDisplayCheck = true;
				break;

			default:
				break;
			}

		}
		return defaultDisplayCheck;
	}
	/**
	 * @return the multifield node value
	 */
	public String getTabListNodeName() {
		return TAB_LIST_NODE_NAME;
	}
	/**
	 * @return the parsys node value
	 */
	public String getParsysNodeName() {
		return PAR_NODE_NAME;
	}
	/**
	 * @return the resource type for parsys
	 */
	public String getResourceType() {
		return RESOURCE_TYPE;
	}
}
