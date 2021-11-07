package org.kp.foundation.core.servlets;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.kp.foundation.core.models.GenericModalModel;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.crx.JcrConstants;
import com.google.gson.Gson;


/**
 * This servlet helps in providing JSON for Generic Modal which could be used
 * in JS to prepare the template for Modal.
 * 
 * @author Tirumala Malladi
 *
 */
@Component(service = Servlet.class, immediate = true,
name = "KP Foundation Modal Servlet",
property = { 
	"sling.servlet.resourceTypes"+"=kporg/kp-foundation/components/content/genericModal",
	"sling.servlet.methods"+"="+HttpConstants.METHOD_GET, 
	"sling.servlet.selectors"+"=modal",
	"sling.servlet.extensions=json" })
public class ModalServlet extends SlingSafeMethodsServlet implements Serializable{
	private static final String APPLICATION_JSON = "application/json";
	private static final String MODAL_START ="beginning of modal dialog content";
	private static final String MODAL_END ="end of modal dialog content";
	private static final String SINGLE_BUTTON = "singleButton";
	private static final String DOUBLE_BUTTON = "doubleButton";
	private static final String HAS_TITLE ="hasTitle";
	
	
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LoggerFactory.getLogger(ModalServlet.class);

	@Override
	protected void doGet(SlingHttpServletRequest request, 
			SlingHttpServletResponse response) throws ServletException, IOException {
		LOGGER.debug("GenericaDataModalServlet - processRequest begin");
		boolean hasTitle=false;
		boolean singleButton = false;
		boolean doubleButton = false;
		try {
			
			String pagePath = request.getParameter("pagePath");
			String path = pagePath + "/"+  JcrConstants.JCR_CONTENT+ "/bodypar/genericmodal";
			Resource resource = request.getResourceResolver().getResource(path);
			if(resource == null) {
				throw new IOException("Resource is Empty in Modal Servlet");
			}
		
			GenericModalModel messageModel = resource.adaptTo(GenericModalModel.class);
			if(messageModel!= null) {
				if(messageModel.getTitle()!= null && messageModel.getTitle().length() > 0){
					hasTitle = true;
				}
				// Removing the Html tags from the Modal Description
				if(messageModel.getSystemMessage()!= null && messageModel.getSystemMessage().length()> 0) {
					messageModel.setBody(messageModel.getSystemMessage().replaceAll("\\<.*?>",""));
					messageModel.setSystemMessage(null);
				} else {
					messageModel.setBody(messageModel.getBody().replaceAll("\\<.*?>",""));
				}
				
				if(messageModel.getPrimaryButton()!= null && messageModel.getPrimaryButton().length() > 0 &&
						messageModel.getSecondaryButton()!=null && messageModel.getSecondaryButton().length() >0 ) {
					singleButton = false;
					doubleButton = true;
				} else {
					singleButton = true;
					doubleButton = false;
				}
				List<String> screenReaderOnly = new ArrayList<>();
				screenReaderOnly.add(MODAL_START);
				screenReaderOnly.add(MODAL_END);
	
				messageModel.setScreenReaderOnly(screenReaderOnly);
	
				Map<String,Boolean> config = new HashMap<>();
				config.put(SINGLE_BUTTON, singleButton);
				config.put(DOUBLE_BUTTON, doubleButton);
				config.put(HAS_TITLE, hasTitle);
	
				messageModel.setConfig(config);
				Gson gson = new Gson();
				String json = "{\"modal\":" + gson.toJson(messageModel) + "}";
			
				response.setStatus(HttpServletResponse.SC_OK);
				response.setContentType(APPLICATION_JSON);
				response.getWriter().write(json);
			}
		} catch (Exception e ) {
		  LOGGER.error("exception  " , e);
		}
	}
}