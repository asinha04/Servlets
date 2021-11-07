package org.kp.foundation.core.service;


import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;

/**
 * @author Rajesh Dwivedi on 9/1/17.
 */
public interface GetJcrSession {
    Session getSystemUserJcrSession() throws RepositoryException;
    
    ResourceResolver getSystemUserResourceResolver(String serviceName) throws LoginException;
}
