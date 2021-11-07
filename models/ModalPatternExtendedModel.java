package org.kp.foundation.core.models;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.Via;
import org.kp.foundation.core.utils.LinkUtil;
import org.kp.foundation.core.utils.SlingModelUtil;
import org.kp.foundation.core.utils.WCMUseUtil;
import org.kp.patterns.core.models.ModalPatternModel;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.WCMMode;

/**
 * Sling Model class for Modal Pattern Extended component.
 * 
 * * @author Mohan Joshi
 */
@Model(adaptables = { SlingHttpServletRequest.class, Resource.class }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ModalPatternExtendedModel extends ModalPatternModel {

	private static final String MODAL_TITLE = "modalTitle";
	private static final String MODAL_TITLE_ALIGN = "modalTitleAlign";

	private static final String ICON_STYLE = "iconStyle";
	private static final String MODAL_CONTENT_PATH = "modalContentPath";
	private static final String ONCE_PER_SESSION = "oncePerSession";

	private static final String SHOW_ON_LOAD = "showOnload";
	private static final String BUTTON_ALIGNMENT = "buttonAlignment";
	private static final String CANCEL_BUTTON_SHOW = "cancelBtnShow";
	private static final String CANCEL_BUTTON_TEXT = "cancelBtnText";

	private static final String CANCEL_BUTTON_STYLE = "cancelBtnStyle";
	private static final String SAVE_CLOSE_BTN_SHOW = "saveAndCloseBtnShow";
	private static final String SAVE_CLOSE_BTN_TEXT = "saveAndCloseBtnText";
	private static final String SAVE_CLOSE_BTN_STYLE = "saveAndCloseBtnStyle";

	private static final String DONT_SHOW_AGAIN_TXT = "dontShowAgainText";
	private static final String BUTTON1 = ".modal-btn1";
	private static final String BUTTON1_SHOW = "btn1Show";
	private static final String BUTTON1_TEXT = "btn1Text";
	private static final String BUTTON1_SHOW_CHECKBOX = "btn1ShowCheckbox";
	private static final String BUTTON1_PRIMARY_CLASS = "primary";
	private static final String BUTTON1_DISABLED_CLASS = "-disabled";

	private static final String BUTTON1_CHECKBOX_TEXT = "btn1CheckboxText";
	private static final String BUTTON1_DISABLE_CHECKED = "btn1DisableUntilChecked";
	private static final String BUTTON2_SHOW = "btn2Show";
	private static final String BUTTON2_TEXT = "btn2Text";
	private static final String TRUE_VALUE = "true";
	private static final String ONCE = "once";
	private static final String ALWAYS = "always";
	private static final String IMAGE_ROOT_PATH = "/etc.clientlibs/settings/wcm/designs/kporg/kp-foundation/clientlib-modules/styleguide/resources/assets/icons/";
	
	

	private static final String MODAL_ONLOAD = "data-modal-onload=";
	private static final String MODAL_ICON_CLASS = "modal-icons-enabled";
	private Map<String,String> attrMap;

	@Inject
	SlingHttpServletRequest request;

	@Inject
	@Via("resource")
	@Optional
	@Named(MODAL_TITLE)
	private String modalTitle;

	@Inject
	@Via("resource")
	@Optional
	@Named(MODAL_TITLE_ALIGN)
	private String modalTitleAlign;

	@Inject
	@Via("resource")
	@Optional
	@Named(ICON_STYLE)
	private String iconStyle;

	@Inject
	@Via("resource")
	@Optional
	@Named(MODAL_CONTENT_PATH)
	private String modalContentPath;

	@Inject
	@Via("resource")
	@Named(ONCE_PER_SESSION)
	@Optional
	private String oncePerSession;

	@Inject
	@Via("resource")
	@Named(SHOW_ON_LOAD)
	@Optional
	private String showOnload;

	@Inject
	@Via("resource")
	@Optional
	@Named(BUTTON_ALIGNMENT)
	private String buttonAlignment;

	@Inject
	@Via("resource")
	@Optional
	@Named(CANCEL_BUTTON_SHOW)
	private String cancelBtnShow;

	@Inject
	@Via("resource")
	@Named(CANCEL_BUTTON_TEXT)
	@Optional
	private String cancelBtnText;

	@Inject
	@Via("resource")
	@Named(CANCEL_BUTTON_STYLE)
	@Optional
	private String cancelBtnStyle;

	@Inject
	@Via("resource")
	@Optional
	@Named(SAVE_CLOSE_BTN_SHOW)
	private String saveAndCloseBtnShow;

	@Inject
	@Via("resource")
	@Optional
	@Named(SAVE_CLOSE_BTN_TEXT)
	private String saveAndCloseBtnText;

	@Inject
	@Via("resource")
	@Named(SAVE_CLOSE_BTN_STYLE)
	@Optional
	private String saveAndCloseBtnStyle;

	@Inject
	@Via("resource")
	@Named(DONT_SHOW_AGAIN_TXT)
	@Optional
	private String dontShowAgainText;

	@Inject
	@Via("resource")
	@Optional
	@Named(BUTTON1_SHOW)
	private String btn1Show;

	@Inject
	@Via("resource")
	@Optional
	@Named(BUTTON1_TEXT)
	private String btn1Text;

	@Inject
	@Via("resource")
	@Named(BUTTON1_SHOW_CHECKBOX)
	@Optional
	private String btn1ShowCheckbox;

	@Inject
	@Via("resource")
	@Named(BUTTON1_CHECKBOX_TEXT)
	@Optional
	private String btn1CheckboxText;

	@Inject
	@Via("resource")
	@Optional
	@Named(BUTTON1_DISABLE_CHECKED)
	private Boolean btn1DisableUntilChecked;

	@Inject
	@Via("resource")
	@Optional
	@Named(BUTTON2_SHOW)
	private String btn2Show;

	@Inject
	@Via("resource")
	@Optional
	@Named(BUTTON2_TEXT)
	private String btn2Text;
	
	@Inject
	private Page currentPage;

	private boolean buttonsPresent = Boolean.FALSE;

	private boolean checkboxesPresent = Boolean.FALSE;

	private String showOnloadType = "";
	private String iconLabel = "";
	private String iconPath = "";
	
	HashMap<String, String> iconPathMap = new HashMap<String, String>();

	@PostConstruct
	public void init() {
		String currentPageLanguage = WCMUseUtil.getCurrentLanguage(currentPage);
		if (StringUtils.isNotBlank(iconStyle) && StringUtils.isNotBlank(currentPageLanguage)
				&& currentPageLanguage.equalsIgnoreCase("EN")) {
			iconLabel = getEnglishIconLabel(iconStyle);
		} else if (StringUtils.isNotBlank(iconStyle) && StringUtils.isNotBlank(currentPageLanguage)
				&& currentPageLanguage.equalsIgnoreCase("ES")) {
			iconLabel = getSpanishIconLabel(iconStyle);
		}		
		iconPathMap.put("none", IMAGE_ROOT_PATH+"no.svg");
		iconPathMap.put("checkmark", IMAGE_ROOT_PATH+"checkmarkcirclesolid.svg");
		iconPathMap.put("exclamation", IMAGE_ROOT_PATH+"errorcirclesolid.svg");
		iconPathMap.put("info", IMAGE_ROOT_PATH+"infocirclesolid.svg");
		iconPathMap.put("alert", IMAGE_ROOT_PATH+"alertsolid.svg");
		if(StringUtils.isNotBlank(iconStyle)) {
			iconPath	=  iconPathMap.get(iconStyle);
		}
	}

	/**
	 * @return the modalTitle
	 */
	public String getModalTitle() {
		return modalTitle;
	}

	/**
	 * @return the modalTitle
	 */
	public String getModalTitleAlign() {
		return modalTitleAlign;
	}

	/**
	 * @return the iconStyle
	 */
	public String getIconStyle() {
		return iconStyle;
	}
	/**
	 * @return the iconLabel
	 */
	public String getIconLabel() {
		return iconLabel;
	}

	/**
	 * @return the icon path
	 */
	public String getIconPath() {
		return iconPath;
	}

	/**
	 * @return the modalContentPath
	 */
	public String getModalContentPath() {
	  if (WCMMode.fromRequest(request) == WCMMode.EDIT) {
		modalContentPath = "";
	  }else {
	    modalContentPath = LinkUtil.getRelativeURL(request, modalContentPath);
	  }
		return modalContentPath;
	}

	/**
	 * @return the oncePerSession
	 */
	public String getOncePerSession() {
		return oncePerSession;
	}

	/**
	 * @return the showOnload
	 */
	public String getShowOnload() {
		return showOnload;
	}

	/**
	 * @return the buttonAlignment
	 */
	public String getButtonAlignment() {
		return buttonAlignment;
	}

	/**
	 * @return the cancelBtnShow
	 */
	public String getCancelBtnShow() {
		return cancelBtnShow;
	}

	/**
	 * @return the cancelBtnText
	 */
	public String getCancelBtnText() {
		return cancelBtnText;
	}

	/**
	 * @return the cancelBtnStyle
	 */
	public String getCancelBtnStyle() {
		return cancelBtnStyle;
	}

	/**
	 * @return the saveAndCloseBtnShow
	 */
	public String getSaveAndCloseBtnShow() {
		return saveAndCloseBtnShow;
	}

	/**
	 * @return the saveAndCloseBtnText
	 */
	public String getSaveAndCloseBtnText() {
		return saveAndCloseBtnText;
	}

	/**
	 * @return the saveAndCloseBtnStyle
	 */
	public String getSaveAndCloseBtnStyle() {
		return saveAndCloseBtnStyle;
	}

	/**
	 * @return the dontShowAgainText
	 */
	public String getDontShowAgainText() {
		return dontShowAgainText;
	}

	/**
	 * @return the btn1Show
	 */
	public String getBtn1Show() {
		return btn1Show;
	}

	/**
	 * @return the btn1Text
	 */
	public String getBtn1Text() {
		return btn1Text;
	}

	/**
	 * @return the btn1ShowCheckbox
	 */
	public String getBtn1ShowCheckbox() {
		return btn1ShowCheckbox;
	}

	/**
	 * @return the btn1CheckboxText
	 */
	public String getBtn1CheckboxText() {
		return btn1CheckboxText;
	}

	/**
	 * @return the btn1DisableUntilChecked
	 */
	public Boolean isBtn1DisableUntilChecked() {
		return btn1DisableUntilChecked;
	}

	/**
	 * @return the btn2Show
	 */
	public String getBtn2Show() {
		return btn2Show;
	}

	/**
	 * @return the btn2Text
	 */
	public String getBtn2Text() {
		return btn2Text;
	}
	
	/**
	 * @return the attrMap
	 */
	public Map<String, String> getAttrMap() {
		attrMap = SlingModelUtil.parseAttributeString(MODAL_ONLOAD + getShowOnloadType());
		return attrMap;
	}

	/**
	 * @return true if any of the checkbox is checked
	 */
	public boolean getCheckboxesPresent() {
		if ((btn1ShowCheckbox != null && btn1ShowCheckbox.equals(TRUE_VALUE))
				|| (saveAndCloseBtnShow != null && saveAndCloseBtnShow.equals(TRUE_VALUE))) {
			checkboxesPresent = true;
		}
		return checkboxesPresent;
	}

	/**
	 * @return true if any of the button is present
	 */
	public boolean getButtonsPresent() {
		if ((cancelBtnShow != null && cancelBtnShow.equals(TRUE_VALUE))
				|| (saveAndCloseBtnShow != null && saveAndCloseBtnShow.equals(TRUE_VALUE))
				|| (btn1Show != null && btn1Show.equals(TRUE_VALUE))
				|| (btn2Show != null && btn2Show.equals(TRUE_VALUE))) {
			buttonsPresent = true;
		}
		return buttonsPresent;
	}

	/**
	 * @return the showOnLoadType
	 */
	public String getShowOnloadType() {
		if (showOnload != null && showOnload.equals(TRUE_VALUE)) {
			if (oncePerSession != null && oncePerSession.equals(TRUE_VALUE)) {
				showOnloadType = ONCE;
			} else {
				showOnloadType = ALWAYS;
			}
		} else {
			showOnloadType = "";
		}
		return showOnloadType;
	}

	/**
	 * @return button disabled checkbox
	 */
	public String getBtn1DisabledCheckbox() {
		return isBtn1DisableUntilChecked() ? BUTTON1 : "";
	}

	/**
	 * @return button1 class
	 */
	public String getBtn1Class() {
		return isBtn1DisableUntilChecked() ? BUTTON1_DISABLED_CLASS : BUTTON1_PRIMARY_CLASS;
	}

	/**
	 * @return modal-icons-enabled class if modal icon is authored, otherwise
	 *         empty string.
	 */
	public String getIconEnabled() {
		return StringUtils.isNotBlank(iconStyle) ? MODAL_ICON_CLASS : StringUtils.EMPTY;
	}
	
	/**
	 * @param iconStyle
	 * @return icon label in English
	 */
	private String getEnglishIconLabel(String iconStyle) {
		HashMap<String, String> iconLabelMap = new HashMap<>();
		iconLabelMap.put("none", "None");
		iconLabelMap.put("checkmark", "Success");
		iconLabelMap.put("exclamation", "Error");
		iconLabelMap.put("info", "Information");
		iconLabelMap.put("alert", "Alert");
		return iconLabelMap.get(iconStyle);
	}
	
	/**
	 * @param iconStyle
	 * @return icon label in Spanish
	 */
	private String getSpanishIconLabel(String iconStyle) {
		HashMap<String, String> iconLabelMapES = new HashMap<>();
		iconLabelMapES.put("none", "Ninguna");
		iconLabelMapES.put("checkmark", "Éxito");
		iconLabelMapES.put("exclamation", "Error");
		iconLabelMapES.put("info", "Información");
		iconLabelMapES.put("alert", "Alerta");
		return iconLabelMapES.get(iconStyle);
	}
	
}