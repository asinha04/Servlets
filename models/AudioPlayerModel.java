/**
 * 
 */
package org.kp.foundation.core.models;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.Via;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Madhu Chaganti(D107273)
 * Sling modal to retrieve the authored content of audio-player component
 * 
 */

@Model(adaptables = {SlingHttpServletRequest.class},defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class AudioPlayerModel {
	private static final Logger LOGGER = LoggerFactory.getLogger(AudioPlayerModel.class);

	@Inject
	Resource resource;

	private static final String PLAYER_ID = "playerID";

	private static final String TRANSCRIPT_LABEL = "transcriptLabel";

	private static final String TRANSCRIPT_URL = "transcriptUrl";
	
	private static final String PLAYER_WIDTH = "playerWidth";

	private static final String PLAYER_HEIGHT = "playerHeight";

	private static final String DISABLE_DOWNLOAD_BUTTON = "disableDownloadButton";

	private static final String THUMBNAIL_WIDTH = "thumbnailWidth";

	private static final String THUMNAIL_HEIGHT = "thumbnailHeight";
	
	private static final String REG_EXPRESSION="[^a-zA-Z0-9 -]";
	
	private static final String SORT_TYPE = "sortType";
	
	private static final String SORT_STRING_ASC="asc";
	
	private static final String SORT_STRING_DESC="desc";
	
	private static final String STRING_TRUE="true";
	
	private static final String ENABLE_SHARE_BUTTON = "enableShareButton";
	
	@Inject
	@Via("resource")
	@Named(PLAYER_ID)
	private String playerID;

	@Inject
	@Via("resource")
	@Named(TRANSCRIPT_LABEL)
	private String transcriptLabel;

	@Inject
	@Via("resource")
	@Named(TRANSCRIPT_URL)
	private String transcriptUrl;

	@Inject
	@Via("resource")
	@Named(PLAYER_HEIGHT)
	private String playerHeight;
	
	@Inject
	@Via("resource")
	@Named(PLAYER_WIDTH)
	private String playerWidth;

	@Inject
	@Via("resource")
	@Named(DISABLE_DOWNLOAD_BUTTON)
	private String disableDownloadButton;

	@Inject
	@Via("resource")
	@Named(THUMBNAIL_WIDTH)
	private String thumbnailWidth;
	
	@Inject
	@Via("resource")
	@Named(THUMNAIL_HEIGHT)
	private String thumbnailHeight;
	
	@Inject
	@Via("resource")
	@Named(SORT_TYPE)
	private String sortType;
	
	@Inject
	@Via("resource")
	@Named(ENABLE_SHARE_BUTTON)
	@Default(values = "false")
	private String enableShareButton;
	
	private String uniqueId="";
	
	/**
	 *
	 */
	@PostConstruct
	protected void init() {
		LOGGER.debug("Start init method");	
		StringBuilder uniqueString=new StringBuilder();
		uniqueString.append(playerID);
		uniqueString.append("-").append(resource.getName());
		Resource parent=resource.getParent();
		if(null!=parent){
			uniqueString.append("-").append(parent.getName().replaceAll(REG_EXPRESSION, ""));
		}
		uniqueId=uniqueString.toString();
		LOGGER.debug("End init method");
	}

	/**
	 * @return the playerID
	 */
	public String getPlayerID() {
		return playerID;
	}

	/**
	 * @return the transcriptLabel
	 */
	public String getTranscriptLabel() {
		return transcriptLabel;
	}

	/**
	 * @return the transcriptUrl
	 */
	public String getTranscriptUrl() {
		return transcriptUrl;
	}


	/**
	 * @return the playerHeight
	 */
	public String getPlayerHeight() {
		return playerHeight;
	}
	
	/**
	 * @return the playerWidth
	 */
	public String getPlayerWidth() {
		return playerWidth;
	}

	/**
	 * @return the disableDownloadButton
	 */
	public String getDisableDownloadButton() {
		return disableDownloadButton;
	}

	/**
	 * @return the thumbnailWidth
	 */
	public String getThumbnailWidth() {
		return thumbnailWidth;
	}

	/**
	 * @return the thumbnailHeight
	 */
	public String getThumbnailHeight() {
		return thumbnailHeight;
	}
	
	/**
	 * @return the enableShareButton
	 */
	public String getEnableShareButton() {
		return enableShareButton;
	}
	
	/**
	 *  @return
	 *  This method returns the unique id for passing to "QUMU" player widget 'selector' parameter
	 *  and to use as unqiue id for <div>
	 */
	public String getUniqueId(){
		return uniqueId;
	}
	
	/**
	 * @return the asc/desc string based on the author configuration
	 * if sortDesc in dialog is true then return 'desc' else 'asc'
	 * The variable is used to sort the playlist in the audio component
	 */
	public String getSortType() {
		String sortStr = SORT_STRING_ASC;
		if(sortType.equals(STRING_TRUE)){
			sortStr = SORT_STRING_DESC;
		}
		return sortStr;
	}
}
