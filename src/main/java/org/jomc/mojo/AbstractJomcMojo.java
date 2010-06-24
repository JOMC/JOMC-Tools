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
import java.net.URL;
import java.net.URLClassLoader;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.jomc.model.modlet.DefaultModelProcessor;
import org.jomc.model.modlet.DefaultModelProvider;
import org.jomc.model.Module;
import org.jomc.model.Modules;
import org.jomc.modlet.DefaultModelContext;
import org.jomc.modlet.DefaultModletProvider;
import org.jomc.modlet.Model;
import org.jomc.modlet.ModelContext;
import org.jomc.modlet.ModelException;
import org.jomc.modlet.ModelValidationReport;
import org.jomc.tools.ClassFileProcessor;
import org.jomc.tools.SourceFileProcessor;
import org.jomc.tools.JomcTool;
import org.jomc.tools.ResourceFileProcessor;

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
     * Contols processing of source code files.
     *
     * @parameter expression="${jomc.sourceProcessing}" default-value="true"
     */
    private boolean sourceProcessingEnabled;

    /**
     * Contols processing of resource files.
     *
     * @parameter expression="${jomc.resourceProcessing}" default-value="true"
     */
    private boolean resourceProcessingEnabled;

    /**
     * Contols processing of class files.
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
     * Directory holding compiled class files of the project.
     *
     * @parameter default-value="${project.build.outputDirectory}"
     */
    private String classesDirectory;

    /**
     * Directory holding compiled test class files of the project.
     *
     * @parameter default-value="${project.build.testOutputDirectory}"
     */
    private String testClassesDirectory;

    /**
     * The Maven project of the instance.
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
    private List pluginArtifacts;

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

            this.logSeparator( Level.INFO );
            this.log( Level.INFO, getMessage( "title" ), null );
            this.logSeparator( Level.INFO );
            this.executeTool();
        }
        catch ( final Exception e )
        {
            throw new MojoExecutionException( e.getMessage(), e );
        }
        finally
        {
            DefaultModletProvider.setDefaultModletLocation( null );
            DefaultModelContext.setDefaultProviderLocation( null );
            DefaultModelContext.setDefaultPlatformProviderLocation( null );
            DefaultModelProvider.setDefaultModuleLocation( null );
            DefaultModelProcessor.setDefaultTransformerLocation( null );
            JomcTool.setDefaultTemplateProfile( null );
        }
    }

    /**
     * Executes this tool.
     *
     * @throws Exception if execution of this tool fais.
     */
    protected abstract void executeTool() throws Exception;

    /**
     * Gets the Maven project of the instance.
     *
     * @return The Maven project of the instance.
     *
     * @throws MojoExecutionException if getting the maven project of the instance fails.
     */
    protected MavenProject getMavenProject() throws MojoExecutionException
    {
        return this.mavenProject;
    }

    /**
     * Creates a new model context instance for a given class loader.
     *
     * @param classLoader The class loader to use for creating the context.
     *
     * @return A new model context instance for {@code classLoader}.
     *
     * @throws MojoExecutionException if creating the model context fails.
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
            throw new MojoExecutionException( e.getMessage(), e );
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
            final Collection<URL> urls = new LinkedList<URL>();

            for ( final Iterator it = this.getMainClasspathElements().iterator(); it.hasNext(); )
            {
                final String element = (String) it.next();
                final URL url = new File( element ).toURI().toURL();
                if ( !urls.contains( url ) )
                {
                    urls.add( url );
                    this.log( Level.FINE, getMessage( "classpathElement", url.toExternalForm() ), null );
                }
            }

            return new URLClassLoader( urls.toArray( new URL[ urls.size() ] ),
                                       Thread.currentThread().getContextClassLoader() );

        }
        catch ( final IOException e )
        {
            throw new MojoExecutionException( e.getMessage(), e );
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
            final Collection<URL> urls = new LinkedList<URL>();

            for ( final Iterator it = this.getTestClasspathElements().iterator(); it.hasNext(); )
            {
                final String element = (String) it.next();
                final URL url = new File( element ).toURI().toURL();
                if ( !urls.contains( url ) )
                {
                    urls.add( url );
                    this.log( Level.FINE, getMessage( "classpathElement", url.toExternalForm() ), null );
                }
            }

            return new URLClassLoader( urls.toArray( new URL[ urls.size() ] ),
                                       Thread.currentThread().getContextClassLoader() );

        }
        catch ( final IOException e )
        {
            throw new MojoExecutionException( e.getMessage(), e );
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
        final Set<String> elements = new HashSet<String>();

        File f = new File( this.classesDirectory );
        if ( !f.isAbsolute() )
        {
            f = new File( this.getMavenProject().getBasedir(), this.classesDirectory );
        }
        elements.add( f.getAbsolutePath() );

        for ( final Iterator it = this.getMavenProject().getRuntimeArtifacts().iterator(); it.hasNext(); )
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
                this.log( Level.FINE, getMessage(
                    "ignoredPluginArtifact", a.toString(), pluginArtifact.toString() ), null );

                continue;
            }

            final String element = a.getFile().getAbsolutePath();
            this.log( Level.FINE, getMessage( "runtimeElement", element ), null );
            elements.add( element );
        }

        for ( final Iterator it = this.getMavenProject().getCompileArtifacts().iterator(); it.hasNext(); )
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
                this.log( Level.FINE, getMessage(
                    "ignoredPluginArtifact", a.toString(), pluginArtifact.toString() ), null );

                continue;
            }

            final String element = a.getFile().getAbsolutePath();
            this.log( Level.FINE, getMessage( "compiletimeElement", element ), null );
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
        final Set<String> elements = new HashSet<String>();

        File f = new File( this.classesDirectory );
        if ( !f.isAbsolute() )
        {
            f = new File( this.getMavenProject().getBasedir(), this.classesDirectory );
        }
        elements.add( f.getAbsolutePath() );

        f = new File( this.testClassesDirectory );
        if ( !f.isAbsolute() )
        {
            f = new File( this.getMavenProject().getBasedir(), this.testClassesDirectory );
        }
        elements.add( f.getAbsolutePath() );

        for ( final Iterator it = this.getMavenProject().getTestArtifacts().iterator(); it.hasNext(); )
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
                this.log( Level.FINE, getMessage(
                    "ignoredPluginArtifact", a.toString(), pluginArtifact.toString() ), null );

                continue;
            }

            final String element = a.getFile().getAbsolutePath();
            this.log( Level.FINE, getMessage( "testElement", element ), null );
            elements.add( element );
        }

        return elements;
    }

    /**
     * Gets a flag indicating the processing of sources is enabled.
     *
     * @return {@code true} if processing of sources is enabled; {@code false} else.
     */
    protected boolean isSourceProcessingEnabled()
    {
        return this.sourceProcessingEnabled;
    }

    /**
     * Gets a flag indicating the processing of resources is enabled.
     *
     * @return {@code true} if processing of resources is enabled; {@code false} else.
     */
    protected boolean isResourceProcessingEnabled()
    {
        return this.resourceProcessingEnabled;
    }

    /**
     * Gets a flag indicating the processing of classes is enabled.
     *
     * @return {@code true} if processing of classes is enabled; {@code false} else.
     */
    protected boolean isClassProcessingEnabled()
    {
        return this.classProcessingEnabled;
    }

    /**
     * Gets a flag indicating the processing of models is enabled.
     *
     * @return {@code true} if processing of models is enabled; {@code false} else.
     */
    protected boolean isModelProcessingEnabled()
    {
        return this.modelProcessingEnabled;
    }

    /**
     * Gets a flag indicating model object classpath resolution is enabled.
     *
     * @return {@code true} if model object classpath resolution is enabled; {@code false} else.
     */
    protected boolean isModelObjectClasspathResolutionEnabled()
    {
        return this.modelObjectClasspathResolutionEnabled;
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

    protected Model getModel( final ModelContext context ) throws MojoExecutionException
    {
        try
        {
            Model model = context.findModel( Modules.MODEL_PUBLIC_ID );
            JAXBElement<Modules> modules = model.getAnyElement( Modules.MODEL_PUBLIC_ID, "modules" );

            if ( modules != null && this.isModelObjectClasspathResolutionEnabled() )
            {
                final Module classpathModule = modules.getValue().getClasspathModule(
                    Modules.getDefaultClasspathModuleName(), context.getClassLoader() );

                if ( classpathModule != null )
                {
                    modules.getValue().getModule().add( classpathModule );
                }
            }

            if ( this.isModelProcessingEnabled() )
            {
                model = context.processModel( model );
            }

            return model;
        }
        catch ( final ModelException e )
        {
            throw new MojoExecutionException( e.getMessage(), e );
        }
    }

    protected Modules getToolModules( final ModelContext context ) throws MojoExecutionException
    {
        final Model model = this.getModel( context );
        final JAXBElement<Modules> modules = model.getAnyElement( Modules.MODEL_PUBLIC_ID, "modules" );
        return modules != null ? modules.getValue() : new Modules();
    }

    protected void logSeparator( final Level level ) throws MojoExecutionException
    {
        this.log( level, getMessage( "separator" ), null );
    }

    protected void logProcessingModule( final String toolName, final String moduleName ) throws MojoExecutionException
    {
        this.log( Level.INFO, getMessage( "processingModule", toolName, moduleName ), null );
    }

    protected void logMissingModule( final String moduleName ) throws MojoExecutionException
    {
        this.log( Level.WARNING, getMessage( "missingModule", moduleName ), null );
    }

    protected void logToolSuccess( final String toolName ) throws MojoExecutionException
    {
        this.log( Level.INFO, getMessage( "toolSuccess", toolName ), null );
    }

    protected void log( final ModelContext context, final Level level, final ModelValidationReport report )
        throws MojoExecutionException
    {
        try
        {
            if ( !report.isModelValid() || !report.getDetails().isEmpty() )
            {
                this.logSeparator( level );
            }

            if ( !report.isModelValid() )
            {
                this.log( level, getMessage( "invalidModel" ), null );
            }

            if ( !report.getDetails().isEmpty() )
            {
                final Marshaller marshaller = context.createMarshaller( Modules.MODEL_PUBLIC_ID );
                marshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );

                for ( ModelValidationReport.Detail detail : report.getDetails() )
                {
                    this.log( detail.getLevel(), "o " + detail.getMessage(), null );

                    if ( detail.getElement() != null )
                    {
                        final StringWriter stringWriter = new StringWriter();
                        marshaller.marshal( detail.getElement(), stringWriter );
                        this.log( Level.FINE, stringWriter.toString(), null );
                    }
                }
            }

            if ( !report.isModelValid() || !report.getDetails().isEmpty() )
            {
                this.logSeparator( level );
            }
        }
        catch ( final ModelException e )
        {
            throw new MojoExecutionException( e.getMessage(), e );
        }
        catch ( final JAXBException e )
        {
            throw new MojoExecutionException( e.getMessage(), e );
        }
    }

    protected void log( final Level level, final String message, final Throwable throwable )
    {
        try
        {
            if ( level.intValue() < Level.INFO.intValue() || level.intValue() >= Level.WARNING.intValue()
                 || this.verbose )
            {
                String line;
                final BufferedReader reader = new BufferedReader( new StringReader( message == null ? "" : message ) );
                boolean throwableLogged = false;

                while ( ( line = reader.readLine() ) != null )
                {
                    final String mojoMessage = "[JOMC] " + line;

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

    protected void setupJomcTool( final ModelContext context, final JomcTool tool ) throws MojoExecutionException
    {
        if ( this.verbose || this.getLog().isDebugEnabled() )
        {
            tool.setLogLevel( this.getLog().isDebugEnabled() ? Level.ALL : Level.INFO );
        }

        tool.getListeners().add( new JomcTool.Listener()
        {

            public void onLog( final Level level, final String message, final Throwable t )
            {
                log( level, message, t );
            }

        } );

        tool.setTemplateEncoding( this.templateEncoding );
        tool.setInputEncoding( this.sourceEncoding );
        tool.setOutputEncoding( this.sourceEncoding );
        tool.setTemplateProfile( this.templateProfile );
        tool.setModules( this.getToolModules( context ) );

        if ( this.indentation != null )
        {
            tool.setIndentation( StringEscapeUtils.unescapeJava( this.indentation ) );
        }
        if ( this.lineSeparator != null )
        {
            tool.setLineSeparator( StringEscapeUtils.unescapeJava( this.lineSeparator ) );
        }
    }

    protected void setupModelContext( final ModelContext modelContext )
    {
        if ( this.verbose || this.getLog().isDebugEnabled() )
        {
            modelContext.setLogLevel( this.getLog().isDebugEnabled() ? Level.ALL : Level.INFO );
        }

        modelContext.getListeners().add( new ModelContext.Listener()
        {

            public void onLog( final Level level, final String message, final Throwable t )
            {
                log( level, message, t );
            }

        } );
    }

    private Artifact getPluginArtifact( final Artifact a )
    {
        for ( final Iterator it = this.pluginArtifacts.iterator(); it.hasNext(); )
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
        return MessageFormat.format( ResourceBundle.getBundle( AbstractJomcMojo.class.getName().replace( '.', '/' ) ).
            getString( key ), args );

    }

}
