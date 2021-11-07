package org.kp.foundation.core.servlets;

import static org.apache.sling.api.servlets.ServletResolverConstants.SLING_SERVLET_EXTENSIONS;
import static org.apache.sling.api.servlets.ServletResolverConstants.SLING_SERVLET_METHODS;
import static org.apache.sling.api.servlets.ServletResolverConstants.SLING_SERVLET_PATHS;
import static org.kp.foundation.core.utils.SearchMapUtil.DATA_SWAP_DETECTED;

import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.text.MessageFormat;

import javax.servlet.Servlet;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.osgi.services.HttpClientBuilderFactory;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.kp.foundation.core.exception.GenericRuntimeException;
import org.kp.foundation.core.models.SearchFacilitytData;
import org.kp.foundation.core.service.SearchMapConfService;
import org.kp.foundation.core.service.SearchMapConfigurationsServiceFactory;
import org.kp.foundation.core.utils.SearchMapUtil;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

/**
 * This is the servlet registered for fetching the Search results and city/island list
 * from watson based on region and other parameters.    
 * 
 * @author Utkarsh Thakkar(s759561)
 *
 */
@Component(service = {Servlet.class},
    property = {SLING_SERVLET_PATHS + "=/bin/kporg/service/kp-foundation/facility/search",
        SLING_SERVLET_METHODS + "=GET", SLING_SERVLET_EXTENSIONS + "=json",})
public class SearchFacilitiesServlet extends SlingSafeMethodsServlet implements Serializable{

  /**
   * Generated Serial Version UUID for SearchFacilitiesServlet
   */
  private static final long serialVersionUID = 5049528181494057483L;

  private static final Logger LOGGER = LoggerFactory.getLogger(SearchFacilitiesServlet.class);
  private static final String CITY_SEARCH = "city_label%3d%3d{0}";
  private static final String DISTANCE_PARAM = "distance_label%3d0%3a{0}";
  private static final String ISLAND_PARAM = "island_label%3d%3d{0}";
  private static final String V_SOURCE_KEY = "v_source";
  private static final String V_SOURCE_PROXIMITY_KEY = "v_source_proximity";
  private static final String CITY_PARAM_NAME = "city";
  private static final String ISLADN_PARAM_NAME = "island";
  private static final String REGION_PARAM_NAME = "rop";
  private static final String USER_ZIP_PARAM_NAME = "user_zip";
  private static final String USER_LAT_PARAM_NAME = "user_lat";
  private static final String USER_LNG_PARAM_NAME = "user_lon";
  private static final String USER_DISTANCE_PARAM_NAME = "distance";
  private static final String USER_LOCALE = "locale";
  private static final String CONTENT_TYPE = "Content-Type";
  private static final String CHARACTER_ENCODING="UTF-8";
  private static final String APPLICATION_JSON = "application/json";
  private static final String ROOT_PATH="/content/kporg";
  private static final String RELATIVE_PATH="relativePath";
  private static final String CONF_CONFIG_NAME="searchandmap";
  private static final String CALL_TYPE="callType";
  private static final String CITY_ISLAND_LOAD = "cityIslandLoad";
  private static final String FACILITY_SEARCH = "facilitySearch";
  
  private static final String LIST_NODE_STRING = "list";
  private static final String DOCUMENT_NODE_STRING = "document";
  private static final String CONTENTS_NODE_STRING = "contents";
  private static final String ANCHOR_START_TAG = "<a";
  private static final String DATES_HOUR_INFO = "dates_hours_info";
  private static final String SLASH_DOUBLE_QUOTES = "\"";
  private static final String SINGLE_QUOTES = "'";
  private static final String FACILITY_INFO = "fac_info";
  private static final String PHONE_NUMBER_INFO = "phone_number_info";
  
  @Reference
  private transient SearchMapConfService confService;
  
  @Reference
  private  transient HttpClientBuilderFactory httpClientBuilderFactory;
  
  /**
   * Method to build the request which hits the watson service and updates the respone 
   * based on the output from the Watson
   */
  @Override
  protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException   {
    LOGGER.debug("SearchFacilitiesServlet :: Starting to execute doGet method for the searchMap component");
    SearchFacilitytData searchFacilityRequestData=new SearchFacilitytData();
    String watsonUrlWithParams = buildWatsonUrl(request,searchFacilityRequestData);
   LOGGER.info("Search Url: {}", watsonUrlWithParams);
   HttpClient client = null;
    try {
      HttpGet watsonRequest = getHttpGet(watsonUrlWithParams);
      HttpClientBuilder builder = httpClientBuilderFactory.newBuilder();
      client = builder.build();
      RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(5000).setSocketTimeout(5000).build();
      builder.setDefaultRequestConfig(requestConfig);
      HttpResponse watsonResponse = client.execute(watsonRequest);
      int statusCode = watsonResponse.getStatusLine().getStatusCode();
      if (statusCode == 200) {
         String responseString = SearchMapUtil.getInstance().getStringFromStream(watsonResponse.getEntity().getContent());
         SearchMapUtil.getInstance().validateSearchFacilityResponse(searchFacilityRequestData,responseString);
         if(StringUtils.equals(FACILITY_SEARCH, request.getParameter(CALL_TYPE))
        		 && StringUtils.isNotBlank(responseString)) {
        	 responseString = formatFluJson(responseString);
         }
         response.setCharacterEncoding(CHARACTER_ENCODING);
         response.getWriter().write(responseString);
      } else {
        SearchMapUtil.getInstance().handleErrors(response, statusCode, watsonResponse.getStatusLine().toString());
      }
      response.setHeader(CONTENT_TYPE, APPLICATION_JSON);
      response.setStatus(SlingHttpServletResponse.SC_OK);
      LOGGER.debug("SearchFacilitiesServlet :: Ending doGet method for the searchMap component");
     } catch (URISyntaxException e) {
      LOGGER.error("SearchFacilitiesServlet URISyntaxException :{}", e);
      SearchMapUtil.getInstance().handleErrors(response, 500, e.getMessage());
    } catch (IOException e) {
      LOGGER.error("SearchFacilitiesServlet IOException :{}", e);
      SearchMapUtil.getInstance().handleErrors(response, 500, e.getMessage());
    } catch (GenericRuntimeException e) {
    	LOGGER.error("SearchFacilitiesServlet GenericRuntimeException :{}", e);
    	int responseCode = 500;
    	if(StringUtils.startsWith(e.getMessage(), DATA_SWAP_DETECTED)) {
			responseCode = 404;
		}
        SearchMapUtil.getInstance().handleErrors(response, responseCode, e.getMessage());
    } 
    finally {
    	 try {
             ((CloseableHttpClient) client).close();
         } catch (Exception e) {
        	 LOGGER.error("SearchFacilitiesServlet GenericRuntimeException :{}", e);
         }
    }
    
  }

  /**
   * Method to check the RTE fields i.e  facility_info, dates_hour_info and phone_number_info
   * and check for anchor tag, if anchor tag is present then replace the "\"" with "'"
   * This is done to avoid link checker stripping the anchor tags in UI.
   * This method is kept as protected for the purpose of Junit
   * @param responseString
   * @return
   * 
   */
  protected String formatFluJson(String responseString) {
	  LOGGER.debug("formatFluJson entry");
		try {
			JsonElement jsonElement = new JsonParser().parse(responseString);
			if(!isValidJsonElement(jsonElement)) {
				return responseString;
			}
			JsonObject jsonObject = jsonElement.getAsJsonObject();
			if (!isValidJsonObject(jsonObject)) {
				return responseString;
			}

			JsonObject jsonListObject = jsonObject.getAsJsonObject(LIST_NODE_STRING);
			if (!isValidJsonObject(jsonListObject)) {
				return responseString;
			}
			
			if(jsonListObject.get(DOCUMENT_NODE_STRING) == null || !jsonListObject.get(DOCUMENT_NODE_STRING).isJsonArray()) {
				return responseString;
			}
			
			JsonArray healthClassResultsArray = jsonListObject.get(DOCUMENT_NODE_STRING).getAsJsonArray();
			if (healthClassResultsArray == null || healthClassResultsArray.isJsonNull() || healthClassResultsArray.size() == 0) {
				return responseString;
			}
			
			int count = healthClassResultsArray.size();

			for (int i = 0; i < count; i++) { // iterate through jsonArray
				JsonObject items = healthClassResultsArray.get(i).getAsJsonObject(); // get jsonObject @ i position
				if (isValidJsonObject(items)) {
					JsonObject contentNodes = items.getAsJsonObject(CONTENTS_NODE_STRING);
					if (isValidJsonObject(contentNodes)) {
						boolean isDateHourUpdated = updateAnchorTag(contentNodes, DATES_HOUR_INFO);
						boolean isFacilityUpdated = updateAnchorTag(contentNodes, FACILITY_INFO);
						boolean isPhoneNumberUpdated = updateAnchorTag(contentNodes, PHONE_NUMBER_INFO);
						if (isDateHourUpdated || isFacilityUpdated || isPhoneNumberUpdated) {
							LOGGER.debug("Replacing Json");
							healthClassResultsArray.set(i, items);
						}
						
					}
				}
			}
			String updatedJson = jsonObject.toString();
			LOGGER.debug("updatedJson:{}", updatedJson);
			LOGGER.debug("formatFluJson exit");
			return updatedJson;
		} catch(JsonSyntaxException e) {
		LOGGER.debug("Exception occured while formatting json:{}", e);
		/*If any exception occurs while formatting the json then return the watson response as it is
		 * */
		LOGGER.debug("formatFluJson exit from catch block");
		return responseString;
	}
  }


  /**
   * Method to check for anchor tag and replace "\"" with "'"
   * @param contentNodes
   * @param rtePropertyName
   * @return
   */
  private boolean updateAnchorTag(JsonObject contentNodes, String rtePropertyName){
		LOGGER.debug("updateAnchorTag entry :{}", rtePropertyName);
		boolean isAnchorTagUpdated = false;
		JsonElement rtePropertyElement = contentNodes.get(rtePropertyName);
		if(rtePropertyElement!= null && rtePropertyElement.getAsString()!= null) {
			String rtePropertyValue = contentNodes.get(rtePropertyName).getAsString();
			if (StringUtils.contains(rtePropertyValue, ANCHOR_START_TAG)) {
				isAnchorTagUpdated = true;
				LOGGER.debug("Anchor tag found ");
				LOGGER.debug("Before replacing:{}", rtePropertyValue);
				rtePropertyValue = StringUtils.replace(rtePropertyValue, SLASH_DOUBLE_QUOTES, SINGLE_QUOTES);
				LOGGER.debug("After replacing :{}", rtePropertyValue);
				contentNodes.remove(rtePropertyName);
				contentNodes.addProperty(rtePropertyName, rtePropertyValue);
			}
		}
		LOGGER.debug("updateAnchorTag exit :{}", isAnchorTagUpdated);
		return isAnchorTagUpdated;
	}
  
  /**
   * 
   * @param jsonObject
   * @return
   */
  private boolean isValidJsonObject(JsonObject jsonObject) {
	  LOGGER.debug("isValidJsonObject entry");
	  if(jsonObject== null || jsonObject.isJsonNull() || !jsonObject.isJsonObject()) {
		  LOGGER.debug("isValidJsonObject exit with false");
		  return false;
	  }
	  LOGGER.debug("isValidJsonObject exit with true");
	  return true;
  }
  
  /**
   * 
   * @param jsonElement
   * @return
   */
  private boolean isValidJsonElement(JsonElement jsonElement) {
	  if (jsonElement == null || jsonElement.isJsonNull() || !jsonElement.isJsonObject()) {
		  LOGGER.debug("isValidJsonElement exit with false");
		  return false;
	  }
	  LOGGER.debug("isValidJsonElement exit with true");
	  return true;
  }
  
  /**
   * Method to get the url from the configuration and replace the place holders 
   * with dynamic parameters from the requests to form the final url to hit watson
   * @param request
   * @return String url
   */
  private String buildWatsonUrl(SlingHttpServletRequest request,SearchFacilitytData searchFacilityRequestData) {
    searchFacilityRequestData.setCity((StringUtils.isNotBlank(request.getParameter(CITY_PARAM_NAME))) ? request.getParameter(CITY_PARAM_NAME) : "");
    searchFacilityRequestData.setIsland((StringUtils.isNotBlank(request.getParameter(ISLADN_PARAM_NAME))) ? request.getParameter(ISLADN_PARAM_NAME): "");
    searchFacilityRequestData.setRegion((StringUtils.isNotBlank(request.getParameter(REGION_PARAM_NAME))) ? request.getParameter(REGION_PARAM_NAME) : "");
    searchFacilityRequestData.setUserZip((StringUtils.isNotBlank(request.getParameter(USER_ZIP_PARAM_NAME))) ? request.getParameter(USER_ZIP_PARAM_NAME) : "");
    searchFacilityRequestData.setUserLatitude((StringUtils.isNotBlank(request.getParameter(USER_LAT_PARAM_NAME))) ? request.getParameter(USER_LAT_PARAM_NAME) : "");
    searchFacilityRequestData.setUserLongitude((StringUtils.isNotBlank(request.getParameter(USER_LNG_PARAM_NAME)))? request.getParameter(USER_LNG_PARAM_NAME): "");
    searchFacilityRequestData.setDistance((StringUtils.isNotBlank(request.getParameter(USER_DISTANCE_PARAM_NAME)))? request.getParameter(USER_DISTANCE_PARAM_NAME): "");
    searchFacilityRequestData.setLocale((StringUtils.isNotBlank(request.getParameter(USER_LOCALE)))? request.getParameter(USER_LOCALE): "");
    searchFacilityRequestData.setCallyType((StringUtils.isNotBlank(request.getParameter(CALL_TYPE)))? request.getParameter(CALL_TYPE): "");
    
    String pagePath = this.getPagePathFromRequest(request); 
    searchFacilityRequestData.setvSource(confService.getConfProperty(V_SOURCE_KEY, request, pagePath, CONF_CONFIG_NAME));
    searchFacilityRequestData.setvSourceProximity(confService.getConfProperty(V_SOURCE_PROXIMITY_KEY, request, pagePath, CONF_CONFIG_NAME));
    searchFacilityRequestData.setTypeOfSearch(isVSource(searchFacilityRequestData.getUserZip(), searchFacilityRequestData.getUserLatitude(), searchFacilityRequestData.getUserLongitude()) ? searchFacilityRequestData.getvSource() : searchFacilityRequestData.getvSourceProximity());
    searchFacilityRequestData.setBinningState(determineBinningState(searchFacilityRequestData.getCity(), searchFacilityRequestData.getIsland(), searchFacilityRequestData.getDistance(), searchFacilityRequestData.getTypeOfSearch(), searchFacilityRequestData.getvSource()));
    
    SearchMapConfigurationsServiceFactory configService = confService.getSearchMapConfigurationService(request, pagePath,CONF_CONFIG_NAME);
    String watsonEndPoint = getWatsonEndPointBasedOnCallType(confService,configService,request, pagePath,searchFacilityRequestData.getCallyType());
    return MessageFormat.format(watsonEndPoint, searchFacilityRequestData.getTypeOfSearch(), searchFacilityRequestData.getRegion(),
            searchFacilityRequestData.getUserZip(),searchFacilityRequestData.getBinningState(), searchFacilityRequestData.getUserLatitude(), searchFacilityRequestData.getUserLongitude(),searchFacilityRequestData.getLocale());
  }

  /**
   * This method determines the current search criteria is V_SOURCE or not  and return
   * If it is not VSOURCE then V_SOURCE_PROXIMITY will be considered as search type
   * 
   * @param zipcode
   * @param userLatitude
   * @param userLongitude
   * @return
   */
    private boolean isVSource(String zipcode, String userLat, String userLang) {
        if (StringUtils.isBlank(zipcode) && StringUtils.isBlank(userLat) && StringUtils.isBlank(userLang)) {
            return true;
        } else {
            return false;
        }
    }
  

  /**
   * Method to customize the request based on user selection of city or island.
   * @param city
   * @param island
   * @param distance
   * @param typeOfSearch
   * @return
   */
  private String determineBinningState(String city, String island, String distance,
      String typeOfSearch, String v_source) {
    try {
      if (v_source.equals(typeOfSearch)) {
        if (StringUtils.isNotBlank(city)) {
          return MessageFormat.format(CITY_SEARCH, URLEncoder.encode(city, CHARACTER_ENCODING));
        }else if (StringUtils.isNotBlank(island)) {
          return MessageFormat.format(ISLAND_PARAM, URLEncoder.encode(island, CHARACTER_ENCODING));
        }
      } else {
          return MessageFormat.format(DISTANCE_PARAM,
              SearchMapUtil.getInstance().extractNumberFromString(distance));
      }
    }catch(UnsupportedEncodingException ignored) {
      /* UTF-8 is the standard followed for all the url encoding across kp.org. It is only kept as it is thrown by method URLEncoder.encode().*/
      LOGGER.error("SearchMapConfigurationsServiceFactory error while determining binning-state :{}", ignored.getMessage());
    }
    return "";
  }
  
    /**
     * Method to obtain the page path from the request
     * 
     * @param request
     * @return
     */
    protected String getPagePathFromRequest(SlingHttpServletRequest request) {
        LOGGER.debug("Inside the getPagePathFromRequest()");
        String pagePath = (StringUtils.isNotBlank(request.getParameter(RELATIVE_PATH)))
                ? request.getParameter(RELATIVE_PATH) : "";
        pagePath = ROOT_PATH + pagePath;
        LOGGER.info("Page Path retrieved from request:{}", pagePath);
        LOGGER.debug("End of the getPagePathFromRequest()");
        return pagePath;
    }
    
    /**
     * Method to get the http get request. Seprate method is added to cover Junit
     * 
     * @param url
     * @return
     * @throws URISyntaxException
     */
    protected HttpGet getHttpGet(String url) throws URISyntaxException {
        URI urlObj = new URI(url);
        return new HttpGet(urlObj);
    }
    
    /**
     * Method return Watson end point based whether call is made for city/island load or facility search
     * @param confService
     * @param configService
     * @param request
     * @param pagePath
     * @param callType
     * @return
     */
    private String getWatsonEndPointBasedOnCallType(SearchMapConfService confService,SearchMapConfigurationsServiceFactory configService,SlingHttpServletRequest request,String pagePath,String callType){
    	String watsonEndPoint = "";
    	if(StringUtils.equals(confService.getConfProperty(CITY_ISLAND_LOAD, request, pagePath, CONF_CONFIG_NAME), callType)){
    		watsonEndPoint = configService.getWatsonEndpointCityIslandLoad();
    	}else{
    		watsonEndPoint = configService.getWatsonSearchEndpoint();
    	}
    	return watsonEndPoint;
    }
}
