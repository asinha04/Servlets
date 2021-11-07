package org.kp.foundation.core.listeners;

import static org.kp.foundation.core.constants.HandlebarConstants.KP_COMPILER_SERVICE;
import static org.kp.foundation.core.constants.HandlebarConstants.ROOT_PAGE_PATH_FOR_PNP_HBAR;
import static org.kp.foundation.core.utils.JCRUtil.isValidComponentToCreateHandlebarsDynamicClientLib;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.observation.Event;
import javax.jcr.observation.EventIterator;
import javax.jcr.observation.EventListener;
import javax.jcr.observation.ObservationManager;

import org.kp.foundation.core.exception.DynamicClientLibException;
import org.kp.foundation.core.exception.DynamicClientLibRuntimeException;
import org.kp.foundation.core.service.DynamicClientLibProcessorService;
import org.kp.foundation.core.service.SystemUserResources;
import org.kp.foundation.core.utils.ClientLibraryUtil;
import org.kp.foundation.core.utils.JCRUtil;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.NameConstants;


/**
 * @author Rajesh Dwivedi on 10/16/17.
 */
@Component(immediate = true, name = "KP Listener to Create Update Dynamic Clientlibs")
public class DynamicClientLibsListener implements EventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(DynamicClientLibsListener.class);
    
    private static final String NT_UNSTRUCTURED = "nt:unstructured";

    private Session session; //NOSONAR

    @Reference
    private SystemUserResources systemUserResources;

    @Reference(target="(clientlib-type=Handlebars)")
    private DynamicClientLibProcessorService dynamicClientLibProcessorService;

    @Override
    public void onEvent(EventIterator eventsIterator) {
        LOGGER.debug("Inside onEvent of DynamicClientLibsListener {}");

        try {
            while (eventsIterator.hasNext()){
                Event event = eventsIterator.nextEvent();
                final String eventPath = event.getPath();
                LOGGER.debug("DynamicClientLibsListener : Event Path from onEvent : {}" , eventPath);

                //If the eventPath ends with "/jcr:lastModified" then it is a component dropped on the page
                if(eventPath.endsWith(NameConstants.PN_LAST_MOD)){
                    String componentPath = JCRUtil.getPreviousNodePath(eventPath);
                    LOGGER.debug("Component Path = {}", componentPath);
                    if(isValidComponentToCreateHandlebarsDynamicClientLib(session, componentPath)){
                        final String featureName = ClientLibraryUtil.getFeatureNameFromEventPath(session, componentPath);
                        dynamicClientLibProcessorService.execute(componentPath, featureName);
                    }
                }
            }

        } catch (RepositoryException | DynamicClientLibException e) {
            throw new DynamicClientLibRuntimeException("Repository or  DynamicClientLibException: ", e);
        }
    }

    @Activate
    protected void activate(ComponentContext ctx) {
        LOGGER.debug("Inside DynamicClientLibsListener activate :: {}");
        try {
            session = systemUserResources.getSystemUserSession(KP_COMPILER_SERVICE);
            final String[] types = {NT_UNSTRUCTURED};
            final ObservationManager observationManager = session.getWorkspace().getObservationManager();
            observationManager.addEventListener(this, Event.PROPERTY_CHANGED , ROOT_PAGE_PATH_FOR_PNP_HBAR,
                    true, null, types, false);

            LOGGER.debug("Event Listener is setup on path {}", ROOT_PAGE_PATH_FOR_PNP_HBAR);
        } catch (DynamicClientLibException | RepositoryException e) {
            throw new DynamicClientLibRuntimeException("DynamicClientLibsListener failed to activate : ",e);
        }
    }

    @Deactivate
    protected void deactivate(ComponentContext cxt) throws RepositoryException {
        LOGGER.debug("IN DynamicClientLibsListener deactivate method! {}");

        ObservationManager observationManager = session.getWorkspace().getObservationManager();
        if (observationManager != null){
            observationManager.removeEventListener(this);
        }
        
		LOGGER.debug("DynamicClientLibsListener deactivate method ends! {}");
    }
}