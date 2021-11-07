package org.kp.foundation.core.service;

import org.apache.sling.api.resource.ValueMap;
import org.kp.foundation.core.exception.DynamicClientLibException;

import javax.jcr.Node;
import java.util.Optional;

/**
 * @author Rajesh Dwivedi on 10/16/17.
 */
public interface DynamicClientLibExecutorService {
    Optional<Node> processPageDataForClientLibNode(final String pagePath, final String featureName) throws DynamicClientLibException;
    Optional<ValueMap> getComponentData(String componentPagePath) throws DynamicClientLibException;
    Optional<String> getJsFrameworkTemplateAsString(final String componentPagePath) throws DynamicClientLibException;
    String getComponentUUID(final String componentPagePath) throws DynamicClientLibException;
    Optional<String> getJSCompiledWithAuthoredData(final String jsTemplate, final ValueMap componentData, final String componentPagePath) throws DynamicClientLibException;
    void writeClientLibJsFile(final Node clientLibNode, final String jsData, final String componentUUID);
}
