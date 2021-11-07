package org.kp.foundation.core.models;

import java.util.HashMap;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Via;

/**
 * ImageSlingModel class is responsible for adding image to the component as
 * authored content.
 * 
 * @author Mallika V
 *
 */
@Model(adaptables = { Resource.class,
		SlingHttpServletRequest.class }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ImageModel extends BaseModel {

	@Inject
	@Via("resource")
	private boolean imageNoFollow;
	@Inject
	@Via("resource")
	private String imageTarget;
	@Inject
	@Via("resource")
	private String imageCaption;
	@Inject
	@Via("resource")
	private String imageLink;
	@Inject
	@Via("resource")
	private String imageAltText;
	@Inject
	@Via("resource")
	private String fileReference;
	private String ldt;
	private String mdt;
	private String sdt;
	private String tablet;
	private String mobile;
	private String defaultImage;
	boolean imageSize;
	private static final String LARGE_IMAGE = "-l-dt";
	private static final String MEDIUM_IMAGE = "-m-dt";
	private static final String SMALL_IMAGE = "-s-dt";
	private static final String TABLET_IMAGE = "tablet";
	private static final String MOBILE_IMAGE = "mobile";
    private static final String NO_FOLLOW_TAG = "nofollow";
    private static final String NO_FOLLOW_EMPTY = "";

	@Inject
	private ResourceResolver resourceResolver;

	@PostConstruct
	// PostConstructs are called after all the injection has occurred, but
	// before the Model object is
	// returned for use.
	public void init() {
	  initImage();
	}
	
	/**
     * 
     * This method initializes the image 
     */
    public void initImage() {
      imageSize = true;
      if (null != fileReference) {
          defaultImage = fileReference;
          Resource resource = resourceResolver.getResource(fileReference);
          if (null != resource) {
              HashMap<String, String> renditionsMap = new HashMap<String, String>();
              for (Resource res : resource.getParent().getChildren()) {
                  if (checkImageContains(res, LARGE_IMAGE)) {
                      renditionsMap.put(LARGE_IMAGE, res.getPath());
                      ldt = res.getPath();
                  } else if (checkImageContains(res, MEDIUM_IMAGE)) {
                      renditionsMap.put(MEDIUM_IMAGE, res.getPath());
                      mdt = res.getPath();
                  } else if (checkImageContains(res, SMALL_IMAGE)) {
                      renditionsMap.put(SMALL_IMAGE, res.getPath());
                      sdt = res.getPath();
                  } else if (checkImageContains(res, TABLET_IMAGE)) {
                      renditionsMap.put(TABLET_IMAGE, res.getPath());
                      tablet = res.getPath();
                  } else if (checkImageContains(res, MOBILE_IMAGE)) {
                      renditionsMap.put(MOBILE_IMAGE, res.getPath());
                      mobile = res.getPath();
                  }
              }
              defaultImage = setDefaultImg(renditionsMap);
          }
      }
    }	
	
	/**
	 * This method checks if resource contains the image suffix or not.
	 * 
	 * @return the boolean if suffix is found in resource.
	 */
	public boolean checkImageContains(Resource res, String imageSuffix) {
		boolean imageContains;
		imageContains = res.getName().contains(imageSuffix);
		return imageContains;
	}

	/**
	 * This method gets the default image out of the various renditions.
	 * 
	 * @return the default image which is largest renditions
	 */
	public String setDefaultImg(HashMap<String, String> renditionsMap) {
		if (renditionsMap.containsKey(LARGE_IMAGE)) {
			defaultImage = renditionsMap.get(LARGE_IMAGE);
		} else if (renditionsMap.containsKey(MEDIUM_IMAGE)) {
			defaultImage = renditionsMap.get(MEDIUM_IMAGE);
		} else if (renditionsMap.containsKey(SMALL_IMAGE)) {
			defaultImage = renditionsMap.get(SMALL_IMAGE);
		} else if (renditionsMap.containsKey(TABLET_IMAGE)) {
			defaultImage = renditionsMap.get(TABLET_IMAGE);
		} else if (renditionsMap.containsKey(MOBILE_IMAGE)) {
			defaultImage = renditionsMap.get(MOBILE_IMAGE);
		}
		return defaultImage;
	}

	/**
	 * Getter for defaultImage.
	 * 
	 * @return the defaultImage
	 */
	public String getDefaultImage() {
		return defaultImage;
	}

	/**
	 * Getter for imageLDT.
	 * 
	 * @return the imageLDT
	 */
	public String getLdt() {
		return ldt;
	}

	/**
	 * Getter for imageMDT.
	 * 
	 * @return the imageMDT
	 */
	public String getMdt() {
		return mdt;
	}

	/**
	 * Getter for imageSDT.
	 * 
	 * @return the imageSDT
	 */
	public String getSdt() {
		return sdt;
	}

	/**
	 * Getter for imageTablet.
	 * 
	 * @return the imageTablet
	 */
	public String getTablet() {
		return tablet;
	}

	/**
	 * Getter for imageMobile.
	 * 
	 * @return the imageMobile
	 */
	public String getMobile() {
		return mobile;
	}

	/**
	 * Getter for imageCaption.
	 * 
	 * @return the imageCaption
	 */
	public String getImageNoFollow() {
		return imageNoFollow ? NO_FOLLOW_TAG : NO_FOLLOW_EMPTY;
	}

	/**
	 * Getter for imageCaption.
	 * 
	 * @return the imageCaption
	 */
	public String getImageCaption() {
		return imageCaption;
	}

	/**
	 * Getter for imageLink.
	 * 
	 * @return the imageLink
	 */
	public String getImageLink() {
		return imageLink;
	}

	/**
	 * Getter for imageAltText.
	 * 
	 * @return the imageAltText
	 */
	public String getImageAltText() {
		return imageAltText;
	}

	/**
	 * Getter for imagePathTarget.
	 * 
	 * @return the imagePathTarget
	 */
	public String getImageTarget() {
		return imageTarget;
	}

	/**
	 * Getter for fileReference.
	 * 
	 * @return the fileReference
	 */
	public String getFileReference() {
		return fileReference;
	}

}
