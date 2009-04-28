/*
 *  JOMC Maven Plugin
 *  Copyright (c) 2005 Christian Schulte <cs@schulte.it>
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 */
package org.jomc.tools.mojo;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.MalformedURLException;
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
import org.jomc.tools.JavaClasses;
import org.jomc.tools.JavaSources;
import org.jomc.tools.JomcTool;
import org.jomc.tools.ModuleAssembler;

/**
 * Base mojo class for executing {@code JomcTool}s.
 *
 * @author <a href="mailto:cs@schulte.it">Christian Schulte</a>
 * @version $Id$
 */
public abstract class AbstractJomcMojo extends AbstractMojo
{

    /**
     * The Maven project of the instance.
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject mavenProject;

    /** The tool for managing sources. */
    private JavaSources mainJavaSourcesTool;

    /** The tool for managing sources. */
    private JavaSources testJavaSourcesTool;

    /** The tool for merging modules. */
    private ModuleAssembler moduleAssemblerTool;

    /** The tool for managing classes. */
    private JavaClasses mainJavaClassesTool;

    /** The tool for managing classes. */
    private JavaClasses testJavaClassesTool;

    /** The classloader of the project's runtime classpath including any provided dependencies. */
    private ClassLoader mainClassLoader;

    /** The classloader of the project's test classpath including any provided dependencies. */
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
        throws IOException, MojoFailureException, ModelException
    {
        if ( this.mainJavaSourcesTool == null )
        {
            this.mainJavaSourcesTool = new JavaSources();
            this.mainJavaSourcesTool.setModuleName( this.getMavenProject().getName() );
            this.setupTool( this.mainJavaSourcesTool, this.getMainClassLoader() );
        }

        return this.mainJavaSourcesTool;
    }

    /**
     * Gets the tool for managing sources.
     *
     * @return The tool for managing sources.
     */
    public JavaSources getTestJavaSourcesTool()
        throws IOException, MojoFailureException, ModelException
    {
        if ( this.testJavaSourcesTool == null )
        {
            this.testJavaSourcesTool = new JavaSources();
            this.testJavaSourcesTool.setModuleName( this.getMavenProject().getName() + " Tests" );
            this.setupTool( this.testJavaSourcesTool, this.getTestClassLoader() );
        }

        return this.testJavaSourcesTool;
    }

    /**
     * Gets the tool for merging modules.
     *
     * @return The tool for merging modules.
     */
    public ModuleAssembler getModuleAssemblerTool()
        throws IOException, MojoFailureException, ModelException
    {
        if ( this.moduleAssemblerTool == null )
        {
            this.moduleAssemblerTool = new ModuleAssembler();
            this.setupTool( this.moduleAssemblerTool, this.getMainClassLoader() );
        }

        return this.moduleAssemblerTool;
    }

    /**
     * Gets the tool for managing classes.
     *
     * @return The tool for managing classes.
     */
    public JavaClasses getMainJavaClassesTool()
        throws IOException, MojoFailureException, ModelException
    {
        if ( this.mainJavaClassesTool == null )
        {
            this.mainJavaClassesTool = new JavaClasses();
            this.mainJavaClassesTool.setModuleName( this.getMavenProject().getName() );
            this.setupTool( this.mainJavaClassesTool, this.getMainClassLoader() );
        }

        return this.mainJavaClassesTool;
    }

    /**
     * Gets the tool for managing classes.
     *
     * @return The tool for managing classes.
     */
    public JavaClasses getTestJavaClassesTool()
        throws IOException, MojoFailureException, ModelException
    {
        if ( this.testJavaClassesTool == null )
        {
            this.testJavaClassesTool = new JavaClasses();
            this.testJavaClassesTool.setModuleName( this.getMavenProject().getName() + " Tests" );
            this.setupTool( this.testJavaClassesTool, this.getTestClassLoader() );
        }

        return this.testJavaClassesTool;
    }

    /**
     * Gets a classloader of the project's runtime classpath including any provided dependencies.
     *
     * @return A {@code ClassLoader} initialized with the project's runtime classpath including any provided
     * dependencies.
     *
     * @throws MojoFailureException for unrecoverable errors.
     */
    public ClassLoader getMainClassLoader() throws MojoFailureException
    {
        try
        {
            if ( this.mainClassLoader == null )
            {
                final Iterator it;
                final Collection urls = new LinkedList();
                for ( it = this.getMainClasspathElements().iterator(); it.hasNext(); )
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
        catch ( DependencyResolutionRequiredException e )
        {
            throw (MojoFailureException) new MojoFailureException( e.getMessage() ).initCause( e );
        }
        catch ( MalformedURLException e )
        {
            throw (MojoFailureException) new MojoFailureException( e.getMessage() ).initCause( e );
        }
    }

    /**
     * Gets a classloader of the project's test classpath including any provided dependencies.
     *
     * @return A {@code ClassLoader} initialized with the project's test classpath including any provided dependencies.
     *
     * @throws MojoFailureException for unrecoverable errors.
     */
    public ClassLoader getTestClassLoader() throws MojoFailureException
    {
        try
        {
            if ( this.testClassLoader == null )
            {
                final Iterator it;
                final Collection urls = new LinkedList();
                for ( it = this.getTestClasspathElements().iterator(); it.hasNext(); )
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
        catch ( DependencyResolutionRequiredException e )
        {
            throw (MojoFailureException) new MojoFailureException( e.getMessage() ).initCause( e );
        }
        catch ( MalformedURLException e )
        {
            throw (MojoFailureException) new MojoFailureException( e.getMessage() ).initCause( e );
        }
    }

    /**
     * Accessor to the project's runtime classpath elements including any provided dependencies.
     *
     * @return A set of classpath element strings.
     *
     * @throws DependencyResolutionRequiredException for any unresolved dependency scopes.
     */
    public Set getMainClasspathElements() throws DependencyResolutionRequiredException
    {
        final Set elements = new HashSet();

        elements.add( this.getMavenProject().getBuild().getOutputDirectory() );

        for ( Iterator it = this.getMavenProject().getRuntimeArtifacts().iterator(); it.hasNext(); )
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

        for ( Iterator it = this.getMavenProject().getCompileArtifacts().iterator(); it.hasNext(); )
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
     */
    public Set getTestClasspathElements() throws DependencyResolutionRequiredException
    {
        final Set elements = new HashSet();

        elements.add( this.getMavenProject().getBuild().getOutputDirectory() );
        elements.add( this.getMavenProject().getBuild().getTestOutputDirectory() );

        for ( Iterator it = this.getMavenProject().getTestArtifacts().iterator(); it.hasNext(); )
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

        return elements;
    }

    public void execute() throws MojoExecutionException, MojoFailureException
    {
        this.logSeparator( Level.INFO );
        this.log( Level.INFO, this.getMessage( "title" ).format( null ), null );
        this.logSeparator( Level.INFO );

        try
        {
            this.executeTool();
        }
        catch ( ModelException e )
        {
            try
            {
                if ( !e.getDetails().isEmpty() )
                {
                    this.logSeparator( Level.INFO );
                    final Marshaller m = new DefaultModelManager().getMarshaller( false, true );

                    for ( ModelException.Detail detail : e.getDetails() )
                    {
                        this.log( detail.getLevel(), detail.getMessage(), null );

                        if ( detail.getElement() != null )
                        {
                            final StringWriter stringWriter = new StringWriter();
                            m.marshal( detail.getElement(), stringWriter );

                            this.log( Level.FINE, "\n", null );
                            this.log( Level.FINE, stringWriter.toString(), null );
                        }
                    }
                }
            }
            catch ( Exception e2 )
            {
                throw new MojoExecutionException( e2.getMessage(), e2 );
            }

            throw new MojoExecutionException( e.getMessage(), e );
        }
        catch ( Exception e )
        {
            throw new MojoExecutionException( e.getMessage(), e );
        }
        finally
        {
            this.logSeparator( Level.INFO );
        }
    }

    protected abstract void executeTool() throws Exception;

    private void setupTool( final JomcTool tool, final ClassLoader classLoader )
        throws IOException, ModelException
    {
        final String toolName = tool.getClass().getName().substring( tool.getClass().getName().lastIndexOf( '.' ) + 1 );
        this.log( Level.INFO, this.getInitializingMessage( toolName ), null );

        tool.getListeners().add( new JomcTool.Listener()
        {

            public void onLog( final Level level, final String message, final Throwable t )
            {
                log( level, message, t );
            }

        } );

        tool.setBuildDirectory( new File( this.getMavenProject().getBasedir(),
                                          this.getMavenProject().getBuild().getDirectory() ) );

        if ( tool.getModelManager() instanceof DefaultModelManager )
        {
            final DefaultModelManager defaultModelManager = (DefaultModelManager) tool.getModelManager();
            defaultModelManager.setClassLoader( classLoader );
            defaultModelManager.getListeners().add( new DefaultModelManager.Listener()
            {

                @Override
                public void onLog( final Level level, final String message, final Throwable t )
                {
                    log( level, message, t );
                }

            } );

            tool.setModules( defaultModelManager.getClasspathModules(
                DefaultModelManager.DEFAULT_DOCUMENT_LOCATION ) );

            final Module classpathModule = defaultModelManager.getClasspathModule( tool.getModules() );
            if ( classpathModule != null )
            {
                tool.getModules().getModule().add( classpathModule );
            }

            this.log( Level.FINE, "\n", null );
            this.log( Level.FINE, this.getMessage( "modulesReport" ).format( null ), null );

            if ( tool.getModules().getModule().isEmpty() )
            {
                this.log( Level.FINE, "\t" + this.getMessage( "missingModules" ).format( null ), null );
            }
            else
            {
                for ( Module m : tool.getModules().getModule() )
                {
                    final StringBuffer moduleInfo = new StringBuffer().append( '\t' );
                    moduleInfo.append( m.getName() );

                    if ( m.getVersion() != null )
                    {
                        moduleInfo.append( " - " ).append( m.getVersion() );
                    }

                    this.log( Level.FINE, moduleInfo.toString(), null );
                }
            }

            this.log( Level.FINE, "\n", null );
        }

        tool.getModelManager().validateModelObject(
            tool.getModelManager().getObjectFactory().createModules( tool.getModules() ) );

    }

    private MessageFormat getMessage( final String key )
    {
        return new MessageFormat( ResourceBundle.getBundle( "org/jomc/tools/mojo/AbstractJomcMojo" ).
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

    private String getInitializingMessage( final String entity )
    {
        return this.getMessage( "initializing" ).format( new Object[]
            {
                entity
            } );

    }

    protected void logSeparator( final Level level )
    {
        this.log( level, this.getMessage( "separator" ).format( null ), null );
    }

    protected void log( final Level level, final String message, final Throwable throwable )
    {
        try
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
        catch ( IOException e )
        {
            throw new AssertionError( e );
        }
    }

}
