package org.kp.foundation.core.service.impl;

import static org.apache.jackrabbit.JcrConstants.JCR_CONTENT;
import static org.apache.jackrabbit.JcrConstants.JCR_DATA;
import static org.kp.foundation.core.constants.HandlebarConstants.KP_COMPILER_SERVICE;
import static org.kp.foundation.core.utils.ClientLibraryUtil.HANDLEBARS_CLIENT_LIB_KP_FOUNDATION;
import static org.kp.foundation.core.utils.ClientLibraryUtil.JAVASCRIPT_SUFFIX;
import static org.kp.foundation.core.utils.ClientLibraryUtil.getPageDynamicClientlibLocation;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.jackrabbit.JcrConstants;
import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.jackrabbit.core.fs.FileSystem;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.kp.foundation.core.exception.DynamicClientLibException;
import org.kp.foundation.core.service.DynamicClientLibExecutorService;
import org.kp.foundation.core.service.SystemUserResources;
import org.kp.foundation.core.utils.ClientLibraryUtil;
import org.kp.foundation.core.utils.HandlebarsUtil;
import org.kp.foundation.core.utils.JCRUtil;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.HandlebarsException;
import com.github.jknack.handlebars.Template;

import freemarker.template.TemplateException;

/**
 * @author Rajesh Dwivedi on 10/16/17.
 */

@Component(service = DynamicClientLibExecutorService.class,
property = { "clientlib-type" +"=Handlebars",
		"service.vendor" +"=Kaiser Permanente"})
public class HandlebarDynamicClientLibExecutorService implements DynamicClientLibExecutorService {

    private static final Logger LOGGER = LoggerFactory.getLogger(HandlebarDynamicClientLibExecutorService.class);

    @Reference
    private SystemUserResources systemUserResources;

    private Session jcrSession; //NOSONAR

    /**
     * Creates and returns an optional clientlib node if does not exist already.
     * @param pagePath - Page path for which clientlib need to be generated
     * @return - Optional clientlib node
     * @throws DynamicClientLibException - throws DynamicClientLibException
     */
    @Override
    public Optional<Node> processPageDataForClientLibNode(String pagePath, String featureName) throws DynamicClientLibException {
        LOGGER.debug("Inside processPageDataForClientLibNode {}");
        try {
            populateJcrSessionIfMissing();
            final String pageJcrContentPath = pagePath + FileSystem.SEPARATOR + JcrConstants.JCR_CONTENT;
            final Node pageNode = jcrSession.getNode(pageJcrContentPath);
            Objects.requireNonNull(pageNode, "Could not fetch the page node from HandlebarDynamicClientLibExecutorService.");
            final String pageUUID = JCRUtil.getUUID(pageNode);
            Objects.requireNonNull(pageUUID,
                    "Page UUID can not be null for clientlib creation :: HandlebarDynamicClientLibExecutorService.processPageDataForClientLibNode()");

            final String hbarClientlibPath = getPageDynamicClientlibLocation(featureName, pageUUID);
            LOGGER.debug("hbarClientlibPath : {} for node {}" , hbarClientlibPath, pageNode);
            final Node clientlibNode = JcrUtils.getNodeIfExists(hbarClientlibPath, jcrSession);
            if(clientlibNode == null){
                return Optional.of(ClientLibraryUtil.createClientLibrary(
                        hbarClientlibPath,
                        new String[]{HandlebarsUtil.getPageDynamicHbarClientlibName(pageUUID, featureName)},
                        null,
                        new String[]{HANDLEBARS_CLIENT_LIB_KP_FOUNDATION},
                        systemUserResources.getSystemUserResourceResolver(KP_COMPILER_SERVICE)));
            } else {
                return Optional.of(clientlibNode);
            }
        } catch (RepositoryException e) {
            LOGGER.error("LoginException or RepositoryException :: ", e);
            throw new DynamicClientLibException("Exception in fetching JCR session from SystemUserResources", e);
        }
    }

    /**
     * Reads the properties of component and return the ValueMap object wrapped in Optional
     * @param componentPagePath - Component path when dropped on page
     * @return - Returns Optional ValueMap with component property daya
     * @throws DynamicClientLibException - throws DynamicClientLibException
     */
    @Override
    public Optional<ValueMap> getComponentData(String componentPagePath) throws DynamicClientLibException {
        Resource componentResource;
        try {
            componentResource = systemUserResources.getSystemUserResourceResolver(KP_COMPILER_SERVICE)
                                                   .getResource(componentPagePath);
            if(componentResource == null){
                throw new DynamicClientLibException("Component node is null for " + componentPagePath );
            }
            return Optional.of(componentResource.getValueMap());
        } catch (DynamicClientLibException e) {
            LOGGER.error("LoginException in get component data :: ", e);
            throw new DynamicClientLibException("LoginException in get component data :: ", e);
        }
    }

    /**
     * Based on component sling:resourceType property, finds the location of handlebar template in component directory
     * under /apps and returns it's data as String.
     * @param componentPagePath - Component path when dropped on page
     * @return - Returns handlebars template for the component as String.
     * @throws DynamicClientLibException - throws DynamicClientLibException
     */
    @Override
    public Optional<String> getJsFrameworkTemplateAsString(String componentPagePath) throws DynamicClientLibException {
        populateJcrSessionIfMissing();
        try {
            final String componentAppPath = JCRUtil.getComponentAppPathFromComponentPagePath(jcrSession, componentPagePath);
            LOGGER.debug("Component app path {}", componentAppPath);

            //Handlebar template location
            final String hbsSrcPath = HandlebarsUtil.getHbsSrcPath(componentAppPath);
            final Node hbsSrcNode = jcrSession.getNode(hbsSrcPath);
            final Iterable<Node> childNodes = JcrUtils.getChildNodes(hbsSrcNode);
            final Iterator<Node> hbarTemplateItr = childNodes.iterator();
            final Optional<String> handlebarTemplatePath = getFirstHandlebarTemplate(hbarTemplateItr);
            if(!handlebarTemplatePath.isPresent()){
                throw new DynamicClientLibException("Handlebars template Path is null for " + componentPagePath );
            }
            final Node hbarNode = jcrSession.getNode(handlebarTemplatePath.get());
            Node jcrContent = hbarNode.getNode(JCR_CONTENT);
            InputStream content = jcrContent.getProperty(JCR_DATA).getBinary().getStream();
            return Optional.of(HandlebarsUtil.getHandlebarTemplateData(content));
        } catch (RepositoryException | IOException e){
            LOGGER.error("RepositoryException | IOException in getJsFrameworkTemplateAsString :: ", e);
            throw new DynamicClientLibException("RepositoryException | IOException in getJsFrameworkTemplateAsString :: ", e);
        }
    }

    static Optional<String> getFirstHandlebarTemplate(Iterator<Node> hbarTemplateItr) {
        return Stream.generate(hbarTemplateItr::next)
                     .map(JCRUtil.getNodePathAsString)
                     .findFirst();
    }

    /**
     * Creates and returns the component UUID (jcr:uuid) property if not already exists.
     * @param componentPagePath - Component path when dropped on page
     * @return - Component UUID
     * @throws DynamicClientLibException - throws DynamicClientLibException
     */
    @Override
    public String getComponentUUID(final String componentPagePath) throws DynamicClientLibException{
        populateJcrSessionIfMissing();
        try{
            final Node componentNode = jcrSession.getNode(componentPagePath);
            return JCRUtil.getUUID(componentNode);
        }catch (RepositoryException e){
            LOGGER.error("RepositoryException in getComponentUUID :: ", e);
            throw new DynamicClientLibException("RepositoryException in getComponentUUID ::", e);
        }
    }

    /**
     * Transpiles the handlebar template with user authored data and creates an immediately invoked function expression.
     * Wraps the iife string in Optional and returns it.
     * @param jsTemplate - Handlebars template as string
     * @param componentData - component data as ValueMap
     * @param componentUUID - component UUID (jcr:uuid)
     * @return Optional String with compiled handlebars template as JS with authored data
     * @throws DynamicClientLibException - throws DynamicClientLibException
     */
    @Override
    public Optional<String> getJSCompiledWithAuthoredData(final String jsTemplate, final ValueMap componentData,
                                                          final String componentUUID) throws DynamicClientLibException {
        try {
            String hbarWithAuthoredData = HandlebarsUtil.transpileHbarWithAuthoredData(jsTemplate, componentData, componentUUID);
            LOGGER.debug("hbarWithAuthoredData :: {}" , hbarWithAuthoredData);
            Handlebars handlebars = new Handlebars();
            handlebars.registerHelperMissing((context, options) -> options.fn.text());
            Template template = handlebars.compileInline(hbarWithAuthoredData);
            String toJavaScriptStr = HandlebarsUtil.getJSFromHbarTemplate(template);
            LOGGER.debug("hbarWithAuthoredData :: {}" , toJavaScriptStr);
            return Optional.of(HandlebarsUtil.buildCompiledHbarIife(componentUUID, toJavaScriptStr));
        }catch (TemplateException | HandlebarsException | IOException e){
            LOGGER.error("Freemarker Template  Exception or IOException in getJSCompiledWithAuthoredData.");
            throw new DynamicClientLibException("Freemarker Template  Exception or IOException in getJSCompiledWithAuthoredData.", e);
        }
    }

    /**
     * Add javascript to the clientlib node.
     * @param clientLibNode - clientlib node generated for the page where component is dropped.
     * @param jsData - iife as string with authored data
     * @param componentUUID - component uuid (jcr:uuid) of the component dropped on the page
     */
    @Override
    public void writeClientLibJsFile(Node clientLibNode, String jsData, String componentUUID) {
        ClientLibraryUtil.addJavaScriptToClientLibrary(
                clientLibNode,
                componentUUID + JAVASCRIPT_SUFFIX,
                jsData,
                true,
                true
        );
    }

    void populateJcrSessionIfMissing() throws DynamicClientLibException {
        if(jcrSession == null){
            try {
                jcrSession = systemUserResources.getSystemUserSession(KP_COMPILER_SERVICE);
            } catch (DynamicClientLibException e) {
                LOGGER.error("LoginException or RepositoryException :: ", e);
                throw new DynamicClientLibException("Exception in processing processPageDataForClientLibNode in HandlebarDynamicClientLibExecutorService", e);
            }
        }
    }
}