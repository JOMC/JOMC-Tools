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
import java.lang.reflect.InvocationTargetException;
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
 * JOMC CLI modlet based command implementation.
 *
 * <dl>
 *   <dt><b>Identifier:</b></dt><dd>JOMC CLI Modlet Command</dd>
 *   <dt><b>Name:</b></dt><dd>JOMC CLI Modlet Command</dd>
 *   <dt><b>Specifications:</b></dt>
 *     <dd>JOMC CLI Command @ 1.0</dd>
 *   <dt><b>Abstract:</b></dt><dd>Yes</dd>
 *   <dt><b>Final:</b></dt><dd>No</dd>
 *   <dt><b>Stateless:</b></dt><dd>No</dd>
 * </dl>
 *
 * @author <a href="mailto:schulte2005@users.sourceforge.net">Christian Schulte</a> 1.0
 * @version 2.0-SNAPSHOT
 */
// </editor-fold>
// SECTION-END
// SECTION-START[Annotations]
// <editor-fold defaultstate="collapsed" desc=" Generated Annotations ">
@javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/2.0/jomc-tools-2.0-SNAPSHOT" )
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
     * @author <a href="mailto:schulte2005@users.sourceforge.net">Christian Schulte</a>
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
            { // JDK: As of JDK 7, super.close();
                this.jdk7Close();
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

        private void jdk7Close()
        {
            try
            {
                URLClassLoader.class.getMethod( "close" ).invoke( this );
            }
            catch ( final NoSuchMethodException e )
            {
                log( Level.FINEST, null, e );
            }
            catch ( final IllegalAccessException e )
            {
                log( Level.FINEST, null, e );
            }
            catch ( final InvocationTargetException e )
            {
                log( Level.FINEST, null, e );
            }
        }

    }

    // SECTION-END
    // SECTION-START[Constructors]
    // <editor-fold defaultstate="collapsed" desc=" Generated Constructors ">
    /** Creates a new {@code AbstractModletCommand} instance. */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/2.0/jomc-tools-2.0-SNAPSHOT" )
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
     * Gets the {@code <ClasspathOption>} dependency.
     * <p>
     *   This method returns the {@code <JOMC CLI Classpath Option>} object of the {@code <JOMC CLI Command Option>} specification at specification level 1.2.
     *   That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.
     * </p>
     * <dl>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl>
     * @return The {@code <ClasspathOption>} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/2.0/jomc-tools-2.0-SNAPSHOT" )
    private org.apache.commons.cli.Option getClasspathOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "ClasspathOption" );
        assert _d != null : "'ClasspathOption' dependency not found.";
        return _d;
    }
    /**
     * Gets the {@code <DocumentsOption>} dependency.
     * <p>
     *   This method returns the {@code <JOMC CLI Documents Option>} object of the {@code <JOMC CLI Command Option>} specification at specification level 1.2.
     *   That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.
     * </p>
     * <dl>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl>
     * @return The {@code <DocumentsOption>} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/2.0/jomc-tools-2.0-SNAPSHOT" )
    private org.apache.commons.cli.Option getDocumentsOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "DocumentsOption" );
        assert _d != null : "'DocumentsOption' dependency not found.";
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
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/2.0/jomc-tools-2.0-SNAPSHOT" )
    private java.util.Locale getLocale()
    {
        final java.util.Locale _d = (java.util.Locale) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "Locale" );
        assert _d != null : "'Locale' dependency not found.";
        return _d;
    }
    /**
     * Gets the {@code <ModelContextFactoryOption>} dependency.
     * <p>
     *   This method returns the {@code <JOMC CLI ModelContextFactory Class Name Option>} object of the {@code <JOMC CLI Command Option>} specification at specification level 1.2.
     *   That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.
     * </p>
     * <dl>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl>
     * @return The {@code <ModelContextFactoryOption>} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/2.0/jomc-tools-2.0-SNAPSHOT" )
    private org.apache.commons.cli.Option getModelContextFactoryOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "ModelContextFactoryOption" );
        assert _d != null : "'ModelContextFactoryOption' dependency not found.";
        return _d;
    }
    /**
     * Gets the {@code <ModelOption>} dependency.
     * <p>
     *   This method returns the {@code <JOMC CLI Model Option>} object of the {@code <JOMC CLI Command Option>} specification at specification level 1.2.
     *   That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.
     * </p>
     * <dl>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl>
     * @return The {@code <ModelOption>} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/2.0/jomc-tools-2.0-SNAPSHOT" )
    private org.apache.commons.cli.Option getModelOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "ModelOption" );
        assert _d != null : "'ModelOption' dependency not found.";
        return _d;
    }
    /**
     * Gets the {@code <ModletLocationOption>} dependency.
     * <p>
     *   This method returns the {@code <JOMC CLI Modlet Location Option>} object of the {@code <JOMC CLI Command Option>} specification at specification level 1.2.
     *   That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.
     * </p>
     * <dl>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl>
     * @return The {@code <ModletLocationOption>} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/2.0/jomc-tools-2.0-SNAPSHOT" )
    private org.apache.commons.cli.Option getModletLocationOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "ModletLocationOption" );
        assert _d != null : "'ModletLocationOption' dependency not found.";
        return _d;
    }
    /**
     * Gets the {@code <ModletSchemaSystemIdOption>} dependency.
     * <p>
     *   This method returns the {@code <JOMC CLI Modlet Schema System Id Option>} object of the {@code <JOMC CLI Command Option>} specification at specification level 1.2.
     *   That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.
     * </p>
     * <dl>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl>
     * @return The {@code <ModletSchemaSystemIdOption>} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/2.0/jomc-tools-2.0-SNAPSHOT" )
    private org.apache.commons.cli.Option getModletSchemaSystemIdOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "ModletSchemaSystemIdOption" );
        assert _d != null : "'ModletSchemaSystemIdOption' dependency not found.";
        return _d;
    }
    /**
     * Gets the {@code <NoModletResourceValidation>} dependency.
     * <p>
     *   This method returns the {@code <JOMC CLI No Modlet Resource Validation Option>} object of the {@code <JOMC CLI Command Option>} specification at specification level 1.2.
     *   That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.
     * </p>
     * <dl>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl>
     * @return The {@code <NoModletResourceValidation>} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/2.0/jomc-tools-2.0-SNAPSHOT" )
    private org.apache.commons.cli.Option getNoModletResourceValidation()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "NoModletResourceValidation" );
        assert _d != null : "'NoModletResourceValidation' dependency not found.";
        return _d;
    }
    /**
     * Gets the {@code <PlatformProviderLocationOption>} dependency.
     * <p>
     *   This method returns the {@code <JOMC CLI Platform Provider Location Option>} object of the {@code <JOMC CLI Command Option>} specification at specification level 1.2.
     *   That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.
     * </p>
     * <dl>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl>
     * @return The {@code <PlatformProviderLocationOption>} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/2.0/jomc-tools-2.0-SNAPSHOT" )
    private org.apache.commons.cli.Option getPlatformProviderLocationOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "PlatformProviderLocationOption" );
        assert _d != null : "'PlatformProviderLocationOption' dependency not found.";
        return _d;
    }
    /**
     * Gets the {@code <ProviderLocationOption>} dependency.
     * <p>
     *   This method returns the {@code <JOMC CLI Provider Location Option>} object of the {@code <JOMC CLI Command Option>} specification at specification level 1.2.
     *   That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.
     * </p>
     * <dl>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl>
     * @return The {@code <ProviderLocationOption>} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/2.0/jomc-tools-2.0-SNAPSHOT" )
    private org.apache.commons.cli.Option getProviderLocationOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "ProviderLocationOption" );
        assert _d != null : "'ProviderLocationOption' dependency not found.";
        return _d;
    }
    // </editor-fold>
    // SECTION-END
    // SECTION-START[Properties]
    // <editor-fold defaultstate="collapsed" desc=" Generated Properties ">
    /**
     * Gets the value of the {@code <abbreviatedCommandName>} property.
     * <p><dl>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl></p>
     * @return Abbreviated name of the command.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/2.0/jomc-tools-2.0-SNAPSHOT" )
    private java.lang.String getAbbreviatedCommandName()
    {
        final java.lang.String _p = (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getProperty( this, "abbreviatedCommandName" );
        assert _p != null : "'abbreviatedCommandName' property not found.";
        return _p;
    }
    /**
     * Gets the value of the {@code <applicationModlet>} property.
     * <p><dl>
     *   <dt><b>Final:</b></dt><dd>Yes</dd>
     * </dl></p>
     * @return Name of the 'shaded' application modlet.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/2.0/jomc-tools-2.0-SNAPSHOT" )
    private java.lang.String getApplicationModlet()
    {
        final java.lang.String _p = (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getProperty( this, "applicationModlet" );
        assert _p != null : "'applicationModlet' property not found.";
        return _p;
    }
    /**
     * Gets the value of the {@code <commandName>} property.
     * <p><dl>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl></p>
     * @return Name of the command.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/2.0/jomc-tools-2.0-SNAPSHOT" )
    private java.lang.String getCommandName()
    {
        final java.lang.String _p = (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getProperty( this, "commandName" );
        assert _p != null : "'commandName' property not found.";
        return _p;
    }
    /**
     * Gets the value of the {@code <modletExcludes>} property.
     * <p><dl>
     *   <dt><b>Final:</b></dt><dd>Yes</dd>
     * </dl></p>
     * @return List of modlet names to exclude from any {@code META-INF/jomc-modlet.xml} files separated by {@code :}.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/2.0/jomc-tools-2.0-SNAPSHOT" )
    private java.lang.String getModletExcludes()
    {
        final java.lang.String _p = (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getProperty( this, "modletExcludes" );
        assert _p != null : "'modletExcludes' property not found.";
        return _p;
    }
    /**
     * Gets the value of the {@code <providerExcludes>} property.
     * <p><dl>
     *   <dt><b>Final:</b></dt><dd>Yes</dd>
     * </dl></p>
     * @return List of providers to exclude from any {@code META-INF/services} files separated by {@code :}.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/2.0/jomc-tools-2.0-SNAPSHOT" )
    private java.lang.String getProviderExcludes()
    {
        final java.lang.String _p = (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getProperty( this, "providerExcludes" );
        assert _p != null : "'providerExcludes' property not found.";
        return _p;
    }
    /**
     * Gets the value of the {@code <schemaExcludes>} property.
     * <p><dl>
     *   <dt><b>Final:</b></dt><dd>Yes</dd>
     * </dl></p>
     * @return List of schema context-ids to exclude from any {@code META-INF/jomc-modlet.xml} files separated by {@code :}.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/2.0/jomc-tools-2.0-SNAPSHOT" )
    private java.lang.String getSchemaExcludes()
    {
        final java.lang.String _p = (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getProperty( this, "schemaExcludes" );
        assert _p != null : "'schemaExcludes' property not found.";
        return _p;
    }
    /**
     * Gets the value of the {@code <serviceExcludes>} property.
     * <p><dl>
     *   <dt><b>Final:</b></dt><dd>Yes</dd>
     * </dl></p>
     * @return List of service classes to exclude from any {@code META-INF/jomc-modlet.xml} files separated by {@code :}.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/2.0/jomc-tools-2.0-SNAPSHOT" )
    private java.lang.String getServiceExcludes()
    {
        final java.lang.String _p = (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getProperty( this, "serviceExcludes" );
        assert _p != null : "'serviceExcludes' property not found.";
        return _p;
    }
    // </editor-fold>
    // SECTION-END
    // SECTION-START[Messages]
    // <editor-fold defaultstate="collapsed" desc=" Generated Messages ">
    /**
     * Gets the text of the {@code <applicationTitle>} message.
     * <p><dl>
     *   <dt><b>Languages:</b></dt>
     *     <dd>English (default)</dd>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl></p>
     * @param locale The locale of the message to return.
     * @return The text of the {@code <applicationTitle>} message for {@code locale}.
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/2.0/jomc-tools-2.0-SNAPSHOT" )
    private String getApplicationTitle( final java.util.Locale locale )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "applicationTitle", locale );
        assert _m != null : "'applicationTitle' message not found.";
        return _m;
    }
    /**
     * Gets the text of the {@code <cannotProcessMessage>} message.
     * <p><dl>
     *   <dt><b>Languages:</b></dt>
     *     <dd>English (default)</dd>
     *     <dd>Deutsch</dd>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl></p>
     * @param locale The locale of the message to return.
     * @param itemInfo Format argument.
     * @param detailMessage Format argument.
     * @return The text of the {@code <cannotProcessMessage>} message for {@code locale}.
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/2.0/jomc-tools-2.0-SNAPSHOT" )
    private String getCannotProcessMessage( final java.util.Locale locale, final java.lang.String itemInfo, final java.lang.String detailMessage )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "cannotProcessMessage", locale, itemInfo, detailMessage );
        assert _m != null : "'cannotProcessMessage' message not found.";
        return _m;
    }
    /**
     * Gets the text of the {@code <classpathElementInfo>} message.
     * <p><dl>
     *   <dt><b>Languages:</b></dt>
     *     <dd>English (default)</dd>
     *     <dd>Deutsch</dd>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl></p>
     * @param locale The locale of the message to return.
     * @param classpathElement Format argument.
     * @return The text of the {@code <classpathElementInfo>} message for {@code locale}.
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/2.0/jomc-tools-2.0-SNAPSHOT" )
    private String getClasspathElementInfo( final java.util.Locale locale, final java.lang.String classpathElement )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "classpathElementInfo", locale, classpathElement );
        assert _m != null : "'classpathElementInfo' message not found.";
        return _m;
    }
    /**
     * Gets the text of the {@code <classpathElementNotFoundWarning>} message.
     * <p><dl>
     *   <dt><b>Languages:</b></dt>
     *     <dd>English (default)</dd>
     *     <dd>Deutsch</dd>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl></p>
     * @param locale The locale of the message to return.
     * @param fileName Format argument.
     * @return The text of the {@code <classpathElementNotFoundWarning>} message for {@code locale}.
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/2.0/jomc-tools-2.0-SNAPSHOT" )
    private String getClasspathElementNotFoundWarning( final java.util.Locale locale, final java.lang.String fileName )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "classpathElementNotFoundWarning", locale, fileName );
        assert _m != null : "'classpathElementNotFoundWarning' message not found.";
        return _m;
    }
    /**
     * Gets the text of the {@code <commandFailureMessage>} message.
     * <p><dl>
     *   <dt><b>Languages:</b></dt>
     *     <dd>English (default)</dd>
     *     <dd>Deutsch</dd>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl></p>
     * @param locale The locale of the message to return.
     * @param toolName Format argument.
     * @return The text of the {@code <commandFailureMessage>} message for {@code locale}.
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/2.0/jomc-tools-2.0-SNAPSHOT" )
    private String getCommandFailureMessage( final java.util.Locale locale, final java.lang.String toolName )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "commandFailureMessage", locale, toolName );
        assert _m != null : "'commandFailureMessage' message not found.";
        return _m;
    }
    /**
     * Gets the text of the {@code <commandInfoMessage>} message.
     * <p><dl>
     *   <dt><b>Languages:</b></dt>
     *     <dd>English (default)</dd>
     *     <dd>Deutsch</dd>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl></p>
     * @param locale The locale of the message to return.
     * @param toolName Format argument.
     * @return The text of the {@code <commandInfoMessage>} message for {@code locale}.
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/2.0/jomc-tools-2.0-SNAPSHOT" )
    private String getCommandInfoMessage( final java.util.Locale locale, final java.lang.String toolName )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "commandInfoMessage", locale, toolName );
        assert _m != null : "'commandInfoMessage' message not found.";
        return _m;
    }
    /**
     * Gets the text of the {@code <commandSuccessMessage>} message.
     * <p><dl>
     *   <dt><b>Languages:</b></dt>
     *     <dd>English (default)</dd>
     *     <dd>Deutsch</dd>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl></p>
     * @param locale The locale of the message to return.
     * @param toolName Format argument.
     * @return The text of the {@code <commandSuccessMessage>} message for {@code locale}.
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/2.0/jomc-tools-2.0-SNAPSHOT" )
    private String getCommandSuccessMessage( final java.util.Locale locale, final java.lang.String toolName )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "commandSuccessMessage", locale, toolName );
        assert _m != null : "'commandSuccessMessage' message not found.";
        return _m;
    }
    /**
     * Gets the text of the {@code <defaultLogLevelInfo>} message.
     * <p><dl>
     *   <dt><b>Languages:</b></dt>
     *     <dd>English (default)</dd>
     *     <dd>Deutsch</dd>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl></p>
     * @param locale The locale of the message to return.
     * @param defaultLogLevel Format argument.
     * @return The text of the {@code <defaultLogLevelInfo>} message for {@code locale}.
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/2.0/jomc-tools-2.0-SNAPSHOT" )
    private String getDefaultLogLevelInfo( final java.util.Locale locale, final java.lang.String defaultLogLevel )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "defaultLogLevelInfo", locale, defaultLogLevel );
        assert _m != null : "'defaultLogLevelInfo' message not found.";
        return _m;
    }
    /**
     * Gets the text of the {@code <documentFileInfo>} message.
     * <p><dl>
     *   <dt><b>Languages:</b></dt>
     *     <dd>English (default)</dd>
     *     <dd>Deutsch</dd>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl></p>
     * @param locale The locale of the message to return.
     * @param documentFile Format argument.
     * @return The text of the {@code <documentFileInfo>} message for {@code locale}.
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/2.0/jomc-tools-2.0-SNAPSHOT" )
    private String getDocumentFileInfo( final java.util.Locale locale, final java.lang.String documentFile )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "documentFileInfo", locale, documentFile );
        assert _m != null : "'documentFileInfo' message not found.";
        return _m;
    }
    /**
     * Gets the text of the {@code <documentFileNotFoundWarning>} message.
     * <p><dl>
     *   <dt><b>Languages:</b></dt>
     *     <dd>English (default)</dd>
     *     <dd>Deutsch</dd>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl></p>
     * @param locale The locale of the message to return.
     * @param fileName Format argument.
     * @return The text of the {@code <documentFileNotFoundWarning>} message for {@code locale}.
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/2.0/jomc-tools-2.0-SNAPSHOT" )
    private String getDocumentFileNotFoundWarning( final java.util.Locale locale, final java.lang.String fileName )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "documentFileNotFoundWarning", locale, fileName );
        assert _m != null : "'documentFileNotFoundWarning' message not found.";
        return _m;
    }
    /**
     * Gets the text of the {@code <excludedModletInfo>} message.
     * <p><dl>
     *   <dt><b>Languages:</b></dt>
     *     <dd>English (default)</dd>
     *     <dd>Deutsch</dd>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl></p>
     * @param locale The locale of the message to return.
     * @param resourceName Format argument.
     * @param modletIdentifier Format argument.
     * @return The text of the {@code <excludedModletInfo>} message for {@code locale}.
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/2.0/jomc-tools-2.0-SNAPSHOT" )
    private String getExcludedModletInfo( final java.util.Locale locale, final java.lang.String resourceName, final java.lang.String modletIdentifier )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "excludedModletInfo", locale, resourceName, modletIdentifier );
        assert _m != null : "'excludedModletInfo' message not found.";
        return _m;
    }
    /**
     * Gets the text of the {@code <excludedProviderInfo>} message.
     * <p><dl>
     *   <dt><b>Languages:</b></dt>
     *     <dd>English (default)</dd>
     *     <dd>Deutsch</dd>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl></p>
     * @param locale The locale of the message to return.
     * @param resourceName Format argument.
     * @param providerName Format argument.
     * @return The text of the {@code <excludedProviderInfo>} message for {@code locale}.
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/2.0/jomc-tools-2.0-SNAPSHOT" )
    private String getExcludedProviderInfo( final java.util.Locale locale, final java.lang.String resourceName, final java.lang.String providerName )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "excludedProviderInfo", locale, resourceName, providerName );
        assert _m != null : "'excludedProviderInfo' message not found.";
        return _m;
    }
    /**
     * Gets the text of the {@code <excludedSchemaInfo>} message.
     * <p><dl>
     *   <dt><b>Languages:</b></dt>
     *     <dd>English (default)</dd>
     *     <dd>Deutsch</dd>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl></p>
     * @param locale The locale of the message to return.
     * @param resourceName Format argument.
     * @param contextId Format argument.
     * @return The text of the {@code <excludedSchemaInfo>} message for {@code locale}.
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/2.0/jomc-tools-2.0-SNAPSHOT" )
    private String getExcludedSchemaInfo( final java.util.Locale locale, final java.lang.String resourceName, final java.lang.String contextId )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "excludedSchemaInfo", locale, resourceName, contextId );
        assert _m != null : "'excludedSchemaInfo' message not found.";
        return _m;
    }
    /**
     * Gets the text of the {@code <excludedServiceInfo>} message.
     * <p><dl>
     *   <dt><b>Languages:</b></dt>
     *     <dd>English (default)</dd>
     *     <dd>Deutsch</dd>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl></p>
     * @param locale The locale of the message to return.
     * @param resourceName Format argument.
     * @param serviceName Format argument.
     * @return The text of the {@code <excludedServiceInfo>} message for {@code locale}.
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/2.0/jomc-tools-2.0-SNAPSHOT" )
    private String getExcludedServiceInfo( final java.util.Locale locale, final java.lang.String resourceName, final java.lang.String serviceName )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "excludedServiceInfo", locale, resourceName, serviceName );
        assert _m != null : "'excludedServiceInfo' message not found.";
        return _m;
    }
    /**
     * Gets the text of the {@code <invalidModelMessage>} message.
     * <p><dl>
     *   <dt><b>Languages:</b></dt>
     *     <dd>English (default)</dd>
     *     <dd>Deutsch</dd>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl></p>
     * @param locale The locale of the message to return.
     * @param modelIdentifier Format argument.
     * @return The text of the {@code <invalidModelMessage>} message for {@code locale}.
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/2.0/jomc-tools-2.0-SNAPSHOT" )
    private String getInvalidModelMessage( final java.util.Locale locale, final java.lang.String modelIdentifier )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "invalidModelMessage", locale, modelIdentifier );
        assert _m != null : "'invalidModelMessage' message not found.";
        return _m;
    }
    /**
     * Gets the text of the {@code <longDescriptionMessage>} message.
     * <p><dl>
     *   <dt><b>Languages:</b></dt>
     *     <dd>English (default)</dd>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl></p>
     * @param locale The locale of the message to return.
     * @return The text of the {@code <longDescriptionMessage>} message for {@code locale}.
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/2.0/jomc-tools-2.0-SNAPSHOT" )
    private String getLongDescriptionMessage( final java.util.Locale locale )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "longDescriptionMessage", locale );
        assert _m != null : "'longDescriptionMessage' message not found.";
        return _m;
    }
    /**
     * Gets the text of the {@code <readingMessage>} message.
     * <p><dl>
     *   <dt><b>Languages:</b></dt>
     *     <dd>English (default)</dd>
     *     <dd>Deutsch</dd>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl></p>
     * @param locale The locale of the message to return.
     * @param locationInfo Format argument.
     * @return The text of the {@code <readingMessage>} message for {@code locale}.
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/2.0/jomc-tools-2.0-SNAPSHOT" )
    private String getReadingMessage( final java.util.Locale locale, final java.lang.String locationInfo )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "readingMessage", locale, locationInfo );
        assert _m != null : "'readingMessage' message not found.";
        return _m;
    }
    /**
     * Gets the text of the {@code <separator>} message.
     * <p><dl>
     *   <dt><b>Languages:</b></dt>
     *     <dd>English (default)</dd>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl></p>
     * @param locale The locale of the message to return.
     * @return The text of the {@code <separator>} message for {@code locale}.
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/2.0/jomc-tools-2.0-SNAPSHOT" )
    private String getSeparator( final java.util.Locale locale )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "separator", locale );
        assert _m != null : "'separator' message not found.";
        return _m;
    }
    /**
     * Gets the text of the {@code <shortDescriptionMessage>} message.
     * <p><dl>
     *   <dt><b>Languages:</b></dt>
     *     <dd>English (default)</dd>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl></p>
     * @param locale The locale of the message to return.
     * @return The text of the {@code <shortDescriptionMessage>} message for {@code locale}.
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/2.0/jomc-tools-2.0-SNAPSHOT" )
    private String getShortDescriptionMessage( final java.util.Locale locale )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "shortDescriptionMessage", locale );
        assert _m != null : "'shortDescriptionMessage' message not found.";
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
     *       <td align="left" valign="top" nowrap>JOMC CLI Command Option {@code (org.apache.commons.cli.Option)} @ 1.2</td>
     *       <td align="left" valign="top" nowrap>JOMC CLI Classpath Option</td>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>JOMC CLI Command Option {@code (org.apache.commons.cli.Option)} @ 1.2</td>
     *       <td align="left" valign="top" nowrap>JOMC CLI Documents Option</td>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>JOMC CLI Command Option {@code (org.apache.commons.cli.Option)} @ 1.2</td>
     *       <td align="left" valign="top" nowrap>JOMC CLI ModelContextFactory Class Name Option</td>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>JOMC CLI Command Option {@code (org.apache.commons.cli.Option)} @ 1.2</td>
     *       <td align="left" valign="top" nowrap>JOMC CLI Model Option</td>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>JOMC CLI Command Option {@code (org.apache.commons.cli.Option)} @ 1.2</td>
     *       <td align="left" valign="top" nowrap>JOMC CLI Modlet Location Option</td>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>JOMC CLI Command Option {@code (org.apache.commons.cli.Option)} @ 1.2</td>
     *       <td align="left" valign="top" nowrap>JOMC CLI Modlet Schema System Id Option</td>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>JOMC CLI Command Option {@code (org.apache.commons.cli.Option)} @ 1.2</td>
     *       <td align="left" valign="top" nowrap>JOMC CLI No Modlet Resource Validation Option</td>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>JOMC CLI Command Option {@code (org.apache.commons.cli.Option)} @ 1.2</td>
     *       <td align="left" valign="top" nowrap>JOMC CLI Platform Provider Location Option</td>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>JOMC CLI Command Option {@code (org.apache.commons.cli.Option)} @ 1.2</td>
     *       <td align="left" valign="top" nowrap>JOMC CLI Provider Location Option</td>
     *     </tr>
     *   </table>
     * </p>
     * @return The options of the command.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/2.0/jomc-tools-2.0-SNAPSHOT" )
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
