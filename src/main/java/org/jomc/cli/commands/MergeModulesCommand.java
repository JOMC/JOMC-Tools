// SECTION-START[License Header]
// <editor-fold defaultstate="collapsed" desc=" Generated License ">
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
// </editor-fold>
// SECTION-END
package org.jomc.cli.commands;

import java.io.File;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.bind.util.JAXBResult;
import javax.xml.bind.util.JAXBSource;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.jomc.model.ModelObjectValidationReport;
import org.jomc.model.Module;
import org.jomc.model.Modules;
import org.jomc.model.ObjectFactory;

// SECTION-START[Documentation]
// <editor-fold defaultstate="collapsed" desc=" Generated Documentation ">
/**
 * Command line interface for merging modules.
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
 * <li>"{@link #getDocumentOptionLongName documentOptionLongName}"
 * <blockquote>Property of type {@code java.lang.String}.
 * <p>Long name of the 'document' option.</p>
 * </blockquote></li>
 * <li>"{@link #getDocumentOptionShortName documentOptionShortName}"
 * <blockquote>Property of type {@code java.lang.String}.
 * <p>Name of the 'document' option.</p>
 * </blockquote></li>
 * <li>"{@link #getDocumentsOptionLongName documentsOptionLongName}"
 * <blockquote>Property of type {@code java.lang.String}.
 * <p>Long name of the 'documents' option.</p>
 * </blockquote></li>
 * <li>"{@link #getDocumentsOptionShortName documentsOptionShortName}"
 * <blockquote>Property of type {@code java.lang.String}.
 * <p>Name of the 'documents' option.</p>
 * </blockquote></li>
 * <li>"{@link #getModuleExcludesOptionLongName moduleExcludesOptionLongName}"
 * <blockquote>Property of type {@code java.lang.String}.
 * <p>Long name of the 'module-excludes' option.</p>
 * </blockquote></li>
 * <li>"{@link #getModuleExcludesOptionShortName moduleExcludesOptionShortName}"
 * <blockquote>Property of type {@code java.lang.String}.
 * <p>Name of the 'module-excludes' option.</p>
 * </blockquote></li>
 * <li>"{@link #getModuleIncludesOptionLongName moduleIncludesOptionLongName}"
 * <blockquote>Property of type {@code java.lang.String}.
 * <p>Long name of the 'module-includes' option.</p>
 * </blockquote></li>
 * <li>"{@link #getModuleIncludesOptionShortName moduleIncludesOptionShortName}"
 * <blockquote>Property of type {@code java.lang.String}.
 * <p>Name of the 'module-includes' option.</p>
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
 * <li>"{@link #getModuleVendorOptionLongName moduleVendorOptionLongName}"
 * <blockquote>Property of type {@code java.lang.String}.
 * <p>Long name of the 'module-vendor' option.</p>
 * </blockquote></li>
 * <li>"{@link #getModuleVendorOptionShortName moduleVendorOptionShortName}"
 * <blockquote>Property of type {@code java.lang.String}.
 * <p>Name of the 'module-vendor' option.</p>
 * </blockquote></li>
 * <li>"{@link #getModuleVersionOptionLongName moduleVersionOptionLongName}"
 * <blockquote>Property of type {@code java.lang.String}.
 * <p>Long name of the 'module-version' option.</p>
 * </blockquote></li>
 * <li>"{@link #getModuleVersionOptionShortName moduleVersionOptionShortName}"
 * <blockquote>Property of type {@code java.lang.String}.
 * <p>Name of the 'module-version' option.</p>
 * </blockquote></li>
 * <li>"{@link #getNoClasspathResolutionOptionLongName noClasspathResolutionOptionLongName}"
 * <blockquote>Property of type {@code java.lang.String}.
 * <p>Long name of the 'no-classpath-resolution' option.</p>
 * </blockquote></li>
 * <li>"{@link #getNoClasspathResolutionOptionShortName noClasspathResolutionOptionShortName}"
 * <blockquote>Property of type {@code java.lang.String}.
 * <p>Name of the 'no-classpath-resolution' option.</p>
 * </blockquote></li>
 * <li>"{@link #getStylesheetOptionLongName stylesheetOptionLongName}"
 * <blockquote>Property of type {@code java.lang.String}.
 * <p>Long name of the 'xslt' option.</p>
 * </blockquote></li>
 * <li>"{@link #getStylesheetOptionShortName stylesheetOptionShortName}"
 * <blockquote>Property of type {@code java.lang.String}.
 * <p>Name of the 'xslt' option.</p>
 * </blockquote></li>
 * </ul></p>
 * <p><b>Dependencies</b><ul>
 * <li>"{@link #getLocale Locale}"<blockquote>
 * Dependency on {@code java.util.Locale} at specification level 1.1 bound to an instance.</blockquote></li>
 * </ul></p>
 * <p><b>Messages</b><ul>
 * <li>"{@link #getApplicationTitleMessage applicationTitle}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>JOMC Version 1.0-alpha-12-SNAPSHOT Build 2009-12-10T06:12:12+0000</pre></td></tr>
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
 * <li>"{@link #getDocumentOptionMessage documentOption}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>Name of the file to write the merged module to.</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Name der Datei in die das zusammengef&uuml;gte Modul geschrieben werden soll.</pre></td></tr>
 * </table>
 * <li>"{@link #getDocumentOptionArgNameMessage documentOptionArgName}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>file</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Datei</pre></td></tr>
 * </table>
 * <li>"{@link #getDocumentsOptionMessage documentsOption}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>Document filenames separated by ''{0}''. If starting with a ''@'' character, a file name of a file holding document filenames.</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Dokument-Dateinamen mit ''{0}'' getrennt. Wenn mit ''@'' beginnend, Dateiname einer Textdatei mit Dokument-Dateinamen.</pre></td></tr>
 * </table>
 * <li>"{@link #getDocumentsOptionArgNameMessage documentsOptionArgName}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>files</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Dateien</pre></td></tr>
 * </table>
 * <li>"{@link #getExcludingModuleMessage excludingModule}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>Excluding module ''{0}''.</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Schlie&szlig;t Modul ''{0}'' aus.</pre></td></tr>
 * </table>
 * <li>"{@link #getIncludingModuleMessage includingModule}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>Including module ''{0}''.</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Schlie&szlig;t Modul ''{0}'' ein.</pre></td></tr>
 * </table>
 * <li>"{@link #getInvalidModelMessage invalidModel}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>Invalid model.</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Ung&uuml;ltiges Modell.</pre></td></tr>
 * </table>
 * <li>"{@link #getLongDescriptionMessage longDescription}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>Example:
 *   jomc merge-modules -df examples/xml/jomc-cli.xml \
 *                      -xs examples/xslt/relocate-classes.xslt \
 *                      -mn &quot;Merged Name&quot; -d /tmp/jomc.xml -v</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Beispiel:
 *   jomc merge-modules -df examples/xml/jomc-cli.xml \
 *                      -xs examples/xslt/relocate-classes.xslt \
 *                      -mn &quot;Merged Name&quot; -d /tmp/jomc.xml -v</pre></td></tr>
 * </table>
 * <li>"{@link #getMissingModuleMessage missingModule}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>Module ''{0}'' not found.</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Modul ''{0}'' nicht gefunden.</pre></td></tr>
 * </table>
 * <li>"{@link #getModuleExcludesOptionMessage moduleExcludesOption}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>Module names separated by '':'' of modules to exclude.</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Modul-Namen auszuschlie&szlig;ender Module mit '':'' getrennt.</pre></td></tr>
 * </table>
 * <li>"{@link #getModuleExcludesOptionArgNameMessage moduleExcludesOptionArgName}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>names</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Namen</pre></td></tr>
 * </table>
 * <li>"{@link #getModuleIncludesOptionMessage moduleIncludesOption}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>Module names separated by '':'' of modules to include.</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Modul-Namen einzuschlie&szlig;ender Module mit '':'' getrennt.</pre></td></tr>
 * </table>
 * <li>"{@link #getModuleIncludesOptionArgNameMessage moduleIncludesOptionArgName}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>names</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Namen</pre></td></tr>
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
 * <li>"{@link #getModulesReportMessage modulesReport}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>Modules</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Module</pre></td></tr>
 * </table>
 * <li>"{@link #getNoClasspathResolutionOptionMessage noClasspathResolutionOption}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>Do not perform classpath resolution.</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Keine Klassenpfad-Aufl&ouml;sung durchf&uuml;hren.</pre></td></tr>
 * </table>
 * <li>"{@link #getSeparatorMessage separator}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>--------------------------------------------------------------------------------</pre></td></tr>
 * </table>
 * <li>"{@link #getShortDescriptionMessage shortDescription}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>Merges modules.</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>F&uuml;gt Module zusammen.</pre></td></tr>
 * </table>
 * <li>"{@link #getStartingModuleProcessingMessage startingModuleProcessing}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>Executing command {0} with module ''{1}'' ...</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>F&uuml;hrt Befehl {0} mit Modul ''{1}'' aus ... </pre></td></tr>
 * </table>
 * <li>"{@link #getStartingProcessingMessage startingProcessing}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>Executing command {0} ...</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>F&uuml;hrt Befehl {0} aus ... </pre></td></tr>
 * </table>
 * <li>"{@link #getStylesheetOptionMessage stylesheetOption}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>Name of a XSLT file to use for transforming the merged module.</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Name einer XSLT Datei mit der das zusammengef&uuml;gte Modul transformiert werden soll.</pre></td></tr>
 * </table>
 * <li>"{@link #getStylesheetOptionArgNameMessage stylesheetOptionArgName}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>XSLT file</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>XSLT-Datei</pre></td></tr>
 * </table>
 * <li>"{@link #getToolFailureMessage toolFailure}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>{0} failure.</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>{0} fehlgeschlagen.</pre></td></tr>
 * </table>
 * <li>"{@link #getToolSuccessMessage toolSuccess}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>{0} successful.</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>{0} erfolgreich.</pre></td></tr>
 * </table>
 * <li>"{@link #getWritingMessage writing}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>Writing ''{0}''.</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Schreibt ''{0}''.</pre></td></tr>
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
                             comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-12-SNAPSHOT/jomc-tools" )
// </editor-fold>
// SECTION-END
public final class MergeModulesCommand extends AbstractJomcCommand
{
    // SECTION-START[Command]

    /** Options of the instance. */
    private Options options;

    @Override
    public Options getOptions()
    {
        if ( this.options == null )
        {
            this.getModuleNameOption().setRequired( true );

            this.options = super.getOptions();
            this.options.addOption( this.getDocumentOption() );
            this.options.addOption( this.getStylesheetOption() );
            this.options.addOption( this.getModuleVendorOption() );
            this.options.addOption( this.getModuleVersionOption() );
            this.options.addOption( this.getModuleIncludesOption() );
            this.options.addOption( this.getModuleExcludesOption() );
        }

        return this.options;
    }

    public int executeCommand( final CommandLine commandLine ) throws Exception
    {
        if ( this.isLoggable( Level.INFO ) )
        {
            this.log( Level.INFO, this.getStartingProcessingMessage( this.getLocale(), this.getCommandName() ), null );
        }

        final Modules modules = this.getModules( commandLine );
        final ClassLoader classLoader = this.getClassLoader( commandLine );
        final JAXBContext context = this.getModelManager().getContext( classLoader );
        final Marshaller marshaller = this.getModelManager().getMarshaller( classLoader );
        final Schema schema = this.getModelManager().getSchema( classLoader );
        final ModelObjectValidationReport validationReport = this.getModelObjectValidator().validateModules(
            new ObjectFactory().createModules( modules ), context, schema );

        this.log( validationReport, marshaller );

        if ( validationReport.isModelObjectValid() )
        {
            modules.getModule().remove( modules.getModule( Modules.getDefaultClasspathModuleName() ) );

            File stylesheetFile = null;
            if ( commandLine.hasOption( this.getStylesheetOption().getOpt() ) )
            {
                stylesheetFile = new File( commandLine.getOptionValue( this.getStylesheetOption().getOpt() ) );
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

            if ( commandLine.hasOption( this.getModuleIncludesOption().getOpt() ) )
            {
                final String[] values = commandLine.getOptionValues( this.getModuleIncludesOption().getOpt() );

                if ( values != null )
                {
                    final List<String> includes = Arrays.asList( values );

                    for ( final Iterator<Module> it = modules.getModule().iterator(); it.hasNext(); )
                    {
                        final Module m = it.next();
                        if ( !includes.contains( m.getName() ) )
                        {
                            this.log( Level.INFO, this.getExcludingModuleMessage(
                                this.getLocale(), m.getName() ), null );

                            it.remove();
                        }
                        else
                        {
                            this.log( Level.INFO, this.getIncludingModuleMessage(
                                this.getLocale(), m.getName() ), null );

                        }
                    }
                }
            }

            if ( commandLine.hasOption( this.getModuleExcludesOption().getOpt() ) )
            {
                final String[] values = commandLine.getOptionValues( this.getModuleExcludesOption().getOpt() );

                if ( values != null )
                {
                    for ( String exclude : values )
                    {
                        final Module m = modules.getModule( exclude );

                        if ( m != null )
                        {
                            this.log( Level.INFO, this.getExcludingModuleMessage(
                                this.getLocale(), m.getName() ), null );

                            modules.getModule().remove( m );
                        }
                    }
                }
            }

            Module mergedModule = modules.getMergedModule();
            mergedModule.setName( commandLine.getOptionValue( this.getModuleNameOption().getOpt() ) );
            mergedModule.setVersion( moduleVersion );
            mergedModule.setVendor( moduleVendor );

            final File moduleFile = new File( commandLine.getOptionValue( this.getDocumentOption().getOpt() ) );

            if ( stylesheetFile != null )
            {
                final Transformer transformer =
                    TransformerFactory.newInstance().newTransformer( new StreamSource( stylesheetFile ) );

                final JAXBSource source =
                    new JAXBSource( this.getModelManager().getMarshaller( classLoader ),
                                    new ObjectFactory().createModule( mergedModule ) );

                final JAXBResult result = new JAXBResult( this.getModelManager().getUnmarshaller( classLoader ) );
                transformer.transform( source, result );
                mergedModule = ( (JAXBElement<Module>) result.getResult() ).getValue();
            }

            marshaller.setSchema( schema );
            marshaller.marshal( new ObjectFactory().createModule( mergedModule ), moduleFile );

            if ( this.isLoggable( Level.INFO ) )
            {
                this.log( Level.INFO, this.getWritingMessage( this.getLocale(), moduleFile.getAbsolutePath() ), null );
            }

            return STATUS_SUCCESS;
        }

        return STATUS_FAILURE;
    }

    // SECTION-END
    // SECTION-START[AssembleModulesCommand]
    private Option stylesheetOption;

    private Option documentOption;

    private Option moduleVersionOption;

    private Option moduleVendorOption;

    private Option moduleIncludesOption;

    private Option moduleExcludesOption;

    protected Option getStylesheetOption()
    {
        if ( this.stylesheetOption == null )
        {
            this.stylesheetOption = new Option( this.getStylesheetOptionShortName(),
                                                this.getStylesheetOptionLongName(), true,
                                                this.getStylesheetOptionMessage( this.getLocale() ) );

            this.stylesheetOption.setArgName( this.getStylesheetOptionArgNameMessage( this.getLocale() ) );
        }

        return this.stylesheetOption;
    }

    protected Option getDocumentOption()
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

    protected Option getModuleVersionOption()
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

    protected Option getModuleVendorOption()
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

    protected Option getModuleIncludesOption()
    {
        if ( this.moduleIncludesOption == null )
        {
            this.moduleIncludesOption = new Option( this.getModuleIncludesOptionShortName(),
                                                    this.getModuleIncludesOptionLongName(), false,
                                                    this.getModuleIncludesOptionMessage( this.getLocale() ) );

            this.moduleIncludesOption.setArgName( this.getModuleIncludesOptionArgNameMessage( this.getLocale() ) );
            this.moduleIncludesOption.setValueSeparator( ':' );
            this.moduleIncludesOption.setArgs( Option.UNLIMITED_VALUES );
        }

        return this.moduleIncludesOption;
    }

    protected Option getModuleExcludesOption()
    {
        if ( this.moduleExcludesOption == null )
        {
            this.moduleExcludesOption = new Option( this.getModuleExcludesOptionShortName(),
                                                    this.getModuleExcludesOptionLongName(), false,
                                                    this.getModuleExcludesOptionMessage( this.getLocale() ) );

            this.moduleExcludesOption.setArgName( this.getModuleExcludesOptionArgNameMessage( this.getLocale() ) );
            this.moduleExcludesOption.setValueSeparator( ':' );
            this.moduleExcludesOption.setArgs( Option.UNLIMITED_VALUES );
        }

        return this.moduleExcludesOption;
    }

    // SECTION-END
    // SECTION-START[Constructors]
    // <editor-fold defaultstate="collapsed" desc=" Generated Constructors ">

    /** Creates a new {@code MergeModulesCommand} instance. */
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-12-SNAPSHOT/jomc-tools" )
    public MergeModulesCommand()
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
     * Gets the {@code Locale} dependency.
     * <p>This method returns the "{@code default}" object of the {@code java.util.Locale} specification at specification level 1.1.</p>
     * <p>That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.</p>
     * @return The {@code Locale} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-12-SNAPSHOT/jomc-tools" )
    private java.util.Locale getLocale()
    {
        final java.util.Locale _d = (java.util.Locale) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "Locale" );
        assert _d != null : "'Locale' dependency not found.";
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
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-12-SNAPSHOT/jomc-tools" )
    private java.lang.String getAbbreviatedCommandName()
    {
        final java.lang.String _p = (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getProperty( this, "abbreviatedCommandName" );
        assert _p != null : "'abbreviatedCommandName' property not found.";
        return _p;
    }

    /**
     * Gets the value of the {@code classpathOptionLongName} property.
     * @return Long name of the 'classpath' option.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-12-SNAPSHOT/jomc-tools" )
    private java.lang.String getClasspathOptionLongName()
    {
        final java.lang.String _p = (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getProperty( this, "classpathOptionLongName" );
        assert _p != null : "'classpathOptionLongName' property not found.";
        return _p;
    }

    /**
     * Gets the value of the {@code classpathOptionShortName} property.
     * @return Name of the 'classpath' option.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-12-SNAPSHOT/jomc-tools" )
    private java.lang.String getClasspathOptionShortName()
    {
        final java.lang.String _p = (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getProperty( this, "classpathOptionShortName" );
        assert _p != null : "'classpathOptionShortName' property not found.";
        return _p;
    }

    /**
     * Gets the value of the {@code commandName} property.
     * @return Name of the command.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-12-SNAPSHOT/jomc-tools" )
    private java.lang.String getCommandName()
    {
        final java.lang.String _p = (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getProperty( this, "commandName" );
        assert _p != null : "'commandName' property not found.";
        return _p;
    }

    /**
     * Gets the value of the {@code documentOptionLongName} property.
     * @return Long name of the 'document' option.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-12-SNAPSHOT/jomc-tools" )
    private java.lang.String getDocumentOptionLongName()
    {
        final java.lang.String _p = (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getProperty( this, "documentOptionLongName" );
        assert _p != null : "'documentOptionLongName' property not found.";
        return _p;
    }

    /**
     * Gets the value of the {@code documentOptionShortName} property.
     * @return Name of the 'document' option.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-12-SNAPSHOT/jomc-tools" )
    private java.lang.String getDocumentOptionShortName()
    {
        final java.lang.String _p = (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getProperty( this, "documentOptionShortName" );
        assert _p != null : "'documentOptionShortName' property not found.";
        return _p;
    }

    /**
     * Gets the value of the {@code documentsOptionLongName} property.
     * @return Long name of the 'documents' option.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-12-SNAPSHOT/jomc-tools" )
    private java.lang.String getDocumentsOptionLongName()
    {
        final java.lang.String _p = (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getProperty( this, "documentsOptionLongName" );
        assert _p != null : "'documentsOptionLongName' property not found.";
        return _p;
    }

    /**
     * Gets the value of the {@code documentsOptionShortName} property.
     * @return Name of the 'documents' option.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-12-SNAPSHOT/jomc-tools" )
    private java.lang.String getDocumentsOptionShortName()
    {
        final java.lang.String _p = (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getProperty( this, "documentsOptionShortName" );
        assert _p != null : "'documentsOptionShortName' property not found.";
        return _p;
    }

    /**
     * Gets the value of the {@code moduleExcludesOptionLongName} property.
     * @return Long name of the 'module-excludes' option.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-12-SNAPSHOT/jomc-tools" )
    private java.lang.String getModuleExcludesOptionLongName()
    {
        final java.lang.String _p = (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getProperty( this, "moduleExcludesOptionLongName" );
        assert _p != null : "'moduleExcludesOptionLongName' property not found.";
        return _p;
    }

    /**
     * Gets the value of the {@code moduleExcludesOptionShortName} property.
     * @return Name of the 'module-excludes' option.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-12-SNAPSHOT/jomc-tools" )
    private java.lang.String getModuleExcludesOptionShortName()
    {
        final java.lang.String _p = (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getProperty( this, "moduleExcludesOptionShortName" );
        assert _p != null : "'moduleExcludesOptionShortName' property not found.";
        return _p;
    }

    /**
     * Gets the value of the {@code moduleIncludesOptionLongName} property.
     * @return Long name of the 'module-includes' option.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-12-SNAPSHOT/jomc-tools" )
    private java.lang.String getModuleIncludesOptionLongName()
    {
        final java.lang.String _p = (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getProperty( this, "moduleIncludesOptionLongName" );
        assert _p != null : "'moduleIncludesOptionLongName' property not found.";
        return _p;
    }

    /**
     * Gets the value of the {@code moduleIncludesOptionShortName} property.
     * @return Name of the 'module-includes' option.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-12-SNAPSHOT/jomc-tools" )
    private java.lang.String getModuleIncludesOptionShortName()
    {
        final java.lang.String _p = (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getProperty( this, "moduleIncludesOptionShortName" );
        assert _p != null : "'moduleIncludesOptionShortName' property not found.";
        return _p;
    }

    /**
     * Gets the value of the {@code moduleLocationOptionLongName} property.
     * @return Long name of the 'module-location' option.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-12-SNAPSHOT/jomc-tools" )
    private java.lang.String getModuleLocationOptionLongName()
    {
        final java.lang.String _p = (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getProperty( this, "moduleLocationOptionLongName" );
        assert _p != null : "'moduleLocationOptionLongName' property not found.";
        return _p;
    }

    /**
     * Gets the value of the {@code moduleLocationOptionShortName} property.
     * @return Name of the 'module-location' option.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-12-SNAPSHOT/jomc-tools" )
    private java.lang.String getModuleLocationOptionShortName()
    {
        final java.lang.String _p = (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getProperty( this, "moduleLocationOptionShortName" );
        assert _p != null : "'moduleLocationOptionShortName' property not found.";
        return _p;
    }

    /**
     * Gets the value of the {@code moduleNameOptionLongName} property.
     * @return Long name of the 'module' option.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-12-SNAPSHOT/jomc-tools" )
    private java.lang.String getModuleNameOptionLongName()
    {
        final java.lang.String _p = (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getProperty( this, "moduleNameOptionLongName" );
        assert _p != null : "'moduleNameOptionLongName' property not found.";
        return _p;
    }

    /**
     * Gets the value of the {@code moduleNameOptionShortName} property.
     * @return Name of the 'module' option.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-12-SNAPSHOT/jomc-tools" )
    private java.lang.String getModuleNameOptionShortName()
    {
        final java.lang.String _p = (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getProperty( this, "moduleNameOptionShortName" );
        assert _p != null : "'moduleNameOptionShortName' property not found.";
        return _p;
    }

    /**
     * Gets the value of the {@code moduleVendorOptionLongName} property.
     * @return Long name of the 'module-vendor' option.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-12-SNAPSHOT/jomc-tools" )
    private java.lang.String getModuleVendorOptionLongName()
    {
        final java.lang.String _p = (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getProperty( this, "moduleVendorOptionLongName" );
        assert _p != null : "'moduleVendorOptionLongName' property not found.";
        return _p;
    }

    /**
     * Gets the value of the {@code moduleVendorOptionShortName} property.
     * @return Name of the 'module-vendor' option.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-12-SNAPSHOT/jomc-tools" )
    private java.lang.String getModuleVendorOptionShortName()
    {
        final java.lang.String _p = (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getProperty( this, "moduleVendorOptionShortName" );
        assert _p != null : "'moduleVendorOptionShortName' property not found.";
        return _p;
    }

    /**
     * Gets the value of the {@code moduleVersionOptionLongName} property.
     * @return Long name of the 'module-version' option.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-12-SNAPSHOT/jomc-tools" )
    private java.lang.String getModuleVersionOptionLongName()
    {
        final java.lang.String _p = (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getProperty( this, "moduleVersionOptionLongName" );
        assert _p != null : "'moduleVersionOptionLongName' property not found.";
        return _p;
    }

    /**
     * Gets the value of the {@code moduleVersionOptionShortName} property.
     * @return Name of the 'module-version' option.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-12-SNAPSHOT/jomc-tools" )
    private java.lang.String getModuleVersionOptionShortName()
    {
        final java.lang.String _p = (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getProperty( this, "moduleVersionOptionShortName" );
        assert _p != null : "'moduleVersionOptionShortName' property not found.";
        return _p;
    }

    /**
     * Gets the value of the {@code noClasspathResolutionOptionLongName} property.
     * @return Long name of the 'no-classpath-resolution' option.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-12-SNAPSHOT/jomc-tools" )
    private java.lang.String getNoClasspathResolutionOptionLongName()
    {
        final java.lang.String _p = (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getProperty( this, "noClasspathResolutionOptionLongName" );
        assert _p != null : "'noClasspathResolutionOptionLongName' property not found.";
        return _p;
    }

    /**
     * Gets the value of the {@code noClasspathResolutionOptionShortName} property.
     * @return Name of the 'no-classpath-resolution' option.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-12-SNAPSHOT/jomc-tools" )
    private java.lang.String getNoClasspathResolutionOptionShortName()
    {
        final java.lang.String _p = (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getProperty( this, "noClasspathResolutionOptionShortName" );
        assert _p != null : "'noClasspathResolutionOptionShortName' property not found.";
        return _p;
    }

    /**
     * Gets the value of the {@code stylesheetOptionLongName} property.
     * @return Long name of the 'xslt' option.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-12-SNAPSHOT/jomc-tools" )
    private java.lang.String getStylesheetOptionLongName()
    {
        final java.lang.String _p = (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getProperty( this, "stylesheetOptionLongName" );
        assert _p != null : "'stylesheetOptionLongName' property not found.";
        return _p;
    }

    /**
     * Gets the value of the {@code stylesheetOptionShortName} property.
     * @return Name of the 'xslt' option.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-12-SNAPSHOT/jomc-tools" )
    private java.lang.String getStylesheetOptionShortName()
    {
        final java.lang.String _p = (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getProperty( this, "stylesheetOptionShortName" );
        assert _p != null : "'stylesheetOptionShortName' property not found.";
        return _p;
    }
    // </editor-fold>
    // SECTION-END
    // SECTION-START[Messages]
    // <editor-fold defaultstate="collapsed" desc=" Generated Messages ">

    /**
     * Gets the text of the {@code applicationTitle} message.
     * <p><b>Templates</b><br/><table>
     * <tr><td valign="top">English:</td><td valign="top"><pre>JOMC Version 1.0-alpha-12-SNAPSHOT Build 2009-12-10T06:12:12+0000</pre></td></tr>
     * </table></p>
     * @param locale The locale of the message to return.
     * @return The text of the {@code applicationTitle} message.
     *
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-12-SNAPSHOT/jomc-tools" )
    private String getApplicationTitleMessage( final java.util.Locale locale )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "applicationTitle", locale,  null );
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
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-12-SNAPSHOT/jomc-tools" )
    private String getCannotProcessMessage( final java.util.Locale locale, final java.lang.String itemInfo, final java.lang.String detailMessage )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "cannotProcess", locale, new Object[] { itemInfo, detailMessage, null } );
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
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-12-SNAPSHOT/jomc-tools" )
    private String getClasspathElementMessage( final java.util.Locale locale, final java.lang.String classpathElement )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "classpathElement", locale, new Object[] { classpathElement, null } );
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
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-12-SNAPSHOT/jomc-tools" )
    private String getClasspathOptionMessage( final java.util.Locale locale, final java.lang.String pathSeparator )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "classpathOption", locale, new Object[] { pathSeparator, null } );
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
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-12-SNAPSHOT/jomc-tools" )
    private String getClasspathOptionArgNameMessage( final java.util.Locale locale )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "classpathOptionArgName", locale,  null );
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
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-12-SNAPSHOT/jomc-tools" )
    private String getDefaultLogLevelInfoMessage( final java.util.Locale locale, final java.lang.String defaultLogLevel )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "defaultLogLevelInfo", locale, new Object[] { defaultLogLevel, null } );
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
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-12-SNAPSHOT/jomc-tools" )
    private String getDocumentFileMessage( final java.util.Locale locale, final java.lang.String documentFile )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "documentFile", locale, new Object[] { documentFile, null } );
        assert _m != null : "'documentFile' message not found.";
        return _m;
    }

    /**
     * Gets the text of the {@code documentOption} message.
     * <p><b>Templates</b><br/><table>
     * <tr><td valign="top">English:</td><td valign="top"><pre>Name of the file to write the merged module to.</pre></td></tr>
     * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Name der Datei in die das zusammengef&uuml;gte Modul geschrieben werden soll.</pre></td></tr>
     * </table></p>
     * @param locale The locale of the message to return.
     * @return The text of the {@code documentOption} message.
     *
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-12-SNAPSHOT/jomc-tools" )
    private String getDocumentOptionMessage( final java.util.Locale locale )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "documentOption", locale,  null );
        assert _m != null : "'documentOption' message not found.";
        return _m;
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
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-12-SNAPSHOT/jomc-tools" )
    private String getDocumentOptionArgNameMessage( final java.util.Locale locale )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "documentOptionArgName", locale,  null );
        assert _m != null : "'documentOptionArgName' message not found.";
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
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-12-SNAPSHOT/jomc-tools" )
    private String getDocumentsOptionMessage( final java.util.Locale locale, final java.lang.String pathSeparator )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "documentsOption", locale, new Object[] { pathSeparator, null } );
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
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-12-SNAPSHOT/jomc-tools" )
    private String getDocumentsOptionArgNameMessage( final java.util.Locale locale )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "documentsOptionArgName", locale,  null );
        assert _m != null : "'documentsOptionArgName' message not found.";
        return _m;
    }

    /**
     * Gets the text of the {@code excludingModule} message.
     * <p><b>Templates</b><br/><table>
     * <tr><td valign="top">English:</td><td valign="top"><pre>Excluding module ''{0}''.</pre></td></tr>
     * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Schlie&szlig;t Modul ''{0}'' aus.</pre></td></tr>
     * </table></p>
     * @param locale The locale of the message to return.
     * @param moduleName Format argument.
     * @return The text of the {@code excludingModule} message.
     *
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-12-SNAPSHOT/jomc-tools" )
    private String getExcludingModuleMessage( final java.util.Locale locale, final java.lang.String moduleName )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "excludingModule", locale, new Object[] { moduleName, null } );
        assert _m != null : "'excludingModule' message not found.";
        return _m;
    }

    /**
     * Gets the text of the {@code includingModule} message.
     * <p><b>Templates</b><br/><table>
     * <tr><td valign="top">English:</td><td valign="top"><pre>Including module ''{0}''.</pre></td></tr>
     * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Schlie&szlig;t Modul ''{0}'' ein.</pre></td></tr>
     * </table></p>
     * @param locale The locale of the message to return.
     * @param moduleName Format argument.
     * @return The text of the {@code includingModule} message.
     *
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-12-SNAPSHOT/jomc-tools" )
    private String getIncludingModuleMessage( final java.util.Locale locale, final java.lang.String moduleName )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "includingModule", locale, new Object[] { moduleName, null } );
        assert _m != null : "'includingModule' message not found.";
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
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-12-SNAPSHOT/jomc-tools" )
    private String getInvalidModelMessage( final java.util.Locale locale )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "invalidModel", locale,  null );
        assert _m != null : "'invalidModel' message not found.";
        return _m;
    }

    /**
     * Gets the text of the {@code longDescription} message.
     * <p><b>Templates</b><br/><table>
     * <tr><td valign="top">English:</td><td valign="top"><pre>Example:
     *   jomc merge-modules -df examples/xml/jomc-cli.xml \
     *                      -xs examples/xslt/relocate-classes.xslt \
     *                      -mn &quot;Merged Name&quot; -d /tmp/jomc.xml -v</pre></td></tr>
     * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Beispiel:
     *   jomc merge-modules -df examples/xml/jomc-cli.xml \
     *                      -xs examples/xslt/relocate-classes.xslt \
     *                      -mn &quot;Merged Name&quot; -d /tmp/jomc.xml -v</pre></td></tr>
     * </table></p>
     * @param locale The locale of the message to return.
     * @return The text of the {@code longDescription} message.
     *
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-12-SNAPSHOT/jomc-tools" )
    private String getLongDescriptionMessage( final java.util.Locale locale )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "longDescription", locale,  null );
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
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-12-SNAPSHOT/jomc-tools" )
    private String getMissingModuleMessage( final java.util.Locale locale, final java.lang.String moduleName )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "missingModule", locale, new Object[] { moduleName, null } );
        assert _m != null : "'missingModule' message not found.";
        return _m;
    }

    /**
     * Gets the text of the {@code moduleExcludesOption} message.
     * <p><b>Templates</b><br/><table>
     * <tr><td valign="top">English:</td><td valign="top"><pre>Module names separated by '':'' of modules to exclude.</pre></td></tr>
     * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Modul-Namen auszuschlie&szlig;ender Module mit '':'' getrennt.</pre></td></tr>
     * </table></p>
     * @param locale The locale of the message to return.
     * @return The text of the {@code moduleExcludesOption} message.
     *
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-12-SNAPSHOT/jomc-tools" )
    private String getModuleExcludesOptionMessage( final java.util.Locale locale )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "moduleExcludesOption", locale,  null );
        assert _m != null : "'moduleExcludesOption' message not found.";
        return _m;
    }

    /**
     * Gets the text of the {@code moduleExcludesOptionArgName} message.
     * <p><b>Templates</b><br/><table>
     * <tr><td valign="top">English:</td><td valign="top"><pre>names</pre></td></tr>
     * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Namen</pre></td></tr>
     * </table></p>
     * @param locale The locale of the message to return.
     * @return The text of the {@code moduleExcludesOptionArgName} message.
     *
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-12-SNAPSHOT/jomc-tools" )
    private String getModuleExcludesOptionArgNameMessage( final java.util.Locale locale )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "moduleExcludesOptionArgName", locale,  null );
        assert _m != null : "'moduleExcludesOptionArgName' message not found.";
        return _m;
    }

    /**
     * Gets the text of the {@code moduleIncludesOption} message.
     * <p><b>Templates</b><br/><table>
     * <tr><td valign="top">English:</td><td valign="top"><pre>Module names separated by '':'' of modules to include.</pre></td></tr>
     * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Modul-Namen einzuschlie&szlig;ender Module mit '':'' getrennt.</pre></td></tr>
     * </table></p>
     * @param locale The locale of the message to return.
     * @return The text of the {@code moduleIncludesOption} message.
     *
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-12-SNAPSHOT/jomc-tools" )
    private String getModuleIncludesOptionMessage( final java.util.Locale locale )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "moduleIncludesOption", locale,  null );
        assert _m != null : "'moduleIncludesOption' message not found.";
        return _m;
    }

    /**
     * Gets the text of the {@code moduleIncludesOptionArgName} message.
     * <p><b>Templates</b><br/><table>
     * <tr><td valign="top">English:</td><td valign="top"><pre>names</pre></td></tr>
     * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Namen</pre></td></tr>
     * </table></p>
     * @param locale The locale of the message to return.
     * @return The text of the {@code moduleIncludesOptionArgName} message.
     *
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-12-SNAPSHOT/jomc-tools" )
    private String getModuleIncludesOptionArgNameMessage( final java.util.Locale locale )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "moduleIncludesOptionArgName", locale,  null );
        assert _m != null : "'moduleIncludesOptionArgName' message not found.";
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
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-12-SNAPSHOT/jomc-tools" )
    private String getModuleLocationOptionMessage( final java.util.Locale locale )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "moduleLocationOption", locale,  null );
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
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-12-SNAPSHOT/jomc-tools" )
    private String getModuleLocationOptionArgNameMessage( final java.util.Locale locale )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "moduleLocationOptionArgName", locale,  null );
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
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-12-SNAPSHOT/jomc-tools" )
    private String getModuleNameOptionMessage( final java.util.Locale locale )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "moduleNameOption", locale,  null );
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
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-12-SNAPSHOT/jomc-tools" )
    private String getModuleNameOptionArgNameMessage( final java.util.Locale locale )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "moduleNameOptionArgName", locale,  null );
        assert _m != null : "'moduleNameOptionArgName' message not found.";
        return _m;
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
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-12-SNAPSHOT/jomc-tools" )
    private String getModuleVendorOptionMessage( final java.util.Locale locale )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "moduleVendorOption", locale,  null );
        assert _m != null : "'moduleVendorOption' message not found.";
        return _m;
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
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-12-SNAPSHOT/jomc-tools" )
    private String getModuleVendorOptionArgNameMessage( final java.util.Locale locale )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "moduleVendorOptionArgName", locale,  null );
        assert _m != null : "'moduleVendorOptionArgName' message not found.";
        return _m;
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
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-12-SNAPSHOT/jomc-tools" )
    private String getModuleVersionOptionMessage( final java.util.Locale locale )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "moduleVersionOption", locale,  null );
        assert _m != null : "'moduleVersionOption' message not found.";
        return _m;
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
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-12-SNAPSHOT/jomc-tools" )
    private String getModuleVersionOptionArgNameMessage( final java.util.Locale locale )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "moduleVersionOptionArgName", locale,  null );
        assert _m != null : "'moduleVersionOptionArgName' message not found.";
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
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-12-SNAPSHOT/jomc-tools" )
    private String getModulesReportMessage( final java.util.Locale locale )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "modulesReport", locale,  null );
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
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-12-SNAPSHOT/jomc-tools" )
    private String getNoClasspathResolutionOptionMessage( final java.util.Locale locale )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "noClasspathResolutionOption", locale,  null );
        assert _m != null : "'noClasspathResolutionOption' message not found.";
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
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-12-SNAPSHOT/jomc-tools" )
    private String getSeparatorMessage( final java.util.Locale locale )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "separator", locale,  null );
        assert _m != null : "'separator' message not found.";
        return _m;
    }

    /**
     * Gets the text of the {@code shortDescription} message.
     * <p><b>Templates</b><br/><table>
     * <tr><td valign="top">English:</td><td valign="top"><pre>Merges modules.</pre></td></tr>
     * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>F&uuml;gt Module zusammen.</pre></td></tr>
     * </table></p>
     * @param locale The locale of the message to return.
     * @return The text of the {@code shortDescription} message.
     *
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-12-SNAPSHOT/jomc-tools" )
    private String getShortDescriptionMessage( final java.util.Locale locale )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "shortDescription", locale,  null );
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
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-12-SNAPSHOT/jomc-tools" )
    private String getStartingModuleProcessingMessage( final java.util.Locale locale, final java.lang.String toolName, final java.lang.String moduleName )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "startingModuleProcessing", locale, new Object[] { toolName, moduleName, null } );
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
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-12-SNAPSHOT/jomc-tools" )
    private String getStartingProcessingMessage( final java.util.Locale locale, final java.lang.String toolName )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "startingProcessing", locale, new Object[] { toolName, null } );
        assert _m != null : "'startingProcessing' message not found.";
        return _m;
    }

    /**
     * Gets the text of the {@code stylesheetOption} message.
     * <p><b>Templates</b><br/><table>
     * <tr><td valign="top">English:</td><td valign="top"><pre>Name of a XSLT file to use for transforming the merged module.</pre></td></tr>
     * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Name einer XSLT Datei mit der das zusammengef&uuml;gte Modul transformiert werden soll.</pre></td></tr>
     * </table></p>
     * @param locale The locale of the message to return.
     * @return The text of the {@code stylesheetOption} message.
     *
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-12-SNAPSHOT/jomc-tools" )
    private String getStylesheetOptionMessage( final java.util.Locale locale )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "stylesheetOption", locale,  null );
        assert _m != null : "'stylesheetOption' message not found.";
        return _m;
    }

    /**
     * Gets the text of the {@code stylesheetOptionArgName} message.
     * <p><b>Templates</b><br/><table>
     * <tr><td valign="top">English:</td><td valign="top"><pre>XSLT file</pre></td></tr>
     * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>XSLT-Datei</pre></td></tr>
     * </table></p>
     * @param locale The locale of the message to return.
     * @return The text of the {@code stylesheetOptionArgName} message.
     *
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-12-SNAPSHOT/jomc-tools" )
    private String getStylesheetOptionArgNameMessage( final java.util.Locale locale )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "stylesheetOptionArgName", locale,  null );
        assert _m != null : "'stylesheetOptionArgName' message not found.";
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
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-12-SNAPSHOT/jomc-tools" )
    private String getToolFailureMessage( final java.util.Locale locale, final java.lang.String toolName )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "toolFailure", locale, new Object[] { toolName, null } );
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
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-12-SNAPSHOT/jomc-tools" )
    private String getToolSuccessMessage( final java.util.Locale locale, final java.lang.String toolName )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "toolSuccess", locale, new Object[] { toolName, null } );
        assert _m != null : "'toolSuccess' message not found.";
        return _m;
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
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-12-SNAPSHOT/jomc-tools" )
    private String getWritingMessage( final java.util.Locale locale, final java.lang.String fileName )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "writing", locale, new Object[] { fileName, null } );
        assert _m != null : "'writing' message not found.";
        return _m;
    }
    // </editor-fold>
    // SECTION-END
}
