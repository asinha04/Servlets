package org.kp.foundation.core.service;

import org.kp.foundation.core.exception.DynamicClientLibException;

/**
 * @author Rajesh Dwivedi on 10/16/17.
 */
public interface DynamicClientLibProcessorService {
    void execute(final String componentPath, String featureName) throws DynamicClientLibException;
}
