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
import org.jomc.model.DefaultModelManager;
import org.jomc.model.ModelException;
import org.jomc.model.Module;
import org.jomc.model.Modules;
import org.jomc.tools.JomcTool;
import org.jomc.cli.Command;
import org.jomc.model.ModelManager;
import org.xml.sax.SAXException;

// SECTION-START[Documentation]
/**
 * Base JOMC {@code Command} implementation.
 * <p><b>Properties</b><ul>
 * <li>"{@link #getAbbreviatedCommandName abbreviatedCommandName}"<blockquote>
 * Property of type {@code java.lang.String} with value "noop".</blockquote></li>
 * <li>"{@link #getClasspathOptionLongName classpathOptionLongName}"<blockquote>
 * Property of type {@code java.lang.String} with value "classpath".</blockquote></li>
 * <li>"{@link #getClasspathOptionShortName classpathOptionShortName}"<blockquote>
 * Property of type {@code java.lang.String} with value "cp".</blockquote></li>
 * <li>"{@link #getCommandName commandName}"<blockquote>
 * Property of type {@code java.lang.String} with value "no-operation".</blockquote></li>
 * <li>"{@link #getDebugOptionLongName debugOptionLongName}"<blockquote>
 * Property of type {@code java.lang.String} with value "debug".</blockquote></li>
 * <li>"{@link #getDebugOptionShortName debugOptionShortName}"<blockquote>
 * Property of type {@code java.lang.String} with value "D".</blockquote></li>
 * <li>"{@link #getDocumentLocationOptionLongName documentLocationOptionLongName}"<blockquote>
 * Property of type {@code java.lang.String} with value "document-location".</blockquote></li>
 * <li>"{@link #getDocumentLocationOptionShortName documentLocationOptionShortName}"<blockquote>
 * Property of type {@code java.lang.String} with value "dl".</blockquote></li>
 * <li>"{@link #getDocumentsOptionLongName documentsOptionLongName}"<blockquote>
 * Property of type {@code java.lang.String} with value "documents".</blockquote></li>
 * <li>"{@link #getDocumentsOptionShortName documentsOptionShortName}"<blockquote>
 * Property of type {@code java.lang.String} with value "df".</blockquote></li>
 * <li>"{@link #getFailOnWarningsOptionLongName failOnWarningsOptionLongName}"<blockquote>
 * Property of type {@code java.lang.String} with value "fail-on-warnings".</blockquote></li>
 * <li>"{@link #getFailOnWarningsOptionShortName failOnWarningsOptionShortName}"<blockquote>
 * Property of type {@code java.lang.String} with value "fw".</blockquote></li>
 * <li>"{@link #getModuleNameOptionLongName moduleNameOptionLongName}"<blockquote>
 * Property of type {@code java.lang.String} with value "module".</blockquote></li>
 * <li>"{@link #getModuleNameOptionShortName moduleNameOptionShortName}"<blockquote>
 * Property of type {@code java.lang.String} with value "mn".</blockquote></li>
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
 * <tr><td valign="top">English:</td><td valign="top"><pre>JOMC Version 1.0-alpha-1-SNAPSHOT Build 2009-08-25T13:38:15+0000</pre></td></tr>
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
 * <li>"{@link #getDescriptionMessage description}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>Does nothing.</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Führt nichts aus.</pre></td></tr>
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
 * <li>"{@link #getSeparatorMessage separator}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>--------------------------------------------------------------------------------</pre></td></tr>
 * </table>
 * <li>"{@link #getStartingModuleProcessingMessage startingModuleProcessing}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>Executing command {0} with module ''{1}'' ...</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Führt Befehl {0} mit Modul ''{1}'' aus ... </pre></td></tr>
 * </table>
 * <li>"{@link #getStartingProcessingMessage startingProcessing}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>Executing command {0} ...</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Führt Befehl {0} aus ... </pre></td></tr>
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
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Aktiviert ausführliche Ausgaben.</pre></td></tr>
 * </table>
 * </ul></p>
 *
 * @author <a href="mailto:cs@jomc.org">Christian Schulte</a> 1.0
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

    /** Greatest severity logged by the command. */
    private Level severity = Level.ALL;

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

    public String getName()
    {
        return this.getCommandName();
    }

    public String getAbbreviatedName()
    {
        return this.getAbbreviatedCommandName();
    }

    public String getDescription( final Locale locale )
    {
        return this.getDescriptionMessage( locale );
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

        if ( status == STATUS_OK && failOnWarnings && this.severity.intValue() >= Level.WARNING.intValue() )
        {
            status = STATUS_FAILURE;
        }

        if ( status == STATUS_OK )
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
                        final BufferedReader reader = new BufferedReader( new FileReader( file ) );
                        while ( ( line = reader.readLine() ) != null )
                        {
                            if ( !line.startsWith( "#" ) )
                            {
                                final URL url = new File( line ).toURI().toURL();
                                urls.add( url );
                            }
                        }
                        reader.close();
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
                        final BufferedReader reader = new BufferedReader( new FileReader( file ) );
                        while ( ( line = reader.readLine() ) != null )
                        {
                            if ( !line.startsWith( "#" ) )
                            {
                                files.add( new File( line ) );
                            }
                        }
                        reader.close();
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

    protected Modules getModules( final ModelManager modelManager, final CommandLine commandLine,
                                  final PrintStream printStream, final boolean includeClasspathModule )
        throws IOException, SAXException, JAXBException, ModelException
    {
        final ClassLoader classLoader = this.getClassLoader( commandLine, printStream );
        final boolean verbose = commandLine.hasOption( getVerboseOption().getOpt() );
        final boolean debug = commandLine.hasOption( getDebugOption().getOpt() );
        Modules modules = new Modules();
        Modules modulesToValidate = null;

        DefaultModelManager defaultModelManager = null;
        if ( modelManager instanceof DefaultModelManager )
        {
            defaultModelManager = (DefaultModelManager) modelManager;
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
            final Unmarshaller u = modelManager.getUnmarshaller( false );
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

        if ( commandLine.hasOption( this.getClasspathOption().getOpt() ) && defaultModelManager != null )
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

            final Modules classpathModulesWithoutClasspathModule = new Modules( classpathModules );

            classpathModules.getModule().addAll( modules.getModule() );
            classpathModulesWithoutClasspathModule.getModule().addAll( modules.getModule() );

            final Module classpathModule = defaultModelManager.getClasspathModule( classpathModules );
            if ( classpathModule != null )
            {
                classpathModules.getModule().add( classpathModule );
            }

            modulesToValidate = classpathModules;
            modules = includeClasspathModule ? classpathModules : classpathModulesWithoutClasspathModule;
        }

        if ( modulesToValidate != null )
        {
            modelManager.validateModelObject( modelManager.getObjectFactory().createModules( modulesToValidate ) );
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

            @Override
            public void onLog( final Level level, final String message, final Throwable throwable )
            {
                log( level, message, throwable, printStream, verbose, debug );
            }

        } );

        tool.setModules( this.getModules( tool.getModelManager(), commandLine, printStream, includeClasspathModule ) );
    }

    protected String getLoglines( final Level level, final String text )
    {
        try
        {
            String logLines = null;

            if ( text != null )
            {
                final StringBuffer lines = new StringBuffer();
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
        catch ( IOException e )
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
    @javax.annotation.Generated
    (
        value = "org.jomc.tools.JavaSources",
        comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-1-SNAPSHOT/jomc-tools"
    )
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
     * Gets the value of the {@code failOnWarningsOptionLongName} property.
     * @return Long name of the 'fail-on-warnings' option.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated
    (
        value = "org.jomc.tools.JavaSources",
        comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-1-SNAPSHOT/jomc-tools"
    )
    private java.lang.String getFailOnWarningsOptionLongName() throws org.jomc.ObjectManagementException
    {
        return (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager().getProperty( this, "failOnWarningsOptionLongName" );
    }

    /**
     * Gets the value of the {@code failOnWarningsOptionShortName} property.
     * @return Name of the 'fail-on-warnings' option.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated
    (
        value = "org.jomc.tools.JavaSources",
        comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-1-SNAPSHOT/jomc-tools"
    )
    private java.lang.String getFailOnWarningsOptionShortName() throws org.jomc.ObjectManagementException
    {
        return (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager().getProperty( this, "failOnWarningsOptionShortName" );
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
     * <tr><td valign="top">English:</td><td valign="top"><pre>JOMC Version 1.0-alpha-1-SNAPSHOT Build 2009-08-25T13:38:15+0000</pre></td></tr>
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
    @javax.annotation.Generated
    (
        value = "org.jomc.tools.JavaSources",
        comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-1-SNAPSHOT/jomc-tools"
    )
    private String getCannotProcessMessage( final java.util.Locale locale, final java.lang.String itemInfo, final java.lang.String detailMessage ) throws org.jomc.ObjectManagementException
    {
        return org.jomc.ObjectManagerFactory.getObjectManager().getMessage( this, "cannotProcess", locale, new Object[] { itemInfo, detailMessage, null } );
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
     * <tr><td valign="top">English:</td><td valign="top"><pre>Does nothing.</pre></td></tr>
     * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Führt nichts aus.</pre></td></tr>
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
    @javax.annotation.Generated
    (
        value = "org.jomc.tools.JavaSources",
        comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-1-SNAPSHOT/jomc-tools"
    )
    private String getFailOnWarningsOptionMessage( final java.util.Locale locale ) throws org.jomc.ObjectManagementException
    {
        return org.jomc.ObjectManagerFactory.getObjectManager().getMessage( this, "failOnWarningsOption", locale,  null );
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
    @javax.annotation.Generated
    (
        value = "org.jomc.tools.JavaSources",
        comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-1-SNAPSHOT/jomc-tools"
    )
    private String getMissingModuleMessage( final java.util.Locale locale, final java.lang.String moduleName ) throws org.jomc.ObjectManagementException
    {
        return org.jomc.ObjectManagerFactory.getObjectManager().getMessage( this, "missingModule", locale, new Object[] { moduleName, null } );
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
    @javax.annotation.Generated
    (
        value = "org.jomc.tools.JavaSources",
        comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-1-SNAPSHOT/jomc-tools"
    )
    private String getModulesReportMessage( final java.util.Locale locale ) throws org.jomc.ObjectManagementException
    {
        return org.jomc.ObjectManagerFactory.getObjectManager().getMessage( this, "modulesReport", locale,  null );
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
     * Gets the text of the {@code startingModuleProcessing} message.
     * <p><b>Templates</b><br/><table>
     * <tr><td valign="top">English:</td><td valign="top"><pre>Executing command {0} with module ''{1}'' ...</pre></td></tr>
     * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Führt Befehl {0} mit Modul ''{1}'' aus ... </pre></td></tr>
     * </table></p>
     * @param locale The locale of the message to return.
     * @param toolName Format argument.
     * @param moduleName Format argument.
     * @return The text of the {@code startingModuleProcessing} message.
     *
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated
    (
        value = "org.jomc.tools.JavaSources",
        comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-1-SNAPSHOT/jomc-tools"
    )
    private String getStartingModuleProcessingMessage( final java.util.Locale locale, final java.lang.String toolName, final java.lang.String moduleName ) throws org.jomc.ObjectManagementException
    {
        return org.jomc.ObjectManagerFactory.getObjectManager().getMessage( this, "startingModuleProcessing", locale, new Object[] { toolName, moduleName, null } );
    }

    /**
     * Gets the text of the {@code startingProcessing} message.
     * <p><b>Templates</b><br/><table>
     * <tr><td valign="top">English:</td><td valign="top"><pre>Executing command {0} ...</pre></td></tr>
     * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Führt Befehl {0} aus ... </pre></td></tr>
     * </table></p>
     * @param locale The locale of the message to return.
     * @param toolName Format argument.
     * @return The text of the {@code startingProcessing} message.
     *
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated
    (
        value = "org.jomc.tools.JavaSources",
        comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-1-SNAPSHOT/jomc-tools"
    )
    private String getStartingProcessingMessage( final java.util.Locale locale, final java.lang.String toolName ) throws org.jomc.ObjectManagementException
    {
        return org.jomc.ObjectManagerFactory.getObjectManager().getMessage( this, "startingProcessing", locale, new Object[] { toolName, null } );
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
    @javax.annotation.Generated
    (
        value = "org.jomc.tools.JavaSources",
        comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-1-SNAPSHOT/jomc-tools"
    )
    private String getToolFailureMessage( final java.util.Locale locale, final java.lang.String toolName ) throws org.jomc.ObjectManagementException
    {
        return org.jomc.ObjectManagerFactory.getObjectManager().getMessage( this, "toolFailure", locale, new Object[] { toolName, null } );
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
    @javax.annotation.Generated
    (
        value = "org.jomc.tools.JavaSources",
        comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-1-SNAPSHOT/jomc-tools"
    )
    private String getToolSuccessMessage( final java.util.Locale locale, final java.lang.String toolName ) throws org.jomc.ObjectManagementException
    {
        return org.jomc.ObjectManagerFactory.getObjectManager().getMessage( this, "toolSuccess", locale, new Object[] { toolName, null } );
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
    // SECTION-END
}
