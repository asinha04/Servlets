/**
 * 
 */
package org.kp.foundation.core.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.kp.foundation.core.service.SearchMapConfigurations;
import org.kp.foundation.core.service.SearchMapConfigurationsServiceFactory;
import org.kp.foundation.core.utils.SearchMapUtil;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation class for fetching the configurations like urls, domain names etc 
 * used in search map.
 * Service implementation for SearchMapConfigurationsServiceFactory
 * @author D107273- Madhu Chaganti
 */

@Component(immediate = true, service = SearchMapConfigurationsServiceFactory.class,configurationPid = "org.kp.foundation.core.service.impl.SearchMapConfigurationsServiceFactoryImpl")
@Designate(ocd = SearchMapConfigurations.class,factory=true)
public class SearchMapConfigurationsServiceFactoryImpl implements SearchMapConfigurationsServiceFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(SearchMapConfigurationsServiceFactoryImpl.class);

    private String fdlServiceUrl;
    private String googleApiKey;
    private String fdlInformationServlet;
    private String watsonCityList;
    private String watsonIslandList;
    private String facilityResultsUrl;
    private String cookieDomainName;
    private String watsonSearchEndpoint;
    private String watsonEndpointCityIslandLoad;
    private String searchMapApplicationId;
    private String authId;
	private String authPass;
	
	private static final String COLON_CONSTANTS = ":" ;
	private static final String EMPTY = "" ;

    /**
     * Method to initialize the properties with configured values.  
     * @param searchMapConfigurations
     */
    @Activate
    @Modified
    protected void activate(final SearchMapConfigurations searchMapConfigurations) {
        LOGGER.debug("Start of activate() method");
        this.fdlServiceUrl = searchMapConfigurations.fdl_service_url();
        this.googleApiKey = searchMapConfigurations.google_map_apiKey();
        this.fdlInformationServlet = searchMapConfigurations.fdl_information_servlet();
        this.watsonCityList = searchMapConfigurations.watson_city_list();
        this.watsonIslandList = searchMapConfigurations.watson_island_list();
        this.facilityResultsUrl = searchMapConfigurations.facility_results_url();
        this.cookieDomainName = searchMapConfigurations.cookie_domain_name();
        this.watsonSearchEndpoint = searchMapConfigurations.watson_search_endpoint();
        this.watsonEndpointCityIslandLoad = searchMapConfigurations.watson_endpoint_city_island_load();
        this.searchMapApplicationId=searchMapConfigurations.searchandmap_application_id();
        this.authId = searchMapConfigurations.basicAuthId();
		this.authPass = searchMapConfigurations.basicAuthPass();	
        LOGGER.debug("End of activate() method");
    }
    
    /**
     * Method to get the Search and map application unique id
     */
    @Override
    public String getSearchMapApplicationId() {
        return searchMapApplicationId;
    }

    /**
     * Method to get the endpoint for fetching facility details in author dialog
     * @return
     */
    @Override
    public String getFdlServiceUrl() {
        return fdlServiceUrl;
    }

    /**
     * Method to get the Google api key for the search map
     * @return
     */
    @Override
    public String getGoogleApiKey() {
        return googleApiKey;
    }


    /**
     * Methdod to get the servlet url which fetches fdl details.
     * @return
     */
    @Override
    public String getFdlInformationServlet() {
        return fdlInformationServlet;
    }

    /**
     * Method to return the servlet url which returns the list of cities 
     * @return
     */
    @Override
    public String getWatsonCityList() {
        return watsonCityList;
    }

    /**
     * Method to return the servlet url which returns the list of islands 
     * @return
     */
    @Override
    public String getWatsonIslandList() {
        return watsonIslandList;
    }

    /**
     * Method to return the facility url which is used to fetch the list of facilities.
     * @return
     */
    @Override
    public String getFacilityResultsUrl() {
        return facilityResultsUrl;
    }

    /**
     * Method to get the domain name stored in cookie.
     * @return
     */
    @Override
    public String getCookieDomainName() {
        return cookieDomainName;
    }

    /**
     * Method to get the IBM watson which returns the search results for search map
     * @return
     */
    @Override
    public String getWatsonSearchEndpoint() {
        return watsonSearchEndpoint;
    }
    
    /**
     * Method to get the IBM watson url which returns the city/island list
     * @return
     */
    @Override
    public String getWatsonEndpointCityIslandLoad() {
        return watsonEndpointCityIslandLoad;
    }

	/**
	 * Method to read the encoded authentication id and password 
	 * decode it and again encode to be used for external calls 
	 * 
	 * @return the basicHttpAuth
	 */
    @Override
	public String getBasicHttpAuth() {
		String authIdDecoded = SearchMapUtil.getInstance().decodeString(authId);
		String authPassDecoded = SearchMapUtil.getInstance().decodeString(authPass);
		String basicAuth = EMPTY;
		if(StringUtils.isNotBlank(authId) &&
					StringUtils.isNotBlank(authPass)) {
			basicAuth = SearchMapUtil.getInstance().encodeString(
				new StringBuilder().append(authIdDecoded).append(COLON_CONSTANTS).append(authPassDecoded).toString());
		}
		return basicAuth;
	}
    
}