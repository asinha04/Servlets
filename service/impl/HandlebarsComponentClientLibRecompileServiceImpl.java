package org.kp.foundation.core.service.impl;

import com.day.cq.replication.ReplicationActionType;
import com.day.cq.replication.ReplicationException;
import com.day.cq.replication.Replicator;
//Replication APIs
import com.day.cq.wcm.api.PageManager;
import org.kp.foundation.core.exception.DynamicClientLibException;
import org.kp.foundation.core.exception.DynamicClientLibRuntimeException;
import org.kp.foundation.core.service.DynamicClientLibExecutorService;
import org.kp.foundation.core.service.DynamicClientLibProcessorService;
import org.kp.foundation.core.service.HandlebarsComponentClientLibRecompileService;
import org.kp.foundation.core.service.SystemUserResources;
import org.kp.foundation.core.utils.ClientLibraryUtil;
import org.kp.foundation.core.utils.JCRUtil;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.sling.api.resource.Resource;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.ResourceResolver;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.*;

import static javax.jcr.query.Query.JCR_SQL2;
import static org.kp.foundation.core.constants.HandlebarConstants.KP_COMPILER_SERVICE;
import static org.kp.foundation.core.utils.ClientLibraryUtil.JS_TXT;
import static org.kp.foundation.core.utils.JCRUtil.isValidComponentToCreateHandlebarsDynamicClientLib;

@Component(service = HandlebarsComponentClientLibRecompileService.class,
        property = { "compiler-type" +"=Handlebars",
                "service.vendor" +"=Kaiser Permanente"})
public class HandlebarsComponentClientLibRecompileServiceImpl implements HandlebarsComponentClientLibRecompileService {
    private static final Logger LOGGER = LoggerFactory.getLogger(HandlebarsComponentClientLibRecompileService.class);

    @Reference
    private SystemUserResources systemUserResources;

    @Reference
    private Replicator replicator;

    @Reference(target="(clientlib-type=Handlebars)")
    private DynamicClientLibProcessorService dynamicClientLibProcessorService;

    @Reference(target="(clientlib-type=Handlebars)")
    private DynamicClientLibExecutorService dynamicClientLibExecutorService;

    /**
     * Given a contentPath and a component path {/apps} recompile handlebars component
     * Optional: Replicate content to publisher instances
     * @param componentPath
     * @param contentPath
     * @param isReplicate
     * @throws DynamicClientLibException
     */
    public ArrayList<String> recompileHandlebarComponents(String componentPath, String contentPath, boolean isReplicate, Session currentUserSession)  throws DynamicClientLibException {
        final String PAGE_CONTENT_SQL2  = "SELECT * FROM [nt:unstructured] AS node\n" +
                "WHERE ISDESCENDANTNODE(node, \""+contentPath+"\")\n" +
                "AND [sling:resourceType] = \""+componentPath+"\" option(traversal ok)";

        final ResourceResolver resourceResolver = systemUserResources.getSystemUserResourceResolver(KP_COMPILER_SERVICE);
        final Session session = systemUserResources.getSystemUserSession(KP_COMPILER_SERVICE);

        ArrayList<String> modifiedPages = new ArrayList<>();
        ArrayList<String> modifiedClientLibs = new ArrayList<>();

        if (resourceResolver != null && session != null) {
            final PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
            if (pageManager != null) {
                Iterator<Resource> resources = resourceResolver.findResources(PAGE_CONTENT_SQL2, JCR_SQL2);
                Iterable<Resource> iterable = () -> resources;
                iterable.forEach(currentResource -> {
                    try {
                        String currentComponentPath = currentResource.getPath();

                        // only select the node with the "sling:resourceType" for more robust search
                        if (StringUtils.isNotBlank(currentResource.getValueMap().get("sling:resourceType", ""))) {

                            // execute handlebars compilation
                            validateAndExecuteDynamicClientLibsProcessor(session, currentResource);

                            String pageUUID = JCRUtil.getUUID(Objects.requireNonNull(currentResource.adaptTo(Node.class)));
                            String pagePath = pageManager.getContainingPage(currentResource).getPath();

                            modifiedPages.add(pagePath);
                            Optional<Node> clientLibNode = dynamicClientLibExecutorService.processPageDataForClientLibNode(pagePath,ClientLibraryUtil.getFeatureNameFromEventPath(session, currentComponentPath));
                            // checking to satisfy non-null; hence, will always be true at this point
                            if (clientLibNode.isPresent()){
                                // add clientlib main folder
                                String clientLibNodeParentPath = clientLibNode.get().getPath();
                                modifiedClientLibs.add(clientLibNodeParentPath);
                                // jsfile path
                                final String hbarClientlibGeneratePath = clientLibNodeParentPath + "/"+ ClientLibraryUtil.JS_FOLDER+"/"+ pageUUID + ClientLibraryUtil.JAVASCRIPT_SUFFIX;
                                // js.txt path
                                final String jsTxtPath = clientLibNodeParentPath + "/" + JS_TXT;
                                modifiedClientLibs.add(jsTxtPath);
                                modifiedClientLibs.add(hbarClientlibGeneratePath);
                            }
                        }
                    } catch (RepositoryException | DynamicClientLibException e) {
                        LOGGER.error("Unable to execute handlebars compilation " + e);
                    }
                });
            }
            //replicate modified pages
            if (isReplicate) {
                replicateListOfContent(modifiedClientLibs, currentUserSession);
            }
        }
        return modifiedClientLibs;
    }

    /**
     * Validates and executes clientLib processor
     * @param session
     * @param componentResource
     */
    private void validateAndExecuteDynamicClientLibsProcessor(Session session, Resource componentResource) {
        String currentComponentPath = componentResource.getPath();
        try {
            if (isValidComponentToCreateHandlebarsDynamicClientLib(session, currentComponentPath)) {
                dynamicClientLibProcessorService.execute(
                        currentComponentPath,
                        ClientLibraryUtil.getFeatureNameFromEventPath(session, currentComponentPath)
                );
            }
        } catch (RepositoryException | DynamicClientLibException e) {
            throw new DynamicClientLibRuntimeException("Repository or DynamicClientLibException: ", e);
        }
    }

    /**
     * Replicates content to publish instance
     * @param session
     * @param path
     */
    private void replicateContent(Session session, String path) {
        try {
            replicator.replicate(session, ReplicationActionType.ACTIVATE, path);
            LOGGER.info("Page activated: "+ path);
        } catch (ReplicationException e) {
            LOGGER.error("Replication failed "+e.getMessage(), path);
        }
    }

    /**
     * Replicates list of content
     * @param contentList
     * @param session
     */
    private void replicateListOfContent(List<String> contentList,Session session) {
        contentList.stream()
                .distinct()
                .forEach(path -> replicateContent(session, path));
    }
}
