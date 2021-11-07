package org.kp.foundation.core.service.impl;

import java.util.Optional;

import javax.jcr.Node;

import org.apache.sling.api.resource.ValueMap;
import org.kp.foundation.core.exception.DynamicClientLibException;
import org.kp.foundation.core.service.DynamicClientLibExecutorService;
import org.kp.foundation.core.service.DynamicClientLibProcessorService;
import org.kp.foundation.core.utils.JCRUtil;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation class for Handlebars dynamic clientlib processor
 * @author Rajesh Dwivedi on 10/16/17.
 */
@Component(service = DynamicClientLibProcessorService.class,
property = { "clientlib-type" +"=Handlebars",
		"service.vendor" +"=Kaiser Permanente"})
public class HandlebarDynamicClientLibProcessorService implements DynamicClientLibProcessorService {
    private static final Logger LOGGER = LoggerFactory.getLogger(HandlebarDynamicClientLibProcessorService.class);

    @Reference(target="(clientlib-type=Handlebars)")
    private DynamicClientLibExecutorService dynamicClientLibExecutorService;

    /**
     * Based on page path and component path from events, it executes sequence of steps to create clientlib if does not
     * already exists. Adds the JS file compiled with authored data from component to the clientlib for the page.
     * @param componentPagePath - Component path from event
     * @param featureName - Application feature-name on kp.org like 'profile-preferences'
     * @throws DynamicClientLibException - throws custom exception in case of issues
     */
    @Override
    public void execute(final String componentPagePath, String featureName) throws DynamicClientLibException {

        Optional<String> jsCompiledWithAuthoredData;

        String pagePath = JCRUtil.getPagePathFromComponentPath(componentPagePath);

        LOGGER.debug("Started processing clientlib node from page path {}", pagePath);
        final Optional<Node> clientLibNode = dynamicClientLibExecutorService.processPageDataForClientLibNode(pagePath, featureName);
        LOGGER.debug("Finished processing clientlib node from page path {}", pagePath);
        if(!clientLibNode.isPresent()){
            throw new DynamicClientLibException("Clientlib node is null. Could not generate clientlib node from page");
        }

        LOGGER.debug("Started processing getJsFrameworkTemplateAsString from component page path {}", componentPagePath);
        Optional<String> jsFrameworkTemplateAsString = dynamicClientLibExecutorService.getJsFrameworkTemplateAsString(componentPagePath);
        LOGGER.debug("Finished processing getJsFrameworkTemplateAsString from component page path {}", componentPagePath);


        LOGGER.debug("Started processing getComponentData from component page path {}", componentPagePath);
        final Optional<ValueMap> componentData = dynamicClientLibExecutorService.getComponentData(componentPagePath);
        LOGGER.debug("Finished processing getComponentData from component page path {}", componentPagePath);


        LOGGER.debug("Started fetching getComponentUUID from component page path {}", componentPagePath);
        final String componentUUID = dynamicClientLibExecutorService.getComponentUUID(componentPagePath);
        LOGGER.debug("Finished fetching getComponentUUID from component page path {}", componentPagePath);


        if(jsFrameworkTemplateAsString.isPresent() && componentData.isPresent()){
            jsCompiledWithAuthoredData = dynamicClientLibExecutorService.getJSCompiledWithAuthoredData(jsFrameworkTemplateAsString.get(),
                    componentData.get(), componentUUID);
            LOGGER.debug("Finished compiling handlebars template with authored data. {}", jsCompiledWithAuthoredData);
        } else{
            throw new DynamicClientLibException("Either Handlebar template data or component properties data is missing.");
        }

        jsCompiledWithAuthoredData.ifPresent(s -> {
            LOGGER.debug("Started writing clientlib JS files for component {}", componentPagePath);
            dynamicClientLibExecutorService.writeClientLibJsFile(clientLibNode.get(), s, componentUUID);
            LOGGER.debug("Finished writing clientlib JS files for component {}", componentPagePath);
        });
    }
}
