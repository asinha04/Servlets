package org.kp.foundation.core.service.impl;

import static org.kp.foundation.core.constants.GlobalConstants.SUB_SERVICE_WRITE_DEFAULT_PAGE_PROP;

import java.util.HashMap;
import java.util.Map;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.jcr.api.SlingRepository;
import org.kp.foundation.core.service.GetJcrSession;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Rajesh Dwivedi on 9/1/17.
 */

@Component(service = GetJcrSession.class, immediate = true)
public class GetJcrSessionImpl implements GetJcrSession{

    private Logger log = LoggerFactory.getLogger(GetJcrSessionImpl.class);

    @Reference
    private SlingRepository repository;
    
    @Reference
    private ResourceResolverFactory resolverFactory;

    @Override
    public Session getSystemUserJcrSession() throws RepositoryException {
        log.debug("Inside getSystemUserJcrSession :: {}");
        return repository.loginService(SUB_SERVICE_WRITE_DEFAULT_PAGE_PROP, repository.getDefaultWorkspace());
    }

	@Override
	public ResourceResolver getSystemUserResourceResolver(String serviceName) throws  LoginException {
		Map<String,Object> map = new HashMap<String,Object>();
        map.put(ResourceResolverFactory.SUBSERVICE, serviceName);
        return resolverFactory.getServiceResourceResolver(map);
	}
}
