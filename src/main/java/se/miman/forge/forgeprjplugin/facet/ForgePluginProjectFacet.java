/**
 * 
 */
package se.miman.forge.forgeprjplugin.facet;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.jboss.forge.maven.MavenCoreFacet;
import org.jboss.forge.project.facets.DependencyFacet;
import org.jboss.forge.project.facets.JavaSourceFacet;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.RequiresFacet;

import se.miman.forge.plugin.util.MimanBaseFacet;
import se.miman.forge.plugin.util.VelocityUtil;
import se.miman.forge.plugin.util.helpers.DomFileHelper;
import se.miman.forge.plugin.util.helpers.DomFileHelperImpl;

/**
 * A Facet for a JBoss forge plugin project.
 * 
 * Adds the necessary dependencies & artifacts for creating a JBoss Forge plugin.
 * 
 * @author Mikael Thorman
 */
@Alias("forge-plugin-prj-facet")
@RequiresFacet({ MavenCoreFacet.class, JavaSourceFacet.class,
		DependencyFacet.class })
public class ForgePluginProjectFacet extends MimanBaseFacet {

	DomFileHelper domFileHelper;

	private final VelocityEngine velocityEngine;
	private VelocityUtil velocityUtil;

	public ForgePluginProjectFacet() {
		super();
		domFileHelper = new DomFileHelperImpl();
		
		velocityUtil = new VelocityUtil();
		
		velocityEngine = new VelocityEngine();
		velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER,
				"classpath");
		velocityEngine.setProperty("classpath.resource.loader.class",
				ClasspathResourceLoader.class.getName());
		velocityEngine.setProperty(
				RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS,
				"org.apache.velocity.runtime.log.JdkLogChute");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jboss.forge.project.Facet#install()
	 */
	@Override
	public boolean install() {
		configureProject();
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jboss.forge.project.Facet#isInstalled()
	 */
	@Override
	public boolean isInstalled() {
		final MavenCoreFacet mvnFacet = project.getFacet(MavenCoreFacet.class);
		Model pom = mvnFacet.getPOM();

		List<Dependency> deps = pom.getDependencies();
		boolean dependenciesOk = false;
		for (Dependency dependency : deps) {
			if (dependency.getGroupId().equals("org.jboss.forge") && dependency.getArtifactId().equals("forge-shell-api")) {
				dependenciesOk = true;
			}
			// TODO more checks should be added here
		}
		
		return dependenciesOk;
	}

	// Helper functions ****************************************
	/**
	 * Configures the project to be a JBoss Forge plugin project.
	 * Adds the necessary dependencies to the pom.xml file.
	 * Creates the Forge.xml file
	 */
	private void configureProject() {
		final MavenCoreFacet mvnFacet = project.getFacet(MavenCoreFacet.class);
		Model pom = mvnFacet.getPOM();

		mergePomFileWithTemplate(pom);
		mvnFacet.setPOM(pom);
		
		createForgeXmlFile();
		createBeansXmlFile();
		createExampleTemplateFiles();
	}

	/**
	 * Creates the forge.xml.
	 * We need this to be able to have a dependency to jar files with Forge classes.
	 */
	private void createForgeXmlFile() {
		String sourceUri = "/template-files/META-INF/forge.xml";
		String targetUri = "META-INF/forge.xml";
		
		Map<String, Object> velocityPlaceholderMap = new HashMap<String, Object>();

		VelocityContext velocityContext = velocityUtil
				.createVelocityContext(velocityPlaceholderMap);
		velocityUtil.createResourceAbsolute(sourceUri, velocityContext, targetUri, project, velocityEngine);
	}

	/**
	 * Creates the beans.xml.
	 */
	private void createBeansXmlFile() {
		String sourceUri = "/template-files/META-INF/beans.xml";
		String targetUri = "META-INF/beans.xml";
		
		Map<String, Object> velocityPlaceholderMap = new HashMap<String, Object>();

		VelocityContext velocityContext = velocityUtil
				.createVelocityContext(velocityPlaceholderMap);
		velocityUtil.createResourceAbsolute(sourceUri, velocityContext, targetUri, project, velocityEngine);
	}
	
	/**
	 * Creates the pom.xml.
	 */
	private void createExampleTemplateFiles() {
		String sourceUri = "/template-files/example_pom.xml";
		String targetUri = "template-files/pom.xml";
		
		Map<String, Object> velocityPlaceholderMap = new HashMap<String, Object>();

		VelocityContext velocityContext = velocityUtil
				.createVelocityContext(velocityPlaceholderMap);
		velocityUtil.createResourceAbsolute(sourceUri, velocityContext, targetUri, project, velocityEngine);

		sourceUri = "/template-files/META-INF/forge.xml";
		targetUri = "template-files/META-INF/forge.xml";
		
		velocityPlaceholderMap = new HashMap<String, Object>();

		velocityContext = velocityUtil
				.createVelocityContext(velocityPlaceholderMap);
		velocityUtil.createResourceAbsolute(sourceUri, velocityContext, targetUri, project, velocityEngine);
}
	
	@Override
	protected String getTargetPomFilePath() {
		return "/template-files/pom.xml";
	}
}
