package org.kp.foundation.core.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ContentFragmentDataModel {

	@JsonProperty("propertyname")
	String title;
	Object value;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Object getValue() {
		if(null == value) {
			value = "";
		}
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

}
