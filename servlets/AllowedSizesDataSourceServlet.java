package org.kp.foundation.core.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.jetbrains.annotations.NotNull;
import org.kp.foundation.core.utils.AllowedSizesValuesDataResourceUtil;
import org.osgi.service.component.annotations.Component;

import com.adobe.granite.ui.components.Value;
import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.SimpleDataSource;
import com.day.cq.wcm.api.policies.ContentPolicy;
import com.day.cq.wcm.api.policies.ContentPolicyManager;


@Component(
        service = { Servlet.class },
        property = {
                "sling.servlet.resourceTypes="+ AllowedSizesDataSourceServlet.RESOURCE_TYPE,
                "sling.servlet.methods=GET",
                "sling.servlet.extensions=html"
        }
)
public class AllowedSizesDataSourceServlet extends SlingSafeMethodsServlet{

	private static final long serialVersionUID = 1501143683076035317L;
	public final static String RESOURCE_TYPE = "kp/core/components/datasource/allowedtypes";
	public final static String PN_DEFAULT_ELEMENT = "defaultElement";
	public final static String PN_ALLOWED_ELEMENTS = "allowedElements";
	public final static String PN_DEFAULT_TYPE = "type";
	public final static String PN_ALLOWED_TYPES = "allowedTypes";
	

	@Override
    protected void doGet(@NotNull SlingHttpServletRequest request, @NotNull SlingHttpServletResponse response)
            throws ServletException, IOException {
        SimpleDataSource allowedTypesDataSource = new SimpleDataSource(getAllowedTypes(request).iterator());
        request.setAttribute(DataSource.class.getName(), allowedTypesDataSource);
    }

    private List<Resource> getAllowedTypes(@NotNull SlingHttpServletRequest request) {
        List<Resource> allowedTypes = new ArrayList<>();
        ResourceResolver resolver = request.getResourceResolver();
        @SuppressWarnings("deprecation")
		Resource contentResource = resolver.getResource((String) request.getAttribute(Value.CONTENTPATH_ATTRIBUTE));
        ContentPolicyManager policyMgr = resolver.adaptTo(ContentPolicyManager.class);
        if (policyMgr != null) {
            ContentPolicy policy = policyMgr.getPolicy(contentResource);
            if (policy != null) {
                ValueMap props = policy.getProperties();
                if (props != null) {
					String[] htmlElements = props.get(PN_ALLOWED_ELEMENTS, String[].class);
					String[] tagTypes = props.get(PN_ALLOWED_TYPES, String[].class);
					String defaultElement = props.get(PN_DEFAULT_ELEMENT, props.get(PN_DEFAULT_TYPE, String.class));
                    if (htmlElements == null || htmlElements.length == 0) {
                        htmlElements = tagTypes;
                    }
                    if (htmlElements != null && htmlElements.length > 0) {
                        for (String htmlElement : htmlElements) {
                        	allowedTypes.add(new ContainerTypeResource(htmlElement,
                                    StringUtils.equals(htmlElement, defaultElement), resolver));
                        }
                    }
                }
            }
        }
        return allowedTypes;
    }

    private static class ContainerTypeResource extends AllowedSizesValuesDataResourceUtil {

        private final String type;
        private boolean selected =false;
        ContainerTypeResource(String htmlElement, boolean defaultElement, ResourceResolver resourceResolver) {
            super(resourceResolver, StringUtils.EMPTY, RESOURCE_TYPE_NON_EXISTING);
            this.type = htmlElement;
            this.selected = defaultElement;
        }

        @Override
		public String getText() {
            Heading heading = Heading.getHeading(type);
            if (heading != null) {
                return heading.getElement();
            }
            return null;
        }

        @Override
		public String getValue() {
            return type;
        }

		@Override
		public boolean getSelected() {
			return selected;
		}
    }

    private enum Heading {
		H1("h1"), H2("h2"), H3("h3"), H4("h4"), H5("h5"), H6("h6"), MAIN("main"), ASIDE("aside"), DIV("div"),
		NAV("nav");

        private String element;

        Heading(String element) {
            this.element = element;
        }

        private static Heading getHeading(String value) {
            for (Heading heading : values()) {
                if (StringUtils.equalsIgnoreCase(heading.element, value)) {
                    return heading;
                }
            }
            return null;
        }

        public String getElement() {
            return element;
        }
    }
}
