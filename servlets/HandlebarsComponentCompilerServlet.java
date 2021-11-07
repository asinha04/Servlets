package org.kp.foundation.core.servlets;

import com.google.gson.JsonObject;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.kp.foundation.core.exception.DynamicClientLibException;
import org.kp.foundation.core.service.HandlebarsComponentClientLibRecompileService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Session;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.ArrayList;

import static org.apache.sling.api.servlets.ServletResolverConstants.*;

@Component(service = {Servlet.class},
        property = {SLING_SERVLET_PATHS + "=/bin/kporg/tools/handlebars/compile",
                SLING_SERVLET_METHODS + "=GET", SLING_SERVLET_EXTENSIONS + "=json",})
public class HandlebarsComponentCompilerServlet extends SlingAllMethodsServlet {

    public static final Logger log = LoggerFactory.getLogger(HandlebarsComponentCompilerServlet.class);

    @Reference
    private transient HandlebarsComponentClientLibRecompileService handlebarsComponentClientLibRecompileService;

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        JsonObject jsonObject = new JsonObject();

        final String contentPath = request.getParameter("contentPath");
        final String componentAbsolutePath = request.getParameter("componentPath");
        final boolean isReplicate = Boolean.parseBoolean(request.getParameter("replicate"));

        try {
            final Resource resource = request.getResourceResolver().getResource(componentAbsolutePath);
            final Session session = request.getResourceResolver().adaptTo(Session.class);

            if (resource != null) {
                final Resource handlebars = resource.getChild("hbs-src");
                if (handlebars != null) {
                    // remove /apps from the component path
                    final String componentPath = StringUtils.replace(componentAbsolutePath, "/apps/", "");
                    jsonObject.addProperty("recompiledList",
                            buildHtmlList(handlebarsComponentClientLibRecompileService.recompileHandlebarComponents(componentPath, contentPath, isReplicate, session)));
                    jsonObject.addProperty("Message", "Compilation Complete!!!!");
                } else {
                    jsonObject.addProperty("error", "Not a handlebars component");
                }
        }

        } catch (DynamicClientLibException e) {
            jsonObject.addProperty("Error","Error recompiling Handlebars components: "+ e.getMsg());
           log.error("Error recompiling Handlebars components" + contentPath + ": "+ componentAbsolutePath, e);
           response.setStatus(SlingHttpServletResponse.SC_FORBIDDEN);
        }
        response.getWriter().write(jsonObject.toString());
    }

    private String buildHtmlList(ArrayList<String> list) {
        StringBuilder htmlList = new StringBuilder();
        list.stream()
                .distinct()
                .forEach(nodePath -> {
            htmlList.append("<li>").append(nodePath).append("</li>").append("\n");
        });
        return "<ul>"+htmlList.toString()+"</ul>";
    }
}
