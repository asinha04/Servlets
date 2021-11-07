package org.kp.foundation.core.service.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.jcr.Session;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ValueMap;
import org.kp.foundation.core.constants.AlertsComponent;
import org.kp.foundation.core.enums.AlertsEnum;
import org.kp.foundation.core.models.AlertModel;
import org.kp.foundation.core.service.AlertsQueryService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.SearchResult;
import com.day.crx.JcrConstants;

/**
 * @author krishan This is the implementation class for AlertsQueryService, This
 *         class implements the alert query logic on AEM QueryBuilder
 */
@Component(service = AlertsQueryService.class,immediate = true)
public class AlertsQueryServiceImpl implements AlertsQueryService {
	private static final String ALERT_EXTERNAL_URL = "alertExternalUrl";

	private static final String PREVIEWIN_AUTHOR_ENVIRONMENT = "previewinAuthorEnvironment";

	private static final String ALERT_LINK_TEXT = "alertLinkText";

	private static final String ALERT_NOTIFICATION_TEXT = "alertNotificationText";

	// Inject a Sling ResourceResolverFactory
	@Reference
	private ResourceResolverFactory resolverFactory;

	@Reference
	private QueryBuilder builder;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.kp.foundation.core.service.AlertsQueryService#getAlerts(java.util.
	 * Map, javax.jcr.Session)
	 */
	@Override
	public List<AlertModel> getAlerts(Map<String, String> predicates, Session session) {
		List<AlertModel> results = new ArrayList<AlertModel>();
		Query query = builder.createQuery(PredicateGroup.create(predicates), session);

		SearchResult queryResult = query.getResult();
		Iterator<Resource> resItr = queryResult.getResources();

		while (resItr.hasNext()) {
			AlertModel model = new AlertModel();
			Resource res = resItr.next();
			ValueMap contentValueMap = res.adaptTo(ValueMap.class);
			String pagePath = res.getPath().replace("/" + JcrConstants.JCR_CONTENT, "");
			String externalURL = contentValueMap.get(ALERT_EXTERNAL_URL, StringUtils.EMPTY);
			if (StringUtils.isNotEmpty(externalURL)) {
				model.setPath(externalURL);
			} else {
				model.setPath(pagePath);
			}
			if (pagePath.contains(AlertsComponent.ALERTS_P2_PATH)) {
				model.setAlertType(AlertsEnum.P2);
			} else if (pagePath.contains(AlertsComponent.ALERTS_P1_PATH)) {
				model.setAlertType(AlertsEnum.P1);
			} else if (pagePath.contains(AlertsComponent.ALERTS_P3_PATH)) {
				model.setAlertType(AlertsEnum.P3);
			}
			model.setTitle(contentValueMap.get(ALERT_NOTIFICATION_TEXT, StringUtils.EMPTY));
			model.setLinkText(contentValueMap.get(ALERT_LINK_TEXT, StringUtils.EMPTY));

			if (contentValueMap.get(PREVIEWIN_AUTHOR_ENVIRONMENT) != null) {
				String previwInAuthorStr = contentValueMap.get(PREVIEWIN_AUTHOR_ENVIRONMENT, StringUtils.EMPTY);
				if (previwInAuthorStr != StringUtils.EMPTY) {
					model.setPreviewinAuthor(Boolean.parseBoolean(previwInAuthorStr));
				} else {
					model.setPreviewinAuthor(Boolean.FALSE);
				}
			}
			results.add(model);

		}
		return results;
	}

}
