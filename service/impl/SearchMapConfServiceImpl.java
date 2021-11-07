/**
 * 
 */
package org.kp.foundation.core.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.caconfig.resource.ConfigurationResourceResolver;
import org.kp.foundation.core.service.SearchMapConfService;
import org.kp.foundation.core.service.SearchMapConfigurationsServiceFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *         @author D107273- Madhu Chaganti
 * 
 *         Service implementation for SearchMapConfService , Provides
 *         /Conf/...../sling:configs/"configName" configurations related
 *         operations
 */
@Component(service = SearchMapConfService.class, immediate = true,configurationPid = "org.kp.foundation.core.service.impl.SearchMapConfServiceImpl")
public class SearchMapConfServiceImpl implements SearchMapConfService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SearchMapConfServiceImpl.class);

    private static final String BUCKET_NAME = "sling:configs";
    private static final String SEARCH_AND_MAP_APPLICATION_ID="searchAndMapApplicationId";


    @Reference
    ConfigurationResourceResolver configurationResourceResolver;
    
    List<SearchMapConfigurationsServiceFactory> configurationList;
    
    @Reference
    SearchMapConfigurationsServiceFactory defaultSearchMapService;

    /**
     * Executes on Configuration Add event of SearchMapConfigurationsServiceFactory
     * 
     * @param config
     * New configuration for factory
     */
    @Reference(name = "searchMapConfigurationService", cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
    protected synchronized void bindConfigurationFactory(final SearchMapConfigurationsServiceFactory config) {
        LOGGER.debug("Start of bindConfigurationFactory method");
        if (configurationList == null) {
            configurationList = new ArrayList<>();
        }
        configurationList.add(config);
        LOGGER.debug("End of bindConfigurationFactory method");
    }

    /**
     * Executes on Configuration Remove event SearchMapConfigurationsServiceFactory
     * 
     * @param config
     * New configuration for factory
     */
    protected synchronized void unbindConfigurationFactory(final SearchMapConfigurationsServiceFactory config) {
        LOGGER.debug("Start of bindConfigurationFactory method");
        configurationList.remove(config);
        LOGGER.debug("End of bindConfigurationFactory method");
    }

    /**
     * @see org.kp.myhealthcare.core.service.SearchMapConfService#getSearchConfConfigs(org.apache.sling.api.SlingHttpServletRequest,
     *      java.lang.String,java.lang.String) Method to get the /conf
     *      configurations from /conf/........../sling:configs/"configName" configurations values
     */
    @Override
    public  synchronized ValueMap getConfConfigurations(SlingHttpServletRequest request, String pagePath, String configName) {
        LOGGER.debug("SearchMapConfServiceImpl Start of getConfConfigurations()");
        ValueMap searchConfig = null;
        Resource contentResource = request.getResourceResolver().getResource(pagePath);
        if (null != contentResource) {
            Resource configResource = configurationResourceResolver.getResource(contentResource, BUCKET_NAME,configName);
            if (null != configResource) {
                Resource contentRes=configResource.getChild("jcr:content");
                if(null!=contentRes){
                    searchConfig=contentRes.getValueMap();
                }
            }
        }
        LOGGER.debug("SearchMapConfServiceImpl End of getConfConfigurations()");
        return searchConfig;
    }

    /**
     * @see org.kp.myhealthcare.core.service.SearchMapConfService#getConfProperty(java.lang.String,
     *      org.apache.sling.api.SlingHttpServletRequest,
     *      java.lang.String,java.lang.String,java.lang.String,java.lang.String)
     *      Method to get the property value of
     *      /conf/........../sling:configs/"configName" configurations values
     */
    @Override
    public String getConfProperty(String key, SlingHttpServletRequest request, String pagePath, String configName) {
        LOGGER.debug("SearchMapConfServiceImpl Start of getConfProperty()");
        String value = "";
        ValueMap searchMapConfigs = getConfConfigurations(request, pagePath, configName);
        if (null != searchMapConfigs && null != searchMapConfigs.get(key)) {
            value = searchMapConfigs.get(key).toString();
        }
        LOGGER.debug("SearchMapConfServiceImpl End of getConfProperty()");
        return value;
    }

    /**
     * @see org.kp.myhealthcare.core.service.SearchMapConfService#getConfArrayProperty(java.lang.String,
     *      org.apache.sling.api.SlingHttpServletRequest,
     *      java.lang.String,java.lang.String,java.lang.String,java.lang.String)
     *      Method to read the array property values from search
     *      /conf/..../sling:configs/"configName" configurations
     */

    @Override
    public String[] getConfArrayProperty(String key, SlingHttpServletRequest request, String pagePath,
            String configName) {
        LOGGER.debug("SearchMapConfServiceImpl Start of getConfArrayProperty()");
        String[] values = {};
        ValueMap searchMapConfigs = this.getConfConfigurations(request, pagePath, configName);
        if (null != searchMapConfigs && null != searchMapConfigs.get(key)) {
            values = (String[]) searchMapConfigs.get(key);
        }
        LOGGER.debug("SearchMapConfServiceImpl End of getConfArrayProperty()");
        return values;
    }
    
    
    /**
     * Method to retrieve the Search Map Configuration service from Search Map Configuration Factory services
     */
    @Override
    public synchronized SearchMapConfigurationsServiceFactory getSearchMapConfigurationService(SlingHttpServletRequest request, String pagePath,
            String configName) {
        LOGGER.debug("Start of getSearchMapConfigurationService using searchAndMapApplicationId");
        String searchAndMapApplicationId=this.getConfProperty(SEARCH_AND_MAP_APPLICATION_ID, request, pagePath,configName);
        SearchMapConfigurationsServiceFactory currentService=defaultSearchMapService;
        if (configurationList != null) {
            for(SearchMapConfigurationsServiceFactory searchMapService:configurationList){
                if(StringUtils.equals(StringUtils.trim(searchMapService.getSearchMapApplicationId()), StringUtils.trim(searchAndMapApplicationId))){
                    currentService= searchMapService;
                     LOGGER.info("searchAndMapApplicationId of current SearchMapConfigurationsServiceFactory :{}",currentService.getSearchMapApplicationId()); 
                    break;
                }
            }
        }
        LOGGER.debug("End of getSearchMapConfigurationService using searchAndMapApplicationId");
        return currentService;
    }
    
    /**
     * Method to retrieve the Search Map Configuration service from Search Map Configuration Factory services
     */
    @Override
    public  synchronized SearchMapConfigurationsServiceFactory getSearchMapConfigurationServiceUsingApplicationId(SlingHttpServletRequest request, String searchAndMapApplicationId,
            String configName) {
        SearchMapConfigurationsServiceFactory currentService=defaultSearchMapService;
        if (configurationList != null) {
            for(SearchMapConfigurationsServiceFactory searchMapService:configurationList){
                if(StringUtils.equals(StringUtils.trim(searchMapService.getSearchMapApplicationId()), StringUtils.trim(searchAndMapApplicationId))){
                    currentService= searchMapService;
                     LOGGER.info("searchAndMapApplicationId of current SearchMapConfigurationsServiceFactory :{}",currentService.getSearchMapApplicationId()); 
                    break;
                }
            }
        }
        LOGGER.debug("End of getSearchMapConfigurationService using searchAndMapApplicationId");
        return currentService;
    }

}
