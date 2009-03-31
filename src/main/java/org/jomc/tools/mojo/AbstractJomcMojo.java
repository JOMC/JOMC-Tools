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

import java.io.File;
import java.io.IOException;
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
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.jomc.model.ModelError;
import org.jomc.model.ModelException;
import org.jomc.tools.JavaSources;
import org.jomc.tools.JomcTool;
import org.jomc.tools.ModuleAssembler;

/**
 * Base mojo class for executing container tools.
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
            this.mainJavaSourcesTool = new JavaSources( this.getMainClassLoader(
                Thread.currentThread().getContextClassLoader() ) );

            this.setupJomcTool( this.mainJavaSourcesTool );
            this.mainJavaSourcesTool.setModuleName( this.getMavenProject().getName() );
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
            this.testJavaSourcesTool = new JavaSources( this.getTestClassLoader(
                Thread.currentThread().getContextClassLoader() ) );

            this.setupJomcTool( this.testJavaSourcesTool );
            this.testJavaSourcesTool.setModuleName( this.getMavenProject().getName() + " Tests" );
        }

        return this.testJavaSourcesTool;
    }

    /**
     * Gets the tool for merging modules.
     *
     * @return The tool for merging modules.
     */
    public ModuleAssembler getModuleAssemblerTool() throws MojoFailureException
    {
        if ( this.moduleAssemblerTool == null )
        {
            this.moduleAssemblerTool = new ModuleAssembler( this.getMainClassLoader(
                Thread.currentThread().getContextClassLoader() ) );

            this.setupJomcTool( this.moduleAssemblerTool );
        }

        return this.moduleAssemblerTool;
    }

    /**
     * Gets a classloader of the project's runtime classpath including any
     * provided dependencies.
     *
     * @param parent The parent classloader to use for the runtime classloader.
     *
     * @return A {@code ClassLoader} initialized with the project's runtime
     * classpath including any provided dependencies.
     *
     * @throws MojoFailureException for unrecoverable errors.
     */
    public ClassLoader getMainClassLoader( final ClassLoader parent ) throws MojoFailureException
    {
        final Iterator it;
        final Collection urls = new LinkedList();

        try
        {
            for ( it = this.getMainClasspathElements().iterator(); it.hasNext(); )
            {
                final String element = (String) it.next();
                final URL url = new File( element ).toURI().toURL();
                if ( !urls.contains( url ) )
                {
                    urls.add( url );

                    if ( this.getLog().isDebugEnabled() )
                    {
                        this.getLog().debug( this.getClasspathElementMessage( url.toExternalForm() ) );
                    }
                }
            }

            return new URLClassLoader( (URL[]) urls.toArray( new URL[ urls.size() ] ), parent );
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
     * @param parent The parent classloader to use for the test classloader.
     *
     * @return A {@code ClassLoader} initialized with the project's test classpath including any provided dependencies.
     *
     * @throws MojoFailureException for unrecoverable errors.
     */
    public ClassLoader getTestClassLoader( final ClassLoader parent ) throws MojoFailureException
    {
        final Iterator it;
        final Collection urls = new LinkedList();

        try
        {
            for ( it = this.getTestClasspathElements().iterator(); it.hasNext(); )
            {
                final String element = (String) it.next();
                final URL url = new File( element ).toURI().toURL();
                if ( !urls.contains( url ) )
                {
                    urls.add( url );

                    if ( this.getLog().isDebugEnabled() )
                    {
                        this.getLog().debug( this.getClasspathElementMessage( url.toExternalForm() ) );
                    }
                }
            }

            return new URLClassLoader( (URL[]) urls.toArray( new URL[ urls.size() ] ), parent );
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

    public final void execute() throws MojoExecutionException, MojoFailureException
    {
        try
        {
            this.executeTool();
        }
        catch ( MojoExecutionException e )
        {
            throw (MojoExecutionException) e;
        }
        catch ( MojoFailureException e )
        {
            throw (MojoFailureException) e;
        }
        catch ( ModelException e )
        {
            throw new MojoExecutionException( e.getMessage(), e );
        }
        catch ( ModelError e )
        {
            throw (MojoFailureException) new MojoFailureException( e.getMessage() ).initCause( e );
        }
        catch ( Exception e )
        {
            if ( e.getMessage() == null )
            {
                throw new MojoExecutionException( e.toString(), e );
            }
            else
            {
                throw new MojoExecutionException( e.getMessage(), e );
            }
        }
    }

    public abstract void executeTool() throws Exception;

    /**
     * Accessor to the project's runtime classpath elements including any provided dependencies.
     *
     * @return A set of classpath element strings.
     *
     * @throws DependencyResolutionRequiredException for any unresolved dependency scopes.
     */
    private Set getMainClasspathElements() throws DependencyResolutionRequiredException
    {
        final Set elements = new HashSet();

        elements.add( this.getMavenProject().getBuild().getOutputDirectory() );

        for ( Iterator it = this.getMavenProject().getRuntimeArtifacts().iterator(); it.hasNext(); )
        {
            final Artifact a = (Artifact) it.next();

            if ( a.getFile() == null )
            {
                this.getLog().warn( this.getIgnoredMessage( a.toString() ) );
                continue;
            }

            final String element = a.getFile().getAbsolutePath();

            if ( this.getLog().isDebugEnabled() )
            {
                this.getLog().debug( this.getRuntimeElementMessage( element ) );
            }

            elements.add( element );
        }

        for ( Iterator it = this.getMavenProject().getCompileArtifacts().iterator(); it.hasNext(); )
        {
            final Artifact a = (Artifact) it.next();

            if ( a.getFile() == null )
            {
                this.getLog().warn( this.getIgnoredMessage( a.toString() ) );
                continue;
            }

            final String element = a.getFile().getAbsolutePath();

            if ( this.getLog().isDebugEnabled() )
            {
                this.getLog().debug( this.getCompileElementMessage( element ) );
            }

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
    private Set getTestClasspathElements() throws DependencyResolutionRequiredException
    {
        final Set elements = new HashSet();

        elements.add( this.getMavenProject().getBuild().getOutputDirectory() );
        elements.add( this.getMavenProject().getBuild().getTestOutputDirectory() );

        for ( Iterator it = this.getMavenProject().getTestArtifacts().iterator(); it.hasNext(); )
        {
            final Artifact a = (Artifact) it.next();

            if ( a.getFile() == null )
            {
                this.getLog().warn( this.getIgnoredMessage( a.toString() ) );
                continue;
            }

            final String element = a.getFile().getAbsolutePath();

            if ( this.getLog().isDebugEnabled() )
            {
                this.getLog().debug( this.getRuntimeElementMessage( element ) );
            }

            elements.add( element );
        }

        return elements;
    }

    private void setupJomcTool( final JomcTool jomcTool )
    {
        jomcTool.getModelManager().setClasspathAware( true );
        jomcTool.getModelManager().setValidating( true );
        jomcTool.setBuildDirectory( new File( this.getMavenProject().getBasedir(),
                                              this.getMavenProject().getBuild().getDirectory() ) );

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

}
