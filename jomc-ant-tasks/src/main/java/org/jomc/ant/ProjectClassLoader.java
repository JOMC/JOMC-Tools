/*
 *   Copyright (C) 2005 Christian Schulte <cs@schulte.it>
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
 *   THIS SOFTWARE IS PROVIDED "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 *   INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY
 *   AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 *   THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY DIRECT, INDIRECT,
 *   INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 *   NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 *   DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 *   THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *   (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 *   THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *   $JOMC$
 *
 */
package org.jomc.ant;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.Path;
import org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement;
import org.jomc.modlet.ModelContext;
import org.jomc.modlet.ModelContextFactory;
import org.jomc.modlet.ModelException;
import org.jomc.modlet.Modlet;
import org.jomc.modlet.ModletObject;
import org.jomc.modlet.Modlets;
import org.jomc.modlet.ObjectFactory;
import org.jomc.modlet.Schema;
import org.jomc.modlet.Schemas;
import org.jomc.modlet.Service;
import org.jomc.modlet.Services;
import org.jomc.util.ParseException;
import org.jomc.util.TokenMgrError;
import org.jomc.util.VersionParser;

/**
 * Class loader supporting JOMC resources backed by a project.
 *
 * @author <a href="mailto:cs@schulte.it">Christian Schulte</a>
 * @version $JOMC$
 */
public class ProjectClassLoader extends URLClassLoader
{

    /**
     * Constant to prefix relative resource names with.
     */
    private static final String ABSOLUTE_RESOURCE_NAME_PREFIX =
        "/" + ProjectClassLoader.class.getPackage().getName().replace( '.', '/' ) + "/";

    /**
     * Empty URL array.
     */
    private static final URL[] NO_URLS =
    {
    };

    /**
     * Set of modlet names to exclude.
     */
    private Set<String> modletExcludes;

    /**
     * Excluded modlets.
     */
    private Modlets excludedModlets;

    /**
     * Set of service class names to exclude.
     */
    private Set<String> serviceExcludes;

    /**
     * Excluded services.
     */
    private Services excludedServices;

    /**
     * Set of schema public ids to exclude.
     */
    private Set<String> schemaExcludes;

    /**
     * Excluded schemas.
     */
    private Schemas excludedSchemas;

    /**
     * Set of providers to exclude.
     */
    private Set<String> providerExcludes;

    /**
     * Set of excluded providers.
     */
    private Set<String> excludedProviders;

    /**
     * The project the class loader is associated with.
     */
    private final Project project;

    /**
     * Set of modlet resource locations to filter.
     */
    private Set<String> modletResourceLocations;

    /**
     * Set of provider resource locations to filter.
     */
    private Set<String> providerResourceLocations;

    /**
     * Set of temporary resources.
     */
    private final Set<File> temporaryResources = new HashSet<File>();

    /**
     * Creates a new {@code ProjectClassLoader} instance taking a project and a class path.
     *
     * @param project The project to which this class loader is to belong.
     * @param classpath The class path to use for loading.
     *
     * @throws MalformedURLException if {@code classpath} contains unsupported elements.
     * @throws IOException if reading configuration resources fails.
     */
    public ProjectClassLoader( final Project project, final Path classpath ) throws MalformedURLException, IOException
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
     * @return An {@code URL} object for reading the resource or {@code null}, if no resource matching {@code name} is
     * found.
     */
    @Override
    public URL findResource( final String name ) //JDK: As of JDK 23 throws IOException
    {
        try
        {
            URL resource = super.findResource( name );

            if ( resource != null )
            {
                if ( this.getProviderResourceLocations().contains( name ) )
                {
                    resource = this.filterProviders( resource );
                }
                else if ( this.getModletResourceLocations().contains( name ) )
                {
                    resource = this.filterModlets( resource );
                }
            }

            return resource;
        }
        catch ( final IOException e )
        {
            this.getProject().log( Messages.getMessage( e ), Project.MSG_ERR );
            return null;
        }
        catch ( final JAXBException e )
        {
            String message = Messages.getMessage( e );
            if ( message == null && e.getLinkedException() != null )
            {
                message = Messages.getMessage( e.getLinkedException() );
            }

            this.getProject().log( message, Project.MSG_ERR );
            return null;
        }
        catch ( final ModelException e )
        {
            this.getProject().log( Messages.getMessage( e ), Project.MSG_ERR );
            return null;
        }
    }

    /**
     * Finds all resources matching a given name.
     *
     * @param name The name of the resources to search.
     *
     * @return An enumeration of {@code URL} objects of resources matching name.
     *
     * @throws IOException if getting resources fails.
     */
    @Override
    public Enumeration<URL> findResources( final String name ) throws IOException
    {
        try
        {
            Enumeration<URL> resources = super.findResources( name );

            if ( this.getProviderResourceLocations().contains( name )
                     || this.getModletResourceLocations().contains( name ) )
            {
                final List<URI> filtered = new LinkedList<URI>();

                while ( resources.hasMoreElements() )
                {
                    final URL resource = resources.nextElement();

                    if ( this.getProviderResourceLocations().contains( name ) )
                    {
                        filtered.add( this.filterProviders( resource ).toURI() );
                    }
                    else if ( this.getModletResourceLocations().contains( name ) )
                    {
                        filtered.add( this.filterModlets( resource ).toURI() );
                    }
                }

                final Iterator<URI> it = filtered.iterator();

                resources = new Enumeration<URL>()
                {

                    public boolean hasMoreElements()
                    {
                        return it.hasNext();
                    }

                    public URL nextElement()
                    {
                        try
                        {
                            return it.next().toURL();
                        }
                        catch ( final MalformedURLException e )
                        {
                            throw new AssertionError( e );
                        }
                    }

                };
            }

            return resources;
        }
        catch ( final URISyntaxException e )
        {
            // JDK: As of JDK 6, new IOException( message, e );
            throw (IOException) new IOException( Messages.getMessage( e ) ).initCause( e );
        }
        catch ( final JAXBException e )
        {
            String message = Messages.getMessage( e );
            if ( message == null && e.getLinkedException() != null )
            {
                message = Messages.getMessage( e.getLinkedException() );
            }

            // JDK: As of JDK 6, new IOException( message, e );
            throw (IOException) new IOException( message ).initCause( e );
        }
        catch ( final ModelException e )
        {
            // JDK: As of JDK 6, new IOException( message, e );
            throw (IOException) new IOException( Messages.getMessage( e ) ).initCause( e );
        }
    }

    /**
     * Gets a set of modlet resource locations to filter.
     * <p>
     * This accessor method returns a reference to the live set, not a snapshot. Therefore any modification you make
     * to the returned set will be present inside the object. This is why there is no {@code set} method for the
     * modlet resource locations property.
     * </p>
     *
     * @return A set of modlet resource locations to filter.
     */
    public final Set<String> getModletResourceLocations()
    {
        if ( this.modletResourceLocations == null )
        {
            this.modletResourceLocations = new HashSet<String>();
        }

        return this.modletResourceLocations;
    }

    /**
     * Gets a set of provider resource locations to filter.
     * <p>
     * This accessor method returns a reference to the live set, not a snapshot. Therefore any modification you make
     * to the returned set will be present inside the object. This is why there is no {@code set} method for the
     * provider resource locations property.
     * </p>
     *
     * @return A set of provider resource locations to filter.
     */
    public final Set<String> getProviderResourceLocations()
    {
        if ( this.providerResourceLocations == null )
        {
            this.providerResourceLocations = new HashSet<String>();
        }

        return this.providerResourceLocations;
    }

    /**
     * Gets a set of modlet names to exclude.
     * <p>
     * This accessor method returns a reference to the live set, not a snapshot. Therefore any modification you make
     * to the returned set will be present inside the object. This is why there is no {@code set} method for the
     * modlet excludes property.
     * </p>
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
     * @return An unmodifiable set of modlet names excluded by default.
     *
     * @throws IOException if reading configuration resources fails.
     */
    public static Set<String> getDefaultModletExcludes() throws IOException
    {
        return readDefaultExcludes( ABSOLUTE_RESOURCE_NAME_PREFIX + "DefaultModletExcludes" );
    }

    /**
     * Gets a set of modlets excluded during resource loading.
     * <p>
     * This accessor method returns a reference to the live set, not a snapshot. Therefore any modification you make
     * to the returned set will be present inside the object. This is why there is no {@code set} method for the
     * excluded modlets property.
     * </p>
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
     * <p>
     * This accessor method returns a reference to the live set, not a snapshot. Therefore any modification you make
     * to the returned set will be present inside the object. This is why there is no {@code set} method for the
     * provider excludes property.
     * </p>
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
     * @return An unmodifiable set of provider names excluded by default.
     *
     * @throws IOException if reading configuration resources fails.
     */
    public static Set<String> getDefaultProviderExcludes() throws IOException
    {
        return readDefaultExcludes( ABSOLUTE_RESOURCE_NAME_PREFIX + "DefaultProviderExcludes" );
    }

    /**
     * Gets a set of providers excluded during resource loading.
     * <p>
     * This accessor method returns a reference to the live set, not a snapshot. Therefore any modification you make
     * to the returned set will be present inside the object. This is why there is no {@code set} method for the
     * excluded providers property.
     * </p>
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
     * <p>
     * This accessor method returns a reference to the live set, not a snapshot. Therefore any modification you make
     * to the returned set will be present inside the object. This is why there is no {@code set} method for the
     * service excludes property.
     * </p>
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
     * @return An unmodifiable set of service class names excluded by default.
     *
     * @throws IOException if reading configuration resources fails.
     */
    public static Set<String> getDefaultServiceExcludes() throws IOException
    {
        return readDefaultExcludes( ABSOLUTE_RESOURCE_NAME_PREFIX + "DefaultServiceExcludes" );
    }

    /**
     * Gets a set of services excluded during resource loading.
     * <p>
     * This accessor method returns a reference to the live set, not a snapshot. Therefore any modification you make
     * to the returned set will be present inside the object. This is why there is no {@code set} method for the
     * excluded services property.
     * </p>
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
     * <p>
     * This accessor method returns a reference to the live set, not a snapshot. Therefore any modification you make
     * to the returned set will be present inside the object. This is why there is no {@code set} method for the
     * schema excludes property.
     * </p>
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
     * @return An unmodifiable set of schema public identifiers excluded by default.
     *
     * @throws IOException if reading configuration resources fails.
     */
    public static Set<String> getDefaultSchemaExcludes() throws IOException
    {
        return readDefaultExcludes( ABSOLUTE_RESOURCE_NAME_PREFIX + "DefaultSchemaExcludes" );
    }

    /**
     * Gets a set of schemas excluded during resource loading.
     * <p>
     * This accessor method returns a reference to the live set, not a snapshot. Therefore any modification you make
     * to the returned set will be present inside the object. This is why there is no {@code set} method for the
     * excluded schemas property.
     * </p>
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

    /**
     * Closes the class loader.
     *
     * @throws IOException if closing the class loader fails.
     */
    @Override
    @IgnoreJRERequirement
    public void close() throws IOException
    {
        for ( final Iterator<File> it = this.temporaryResources.iterator(); it.hasNext(); )
        {
            final File temporaryResource = it.next();

            if ( temporaryResource.exists() && temporaryResource.delete() )
            {
                it.remove();
            }
        }

        if ( Closeable.class.isAssignableFrom( ProjectClassLoader.class ) )
        {
            super.close();
        }
    }

    /**
     * Removes temporary resources.
     *
     * @throws Throwable if finalization fails.
     */
    @Override
    protected void finalize() throws Throwable
    {
        for ( final Iterator<File> it = this.temporaryResources.iterator(); it.hasNext(); )
        {
            final File temporaryResource = it.next();

            if ( temporaryResource.exists() && !temporaryResource.delete() )
            {
                temporaryResource.deleteOnExit();
            }

            it.remove();
        }

        super.finalize();
    }

    private URL filterProviders( final URL resource ) throws IOException
    {
        InputStream in = null;
        BufferedReader reader = null;
        OutputStream out = null;
        BufferedWriter writer = null;
        URL filteredResource = resource;
        final List<String> filteredLines = new ArrayList<String>();

        try
        {
            boolean filtered = false;
            in = resource.openStream();
            reader = new BufferedReader( new InputStreamReader( in, "UTF-8" ) );

            for ( String line = reader.readLine(); line != null; line = reader.readLine() )
            {
                String normalized = line.trim();

                if ( !this.getProviderExcludes().contains( normalized ) )
                {
                    filteredLines.add( normalized );
                }
                else
                {
                    filtered = true;
                    this.getExcludedProviders().add( normalized );
                    this.getProject().log( Messages.getMessage( "providerExclusion", resource.toExternalForm(),
                                                                line.trim() ), Project.MSG_DEBUG );

                }
            }

            reader.close();
            reader = null;
            in = null;

            if ( filtered )
            {
                final File tmpResource = File.createTempFile( this.getClass().getName(), ".rsrc" );
                this.temporaryResources.add( tmpResource );

                out = new FileOutputStream( tmpResource );
                writer = new BufferedWriter( new OutputStreamWriter( out, "UTF-8" ) );

                for ( final String line : filteredLines )
                {
                    writer.write( line );
                    writer.newLine();
                }

                writer.close();
                writer = null;
                out = null;

                filteredResource = tmpResource.toURI().toURL();
            }

            return filteredResource;
        }
        finally
        {
            try
            {
                if ( reader != null )
                {
                    reader.close();
                }
            }
            catch ( final IOException e )
            {
                this.project.log( Messages.getMessage( e ), e, Project.MSG_ERR );
            }
            finally
            {
                try
                {
                    if ( in != null )
                    {
                        in.close();
                    }
                }
                catch ( final IOException e )
                {
                    this.project.log( Messages.getMessage( e ), e, Project.MSG_ERR );
                }
                finally
                {
                    try
                    {
                        if ( writer != null )
                        {
                            writer.close();
                        }
                    }
                    catch ( final IOException e )
                    {
                        this.project.log( Messages.getMessage( e ), e, Project.MSG_ERR );
                    }
                    finally
                    {
                        try
                        {
                            if ( out != null )
                            {
                                out.close();
                            }
                        }
                        catch ( final IOException e )
                        {
                            this.project.log( Messages.getMessage( e ), e, Project.MSG_ERR );
                        }
                    }
                }
            }
        }
    }

    private URL filterModlets( final URL resource ) throws ModelException, IOException, JAXBException
    {
        InputStream in = null;

        try
        {
            URL filteredResource = resource;
            in = resource.openStream();

            final ModelContext modelContext = ModelContextFactory.newInstance().newModelContext();
            final JAXBElement<?> e =
                (JAXBElement<?>) modelContext.createUnmarshaller( ModletObject.MODEL_PUBLIC_ID ).unmarshal( in );

            in.close();
            in = null;

            final Object o = e.getValue();
            Modlets modlets = null;
            boolean filtered = false;

            if ( o instanceof Modlets )
            {
                modlets = (Modlets) o;
            }
            else if ( o instanceof Modlet )
            {
                modlets = new Modlets();
                modlets.getModlet().add( (Modlet) o );
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
                        this.addExcludedModlet( m );
                        this.getProject().log( Messages.getMessage( "modletExclusion", resource.toExternalForm(),
                                                                    m.getName() ), Project.MSG_DEBUG );

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
                    this.temporaryResources.add( tmpResource );
                    modelContext.createMarshaller( ModletObject.MODEL_PUBLIC_ID ).marshal(
                        new ObjectFactory().createModlets( modlets ), tmpResource );

                    filteredResource = tmpResource.toURI().toURL();
                }
            }

            return filteredResource;
        }
        finally
        {
            try
            {
                if ( in != null )
                {
                    in.close();
                }
            }
            catch ( final IOException e )
            {
                this.project.log( Messages.getMessage( e ), e, Project.MSG_ERR );
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

            for ( final Schema s : modlet.getSchemas().getSchema() )
            {
                if ( !this.getSchemaExcludes().contains( s.getPublicId() ) )
                {
                    schemas.getSchema().add( s );
                }
                else
                {
                    this.getProject().log( Messages.getMessage( "schemaExclusion", resourceInfo, s.getPublicId() ),
                                           Project.MSG_DEBUG );

                    this.addExcludedSchema( s );
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

            for ( final Service s : modlet.getServices().getService() )
            {
                if ( !this.getServiceExcludes().contains( s.getClazz() ) )
                {
                    services.getService().add( s );
                }
                else
                {
                    this.getProject().log( Messages.getMessage( "serviceExclusion", resourceInfo, s.getClazz() ),
                                           Project.MSG_DEBUG );

                    this.addExcludedService( s );
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

    private void addExcludedModlet( final Modlet modlet )
    {
        try
        {
            final Modlet m = this.getExcludedModlets().getModlet( modlet.getName() );

            if ( m != null )
            {
                if ( m.getVersion() != null && modlet.getVersion() != null
                         && VersionParser.compare( m.getVersion(), modlet.getVersion() ) < 0 )
                {
                    this.getExcludedModlets().getModlet().remove( m );
                    this.getExcludedModlets().getModlet().add( modlet );
                }
            }
            else
            {
                this.getExcludedModlets().getModlet().add( modlet );
            }
        }
        catch ( final ParseException e )
        {
            this.getProject().log( Messages.getMessage( e ), e, Project.MSG_WARN );
        }
        catch ( final TokenMgrError e )
        {
            this.getProject().log( Messages.getMessage( e ), e, Project.MSG_WARN );
        }
    }

    private void addExcludedSchema( final Schema schema )
    {
        if ( this.getExcludedSchemas().getSchemaBySystemId( schema.getSystemId() ) == null )
        {
            this.getExcludedSchemas().getSchema().add( schema );
        }
    }

    private void addExcludedService( final Service service )
    {
        for ( int i = 0, s0 = this.getExcludedServices().getService().size(); i < s0; i++ )
        {
            final Service s = this.getExcludedServices().getService().get( i );

            if ( s.getIdentifier().equals( service.getIdentifier() ) && s.getClazz().equals( service.getClazz() ) )
            {
                return;
            }
        }

        this.getExcludedServices().getService().add( service );
    }

    private static Set<String> readDefaultExcludes( final String location ) throws IOException
    {
        InputStream in = null;
        BufferedReader reader = null;
        final Set<String> defaultExcludes = new HashSet<String>();

        try
        {
            in = ProjectClassLoader.class.getResourceAsStream( location );
            assert in != null : "Expected resource '" + location + "' not found.";
            reader = new BufferedReader( new InputStreamReader( in, "UTF-8" ) );

            for ( String line = reader.readLine(); line != null; line = reader.readLine() )
            {
                final String normalized = line.trim();

                if ( normalized.length() > 0 && !normalized.contains( "#" ) )
                {
                    defaultExcludes.add( line.trim() );
                }
            }

            reader.close();
            reader = null;
            in = null;

            return Collections.unmodifiableSet( defaultExcludes );
        }
        finally
        {
            try
            {
                if ( reader != null )
                {
                    reader.close();
                }
            }
            catch ( final IOException e )
            {
                // Suppressed.
            }
            finally
            {
                try
                {
                    if ( in != null )
                    {
                        in.close();
                    }
                }
                catch ( final IOException e )
                {
                    // Suppressed.
                }
            }
        }
    }

}
