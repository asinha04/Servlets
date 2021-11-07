package org.kp.foundation.core.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.commons.lang.time.FastDateFormat;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingException;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.kp.foundation.core.constants.GlobalConstants;
import org.kp.foundation.core.constants.RegionPicker;
import org.kp.foundation.core.utils.GenericUtil;
import org.kp.foundation.core.utils.LinkUtil;
import org.kp.foundation.core.utils.WCMUseUtil;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.dam.api.Asset;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageFilter;
import com.day.cq.wcm.api.PageManager;
import com.day.crx.JcrConstants;

//Waiver details on GSC-3627
@SuppressWarnings({ "squid:S3776" })
/**
 * This servlet is responsible for rendering site-map for the application, It
 * renders two different sitemaps based upon selectors 1)sitemap and
 * 2)sitemapdam.
 * 
 * @author krishan rathi
 *
 */
@Component(service = Servlet.class, immediate = true, configurationPid = "org.kp.foundation.core.servlets.SiteMapServlet", name = "KP Foundation Site Map Servlet", property = {
		"process.label= KP Foundation Site Map Servlet",
		"sling.servlet.resourceTypes" + "=" + GlobalConstants.HOME_PAGE_RES_TYPE,
		"sling.servlet.resourceTypes" + "=" + GlobalConstants.SITE_PAGE_RES_TYPE,
		"sling.servlet.methods" + "=" + HttpConstants.METHOD_GET,
		"sling.servlet.selectors" + "=" + GlobalConstants.SITEMAP_PAGES,
		"sling.servlet.selectors" + "=" + GlobalConstants.SITEMAP_ASSETS, "sling.servlet.extensions=xml" })
@Designate(ocd = SiteMapServlet.Config.class)
public class SiteMapServlet extends SlingAllMethodsServlet {

	private static final String KP_CONTENT_DAM_PATH = "/content/dam/kporg";
	private static final String NS = "http://www.sitemaps.org/schemas/sitemap/0.9";
	private static final String IMAGE_NS = "http://www.google.com/schemas/sitemap-image/1.1";
	private static final FastDateFormat DATE_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd");
	private static final String CONTENT_PATH_PREFIX = "/content/kporg/[^/]+/[^/]+/";
	private static final String CONTENT_PATH_CHILD_SUFFIX = "/.*";
	private static final String CONTENT_PATH_PAGE_SUFFIX = "$";
	private static final String DAM_ASSET = "dam:Asset";
	private String[] blockedURlsInOsgi;

	/**
	* fix for resolving sonarqube issue (squid:S1948) to 
	* make "dhoTemplatePaths" transient or serializable.
	*/
	private ArrayList<String> dhoTemplatePaths = new ArrayList<String>();
	
	private boolean isDhoAllowed;

	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LoggerFactory.getLogger(SiteMapServlet.class);
	private static final String DCF_COMPONENT_NAME = "dynamiccontentfragment";
	private static final String DCF_COMPONENT_PROP_EXECUTION_TYPE = "executionType";
	private static final String DCF_COMPONENT_PROP_EXECUTION_TYPE_DYNAMIC = "dynamic";
	
	private static final String DCF_COMPONENT_PROP_FRAGMENT_PATH = "fragmentPath";
	private static final String DCF_COMPONENT_PROP_PARENT_PATHS = "parentPaths";
	
	private static final String JCR_CONTENT = "jcr:content";
	private static final String PROP_CONTENT_FRAGMENT = "contentFragment";
	private static final String PROP_JCR_LAST_MODIFIED = "jcr:lastModified";
	private static final String DOT_CONSTANT = ".";
	
	private static final String DOT_HTML_EXTENSION = ".html";
	private static final String CONSTANT_SINGLE_SLASH = "/";
	private static final String PROP_CONTENT_FRAGMENT_REGION = "region";
	private static final String PROP_CQ_TEMPLATE = "cq:template";
	private static final String DHO_CONTENT_FRAGMENT_SERVICE = "kpDhoContentFragmentService";
	
	
	@ObjectClassDefinition(name = "Configuration SiteMap Servlet", description = "Configuration SiteMap Servlet")
	public static @interface Config {
		@AttributeDefinition(name = "blockedUrls", description = "Blocked urls in Site Map")
		String[] blockedUrls() default { "" };
		@AttributeDefinition(name = "dhoTemplatePaths", description = "List of Allowed Dho template paths")
		String[] dhoTemplatePaths() default  { "" } ;
		@AttributeDefinition(name = "isDhoAllowed", description = "Flag to enable or disable the dho sitemap generation")
		boolean isDhoAllowed() default true;
	}

	@Override
	protected void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response)
			throws ServletException, IOException {
		processRequest(request, response);

	}

	@Override
	protected void doPost(final SlingHttpServletRequest request, final SlingHttpServletResponse response)
			throws ServletException, IOException {
		processRequest(request, response);
	}
	
	@Reference
	private transient ResourceResolverFactory resolverFactory;
	
	@Activate
	protected void activate(final Config config) {
		blockedURlsInOsgi = config.blockedUrls();
		/**
		* fix for resolving sonarqube issue (squid:S1948) to 
		* make "dhoTemplatePaths" transient or serializable.
		*/
		Collections.addAll(dhoTemplatePaths, config.dhoTemplatePaths());
		isDhoAllowed = config.isDhoAllowed();
	}

	/**
	 * The main method which does all processing for rendering the sitemap. It also
	 * apply all necessary filters to ensure denial of service attack is mitigated.
	 * 
	 * @param slingRequest
	 * @param slingResponse
	 * @throws ServletException
	 * @throws IOException
	 */
	protected void processRequest(final SlingHttpServletRequest slingRequest,
			final SlingHttpServletResponse slingResponse) throws ServletException, IOException {
		
		slingResponse.setContentType(slingRequest.getResponseContentType());
		ResourceResolver resourceResolver = slingRequest.getResourceResolver();
		PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
		Page page = pageManager.getContainingPage(slingRequest.getResource());
		XMLOutputFactory outputFactory = XMLOutputFactory.newFactory();
		ResourceResolver contentFragmentResolver = null;
		Map<String, Object> param = new HashMap<String, Object>();
		param.put(ResourceResolverFactory.SUBSERVICE, DHO_CONTENT_FRAGMENT_SERVICE);
		try {
			contentFragmentResolver = resolverFactory.getServiceResourceResolver(param);
			XMLStreamWriter stream = outputFactory.createXMLStreamWriter(slingResponse.getWriter());
			writeHeaderXML(stream);
			Resource pageContentresource = page.getContentResource();
			if (pageContentresource != null) {
				String resType = pageContentresource.getResourceType();
				LOGGER.debug("sitemap processing started");
				LOGGER.debug("sitemap reource type:: {}.", pageContentresource.getResourceType());
				if (GlobalConstants.HOME_PAGE_RES_TYPE.contains(resType) && GlobalConstants.SITEMAP_PAGES
						.equalsIgnoreCase(slingRequest.getRequestPathInfo().getSelectorString())) {
					LOGGER.debug("sitemap handlePages started");
					handlePages(slingRequest, slingResponse, resourceResolver, page, stream, contentFragmentResolver);
					LOGGER.debug("sitemap handlePages finished");
				} else if (GlobalConstants.SITEMAP_ASSETS
						.equalsIgnoreCase(slingRequest.getRequestPathInfo().getSelectorString())) {
					LOGGER.debug("sitemap handleAssets started");
					stream.writeNamespace("image", IMAGE_NS);
					handleAssets(resourceResolver, page, stream, slingRequest);
					LOGGER.debug("sitemap handleAssets ended");
				}
			}
			stream.writeEndElement();
			stream.writeEndDocument();
			LOGGER.debug("sitemap processing finished");
		} catch (XMLStreamException | LoginException e) {
			throw new IOException(e);
		} finally {
			 LOGGER.debug("In finally block contentFragmentResolver :{}", contentFragmentResolver);
			if(contentFragmentResolver!= null && contentFragmentResolver.isLive()) {
				LOGGER.debug("Closing contentFragmentResolver");
				contentFragmentResolver.close();
				LOGGER.debug("Closed contentFragmentResolver");
			}
			
			if(resourceResolver.isLive()) {
				resourceResolver.close();
			}
		}

	}

	/*
	 * This method handles rendering of assets in site-map from DAM.
	 */
	private void handleAssets(ResourceResolver resourceResolver, Page page, XMLStreamWriter stream,
			final SlingHttpServletRequest slingRequest) throws XMLStreamException {
		// writing the top page
		stream.writeStartElement(NS, "url");
		String loc = LinkUtil.getAbsoluteURL(slingRequest, page.getPath(), page);
		writeElement(stream, "loc", loc);

		for (Resource assetFolder : getAssetFolders(page, resourceResolver)) {
			writeAssets(stream, assetFolder, resourceResolver, slingRequest, page);
		}
		stream.writeEndElement();
	}

	/**
	 * This method handles rendering of page url's in sitemap.
	 * 
	 * @param slingRequest
	 * @param slingResponse
	 * @param resourceResolver
	 * @param page
	 * @param stream
	 * @param contentFragmentResolver
	 * @throws XMLStreamException
	 */
	private void handlePages(final SlingHttpServletRequest slingRequest, final SlingHttpServletResponse slingResponse,
			ResourceResolver resourceResolver, Page page, XMLStreamWriter stream, ResourceResolver contentFragmentResolver) throws XMLStreamException {
		// first do the current page
		write(page, stream, resourceResolver, slingRequest);
		for (Iterator<Page> children = page.listChildren(new PageFilter(), true); children.hasNext();) {
			Page chidPage = children.next();
			if (!isUrlBlocked(chidPage.getPath())) {
				Resource resource = chidPage.getContentResource();
				if (resource != null) {
					if (WCMUseUtil.getSearchable(resource).equalsIgnoreCase(GlobalConstants.YES)) {
						
						/*logic to identify dho page and processs it
						 * isDhoPageProcessingAllowed() 
						 * checks below two condition,
						 * 1)IS_DHO_ALLOWED is a flag to turn on or off the generation of sitemap for dho dynamic content fragment page
						 * 2)check whether the page is a  dho  dynamic content fragment page
						 * 
						 * contentFragmentResolver != null
						 * If the resource resolver for content fragment is not obtained then stop processing the DHO Page
						 */
						if (isDhoPageProcessingAllowed(chidPage) && contentFragmentResolver != null && contentFragmentResolver.isLive()) {
							LOGGER.info("DHO processing is allowed and obtained resource resolver for Content Fragment - page:{}", chidPage.getPath());
								try {
									processDhoPage(slingRequest, contentFragmentResolver, stream, chidPage);
								} catch (IllegalStateException | SlingException e ) {
									/**
									 * This catch block is added so that dho page exception will be logged, but processing of 
									 * kp content pages can continue
									 */
									LOGGER.debug("Exception occured in processing dho page :{}", chidPage.getPath());
									LOGGER.error("Exception :{}", e);
								}
						} else {
							write(chidPage, stream, resourceResolver, slingRequest);
						}
						
					} else {
						continue;
					}
				}
			}
		}
	}

	
	
	/**
	 * This method checks if the current path/folder is not in blocked list of
	 * sitemap. If true, then that page or pages in that folder will not be included
	 * in the generated sitemap. This rule is only applicable for all top level
	 * pages/folders.
	 *
	 */
	boolean isUrlBlocked(String path) {
		if (blockedURlsInOsgi != null) {
			for (String url : blockedURlsInOsgi) {
				if (path.matches(CONTENT_PATH_PREFIX + url + CONTENT_PATH_CHILD_SUFFIX)
						|| path.matches(CONTENT_PATH_PREFIX + url + CONTENT_PATH_PAGE_SUFFIX)) {
					return true;
				}
			}
		}
		return false;
	}

	private void writeHeaderXML(XMLStreamWriter stream) throws XMLStreamException {
		stream.writeStartDocument("1.0");
		stream.writeStartElement("", "urlset", NS);
		stream.writeNamespace("", NS);
	}

	private void write(Page page, XMLStreamWriter stream, ResourceResolver resolver,
			final SlingHttpServletRequest request) throws XMLStreamException {
		stream.writeStartElement(NS, "url");

		String loc = LinkUtil.getAbsoluteURL(request, page.getPath(), page);
		writeElement(stream, "loc", loc);
		Calendar cal = page.getLastModified();
		if (cal != null) {
			writeElement(stream, "lastmod", DATE_FORMAT.format(cal));
		}

		stream.writeEndElement();
	}

	private void writeElement(final XMLStreamWriter stream, final String elementName, final String text)
			throws XMLStreamException {
		stream.writeStartElement(NS, elementName);
		stream.writeCharacters(text);
		stream.writeEndElement();
	}

	private void writeImageElement(final XMLStreamWriter stream, final String text) throws XMLStreamException {
		stream.writeStartElement(IMAGE_NS, "image");
		stream.writeStartElement(IMAGE_NS, "loc");
		stream.writeCharacters(text);
		stream.writeEndElement();
		stream.writeEndElement();
	}

	private void writeAsset(Asset asset, XMLStreamWriter stream, ResourceResolver resolver,
			final SlingHttpServletRequest slingRequest, Page page) throws XMLStreamException {

		String loc = LinkUtil.getExternalURL(slingRequest, asset.getPath(), page);

		writeImageElement(stream, loc);
		Resource contentResource = asset.adaptTo(Resource.class).getChild("/" + JcrConstants.JCR_CONTENT);
		if (contentResource != null) {
		}
	}

	private void writeAssets(final XMLStreamWriter stream, final Resource assetFolder, final ResourceResolver resolver,
			final SlingHttpServletRequest slingRequest, Page page) throws XMLStreamException {
		for (Iterator<Resource> children = assetFolder.listChildren(); children.hasNext();) {
			Resource assetFolderChild = children.next();
			if (assetFolderChild.isResourceType(DAM_ASSET)) {
				Resource assetJcrResource = resolver.getResource(assetFolderChild.getPath() + "/jcr:content");
				ValueMap property = assetJcrResource.adaptTo(ValueMap.class);
				Boolean isContentFragment = property.get("contentFragment", Boolean.FALSE);
				if (!isContentFragment) {
					Asset asset = assetFolderChild.adaptTo(Asset.class);
					if (StringUtils.isBlank(asset.getMimeType())) {
						LOGGER.error("Asset mime type not available ---------" + asset.getPath());
					} else {
						if (asset.getMimeType().startsWith("image/")) {
							writeAsset(asset, stream, resolver, slingRequest, page);
						}
					}
				}
			} else {
				writeAssets(stream, assetFolderChild, resolver, slingRequest, page);
			}
		}
	}

	private Collection<Resource> getAssetFolders(Page page, ResourceResolver resolver) {
		List<Resource> allAssetFolders = new ArrayList<Resource>();
		String configuredAssetFolderPath = KP_CONTENT_DAM_PATH;
		Resource assetFolder = resolver.getResource(configuredAssetFolderPath);
		if (assetFolder != null) {
			allAssetFolders.add(assetFolder);
		}
		return allAssetFolders;
	}
	
	/**
	 * Method to process the pages created using dynamic content fragment template.
	 * Dynamic content fragment template execution type dynamic will be accessed via selector
	 * selector is the name of the fragment
	 *
	 * @param slingRequest
	 * @param resourceResolver
	 * @param stream
	 * @param childPage
	 * @throws XMLStreamException
	 */
	private void processDhoPage(final SlingHttpServletRequest slingRequest, ResourceResolver resourceResolver,
			XMLStreamWriter stream, Page childPage) throws XMLStreamException, IllegalStateException, SlingException {
	   LOGGER.debug("processDhoPage entry page: {}", childPage.getPath());
	   if(childPage.getContentResource(DCF_COMPONENT_NAME)!= null) {
		   LOGGER.debug("dynamic content fragment component found");
		Resource dcfResource = childPage.getContentResource(DCF_COMPONENT_NAME);
		if (dcfResource!= null && dcfResource.getValueMap() !=null) {
			ValueMap dcfValueMap = dcfResource.getValueMap();
			LOGGER.debug("dynamic content fragment value map :{}", dcfValueMap);
			if(dcfValueMap.get(DCF_COMPONENT_PROP_EXECUTION_TYPE, String.class)!= null) {
				String executionType = dcfValueMap.get(DCF_COMPONENT_PROP_EXECUTION_TYPE, String.class);
				LOGGER.debug("Execution type :{}", executionType);
				if(StringUtils.equals(DCF_COMPONENT_PROP_EXECUTION_TYPE_DYNAMIC, executionType)) {
					processDynamicDhoPage(slingRequest, resourceResolver, stream, childPage, dcfValueMap);
				} else {
					// not a dynamic page, to be considered as normal page
					LOGGER.debug("Execution type is static to be processed using existing method:{}", executionType);
					write(childPage, stream, resourceResolver, slingRequest);
				}
				LOGGER.debug("Exiting processDhoPage");
				}
			}
	   	}
	}

	/**
	 * Method to get the list of path configured
	 * @param slingRequest
	 * @param resourceResolver
	 * @param stream
	 * @param childPage
	 * @param dcfValueMap
	 * @throws XMLStreamException
	 */
	private void processDynamicDhoPage(final SlingHttpServletRequest slingRequest, ResourceResolver resourceResolver,
			XMLStreamWriter stream, Page childPage, ValueMap dcfValueMap) throws XMLStreamException, IllegalStateException, SlingException {
		String fragmentPath = dcfValueMap.get(DCF_COMPONENT_PROP_FRAGMENT_PATH, String.class);
		LOGGER.debug("fragmentPath :{}", fragmentPath);
		String additionalPaths [] = null;
		
		Set<String> parentPaths = new HashSet<String>();
		if(StringUtils.isNotBlank(fragmentPath)) {
			parentPaths.add(fragmentPath);
		}
		
		if(dcfValueMap.get(DCF_COMPONENT_PROP_PARENT_PATHS, String[].class) != null) {
			additionalPaths = dcfValueMap.get(DCF_COMPONENT_PROP_PARENT_PATHS, String[].class);
			LOGGER.debug("Additional Path is type of String array");
		}
		else if(dcfValueMap.get(DCF_COMPONENT_PROP_PARENT_PATHS, String.class) != null){
			parentPaths.add(dcfValueMap.get(DCF_COMPONENT_PROP_PARENT_PATHS, String.class));
			LOGGER.debug("Additional Path is type of String");
		}
		
		if((additionalPaths!= null && additionalPaths.length > 0)) {
			parentPaths.addAll(Arrays.asList(additionalPaths));
		}
		
		LOGGER.debug("parentPaths :{}", parentPaths);
		if(parentPaths.size() > 0) {
			resolveFragmentPaths(slingRequest, resourceResolver, stream, childPage, parentPaths);
		}
	}

	/**
	 * Method to resolve the configured path and fetch the content fragment properties
	 * @param slingRequest
	 * @param resourceResolver
	 * @param stream
	 * @param childPage
	 * @param parentPaths
	 * @throws XMLStreamException
	 */
	private void resolveFragmentPaths(final SlingHttpServletRequest slingRequest, ResourceResolver resourceResolver,
			XMLStreamWriter stream, Page childPage, Set<String> parentPaths) throws XMLStreamException, IllegalStateException, SlingException {
		for (String path : parentPaths) {
			LOGGER.debug("processing parent path :{}", path);
			Resource fragmentResource = resourceResolver.getResource(path);
			LOGGER.debug("Resource after resolving path :{}", fragmentResource);
			if (fragmentResource != null) {
				
				processContentFragment(slingRequest, resourceResolver, stream, childPage, fragmentResource);
			}
		}
	}

	/**
	 * Method to Check whether the fragment path is a content fragment or a folder and proceed based on that.
	 * @param slingRequest
	 * @param resourceResolver
	 * @param stream
	 * @param childPage
	 * @param fragmentResource
	 * @throws XMLStreamException
	 */
	private void processContentFragment(final SlingHttpServletRequest slingRequest, ResourceResolver resourceResolver,
			XMLStreamWriter stream, Page childPage, Resource fragmentResource) throws XMLStreamException, IllegalStateException, SlingException {
		Resource fragmentJcrResource = fragmentResource.getChild(JCR_CONTENT);
		if (fragmentJcrResource != null 
				&& fragmentJcrResource.getValueMap()!=null
				&& fragmentJcrResource.getValueMap().containsKey(PROP_CONTENT_FRAGMENT)
				&& fragmentJcrResource.getValueMap().get(PROP_CONTENT_FRAGMENT, Boolean.class)) {
			/**
			 * This if condition is if author has given a fragment path which itself is a 
			 * content fragment rather than a folder or parent node under which content 
			 * fragments are present
			 */
			LOGGER.debug("parent path has no children and checking whether this path itself is a content fragment");
			getContentFragmentDetails(slingRequest, resourceResolver, stream, childPage, fragmentResource,
					fragmentJcrResource,  fragmentJcrResource.getValueMap());
		} 
		else if(fragmentResource.hasChildren()) {
			/**
			 * This else if conditions is if a parent path is given and under parent path there are content fragments
			 */
			LOGGER.debug("parent path has children");
			Iterator<Resource> childResources = fragmentResource.listChildren();
			{
				while (childResources.hasNext()) {
					Resource childResource = childResources.next();
					if (childResource != null) {
						checkAndProcessContentFragment(slingRequest, resourceResolver, stream, childPage,
								childResource);
					}
				}
			}

		}
	}

	/**
	 * Method to check whether the resource is a content fragment, if so get the name and last modified time
	 * name is used as a selector which will be appended to the page path
	 * @param slingRequest
	 * @param resourceResolver
	 * @param stream
	 * @param childPage
	 * @param fragmentResource
	 * @throws XMLStreamException
	 */
	private void checkAndProcessContentFragment(final SlingHttpServletRequest slingRequest, ResourceResolver resourceResolver,
			XMLStreamWriter stream, Page childPage, Resource fragmentResource) throws XMLStreamException, IllegalStateException, SlingException {
		LOGGER.debug("Page path:{}", childPage.getPath());
		LOGGER.debug("fragmentResource path:{}", fragmentResource.getPath());
		Resource fragmentJcrResource = fragmentResource.getChild(JCR_CONTENT);
		if(fragmentJcrResource!= null && fragmentJcrResource.getValueMap()!=null) {
			
			ValueMap fragmentJcrValueMap = fragmentJcrResource.getValueMap();
			if(fragmentJcrValueMap.containsKey(PROP_CONTENT_FRAGMENT)) {
				boolean isContentFragment = fragmentJcrValueMap.get(PROP_CONTENT_FRAGMENT, Boolean.class);
				if(isContentFragment) {
					getContentFragmentDetails(slingRequest, resourceResolver, stream, childPage, fragmentResource,
							fragmentJcrResource, fragmentJcrValueMap);
				}
			}
		}
						
	}

	/**
	 * Method toget the last modified time and page path
	 * @param slingRequest
	 * @param resourceResolver
	 * @param stream
	 * @param childPage
	 * @param fragmentResource
	 * @param fragmentJcrResource
	 * @param fragmentJcrValueMap
	 * @throws XMLStreamException
	 */
	private void getContentFragmentDetails(final SlingHttpServletRequest slingRequest,
			ResourceResolver resourceResolver, XMLStreamWriter stream, Page childPage, Resource fragmentResource,
			Resource fragmentJcrResource, ValueMap fragmentJcrValueMap) throws XMLStreamException {
		LOGGER.debug("Content Fragment path:{}", fragmentResource.getPath());
		String selector = fragmentResource.getName();
		Calendar lastModifiedCal = null;
		if(fragmentJcrValueMap.containsKey(PROP_JCR_LAST_MODIFIED)) {
			lastModifiedCal = fragmentJcrResource.getValueMap().get(PROP_JCR_LAST_MODIFIED, Calendar.class);
		}
		if(isRegionAllowedForCF(fragmentJcrResource, childPage)) {
			LOGGER.debug("Current page region allowed for content fragment");
			writeDynamicDhoPage(childPage, stream, resourceResolver, slingRequest, selector, lastModifiedCal);
		}
	}
	
	
	/**
	 * Method to write the path of the dho page and last modified time of the content 
	 * fragment associated associated with the selector of dho page.
	 * @param page
	 * @param stream
	 * @param resolver
	 * @param request
	 * @param selector
	 * @param lastModifiedCal
	 * @throws XMLStreamException
	 */
	private void writeDynamicDhoPage(Page page, XMLStreamWriter stream, ResourceResolver resolver,
			final SlingHttpServletRequest request, String selector, Calendar lastModifiedCal) throws XMLStreamException {
		LOGGER.debug("writeDynamicDhoPage entry");
		stream.writeStartElement(NS, "url");
		LOGGER.debug("selector:{}", selector);
		String path = new StringBuilder().append(page.getPath()).append(DOT_CONSTANT).append(selector).toString();
		LOGGER.debug("Page path:{}", path);
		String loc = LinkUtil.getAbsoluteURL(request, path, page);
		if(GenericUtil.isAuthorMode() && !loc.endsWith(CONSTANT_SINGLE_SLASH)) {
			loc = new StringBuilder().append(loc).append(DOT_HTML_EXTENSION).toString();
		}
		LOGGER.debug("Final Page path after link util call:{}", loc);
		writeElement(stream, "loc", loc);
		if (lastModifiedCal != null) {
			LOGGER.debug("lastModifiedCal:{}", lastModifiedCal);
			writeElement(stream, "lastmod", DATE_FORMAT.format(lastModifiedCal));
		}
		stream.writeEndElement();
		LOGGER.debug("writeDynamicDhoPage exit");
	}
	
	/**
	 * Method to check whether any regions is configured for content fragment,
	 * if so check whether the current page region matches with one of the regions
	 * configured.
	 * @param fragmentJcrResource
	 * @param childPage
	 * @return
	 */
	private boolean isRegionAllowedForCF(Resource fragmentJcrResource, Page childPage) {
		LOGGER.debug("isRegionAllowedForCF entry");
		Resource masterCFResource = fragmentJcrResource.getChild("data/master");
		if (masterCFResource == null) { return true; }
		ValueMap fragmentJcrValueMap = masterCFResource.getValueMap();
		if (fragmentJcrValueMap == null || 
				!fragmentJcrValueMap.containsKey(PROP_CONTENT_FRAGMENT_REGION)) { return true; }
		//allow page to be added when content fragment region is not configured
		String[] regionsAllowed = fragmentJcrValueMap.get(PROP_CONTENT_FRAGMENT_REGION, String[].class);
		if (regionsAllowed == null || regionsAllowed.length == 0)  { return true; }
		
		String pageRegion = childPage.getAbsoluteParent(RegionPicker.CUR_REGION_ROOT_LEVEL).getName();
		String searchSequence = new StringBuilder().append(CONSTANT_SINGLE_SLASH).append(pageRegion).toString();
		for (String region : regionsAllowed) {
			if (StringUtils.contains(region, searchSequence)) { return true; }
		}
		//return false when current region doesnt match with any of configured regions
		LOGGER.debug("Current page region is not allowed for content fragment");
		LOGGER.debug("isRegionAllowedForCF exit with value as false");
		return false;
	}
	
	
	/**
	 * Method to determine whether dho page processing with selector is allowed.
	 * IS_DHO_ALLOWED is a flag to turn on or off the generation of sitemap for dho dynamic content fragment page
	 * Second condition is to check whether the page is a  dho  dynamic content fragment page
	 * @param chidPage
	 * @return
	 */
	private boolean isDhoPageProcessingAllowed(Page chidPage) {
		LOGGER.debug("isDhoPageProcessingAllowed Method Entry");
		ValueMap pageValuMap = chidPage.getProperties();
		LOGGER.debug("dhoTemplatePaths from config:{}", dhoTemplatePaths);
		LOGGER.debug("DhoAllowed Flag from config :{}", isDhoAllowed);
		
		if (isDhoAllowed 
			&& pageValuMap != null && pageValuMap.containsKey(PROP_CQ_TEMPLATE)
				&& pageValuMap.get(PROP_CQ_TEMPLATE) != null && dhoTemplatePaths != null
				  && dhoTemplatePaths.contains(pageValuMap.get(PROP_CQ_TEMPLATE).toString())) {
			
			LOGGER.debug("dhoTemplatePaths size:{}", dhoTemplatePaths.size());
			LOGGER.debug("Template path:{}", pageValuMap.get(PROP_CQ_TEMPLATE).toString());
			LOGGER.debug("Dho Page Processing is ALLOWED");
			LOGGER.debug("isDhoPageProcessingAllowed Method Exit");
			
			return true;
		}
		LOGGER.debug("Dho Page Processing NOT ALLOWED");
		LOGGER.debug("isDhoPageProcessingAllowed Method Exit");
		return false;
	}
}
