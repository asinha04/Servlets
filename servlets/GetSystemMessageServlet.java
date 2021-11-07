package org.kp.foundation.core.servlets;


import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.kp.foundation.core.constants.GlobalConstants;
import org.kp.foundation.core.constants.TransformerConstants;
import org.kp.foundation.core.exception.GenericRuntimeException;
import org.kp.foundation.core.models.SystemMessageModel;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.Page;
import com.google.gson.Gson;


/**
 * Servlet to create a product pages using ProductPageCreatorService
 */
@Component(service = Servlet.class, immediate = true,
name = "KP Foundation Get System Message Servlet",
property = { 
    "process.label= KP Foundation Get System Message Servlet", 
	"sling.servlet.resourceTypes"+"=sling/servlet/default",
	"sling.servlet.methods"+"="+HttpConstants.METHOD_GET, 
	"sling.servlet.selectors"+"="+GlobalConstants.SRS_SERVLET_LIST_SELECTOR,
	"sling.servlet.selectors"+"="+GlobalConstants.SRS_SERVLET_DATA_SELECTOR,
	"sling.servlet.extensions=json" })
public class GetSystemMessageServlet extends SlingSafeMethodsServlet{

    private static final long serialVersionUID = 1L;
    private static final Logger log = LoggerFactory.getLogger(GetSystemMessageServlet.class);
    private static final String TEMPLATE = "kporg/kp-foundation/components/structure/systemResponseMessage";

    @Reference
    private transient ResourceResolverFactory resourceResolverFactory;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException,
    IOException {
    	 ResourceResolver resourceResolver = null;
        String outPut = "";
        try {
            Map<String,Object> map = new HashMap<String,Object>();
            map.put(ResourceResolverFactory.SUBSERVICE, GlobalConstants.KP_CONTENT_ADMIN_SERVICE);
            resourceResolver = resourceResolverFactory.getServiceResourceResolver(map);
            String dataType = getDataType(request);
            Resource resource = resourceResolver.getResource(request.getRequestPathInfo().getResourcePath());
            if (resource != null && StringUtils.isNotBlank(resource.getPath())) {
              if (resource.getPath().endsWith(JcrConstants.JCR_CONTENT)) {
                resource = resource.getParent();
              }
                Page currentPage = resource.adaptTo(Page.class);
                if(currentPage.getContentResource().getResourceType().equals(TEMPLATE)){
                    if (dataType.equals(GlobalConstants.SRS_SERVLET_DATA_SELECTOR)) {
                        outPut = getMessageData(currentPage);
                    }
                    if (dataType.equals(GlobalConstants.SRS_SERVLET_LIST_SELECTOR)) {
                        outPut = getListData(currentPage);
                    }
                }
            }
        } catch (Exception e) {
            throw new GenericRuntimeException("GetSystemMessageServlet :: doGet method {}.", e);
        } finally {
            if (resourceResolver != null) {
                resourceResolver.close();
            }
        }

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");
        response.getWriter().write(outPut);
        response.getWriter().flush();
        response.getWriter().close();

    }

    private String getDataType(SlingHttpServletRequest request){
        String[] Selectors = request.getRequestPathInfo().getSelectors();
        return (Selectors != null && Selectors.length > 0) ? Selectors[0] : "";
    }

    private String getMessageData(Page messagePage){
        String message = "";
        Gson gson = new Gson();
        try {
            SystemMessageModel messageModel = messagePage.adaptTo(SystemMessageModel.class);
            message = gson.toJson(messageModel);
            message = StringUtils.replace(message, TransformerConstants.HTML, "");
        }
        catch (Exception e){
            log.error(GlobalConstants.EXCEPTION , e);
        }
        return message;

    }

    private String getListData(Page messagePage){
        String message = "";
        Gson gson = new Gson();
        Map<String, SystemMessageModel> messageModels = new HashMap<String, SystemMessageModel>();
        try {
            Iterator<Page> pageList = messagePage.listChildren();
            while(pageList.hasNext()){
                Page childPage = pageList.next();
                SystemMessageModel messageModel = childPage.adaptTo(SystemMessageModel.class);
                message = gson.toJson(messageModel);
                messageModels.put(childPage.getName(),messageModel);

            }
            message = gson.toJson(messageModels);
            message = StringUtils.replace(message, TransformerConstants.HTML, "");
        }
        catch (Exception e){
            throw new GenericRuntimeException("GetSystemMessageServlet :: getListData method {}.", e);
        }
        return message;

    }


}
