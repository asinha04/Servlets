package org.kp.foundation.core.use;

import com.adobe.cq.sightly.WCMUsePojo;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ContainerUse class is responsible to read color and include margin selected by
 * the author
 * 
 * @author Mallika Venkapalli
 *
 */

public class ContainerUse extends WCMUsePojo {

	private static final Logger LOGGER = LoggerFactory.getLogger(ContainerUse.class);

	private String color;
	private String includeMargin;
	private String containerHeight;
	private static final String INCLUDE_MARGIN = "includeMargin";
	private static final String CONTAINER_COLOR = "color";
	private static final String CONTAINER_HEIGHT = "containerHeight";
	private static final String HEIGHT = "height:";
	private static final String STYLE = "px;";

	@Override
	public void activate() throws Exception {
		LOGGER.info("Activating Container Use");
		color = getProperties().get(CONTAINER_COLOR, "none");
		includeMargin = getProperties().get(INCLUDE_MARGIN, "false");
		containerHeight = getProperties().get(CONTAINER_HEIGHT, "");
	}

	public String getColor() {
		return color;
	}
	public String getIncludeMargin() {
	    return  Boolean.parseBoolean(includeMargin) ? "container-page" : "";
	}
	
	/**
	 * @return the style to apply scroll in container
	 */
	public String getStyles() {
		String styles = "";
		if (StringUtils.isNotBlank(containerHeight)) {
			styles = HEIGHT + containerHeight + STYLE;
		}
		return styles;
	}
}
