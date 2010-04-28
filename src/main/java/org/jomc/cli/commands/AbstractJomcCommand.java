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
import org.apache.commons.io.IOUtils;
import org.jomc.cli.Command;
import org.jomc.model.DefaultModelProcessor;
import org.jomc.model.DefaultModelProvider;
import org.jomc.model.ModelContext;
import org.jomc.model.ModelException;
import org.jomc.model.Module;
import org.jomc.model.Modules;
import org.jomc.model.bootstrap.BootstrapException;
import org.jomc.tools.JomcTool;
import org.jomc.model.ModelValidationReport;
import org.jomc.model.bootstrap.BootstrapContext;
import org.jomc.model.bootstrap.DefaultBootstrapContext;
import org.jomc.model.bootstrap.DefaultSchemaProvider;
import org.jomc.model.bootstrap.DefaultServiceProvider;
import org.jomc.model.bootstrap.ObjectFactory;
import org.jomc.model.bootstrap.Schema;
import org.jomc.model.bootstrap.Schemas;
import org.jomc.model.bootstrap.Service;
import org.jomc.model.bootstrap.Services;
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
 * <li>"{@link #getProviderExcludes providerExcludes}"
 * <blockquote>Property of type {@code java.lang.String}.
 * <p>List of providers to exclude from any {@code META-INF/services} file separated by {@code :}.</p>
 * </blockquote></li>
 * <li>"{@link #getSchemaExcludes schemaExcludes}"
 * <blockquote>Property of type {@code java.lang.String}.
 * <p>List of schema context-ids to exclude from any {@code META-INF/jomc-schemas.xml} file separated by {@code :}.</p>
 * </blockquote></li>
 * <li>"{@link #getServiceExcludes serviceExcludes}"
 * <blockquote>Property of type {@code java.lang.String}.
 * <p>List of service classes to exclude from any {@code META-INF/jomc-services.xml} file separated by {@code :}.</p>
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
 * <li>"{@link #getPlatformProviderLocationOption PlatformProviderLocationOption}"<blockquote>
 * Dependency on {@code org.apache.commons.cli.Option} bound to an instance.</blockquote></li>
 * <li>"{@link #getProviderLocationOption ProviderLocationOption}"<blockquote>
 * Dependency on {@code org.apache.commons.cli.Option} bound to an instance.</blockquote></li>
 * <li>"{@link #getSchemaLocationOption SchemaLocationOption}"<blockquote>
 * Dependency on {@code org.apache.commons.cli.Option} bound to an instance.</blockquote></li>
 * <li>"{@link #getServiceLocationOption ServiceLocationOption}"<blockquote>
 * Dependency on {@code org.apache.commons.cli.Option} bound to an instance.</blockquote></li>
 * <li>"{@link #getTransformerLocationOption TransformerLocationOption}"<blockquote>
 * Dependency on {@code org.apache.commons.cli.Option} bound to an instance.</blockquote></li>
 * </ul></p>
 * <p><b>Messages</b><ul>
 * <li>"{@link #getApplicationTitle applicationTitle}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>JOMC Version 1.0-beta-4-SNAPSHOT Build 2010-04-28T16:04:36+0200</pre></td></tr>
 * </table>
 * <li>"{@link #getCannotProcessMessage cannotProcessMessage}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>Cannot process ''{0}'': {1}</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Kann ''{0}'' nicht verarbeiten: {1}</pre></td></tr>
 * </table>
 * <li>"{@link #getClasspathElementInfo classpathElementInfo}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>Classpath element: ''{0}''</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Klassenpfad-Element: ''{0}''</pre></td></tr>
 * </table>
 * <li>"{@link #getClasspathElementNotFoundWarning classpathElementNotFoundWarning}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>Classpath element ''{0}'' ignored. File not found.</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Klassenpfad-Element ''{0}'' ignoriert. Datei nicht gefunden.</pre></td></tr>
 * </table>
 * <li>"{@link #getDefaultLogLevelInfo defaultLogLevelInfo}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>Default log level: ''{0}''</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Standard-Protokollierungsstufe: ''{0}''</pre></td></tr>
 * </table>
 * <li>"{@link #getDocumentFileInfo documentFileInfo}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>Document file: ''{0}''</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Dokument-Datei: ''{0}''</pre></td></tr>
 * </table>
 * <li>"{@link #getDocumentFileNotFoundWarning documentFileNotFoundWarning}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>Document file ''{0}'' ignored. File not found.</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Dokument-Datei ''{0}'' ignoriert. Datei nicht gefunden.</pre></td></tr>
 * </table>
 * <li>"{@link #getExcludedModuleFromClasspathInfo excludedModuleFromClasspathInfo}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>Module ''{0}'' from class path ignored. Module with identical name already loaded.</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Modul ''{0}'' aus Klassenpfad ignoriert. Modul mit identischem Namen bereits geladen.</pre></td></tr>
 * </table>
 * <li>"{@link #getExcludedProviderInfo excludedProviderInfo}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>Provider ''{1}'' from class path resource ''{0}'' ignored.</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Provider ''{1}'' aus Klassenpfad-Ressource ''{0}'' ignoriert.</pre></td></tr>
 * </table>
 * <li>"{@link #getExcludedSchemaInfo excludedSchemaInfo}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>Context ''{1}'' from class path resource ''{0}'' ignored.</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Kontext ''{1}'' aus Klassenpfad-Ressource ''{0}'' ignoriert.</pre></td></tr>
 * </table>
 * <li>"{@link #getExcludedServiceInfo excludedServiceInfo}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>Service ''{1}'' from class path resource ''{0}'' ignored.</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Service ''{1}'' aus Klassenpfad-Ressource ''{0}'' ignoriert.</pre></td></tr>
 * </table>
 * <li>"{@link #getInvalidModelMessage invalidModelMessage}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>Invalid model.</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Ung&uuml;ltiges Modell.</pre></td></tr>
 * </table>
 * <li>"{@link #getLongDescriptionMessage longDescriptionMessage}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre></pre></td></tr>
 * </table>
 * <li>"{@link #getMissingModuleMessage missingModuleMessage}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>Module ''{0}'' not found.</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Modul ''{0}'' nicht gefunden.</pre></td></tr>
 * </table>
 * <li>"{@link #getModulesReport modulesReport}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>Modules</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Module</pre></td></tr>
 * </table>
 * <li>"{@link #getSeparator separator}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>--------------------------------------------------------------------------------</pre></td></tr>
 * </table>
 * <li>"{@link #getShortDescriptionMessage shortDescriptionMessage}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre></pre></td></tr>
 * </table>
 * <li>"{@link #getStartingModuleProcessingMessage startingModuleProcessingMessage}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>Executing command {0} with module ''{1}'' ...</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>F&uuml;hrt Befehl {0} mit Modul ''{1}'' aus ... </pre></td></tr>
 * </table>
 * <li>"{@link #getStartingProcessingMessage startingProcessingMessage}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>Executing command {0} ...</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>F&uuml;hrt Befehl {0} aus ... </pre></td></tr>
 * </table>
 * <li>"{@link #getToolFailureMessage toolFailureMessage}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>{0} failure.</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>{0} fehlgeschlagen.</pre></td></tr>
 * </table>
 * <li>"{@link #getToolSuccessMessage toolSuccessMessage}"<table>
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
                             comments = "See http://jomc.sourceforge.net/jomc/1.0-beta-4-SNAPSHOT/jomc-tools" )
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
            this.log( Level.CONFIG, this.getDefaultLogLevelInfo(
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
        options.addOption( this.getProviderLocationOption() );
        options.addOption( this.getPlatformProviderLocationOption() );
        options.addOption( this.getModuleNameOption() );
        options.addOption( this.getNoClasspathResolutionOption() );
        options.addOption( this.getNoModelProcessingOption() );
        return options;
    }

    public final int execute( final CommandLine commandLine )
    {
        try
        {
            if ( commandLine.hasOption( this.getProviderLocationOption().getOpt() ) )
            {
                DefaultBootstrapContext.setDefaultProviderLocation(
                    commandLine.getOptionValue( this.getProviderLocationOption().getOpt() ) );

            }
            else
            {
                DefaultBootstrapContext.setDefaultProviderLocation( null );
            }

            if ( commandLine.hasOption( this.getPlatformProviderLocationOption().getOpt() ) )
            {
                DefaultBootstrapContext.setDefaultPlatformProviderLocation(
                    commandLine.getOptionValue( this.getPlatformProviderLocationOption().getOpt() ) );

            }
            else
            {
                DefaultBootstrapContext.setDefaultPlatformProviderLocation( null );
            }

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
                this.log( Level.INFO, this.getSeparator( this.getLocale() ), null );
                this.log( Level.INFO, this.getApplicationTitle( this.getLocale() ), null );
                this.log( Level.INFO, this.getSeparator( this.getLocale() ), null );
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
            DefaultBootstrapContext.setDefaultBootstrapSchemaSystemId( null );
            DefaultBootstrapContext.setDefaultPlatformProviderLocation( null );
            DefaultBootstrapContext.setDefaultProviderLocation( null );
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

    protected ResourceFileProcessor createResourceFileProcessor()
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

    protected ClassFileProcessor createClassFileProcessor()
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

    protected SourceFileProcessor createSourceFileProcessor()
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
                                        if ( this.isLoggable( Level.FINE ) )
                                        {
                                            this.log( Level.FINE, this.getDocumentFileInfo(
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
                            if ( this.isLoggable( Level.FINE ) )
                            {
                                this.log( Level.FINE, this.getDocumentFileInfo(
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
                else if ( this.isLoggable( Level.FINE ) )
                {
                    this.log( Level.FINE, this.getExcludedModuleFromClasspathInfo(
                        this.getLocale(), m.getName() ), null );

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
            this.log( Level.FINE, this.getModulesReport( this.getLocale() ), null );
            for ( Module m : modules.getModule() )
            {
                this.log( Level.FINE, "\t" + m.getName(), null );
            }
        }

        return modules;
    }

    /**
     * Class loader backed by a command line.
     *
     * @author <a href="mailto:schulte2005@users.sourceforge.net">Christian Schulte</a>
     * @version $Id$
     */
    public class CommandLineClassLoader extends URLClassLoader
    {

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
                        log( Level.FINE, getClasspathElementInfo( getLocale(), uri.toASCIIString() ), null );
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
                    if ( name.contains( DefaultBootstrapContext.getDefaultProviderLocation() ) )
                    {
                        resource = this.filterProviders( resource );
                    }
                    else if ( name.contains( DefaultSchemaProvider.getDefaultSchemaLocation() ) )
                    {
                        resource = this.filterSchemas( resource );
                    }
                    else if ( name.contains( DefaultServiceProvider.getDefaultServiceLocation() ) )
                    {
                        resource = this.filterServices( resource );
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
                log( Level.SEVERE, e.getMessage(), e );
                return null;
            }
            catch ( final BootstrapException e )
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

            if ( name.contains( DefaultBootstrapContext.getDefaultProviderLocation() ) )
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
            else if ( name.contains( DefaultSchemaProvider.getDefaultSchemaLocation() ) )
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
                            return filterSchemas( allResources.nextElement() );
                        }
                        catch ( final IOException e )
                        {
                            log( Level.SEVERE, e.getMessage(), e );
                            return null;
                        }
                        catch ( final JAXBException e )
                        {
                            log( Level.SEVERE, e.getMessage(), e );
                            return null;
                        }
                        catch ( final BootstrapException e )
                        {
                            log( Level.SEVERE, e.getMessage(), e );
                            return null;
                        }
                    }

                };
            }
            else if ( name.contains( DefaultServiceProvider.getDefaultServiceLocation() ) )
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
                            return filterServices( allResources.nextElement() );
                        }
                        catch ( final IOException e )
                        {
                            log( Level.SEVERE, e.getMessage(), e );
                            return null;
                        }
                        catch ( final JAXBException e )
                        {
                            log( Level.SEVERE, e.getMessage(), e );
                            return null;
                        }
                        catch ( final BootstrapException e )
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
                    log( Level.FINE, getExcludedProviderInfo(
                        getLocale(), resource.toExternalForm(), line.toString() ), null );

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

        private URL filterSchemas( final URL resource ) throws BootstrapException, IOException, JAXBException
        {
            URL filteredResource = resource;
            final List<String> excludedSchemas = Arrays.asList( getSchemaExcludes().split( ":" ) );
            final BootstrapContext bootstrapContext =
                BootstrapContext.createBootstrapContext( this.getClass().getClassLoader() );

            final InputStream in = resource.openStream();
            final JAXBElement e = (JAXBElement) bootstrapContext.createUnmarshaller().unmarshal( in );
            final Object o = e.getValue();
            final Schemas schemas = new Schemas();
            boolean filtered = false;

            if ( o instanceof Schemas )
            {
                for ( Schema s : ( (Schemas) e.getValue() ).getSchema() )
                {
                    if ( !excludedSchemas.contains( s.getContextId() ) )
                    {
                        schemas.getSchema().add( s );
                    }
                    else
                    {
                        log( Level.FINE, getExcludedSchemaInfo(
                            getLocale(), resource.toExternalForm(), s.getContextId() ), null );

                        filtered = true;
                    }
                }
            }
            else if ( o instanceof Schema )
            {
                final Schema s = (Schema) o;
                if ( !excludedSchemas.contains( s.getContextId() ) )
                {
                    schemas.getSchema().add( s );
                }
                else
                {
                    log( Level.FINE, getExcludedSchemaInfo(
                        getLocale(), resource.toExternalForm(), s.getContextId() ), null );

                    filtered = true;
                }
            }

            if ( filtered )
            {
                final File tmpResource = File.createTempFile( this.getClass().getName(), ".rsrc" );
                tmpResource.deleteOnExit();
                bootstrapContext.createMarshaller().marshal(
                    new ObjectFactory().createSchemas( schemas ), tmpResource );

                filteredResource = tmpResource.toURI().toURL();
            }

            return filteredResource;
        }

        private URL filterServices( final URL resource ) throws BootstrapException, IOException, JAXBException
        {
            URL filteredResource = resource;
            final List<String> excludedServices = Arrays.asList( getServiceExcludes().split( ":" ) );
            final BootstrapContext bootstrapContext =
                BootstrapContext.createBootstrapContext( this.getClass().getClassLoader() );

            final InputStream in = resource.openStream();
            final JAXBElement e = (JAXBElement) bootstrapContext.createUnmarshaller().unmarshal( in );
            final Object o = e.getValue();
            final Services services = new Services();
            boolean filtered = false;

            if ( o instanceof Services )
            {
                for ( Service s : ( (Services) e.getValue() ).getService() )
                {
                    if ( !excludedServices.contains( s.getClazz() ) )
                    {
                        services.getService().add( s );
                    }
                    else
                    {
                        log( Level.FINE, getExcludedServiceInfo(
                            getLocale(), resource.toExternalForm(), s.getClazz() ), null );

                        filtered = true;
                    }
                }
            }
            else if ( o instanceof Service )
            {
                final Service s = (Service) o;
                if ( !excludedServices.contains( s.getClazz() ) )
                {
                    services.getService().add( s );
                }
                else
                {
                    log( Level.FINE, getExcludedServiceInfo(
                        getLocale(), resource.toExternalForm(), s.getClazz() ), null );

                    filtered = true;
                }
            }

            if ( filtered )
            {
                final File tmpResource = File.createTempFile( this.getClass().getName(), ".rsrc" );
                tmpResource.deleteOnExit();
                bootstrapContext.createMarshaller().marshal(
                    new ObjectFactory().createServices( services ), tmpResource );

                filteredResource = tmpResource.toURI().toURL();
            }

            return filteredResource;
        }

    }

    // SECTION-END
    // SECTION-START[Constructors]
    // <editor-fold defaultstate="collapsed" desc=" Generated Constructors ">

    /** Creates a new {@code AbstractJomcCommand} instance. */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-beta-4-SNAPSHOT/jomc-tools" )
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
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-beta-4-SNAPSHOT/jomc-tools" )
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
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-beta-4-SNAPSHOT/jomc-tools" )
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
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-beta-4-SNAPSHOT/jomc-tools" )
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
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-beta-4-SNAPSHOT/jomc-tools" )
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
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-beta-4-SNAPSHOT/jomc-tools" )
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
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-beta-4-SNAPSHOT/jomc-tools" )
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
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-beta-4-SNAPSHOT/jomc-tools" )
    private org.apache.commons.cli.Option getNoModelProcessingOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "NoModelProcessingOption" );
        assert _d != null : "'NoModelProcessingOption' dependency not found.";
        return _d;
    }

    /**
     * Gets the {@code PlatformProviderLocationOption} dependency.
     * <p>This method returns the "{@code JOMC CLI Platform Provider Location Option}" object of the {@code org.apache.commons.cli.Option} specification.</p>
     * <p>That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.</p>
     * @return The {@code PlatformProviderLocationOption} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-beta-4-SNAPSHOT/jomc-tools" )
    private org.apache.commons.cli.Option getPlatformProviderLocationOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "PlatformProviderLocationOption" );
        assert _d != null : "'PlatformProviderLocationOption' dependency not found.";
        return _d;
    }

    /**
     * Gets the {@code ProviderLocationOption} dependency.
     * <p>This method returns the "{@code JOMC CLI Provider Location Option}" object of the {@code org.apache.commons.cli.Option} specification.</p>
     * <p>That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.</p>
     * @return The {@code ProviderLocationOption} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-beta-4-SNAPSHOT/jomc-tools" )
    private org.apache.commons.cli.Option getProviderLocationOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "ProviderLocationOption" );
        assert _d != null : "'ProviderLocationOption' dependency not found.";
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
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-beta-4-SNAPSHOT/jomc-tools" )
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
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-beta-4-SNAPSHOT/jomc-tools" )
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
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-beta-4-SNAPSHOT/jomc-tools" )
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
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-beta-4-SNAPSHOT/jomc-tools" )
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
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-beta-4-SNAPSHOT/jomc-tools" )
    private java.lang.String getCommandName()
    {
        final java.lang.String _p = (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getProperty( this, "commandName" );
        assert _p != null : "'commandName' property not found.";
        return _p;
    }

    /**
     * Gets the value of the {@code providerExcludes} property.
     * @return List of providers to exclude from any {@code META-INF/services} file separated by {@code :}.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-beta-4-SNAPSHOT/jomc-tools" )
    private java.lang.String getProviderExcludes()
    {
        final java.lang.String _p = (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getProperty( this, "providerExcludes" );
        assert _p != null : "'providerExcludes' property not found.";
        return _p;
    }

    /**
     * Gets the value of the {@code schemaExcludes} property.
     * @return List of schema context-ids to exclude from any {@code META-INF/jomc-schemas.xml} file separated by {@code :}.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-beta-4-SNAPSHOT/jomc-tools" )
    private java.lang.String getSchemaExcludes()
    {
        final java.lang.String _p = (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getProperty( this, "schemaExcludes" );
        assert _p != null : "'schemaExcludes' property not found.";
        return _p;
    }

    /**
     * Gets the value of the {@code serviceExcludes} property.
     * @return List of service classes to exclude from any {@code META-INF/jomc-services.xml} file separated by {@code :}.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-beta-4-SNAPSHOT/jomc-tools" )
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
     * <tr><td valign="top">English:</td><td valign="top"><pre>JOMC Version 1.0-beta-4-SNAPSHOT Build 2010-04-28T16:04:36+0200</pre></td></tr>
     * </table></p>
     * @param locale The locale of the message to return.
     * @return The text of the {@code applicationTitle} message.
     *
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-beta-4-SNAPSHOT/jomc-tools" )
    private String getApplicationTitle( final java.util.Locale locale )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "applicationTitle", locale );
        assert _m != null : "'applicationTitle' message not found.";
        return _m;
    }

    /**
     * Gets the text of the {@code cannotProcessMessage} message.
     * <p><b>Templates</b><br/><table>
     * <tr><td valign="top">English:</td><td valign="top"><pre>Cannot process ''{0}'': {1}</pre></td></tr>
     * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Kann ''{0}'' nicht verarbeiten: {1}</pre></td></tr>
     * </table></p>
     * @param locale The locale of the message to return.
     * @param itemInfo Format argument.
     * @param detailMessage Format argument.
     * @return The text of the {@code cannotProcessMessage} message.
     *
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-beta-4-SNAPSHOT/jomc-tools" )
    private String getCannotProcessMessage( final java.util.Locale locale, final java.lang.String itemInfo, final java.lang.String detailMessage )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "cannotProcessMessage", locale, itemInfo, detailMessage );
        assert _m != null : "'cannotProcessMessage' message not found.";
        return _m;
    }

    /**
     * Gets the text of the {@code classpathElementInfo} message.
     * <p><b>Templates</b><br/><table>
     * <tr><td valign="top">English:</td><td valign="top"><pre>Classpath element: ''{0}''</pre></td></tr>
     * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Klassenpfad-Element: ''{0}''</pre></td></tr>
     * </table></p>
     * @param locale The locale of the message to return.
     * @param classpathElement Format argument.
     * @return The text of the {@code classpathElementInfo} message.
     *
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-beta-4-SNAPSHOT/jomc-tools" )
    private String getClasspathElementInfo( final java.util.Locale locale, final java.lang.String classpathElement )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "classpathElementInfo", locale, classpathElement );
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
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-beta-4-SNAPSHOT/jomc-tools" )
    private String getClasspathElementNotFoundWarning( final java.util.Locale locale, final java.lang.String fileName )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "classpathElementNotFoundWarning", locale, fileName );
        assert _m != null : "'classpathElementNotFoundWarning' message not found.";
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
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-beta-4-SNAPSHOT/jomc-tools" )
    private String getDefaultLogLevelInfo( final java.util.Locale locale, final java.lang.String defaultLogLevel )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "defaultLogLevelInfo", locale, defaultLogLevel );
        assert _m != null : "'defaultLogLevelInfo' message not found.";
        return _m;
    }

    /**
     * Gets the text of the {@code documentFileInfo} message.
     * <p><b>Templates</b><br/><table>
     * <tr><td valign="top">English:</td><td valign="top"><pre>Document file: ''{0}''</pre></td></tr>
     * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Dokument-Datei: ''{0}''</pre></td></tr>
     * </table></p>
     * @param locale The locale of the message to return.
     * @param documentFile Format argument.
     * @return The text of the {@code documentFileInfo} message.
     *
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-beta-4-SNAPSHOT/jomc-tools" )
    private String getDocumentFileInfo( final java.util.Locale locale, final java.lang.String documentFile )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "documentFileInfo", locale, documentFile );
        assert _m != null : "'documentFileInfo' message not found.";
        return _m;
    }

    /**
     * Gets the text of the {@code documentFileNotFoundWarning} message.
     * <p><b>Templates</b><br/><table>
     * <tr><td valign="top">English:</td><td valign="top"><pre>Document file ''{0}'' ignored. File not found.</pre></td></tr>
     * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Dokument-Datei ''{0}'' ignoriert. Datei nicht gefunden.</pre></td></tr>
     * </table></p>
     * @param locale The locale of the message to return.
     * @param fileName Format argument.
     * @return The text of the {@code documentFileNotFoundWarning} message.
     *
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-beta-4-SNAPSHOT/jomc-tools" )
    private String getDocumentFileNotFoundWarning( final java.util.Locale locale, final java.lang.String fileName )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "documentFileNotFoundWarning", locale, fileName );
        assert _m != null : "'documentFileNotFoundWarning' message not found.";
        return _m;
    }

    /**
     * Gets the text of the {@code excludedModuleFromClasspathInfo} message.
     * <p><b>Templates</b><br/><table>
     * <tr><td valign="top">English:</td><td valign="top"><pre>Module ''{0}'' from class path ignored. Module with identical name already loaded.</pre></td></tr>
     * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Modul ''{0}'' aus Klassenpfad ignoriert. Modul mit identischem Namen bereits geladen.</pre></td></tr>
     * </table></p>
     * @param locale The locale of the message to return.
     * @param moduleName Format argument.
     * @return The text of the {@code excludedModuleFromClasspathInfo} message.
     *
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-beta-4-SNAPSHOT/jomc-tools" )
    private String getExcludedModuleFromClasspathInfo( final java.util.Locale locale, final java.lang.String moduleName )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "excludedModuleFromClasspathInfo", locale, moduleName );
        assert _m != null : "'excludedModuleFromClasspathInfo' message not found.";
        return _m;
    }

    /**
     * Gets the text of the {@code excludedProviderInfo} message.
     * <p><b>Templates</b><br/><table>
     * <tr><td valign="top">English:</td><td valign="top"><pre>Provider ''{1}'' from class path resource ''{0}'' ignored.</pre></td></tr>
     * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Provider ''{1}'' aus Klassenpfad-Ressource ''{0}'' ignoriert.</pre></td></tr>
     * </table></p>
     * @param locale The locale of the message to return.
     * @param resourceName Format argument.
     * @param providerName Format argument.
     * @return The text of the {@code excludedProviderInfo} message.
     *
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-beta-4-SNAPSHOT/jomc-tools" )
    private String getExcludedProviderInfo( final java.util.Locale locale, final java.lang.String resourceName, final java.lang.String providerName )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "excludedProviderInfo", locale, resourceName, providerName );
        assert _m != null : "'excludedProviderInfo' message not found.";
        return _m;
    }

    /**
     * Gets the text of the {@code excludedSchemaInfo} message.
     * <p><b>Templates</b><br/><table>
     * <tr><td valign="top">English:</td><td valign="top"><pre>Context ''{1}'' from class path resource ''{0}'' ignored.</pre></td></tr>
     * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Kontext ''{1}'' aus Klassenpfad-Ressource ''{0}'' ignoriert.</pre></td></tr>
     * </table></p>
     * @param locale The locale of the message to return.
     * @param resourceName Format argument.
     * @param contextId Format argument.
     * @return The text of the {@code excludedSchemaInfo} message.
     *
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-beta-4-SNAPSHOT/jomc-tools" )
    private String getExcludedSchemaInfo( final java.util.Locale locale, final java.lang.String resourceName, final java.lang.String contextId )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "excludedSchemaInfo", locale, resourceName, contextId );
        assert _m != null : "'excludedSchemaInfo' message not found.";
        return _m;
    }

    /**
     * Gets the text of the {@code excludedServiceInfo} message.
     * <p><b>Templates</b><br/><table>
     * <tr><td valign="top">English:</td><td valign="top"><pre>Service ''{1}'' from class path resource ''{0}'' ignored.</pre></td></tr>
     * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Service ''{1}'' aus Klassenpfad-Ressource ''{0}'' ignoriert.</pre></td></tr>
     * </table></p>
     * @param locale The locale of the message to return.
     * @param resourceName Format argument.
     * @param serviceName Format argument.
     * @return The text of the {@code excludedServiceInfo} message.
     *
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-beta-4-SNAPSHOT/jomc-tools" )
    private String getExcludedServiceInfo( final java.util.Locale locale, final java.lang.String resourceName, final java.lang.String serviceName )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "excludedServiceInfo", locale, resourceName, serviceName );
        assert _m != null : "'excludedServiceInfo' message not found.";
        return _m;
    }

    /**
     * Gets the text of the {@code invalidModelMessage} message.
     * <p><b>Templates</b><br/><table>
     * <tr><td valign="top">English:</td><td valign="top"><pre>Invalid model.</pre></td></tr>
     * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Ung&uuml;ltiges Modell.</pre></td></tr>
     * </table></p>
     * @param locale The locale of the message to return.
     * @return The text of the {@code invalidModelMessage} message.
     *
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-beta-4-SNAPSHOT/jomc-tools" )
    private String getInvalidModelMessage( final java.util.Locale locale )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "invalidModelMessage", locale );
        assert _m != null : "'invalidModelMessage' message not found.";
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
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-beta-4-SNAPSHOT/jomc-tools" )
    private String getLongDescriptionMessage( final java.util.Locale locale )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "longDescriptionMessage", locale );
        assert _m != null : "'longDescriptionMessage' message not found.";
        return _m;
    }

    /**
     * Gets the text of the {@code missingModuleMessage} message.
     * <p><b>Templates</b><br/><table>
     * <tr><td valign="top">English:</td><td valign="top"><pre>Module ''{0}'' not found.</pre></td></tr>
     * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Modul ''{0}'' nicht gefunden.</pre></td></tr>
     * </table></p>
     * @param locale The locale of the message to return.
     * @param moduleName Format argument.
     * @return The text of the {@code missingModuleMessage} message.
     *
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-beta-4-SNAPSHOT/jomc-tools" )
    private String getMissingModuleMessage( final java.util.Locale locale, final java.lang.String moduleName )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "missingModuleMessage", locale, moduleName );
        assert _m != null : "'missingModuleMessage' message not found.";
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
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-beta-4-SNAPSHOT/jomc-tools" )
    private String getModulesReport( final java.util.Locale locale )
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
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-beta-4-SNAPSHOT/jomc-tools" )
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
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-beta-4-SNAPSHOT/jomc-tools" )
    private String getShortDescriptionMessage( final java.util.Locale locale )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "shortDescriptionMessage", locale );
        assert _m != null : "'shortDescriptionMessage' message not found.";
        return _m;
    }

    /**
     * Gets the text of the {@code startingModuleProcessingMessage} message.
     * <p><b>Templates</b><br/><table>
     * <tr><td valign="top">English:</td><td valign="top"><pre>Executing command {0} with module ''{1}'' ...</pre></td></tr>
     * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>F&uuml;hrt Befehl {0} mit Modul ''{1}'' aus ... </pre></td></tr>
     * </table></p>
     * @param locale The locale of the message to return.
     * @param toolName Format argument.
     * @param moduleName Format argument.
     * @return The text of the {@code startingModuleProcessingMessage} message.
     *
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-beta-4-SNAPSHOT/jomc-tools" )
    private String getStartingModuleProcessingMessage( final java.util.Locale locale, final java.lang.String toolName, final java.lang.String moduleName )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "startingModuleProcessingMessage", locale, toolName, moduleName );
        assert _m != null : "'startingModuleProcessingMessage' message not found.";
        return _m;
    }

    /**
     * Gets the text of the {@code startingProcessingMessage} message.
     * <p><b>Templates</b><br/><table>
     * <tr><td valign="top">English:</td><td valign="top"><pre>Executing command {0} ...</pre></td></tr>
     * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>F&uuml;hrt Befehl {0} aus ... </pre></td></tr>
     * </table></p>
     * @param locale The locale of the message to return.
     * @param toolName Format argument.
     * @return The text of the {@code startingProcessingMessage} message.
     *
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-beta-4-SNAPSHOT/jomc-tools" )
    private String getStartingProcessingMessage( final java.util.Locale locale, final java.lang.String toolName )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "startingProcessingMessage", locale, toolName );
        assert _m != null : "'startingProcessingMessage' message not found.";
        return _m;
    }

    /**
     * Gets the text of the {@code toolFailureMessage} message.
     * <p><b>Templates</b><br/><table>
     * <tr><td valign="top">English:</td><td valign="top"><pre>{0} failure.</pre></td></tr>
     * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>{0} fehlgeschlagen.</pre></td></tr>
     * </table></p>
     * @param locale The locale of the message to return.
     * @param toolName Format argument.
     * @return The text of the {@code toolFailureMessage} message.
     *
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-beta-4-SNAPSHOT/jomc-tools" )
    private String getToolFailureMessage( final java.util.Locale locale, final java.lang.String toolName )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "toolFailureMessage", locale, toolName );
        assert _m != null : "'toolFailureMessage' message not found.";
        return _m;
    }

    /**
     * Gets the text of the {@code toolSuccessMessage} message.
     * <p><b>Templates</b><br/><table>
     * <tr><td valign="top">English:</td><td valign="top"><pre>{0} successful.</pre></td></tr>
     * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>{0} erfolgreich.</pre></td></tr>
     * </table></p>
     * @param locale The locale of the message to return.
     * @param toolName Format argument.
     * @return The text of the {@code toolSuccessMessage} message.
     *
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-beta-4-SNAPSHOT/jomc-tools" )
    private String getToolSuccessMessage( final java.util.Locale locale, final java.lang.String toolName )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "toolSuccessMessage", locale, toolName );
        assert _m != null : "'toolSuccessMessage' message not found.";
        return _m;
    }
    // </editor-fold>
    // SECTION-END
}
