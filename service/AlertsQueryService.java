package org.kp.foundation.core.service;

import java.util.List;
import java.util.Map;

import javax.jcr.Session;

import org.kp.foundation.core.models.AlertModel;
/**
 * This class defines the interface for alerts query logic implementation.
 * @author Krishan Rathi
 *
 */
public interface AlertsQueryService {

	/**
	 * This method returns the set of alerts for the incoming predicate query map, the query is excecuted on AEM QueryBuilder.
	 * @param predicates
	 * @param session
	 * @return
	 */
	List<AlertModel> getAlerts(Map<String, String> predicates,Session session);
}
