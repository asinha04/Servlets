package org.kp.patterns.core.models;

import javax.inject.Inject;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.Via;
import org.kp.foundation.core.models.BaseModel;

/**
 * AutoCompleteSearchPatternModel is responsible for providing authored content
 * from AutoComplete Search Component.
 * 
 * @author Mohan Joshi
 *
 */
@Model(adaptables = { Resource.class,
		SlingHttpServletRequest.class }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class AutoCompleteSearchPatternModel extends BaseModel {

	@Inject
	@Via("resource")
	@Default(values = "Search")
	private String searchButton;

	@Inject
	@Via("resource")
	@Default(booleanValues = false)
	private boolean disableSearch;

	@Inject
	@Default(values = "id_autocomplete-search__input_label_default")
	@Optional
	private String id;

	private String dataAnalyticsClickDesktop = "clear search";
	private String dataAnalyticsClickMobile = "close search bar";
	private String dataAnalyticsLocation = "global search tool";

	/**
	 * Getter for SearchButton.
	 * 
	 * @return the searchButton
	 */
	public String getSearchButton() {
		return searchButton;
	}

	public String getId() {
		return id;
	}

	public String getDataAnalyticsClickMobile() {
		return dataAnalyticsClickMobile;
	}

	public String getDataAnalyticsClickDesktop() {
		return dataAnalyticsClickDesktop;
	}

	public String getDataAnalyticsLocation() {
		return dataAnalyticsLocation;
	}

	public boolean isDisableSearch() {
		return disableSearch;
	}
}