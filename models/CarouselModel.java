package org.kp.foundation.core.models;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.jcr.RepositoryException;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Via;
import org.kp.foundation.core.utils.LinkUtil;

/**
 * Sling Model class for Carousel Component.
 * 
 * * @author Mohan Joshi
 */
@Model(adaptables = { Resource.class,
		SlingHttpServletRequest.class }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class CarouselModel extends BaseModel {

	private static final String NO_FOLLOW_TAG = "noFollow";
	private static final String PROPERTY_NAME = "options";
	private static final String NO_PREVIEW = "-no-preview";
	private static final String HR_LINE = "-line";

	@Inject
	SlingHttpServletRequest request;

	@Inject
	@Via("resource")
	@Default(values = "")
	private String slidesToShow;

	@Inject
	@Via("resource")
	@Default(values = "")
	private String includeDots;

	@Inject
	@Via("resource")
	@Default(values = "")
	private String carouselPreview;

	@Inject
	@Via("resource")
	@Default(values = "")
	private String infiniteScroll;

	@Inject
	@Via("resource")
	@Default(values = "")
	private String carouselStyle;

	private List<MultifieldImageModel> imageModelsList = null;
	
	@PostConstruct
	protected void init() {
		imageModelsList = new ArrayList<MultifieldImageModel>();
		Resource linkRootRes = request.getResource().getChild(PROPERTY_NAME);
		
		if(linkRootRes!=null) {
			Iterable<Resource> linkResItr = linkRootRes.getChildren();	
			for(Resource res : linkResItr) {
				MultifieldImageModel imageModel = res.adaptTo(MultifieldImageModel.class);
				createImageModel(imageModel);
			}
		}
	}

	private void createImageModel(MultifieldImageModel imageModel) {
		if(imageModel!=null) {
			String path = LinkUtil.getRelativeURL(request, imageModel.getLinkPath());
			imageModel.setLinkPath(path);
			if (imageModel.getNoLinkFollow() != null) {
				boolean noFollow = Boolean.parseBoolean(imageModel.getNoLinkFollow());
				imageModel.setNoLinkFollow(noFollow ? NO_FOLLOW_TAG : "");
			}
			if (imageModel.getHrLine() != null) {
				boolean hrLine = Boolean.parseBoolean(imageModel.getHrLine());
				imageModel.setHrLine(hrLine ? HR_LINE : "");
			}
			imageModelsList.add(imageModel);
		}
	}
	
	/**
	 * This method gets the authored multifield slides and returns a list to the
	 * carousel html.This html iterates over the list and displays the entire
	 * carousel.
	 * 
	 * @return List
	 * @throws RepositoryException
	 *             when multifield has error
	 */
	public List<MultifieldImageModel> getListOfSlides() throws RepositoryException {
		return new ArrayList<>(imageModelsList);
	}

	/**
	 * Getter for slidesToShow.
	 * 
	 * @return the slidesToShow
	 */
	public String getSlidesToShow() {
		return slidesToShow;
	}

	/**
	 * Getter for includeDots.
	 * 
	 * @return the includeDots
	 */
	public String getIncludeDots() {
		boolean dots = Boolean.parseBoolean(includeDots);
		includeDots = dots ? "true" : "false";
		return includeDots;
	}

	/**
	 * Getter for carouselPreview.
	 * 
	 * @return the carouselPreview
	 */
	public String getCarouselPreview() {
		if (!StringUtils.isEmpty(carouselPreview)) {
			boolean preview = Boolean.parseBoolean(carouselPreview);
			carouselPreview = preview ? "" : NO_PREVIEW;
		}
		return carouselPreview;
	}

	/**
	 * Getter for infiniteScroll.
	 * 
	 * @return the infiniteScroll
	 */
	public String getInfiniteScroll() {
		return infiniteScroll;
	}

	/**
	 * Getter for carouselStyle.
	 * 
	 * @return the carouselStyle
	 */
	public String getCarouselStyle() {
		boolean style = Boolean.parseBoolean(carouselStyle);
		carouselStyle = style ? "-modal-carousel" : "-full-carousel";
		return carouselStyle;
	}
}