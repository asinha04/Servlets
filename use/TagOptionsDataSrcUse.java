package org.kp.foundation.core.use;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceMetadata;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.wrappers.ValueMapDecorator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.sightly.WCMUsePojo;
import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.EmptyDataSource;
import com.adobe.granite.ui.components.ds.SimpleDataSource;
import com.adobe.granite.ui.components.ds.ValueMapResource;

/**
 * TagOptionsDataSrcUse class responsible for populating page dialog properties.
 * 
 * @author Vijayanandh Samy Raju
 *
 */

public class TagOptionsDataSrcUse extends WCMUsePojo {
	
  private static final Logger LOGGER = LoggerFactory.getLogger(TagOptionsDataSrcUse.class);

  private static final String DATASOURCE_ELEMENT_NAME="datasource";
  
  private static String TITLE_ATTR = "jcr:title";
  private static String DEFAULT_LOCALE="en";

  @Override
  public void activate() throws Exception {
	  
    LOGGER.info("Activating");
    HashMap<String, String> tags;	
    Resource datasource = getResource().getChild(DATASOURCE_ELEMENT_NAME);
    ValueMap dsProperties = ResourceUtil.getValueMap(datasource);
    String tagPath = dsProperties.get("path", String.class);
    boolean useTagName = dsProperties.get("useTagName", false);
    
   	if (tagPath.equals("")) {	   		
        getRequest().setAttribute(DataSource.class.getName(), EmptyDataSource.instance());
        return;
    }
	
   	ResourceResolver resolver = getRequest().getResourceResolver();
    Resource tagRes = resolver.getResource(tagPath);
    
    if (ResourceUtil.isNonExistingResource(tagRes)) {
        getRequest().setAttribute(DataSource.class.getName(), EmptyDataSource.instance());
        return ;
    }

    tags = new LinkedHashMap<String, String>();
    if (dsProperties .get("addNone", false)) {
        tags.put("", "");
    }

    String locale;
    try { 
    	locale = getPageManager().getContainingPage(getRequest().getRequestPathInfo().getSuffix()).getLanguage(false).getLanguage();   
    } catch(Exception e){
    	LOGGER.error("Exception while fetching language code - defaulting to english" , e);
    	locale=DEFAULT_LOCALE;
    }
 
    String titleAttr_locale = TITLE_ATTR + "." + locale;
    
    for (Iterator<Resource> it = tagRes.listChildren(); it.hasNext();) {
        Resource tag = it.next();
        ValueMap vm = ResourceUtil.getValueMap(tag);
        String value = vm.get(titleAttr_locale, String.class);
        String name = tag.getName();
        if(value == null ){
            value = vm.get(TITLE_ATTR, tag.getName());
        };
        if(!useTagName){
        	name=value;
        }
        tags.put(name, value);
    }
    
    List<Resource> resourceList = new ArrayList<Resource>();
    Iterator it;
    
    Boolean donotSortAlphabetically = false;
    
    ValueMap properties = ResourceUtil.getValueMap(getResource());
    donotSortAlphabetically = properties.get("donotSortAlphabetically", false);
    
    if (donotSortAlphabetically) {
    	it = tags.entrySet().iterator();
    } else {
    	it = tags.entrySet().stream().sorted(Map.Entry.comparingByValue()).iterator();
    }
    
	  while(it.hasNext()){
		  Map.Entry<String, String> tag = (Map.Entry<String, String> )it.next();
		  ValueMap vm = new ValueMapDecorator(new LinkedHashMap<String, Object>());
		   vm.put("value", tag.getKey());
           vm.put("text", tag.getValue());
	      resourceList.add(new ValueMapResource(resolver, new ResourceMetadata(), "nt:unstructured", vm));	
	    }
  	  DataSource ds = new SimpleDataSource(resourceList.iterator());
      getRequest().setAttribute(DataSource.class.getName(), ds);
    LOGGER.debug("Done: Activating");    
  } 
 }	    
  