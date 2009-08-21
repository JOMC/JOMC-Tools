// SECTION-START[License Header]
/*
 *   Copyright (c) 2009 The JOMC Project
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
// SECTION-END
package org.jomc.cli.commands;

import java.io.File;
import java.io.PrintStream;
import java.util.logging.Level;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.jomc.model.ModelException;
import org.jomc.model.Module;
import org.jomc.tools.ModuleAssembler;

// SECTION-START[Documentation]
/**
 * Command line interface for the {@code ModuleAssembler} tool.
 * <p><b>Specifications</b><ul>
 * <li>{@code org.jomc.cli.Command} {@code 1.0}</li>
 * </ul></p>
 * <p><b>Properties</b><ul>
 * <li>"{@link #getAbbreviatedCommandName abbreviatedCommandName}"<blockquote>
 * Property of type {@code java.lang.String} with value "am".</blockquote></li>
 * <li>"{@link #getBuildDirectoryOptionLongName buildDirectoryOptionLongName}"<blockquote>
 * Property of type {@code java.lang.String} with value "build-dir".</blockquote></li>
 * <li>"{@link #getBuildDirectoryOptionShortName buildDirectoryOptionShortName}"<blockquote>
 * Property of type {@code java.lang.String} with value "bd".</blockquote></li>
 * <li>"{@link #getClasspathOptionLongName classpathOptionLongName}"<blockquote>
 * Property of type {@code java.lang.String} with value "classpath".</blockquote></li>
 * <li>"{@link #getClasspathOptionShortName classpathOptionShortName}"<blockquote>
 * Property of type {@code java.lang.String} with value "cp".</blockquote></li>
 * <li>"{@link #getCommandName commandName}"<blockquote>
 * Property of type {@code java.lang.String} with value "assemble-modules".</blockquote></li>
 * <li>"{@link #getDebugOptionLongName debugOptionLongName}"<blockquote>
 * Property of type {@code java.lang.String} with value "debug".</blockquote></li>
 * <li>"{@link #getDebugOptionShortName debugOptionShortName}"<blockquote>
 * Property of type {@code java.lang.String} with value "D".</blockquote></li>
 * <li>"{@link #getDocumentLocationOptionLongName documentLocationOptionLongName}"<blockquote>
 * Property of type {@code java.lang.String} with value "document-location".</blockquote></li>
 * <li>"{@link #getDocumentLocationOptionShortName documentLocationOptionShortName}"<blockquote>
 * Property of type {@code java.lang.String} with value "dl".</blockquote></li>
 * <li>"{@link #getDocumentOptionLongName documentOptionLongName}"<blockquote>
 * Property of type {@code java.lang.String} with value "document".</blockquote></li>
 * <li>"{@link #getDocumentOptionShortName documentOptionShortName}"<blockquote>
 * Property of type {@code java.lang.String} with value "d".</blockquote></li>
 * <li>"{@link #getDocumentsOptionLongName documentsOptionLongName}"<blockquote>
 * Property of type {@code java.lang.String} with value "documents".</blockquote></li>
 * <li>"{@link #getDocumentsOptionShortName documentsOptionShortName}"<blockquote>
 * Property of type {@code java.lang.String} with value "df".</blockquote></li>
 * <li>"{@link #getMergeDirectoryOptionLongName mergeDirectoryOptionLongName}"<blockquote>
 * Property of type {@code java.lang.String} with value "merge-directory".</blockquote></li>
 * <li>"{@link #getMergeDirectoryOptionShortName mergeDirectoryOptionShortName}"<blockquote>
 * Property of type {@code java.lang.String} with value "md".</blockquote></li>
 * <li>"{@link #getModuleNameOptionLongName moduleNameOptionLongName}"<blockquote>
 * Property of type {@code java.lang.String} with value "module".</blockquote></li>
 * <li>"{@link #getModuleNameOptionShortName moduleNameOptionShortName}"<blockquote>
 * Property of type {@code java.lang.String} with value "mn".</blockquote></li>
 * <li>"{@link #getModuleVendorOptionLongName moduleVendorOptionLongName}"<blockquote>
 * Property of type {@code java.lang.String} with value "module-vendor".</blockquote></li>
 * <li>"{@link #getModuleVendorOptionShortName moduleVendorOptionShortName}"<blockquote>
 * Property of type {@code java.lang.String} with value "mve".</blockquote></li>
 * <li>"{@link #getModuleVersionOptionLongName moduleVersionOptionLongName}"<blockquote>
 * Property of type {@code java.lang.String} with value "module-version".</blockquote></li>
 * <li>"{@link #getModuleVersionOptionShortName moduleVersionOptionShortName}"<blockquote>
 * Property of type {@code java.lang.String} with value "mv".</blockquote></li>
 * <li>"{@link #getVerboseOptionLongName verboseOptionLongName}"<blockquote>
 * Property of type {@code java.lang.String} with value "verbose".</blockquote></li>
 * <li>"{@link #getVerboseOptionShortName verboseOptionShortName}"<blockquote>
 * Property of type {@code java.lang.String} with value "v".</blockquote></li>
 * </ul></p>
 * <p><b>Dependencies</b><ul>
 * <li>"{@link #getLocale Locale}"<blockquote>
 * Dependency on {@code java.util.Locale} at specification level 1.1 bound to an instance.</blockquote></li>
 * </ul></p>
 * <p><b>Messages</b><ul>
 * <li>"{@link #getApplicationTitleMessage applicationTitle}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>JOMC Version 1.0-alpha-1-SNAPSHOT Build 2009-08-21T08:09:01+0000</pre></td></tr>
 * </table>
 * <li>"{@link #getBuildDirectoryOptionMessage buildDirectoryOption}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>Work directory of the process.</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Arbeitsverzeichnis des Vorgangs.</pre></td></tr>
 * </table>
 * <li>"{@link #getBuildDirectoryOptionArgNameMessage buildDirectoryOptionArgName}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>directory</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Verzeichnis</pre></td></tr>
 * </table>
 * <li>"{@link #getClasspathElementMessage classpathElement}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>Classpath element: ''{0}''</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Klassenpfad-Element: ''{0}''</pre></td></tr>
 * </table>
 * <li>"{@link #getClasspathOptionMessage classpathOption}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>Classpath elements separated by '':''. If starting with a ''@'' character, a file name of a file holding classpath elements.</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Klassenpfad-Elemente mit '':'' getrennt. Wenn mit ''@'' beginnend, Dateiname einer Textdatei mit Klassenpfad-Elementen.</pre></td></tr>
 * </table>
 * <li>"{@link #getClasspathOptionArgNameMessage classpathOptionArgName}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>elements</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Elemente</pre></td></tr>
 * </table>
 * <li>"{@link #getDebugOptionMessage debugOption}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>Enables debug output.</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Aktiviert Diagnose-Ausgaben.</pre></td></tr>
 * </table>
 * <li>"{@link #getDescriptionMessage description}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>Assembles modules.</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Assembliert Module.</pre></td></tr>
 * </table>
 * <li>"{@link #getDocumentFileMessage documentFile}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>Document file: ''{0}''</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Dokument-Datei: ''{0}''</pre></td></tr>
 * </table>
 * <li>"{@link #getDocumentLocationOptionMessage documentLocationOption}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>Location of classpath documents.</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Ort der Klassenpfad-Dokumente.</pre></td></tr>
 * </table>
 * <li>"{@link #getDocumentLocationOptionArgNameMessage documentLocationOptionArgName}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>location</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Ort</pre></td></tr>
 * </table>
 * <li>"{@link #getDocumentOptionMessage documentOption}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>Name of the file to write the assembled module to.</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Name der Datei in die das assemblierte Modul geschrieben werden soll.</pre></td></tr>
 * </table>
 * <li>"{@link #getDocumentOptionArgNameMessage documentOptionArgName}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>file</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Datei</pre></td></tr>
 * </table>
 * <li>"{@link #getDocumentsOptionMessage documentsOption}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>Document filenames separated by '':''. If starting with a ''@'' character, a file name of a file holding document filenames.</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Dokument-Dateinamen mit '':'' getrennt. Wenn mit ''@'' beginnend, Dateiname einer Textdatei mit Dokument-Dateinamen.</pre></td></tr>
 * </table>
 * <li>"{@link #getDocumentsOptionArgNameMessage documentsOptionArgName}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>files</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Dateien</pre></td></tr>
 * </table>
 * <li>"{@link #getMergeDirectoryOptionMessage mergeDirectoryOption}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>Directory holding documents to merge.</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Verzeichnis mit zusammen zu fügenden Dokumenten.</pre></td></tr>
 * </table>
 * <li>"{@link #getMergeDirectoryOptionArgNameMessage mergeDirectoryOptionArgName}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>directory</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Verzeichnis</pre></td></tr>
 * </table>
 * <li>"{@link #getModuleNameOptionMessage moduleNameOption}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>Name of the module to process.</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Name des zu verarbeitenden Moduls.</pre></td></tr>
 * </table>
 * <li>"{@link #getModuleNameOptionArgNameMessage moduleNameOptionArgName}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>name</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Name</pre></td></tr>
 * </table>
 * <li>"{@link #getModuleVendorOptionMessage moduleVendorOption}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>Vendor of the merged module.</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Hersteller des Ergebnis-Moduls.</pre></td></tr>
 * </table>
 * <li>"{@link #getModuleVendorOptionArgNameMessage moduleVendorOptionArgName}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>vendor</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Hersteller</pre></td></tr>
 * </table>
 * <li>"{@link #getModuleVersionOptionMessage moduleVersionOption}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>Version of the merged module.</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Version des Ergebnis-Moduls.</pre></td></tr>
 * </table>
 * <li>"{@link #getModuleVersionOptionArgNameMessage moduleVersionOptionArgName}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>version</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Version</pre></td></tr>
 * </table>
 * <li>"{@link #getSeparatorMessage separator}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>--------------------------------------------------------------------------------</pre></td></tr>
 * </table>
 * <li>"{@link #getVerboseOptionMessage verboseOption}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>Enables verbose output.</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Aktiviert ausführliche Ausgaben.</pre></td></tr>
 * </table>
 * <li>"{@link #getWritingMessage writing}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>Writing ''{0}''.</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Schreibt ''{0}''.</pre></td></tr>
 * </table>
 * </ul></p>
 *
 * @author <a href="mailto:schulte2005@users.sourceforge.net">Christian Schulte</a> 1.0
 * @version $Id$
 */
// SECTION-END
// SECTION-START[Annotations]
@javax.annotation.Generated
(
    value = "org.jomc.tools.JavaSources",
    comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-1-SNAPSHOT/jomc-tools"
)
// SECTION-END
public class AssembleModulesCommand
    extends AbstractJomcCommand
    implements
    org.jomc.cli.Command
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
            this.options.addOption( this.getMergeDirectoryOption() );
            this.options.addOption( this.getDocumentOption() );
            this.options.addOption( this.getModuleVendorOption() );
            this.options.addOption( this.getModuleVersionOption() );
        }

        return this.options;
    }

    public int execute( final PrintStream printStream, final CommandLine commandLine )
    {
        int status = super.execute( commandLine, printStream );

        final boolean verbose = commandLine.hasOption( this.getVerboseOption().getOpt() );
        final boolean debug = commandLine.hasOption( this.getDebugOption().getOpt() );

        try
        {
            final ModuleAssembler tool = new ModuleAssembler();
            this.configureTool( tool, commandLine, printStream, false );

            File mergeDirectory = null;
            if ( commandLine.hasOption( this.getMergeDirectoryOption().getOpt() ) )
            {
                mergeDirectory = new File( commandLine.getOptionValue( this.getMergeDirectoryOption().getOpt() ) );
            }

            String moduleVersion = null;
            if ( commandLine.hasOption( this.getModuleVersionOption().getOpt() ) )
            {
                moduleVersion = commandLine.getOptionValue( this.getModuleVersionOption().getOpt() );
            }

            String moduleVendor = null;
            if ( commandLine.hasOption( this.getModuleVendorOption().getOpt() ) )
            {
                moduleVendor = commandLine.getOptionValue( this.getModuleVendorOption().getOpt() );
            }

            final Module mergedModule =
                tool.mergeModules( commandLine.getOptionValue( this.getModuleNameOption().getOpt() ),
                                   moduleVersion, moduleVendor, mergeDirectory, null );

            final File moduleFile = new File( commandLine.getOptionValue( this.getDocumentOption().getOpt() ) );

            tool.getModelManager().getMarshaller( true, true ).marshal(
                tool.getModelManager().getObjectFactory().createModule( mergedModule ), moduleFile );

            this.log( Level.INFO, this.getWritingMessage( this.getLocale(), moduleFile.getAbsolutePath() ), null,
                      printStream, verbose, debug );

        }
        catch ( ModelException e )
        {
            for ( ModelException.Detail d : e.getDetails() )
            {
                this.log( d.getLevel(), d.getMessage(), null, printStream, verbose, debug );
            }

            this.log( Level.SEVERE, e.getMessage(), e, printStream, verbose, debug );
            status = STATUS_FAILURE;
        }
        catch ( Throwable t )
        {
            this.log( Level.SEVERE, t.getMessage(), t, printStream, verbose, debug );
            status = STATUS_FAILURE;
        }

        this.log( Level.INFO, this.getSeparatorMessage( this.getLocale() ), null, printStream, verbose, debug );
        return status;
    }

    // SECTION-END
    // SECTION-START[AssembleModulesCommand]
    private Option mergeDirectoryOption;

    private Option documentOption;

    private Option moduleVersionOption;

    private Option moduleVendorOption;

    public Option getMergeDirectoryOption()
    {
        if ( this.mergeDirectoryOption == null )
        {
            this.mergeDirectoryOption = new Option( this.getMergeDirectoryOptionShortName(),
                                                    this.getMergeDirectoryOptionLongName(), true,
                                                    this.getMergeDirectoryOptionMessage( this.getLocale() ) );

            this.mergeDirectoryOption.setArgName( this.getMergeDirectoryOptionArgNameMessage( this.getLocale() ) );
        }

        return this.mergeDirectoryOption;
    }

    public Option getDocumentOption()
    {
        if ( this.documentOption == null )
        {
            this.documentOption = new Option( this.getDocumentOptionShortName(),
                                              this.getDocumentOptionLongName(), true,
                                              this.getDocumentOptionMessage( this.getLocale() ) );

            this.documentOption.setRequired( true );
            this.documentOption.setArgName( this.getDocumentOptionArgNameMessage( this.getLocale() ) );
        }

        return this.documentOption;
    }

    public Option getModuleVersionOption()
    {
        if ( this.moduleVersionOption == null )
        {
            this.moduleVersionOption = new Option( this.getModuleVersionOptionShortName(),
                                                   this.getModuleVersionOptionLongName(), true,
                                                   this.getModuleVersionOptionMessage( this.getLocale() ) );

            this.moduleVersionOption.setArgName( this.getModuleVersionOptionArgNameMessage( this.getLocale() ) );
        }

        return this.moduleVersionOption;
    }

    public Option getModuleVendorOption()
    {
        if ( this.moduleVendorOption == null )
        {
            this.moduleVendorOption = new Option( this.getModuleVendorOptionShortName(),
                                                  this.getModuleVendorOptionLongName(), true,
                                                  this.getModuleVendorOptionMessage( this.getLocale() ) );

            this.moduleVendorOption.setArgName( this.getModuleVendorOptionArgNameMessage( this.getLocale() ) );
        }

        return this.moduleVendorOption;
    }

    // SECTION-END
    // SECTION-START[Constructors]

    /** Creates a new {@code AssembleModulesCommand} instance. */
    @javax.annotation.Generated
    (
        value = "org.jomc.tools.JavaSources",
        comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-1-SNAPSHOT/jomc-tools"
    )
    public AssembleModulesCommand()
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
     * @return The {@code Locale} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @javax.annotation.Generated
    (
        value = "org.jomc.tools.JavaSources",
        comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-1-SNAPSHOT/jomc-tools"
    )
    private java.util.Locale getLocale() throws org.jomc.ObjectManagementException
    {
        return (java.util.Locale) org.jomc.ObjectManagerFactory.getObjectManager().getDependency( this, "Locale" );
    }
    // SECTION-END
    // SECTION-START[Properties]

    /**
     * Gets the value of the {@code abbreviatedCommandName} property.
     * @return Abbreviated name of the command.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated
    (
        value = "org.jomc.tools.JavaSources",
        comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-1-SNAPSHOT/jomc-tools"
    )
    private java.lang.String getAbbreviatedCommandName() throws org.jomc.ObjectManagementException
    {
        return (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager().getProperty( this, "abbreviatedCommandName" );
    }

    /**
     * Gets the value of the {@code buildDirectoryOptionLongName} property.
     * @return Long name of the 'build-dir' option.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated
    (
        value = "org.jomc.tools.JavaSources",
        comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-1-SNAPSHOT/jomc-tools"
    )
    private java.lang.String getBuildDirectoryOptionLongName() throws org.jomc.ObjectManagementException
    {
        return (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager().getProperty( this, "buildDirectoryOptionLongName" );
    }

    /**
     * Gets the value of the {@code buildDirectoryOptionShortName} property.
     * @return Name of the 'build-dir' option.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated
    (
        value = "org.jomc.tools.JavaSources",
        comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-1-SNAPSHOT/jomc-tools"
    )
    private java.lang.String getBuildDirectoryOptionShortName() throws org.jomc.ObjectManagementException
    {
        return (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager().getProperty( this, "buildDirectoryOptionShortName" );
    }

    /**
     * Gets the value of the {@code classpathOptionLongName} property.
     * @return Long name of the 'classpath' option.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated
    (
        value = "org.jomc.tools.JavaSources",
        comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-1-SNAPSHOT/jomc-tools"
    )
    private java.lang.String getClasspathOptionLongName() throws org.jomc.ObjectManagementException
    {
        return (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager().getProperty( this, "classpathOptionLongName" );
    }

    /**
     * Gets the value of the {@code classpathOptionShortName} property.
     * @return Name of the 'classpath' option.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated
    (
        value = "org.jomc.tools.JavaSources",
        comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-1-SNAPSHOT/jomc-tools"
    )
    private java.lang.String getClasspathOptionShortName() throws org.jomc.ObjectManagementException
    {
        return (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager().getProperty( this, "classpathOptionShortName" );
    }

    /**
     * Gets the value of the {@code commandName} property.
     * @return Name of the command.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated
    (
        value = "org.jomc.tools.JavaSources",
        comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-1-SNAPSHOT/jomc-tools"
    )
    private java.lang.String getCommandName() throws org.jomc.ObjectManagementException
    {
        return (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager().getProperty( this, "commandName" );
    }

    /**
     * Gets the value of the {@code debugOptionLongName} property.
     * @return Long name of the 'debug' option.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated
    (
        value = "org.jomc.tools.JavaSources",
        comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-1-SNAPSHOT/jomc-tools"
    )
    private java.lang.String getDebugOptionLongName() throws org.jomc.ObjectManagementException
    {
        return (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager().getProperty( this, "debugOptionLongName" );
    }

    /**
     * Gets the value of the {@code debugOptionShortName} property.
     * @return Name of the 'debug' option.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated
    (
        value = "org.jomc.tools.JavaSources",
        comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-1-SNAPSHOT/jomc-tools"
    )
    private java.lang.String getDebugOptionShortName() throws org.jomc.ObjectManagementException
    {
        return (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager().getProperty( this, "debugOptionShortName" );
    }

    /**
     * Gets the value of the {@code documentLocationOptionLongName} property.
     * @return Long name of the 'document-location' option.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated
    (
        value = "org.jomc.tools.JavaSources",
        comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-1-SNAPSHOT/jomc-tools"
    )
    private java.lang.String getDocumentLocationOptionLongName() throws org.jomc.ObjectManagementException
    {
        return (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager().getProperty( this, "documentLocationOptionLongName" );
    }

    /**
     * Gets the value of the {@code documentLocationOptionShortName} property.
     * @return Name of the 'document-location' option.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated
    (
        value = "org.jomc.tools.JavaSources",
        comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-1-SNAPSHOT/jomc-tools"
    )
    private java.lang.String getDocumentLocationOptionShortName() throws org.jomc.ObjectManagementException
    {
        return (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager().getProperty( this, "documentLocationOptionShortName" );
    }

    /**
     * Gets the value of the {@code documentOptionLongName} property.
     * @return Long name of the 'document' option.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated
    (
        value = "org.jomc.tools.JavaSources",
        comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-1-SNAPSHOT/jomc-tools"
    )
    private java.lang.String getDocumentOptionLongName() throws org.jomc.ObjectManagementException
    {
        return (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager().getProperty( this, "documentOptionLongName" );
    }

    /**
     * Gets the value of the {@code documentOptionShortName} property.
     * @return Name of the 'document' option.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated
    (
        value = "org.jomc.tools.JavaSources",
        comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-1-SNAPSHOT/jomc-tools"
    )
    private java.lang.String getDocumentOptionShortName() throws org.jomc.ObjectManagementException
    {
        return (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager().getProperty( this, "documentOptionShortName" );
    }

    /**
     * Gets the value of the {@code documentsOptionLongName} property.
     * @return Long name of the 'documents' option.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated
    (
        value = "org.jomc.tools.JavaSources",
        comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-1-SNAPSHOT/jomc-tools"
    )
    private java.lang.String getDocumentsOptionLongName() throws org.jomc.ObjectManagementException
    {
        return (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager().getProperty( this, "documentsOptionLongName" );
    }

    /**
     * Gets the value of the {@code documentsOptionShortName} property.
     * @return Name of the 'documents' option.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated
    (
        value = "org.jomc.tools.JavaSources",
        comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-1-SNAPSHOT/jomc-tools"
    )
    private java.lang.String getDocumentsOptionShortName() throws org.jomc.ObjectManagementException
    {
        return (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager().getProperty( this, "documentsOptionShortName" );
    }

    /**
     * Gets the value of the {@code mergeDirectoryOptionLongName} property.
     * @return Long name of the 'merge-directory' option.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated
    (
        value = "org.jomc.tools.JavaSources",
        comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-1-SNAPSHOT/jomc-tools"
    )
    private java.lang.String getMergeDirectoryOptionLongName() throws org.jomc.ObjectManagementException
    {
        return (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager().getProperty( this, "mergeDirectoryOptionLongName" );
    }

    /**
     * Gets the value of the {@code mergeDirectoryOptionShortName} property.
     * @return Name of the 'merge-directory' option.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated
    (
        value = "org.jomc.tools.JavaSources",
        comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-1-SNAPSHOT/jomc-tools"
    )
    private java.lang.String getMergeDirectoryOptionShortName() throws org.jomc.ObjectManagementException
    {
        return (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager().getProperty( this, "mergeDirectoryOptionShortName" );
    }

    /**
     * Gets the value of the {@code moduleNameOptionLongName} property.
     * @return Long name of the 'module' option.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated
    (
        value = "org.jomc.tools.JavaSources",
        comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-1-SNAPSHOT/jomc-tools"
    )
    private java.lang.String getModuleNameOptionLongName() throws org.jomc.ObjectManagementException
    {
        return (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager().getProperty( this, "moduleNameOptionLongName" );
    }

    /**
     * Gets the value of the {@code moduleNameOptionShortName} property.
     * @return Name of the 'module' option.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated
    (
        value = "org.jomc.tools.JavaSources",
        comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-1-SNAPSHOT/jomc-tools"
    )
    private java.lang.String getModuleNameOptionShortName() throws org.jomc.ObjectManagementException
    {
        return (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager().getProperty( this, "moduleNameOptionShortName" );
    }

    /**
     * Gets the value of the {@code moduleVendorOptionLongName} property.
     * @return Long name of the 'module-vendor' option.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated
    (
        value = "org.jomc.tools.JavaSources",
        comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-1-SNAPSHOT/jomc-tools"
    )
    private java.lang.String getModuleVendorOptionLongName() throws org.jomc.ObjectManagementException
    {
        return (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager().getProperty( this, "moduleVendorOptionLongName" );
    }

    /**
     * Gets the value of the {@code moduleVendorOptionShortName} property.
     * @return Name of the 'module-vendor' option.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated
    (
        value = "org.jomc.tools.JavaSources",
        comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-1-SNAPSHOT/jomc-tools"
    )
    private java.lang.String getModuleVendorOptionShortName() throws org.jomc.ObjectManagementException
    {
        return (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager().getProperty( this, "moduleVendorOptionShortName" );
    }

    /**
     * Gets the value of the {@code moduleVersionOptionLongName} property.
     * @return Long name of the 'module-version' option.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated
    (
        value = "org.jomc.tools.JavaSources",
        comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-1-SNAPSHOT/jomc-tools"
    )
    private java.lang.String getModuleVersionOptionLongName() throws org.jomc.ObjectManagementException
    {
        return (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager().getProperty( this, "moduleVersionOptionLongName" );
    }

    /**
     * Gets the value of the {@code moduleVersionOptionShortName} property.
     * @return Name of the 'module-version' option.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated
    (
        value = "org.jomc.tools.JavaSources",
        comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-1-SNAPSHOT/jomc-tools"
    )
    private java.lang.String getModuleVersionOptionShortName() throws org.jomc.ObjectManagementException
    {
        return (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager().getProperty( this, "moduleVersionOptionShortName" );
    }

    /**
     * Gets the value of the {@code verboseOptionLongName} property.
     * @return Long name of the 'verbose' option.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated
    (
        value = "org.jomc.tools.JavaSources",
        comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-1-SNAPSHOT/jomc-tools"
    )
    private java.lang.String getVerboseOptionLongName() throws org.jomc.ObjectManagementException
    {
        return (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager().getProperty( this, "verboseOptionLongName" );
    }

    /**
     * Gets the value of the {@code verboseOptionShortName} property.
     * @return Name of the 'verbose' option.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated
    (
        value = "org.jomc.tools.JavaSources",
        comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-1-SNAPSHOT/jomc-tools"
    )
    private java.lang.String getVerboseOptionShortName() throws org.jomc.ObjectManagementException
    {
        return (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager().getProperty( this, "verboseOptionShortName" );
    }
    // SECTION-END
    // SECTION-START[Messages]

    /**
     * Gets the text of the {@code applicationTitle} message.
     * <p><b>Templates</b><br/><table>
     * <tr><td valign="top">English:</td><td valign="top"><pre>JOMC Version 1.0-alpha-1-SNAPSHOT Build 2009-08-21T08:09:01+0000</pre></td></tr>
     * </table></p>
     * @param locale The locale of the message to return.
     * @return The text of the {@code applicationTitle} message.
     *
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated
    (
        value = "org.jomc.tools.JavaSources",
        comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-1-SNAPSHOT/jomc-tools"
    )
    private String getApplicationTitleMessage( final java.util.Locale locale ) throws org.jomc.ObjectManagementException
    {
        return org.jomc.ObjectManagerFactory.getObjectManager().getMessage( this, "applicationTitle", locale,  null );
    }

    /**
     * Gets the text of the {@code buildDirectoryOption} message.
     * <p><b>Templates</b><br/><table>
     * <tr><td valign="top">English:</td><td valign="top"><pre>Work directory of the process.</pre></td></tr>
     * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Arbeitsverzeichnis des Vorgangs.</pre></td></tr>
     * </table></p>
     * @param locale The locale of the message to return.
     * @return The text of the {@code buildDirectoryOption} message.
     *
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated
    (
        value = "org.jomc.tools.JavaSources",
        comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-1-SNAPSHOT/jomc-tools"
    )
    private String getBuildDirectoryOptionMessage( final java.util.Locale locale ) throws org.jomc.ObjectManagementException
    {
        return org.jomc.ObjectManagerFactory.getObjectManager().getMessage( this, "buildDirectoryOption", locale,  null );
    }

    /**
     * Gets the text of the {@code buildDirectoryOptionArgName} message.
     * <p><b>Templates</b><br/><table>
     * <tr><td valign="top">English:</td><td valign="top"><pre>directory</pre></td></tr>
     * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Verzeichnis</pre></td></tr>
     * </table></p>
     * @param locale The locale of the message to return.
     * @return The text of the {@code buildDirectoryOptionArgName} message.
     *
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated
    (
        value = "org.jomc.tools.JavaSources",
        comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-1-SNAPSHOT/jomc-tools"
    )
    private String getBuildDirectoryOptionArgNameMessage( final java.util.Locale locale ) throws org.jomc.ObjectManagementException
    {
        return org.jomc.ObjectManagerFactory.getObjectManager().getMessage( this, "buildDirectoryOptionArgName", locale,  null );
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
    @javax.annotation.Generated
    (
        value = "org.jomc.tools.JavaSources",
        comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-1-SNAPSHOT/jomc-tools"
    )
    private String getClasspathElementMessage( final java.util.Locale locale, final java.lang.String classpathElement ) throws org.jomc.ObjectManagementException
    {
        return org.jomc.ObjectManagerFactory.getObjectManager().getMessage( this, "classpathElement", locale, new Object[] { classpathElement, null } );
    }

    /**
     * Gets the text of the {@code classpathOption} message.
     * <p><b>Templates</b><br/><table>
     * <tr><td valign="top">English:</td><td valign="top"><pre>Classpath elements separated by '':''. If starting with a ''@'' character, a file name of a file holding classpath elements.</pre></td></tr>
     * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Klassenpfad-Elemente mit '':'' getrennt. Wenn mit ''@'' beginnend, Dateiname einer Textdatei mit Klassenpfad-Elementen.</pre></td></tr>
     * </table></p>
     * @param locale The locale of the message to return.
     * @return The text of the {@code classpathOption} message.
     *
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated
    (
        value = "org.jomc.tools.JavaSources",
        comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-1-SNAPSHOT/jomc-tools"
    )
    private String getClasspathOptionMessage( final java.util.Locale locale ) throws org.jomc.ObjectManagementException
    {
        return org.jomc.ObjectManagerFactory.getObjectManager().getMessage( this, "classpathOption", locale,  null );
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
    @javax.annotation.Generated
    (
        value = "org.jomc.tools.JavaSources",
        comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-1-SNAPSHOT/jomc-tools"
    )
    private String getClasspathOptionArgNameMessage( final java.util.Locale locale ) throws org.jomc.ObjectManagementException
    {
        return org.jomc.ObjectManagerFactory.getObjectManager().getMessage( this, "classpathOptionArgName", locale,  null );
    }

    /**
     * Gets the text of the {@code debugOption} message.
     * <p><b>Templates</b><br/><table>
     * <tr><td valign="top">English:</td><td valign="top"><pre>Enables debug output.</pre></td></tr>
     * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Aktiviert Diagnose-Ausgaben.</pre></td></tr>
     * </table></p>
     * @param locale The locale of the message to return.
     * @return The text of the {@code debugOption} message.
     *
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated
    (
        value = "org.jomc.tools.JavaSources",
        comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-1-SNAPSHOT/jomc-tools"
    )
    private String getDebugOptionMessage( final java.util.Locale locale ) throws org.jomc.ObjectManagementException
    {
        return org.jomc.ObjectManagerFactory.getObjectManager().getMessage( this, "debugOption", locale,  null );
    }

    /**
     * Gets the text of the {@code description} message.
     * <p><b>Templates</b><br/><table>
     * <tr><td valign="top">English:</td><td valign="top"><pre>Assembles modules.</pre></td></tr>
     * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Assembliert Module.</pre></td></tr>
     * </table></p>
     * @param locale The locale of the message to return.
     * @return The text of the {@code description} message.
     *
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated
    (
        value = "org.jomc.tools.JavaSources",
        comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-1-SNAPSHOT/jomc-tools"
    )
    private String getDescriptionMessage( final java.util.Locale locale ) throws org.jomc.ObjectManagementException
    {
        return org.jomc.ObjectManagerFactory.getObjectManager().getMessage( this, "description", locale,  null );
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
    @javax.annotation.Generated
    (
        value = "org.jomc.tools.JavaSources",
        comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-1-SNAPSHOT/jomc-tools"
    )
    private String getDocumentFileMessage( final java.util.Locale locale, final java.lang.String documentFile ) throws org.jomc.ObjectManagementException
    {
        return org.jomc.ObjectManagerFactory.getObjectManager().getMessage( this, "documentFile", locale, new Object[] { documentFile, null } );
    }

    /**
     * Gets the text of the {@code documentLocationOption} message.
     * <p><b>Templates</b><br/><table>
     * <tr><td valign="top">English:</td><td valign="top"><pre>Location of classpath documents.</pre></td></tr>
     * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Ort der Klassenpfad-Dokumente.</pre></td></tr>
     * </table></p>
     * @param locale The locale of the message to return.
     * @return The text of the {@code documentLocationOption} message.
     *
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated
    (
        value = "org.jomc.tools.JavaSources",
        comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-1-SNAPSHOT/jomc-tools"
    )
    private String getDocumentLocationOptionMessage( final java.util.Locale locale ) throws org.jomc.ObjectManagementException
    {
        return org.jomc.ObjectManagerFactory.getObjectManager().getMessage( this, "documentLocationOption", locale,  null );
    }

    /**
     * Gets the text of the {@code documentLocationOptionArgName} message.
     * <p><b>Templates</b><br/><table>
     * <tr><td valign="top">English:</td><td valign="top"><pre>location</pre></td></tr>
     * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Ort</pre></td></tr>
     * </table></p>
     * @param locale The locale of the message to return.
     * @return The text of the {@code documentLocationOptionArgName} message.
     *
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated
    (
        value = "org.jomc.tools.JavaSources",
        comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-1-SNAPSHOT/jomc-tools"
    )
    private String getDocumentLocationOptionArgNameMessage( final java.util.Locale locale ) throws org.jomc.ObjectManagementException
    {
        return org.jomc.ObjectManagerFactory.getObjectManager().getMessage( this, "documentLocationOptionArgName", locale,  null );
    }

    /**
     * Gets the text of the {@code documentOption} message.
     * <p><b>Templates</b><br/><table>
     * <tr><td valign="top">English:</td><td valign="top"><pre>Name of the file to write the assembled module to.</pre></td></tr>
     * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Name der Datei in die das assemblierte Modul geschrieben werden soll.</pre></td></tr>
     * </table></p>
     * @param locale The locale of the message to return.
     * @return The text of the {@code documentOption} message.
     *
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated
    (
        value = "org.jomc.tools.JavaSources",
        comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-1-SNAPSHOT/jomc-tools"
    )
    private String getDocumentOptionMessage( final java.util.Locale locale ) throws org.jomc.ObjectManagementException
    {
        return org.jomc.ObjectManagerFactory.getObjectManager().getMessage( this, "documentOption", locale,  null );
    }

    /**
     * Gets the text of the {@code documentOptionArgName} message.
     * <p><b>Templates</b><br/><table>
     * <tr><td valign="top">English:</td><td valign="top"><pre>file</pre></td></tr>
     * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Datei</pre></td></tr>
     * </table></p>
     * @param locale The locale of the message to return.
     * @return The text of the {@code documentOptionArgName} message.
     *
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated
    (
        value = "org.jomc.tools.JavaSources",
        comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-1-SNAPSHOT/jomc-tools"
    )
    private String getDocumentOptionArgNameMessage( final java.util.Locale locale ) throws org.jomc.ObjectManagementException
    {
        return org.jomc.ObjectManagerFactory.getObjectManager().getMessage( this, "documentOptionArgName", locale,  null );
    }

    /**
     * Gets the text of the {@code documentsOption} message.
     * <p><b>Templates</b><br/><table>
     * <tr><td valign="top">English:</td><td valign="top"><pre>Document filenames separated by '':''. If starting with a ''@'' character, a file name of a file holding document filenames.</pre></td></tr>
     * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Dokument-Dateinamen mit '':'' getrennt. Wenn mit ''@'' beginnend, Dateiname einer Textdatei mit Dokument-Dateinamen.</pre></td></tr>
     * </table></p>
     * @param locale The locale of the message to return.
     * @return The text of the {@code documentsOption} message.
     *
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated
    (
        value = "org.jomc.tools.JavaSources",
        comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-1-SNAPSHOT/jomc-tools"
    )
    private String getDocumentsOptionMessage( final java.util.Locale locale ) throws org.jomc.ObjectManagementException
    {
        return org.jomc.ObjectManagerFactory.getObjectManager().getMessage( this, "documentsOption", locale,  null );
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
    @javax.annotation.Generated
    (
        value = "org.jomc.tools.JavaSources",
        comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-1-SNAPSHOT/jomc-tools"
    )
    private String getDocumentsOptionArgNameMessage( final java.util.Locale locale ) throws org.jomc.ObjectManagementException
    {
        return org.jomc.ObjectManagerFactory.getObjectManager().getMessage( this, "documentsOptionArgName", locale,  null );
    }

    /**
     * Gets the text of the {@code mergeDirectoryOption} message.
     * <p><b>Templates</b><br/><table>
     * <tr><td valign="top">English:</td><td valign="top"><pre>Directory holding documents to merge.</pre></td></tr>
     * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Verzeichnis mit zusammen zu fügenden Dokumenten.</pre></td></tr>
     * </table></p>
     * @param locale The locale of the message to return.
     * @return The text of the {@code mergeDirectoryOption} message.
     *
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated
    (
        value = "org.jomc.tools.JavaSources",
        comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-1-SNAPSHOT/jomc-tools"
    )
    private String getMergeDirectoryOptionMessage( final java.util.Locale locale ) throws org.jomc.ObjectManagementException
    {
        return org.jomc.ObjectManagerFactory.getObjectManager().getMessage( this, "mergeDirectoryOption", locale,  null );
    }

    /**
     * Gets the text of the {@code mergeDirectoryOptionArgName} message.
     * <p><b>Templates</b><br/><table>
     * <tr><td valign="top">English:</td><td valign="top"><pre>directory</pre></td></tr>
     * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Verzeichnis</pre></td></tr>
     * </table></p>
     * @param locale The locale of the message to return.
     * @return The text of the {@code mergeDirectoryOptionArgName} message.
     *
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated
    (
        value = "org.jomc.tools.JavaSources",
        comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-1-SNAPSHOT/jomc-tools"
    )
    private String getMergeDirectoryOptionArgNameMessage( final java.util.Locale locale ) throws org.jomc.ObjectManagementException
    {
        return org.jomc.ObjectManagerFactory.getObjectManager().getMessage( this, "mergeDirectoryOptionArgName", locale,  null );
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
    @javax.annotation.Generated
    (
        value = "org.jomc.tools.JavaSources",
        comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-1-SNAPSHOT/jomc-tools"
    )
    private String getModuleNameOptionMessage( final java.util.Locale locale ) throws org.jomc.ObjectManagementException
    {
        return org.jomc.ObjectManagerFactory.getObjectManager().getMessage( this, "moduleNameOption", locale,  null );
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
    @javax.annotation.Generated
    (
        value = "org.jomc.tools.JavaSources",
        comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-1-SNAPSHOT/jomc-tools"
    )
    private String getModuleNameOptionArgNameMessage( final java.util.Locale locale ) throws org.jomc.ObjectManagementException
    {
        return org.jomc.ObjectManagerFactory.getObjectManager().getMessage( this, "moduleNameOptionArgName", locale,  null );
    }

    /**
     * Gets the text of the {@code moduleVendorOption} message.
     * <p><b>Templates</b><br/><table>
     * <tr><td valign="top">English:</td><td valign="top"><pre>Vendor of the merged module.</pre></td></tr>
     * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Hersteller des Ergebnis-Moduls.</pre></td></tr>
     * </table></p>
     * @param locale The locale of the message to return.
     * @return The text of the {@code moduleVendorOption} message.
     *
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated
    (
        value = "org.jomc.tools.JavaSources",
        comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-1-SNAPSHOT/jomc-tools"
    )
    private String getModuleVendorOptionMessage( final java.util.Locale locale ) throws org.jomc.ObjectManagementException
    {
        return org.jomc.ObjectManagerFactory.getObjectManager().getMessage( this, "moduleVendorOption", locale,  null );
    }

    /**
     * Gets the text of the {@code moduleVendorOptionArgName} message.
     * <p><b>Templates</b><br/><table>
     * <tr><td valign="top">English:</td><td valign="top"><pre>vendor</pre></td></tr>
     * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Hersteller</pre></td></tr>
     * </table></p>
     * @param locale The locale of the message to return.
     * @return The text of the {@code moduleVendorOptionArgName} message.
     *
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated
    (
        value = "org.jomc.tools.JavaSources",
        comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-1-SNAPSHOT/jomc-tools"
    )
    private String getModuleVendorOptionArgNameMessage( final java.util.Locale locale ) throws org.jomc.ObjectManagementException
    {
        return org.jomc.ObjectManagerFactory.getObjectManager().getMessage( this, "moduleVendorOptionArgName", locale,  null );
    }

    /**
     * Gets the text of the {@code moduleVersionOption} message.
     * <p><b>Templates</b><br/><table>
     * <tr><td valign="top">English:</td><td valign="top"><pre>Version of the merged module.</pre></td></tr>
     * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Version des Ergebnis-Moduls.</pre></td></tr>
     * </table></p>
     * @param locale The locale of the message to return.
     * @return The text of the {@code moduleVersionOption} message.
     *
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated
    (
        value = "org.jomc.tools.JavaSources",
        comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-1-SNAPSHOT/jomc-tools"
    )
    private String getModuleVersionOptionMessage( final java.util.Locale locale ) throws org.jomc.ObjectManagementException
    {
        return org.jomc.ObjectManagerFactory.getObjectManager().getMessage( this, "moduleVersionOption", locale,  null );
    }

    /**
     * Gets the text of the {@code moduleVersionOptionArgName} message.
     * <p><b>Templates</b><br/><table>
     * <tr><td valign="top">English:</td><td valign="top"><pre>version</pre></td></tr>
     * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Version</pre></td></tr>
     * </table></p>
     * @param locale The locale of the message to return.
     * @return The text of the {@code moduleVersionOptionArgName} message.
     *
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated
    (
        value = "org.jomc.tools.JavaSources",
        comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-1-SNAPSHOT/jomc-tools"
    )
    private String getModuleVersionOptionArgNameMessage( final java.util.Locale locale ) throws org.jomc.ObjectManagementException
    {
        return org.jomc.ObjectManagerFactory.getObjectManager().getMessage( this, "moduleVersionOptionArgName", locale,  null );
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
    @javax.annotation.Generated
    (
        value = "org.jomc.tools.JavaSources",
        comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-1-SNAPSHOT/jomc-tools"
    )
    private String getSeparatorMessage( final java.util.Locale locale ) throws org.jomc.ObjectManagementException
    {
        return org.jomc.ObjectManagerFactory.getObjectManager().getMessage( this, "separator", locale,  null );
    }

    /**
     * Gets the text of the {@code verboseOption} message.
     * <p><b>Templates</b><br/><table>
     * <tr><td valign="top">English:</td><td valign="top"><pre>Enables verbose output.</pre></td></tr>
     * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Aktiviert ausführliche Ausgaben.</pre></td></tr>
     * </table></p>
     * @param locale The locale of the message to return.
     * @return The text of the {@code verboseOption} message.
     *
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated
    (
        value = "org.jomc.tools.JavaSources",
        comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-1-SNAPSHOT/jomc-tools"
    )
    private String getVerboseOptionMessage( final java.util.Locale locale ) throws org.jomc.ObjectManagementException
    {
        return org.jomc.ObjectManagerFactory.getObjectManager().getMessage( this, "verboseOption", locale,  null );
    }

    /**
     * Gets the text of the {@code writing} message.
     * <p><b>Templates</b><br/><table>
     * <tr><td valign="top">English:</td><td valign="top"><pre>Writing ''{0}''.</pre></td></tr>
     * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Schreibt ''{0}''.</pre></td></tr>
     * </table></p>
     * @param locale The locale of the message to return.
     * @param fileName Format argument.
     * @return The text of the {@code writing} message.
     *
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated
    (
        value = "org.jomc.tools.JavaSources",
        comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-1-SNAPSHOT/jomc-tools"
    )
    private String getWritingMessage( final java.util.Locale locale, final java.lang.String fileName ) throws org.jomc.ObjectManagementException
    {
        return org.jomc.ObjectManagerFactory.getObjectManager().getMessage( this, "writing", locale, new Object[] { fileName, null } );
    }
    // SECTION-END
}
