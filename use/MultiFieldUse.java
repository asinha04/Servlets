// ---------------------------------------------------------------------------
/*
 CreatedOn       : July 12, 2016 
 CreatedBy       : Irene Appraem
 ProjectName     : kp-foundation.core
 CompilationUnit : 1.6

 */
// ---------------------------------------------------------------------------
package org.kp.foundation.core.use;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.apache.sling.api.resource.ResourceResolver;
import org.kp.foundation.core.exception.GenericRuntimeException;
import org.kp.foundation.core.utils.MultiFieldUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 
 * The MultiFieldUse class provides the logic to return the data saved from
 * multifield widget as list of JSON objects
 * 
 * @author irene.appraem
 *
 */
public class MultiFieldUse extends BaseWCMUse {

    private static final Logger LOGGER = LoggerFactory.getLogger(MultiFieldUse.class);
    private static final String PROPERTY_NAME = "propertyName";
    Node currentNode;
    String propertyName;
    ResourceResolver resourceResolver;

    public MultiFieldUse() {
        super();
    }

     @Override
    public void activate() throws Exception {
        LOGGER.debug("--- Started : MultiFieldModel : Activate");
        currentNode = getResource().adaptTo(Node.class);
        resourceResolver = getResourceResolver();
        propertyName = get(PROPERTY_NAME, String.class);
        LOGGER.debug("property name is ---->" , propertyName);
    }

    // ---------------------------------------------------------------------------
    /**
     * Gets the multifield values.
     * 
     * @return the multifield values
     */
    // ---------------------------------------------------------------------------
    public List<HashMap<String, Object>> getMultiFieldValues() {
      LOGGER.debug("inside getMultiFieldValues method");
      List<HashMap<String, Object>> multiFieldValues = new ArrayList<HashMap<String, Object>>();
      try {
        multiFieldValues = MultiFieldUtil.getMultiFieldValues(currentNode, propertyName);
      } catch (RepositoryException e) {
        throw new GenericRuntimeException("MultiFieldUse :: getMultiFieldValues :: Error while generating multifield values: {}", e);
      }
      return multiFieldValues;
    }
}