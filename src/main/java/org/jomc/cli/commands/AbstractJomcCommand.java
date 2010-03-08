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
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.logging.Level;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.jomc.cli.Command;
import org.jomc.model.DefaultModelProcessor;
import org.jomc.model.DefaultModelProvider;
import org.jomc.model.ModelContext;
import org.jomc.model.ModelException;
import org.jomc.model.Module;
import org.jomc.model.Modules;
import org.jomc.tools.JomcTool;
import org.jomc.model.ModelValidationReport;
import org.jomc.model.bootstrap.DefaultSchemaProvider;
import org.jomc.model.bootstrap.DefaultServiceProvider;
import org.jomc.tools.ResourceFileProcessor;
import org.jomc.tools.ClassFileProcessor;
import org.jomc.tools.SourceFileProcessor;
import org.xml.sax.SAXException;

// SECTION-START[Documentation]
// <editor-fold defaultstate="collapsed" desc=" Generated Documentation ">
/**
 * Base JOMC {@code Command} implementation.
 * <p><b>Specifications</b><ul>
 * <li>{@code org.jomc.cli.Command} {@code 1.0} {@code Multiton}</li>
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
 * </ul></p>
 * <p><b>Dependencies</b><ul>
 * <li>"{@link #getClasspathOption ClasspathOption}"<blockquote>
 * Dependency on {@code org.apache.commons.cli.Option} bound to an instance.</blockquote></li>
 * <li>"{@link #getDocumentsOption DocumentsOption}"<blockquote>
 * Dependency on {@code org.apache.commons.cli.Option} bound to an instance.</blockquote></li>
 * <li>"{@link #getLocale Locale}"<blockquote>
 * Dependency on {@code java.util.Locale} at specification level 1.1 bound to an instance.</blockquote></li>
 * <li>"{@link #getModuleLocationOption ModuleLocationOption}"<blockquote>
 * Dependency on {@code org.apache.commons.cli.Option} bound to an instance.</blockquote></li>
 * <li>"{@link #getModuleNameOption ModuleNameOption}"<blockquote>
 * Dependency on {@code org.apache.commons.cli.Option} bound to an instance.</blockquote></li>
 * <li>"{@link #getNoClasspathResolutionOption NoClasspathResolutionOption}"<blockquote>
 * Dependency on {@code org.apache.commons.cli.Option} bound to an instance.</blockquote></li>
 * <li>"{@link #getNoModelProcessingOption NoModelProcessingOption}"<blockquote>
 * Dependency on {@code org.apache.commons.cli.Option} bound to an instance.</blockquote></li>
 * <li>"{@link #getSchemaLocationOption SchemaLocationOption}"<blockquote>
 * Dependency on {@code org.apache.commons.cli.Option} bound to an instance.</blockquote></li>
 * <li>"{@link #getServiceLocationOption ServiceLocationOption}"<blockquote>
 * Dependency on {@code org.apache.commons.cli.Option} bound to an instance.</blockquote></li>
 * <li>"{@link #getTransformerLocationOption TransformerLocationOption}"<blockquote>
 * Dependency on {@code org.apache.commons.cli.Option} bound to an instance.</blockquote></li>
 * </ul></p>
 * <p><b>Messages</b><ul>
 * <li>"{@link #getApplicationTitleMessage applicationTitle}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>JOMC Version 1.0-alpha-18-SNAPSHOT Build 2010-03-08T03:16:24+0000</pre></td></tr>
 * </table>
 * <li>"{@link #getCannotProcessMessage cannotProcess}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>Cannot process ''{0}'': {1}</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Kann ''{0}'' nicht verarbeiten: {1}</pre></td></tr>
 * </table>
 * <li>"{@link #getClasspathElementMessage classpathElement}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>Classpath element: ''{0}''</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Klassenpfad-Element: ''{0}''</pre></td></tr>
 * </table>
 * <li>"{@link #getDefaultLogLevelInfoMessage defaultLogLevelInfo}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>Default log level: ''{0}''</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Standard-Protokollierungsstufe: ''{0}''</pre></td></tr>
 * </table>
 * <li>"{@link #getDocumentFileMessage documentFile}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>Document file: ''{0}''</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Dokument-Datei: ''{0}''</pre></td></tr>
 * </table>
 * <li>"{@link #getInvalidModelMessage invalidModel}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>Invalid model.</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Ung&uuml;ltiges Modell.</pre></td></tr>
 * </table>
 * <li>"{@link #getLongDescriptionMessage longDescription}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre></pre></td></tr>
 * </table>
 * <li>"{@link #getMissingModuleMessage missingModule}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>Module ''{0}'' not found.</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Modul ''{0}'' nicht gefunden.</pre></td></tr>
 * </table>
 * <li>"{@link #getModulesReportMessage modulesReport}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>Modules</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Module</pre></td></tr>
 * </table>
 * <li>"{@link #getSeparatorMessage separator}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>--------------------------------------------------------------------------------</pre></td></tr>
 * </table>
 * <li>"{@link #getShortDescriptionMessage shortDescription}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre></pre></td></tr>
 * </table>
 * <li>"{@link #getStartingModuleProcessingMessage startingModuleProcessing}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>Executing command {0} with module ''{1}'' ...</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>F&uuml;hrt Befehl {0} mit Modul ''{1}'' aus ... </pre></td></tr>
 * </table>
 * <li>"{@link #getStartingProcessingMessage startingProcessing}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>Executing command {0} ...</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>F&uuml;hrt Befehl {0} aus ... </pre></td></tr>
 * </table>
 * <li>"{@link #getToolFailureMessage toolFailure}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>{0} failure.</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>{0} fehlgeschlagen.</pre></td></tr>
 * </table>
 * <li>"{@link #getToolSuccessMessage toolSuccess}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>{0} successful.</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>{0} erfolgreich.</pre></td></tr>
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
@javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor",
                             comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-18-SNAPSHOT/jomc-tools" )
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
            this.log( Level.CONFIG, this.getDefaultLogLevelInfoMessage(
                this.getLocale(), this.logLevel.getLocalizedName() ), null );

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

    public Options getOptions()
    {
        final Options options = new Options();
        options.addOption( this.getClasspathOption() );
        options.addOption( this.getDocumentsOption() );
        options.addOption( this.getModuleLocationOption() );
        options.addOption( this.getSchemaLocationOption() );
        options.addOption( this.getServiceLocationOption() );
        options.addOption( this.getTransformerLocationOption() );
        options.addOption( this.getModuleNameOption() );
        options.addOption( this.getNoClasspathResolutionOption() );
        options.addOption( this.getNoModelProcessingOption() );
        return options;
    }

    public final int execute( final CommandLine commandLine )
    {
        try
        {
            if ( commandLine.hasOption( this.getSchemaLocationOption().getOpt() ) )
            {
                DefaultSchemaProvider.setDefaultSchemaLocation(
                    commandLine.getOptionValue( this.getSchemaLocationOption().getOpt() ) );

            }
            else
            {
                DefaultSchemaProvider.setDefaultSchemaLocation( null );
            }

            if ( commandLine.hasOption( this.getServiceLocationOption().getOpt() ) )
            {
                DefaultServiceProvider.setDefaultServiceLocation(
                    commandLine.getOptionValue( this.getServiceLocationOption().getOpt() ) );

            }
            else
            {
                DefaultServiceProvider.setDefaultServiceLocation( null );
            }

            if ( commandLine.hasOption( this.getTransformerLocationOption().getOpt() ) )
            {
                DefaultModelProcessor.setDefaultTransformerLocation(
                    commandLine.getOptionValue( this.getTransformerLocationOption().getOpt() ) );

            }
            else
            {
                DefaultModelProcessor.setDefaultTransformerLocation( null );
            }

            if ( commandLine.hasOption( this.getModuleLocationOption().getOpt() ) )
            {
                DefaultModelProvider.setDefaultModuleLocation(
                    commandLine.getOptionValue( this.getModuleLocationOption().getOpt() ) );

            }
            else
            {
                DefaultModelProvider.setDefaultModuleLocation( null );
            }

            if ( this.isLoggable( Level.INFO ) )
            {
                this.log( Level.INFO, this.getSeparatorMessage( this.getLocale() ), null );
                this.log( Level.INFO, this.getApplicationTitleMessage( this.getLocale() ), null );
                this.log( Level.INFO, this.getSeparatorMessage( this.getLocale() ), null );
            }

            final int status = this.executeCommand( commandLine );

            if ( this.isLoggable( Level.INFO ) )
            {
                if ( status == Command.STATUS_SUCCESS )
                {
                    this.log( Level.INFO, this.getToolSuccessMessage( this.getLocale(), this.getCommandName() ), null );
                }
                else
                {
                    this.log( Level.INFO, this.getToolFailureMessage( this.getLocale(), this.getCommandName() ), null );

                }

                this.log( Level.INFO, this.getSeparatorMessage( this.getLocale() ), null );
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
            DefaultSchemaProvider.setDefaultSchemaLocation( null );
            DefaultServiceProvider.setDefaultServiceLocation( null );
            DefaultModelProcessor.setDefaultTransformerLocation( null );
            DefaultModelProvider.setDefaultModuleLocation( null );
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

    /** The {@code ResourceFileProcessor} tool of the instance. */
    private ResourceFileProcessor resourceFileProcessor;

    /** The {@code ClassFileProcessor} tool of the instance. */
    private ClassFileProcessor classFileProcessor;

    /** The {@code SourceFileProcessor} tool of the instance. */
    private SourceFileProcessor sourceFileProcessor;

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

    protected void log( final ModelValidationReport validationReport, final Marshaller marshaller )
        throws JAXBException
    {
        if ( !validationReport.isModelValid() && this.isLoggable( Level.SEVERE ) )
        {
            this.log( Level.SEVERE, this.getInvalidModelMessage( this.getLocale() ), null );
        }

        marshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
        for ( ModelValidationReport.Detail d : validationReport.getDetails() )
        {
            if ( this.isLoggable( d.getLevel() ) )
            {
                this.log( d.getLevel(), d.getMessage(), null );

                if ( d.getElement() != null && this.getLogLevel().intValue() < Level.INFO.intValue() )
                {
                    final StringWriter stringWriter = new StringWriter();
                    marshaller.marshal( d.getElement(), stringWriter );
                    this.log( d.getLevel(), stringWriter.toString(), null );
                }
            }
        }
    }

    protected ModelContext getModelContext( final ClassLoader classLoader ) throws ModelException
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

    protected ResourceFileProcessor getResourceFileProcessor()
    {
        if ( this.resourceFileProcessor == null )
        {
            final ResourceFileProcessor tool = new ResourceFileProcessor();
            tool.setLogLevel( this.getLogLevel() );
            tool.getListeners().add( new JomcTool.Listener()
            {

                public void onLog( final Level level, final String message, final Throwable throwable )
                {
                    log( level, message, throwable );
                }

            } );

            return tool;
        }

        return this.resourceFileProcessor;
    }

    protected ClassFileProcessor getClassFileProcessor()
    {
        if ( this.classFileProcessor == null )
        {
            final ClassFileProcessor tool = new ClassFileProcessor();
            tool.setLogLevel( this.getLogLevel() );
            tool.getListeners().add( new JomcTool.Listener()
            {

                public void onLog( final Level level, final String message, final Throwable throwable )
                {
                    log( level, message, throwable );
                }

            } );

            return tool;
        }

        return this.classFileProcessor;
    }

    protected SourceFileProcessor getSourceFileProcessor()
    {
        if ( this.sourceFileProcessor == null )
        {
            final SourceFileProcessor tool = new SourceFileProcessor();
            tool.setLogLevel( this.getLogLevel() );
            tool.getListeners().add( new JomcTool.Listener()
            {

                public void onLog( final Level level, final String message, final Throwable throwable )
                {
                    log( level, message, throwable );
                }

            } );

            return tool;
        }

        return this.sourceFileProcessor;
    }

    protected ClassLoader getClassLoader( final CommandLine commandLine ) throws IOException
    {
        final Set<URI> uris = new HashSet<URI>();

        if ( commandLine.hasOption( this.getClasspathOption().getOpt() ) )
        {
            final String[] elements = commandLine.getOptionValues( this.getClasspathOption().getOpt() );
            if ( elements != null )
            {
                for ( String e : elements )
                {
                    if ( this.isLoggable( Level.FINE ) )
                    {
                        this.log( Level.FINE, this.getClasspathElementMessage( this.getLocale(), e ), null );
                    }

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
                                if ( !line.startsWith( "#" ) )
                                {
                                    uris.add( new File( line ).toURI() );
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
                        uris.add( new File( e ).toURI() );
                    }
                }
            }
        }

        int i = 0;
        final URL[] urls = new URL[ uris.size() ];
        for ( URI uri : uris )
        {
            urls[i++] = uri.toURL();
        }

        return new URLClassLoader( urls );
    }

    protected Set<File> getDocumentFiles( final CommandLine commandLine ) throws IOException
    {
        final Set<File> files = new HashSet<File>();

        if ( commandLine.hasOption( this.getDocumentsOption().getOpt() ) )
        {
            final String[] elements = commandLine.getOptionValues( this.getDocumentsOption().getOpt() );
            if ( elements != null )
            {
                for ( String e : elements )
                {
                    if ( this.isLoggable( Level.FINE ) )
                    {
                        this.log( Level.FINE, this.getDocumentFileMessage( this.getLocale(), e ), null );
                    }

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
                                if ( !line.startsWith( "#" ) )
                                {
                                    files.add( new File( line ) );
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
                        files.add( new File( e ) );
                    }
                }
            }
        }

        return files;
    }

    protected Modules getModules( final ModelContext context, final CommandLine commandLine )
        throws IOException, SAXException, JAXBException, ModelException
    {
        Modules modules = new Modules();

        if ( commandLine.hasOption( this.getDocumentsOption().getOpt() ) )
        {
            final Unmarshaller u = context.createUnmarshaller();
            for ( File f : this.getDocumentFiles( commandLine ) )
            {
                final InputStream in = new FileInputStream( f );
                Object o = u.unmarshal( new StreamSource( in ) );
                if ( o instanceof JAXBElement )
                {
                    o = ( (JAXBElement) o ).getValue();
                }

                in.close();

                if ( o instanceof Module )
                {
                    modules.getModule().add( (Module) o );
                }
                else if ( o instanceof Modules )
                {
                    modules.getModule().addAll( ( (Modules) o ).getModule() );
                }
                else if ( this.isLoggable( Level.WARNING ) )
                {
                    this.log( Level.WARNING, this.getCannotProcessMessage(
                        this.getLocale(), f.getAbsolutePath(), o.toString() ), null );

                }
            }
        }

        if ( commandLine.hasOption( this.getClasspathOption().getOpt() ) )
        {
            final Modules classpathModules = context.findModules();
            for ( Module m : classpathModules.getModule() )
            {
                if ( modules.getModule( m.getName() ) == null )
                {
                    modules.getModule().add( m );
                }
            }
        }

        if ( !commandLine.hasOption( this.getNoClasspathResolutionOption().getOpt() ) )
        {
            final Module classpathModule = modules.getClasspathModule(
                Modules.getDefaultClasspathModuleName(), context.getClassLoader() );

            if ( classpathModule != null )
            {
                modules.getModule().add( classpathModule );
            }
        }

        if ( !commandLine.hasOption( this.getNoModelProcessingOption().getOpt() ) )
        {
            modules = context.processModules( modules );
        }

        if ( this.isLoggable( Level.FINE ) )
        {
            this.log( Level.FINE, this.getModulesReportMessage( this.getLocale() ), null );
            for ( Module m : modules.getModule() )
            {
                this.log( Level.FINE, "\t" + m.getName(), null );
            }
        }

        return modules;
    }

    // SECTION-END
    // SECTION-START[Constructors]
    // <editor-fold defaultstate="collapsed" desc=" Generated Constructors ">

    /** Creates a new {@code AbstractJomcCommand} instance. */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-18-SNAPSHOT/jomc-tools" )
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
     * <p>This method returns the "{@code JOMC CLI Classpath Option}" object of the {@code org.apache.commons.cli.Option} specification.</p>
     * <p>That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.</p>
     * @return The {@code ClasspathOption} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-18-SNAPSHOT/jomc-tools" )
    private org.apache.commons.cli.Option getClasspathOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "ClasspathOption" );
        assert _d != null : "'ClasspathOption' dependency not found.";
        return _d;
    }

    /**
     * Gets the {@code DocumentsOption} dependency.
     * <p>This method returns the "{@code JOMC CLI Documents Option}" object of the {@code org.apache.commons.cli.Option} specification.</p>
     * <p>That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.</p>
     * @return The {@code DocumentsOption} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-18-SNAPSHOT/jomc-tools" )
    private org.apache.commons.cli.Option getDocumentsOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "DocumentsOption" );
        assert _d != null : "'DocumentsOption' dependency not found.";
        return _d;
    }

    /**
     * Gets the {@code Locale} dependency.
     * <p>This method returns the "{@code default}" object of the {@code java.util.Locale} specification at specification level 1.1.</p>
     * <p>That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.</p>
     * @return The {@code Locale} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-18-SNAPSHOT/jomc-tools" )
    private java.util.Locale getLocale()
    {
        final java.util.Locale _d = (java.util.Locale) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "Locale" );
        assert _d != null : "'Locale' dependency not found.";
        return _d;
    }

    /**
     * Gets the {@code ModuleLocationOption} dependency.
     * <p>This method returns the "{@code JOMC CLI Module Location Option}" object of the {@code org.apache.commons.cli.Option} specification.</p>
     * <p>That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.</p>
     * @return The {@code ModuleLocationOption} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-18-SNAPSHOT/jomc-tools" )
    private org.apache.commons.cli.Option getModuleLocationOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "ModuleLocationOption" );
        assert _d != null : "'ModuleLocationOption' dependency not found.";
        return _d;
    }

    /**
     * Gets the {@code ModuleNameOption} dependency.
     * <p>This method returns the "{@code JOMC CLI Module Name Option}" object of the {@code org.apache.commons.cli.Option} specification.</p>
     * <p>That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.</p>
     * @return The {@code ModuleNameOption} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-18-SNAPSHOT/jomc-tools" )
    private org.apache.commons.cli.Option getModuleNameOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "ModuleNameOption" );
        assert _d != null : "'ModuleNameOption' dependency not found.";
        return _d;
    }

    /**
     * Gets the {@code NoClasspathResolutionOption} dependency.
     * <p>This method returns the "{@code JOMC CLI No Classpath Resolution Option}" object of the {@code org.apache.commons.cli.Option} specification.</p>
     * <p>That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.</p>
     * @return The {@code NoClasspathResolutionOption} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-18-SNAPSHOT/jomc-tools" )
    private org.apache.commons.cli.Option getNoClasspathResolutionOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "NoClasspathResolutionOption" );
        assert _d != null : "'NoClasspathResolutionOption' dependency not found.";
        return _d;
    }

    /**
     * Gets the {@code NoModelProcessingOption} dependency.
     * <p>This method returns the "{@code JOMC CLI No Model Processing Option}" object of the {@code org.apache.commons.cli.Option} specification.</p>
     * <p>That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.</p>
     * @return The {@code NoModelProcessingOption} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-18-SNAPSHOT/jomc-tools" )
    private org.apache.commons.cli.Option getNoModelProcessingOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "NoModelProcessingOption" );
        assert _d != null : "'NoModelProcessingOption' dependency not found.";
        return _d;
    }

    /**
     * Gets the {@code SchemaLocationOption} dependency.
     * <p>This method returns the "{@code JOMC CLI Schema Location Option}" object of the {@code org.apache.commons.cli.Option} specification.</p>
     * <p>That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.</p>
     * @return The {@code SchemaLocationOption} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-18-SNAPSHOT/jomc-tools" )
    private org.apache.commons.cli.Option getSchemaLocationOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "SchemaLocationOption" );
        assert _d != null : "'SchemaLocationOption' dependency not found.";
        return _d;
    }

    /**
     * Gets the {@code ServiceLocationOption} dependency.
     * <p>This method returns the "{@code JOMC CLI Service Location Option}" object of the {@code org.apache.commons.cli.Option} specification.</p>
     * <p>That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.</p>
     * @return The {@code ServiceLocationOption} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-18-SNAPSHOT/jomc-tools" )
    private org.apache.commons.cli.Option getServiceLocationOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "ServiceLocationOption" );
        assert _d != null : "'ServiceLocationOption' dependency not found.";
        return _d;
    }

    /**
     * Gets the {@code TransformerLocationOption} dependency.
     * <p>This method returns the "{@code JOMC CLI Transformer Location Option}" object of the {@code org.apache.commons.cli.Option} specification.</p>
     * <p>That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.</p>
     * @return The {@code TransformerLocationOption} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-18-SNAPSHOT/jomc-tools" )
    private org.apache.commons.cli.Option getTransformerLocationOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "TransformerLocationOption" );
        assert _d != null : "'TransformerLocationOption' dependency not found.";
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
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-18-SNAPSHOT/jomc-tools" )
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
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-18-SNAPSHOT/jomc-tools" )
    private java.lang.String getCommandName()
    {
        final java.lang.String _p = (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getProperty( this, "commandName" );
        assert _p != null : "'commandName' property not found.";
        return _p;
    }
    // </editor-fold>
    // SECTION-END
    // SECTION-START[Messages]
    // <editor-fold defaultstate="collapsed" desc=" Generated Messages ">

    /**
     * Gets the text of the {@code applicationTitle} message.
     * <p><b>Templates</b><br/><table>
     * <tr><td valign="top">English:</td><td valign="top"><pre>JOMC Version 1.0-alpha-18-SNAPSHOT Build 2010-03-08T03:16:24+0000</pre></td></tr>
     * </table></p>
     * @param locale The locale of the message to return.
     * @return The text of the {@code applicationTitle} message.
     *
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-18-SNAPSHOT/jomc-tools" )
    private String getApplicationTitleMessage( final java.util.Locale locale )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "applicationTitle", locale );
        assert _m != null : "'applicationTitle' message not found.";
        return _m;
    }

    /**
     * Gets the text of the {@code cannotProcess} message.
     * <p><b>Templates</b><br/><table>
     * <tr><td valign="top">English:</td><td valign="top"><pre>Cannot process ''{0}'': {1}</pre></td></tr>
     * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Kann ''{0}'' nicht verarbeiten: {1}</pre></td></tr>
     * </table></p>
     * @param locale The locale of the message to return.
     * @param itemInfo Format argument.
     * @param detailMessage Format argument.
     * @return The text of the {@code cannotProcess} message.
     *
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-18-SNAPSHOT/jomc-tools" )
    private String getCannotProcessMessage( final java.util.Locale locale, final java.lang.String itemInfo, final java.lang.String detailMessage )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "cannotProcess", locale, itemInfo, detailMessage );
        assert _m != null : "'cannotProcess' message not found.";
        return _m;
    }

    /**
     * Gets the text of the {@code classpathElement} message.
     * <p><b>Templates</b><br/><table>
     * <tr><td valign="top">English:</td><td valign="top"><pre>Classpath element: ''{0}''</pre></td></tr>
     * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Klassenpfad-Element: ''{0}''</pre></td></tr>
     * </table></p>
     * @param locale The locale of the message to return.
     * @param classpathElement Format argument.
     * @return The text of the {@code classpathElement} message.
     *
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-18-SNAPSHOT/jomc-tools" )
    private String getClasspathElementMessage( final java.util.Locale locale, final java.lang.String classpathElement )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "classpathElement", locale, classpathElement );
        assert _m != null : "'classpathElement' message not found.";
        return _m;
    }

    /**
     * Gets the text of the {@code defaultLogLevelInfo} message.
     * <p><b>Templates</b><br/><table>
     * <tr><td valign="top">English:</td><td valign="top"><pre>Default log level: ''{0}''</pre></td></tr>
     * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Standard-Protokollierungsstufe: ''{0}''</pre></td></tr>
     * </table></p>
     * @param locale The locale of the message to return.
     * @param defaultLogLevel Format argument.
     * @return The text of the {@code defaultLogLevelInfo} message.
     *
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-18-SNAPSHOT/jomc-tools" )
    private String getDefaultLogLevelInfoMessage( final java.util.Locale locale, final java.lang.String defaultLogLevel )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "defaultLogLevelInfo", locale, defaultLogLevel );
        assert _m != null : "'defaultLogLevelInfo' message not found.";
        return _m;
    }

    /**
     * Gets the text of the {@code documentFile} message.
     * <p><b>Templates</b><br/><table>
     * <tr><td valign="top">English:</td><td valign="top"><pre>Document file: ''{0}''</pre></td></tr>
     * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Dokument-Datei: ''{0}''</pre></td></tr>
     * </table></p>
     * @param locale The locale of the message to return.
     * @param documentFile Format argument.
     * @return The text of the {@code documentFile} message.
     *
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-18-SNAPSHOT/jomc-tools" )
    private String getDocumentFileMessage( final java.util.Locale locale, final java.lang.String documentFile )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "documentFile", locale, documentFile );
        assert _m != null : "'documentFile' message not found.";
        return _m;
    }

    /**
     * Gets the text of the {@code invalidModel} message.
     * <p><b>Templates</b><br/><table>
     * <tr><td valign="top">English:</td><td valign="top"><pre>Invalid model.</pre></td></tr>
     * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Ung&uuml;ltiges Modell.</pre></td></tr>
     * </table></p>
     * @param locale The locale of the message to return.
     * @return The text of the {@code invalidModel} message.
     *
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-18-SNAPSHOT/jomc-tools" )
    private String getInvalidModelMessage( final java.util.Locale locale )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "invalidModel", locale );
        assert _m != null : "'invalidModel' message not found.";
        return _m;
    }

    /**
     * Gets the text of the {@code longDescription} message.
     * <p><b>Templates</b><br/><table>
     * <tr><td valign="top">English:</td><td valign="top"><pre></pre></td></tr>
     * </table></p>
     * @param locale The locale of the message to return.
     * @return The text of the {@code longDescription} message.
     *
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-18-SNAPSHOT/jomc-tools" )
    private String getLongDescriptionMessage( final java.util.Locale locale )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "longDescription", locale );
        assert _m != null : "'longDescription' message not found.";
        return _m;
    }

    /**
     * Gets the text of the {@code missingModule} message.
     * <p><b>Templates</b><br/><table>
     * <tr><td valign="top">English:</td><td valign="top"><pre>Module ''{0}'' not found.</pre></td></tr>
     * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Modul ''{0}'' nicht gefunden.</pre></td></tr>
     * </table></p>
     * @param locale The locale of the message to return.
     * @param moduleName Format argument.
     * @return The text of the {@code missingModule} message.
     *
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-18-SNAPSHOT/jomc-tools" )
    private String getMissingModuleMessage( final java.util.Locale locale, final java.lang.String moduleName )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "missingModule", locale, moduleName );
        assert _m != null : "'missingModule' message not found.";
        return _m;
    }

    /**
     * Gets the text of the {@code modulesReport} message.
     * <p><b>Templates</b><br/><table>
     * <tr><td valign="top">English:</td><td valign="top"><pre>Modules</pre></td></tr>
     * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Module</pre></td></tr>
     * </table></p>
     * @param locale The locale of the message to return.
     * @return The text of the {@code modulesReport} message.
     *
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-18-SNAPSHOT/jomc-tools" )
    private String getModulesReportMessage( final java.util.Locale locale )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "modulesReport", locale );
        assert _m != null : "'modulesReport' message not found.";
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
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-18-SNAPSHOT/jomc-tools" )
    private String getSeparatorMessage( final java.util.Locale locale )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "separator", locale );
        assert _m != null : "'separator' message not found.";
        return _m;
    }

    /**
     * Gets the text of the {@code shortDescription} message.
     * <p><b>Templates</b><br/><table>
     * <tr><td valign="top">English:</td><td valign="top"><pre></pre></td></tr>
     * </table></p>
     * @param locale The locale of the message to return.
     * @return The text of the {@code shortDescription} message.
     *
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-18-SNAPSHOT/jomc-tools" )
    private String getShortDescriptionMessage( final java.util.Locale locale )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "shortDescription", locale );
        assert _m != null : "'shortDescription' message not found.";
        return _m;
    }

    /**
     * Gets the text of the {@code startingModuleProcessing} message.
     * <p><b>Templates</b><br/><table>
     * <tr><td valign="top">English:</td><td valign="top"><pre>Executing command {0} with module ''{1}'' ...</pre></td></tr>
     * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>F&uuml;hrt Befehl {0} mit Modul ''{1}'' aus ... </pre></td></tr>
     * </table></p>
     * @param locale The locale of the message to return.
     * @param toolName Format argument.
     * @param moduleName Format argument.
     * @return The text of the {@code startingModuleProcessing} message.
     *
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-18-SNAPSHOT/jomc-tools" )
    private String getStartingModuleProcessingMessage( final java.util.Locale locale, final java.lang.String toolName, final java.lang.String moduleName )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "startingModuleProcessing", locale, toolName, moduleName );
        assert _m != null : "'startingModuleProcessing' message not found.";
        return _m;
    }

    /**
     * Gets the text of the {@code startingProcessing} message.
     * <p><b>Templates</b><br/><table>
     * <tr><td valign="top">English:</td><td valign="top"><pre>Executing command {0} ...</pre></td></tr>
     * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>F&uuml;hrt Befehl {0} aus ... </pre></td></tr>
     * </table></p>
     * @param locale The locale of the message to return.
     * @param toolName Format argument.
     * @return The text of the {@code startingProcessing} message.
     *
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-18-SNAPSHOT/jomc-tools" )
    private String getStartingProcessingMessage( final java.util.Locale locale, final java.lang.String toolName )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "startingProcessing", locale, toolName );
        assert _m != null : "'startingProcessing' message not found.";
        return _m;
    }

    /**
     * Gets the text of the {@code toolFailure} message.
     * <p><b>Templates</b><br/><table>
     * <tr><td valign="top">English:</td><td valign="top"><pre>{0} failure.</pre></td></tr>
     * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>{0} fehlgeschlagen.</pre></td></tr>
     * </table></p>
     * @param locale The locale of the message to return.
     * @param toolName Format argument.
     * @return The text of the {@code toolFailure} message.
     *
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-18-SNAPSHOT/jomc-tools" )
    private String getToolFailureMessage( final java.util.Locale locale, final java.lang.String toolName )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "toolFailure", locale, toolName );
        assert _m != null : "'toolFailure' message not found.";
        return _m;
    }

    /**
     * Gets the text of the {@code toolSuccess} message.
     * <p><b>Templates</b><br/><table>
     * <tr><td valign="top">English:</td><td valign="top"><pre>{0} successful.</pre></td></tr>
     * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>{0} erfolgreich.</pre></td></tr>
     * </table></p>
     * @param locale The locale of the message to return.
     * @param toolName Format argument.
     * @return The text of the {@code toolSuccess} message.
     *
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-18-SNAPSHOT/jomc-tools" )
    private String getToolSuccessMessage( final java.util.Locale locale, final java.lang.String toolName )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "toolSuccess", locale, toolName );
        assert _m != null : "'toolSuccess' message not found.";
        return _m;
    }
    // </editor-fold>
    // SECTION-END
}
