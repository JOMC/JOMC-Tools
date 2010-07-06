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
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import org.apache.commons.cli.CommandLine;
import org.jomc.model.Module;
import org.jomc.model.Modules;
import org.jomc.model.modlet.DefaultModelProcessor;
import org.jomc.model.modlet.DefaultModelProvider;
import org.jomc.model.modlet.ModelHelper;
import org.jomc.modlet.Model;
import org.jomc.modlet.ModelContext;
import org.jomc.modlet.ModelException;
import org.jomc.modlet.ModelValidationReport;
import org.jomc.tools.ClassFileProcessor;
import org.jomc.tools.JomcTool;
import org.jomc.tools.ResourceFileProcessor;
import org.jomc.tools.SourceFileProcessor;
import org.xml.sax.SAXException;

// SECTION-START[Documentation]
// <editor-fold defaultstate="collapsed" desc=" Generated Documentation ">
/**
 * Base JOMC {@code Command} implementation for processing modules.
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
 * <li>"{@link #getDocumentsOption DocumentsOption}"<blockquote>
 * Dependency on {@code 'org.apache.commons.cli.Option'} {@code (org.apache.commons.cli.Option)} bound to an instance.</blockquote></li>
 * <li>"{@link #getLocale Locale}"<blockquote>
 * Dependency on {@code 'java.util.Locale'} {@code (java.util.Locale)} at specification level 1.1 bound to an instance.</blockquote></li>
 * <li>"{@link #getModelOption ModelOption}"<blockquote>
 * Dependency on {@code 'org.apache.commons.cli.Option'} {@code (org.apache.commons.cli.Option)} bound to an instance.</blockquote></li>
 * <li>"{@link #getModletLocationOption ModletLocationOption}"<blockquote>
 * Dependency on {@code 'org.apache.commons.cli.Option'} {@code (org.apache.commons.cli.Option)} bound to an instance.</blockquote></li>
 * <li>"{@link #getModuleLocationOption ModuleLocationOption}"<blockquote>
 * Dependency on {@code 'org.apache.commons.cli.Option'} {@code (org.apache.commons.cli.Option)} bound to an instance.</blockquote></li>
 * <li>"{@link #getNoClasspathResolutionOption NoClasspathResolutionOption}"<blockquote>
 * Dependency on {@code 'org.apache.commons.cli.Option'} {@code (org.apache.commons.cli.Option)} bound to an instance.</blockquote></li>
 * <li>"{@link #getNoModelProcessingOption NoModelProcessingOption}"<blockquote>
 * Dependency on {@code 'org.apache.commons.cli.Option'} {@code (org.apache.commons.cli.Option)} bound to an instance.</blockquote></li>
 * <li>"{@link #getPlatformProviderLocationOption PlatformProviderLocationOption}"<blockquote>
 * Dependency on {@code 'org.apache.commons.cli.Option'} {@code (org.apache.commons.cli.Option)} bound to an instance.</blockquote></li>
 * <li>"{@link #getProviderLocationOption ProviderLocationOption}"<blockquote>
 * Dependency on {@code 'org.apache.commons.cli.Option'} {@code (org.apache.commons.cli.Option)} bound to an instance.</blockquote></li>
 * <li>"{@link #getTransformerLocationOption TransformerLocationOption}"<blockquote>
 * Dependency on {@code 'org.apache.commons.cli.Option'} {@code (org.apache.commons.cli.Option)} bound to an instance.</blockquote></li>
 * </ul></p>
 * <p><b>Messages</b><ul>
 * <li>"{@link #getApplicationTitle applicationTitle}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>JOMC Version 1.1-SNAPSHOT Build 2010-07-06T19:48:38+0200</pre></td></tr>
 * </table>
 * <li>"{@link #getCannotProcessMessage cannotProcessMessage}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>Cannot process ''{0}'': {1}</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Kann ''{0}'' nicht verarbeiten: {1}</pre></td></tr>
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
 * <li>"{@link #getDocumentFileInfo documentFileInfo}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>{0}: Document file: ''{1}''</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>{0}: Dokument-Datei: ''{1}''</pre></td></tr>
 * </table>
 * <li>"{@link #getDocumentFileNotFoundWarning documentFileNotFoundWarning}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>Document file ''{0}'' ignored. File not found.</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Dokument-Datei ''{0}'' ignoriert. Datei nicht gefunden.</pre></td></tr>
 * </table>
 * <li>"{@link #getExcludedModletInfo excludedModletInfo}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>{0}: Modlet ''{2}'' from class path resource ''{1}'' ignored.</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>{0}: Modlet ''{2}'' aus Klassenpfad-Ressource ''{1}'' ignoriert.</pre></td></tr>
 * </table>
 * <li>"{@link #getExcludedModuleFromClasspathInfo excludedModuleFromClasspathInfo}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>{0}: Module ''{1}'' from class path ignored. Module with identical name already loaded.</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>{0}: Modul ''{1}'' aus Klassenpfad ignoriert. Modul mit identischem Namen bereits geladen.</pre></td></tr>
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
 * <li>"{@link #getInvalidModelMessage invalidModelMessage}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>Invalid model.</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Ung&uuml;ltiges Modell.</pre></td></tr>
 * </table>
 * <li>"{@link #getLongDescriptionMessage longDescriptionMessage}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre></pre></td></tr>
 * </table>
 * <li>"{@link #getModulesReport modulesReport}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>{0}: Modules</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>{0}: Module</pre></td></tr>
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
@javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.1-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.1/jomc-tools" )
// </editor-fold>
// SECTION-END
public abstract class AbstractJomcToolCommand extends AbstractJomcCommand
{
    // SECTION-START[Command]
    // SECTION-END
    // SECTION-START[AbstractJomcToolCommand]

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
                                            this.log( Level.FINE,
                                                      this.getDocumentFileInfo( this.getLocale(),
                                                                                this.getClass().getName(),
                                                                                f.getAbsolutePath() ), null );

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
                                this.log( Level.FINE,
                                          this.getDocumentFileInfo( this.getLocale(), this.getClass().getName(),
                                                                    file.getAbsolutePath() ), null );

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

    protected Model getModel( final ModelContext context, final CommandLine commandLine )
        throws IOException, SAXException, JAXBException, ModelException
    {
        Model model = new Model();
        model.setIdentifier( this.getModel( commandLine ) );
        Modules modules = new Modules();

        if ( commandLine.hasOption( this.getDocumentsOption().getOpt() ) )
        {
            final Unmarshaller u = context.createUnmarshaller( model.getIdentifier() );
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
            final Model foundModel = context.findModel( model.getIdentifier() );
            final Modules modelModules = ModelHelper.getModules( foundModel );

            if ( modelModules != null )
            {
                for ( Module m : modelModules.getModule() )
                {
                    if ( modules.getModule( m.getName() ) == null )
                    {
                        modules.getModule().add( m );
                    }
                    else if ( this.isLoggable( Level.FINE ) )
                    {
                        this.log( Level.FINE,
                                  this.getExcludedModuleFromClasspathInfo( this.getLocale(), this.getClass().getName(),
                                                                           m.getName() ), null );

                    }
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

        ModelHelper.setModules( model, modules );

        if ( !commandLine.hasOption( this.getNoModelProcessingOption().getOpt() ) )
        {
            model = context.processModel( model );
            modules = ModelHelper.getModules( model );
        }

        assert modules != null : "Modules '" + this.getModel( commandLine ) + "' not found.";

        if ( this.isLoggable( Level.FINE ) )
        {
            this.log( Level.FINE, this.getModulesReport( this.getLocale(), this.getClass().getName() ), null );
            for ( Module m : modules.getModule() )
            {
                this.log( Level.FINE, this.getClass().getName() + ":\t" + m.getName(), null );
            }
        }

        return model;
    }

    protected final int executeCommand( final CommandLine commandLine ) throws Exception
    {
        try
        {
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


            return this.executeJomcToolCommand( commandLine );
        }
        finally
        {
            DefaultModelProcessor.setDefaultTransformerLocation( null );
            DefaultModelProvider.setDefaultModuleLocation( null );
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

    protected abstract int executeJomcToolCommand( final CommandLine commandLine ) throws Exception;

    // SECTION-END
    // SECTION-START[Constructors]
    // <editor-fold defaultstate="collapsed" desc=" Generated Constructors ">

    /** Creates a new {@code AbstractJomcToolCommand} instance. */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.1-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.1/jomc-tools" )
    public AbstractJomcToolCommand()
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
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.1-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.1/jomc-tools" )
    private org.apache.commons.cli.Option getClasspathOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "ClasspathOption" );
        assert _d != null : "'ClasspathOption' dependency not found.";
        return _d;
    }

    /**
     * Gets the {@code DocumentsOption} dependency.
     * <p>This method returns the {@code 'JOMC CLI Documents Option'} object of the {@code 'org.apache.commons.cli.Option'} {@code (org.apache.commons.cli.Option)} specification.</p>
     * <p>That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.</p>
     * @return The {@code DocumentsOption} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.1-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.1/jomc-tools" )
    private org.apache.commons.cli.Option getDocumentsOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "DocumentsOption" );
        assert _d != null : "'DocumentsOption' dependency not found.";
        return _d;
    }

    /**
     * Gets the {@code Locale} dependency.
     * <p>This method returns the {@code 'default'} object of the {@code 'java.util.Locale'} {@code (java.util.Locale)} specification at specification level 1.1.</p>
     * <p>That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.</p>
     * @return The {@code Locale} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.1-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.1/jomc-tools" )
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
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.1-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.1/jomc-tools" )
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
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.1-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.1/jomc-tools" )
    private org.apache.commons.cli.Option getModletLocationOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "ModletLocationOption" );
        assert _d != null : "'ModletLocationOption' dependency not found.";
        return _d;
    }

    /**
     * Gets the {@code ModuleLocationOption} dependency.
     * <p>This method returns the {@code 'JOMC CLI Module Location Option'} object of the {@code 'org.apache.commons.cli.Option'} {@code (org.apache.commons.cli.Option)} specification.</p>
     * <p>That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.</p>
     * @return The {@code ModuleLocationOption} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.1-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.1/jomc-tools" )
    private org.apache.commons.cli.Option getModuleLocationOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "ModuleLocationOption" );
        assert _d != null : "'ModuleLocationOption' dependency not found.";
        return _d;
    }

    /**
     * Gets the {@code NoClasspathResolutionOption} dependency.
     * <p>This method returns the {@code 'JOMC CLI No Classpath Resolution Option'} object of the {@code 'org.apache.commons.cli.Option'} {@code (org.apache.commons.cli.Option)} specification.</p>
     * <p>That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.</p>
     * @return The {@code NoClasspathResolutionOption} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.1-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.1/jomc-tools" )
    private org.apache.commons.cli.Option getNoClasspathResolutionOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "NoClasspathResolutionOption" );
        assert _d != null : "'NoClasspathResolutionOption' dependency not found.";
        return _d;
    }

    /**
     * Gets the {@code NoModelProcessingOption} dependency.
     * <p>This method returns the {@code 'JOMC CLI No Model Processing Option'} object of the {@code 'org.apache.commons.cli.Option'} {@code (org.apache.commons.cli.Option)} specification.</p>
     * <p>That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.</p>
     * @return The {@code NoModelProcessingOption} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.1-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.1/jomc-tools" )
    private org.apache.commons.cli.Option getNoModelProcessingOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "NoModelProcessingOption" );
        assert _d != null : "'NoModelProcessingOption' dependency not found.";
        return _d;
    }

    /**
     * Gets the {@code PlatformProviderLocationOption} dependency.
     * <p>This method returns the {@code 'JOMC CLI Platform Provider Location Option'} object of the {@code 'org.apache.commons.cli.Option'} {@code (org.apache.commons.cli.Option)} specification.</p>
     * <p>That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.</p>
     * @return The {@code PlatformProviderLocationOption} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.1-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.1/jomc-tools" )
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
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.1-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.1/jomc-tools" )
    private org.apache.commons.cli.Option getProviderLocationOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "ProviderLocationOption" );
        assert _d != null : "'ProviderLocationOption' dependency not found.";
        return _d;
    }

    /**
     * Gets the {@code TransformerLocationOption} dependency.
     * <p>This method returns the {@code 'JOMC CLI Transformer Location Option'} object of the {@code 'org.apache.commons.cli.Option'} {@code (org.apache.commons.cli.Option)} specification.</p>
     * <p>That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.</p>
     * @return The {@code TransformerLocationOption} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.1-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.1/jomc-tools" )
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
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.1-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.1/jomc-tools" )
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
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.1-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.1/jomc-tools" )
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
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.1-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.1/jomc-tools" )
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
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.1-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.1/jomc-tools" )
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
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.1-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.1/jomc-tools" )
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
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.1-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.1/jomc-tools" )
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
     * <tr><td valign="top">English:</td><td valign="top"><pre>JOMC Version 1.1-SNAPSHOT Build 2010-07-06T19:48:38+0200</pre></td></tr>
     * </table></p>
     * @param locale The locale of the message to return.
     * @return The text of the {@code applicationTitle} message.
     *
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.1-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.1/jomc-tools" )
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
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.1-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.1/jomc-tools" )
    private String getCannotProcessMessage( final java.util.Locale locale, final java.lang.String itemInfo, final java.lang.String detailMessage )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "cannotProcessMessage", locale, itemInfo, detailMessage );
        assert _m != null : "'cannotProcessMessage' message not found.";
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
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.1-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.1/jomc-tools" )
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
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.1-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.1/jomc-tools" )
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
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.1-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.1/jomc-tools" )
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
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.1-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.1/jomc-tools" )
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
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.1-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.1/jomc-tools" )
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
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.1-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.1/jomc-tools" )
    private String getDefaultLogLevelInfo( final java.util.Locale locale, final java.lang.String className, final java.lang.String defaultLogLevel )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "defaultLogLevelInfo", locale, className, defaultLogLevel );
        assert _m != null : "'defaultLogLevelInfo' message not found.";
        return _m;
    }

    /**
     * Gets the text of the {@code documentFileInfo} message.
     * <p><b>Templates</b><br/><table>
     * <tr><td valign="top">English:</td><td valign="top"><pre>{0}: Document file: ''{1}''</pre></td></tr>
     * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>{0}: Dokument-Datei: ''{1}''</pre></td></tr>
     * </table></p>
     * @param locale The locale of the message to return.
     * @param className Format argument.
     * @param documentFile Format argument.
     * @return The text of the {@code documentFileInfo} message.
     *
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.1-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.1/jomc-tools" )
    private String getDocumentFileInfo( final java.util.Locale locale, final java.lang.String className, final java.lang.String documentFile )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "documentFileInfo", locale, className, documentFile );
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
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.1-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.1/jomc-tools" )
    private String getDocumentFileNotFoundWarning( final java.util.Locale locale, final java.lang.String fileName )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "documentFileNotFoundWarning", locale, fileName );
        assert _m != null : "'documentFileNotFoundWarning' message not found.";
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
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.1-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.1/jomc-tools" )
    private String getExcludedModletInfo( final java.util.Locale locale, final java.lang.String className, final java.lang.String resourceName, final java.lang.String modletIdentifier )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "excludedModletInfo", locale, className, resourceName, modletIdentifier );
        assert _m != null : "'excludedModletInfo' message not found.";
        return _m;
    }

    /**
     * Gets the text of the {@code excludedModuleFromClasspathInfo} message.
     * <p><b>Templates</b><br/><table>
     * <tr><td valign="top">English:</td><td valign="top"><pre>{0}: Module ''{1}'' from class path ignored. Module with identical name already loaded.</pre></td></tr>
     * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>{0}: Modul ''{1}'' aus Klassenpfad ignoriert. Modul mit identischem Namen bereits geladen.</pre></td></tr>
     * </table></p>
     * @param locale The locale of the message to return.
     * @param className Format argument.
     * @param moduleName Format argument.
     * @return The text of the {@code excludedModuleFromClasspathInfo} message.
     *
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.1-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.1/jomc-tools" )
    private String getExcludedModuleFromClasspathInfo( final java.util.Locale locale, final java.lang.String className, final java.lang.String moduleName )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "excludedModuleFromClasspathInfo", locale, className, moduleName );
        assert _m != null : "'excludedModuleFromClasspathInfo' message not found.";
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
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.1-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.1/jomc-tools" )
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
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.1-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.1/jomc-tools" )
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
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.1-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.1/jomc-tools" )
    private String getExcludedServiceInfo( final java.util.Locale locale, final java.lang.String className, final java.lang.String resourceName, final java.lang.String serviceName )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "excludedServiceInfo", locale, className, resourceName, serviceName );
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
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.1-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.1/jomc-tools" )
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
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.1-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.1/jomc-tools" )
    private String getLongDescriptionMessage( final java.util.Locale locale )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "longDescriptionMessage", locale );
        assert _m != null : "'longDescriptionMessage' message not found.";
        return _m;
    }

    /**
     * Gets the text of the {@code modulesReport} message.
     * <p><b>Templates</b><br/><table>
     * <tr><td valign="top">English:</td><td valign="top"><pre>{0}: Modules</pre></td></tr>
     * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>{0}: Module</pre></td></tr>
     * </table></p>
     * @param locale The locale of the message to return.
     * @param className Format argument.
     * @return The text of the {@code modulesReport} message.
     *
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.1-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.1/jomc-tools" )
    private String getModulesReport( final java.util.Locale locale, final java.lang.String className )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "modulesReport", locale, className );
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
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.1-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.1/jomc-tools" )
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
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.1-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.1/jomc-tools" )
    private String getShortDescriptionMessage( final java.util.Locale locale )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "shortDescriptionMessage", locale );
        assert _m != null : "'shortDescriptionMessage' message not found.";
        return _m;
    }
    // </editor-fold>
    // SECTION-END
}
