package org.kp.foundation.core.migration;

import com.adobe.granite.jmx.annotation.Description;
import aQute.bnd.annotation.ProviderType;

/**
 * MBean for content migration from static to editable template
 */
@ProviderType
@Description("MBean for content migration from static to editable template")
public interface ContentMigrationStaticToEditableTemplateMBean {
	@Description("Content migration script. Enter content path in first parameter and this is a mandatory parameter. For ex. /content/kporg/en ."
			+ "And second parameter is optional and it accepts true/false. "
			+ "Enter true if you want to run this migration for given page, and not for any child pages. If you don't enter second parameter or enter false, script will run for entire content tree.")
	public String runMigration(String path, Boolean runForSinglePageOnly);
}