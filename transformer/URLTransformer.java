package org.kp.foundation.core.transformer;

import java.io.IOException;
import java.util.Arrays;

import org.apache.cocoon.xml.sax.AbstractSAXPipe;
import org.apache.cocoon.xml.sax.AttributesImpl;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.rewriter.ProcessingComponentConfiguration;
import org.apache.sling.rewriter.ProcessingContext;
import org.apache.sling.rewriter.Transformer;
import org.kp.foundation.core.constants.GlobalConstants;
import org.kp.foundation.core.constants.TransformerConstants;
import org.kp.foundation.core.transformer.impl.URLTransformerFactoryImpl;
import org.kp.foundation.core.utils.TransformerUtil;
import org.kp.foundation.core.utils.WCMUseUtil;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;

/**
 * The Class Transformer. It removes the .html of the urls and replaces it with single forward slash
 * (/).
 */
//Waiver details on GSC-3626
@SuppressWarnings({"squid:S3776"})
public class URLTransformer extends AbstractSAXPipe implements Transformer {

  /** The Constant LOG. */
  private static final Logger LOG = LoggerFactory.getLogger(URLTransformer.class);
  /** The factory. */
  @Reference
  private final URLTransformerFactoryImpl factory;
  /** The in scope. */
  private boolean inScope;
  private static final String VALUE = "value";
  private static final String DATA_SRC = "data-src";
  private static final String ATTRIBUTE_CLASS = "class";
  private static final String CDATA = "CDATA";
  private static final String DISABLE_LINKS = "disableLinks";
  private static final String TELEPHONE_STRING = "tel:800";
  private static final String ATTRIBUTE_TITLE_LABEL = "title";
  private static final String ATTRIBUTE_TARGET_LABEL = "target";
  private static final String ATTRIBUTE_TARGET_VALUE = "_blank";
  private static final String ATTRIBUTE_ARIA_LABEL = "aria-label";
  private static final String ES = "es";
  private String EDITABLE_TEMPLATE_PATH  = "/settings/wcm/templates/";
  private String enabledTemplate = "/conf/kporg/";
  private String requestedServerName = null;
  private int languageDepthValue = 4;
  private String primaryLang = null;

  private ContentHandler contentHandler;
  private boolean externalLink = false;

  /** The kp domains paths. */
  private String[] kpdomainList;
  private String[] kpExternal;
  private String[] kplinkdialog;

  private String[] selectors;
  private boolean mobileSelector = false;
  private boolean disableLinks = false;
  boolean disableTelephoneLinks = true;
  boolean newWindowFlag=false;
  String pageLanguage;
  boolean internalpathFlag = false;
  boolean modalWindowFlag = false;
  String titleAttributeValue = "";
  private Page page;
  /**
   * Instantiates a new DMFC transformer.
   * 
   * @param newFactory the factory
   */
  public URLTransformer(URLTransformerFactoryImpl newFactory) {
    super();
    this.factory = newFactory;
  }

  /**
   * Instantiates a new DMFC transformer.
   * 
   * @param processingContext the ProcessingContext
   * @param config the ProcessingComponentConfiguration.
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public void init(ProcessingContext processingContext, ProcessingComponentConfiguration config)
      throws IOException {
    LOG.debug("Entry into init method");
    SlingHttpServletRequest request = processingContext.getRequest();
//  Get language for the page
    Resource resource = request.getResource();
    PageManager pMgr = resource.getResourceResolver().adaptTo(PageManager.class);
    page = pMgr.getContainingPage(resource);
    if(null != page) {
      pageLanguage = WCMUseUtil.getCurrentLanguage(page);
    }
    requestedServerName = request.getServerName();
    selectors = request.getRequestPathInfo().getSelectors();
    for (String sel : selectors) {
      if (sel.equalsIgnoreCase(GlobalConstants.MOBILE)) {
        mobileSelector = true;
      }
    }
    disableLinks = TransformerUtil.getPagePropertyValue(request, DISABLE_LINKS);
    LOG.debug("requestedServerName " + requestedServerName);
    if (null != factory && null != factory.getRootPaths() && factory.getRootPaths().length > 0) {
      LOG.debug("Service returned an instance of Transformer");
      for (String sitePath : factory.getRootPaths()) {
        if (StringUtils.isNotEmpty(sitePath) && null != processingContext.getRequest()
            && null != processingContext.getRequest().getResource()
            && processingContext.getRequest().getResource().getPath().startsWith(sitePath)) {
          LOG.debug("Selected Root Path is " + sitePath);
          inScope = true;
          break;
        }
      }
    }
    if (null != factory && null != factory.getBlacklistDomainList()
        && factory.getBlacklistDomainList().length > 0) {
      for (String blacklistDomain : factory.getBlacklistDomainList()) {
        if (StringUtils.isNotEmpty(blacklistDomain) && null != requestedServerName
            && requestedServerName.equals(blacklistDomain)) {
          LOG.debug("black list Domain name is " + blacklistDomain);
          inScope = false;
          break;
        }
      }
    }
    // read KP Domains
    kpdomainList = factory.getKPDomains();
    kpExternal = factory.getKPExternal();
    kplinkdialog = factory.getKPLinkDialog();
    LOG.debug("Exit from init method");
  }

  /**
   * Start element.
   * 
   * @param uri the uri
   * @param loc the loc
   * @param raw the raw
   * @param attributes the attributes
   * @throws SAXException the SAX exception
   */
  @Override
  public void startElement(String uri, String loc, String raw, Attributes attributes)
      throws SAXException {
    LOG.debug("Entry into startElement method");
    AttributesImpl attrs = new AttributesImpl(attributes);
    boolean dataskipfound = false;
    boolean imageElementFlag = false;
    for (int i = 0; i < attrs.getLength(); i++) {
      String name = attrs.getLocalName(i);
      String attrValue = attrs.getValue(i);
      if(null != attrValue) {
        attrValue = attrValue.replace(" " , "");
      }
      if(null != attrValue && attrValue.startsWith(TELEPHONE_STRING)) {
          disableTelephoneLinks = false;
      }
      modalWindowFlag = false;
      if (kplinkdialog != null) {
        for (String s : kplinkdialog) {
          if((null != name && name.equalsIgnoreCase(s) && attrValue.equalsIgnoreCase("true")) || (null != attrValue && attrValue.contains(s))) {
	            attrs.removeAttribute(ATTRIBUTE_ARIA_LABEL);
	            addTitleAttributeForDialog(attrs);
	            modalWindowFlag = true;
            }
          }
        }
      
      if ((raw.equalsIgnoreCase(TransformerConstants.ANCHOR_TAG_NAME) && mobileSelector && disableLinks && disableTelephoneLinks)) {
          attrs.removeAttribute(TransformerConstants.HREF);
      }
      
      if(raw.equalsIgnoreCase(TransformerConstants.IMG_TAG_NAME)) {
    	  imageElementFlag = true;
      }
      
      if (!dataskipfound) {
        for (int j = 0; j < attrs.getLength(); j++) {
          String attributeName = attrs.getLocalName(j);
          String attrValueTemp = attrs.getValue(j);
          if(null != attrValueTemp && attrValueTemp.equalsIgnoreCase(ATTRIBUTE_TARGET_VALUE)) {
            newWindowFlag = true;
          }
          if (TransformerConstants.DATA_SKIP_EXT_ICON.equalsIgnoreCase(attributeName)) {
            dataskipfound = true;
            break;
          }
        }
      }
      if (TransformerConstants.HREF.equalsIgnoreCase(name)
          || TransformerConstants.ACTION.equalsIgnoreCase(name)
          || TransformerConstants.SRC.equalsIgnoreCase(name) || VALUE.equalsIgnoreCase(name)
          || DATA_SRC.equals(name)) {
        String resourcePath = attrs.getValue(i);
        if (StringUtils.isNotEmpty(resourcePath)) {
          LOG.debug(" resourcePath :::::::::::  " + resourcePath);
          if (resourcePath.startsWith(TransformerConstants.CONTENT_DAM)) { // code for content/dam
                                                                           // Resource
          } else if (resourcePath.contains("_poc")) { // code for content Resource
            resourcePath = resourcePath.substring(0, resourcePath.indexOf("_poc")) + "poc"
                + (resourcePath.substring(resourcePath.indexOf("_poc") + 4, resourcePath.length()))
                    .replaceFirst("_", ":");
            LOG.debug("POC resourcePath :::::::::::  " + resourcePath);
          } else if (StringUtils.contains(resourcePath,
              "/" + GlobalConstants.PORTAL_HEALTH_URL + "/")
              || StringUtils.contains(resourcePath, "/" + GlobalConstants.PORTAL_SYSTEM_URL + "/")
              || StringUtils.contains(resourcePath,
                  "/" + GlobalConstants.PORTAL_STATIC_URL + "/")) {
            resourcePath = StringUtils.replace(resourcePath, GlobalConstants.HTML_SUFFIX, "");
            LOG.debug("Portal resourcePath :::::::::::  " + resourcePath);
          } else if (resourcePath.startsWith(TransformerConstants.CONTENT)
              && resourcePath.endsWith(TransformerConstants.HTML)) { // code for content Resource
            resourcePath = StringUtils.replace(resourcePath, TransformerConstants.HTML, "");
            LOG.debug("new  resourcePath :::::::::::  " + resourcePath);
          } else if (resourcePath.startsWith(TransformerConstants.ETC)) { // code for etc Resource
          } else if (resourcePath.startsWith(TransformerConstants.CONTENT)
              && resourcePath.endsWith(TransformerConstants.DOT_JS)) { // code for etc Resource
          }

          if (inScope) {
            String url = TransformerConstants.EMPTY_STRING;
            url = TransformerUtil.validateURL(attrs.getValue(i), primaryLang, languageDepthValue);
            url = (url.endsWith(TransformerConstants.DOUBLE_SLASH))
                ? url.replace(TransformerConstants.DOUBLE_SLASH, TransformerConstants.SINGLE_SLASH)
                : url;
            LOG.debug(" Before ser Attrs url  :::::: " + url);
            attrs.setValue(i, url);
          } else {
            String url = TransformerConstants.EMPTY_STRING;
            url = attrs.getValue(i);
            attrs.setValue(i, url);
          }
          // if href is not in list of KP domains add extra code.
          Boolean internalpath = false;
          if (TransformerConstants.HREF.equalsIgnoreCase(name)
              && resourcePath.toLowerCase().startsWith("http")
              && raw.equalsIgnoreCase(TransformerConstants.ANCHOR_TAG_NAME)) {
            // only href attribute, anchor links and starts with http(s) to simplyfy
            if (kpdomainList != null) {
              for (String s : kpdomainList) {
                internalpath = resourcePath.matches(".*" + s + ".*");
                if (internalpath)
                  break;
              }
            } else {
              // if for any reason the domains are not configured treat every link as internal.
              internalpath = true;
            }
            if (internalpath) {
              Boolean overrideExternalLink = false;
              // When kpExternal is defined, it contains the list of links, which should actually
              // be considered as external eventhough it is part of the kp subdomain
              if (kpExternal != null) {
                for (String s : kpExternal) {
                  overrideExternalLink = resourcePath.matches(".*" + s + ".*");
                  if (overrideExternalLink) {
                    internalpath = false;
                    break;
                  }
                }
              }
            }
            if (!dataskipfound) {
//            if author somehow misses to select new window for external links, this check will make sure that all of them should open in a new window.
              if(!internalpath && !newWindowFlag) {
                newWindowFlag = true;
                attrs.removeAttribute(ATTRIBUTE_TARGET_LABEL);
                attrs.addAttribute("", ATTRIBUTE_TARGET_LABEL, ATTRIBUTE_TARGET_LABEL, CDATA, ATTRIBUTE_TARGET_VALUE);
              }
              if (!internalpath && newWindowFlag) {
                externalLink = true;
                attrs.addAttribute("", ATTRIBUTE_CLASS, ATTRIBUTE_CLASS, CDATA, "external-link");
                addTitleAttributeForExternalLinks(attrs, pageLanguage);
              }else if(internalpath && newWindowFlag) {
                internalpathFlag = true;
                addAttribute(attrs);
              }
            }else if(!internalpath && dataskipfound) {
              newWindowFlag = true;
              attrs.removeAttribute(ATTRIBUTE_TARGET_LABEL);
              attrs.addAttribute("", ATTRIBUTE_TARGET_LABEL, ATTRIBUTE_TARGET_LABEL, CDATA, ATTRIBUTE_TARGET_VALUE);
              addTitleAttributeForExternalLinks(attrs, pageLanguage);
            }
          }else if(newWindowFlag && !modalWindowFlag && !imageElementFlag){
            internalpathFlag = true;
            addAttribute(attrs);
          }
        }
      }
    }
    if ((raw.equalsIgnoreCase(TransformerConstants.ANCHOR_TAG_NAME) && mobileSelector && disableLinks && disableTelephoneLinks)) {
          super.startElement(uri, loc, raw, attrs);
    }else{
          disableTelephoneLinks = true;
          contentHandler.startElement(uri, loc, raw, attrs);
          super.startElement(uri, loc, raw, attrs);
    }
    LOG.debug("Exit from startElement method");
  }

  /**
   * Dispose.
   */
  public void dispose() {
    LOG.debug("Entry into dispose method");
  }

  @Override
  public void setContentHandler(ContentHandler handler) {
    this.contentHandler = handler;
  }

  @Override
  public void endElement(String uri, String localName, String qName) throws SAXException {
    if ( externalLink || newWindowFlag || internalpathFlag) {
      internalWrapElemStart();
      internalWrapElemEnd();
      externalLink = false;
      newWindowFlag = false;
      internalpathFlag = false;
    } 
     contentHandler.endElement(uri, localName, qName);
  }

  private void internalWrapElemStart() throws SAXException {
    AttributesImpl attrs = new AttributesImpl();
    if(externalLink) {
    	getLinkIcon(attrs,"icon-zzz0027-link-external","icon-link-out");
    }else if(internalpathFlag && newWindowFlag){
    	getLinkIcon(attrs,"icon-zzz0028-link-internal","icon-zz009new-window");
    }
    attrs.addAttribute("", " aria-hidden", " aria-hidden", CDATA, "true");
    contentHandler.startElement("", "i", "i", attrs);
  }

  private void internalWrapElemEnd() throws SAXException {
    contentHandler.endElement("", "i", "i");
  }
  
  public void addAttribute(AttributesImpl attrs) throws SAXException{
    newWindowFlag = false;
    attrs.addAttribute("", ATTRIBUTE_CLASS, ATTRIBUTE_CLASS, CDATA, "new-window-link");
    attrs = checkIfTitleAttributeExists(attrs);    
    if(null != pageLanguage && pageLanguage.equalsIgnoreCase(ES)) {
      attrs.addAttribute("", ATTRIBUTE_TITLE_LABEL, ATTRIBUTE_TITLE_LABEL, CDATA, titleAttributeValue + "Abre una Ventana Nueva");
    }else {
      attrs.addAttribute("", ATTRIBUTE_TITLE_LABEL, ATTRIBUTE_TITLE_LABEL, CDATA, titleAttributeValue + "Opens in a new window");
    }
  }

  public void addTitleAttributeForDialog(AttributesImpl attrs) throws SAXException{
    attrs = checkIfTitleAttributeExists(attrs);
    if(null != pageLanguage && pageLanguage.equalsIgnoreCase(ES)) {
      attrs.addAttribute("", ATTRIBUTE_TITLE_LABEL, ATTRIBUTE_TITLE_LABEL, CDATA, titleAttributeValue + "Abre un Cuadro de Diálogo");
    }else {
      attrs.addAttribute("", ATTRIBUTE_TITLE_LABEL, ATTRIBUTE_TITLE_LABEL, CDATA, titleAttributeValue + "Opens a Dialog");
    }
  }
  
  public void addTitleAttributeForExternalLinks(AttributesImpl attrs, String pageLanguage) throws SAXException{
    attrs = checkIfTitleAttributeExists(attrs);
    if(null != pageLanguage && pageLanguage.equalsIgnoreCase(ES)) {
      attrs.addAttribute("", ATTRIBUTE_TITLE_LABEL, ATTRIBUTE_TITLE_LABEL, CDATA, titleAttributeValue + "Abre una Ventana Nueva, externo");
    }else {
      attrs.addAttribute("", ATTRIBUTE_TITLE_LABEL, ATTRIBUTE_TITLE_LABEL, CDATA, titleAttributeValue + "Opens in a new window, external");
    }
  }
  
  public AttributesImpl checkIfTitleAttributeExists(AttributesImpl attrs) {
    titleAttributeValue = "";
    for (int i = 0; i < attrs.getLength(); i++) {
      if (attrs.getLocalName(i).equalsIgnoreCase(ATTRIBUTE_TITLE_LABEL)) {
        titleAttributeValue = attrs.getValue(i) + ", ";
        attrs.removeAttribute(ATTRIBUTE_TITLE_LABEL);
      }
    }
    return attrs;
  }
  
  /**
   * This method adds the icon for links based on the type of template either editable or static
   * @param attrs
   * @param editableTemplateIcon
   * @param staticTemplateIcon
   */
	private void getLinkIcon(AttributesImpl attrs, String editableTemplateIcon, String staticTemplateIcon) {
		if (null != page && isEditable()) {
			attrs.addAttribute("", ATTRIBUTE_CLASS, ATTRIBUTE_CLASS, CDATA, editableTemplateIcon);
		} else {
			attrs.addAttribute("", ATTRIBUTE_CLASS, ATTRIBUTE_CLASS, CDATA, staticTemplateIcon);
		}
	}
  
  /**
   * This method reads the page property of cq:template and results is it a editable template or not.
   * @return
   */
  private boolean isEditable() {
    String templatePath = this.page.getProperties().get("cq:template").toString();
    return (templatePath.contains(enabledTemplate) && templatePath.contains(EDITABLE_TEMPLATE_PATH)) ? true : false;
  }
  
  @Override
  public void characters(char[] ch, int start, int length) throws SAXException {
      contentHandler.characters(ch, start, length);
  }
  
  @Override
  public void endDocument() throws SAXException {
    contentHandler.endDocument();
  }

  public void endPrefixMapping(String prefix) throws SAXException {
    contentHandler.endPrefixMapping(prefix);
  }

  public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
    contentHandler.ignorableWhitespace(ch, start, length);
  }


  @Override
  public void processingInstruction(String target, String data) throws SAXException {
    contentHandler.processingInstruction(target, data);
  }

  @Override
  public void setDocumentLocator(Locator locator) {
    contentHandler.setDocumentLocator(locator);
  }

  @Override
  public void skippedEntity(String name) throws SAXException {
    contentHandler.skippedEntity(name);
  }

  @Override
  public void startDocument() throws SAXException {
    contentHandler.startDocument();
  }

  @Override
  public void startPrefixMapping(String prefix, String uri) throws SAXException {
    contentHandler.startPrefixMapping(prefix, uri);
  }
}
