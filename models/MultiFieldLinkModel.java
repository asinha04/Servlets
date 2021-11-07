package org.kp.foundation.core.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;

/**
 * LinkModel class.
 * 
 * @author - Tirumala malladi
 * 
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class MultiFieldLinkModel implements Serializable {
	private static final long serialVersionUID = 1L;

	@Inject
	private String linkTarget;

	@Inject
	private String linkType;

	@Inject
	private String listType;

	@Inject
	private String linkLabel;

	@Inject
	private String linkPath;

	@Inject
	private String noLinkFollow;
	
//	this is not a duplicate a field for DLL component
	@Inject
    private String nolinkFollow;

	@Inject
	private String selectedPage;

	@Inject
	private String amount;

	@Inject
	private String id;

	@Inject
	private String ariaCurrent;

	@Inject
	private boolean mobileSelected;

	@Inject
	private String classes;

	@Inject
	private List<MultiFieldLinkModel> childLinks = new ArrayList<>();

	@Inject
	private String ariaLabelledBy;

	@Inject
	private String linkClass;

	@Inject
	private int index;
	
	@Inject
	private String mobilePosition;
	
	@Inject
	private String mobilePositionClass;
	
	@Inject
	private String attributeValue;

	@Inject
	private String attributeName;
	
	@Inject
	private String hideLinks;
	
	@Inject
	private String qualifiedLink;

	public void setLinkTarget(String linkTarget) {
		this.linkTarget = linkTarget;
	}

	public void setLinkType(String linkType) {
		this.linkType = linkType;
	}

	public String getLinkType() {
		return linkType;
	}

	public String getLinkTarget() {
		return linkTarget;
	}

	public String getListType() {
		return listType;
	}

	public void setListType(String listType) {
		this.listType = listType;
	}

	public String getLinkLabel() {
		return linkLabel;
	}

	public void setLinkLabel(String linkLabel) {
		this.linkLabel = linkLabel;
	}

	public String getLinkPath() {
		return linkPath;
	}

	public void setLinkPath(String linkPath) {
		this.linkPath = linkPath;
	}

	public String getNoLinkFollow() {
		return noLinkFollow;
	}

	public void setNoLinkFollow(String noLinkFollow) {
		this.noLinkFollow = noLinkFollow;
	}

	public String getSelectedPage() {
		return selectedPage;
	}

	public void setSelectedPage(String selectedPage) {
		this.selectedPage = selectedPage;
	}

	public List<MultiFieldLinkModel> getChildLinks() {
		return new ArrayList<>(childLinks);
	}

	public void setChildLinks(List<MultiFieldLinkModel> childLinks) {
		this.childLinks.addAll(childLinks);
	}

	/**
	 * @return the amount
	 */
	public String getAmount() {
		return amount;
	}

	/**
	 * @param amount the amount to set
	 */
	public void setAmount(String amount) {
		this.amount = amount;
	}

	/**
	 * @return the linkId
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param linkId the linkId to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	public String getAriaCurrent() {
		return ariaCurrent;
	}

	public void setAriaCurrent(String ariaCurrent) {
		this.ariaCurrent = ariaCurrent;
	}

	/**
	 * @return the mobileSelected
	 */
	public boolean isMobileSelected() {
		return mobileSelected;
	}

	/**
	 * @param mobileSelected the mobileSelected to set
	 */
	public void setMobileSelected(boolean mobileSelected) {
		this.mobileSelected = mobileSelected;
	}

	public String getClasses() {
		return classes;
	}

	public void setClasses(String classes) {
		this.classes = classes;
	}

	public String getAriaLabelledBy() {
		return ariaLabelledBy;
	}

	public void setAriaLabelledBy(String ariaLabelledBy) {
		this.ariaLabelledBy = ariaLabelledBy;
	}

	public String getLinkClass() {
		return linkClass;
	}

	public void setLinkClass(String linkClass) {
		this.linkClass = linkClass;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public String getMobilePosition() {
		return mobilePosition;
	}

	public void setMobilePosition(String mobilePosition) {
		this.mobilePosition = mobilePosition;
	}

	public String getMobilePositionClass() {
		return mobilePositionClass;
	}

	public void setMobilePositionClass(String mobilePositionClass) {
		this.mobilePositionClass = mobilePositionClass;
	}

    public String getNolinkFollow() {
      return nolinkFollow;
    }
  
    public void setNolinkFollow(String nolinkFollow) {
      this.nolinkFollow = nolinkFollow;
    }

	public String getAttributeValue() {
		return attributeValue;
	}

	public void setAttributeValue(String attributeValue) {
		this.attributeValue = attributeValue;
	}

	public String getAttributeName() {
		return attributeName;
	}

	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}

    /**
     * @return String return the hideLinks
     */
    public String getHideLinks() {
        return hideLinks;
    }

    /**
     * @param hideLinks the hideLinks to set
     */
    public void setHideLinks(String hideLinks) {
        this.hideLinks = hideLinks;
    }

	/**
	 * @return the qualifiedLink
	 */
	public String getQualifiedLink() {
		return qualifiedLink;
	}

	/**
	 * @param qualifiedLink the qualifiedLink to set
	 */
	public void setQualifiedLink(String qualifiedLink) {
		this.qualifiedLink = qualifiedLink;
	}

}