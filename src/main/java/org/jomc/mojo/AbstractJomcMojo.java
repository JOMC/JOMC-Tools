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
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.jomc.model.DefaultModelManager;
import org.jomc.model.ModelException;
import org.jomc.model.Module;
import org.jomc.model.Modules;
import org.jomc.tools.JavaClasses;
import org.jomc.tools.JavaSources;
import org.jomc.tools.JomcTool;
import org.xml.sax.SAXException;

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
     * The location to search for documents.
     *
     * @parameter
     */
    private String documentLocation;

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
     * @parameter expression="${jomc.javaSources.disabled}" default-value="false
     */
    private boolean javaSourceProcessingDisabled;

    /**
     * Contols processing of java classes.
     *
     * @parameter expression="${jomc.javaClasses.disabled}" default-value="false
     */
    private boolean javaClassProcessingDisabled;

    /** The tool for managing sources. */
    private JavaSources mainJavaSourcesTool;

    /** The tool for managing sources. */
    private JavaSources testJavaSourcesTool;

    /** The tool for managing classes. */
    private JavaClasses mainJavaClassesTool;

    /** The tool for managing classes. */
    private JavaClasses testJavaClassesTool;

    /** The class loader of the project's runtime classpath including any provided dependencies. */
    private ClassLoader mainClassLoader;

    /** The class loader of the project's test classpath including any provided dependencies. */
    private ClassLoader testClassLoader;

    /**
     * Gets the Maven project of the instance.
     *
     * @return The Maven project of the instance.
     */
    public MavenProject getMavenProject()
    {
        return this.mavenProject;
    }

    /**
     * Gets the tool for managing sources.
     *
     * @return The tool for managing sources.
     */
    public JavaSources getMainJavaSourcesTool()
        throws DependencyResolutionRequiredException, ModelException, IOException, SAXException, JAXBException
    {
        if ( this.mainJavaSourcesTool == null )
        {
            this.mainJavaSourcesTool = new JavaSources();
            this.setupTool( this.mainJavaSourcesTool, this.getMainClassLoader(), true );
        }

        return this.mainJavaSourcesTool;
    }

    /**
     * Gets the tool for managing sources.
     *
     * @return The tool for managing sources.
     */
    public JavaSources getTestJavaSourcesTool()
        throws DependencyResolutionRequiredException, ModelException, IOException, SAXException, JAXBException
    {
        if ( this.testJavaSourcesTool == null )
        {
            this.testJavaSourcesTool = new JavaSources();
            this.setupTool( this.testJavaSourcesTool, this.getTestClassLoader(), true );
        }

        return this.testJavaSourcesTool;
    }

    /**
     * Gets the tool for managing classes.
     *
     * @return The tool for managing classes.
     */
    public JavaClasses getMainJavaClassesTool()
        throws DependencyResolutionRequiredException, ModelException, IOException, SAXException, JAXBException
    {
        if ( this.mainJavaClassesTool == null )
        {
            this.mainJavaClassesTool = new JavaClasses();
            this.setupTool( this.mainJavaClassesTool, this.getMainClassLoader(), true );
        }

        return this.mainJavaClassesTool;
    }

    /**
     * Gets the tool for managing classes.
     *
     * @return The tool for managing classes.
     */
    public JavaClasses getTestJavaClassesTool()
        throws DependencyResolutionRequiredException, ModelException, IOException, SAXException, JAXBException
    {
        if ( this.testJavaClassesTool == null )
        {
            this.testJavaClassesTool = new JavaClasses();
            this.setupTool( this.testJavaClassesTool, this.getTestClassLoader(), true );
        }

        return this.testJavaClassesTool;
    }

    /**
     * Gets a class loader of the project's runtime classpath including any provided dependencies.
     *
     * @return A {@code ClassLoader} initialized with the project's runtime classpath including any provided
     * dependencies.
     *
     * @throws DependencyResolutionRequiredException for any unresolved dependency scopes.
     * @throws IOException if building the classpath of the class loader fails.
     */
    public ClassLoader getMainClassLoader() throws DependencyResolutionRequiredException, IOException
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

    /**
     * Gets a class loader of the project's test classpath including any provided dependencies.
     *
     * @return A {@code ClassLoader} initialized with the project's test classpath including any provided dependencies.
     *
     * @throws DependencyResolutionRequiredException for any unresolved dependency scopes.
     * @throws IOException if building the classpath of the class loader fails.
     */
    public ClassLoader getTestClassLoader() throws DependencyResolutionRequiredException, IOException
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

    /**
     * Accessor to the project's runtime classpath elements including any provided dependencies.
     *
     * @return A set of classpath element strings.
     *
     * @throws DependencyResolutionRequiredException for any unresolved dependency scopes.
     * @throws IOException if getting main classpath elements fails.
     */
    public Set getMainClasspathElements() throws DependencyResolutionRequiredException, IOException
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

            final String element = a.getFile().getAbsolutePath();
            this.log( Level.FINE, this.getCompileElementMessage( element ), null );
            elements.add( element );
        }

        return elements;
    }

    /**
     * Accessor to the project's runtime classpath elements including any provided dependencies.
     *
     * @return A set of classpath element strings.
     *
     * @throws DependencyResolutionRequiredException for any unresolved dependency scopes.
     * @throws IOException if getting test classpath elements fails.
     */
    public Set getTestClasspathElements() throws DependencyResolutionRequiredException, IOException
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

            final String element = a.getFile().getAbsolutePath();
            this.log( Level.FINE, this.getTestElementMessage( element ), null );
            elements.add( element );
        }

        return elements;
    }

    /**
     * Gets a flag indicating the processing of Java sources is disabled.
     *
     * @return {@code true} if processing of Java sources is disabled; {@code false} else.
     */
    public boolean isJavaSourceProcessingDisabled()
    {
        return this.javaSourceProcessingDisabled;
    }

    /**
     * Gets a flag indicating the processing of Java classes is disabled.
     *
     * @return {@code true} if processing of Java classes is disabled; {@code false} else.
     */
    public boolean isJavaClassProcessingDisabled()
    {
        return this.javaClassProcessingDisabled;
    }

    /**
     * Gets the name of the JOMC module to process.
     *
     * @return The name of the JOMC module to process.
     */
    public String getJomcModuleName()
    {
        return this.getMavenProject().getName();
    }

    /**
     * Gets the name of the JOMC test module to process.
     *
     * @return The name of the JOMC test module to process.
     */
    public String getJomcTestModuleName()
    {
        return this.getMavenProject().getName() + " Tests";
    }

    public void execute() throws MojoExecutionException, MojoFailureException
    {
        try
        {
            this.logSeparator( Level.INFO );
            this.log( Level.INFO, this.getMessage( "title" ).format( null ), null );
            this.logSeparator( Level.INFO );
            this.executeTool();
        }
        catch ( final ModelException e )
        {
            try
            {
                this.log( Level.SEVERE, e );
            }
            catch ( final IOException e2 )
            {
                this.getLog().error( e );
                throw new MojoExecutionException( e2.getMessage(), e2 );
            }
            catch ( final SAXException e2 )
            {
                this.getLog().error( e );
                throw new MojoExecutionException( e2.getMessage(), e2 );
            }
            catch ( final JAXBException e2 )
            {
                this.getLog().error( e );
                throw new MojoExecutionException( e2.getMessage(), e2 );
            }

            throw new MojoExecutionException( e.getMessage(), e );
        }
        catch ( final Exception e )
        {
            throw new MojoExecutionException( e.getMessage(), e );
        }
    }

    protected abstract void executeTool() throws Exception;

    private void setupTool( final JomcTool tool, final ClassLoader classLoader, final boolean includeClasspathModule )
        throws ModelException, IOException, SAXException, JAXBException
    {
        Modules modulesToValidate = null;
        tool.getListeners().add( new JomcTool.Listener()
        {

            public void onLog( final Level level, final String message, final Throwable t )
            {
                try
                {
                    log( level, message, t );
                }
                catch ( final IOException e )
                {
                    getLog().error( e );
                }
            }

        } );

        tool.setTemplateEncoding( this.templateEncoding );
        tool.setInputEncoding( this.sourceEncoding );
        tool.setOutputEncoding( this.sourceEncoding );
        tool.setProfile( this.templateProfile );

        if ( tool.getModelManager() instanceof DefaultModelManager )
        {
            final DefaultModelManager defaultModelManager = (DefaultModelManager) tool.getModelManager();
            defaultModelManager.setClassLoader( classLoader );
            defaultModelManager.getListeners().add( new DefaultModelManager.Listener()
            {

                public void onLog( final Level level, final String message, final Throwable t )
                {
                    try
                    {
                        log( level, message, t );
                    }
                    catch ( final IOException e )
                    {
                        getLog().error( e );
                    }
                }

            } );

            final Modules classpathModules = defaultModelManager.getClasspathModules(
                this.documentLocation == null
                ? defaultModelManager.getDefaultDocumentLocation() : this.documentLocation );

            final Modules modulesWithoutClasspath = new Modules( classpathModules );
            final Module classpathModule = defaultModelManager.getClasspathModule( classpathModules );

            if ( classpathModule != null )
            {
                classpathModules.getModule().add( classpathModule );
            }

            this.log( Level.FINE, System.getProperty( "line.separator" ), null );
            this.log( Level.FINE, this.getMessage( "modulesReport" ).format( null ), null );

            if ( classpathModules.getModule().isEmpty() )
            {
                this.log( Level.FINE, "\t" + this.getMessage( "missingModules" ).format( null ), null );
            }
            else
            {
                for ( final Module m : classpathModules.getModule() )
                {
                    final StringBuilder moduleInfo = new StringBuilder().append( '\t' );
                    moduleInfo.append( m.getName() );

                    if ( m.getVersion() != null )
                    {
                        moduleInfo.append( " - " ).append( m.getVersion() );
                    }

                    this.log( Level.FINE, moduleInfo.toString(), null );
                }
            }

            this.log( Level.FINE, System.getProperty( "line.separator" ), null );

            modulesToValidate = classpathModules;
            tool.setModules( includeClasspathModule ? classpathModules : modulesWithoutClasspath );
        }

        if ( modulesToValidate != null )
        {
            tool.getModelManager().validateModelObject(
                tool.getModelManager().getObjectFactory().createModules( modulesToValidate ) );

        }
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

    private String getProcessingModuleMesage( final Module module )
    {
        return this.getMessage( "processingModule" ).format( new Object[]
            {
                this.getToolName(), module.getName()
            } );

    }

    private String getToolSuccessMessage()
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

    protected abstract String getToolName();

    protected void logSeparator( final Level level ) throws IOException
    {
        this.log( level, this.getMessage( "separator" ).format( null ), null );
    }

    protected void logProcessingModule( final Module module ) throws IOException
    {
        this.log( Level.INFO, this.getProcessingModuleMesage( module ), null );
    }

    protected void logMissingModule( final String moduleName ) throws IOException
    {
        this.log( Level.WARNING, this.getMissingModuleMesage( moduleName ), null );
    }

    protected void logToolSuccess() throws IOException
    {
        this.log( Level.INFO, this.getToolSuccessMessage(), null );
    }

    protected void log( final Level level, final String message, final Throwable throwable ) throws IOException
    {
        if ( level.intValue() < Level.INFO.intValue() || level.intValue() >= Level.WARNING.intValue() || this.verbose )
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

    protected void log( final Level level, final ModelException e ) throws IOException, SAXException, JAXBException
    {
        this.logSeparator( level );
        if ( e.getMessage() != null )
        {
            this.log( level, e.getMessage(), null );
        }

        if ( !e.getDetails().isEmpty() )
        {
            Marshaller marshaller = null;
            for ( ModelException.Detail detail : e.getDetails() )
            {
                this.log( detail.getLevel(), detail.getMessage(), null );

                if ( detail.getElement() != null )
                {
                    if ( marshaller == null )
                    {
                        marshaller = new DefaultModelManager().getMarshaller( false, true );
                    }

                    final StringWriter stringWriter = new StringWriter();
                    marshaller.marshal( detail.getElement(), stringWriter );

                    this.log( Level.FINE, System.getProperty( "line.separator" ), null );
                    this.log( Level.FINE, stringWriter.toString(), null );
                }
            }
        }

        this.logSeparator( level );
    }

}
