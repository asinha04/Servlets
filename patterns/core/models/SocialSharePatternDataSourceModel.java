package org.kp.patterns.core.models;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceMetadata;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.wrappers.ValueMapDecorator;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;

import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.EmptyDataSource;
import com.adobe.granite.ui.components.ds.SimpleDataSource;
import com.adobe.granite.ui.components.ds.ValueMapResource;

/**
 * This class is responsible to update the acs-commons datasource value and text.
 * 
 * @author Venkata Malladi
 */

@Model(adaptables = { Resource.class,
		SlingHttpServletRequest.class }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class SocialSharePatternDataSourceModel {

	@Inject
	private SlingHttpServletRequest request;
	
	@Inject
	private ResourceResolver resourceResolver;
	
	private static final String SOCIAL_ICONS_PATH="/etc/acs-commons/lists/social-icons/jcr:content/list";
	private static final String REG_EXP="::";
	private static final String JCR_TITLE="jcr:title";
	private static final String VALUE="value";
	private static final String TEXT="text";
	private static final String NT_UNSTRUCTURED="nt:unstructured";
	
	/**
	 * Init method updates the  acs-commons datasource value and text.
	 *
	 * @throws Exception
	 */
	@PostConstruct
	protected void init() {
		request.setAttribute(DataSource.class.getName(), EmptyDataSource.instance());
		Resource iconsRes = resourceResolver.getResource(SOCIAL_ICONS_PATH);
		List<Resource> resourceList = new ArrayList<Resource>();
		for (Iterator<Resource> it = iconsRes.listChildren(); it.hasNext();) {
	        Resource currentResource = it.next();
	        ValueMap valueMap = ResourceUtil.getValueMap(currentResource);
	        ValueMap vm = new ValueMapDecorator(new LinkedHashMap<String, Object>());
			vm.put(VALUE, valueMap.get(VALUE).toString().split(REG_EXP)[0]);
	        vm.put(TEXT, valueMap.get(JCR_TITLE).toString().split(REG_EXP)[0]);
		    resourceList.add(new ValueMapResource(resourceResolver, new ResourceMetadata(), NT_UNSTRUCTURED, vm));	
	    }
	    DataSource ds = new SimpleDataSource(resourceList.iterator());
	    request.setAttribute(DataSource.class.getName(), ds);
	    
	}
	
}
