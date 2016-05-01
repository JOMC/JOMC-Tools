/*
 * Copyright (C) 2009 Christian Schulte <cs@schulte.it>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   o Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   o Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in
 *     the documentation and/or other materials provided with the
 *     distribution.
 *
 * THIS SOFTWARE IS PROVIDED "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY
 * AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * $JOMC$
 *
 */
package org.jomc.cli.commands;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
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
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import org.apache.commons.cli.CommandLine;
import org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement;
import org.jomc.model.ModelObject;
import org.jomc.modlet.DefaultModelContext;
import org.jomc.modlet.DefaultModletProvider;
import org.jomc.modlet.ModelContext;
import org.jomc.modlet.ModelContextFactory;
import org.jomc.modlet.ModelException;
import org.jomc.modlet.ModelValidationReport;
import org.jomc.modlet.Modlet;
import org.jomc.modlet.ModletObject;
import org.jomc.modlet.ModletProcessor;
import org.jomc.modlet.ModletProvider;
import org.jomc.modlet.ModletValidator;
import org.jomc.modlet.Modlets;
import org.jomc.modlet.ObjectFactory;
import org.jomc.modlet.Schema;
import org.jomc.modlet.Schemas;
import org.jomc.modlet.Service;
import org.jomc.modlet.ServiceFactory;
import org.jomc.modlet.Services;

/**
 * {@code ModelContext} based command implementation.
 *
 * @author <a href="mailto:cs@schulte.it">Christian Schulte</a>
 */
public abstract class AbstractModletCommand extends AbstractCommand
{

    /**
     * Constant to prefix relative resource names with.
     */
    private static final String ABSOLUTE_RESOURCE_NAME_PREFIX =
        "/" + AbstractModletCommand.class.getPackage().getName().replace( '.', '/' ) + "/";

    /**
     * Creates a new {@code AbstractModletCommand} instance.
     */
    public AbstractModletCommand()
    {
        super();
    }

    @Override
    public org.apache.commons.cli.Options getOptions()
    {
        final org.apache.commons.cli.Options options = new org.apache.commons.cli.Options();
        options.addOption( Options.CLASSPATH_OPTION );
        options.addOption( Options.DOCUMENTS_OPTION );
        options.addOption( Options.MODEL_CONTEXT_FACTORY_CLASSNAME_OPTION );
        options.addOption( Options.MODEL_OPTION );
        options.addOption( Options.MODLET_SCHEMA_SYSTEM_ID_OPTION );
        options.addOption( Options.MODLET_LOCATION_OPTION );
        options.addOption( Options.PROVIDER_LOCATION_OPTION );
        options.addOption( Options.PLATFORM_PROVIDER_LOCATION_OPTION );
        options.addOption( Options.NO_MODLET_RESOURCE_VALIDATION_OPTION );
        return options;
    }

    /**
     * Creates a new {@code Transformer} from a given {@code Source}.
     *
     * @param source The source to initialize the transformer with.
     *
     * @return A {@code Transformer} backed by {@code source}.
     *
     * @throws NullPointerException if {@code source} is {@code null}.
     * @throws CommandExecutionException if creating a transformer fails.
     */
    protected Transformer createTransformer( final Source source ) throws CommandExecutionException
    {
        if ( source == null )
        {
            throw new NullPointerException( "source" );
        }

        final ErrorListener errorListener = new ErrorListener()
        {

            public void warning( final TransformerException exception ) throws TransformerException
            {
                log( Level.WARNING, null, exception );
            }

            public void error( final TransformerException exception ) throws TransformerException
            {
                throw exception;
            }

            public void fatalError( final TransformerException exception ) throws TransformerException
            {
                throw exception;
            }

        };

        try
        {
            final TransformerFactory transformerFactory = TransformerFactory.newInstance();
            transformerFactory.setErrorListener( errorListener );
            final Transformer transformer = transformerFactory.newTransformer( source );
            transformer.setErrorListener( errorListener );

            for ( final Map.Entry<Object, Object> e : System.getProperties().entrySet() )
            {
                transformer.setParameter( e.getKey().toString(), e.getValue() );
            }

            return transformer;
        }
        catch ( final TransformerConfigurationException e )
        {
            throw new CommandExecutionException( Messages.getMessage( e ), e );
        }
    }

    /**
     * Creates a new {@code ModelContext} for a given {@code CommandLine} and {@code ClassLoader}.
     *
     * @param commandLine The {@code CommandLine} to create a new {@code ModelContext} with.
     * @param classLoader The {@code ClassLoader} to create a new {@code ModelContext} with.
     *
     * @return A new {@code ModelContext} for {@code classLoader} setup using {@code commandLine}.
     *
     * @throws NullPointerException if {@code commandLine} is {@code null}.
     * @throws CommandExecutionException if creating an new {@code ModelContext} fails.
     */
    protected ModelContext createModelContext( final CommandLine commandLine, final ClassLoader classLoader )
        throws CommandExecutionException
    {
        if ( commandLine == null )
        {
            throw new NullPointerException( "commandLine" );
        }

        final ModelContextFactory modelContextFactory =
            commandLine.hasOption( Options.MODEL_CONTEXT_FACTORY_CLASSNAME_OPTION.getOpt() )
                ? ModelContextFactory.newInstance( commandLine.getOptionValue(
                        Options.MODEL_CONTEXT_FACTORY_CLASSNAME_OPTION.getOpt() ) )
                : ModelContextFactory.newInstance();

        final ModelContext modelContext = modelContextFactory.newModelContext( classLoader );

        if ( commandLine.hasOption( Options.MODLET_SCHEMA_SYSTEM_ID_OPTION.getOpt() ) )
        {
            modelContext.setModletSchemaSystemId(
                commandLine.getOptionValue( Options.MODLET_SCHEMA_SYSTEM_ID_OPTION.getOpt() ) );

        }

        modelContext.setLogLevel( this.getLogLevel() );
        modelContext.getListeners().add( new ModelContext.Listener()
        {

            @Override
            public void onLog( final Level level, final String message, final Throwable t )
            {
                super.onLog( level, message, t );
                log( level, message, t );
            }

        } );

        if ( commandLine.hasOption( Options.PROVIDER_LOCATION_OPTION.getOpt() ) )
        {
            modelContext.setAttribute( DefaultModelContext.PROVIDER_LOCATION_ATTRIBUTE_NAME,
                                       commandLine.getOptionValue( Options.PROVIDER_LOCATION_OPTION.getOpt() ) );

        }

        if ( commandLine.hasOption( Options.PLATFORM_PROVIDER_LOCATION_OPTION.getOpt() ) )
        {
            modelContext.setAttribute(
                DefaultModelContext.PLATFORM_PROVIDER_LOCATION_ATTRIBUTE_NAME,
                commandLine.getOptionValue( Options.PLATFORM_PROVIDER_LOCATION_OPTION.getOpt() ) );

        }

        if ( commandLine.hasOption( Options.MODLET_LOCATION_OPTION.getOpt() ) )
        {
            modelContext.setAttribute( DefaultModletProvider.MODLET_LOCATION_ATTRIBUTE_NAME,
                                       commandLine.getOptionValue( Options.MODLET_LOCATION_OPTION.getOpt() ) );

        }

        modelContext.setAttribute( DefaultModletProvider.VALIDATING_ATTRIBUTE_NAME,
                                   !commandLine.hasOption( Options.NO_MODLET_RESOURCE_VALIDATION_OPTION.getOpt() ) );

        return modelContext;
    }

    /**
     * Gets the identifier of the model to process.
     *
     * @param commandLine The command line to get the identifier of the model to process from.
     *
     * @return The identifier of the model to process.
     *
     * @throws NullPointerException if {@code commandLine} is {@code null}.
     */
    protected String getModel( final CommandLine commandLine )
    {
        if ( commandLine == null )
        {
            throw new NullPointerException( "commandLine" );
        }

        return commandLine.hasOption( Options.MODEL_OPTION.getOpt() )
                   ? commandLine.getOptionValue( Options.MODEL_OPTION.getOpt() )
                   : ModelObject.MODEL_PUBLIC_ID;

    }

    /**
     * Logs a validation report.
     *
     * @param validationReport The report to log.
     * @param marshaller The marshaller to use for logging the report.
     *
     * @throws CommandExecutionException if logging a report detail element fails.
     */
    protected void log( final ModelValidationReport validationReport, final Marshaller marshaller )
        throws CommandExecutionException
    {
        Object jaxbFormattedOutput = null;
        try
        {
            jaxbFormattedOutput = marshaller.getProperty( Marshaller.JAXB_FORMATTED_OUTPUT );
            marshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
        }
        catch ( final PropertyException e )
        {
            this.log( Level.INFO, Messages.getMessage( e ), e );
            jaxbFormattedOutput = null;
        }

        try
        {
            for ( final ModelValidationReport.Detail d : validationReport.getDetails() )
            {
                if ( this.isLoggable( d.getLevel() ) )
                {
                    this.log( d.getLevel(), "o " + d.getMessage(), null );

                    if ( d.getElement() != null && this.getLogLevel().intValue() < Level.INFO.intValue() )
                    {
                        final StringWriter stringWriter = new StringWriter();
                        marshaller.marshal( d.getElement(), stringWriter );
                        this.log( d.getLevel(), stringWriter.toString(), null );
                    }
                }
            }
        }
        catch ( final JAXBException e )
        {
            String message = Messages.getMessage( e );
            if ( message == null )
            {
                message = Messages.getMessage( e.getLinkedException() );
            }

            throw new CommandExecutionException( message, e );
        }
        finally
        {
            try
            {
                marshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, jaxbFormattedOutput );
            }
            catch ( final PropertyException e )
            {
                this.log( Level.INFO, Messages.getMessage( e ), e );
            }
        }
    }

    /**
     * Gets the document files specified by a given command line.
     *
     * @param commandLine The command line specifying the document files to get.
     *
     * @return The document files specified by {@code commandLine}.
     *
     * @throws CommandExecutionException if getting the document files fails.
     */
    protected Set<File> getDocumentFiles( final CommandLine commandLine ) throws CommandExecutionException
    {
        try
        {
            final Set<File> files = new HashSet<File>();

            if ( commandLine.hasOption( Options.DOCUMENTS_OPTION.getOpt() ) )
            {
                final String[] elements = commandLine.getOptionValues( Options.DOCUMENTS_OPTION.getOpt() );

                if ( elements != null )
                {
                    for ( final String e : elements )
                    {
                        if ( e.startsWith( "@" ) )
                        {
                            final File file = new File( e.substring( 1 ) );
                            BufferedReader reader = null;

                            try
                            {
                                reader = new BufferedReader( new FileReader( file ) );

                                for ( String line = reader.readLine(); line != null; line = reader.readLine() )
                                {
                                    line = line.trim();

                                    if ( !line.startsWith( "#" ) )
                                    {
                                        final File f = new File( line );

                                        if ( f.exists() )
                                        {
                                            if ( this.isLoggable( Level.FINER ) )
                                            {
                                                this.log( Level.FINER,
                                                          Messages.getMessage( "documentFileInfo",
                                                                               f.getAbsolutePath() ),
                                                          null );

                                            }

                                            files.add( f );
                                        }
                                        else if ( this.isLoggable( Level.WARNING ) )
                                        {
                                            this.log( Level.WARNING,
                                                      Messages.getMessage( "documentFileNotFoundWarning",
                                                                           f.getAbsolutePath() ),
                                                      null );

                                        }
                                    }
                                }

                                reader.close();
                                reader = null;
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
                                catch ( final IOException ex )
                                {
                                    this.log( Level.SEVERE, Messages.getMessage( ex ), ex );
                                }
                            }
                        }
                        else
                        {
                            final File file = new File( e );

                            if ( file.exists() )
                            {
                                if ( this.isLoggable( Level.FINER ) )
                                {
                                    this.log( Level.FINER,
                                              Messages.getMessage( "documentFileInfo", file.getAbsolutePath() ),
                                              null );

                                }

                                files.add( file );
                            }
                            else if ( this.isLoggable( Level.WARNING ) )
                            {
                                this.log( Level.WARNING,
                                          Messages.getMessage( "documentFileNotFoundWarning", file.getAbsolutePath() ),
                                          null );

                            }
                        }
                    }
                }
            }

            return files;
        }
        catch ( final IOException e )
        {
            throw new CommandExecutionException( Messages.getMessage( e ), e );
        }
    }

    /**
     * Class loader backed by a command line.
     *
     * @author <a href="mailto:cs@schulte.it">Christian Schulte</a>
     * @version $JOMC$
     */
    public class CommandLineClassLoader extends URLClassLoader
    {

        /**
         * {@code Modlets} excluded by the instance.
         */
        private Modlets excludedModlets;

        /**
         * Set of provider resource locations to filter.
         */
        private final Set<String> providerResourceLocations = new HashSet<String>();

        /**
         * Set of modlet resource locations to filter.
         */
        private final Set<String> modletResourceLocations = new HashSet<String>();

        /**
         * Set of temporary resources.
         */
        private final Set<File> temporaryResources = new HashSet<File>();

        /**
         * Creates a new {@code CommandLineClassLoader} taking a command line backing the class loader.
         *
         * @param commandLine The command line backing the class loader.
         *
         * @throws NullPointerException if {@code commandLine} is {@code null}.
         * @throws CommandExecutionException if processing {@code commandLine} fails.
         */
        public CommandLineClassLoader( final CommandLine commandLine ) throws CommandExecutionException
        {
            super( new URL[ 0 ] );

            try
            {
                if ( commandLine.hasOption( Options.CLASSPATH_OPTION.getOpt() ) )
                {
                    final Set<URI> uris = new HashSet<URI>();
                    final String[] elements = commandLine.getOptionValues( Options.CLASSPATH_OPTION.getOpt() );

                    if ( elements != null )
                    {
                        for ( final String e : elements )
                        {
                            if ( e.startsWith( "@" ) )
                            {
                                final File file = new File( e.substring( 1 ) );
                                BufferedReader reader = null;

                                try
                                {
                                    reader = new BufferedReader( new FileReader( file ) );

                                    for ( String line = reader.readLine(); line != null; line = reader.readLine() )
                                    {
                                        line = line.trim();

                                        if ( !line.startsWith( "#" ) )
                                        {
                                            final File f = new File( line );

                                            if ( f.exists() )
                                            {
                                                uris.add( f.toURI() );
                                            }
                                            else if ( isLoggable( Level.WARNING ) )
                                            {
                                                log( Level.WARNING,
                                                     Messages.getMessage( "classpathElementNotFoundWarning",
                                                                          f.getAbsolutePath() ),
                                                     null );

                                            }
                                        }
                                    }

                                    reader.close();
                                    reader = null;
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
                                    catch ( final IOException ex )
                                    {
                                        log( Level.SEVERE, Messages.getMessage( ex ), ex );
                                    }
                                }
                            }
                            else
                            {
                                final File file = new File( e );

                                if ( file.exists() )
                                {
                                    uris.add( file.toURI() );
                                }
                                else if ( isLoggable( Level.WARNING ) )
                                {
                                    log( Level.WARNING,
                                         Messages.getMessage( "classpathElementNotFoundWarning",
                                                              file.getAbsolutePath() ),
                                         null );

                                }
                            }
                        }
                    }

                    for ( final URI uri : uris )
                    {
                        if ( isLoggable( Level.FINEST ) )
                        {
                            log( Level.FINEST,
                                 Messages.getMessage( "classpathElementInfo", uri.toASCIIString() ),
                                 null );

                        }

                        this.addURL( uri.toURL() );
                    }

                    // Assumes the default modlet location matches the location of resources of the applications'
                    // dependencies.
                    this.modletResourceLocations.add( DefaultModletProvider.getDefaultModletLocation() );

                    // Assumes the default provider location matches the location of resources of the applications'
                    // dependencies.
                    final String providerLocationPrefix = DefaultModelContext.getDefaultProviderLocation() + "/";
                    this.providerResourceLocations.add( providerLocationPrefix + ModletProcessor.class.getName() );
                    this.providerResourceLocations.add( providerLocationPrefix + ModletProvider.class.getName() );
                    this.providerResourceLocations.add( providerLocationPrefix + ModletValidator.class.getName() );
                    this.providerResourceLocations.add( providerLocationPrefix + ServiceFactory.class.getName() );
                }
            }
            catch ( final IOException e )
            {
                throw new CommandExecutionException( Messages.getMessage( e ), e );
            }
        }

        /**
         * Gets the {@code Modlets} excluded by the instance.
         *
         * @return The {@code Modlets} excluded by the instance.
         */
        public Modlets getExcludedModlets()
        {
            if ( this.excludedModlets == null )
            {
                this.excludedModlets = new Modlets();
            }

            return this.excludedModlets;
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
                    if ( this.providerResourceLocations.contains( name ) )
                    {
                        resource = this.filterProviders( resource );
                    }
                    else if ( this.modletResourceLocations.contains( name ) )
                    {
                        resource = this.filterModlets( resource );
                    }
                }

                return resource;
            }
            catch ( final IOException e )
            {
                log( Level.SEVERE, Messages.getMessage( e ), e );
                return null;
            }
            catch ( final JAXBException e )
            {
                log( Level.SEVERE, Messages.getMessage( e ), e );
                return null;
            }
            catch ( final ModelException e )
            {
                log( Level.SEVERE, Messages.getMessage( e ), e );
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

                if ( this.providerResourceLocations.contains( name )
                         || this.modletResourceLocations.contains( name ) )
                {
                    final List<URI> filtered = new LinkedList<URI>();

                    while ( resources.hasMoreElements() )
                    {
                        final URL resource = resources.nextElement();

                        if ( this.providerResourceLocations.contains( name ) )
                        {
                            filtered.add( this.filterProviders( resource ).toURI() );
                        }
                        else if ( this.modletResourceLocations.contains( name ) )
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

            if ( Closeable.class.isAssignableFrom( CommandLineClassLoader.class ) )
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
            final Set<String> providerExcludes = this.getProviderExcludes();
            final List<String> lines = new ArrayList<String>();

            try
            {
                URL filteredResource = resource;
                boolean filtered = false;
                in = resource.openStream();
                reader = new BufferedReader( new InputStreamReader( in, "UTF-8" ) );

                for ( String line = reader.readLine(); line != null; line = reader.readLine() )
                {
                    if ( !providerExcludes.contains( line.trim() ) )
                    {
                        lines.add( line );
                    }
                    else
                    {
                        filtered = true;
                        log( Level.FINE,
                             Messages.getMessage( "providerExclusionInfo", resource.toExternalForm(), line ),
                             null );

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

                    for ( final String line : lines )
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
                    log( Level.SEVERE, Messages.getMessage( e ), e );
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
                        log( Level.SEVERE, Messages.getMessage( e ), e );
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
                            log( Level.SEVERE, Messages.getMessage( e ), e );
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
                                log( Level.SEVERE, Messages.getMessage( e ), e );
                            }
                        }
                    }
                }
            }
        }

        private URL filterModlets( final URL resource ) throws ModelException, IOException, JAXBException
        {
            URL filteredResource = resource;
            final Set<String> excludedModletNames = this.getModletExcludes();
            final ModelContext modelContext = ModelContextFactory.newInstance().newModelContext();
            Object o = modelContext.createUnmarshaller( ModletObject.MODEL_PUBLIC_ID ).unmarshal( resource );
            if ( o instanceof JAXBElement<?> )
            {
                o = ( (JAXBElement<?>) o ).getValue();
            }

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

                    if ( excludedModletNames.contains( m.getName() ) )
                    {
                        it.remove();
                        filtered = true;
                        this.getExcludedModlets().getModlet().add( m );
                        log( Level.FINE,
                             Messages.getMessage( "modletExclusionInfo", resource.toExternalForm(), m.getName() ),
                             null );

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
                    modelContext.createMarshaller( ModletObject.MODEL_PUBLIC_ID ).
                        marshal( new ObjectFactory().createModlets( modlets ), tmpResource );

                    filteredResource = tmpResource.toURI().toURL();
                }
            }

            return filteredResource;
        }

        private boolean filterModlet( final Modlet modlet, final String resourceInfo ) throws IOException
        {
            boolean filteredSchemas = false;
            boolean filteredServices = false;
            final Set<String> excludedSchemas = this.getSchemaExcludes();
            final Set<String> excludedServices = this.getServiceExcludes();

            if ( modlet.getSchemas() != null )
            {
                final Schemas schemas = new Schemas();

                for ( final Schema s : modlet.getSchemas().getSchema() )
                {
                    if ( !excludedSchemas.contains( s.getPublicId() ) )
                    {
                        schemas.getSchema().add( s );
                    }
                    else
                    {
                        log( Level.FINE,
                             Messages.getMessage( "schemaExclusionInfo", resourceInfo, s.getContextId() ),
                             null );

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
                    if ( !excludedServices.contains( s.getClazz() ) )
                    {
                        services.getService().add( s );
                    }
                    else
                    {
                        log( Level.FINE,
                             Messages.getMessage( "serviceExclusionInfo", resourceInfo, s.getClazz() ),
                             null );

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

        /**
         * Gets a set of modlet names to filter.
         *
         * @return An unmodifiable set of modlet names to filter.
         *
         * @throws IOException if reading configuration resources fails.
         */
        private Set<String> getModletExcludes() throws IOException
        {
            return this.readDefaultExcludes( ABSOLUTE_RESOURCE_NAME_PREFIX + "DefaultModletExcludes" );
        }

        /**
         * Gets a set of provider names to filter.
         *
         * @return An unmodifiable set of provider names to filter.
         *
         * @throws IOException if reading configuration resources fails.
         */
        private Set<String> getProviderExcludes() throws IOException
        {
            return this.readDefaultExcludes( ABSOLUTE_RESOURCE_NAME_PREFIX + "DefaultProviderExcludes" );
        }

        /**
         * Gets a set of service class names to filter.
         *
         * @return An unmodifiable set of service class names to filter.
         *
         * @throws IOException if reading configuration resources fails.
         */
        private Set<String> getServiceExcludes() throws IOException
        {
            return this.readDefaultExcludes( ABSOLUTE_RESOURCE_NAME_PREFIX + "DefaultServiceExcludes" );
        }

        /**
         * Gets a set of schema public identifiers excluded by default.
         *
         * @return An unmodifiable set of schema public identifiers excluded by default.
         *
         * @throws IOException if reading configuration resources fails.
         */
        private Set<String> getSchemaExcludes() throws IOException
        {
            return this.readDefaultExcludes( ABSOLUTE_RESOURCE_NAME_PREFIX + "DefaultSchemaExcludes" );
        }

        private Set<String> readDefaultExcludes( final String location ) throws IOException
        {
            InputStream in = null;
            BufferedReader reader = null;
            final Set<String> defaultExcludes = new HashSet<String>();

            try
            {
                in = CommandLineClassLoader.class.getResourceAsStream( location );
                assert in != null : "Expected resource '" + location + "' not found.";
                reader = new BufferedReader( new InputStreamReader( in, "UTF-8" ) );

                for ( String line = reader.readLine(); line != null; line = reader.readLine() )
                {
                    final String normalized = line.trim();

                    if ( normalized.length() > 0 && !normalized.contains( "#" ) )
                    {
                        defaultExcludes.add( normalized );
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

}
