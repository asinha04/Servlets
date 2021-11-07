package org.kp.patterns.core.models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.ExporterOption;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Via;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.apache.sling.models.annotations.via.ResourceSuperType;
import org.kp.foundation.core.enums.AlertsEnum;
import org.kp.foundation.core.impl.DAMContentFragmentImpl;
import org.kp.foundation.core.models.AlertsFragmentListDataModel;
import org.kp.foundation.core.models.SelectorUtilModel;

import com.adobe.cq.dam.cfm.converter.ContentTypeConverter;
import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.adobe.cq.wcm.core.components.models.contentfragment.ContentFragmentList;
import com.adobe.cq.wcm.core.components.models.contentfragment.DAMContentFragment;
import com.adobe.cq.wcm.core.components.models.contentfragment.DAMContentFragment.DAMContentElement;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Sling Model class for Customizing Core Layout container Component. Added
 * sling exporter annotations to expose content as json.
 * 
 * * @author Ravish Sehgal
 */
@Model(adaptables = SlingHttpServletRequest.class, adapters = { ContentFragmentList.class,
		ComponentExporter.class }, resourceType = {
				"kporg/kp-foundation/components/content/alerts" }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME, extensions = ExporterConstants.SLING_MODEL_EXTENSION, options = {
		@ExporterOption(name = "MapperFeature.SORT_PROPERTIES_ALPHABETICALLY", value = "true") })
public class AlertsPattern implements ContentFragmentList {

	@Inject
	SlingHttpServletRequest request;

	@Self
	private SelectorUtilModel selectorUtil;

	@Self
	@Via(type = ResourceSuperType.class)
	private ContentFragmentList delegate;

	@Inject
	@Via("resource")
	@Default(values = "headerAlerts")
	private String notificationType;

	@Inject
	@Via("resource")
	@Default(values = "Important Notices")
	private String bulletinHeading;

	/**
	 * Component properties.
	 */
	@ScriptVariable
	private ValueMap properties;

	@Inject
	private ResourceResolver resourceResolver;

	@ValueMapValue(name = ContentFragmentList.PN_ELEMENT_NAMES, injectionStrategy = InjectionStrategy.OPTIONAL)
	private String[] elementNames;
	private List<DAMContentFragment> staticListItems = new ArrayList<>();

	@Inject
	private ContentTypeConverter contentTypeConverter;

	Collection<DAMContentFragment> originalItems = new ArrayList<DAMContentFragment>();
	String PN_SOURCE = "listFrom";
	String PN_FRAGMENT_SOURCE = "fragmentPath";

	private List<AlertsFragmentListDataModel> filteredAlertsList = new ArrayList<>();
	private List<DAMContentFragment> unfilteredAlertsList = new ArrayList<>();
	private List<AlertsFragmentListDataModel> p1Alerts = new ArrayList<>();
	private List<AlertsFragmentListDataModel> p2Alerts = new ArrayList<>();
	private List<AlertsFragmentListDataModel> p3Alerts = new ArrayList<>();
	private static final String ALERTMESSAGE = "alertMessage";
	private static final String ALERT_TYPE = "alertType";
	private static final String RESOUCRCE_TYPE = "dam:Asset";

	private AlertsFragmentListDataModel alertsdto = null;

	@PostConstruct
	public void initModel() {
		if (null != properties.get(PN_SOURCE, String.class)
				&& properties.get(PN_SOURCE, String.class).equalsIgnoreCase("static")) {
			getStaticListItems();
		} else if (null != properties.get(PN_SOURCE, String.class)
				&& properties.get(PN_SOURCE, String.class).equalsIgnoreCase("all")) {
			unfilteredAlertsList.addAll(delegate.getListItems());
			buildAlertsList(unfilteredAlertsList);
		}
	}

	private void getStaticListItems() {
		staticListItems = new ArrayList<>();
		Stream<String> stream = Stream.of(this.properties.get(PN_FRAGMENT_SOURCE, new String[0]));
		stream.forEach(s -> processStream(s));

	}

	private void processStream(String s) {
		Resource fragmentResource = resourceResolver.getResource(s);
		if (fragmentResource != null && fragmentResource.getResourceType().equalsIgnoreCase(RESOUCRCE_TYPE)) {
			DAMContentFragmentImpl contentFragmentModel = new DAMContentFragmentImpl(fragmentResource,
					contentTypeConverter, null, elementNames);
			staticListItems.add(contentFragmentModel);
			buildAlertsList(staticListItems);
		}
	}

	public void buildAlertsList(List<DAMContentFragment> list) {
		p1Alerts = new ArrayList<>();
		p2Alerts = new ArrayList<>();
		p3Alerts = new ArrayList<>();
		for (int i = 0; i < list.size(); i++) {
			alertsdto = new AlertsFragmentListDataModel();
			alertsdto.setTitle(list.get(i).getTitle());
			alertsdto.setDescription(list.get(i).getDescription());
			List<DAMContentElement> contentElements = new ArrayList<>();
			contentElements.addAll(list.get(i).getElements());
			for (int j = 0; j < contentElements.size(); j++) {
				if (contentElements.get(j).getName().equalsIgnoreCase(ALERTMESSAGE)) {
					alertsdto.setMessage(contentElements.get(j).getValue());
				} else if (contentElements.get(j).getName().equalsIgnoreCase(ALERT_TYPE)) {
					alertsdto.setType(contentElements.get(j).getValue());
				}
			}
			if (null != alertsdto.getType()
					&& alertsdto.getType().toString().equalsIgnoreCase(AlertsEnum.P1.toString())) {
				p1Alerts.add(alertsdto);
			} else if (null != alertsdto.getType()
					&& alertsdto.getType().toString().equalsIgnoreCase(AlertsEnum.P2.toString())) {
				p2Alerts.add(alertsdto);
			} else if (null != alertsdto.getType()
					&& alertsdto.getType().toString().equalsIgnoreCase(AlertsEnum.P3.toString())) {
				p3Alerts.add(alertsdto);
			}
			alertsdto.setOriginalItems(null);
		}
	}

	public void checkForCompleteJSON() {
		if (selectorUtil != null && !selectorUtil.isTidySelector()) {
//			done to make sure P1 always appear before P2 and P2 before P3s.
			filteredAlertsList.addAll(p1Alerts);
			filteredAlertsList.addAll(p2Alerts);
			filteredAlertsList.addAll(p3Alerts);
		} else {
//			done to generate for original JSON with all attributes.
			alertsdto = new AlertsFragmentListDataModel();
			filteredAlertsList = new ArrayList<>();
			alertsdto.getOriginalItems().addAll(delegate.getListItems());
			filteredAlertsList.add(alertsdto);
		}
	}

//	consolidated list of alerts that will output the desired altered and original JSON.
	public List<AlertsFragmentListDataModel> getAlerts() {
		checkForCompleteJSON();
		filteredAlertsList = new ArrayList<AlertsFragmentListDataModel>(
				new LinkedHashSet<AlertsFragmentListDataModel>(filteredAlertsList));
		return new ArrayList<>(filteredAlertsList);
	}

//	consolidated list of alerts that will output the desired altered and original JSON.
	@JsonIgnore
	public List<AlertsFragmentListDataModel> getBulletinAlerts() {
		List<AlertsFragmentListDataModel> bulletinList = new ArrayList<>();
		bulletinList.addAll(p3Alerts);
		return bulletinList;
	}

//  list of P1 alerts to be displayed in the alerts component	
	@JsonIgnore
	public List<AlertsFragmentListDataModel> getP1Alerts() {
		return new ArrayList<>(p1Alerts);
	}

//  list of P2 alerts to be displayed in the alerts component
	@JsonIgnore
	public List<AlertsFragmentListDataModel> getP2Alerts() {
		return new ArrayList<>(p2Alerts);
	}

	@JsonIgnore
	public String getNotificationType() {
		return notificationType;
	}

	@JsonIgnore
	public String getBulletinHeading() {
		return bulletinHeading;
	}
}