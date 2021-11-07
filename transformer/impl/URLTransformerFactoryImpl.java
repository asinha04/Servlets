package org.kp.foundation.core.transformer.impl;

import java.util.Arrays;

import org.apache.sling.jcr.api.SlingRepository;
import org.kp.foundation.core.transformer.IURLTransformerFactory;
import org.kp.foundation.core.transformer.URLTransformer;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A factory for creating Transformer objects. It creates and returns an
 * instance of Transformer.
 */
@Component(immediate = true, name = "KP Transformer Factory", property = { "process.label= KP Transformer Factory"}, enabled = true,configurationPid = "org.kp.foundation.core.transformer.impl.URLTransformerFactoryImpl")
@Designate(ocd = URLTransformerFactoryImpl.Config.class)
public class URLTransformerFactoryImpl implements org.apache.sling.rewriter.TransformerFactory, IURLTransformerFactory {
	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory.getLogger(URLTransformerFactoryImpl.class);

	/**
	 * Instantiates a new URL TransformerFactory.
	 */
	public URLTransformerFactoryImpl() {
		LOG.debug("inside the constructor");
	}

	@ObjectClassDefinition(name = "Configuration URLTransformerFactory", description = "Configuration URLTransformerFactory")
	public static @interface Config {
		@AttributeDefinition(name = "transformer.root.paths", cardinality = Integer.MAX_VALUE, description = "Paths where transformation will take place")
		String[] transformer_root_paths() default { "/content/kporg" };

		@AttributeDefinition(name = "blacklist.domain", cardinality = Integer.MAX_VALUE, description = "blacklist.domain")
		String[] blacklist_domain() default { "localhost" };

		@AttributeDefinition(name = "kpdomains", cardinality = 10, description = "The links pointing to a domain not in this list will be appended with a KP external domain message.")
		String[] kpdomains();

		@AttributeDefinition(name = "kpexternal", cardinality = 10, description = "This will override the condition when KP Domain list matches, but links still needs to be considered as external.")
		String[] kpexternal();

		@AttributeDefinition(name = "kplinkdialog", cardinality = 10, description = "This will override the condition when KP Link Dialog list matches and add title attribute with the Opens a dialog message.")
		String[] kplinkdialog();
	}

	private String[] blacklistDomainList = null;
	/** The root paths. */
	private String[] rootPaths;
	/** The repository. */
	@Reference
	private SlingRepository repository;
	/** The config admin. */
	@Reference(cardinality = ReferenceCardinality.MANDATORY, policy = ReferencePolicy.STATIC)
	private ConfigurationAdmin configAdmin;

	/** The kp domains paths. */
	private String[] kpdomainList;

	/** The kp domains paths. */
	private String[] kpexternalList;

	/** The kp domains paths. */
	private String[] kplinkdialogList;

	/**
	 * Creates the URL Transformer.
	 * 
	 * @return the URLTransformer
	 */
	public URLTransformer createTransformer() {
		LOG.debug("create URLTransformer...");
		return new URLTransformer(this);
	}

	/**
	 * Gets the repository.
	 * 
	 * @return the repository
	 */
	public SlingRepository getRepository() {
		return repository;
	}

	/**
	 * Bind repository.
	 * 
	 * @param newRepository the repository
	 */
	protected void bindRepository(SlingRepository newRepository) {
		this.repository = newRepository;
	}

	/**
	 * Unbind repository.
	 * 
	 * @param newRepository the repository
	 */
	protected void unbindRepository(SlingRepository newRepository) {
		this.repository = null;
	}

	// Declarative Service Component Interfaces
	/**
	 * Activate.
	 * 
	 * @param componentContext the component context
	 */
	@Activate
	protected void activate(final Config config) {
		String[] props = config.transformer_root_paths();
		LOG.debug("rootPathList: " + props);
		if (null != props) {
			setRootPaths(props);
		}
		String[] blackListDomainList = config.blacklist_domain();
		LOG.debug("blackListDomainList: " + blackListDomainList);
		if (null != blackListDomainList) {
			setBlacklistDomainList(blackListDomainList);
		}

		kpdomainList = config.kpdomains();
		kpexternalList = config.kpexternal();
		kplinkdialogList = config.kplinkdialog();
	}

	/**
	 * Modified.
	 * 
	 * @param newComponentContext the component context
	 */
	@Modified
	protected void modified(final Config config) {
		LOG.debug("Entry into TransformerFactory");
		this.activate(config);
		LOG.debug("Exit from TransformerFactory");
	}

	/**
	 * Gets the root paths.
	 * 
	 * @return the root paths
	 */
	public String[] getRootPaths() {
		LOG.debug("Entry into getRootPaths");
		String[] newRootPaths = null;
		if (null != this.rootPaths) {
			newRootPaths = Arrays.copyOf(this.rootPaths, this.rootPaths.length);
		}
		return newRootPaths;
	}

	/**
	 * Gets the kp domains paths.
	 * 
	 * @return the configured domains
	 */
	public String[] getKPDomains() {
		LOG.debug("Entry into getKPDomains");
		if(this.kpdomainList == null) { 
		    this.kpdomainList = new String[0]; 
	      } 
	        return this.kpdomainList.clone();
	}

	/**
	 * Gets the kp domains paths.
	 * 
	 * @return the configured domains
	 */
	public String[] getKPExternal() {
		LOG.debug("Entry into getKPExternal");
		if(this.kpexternalList == null) { 
            this.kpexternalList = new String[0]; 
          } 
          return this.kpexternalList.clone();
	}

	/**
	 * Gets the kp domains paths.
	 * 
	 * @return the configured domains
	 */
	public String[] getKPLinkDialog() {
		LOG.debug("Entry into getKPLinkDialog");
		if(this.kplinkdialogList == null) { 
		    this.kplinkdialogList = new String[0]; 
		  }  
          return this.kplinkdialogList.clone();
	}

	/**
	 * Sets the root paths.
	 * 
	 * @param newRootPaths the new root paths
	 */
	private void setRootPaths(final String[] newRootPaths) {
		LOG.debug("Entry into setRootPaths");
		String[] copyNewRootPaths = newRootPaths.clone();
		this.rootPaths = copyNewRootPaths;
		LOG.debug("Exit from setRootPaths");
	}

	/**
	 * 
	 * @return
	 */
	public String[] getBlacklistDomainList() {
		return blacklistDomainList.clone();
	}

	/**
	 * Sets the black list DomainList .
	 * 
	 * @param blacklistDomainList
	 */
	public void setBlacklistDomainList(String[] blacklistDomainList) {
		LOG.debug("Entry into setBlacklistDomainList");
		String[] copyBlacklistDomainList = blacklistDomainList.clone();
		this.blacklistDomainList = copyBlacklistDomainList;
		LOG.debug("Exit from setBlacklistDomainList");
	}

}
