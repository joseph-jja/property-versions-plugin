package com.buildtools.maven.plugins;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import java.util.Arrays;

/* extends AbstractMojo */
/**
 * @phase generate-sources
 * @goal identify
 */
public class PropertyVersionPlugin extends AbstractMojo {

    /**
     * Base directory of the project.
     * 
     * @parameter expression="${basedir}"
     */
    private File baseDirectory;

    /**
     * Project version.
     * 
     * @parameter expression="${projectVersion}"
     */
    private String projectVersion;
    
    /**
     * Project version.
     * 
     * @parameter expression="${breakBuild}" default=false
     */
    private boolean breakBuild;

    /**
     * @parameter expression="${exclusionProperties}"
     */
    private String[] exclusionProperties;

    /**
     * The Maven Session Object
     * 
     * @parameter expression="${session}"
     * @required
     * @readonly
     */
    protected MavenSession session;

    public void execute() throws MojoExecutionException, MojoFailureException {

        if (session == null) {
            getLog().error("Could not run plugin session is null!");
        }

        if (session.getCurrentProject() == null) {
            getLog().error("Could not run plugin Current Project is null!");
        }

        List<String> exclusions = new ArrayList<String>();
        if (exclusionProperties != null && exclusionProperties.length > 0) {
            exclusions.addAll(Arrays.asList(exclusionProperties));
        }

        boolean isReleaseVersion = false;
        if (projectVersion != null && projectVersion.indexOf("SNAPSHOT") != -1) {
            isReleaseVersion = true;
        }
        
        getLog().info("Listing all properties and their values.");
        final Properties parentProperties = session.getCurrentProject().getProperties();
        Set<Map.Entry<Object, Object>> props = parentProperties.entrySet();
        for (Map.Entry<Object, Object> prop : props) {
            final String propertyName = prop.getKey().toString();
            final String propertyValue = prop.getValue().toString();
            if (exclusions == null || exclusions.isEmpty() || !exclusions.contains(propertyName)) {
                getLog().info("Property name: " + propertyName + " => property version: " + propertyValue);
            }
            if (isReleaseVersion && propertyValue.indexOf("SNAPSHOT") != -1 ) {
                    if ( breakBuild ) { 
                        throw new MojoFailureException("Bad mojo! Property " + propertyName + " has SNAPSHOT version number " + propertyValue + ".");
                    } else {
                        getLog().info("Bad mojo! Property " + propertyName + " has SNAPSHOT version number " + propertyValue + ".");
                    }
                
            }
        }
    }
}
