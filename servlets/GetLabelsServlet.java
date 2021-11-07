package org.kp.foundation.core.servlets;


import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.kp.foundation.core.exception.GenericRuntimeException;
import org.kp.foundation.core.utils.ValueMapTransformUtil;
import org.osgi.service.component.annotations.Component;

import com.day.cq.wcm.api.Page;
import com.google.gson.Gson;


/**
 * Servlet get Lables from either generic list template or lables component
 */

//Waiver details on GSC-3617
@SuppressWarnings({"squid:S3776"})
@Component(service = Servlet.class, immediate = true,
name = "KP Foundation Get Labels Servlet",
property = { 
    "process.label= KP Foundation Get Labels Servlet", 
	"sling.servlet.resourceTypes"+"=sling/servlet/default",
	"sling.servlet.methods"+"="+HttpConstants.METHOD_GET, 
	"sling.servlet.selectors"+"=labels",
	"sling.servlet.extensions=json" })
public class GetLabelsServlet extends SlingSafeMethodsServlet implements Serializable{

    private static final long serialVersionUID = 1L;
    private static final String IS_LABEL_PROPERTY = "isLabelResource";
    private static final String GENERIC_LIST_TEMPLATE = "kporg/kp-foundation/components/structure/genericListPage";
    private static final String PAR_RESOURCE_NODE_NAME = "list";
   
  
    /**
     * {@inheritDoc}
     */
    @Override
    protected void doGet(SlingHttpServletRequest request,
                         SlingHttpServletResponse response) throws ServletException,
            IOException {
    	Gson gson = new Gson();
    	List<ValueMap> lableValueMapList = new ArrayList<ValueMap>();
        ResourceResolver resourceResolver = null;
        String lableOutPut = "";
        try {
            resourceResolver = request.getResourceResolver();
           
            String dataType = getDataType(request);
            Page currentPage = resourceResolver.getResource(request.getRequestPathInfo().getResourcePath()).adaptTo(Page.class);

            if (dataType.equals("labels")) {
                if(currentPage.getContentResource().getResourceType().equals(GENERIC_LIST_TEMPLATE)){

                    Map<String, Object> lablesOutputMap = getExternalLables(currentPage.getContentResource());
                    if (lablesOutputMap != null) {
                        lableOutPut = gson.toJson(lablesOutputMap);
                    }
                }
                else {
                    getLableResource(currentPage.getContentResource(),lableValueMapList);
                    if (lableValueMapList.size()!=0) {
                        lableOutPut = gson.toJson(ValueMapTransformUtil.getPropertiesValueMap(lableValueMapList));
                    }
                }
            }
        } catch (Exception e) {
            throw new GenericRuntimeException("GetLabelsServlet :: Error Inside doGet :: ", e);
        }

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");
        response.getWriter().write(lableOutPut);
        response.getWriter().flush();
        response.getWriter().close();

    }

    private String getDataType(SlingHttpServletRequest request){
        String[] Selectors = request.getRequestPathInfo().getSelectors();
        return (Selectors != null && Selectors.length > 0) ? Selectors[0] : "";
    }


    /**
     * This method returns the ValueMap of the Lables Component
     * It also removes default key value pairs from the value map like jcr:created etc
     * @param resource
     * @throws Exception
     */


    private void getLableResource(Resource resource,List<ValueMap> lableValueMapList){


        try {
            Boolean isLabelResource = resource.adaptTo(ValueMap.class).get(IS_LABEL_PROPERTY,Boolean.FALSE);
            if(isLabelResource){
                lableValueMapList.add(resource.adaptTo(ValueMap.class));
            }

            Iterator<Resource> childResources = resource.listChildren();
            while(childResources.hasNext()){
                getLableResource(childResources.next(), lableValueMapList);
            }
        }
        catch (Exception e){
            throw new GenericRuntimeException("GetLabelsServlet :: Error Inside getLableResource :: ", e);
        }


    }

    /**
     * This method returns Map of all the Generic List components added to the Generic List template page
     * @param resource
     * @throws Exception
     */

    private Map<String, Object> getExternalLables(Resource resource){

        Map<String, Object> lablesMap = new LinkedHashMap<String, Object>();
        try {
            String resourceNode = resource.adaptTo(Node.class).getPrimaryNodeType().getName();
            ValueMap properies = resource.adaptTo(ValueMap.class);
            String type = properies.get("type", "");

            if (resourceNode.equals("cq:PageContent") || type.equals("nested")) {
                if(resource.hasChildren()){
                    Resource listResource = resource.getChild(PAR_RESOURCE_NODE_NAME);
                    if(listResource.hasChildren()){
                        Iterator<Resource> listChildren = listResource.listChildren();
                        while(listChildren.hasNext()){
                            Resource childResource = listChildren.next();
                            ValueMap childProperies = childResource.adaptTo(ValueMap.class);
                            String childType = childProperies.get("type", "");
                            String childKey = childProperies.get("key", "");
                            Object childValue = null;
                            if(childType.equals("") || childType.equals("text"))
                                childValue = childProperies.get("value","");
                            if(childType.equals("richtext"))
                                childValue = childProperies.get("richText","");
                            if(childType.equals("multifield"))
                                childValue = childProperies.get("multiField",String[].class);
                            if(childType.equals("nested"))
                                childValue = getExternalLables(childResource);
                            lablesMap.put(childKey,childValue);
                        }
                    }
                }
            }

        }
        catch(Exception e){
            throw new GenericRuntimeException("GetLabelsServlet :: Error Inside getExternalLables :: ", e);
        }
        return lablesMap;
    }

}
