/**
 * 
 */
package org.kp.foundation.core.service.impl;

import org.kp.foundation.core.service.PropertyConfigService;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

/**
 * Configuration Property Loader
 * 
 * @author
 *
 */
@Component(service = PropertyConfigService.class, immediate = false,configurationPid = "org.kp.foundation.core.service.impl.PropertyConfigServiceImpl")
@Designate(ocd = PropertyConfigServiceImpl.Config.class)
public class PropertyConfigServiceImpl implements PropertyConfigService {

	private Config config;

	@ObjectClassDefinition(name = "Configuration Property Loader", description = "Configuration Property Loader")
	public static @interface Config {
		@AttributeDefinition(name = "DISPATCHER_SERVER", description = "Dispatcher Server address", type = AttributeType.STRING)
		String DISPATCHER__SERVER();

		@AttributeDefinition(name = "DTM_JS", description = "DTM JS File", type = AttributeType.STRING)
		String DTM__JS();
	}

	@Activate
	protected void activate(Config config) {
		this.config = config;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.kp.foundation.core.service.PropertyConfigService#getDTMJsfile()
	 */
	@Override
	public String getDTMJsfile() {
		return config.DTM__JS();
	}

	@Override
	public String getDispatcherServer() {
		return config.DISPATCHER__SERVER();
	}

}
