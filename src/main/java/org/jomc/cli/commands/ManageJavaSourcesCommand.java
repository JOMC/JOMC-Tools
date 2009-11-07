// SECTION-START[License Header]
/*
 *   Copyright (c) 2009 The JOMC Project
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
// SECTION-END
package org.jomc.cli.commands;

import java.io.File;
import java.util.logging.Level;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.validation.Schema;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.jomc.model.ModelObjectValidationReport;
import org.jomc.model.Module;
import org.jomc.model.Modules;
import org.jomc.model.ObjectFactory;
import org.jomc.tools.JavaSources;

// SECTION-START[Documentation]
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
 * <li>"{@link #getClasspathOptionLongName classpathOptionLongName}"
 * <blockquote>Property of type {@code java.lang.String}.
 * <p>Long name of the 'classpath' option.</p>
 * </blockquote></li>
 * <li>"{@link #getClasspathOptionShortName classpathOptionShortName}"
 * <blockquote>Property of type {@code java.lang.String}.
 * <p>Name of the 'classpath' option.</p>
 * </blockquote></li>
 * <li>"{@link #getCommandName commandName}"
 * <blockquote>Property of type {@code java.lang.String}.
 * <p>Name of the command.</p>
 * </blockquote></li>
 * <li>"{@link #getDocumentsOptionLongName documentsOptionLongName}"
 * <blockquote>Property of type {@code java.lang.String}.
 * <p>Long name of the 'documents' option.</p>
 * </blockquote></li>
 * <li>"{@link #getDocumentsOptionShortName documentsOptionShortName}"
 * <blockquote>Property of type {@code java.lang.String}.
 * <p>Name of the 'documents' option.</p>
 * </blockquote></li>
 * <li>"{@link #getInputEncodingOptionLongName inputEncodingOptionLongName}"
 * <blockquote>Property of type {@code java.lang.String}.
 * <p>Long name of the 'input-encoding' option.</p>
 * </blockquote></li>
 * <li>"{@link #getInputEncodingOptionShortName inputEncodingOptionShortName}"
 * <blockquote>Property of type {@code java.lang.String}.
 * <p>Name of the 'input-encoding' option.</p>
 * </blockquote></li>
 * <li>"{@link #getModuleLocationOptionLongName moduleLocationOptionLongName}"
 * <blockquote>Property of type {@code java.lang.String}.
 * <p>Long name of the 'module-location' option.</p>
 * </blockquote></li>
 * <li>"{@link #getModuleLocationOptionShortName moduleLocationOptionShortName}"
 * <blockquote>Property of type {@code java.lang.String}.
 * <p>Name of the 'module-location' option.</p>
 * </blockquote></li>
 * <li>"{@link #getModuleNameOptionLongName moduleNameOptionLongName}"
 * <blockquote>Property of type {@code java.lang.String}.
 * <p>Long name of the 'module' option.</p>
 * </blockquote></li>
 * <li>"{@link #getModuleNameOptionShortName moduleNameOptionShortName}"
 * <blockquote>Property of type {@code java.lang.String}.
 * <p>Name of the 'module' option.</p>
 * </blockquote></li>
 * <li>"{@link #getNoClasspathResolutionOptionLongName noClasspathResolutionOptionLongName}"
 * <blockquote>Property of type {@code java.lang.String}.
 * <p>Long name of the 'no-classpath-resolution' option.</p>
 * </blockquote></li>
 * <li>"{@link #getNoClasspathResolutionOptionShortName noClasspathResolutionOptionShortName}"
 * <blockquote>Property of type {@code java.lang.String}.
 * <p>Name of the 'no-classpath-resolution' option.</p>
 * </blockquote></li>
 * <li>"{@link #getOutputEncodingOptionLongName outputEncodingOptionLongName}"
 * <blockquote>Property of type {@code java.lang.String}.
 * <p>Long name of the 'output-encoding' option.</p>
 * </blockquote></li>
 * <li>"{@link #getOutputEncodingOptionShortName outputEncodingOptionShortName}"
 * <blockquote>Property of type {@code java.lang.String}.
 * <p>Name of the 'output-encoding' option.</p>
 * </blockquote></li>
 * <li>"{@link #getProfileOptionLongName profileOptionLongName}"
 * <blockquote>Property of type {@code java.lang.String}.
 * <p>Long name of the 'profile' option.</p>
 * </blockquote></li>
 * <li>"{@link #getProfileOptionShortName profileOptionShortName}"
 * <blockquote>Property of type {@code java.lang.String}.
 * <p>Name of the 'profile' option.</p>
 * </blockquote></li>
 * <li>"{@link #getSourceDirectoryOptionLongName sourceDirectoryOptionLongName}"
 * <blockquote>Property of type {@code java.lang.String}.
 * <p>Long name of the 'source-dir' option.</p>
 * </blockquote></li>
 * <li>"{@link #getSourceDirectoryOptionShortName sourceDirectoryOptionShortName}"
 * <blockquote>Property of type {@code java.lang.String}.
 * <p>Name of the 'source-dir' option.</p>
 * </blockquote></li>
 * <li>"{@link #getTemplateEncodingOptionLongName templateEncodingOptionLongName}"
 * <blockquote>Property of type {@code java.lang.String}.
 * <p>Long name of the 'template-encoding' option.</p>
 * </blockquote></li>
 * <li>"{@link #getTemplateEncodingOptionShortName templateEncodingOptionShortName}"
 * <blockquote>Property of type {@code java.lang.String}.
 * <p>Name of the 'template-encoding' option.</p>
 * </blockquote></li>
 * </ul></p>
 * <p><b>Dependencies</b><ul>
 * <li>"{@link #getLocale Locale}"<blockquote>
 * Dependency on {@code java.util.Locale} at specification level 1.1 bound to an instance.</blockquote></li>
 * </ul></p>
 * <p><b>Messages</b><ul>
 * <li>"{@link #getApplicationTitleMessage applicationTitle}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>JOMC Version 1.0-alpha-8-SNAPSHOT Build 2009-11-07T23:17:40+0000</pre></td></tr>
 * </table>
 * <li>"{@link #getCannotProcessMessage cannotProcess}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>Cannot process ''{0}'': {1}</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Kann ''{0}'' nicht verarbeiten: {1}</pre></td></tr>
 * </table>
 * <li>"{@link #getClasspathElementMessage classpathElement}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>Classpath element: ''{0}''</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Klassenpfad-Element: ''{0}''</pre></td></tr>
 * </table>
 * <li>"{@link #getClasspathOptionMessage classpathOption}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>Classpath elements separated by ''{0}''. If starting with a ''@'' character, a file name of a file holding classpath elements.</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Klassenpfad-Elemente mit ''{0}'' getrennt. Wenn mit ''@'' beginnend, Dateiname einer Textdatei mit Klassenpfad-Elementen.</pre></td></tr>
 * </table>
 * <li>"{@link #getClasspathOptionArgNameMessage classpathOptionArgName}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>elements</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Elemente</pre></td></tr>
 * </table>
 * <li>"{@link #getDefaultLogLevelInfoMessage defaultLogLevelInfo}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>Default log level: ''{0}''</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Standard-Protokollierungsstufe: ''{0}''</pre></td></tr>
 * </table>
 * <li>"{@link #getDocumentFileMessage documentFile}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>Document file: ''{0}''</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Dokument-Datei: ''{0}''</pre></td></tr>
 * </table>
 * <li>"{@link #getDocumentsOptionMessage documentsOption}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>Document filenames separated by ''{0}''. If starting with a ''@'' character, a file name of a file holding document filenames.</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Dokument-Dateinamen mit ''{0}'' getrennt. Wenn mit ''@'' beginnend, Dateiname einer Textdatei mit Dokument-Dateinamen.</pre></td></tr>
 * </table>
 * <li>"{@link #getDocumentsOptionArgNameMessage documentsOptionArgName}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>files</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Dateien</pre></td></tr>
 * </table>
 * <li>"{@link #getInputEncodingOptionMessage inputEncodingOption}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>Input encoding.</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Eingabekodierung.</pre></td></tr>
 * </table>
 * <li>"{@link #getInputEncodingOptionArgNameMessage inputEncodingOptionArgName}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>encoding</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Kodierung</pre></td></tr>
 * </table>
 * <li>"{@link #getInvalidModelMessage invalidModel}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>Invalid model.</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Ung&uuml;ltiges Modell.</pre></td></tr>
 * </table>
 * <li>"{@link #getLongDescriptionMessage longDescription}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>Example:
 *   jomc manage-java-sources -sd /tmp/src -df examples/xml/jomc-cli.xml } \
 *                            -mn &quot;JOMC CLI&quot; -v</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Beispiel:
 *   jomc manage-java-sources -sd /tmp/src -df examples/xml/jomc-cli.xml \
 *                            -mn &quot;JOMC CLI&quot; -v</pre></td></tr>
 * </table>
 * <li>"{@link #getMissingModuleMessage missingModule}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>Module ''{0}'' not found.</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Modul ''{0}'' nicht gefunden.</pre></td></tr>
 * </table>
 * <li>"{@link #getModuleLocationOptionMessage moduleLocationOption}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>Location of classpath modules.</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Ort der Klassenpfad-Module.</pre></td></tr>
 * </table>
 * <li>"{@link #getModuleLocationOptionArgNameMessage moduleLocationOptionArgName}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>location</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Ort</pre></td></tr>
 * </table>
 * <li>"{@link #getModuleNameOptionMessage moduleNameOption}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>Name of the module to process.</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Name des zu verarbeitenden Moduls.</pre></td></tr>
 * </table>
 * <li>"{@link #getModuleNameOptionArgNameMessage moduleNameOptionArgName}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>name</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Name</pre></td></tr>
 * </table>
 * <li>"{@link #getModulesReportMessage modulesReport}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>Modules</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Module</pre></td></tr>
 * </table>
 * <li>"{@link #getNoClasspathResolutionOptionMessage noClasspathResolutionOption}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>Do not perform classpath resolution.</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Keine Klassenpfad-Aufl&ouml;sung durchf&uuml;hren.</pre></td></tr>
 * </table>
 * <li>"{@link #getOutputEncodingOptionMessage outputEncodingOption}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>Output encoding.</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Ausgabekodierung.</pre></td></tr>
 * </table>
 * <li>"{@link #getOutputEncodingOptionArgNameMessage outputEncodingOptionArgName}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>encoding</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Kodierung</pre></td></tr>
 * </table>
 * <li>"{@link #getProfileOptionMessage profileOption}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>Templates profile to use.</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Zu verwendendes Vorlagen-Profil.</pre></td></tr>
 * </table>
 * <li>"{@link #getProfileOptionArgNameMessage profileOptionArgName}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>profile</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Profil</pre></td></tr>
 * </table>
 * <li>"{@link #getSeparatorMessage separator}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>--------------------------------------------------------------------------------</pre></td></tr>
 * </table>
 * <li>"{@link #getShortDescriptionMessage shortDescription}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>Manages Java source files.</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Verwaltet Java Quelltext-Dateien.</pre></td></tr>
 * </table>
 * <li>"{@link #getSourceDirectoryOptionMessage sourceDirectoryOption}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>Directory to write source files to.</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Verzeichnis in das Quelltext-Dateien geschrieben werden sollen.</pre></td></tr>
 * </table>
 * <li>"{@link #getSourceDirectoryOptionArgNameMessage sourceDirectoryOptionArgName}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>directory</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Verzeichnis</pre></td></tr>
 * </table>
 * <li>"{@link #getStartingModuleProcessingMessage startingModuleProcessing}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>Executing command {0} with module ''{1}'' ...</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>F&uuml;hrt Befehl {0} mit Modul ''{1}'' aus ... </pre></td></tr>
 * </table>
 * <li>"{@link #getStartingProcessingMessage startingProcessing}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>Executing command {0} ...</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>F&uuml;hrt Befehl {0} aus ... </pre></td></tr>
 * </table>
 * <li>"{@link #getTemplateEncodingOptionMessage templateEncodingOption}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>Template encoding.</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Vorlagenkodierung.</pre></td></tr>
 * </table>
 * <li>"{@link #getTemplateEncodingOptionArgNameMessage templateEncodingOptionArgName}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>encoding</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Kodierung</pre></td></tr>
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
// SECTION-END
// SECTION-START[Annotations]
@javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                             comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-8-SNAPSHOT/jomc-tools" )
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
        }

        return this.options;
    }

    public int executeCommand( final CommandLine commandLine ) throws Exception
    {
        final Modules modules = this.getModules( commandLine );
        final ClassLoader classLoader = this.getClassLoader( commandLine );
        final JAXBContext context = this.getModelManager().getContext( classLoader );
        final Marshaller marshaller = this.getModelManager().getMarshaller( classLoader );
        final Schema schema = this.getModelManager().getSchema( classLoader );
        final ModelObjectValidationReport validationReport = this.getModelObjectValidator().validateModelObject(
            new ObjectFactory().createModules( modules ), context, schema );

        this.log( validationReport, marshaller );

        if ( validationReport.isModelObjectValid() )
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
    private Option sourceDirectoryOption;

    private Option profileOption;

    private Option templateEncodingOption;

    private Option outputEncodingOption;

    private Option inputEncodingOption;

    protected Option getSourceDirectoryOption()
    {
        if ( this.sourceDirectoryOption == null )
        {
            this.sourceDirectoryOption = new Option( this.getSourceDirectoryOptionShortName(),
                                                     this.getSourceDirectoryOptionLongName(), true,
                                                     this.getSourceDirectoryOptionMessage( this.getLocale() ) );

            this.sourceDirectoryOption.setRequired( true );
            this.sourceDirectoryOption.setArgName( this.getSourceDirectoryOptionArgNameMessage( this.getLocale() ) );
        }

        return this.sourceDirectoryOption;
    }

    protected Option getProfileOption()
    {
        if ( this.profileOption == null )
        {
            this.profileOption = new Option( this.getProfileOptionShortName(), this.getProfileOptionLongName(),
                                             true, this.getProfileOptionMessage( this.getLocale() ) );

            this.profileOption.setArgName( this.getProfileOptionArgNameMessage( this.getLocale() ) );
        }

        return this.profileOption;
    }

    protected Option getTemplateEncodingOption()
    {
        if ( this.templateEncodingOption == null )
        {
            this.templateEncodingOption = new Option( this.getTemplateEncodingOptionShortName(),
                                                      this.getTemplateEncodingOptionLongName(), true,
                                                      this.getTemplateEncodingOptionMessage( this.getLocale() ) );

            this.templateEncodingOption.setArgName( this.getTemplateEncodingOptionArgNameMessage( this.getLocale() ) );
        }

        return this.templateEncodingOption;
    }

    protected Option getInputEncodingOption()
    {
        if ( this.inputEncodingOption == null )
        {
            this.inputEncodingOption = new Option( this.getInputEncodingOptionShortName(),
                                                   this.getInputEncodingOptionLongName(), true,
                                                   this.getInputEncodingOptionMessage( this.getLocale() ) );

            this.inputEncodingOption.setArgName( this.getInputEncodingOptionArgNameMessage( this.getLocale() ) );
        }

        return this.inputEncodingOption;
    }

    protected Option getOutputEncodingOption()
    {
        if ( this.outputEncodingOption == null )
        {
            this.outputEncodingOption = new Option( this.getOutputEncodingOptionShortName(),
                                                    this.getOutputEncodingOptionLongName(), true,
                                                    this.getOutputEncodingOptionMessage( this.getLocale() ) );

            this.outputEncodingOption.setArgName( this.getOutputEncodingOptionArgNameMessage( this.getLocale() ) );
        }

        return this.outputEncodingOption;
    }

    // SECTION-END
    // SECTION-START[Constructors]

    /** Creates a new {@code ManageJavaSourcesCommand} instance. */
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-8-SNAPSHOT/jomc-tools" )
    public ManageJavaSourcesCommand()
    {
        // SECTION-START[Default Constructor]
        super();
        // SECTION-END
    }
    // SECTION-END
    // SECTION-START[Dependencies]

    /**
     * Gets the {@code Locale} dependency.
     * <p>This method returns the "{@code default}" object of the {@code java.util.Locale} specification at specification level 1.1.</p>
     * <p>That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.</p>
     * @return The {@code Locale} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-8-SNAPSHOT/jomc-tools" )
    private java.util.Locale getLocale()
    {
        final java.util.Locale _d = (java.util.Locale) org.jomc.ObjectManagerFactory.getObjectManager().getDependency( this, "Locale" );
        assert _d != null : "'Locale' dependency not found.";
        return _d;
    }
    // SECTION-END
    // SECTION-START[Properties]

    /**
     * Gets the value of the {@code abbreviatedCommandName} property.
     * @return Abbreviated name of the command.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-8-SNAPSHOT/jomc-tools" )
    private java.lang.String getAbbreviatedCommandName()
    {
        final java.lang.String _p = (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager().getProperty( this, "abbreviatedCommandName" );
        assert _p != null : "'abbreviatedCommandName' property not found.";
        return _p;
    }

    /**
     * Gets the value of the {@code classpathOptionLongName} property.
     * @return Long name of the 'classpath' option.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-8-SNAPSHOT/jomc-tools" )
    private java.lang.String getClasspathOptionLongName()
    {
        final java.lang.String _p = (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager().getProperty( this, "classpathOptionLongName" );
        assert _p != null : "'classpathOptionLongName' property not found.";
        return _p;
    }

    /**
     * Gets the value of the {@code classpathOptionShortName} property.
     * @return Name of the 'classpath' option.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-8-SNAPSHOT/jomc-tools" )
    private java.lang.String getClasspathOptionShortName()
    {
        final java.lang.String _p = (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager().getProperty( this, "classpathOptionShortName" );
        assert _p != null : "'classpathOptionShortName' property not found.";
        return _p;
    }

    /**
     * Gets the value of the {@code commandName} property.
     * @return Name of the command.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-8-SNAPSHOT/jomc-tools" )
    private java.lang.String getCommandName()
    {
        final java.lang.String _p = (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager().getProperty( this, "commandName" );
        assert _p != null : "'commandName' property not found.";
        return _p;
    }

    /**
     * Gets the value of the {@code documentsOptionLongName} property.
     * @return Long name of the 'documents' option.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-8-SNAPSHOT/jomc-tools" )
    private java.lang.String getDocumentsOptionLongName()
    {
        final java.lang.String _p = (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager().getProperty( this, "documentsOptionLongName" );
        assert _p != null : "'documentsOptionLongName' property not found.";
        return _p;
    }

    /**
     * Gets the value of the {@code documentsOptionShortName} property.
     * @return Name of the 'documents' option.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-8-SNAPSHOT/jomc-tools" )
    private java.lang.String getDocumentsOptionShortName()
    {
        final java.lang.String _p = (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager().getProperty( this, "documentsOptionShortName" );
        assert _p != null : "'documentsOptionShortName' property not found.";
        return _p;
    }

    /**
     * Gets the value of the {@code inputEncodingOptionLongName} property.
     * @return Long name of the 'input-encoding' option.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-8-SNAPSHOT/jomc-tools" )
    private java.lang.String getInputEncodingOptionLongName()
    {
        final java.lang.String _p = (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager().getProperty( this, "inputEncodingOptionLongName" );
        assert _p != null : "'inputEncodingOptionLongName' property not found.";
        return _p;
    }

    /**
     * Gets the value of the {@code inputEncodingOptionShortName} property.
     * @return Name of the 'input-encoding' option.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-8-SNAPSHOT/jomc-tools" )
    private java.lang.String getInputEncodingOptionShortName()
    {
        final java.lang.String _p = (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager().getProperty( this, "inputEncodingOptionShortName" );
        assert _p != null : "'inputEncodingOptionShortName' property not found.";
        return _p;
    }

    /**
     * Gets the value of the {@code moduleLocationOptionLongName} property.
     * @return Long name of the 'module-location' option.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-8-SNAPSHOT/jomc-tools" )
    private java.lang.String getModuleLocationOptionLongName()
    {
        final java.lang.String _p = (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager().getProperty( this, "moduleLocationOptionLongName" );
        assert _p != null : "'moduleLocationOptionLongName' property not found.";
        return _p;
    }

    /**
     * Gets the value of the {@code moduleLocationOptionShortName} property.
     * @return Name of the 'module-location' option.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-8-SNAPSHOT/jomc-tools" )
    private java.lang.String getModuleLocationOptionShortName()
    {
        final java.lang.String _p = (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager().getProperty( this, "moduleLocationOptionShortName" );
        assert _p != null : "'moduleLocationOptionShortName' property not found.";
        return _p;
    }

    /**
     * Gets the value of the {@code moduleNameOptionLongName} property.
     * @return Long name of the 'module' option.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-8-SNAPSHOT/jomc-tools" )
    private java.lang.String getModuleNameOptionLongName()
    {
        final java.lang.String _p = (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager().getProperty( this, "moduleNameOptionLongName" );
        assert _p != null : "'moduleNameOptionLongName' property not found.";
        return _p;
    }

    /**
     * Gets the value of the {@code moduleNameOptionShortName} property.
     * @return Name of the 'module' option.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-8-SNAPSHOT/jomc-tools" )
    private java.lang.String getModuleNameOptionShortName()
    {
        final java.lang.String _p = (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager().getProperty( this, "moduleNameOptionShortName" );
        assert _p != null : "'moduleNameOptionShortName' property not found.";
        return _p;
    }

    /**
     * Gets the value of the {@code noClasspathResolutionOptionLongName} property.
     * @return Long name of the 'no-classpath-resolution' option.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-8-SNAPSHOT/jomc-tools" )
    private java.lang.String getNoClasspathResolutionOptionLongName()
    {
        final java.lang.String _p = (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager().getProperty( this, "noClasspathResolutionOptionLongName" );
        assert _p != null : "'noClasspathResolutionOptionLongName' property not found.";
        return _p;
    }

    /**
     * Gets the value of the {@code noClasspathResolutionOptionShortName} property.
     * @return Name of the 'no-classpath-resolution' option.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-8-SNAPSHOT/jomc-tools" )
    private java.lang.String getNoClasspathResolutionOptionShortName()
    {
        final java.lang.String _p = (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager().getProperty( this, "noClasspathResolutionOptionShortName" );
        assert _p != null : "'noClasspathResolutionOptionShortName' property not found.";
        return _p;
    }

    /**
     * Gets the value of the {@code outputEncodingOptionLongName} property.
     * @return Long name of the 'output-encoding' option.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-8-SNAPSHOT/jomc-tools" )
    private java.lang.String getOutputEncodingOptionLongName()
    {
        final java.lang.String _p = (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager().getProperty( this, "outputEncodingOptionLongName" );
        assert _p != null : "'outputEncodingOptionLongName' property not found.";
        return _p;
    }

    /**
     * Gets the value of the {@code outputEncodingOptionShortName} property.
     * @return Name of the 'output-encoding' option.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-8-SNAPSHOT/jomc-tools" )
    private java.lang.String getOutputEncodingOptionShortName()
    {
        final java.lang.String _p = (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager().getProperty( this, "outputEncodingOptionShortName" );
        assert _p != null : "'outputEncodingOptionShortName' property not found.";
        return _p;
    }

    /**
     * Gets the value of the {@code profileOptionLongName} property.
     * @return Long name of the 'profile' option.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-8-SNAPSHOT/jomc-tools" )
    private java.lang.String getProfileOptionLongName()
    {
        final java.lang.String _p = (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager().getProperty( this, "profileOptionLongName" );
        assert _p != null : "'profileOptionLongName' property not found.";
        return _p;
    }

    /**
     * Gets the value of the {@code profileOptionShortName} property.
     * @return Name of the 'profile' option.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-8-SNAPSHOT/jomc-tools" )
    private java.lang.String getProfileOptionShortName()
    {
        final java.lang.String _p = (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager().getProperty( this, "profileOptionShortName" );
        assert _p != null : "'profileOptionShortName' property not found.";
        return _p;
    }

    /**
     * Gets the value of the {@code sourceDirectoryOptionLongName} property.
     * @return Long name of the 'source-dir' option.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-8-SNAPSHOT/jomc-tools" )
    private java.lang.String getSourceDirectoryOptionLongName()
    {
        final java.lang.String _p = (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager().getProperty( this, "sourceDirectoryOptionLongName" );
        assert _p != null : "'sourceDirectoryOptionLongName' property not found.";
        return _p;
    }

    /**
     * Gets the value of the {@code sourceDirectoryOptionShortName} property.
     * @return Name of the 'source-dir' option.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-8-SNAPSHOT/jomc-tools" )
    private java.lang.String getSourceDirectoryOptionShortName()
    {
        final java.lang.String _p = (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager().getProperty( this, "sourceDirectoryOptionShortName" );
        assert _p != null : "'sourceDirectoryOptionShortName' property not found.";
        return _p;
    }

    /**
     * Gets the value of the {@code templateEncodingOptionLongName} property.
     * @return Long name of the 'template-encoding' option.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-8-SNAPSHOT/jomc-tools" )
    private java.lang.String getTemplateEncodingOptionLongName()
    {
        final java.lang.String _p = (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager().getProperty( this, "templateEncodingOptionLongName" );
        assert _p != null : "'templateEncodingOptionLongName' property not found.";
        return _p;
    }

    /**
     * Gets the value of the {@code templateEncodingOptionShortName} property.
     * @return Name of the 'template-encoding' option.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-8-SNAPSHOT/jomc-tools" )
    private java.lang.String getTemplateEncodingOptionShortName()
    {
        final java.lang.String _p = (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager().getProperty( this, "templateEncodingOptionShortName" );
        assert _p != null : "'templateEncodingOptionShortName' property not found.";
        return _p;
    }
    // SECTION-END
    // SECTION-START[Messages]

    /**
     * Gets the text of the {@code applicationTitle} message.
     * <p><b>Templates</b><br/><table>
     * <tr><td valign="top">English:</td><td valign="top"><pre>JOMC Version 1.0-alpha-8-SNAPSHOT Build 2009-11-07T23:17:40+0000</pre></td></tr>
     * </table></p>
     * @param locale The locale of the message to return.
     * @return The text of the {@code applicationTitle} message.
     *
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-8-SNAPSHOT/jomc-tools" )
    private String getApplicationTitleMessage( final java.util.Locale locale )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager().getMessage( this, "applicationTitle", locale,  null );
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
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-8-SNAPSHOT/jomc-tools" )
    private String getCannotProcessMessage( final java.util.Locale locale, final java.lang.String itemInfo, final java.lang.String detailMessage )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager().getMessage( this, "cannotProcess", locale, new Object[] { itemInfo, detailMessage, null } );
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
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-8-SNAPSHOT/jomc-tools" )
    private String getClasspathElementMessage( final java.util.Locale locale, final java.lang.String classpathElement )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager().getMessage( this, "classpathElement", locale, new Object[] { classpathElement, null } );
        assert _m != null : "'classpathElement' message not found.";
        return _m;
    }

    /**
     * Gets the text of the {@code classpathOption} message.
     * <p><b>Templates</b><br/><table>
     * <tr><td valign="top">English:</td><td valign="top"><pre>Classpath elements separated by ''{0}''. If starting with a ''@'' character, a file name of a file holding classpath elements.</pre></td></tr>
     * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Klassenpfad-Elemente mit ''{0}'' getrennt. Wenn mit ''@'' beginnend, Dateiname einer Textdatei mit Klassenpfad-Elementen.</pre></td></tr>
     * </table></p>
     * @param locale The locale of the message to return.
     * @param pathSeparator Format argument.
     * @return The text of the {@code classpathOption} message.
     *
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-8-SNAPSHOT/jomc-tools" )
    private String getClasspathOptionMessage( final java.util.Locale locale, final java.lang.String pathSeparator )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager().getMessage( this, "classpathOption", locale, new Object[] { pathSeparator, null } );
        assert _m != null : "'classpathOption' message not found.";
        return _m;
    }

    /**
     * Gets the text of the {@code classpathOptionArgName} message.
     * <p><b>Templates</b><br/><table>
     * <tr><td valign="top">English:</td><td valign="top"><pre>elements</pre></td></tr>
     * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Elemente</pre></td></tr>
     * </table></p>
     * @param locale The locale of the message to return.
     * @return The text of the {@code classpathOptionArgName} message.
     *
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-8-SNAPSHOT/jomc-tools" )
    private String getClasspathOptionArgNameMessage( final java.util.Locale locale )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager().getMessage( this, "classpathOptionArgName", locale,  null );
        assert _m != null : "'classpathOptionArgName' message not found.";
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
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-8-SNAPSHOT/jomc-tools" )
    private String getDefaultLogLevelInfoMessage( final java.util.Locale locale, final java.lang.String defaultLogLevel )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager().getMessage( this, "defaultLogLevelInfo", locale, new Object[] { defaultLogLevel, null } );
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
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-8-SNAPSHOT/jomc-tools" )
    private String getDocumentFileMessage( final java.util.Locale locale, final java.lang.String documentFile )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager().getMessage( this, "documentFile", locale, new Object[] { documentFile, null } );
        assert _m != null : "'documentFile' message not found.";
        return _m;
    }

    /**
     * Gets the text of the {@code documentsOption} message.
     * <p><b>Templates</b><br/><table>
     * <tr><td valign="top">English:</td><td valign="top"><pre>Document filenames separated by ''{0}''. If starting with a ''@'' character, a file name of a file holding document filenames.</pre></td></tr>
     * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Dokument-Dateinamen mit ''{0}'' getrennt. Wenn mit ''@'' beginnend, Dateiname einer Textdatei mit Dokument-Dateinamen.</pre></td></tr>
     * </table></p>
     * @param locale The locale of the message to return.
     * @param pathSeparator Format argument.
     * @return The text of the {@code documentsOption} message.
     *
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-8-SNAPSHOT/jomc-tools" )
    private String getDocumentsOptionMessage( final java.util.Locale locale, final java.lang.String pathSeparator )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager().getMessage( this, "documentsOption", locale, new Object[] { pathSeparator, null } );
        assert _m != null : "'documentsOption' message not found.";
        return _m;
    }

    /**
     * Gets the text of the {@code documentsOptionArgName} message.
     * <p><b>Templates</b><br/><table>
     * <tr><td valign="top">English:</td><td valign="top"><pre>files</pre></td></tr>
     * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Dateien</pre></td></tr>
     * </table></p>
     * @param locale The locale of the message to return.
     * @return The text of the {@code documentsOptionArgName} message.
     *
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-8-SNAPSHOT/jomc-tools" )
    private String getDocumentsOptionArgNameMessage( final java.util.Locale locale )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager().getMessage( this, "documentsOptionArgName", locale,  null );
        assert _m != null : "'documentsOptionArgName' message not found.";
        return _m;
    }

    /**
     * Gets the text of the {@code inputEncodingOption} message.
     * <p><b>Templates</b><br/><table>
     * <tr><td valign="top">English:</td><td valign="top"><pre>Input encoding.</pre></td></tr>
     * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Eingabekodierung.</pre></td></tr>
     * </table></p>
     * @param locale The locale of the message to return.
     * @return The text of the {@code inputEncodingOption} message.
     *
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-8-SNAPSHOT/jomc-tools" )
    private String getInputEncodingOptionMessage( final java.util.Locale locale )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager().getMessage( this, "inputEncodingOption", locale,  null );
        assert _m != null : "'inputEncodingOption' message not found.";
        return _m;
    }

    /**
     * Gets the text of the {@code inputEncodingOptionArgName} message.
     * <p><b>Templates</b><br/><table>
     * <tr><td valign="top">English:</td><td valign="top"><pre>encoding</pre></td></tr>
     * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Kodierung</pre></td></tr>
     * </table></p>
     * @param locale The locale of the message to return.
     * @return The text of the {@code inputEncodingOptionArgName} message.
     *
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-8-SNAPSHOT/jomc-tools" )
    private String getInputEncodingOptionArgNameMessage( final java.util.Locale locale )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager().getMessage( this, "inputEncodingOptionArgName", locale,  null );
        assert _m != null : "'inputEncodingOptionArgName' message not found.";
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
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-8-SNAPSHOT/jomc-tools" )
    private String getInvalidModelMessage( final java.util.Locale locale )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager().getMessage( this, "invalidModel", locale,  null );
        assert _m != null : "'invalidModel' message not found.";
        return _m;
    }

    /**
     * Gets the text of the {@code longDescription} message.
     * <p><b>Templates</b><br/><table>
     * <tr><td valign="top">English:</td><td valign="top"><pre>Example:
     *   jomc manage-java-sources -sd /tmp/src -df examples/xml/jomc-cli.xml } \
     *                            -mn &quot;JOMC CLI&quot; -v</pre></td></tr>
     * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Beispiel:
     *   jomc manage-java-sources -sd /tmp/src -df examples/xml/jomc-cli.xml \
     *                            -mn &quot;JOMC CLI&quot; -v</pre></td></tr>
     * </table></p>
     * @param locale The locale of the message to return.
     * @return The text of the {@code longDescription} message.
     *
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-8-SNAPSHOT/jomc-tools" )
    private String getLongDescriptionMessage( final java.util.Locale locale )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager().getMessage( this, "longDescription", locale,  null );
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
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-8-SNAPSHOT/jomc-tools" )
    private String getMissingModuleMessage( final java.util.Locale locale, final java.lang.String moduleName )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager().getMessage( this, "missingModule", locale, new Object[] { moduleName, null } );
        assert _m != null : "'missingModule' message not found.";
        return _m;
    }

    /**
     * Gets the text of the {@code moduleLocationOption} message.
     * <p><b>Templates</b><br/><table>
     * <tr><td valign="top">English:</td><td valign="top"><pre>Location of classpath modules.</pre></td></tr>
     * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Ort der Klassenpfad-Module.</pre></td></tr>
     * </table></p>
     * @param locale The locale of the message to return.
     * @return The text of the {@code moduleLocationOption} message.
     *
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-8-SNAPSHOT/jomc-tools" )
    private String getModuleLocationOptionMessage( final java.util.Locale locale )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager().getMessage( this, "moduleLocationOption", locale,  null );
        assert _m != null : "'moduleLocationOption' message not found.";
        return _m;
    }

    /**
     * Gets the text of the {@code moduleLocationOptionArgName} message.
     * <p><b>Templates</b><br/><table>
     * <tr><td valign="top">English:</td><td valign="top"><pre>location</pre></td></tr>
     * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Ort</pre></td></tr>
     * </table></p>
     * @param locale The locale of the message to return.
     * @return The text of the {@code moduleLocationOptionArgName} message.
     *
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-8-SNAPSHOT/jomc-tools" )
    private String getModuleLocationOptionArgNameMessage( final java.util.Locale locale )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager().getMessage( this, "moduleLocationOptionArgName", locale,  null );
        assert _m != null : "'moduleLocationOptionArgName' message not found.";
        return _m;
    }

    /**
     * Gets the text of the {@code moduleNameOption} message.
     * <p><b>Templates</b><br/><table>
     * <tr><td valign="top">English:</td><td valign="top"><pre>Name of the module to process.</pre></td></tr>
     * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Name des zu verarbeitenden Moduls.</pre></td></tr>
     * </table></p>
     * @param locale The locale of the message to return.
     * @return The text of the {@code moduleNameOption} message.
     *
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-8-SNAPSHOT/jomc-tools" )
    private String getModuleNameOptionMessage( final java.util.Locale locale )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager().getMessage( this, "moduleNameOption", locale,  null );
        assert _m != null : "'moduleNameOption' message not found.";
        return _m;
    }

    /**
     * Gets the text of the {@code moduleNameOptionArgName} message.
     * <p><b>Templates</b><br/><table>
     * <tr><td valign="top">English:</td><td valign="top"><pre>name</pre></td></tr>
     * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Name</pre></td></tr>
     * </table></p>
     * @param locale The locale of the message to return.
     * @return The text of the {@code moduleNameOptionArgName} message.
     *
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-8-SNAPSHOT/jomc-tools" )
    private String getModuleNameOptionArgNameMessage( final java.util.Locale locale )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager().getMessage( this, "moduleNameOptionArgName", locale,  null );
        assert _m != null : "'moduleNameOptionArgName' message not found.";
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
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-8-SNAPSHOT/jomc-tools" )
    private String getModulesReportMessage( final java.util.Locale locale )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager().getMessage( this, "modulesReport", locale,  null );
        assert _m != null : "'modulesReport' message not found.";
        return _m;
    }

    /**
     * Gets the text of the {@code noClasspathResolutionOption} message.
     * <p><b>Templates</b><br/><table>
     * <tr><td valign="top">English:</td><td valign="top"><pre>Do not perform classpath resolution.</pre></td></tr>
     * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Keine Klassenpfad-Aufl&ouml;sung durchf&uuml;hren.</pre></td></tr>
     * </table></p>
     * @param locale The locale of the message to return.
     * @return The text of the {@code noClasspathResolutionOption} message.
     *
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-8-SNAPSHOT/jomc-tools" )
    private String getNoClasspathResolutionOptionMessage( final java.util.Locale locale )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager().getMessage( this, "noClasspathResolutionOption", locale,  null );
        assert _m != null : "'noClasspathResolutionOption' message not found.";
        return _m;
    }

    /**
     * Gets the text of the {@code outputEncodingOption} message.
     * <p><b>Templates</b><br/><table>
     * <tr><td valign="top">English:</td><td valign="top"><pre>Output encoding.</pre></td></tr>
     * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Ausgabekodierung.</pre></td></tr>
     * </table></p>
     * @param locale The locale of the message to return.
     * @return The text of the {@code outputEncodingOption} message.
     *
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-8-SNAPSHOT/jomc-tools" )
    private String getOutputEncodingOptionMessage( final java.util.Locale locale )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager().getMessage( this, "outputEncodingOption", locale,  null );
        assert _m != null : "'outputEncodingOption' message not found.";
        return _m;
    }

    /**
     * Gets the text of the {@code outputEncodingOptionArgName} message.
     * <p><b>Templates</b><br/><table>
     * <tr><td valign="top">English:</td><td valign="top"><pre>encoding</pre></td></tr>
     * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Kodierung</pre></td></tr>
     * </table></p>
     * @param locale The locale of the message to return.
     * @return The text of the {@code outputEncodingOptionArgName} message.
     *
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-8-SNAPSHOT/jomc-tools" )
    private String getOutputEncodingOptionArgNameMessage( final java.util.Locale locale )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager().getMessage( this, "outputEncodingOptionArgName", locale,  null );
        assert _m != null : "'outputEncodingOptionArgName' message not found.";
        return _m;
    }

    /**
     * Gets the text of the {@code profileOption} message.
     * <p><b>Templates</b><br/><table>
     * <tr><td valign="top">English:</td><td valign="top"><pre>Templates profile to use.</pre></td></tr>
     * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Zu verwendendes Vorlagen-Profil.</pre></td></tr>
     * </table></p>
     * @param locale The locale of the message to return.
     * @return The text of the {@code profileOption} message.
     *
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-8-SNAPSHOT/jomc-tools" )
    private String getProfileOptionMessage( final java.util.Locale locale )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager().getMessage( this, "profileOption", locale,  null );
        assert _m != null : "'profileOption' message not found.";
        return _m;
    }

    /**
     * Gets the text of the {@code profileOptionArgName} message.
     * <p><b>Templates</b><br/><table>
     * <tr><td valign="top">English:</td><td valign="top"><pre>profile</pre></td></tr>
     * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Profil</pre></td></tr>
     * </table></p>
     * @param locale The locale of the message to return.
     * @return The text of the {@code profileOptionArgName} message.
     *
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-8-SNAPSHOT/jomc-tools" )
    private String getProfileOptionArgNameMessage( final java.util.Locale locale )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager().getMessage( this, "profileOptionArgName", locale,  null );
        assert _m != null : "'profileOptionArgName' message not found.";
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
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-8-SNAPSHOT/jomc-tools" )
    private String getSeparatorMessage( final java.util.Locale locale )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager().getMessage( this, "separator", locale,  null );
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
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-8-SNAPSHOT/jomc-tools" )
    private String getShortDescriptionMessage( final java.util.Locale locale )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager().getMessage( this, "shortDescription", locale,  null );
        assert _m != null : "'shortDescription' message not found.";
        return _m;
    }

    /**
     * Gets the text of the {@code sourceDirectoryOption} message.
     * <p><b>Templates</b><br/><table>
     * <tr><td valign="top">English:</td><td valign="top"><pre>Directory to write source files to.</pre></td></tr>
     * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Verzeichnis in das Quelltext-Dateien geschrieben werden sollen.</pre></td></tr>
     * </table></p>
     * @param locale The locale of the message to return.
     * @return The text of the {@code sourceDirectoryOption} message.
     *
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-8-SNAPSHOT/jomc-tools" )
    private String getSourceDirectoryOptionMessage( final java.util.Locale locale )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager().getMessage( this, "sourceDirectoryOption", locale,  null );
        assert _m != null : "'sourceDirectoryOption' message not found.";
        return _m;
    }

    /**
     * Gets the text of the {@code sourceDirectoryOptionArgName} message.
     * <p><b>Templates</b><br/><table>
     * <tr><td valign="top">English:</td><td valign="top"><pre>directory</pre></td></tr>
     * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Verzeichnis</pre></td></tr>
     * </table></p>
     * @param locale The locale of the message to return.
     * @return The text of the {@code sourceDirectoryOptionArgName} message.
     *
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-8-SNAPSHOT/jomc-tools" )
    private String getSourceDirectoryOptionArgNameMessage( final java.util.Locale locale )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager().getMessage( this, "sourceDirectoryOptionArgName", locale,  null );
        assert _m != null : "'sourceDirectoryOptionArgName' message not found.";
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
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-8-SNAPSHOT/jomc-tools" )
    private String getStartingModuleProcessingMessage( final java.util.Locale locale, final java.lang.String toolName, final java.lang.String moduleName )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager().getMessage( this, "startingModuleProcessing", locale, new Object[] { toolName, moduleName, null } );
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
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-8-SNAPSHOT/jomc-tools" )
    private String getStartingProcessingMessage( final java.util.Locale locale, final java.lang.String toolName )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager().getMessage( this, "startingProcessing", locale, new Object[] { toolName, null } );
        assert _m != null : "'startingProcessing' message not found.";
        return _m;
    }

    /**
     * Gets the text of the {@code templateEncodingOption} message.
     * <p><b>Templates</b><br/><table>
     * <tr><td valign="top">English:</td><td valign="top"><pre>Template encoding.</pre></td></tr>
     * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Vorlagenkodierung.</pre></td></tr>
     * </table></p>
     * @param locale The locale of the message to return.
     * @return The text of the {@code templateEncodingOption} message.
     *
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-8-SNAPSHOT/jomc-tools" )
    private String getTemplateEncodingOptionMessage( final java.util.Locale locale )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager().getMessage( this, "templateEncodingOption", locale,  null );
        assert _m != null : "'templateEncodingOption' message not found.";
        return _m;
    }

    /**
     * Gets the text of the {@code templateEncodingOptionArgName} message.
     * <p><b>Templates</b><br/><table>
     * <tr><td valign="top">English:</td><td valign="top"><pre>encoding</pre></td></tr>
     * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Kodierung</pre></td></tr>
     * </table></p>
     * @param locale The locale of the message to return.
     * @return The text of the {@code templateEncodingOptionArgName} message.
     *
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-8-SNAPSHOT/jomc-tools" )
    private String getTemplateEncodingOptionArgNameMessage( final java.util.Locale locale )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager().getMessage( this, "templateEncodingOptionArgName", locale,  null );
        assert _m != null : "'templateEncodingOptionArgName' message not found.";
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
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-8-SNAPSHOT/jomc-tools" )
    private String getToolFailureMessage( final java.util.Locale locale, final java.lang.String toolName )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager().getMessage( this, "toolFailure", locale, new Object[] { toolName, null } );
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
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-8-SNAPSHOT/jomc-tools" )
    private String getToolSuccessMessage( final java.util.Locale locale, final java.lang.String toolName )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager().getMessage( this, "toolSuccess", locale, new Object[] { toolName, null } );
        assert _m != null : "'toolSuccess' message not found.";
        return _m;
    }
    // SECTION-END
}
