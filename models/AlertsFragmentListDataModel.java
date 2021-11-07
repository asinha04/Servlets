package org.kp.foundation.core.models;

import java.util.ArrayList;
import java.util.Collection;

import com.adobe.cq.wcm.core.components.models.contentfragment.DAMContentFragment;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "type", "title", "description", "previewInAuthor", "region", "mobileOnly", "message" })
public class AlertsFragmentListDataModel {

	String title;	
	String description;
	@JsonProperty("alertType")
	Object type;
	Object message;
	String region;
	@JsonIgnore
	Object showAlert;
	Object mobileOnly;
	
	Collection<DAMContentFragment> originalItems = new ArrayList<DAMContentFragment>();

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Collection<DAMContentFragment> getOriginalItems() {
		return originalItems;
	}

	public void setOriginalItems(Collection<DAMContentFragment> originalItems) {
		this.originalItems = originalItems;
	}

	public Object getMessage() {
		return message;
	}

	public void setMessage(Object message) {
		this.message = message;
	}

	public Object getType() {
		return type;
	}

	public void setType(Object type) {
		this.type = type;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public Object getShowAlert() {
		return showAlert;
	}

	public void setShowAlert(Object showAlert) {
		this.showAlert = showAlert;
	}

	public Object getMobileOnly() {
		return mobileOnly;
	}

	public void setMobileOnly(Object mobileOnly) {
		this.mobileOnly = mobileOnly;
	}
}