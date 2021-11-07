package org.kp.foundation.core.service.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.jcr.api.SlingRepository;
import org.kp.foundation.core.exception.DynamicClientLibException;
import org.kp.foundation.core.service.SystemUserResources;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Rajesh Dwivedi on 10/12/17.
 */
@Component(service = SystemUserResources.class, immediate = false,
property = { "service.vendor" +"=Kaiser Permanente"})
public class SystemUserResourcesImpl implements SystemUserResources {

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Reference
    private SlingRepository slingRepository;

    private static final int MAX_NUMBER_SESSION = 10;

    private final Map<String, Session> sessionMapper = new HashMap<>(MAX_NUMBER_SESSION);
    private final Map<String, ResourceResolver> resourceResolverMapper = new HashMap<>(MAX_NUMBER_SESSION);

    /**
     * Creates and returns the system user JCR session
     * @param userService - service name mapped to bundle with user
     * @return - JcrSession object of system user
     * @throws DynamicClientLibException - throws DynamicClientLibException
     */
    @Override
    public Session getSystemUserSession(final String userService) throws DynamicClientLibException {
        if(!sessionMapper.containsKey(userService)){
            if(isCapacityFull(sessionMapper.size())){
                throw new DynamicClientLibException("Service has reached it's maximum session capacity. So can not provide JCR Session for user : " + userService + " : Connect with AEM Governance team to increase it's size.");
            }
            try {
                sessionMapper.put(userService, slingRepository.loginService(userService, slingRepository.getDefaultWorkspace()));
            } catch (RepositoryException e) {
                throw new DynamicClientLibException("Could not get JCR Session for user : " + userService, e);
            }
        }
        return sessionMapper.get(userService);
    }

    /**
     * Logs out system user JCR session and removes it's entry from the pool.
     * @param userService - service name mapped to bundle with user
     */
    public void releaseSystemUserSession(final String userService){
        final Optional<Session> optionalUserSession = Optional.ofNullable(sessionMapper.get(userService));
        optionalUserSession.ifPresent(userSession -> {
            userSession.logout();
            sessionMapper.remove(userService);
        });
    }

    /**
     * @param userService - service name mapped to bundle with user
     * @return - system user resource resolver object
     * @throws DynamicClientLibException - throws DynamicClientLibException
     */
    @Override
    public ResourceResolver getSystemUserResourceResolver(final String userService) throws DynamicClientLibException {
        if(!resourceResolverMapper.containsKey(userService)){
            if(isCapacityFull(resourceResolverMapper.size())){
                throw new DynamicClientLibException("Service has reached it's maximum resourceResolver capacity. So can not provide JCR resourceResolver for user : " + userService + " : Connect with AEM Governance team to increase it's size.");
            }
            try {
            Map<String, Object> param = new HashMap<>();
            param.put(ResourceResolverFactory.SUBSERVICE, userService);
            resourceResolverMapper.put(userService, resourceResolverFactory.getServiceResourceResolver(param));
            }catch (LoginException e){
                throw new DynamicClientLibException("Could not get JCR resourceResolver for user : " + userService, e);
            }
        }
        return resourceResolverMapper.get(userService);
    }

    /**
     * Closes system user ResourceResolver and removes it's entry from the pool.
     * @param userService - service name mapped to bundle with user
     */
    public void releaseSystemUserResourceResolver(final String userService){
        final Optional<ResourceResolver> optionalUserSession = Optional.ofNullable(resourceResolverMapper.get(userService));
        optionalUserSession.ifPresent(userResourceResolver -> {
            userResourceResolver.close();
            resourceResolverMapper.remove(userService);
        });
    }

    private boolean isCapacityFull(int size) {
        return size >= MAX_NUMBER_SESSION;
    }
}
