/**
 * 
 */
package org.kp.foundation.core.service;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

/**
 * Search Map properties configuration service
 *
 */

@ObjectClassDefinition(name = "Search Map Configurations", description = "Search Map Configurations Service Factory")
public @interface SearchMapConfigurations {
    
      @AttributeDefinition(name = "Search and Map Application Unique ID", description = "Configure unique id to identify configuration and configure this id in /conf", type=AttributeType.STRING)
      String searchandmap_application_id() default "";

      @AttributeDefinition(name = "FDL Service URL", description = "Configure FDL service URL", type=AttributeType.STRING)
      String fdl_service_url() default "";
      
      @AttributeDefinition(name = "Google Map API Key", description = "Configure map api key", type=AttributeType.STRING)
      String google_map_apiKey() default "";
      
      @AttributeDefinition(name = "FDL information servlet path", description = "Servlet that fetches FDL information and populates in dialog", type=AttributeType.STRING)
      String fdl_information_servlet() default "";
      
      @AttributeDefinition(name = "Watson URL for Facility Search", description = "Watson URL that will be used to fetch indexed Facility data for Search and Map", type=AttributeType.STRING)
      String watson_search_endpoint() default "";
      
      @AttributeDefinition(name = "Watson URL for loading city/island list", description = "Watson URL that will be used to load the city/island list", type=AttributeType.STRING)
      String watson_endpoint_city_island_load() default "";

      @AttributeDefinition(name = "Watson City List", description = "Configure watson end point to fetch the list of cities", type=AttributeType.STRING)
      String watson_city_list() default "";
      
      @AttributeDefinition(name = "Watson Island List", description = "Configure watson end point to fetch the list of islands", type=AttributeType.STRING)
      String watson_island_list() default "";
      
      @AttributeDefinition(name = "Facility Results Url", description = "Configure fdl url fetch the list of facilities", type=AttributeType.STRING)
      String facility_results_url() default "";
      
      @AttributeDefinition(name = "Domain Name For Cookie", description = "Configure the domain to set to cookies", type=AttributeType.STRING)
      String cookie_domain_name() default "";
      
      @AttributeDefinition( name = "Gateway User Id", description = "Basic HTTP Auth ID")
	  String basicAuthId() default "";
		
	  @AttributeDefinition( name = "Gateway Password", description = "Basic HTTP Auth Password")
	  String basicAuthPass() default "";
}

