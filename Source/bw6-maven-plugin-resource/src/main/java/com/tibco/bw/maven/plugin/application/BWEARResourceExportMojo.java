package com.tibco.bw.maven.plugin.application;


import com.tibco.bw.maven.plugin.osgi.helpers.ManifestParser;
import com.tibco.bw.maven.plugin.osgi.helpers.ManifestWriter;
import com.tibco.bw.maven.plugin.osgi.helpers.Version;
import com.tibco.bw.maven.plugin.osgi.helpers.VersionParser;
import com.tibco.bw.maven.plugin.utils.BWFileUtils;
import com.tibco.bw.maven.plugin.utils.BWModulesParser;
import com.tibco.bw.maven.plugin.utils.BWProjectUtils;
import com.tibco.bw.maven.plugin.utils.Constants;


import com.tibco.bw.maven.plugin.utils.FileUtilsProject;
import org.apache.commons.io.FileUtils;
import org.apache.maven.archiver.MavenArchiveConfiguration;
import org.apache.maven.archiver.MavenArchiver;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.archiver.jar.JarArchiver;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


@Mojo(name = "bwexport" )
public class BWEARResourceExportMojo extends AbstractMojo {

    @Component
    private MavenSession session;

    @Component
    private MavenProject project;
	
	@Parameter(property="project.build.directory")
    private File outputDirectory;

	@Parameter(property="project.basedir")
	private File projectBasedir;

    
	@Parameter(property="profile")
	private String profile;
	
	@Parameter(property="propertyfile")
	private String propertyfile;

    

    /**
     * Execute Method.
     * 
     */
    public void execute() throws MojoExecutionException {
    	try {

    		 getLog().info("bwresourceExport Mojo started execution");
    		 getLog().info("Loading property file -->" + propertyfile);
    		 getLog().info("update  profile -->" + profile);
    		 getLog().info(outputDirectory.getAbsolutePath());
    		 Properties prop = new Properties();
    		 
			if (new File(com.tibco.bw.maven.plugin.utils.FileUtilsProject.getApplicationMetaInf(projectBasedir).getAbsolutePath() + "/TIBCO.xml").exists()) {
				
				for (File file : com.tibco.bw.maven.plugin.utils.FileUtilsProject.getApplicationMetaInf(projectBasedir).listFiles()) {
					if (file.getName().equals(profile.toString())) {
						Document doc = loadTibcoXML(file);
						NodeList nList = doc.getElementsByTagName("globalVariable");
						for (int i = 0; i < nList.getLength(); ++i) {
							Element name = (Element) nList.item(i);

							String NameValueText = "";
							String ValueValueText = "";

							NameValueText = getNameNode(name);
							ValueValueText = getValueNode(name);
							prop.setProperty(NameValueText, ValueValueText);

						}
					}
				}
    			 
    		 }
    		 
    		 
			com.tibco.bw.maven.plugin.utils.FileUtilsProject.setSavePropertyOrder(prop,outputDirectory+"/"+propertyfile);
    		 
    		 
    		 
		} catch (Exception e1) {
			throw new MojoExecutionException("Failed to create BW EAR Archive ", e1);
		}
	}



	/**
	 * @param Element name
	 * @return String
	 */
	private String getValueNode(Element name) {
		NodeList ValueList = name.getElementsByTagName("value");
		String ValueValueText="";
		 //getLog().info(TagName.item(0).getNodeName()+"="+ TagName.item(0).getTextContent());
		 for (int k = 0; k < ValueList.getLength(); ++k)
		 {
//        					 Element value = (Element)TagName.item(j);
//        					 getLog().info( TagName.item(j).getNodeValue() +TagName.item(j).getTextContent());
		       Element ValueText = (Element) ValueList.item(k);
		       ValueValueText = ValueText.getTextContent();

		 }
		return ValueValueText;
	}
	
	/**
	 * @param Element name
	 * @return String
	 */
	private String getNameNode(Element name) {
		String ValueValueText="";
		NodeList ValueList = name.getElementsByTagName("name");
		 //getLog().info(TagName.item(0).getNodeName()+"="+ TagName.item(0).getTextContent());
		 for (int k = 0; k < ValueList.getLength(); ++k)
		 {
//        					 Element value = (Element)TagName.item(j);
//        					 getLog().info( TagName.item(j).getNodeValue() +TagName.item(j).getTextContent());
		       Element ValueText = (Element) ValueList.item(k);
		       ValueValueText = ValueText.getTextContent();

		 }
		return ValueValueText;
	}
	
	




    
//    /**
//     * Finds the JAR file for the Module.
//     * 
//     * @param target the Module Output directory which is the target directory.
//     * 
//     * @return the Module JAR.
//     * 
//     * @throws Exception
//     */
//	private File getModuleJar(File target) throws Exception {
//      File[] files = BWFileUtils.getFilesForType(target, ".jar");
//      files = BWFileUtils.sortFilesByDateDesc(files);
//      if(files.length == 0) {
//       	throw new Exception("Module is not built yet. Please check your Application PO for the Module entry.");
//      }
//      return files[0];
//	}



	/**
	 * Loads the TibcoXMLfile in a Document object (DOM)
	 * 
	 * @param file the TIBCO.xml file
	 * 
	 * @return the root Document object for the TIBCO.xml file
	 * 
	 * @throws Exception
	 */
	private Document loadTibcoXML(File file) throws Exception {
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		docFactory.setNamespaceAware(true);
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		Document doc = docBuilder.parse(file);
		getLog().debug("Loaded Tibco.xml file");
		return doc;
	}
	



}
