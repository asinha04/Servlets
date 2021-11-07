package org.kp.foundation.core.models;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.Via;
import org.kp.foundation.core.models.ImageModel;

/**
 * This is the Model class for Text and Image component. This model class is embedding the image
 * model object in such a way that there is no need to inject the image properties here. Those image
 * properties will be adapted to imageModel object automatically in the init method.
 * 
 * @author Mohan Joshi
 *
 */
@Model(adaptables = {Resource.class, SlingHttpServletRequest.class }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class TextAndImageModel extends ImageModel{

  @Inject
  @Default(values = "")
  @Via("resource")
  private String subText;

  @Inject
  @Default(values = "")
  @Via("resource")
  private String layoutStyle;

  private boolean headerOnTop = Boolean.TRUE;

  @PostConstruct
  public void init() {
    initImage();
    if (layoutStyle.equalsIgnoreCase("-layout3")) {
      headerOnTop = Boolean.FALSE;
    }
  }


  /**
   * @return the subText
   */
  public String getSubText() {
    return subText;
  }

  /**
   * @return the layoutStyle
   */
  public String getLayoutStyle() {
    return layoutStyle;
  }

  public boolean isHeaderOnTop() {
    return headerOnTop;
  }

  public void setHeaderOnTop(boolean headerOnTop) {
    this.headerOnTop = headerOnTop;
  }
}
