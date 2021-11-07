package org.kp.foundation.core.servlets;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.api.wrappers.SlingHttpServletRequestWrapper;
import org.kp.foundation.core.exception.GenericRuntimeException;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(service = Servlet.class, 
name = "KP Foundation Mobile External POST Servlet",
property = { 
    "process.label= KP Foundation Mobile External POST Servlet", 
	"sling.servlet.resourceTypes"+"=sling/servlet/default",
	"sling.servlet.methods"+"="+HttpConstants.METHOD_POST, 
	"sling.servlet.selectors"+"=mobile",
	"sling.servlet.selectors"+"=external",
	"sling.servlet.extensions=html" })
public class ExternalPostServlet extends SlingAllMethodsServlet {
    public static final Logger log = LoggerFactory.getLogger(ExternalPostServlet.class);

    public ExternalPostServlet() {
        //Constructor
    }

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
        log.debug("Inside " , this.getClass().getName());
        try {
            request.setAttribute("cq.ext.app.method", request.getMethod());
            request.getRequestDispatcher(request.getResource().getPath()).include(new GetWrapper(request), response);
        } catch (Exception var4) {
          throw new GenericRuntimeException("ExternalPostServlet :: Error Inside External :: doPost", var4);
        }
    }

    public class GetWrapper extends SlingHttpServletRequestWrapper {

        public GetWrapper(SlingHttpServletRequest wrappedRequest) {
            super(wrappedRequest);
        }

        @Override
        public String getMethod() {
            return "GET";
        }
    }
}