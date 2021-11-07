package org.kp.foundation.core.models;

import java.util.ArrayList;
import java.util.List;

import com.adobe.cq.wcm.core.components.models.contentfragment.DAMContentFragment;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "title", "description","items" })
public class ContentFragmentListDataModel {

	String title;
	String description;
	List<ContentFragmentDataModel> items = new ArrayList<ContentFragmentDataModel>();
	
	List<DAMContentFragment> originalItems = new ArrayList<DAMContentFragment>();
	
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

	public List<ContentFragmentDataModel> getItems() {
		return items;
	}

	public void setItems(List<ContentFragmentDataModel> items) {
		this.items = items;
	}

	public List<DAMContentFragment> getOriginalItems() {
		return originalItems;
	}

	public void setOriginalItems(List<DAMContentFragment> originalItems) {
		this.originalItems = originalItems;
	}
}