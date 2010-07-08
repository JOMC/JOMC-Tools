// SECTION-START[License Header]
// <editor-fold defaultstate="collapsed" desc=" Generated License ">
/*
 *   Copyright (c) 2010 The JOMC Project
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
// </editor-fold>
// SECTION-END
package org.jomc.cli.commands;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.logging.Level;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.io.IOUtils;
import org.jomc.cli.Command;
import org.jomc.model.ModelObject;
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

// SECTION-START[Documentation]
// <editor-fold defaultstate="collapsed" desc=" Generated Documentation ">
/**
 * Base JOMC {@code Command} implementation.
 * <p><b>Specifications</b><ul>
 * <li>{@code 'JOMC CLI Command'} {@code (org.jomc.cli.Command)} {@code 1.0} {@code Multiton}</li>
 * </ul></p>
 * <p><b>Properties</b><ul>
 * <li>"{@link #getAbbreviatedCommandName abbreviatedCommandName}"
 * <blockquote>Property of type {@code java.lang.String}.
 * <p>Abbreviated name of the command.</p>
 * </blockquote></li>
 * <li>"{@link #getCommandName commandName}"
 * <blockquote>Property of type {@code java.lang.String}.
 * <p>Name of the command.</p>
 * </blockquote></li>
 * <li>"{@link #getModletExcludes modletExcludes}"
 * <blockquote>Property of type {@code java.lang.String}.
 * <p>List of modlet names to exclude from any {@code META-INF/jomc-modlet.xml} file separated by {@code :}.</p>
 * </blockquote></li>
 * <li>"{@link #getProviderExcludes providerExcludes}"
 * <blockquote>Property of type {@code java.lang.String}.
 * <p>List of providers to exclude from any {@code META-INF/services} file separated by {@code :}.</p>
 * </blockquote></li>
 * <li>"{@link #getSchemaExcludes schemaExcludes}"
 * <blockquote>Property of type {@code java.lang.String}.
 * <p>List of schema context-ids to exclude from any {@code META-INF/jomc-modlet.xml} file separated by {@code :}.</p>
 * </blockquote></li>
 * <li>"{@link #getServiceExcludes serviceExcludes}"
 * <blockquote>Property of type {@code java.lang.String}.
 * <p>List of service classes to exclude from any {@code META-INF/jomc-modlet.xml} file separated by {@code :}.</p>
 * </blockquote></li>
 * </ul></p>
 * <p><b>Dependencies</b><ul>
 * <li>"{@link #getClasspathOption ClasspathOption}"<blockquote>
 * Dependency on {@code 'org.apache.commons.cli.Option'} {@code (org.apache.commons.cli.Option)} bound to an instance.</blockquote></li>
 * <li>"{@link #getLocale Locale}"<blockquote>
 * Dependency on {@code 'java.util.Locale'} {@code (java.util.Locale)} at specification level 1.1 bound to an instance.</blockquote></li>
 * <li>"{@link #getModelOption ModelOption}"<blockquote>
 * Dependency on {@code 'org.apache.commons.cli.Option'} {@code (org.apache.commons.cli.Option)} bound to an instance.</blockquote></li>
 * <li>"{@link #getModletLocationOption ModletLocationOption}"<blockquote>
 * Dependency on {@code 'org.apache.commons.cli.Option'} {@code (org.apache.commons.cli.Option)} bound to an instance.</blockquote></li>
 * <li>"{@link #getPlatformProviderLocationOption PlatformProviderLocationOption}"<blockquote>
 * Dependency on {@code 'org.apache.commons.cli.Option'} {@code (org.apache.commons.cli.Option)} bound to an instance.</blockquote></li>
 * <li>"{@link #getProviderLocationOption ProviderLocationOption}"<blockquote>
 * Dependency on {@code 'org.apache.commons.cli.Option'} {@code (org.apache.commons.cli.Option)} bound to an instance.</blockquote></li>
 * </ul></p>
 * <p><b>Messages</b><ul>
 * <li>"{@link #getApplicationTitle applicationTitle}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>JOMC CLI Version 1.1-SNAPSHOT Build 2010-07-08T15:25:19+0200</pre></td></tr>
 * </table>
 * <li>"{@link #getClasspathElementInfo classpathElementInfo}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>{0}: Classpath element: ''{1}''</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>{0}: Klassenpfad-Element: ''{1}''</pre></td></tr>
 * </table>
 * <li>"{@link #getClasspathElementNotFoundWarning classpathElementNotFoundWarning}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>Classpath element ''{0}'' ignored. File not found.</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Klassenpfad-Element ''{0}'' ignoriert. Datei nicht gefunden.</pre></td></tr>
 * </table>
 * <li>"{@link #getCommandFailureMessage commandFailureMessage}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>{0} failure.</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>{0} fehlgeschlagen.</pre></td></tr>
 * </table>
 * <li>"{@link #getCommandInfoMessage commandInfoMessage}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>Executing command {0} ...</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>F&uuml;hrt Befehl {0} aus ... </pre></td></tr>
 * </table>
 * <li>"{@link #getCommandSuccessMessage commandSuccessMessage}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>{0} successful.</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>{0} erfolgreich.</pre></td></tr>
 * </table>
 * <li>"{@link #getDefaultLogLevelInfo defaultLogLevelInfo}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>{0}: Default log level: ''{1}''</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>{0}: Standard-Protokollierungsstufe: ''{1}''</pre></td></tr>
 * </table>
 * <li>"{@link #getExcludedModletInfo excludedModletInfo}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>{0}: Modlet ''{2}'' from class path resource ''{1}'' ignored.</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>{0}: Modlet ''{2}'' aus Klassenpfad-Ressource ''{1}'' ignoriert.</pre></td></tr>
 * </table>
 * <li>"{@link #getExcludedProviderInfo excludedProviderInfo}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>{0}: Provider ''{2}'' from class path resource ''{1}'' ignored.</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>{0}: Provider ''{2}'' aus Klassenpfad-Ressource ''{1}'' ignoriert.</pre></td></tr>
 * </table>
 * <li>"{@link #getExcludedSchemaInfo excludedSchemaInfo}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>{0}: Context ''{2}'' from class path resource ''{1}'' ignored.</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>{0}: Kontext ''{2}'' aus Klassenpfad-Ressource ''{1}'' ignoriert.</pre></td></tr>
 * </table>
 * <li>"{@link #getExcludedServiceInfo excludedServiceInfo}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>{0}: Service ''{2}'' from class path resource ''{1}'' ignored.</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>{0}: Service ''{2}'' aus Klassenpfad-Ressource ''{1}'' ignoriert.</pre></td></tr>
 * </table>
 * <li>"{@link #getLongDescriptionMessage longDescriptionMessage}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre></pre></td></tr>
 * </table>
 * <li>"{@link #getSeparator separator}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>--------------------------------------------------------------------------------</pre></td></tr>
 * </table>
 * <li>"{@link #getShortDescriptionMessage shortDescriptionMessage}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre></pre></td></tr>
 * </table>
 * </ul></p>
 *
 * @author <a href="mailto:schulte2005@users.sourceforge.net">Christian Schulte</a> 1.0
 * @version $Id$
 */
// </editor-fold>
// SECTION-END
// SECTION-START[Annotations]
// <editor-fold defaultstate="collapsed" desc=" Generated Annotations ">
@javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.1-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.1.x/jomc-tools" )
// </editor-fold>
// SECTION-END
public abstract class AbstractJomcCommand implements Command
{
    // SECTION-START[Command]

    /**
     * Gets the list of registered listeners.
     * <p>This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make
     * to the returned list will be present inside the object. This is why there is no {@code set} method for the
     * listeners property.</p>
     *
     * @return The list of registered listeners.
     *
     * @see #log(java.util.logging.Level, java.lang.String, java.lang.Throwable)
     */
    public List<Listener> getListeners()
    {
        if ( this.listeners == null )
        {
            this.listeners = new LinkedList<Listener>();
        }

        return this.listeners;
    }

    /**
     * Gets the log level of the instance.
     *
     * @return The log level of the instance.
     *
     * @see #getDefaultLogLevel()
     * @see #setLogLevel(java.util.logging.Level)
     * @see #isLoggable(java.util.logging.Level)
     */
    public Level getLogLevel()
    {
        if ( this.logLevel == null )
        {
            this.logLevel = getDefaultLogLevel();
            this.log( Level.CONFIG, this.getDefaultLogLevelInfo( this.getLocale(), this.getClass().getName(),
                                                                 this.logLevel.getLocalizedName() ), null );

        }

        return this.logLevel;
    }

    /**
     * Sets the log level of the instance.
     *
     * @param value The new log level of the instance or {@code null}.
     *
     * @see #getLogLevel()
     * @see #isLoggable(java.util.logging.Level)
     */
    public void setLogLevel( final Level value )
    {
        this.logLevel = value;
    }

    public String getName()
    {
        return this.getCommandName();
    }

    public String getAbbreviatedName()
    {
        return this.getAbbreviatedCommandName();
    }

    public String getShortDescription( final Locale locale )
    {
        return this.getShortDescriptionMessage( locale );
    }

    public String getLongDescription( final Locale locale )
    {
        return this.getLongDescriptionMessage( locale );
    }

    public final int execute( final CommandLine commandLine )
    {
        try
        {
            if ( commandLine.hasOption( this.getProviderLocationOption().getOpt() ) )
            {
                DefaultModelContext.setDefaultProviderLocation(
                    commandLine.getOptionValue( this.getProviderLocationOption().getOpt() ) );

            }
            else
            {
                DefaultModelContext.setDefaultProviderLocation( null );
            }

            if ( commandLine.hasOption( this.getPlatformProviderLocationOption().getOpt() ) )
            {
                DefaultModelContext.setDefaultPlatformProviderLocation(
                    commandLine.getOptionValue( this.getPlatformProviderLocationOption().getOpt() ) );

            }
            else
            {
                DefaultModelContext.setDefaultPlatformProviderLocation( null );
            }

            if ( commandLine.hasOption( this.getModletLocationOption().getOpt() ) )
            {
                DefaultModletProvider.setDefaultModletLocation(
                    commandLine.getOptionValue( this.getModletLocationOption().getOpt() ) );

            }
            else
            {
                DefaultModletProvider.setDefaultModletLocation( null );
            }

            if ( this.isLoggable( Level.INFO ) )
            {
                this.log( Level.INFO, this.getSeparator( this.getLocale() ), null );
                this.log( Level.INFO, this.getApplicationTitle( this.getLocale() ), null );
                this.log( Level.INFO, this.getSeparator( this.getLocale() ), null );
                this.log( Level.INFO, this.getCommandInfoMessage( this.getLocale(), this.getCommandName() ), null );
            }

            final int status = this.executeCommand( commandLine );

            if ( this.isLoggable( Level.INFO ) )
            {
                if ( status == Command.STATUS_SUCCESS )
                {
                    this.log( Level.INFO, this.getCommandSuccessMessage(
                        this.getLocale(), this.getCommandName() ), null );

                }
                else
                {
                    this.log( Level.INFO, this.getCommandFailureMessage(
                        this.getLocale(), this.getCommandName() ), null );

                }

                this.log( Level.INFO, this.getSeparator( this.getLocale() ), null );
            }

            return status;
        }
        catch ( final Throwable t )
        {
            this.log( Level.SEVERE, t.getMessage(), t );
            return Command.STATUS_FAILURE;
        }
        finally
        {
            ModelContext.setDefaultModletSchemaSystemId( null );
            DefaultModelContext.setDefaultPlatformProviderLocation( null );
            DefaultModelContext.setDefaultProviderLocation( null );
            DefaultModletProvider.setDefaultModletLocation( null );
        }
    }

    // SECTION-END
    // SECTION-START[AbstractJomcCommand]
    /**
     * Log level events are logged at by default.
     * @see #getDefaultLogLevel()
     */
    private static final Level DEFAULT_LOG_LEVEL = Level.WARNING;

    /** Default log level. */
    private static volatile Level defaultLogLevel;

    /** The listeners of the instance. */
    private List<Listener> listeners;

    /** Log level of the instance. */
    private Level logLevel;

    /**
     * Gets the default log level events are logged at.
     * <p>The default log level is controlled by system property
     * {@code org.jomc.cli.command.AbstractJomcCommand.defaultLogLevel} holding the log level to log events at by
     * default. If that property is not set, the {@code WARNING} default is returned.</p>
     *
     * @return The log level events are logged at by default.
     *
     * @see #getLogLevel()
     * @see Level#parse(java.lang.String)
     */
    public static Level getDefaultLogLevel()
    {
        if ( defaultLogLevel == null )
        {
            defaultLogLevel = Level.parse( System.getProperty(
                "org.jomc.cli.command.AbstractJomcCommand.defaultLogLevel", DEFAULT_LOG_LEVEL.getName() ) );

        }

        return defaultLogLevel;
    }

    /**
     * Sets the default log level events are logged at.
     *
     * @param value The new default level events are logged at or {@code null}.
     *
     * @see #getDefaultLogLevel()
     */
    public static void setDefaultLogLevel( final Level value )
    {
        defaultLogLevel = value;
    }

    protected abstract int executeCommand( final CommandLine commandLine ) throws Exception;

    /**
     * Checks if a message at a given level is provided to the listeners of the instance.
     *
     * @param level The level to test.
     *
     * @return {@code true} if messages at {@code level} are provided to the listeners of the instance;
     * {@code false} if messages at {@code level} are not provided to the listeners of the instance.
     *
     * @throws NullPointerException if {@code level} is {@code null}.
     *
     * @see #getLogLevel()
     * @see #setLogLevel(java.util.logging.Level)
     */
    protected boolean isLoggable( final Level level )
    {
        if ( level == null )
        {
            throw new NullPointerException( "level" );
        }

        return level.intValue() >= this.getLogLevel().intValue();
    }

    /**
     * Notifies registered listeners.
     *
     * @param level The level of the event.
     * @param message The message of the event or {@code null}.
     * @param throwable The throwable of the event {@code null}.
     *
     * @throws NullPointerException if {@code level} is {@code null}.
     *
     * @see #getListeners()
     * @see #isLoggable(java.util.logging.Level)
     */
    protected void log( final Level level, final String message, final Throwable throwable )
    {
        if ( level == null )
        {
            throw new NullPointerException( "level" );
        }

        if ( this.isLoggable( level ) )
        {
            for ( Listener l : this.getListeners() )
            {
                l.onLog( level, message, throwable );
            }
        }
    }

    protected ModelContext createModelContext( final ClassLoader classLoader ) throws ModelException
    {
        final ModelContext modelContext = ModelContext.createModelContext( classLoader );
        modelContext.setLogLevel( this.getLogLevel() );
        modelContext.getListeners().add( new ModelContext.Listener()
        {

            public void onLog( final Level level, final String message, final Throwable t )
            {
                log( level, message, t );
            }

        } );

        return modelContext;
    }

    /**
     * Gets the identifier of the model to process.
     *
     * @param commandLine The command line to get the model identifier of the model to process from.
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
     * Class loader backed by a command line.
     *
     * @author <a href="mailto:schulte2005@users.sourceforge.net">Christian Schulte</a>
     * @version $Id$
     */
    public class CommandLineClassLoader extends URLClassLoader
    {

        private static final String LOG_PREFIX = "org.jomc.cli.commands.CommandLineClassLoader";

        /**
         * Creates a new {@code CommandLineClassLoader} taking a command line backing the class loader.
         *
         * @param commandLine The command line backing the class loader.
         *
         * @throws NullPointerException if {@code commandLine} is {@code null}.
         * @throws IOException if processing {@code commandLine} fails.
         */
        public CommandLineClassLoader( final CommandLine commandLine ) throws IOException
        {
            super( new URL[ 0 ] );

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
                            }
                            finally
                            {
                                if ( reader != null )
                                {
                                    reader.close();
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
                    if ( isLoggable( Level.FINE ) )
                    {
                        log( Level.FINE, getClasspathElementInfo( getLocale(), LOG_PREFIX, uri.toASCIIString() ),
                             null );

                    }

                    this.addURL( uri.toURL() );
                }
            }
        }

        /**
         * Finds the resource with the specified name on the URL search path.
         *
         * @param name The name of the resource.
         *
         * @return A {@code URL} for the resource, or {@code null} if the resource could not be found.
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
                log( Level.SEVERE, e.getMessage(), e );
                return null;
            }
            catch ( final JAXBException e )
            {
                String message = e.getMessage();
                if ( message == null && e.getLinkedException() != null )
                {
                    message = e.getLinkedException().getMessage();
                }

                log( Level.SEVERE, message, e );
                return null;
            }
            catch ( final ModelException e )
            {
                log( Level.SEVERE, e.getMessage(), e );
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
                            log( Level.SEVERE, e.getMessage(), e );
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
                            log( Level.SEVERE, e.getMessage(), e );
                            return null;
                        }
                        catch ( final JAXBException e )
                        {
                            String message = e.getMessage();
                            if ( message == null && e.getLinkedException() != null )
                            {
                                message = e.getLinkedException().getMessage();
                            }

                            log( Level.SEVERE, message, e );
                            return null;
                        }
                        catch ( final ModelException e )
                        {
                            log( Level.SEVERE, e.getMessage(), e );
                            return null;
                        }
                    }

                };
            }

            return enumeration;
        }

        private URL filterProviders( final URL resource ) throws IOException
        {
            URL filteredResource = resource;
            final InputStream in = resource.openStream();
            final List lines = IOUtils.readLines( in, "UTF-8" );
            final List<String> providerExcludes = Arrays.asList( getProviderExcludes().split( ":" ) );
            final List<String> filteredLines = new ArrayList<String>( lines.size() );

            for ( Object line : lines )
            {
                if ( !providerExcludes.contains( line.toString() ) )
                {
                    filteredLines.add( line.toString() );
                }
                else
                {
                    log( Level.FINE, getExcludedProviderInfo( getLocale(), LOG_PREFIX, resource.toExternalForm(),
                                                              line.toString() ), null );

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

        private URL filterModlets( final URL resource ) throws ModelException, IOException, JAXBException
        {
            URL filteredResource = resource;
            final List<String> excludedModlets = Arrays.asList( getModletExcludes().split( ":" ) );
            final ModelContext modelContext = ModelContext.createModelContext( this.getClass().getClassLoader() );
            final InputStream in = resource.openStream();
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

                    if ( excludedModlets.contains( m.getName() ) )
                    {
                        it.remove();
                        filtered = true;
                        log( Level.FINE, getExcludedModletInfo( getLocale(), LOG_PREFIX, resource.toExternalForm(),
                                                                m.getName() ), null );

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
                        log( Level.FINE, getExcludedSchemaInfo( getLocale(), LOG_PREFIX, resourceInfo,
                                                                s.getContextId() ), null );

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
                        log( Level.FINE, getExcludedServiceInfo( getLocale(), LOG_PREFIX, resourceInfo, s.getClazz() ),
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

    }

    // SECTION-END
    // SECTION-START[Constructors]
    // <editor-fold defaultstate="collapsed" desc=" Generated Constructors ">

    /** Creates a new {@code AbstractJomcCommand} instance. */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.1-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.1.x/jomc-tools" )
    public AbstractJomcCommand()
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
     * Gets the {@code ClasspathOption} dependency.
     * <p>This method returns the {@code 'JOMC CLI Classpath Option'} object of the {@code 'org.apache.commons.cli.Option'} {@code (org.apache.commons.cli.Option)} specification.</p>
     * <p>That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.</p>
     * @return The {@code ClasspathOption} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.1-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.1.x/jomc-tools" )
    private org.apache.commons.cli.Option getClasspathOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "ClasspathOption" );
        assert _d != null : "'ClasspathOption' dependency not found.";
        return _d;
    }

    /**
     * Gets the {@code Locale} dependency.
     * <p>This method returns the {@code 'default'} object of the {@code 'java.util.Locale'} {@code (java.util.Locale)} specification at specification level 1.1.</p>
     * <p>That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.</p>
     * @return The {@code Locale} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.1-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.1.x/jomc-tools" )
    private java.util.Locale getLocale()
    {
        final java.util.Locale _d = (java.util.Locale) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "Locale" );
        assert _d != null : "'Locale' dependency not found.";
        return _d;
    }

    /**
     * Gets the {@code ModelOption} dependency.
     * <p>This method returns the {@code 'JOMC CLI Model Option'} object of the {@code 'org.apache.commons.cli.Option'} {@code (org.apache.commons.cli.Option)} specification.</p>
     * <p>That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.</p>
     * @return The {@code ModelOption} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.1-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.1.x/jomc-tools" )
    private org.apache.commons.cli.Option getModelOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "ModelOption" );
        assert _d != null : "'ModelOption' dependency not found.";
        return _d;
    }

    /**
     * Gets the {@code ModletLocationOption} dependency.
     * <p>This method returns the {@code 'JOMC CLI Modlet Location Option'} object of the {@code 'org.apache.commons.cli.Option'} {@code (org.apache.commons.cli.Option)} specification.</p>
     * <p>That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.</p>
     * @return The {@code ModletLocationOption} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.1-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.1.x/jomc-tools" )
    private org.apache.commons.cli.Option getModletLocationOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "ModletLocationOption" );
        assert _d != null : "'ModletLocationOption' dependency not found.";
        return _d;
    }

    /**
     * Gets the {@code PlatformProviderLocationOption} dependency.
     * <p>This method returns the {@code 'JOMC CLI Platform Provider Location Option'} object of the {@code 'org.apache.commons.cli.Option'} {@code (org.apache.commons.cli.Option)} specification.</p>
     * <p>That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.</p>
     * @return The {@code PlatformProviderLocationOption} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.1-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.1.x/jomc-tools" )
    private org.apache.commons.cli.Option getPlatformProviderLocationOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "PlatformProviderLocationOption" );
        assert _d != null : "'PlatformProviderLocationOption' dependency not found.";
        return _d;
    }

    /**
     * Gets the {@code ProviderLocationOption} dependency.
     * <p>This method returns the {@code 'JOMC CLI Provider Location Option'} object of the {@code 'org.apache.commons.cli.Option'} {@code (org.apache.commons.cli.Option)} specification.</p>
     * <p>That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.</p>
     * @return The {@code ProviderLocationOption} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.1-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.1.x/jomc-tools" )
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
     * Gets the value of the {@code abbreviatedCommandName} property.
     * @return Abbreviated name of the command.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.1-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.1.x/jomc-tools" )
    private java.lang.String getAbbreviatedCommandName()
    {
        final java.lang.String _p = (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getProperty( this, "abbreviatedCommandName" );
        assert _p != null : "'abbreviatedCommandName' property not found.";
        return _p;
    }

    /**
     * Gets the value of the {@code commandName} property.
     * @return Name of the command.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.1-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.1.x/jomc-tools" )
    private java.lang.String getCommandName()
    {
        final java.lang.String _p = (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getProperty( this, "commandName" );
        assert _p != null : "'commandName' property not found.";
        return _p;
    }

    /**
     * Gets the value of the {@code modletExcludes} property.
     * @return List of modlet names to exclude from any {@code META-INF/jomc-modlet.xml} file separated by {@code :}.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.1-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.1.x/jomc-tools" )
    private java.lang.String getModletExcludes()
    {
        final java.lang.String _p = (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getProperty( this, "modletExcludes" );
        assert _p != null : "'modletExcludes' property not found.";
        return _p;
    }

    /**
     * Gets the value of the {@code providerExcludes} property.
     * @return List of providers to exclude from any {@code META-INF/services} file separated by {@code :}.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.1-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.1.x/jomc-tools" )
    private java.lang.String getProviderExcludes()
    {
        final java.lang.String _p = (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getProperty( this, "providerExcludes" );
        assert _p != null : "'providerExcludes' property not found.";
        return _p;
    }

    /**
     * Gets the value of the {@code schemaExcludes} property.
     * @return List of schema context-ids to exclude from any {@code META-INF/jomc-modlet.xml} file separated by {@code :}.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.1-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.1.x/jomc-tools" )
    private java.lang.String getSchemaExcludes()
    {
        final java.lang.String _p = (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getProperty( this, "schemaExcludes" );
        assert _p != null : "'schemaExcludes' property not found.";
        return _p;
    }

    /**
     * Gets the value of the {@code serviceExcludes} property.
     * @return List of service classes to exclude from any {@code META-INF/jomc-modlet.xml} file separated by {@code :}.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.1-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.1.x/jomc-tools" )
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
     * Gets the text of the {@code applicationTitle} message.
     * <p><b>Templates</b><br/><table>
     * <tr><td valign="top">English:</td><td valign="top"><pre>JOMC CLI Version 1.1-SNAPSHOT Build 2010-07-08T15:25:19+0200</pre></td></tr>
     * </table></p>
     * @param locale The locale of the message to return.
     * @return The text of the {@code applicationTitle} message.
     *
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.1-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.1.x/jomc-tools" )
    private String getApplicationTitle( final java.util.Locale locale )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "applicationTitle", locale );
        assert _m != null : "'applicationTitle' message not found.";
        return _m;
    }

    /**
     * Gets the text of the {@code classpathElementInfo} message.
     * <p><b>Templates</b><br/><table>
     * <tr><td valign="top">English:</td><td valign="top"><pre>{0}: Classpath element: ''{1}''</pre></td></tr>
     * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>{0}: Klassenpfad-Element: ''{1}''</pre></td></tr>
     * </table></p>
     * @param locale The locale of the message to return.
     * @param className Format argument.
     * @param classpathElement Format argument.
     * @return The text of the {@code classpathElementInfo} message.
     *
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.1-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.1.x/jomc-tools" )
    private String getClasspathElementInfo( final java.util.Locale locale, final java.lang.String className, final java.lang.String classpathElement )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "classpathElementInfo", locale, className, classpathElement );
        assert _m != null : "'classpathElementInfo' message not found.";
        return _m;
    }

    /**
     * Gets the text of the {@code classpathElementNotFoundWarning} message.
     * <p><b>Templates</b><br/><table>
     * <tr><td valign="top">English:</td><td valign="top"><pre>Classpath element ''{0}'' ignored. File not found.</pre></td></tr>
     * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Klassenpfad-Element ''{0}'' ignoriert. Datei nicht gefunden.</pre></td></tr>
     * </table></p>
     * @param locale The locale of the message to return.
     * @param fileName Format argument.
     * @return The text of the {@code classpathElementNotFoundWarning} message.
     *
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.1-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.1.x/jomc-tools" )
    private String getClasspathElementNotFoundWarning( final java.util.Locale locale, final java.lang.String fileName )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "classpathElementNotFoundWarning", locale, fileName );
        assert _m != null : "'classpathElementNotFoundWarning' message not found.";
        return _m;
    }

    /**
     * Gets the text of the {@code commandFailureMessage} message.
     * <p><b>Templates</b><br/><table>
     * <tr><td valign="top">English:</td><td valign="top"><pre>{0} failure.</pre></td></tr>
     * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>{0} fehlgeschlagen.</pre></td></tr>
     * </table></p>
     * @param locale The locale of the message to return.
     * @param toolName Format argument.
     * @return The text of the {@code commandFailureMessage} message.
     *
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.1-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.1.x/jomc-tools" )
    private String getCommandFailureMessage( final java.util.Locale locale, final java.lang.String toolName )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "commandFailureMessage", locale, toolName );
        assert _m != null : "'commandFailureMessage' message not found.";
        return _m;
    }

    /**
     * Gets the text of the {@code commandInfoMessage} message.
     * <p><b>Templates</b><br/><table>
     * <tr><td valign="top">English:</td><td valign="top"><pre>Executing command {0} ...</pre></td></tr>
     * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>F&uuml;hrt Befehl {0} aus ... </pre></td></tr>
     * </table></p>
     * @param locale The locale of the message to return.
     * @param toolName Format argument.
     * @return The text of the {@code commandInfoMessage} message.
     *
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.1-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.1.x/jomc-tools" )
    private String getCommandInfoMessage( final java.util.Locale locale, final java.lang.String toolName )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "commandInfoMessage", locale, toolName );
        assert _m != null : "'commandInfoMessage' message not found.";
        return _m;
    }

    /**
     * Gets the text of the {@code commandSuccessMessage} message.
     * <p><b>Templates</b><br/><table>
     * <tr><td valign="top">English:</td><td valign="top"><pre>{0} successful.</pre></td></tr>
     * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>{0} erfolgreich.</pre></td></tr>
     * </table></p>
     * @param locale The locale of the message to return.
     * @param toolName Format argument.
     * @return The text of the {@code commandSuccessMessage} message.
     *
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.1-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.1.x/jomc-tools" )
    private String getCommandSuccessMessage( final java.util.Locale locale, final java.lang.String toolName )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "commandSuccessMessage", locale, toolName );
        assert _m != null : "'commandSuccessMessage' message not found.";
        return _m;
    }

    /**
     * Gets the text of the {@code defaultLogLevelInfo} message.
     * <p><b>Templates</b><br/><table>
     * <tr><td valign="top">English:</td><td valign="top"><pre>{0}: Default log level: ''{1}''</pre></td></tr>
     * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>{0}: Standard-Protokollierungsstufe: ''{1}''</pre></td></tr>
     * </table></p>
     * @param locale The locale of the message to return.
     * @param className Format argument.
     * @param defaultLogLevel Format argument.
     * @return The text of the {@code defaultLogLevelInfo} message.
     *
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.1-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.1.x/jomc-tools" )
    private String getDefaultLogLevelInfo( final java.util.Locale locale, final java.lang.String className, final java.lang.String defaultLogLevel )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "defaultLogLevelInfo", locale, className, defaultLogLevel );
        assert _m != null : "'defaultLogLevelInfo' message not found.";
        return _m;
    }

    /**
     * Gets the text of the {@code excludedModletInfo} message.
     * <p><b>Templates</b><br/><table>
     * <tr><td valign="top">English:</td><td valign="top"><pre>{0}: Modlet ''{2}'' from class path resource ''{1}'' ignored.</pre></td></tr>
     * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>{0}: Modlet ''{2}'' aus Klassenpfad-Ressource ''{1}'' ignoriert.</pre></td></tr>
     * </table></p>
     * @param locale The locale of the message to return.
     * @param className Format argument.
     * @param resourceName Format argument.
     * @param modletIdentifier Format argument.
     * @return The text of the {@code excludedModletInfo} message.
     *
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.1-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.1.x/jomc-tools" )
    private String getExcludedModletInfo( final java.util.Locale locale, final java.lang.String className, final java.lang.String resourceName, final java.lang.String modletIdentifier )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "excludedModletInfo", locale, className, resourceName, modletIdentifier );
        assert _m != null : "'excludedModletInfo' message not found.";
        return _m;
    }

    /**
     * Gets the text of the {@code excludedProviderInfo} message.
     * <p><b>Templates</b><br/><table>
     * <tr><td valign="top">English:</td><td valign="top"><pre>{0}: Provider ''{2}'' from class path resource ''{1}'' ignored.</pre></td></tr>
     * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>{0}: Provider ''{2}'' aus Klassenpfad-Ressource ''{1}'' ignoriert.</pre></td></tr>
     * </table></p>
     * @param locale The locale of the message to return.
     * @param className Format argument.
     * @param resourceName Format argument.
     * @param providerName Format argument.
     * @return The text of the {@code excludedProviderInfo} message.
     *
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.1-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.1.x/jomc-tools" )
    private String getExcludedProviderInfo( final java.util.Locale locale, final java.lang.String className, final java.lang.String resourceName, final java.lang.String providerName )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "excludedProviderInfo", locale, className, resourceName, providerName );
        assert _m != null : "'excludedProviderInfo' message not found.";
        return _m;
    }

    /**
     * Gets the text of the {@code excludedSchemaInfo} message.
     * <p><b>Templates</b><br/><table>
     * <tr><td valign="top">English:</td><td valign="top"><pre>{0}: Context ''{2}'' from class path resource ''{1}'' ignored.</pre></td></tr>
     * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>{0}: Kontext ''{2}'' aus Klassenpfad-Ressource ''{1}'' ignoriert.</pre></td></tr>
     * </table></p>
     * @param locale The locale of the message to return.
     * @param className Format argument.
     * @param resourceName Format argument.
     * @param contextId Format argument.
     * @return The text of the {@code excludedSchemaInfo} message.
     *
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.1-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.1.x/jomc-tools" )
    private String getExcludedSchemaInfo( final java.util.Locale locale, final java.lang.String className, final java.lang.String resourceName, final java.lang.String contextId )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "excludedSchemaInfo", locale, className, resourceName, contextId );
        assert _m != null : "'excludedSchemaInfo' message not found.";
        return _m;
    }

    /**
     * Gets the text of the {@code excludedServiceInfo} message.
     * <p><b>Templates</b><br/><table>
     * <tr><td valign="top">English:</td><td valign="top"><pre>{0}: Service ''{2}'' from class path resource ''{1}'' ignored.</pre></td></tr>
     * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>{0}: Service ''{2}'' aus Klassenpfad-Ressource ''{1}'' ignoriert.</pre></td></tr>
     * </table></p>
     * @param locale The locale of the message to return.
     * @param className Format argument.
     * @param resourceName Format argument.
     * @param serviceName Format argument.
     * @return The text of the {@code excludedServiceInfo} message.
     *
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.1-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.1.x/jomc-tools" )
    private String getExcludedServiceInfo( final java.util.Locale locale, final java.lang.String className, final java.lang.String resourceName, final java.lang.String serviceName )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "excludedServiceInfo", locale, className, resourceName, serviceName );
        assert _m != null : "'excludedServiceInfo' message not found.";
        return _m;
    }

    /**
     * Gets the text of the {@code longDescriptionMessage} message.
     * <p><b>Templates</b><br/><table>
     * <tr><td valign="top">English:</td><td valign="top"><pre></pre></td></tr>
     * </table></p>
     * @param locale The locale of the message to return.
     * @return The text of the {@code longDescriptionMessage} message.
     *
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.1-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.1.x/jomc-tools" )
    private String getLongDescriptionMessage( final java.util.Locale locale )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "longDescriptionMessage", locale );
        assert _m != null : "'longDescriptionMessage' message not found.";
        return _m;
    }

    /**
     * Gets the text of the {@code separator} message.
     * <p><b>Templates</b><br/><table>
     * <tr><td valign="top">English:</td><td valign="top"><pre>--------------------------------------------------------------------------------</pre></td></tr>
     * </table></p>
     * @param locale The locale of the message to return.
     * @return The text of the {@code separator} message.
     *
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.1-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.1.x/jomc-tools" )
    private String getSeparator( final java.util.Locale locale )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "separator", locale );
        assert _m != null : "'separator' message not found.";
        return _m;
    }

    /**
     * Gets the text of the {@code shortDescriptionMessage} message.
     * <p><b>Templates</b><br/><table>
     * <tr><td valign="top">English:</td><td valign="top"><pre></pre></td></tr>
     * </table></p>
     * @param locale The locale of the message to return.
     * @return The text of the {@code shortDescriptionMessage} message.
     *
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.1-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.1.x/jomc-tools" )
    private String getShortDescriptionMessage( final java.util.Locale locale )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "shortDescriptionMessage", locale );
        assert _m != null : "'shortDescriptionMessage' message not found.";
        return _m;
    }
    // </editor-fold>
    // SECTION-END
}
