package se.miman.forge.forgeprjplugin;

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.ResourceFacet;
import org.jboss.forge.project.facets.events.InstallFacets;
import org.jboss.forge.shell.ShellMessages;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.Command;
import org.jboss.forge.shell.plugins.Help;
import org.jboss.forge.shell.plugins.Option;
import org.jboss.forge.shell.plugins.PipeOut;
import org.jboss.forge.shell.plugins.Plugin;
import org.jboss.forge.shell.plugins.RequiresProject;
import org.jboss.forge.shell.plugins.SetupCommand;

import se.miman.forge.forgeprjplugin.facet.ForgePluginProjectFacet;
import se.miman.forge.plugin.util.VelocityUtil;
import se.miman.forge.plugin.util.helpers.FilePathUtil;
import se.miman.forge.plugin.util.helpers.FilePathUtilImpl;

/**
 * Modifies a project to a JBoss Forge plugin project.
 */
@Alias("forge-plugin-prj")
@Help("A plugin that helps to build a JBoss Forge plugin project in an easy fashion")
@RequiresProject
public class ForgePluginProjectPlugin implements Plugin {
	@Inject
	private Event<InstallFacets> event;

	@Inject
	private Project project;

	FilePathUtil filePathUtil;
	
	/**
	 * The velocity engine used to replace data in the supplied templates with the correct info.
	 */
	private final VelocityEngine velocityEngine;
	private VelocityUtil velocityUtil;
	
	public ForgePluginProjectPlugin() {
		super();
		filePathUtil = new FilePathUtilImpl();
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
	
	@SetupCommand
	@Command(value = "setup", help = "Convert project to a Forge plugin project")
	public void setup(PipeOut out) {

		if (!project.hasFacet(ResourceFacet.class)) {
			event.fire(new InstallFacets(ResourceFacet.class));
		}

		if (!project.hasFacet(ForgePluginProjectFacet.class))
			event.fire(new InstallFacets(ForgePluginProjectFacet.class));
		else
			ShellMessages.info(out, "Project already a Forge plugin project.");
	}

	/**
	 * Command to create an example Plugin and Facet class
	 * @param name	The wanted name of the Plugin/Facet
	 * @param path	Where the plugin should be created (under java/src)
	 * @param alias	The alias the plugin should have
	 * @param out	Error info statements are written to this pipe to be displayed to the user
	 */
	@Command(value = "add-plugin", help = "Adds an example Forge plugin and Facet to this project")
	public void addRoute(
			@Option(name = "name", shortName = "n") String name,
			@Option(name = "path") String path,
			@Option(name = "alias") String alias,
			PipeOut out) {
		String usedPath = filePathUtil.replaceRelativePathToken(path, project);
		addExampleFiles(name, usedPath, alias, out);
	}
	
	//**********************************************
	// Helper functions
	
	private void addExampleFiles(String exampleClassname, String path, String alias, PipeOut out) {
		createPluginExampleFile(exampleClassname, path, alias, out);
		createFacetExampleFile(exampleClassname, path, alias, out);
	}

	private void createFacetExampleFile(String exampleClassname, String path, String alias, PipeOut out) {
		String parentPomUri = "/template-files/class-templates/facet/Example_Facet.java.template";
		
		Map<String, Object> velocityPlaceholderMap = new HashMap<String, Object>();
		velocityPlaceholderMap.put("class-name", exampleClassname);
		String usedAlias = alias;
		if (usedAlias == null) {
			usedAlias = exampleClassname.toLowerCase();
		}
		velocityPlaceholderMap.put("alias", usedAlias);
		velocityPlaceholderMap.put("path", path);
		
		VelocityContext velocityContext = velocityUtil.createVelocityContext(velocityPlaceholderMap);
		velocityUtil.createJavaSource(parentPomUri, velocityContext, project, velocityEngine);
	}

	private void createPluginExampleFile(String exampleClassname, String path, String alias, PipeOut out) {
		String parentPomUri = "/template-files/class-templates/Example_Plugin.java.template";
		
		Map<String, Object> velocityPlaceholderMap = new HashMap<String, Object>();
		velocityPlaceholderMap.put("class-name", exampleClassname);
		String usedAlias = alias;
		if (usedAlias == null) {
			usedAlias = exampleClassname.toLowerCase();
		}
		velocityPlaceholderMap.put("alias", usedAlias);
		velocityPlaceholderMap.put("path", path);
		
		VelocityContext velocityContext = velocityUtil.createVelocityContext(velocityPlaceholderMap);
		velocityUtil.createJavaSource(parentPomUri, velocityContext, project, velocityEngine);

		createTypeFile(exampleClassname, path, out);
		createCompleterFile(exampleClassname, path, out);
	}

	/**
	 * Creates a completer class to demonstrate the usage of one of these 
	 * @param exampleClassname
	 * @param path
	 * @param out
	 */
	private void createCompleterFile(String exampleClassname, String path, PipeOut out) {
		String parentPomUri = "/template-files/class-templates/completer/Example_Completer.java.template";
		
		Map<String, Object> velocityPlaceholderMap = new HashMap<String, Object>();
		velocityPlaceholderMap.put("class-name", exampleClassname);
		velocityPlaceholderMap.put("path", path);
		
		VelocityContext velocityContext = velocityUtil.createVelocityContext(velocityPlaceholderMap);
		velocityUtil.createJavaSource(parentPomUri, velocityContext, project, velocityEngine);
	}

	private void createTypeFile(String exampleClassname, String path,
			PipeOut out) {
		String parentPomUri = "/template-files/class-templates/completer/Example_Type.java.template";
		
		Map<String, Object> velocityPlaceholderMap = new HashMap<String, Object>();
		velocityPlaceholderMap.put("class-name", exampleClassname);
		velocityPlaceholderMap.put("path", path);
		
		VelocityContext velocityContext = velocityUtil.createVelocityContext(velocityPlaceholderMap);
		velocityUtil.createJavaEnumSource(parentPomUri, velocityContext, project, velocityEngine);
	}
}
