// SECTION-START[License Header]
// <editor-fold defaultstate="collapsed" desc=" Generated License ">
/*
 *   Copyright (c) 2010 The JOMC Project
 *   Copyright (c) 2005 Christian Schulte <cs@jomc.org>
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

import java.io.File;
import java.util.logging.Level;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.util.JAXBSource;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.jomc.model.ModelContext;
import org.jomc.model.ModelValidationReport;
import org.jomc.model.Module;
import org.jomc.model.Modules;
import org.jomc.model.ObjectFactory;
import org.jomc.tools.JavaSources;

// SECTION-START[Documentation]
// <editor-fold defaultstate="collapsed" desc=" Generated Documentation ">
/**
 * Command line interface for the {@code JavaSources} tool.
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
 * <li>"{@link #getIndentationCharacterOption IndentationCharacterOption}"<blockquote>
 * Dependency on {@code org.apache.commons.cli.Option} bound to an instance.</blockquote></li>
 * <li>"{@link #getInputEncodingOption InputEncodingOption}"<blockquote>
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
 * <li>"{@link #getOutputEncodingOption OutputEncodingOption}"<blockquote>
 * Dependency on {@code org.apache.commons.cli.Option} bound to an instance.</blockquote></li>
 * <li>"{@link #getProfileOption ProfileOption}"<blockquote>
 * Dependency on {@code org.apache.commons.cli.Option} bound to an instance.</blockquote></li>
 * <li>"{@link #getSchemaLocationOption SchemaLocationOption}"<blockquote>
 * Dependency on {@code org.apache.commons.cli.Option} bound to an instance.</blockquote></li>
 * <li>"{@link #getServiceLocationOption ServiceLocationOption}"<blockquote>
 * Dependency on {@code org.apache.commons.cli.Option} bound to an instance.</blockquote></li>
 * <li>"{@link #getSourceDirectoryOption SourceDirectoryOption}"<blockquote>
 * Dependency on {@code org.apache.commons.cli.Option} bound to an instance.</blockquote></li>
 * <li>"{@link #getTemplateEncodingOption TemplateEncodingOption}"<blockquote>
 * Dependency on {@code org.apache.commons.cli.Option} bound to an instance.</blockquote></li>
 * <li>"{@link #getTransformerLocationOption TransformerLocationOption}"<blockquote>
 * Dependency on {@code org.apache.commons.cli.Option} bound to an instance.</blockquote></li>
 * <li>"{@link #getWhitepsacesPerIndentOption WhitepsacesPerIndentOption}"<blockquote>
 * Dependency on {@code org.apache.commons.cli.Option} bound to an instance.</blockquote></li>
 * </ul></p>
 * <p><b>Messages</b><ul>
 * <li>"{@link #getApplicationTitleMessage applicationTitle}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>JOMC Version 1.0-alpha-17-SNAPSHOT Build 2010-02-11T23:16:00+0000</pre></td></tr>
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
 * <tr><td valign="top">English:</td><td valign="top"><pre>Example:
 *   jomc manage-java-sources -cp &lt;classpath&gt; \
 *                            -sd /tmp/src \
 *                            -df examples/xml/jomc-cli.xml \
 *                            -mn &quot;JOMC CLI&quot; \
 *                            -v</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Beispiel:
 *   jomc manage-java-sources -cp &lt;classpath&gt; \
 *                            -sd /tmp/src \
 *                            -df examples/xml/jomc-cli.xml \
 *                            -mn &quot;JOMC CLI&quot; \
 *                            -v</pre></td></tr>
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
 * <tr><td valign="top">English:</td><td valign="top"><pre>Manages Java source files.</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Verwaltet Java Quelltext-Dateien.</pre></td></tr>
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
 * @author <a href="mailto:cs@jomc.org">Christian Schulte</a> 1.0
 * @version $Id$
 */
// </editor-fold>
// SECTION-END
// SECTION-START[Annotations]
// <editor-fold defaultstate="collapsed" desc=" Generated Annotations ">
@javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                             comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-17-SNAPSHOT/jomc-tools" )
// </editor-fold>
// SECTION-END
public final class ManageJavaSourcesCommand extends AbstractJomcCommand
{
    // SECTION-START[Command]

    /** Options of the instance. */
    private Options options;

    @Override
    public Options getOptions()
    {
        if ( this.options == null )
        {
            this.options = super.getOptions();
            this.options.addOption( this.getSourceDirectoryOption() );
            this.options.addOption( this.getProfileOption() );
            this.options.addOption( this.getTemplateEncodingOption() );
            this.options.addOption( this.getInputEncodingOption() );
            this.options.addOption( this.getOutputEncodingOption() );
            this.options.addOption( this.getWhitepsacesPerIndentOption() );
            this.options.addOption( this.getIndentationCharacterOption() );
        }

        return this.options;
    }

    public int executeCommand( final CommandLine commandLine ) throws Exception
    {
        final ClassLoader classLoader = this.getClassLoader( commandLine );
        final ModelContext context = this.getModelContext( classLoader );
        final Modules modules = this.getModules( context, commandLine );
        final JAXBContext jaxbContext = context.createContext();
        final Marshaller marshaller = context.createMarshaller();
        final ModelValidationReport validationReport =
            context.validateModel( new JAXBSource( jaxbContext, new ObjectFactory().createModules( modules ) ) );

        this.log( validationReport, marshaller );

        if ( validationReport.isModelValid() )
        {
            final JavaSources tool = this.getJavaSources();
            tool.setModules( modules );

            if ( commandLine.hasOption( this.getProfileOption().getOpt() ) )
            {
                tool.setProfile( commandLine.getOptionValue( this.getProfileOption().getOpt() ) );
            }
            if ( commandLine.hasOption( this.getTemplateEncodingOption().getOpt() ) )
            {
                tool.setTemplateEncoding( commandLine.getOptionValue( this.getTemplateEncodingOption().getOpt() ) );
            }
            if ( commandLine.hasOption( this.getInputEncodingOption().getOpt() ) )
            {
                tool.setInputEncoding( commandLine.getOptionValue( this.getInputEncodingOption().getOpt() ) );
            }
            if ( commandLine.hasOption( this.getOutputEncodingOption().getOpt() ) )
            {
                tool.setOutputEncoding( commandLine.getOptionValue( this.getOutputEncodingOption().getOpt() ) );
            }
            if ( commandLine.hasOption( this.getWhitepsacesPerIndentOption().getOpt() ) )
            {
                tool.setWhitespacesPerIndent( Integer.parseInt( commandLine.getOptionValue(
                    this.getWhitepsacesPerIndentOption().getOpt() ) ) );

            }
            if ( commandLine.hasOption( this.getIndentationCharacterOption().getOpt() ) )
            {
                tool.setIndentationCharacter( commandLine.getOptionValue(
                    this.getIndentationCharacterOption().getOpt() ).charAt( 0 ) );

            }

            final File sourcesDirectory =
                new File( commandLine.getOptionValue( this.getSourceDirectoryOption().getOpt() ) );

            if ( commandLine.hasOption( this.getModuleNameOption().getOpt() ) )
            {
                final String moduleName = commandLine.getOptionValue( this.getModuleNameOption().getOpt() );
                final Module module = tool.getModules().getModule( moduleName );

                if ( module != null )
                {
                    if ( this.isLoggable( Level.INFO ) )
                    {
                        this.log( Level.INFO, this.getStartingModuleProcessingMessage(
                            this.getLocale(), this.getCommandName(), module.getName() ), null );

                    }

                    tool.manageSources( module, sourcesDirectory );
                }
                else if ( this.isLoggable( Level.WARNING ) )
                {
                    this.log( Level.WARNING, this.getMissingModuleMessage( this.getLocale(), moduleName ), null );
                }
            }
            else
            {
                if ( this.isLoggable( Level.INFO ) )
                {
                    this.log( Level.INFO, this.getStartingProcessingMessage(
                        this.getLocale(), this.getCommandName() ), null );

                }

                tool.manageSources( sourcesDirectory );
            }

            return STATUS_SUCCESS;
        }

        return STATUS_FAILURE;
    }

    // SECTION-END
    // SECTION-START[ManageJavaSourcesCommand]
    // SECTION-END
    // SECTION-START[Constructors]
    // <editor-fold defaultstate="collapsed" desc=" Generated Constructors ">

    /** Creates a new {@code ManageJavaSourcesCommand} instance. */
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-17-SNAPSHOT/jomc-tools" )
    public ManageJavaSourcesCommand()
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
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-17-SNAPSHOT/jomc-tools" )
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
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-17-SNAPSHOT/jomc-tools" )
    private org.apache.commons.cli.Option getDocumentsOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "DocumentsOption" );
        assert _d != null : "'DocumentsOption' dependency not found.";
        return _d;
    }

    /**
     * Gets the {@code IndentationCharacterOption} dependency.
     * <p>This method returns the "{@code JOMC CLI Indentation Character Option}" object of the {@code org.apache.commons.cli.Option} specification.</p>
     * <p>That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.</p>
     * @return The {@code IndentationCharacterOption} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-17-SNAPSHOT/jomc-tools" )
    private org.apache.commons.cli.Option getIndentationCharacterOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "IndentationCharacterOption" );
        assert _d != null : "'IndentationCharacterOption' dependency not found.";
        return _d;
    }

    /**
     * Gets the {@code InputEncodingOption} dependency.
     * <p>This method returns the "{@code JOMC CLI Input Encoding Option}" object of the {@code org.apache.commons.cli.Option} specification.</p>
     * <p>That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.</p>
     * @return The {@code InputEncodingOption} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-17-SNAPSHOT/jomc-tools" )
    private org.apache.commons.cli.Option getInputEncodingOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "InputEncodingOption" );
        assert _d != null : "'InputEncodingOption' dependency not found.";
        return _d;
    }

    /**
     * Gets the {@code Locale} dependency.
     * <p>This method returns the "{@code default}" object of the {@code java.util.Locale} specification at specification level 1.1.</p>
     * <p>That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.</p>
     * @return The {@code Locale} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-17-SNAPSHOT/jomc-tools" )
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
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-17-SNAPSHOT/jomc-tools" )
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
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-17-SNAPSHOT/jomc-tools" )
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
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-17-SNAPSHOT/jomc-tools" )
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
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-17-SNAPSHOT/jomc-tools" )
    private org.apache.commons.cli.Option getNoModelProcessingOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "NoModelProcessingOption" );
        assert _d != null : "'NoModelProcessingOption' dependency not found.";
        return _d;
    }

    /**
     * Gets the {@code OutputEncodingOption} dependency.
     * <p>This method returns the "{@code JOMC CLI Output Encoding Option}" object of the {@code org.apache.commons.cli.Option} specification.</p>
     * <p>That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.</p>
     * @return The {@code OutputEncodingOption} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-17-SNAPSHOT/jomc-tools" )
    private org.apache.commons.cli.Option getOutputEncodingOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "OutputEncodingOption" );
        assert _d != null : "'OutputEncodingOption' dependency not found.";
        return _d;
    }

    /**
     * Gets the {@code ProfileOption} dependency.
     * <p>This method returns the "{@code JOMC CLI Profile Option}" object of the {@code org.apache.commons.cli.Option} specification.</p>
     * <p>That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.</p>
     * @return The {@code ProfileOption} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-17-SNAPSHOT/jomc-tools" )
    private org.apache.commons.cli.Option getProfileOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "ProfileOption" );
        assert _d != null : "'ProfileOption' dependency not found.";
        return _d;
    }

    /**
     * Gets the {@code SchemaLocationOption} dependency.
     * <p>This method returns the "{@code JOMC CLI Schema Location Option}" object of the {@code org.apache.commons.cli.Option} specification.</p>
     * <p>That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.</p>
     * @return The {@code SchemaLocationOption} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-17-SNAPSHOT/jomc-tools" )
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
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-17-SNAPSHOT/jomc-tools" )
    private org.apache.commons.cli.Option getServiceLocationOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "ServiceLocationOption" );
        assert _d != null : "'ServiceLocationOption' dependency not found.";
        return _d;
    }

    /**
     * Gets the {@code SourceDirectoryOption} dependency.
     * <p>This method returns the "{@code JOMC CLI Source Directory Option}" object of the {@code org.apache.commons.cli.Option} specification.</p>
     * <p>That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.</p>
     * <p><b>Properties</b><dl>
     * <dt>"{@code required}"</dt>
     * <dd>Property of type {@code boolean}.
     * </dd>
     * </dl>
     * @return The {@code SourceDirectoryOption} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-17-SNAPSHOT/jomc-tools" )
    private org.apache.commons.cli.Option getSourceDirectoryOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "SourceDirectoryOption" );
        assert _d != null : "'SourceDirectoryOption' dependency not found.";
        return _d;
    }

    /**
     * Gets the {@code TemplateEncodingOption} dependency.
     * <p>This method returns the "{@code JOMC CLI Template Encoding Option}" object of the {@code org.apache.commons.cli.Option} specification.</p>
     * <p>That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.</p>
     * @return The {@code TemplateEncodingOption} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-17-SNAPSHOT/jomc-tools" )
    private org.apache.commons.cli.Option getTemplateEncodingOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "TemplateEncodingOption" );
        assert _d != null : "'TemplateEncodingOption' dependency not found.";
        return _d;
    }

    /**
     * Gets the {@code TransformerLocationOption} dependency.
     * <p>This method returns the "{@code JOMC CLI Transformer Location Option}" object of the {@code org.apache.commons.cli.Option} specification.</p>
     * <p>That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.</p>
     * @return The {@code TransformerLocationOption} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-17-SNAPSHOT/jomc-tools" )
    private org.apache.commons.cli.Option getTransformerLocationOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "TransformerLocationOption" );
        assert _d != null : "'TransformerLocationOption' dependency not found.";
        return _d;
    }

    /**
     * Gets the {@code WhitepsacesPerIndentOption} dependency.
     * <p>This method returns the "{@code JOMC CLI Whitespaces Per Indent Option}" object of the {@code org.apache.commons.cli.Option} specification.</p>
     * <p>That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.</p>
     * @return The {@code WhitepsacesPerIndentOption} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-17-SNAPSHOT/jomc-tools" )
    private org.apache.commons.cli.Option getWhitepsacesPerIndentOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "WhitepsacesPerIndentOption" );
        assert _d != null : "'WhitepsacesPerIndentOption' dependency not found.";
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
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-17-SNAPSHOT/jomc-tools" )
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
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-17-SNAPSHOT/jomc-tools" )
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
     * <tr><td valign="top">English:</td><td valign="top"><pre>JOMC Version 1.0-alpha-17-SNAPSHOT Build 2010-02-11T23:16:00+0000</pre></td></tr>
     * </table></p>
     * @param locale The locale of the message to return.
     * @return The text of the {@code applicationTitle} message.
     *
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-17-SNAPSHOT/jomc-tools" )
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
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-17-SNAPSHOT/jomc-tools" )
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
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-17-SNAPSHOT/jomc-tools" )
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
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-17-SNAPSHOT/jomc-tools" )
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
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-17-SNAPSHOT/jomc-tools" )
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
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-17-SNAPSHOT/jomc-tools" )
    private String getInvalidModelMessage( final java.util.Locale locale )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "invalidModel", locale );
        assert _m != null : "'invalidModel' message not found.";
        return _m;
    }

    /**
     * Gets the text of the {@code longDescription} message.
     * <p><b>Templates</b><br/><table>
     * <tr><td valign="top">English:</td><td valign="top"><pre>Example:
     *   jomc manage-java-sources -cp &lt;classpath&gt; \
     *                            -sd /tmp/src \
     *                            -df examples/xml/jomc-cli.xml \
     *                            -mn &quot;JOMC CLI&quot; \
     *                            -v</pre></td></tr>
     * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Beispiel:
     *   jomc manage-java-sources -cp &lt;classpath&gt; \
     *                            -sd /tmp/src \
     *                            -df examples/xml/jomc-cli.xml \
     *                            -mn &quot;JOMC CLI&quot; \
     *                            -v</pre></td></tr>
     * </table></p>
     * @param locale The locale of the message to return.
     * @return The text of the {@code longDescription} message.
     *
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-17-SNAPSHOT/jomc-tools" )
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
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-17-SNAPSHOT/jomc-tools" )
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
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-17-SNAPSHOT/jomc-tools" )
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
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-17-SNAPSHOT/jomc-tools" )
    private String getSeparatorMessage( final java.util.Locale locale )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "separator", locale );
        assert _m != null : "'separator' message not found.";
        return _m;
    }

    /**
     * Gets the text of the {@code shortDescription} message.
     * <p><b>Templates</b><br/><table>
     * <tr><td valign="top">English:</td><td valign="top"><pre>Manages Java source files.</pre></td></tr>
     * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Verwaltet Java Quelltext-Dateien.</pre></td></tr>
     * </table></p>
     * @param locale The locale of the message to return.
     * @return The text of the {@code shortDescription} message.
     *
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-17-SNAPSHOT/jomc-tools" )
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
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-17-SNAPSHOT/jomc-tools" )
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
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-17-SNAPSHOT/jomc-tools" )
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
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-17-SNAPSHOT/jomc-tools" )
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
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-17-SNAPSHOT/jomc-tools" )
    private String getToolSuccessMessage( final java.util.Locale locale, final java.lang.String toolName )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "toolSuccess", locale, toolName );
        assert _m != null : "'toolSuccess' message not found.";
        return _m;
    }
    // </editor-fold>
    // SECTION-END
}
