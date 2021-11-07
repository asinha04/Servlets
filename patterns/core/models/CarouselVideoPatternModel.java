package org.kp.patterns.core.models;

import javax.inject.Inject;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.kp.foundation.core.constants.GlobalConstants;
import org.kp.foundation.core.utils.GenericUtil;

/**
 * Model class for carousel
 * 
 * @author Ravish
 *
 */
@Model(adaptables = Resource.class)
public class CarouselVideoPatternModel {

	@Inject
	@Optional
	@Default(values = "")
	private String playlistid;

	@Inject
	@Optional
	@Default(intValues = 4)
	private int slidesToShow;

	@Inject
	@Optional
	@Default(booleanValues = true)
	private boolean infiniteScroll;
	
	private String PROD_RUN_MODE="Prod";
	
	/**
	 * .
	 * 
	 * @return playlistid
	 */
	public String getPlaylistid() {
		return playlistid;
	}

	/**
	 * .
	 * 
	 * @return slidesToShow
	 */
	public int getSlidesToShow() {
		return slidesToShow;
	}

	/**
	 * @return infiniteScroll
	 */
	public boolean isInfiniteScroll() {
		return infiniteScroll;
	}
	
	/**
	 * .
	 * 
	 * @return nothing
	 */
	public void setPlaylistid(String playlistid) {
		this.playlistid = playlistid;
	}

	public String getRunMode() {
	  return GenericUtil.isRunModeAvailable(GlobalConstants.PROD_RUN_MODE) ? PROD_RUN_MODE: "";
	}
}