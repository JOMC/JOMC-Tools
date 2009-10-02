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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.logging.Level;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.jomc.cli.Command;
import org.jomc.model.DefaultModelManager;
import org.jomc.model.ModelException;
import org.jomc.model.Module;
import org.jomc.model.Modules;
import org.jomc.tools.JomcTool;
import org.jomc.model.ModelManager;
import org.jomc.tools.JavaBundles;
import org.jomc.tools.JavaClasses;
import org.jomc.tools.JavaSources;
import org.xml.sax.SAXException;

// SECTION-START[Documentation]
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
 * <li>"{@link #getDebugOptionLongName debugOptionLongName}"
 * <blockquote>Property of type {@code java.lang.String}.
 * <p>Long name of the 'debug' option.</p>
 * </blockquote></li>
 * <li>"{@link #getDebugOptionShortName debugOptionShortName}"
 * <blockquote>Property of type {@code java.lang.String}.
 * <p>Name of the 'debug' option.</p>
 * </blockquote></li>
 * <li>"{@link #getDocumentLocationOptionLongName documentLocationOptionLongName}"
 * <blockquote>Property of type {@code java.lang.String}.
 * <p>Long name of the 'document-location' option.</p>
 * </blockquote></li>
 * <li>"{@link #getDocumentLocationOptionShortName documentLocationOptionShortName}"
 * <blockquote>Property of type {@code java.lang.String}.
 * <p>Name of the 'document-location' option.</p>
 * </blockquote></li>
 * <li>"{@link #getDocumentsOptionLongName documentsOptionLongName}"
 * <blockquote>Property of type {@code java.lang.String}.
 * <p>Long name of the 'documents' option.</p>
 * </blockquote></li>
 * <li>"{@link #getDocumentsOptionShortName documentsOptionShortName}"
 * <blockquote>Property of type {@code java.lang.String}.
 * <p>Name of the 'documents' option.</p>
 * </blockquote></li>
 * <li>"{@link #getFailOnWarningsOptionLongName failOnWarningsOptionLongName}"
 * <blockquote>Property of type {@code java.lang.String}.
 * <p>Long name of the 'fail-on-warnings' option.</p>
 * </blockquote></li>
 * <li>"{@link #getFailOnWarningsOptionShortName failOnWarningsOptionShortName}"
 * <blockquote>Property of type {@code java.lang.String}.
 * <p>Name of the 'fail-on-warnings' option.</p>
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
 * <li>"{@link #getVerboseOptionLongName verboseOptionLongName}"
 * <blockquote>Property of type {@code java.lang.String}.
 * <p>Long name of the 'verbose' option.</p>
 * </blockquote></li>
 * <li>"{@link #getVerboseOptionShortName verboseOptionShortName}"
 * <blockquote>Property of type {@code java.lang.String}.
 * <p>Name of the 'verbose' option.</p>
 * </blockquote></li>
 * </ul></p>
 * <p><b>Dependencies</b><ul>
 * <li>"{@link #getLocale Locale}"<blockquote>
 * Dependency on {@code java.util.Locale} at specification level 1.1 bound to an instance.</blockquote></li>
 * </ul></p>
 * <p><b>Messages</b><ul>
 * <li>"{@link #getApplicationTitleMessage applicationTitle}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>JOMC Version 1.0-alpha-3-SNAPSHOT Build 2009-10-02T17:08:04+0000</pre></td></tr>
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
 * <li>"{@link #getDocumentsOptionMessage documentsOption}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>Document filenames separated by '':''. If starting with a ''@'' character, a file name of a file holding document filenames.</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Dokument-Dateinamen mit '':'' getrennt. Wenn mit ''@'' beginnend, Dateiname einer Textdatei mit Dokument-Dateinamen.</pre></td></tr>
 * </table>
 * <li>"{@link #getDocumentsOptionArgNameMessage documentsOptionArgName}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>files</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Dateien</pre></td></tr>
 * </table>
 * <li>"{@link #getFailOnWarningsOptionMessage failOnWarningsOption}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>Exit with failure on warnings.</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Bei Warnungen Fehler melden.</pre></td></tr>
 * </table>
 * <li>"{@link #getLongDescriptionMessage longDescription}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre></pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre></pre></td></tr>
 * </table>
 * <li>"{@link #getMissingModuleMessage missingModule}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>Module ''{0}'' not found.</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Modul ''{0}'' nicht gefunden.</pre></td></tr>
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
 * <li>"{@link #getSeparatorMessage separator}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>--------------------------------------------------------------------------------</pre></td></tr>
 * </table>
 * <li>"{@link #getShortDescriptionMessage shortDescription}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>Does nothing.</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>F&uuml;hrt nichts aus.</pre></td></tr>
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
 * <li>"{@link #getVerboseOptionMessage verboseOption}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>Enables verbose output.</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Aktiviert ausf&uuml;hrliche Ausgaben.</pre></td></tr>
 * </table>
 * </ul></p>
 *
 * @author <a href="mailto:cs@jomc.org">Christian Schulte</a> 1.0
 * @version $Id$
 */
// SECTION-END
// SECTION-START[Annotations]
@javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                             comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-3-SNAPSHOT/jomc-tools" )
// SECTION-END
public abstract class AbstractJomcCommand implements Command
{
    // SECTION-START[AbstractJomcCommand]

    /** 'verbose' option of the instance. */
    private Option verboseOption;

    /** 'debug' option of the instance. */
    private Option debugOption;

    /** 'classpath' option of the instance. */
    private Option classpathOption;

    /** 'documents' option of the instance. */
    private Option documentsOption;

    /** 'document-location' option of the instance. */
    private Option documentLocationOption;

    /** 'module-name' option of the instance. */
    private Option moduleNameOption;

    /** 'fail-on-warnings' option of the instance. */
    private Option failOnWarningsOption;

    /** 'no-classpath-resolution' option of the instance. */
    private Option noClasspathResolutionOption;

    /** Greatest severity logged by the command. */
    private Level severity = Level.ALL;

    /** The {@code ModelManager} of the instance. */
    private ModelManager modelManager;

    /** The {@code JavaBundles} tool of the instance. */
    private JavaBundles javaBundles;

    /** The {@code JavaClasses} tool of the instance. */
    private JavaClasses javaClasses;

    /** The {@code JavaSources} tool of the instance. */
    private JavaSources javaSources;

    public ModelManager getModelManager()
    {
        if ( this.modelManager == null )
        {
            this.modelManager = new DefaultModelManager();
        }

        return this.modelManager;
    }

    public JavaBundles getJavaBundles()
    {
        if ( this.javaBundles == null )
        {
            return new JavaBundles();
        }

        return this.javaBundles;
    }

    public JavaClasses getJavaClasses()
    {
        if ( this.javaClasses == null )
        {
            return new JavaClasses();
        }

        return this.javaClasses;
    }

    public JavaSources getJavaSources()
    {
        if ( this.javaSources == null )
        {
            return new JavaSources();
        }

        return this.javaSources;
    }

    public Option getVerboseOption()
    {
        if ( this.verboseOption == null )
        {
            this.verboseOption = new Option( this.getVerboseOptionShortName(), this.getVerboseOptionLongName(),
                                             false, this.getVerboseOptionMessage( this.getLocale() ) );

        }

        return this.verboseOption;
    }

    public Option getDebugOption()
    {
        if ( this.debugOption == null )
        {
            this.debugOption = new Option( this.getDebugOptionShortName(), this.getDebugOptionLongName(),
                                           false, this.getDebugOptionMessage( this.getLocale() ) );

        }

        return this.debugOption;
    }

    public Option getClasspathOption()
    {
        if ( this.classpathOption == null )
        {
            this.classpathOption = new Option( this.getClasspathOptionShortName(), this.getClasspathOptionLongName(),
                                               true, this.getClasspathOptionMessage( this.getLocale() ) );

            this.classpathOption.setArgs( Option.UNLIMITED_VALUES );
            this.classpathOption.setValueSeparator( ':' );
            this.classpathOption.setArgName( this.getClasspathOptionArgNameMessage( this.getLocale() ) );
        }

        return this.classpathOption;
    }

    public Option getDocumentsOption()
    {
        if ( this.documentsOption == null )
        {
            this.documentsOption = new Option( this.getDocumentsOptionShortName(), this.getDocumentsOptionLongName(),
                                               true, this.getDocumentsOptionMessage( this.getLocale() ) );

            this.documentsOption.setArgs( Option.UNLIMITED_VALUES );
            this.documentsOption.setValueSeparator( ':' );
            this.documentsOption.setArgName( this.getDocumentsOptionArgNameMessage( this.getLocale() ) );
        }

        return this.documentsOption;
    }

    public Option getDocumentLocationOption()
    {
        if ( this.documentLocationOption == null )
        {
            this.documentLocationOption = new Option( this.getDocumentLocationOptionShortName(),
                                                      this.getDocumentLocationOptionLongName(), true,
                                                      this.getDocumentLocationOptionMessage( this.getLocale() ) );

            this.documentLocationOption.setArgName( this.getDocumentLocationOptionArgNameMessage( this.getLocale() ) );
        }

        return this.documentLocationOption;
    }

    public Option getModuleNameOption()
    {
        if ( this.moduleNameOption == null )
        {
            this.moduleNameOption = new Option( this.getModuleNameOptionShortName(),
                                                this.getModuleNameOptionLongName(),
                                                true, this.getModuleNameOptionMessage( this.getLocale() ) );

            this.moduleNameOption.setArgName( this.getModuleNameOptionArgNameMessage( this.getLocale() ) );
        }

        return this.moduleNameOption;
    }

    public Option getFailOnWarningsOption()
    {
        if ( this.failOnWarningsOption == null )
        {
            this.failOnWarningsOption = new Option( this.getFailOnWarningsOptionShortName(),
                                                    this.getFailOnWarningsOptionLongName(),
                                                    false, this.getFailOnWarningsOptionMessage( this.getLocale() ) );

        }

        return this.failOnWarningsOption;
    }

    public Option getNoClasspathResolutionOption()
    {
        if ( this.noClasspathResolutionOption == null )
        {
            this.noClasspathResolutionOption = new Option(
                this.getNoClasspathResolutionOptionShortName(), this.getNoClasspathResolutionOptionLongName(),
                false, this.getNoClasspathResolutionOptionMessage( this.getLocale() ) );

        }

        return this.noClasspathResolutionOption;
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
        options.addOption( this.getDebugOption() );
        options.addOption( this.getVerboseOption() );
        options.addOption( this.getClasspathOption() );
        options.addOption( this.getDocumentsOption() );
        options.addOption( this.getDocumentLocationOption() );
        options.addOption( this.getModuleNameOption() );
        options.addOption( this.getFailOnWarningsOption() );
        options.addOption( this.getNoClasspathResolutionOption() );
        return options;
    }

    public final int execute( final CommandLine commandLine, final PrintStream printStream )
    {
        final boolean debug = commandLine.hasOption( this.getDebugOption().getOpt() );
        final boolean verbose = commandLine.hasOption( this.getVerboseOption().getOpt() );
        final boolean failOnWarnings = commandLine.hasOption( this.getFailOnWarningsOption().getOpt() );

        this.log( Level.INFO, this.getSeparatorMessage( this.getLocale() ), null, printStream, verbose, debug );
        this.log( Level.INFO, this.getApplicationTitleMessage( this.getLocale() ), null, printStream, verbose, debug );
        this.log( Level.INFO, this.getSeparatorMessage( this.getLocale() ), null, printStream, verbose, debug );

        int status = this.executeCommand( commandLine, printStream );

        if ( status == Command.STATUS_SUCCESS && failOnWarnings &&
             this.severity.intValue() >= Level.WARNING.intValue() )
        {
            status = Command.STATUS_FAILURE;
        }

        if ( status == Command.STATUS_SUCCESS )
        {
            this.log( Level.INFO, this.getToolSuccessMessage( this.getLocale(), this.getCommandName() ), null,
                      printStream, verbose, debug );

        }
        else
        {
            this.log( Level.INFO, this.getToolFailureMessage( this.getLocale(), this.getCommandName() ), null,
                      printStream, verbose, debug );

        }

        this.log( Level.INFO, this.getSeparatorMessage( this.getLocale() ), null, printStream, verbose, debug );

        return status;
    }

    protected abstract int executeCommand( final CommandLine commandLine, final PrintStream printStream );

    protected ClassLoader getClassLoader( final CommandLine commandLine, final PrintStream printStream )
        throws IOException
    {
        final boolean debug = commandLine.hasOption( this.getDebugOption().getOpt() );
        final boolean verbose = commandLine.hasOption( this.getVerboseOption().getOpt() );
        final Set<URL> urls = new HashSet<URL>();

        if ( commandLine.hasOption( this.getClasspathOption().getOpt() ) )
        {
            final String[] elements = commandLine.getOptionValues( this.getClasspathOption().getOpt() );
            if ( elements != null )
            {
                for ( String e : elements )
                {
                    this.log( Level.FINE, this.getClasspathElementMessage( this.getLocale(), e ), null, printStream,
                              verbose, debug );

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
                                    final URL url = new File( line ).toURI().toURL();
                                    urls.add( url );
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
                        urls.add( new File( e ).toURI().toURL() );
                    }
                }
            }
        }

        return new URLClassLoader( urls.toArray( new URL[ urls.size() ] ) );
    }

    protected Set<File> getDocumentFiles( final CommandLine commandLine, final PrintStream printStream )
        throws IOException
    {
        final boolean debug = commandLine.hasOption( this.getDebugOption().getOpt() );
        final boolean verbose = commandLine.hasOption( this.getVerboseOption().getOpt() );
        final Set<File> files = new HashSet<File>();

        if ( commandLine.hasOption( this.getDocumentsOption().getOpt() ) )
        {
            final String[] elements = commandLine.getOptionValues( this.getDocumentsOption().getOpt() );
            if ( elements != null )
            {
                for ( String e : elements )
                {
                    this.log( Level.FINE, this.getDocumentFileMessage( this.getLocale(), e ), null, printStream,
                              verbose, debug );

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

    protected Modules getModules( final ModelManager manager, final CommandLine commandLine,
                                  final PrintStream printStream, final boolean includeClasspathModule,
                                  final boolean strictValidation )
        throws IOException, SAXException, JAXBException, ModelException
    {
        final ClassLoader classLoader = this.getClassLoader( commandLine, printStream );
        final boolean verbose = commandLine.hasOption( getVerboseOption().getOpt() );
        final boolean debug = commandLine.hasOption( getDebugOption().getOpt() );
        final Modules modules = new Modules();
        Modules modulesToValidate = null;

        DefaultModelManager defaultModelManager = null;
        if ( manager instanceof DefaultModelManager )
        {
            defaultModelManager = (DefaultModelManager) manager;
            defaultModelManager.getListeners().add( new DefaultModelManager.Listener()
            {

                public void onLog( final Level level, final String message, final Throwable t )
                {
                    log( level, message, t, printStream, verbose, debug );
                }

            } );

            defaultModelManager.setClassLoader( classLoader );
        }

        if ( commandLine.hasOption( this.getDocumentsOption().getOpt() ) )
        {
            final Unmarshaller u = manager.getUnmarshaller( false );
            for ( File f : this.getDocumentFiles( commandLine, printStream ) )
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
                else
                {
                    this.log( Level.WARNING, this.getCannotProcessMessage(
                        this.getLocale(), f.getAbsolutePath(), o.toString() ), null, printStream, verbose, debug );

                }
            }

            modulesToValidate = modules;
        }

        if ( defaultModelManager != null )
        {
            if ( commandLine.hasOption( this.getClasspathOption().getOpt() ) )
            {
                final Modules classpathModules;
                if ( commandLine.hasOption( this.getDocumentLocationOption().getOpt() ) )
                {
                    classpathModules = defaultModelManager.getClasspathModules(
                        commandLine.getOptionValue( this.getDocumentLocationOption().getOpt() ) );

                }
                else
                {
                    classpathModules = defaultModelManager.getClasspathModules(
                        defaultModelManager.getDefaultDocumentLocation() );

                }

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
                final Module classpathModule = defaultModelManager.getClasspathModule( modules );
                if ( classpathModule != null )
                {
                    if ( includeClasspathModule )
                    {
                        modules.getModule().add( classpathModule );
                    }
                    else
                    {
                        modulesToValidate = new Modules( modules );
                        modulesToValidate.getModule().add( classpathModule );
                    }
                }
            }
        }

        if ( modulesToValidate != null )
        {
            if ( strictValidation )
            {
                manager.validateModules( modulesToValidate );
            }
            else
            {
                manager.validateModelObject( manager.getObjectFactory().createModules( modulesToValidate ) );
            }
        }

        this.log( Level.FINE, this.getModulesReportMessage( this.getLocale() ), null, printStream, verbose, debug );

        for ( Module m : modules.getModule() )
        {
            this.log( Level.FINE, "\t" + m.getName(), null, printStream, verbose, debug );
        }

        return modules;
    }

    protected void configureTool( final JomcTool tool, final CommandLine commandLine, final PrintStream printStream,
                                  final boolean includeClasspathModule )
        throws IOException, SAXException, JAXBException, ModelException
    {
        final boolean verbose = commandLine.hasOption( getVerboseOption().getOpt() );
        final boolean debug = commandLine.hasOption( getDebugOption().getOpt() );

        tool.getListeners().add( new JomcTool.Listener()
        {

            public void onLog( final Level level, final String message, final Throwable throwable )
            {
                log( level, message, throwable, printStream, verbose, debug );
            }

        } );

        tool.setModules( this.getModules(
            tool.getModelManager(), commandLine, printStream, includeClasspathModule, false ) );

    }

    protected String getLoglines( final Level level, final String text )
    {
        try
        {
            String logLines = null;

            if ( text != null )
            {
                final StringBuilder lines = new StringBuilder();
                final BufferedReader reader = new BufferedReader( new StringReader( text ) );

                String line;
                while ( ( line = reader.readLine() ) != null )
                {
                    lines.append( "[" ).append( level.getLocalizedName() ).append( "] " );
                    lines.append( line ).append( System.getProperty( "line.separator" ) );
                }

                logLines = lines.toString();
            }

            return logLines;
        }
        catch ( final IOException e )
        {
            throw new AssertionError( e );
        }
    }

    protected void log( final Level level, final String message, final Throwable throwable,
                        final PrintStream printStream, final boolean verbose, final boolean debug )
    {
        Level logLevel = Level.WARNING;
        if ( verbose )
        {
            logLevel = Level.INFO;
        }
        if ( debug )
        {
            logLevel = Level.FINEST;
        }

        if ( level.intValue() >= logLevel.intValue() )
        {
            if ( message != null )
            {
                printStream.print( this.getLoglines( level, message ) );
            }

            if ( throwable != null && debug )
            {
                final StringWriter stackTrace = new StringWriter();
                final PrintWriter pw = new PrintWriter( stackTrace );
                throwable.printStackTrace( pw );
                pw.flush();
                printStream.print( this.getLoglines( level, stackTrace.toString() ) );
            }
        }

        if ( level.intValue() > this.severity.intValue() )
        {
            this.severity = level;
        }
    }

    // SECTION-END
    // SECTION-START[Constructors]

    /** Creates a new {@code AbstractJomcCommand} instance. */
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-3-SNAPSHOT/jomc-tools" )
    public AbstractJomcCommand()
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
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-3-SNAPSHOT/jomc-tools" )
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
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-3-SNAPSHOT/jomc-tools" )
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
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-3-SNAPSHOT/jomc-tools" )
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
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-3-SNAPSHOT/jomc-tools" )
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
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-3-SNAPSHOT/jomc-tools" )
    private java.lang.String getCommandName()
    {
        final java.lang.String _p = (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager().getProperty( this, "commandName" );
        assert _p != null : "'commandName' property not found.";
        return _p;
    }

    /**
     * Gets the value of the {@code debugOptionLongName} property.
     * @return Long name of the 'debug' option.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-3-SNAPSHOT/jomc-tools" )
    private java.lang.String getDebugOptionLongName()
    {
        final java.lang.String _p = (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager().getProperty( this, "debugOptionLongName" );
        assert _p != null : "'debugOptionLongName' property not found.";
        return _p;
    }

    /**
     * Gets the value of the {@code debugOptionShortName} property.
     * @return Name of the 'debug' option.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-3-SNAPSHOT/jomc-tools" )
    private java.lang.String getDebugOptionShortName()
    {
        final java.lang.String _p = (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager().getProperty( this, "debugOptionShortName" );
        assert _p != null : "'debugOptionShortName' property not found.";
        return _p;
    }

    /**
     * Gets the value of the {@code documentLocationOptionLongName} property.
     * @return Long name of the 'document-location' option.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-3-SNAPSHOT/jomc-tools" )
    private java.lang.String getDocumentLocationOptionLongName()
    {
        final java.lang.String _p = (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager().getProperty( this, "documentLocationOptionLongName" );
        assert _p != null : "'documentLocationOptionLongName' property not found.";
        return _p;
    }

    /**
     * Gets the value of the {@code documentLocationOptionShortName} property.
     * @return Name of the 'document-location' option.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-3-SNAPSHOT/jomc-tools" )
    private java.lang.String getDocumentLocationOptionShortName()
    {
        final java.lang.String _p = (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager().getProperty( this, "documentLocationOptionShortName" );
        assert _p != null : "'documentLocationOptionShortName' property not found.";
        return _p;
    }

    /**
     * Gets the value of the {@code documentsOptionLongName} property.
     * @return Long name of the 'documents' option.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-3-SNAPSHOT/jomc-tools" )
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
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-3-SNAPSHOT/jomc-tools" )
    private java.lang.String getDocumentsOptionShortName()
    {
        final java.lang.String _p = (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager().getProperty( this, "documentsOptionShortName" );
        assert _p != null : "'documentsOptionShortName' property not found.";
        return _p;
    }

    /**
     * Gets the value of the {@code failOnWarningsOptionLongName} property.
     * @return Long name of the 'fail-on-warnings' option.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-3-SNAPSHOT/jomc-tools" )
    private java.lang.String getFailOnWarningsOptionLongName()
    {
        final java.lang.String _p = (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager().getProperty( this, "failOnWarningsOptionLongName" );
        assert _p != null : "'failOnWarningsOptionLongName' property not found.";
        return _p;
    }

    /**
     * Gets the value of the {@code failOnWarningsOptionShortName} property.
     * @return Name of the 'fail-on-warnings' option.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-3-SNAPSHOT/jomc-tools" )
    private java.lang.String getFailOnWarningsOptionShortName()
    {
        final java.lang.String _p = (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager().getProperty( this, "failOnWarningsOptionShortName" );
        assert _p != null : "'failOnWarningsOptionShortName' property not found.";
        return _p;
    }

    /**
     * Gets the value of the {@code moduleNameOptionLongName} property.
     * @return Long name of the 'module' option.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-3-SNAPSHOT/jomc-tools" )
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
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-3-SNAPSHOT/jomc-tools" )
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
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-3-SNAPSHOT/jomc-tools" )
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
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-3-SNAPSHOT/jomc-tools" )
    private java.lang.String getNoClasspathResolutionOptionShortName()
    {
        final java.lang.String _p = (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager().getProperty( this, "noClasspathResolutionOptionShortName" );
        assert _p != null : "'noClasspathResolutionOptionShortName' property not found.";
        return _p;
    }

    /**
     * Gets the value of the {@code verboseOptionLongName} property.
     * @return Long name of the 'verbose' option.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-3-SNAPSHOT/jomc-tools" )
    private java.lang.String getVerboseOptionLongName()
    {
        final java.lang.String _p = (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager().getProperty( this, "verboseOptionLongName" );
        assert _p != null : "'verboseOptionLongName' property not found.";
        return _p;
    }

    /**
     * Gets the value of the {@code verboseOptionShortName} property.
     * @return Name of the 'verbose' option.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-3-SNAPSHOT/jomc-tools" )
    private java.lang.String getVerboseOptionShortName()
    {
        final java.lang.String _p = (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager().getProperty( this, "verboseOptionShortName" );
        assert _p != null : "'verboseOptionShortName' property not found.";
        return _p;
    }
    // SECTION-END
    // SECTION-START[Messages]

    /**
     * Gets the text of the {@code applicationTitle} message.
     * <p><b>Templates</b><br/><table>
     * <tr><td valign="top">English:</td><td valign="top"><pre>JOMC Version 1.0-alpha-3-SNAPSHOT Build 2009-10-02T17:08:04+0000</pre></td></tr>
     * </table></p>
     * @param locale The locale of the message to return.
     * @return The text of the {@code applicationTitle} message.
     *
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-3-SNAPSHOT/jomc-tools" )
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
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-3-SNAPSHOT/jomc-tools" )
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
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-3-SNAPSHOT/jomc-tools" )
    private String getClasspathElementMessage( final java.util.Locale locale, final java.lang.String classpathElement )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager().getMessage( this, "classpathElement", locale, new Object[] { classpathElement, null } );
        assert _m != null : "'classpathElement' message not found.";
        return _m;
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
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-3-SNAPSHOT/jomc-tools" )
    private String getClasspathOptionMessage( final java.util.Locale locale )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager().getMessage( this, "classpathOption", locale,  null );
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
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-3-SNAPSHOT/jomc-tools" )
    private String getClasspathOptionArgNameMessage( final java.util.Locale locale )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager().getMessage( this, "classpathOptionArgName", locale,  null );
        assert _m != null : "'classpathOptionArgName' message not found.";
        return _m;
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
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-3-SNAPSHOT/jomc-tools" )
    private String getDebugOptionMessage( final java.util.Locale locale )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager().getMessage( this, "debugOption", locale,  null );
        assert _m != null : "'debugOption' message not found.";
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
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-3-SNAPSHOT/jomc-tools" )
    private String getDocumentFileMessage( final java.util.Locale locale, final java.lang.String documentFile )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager().getMessage( this, "documentFile", locale, new Object[] { documentFile, null } );
        assert _m != null : "'documentFile' message not found.";
        return _m;
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
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-3-SNAPSHOT/jomc-tools" )
    private String getDocumentLocationOptionMessage( final java.util.Locale locale )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager().getMessage( this, "documentLocationOption", locale,  null );
        assert _m != null : "'documentLocationOption' message not found.";
        return _m;
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
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-3-SNAPSHOT/jomc-tools" )
    private String getDocumentLocationOptionArgNameMessage( final java.util.Locale locale )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager().getMessage( this, "documentLocationOptionArgName", locale,  null );
        assert _m != null : "'documentLocationOptionArgName' message not found.";
        return _m;
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
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-3-SNAPSHOT/jomc-tools" )
    private String getDocumentsOptionMessage( final java.util.Locale locale )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager().getMessage( this, "documentsOption", locale,  null );
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
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-3-SNAPSHOT/jomc-tools" )
    private String getDocumentsOptionArgNameMessage( final java.util.Locale locale )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager().getMessage( this, "documentsOptionArgName", locale,  null );
        assert _m != null : "'documentsOptionArgName' message not found.";
        return _m;
    }

    /**
     * Gets the text of the {@code failOnWarningsOption} message.
     * <p><b>Templates</b><br/><table>
     * <tr><td valign="top">English:</td><td valign="top"><pre>Exit with failure on warnings.</pre></td></tr>
     * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Bei Warnungen Fehler melden.</pre></td></tr>
     * </table></p>
     * @param locale The locale of the message to return.
     * @return The text of the {@code failOnWarningsOption} message.
     *
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-3-SNAPSHOT/jomc-tools" )
    private String getFailOnWarningsOptionMessage( final java.util.Locale locale )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager().getMessage( this, "failOnWarningsOption", locale,  null );
        assert _m != null : "'failOnWarningsOption' message not found.";
        return _m;
    }

    /**
     * Gets the text of the {@code longDescription} message.
     * <p><b>Templates</b><br/><table>
     * <tr><td valign="top">English:</td><td valign="top"><pre></pre></td></tr>
     * <tr><td valign="top">Deutsch:</td><td valign="top"><pre></pre></td></tr>
     * </table></p>
     * @param locale The locale of the message to return.
     * @return The text of the {@code longDescription} message.
     *
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-3-SNAPSHOT/jomc-tools" )
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
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-3-SNAPSHOT/jomc-tools" )
    private String getMissingModuleMessage( final java.util.Locale locale, final java.lang.String moduleName )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager().getMessage( this, "missingModule", locale, new Object[] { moduleName, null } );
        assert _m != null : "'missingModule' message not found.";
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
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-3-SNAPSHOT/jomc-tools" )
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
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-3-SNAPSHOT/jomc-tools" )
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
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-3-SNAPSHOT/jomc-tools" )
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
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-3-SNAPSHOT/jomc-tools" )
    private String getNoClasspathResolutionOptionMessage( final java.util.Locale locale )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager().getMessage( this, "noClasspathResolutionOption", locale,  null );
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
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-3-SNAPSHOT/jomc-tools" )
    private String getSeparatorMessage( final java.util.Locale locale )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager().getMessage( this, "separator", locale,  null );
        assert _m != null : "'separator' message not found.";
        return _m;
    }

    /**
     * Gets the text of the {@code shortDescription} message.
     * <p><b>Templates</b><br/><table>
     * <tr><td valign="top">English:</td><td valign="top"><pre>Does nothing.</pre></td></tr>
     * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>F&uuml;hrt nichts aus.</pre></td></tr>
     * </table></p>
     * @param locale The locale of the message to return.
     * @return The text of the {@code shortDescription} message.
     *
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-3-SNAPSHOT/jomc-tools" )
    private String getShortDescriptionMessage( final java.util.Locale locale )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager().getMessage( this, "shortDescription", locale,  null );
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
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-3-SNAPSHOT/jomc-tools" )
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
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-3-SNAPSHOT/jomc-tools" )
    private String getStartingProcessingMessage( final java.util.Locale locale, final java.lang.String toolName )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager().getMessage( this, "startingProcessing", locale, new Object[] { toolName, null } );
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
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-3-SNAPSHOT/jomc-tools" )
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
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-3-SNAPSHOT/jomc-tools" )
    private String getToolSuccessMessage( final java.util.Locale locale, final java.lang.String toolName )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager().getMessage( this, "toolSuccess", locale, new Object[] { toolName, null } );
        assert _m != null : "'toolSuccess' message not found.";
        return _m;
    }

    /**
     * Gets the text of the {@code verboseOption} message.
     * <p><b>Templates</b><br/><table>
     * <tr><td valign="top">English:</td><td valign="top"><pre>Enables verbose output.</pre></td></tr>
     * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Aktiviert ausf&uuml;hrliche Ausgaben.</pre></td></tr>
     * </table></p>
     * @param locale The locale of the message to return.
     * @return The text of the {@code verboseOption} message.
     *
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.JavaSources",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-3-SNAPSHOT/jomc-tools" )
    private String getVerboseOptionMessage( final java.util.Locale locale )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager().getMessage( this, "verboseOption", locale,  null );
        assert _m != null : "'verboseOption' message not found.";
        return _m;
    }
    // SECTION-END
}
