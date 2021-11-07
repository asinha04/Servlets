/*
 *  Copyright 2015 Adobe Systems Incorporated
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.kp.foundation.core.listeners;

import java.util.HashMap;
import java.util.Map;

import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ValueMap;
import org.kp.foundation.core.constants.AlertsComponent;
import org.kp.foundation.core.constants.GlobalConstants;
import org.kp.foundation.core.exception.GenericRuntimeException;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.replication.ReplicationAction;
import com.day.cq.replication.ReplicationActionType;
import com.day.crx.JcrConstants;

/**
 * This service takes care of resetting PREVIEWIN_AUTHOR_ENVIRONMENT field after
 * a alerts page is deactivated it ensures
 * 
 * @author Krishan Rathi
 *
 */

@Component(service = EventHandler.class, immediate = true, 
	property = { Constants.SERVICE_DESCRIPTION + "=Handles alerts replication events.",
			EventConstants.EVENT_TOPIC+"=ReplicationAction.EVENT_TOPIC"})
public class AlertsReplicationListener implements EventHandler {

	@Reference
	private ResourceResolverFactory resolverFactory;
	private static final Logger logger = LoggerFactory.getLogger(AlertsReplicationListener.class);
	

	public void handleEvent(final Event event) {
		logger.debug("Entering AlertsReplicationListener handleEvent method");
		ResourceResolver resolver = null;
		try {
			ReplicationAction action = ReplicationAction.fromEvent(event);

			if (action != null) {
				if (ReplicationActionType.DEACTIVATE == action.getType()) {
					String path = action.getPath();
					if (path.contains(AlertsComponent.ALERTS_P1_PATH) || path.contains(AlertsComponent.ALERTS_P2_PATH)
							|| path.contains(AlertsComponent.ALERTS_P3_PATH)) {
						Map<String, Object> param = new HashMap<String, Object>();
						param.put(ResourceResolverFactory.SUBSERVICE, "datawriter");
						resolver = resolverFactory.getServiceResourceResolver(param);
						Resource res = resolver.getResource(path +'/' + JcrConstants.JCR_CONTENT);
						ValueMap readMap = res.getValueMap();
						logger.debug("JCR Primary Type {} for the event action is {} ", readMap.get(JcrConstants.JCR_PRIMARYTYPE, action.getType()));
						ModifiableValueMap modMap = res.adaptTo(ModifiableValueMap.class);
						if (modMap != null) {
							modMap.remove(GlobalConstants.PREVIEWIN_AUTHOR_ENVIRONMENT);
							resolver.commit();
							logger.debug("Successfully saved");

						}
					}

				}
			}
			logger.debug("exiting  AlertsReplicationListener handleEvent method");

		} catch (Exception e) {
		  throw new GenericRuntimeException("AlertsReplicationListener :: handleEvent method {}.", e);
		} finally {
			if (resolver != null && resolver.isLive()) {
				resolver.close();
			}
		}
	}
}