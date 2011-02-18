/*
 *   Copyright (C) 2009 The JOMC Project
 *   Copyright (C) 2005 Christian Schulte <schulte2005@users.sourceforge.net>
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
package org.jomc.ant;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.Path;
import org.jomc.modlet.DefaultModelContext;
import org.jomc.modlet.DefaultModletProvider;
import org.jomc.modlet.ModelContext;
import org.jomc.modlet.ModelException;
import org.jomc.modlet.Modlet;
import org.jomc.modlet.ModletObject;
import org.jomc.modlet.Modlets;
import org.jomc.modlet.ObjectFactory;
import org.jomc.modlet.Schema;
import org.jomc.modlet.Schemas;
import org.jomc.modlet.Service;
import org.jomc.modlet.Services;

/**
 * Class loader supporting JOMC resources backed by a project.
 *
 * @author <a href="mailto:schulte2005@users.sourceforge.net">Christian Schulte</a>
 * @version $Id$
 */
public class ProjectClassLoader extends URLClassLoader
{

    /** Constant to prefix relative resource names with. */
    private static final String ABSOLUTE_RESOURCE_NAME_PREFIX = "/org/jomc/ant/";

    /** Empty URL array. */
    private static final URL[] NO_URLS =
    {
    };

    /** Set of modlet names to exclude. */
    private Set<String> modletExcludes;

    /** Excluded modlets. */
    private Modlets excludedModlets;

    /** Set of service class names to exclude. */
    private Set<String> serviceExcludes;

    /** Excluded services. */
    private Services excludedServices;

    /** Set of schema public ids to exclude. */
    private Set<String> schemaExcludes;

    /** Excluded schemas. */
    private Schemas excludedSchemas;

    /** Set of providers to exclude. */
    private Set<String> providerExcludes;

    /** Set of excluded providers. */
    private Set<String> excludedProviders;

    /** The project the class loader is associated with. */
    private final Project project;

    /**
     * Creates a new {@code ProjectClassLoader} instance taking a project and a class path.
     *
     * @param project The project to which this class loader is to belong.
     * @param classpath The class path to use for loading.
     *
     * @throws MalformedURLException if {@code classpath} contains unsupported elements.
     */
    public ProjectClassLoader( final Project project, final Path classpath ) throws MalformedURLException
    {
        super( NO_URLS, ProjectClassLoader.class.getClassLoader() );

        for ( final String name : classpath.list() )
        {
            final File resolved = project.resolveFile( name );
            this.addURL( resolved.toURI().toURL() );
        }

        this.project = project;
    }

    /**
     * Gets the project of the instance.
     *
     * @return The project of the instance.
     */
    public final Project getProject()
    {
        return this.project;
    }

    /**
     * Finds a resource with a given name.
     *
     * @param name The name of the resource to search.
     *
     * @return An {@code URL} object for reading the resource, or {@code null} if no resource matching {@code name} is
     * found.
     */
    @Override
    public URL findResource( final String name )
    {
        try
        {
            URL resource = super.findResource( name );

            if ( resource != null )
            {
                if ( name.contains( DefaultModelContext.getDefaultProviderLocation() ) )
                {
                    resource = this.filterProviders( resource );
                }
                else if ( name.contains( DefaultModletProvider.getDefaultModletLocation() ) )
                {
                    resource = this.filterModlets( resource );
                }
            }

            return resource;
        }
        catch ( final IOException e )
        {
            this.getProject().log( getMessage( e ), Project.MSG_ERR );
            return null;
        }
        catch ( final JAXBException e )
        {
            String message = getMessage( e );
            if ( message == null && e.getLinkedException() != null )
            {
                message = getMessage( e.getLinkedException() );
            }

            this.getProject().log( message, Project.MSG_ERR );
            return null;
        }
        catch ( final ModelException e )
        {
            this.getProject().log( getMessage( e ), Project.MSG_ERR );
            return null;
        }
    }

    /**
     * Gets all resources matching a given name.
     *
     * @param name The name of the resources to get.
     *
     * @return An enumeration of {@code URL} objects of found resources.
     *
     * @throws IOException if getting resources fails.
     */
    @Override
    public Enumeration<URL> findResources( final String name ) throws IOException
    {
        final Enumeration<URL> allResources = super.findResources( name );
        Enumeration<URL> enumeration = allResources;

        if ( name.contains( DefaultModelContext.getDefaultProviderLocation() ) )
        {
            enumeration = new Enumeration<URL>()
            {

                public boolean hasMoreElements()
                {
                    return allResources.hasMoreElements();
                }

                public URL nextElement()
                {
                    try
                    {
                        return filterProviders( allResources.nextElement() );
                    }
                    catch ( final IOException e )
                    {
                        getProject().log( getMessage( e ), Project.MSG_ERR );
                        return null;
                    }
                }

            };
        }
        else if ( name.contains( DefaultModletProvider.getDefaultModletLocation() ) )
        {
            enumeration = new Enumeration<URL>()
            {

                public boolean hasMoreElements()
                {
                    return allResources.hasMoreElements();
                }

                public URL nextElement()
                {
                    try
                    {
                        return filterModlets( allResources.nextElement() );
                    }
                    catch ( final IOException e )
                    {
                        getProject().log( getMessage( e ), Project.MSG_ERR );
                        return null;
                    }
                    catch ( final JAXBException e )
                    {
                        String message = getMessage( e );
                        if ( message == null && e.getLinkedException() != null )
                        {
                            message = getMessage( e.getLinkedException() );
                        }

                        getProject().log( message, Project.MSG_ERR );
                        return null;
                    }
                    catch ( final ModelException e )
                    {
                        getProject().log( getMessage( e ), Project.MSG_ERR );
                        return null;
                    }
                }

            };
        }

        return enumeration;
    }

    /**
     * Gets a set of modlet names to exclude.
     * <p>This accessor method returns a reference to the live set, not a snapshot. Therefore any modification you make
     * to the returned set will be present inside the object. This is why there is no {@code set} method for the
     * modlet excludes property.</p>
     *
     * @return A set of modlet names to exclude.
     */
    public final Set<String> getModletExcludes()
    {
        if ( this.modletExcludes == null )
        {
            this.modletExcludes = new HashSet<String>();
        }

        return this.modletExcludes;
    }

    /**
     * Gets a set of modlet names excluded by default.
     *
     * @return A set of modlet names excluded by default.
     *
     * @throws IOException if reading configuration resources fails.
     */
    public static Set<String> getDefaultModletExcludes() throws IOException
    {
        InputStream resource = null;
        final Set<String> defaultModletExcludes = new HashSet<String>();

        try
        {
            resource = ProjectClassLoader.class.getResourceAsStream(
                ABSOLUTE_RESOURCE_NAME_PREFIX + "DefaultModletExcludes" );

            if ( resource != null )
            {
                final List<?> lines = IOUtils.readLines( resource, "UTF-8" );

                for ( Object line : lines )
                {
                    final String trimmed = line.toString().trim();

                    if ( trimmed.contains( "#" ) || StringUtils.isEmpty( trimmed ) )
                    {
                        continue;
                    }

                    defaultModletExcludes.add( trimmed );
                }
            }

            return defaultModletExcludes;
        }
        finally
        {
            if ( resource != null )
            {
                resource.close();
            }
        }
    }

    /**
     * Gets a set of modlets excluded during resource loading.
     * <p>This accessor method returns a reference to the live set, not a snapshot. Therefore any modification you make
     * to the returned set will be present inside the object. This is why there is no {@code set} method for the
     * excluded modlets property.</p>
     *
     * @return A set of modlets excluded during resource loading.
     */
    public final Modlets getExcludedModlets()
    {
        if ( this.excludedModlets == null )
        {
            this.excludedModlets = new Modlets();
        }

        return this.excludedModlets;
    }

    /**
     * Gets a set of provider names to exclude.
     * <p>This accessor method returns a reference to the live set, not a snapshot. Therefore any modification you make
     * to the returned set will be present inside the object. This is why there is no {@code set} method for the
     * provider excludes property.</p>
     *
     * @return A set of providers to exclude.
     */
    public final Set<String> getProviderExcludes()
    {
        if ( this.providerExcludes == null )
        {
            this.providerExcludes = new HashSet<String>();
        }

        return this.providerExcludes;
    }

    /**
     * Gets a set of provider names excluded by default.
     *
     * @return A set of provider names excluded by default.
     *
     * @throws IOException if reading configuration resources fails.
     */
    public static Set<String> getDefaultProviderExcludes() throws IOException
    {
        InputStream resource = null;
        final Set<String> defaultProviderExcludes = new HashSet<String>();

        try
        {
            resource = ProjectClassLoader.class.getResourceAsStream(
                ABSOLUTE_RESOURCE_NAME_PREFIX + "DefaultProviderExcludes" );

            if ( resource != null )
            {
                final List<?> lines = IOUtils.readLines( resource, "UTF-8" );

                for ( Object line : lines )
                {
                    final String trimmed = line.toString().trim();

                    if ( trimmed.contains( "#" ) || StringUtils.isEmpty( trimmed ) )
                    {
                        continue;
                    }

                    defaultProviderExcludes.add( trimmed );
                }
            }

            return defaultProviderExcludes;
        }
        finally
        {
            if ( resource != null )
            {
                resource.close();
            }
        }
    }

    /**
     * Gets a set of providers excluded during resource loading.
     * <p>This accessor method returns a reference to the live set, not a snapshot. Therefore any modification you make
     * to the returned set will be present inside the object. This is why there is no {@code set} method for the
     * excluded providers property.</p>
     *
     * @return A set of providers excluded during resource loading.
     */
    public final Set<String> getExcludedProviders()
    {
        if ( this.excludedProviders == null )
        {
            this.excludedProviders = new HashSet<String>();
        }

        return this.excludedProviders;
    }

    /**
     * Gets a set of service class names to exclude.
     * <p>This accessor method returns a reference to the live set, not a snapshot. Therefore any modification you make
     * to the returned set will be present inside the object. This is why there is no {@code set} method for the
     * service excludes property.</p>
     *
     * @return A set of service class names to exclude.
     */
    public final Set<String> getServiceExcludes()
    {
        if ( this.serviceExcludes == null )
        {
            this.serviceExcludes = new HashSet<String>();
        }

        return this.serviceExcludes;
    }

    /**
     * Gets a set of service class names excluded by default.
     *
     * @return A set of service class names excluded by default.
     *
     * @throws IOException if reading configuration resources fails.
     */
    public static Set<String> getDefaultServiceExcludes() throws IOException
    {
        InputStream resource = null;
        final Set<String> defaultServiceExcludes = new HashSet<String>();

        try
        {
            resource = ProjectClassLoader.class.getResourceAsStream(
                ABSOLUTE_RESOURCE_NAME_PREFIX + "DefaultServiceExcludes" );

            if ( resource != null )
            {
                final List<?> lines = IOUtils.readLines( resource, "UTF-8" );

                for ( Object line : lines )
                {
                    final String trimmed = line.toString().trim();

                    if ( trimmed.contains( "#" ) || StringUtils.isEmpty( trimmed ) )
                    {
                        continue;
                    }

                    defaultServiceExcludes.add( trimmed );
                }
            }

            return defaultServiceExcludes;
        }
        finally
        {
            if ( resource != null )
            {
                resource.close();
            }
        }
    }

    /**
     * Gets a set of services excluded during resource loading.
     * <p>This accessor method returns a reference to the live set, not a snapshot. Therefore any modification you make
     * to the returned set will be present inside the object. This is why there is no {@code set} method for the
     * excluded services property.</p>
     *
     * @return Services excluded during resource loading.
     */
    public final Services getExcludedServices()
    {
        if ( this.excludedServices == null )
        {
            this.excludedServices = new Services();
        }

        return this.excludedServices;
    }

    /**
     * Gets a set of schema public identifiers to exclude.
     * <p>This accessor method returns a reference to the live set, not a snapshot. Therefore any modification you make
     * to the returned set will be present inside the object. This is why there is no {@code set} method for the
     * schema excludes property.</p>
     *
     * @return A set of schema public identifiers to exclude.
     */
    public final Set<String> getSchemaExcludes()
    {
        if ( this.schemaExcludes == null )
        {
            this.schemaExcludes = new HashSet<String>();
        }

        return this.schemaExcludes;
    }

    /**
     * Gets a set of schema public identifiers excluded by default.
     *
     * @return A set of schema public identifiers excluded by default.
     *
     * @throws IOException if reading configuration resources fails.
     */
    public static Set<String> getDefaultSchemaExcludes() throws IOException
    {
        InputStream resource = null;
        final Set<String> defaultSchemaExcludes = new HashSet<String>();

        try
        {
            resource = ProjectClassLoader.class.getResourceAsStream(
                ABSOLUTE_RESOURCE_NAME_PREFIX + "DefaultSchemaExcludes" );

            if ( resource != null )
            {
                final List<?> lines = IOUtils.readLines( resource, "UTF-8" );

                for ( Object line : lines )
                {
                    final String trimmed = line.toString().trim();

                    if ( trimmed.contains( "#" ) || StringUtils.isEmpty( trimmed ) )
                    {
                        continue;
                    }

                    defaultSchemaExcludes.add( trimmed );
                }
            }

            return defaultSchemaExcludes;
        }
        finally
        {
            if ( resource != null )
            {
                resource.close();
            }
        }
    }

    /**
     * Gets a set of schemas excluded during resource loading.
     * <p>This accessor method returns a reference to the live set, not a snapshot. Therefore any modification you make
     * to the returned set will be present inside the object. This is why there is no {@code set} method for the
     * excluded schemas property.</p>
     *
     * @return Schemas excluded during resource loading.
     */
    public final Schemas getExcludedSchemas()
    {
        if ( this.excludedSchemas == null )
        {
            this.excludedSchemas = new Schemas();
        }

        return this.excludedSchemas;
    }

    private URL filterProviders( final URL resource ) throws IOException
    {
        InputStream in = null;

        try
        {
            URL filteredResource = resource;
            in = resource.openStream();
            final List<?> lines = IOUtils.readLines( in, "UTF-8" );
            final List<String> filteredLines = new ArrayList<String>( lines.size() );

            for ( Object line : lines )
            {
                if ( !this.getProviderExcludes().contains( line.toString().trim() ) )
                {
                    filteredLines.add( line.toString().trim() );
                }
                else
                {
                    this.getExcludedProviders().add( line.toString().trim() );
                    this.getProject().log( getMessage( "providerExclusion", resource.toExternalForm(),
                                                       line.toString().trim() ), Project.MSG_DEBUG );

                }
            }

            if ( lines.size() != filteredLines.size() )
            {
                OutputStream out = null;
                final File tmpResource = File.createTempFile( this.getClass().getName(), ".rsrc" );
                tmpResource.deleteOnExit();

                try
                {
                    out = new FileOutputStream( tmpResource );
                    IOUtils.writeLines( filteredLines, System.getProperty( "line.separator" ), out, "UTF-8" );
                }
                finally
                {
                    if ( out != null )
                    {
                        out.close();
                    }
                }

                filteredResource = tmpResource.toURI().toURL();
            }

            in.close();
            return filteredResource;
        }
        finally
        {
            if ( in != null )
            {
                in.close();
            }
        }
    }

    private URL filterModlets( final URL resource ) throws ModelException, IOException, JAXBException
    {
        InputStream in = null;

        try
        {
            URL filteredResource = resource;
            final ModelContext modelContext = ModelContext.createModelContext( this.getClass().getClassLoader() );
            in = resource.openStream();
            final JAXBElement<?> e =
                (JAXBElement<?>) modelContext.createUnmarshaller( ModletObject.MODEL_PUBLIC_ID ).unmarshal( in );

            final Object o = e.getValue();
            Modlets modlets = null;
            boolean filtered = false;

            if ( o instanceof Modlets )
            {
                modlets = new Modlets( (Modlets) o );
            }
            else if ( o instanceof Modlet )
            {
                modlets = new Modlets();
                modlets.getModlet().add( new Modlet( (Modlet) o ) );
            }

            if ( modlets != null )
            {
                for ( final Iterator<Modlet> it = modlets.getModlet().iterator(); it.hasNext(); )
                {
                    final Modlet m = it.next();

                    if ( this.getModletExcludes().contains( m.getName() ) )
                    {
                        it.remove();
                        filtered = true;
                        this.getExcludedModlets().getModlet().add( m );
                        this.getProject().log( getMessage( "modletExclusion", resource.toExternalForm(), m.getName() ),
                                               Project.MSG_DEBUG );

                        continue;
                    }

                    if ( this.filterModlet( m, resource.toExternalForm() ) )
                    {
                        filtered = true;
                    }
                }

                if ( filtered )
                {
                    final File tmpResource = File.createTempFile( this.getClass().getName(), ".rsrc" );
                    tmpResource.deleteOnExit();
                    modelContext.createMarshaller( ModletObject.MODEL_PUBLIC_ID ).marshal(
                        new ObjectFactory().createModlets( modlets ), tmpResource );

                    filteredResource = tmpResource.toURI().toURL();
                }
            }

            return filteredResource;
        }
        finally
        {
            if ( in != null )
            {
                in.close();
            }
        }
    }

    private boolean filterModlet( final Modlet modlet, final String resourceInfo )
    {
        boolean filteredSchemas = false;
        boolean filteredServices = false;

        if ( modlet.getSchemas() != null )
        {
            final Schemas schemas = new Schemas();

            for ( Schema s : modlet.getSchemas().getSchema() )
            {
                if ( !this.getModletExcludes().contains( s.getPublicId() ) )
                {
                    schemas.getSchema().add( s );
                }
                else
                {
                    this.getProject().log( getMessage( "schemaExclusion", resourceInfo, s.getPublicId() ),
                                           Project.MSG_DEBUG );

                    filteredSchemas = true;
                }
            }

            if ( filteredSchemas )
            {
                modlet.setSchemas( schemas );
            }
        }

        if ( modlet.getServices() != null )
        {
            final Services services = new Services();

            for ( Service s : modlet.getServices().getService() )
            {
                if ( !this.getServiceExcludes().contains( s.getClazz() ) )
                {
                    services.getService().add( s );
                }
                else
                {
                    this.getProject().log( getMessage( "serviceExclusion", resourceInfo, s.getClazz() ),
                                           Project.MSG_DEBUG );

                    filteredServices = true;
                }
            }

            if ( filteredServices )
            {
                modlet.setServices( services );
            }
        }

        return filteredSchemas || filteredServices;
    }

    private static String getMessage( final String key, final Object... args )
    {
        return MessageFormat.format( ResourceBundle.getBundle(
            ProjectClassLoader.class.getName().replace( '.', '/' ) ).getString( key ), args );

    }

    private static String getMessage( final Throwable t )
    {
        return t != null ? t.getMessage() != null ? t.getMessage() : getMessage( t.getCause() ) : null;
    }

}
