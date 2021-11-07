package org.kp.foundation.core.models;

import org.apache.commons.lang.StringUtils;
import org.kp.foundation.core.constants.GlobalConstants;

/**
 * Bean class used in Hero Image model class
 * 
 * @author Karthikeyan
 *
 */

public class Image {

	private String imageUrl;
	private String imageAltText;
	private String mediaAttributeValue;
	

	/**
	 * 
	 * @return the imageUrl
	 */
	public String getImageUrl() {
		return imageUrl;
	}

	/**
	 * To set the ImageUrl
	 * @param imageUrl
	 */
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	
	/**
	 * @return the imageAltText
	 */
	public String getImageAltText() {
		return imageAltText;
	}

	/**
	 * @param imageAltText the imageAltText to set
	 */
	public void setImageAltText(String imageAltText) {
		this.imageAltText = StringUtils.isNotEmpty(imageAltText) ?  imageAltText : GlobalConstants.EMPTY_STRING;
	}

	/**
	 * 
	 * @return the mediaAttributeValue
	 */
	public String getMediaAttributeValue() {
		return mediaAttributeValue;
	}

	/**
	 * To set the mediaAttributeValue
	 * @param mediaAttributeValue
	 */
	public void setMediaAttributeValue(String mediaAttributeValue) {
		this.mediaAttributeValue = mediaAttributeValue;
	}
	
	@Override
	 public String toString() {
	    return String.format(
	        "HeroImage [ imageUrl= %1$s, mediaAttributeValue= %2$s]",
	        imageUrl, mediaAttributeValue);
	  }

}
