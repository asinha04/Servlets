/**
 * 
 */
package org.kp.foundation.core.service;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ValueMap;

/**
 * @author Madhu 
 * SearchMapConfService : Provides /Conf configurations related operations
 * 
 *  /Conf/...../sling:configs/"configName" configurations related operations
 */
public interface SearchMapConfService {
    
    public ValueMap getConfConfigurations(SlingHttpServletRequest request,String pagePath,String cofigName);
    
    public String getConfProperty(String key,SlingHttpServletRequest request,String pagePath,String cofigName);
    
    public String[] getConfArrayProperty(String key, SlingHttpServletRequest request, String pagePath,String cofigName);
    
    public SearchMapConfigurationsServiceFactory getSearchMapConfigurationService(SlingHttpServletRequest request, String pagePath,String configName);
    
    public SearchMapConfigurationsServiceFactory getSearchMapConfigurationServiceUsingApplicationId(SlingHttpServletRequest request, String searchAndMapApplicationId,String configName);
    
}