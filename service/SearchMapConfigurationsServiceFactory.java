package org.kp.foundation.core.service;

/**
 * Interface containing methods related to search map.
 * 
 * @author D107273
 *
 */
public interface SearchMapConfigurationsServiceFactory {
    

    /**
     * Method to get the search and map application id
     * @return
     */
    public String getSearchMapApplicationId();

    
    /**
     * Method to get the endpoint for fetching facility details in author dialog
     * @return
     */
    public String getFdlServiceUrl();
    
    /**
     * Method to get the Google api key for the search map
     * @return
     */
    public String getGoogleApiKey();
    
    /**
     * Methdod to get the servlet url which fetches fdl details.
     * @return
     */
    public String getFdlInformationServlet();
    
    /**
     * Method to get the IBM watson point which returns the search results for search map
     * @return
     */
    public String getWatsonSearchEndpoint();
    
    /**
     * Method to get the IBM watson end point which returns the city/island list
     * @return
     */
    public String getWatsonEndpointCityIslandLoad();

    
    /**
     * Method to return the servlet url which returns the list of cities 
     * @return
     */
    public String getWatsonCityList();
    
    /**
     * Method to return the servlet url which returns the list of islands 
     * @return
     */
    public String getWatsonIslandList();
    
    /**
     * Method to return the facility url which is used to fetch the list of facilities.
     * @return
     */
    public String getFacilityResultsUrl();
    
    /**
     * Method to get the domain name stored in cookie.
     * @return
     */
    public String getCookieDomainName();
    
    /**
	   * @return the basicHttpAuth
	   */
	public String getBasicHttpAuth();
    
}
