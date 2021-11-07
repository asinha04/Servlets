package org.kp.foundation.core.service;

import org.apache.sling.api.resource.ResourceResolver;
import org.kp.foundation.core.exception.DynamicClientLibException;

import javax.jcr.Session;

/**
 * @author Rajesh Dwivedi on 10/12/17.
 */
public interface SystemUserResources {
    Session getSystemUserSession(final String userService) throws DynamicClientLibException;
    ResourceResolver getSystemUserResourceResolver(final String userService) throws DynamicClientLibException;
    void releaseSystemUserSession(final String userService);
    void releaseSystemUserResourceResolver(final String userService);
}
