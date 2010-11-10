/*
 *   Copyright (c) 2009 The JOMC Project
 *   Copyright (c) 2005 Christian Schulte <schulte2005@users.sourceforge.net>
 *   All rights reserved.
 *
 *   Redistribution and use in source and binary forms, with or without
 *   modification, are permitted provided that the following conditions
 *   are met:
 *
 *     o Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *
 *     o Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in
 *       the documentation and/or other materials provided with the
 *       distribution.
 *
 *   THIS SOFTWARE IS PROVIDED BY THE JOMC PROJECT AND CONTRIBUTORS "AS IS"
 *   AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 *   THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 *   PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE JOMC PROJECT OR
 *   CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 *   EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 *   PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 *   OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 *   WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 *   OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 *   ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *   $Id$
 *
 */
package org.jomc.mojo;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.MessageFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.ArtifactUtils;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.descriptor.MojoDescriptor;
import org.apache.maven.project.MavenProject;
import org.jomc.model.Module;
import org.jomc.model.Modules;
import org.jomc.model.modlet.DefaultModelProcessor;
import org.jomc.model.modlet.DefaultModelProvider;
import org.jomc.model.modlet.ModelHelper;
import org.jomc.modlet.DefaultModelContext;
import org.jomc.modlet.DefaultModletProvider;
import org.jomc.modlet.Model;
import org.jomc.modlet.ModelContext;
import org.jomc.modlet.ModelException;
import org.jomc.modlet.ModelValidationReport;
import org.jomc.tools.ClassFileProcessor;
import org.jomc.tools.JomcTool;
import org.jomc.tools.ResourceFileProcessor;
import org.jomc.tools.SourceFileProcessor;

/**
 * Base class for executing {@code JomcTool}s.
 *
 * @author <a href="mailto:schulte2005@users.sourceforge.net">Christian Schulte</a>
 * @version $Id$
 */
public abstract class AbstractJomcMojo extends AbstractMojo
{

    /**
     * The encoding to use for reading and writing files.
     *
     * @parameter default-value="${project.build.sourceEncoding}"
     */
    private String sourceEncoding;

    /**
     * The encoding to use for reading templates.
     *
     * @parameter
     */
    private String templateEncoding;

    /**
     * The template profile to use when accessing templates.
     *
     * @parameter
     */
    private String templateProfile;

    /**
     * The default template profile to use when accessing templates.
     *
     * @parameter
     */
    private String defaultTemplateProfile;

    /**
     * The location to search for providers.
     *
     * @parameter
     */
    private String providerLocation;

    /**
     * The location to search for platform providers.
     *
     * @parameter
     */
    private String platformProviderLocation;

    /**
     * The identifier of the model to process.
     *
     * @parameter default-value="http://jomc.org/model"
     */
    private String model;

    /**
     * The location to search for modlets.
     *
     * @parameter
     */
    private String modletLocation;

    /**
     * The location to search for modules.
     *
     * @parameter
     */
    private String moduleLocation;

    /**
     * The location to search for transformers.
     *
     * @parameter
     */
    private String transformerLocation;

    /**
     * The indentation string ('\t' for tab).
     *
     * @parameter
     */
    private String indentation;

    /**
     * The line separator ('\r\n' for DOS, '\r' for Mac, '\n' for Unix).
     *
     * @parameter
     */
    private String lineSeparator;

    /**
     * Controls verbosity of the plugin.
     *
     * @parameter expression="${jomc.verbose}" default-value="false"
     */
    private boolean verbose;

    /**
     * Controls processing of source code files.
     *
     * @parameter expression="${jomc.sourceProcessing}" default-value="true"
     */
    private boolean sourceProcessingEnabled;

    /**
     * Controls processing of resource files.
     *
     * @parameter expression="${jomc.resourceProcessing}" default-value="true"
     */
    private boolean resourceProcessingEnabled;

    /**
     * Controls processing of class files.
     *
     * @parameter expression="${jomc.classProcessing}" default-value="true"
     */
    private boolean classProcessingEnabled;

    /**
     * Controls processing of models.
     *
     * @parameter expression="${jomc.modelProcessing}" default-value="true"
     */
    private boolean modelProcessingEnabled;

    /**
     * Controls model object classpath resolution.
     *
     * @parameter expression="${jomc.modelObjectClasspathResolution}" default-value="true"
     */
    private boolean modelObjectClasspathResolutionEnabled;

    /**
     * Name of the module to process.
     *
     * @parameter default-value="${project.name}"
     */
    private String moduleName;

    /**
     * Name of the test module to process.
     *
     * @parameter default-value="${project.name} Tests"
     */
    private String testModuleName;

    /**
     * Directory holding the compiled class files of the project.
     * <strong>Replaced by 'outputDirectory' parameter.</strong>
     *
     * @parameter
     */
    @Deprecated
    private String classesDirectory;

    /**
     * Directory holding the compiled test class files of the project.
     * <strong>Replaced by 'testOutputDirectory' parameter.</strong>
     *
     * @parameter
     */
    @Deprecated
    private String testClassesDirectory;

    /**
     * Output directory of the project.
     *
     * @parameter default-value="${project.build.outputDirectory}" expression="${jomc.outputDirectory}"
     * @since 1.1
     */
    private String outputDirectory;

    /**
     * Test output directory of the project.
     *
     * @parameter default-value="${project.build.testOutputDirectory}" expression="${jomc.testOutputDirectory}"
     * @since 1.1
     */
    private String testOutputDirectory;

    /**
     * Directory holding the source files of the project.
     *
     * @parameter default-value="${project.build.sourceDirectory}" expression="${jomc.sourceDirectory}"
     * @since 1.1
     */
    private String sourceDirectory;

    /**
     * Directory holding the test source files of the project.
     *
     * @parameter default-value="${project.build.testSourceDirectory}" expression="${jomc.testSourceDirectory}"
     * @since 1.1
     */
    private String testSourceDirectory;

    /**
     * Directory holding the session related files of the project.
     *
     * @parameter default-value="${project.build.directory}/jomc-sessions" expression="${jomc.sessionDirectory}"
     * @since 1.1
     */
    private String sessionDirectory;

    /**
     * Directory holding the reports of the project.
     *
     * @parameter default-value="${project.reporting.outputDirectory}" expression="${jomc.reportOutputDirectory}"
     * @since 1.1
     */
    private String reportOutputDirectory;

    /**
     * The Maven project of the instance.
     *
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject mavenProject;

    /**
     * List of plugin artifacts.
     *
     * @parameter expression="${plugin.artifacts}"
     * @required
     * @readonly
     */
    private List<?> pluginArtifacts;

    /**
     * The Maven session of the instance.
     *
     * @parameter expression="${session}"
     * @required
     * @readonly
     * @since 1.1
     */
    private MavenSession mavenSession;

    /** Creates a new {@code AbstractJomcMojo} instance. */
    public AbstractJomcMojo()
    {
        super();
    }

    public void execute() throws MojoExecutionException, MojoFailureException
    {
        try
        {
            DefaultModletProvider.setDefaultModletLocation( this.modletLocation );
            DefaultModelContext.setDefaultProviderLocation( this.providerLocation );
            DefaultModelContext.setDefaultPlatformProviderLocation( this.platformProviderLocation );
            DefaultModelProvider.setDefaultModuleLocation( this.moduleLocation );
            DefaultModelProcessor.setDefaultTransformerLocation( this.transformerLocation );
            JomcTool.setDefaultTemplateProfile( this.defaultTemplateProfile );

            this.logSeparator();
            this.log( Level.INFO, getMessage( "title" ), null );

            if ( this.isExecutionPermitted() )
            {
                this.executeTool();
            }
            else
            {
                this.log( Level.INFO, getMessage( "executionSuppressed", this.getExecutionStrategy() ), null );
            }
        }
        catch ( final Exception e )
        {
            throw new MojoExecutionException( getMessage( e ), e );
        }
        finally
        {
            DefaultModletProvider.setDefaultModletLocation( null );
            DefaultModelContext.setDefaultProviderLocation( null );
            DefaultModelContext.setDefaultPlatformProviderLocation( null );
            DefaultModelProvider.setDefaultModuleLocation( null );
            DefaultModelProcessor.setDefaultTransformerLocation( null );
            JomcTool.setDefaultTemplateProfile( null );
            this.logSeparator();
        }
    }

    /**
     * Executes this tool.
     *
     * @throws Exception if execution of this tool fais.
     */
    protected abstract void executeTool() throws Exception;

    /**
     * Gets the goal of the instance.
     *
     * @return The goal of the instance.
     *
     * @throws MojoExecutionException if getting the goal of the instance fails.
     * @since 1.1
     */
    protected abstract String getGoal() throws MojoExecutionException;

    /**
     * Gets the execution strategy of the instance.
     *
     * @return The execution strategy of the instance.
     *
     * @throws MojoExecutionException if getting the execution strategy of the instance fails.
     * @since 1.1
     */
    protected abstract String getExecutionStrategy() throws MojoExecutionException;

    /**
     * Gets a flag indicating the current execution is permitted.
     *
     * @return {@code true} if the current execution is permitted; {@code false} if the current execution is suppressed.
     *
     * @throws MojoExecutionException if getting the flag fails.
     *
     * @since 1.1
     * @see #getGoal()
     * @see #getExecutionStrategy()
     */
    protected boolean isExecutionPermitted() throws MojoExecutionException
    {
        try
        {
            boolean permitted = true;

            if ( MojoDescriptor.SINGLE_PASS_EXEC_STRATEGY.equals( this.getExecutionStrategy() ) )
            {
                final File flagFile =
                    new File( this.getSessionDirectory(),
                              ArtifactUtils.versionlessKey( this.getMavenProject().getArtifact() ).hashCode()
                              + "-" + this.getGoal()
                              + "-" + this.getMavenSession().getStartTime().getTime() + ".flg" );

                if ( !this.getSessionDirectory().exists() && !this.getSessionDirectory().mkdirs() )
                {
                    throw new MojoExecutionException( getMessage( "failedCreatingDirectory",
                                                                  this.getSessionDirectory().getAbsolutePath() ) );

                }

                permitted = flagFile.createNewFile();
            }

            return permitted;
        }
        catch ( final IOException e )
        {
            throw new MojoExecutionException( getMessage( e ), e );
        }
    }

    /**
     * Gets the Maven project of the instance.
     *
     * @return The Maven project of the instance.
     *
     * @throws MojoExecutionException if getting the Maven project of the instance fails.
     */
    protected MavenProject getMavenProject() throws MojoExecutionException
    {
        return this.mavenProject;
    }

    /**
     * Gets the Maven session of the instance.
     *
     * @return The Maven session of the instance.
     *
     * @throws MojoExecutionException if getting the Maven session of the instance fails.
     *
     * @since 1.1
     */
    protected MavenSession getMavenSession() throws MojoExecutionException
    {
        return this.mavenSession;
    }

    /**
     * Gets an absolute {@code File} instance for a given name.
     * <p>This method constructs a new {@code File} instance using the given name. If the resulting file is not
     * absolute, the value of the {@code basedir} property of the current Maven project is prepended.</p>
     *
     * @param name The name to get an absolute {@code File} instance for.
     *
     * @return An absolute {@code File} instance constructed from {@code name}.
     *
     * @throws MojoExecutionException if getting an absolute {@code File} instance for {@code name} fails.
     * @throws NullPointerException if {@code name} is {@code null}.
     *
     * @since 1.1
     */
    protected File getAbsoluteFile( final String name ) throws MojoExecutionException
    {
        if ( name == null )
        {
            throw new NullPointerException( "name" );
        }

        File file = new File( name );
        if ( !file.isAbsolute() )
        {
            file = new File( this.getMavenProject().getBasedir(), name );
        }

        return file;
    }

    /**
     * Gets the directory holding the compiled class files of the project.
     *
     * @return The directory holding the compiled class files of the project.
     *
     * @throws MojoExecutionException if getting the directory fails.
     *
     * @since 1.1
     */
    protected File getOutputDirectory() throws MojoExecutionException
    {
        if ( this.classesDirectory != null )
        {
            this.log( Level.WARNING, getMessage( "deprecationWarning", "classesDirectory", "outputDirectory" ), null );

            if ( !this.classesDirectory.equals( this.outputDirectory ) )
            {
                this.log( Level.WARNING, getMessage( "ignoringParameter", "outputDirectory" ), null );
                this.outputDirectory = this.classesDirectory;
            }

            this.classesDirectory = null;
        }

        final File dir = this.getAbsoluteFile( this.outputDirectory );
        if ( !dir.exists() && !dir.mkdirs() )
        {
            throw new MojoExecutionException( getMessage( "failedCreatingDirectory", dir.getAbsolutePath() ) );
        }

        return dir;
    }

    /**
     * Gets the directory holding the compiled test class files of the project.
     *
     * @return The directory holding the compiled test class files of the project.
     *
     * @throws MojoExecutionException if getting the directory fails.
     *
     * @since 1.1
     */
    protected File getTestOutputDirectory() throws MojoExecutionException
    {
        if ( this.testClassesDirectory != null )
        {
            this.log( Level.WARNING, getMessage( "deprecationWarning", "testClassesDirectory",
                                                 "testOutputDirectory" ), null );

            if ( !this.testClassesDirectory.equals( this.testOutputDirectory ) )
            {
                this.log( Level.WARNING, getMessage( "ignoringParameter", "testOutputDirectory" ), null );
                this.testOutputDirectory = this.testClassesDirectory;
            }

            this.testClassesDirectory = null;
        }

        final File dir = this.getAbsoluteFile( this.testOutputDirectory );
        if ( !dir.exists() && !dir.mkdirs() )
        {
            throw new MojoExecutionException( getMessage( "failedCreatingDirectory", dir.getAbsolutePath() ) );
        }

        return dir;
    }

    /**
     * Gets the directory holding the source files of the project.
     *
     * @return The directory holding the source files of the project.
     *
     * @throws MojoExecutionException if getting the directory fails.
     *
     * @since 1.1
     */
    protected File getSourceDirectory() throws MojoExecutionException
    {
        return this.getAbsoluteFile( this.sourceDirectory );
    }

    /**
     * Gets the directory holding the test source files of the project.
     *
     * @return The directory holding the test source files of the project.
     *
     * @throws MojoExecutionException if getting the directory fails.
     *
     * @since 1.1
     */
    protected File getTestSourceDirectory() throws MojoExecutionException
    {
        return this.getAbsoluteFile( this.testSourceDirectory );
    }

    /**
     * Gets the directory holding the session related files of the project.
     *
     * @return The directory holding the session related files of the project.
     *
     * @throws MojoExecutionException if getting the directory fails.
     *
     * @since 1.1
     */
    protected File getSessionDirectory() throws MojoExecutionException
    {
        return this.getAbsoluteFile( this.sessionDirectory );
    }

    /**
     * Gets the directory holding the reports of the project.
     *
     * @return The directory holding the reports of the project.
     *
     * @throws MojoExecutionException if getting the directory fails.
     *
     * @since 1.1
     */
    protected File getReportOutputDirectory() throws MojoExecutionException
    {
        return this.getAbsoluteFile( this.reportOutputDirectory );
    }

    /**
     * Gets the project's runtime class loader of the instance.
     *
     * @return The project's runtime class loader of the instance.
     *
     * @throws MojoExecutionException if getting the class loader fails.
     */
    protected ClassLoader getMainClassLoader() throws MojoExecutionException
    {
        try
        {
            final Set<String> mainClasspathElements = this.getMainClasspathElements();
            final Set<URI> uris = new HashSet<URI>( mainClasspathElements.size() );

            for ( String element : mainClasspathElements )
            {
                final URI uri = new File( element ).toURI();
                if ( !uris.contains( uri ) )
                {
                    uris.add( uri );
                }
            }

            this.log( Level.FINEST, getMessage( "mainClasspathInfo" ), null );

            int i = 0;
            final URL[] urls = new URL[ uris.size() ];
            for ( URI uri : uris )
            {
                urls[i] = uri.toURL();
                this.log( Level.FINEST, "\t" + urls[i++].toExternalForm(), null );
            }

            return new URLClassLoader( urls, Thread.currentThread().getContextClassLoader() );
        }
        catch ( final IOException e )
        {
            throw new MojoExecutionException( getMessage( e ), e );
        }
    }

    /**
     * Gets the project's test class loader of the instance.
     *
     * @return The project's test class loader of the instance.
     *
     * @throws MojoExecutionException if getting the class loader fails.
     */
    protected ClassLoader getTestClassLoader() throws MojoExecutionException
    {
        try
        {
            final Set<String> testClasspathElements = this.getTestClasspathElements();
            final Set<URI> uris = new HashSet<URI>( testClasspathElements.size() );

            for ( String element : testClasspathElements )
            {
                final URI uri = new File( element ).toURI();
                if ( !uris.contains( uri ) )
                {
                    uris.add( uri );
                }
            }

            this.log( Level.FINEST, getMessage( "testClasspathInfo" ), null );

            int i = 0;
            final URL[] urls = new URL[ uris.size() ];
            for ( URI uri : uris )
            {
                urls[i] = uri.toURL();
                this.log( Level.FINEST, "\t" + urls[i++].toExternalForm(), null );
            }

            return new URLClassLoader( urls, Thread.currentThread().getContextClassLoader() );
        }
        catch ( final IOException e )
        {
            throw new MojoExecutionException( getMessage( e ), e );
        }
    }

    /**
     * Gets the project's runtime classpath elements.
     *
     * @return A set of classpath element strings.
     *
     * @throws MojoExecutionException if getting the classpath elements fails.
     */
    protected Set<String> getMainClasspathElements() throws MojoExecutionException
    {
        final List<?> runtimeArtifacts = this.getMavenProject().getRuntimeArtifacts();
        final List<?> compileArtifacts = this.getMavenProject().getCompileArtifacts();
        final Set<String> elements = new HashSet<String>( runtimeArtifacts.size() + compileArtifacts.size() + 1 );
        elements.add( this.getOutputDirectory().getAbsolutePath() );

        for ( final Iterator<?> it = runtimeArtifacts.iterator(); it.hasNext(); )
        {
            final Artifact a = (Artifact) it.next();
            final Artifact pluginArtifact = this.getPluginArtifact( a );

            if ( a.getFile() == null )
            {
                this.log( Level.WARNING, getMessage( "ignored", a.toString() ), null );
                continue;
            }

            if ( pluginArtifact != null )
            {
                this.log( Level.FINER,
                          getMessage( "ignoringPluginArtifact", a.toString(), pluginArtifact.toString() ), null );

                continue;
            }

            final String element = a.getFile().getAbsolutePath();
            elements.add( element );
        }

        for ( final Iterator<?> it = compileArtifacts.iterator(); it.hasNext(); )
        {
            final Artifact a = (Artifact) it.next();
            final Artifact pluginArtifact = this.getPluginArtifact( a );

            if ( a.getFile() == null )
            {
                this.log( Level.WARNING, getMessage( "ignored", a.toString() ), null );
                continue;
            }

            if ( pluginArtifact != null )
            {
                this.log( Level.FINER,
                          getMessage( "ignoringPluginArtifact", a.toString(), pluginArtifact.toString() ), null );

                continue;
            }

            final String element = a.getFile().getAbsolutePath();
            elements.add( element );
        }

        return elements;
    }

    /**
     * Gets the project's test classpath elements.
     *
     * @return A set of classpath element strings.
     *
     * @throws MojoExecutionException if getting the classpath elements fails.
     */
    protected Set<String> getTestClasspathElements() throws MojoExecutionException
    {
        final List<?> testArtifacts = this.getMavenProject().getTestArtifacts();
        final Set<String> elements = new HashSet<String>( testArtifacts.size() + 2 );
        elements.add( this.getOutputDirectory().getAbsolutePath() );
        elements.add( this.getTestOutputDirectory().getAbsolutePath() );

        for ( final Iterator<?> it = testArtifacts.iterator(); it.hasNext(); )
        {
            final Artifact a = (Artifact) it.next();
            final Artifact pluginArtifact = this.getPluginArtifact( a );

            if ( a.getFile() == null )
            {
                this.log( Level.WARNING, getMessage( "ignored", a.toString() ), null );
                continue;
            }

            if ( pluginArtifact != null )
            {
                this.log( Level.FINER,
                          getMessage( "ignoringPluginArtifact", a.toString(), pluginArtifact.toString() ), null );

                continue;
            }

            final String element = a.getFile().getAbsolutePath();
            elements.add( element );
        }

        return elements;
    }

    /**
     * Gets a flag indicating verbose output is enabled.
     *
     * @return {@code true} if verbose output is enabled; {@code false} if information messages are suppressed.
     *
     * @throws MojoExecutionException if getting the flag fails.
     *
     * @since 1.1
     */
    protected final boolean isVerbose() throws MojoExecutionException
    {
        return this.verbose;
    }

    /**
     * Gets a flag indicating verbose output is enabled.
     *
     * @param value {@code true} if verbose output is enabled; {@code false} if information messages are suppressed.
     *
     * @throws MojoExecutionException if setting the flag fails.
     *
     * @since 1.1
     */
    protected final void setVerbose( final boolean value ) throws MojoExecutionException
    {
        this.verbose = value;
    }

    /**
     * Gets a flag indicating the processing of sources is enabled.
     *
     * @return {@code true} if processing of sources is enabled; {@code false} else.
     *
     * @throws MojoExecutionException if getting the flag fails.
     */
    protected final boolean isSourceProcessingEnabled() throws MojoExecutionException
    {
        return this.sourceProcessingEnabled;
    }

    /**
     * Sets the flag indicating the processing of sources is enabled.
     *
     * @param value {@code true} to enable processing of sources; {@code false} to disable processing of sources.
     *
     * @throws MojoExecutionException if setting the flag fails.
     *
     * @since 1.1
     */
    protected final void setSourceProcessingEnabled( final boolean value ) throws MojoExecutionException
    {
        this.sourceProcessingEnabled = value;
    }

    /**
     * Gets a flag indicating the processing of resources is enabled.
     *
     * @return {@code true} if processing of resources is enabled; {@code false} else.
     *
     * @throws MojoExecutionException if getting the flag fails.
     */
    protected final boolean isResourceProcessingEnabled() throws MojoExecutionException
    {
        return this.resourceProcessingEnabled;
    }

    /**
     * Sets the flag indicating the processing of resources is enabled.
     *
     * @param value {@code true} to enable processing of resources; {@code false} to disable processing of resources.
     *
     * @throws MojoExecutionException if setting the flag fails.
     *
     * @since 1.1
     */
    protected final void setResourceProcessingEnabled( final boolean value ) throws MojoExecutionException
    {
        this.resourceProcessingEnabled = value;
    }

    /**
     * Gets a flag indicating the processing of classes is enabled.
     *
     * @return {@code true} if processing of classes is enabled; {@code false} else.
     *
     * @throws MojoExecutionException if getting the flag fails.
     */
    protected final boolean isClassProcessingEnabled() throws MojoExecutionException
    {
        return this.classProcessingEnabled;
    }

    /**
     * Sets the flag indicating the processing of classes is enabled.
     *
     * @param value {@code true} to enable processing of classes; {@code false} to disable processing of classes.
     *
     * @throws MojoExecutionException if setting the flag fails.
     *
     * @since 1.1
     */
    protected final void setClassProcessingEnabled( final boolean value ) throws MojoExecutionException
    {
        this.classProcessingEnabled = value;
    }

    /**
     * Gets a flag indicating the processing of models is enabled.
     *
     * @return {@code true} if processing of models is enabled; {@code false} else.
     *
     * @throws MojoExecutionException if getting the flag fails.
     */
    protected final boolean isModelProcessingEnabled() throws MojoExecutionException
    {
        return this.modelProcessingEnabled;
    }

    /**
     * Sets the flag indicating the processing of models is enabled.
     *
     * @param value {@code true} to enable processing of models; {@code false} to disable processing of models.
     *
     * @throws MojoExecutionException if setting the flag fails.
     *
     * @since 1.1
     */
    protected final void setModelProcessingEnabled( final boolean value ) throws MojoExecutionException
    {
        this.modelProcessingEnabled = value;
    }

    /**
     * Gets a flag indicating model object classpath resolution is enabled.
     *
     * @return {@code true} if model object classpath resolution is enabled; {@code false} else.
     *
     * @throws MojoExecutionException if getting the flag fails.
     */
    protected final boolean isModelObjectClasspathResolutionEnabled() throws MojoExecutionException
    {
        return this.modelObjectClasspathResolutionEnabled;
    }

    /**
     * Sets the flag indicating model object classpath resolution is enabled.
     *
     * @param value {@code true} to enable model object classpath resolution; {@code false} to disable model object
     * classpath resolution.
     *
     * @throws MojoExecutionException if setting the flag fails.
     *
     * @since 1.1
     */
    protected final void setModelObjectClasspathResolutionEnabled( final boolean value ) throws MojoExecutionException
    {
        this.modelObjectClasspathResolutionEnabled = value;
    }

    /**
     * Gets the identifier of the model to process.
     *
     * @return The identifier of the model to process.
     *
     * @throws MojoExecutionException if getting the identifier fails.
     */
    protected String getModel() throws MojoExecutionException
    {
        return this.model;
    }

    /**
     * Gets the name of the module to process.
     *
     * @return The name of the module to process.
     *
     * @throws MojoExecutionException if getting the name of the module fails.
     */
    protected String getModuleName() throws MojoExecutionException
    {
        return this.moduleName;
    }

    /**
     * Gets the name of the test module to process.
     *
     * @return The name of the test module to process.
     *
     * @throws MojoExecutionException if getting the name of the test module fails.
     */
    protected String getTestModuleName() throws MojoExecutionException
    {
        return this.testModuleName;
    }

    /**
     * Gets the model to process.
     *
     * @param context The model context to get the model to process with.
     *
     * @return The model to process.
     *
     * @throws NullPointerException if {@code context} is {@code null}.
     * @throws MojoExecutionException if getting the model fails.
     */
    protected Model getModel( final ModelContext context ) throws MojoExecutionException
    {
        if ( context == null )
        {
            throw new NullPointerException( "context" );
        }

        try
        {
            Model m = context.findModel( this.getModel() );
            final Modules modules = ModelHelper.getModules( m );

            if ( modules != null && this.isModelObjectClasspathResolutionEnabled() )
            {
                final Module classpathModule =
                    modules.getClasspathModule( Modules.getDefaultClasspathModuleName(), context.getClassLoader() );

                if ( classpathModule != null )
                {
                    modules.getModule().add( classpathModule );
                }
            }

            if ( this.isModelProcessingEnabled() )
            {
                m = context.processModel( m );
            }

            return m;
        }
        catch ( final ModelException e )
        {
            throw new MojoExecutionException( getMessage( e ), e );
        }
    }

    /**
     * Creates a new model context instance for a given class loader.
     *
     * @param classLoader The class loader to use for creating the context.
     *
     * @return A new model context instance for {@code classLoader}.
     *
     * @throws MojoExecutionException if creating the model context fails.
     *
     * @see #setupModelContext(org.jomc.modlet.ModelContext)
     */
    protected ModelContext createModelContext( final ClassLoader classLoader ) throws MojoExecutionException
    {
        try
        {
            final ModelContext context = ModelContext.createModelContext( classLoader );
            this.setupModelContext( context );
            return context;
        }
        catch ( final ModelException e )
        {
            throw new MojoExecutionException( getMessage( e ), e );
        }
    }

    /**
     * Creates a new tool instance for managing source files.
     *
     * @param context The context of the tool.
     *
     * @return A new tool instance for managing source files.
     *
     * @throws NullPointerException if {@code context} is {@code null}.
     * @throws MojoExecutionException if getting the tool of the instance fails.
     *
     * @see #setupJomcTool(org.jomc.modlet.ModelContext, org.jomc.tools.JomcTool)
     */
    protected SourceFileProcessor createSourceFileProcessor( final ModelContext context ) throws MojoExecutionException
    {
        if ( context == null )
        {
            throw new NullPointerException( "context" );
        }

        final SourceFileProcessor tool = new SourceFileProcessor();
        this.setupJomcTool( context, tool );
        return tool;
    }

    /**
     * Creates a new tool instance for managing resource files.
     *
     * @param context The context of the tool.
     *
     * @return A new tool instance for managing resource files.
     *
     * @throws NullPointerException if {@code context} is {@code null}.
     * @throws MojoExecutionException if getting the tool of the instance fails.
     *
     * @see #setupJomcTool(org.jomc.modlet.ModelContext, org.jomc.tools.JomcTool)
     */
    protected ResourceFileProcessor createResourceFileProcessor( final ModelContext context )
        throws MojoExecutionException
    {
        if ( context == null )
        {
            throw new NullPointerException( "context" );
        }

        final ResourceFileProcessor tool = new ResourceFileProcessor();
        this.setupJomcTool( context, tool );
        return tool;
    }

    /**
     * Creates a new tool instance for managing class files.
     *
     * @param context The context of the tool.
     *
     * @return A new tool instance for managing class files.
     *
     * @throws NullPointerException if {@code context} is {@code null}.
     * @throws MojoExecutionException if getting the tool of the instance fails.
     *
     * @see #setupJomcTool(org.jomc.modlet.ModelContext, org.jomc.tools.JomcTool)
     */
    protected ClassFileProcessor createClassFileProcessor( final ModelContext context ) throws MojoExecutionException
    {
        if ( context == null )
        {
            throw new NullPointerException( "context" );
        }

        final ClassFileProcessor tool = new ClassFileProcessor();
        this.setupJomcTool( context, tool );
        return tool;
    }

    /**
     * Creates a new {@code Transformer} from a given {@code Source}.
     *
     * @param source The source to initialize the transformer with.
     *
     * @return A {@code Transformer} backed by {@code source}.
     *
     * @throws NullPointerException if {@code source} is {@code null}.
     * @throws TransformerConfigurationException if creating a transformer fails.
     *
     * @since 1.2
     */
    protected Transformer createTransformer( final Source source ) throws TransformerConfigurationException
    {
        if ( source == null )
        {
            throw new NullPointerException( "source" );
        }

        final ErrorListener errorListener = new ErrorListener()
        {

            public void warning( final TransformerException exception ) throws TransformerException
            {
                try
                {
                    log( Level.WARNING, getMessage( exception ), exception );
                }
                catch ( final MojoExecutionException e )
                {
                    getLog().warn( exception );
                    getLog().error( e );
                }
            }

            public void error( final TransformerException exception ) throws TransformerException
            {
                try
                {
                    log( Level.SEVERE, getMessage( exception ), exception );
                }
                catch ( final MojoExecutionException e )
                {
                    getLog().error( exception );
                    getLog().error( e );
                }

                throw exception;
            }

            public void fatalError( final TransformerException exception ) throws TransformerException
            {
                try
                {
                    log( Level.SEVERE, getMessage( exception ), exception );
                }
                catch ( final MojoExecutionException e )
                {
                    getLog().error( exception );
                    getLog().error( e );
                }

                throw exception;
            }

        };

        final TransformerFactory transformerFactory = TransformerFactory.newInstance();
        transformerFactory.setErrorListener( errorListener );
        final Transformer transformer = transformerFactory.newTransformer( source );
        transformer.setErrorListener( errorListener );

        for ( Map.Entry<Object, Object> e : System.getProperties().entrySet() )
        {
            transformer.setParameter( e.getKey().toString(), e.getValue() );
        }

        return transformer;
    }

    /**
     * Logs a separator at a given level.
     *
     * @param level The level to log a separator at.
     *
     * @throws MojoExecutionException if logging fails.
     *
     * @deprecated Replaced by {@link #logSeparator()}.
     */
    @Deprecated
    protected void logSeparator( final Level level ) throws MojoExecutionException
    {
        this.logSeparator();
    }

    /**
     * Logs a separator.
     *
     * @throws MojoExecutionException if logging fails.
     *
     * @since 1.1
     */
    protected void logSeparator() throws MojoExecutionException
    {
        this.log( Level.INFO, getMessage( "separator" ), null );
    }

    /**
     * Logs a message stating a tool is starting to process a module.
     *
     * @param toolName The tool starting execution.
     * @param module The module getting processed.
     *
     * @throws MojoExecutionException if logging fails.
     */
    protected void logProcessingModule( final String toolName, final String module ) throws MojoExecutionException
    {
        this.log( Level.INFO, getMessage( "processingModule", toolName, module ), null );
    }

    /**
     * Logs a message stating a tool is starting to process a model.
     *
     * @param toolName The tool starting execution.
     * @param model The model getting processed.
     *
     * @throws MojoExecutionException if logging fails.
     *
     * @since 1.1
     */
    protected void logProcessingModel( final String toolName, final String model ) throws MojoExecutionException
    {
        this.log( Level.INFO, getMessage( "processingModel", toolName, model ), null );
    }

    /**
     * Logs a message stating that a module has not been found.
     *
     * @param module The module which has not been found.
     *
     * @throws MojoExecutionException if logging fails.
     */
    protected void logMissingModule( final String module ) throws MojoExecutionException
    {
        this.log( Level.WARNING, getMessage( "missingModule", module ), null );
    }

    /**
     * Logs a message stating that a tool successfully completed execution.
     *
     * @param toolName The name of the tool.
     *
     * @throws MojoExecutionException if logging fails.
     */
    protected void logToolSuccess( final String toolName ) throws MojoExecutionException
    {
        this.log( Level.INFO, getMessage( "toolSuccess", toolName ), null );
    }

    protected void log( final ModelContext context, final Level level, final ModelValidationReport report )
        throws MojoExecutionException
    {
        try
        {
            if ( !report.getDetails().isEmpty() )
            {
                this.logSeparator();
                Marshaller marshaller = null;

                for ( ModelValidationReport.Detail detail : report.getDetails() )
                {
                    this.log( detail.getLevel(), "o " + detail.getMessage(), null );

                    if ( detail.getElement() != null )
                    {
                        if ( marshaller == null )
                        {
                            marshaller = context.createMarshaller( this.getModel() );
                            marshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
                        }

                        final StringWriter stringWriter = new StringWriter();
                        marshaller.marshal( detail.getElement(), stringWriter );
                        this.log( Level.FINEST, stringWriter.toString(), null );
                    }
                }
            }
        }
        catch ( final ModelException e )
        {
            throw new MojoExecutionException( getMessage( e ), e );
        }
        catch ( final JAXBException e )
        {
            String message = getMessage( e );
            if ( message == null && e.getLinkedException() != null )
            {
                message = getMessage( e.getLinkedException() );
            }

            throw new MojoExecutionException( message, e );
        }
    }

    protected void log( final Level level, final String message, final Throwable throwable )
        throws MojoExecutionException
    {
        try
        {
            if ( this.getLog().isDebugEnabled()
                 || level.intValue() >= ( this.isVerbose() ? Level.INFO.intValue() : Level.WARNING.intValue() ) )
            {
                String line;
                final BufferedReader reader = new BufferedReader( new StringReader( message == null ? "" : message ) );
                boolean throwableLogged = false;

                while ( ( line = reader.readLine() ) != null )
                {
                    final String mojoMessage =
                        getMessage( this.getLog().isDebugEnabled() ? "debugMessage" : "logMessage", line,
                                    Thread.currentThread().getName(), new Date( System.currentTimeMillis() ) );

                    if ( ( level.equals( Level.CONFIG ) || level.equals( Level.FINE ) || level.equals( Level.FINER )
                           || level.equals( Level.FINEST ) ) && this.getLog().isDebugEnabled() )
                    {
                        this.getLog().debug( mojoMessage, throwableLogged ? null : throwable );
                    }
                    else if ( level.equals( Level.INFO ) && this.getLog().isInfoEnabled() )
                    {
                        this.getLog().info( mojoMessage, throwableLogged ? null : throwable );
                    }
                    else if ( level.equals( Level.SEVERE ) && this.getLog().isErrorEnabled() )
                    {
                        this.getLog().error( mojoMessage, throwableLogged ? null : throwable );
                    }
                    else if ( level.equals( Level.WARNING ) && this.getLog().isWarnEnabled() )
                    {
                        this.getLog().warn( mojoMessage, throwableLogged ? null : throwable );
                    }
                    else if ( this.getLog().isDebugEnabled() )
                    {
                        this.getLog().debug( mojoMessage, throwableLogged ? null : throwable );
                    }

                    throwableLogged = true;
                }
            }
        }
        catch ( final IOException e )
        {
            this.getLog().error( e );
            throw new AssertionError( e );
        }
    }

    /**
     * Configures a {@code ModelContext} instance.
     *
     * @param context The model context to configure.
     *
     * @throws NullPointerException if {@code context} is {@code null}.
     * @throws MojoExecutionException if configuring {@code context} fails.
     */
    protected void setupModelContext( final ModelContext context ) throws MojoExecutionException
    {
        if ( context == null )
        {
            throw new NullPointerException( "context" );
        }

        if ( this.isVerbose() || this.getLog().isDebugEnabled() )
        {
            context.setLogLevel( this.getLog().isDebugEnabled() ? Level.ALL : Level.INFO );
        }

        context.getListeners().add( new ModelContext.Listener()
        {

            public void onLog( final Level level, final String message, final Throwable t )
            {
                try
                {
                    log( level, message, t );
                }
                catch ( final MojoExecutionException e )
                {
                    getLog().error( e );
                }
            }

        } );
    }

    /**
     * Configures a {@code JomcTool} instance.
     *
     * @param context The model context to use for configuring {@code tool}.
     * @param tool The tool to configure.
     *
     * @throws NullPointerException if {@code context} of {@code tool} is {@code null}.
     * @throws MojoExecutionException if configuring {@code tool} fails.
     */
    protected void setupJomcTool( final ModelContext context, final JomcTool tool ) throws MojoExecutionException
    {
        if ( context == null )
        {
            throw new NullPointerException( "context" );
        }
        if ( tool == null )
        {
            throw new NullPointerException( "tool" );
        }

        if ( this.isVerbose() || this.getLog().isDebugEnabled() )
        {
            tool.setLogLevel( this.getLog().isDebugEnabled() ? Level.ALL : Level.INFO );
        }

        tool.getListeners().add( new JomcTool.Listener()
        {

            public void onLog( final Level level, final String message, final Throwable t )
            {
                try
                {
                    log( level, message, t );
                }
                catch ( final MojoExecutionException e )
                {
                    getLog().error( e );
                }
            }

        } );

        tool.setTemplateEncoding( this.templateEncoding );
        tool.setInputEncoding( this.sourceEncoding );
        tool.setOutputEncoding( this.sourceEncoding );
        tool.setTemplateProfile( this.templateProfile );
        tool.setModel( this.getModel( context ) );

        if ( this.indentation != null )
        {
            tool.setIndentation( StringEscapeUtils.unescapeJava( this.indentation ) );
        }
        if ( this.lineSeparator != null )
        {
            tool.setLineSeparator( StringEscapeUtils.unescapeJava( this.lineSeparator ) );
        }
    }

    private Artifact getPluginArtifact( final Artifact a )
    {
        for ( final Iterator<?> it = this.pluginArtifacts.iterator(); it.hasNext(); )
        {
            final Artifact pluginArtifact = (Artifact) it.next();

            if ( pluginArtifact.getGroupId().equals( a.getGroupId() )
                 && pluginArtifact.getArtifactId().equals( a.getArtifactId() )
                 && ( pluginArtifact.hasClassifier()
                      ? pluginArtifact.getClassifier().equals( a.getClassifier() )
                      : !a.hasClassifier() ) )
            {
                return pluginArtifact;
            }
        }

        return null;
    }

    private static String getMessage( final String key, final Object... args )
    {
        return MessageFormat.format( ResourceBundle.getBundle(
            AbstractJomcMojo.class.getName().replace( '.', '/' ) ).getString( key ), args );

    }

    private static String getMessage( final Throwable t )
    {
        return t != null ? t.getMessage() != null ? t.getMessage() : getMessage( t.getCause() ) : null;
    }

}
