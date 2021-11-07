
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

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Via;
import org.kp.foundation.core.models.BaseModel;
import org.kp.patterns.core.panels.ContentTogglePanel;
import org.osgi.framework.InvalidSyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ContentTogglePatternModel is Sling Model class, This class provides the data
 * for the accordion component multi field node that are generated through
 * authoring dialog . Enhanced by - Ravish Sehgal
 */
@Model(adaptables = { Resource.class,
		SlingHttpServletRequest.class }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ContentTogglePatternModel extends BaseModel {

	@Inject
	private Resource resource;

	@Inject
	@Via("resource")
	@Default(values = "")
	private String accTitle;

	@Inject
	@Via("resource")
	@Default(values = "")
	private String sectionAnchor;

	@Inject
	@Via("resource")
	@Default(values = "")
	private String openByDefault;

	@Inject
	@Via("resource")
	@Default(values = "-variation-4")
	private String styling;

	@Inject
	@Via("resource")
	@Default(values = "expand-collapse")
	private String behaviourStyle;

	@Inject
	@Via("resource")
	@Default(booleanValues = false)
	private boolean horizontalRule;
	
	@Inject
	@Via("resource")
	@Default(booleanValues = false)
	private boolean disableOnDesktop;

	private static final String ACCORDION_TITLE_PROP = "accTitle";
	private static final String ACCORDION_ANCHOR_PROP = "sectionAnchor";
	private static final String ACCORDION_EXPAND_PROP = "openByDefault";

	private static final String DEFAULT_HORIZONTAL_RULE_VALUE = "-bordered";
	private static final String DEFAULT_DISABLE_DESKTOP_VALUE = "-device-only";

	private static final String ACC_LINK_COLL_CSS = "accordion-link-collapse";
	private static final String ACC_LINK_EXP_CSS = "accordion-link-expand";
	private static final String SHOW_CSS = "show";

	private static final String PAR_NODE = "par";
	private static final String NODE_NAME = "accList";
	private static final boolean PARSYS_NODE_EXIST = true;

	private static final Logger LOGGER = LoggerFactory.getLogger(ContentTogglePatternModel.class);
	private List<ContentTogglePanel> accItemList = new ArrayList<>();

	/**
	 * Init method populates the Accordion content list for items that are created
	 * while authoring in dialog..
	 * 
	 * @throws InvalidSyntaxException -
	 * @throws JSONException          -
	 * @throws RepositoryException    -
	 * @throws ValueFormatException   -
	 * @throws PathNotFoundException  -
	 * @throws Exception              exception.
	 */
	@PostConstruct
	public void init() throws InvalidSyntaxException, PathNotFoundException, ValueFormatException, RepositoryException {
		setAccContent();
	}

	public List<ContentTogglePanel> getItemContentList() {
		return new ArrayList<>(accItemList);
	}

	/**
	 * This method calls the "getAccListValue(nodeName, resourseNode)" by passing
	 * the "multifieldContentNode" and "resourceRootNode".
	 * 
	 * @throws RepositoryException   -
	 * @throws ValueFormatException  -
	 * @throws PathNotFoundException -
	 */
	private void setAccContent() throws PathNotFoundException, ValueFormatException, RepositoryException {
		Node currentNode = resource.adaptTo(Node.class);
		if (currentNode.hasNode(NODE_NAME)) {
			getAccListValue(NODE_NAME, currentNode);
		}
		LOGGER.info("Resource node path::{}", currentNode.getPath());
	}

	/**
	 * construct accordion Pojo list by adding the parsys node value to each
	 * accordion pojo object.
	 * 
	 * @param nodeVal     -
	 * @param currentNode -
	 * @throws PathNotFoundException -
	 * @throws RepositoryException   -
	 * @throws ValueFormatException  -
	 */
	private void getAccListValue(String nodeVal, Node currentNode)
			throws PathNotFoundException, RepositoryException, ValueFormatException {
		accItemList = new ArrayList<ContentTogglePanel>();
		Node mapNode = currentNode.getNode(nodeVal);
		NodeIterator itr = mapNode.getNodes();
		while(itr.hasNext()) {
			Node childNode = itr.nextNode();
			ContentTogglePanel accordionPanel = new ContentTogglePanel();
			// check parsys node exist or not and then add it Pojo Accordion.
			updateAccordionPanel(childNode, accordionPanel);
			updateAccordionPanelProps(childNode, accordionPanel);
		}
		LOGGER.info("AccordionPanel Item size::{}", accItemList.size());
	}

	/**
	 * check parsys node exist or not and then add it pojo accordion.
	 * 
	 * @param Multifield    Content Node
	 * @param accordionPojo -
	 * @return -
	 * @throws RepositoryException   -
	 * @throws PathNotFoundException -
	 */
	private void updateAccordionPanel(Node cnode, ContentTogglePanel accordionPanel)
			throws PathNotFoundException, RepositoryException {
		accordionPanel.setParsysNode(false);
		if (cnode.hasNodes()) {
			Node parNode = cnode.getNode(PAR_NODE);
			if (parNode.hasNodes()) {
				accordionPanel.setParsysNode(PARSYS_NODE_EXIST);
			}
		}
	}

	/*
	 * This method iterate over the multi_field content node properties and fill
	 * each required properties to Accordion Pojo
	 */
	/**
	 * @param counter.
	 * @param itr           -
	 * @param accordionPojo -
	 * @return -
	 * @throws RepositoryException  -
	 * @throws ValueFormatException -
	 */
	private void updateAccordionPanelProps(Node cnode, ContentTogglePanel accordionPanel)
			throws RepositoryException, ValueFormatException {
		PropertyIterator itr;
		itr = cnode.getProperties();
		boolean openByDefaultMulti = false;
		Property property;
		while (itr.hasNext()) {
			property = itr.nextProperty();
			switch (property.getName()) {
			case ACCORDION_TITLE_PROP:
				accordionPanel.setAccTitle(property.getString());
				break;

			case ACCORDION_ANCHOR_PROP:
				accordionPanel.setSectionAnchor(property.getString());
				break;

			case ACCORDION_EXPAND_PROP:
				accordionPanel.setOpenByDefault(property.getBoolean());
				accordionPanel.setAreaExpand(String.valueOf(property.getBoolean()));
				accordionPanel.setAnalyticsData(property.getBoolean() ? ACC_LINK_EXP_CSS : ACC_LINK_COLL_CSS);
				accordionPanel.setShowClass(property.getBoolean() ? SHOW_CSS : "");
				openByDefaultMulti = true;
				break;

			default:
				break;
			}

		}
		if (!openByDefaultMulti) {
			accordionPanel.setOpenByDefault(false);
			accordionPanel.setAreaExpand(Boolean.FALSE.toString());
			accordionPanel.setAnalyticsData(ACC_LINK_COLL_CSS);
			accordionPanel.setShowClass("");
		}
		accItemList.add(accordionPanel);
	}

	public static String getAccList() {
		return NODE_NAME;
	}

	public static String getPar() {
		return PAR_NODE;
	}

	public String getHorizontalRule() {
		return horizontalRule ? DEFAULT_HORIZONTAL_RULE_VALUE : "";
	}
	
	public String getDisableOnDesktop() {
		return disableOnDesktop ? DEFAULT_DISABLE_DESKTOP_VALUE : "";
	}

	public String getStyling() {
		return styling;
	}

	public String getBehaviourStyle() {
		return behaviourStyle;
	}
}
