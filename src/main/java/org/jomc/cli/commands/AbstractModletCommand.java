// SECTION-START[License Header]
// <editor-fold defaultstate="collapsed" desc=" Generated License ">
/*
 *   Java Object Management and Configuration
 *   Copyright (C) Christian Schulte, 2005-206
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
// </editor-fold>
// SECTION-END
package org.jomc.cli.commands;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
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
import org.apache.commons.io.IOUtils;
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
import org.jomc.modlet.ModletProvider;
import org.jomc.modlet.Modlets;
import org.jomc.modlet.ObjectFactory;
import org.jomc.modlet.Schema;
import org.jomc.modlet.Schemas;
import org.jomc.modlet.Service;
import org.jomc.modlet.Services;

// SECTION-START[Documentation]
// <editor-fold defaultstate="collapsed" desc=" Generated Documentation ">
/**
 * JOMC ⁑ CLI ⁑ modlet based command implementation.
 *
 * <dl>
 *   <dt><b>Identifier:</b></dt><dd>JOMC ⁑ CLI ⁑ Modlet Command</dd>
 *   <dt><b>Name:</b></dt><dd>JOMC ⁑ CLI ⁑ Modlet Command</dd>
 *   <dt><b>Specifications:</b></dt>
 *     <dd>JOMC ⁑ CLI ⁑ Command @ 1.0</dd>
 *   <dt><b>Abstract:</b></dt><dd>Yes</dd>
 *   <dt><b>Final:</b></dt><dd>No</dd>
 *   <dt><b>Stateless:</b></dt><dd>No</dd>
 * </dl>
 *
 * @author <a href="mailto:cs@schulte.it">Christian Schulte</a> 1.0
 * @version 2.0-SNAPSHOT
 */
// </editor-fold>
// SECTION-END
// SECTION-START[Annotations]
// <editor-fold defaultstate="collapsed" desc=" Generated Annotations ">
@javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://www.jomc.org/jomc/2.0/jomc-tools-2.0-SNAPSHOT" )
// </editor-fold>
// SECTION-END
public abstract class AbstractModletCommand extends AbstractCommand
{
    // SECTION-START[Command]
    // SECTION-END
    // SECTION-START[AbstractModletCommand]

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

            for ( Map.Entry<Object, Object> e : System.getProperties().entrySet() )
            {
                transformer.setParameter( e.getKey().toString(), e.getValue() );
            }

            return transformer;
        }
        catch ( final TransformerConfigurationException e )
        {
            throw new CommandExecutionException( getExceptionMessage( e ), e );
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

        final ModelContextFactory modelContextFactory;
        if ( commandLine.hasOption( this.getModelContextFactoryOption().getOpt() ) )
        {
            modelContextFactory = ModelContextFactory.newInstance( commandLine.getOptionValue(
                this.getModelContextFactoryOption().getOpt() ) );

        }
        else
        {
            modelContextFactory = ModelContextFactory.newInstance();
        }

        final ModelContext modelContext = modelContextFactory.newModelContext( classLoader );

        if ( commandLine.hasOption( this.getModletSchemaSystemIdOption().getOpt() ) )
        {
            modelContext.setModletSchemaSystemId(
                commandLine.getOptionValue( this.getModletSchemaSystemIdOption().getOpt() ) );

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

        if ( commandLine.hasOption( this.getProviderLocationOption().getOpt() ) )
        {
            modelContext.setAttribute( DefaultModelContext.PROVIDER_LOCATION_ATTRIBUTE_NAME,
                                       commandLine.getOptionValue( this.getProviderLocationOption().getOpt() ) );

        }

        if ( commandLine.hasOption( this.getPlatformProviderLocationOption().getOpt() ) )
        {
            modelContext.setAttribute(
                DefaultModelContext.PLATFORM_PROVIDER_LOCATION_ATTRIBUTE_NAME,
                commandLine.getOptionValue( this.getPlatformProviderLocationOption().getOpt() ) );

        }

        if ( commandLine.hasOption( this.getModletLocationOption().getOpt() ) )
        {
            modelContext.setAttribute( DefaultModletProvider.MODLET_LOCATION_ATTRIBUTE_NAME,
                                       commandLine.getOptionValue( this.getModletLocationOption().getOpt() ) );

        }

        modelContext.setAttribute( DefaultModletProvider.VALIDATING_ATTRIBUTE_NAME,
                                   !commandLine.hasOption( this.getNoModletResourceValidation().getOpt() ) );

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

        return commandLine.hasOption( this.getModelOption().getOpt() )
               ? commandLine.getOptionValue( this.getModelOption().getOpt() )
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
            this.log( Level.INFO, null, e );
            jaxbFormattedOutput = null;
        }

        try
        {

            for ( ModelValidationReport.Detail d : validationReport.getDetails() )
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
            String message = getExceptionMessage( e );
            if ( message == null )
            {
                message = getExceptionMessage( e.getLinkedException() );
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
                this.log( Level.INFO, null, e );
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

            if ( commandLine.hasOption( this.getDocumentsOption().getOpt() ) )
            {
                final String[] elements = commandLine.getOptionValues( this.getDocumentsOption().getOpt() );
                if ( elements != null )
                {
                    for ( String e : elements )
                    {
                        if ( e.startsWith( "@" ) )
                        {
                            String line = null;
                            final File file = new File( e.substring( 1 ) );
                            BufferedReader reader = null;
                            boolean suppressExceptionOnClose = true;

                            try
                            {
                                reader = new BufferedReader( new FileReader( file ) );
                                while ( ( line = reader.readLine() ) != null )
                                {
                                    line = line.trim();
                                    if ( !line.startsWith( "#" ) )
                                    {
                                        final File f = new File( line );

                                        if ( f.exists() )
                                        {
                                            if ( this.isLoggable( Level.FINER ) )
                                            {
                                                this.log( Level.FINER, this.getDocumentFileInfo(
                                                    this.getLocale(), f.getAbsolutePath() ), null );

                                            }

                                            files.add( f );
                                        }
                                        else if ( this.isLoggable( Level.WARNING ) )
                                        {
                                            this.log( Level.WARNING, this.getDocumentFileNotFoundWarning(
                                                this.getLocale(), f.getAbsolutePath() ), null );

                                        }
                                    }
                                }

                                suppressExceptionOnClose = false;
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
                                    if ( suppressExceptionOnClose )
                                    {
                                        this.log( Level.SEVERE, getExceptionMessage( ex ), ex );
                                    }
                                    else
                                    {
                                        throw new CommandExecutionException( getExceptionMessage( ex ), ex );
                                    }
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
                                    this.log( Level.FINER, this.getDocumentFileInfo(
                                        this.getLocale(), file.getAbsolutePath() ), null );

                                }

                                files.add( file );
                            }
                            else if ( this.isLoggable( Level.WARNING ) )
                            {
                                this.log( Level.WARNING, this.getDocumentFileNotFoundWarning(
                                    this.getLocale(), file.getAbsolutePath() ), null );

                            }
                        }
                    }
                }
            }

            return files;
        }
        catch ( final IOException e )
        {
            throw new CommandExecutionException( getExceptionMessage( e ), e );
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

        /** {@code Modlets} excluded by the instance. */
        private Modlets excludedModlets;

        /** Set of provider resource locations to filter. */
        private final Set<String> providerResourceLocations = new HashSet<String>();

        /** Set of modlet resource locations to filter. */
        private final Set<String> modletResourceLocations = new HashSet<String>();

        /** Set of temporary resources. */
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
                if ( commandLine.hasOption( getClasspathOption().getOpt() ) )
                {
                    final Set<URI> uris = new HashSet<URI>();
                    final String[] elements = commandLine.getOptionValues( getClasspathOption().getOpt() );

                    if ( elements != null )
                    {
                        for ( String e : elements )
                        {
                            if ( e.startsWith( "@" ) )
                            {
                                String line = null;
                                final File file = new File( e.substring( 1 ) );
                                BufferedReader reader = null;
                                boolean suppressExceptionOnClose = true;

                                try
                                {
                                    reader = new BufferedReader( new FileReader( file ) );
                                    while ( ( line = reader.readLine() ) != null )
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
                                                log( Level.WARNING, getClasspathElementNotFoundWarning(
                                                    getLocale(), f.getAbsolutePath() ), null );

                                            }
                                        }
                                    }

                                    suppressExceptionOnClose = false;
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
                                        if ( suppressExceptionOnClose )
                                        {
                                            log( Level.SEVERE, getExceptionMessage( ex ), ex );
                                        }
                                        else
                                        {
                                            throw new CommandExecutionException( getExceptionMessage( ex ), ex );
                                        }
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
                                    log( Level.WARNING, getClasspathElementNotFoundWarning(
                                        getLocale(), file.getAbsolutePath() ), null );

                                }
                            }
                        }
                    }

                    for ( URI uri : uris )
                    {
                        if ( isLoggable( Level.FINEST ) )
                        {
                            log( Level.FINEST, getClasspathElementInfo( getLocale(), uri.toASCIIString() ), null );
                        }

                        this.addURL( uri.toURL() );
                    }

                    if ( commandLine.hasOption( getProviderLocationOption().getOpt() ) )
                    {
                        this.providerResourceLocations.add(
                            commandLine.getOptionValue( getProviderLocationOption().getOpt() )
                            + "/" + ModletProvider.class.getName() );

                    }
                    else
                    {
                        this.providerResourceLocations.add(
                            DefaultModelContext.getDefaultProviderLocation() + "/" + ModletProvider.class.getName() );

                    }

                    if ( commandLine.hasOption( getModletLocationOption().getOpt() ) )
                    {
                        this.modletResourceLocations.add(
                            commandLine.getOptionValue( getModletLocationOption().getOpt() ) );

                    }
                    else
                    {
                        this.modletResourceLocations.add( DefaultModletProvider.getDefaultModletLocation() );
                    }
                }
            }
            catch ( final IOException e )
            {
                throw new CommandExecutionException( getExceptionMessage( e ), e );
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
         * Finds the resource with the specified name on the URL search path.
         *
         * @param name The name of the resource.
         *
         * @return A {@code URL} for the resource or {@code null}, if the resource could not be found.
         */
        @Override
        public URL findResource( final String name )
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
                log( Level.SEVERE, null, e );
                return null;
            }
            catch ( final JAXBException e )
            {
                log( Level.SEVERE, null, e );
                return null;
            }
            catch ( final ModelException e )
            {
                log( Level.SEVERE, null, e );
                return null;
            }
        }

        /**
         * Returns an {@code Enumeration} of {@code URL}s representing all of the resources on the URL search path
         * having the specified name.
         *
         * @param name The resource name.
         *
         * @throws IOException if an I/O exception occurs
         *
         * @return An {@code Enumeration} of {@code URL}s.
         */
        @Override
        public Enumeration<URL> findResources( final String name ) throws IOException
        {
            final Enumeration<URL> allResources = super.findResources( name );

            Enumeration<URL> enumeration = allResources;

            if ( this.providerResourceLocations.contains( name ) )
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
                            log( Level.SEVERE, null, e );
                            return null;
                        }
                    }

                };
            }
            else if ( this.modletResourceLocations.contains( name ) )
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
                            log( Level.SEVERE, null, e );
                            return null;
                        }
                        catch ( final JAXBException e )
                        {
                            log( Level.SEVERE, null, e );
                            return null;
                        }
                        catch ( final ModelException e )
                        {
                            log( Level.SEVERE, null, e );
                            return null;
                        }
                    }

                };
            }

            return enumeration;
        }

        /**
         * Closes the class loader.
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
            boolean suppressExceptionOnClose = true;

            try
            {
                in = resource.openStream();
                URL filteredResource = resource;
                final List<String> lines = IOUtils.readLines( in, "UTF-8" );
                final List<String> providerExcludes = Arrays.asList( getProviderExcludes().split( ":" ) );
                final List<String> filteredLines = new ArrayList<String>( lines.size() );

                for ( String line : lines )
                {
                    if ( !providerExcludes.contains( line.trim() ) )
                    {
                        filteredLines.add( line.trim() );
                    }
                    else
                    {
                        log( Level.FINE,
                             getExcludedProviderInfo( getLocale(), resource.toExternalForm(), line.toString() ), null );

                    }
                }

                if ( lines.size() != filteredLines.size() )
                {
                    OutputStream out = null;
                    final File tmpResource = File.createTempFile( this.getClass().getName(), ".rsrc" );
                    this.temporaryResources.add( tmpResource );

                    try
                    {
                        out = new FileOutputStream( tmpResource );
                        IOUtils.writeLines( filteredLines, System.getProperty( "line.separator", "\n" ), out, "UTF-8" );
                        suppressExceptionOnClose = false;
                    }
                    finally
                    {
                        try
                        {
                            if ( out != null )
                            {
                                out.close();
                            }

                            suppressExceptionOnClose = true;
                        }
                        catch ( final IOException e )
                        {
                            if ( suppressExceptionOnClose )
                            {
                                log( Level.SEVERE, getExceptionMessage( e ), e );
                            }
                            else
                            {
                                throw e;
                            }
                        }
                    }

                    filteredResource = tmpResource.toURI().toURL();
                }

                suppressExceptionOnClose = false;
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
                    if ( suppressExceptionOnClose )
                    {
                        log( Level.SEVERE, getExceptionMessage( e ), e );
                    }
                    else
                    {
                        throw e;
                    }
                }
            }
        }

        private URL filterModlets( final URL resource ) throws ModelException, IOException, JAXBException
        {
            URL filteredResource = resource;
            final List<String> excludedModletNames = Arrays.asList( getModletExcludes().split( ":" ) );
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
                             getExcludedModletInfo( getLocale(), resource.toExternalForm(), m.getName() ), null );

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

        private boolean filterModlet( final Modlet modlet, final String resourceInfo )
        {
            boolean filteredSchemas = false;
            boolean filteredServices = false;
            final List<String> excludedSchemas = Arrays.asList( getSchemaExcludes().split( ":" ) );
            final List<String> excludedServices = Arrays.asList( getServiceExcludes().split( ":" ) );

            if ( modlet.getSchemas() != null )
            {
                final Schemas schemas = new Schemas();

                for ( Schema s : modlet.getSchemas().getSchema() )
                {
                    if ( !excludedSchemas.contains( s.getContextId() ) )
                    {
                        schemas.getSchema().add( s );
                    }
                    else
                    {
                        log( Level.FINE, getExcludedSchemaInfo( getLocale(), resourceInfo, s.getContextId() ), null );
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
                    if ( !excludedServices.contains( s.getClazz() ) )
                    {
                        services.getService().add( s );
                    }
                    else
                    {
                        log( Level.FINE, getExcludedServiceInfo( getLocale(), resourceInfo, s.getClazz() ), null );
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

    }

    // SECTION-END
    // SECTION-START[Constructors]
    // <editor-fold defaultstate="collapsed" desc=" Generated Constructors ">
    /** Creates a new {@code AbstractModletCommand} instance. */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://www.jomc.org/jomc/2.0/jomc-tools-2.0-SNAPSHOT" )
    public AbstractModletCommand()
    {
        // SECTION-START[Default Constructor]
        super();
        // SECTION-END
    }
    // </editor-fold>
    // SECTION-END
    // SECTION-START[Dependencies]
    // <editor-fold defaultstate="collapsed" desc=" Generated Dependencies ">
    /**
     * Gets the {@code <Classpath Option>} dependency.
     * <p>
     *   This method returns the {@code <JOMC ⁑ CLI ⁑ Classpath Option>} object of the {@code <JOMC ⁑ CLI ⁑ Command Option>} specification at specification level 1.2.
     *   That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.
     * </p>
     * <dl>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl>
     * @return The {@code <Classpath Option>} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @SuppressWarnings("unused")
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://www.jomc.org/jomc/2.0/jomc-tools-2.0-SNAPSHOT" )
    private org.apache.commons.cli.Option getClasspathOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "Classpath Option" );
        assert _d != null : "'Classpath Option' dependency not found.";
        return _d;
    }
    /**
     * Gets the {@code <Documents Option>} dependency.
     * <p>
     *   This method returns the {@code <JOMC ⁑ CLI ⁑ Documents Option>} object of the {@code <JOMC ⁑ CLI ⁑ Command Option>} specification at specification level 1.2.
     *   That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.
     * </p>
     * <dl>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl>
     * @return The {@code <Documents Option>} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @SuppressWarnings("unused")
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://www.jomc.org/jomc/2.0/jomc-tools-2.0-SNAPSHOT" )
    private org.apache.commons.cli.Option getDocumentsOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "Documents Option" );
        assert _d != null : "'Documents Option' dependency not found.";
        return _d;
    }
    /**
     * Gets the {@code <Locale>} dependency.
     * <p>
     *   This method returns the {@code <default>} object of the {@code <java.util.Locale>} specification at specification level 1.1.
     *   That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.
     * </p>
     * <dl>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl>
     * @return The {@code <Locale>} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @SuppressWarnings("unused")
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://www.jomc.org/jomc/2.0/jomc-tools-2.0-SNAPSHOT" )
    private java.util.Locale getLocale()
    {
        final java.util.Locale _d = (java.util.Locale) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "Locale" );
        assert _d != null : "'Locale' dependency not found.";
        return _d;
    }
    /**
     * Gets the {@code <Model Context Factory Option>} dependency.
     * <p>
     *   This method returns the {@code <JOMC ⁑ CLI ⁑ ModelContextFactory Class Name Option>} object of the {@code <JOMC ⁑ CLI ⁑ Command Option>} specification at specification level 1.2.
     *   That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.
     * </p>
     * <dl>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl>
     * @return The {@code <Model Context Factory Option>} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @SuppressWarnings("unused")
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://www.jomc.org/jomc/2.0/jomc-tools-2.0-SNAPSHOT" )
    private org.apache.commons.cli.Option getModelContextFactoryOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "Model Context Factory Option" );
        assert _d != null : "'Model Context Factory Option' dependency not found.";
        return _d;
    }
    /**
     * Gets the {@code <Model Option>} dependency.
     * <p>
     *   This method returns the {@code <JOMC ⁑ CLI ⁑ Model Option>} object of the {@code <JOMC ⁑ CLI ⁑ Command Option>} specification at specification level 1.2.
     *   That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.
     * </p>
     * <dl>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl>
     * @return The {@code <Model Option>} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @SuppressWarnings("unused")
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://www.jomc.org/jomc/2.0/jomc-tools-2.0-SNAPSHOT" )
    private org.apache.commons.cli.Option getModelOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "Model Option" );
        assert _d != null : "'Model Option' dependency not found.";
        return _d;
    }
    /**
     * Gets the {@code <Modlet Location Option>} dependency.
     * <p>
     *   This method returns the {@code <JOMC ⁑ CLI ⁑ Modlet Location Option>} object of the {@code <JOMC ⁑ CLI ⁑ Command Option>} specification at specification level 1.2.
     *   That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.
     * </p>
     * <dl>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl>
     * @return The {@code <Modlet Location Option>} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @SuppressWarnings("unused")
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://www.jomc.org/jomc/2.0/jomc-tools-2.0-SNAPSHOT" )
    private org.apache.commons.cli.Option getModletLocationOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "Modlet Location Option" );
        assert _d != null : "'Modlet Location Option' dependency not found.";
        return _d;
    }
    /**
     * Gets the {@code <Modlet Schema System Id Option>} dependency.
     * <p>
     *   This method returns the {@code <JOMC ⁑ CLI ⁑ Modlet Schema System Id Option>} object of the {@code <JOMC ⁑ CLI ⁑ Command Option>} specification at specification level 1.2.
     *   That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.
     * </p>
     * <dl>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl>
     * @return The {@code <Modlet Schema System Id Option>} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @SuppressWarnings("unused")
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://www.jomc.org/jomc/2.0/jomc-tools-2.0-SNAPSHOT" )
    private org.apache.commons.cli.Option getModletSchemaSystemIdOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "Modlet Schema System Id Option" );
        assert _d != null : "'Modlet Schema System Id Option' dependency not found.";
        return _d;
    }
    /**
     * Gets the {@code <No Modlet Resource Validation>} dependency.
     * <p>
     *   This method returns the {@code <JOMC ⁑ CLI ⁑ No Modlet Resource Validation Option>} object of the {@code <JOMC ⁑ CLI ⁑ Command Option>} specification at specification level 1.2.
     *   That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.
     * </p>
     * <dl>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl>
     * @return The {@code <No Modlet Resource Validation>} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @SuppressWarnings("unused")
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://www.jomc.org/jomc/2.0/jomc-tools-2.0-SNAPSHOT" )
    private org.apache.commons.cli.Option getNoModletResourceValidation()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "No Modlet Resource Validation" );
        assert _d != null : "'No Modlet Resource Validation' dependency not found.";
        return _d;
    }
    /**
     * Gets the {@code <Platform Provider Location Option>} dependency.
     * <p>
     *   This method returns the {@code <JOMC ⁑ CLI ⁑ Platform Provider Location Option>} object of the {@code <JOMC ⁑ CLI ⁑ Command Option>} specification at specification level 1.2.
     *   That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.
     * </p>
     * <dl>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl>
     * @return The {@code <Platform Provider Location Option>} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @SuppressWarnings("unused")
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://www.jomc.org/jomc/2.0/jomc-tools-2.0-SNAPSHOT" )
    private org.apache.commons.cli.Option getPlatformProviderLocationOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "Platform Provider Location Option" );
        assert _d != null : "'Platform Provider Location Option' dependency not found.";
        return _d;
    }
    /**
     * Gets the {@code <Provider Location Option>} dependency.
     * <p>
     *   This method returns the {@code <JOMC ⁑ CLI ⁑ Provider Location Option>} object of the {@code <JOMC ⁑ CLI ⁑ Command Option>} specification at specification level 1.2.
     *   That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.
     * </p>
     * <dl>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl>
     * @return The {@code <Provider Location Option>} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @SuppressWarnings("unused")
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://www.jomc.org/jomc/2.0/jomc-tools-2.0-SNAPSHOT" )
    private org.apache.commons.cli.Option getProviderLocationOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "Provider Location Option" );
        assert _d != null : "'Provider Location Option' dependency not found.";
        return _d;
    }
    // </editor-fold>
    // SECTION-END
    // SECTION-START[Properties]
    // <editor-fold defaultstate="collapsed" desc=" Generated Properties ">
    /**
     * Gets the value of the {@code <Abbreviated Command Name>} property.
     * <p><dl>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl></p>
     * @return Abbreviated name of the command.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @SuppressWarnings("unused")
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://www.jomc.org/jomc/2.0/jomc-tools-2.0-SNAPSHOT" )
    private java.lang.String getAbbreviatedCommandName()
    {
        final java.lang.String _p = (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getProperty( this, "Abbreviated Command Name" );
        assert _p != null : "'Abbreviated Command Name' property not found.";
        return _p;
    }
    /**
     * Gets the value of the {@code <Application Modlet>} property.
     * <p><dl>
     *   <dt><b>Final:</b></dt><dd>Yes</dd>
     * </dl></p>
     * @return Name of the 'shaded' application modlet.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @SuppressWarnings("unused")
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://www.jomc.org/jomc/2.0/jomc-tools-2.0-SNAPSHOT" )
    private java.lang.String getApplicationModlet()
    {
        final java.lang.String _p = (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getProperty( this, "Application Modlet" );
        assert _p != null : "'Application Modlet' property not found.";
        return _p;
    }
    /**
     * Gets the value of the {@code <Command Name>} property.
     * <p><dl>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl></p>
     * @return Name of the command.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @SuppressWarnings("unused")
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://www.jomc.org/jomc/2.0/jomc-tools-2.0-SNAPSHOT" )
    private java.lang.String getCommandName()
    {
        final java.lang.String _p = (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getProperty( this, "Command Name" );
        assert _p != null : "'Command Name' property not found.";
        return _p;
    }
    /**
     * Gets the value of the {@code <Modlet Excludes>} property.
     * <p><dl>
     *   <dt><b>Final:</b></dt><dd>Yes</dd>
     * </dl></p>
     * @return List of modlet names to exclude from any {@code META-INF/jomc-modlet.xml} files separated by {@code :}.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @SuppressWarnings("unused")
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://www.jomc.org/jomc/2.0/jomc-tools-2.0-SNAPSHOT" )
    private java.lang.String getModletExcludes()
    {
        final java.lang.String _p = (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getProperty( this, "Modlet Excludes" );
        assert _p != null : "'Modlet Excludes' property not found.";
        return _p;
    }
    /**
     * Gets the value of the {@code <Provider Excludes>} property.
     * <p><dl>
     *   <dt><b>Final:</b></dt><dd>Yes</dd>
     * </dl></p>
     * @return List of providers to exclude from any {@code META-INF/services} files separated by {@code :}.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @SuppressWarnings("unused")
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://www.jomc.org/jomc/2.0/jomc-tools-2.0-SNAPSHOT" )
    private java.lang.String getProviderExcludes()
    {
        final java.lang.String _p = (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getProperty( this, "Provider Excludes" );
        assert _p != null : "'Provider Excludes' property not found.";
        return _p;
    }
    /**
     * Gets the value of the {@code <Schema Excludes>} property.
     * <p><dl>
     *   <dt><b>Final:</b></dt><dd>Yes</dd>
     * </dl></p>
     * @return List of schema context-ids to exclude from any {@code META-INF/jomc-modlet.xml} files separated by {@code :}.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @SuppressWarnings("unused")
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://www.jomc.org/jomc/2.0/jomc-tools-2.0-SNAPSHOT" )
    private java.lang.String getSchemaExcludes()
    {
        final java.lang.String _p = (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getProperty( this, "Schema Excludes" );
        assert _p != null : "'Schema Excludes' property not found.";
        return _p;
    }
    /**
     * Gets the value of the {@code <Service Excludes>} property.
     * <p><dl>
     *   <dt><b>Final:</b></dt><dd>Yes</dd>
     * </dl></p>
     * @return List of service classes to exclude from any {@code META-INF/jomc-modlet.xml} files separated by {@code :}.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @SuppressWarnings("unused")
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://www.jomc.org/jomc/2.0/jomc-tools-2.0-SNAPSHOT" )
    private java.lang.String getServiceExcludes()
    {
        final java.lang.String _p = (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getProperty( this, "Service Excludes" );
        assert _p != null : "'Service Excludes' property not found.";
        return _p;
    }
    // </editor-fold>
    // SECTION-END
    // SECTION-START[Messages]
    // <editor-fold defaultstate="collapsed" desc=" Generated Messages ">
    /**
     * Gets the text of the {@code <Application Title>} message.
     * <p><dl>
     *   <dt><b>Languages:</b></dt>
     *     <dd>English (default)</dd>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl></p>
     * @param locale The locale of the message to return.
     * @return The text of the {@code <Application Title>} message for {@code locale}.
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @SuppressWarnings("unused")
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://www.jomc.org/jomc/2.0/jomc-tools-2.0-SNAPSHOT" )
    private String getApplicationTitle( final java.util.Locale locale )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "Application Title", locale );
        assert _m != null : "'Application Title' message not found.";
        return _m;
    }
    /**
     * Gets the text of the {@code <Cannot Process Message>} message.
     * <p><dl>
     *   <dt><b>Languages:</b></dt>
     *     <dd>English (default)</dd>
     *     <dd>Deutsch</dd>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl></p>
     * @param locale The locale of the message to return.
     * @param itemInfo Format argument.
     * @param detailMessage Format argument.
     * @return The text of the {@code <Cannot Process Message>} message for {@code locale}.
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @SuppressWarnings("unused")
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://www.jomc.org/jomc/2.0/jomc-tools-2.0-SNAPSHOT" )
    private String getCannotProcessMessage( final java.util.Locale locale, final java.lang.String itemInfo, final java.lang.String detailMessage )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "Cannot Process Message", locale, itemInfo, detailMessage );
        assert _m != null : "'Cannot Process Message' message not found.";
        return _m;
    }
    /**
     * Gets the text of the {@code <Classpath Element Info>} message.
     * <p><dl>
     *   <dt><b>Languages:</b></dt>
     *     <dd>English (default)</dd>
     *     <dd>Deutsch</dd>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl></p>
     * @param locale The locale of the message to return.
     * @param classpathElement Format argument.
     * @return The text of the {@code <Classpath Element Info>} message for {@code locale}.
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @SuppressWarnings("unused")
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://www.jomc.org/jomc/2.0/jomc-tools-2.0-SNAPSHOT" )
    private String getClasspathElementInfo( final java.util.Locale locale, final java.lang.String classpathElement )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "Classpath Element Info", locale, classpathElement );
        assert _m != null : "'Classpath Element Info' message not found.";
        return _m;
    }
    /**
     * Gets the text of the {@code <Classpath Element Not Found Warning>} message.
     * <p><dl>
     *   <dt><b>Languages:</b></dt>
     *     <dd>English (default)</dd>
     *     <dd>Deutsch</dd>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl></p>
     * @param locale The locale of the message to return.
     * @param fileName Format argument.
     * @return The text of the {@code <Classpath Element Not Found Warning>} message for {@code locale}.
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @SuppressWarnings("unused")
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://www.jomc.org/jomc/2.0/jomc-tools-2.0-SNAPSHOT" )
    private String getClasspathElementNotFoundWarning( final java.util.Locale locale, final java.lang.String fileName )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "Classpath Element Not Found Warning", locale, fileName );
        assert _m != null : "'Classpath Element Not Found Warning' message not found.";
        return _m;
    }
    /**
     * Gets the text of the {@code <Command Failure Message>} message.
     * <p><dl>
     *   <dt><b>Languages:</b></dt>
     *     <dd>English (default)</dd>
     *     <dd>Deutsch</dd>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl></p>
     * @param locale The locale of the message to return.
     * @param toolName Format argument.
     * @return The text of the {@code <Command Failure Message>} message for {@code locale}.
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @SuppressWarnings("unused")
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://www.jomc.org/jomc/2.0/jomc-tools-2.0-SNAPSHOT" )
    private String getCommandFailureMessage( final java.util.Locale locale, final java.lang.String toolName )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "Command Failure Message", locale, toolName );
        assert _m != null : "'Command Failure Message' message not found.";
        return _m;
    }
    /**
     * Gets the text of the {@code <Command Info Message>} message.
     * <p><dl>
     *   <dt><b>Languages:</b></dt>
     *     <dd>English (default)</dd>
     *     <dd>Deutsch</dd>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl></p>
     * @param locale The locale of the message to return.
     * @param toolName Format argument.
     * @return The text of the {@code <Command Info Message>} message for {@code locale}.
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @SuppressWarnings("unused")
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://www.jomc.org/jomc/2.0/jomc-tools-2.0-SNAPSHOT" )
    private String getCommandInfoMessage( final java.util.Locale locale, final java.lang.String toolName )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "Command Info Message", locale, toolName );
        assert _m != null : "'Command Info Message' message not found.";
        return _m;
    }
    /**
     * Gets the text of the {@code <Command Success Message>} message.
     * <p><dl>
     *   <dt><b>Languages:</b></dt>
     *     <dd>English (default)</dd>
     *     <dd>Deutsch</dd>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl></p>
     * @param locale The locale of the message to return.
     * @param toolName Format argument.
     * @return The text of the {@code <Command Success Message>} message for {@code locale}.
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @SuppressWarnings("unused")
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://www.jomc.org/jomc/2.0/jomc-tools-2.0-SNAPSHOT" )
    private String getCommandSuccessMessage( final java.util.Locale locale, final java.lang.String toolName )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "Command Success Message", locale, toolName );
        assert _m != null : "'Command Success Message' message not found.";
        return _m;
    }
    /**
     * Gets the text of the {@code <Default Log Level Info>} message.
     * <p><dl>
     *   <dt><b>Languages:</b></dt>
     *     <dd>English (default)</dd>
     *     <dd>Deutsch</dd>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl></p>
     * @param locale The locale of the message to return.
     * @param defaultLogLevel Format argument.
     * @return The text of the {@code <Default Log Level Info>} message for {@code locale}.
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @SuppressWarnings("unused")
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://www.jomc.org/jomc/2.0/jomc-tools-2.0-SNAPSHOT" )
    private String getDefaultLogLevelInfo( final java.util.Locale locale, final java.lang.String defaultLogLevel )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "Default Log Level Info", locale, defaultLogLevel );
        assert _m != null : "'Default Log Level Info' message not found.";
        return _m;
    }
    /**
     * Gets the text of the {@code <Document File Info>} message.
     * <p><dl>
     *   <dt><b>Languages:</b></dt>
     *     <dd>English (default)</dd>
     *     <dd>Deutsch</dd>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl></p>
     * @param locale The locale of the message to return.
     * @param documentFile Format argument.
     * @return The text of the {@code <Document File Info>} message for {@code locale}.
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @SuppressWarnings("unused")
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://www.jomc.org/jomc/2.0/jomc-tools-2.0-SNAPSHOT" )
    private String getDocumentFileInfo( final java.util.Locale locale, final java.lang.String documentFile )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "Document File Info", locale, documentFile );
        assert _m != null : "'Document File Info' message not found.";
        return _m;
    }
    /**
     * Gets the text of the {@code <Document File Not Found Warning>} message.
     * <p><dl>
     *   <dt><b>Languages:</b></dt>
     *     <dd>English (default)</dd>
     *     <dd>Deutsch</dd>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl></p>
     * @param locale The locale of the message to return.
     * @param fileName Format argument.
     * @return The text of the {@code <Document File Not Found Warning>} message for {@code locale}.
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @SuppressWarnings("unused")
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://www.jomc.org/jomc/2.0/jomc-tools-2.0-SNAPSHOT" )
    private String getDocumentFileNotFoundWarning( final java.util.Locale locale, final java.lang.String fileName )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "Document File Not Found Warning", locale, fileName );
        assert _m != null : "'Document File Not Found Warning' message not found.";
        return _m;
    }
    /**
     * Gets the text of the {@code <Excluded Modlet Info>} message.
     * <p><dl>
     *   <dt><b>Languages:</b></dt>
     *     <dd>English (default)</dd>
     *     <dd>Deutsch</dd>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl></p>
     * @param locale The locale of the message to return.
     * @param resourceName Format argument.
     * @param modletIdentifier Format argument.
     * @return The text of the {@code <Excluded Modlet Info>} message for {@code locale}.
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @SuppressWarnings("unused")
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://www.jomc.org/jomc/2.0/jomc-tools-2.0-SNAPSHOT" )
    private String getExcludedModletInfo( final java.util.Locale locale, final java.lang.String resourceName, final java.lang.String modletIdentifier )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "Excluded Modlet Info", locale, resourceName, modletIdentifier );
        assert _m != null : "'Excluded Modlet Info' message not found.";
        return _m;
    }
    /**
     * Gets the text of the {@code <Excluded Provider Info>} message.
     * <p><dl>
     *   <dt><b>Languages:</b></dt>
     *     <dd>English (default)</dd>
     *     <dd>Deutsch</dd>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl></p>
     * @param locale The locale of the message to return.
     * @param resourceName Format argument.
     * @param providerName Format argument.
     * @return The text of the {@code <Excluded Provider Info>} message for {@code locale}.
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @SuppressWarnings("unused")
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://www.jomc.org/jomc/2.0/jomc-tools-2.0-SNAPSHOT" )
    private String getExcludedProviderInfo( final java.util.Locale locale, final java.lang.String resourceName, final java.lang.String providerName )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "Excluded Provider Info", locale, resourceName, providerName );
        assert _m != null : "'Excluded Provider Info' message not found.";
        return _m;
    }
    /**
     * Gets the text of the {@code <Excluded Schema Info>} message.
     * <p><dl>
     *   <dt><b>Languages:</b></dt>
     *     <dd>English (default)</dd>
     *     <dd>Deutsch</dd>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl></p>
     * @param locale The locale of the message to return.
     * @param resourceName Format argument.
     * @param contextId Format argument.
     * @return The text of the {@code <Excluded Schema Info>} message for {@code locale}.
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @SuppressWarnings("unused")
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://www.jomc.org/jomc/2.0/jomc-tools-2.0-SNAPSHOT" )
    private String getExcludedSchemaInfo( final java.util.Locale locale, final java.lang.String resourceName, final java.lang.String contextId )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "Excluded Schema Info", locale, resourceName, contextId );
        assert _m != null : "'Excluded Schema Info' message not found.";
        return _m;
    }
    /**
     * Gets the text of the {@code <Excluded Service Info>} message.
     * <p><dl>
     *   <dt><b>Languages:</b></dt>
     *     <dd>English (default)</dd>
     *     <dd>Deutsch</dd>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl></p>
     * @param locale The locale of the message to return.
     * @param resourceName Format argument.
     * @param serviceName Format argument.
     * @return The text of the {@code <Excluded Service Info>} message for {@code locale}.
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @SuppressWarnings("unused")
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://www.jomc.org/jomc/2.0/jomc-tools-2.0-SNAPSHOT" )
    private String getExcludedServiceInfo( final java.util.Locale locale, final java.lang.String resourceName, final java.lang.String serviceName )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "Excluded Service Info", locale, resourceName, serviceName );
        assert _m != null : "'Excluded Service Info' message not found.";
        return _m;
    }
    /**
     * Gets the text of the {@code <Invalid Model Message>} message.
     * <p><dl>
     *   <dt><b>Languages:</b></dt>
     *     <dd>English (default)</dd>
     *     <dd>Deutsch</dd>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl></p>
     * @param locale The locale of the message to return.
     * @param modelIdentifier Format argument.
     * @return The text of the {@code <Invalid Model Message>} message for {@code locale}.
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @SuppressWarnings("unused")
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://www.jomc.org/jomc/2.0/jomc-tools-2.0-SNAPSHOT" )
    private String getInvalidModelMessage( final java.util.Locale locale, final java.lang.String modelIdentifier )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "Invalid Model Message", locale, modelIdentifier );
        assert _m != null : "'Invalid Model Message' message not found.";
        return _m;
    }
    /**
     * Gets the text of the {@code <Long Description Message>} message.
     * <p><dl>
     *   <dt><b>Languages:</b></dt>
     *     <dd>English (default)</dd>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl></p>
     * @param locale The locale of the message to return.
     * @return The text of the {@code <Long Description Message>} message for {@code locale}.
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @SuppressWarnings("unused")
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://www.jomc.org/jomc/2.0/jomc-tools-2.0-SNAPSHOT" )
    private String getLongDescriptionMessage( final java.util.Locale locale )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "Long Description Message", locale );
        assert _m != null : "'Long Description Message' message not found.";
        return _m;
    }
    /**
     * Gets the text of the {@code <Reading Message>} message.
     * <p><dl>
     *   <dt><b>Languages:</b></dt>
     *     <dd>English (default)</dd>
     *     <dd>Deutsch</dd>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl></p>
     * @param locale The locale of the message to return.
     * @param locationInfo Format argument.
     * @return The text of the {@code <Reading Message>} message for {@code locale}.
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @SuppressWarnings("unused")
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://www.jomc.org/jomc/2.0/jomc-tools-2.0-SNAPSHOT" )
    private String getReadingMessage( final java.util.Locale locale, final java.lang.String locationInfo )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "Reading Message", locale, locationInfo );
        assert _m != null : "'Reading Message' message not found.";
        return _m;
    }
    /**
     * Gets the text of the {@code <Separator>} message.
     * <p><dl>
     *   <dt><b>Languages:</b></dt>
     *     <dd>English (default)</dd>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl></p>
     * @param locale The locale of the message to return.
     * @return The text of the {@code <Separator>} message for {@code locale}.
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @SuppressWarnings("unused")
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://www.jomc.org/jomc/2.0/jomc-tools-2.0-SNAPSHOT" )
    private String getSeparator( final java.util.Locale locale )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "Separator", locale );
        assert _m != null : "'Separator' message not found.";
        return _m;
    }
    /**
     * Gets the text of the {@code <Short Description Message>} message.
     * <p><dl>
     *   <dt><b>Languages:</b></dt>
     *     <dd>English (default)</dd>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl></p>
     * @param locale The locale of the message to return.
     * @return The text of the {@code <Short Description Message>} message for {@code locale}.
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @SuppressWarnings("unused")
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://www.jomc.org/jomc/2.0/jomc-tools-2.0-SNAPSHOT" )
    private String getShortDescriptionMessage( final java.util.Locale locale )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "Short Description Message", locale );
        assert _m != null : "'Short Description Message' message not found.";
        return _m;
    }
    // </editor-fold>
    // SECTION-END
    // SECTION-START[Generated Command]
    // <editor-fold defaultstate="collapsed" desc=" Generated Options ">
    /**
     * Gets the options of the command.
     * <p><strong>Options:</strong>
     *   <table border="1" width="100%" cellpadding="3" cellspacing="0">
     *     <tr class="TableSubHeadingColor">
     *       <th align="left" scope="col" nowrap><b>Specification</b></th>
     *       <th align="left" scope="col" nowrap><b>Implementation</b></th>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>JOMC ⁑ CLI ⁑ Command Option {@code (org.apache.commons.cli.Option)} @ 1.2</td>
     *       <td align="left" valign="top" nowrap>JOMC ⁑ CLI ⁑ Classpath Option</td>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>JOMC ⁑ CLI ⁑ Command Option {@code (org.apache.commons.cli.Option)} @ 1.2</td>
     *       <td align="left" valign="top" nowrap>JOMC ⁑ CLI ⁑ Documents Option</td>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>JOMC ⁑ CLI ⁑ Command Option {@code (org.apache.commons.cli.Option)} @ 1.2</td>
     *       <td align="left" valign="top" nowrap>JOMC ⁑ CLI ⁑ ModelContextFactory Class Name Option</td>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>JOMC ⁑ CLI ⁑ Command Option {@code (org.apache.commons.cli.Option)} @ 1.2</td>
     *       <td align="left" valign="top" nowrap>JOMC ⁑ CLI ⁑ Model Option</td>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>JOMC ⁑ CLI ⁑ Command Option {@code (org.apache.commons.cli.Option)} @ 1.2</td>
     *       <td align="left" valign="top" nowrap>JOMC ⁑ CLI ⁑ Modlet Location Option</td>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>JOMC ⁑ CLI ⁑ Command Option {@code (org.apache.commons.cli.Option)} @ 1.2</td>
     *       <td align="left" valign="top" nowrap>JOMC ⁑ CLI ⁑ Modlet Schema System Id Option</td>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>JOMC ⁑ CLI ⁑ Command Option {@code (org.apache.commons.cli.Option)} @ 1.2</td>
     *       <td align="left" valign="top" nowrap>JOMC ⁑ CLI ⁑ No Modlet Resource Validation Option</td>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>JOMC ⁑ CLI ⁑ Command Option {@code (org.apache.commons.cli.Option)} @ 1.2</td>
     *       <td align="left" valign="top" nowrap>JOMC ⁑ CLI ⁑ Platform Provider Location Option</td>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>JOMC ⁑ CLI ⁑ Command Option {@code (org.apache.commons.cli.Option)} @ 1.2</td>
     *       <td align="left" valign="top" nowrap>JOMC ⁑ CLI ⁑ Provider Location Option</td>
     *     </tr>
     *   </table>
     * </p>
     * @return The options of the command.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://www.jomc.org/jomc/2.0/jomc-tools-2.0-SNAPSHOT" )
    @Override
    public org.apache.commons.cli.Options getOptions()
    {
        final org.apache.commons.cli.Options options = new org.apache.commons.cli.Options();
        options.addOption( this.getClasspathOption() );
        options.addOption( this.getDocumentsOption() );
        options.addOption( this.getModelContextFactoryOption() );
        options.addOption( this.getModelOption() );
        options.addOption( this.getModletLocationOption() );
        options.addOption( this.getModletSchemaSystemIdOption() );
        options.addOption( this.getNoModletResourceValidation() );
        options.addOption( this.getPlatformProviderLocationOption() );
        options.addOption( this.getProviderLocationOption() );
        return options;
    }
    // </editor-fold>
    // SECTION-END
}
