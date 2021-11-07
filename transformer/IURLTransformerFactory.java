package org.kp.foundation.core.transformer;

import org.apache.sling.jcr.api.SlingRepository;
/**
 * A factory for creating Transformer objects. It creates and returns an instance of Transformer.
 */

public interface IURLTransformerFactory {
	
	public URLTransformer createTransformer();
	
	public SlingRepository getRepository();

	public String[] getRootPaths() ;
	
	public String[] getBlacklistDomainList() ;
	
	
}
