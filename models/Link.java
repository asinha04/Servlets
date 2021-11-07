package org.kp.foundation.core.models;

//------------------------------------------------------------------------------
/**
 * Link class for Dynamic Link List component.
 */
// ------------------------------------------------------------------------------
public class Link {
	protected String title;
	protected String href;
	protected boolean rel;
	protected String target;

	// -------------------------------------------------------setting the variables
	public Link(String title, String href, boolean rel, String target) {
		this.title = title;
		this.href = href;
		this.rel = rel;
		this.target = target;
	} // dllModel

} // dllModel
