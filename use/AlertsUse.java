package org.kp.foundation.core.use;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.kp.foundation.core.constants.AlertsComponent;
import org.kp.foundation.core.constants.GlobalConstants;
import org.kp.foundation.core.constants.LanguagePicker;
import org.kp.foundation.core.constants.RegionPicker;
import org.kp.foundation.core.enums.AlertsEnum;
import org.kp.foundation.core.exception.GenericRuntimeException;
import org.kp.foundation.core.models.AlertModel;
import org.kp.foundation.core.models.ExcludedPagesModel;
import org.kp.foundation.core.service.AlertsQueryService;
import org.kp.foundation.core.utils.GenericUtil;
import org.kp.foundation.core.utils.LinkUtil;
import org.kp.foundation.core.utils.PropertyInheritedUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.wcm.api.Page;

/**
 * The AlertsUse class provides the logic to return alerts detail for Notification and Bulletin
 * sections.
 *
 * @author Ravish Sehgal
 *
 */
//Waiver details on GSC-3623
@SuppressWarnings({"squid:S3776"})
public class AlertsUse extends WCMUsePojo {

  private static final Logger LOGGER = LoggerFactory.getLogger(AlertsUse.class);
  
  AlertsQueryService service;
  private String p1Path;
  private String p2Path;
  private String p3Path;
  private int maxLimit = AlertsComponent.MAX_LIMIT_NOTIFICATION_ALERTS;
  String excludedPageName;
  String tempExcludedPageName [] = null;
  List<ExcludedPagesModel> excludedList = new ArrayList<ExcludedPagesModel>();
  Page currRegion;

  @Override
  /**
   *
   * Activate method that Sightly executes
   */
  public void activate() throws Exception {
    currRegion = getCurrentPage().getAbsoluteParent(RegionPicker.CUR_REGION_ROOT_LEVEL);
    Page languagePage = getCurrentPage().getAbsoluteParent(LanguagePicker.SITE_LANGUAGE_PAGE_LEVEL);

    if (languagePage != null) {
      this.p1Path =
          languagePage.getPath() + AlertsComponent.NATIONAL_PATH + AlertsComponent.ALERTS_P1_PATH;
    }
    if (currRegion != null) {
      this.p2Path = currRegion.getPath() + AlertsComponent.ALERTS_P2_PATH;
      this.p3Path = currRegion.getPath() + AlertsComponent.ALERTS_P3_PATH;
    }
    String maxLimitStr = getProperties().get(GlobalConstants.MAX_LIMIT, StringUtils.EMPTY);
    if (StringUtils.isNotEmpty(maxLimitStr)) {
      try {
        maxLimit = Integer.parseInt(maxLimitStr.trim());
      } catch (NumberFormatException e) {
          throw new GenericRuntimeException("AlertsUse :: NumberFormatException :: setting maximum limit for alerts please fix in Alert Notification component dialog, restoring max limit to 5 {}.", e);
      }
    }
  }

  /**
   * Provides list of alerts which needs to be depicted on Notification section on Header for each
   * page.
   *
   * @return alertsList
   */
  public List<AlertModel> getNotificationAlerts() { 
    // Full path is used to ensure that alerts are always picked from
    // national home page.
    List<AlertModel> results1 = getAlerts(p1Path, true);
    if (results1.size() < maxLimit) {
      List<AlertModel> results2 = getAlerts(p2Path, true);
      int diff = maxLimit - results1.size();
      if (results2.size() > diff) {
        results1.addAll(results2.subList(0, diff));
      } else {
        results1.addAll(results2);
      }
    }
    return results1;
  }

  /**
   * Obtain list of alerts for a give path and max limit.
   * 
   * @param path -
   * @param isMaxLimit -
   * @return -
   */
  private List<AlertModel> getAlerts(String path, Boolean isMaxLimit) {
    List<AlertModel> results = new ArrayList<AlertModel>();
    try {
      Resource res = getResourceResolver().getResource(path);
      if (res != null) {
        Page currPage = res.adaptTo(Page.class);
        if (currPage != null) {
          if(null != currPage.getProperties()) {
            ValueMap vp = currPage.getProperties();
            if (vp.containsKey(AlertsComponent.EXCLUDED_PAGES_PROPERTY)) {
              tempExcludedPageName = vp.get(AlertsComponent.EXCLUDED_PAGES_PROPERTY, String[].class);
            }
          }
          java.util.Iterator<Page> landingPageItr = currPage.listChildren();
          int count = 1;
          while (landingPageItr != null && landingPageItr.hasNext()) {
            AlertModel model = new AlertModel();
            Page page = landingPageItr.next();
            Resource resource = page.getContentResource();
            if (resource != null) {
              ValueMap contentValueMap = resource.adaptTo(ValueMap.class);
              String pagePath = page.getPath();
              if (isRTE(contentValueMap)) {
                setAlertModelRTE(results, model, resource, contentValueMap, pagePath);
              } else {
                setAlertModelTTL(results, model, resource, contentValueMap, pagePath);
              }
            }
            if (isMaxLimit) {
              if (++count > maxLimit) {
                break;
              }
            }
          }
        }
      }
    } catch (Exception e) {
      throw new GenericRuntimeException("AlertsUse :: error in getALerts() method", e);
    }
    return results;
  }

  private void setAlertModelRTE(List<AlertModel> results, AlertModel model, Resource resource,
      ValueMap contentValueMap, String pagePath) {

    if (pagePath.contains(AlertsComponent.ALERTS_P2_PATH)) {
      model.setAlertType(AlertsEnum.P2);
    } else if (pagePath.contains(AlertsComponent.ALERTS_P1_PATH)) {
      model.setAlertType(AlertsEnum.P1);
    } else if (pagePath.contains(AlertsComponent.ALERTS_P3_PATH)) {
      model.setAlertType(AlertsEnum.P3);
    }
    model.setMessageDetail(
        contentValueMap.get(AlertsComponent.ALERT_FULL_CONTENT, StringUtils.EMPTY));
    disableAlertsOnExcludedPages(model, resource, contentValueMap, results);
  }

  private boolean isRTE(ValueMap contentValueMap) {

    if (contentValueMap.get(AlertsComponent.ALERT_FULL_CONTENT, StringUtils.EMPTY).trim()
        .length() > 0) {
      return true;
    }
    return false;
  }

  private void setAlertModelTTL(List<AlertModel> results, AlertModel model, Resource resource,
      ValueMap contentValueMap, String pagePath) {

    String externalURL = contentValueMap.get(AlertsComponent.ALERT_EXTERNAL_URL, StringUtils.EMPTY);
    if (StringUtils.isNotEmpty(externalURL)) {
      model.setPath(externalURL);
    } else {
      model.setPath(LinkUtil.getPathfieldURL(pagePath));
    }
    if (pagePath.contains(AlertsComponent.ALERTS_P2_PATH)) {
      model.setAlertType(AlertsEnum.P2);
    } else if (pagePath.contains(AlertsComponent.ALERTS_P1_PATH)) {
      model.setAlertType(AlertsEnum.P1);
    } else if (pagePath.contains(AlertsComponent.ALERTS_P3_PATH)) {
      model.setAlertType(AlertsEnum.P3);
    }
    model.setTitle(contentValueMap.get(AlertsComponent.ALERT_NOTIFICATION_TEXT, StringUtils.EMPTY));
    model.setLinkText(contentValueMap.get(AlertsComponent.ALERT_LINK_TEXT, StringUtils.EMPTY));
    LOGGER.debug("Alert resource-->", resource.getPath());
    disableAlertsOnExcludedPages(model, resource, contentValueMap, results);
  }

  /**
   * @param model - .
   * @param resource -
   * @param contentValueMap -
   * @param results -
   */
  
  private void disableAlertsOnExcludedPages(AlertModel model, Resource resource,
      ValueMap contentValueMap, List<AlertModel> results) {
    if (PropertyInheritedUtil.getListProperty(resource,
        AlertsComponent.EXCLUDED_PAGES_PROPERTY) != null) {
//    Add Parent page properties along with the child pages to an array to disable alerts from all the specified pages
      String [] excludedPages = PropertyInheritedUtil.getListProperty(resource, AlertsComponent.EXCLUDED_PAGES_PROPERTY);
      excludedPages = (String[])ArrayUtils.addAll(tempExcludedPageName, excludedPages);
      excludedPages = Arrays.stream(excludedPages).distinct().toArray(String[]::new);
      excludedList = new ArrayList<ExcludedPagesModel>();
      for (String excludedPage : excludedPages) {
        try {
          LOGGER.debug("excludePage-->", excludedPage);
          ExcludedPagesModel excludedPageModel = new ExcludedPagesModel();
          
          if(model.getAlertType().equals(AlertsEnum.P1) && !currRegion.getName().equalsIgnoreCase(AlertsComponent.ALERT_NATIONAL)) {
            excludedPage = excludedPage.replaceAll(AlertsComponent.ALERT_NATIONAL, currRegion.getName());
          }
          
          excludedPageModel.setExcludedPage(excludedPage);
          excludedPageName = excludedPageModel.getExcludedPage();
          if(null != getResourceResolver().getResource(excludedPageName)) {
            Page excludedPageNamePg =
                getResourceResolver().getResource(excludedPageName).adaptTo(Page.class);
  
            Page excludedPageNamePgRegion =
                excludedPageNamePg.getAbsoluteParent(RegionPicker.CUR_REGION_ROOT_LEVEL);
            
            LOGGER.debug("Excluded Page name ::"
                ,excludedPageName.substring(excludedPageName.lastIndexOf('/') + 1));
            LOGGER.debug("Excluded Page region-->", excludedPageNamePgRegion.getName());
//          not disabling alerts anymore to optimize caching and just dumping as is on HTML for js handle show/hide
            if (StringUtils.equalsIgnoreCase(
                getCurrentPage().getAbsoluteParent(RegionPicker.CUR_REGION_ROOT_LEVEL).getName(),
                excludedPageNamePgRegion.getName())) {
                LOGGER.info("This alert is disabled for this page-->", excludedPageName);
                excludedList.add(excludedPageModel);
            }
          }
        } catch (Exception e) {
          throw new GenericRuntimeException("AlertsUse :: disableAlertsOnExcludedPages Excluded Page resource does not exist "+excludedPageName , e);
        }
      }
      model.setExcludedList(excludedList);
    }
    if (!model.getDisabled()) {
      if (GenericUtil.isAuthorMode()) {
        String previwInAuthorStr =
            contentValueMap.get(GlobalConstants.PREVIEWIN_AUTHOR_ENVIRONMENT, StringUtils.EMPTY);
        String replicationAction =
            contentValueMap.get(GlobalConstants.CQ_LAST_REPLICATION_ACTION, StringUtils.EMPTY);
        if (GlobalConstants.TRUE.equalsIgnoreCase(previwInAuthorStr)
            || GlobalConstants.ACTIVATE.equalsIgnoreCase(replicationAction)) {
          model.setPreviewinAuthor(Boolean.parseBoolean(previwInAuthorStr));
          if ((StringUtils.isNotEmpty(model.getTitle())
              && StringUtils.isNotEmpty(model.getLinkText())
              && StringUtils.isNotEmpty(model.getPath()))
              || (StringUtils.isNotEmpty(model.getMessageDetail()))) {
            // add the model only if title text and link are not empty
            results.add(model);
          }
        }
      } else {
        if ((StringUtils.isNotEmpty(model.getTitle()) && StringUtils.isNotEmpty(model.getLinkText())
            && StringUtils.isNotEmpty(model.getPath()))
            || (StringUtils.isNotEmpty(model.getMessageDetail()))) {
          // add the model only if title text and link are not empty
          results.add(model);
        }
      }
    }
  }

  /**
   * Provides list of alerts which needs to be depicted on Bulletin section on each page.
   *
   * @return alertsList
   */
  public List<AlertModel> getBulletinAlerts() {
    List<AlertModel> results1 = getAlerts(p1Path, false);
    List<AlertModel> results2 = getAlerts(p2Path, false);
    List<AlertModel> results3 = getAlerts(p3Path, false);
    results1.addAll(results2);
    results1.addAll(results3);
    return results1;
  }
}
