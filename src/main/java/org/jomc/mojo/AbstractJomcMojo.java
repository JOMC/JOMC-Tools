/*
 *   Copyright (c) 2009 The JOMC Project
 *   Copyright (c) 2005 Christian Schulte <cs@jomc.org>
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
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.jomc.model.DefaultModelProvider;
import org.jomc.model.ModelContext;
import org.jomc.model.ModelException;
import org.jomc.model.ModelValidationReport;
import org.jomc.model.Module;
import org.jomc.model.Modules;
import org.jomc.tools.JavaClasses;
import org.jomc.tools.JavaSources;
import org.jomc.tools.JomcTool;

/**
 * Base mojo class for executing {@code JomcTool}s.
 *
 * @author <a href="mailto:cs@jomc.org">Christian Schulte</a>
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
     * @parameter default-value="default"
     */
    private String templateProfile;

    /**
     * The location to search for modules.
     *
     * @parameter
     */
    private String moduleLocation;

    /**
     * The Maven project of the instance.
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject mavenProject;

    /**
     * Controls verbosity of the plugin.
     *
     * @parameter expression="${jomc.verbose}" default-value="false"
     */
    private boolean verbose;

    /**
     * Contols processing of java sources.
     *
     * @parameter expression="${jomc.javaSources.enabled}" default-value="true"
     */
    private boolean javaSourceProcessingEnabled;

    /**
     * Contols processing of java classes.
     *
     * @parameter expression="${jomc.javaClasses.enabled}" default-value="true"
     */
    private boolean javaClassProcessingEnabled;

    /** The tool for managing sources. */
    private JavaSources javaSourcesTool;

    /** The tool for managing classes. */
    private JavaClasses javaClassesTool;

    /** The class loader of the project's runtime classpath including any provided dependencies. */
    private ClassLoader mainClassLoader;

    /** The class loader of the project's test classpath including any provided dependencies. */
    private ClassLoader testClassLoader;

    public void execute() throws MojoExecutionException, MojoFailureException
    {
        try
        {
            this.logSeparator( Level.INFO );
            this.log( Level.INFO, this.getMessage( "title" ).format( null ), null );
            this.logSeparator( Level.INFO );
            this.executeTool();
        }
        catch ( final Exception e )
        {
            throw new MojoExecutionException( e.getMessage(), e );
        }
    }

    /**
     * Gets the name of this tool.
     *
     * @return The name of this tool.
     * @throws MojoExecutionException if getting the name of this tool fails.
     */
    protected abstract String getToolName() throws MojoExecutionException;

    /**
     * Gets the class loader of this tool.
     *
     * @return The class loader of this tool.
     *
     * @throws MojoExecutionException if getting the class loader fails.
     */
    protected abstract ClassLoader getToolClassLoader() throws MojoExecutionException;

    /**
     * Executes this tool.
     *
     * @throws Exception if execution of this tool fais.
     */
    protected abstract void executeTool() throws Exception;

    /**
     * Gets the model context of the instance.
     *
     * @return The model context of the instance.
     *
     * @throws MojoExecutionException if getting the model context of the instance fails.
     */
    protected ModelContext getModelContext() throws MojoExecutionException
    {
        try
        {
            final ModelContext context = ModelContext.createModelContext( this.getToolClassLoader() );
            this.setupModelContext( context );
            return context;
        }
        catch ( final ModelException e )
        {
            throw new MojoExecutionException( e.getMessage(), e );
        }
    }

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
     * Gets the tool for managing sources of the instance.
     *
     * @return The tool for managing sources of the instance.
     *
     * @throws MojoExecutionException if getting the tool of the instance fails.
     */
    protected JavaSources getJavaSourcesTool() throws MojoExecutionException
    {
        if ( this.javaSourcesTool == null )
        {
            this.javaSourcesTool = new JavaSources();
            this.setupJomcTool( this.javaSourcesTool );
        }

        return this.javaSourcesTool;
    }

    /**
     * Gets the tool for managing classes of the instance.
     *
     * @return The tool for managing classes of the instance.
     *
     * @throws MojoExecutionException if getting the tool of the instance fails.
     */
    protected JavaClasses getJavaClassesTool() throws MojoExecutionException
    {
        if ( this.javaClassesTool == null )
        {
            this.javaClassesTool = new JavaClasses();
            this.setupJomcTool( this.javaClassesTool );
        }

        return this.javaClassesTool;
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
            if ( this.mainClassLoader == null )
            {
                final Collection urls = new LinkedList();
                for ( final Iterator it = this.getMainClasspathElements().iterator(); it.hasNext(); )
                {
                    final String element = (String) it.next();
                    final URL url = new File( element ).toURI().toURL();
                    if ( !urls.contains( url ) )
                    {
                        urls.add( url );
                        this.log( Level.FINE, this.getClasspathElementMessage( url.toExternalForm() ), null );
                    }
                }

                this.mainClassLoader = new URLClassLoader( (URL[]) urls.toArray( new URL[ urls.size() ] ),
                                                           Thread.currentThread().getContextClassLoader() );

            }

            return this.mainClassLoader;
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
            if ( this.testClassLoader == null )
            {
                final Collection urls = new LinkedList();
                for ( final Iterator it = this.getTestClasspathElements().iterator(); it.hasNext(); )
                {
                    final String element = (String) it.next();
                    final URL url = new File( element ).toURI().toURL();
                    if ( !urls.contains( url ) )
                    {
                        urls.add( url );
                        this.log( Level.FINE, this.getClasspathElementMessage( url.toExternalForm() ), null );
                    }
                }

                this.testClassLoader = new URLClassLoader( (URL[]) urls.toArray( new URL[ urls.size() ] ),
                                                           Thread.currentThread().getContextClassLoader() );

            }

            return this.testClassLoader;
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
    protected Set getMainClasspathElements() throws MojoExecutionException
    {
        final Set elements = new HashSet();
        elements.add( this.getMavenProject().getBuild().getOutputDirectory() );

        for ( final Iterator it = this.getMavenProject().getRuntimeArtifacts().iterator(); it.hasNext(); )
        {
            final Artifact a = (Artifact) it.next();

            if ( a.getFile() == null )
            {
                this.log( Level.WARNING, this.getIgnoredMessage( a.toString() ), null );
                continue;
            }

            if ( a.getGroupId().equals( "org.jomc" ) &&
                 ( a.getArtifactId().equals( "jomc-util" ) || a.getArtifactId().equals( "jomc-model" ) ||
                   a.getArtifactId().equals( "jomc-tools" ) ) )
            {
                continue;
            }

            final String element = a.getFile().getAbsolutePath();
            this.log( Level.FINE, this.getRuntimeElementMessage( element ), null );
            elements.add( element );
        }

        for ( final Iterator it = this.getMavenProject().getCompileArtifacts().iterator(); it.hasNext(); )
        {
            final Artifact a = (Artifact) it.next();

            if ( a.getFile() == null )
            {
                this.log( Level.WARNING, this.getIgnoredMessage( a.toString() ), null );
                continue;
            }

            if ( a.getGroupId().equals( "org.jomc" ) &&
                 ( a.getArtifactId().equals( "jomc-util" ) || a.getArtifactId().equals( "jomc-model" ) ||
                   a.getArtifactId().equals( "jomc-tools" ) ) )
            {
                continue;
            }

            final String element = a.getFile().getAbsolutePath();
            this.log( Level.FINE, this.getCompileElementMessage( element ), null );
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
    protected Set getTestClasspathElements() throws MojoExecutionException
    {
        final Set elements = new HashSet();

        elements.add( this.getMavenProject().getBuild().getOutputDirectory() );
        elements.add( this.getMavenProject().getBuild().getTestOutputDirectory() );

        for ( final Iterator it = this.getMavenProject().getTestArtifacts().iterator(); it.hasNext(); )
        {
            final Artifact a = (Artifact) it.next();

            if ( a.getFile() == null )
            {
                this.log( Level.WARNING, this.getIgnoredMessage( a.toString() ), null );
                continue;
            }

            if ( a.getGroupId().equals( "org.jomc" ) &&
                 ( a.getArtifactId().equals( "jomc-util" ) || a.getArtifactId().equals( "jomc-model" ) ||
                   a.getArtifactId().equals( "jomc-tools" ) ) )
            {
                continue;
            }

            final String element = a.getFile().getAbsolutePath();
            this.log( Level.FINE, this.getTestElementMessage( element ), null );
            elements.add( element );
        }

        return elements;
    }

    /**
     * Gets a flag indicating the processing of Java sources is enabled.
     *
     * @return {@code true} if processing of Java sources is enabled; {@code false} else.
     */
    protected boolean isJavaSourceProcessingEnabled()
    {
        return this.javaSourceProcessingEnabled;
    }

    /**
     * Gets a flag indicating the processing of Java classes is enabled.
     *
     * @return {@code true} if processing of Java classes is enabled; {@code false} else.
     */
    protected boolean isJavaClassProcessingEnabled()
    {
        return this.javaClassProcessingEnabled;
    }

    /**
     * Gets the name of the JOMC module to process.
     *
     * @return The name of the JOMC module to process.
     *
     * @throws MojoExecutionException if getting the name of the JOMC module fails.
     */
    protected String getJomcModuleName() throws MojoExecutionException
    {
        return this.getMavenProject().getName();
    }

    /**
     * Gets the name of the JOMC test module to process.
     *
     * @return The name of the JOMC test module to process.
     *
     * @throws MojoExecutionException if getting the name of the JOMC test module fails.
     */
    protected String getJomcTestModuleName() throws MojoExecutionException
    {
        return this.getMavenProject().getName() + " Tests";
    }

    /**
     * Gets a transformer from a given file.
     *
     * @param file The file to initialize the transformer with.
     *
     * @return A {@code Transformer} backed by {@code file}.
     *
     * @throws NullPointerException if {@code file} is {@code null}.
     * @throws MojoExecutionException if there are errors when parsing {@code file} or creating a
     * {@code Transformer} fails.
     */
    protected Transformer getTransformer( final File file ) throws MojoExecutionException
    {
        if ( file == null )
        {
            throw new NullPointerException( "file" );
        }

        try
        {
            final TransformerFactory transformerFactory = TransformerFactory.newInstance();
            transformerFactory.setErrorListener( new ErrorListener()
            {

                public void warning( final TransformerException exception ) throws TransformerException
                {
                    getLog().warn( exception );
                }

                public void error( final TransformerException exception ) throws TransformerException
                {
                    getLog().error( exception );
                    throw exception;
                }

                public void fatalError( final TransformerException exception ) throws TransformerException
                {
                    getLog().error( exception );
                    throw exception;
                }

            } );

            return transformerFactory.newTransformer( new StreamSource( file ) );
        }
        catch ( final TransformerConfigurationException e )
        {
            throw new MojoExecutionException( e.getMessage(), e );
        }
    }

    protected Modules getToolModules() throws MojoExecutionException
    {
        try
        {
            DefaultModelProvider.setDefaultModuleLocation( this.moduleLocation );
            final Modules modules = this.getModelContext().findModules();
            final Module classpathModule = modules.getClasspathModule(
                Modules.getDefaultClasspathModuleName(), this.getToolClassLoader() );

            if ( classpathModule != null )
            {
                modules.getModule().add( classpathModule );
            }

            return modules;
        }
        catch ( final ModelException e )
        {
            throw new MojoExecutionException( e.getMessage(), e );
        }
    }

    protected void logSeparator( final Level level ) throws MojoExecutionException
    {
        this.log( level, this.getMessage( "separator" ).format( null ), null );
    }

    protected void logProcessingModule( final Module module ) throws MojoExecutionException
    {
        this.log( Level.INFO, this.getProcessingModuleMesage( module ), null );
    }

    protected void logMissingModule( final String moduleName ) throws MojoExecutionException
    {
        this.log( Level.WARNING, this.getMissingModuleMesage( moduleName ), null );
    }

    protected void logToolSuccess() throws MojoExecutionException
    {
        this.log( Level.INFO, this.getToolSuccessMessage(), null );
    }

    protected void log( final Level level, final ModelValidationReport report ) throws MojoExecutionException
    {
        try
        {
            if ( !report.isModelValid() || !report.getDetails().isEmpty() )
            {
                this.logSeparator( level );
            }

            if ( !report.isModelValid() )
            {
                this.log( level, this.getMessage( "invalidModel" ).format( null ), null );
            }

            if ( !report.getDetails().isEmpty() )
            {
                final Marshaller marshaller = this.getModelContext().createMarshaller();

                for ( ModelValidationReport.Detail detail : report.getDetails() )
                {
                    this.log( detail.getLevel(), System.getProperty( "line.separator" ), null );
                    this.log( detail.getLevel(), detail.getMessage(), null );

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
            if ( level.intValue() < Level.INFO.intValue() || level.intValue() >= Level.WARNING.intValue() ||
                 this.verbose )
            {
                String line;
                final BufferedReader reader = new BufferedReader( new StringReader( message ) );
                while ( ( line = reader.readLine() ) != null )
                {
                    final String mojoMessage = "[JOMC] " + line;

                    if ( ( level.equals( Level.CONFIG ) || level.equals( Level.FINE ) || level.equals( Level.FINER ) ||
                           level.equals( Level.FINEST ) ) && this.getLog().isDebugEnabled() )
                    {
                        this.getLog().debug( mojoMessage, throwable );
                    }
                    else if ( level.equals( Level.INFO ) && this.getLog().isInfoEnabled() )
                    {
                        this.getLog().info( mojoMessage, throwable );
                    }
                    else if ( level.equals( Level.SEVERE ) && this.getLog().isErrorEnabled() )
                    {
                        this.getLog().error( mojoMessage, throwable );
                    }
                    else if ( level.equals( Level.WARNING ) && this.getLog().isWarnEnabled() )
                    {
                        this.getLog().warn( mojoMessage, throwable );
                    }
                    else if ( this.getLog().isDebugEnabled() )
                    {
                        this.getLog().debug( mojoMessage, throwable );
                    }
                }
            }
        }
        catch ( final IOException e )
        {
            this.getLog().error( e );
            throw new AssertionError( e );
        }
    }

    private void setupJomcTool( final JomcTool tool ) throws MojoExecutionException
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
        tool.setProfile( this.templateProfile );
        tool.setModules( this.getToolModules() );
    }

    private void setupModelContext( final ModelContext modelContext )
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

    private MessageFormat getMessage( final String key )
    {
        return new MessageFormat( ResourceBundle.getBundle( AbstractJomcMojo.class.getName().replace( '.', '/' ) ).
            getString( key ) );

    }

    private String getIgnoredMessage( final String item )
    {
        return this.getMessage( "ignored" ).format( new Object[]
            {
                item
            } );

    }

    private String getRuntimeElementMessage( final String element )
    {
        return this.getMessage( "runtimeElement" ).format( new Object[]
            {
                element
            } );

    }

    private String getTestElementMessage( final String element )
    {
        return this.getMessage( "testElement" ).format( new Object[]
            {
                element
            } );

    }

    private String getCompileElementMessage( final String element )
    {
        return this.getMessage( "compiletimeElement" ).format( new Object[]
            {
                element
            } );

    }

    private String getClasspathElementMessage( final String element )
    {
        return this.getMessage( "classpathElement" ).format( new Object[]
            {
                element
            } );

    }

    private String getProcessingModuleMesage( final Module module ) throws MojoExecutionException
    {
        return this.getMessage( "processingModule" ).format( new Object[]
            {
                this.getToolName(), module.getName()
            } );

    }

    private String getToolSuccessMessage() throws MojoExecutionException
    {
        return this.getMessage( "toolSuccess" ).format( new Object[]
            {
                this.getToolName()
            } );

    }

    private String getMissingModuleMesage( final String moduleName )
    {
        return this.getMessage( "missingModule" ).format( new Object[]
            {
                moduleName
            } );

    }

}
