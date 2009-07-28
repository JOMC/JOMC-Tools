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
package org.jomc.tools.cli.commands;

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
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.jomc.model.DefaultModelManager;
import org.jomc.model.ModelException;
import org.jomc.model.ModelObject;
import org.jomc.model.Module;
import org.jomc.model.Modules;
import org.jomc.tools.JomcTool;
import org.jomc.tools.cli.Command;
import org.xml.sax.SAXException;

// SECTION-START[Implementation Comment]
/**
 * Base JOMC {@code Command} implementation.
 * <p><b>Properties</b><ul>
 * <li>"{@link #getBuildDirectoryOptionLongName buildDirectoryOptionLongName}"<blockquote>
 * Property of type {@code java.lang.String} with value "build-dir".</blockquote></li>
 * <li>"{@link #getBuildDirectoryOptionShortName buildDirectoryOptionShortName}"<blockquote>
 * Property of type {@code java.lang.String} with value "bd".</blockquote></li>
 * <li>"{@link #getClasspathOptionLongName classpathOptionLongName}"<blockquote>
 * Property of type {@code java.lang.String} with value "classpath".</blockquote></li>
 * <li>"{@link #getClasspathOptionShortName classpathOptionShortName}"<blockquote>
 * Property of type {@code java.lang.String} with value "cp".</blockquote></li>
 * <li>"{@link #getCommandName commandName}"<blockquote>
 * Property of type {@code java.lang.String} with value "noop".</blockquote></li>
 * <li>"{@link #getDebugOptionLongName debugOptionLongName}"<blockquote>
 * Property of type {@code java.lang.String} with value "debug".</blockquote></li>
 * <li>"{@link #getDebugOptionShortName debugOptionShortName}"<blockquote>
 * Property of type {@code java.lang.String} with value "D".</blockquote></li>
 * <li>"{@link #getDocumentsOptionLongName documentsOptionLongName}"<blockquote>
 * Property of type {@code java.lang.String} with value "documents".</blockquote></li>
 * <li>"{@link #getDocumentsOptionShortName documentsOptionShortName}"<blockquote>
 * Property of type {@code java.lang.String} with value "df".</blockquote></li>
 * <li>"{@link #getVerboseOptionLongName verboseOptionLongName}"<blockquote>
 * Property of type {@code java.lang.String} with value "verbose".</blockquote></li>
 * <li>"{@link #getVerboseOptionShortName verboseOptionShortName}"<blockquote>
 * Property of type {@code java.lang.String} with value "v".</blockquote></li>
 * </ul></p>
 * <p><b>Dependencies</b><ul>
 * <li>"{@link #getLocale Locale}"<blockquote>
 * Dependency on {@code java.util.Locale} at specification level 1.1 applying to Multiton scope bound to an instance.</blockquote></li>
 * </ul></p>
 * <p><b>Messages</b><ul>
 * <li>"{@link #getBuildDirectoryOptionMessage buildDirectoryOption}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>Work directory of the process.</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Arbeitsverzeichnis des Vorgangs.</pre></td></tr>
 * </table>
 * <li>"{@link #getClasspathOptionMessage classpathOption}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>Classpath elements separated by '':''. If starting with a ''@'' character, a file name of a file holding classpath elements.</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Klassenpfad-Elemente mit '':'' getrennt. Wenn mit ''@'' beginnend, Dateiname einer Textdatei mit Klassenpfad-Elementen.</pre></td></tr>
 * </table>
 * <li>"{@link #getDebugOptionMessage debugOption}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>Enables debug output.</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Aktiviert Diagnose-Ausgaben.</pre></td></tr>
 * </table>
 * <li>"{@link #getDescriptionMessage description}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>Does nothing.</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Führt nichts aus.</pre></td></tr>
 * </table>
 * <li>"{@link #getDocumentsOptionMessage documentsOption}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>Document filenames separated by '':''. If starting with a ''@'' character, a file name of a file holding document filenames.</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Dokument-Dateinamen mit '':'' getrennt. Wenn mit ''@'' beginnend, Dateiname einer Textdatei mit Dokument-Dateinamen.</pre></td></tr>
 * </table>
 * <li>"{@link #getVerboseOptionMessage verboseOption}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>Enables verbose output.</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Aktiviert ausführliche Ausgaben.</pre></td></tr>
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

    /** 'build-dir' option of the instance. */
    private Option buildDirectoryOption;

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
        }

        return this.documentsOption;
    }

    public Option getBuildDirectoryOption()
    {
        if ( this.buildDirectoryOption == null )
        {
            this.buildDirectoryOption = new Option( this.getBuildDirectoryOptionShortName(),
                                                    this.getBuildDirectoryOptionLongName(), true,
                                                    this.getBuildDirectoryOptionMessage( this.getLocale() ) );

        }

        return this.buildDirectoryOption;
    }

    public String getName()
    {
        return this.getCommandName();
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
        options.addOption( this.getBuildDirectoryOption() );
        return options;
    }

    protected ClassLoader getClassLoader( final CommandLine commandLine ) throws IOException
    {
        final Set<URL> urls = new HashSet<URL>();

        if ( commandLine.hasOption( this.getClasspathOption().getOpt() ) )
        {
            final String[] elements = commandLine.getOptionValues( this.getClasspathOption().getOpt() );
            if ( elements != null )
            {
                for ( String e : elements )
                {
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

    protected void configureTool( final JomcTool tool, final CommandLine commandLine, final PrintStream printStream )
        throws IOException, SAXException, JAXBException, ModelException
    {
        final ClassLoader classLoader = this.getClassLoader( commandLine );

        tool.getListeners().add( new JomcTool.Listener()
        {

            @Override
            public void onLog( final Level level, final String message, final Throwable throwable )
            {
                final boolean verbose = commandLine.hasOption( getVerboseOption().getOpt() );
                final boolean debug = commandLine.hasOption( getDebugOption().getOpt() );
                log( level, message, throwable, printStream, verbose, debug );
            }

        } );

        if ( tool.getModelManager() instanceof DefaultModelManager )
        {
            final DefaultModelManager defaultModelManager = (DefaultModelManager) tool.getModelManager();
            defaultModelManager.getListeners().add( new DefaultModelManager.Listener()
            {

                public void onLog( final Level level, final String message, final Throwable t )
                {
                    final boolean verbose = commandLine.hasOption( getVerboseOption().getOpt() );
                    final boolean debug = commandLine.hasOption( getDebugOption().getOpt() );
                    log( level, message, t, printStream, verbose, debug );
                }

            } );

            defaultModelManager.setClassLoader( classLoader );
        }

        if ( commandLine.hasOption( this.getBuildDirectoryOption().getOpt() ) )
        {
            tool.setBuildDirectory( new File( commandLine.getOptionValue( this.getBuildDirectoryOption().getOpt() ) ) );
        }
        if ( commandLine.hasOption( this.getDocumentsOption().getOpt() ) )
        {
            final Unmarshaller u = tool.getModelManager().getUnmarshaller( false );
            for ( File f : this.getDocumentFiles( commandLine ) )
            {
                final InputStream in = new FileInputStream( f );
                final ModelObject mo = u.unmarshal( new StreamSource( in ), ModelObject.class ).getValue();
                in.close();

                if ( mo instanceof Module )
                {
                    tool.getModules().getModule().add( (Module) mo );
                }
                else if ( mo instanceof Modules )
                {
                    tool.getModules().getModule().addAll( ( (Modules) mo ).getModule() );
                }
            }
        }
        if ( tool.getModelManager() instanceof DefaultModelManager )
        {
            final DefaultModelManager defaultModelManager = (DefaultModelManager) tool.getModelManager();
            final Modules classpathModules = defaultModelManager.getClasspathModules(
                DefaultModelManager.DEFAULT_DOCUMENT_LOCATION ); // TODO Add cli argument.

            if ( classpathModules != null )
            {
                tool.getModules().getModule().addAll( classpathModules.getModule() );
            }

            final Module classpathModule = defaultModelManager.getClasspathModule( tool.getModules() );
            if ( classpathModule != null )
            {
                tool.getModules().getModule().add( classpathModule );
            }
        }

        tool.getModelManager().validateModules( tool.getModules() );
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
                    lines.append( "[" ).append( level ).append( "] " );
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
        else if ( debug )
        {
            logLevel = Level.FINEST;
        }

        if ( level.intValue() >= logLevel.intValue() )
        {
            if ( message != null )
            {
                printStream.print( this.getLoglines( level, message ) );
            }

            if ( throwable != null )
            {
                if ( debug )
                {
                    final StringWriter stackTrace = new StringWriter();
                    final PrintWriter pw = new PrintWriter( stackTrace );
                    throwable.printStackTrace( pw );
                    pw.flush();
                    printStream.print( this.getLoglines( level, stackTrace.toString() ) );
                }
                else
                {
                    printStream.print( this.getLoglines( level, throwable.getMessage() ) );
                }
            }
        }
    }

    // SECTION-END
    // SECTION-START[Constructors]

    /** Default implementation constructor. */
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
