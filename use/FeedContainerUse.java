package org.kp.foundation.core.use;

import com.adobe.cq.sightly.WCMUsePojo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.kp.foundation.core.utils.PropertyInheritedUtil;

/**
 * Created by Devi Anala on 3/21/2017.
 */
public class FeedContainerUse extends WCMUsePojo {
    protected final Logger LOGGER = LoggerFactory.getLogger(FeedContainerUse.class);
    private String title;
    private String titleCount;
    private String displayLimit;
    private String hide;
    private String pagination;
    private String display;
    private static final String CONTAINER_TITLE="title";
    private static final String TITLE_COUNT="titleCount";
    private static final String DISPLAY_LIMIT="displayLimit";
    private static final String HIDE_FEED_CONTAINER="hide";
    private static final String CONTAINER_PAGINATION="pagination";
    private static final String DISPLAY_RESULTS="display";

    @Override
    public void activate() throws Exception {

        title = PropertyInheritedUtil.getProperty(getResource(),CONTAINER_TITLE);
        titleCount = PropertyInheritedUtil.getProperty(getResource(),TITLE_COUNT);
        displayLimit = PropertyInheritedUtil.getProperty(getResource(),DISPLAY_LIMIT);
        hide = PropertyInheritedUtil.getProperty(getResource(),HIDE_FEED_CONTAINER);
        pagination = PropertyInheritedUtil.getProperty(getResource(),CONTAINER_PAGINATION);
        display = PropertyInheritedUtil.getProperty(getResource(), DISPLAY_RESULTS);
    }

    public String getTitle() {
        return title;
    }

    public String getTitleCount() {
        return titleCount;
    }

    public String getDisplayLimit() {
        return displayLimit;
    }

    public String getHide() {
        return hide;
    }

    public String getPagination() {
        return pagination;
    }

    public String getDisplay() {
        return display;
    }
}