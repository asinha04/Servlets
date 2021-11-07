package org.kp.foundation.core.models;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.adobe.cq.wcm.core.components.models.Image;
import com.adobe.cq.wcm.core.components.models.ImageArea;
import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.DamConstants;
import com.day.cq.dam.api.Rendition;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.*;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.via.ResourceSuperType;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.List;

@Model(adaptables = SlingHttpServletRequest.class,
        adapters = { Image.class, ComponentExporter.class },
        resourceType = ImageCoreModel.RESOURCE_TYPE,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME,
        extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class ImageCoreModel implements Image {
    public static final String RESOURCE_TYPE = "kporg/kp-foundation/components/core/image";
    
    @Inject
    private SlingHttpServletRequest request;

    private String originalHeight;

    private String originalWidth;
    
    private String tablet;
    
    private String mobile;
    
	private static final String MEDIUM_IMAGE = "600";
	private static final String SMALL_IMAGE = "319";
    

    @Self
    @Via(type = ResourceSuperType.class)
    private Image image;

    @PostConstruct
    protected void init() {
    	Resource imageResource=  request.getResourceResolver().getResource(this.getFileReference());
        if (imageResource != null) {
            Asset asset = imageResource.adaptTo(Asset.class);
            if (asset != null) {
                originalWidth = asset.getMetadataValue(DamConstants.TIFF_IMAGEWIDTH);
                originalHeight = asset.getMetadataValue(DamConstants.TIFF_IMAGELENGTH);
                List<Rendition> renditions =  asset.getRenditions();
                for(Rendition versions: renditions) {
                 if(checkRenditonCheck(versions, MEDIUM_IMAGE)) {
                	  tablet = versions.getPath();
                  }
                  else if(checkRenditonCheck(versions,SMALL_IMAGE)) {
                	  mobile = versions.getPath();
                  }
                }
                
                
            }
        }
    }
    
    public boolean checkRenditonCheck(Rendition versions, String imageSuffix) {
    	boolean imageContains;
    	imageContains = versions.getPath().contains(imageSuffix);
		return imageContains;
	}

	public String getImageHeight() {
        return originalHeight;
    }

    public String getImageWidth() {
        return originalWidth;
    }

    @Override
    public String getSrc() {
        return image.getSrc();
    }

    @Override
    public String getAlt() {
        return image.getAlt();
    }

    @Override
    public String getTitle() {
        return image.getTitle();
    }

    @Override
    public String getUuid() {
        return image.getUuid();
    }

    @Override
    public String getLink() {
        return image.getLink();
    }

    @Override
    public boolean displayPopupTitle() {
        return image.displayPopupTitle();
    }

    @Override
    @JsonIgnore
    public String getFileReference() {
        return image.getFileReference();
    }

    @Override
    @NotNull
    public int[] getWidths() {
        return image.getWidths();
    }

    @Override
    public String getSrcUriTemplate() {
        return image.getSrcUriTemplate();
    }

    @Override
    public boolean isLazyEnabled() {
        return image.isLazyEnabled();
    }

    @Override
    public List<ImageArea> getAreas() {
        return image.getAreas();
    }

    @Override
    @NotNull
    public String getExportedType() {
        return image.getExportedType();
    }

    @Override
    public boolean isDecorative() {
        return image.isDecorative();
    }

	public String getTablet() {
		return tablet;
	}

	public String getMobile() {
		return mobile;
	}
}
