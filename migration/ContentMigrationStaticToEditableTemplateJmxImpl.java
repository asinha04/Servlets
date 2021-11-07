package org.kp.foundation.core.migration;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Session;
import javax.jcr.Workspace;
import javax.management.DynamicMBean;
import javax.management.NotCompliantMBeanException;
import javax.jcr.Property;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.adobe.granite.jmx.annotation.AnnotatedStandardMBean;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;

/**
 * This Mbean class does the content migration from static to editable template
 */
@Component(service = DynamicMBean.class, property = {
		"jmx.objectname=org.kp.migration:type=Content Migration from static to editable template" })
public class ContentMigrationStaticToEditableTemplateJmxImpl extends AnnotatedStandardMBean
		implements ContentMigrationStaticToEditableTemplateMBean {

	private static final Logger log = LoggerFactory.getLogger(ContentMigrationStaticToEditableTemplateJmxImpl.class);
	private static final String NEW_LINE_CONSTANT = "\n \n";
	private static final String JCR_CONTENT = "/jcr:content";
	private String editableTemplatePath = "/structure/jcr:content/root/responsivegrid";
	private static final String RESPONSIVE_GRID = "/root/responsivegrid/container/responsivegrid";
	private static final String CQ_RESPONSIVE = "/root/responsivegrid/cq:responsive";
	private static final String JCR_MIXINS = "jcr:mixinTypes";
	private static final String JCR_MIXINS_VALUE = "cq:LiveRelationship";
	private static final String ROOT_RESPONSIVE_GRID  = "/root/responsivegrid";
	private static final String CONTAINER_RESPONSIVE_GRID = "/root/responsivegrid/container";
	private static final String EN_NATIONAL = "/content/kporg/en/national";
	private static final String EDITABLE = "editable";
	private String[] templateMappings;
	private String[] parsysNamesArray;
	private Map<String, Object> param;
	private StringBuilder migrationLogs;
	private Workspace wsp;
	private String sourceTemplatePath = null;
	private List<String> parsysNamesList;
	private Map<String, String> templateMap;
	private SearchResult result;
	private int resultingPagesCount;
	private String osgiEditableTemplatePath;
	private boolean setJcrMixins = Boolean.FALSE;

	@Reference
	private ResourceResolverFactory resolverFactory;

	@Reference
	private ContentMigrationStaticToEditableTemplateConfigService config;

	@Reference
	private QueryBuilder builder;

	public ContentMigrationStaticToEditableTemplateJmxImpl() throws NotCompliantMBeanException {
		super(ContentMigrationStaticToEditableTemplateMBean.class);
	}

	public ContentMigrationStaticToEditableTemplateJmxImpl(Class<?> mbeanInterface) throws NotCompliantMBeanException {
		super(mbeanInterface);
	}

	/**
	 * This is the first method which gets invoked when JMX script is executed. All
	 * code flow happens from here. We can run the script for entire content tree or
	 * just for a single page.
	 */
	@Override
	public String runMigration(String path, Boolean runForSinglePageOnly) {
		ResourceResolver resolver = null;
		Session session = null;
		log.info("starting content migration from static to editable ...");
		param = new HashMap<String, Object>();
		param.put(ResourceResolverFactory.SUBSERVICE, "kpContentMigrationService");
		migrationLogs = new StringBuilder();
		try {
			resolver = resolverFactory.getServiceResourceResolver(param);
			session = resolver.adaptTo(Session.class);
			if (StringUtils.isNotBlank(path)) {
				migrateContentfromStaticToEditableTemplate(path, session, runForSinglePageOnly);
			} else {
				migrationLogs
						.append("No content path entered above. Please enter content path. \n" + NEW_LINE_CONSTANT);
			}
		} catch (Exception e) {
			migrationLogs.append("Error occured during content migration to get service resource resolver \n" + e
					+ NEW_LINE_CONSTANT);
			log.error("Error occured during content migration to get service resource resolver  --- ", e);
		} finally {
			if (session != null && session.isLive()) {
				session.logout();
			}
			if (resolver != null && resolver.isLive()) {
				resolver.close();
			}
		}
		return migrationLogs.toString();
	}

	/**
	 * This method is responsible for executing query and migrating content.
	 */
	private void migrateContentfromStaticToEditableTemplate(String path, Session session,
			Boolean runForSinglePageOnly) {
		try {
			migrationLogs.append(
					"<---------------------Content migration from static to editable template --------------------->"
							+ NEW_LINE_CONSTANT);

			// Reads the parsys name from osgi and converts into list
			parsysNamesArray = config.parsysNamesForMigration();
			parsysNamesList = Arrays.asList(parsysNamesArray);
			templateMap = new HashMap<>();

			// Reads the template mapping from osgi and creates a map (key:static template
			// path, value:editable template path)
			templateMappings = config.staticToEditableTemplateMapping();
			for (String templatePath : templateMappings) {
				String[] templatePathTokenize = templatePath.split("\\|");
				if (templatePathTokenize != null && templatePathTokenize.length == 2) {
					templateMap.put(templatePathTokenize[0], templatePathTokenize[1]);
					templateMap.put("/apps/" + templatePathTokenize[0], templatePathTokenize[1]);
				}
			}
			// Condition to check if parsys names are configured in the osgi. If there are
			// no parsys name, then code doesn't go further.
			if (!parsysNamesList.isEmpty()) {

				// Condition to check if template mappings are configured in the osgi. If there
				// are no template mappings, then code doesn't go further.
				if (templateMap.size() > 0) {

					wsp = session.getWorkspace();

					// This if check is to migrate single page only
					if (runForSinglePageOnly) {
						migrationLogs.append(
								"<---------------------Content migration for single page only--------------------->"
										+ NEW_LINE_CONSTANT);
						String contentPath = path + JCR_CONTENT;
						migrationLogs.append("content page ----->" + contentPath + NEW_LINE_CONSTANT);
						Node contentNode = session.getNode(contentPath);
						Boolean editableStructureExist = false;
						if (session.nodeExists(contentPath + RESPONSIVE_GRID)) {
							editableStructureExist = true;
						}
						if (null != contentNode) {
							if (!editableStructureExist) {
								Boolean skipIteration = findSourceEditableTemplatePath(contentPath, contentNode);
								if (!skipIteration) {
									copyComponentsFromParsys(contentPath, contentNode, editableStructureExist, session);
								}
							} else {
								copyComponentsFromParsys(contentPath, contentNode, editableStructureExist, session);
							}
						}
					} else {
						// execute query and get the result.
						createQuery(path, session);

						// Check to see if there are more than 0 results to migrate
						if (resultingPagesCount > 0) {
							int counter = 1;
							for (Hit hit : result.getHits()) {

								// Content migration started for resulted content
								migrationLogs.append("<---------------------Content migration for resulted content("
										+ counter + ")--------------------->" + NEW_LINE_CONSTANT);
								String jcrNodePath = null;

								try {
									jcrNodePath = hit.getPath() + JCR_CONTENT;
									migrationLogs.append("content page ----->" + jcrNodePath + NEW_LINE_CONSTANT);
									Node jcrNode = session.getNode(jcrNodePath);
									if (null != jcrNode && jcrNode.isCheckedOut()) {
										Boolean editableStructureExist = false;
										if (session.nodeExists(jcrNodePath + RESPONSIVE_GRID)) {
											editableStructureExist = true;
										}
										if (!editableStructureExist) {
											Boolean skipIteration = findSourceEditableTemplatePath(jcrNodePath,
													jcrNode);
											if (skipIteration) {
												continue;
											}											
										} 
										copyComponentsFromParsys(jcrNodePath, jcrNode, editableStructureExist, session);
									}
								} catch (Exception e) {
									migrationLogs.append("Error while migrating content \n" + jcrNodePath + "\n" + e
											+ NEW_LINE_CONSTANT);
									log.error("Error while migrating content ---  \n" + jcrNodePath + "\n" + e);
								}
								counter++;
							}
						} else {
							migrationLogs.append(
									"Query resulted no content pages. Modify the search criteria" + NEW_LINE_CONSTANT);
						}
					}
				} else {
					migrationLogs.append(
							"There is no template mapping configured in osgi configuration. Please configure template mapping."
									+ NEW_LINE_CONSTANT);
				}
			} else {
				migrationLogs.append(
						"There is no parsys node configured in osgi configuration. Please configure parsys node."
								+ NEW_LINE_CONSTANT);
			}

		} catch (Exception e) {
			migrationLogs.append("Error occured during content migration --- " + e + NEW_LINE_CONSTANT);
			log.error("Error occured during content migration --- ", e);
		}
	}

	/**
	 * This method copies the editable template structure from editable template to
	 * content path. For ex:
	 * /content/kporg/en/national/jcr:content/root/responsivegrid/container/responsivegrid
	 */
	private void createStructureFromEditableTemplate(String jcrNodePath, Node jcrNode, Session session) throws Exception {
		if (!session.nodeExists(jcrNodePath + "/root")) {
			Node rootNode = jcrNode.addNode("root", "nt:unstructured");
			rootNode.setProperty("sling:resourceType", "wcm/foundation/components/responsivegrid");
			if (setJcrMixins) {
				rootNode.addMixin(JCR_MIXINS_VALUE);
			}
			session.save();
		}
		String finalDestinationRoot = jcrNodePath + "/root" + "/responsivegrid";
		if (session.nodeExists(sourceTemplatePath) && !session.nodeExists(finalDestinationRoot)) {
			wsp.copy(sourceTemplatePath, finalDestinationRoot);
			String deleteNodePath = jcrNodePath + "/root/responsivegrid/container/attributesList";
			if (session.nodeExists(deleteNodePath)) {
				session.getNode(deleteNodePath).remove();
				session.save();
			}
			
			String deleteResponsiveNodePath = jcrNodePath + CQ_RESPONSIVE;			
			if (session.nodeExists(deleteResponsiveNodePath)) {
				session.getNode(deleteResponsiveNodePath).remove();
				session.save();
			}
			
			if (setJcrMixins) {
				addMixins(jcrNodePath, session);
			}
			
			String lastResponsiveNodePath = jcrNodePath + RESPONSIVE_GRID;
			if (session.nodeExists(lastResponsiveNodePath)) {
				Node lastResponsiveNode = session.getNode(lastResponsiveNodePath);
				if (lastResponsiveNode.hasProperty(EDITABLE)) {
					Property editableProperty = lastResponsiveNode.getProperty(EDITABLE);
					if (null != editableProperty) {
						editableProperty.remove();
						session.save();
					}
				}
			}
		}
	}

	/**
	 * This method adds jcr mixins property to the editable template root structure.
	 * 
	 */
	private void addMixins(String jcrNodePath, Session session) throws Exception {
		if (session.nodeExists(jcrNodePath + RESPONSIVE_GRID)) {
			Node rootNode = session.getNode(jcrNodePath + ROOT_RESPONSIVE_GRID);
			rootNode.addMixin(JCR_MIXINS_VALUE);

			Node containerNode = session.getNode(jcrNodePath + CONTAINER_RESPONSIVE_GRID);
			containerNode.addMixin(JCR_MIXINS_VALUE);

			Node responsiveGridNode = session.getNode(jcrNodePath + RESPONSIVE_GRID);
			responsiveGridNode.addMixin(JCR_MIXINS_VALUE);

			session.save();
			setJcrMixins = false;
		} else {
			migrationLogs.append("The node doesn't exist at ----->" + RESPONSIVE_GRID + NEW_LINE_CONSTANT);
		}
	}

	/**
	 * This method updates the resource type property and cq:Template property of the content page.
	 * For ex: kporg/kp-foundation/components/structure/contentPage -> kporg/kp-foundation/components/structure/editable-page
	 * 
	 */
	private void updateResourceType(String jcrNodePath, Node jcrNode, Session session) throws Exception {
		if (StringUtils.isNotBlank(osgiEditableTemplatePath)) {
			String editableTemplateStrNodePath = osgiEditableTemplatePath + "/structure/jcr:content";
			if (session.nodeExists(editableTemplateStrNodePath)) {
				Node editableTemplateNode = session.getNode(editableTemplateStrNodePath);
				String editableTemplateResourceType = editableTemplateNode.getProperty("sling:resourceType")
						.getString();
				jcrNode.setProperty("sling:resourceType", editableTemplateResourceType);
				jcrNode.setProperty("cq:template", osgiEditableTemplatePath);
				session.save();
			} else {
				migrationLogs.append("No node exists at ----->" + editableTemplateStrNodePath + NEW_LINE_CONSTANT);
			}
		}
	}

	/**
	 * This method copies the components under parsys to the new path of content.
	 * For ex: bodypar or other parsys components gets copied to below path
	 * /content/kporg/en/national/jcr:content/root/responsivegrid/container/responsivegrid
	 * 
	 * Also, this method copies the editable template structure from editable template to content path.
	 */
	private void copyComponentsFromParsys(String jcrNodePath, Node jcrNode, Boolean editableStructureExist, Session session) throws Exception {
		if (jcrNode.hasNodes()) {
			NodeIterator nodeItr = jcrNode.getNodes();
			Boolean noParsysToMigrate = false;
			boolean structureCreatedAndResTypeUpdated = false;
			while (nodeItr.hasNext()) {
				Node cNode = nodeItr.nextNode();
				String parsysNodeName = cNode.getName();
				if (parsysNamesList.contains(parsysNodeName)) {
					noParsysToMigrate = true;
					if (cNode.hasNodes()) {
						if (!structureCreatedAndResTypeUpdated && !editableStructureExist) {
							updateResourceType(jcrNodePath, jcrNode, session);
							
							if (!jcrNodePath.startsWith(EN_NATIONAL) && cNode.hasProperty(JCR_MIXINS)) {
								setJcrMixins = Boolean.TRUE;
							}
							
							createStructureFromEditableTemplate(jcrNodePath, jcrNode, session);
							structureCreatedAndResTypeUpdated = true;
						}

						NodeIterator childNodeItr = cNode.getNodes();
						while (childNodeItr.hasNext()) {
							Node finalNode = childNodeItr.nextNode();
							wsp.move(finalNode.getPath(), jcrNodePath + RESPONSIVE_GRID + "/" + finalNode.getName());
							migrationLogs.append(parsysNodeName + " moved component name ----->" + finalNode.getName()
									+ NEW_LINE_CONSTANT);
						}
						migrationLogs
								.append(cNode.getName() + " parsys components moved successfully " + NEW_LINE_CONSTANT);
						if (!cNode.hasNodes()) {
							cNode.remove();
							session.save();
							migrationLogs.append(
									parsysNodeName + " parsys deleted successfully " + NEW_LINE_CONSTANT);
						}
					} else {
						migrationLogs.append("No components under " + cNode.getName() + NEW_LINE_CONSTANT);
					}

				}
			}
			if (!noParsysToMigrate) {
				migrationLogs.append("This page doesn't have any eligible parsys(configured in osgi) to migrate "
						+ jcrNodePath + NEW_LINE_CONSTANT);
			}
		} else {
			migrationLogs.append("No node under " + jcrNodePath + NEW_LINE_CONSTANT);
		}
	}

	/**
	 * This method creates a jcr query and gets the result.
	 */
	private void createQuery(String path, Session session) throws Exception {
		Map<String, String> map = new HashMap<String, String>();

		migrationLogs.append("Below is query search criteria ----->   " + NEW_LINE_CONSTANT);
		migrationLogs.append("Path ----->" + path + NEW_LINE_CONSTANT);
		log.info("path::" + path);

		// Query execution starts
		map.put("path", path);
		map.put("path.self", "true");
		map.put("type", "cq:Page");
		map.put("1_property", "jcr:content/sling:resourceType");

		int i = 1;
		for (Map.Entry<String, String> entry : templateMap.entrySet()) {
			String propertykey = "1_property." + String.valueOf(i) + "_value";
			map.put(propertykey, entry.getKey());
			migrationLogs.append("Static template path   ----->" + entry.getKey() + NEW_LINE_CONSTANT);
			migrationLogs.append("Editable template path ----->" + entry.getValue() + NEW_LINE_CONSTANT);
			migrationLogs.append("----------" + NEW_LINE_CONSTANT);
			i++;
		}

		for (String parList : parsysNamesList) {
			migrationLogs.append("Parsys name ----->" + parList + NEW_LINE_CONSTANT);
		}

		map.put("p.offset", "0");
		map.put("p.limit", "-1");
		map.put("orderby", "path");
		Query query = builder.createQuery(PredicateGroup.create(map), session);
		result = query.getResult();
		resultingPagesCount = result.getHits().size();
		// Query execution end

		migrationLogs.append("End query search criteria ----->" + NEW_LINE_CONSTANT);
		migrationLogs.append(
				"Query executetion completed. Total results are -----> " + resultingPagesCount + NEW_LINE_CONSTANT);
	}

	/**
	 * This method finds source editable template path. We will use this path to
	 * copy the structure from editable template to content path
	 */
	private Boolean findSourceEditableTemplatePath(String jcrNodePath, Node jcrNode) throws Exception {
		osgiEditableTemplatePath = null;
		Boolean skipIteration = false;
		if (jcrNode != null && jcrNode.hasProperty("sling:resourceType")) {
			String slingResourceType = jcrNode.getProperty("sling:resourceType").getString();
			osgiEditableTemplatePath = templateMap.get(slingResourceType);
			if (StringUtils.isNotBlank(osgiEditableTemplatePath)) {
				sourceTemplatePath = osgiEditableTemplatePath + editableTemplatePath;
			} else {
				migrationLogs.append("Template mapping doesn't contain page resourceType ----->" + slingResourceType
						+ NEW_LINE_CONSTANT);
				skipIteration = true;
				return skipIteration;
			}
		} else {
			migrationLogs.append("resourceType is not present at the node ----->" + jcrNodePath + NEW_LINE_CONSTANT);
			skipIteration = true;
			return skipIteration;
		}
		return skipIteration;
	}
}