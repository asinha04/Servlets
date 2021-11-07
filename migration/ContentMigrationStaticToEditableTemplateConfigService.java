package org.kp.foundation.core.migration;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@Component(immediate = true, name="Content migration from static to editable template", service = ContentMigrationStaticToEditableTemplateConfigService.class, configurationPid = "org.kp.foundation.core.migration.ContentMigrationStaticToEditableTemplateConfigService")
@Designate(ocd = ContentMigrationStaticToEditableTemplateConfigService.Configuration.class)
public class ContentMigrationStaticToEditableTemplateConfigService {

	@ObjectClassDefinition(name = "Content migration from static to editable template", description = "This script will be used for content migration from static to editable template")
	public @interface Configuration {
		@AttributeDefinition(name = "Template Mappings", description = "Static to Editable template mapping separated by pipe operator. "
				+ "For example -  kporg/kp-foundation/components/structure/contentPage|/conf/kporg/kp-foundation/settings/wcm/templates/gsc-template")
		String[] staticToEditableTemplateMapping();
			

		@AttributeDefinition(name = "Parsys Names", description = "Parsys names to be included in migration. For example - bodypar, contentpar")
		String[] parsysNamesForMigration();
	}

	private Configuration config;
	
	public String[] staticToEditableTemplateMapping() {
		return config.staticToEditableTemplateMapping();
	}
	
	public String[] parsysNamesForMigration() {
		return config.parsysNamesForMigration();
	}
	
	@Activate
	@Modified
	protected void activate(Configuration config) {
		this.config = config;
	}
}