package org.kp.foundation.core.service.impl;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.Cookie;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.wrappers.SlingHttpServletRequestWrapper;
import org.kp.foundation.core.constants.GlobalConstants;
import org.kp.foundation.core.enums.LanguageLocaleEnum;
import org.kp.foundation.core.service.KPErrorHandlerService;
import org.kp.foundation.core.utils.PropertyInheritedUtil;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.crx.JcrConstants;



/**
 * @author krishan This is the implementation class for Error page handler for KP.
 */
@Component(service = KPErrorHandlerService.class, immediate = true)
public class KPErrorhandlerServiceImpl implements KPErrorHandlerService {

  private static final String ES_NODE = "/content/kporg/es/" + JcrConstants.JCR_CONTENT;
  private static final String ENGLISH_NODE = "/content/kporg/en/" + JcrConstants.JCR_CONTENT;
  private static final String PAGE_404 = "/404.html";
  private static final String ERROR_PAGES_PROPERTY = "errorPages";
  // Inject a Sling ResourceResolverFactory
  @Reference
  private ResourceResolverFactory resolverFactory;
  private static final Logger log = LoggerFactory.getLogger(KPErrorHandlerService.class);

  @Override
  public String getDefaultPath(SlingHttpServletRequest request, Resource errorResource) {
    String errorPagePath = null;
    log.debug("errorResource {}", errorResource);
    log.debug("SlingHttpServletRequest {}", request);
    if (errorResource != null) {
      final ResourceResolver resourceResolver = errorResource.getResourceResolver();
      if (resourceResolver != null) {
        String kpLanguage = getCookie(request, GlobalConstants.KP_LANGUAGE_COOKIE);
        if (LanguageLocaleEnum.ES.getLabel().equalsIgnoreCase(kpLanguage)) {
          String spanishPage = ES_NODE;
          errorPagePath = deriveErrorPagePath(request, resourceResolver, spanishPage);
        } else {
          String englishPage = ENGLISH_NODE;
          errorPagePath = deriveErrorPagePath(request, resourceResolver, englishPage);
        }
      }
    }
    log.debug("errorPagePath {}", errorPagePath);
    return errorPagePath;
  }

  private String deriveErrorPagePath(SlingHttpServletRequest request,
      final ResourceResolver resourceResolver, String englishPage) {
    String errorPagePath;

    Resource resource = resourceResolver.resolve(request, englishPage);
    errorPagePath = PropertyInheritedUtil.getProperty(resource, ERROR_PAGES_PROPERTY);
    if (errorPagePath != null) {
      errorPagePath = errorPagePath + PAGE_404;
    }
    return errorPagePath;
  }

  private String getCookie(SlingHttpServletRequest request, String cookieName) {
    Cookie[] cookies = request.getCookies();
    String cookieValue = null;
    if (cookies != null) {
      for (Cookie cookie : cookies) {
        if (cookieName.equalsIgnoreCase(cookie.getName())) {
          cookieValue = cookie.getValue();
        }
      }
    }
    return cookieValue;
  }

  @Override
  public void includeUsingGET(final SlingHttpServletRequest request,
      final SlingHttpServletResponse response, final String path) {

    final RequestDispatcher dispatcher = request.getRequestDispatcher(path);

    if (dispatcher != null) {
      try {
        dispatcher.include(new GetRequest(request), response);
      } catch (Exception e) {
        log.error("Exception swallowed while including error page {}.", e);
      }
    }

  }

  /**
   * Forces request to behave as a GET Request.
   */
  private static class GetRequest extends SlingHttpServletRequestWrapper {

    public GetRequest(SlingHttpServletRequest wrappedRequest) {
      super(wrappedRequest);
    }

    @Override
    public String getMethod() {
      return "GET";
    }
  }
}
