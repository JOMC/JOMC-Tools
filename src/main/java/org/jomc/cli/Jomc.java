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
package org.jomc.cli;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.logging.Level;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang.StringUtils;
import org.jomc.model.DefaultModelProcessor;
import org.jomc.model.DefaultModelProvider;
import org.jomc.model.bootstrap.DefaultSchemaProvider;
import org.jomc.model.bootstrap.DefaultServiceProvider;

// SECTION-START[Documentation]
// <editor-fold defaultstate="collapsed" desc=" Generated Documentation ">
/**
 * JOMC command line interface.
 * <p><b>Properties</b><ul>
 * <li>"{@link #getDescPad descPad}"
 * <blockquote>Property of type {@code int}.
 * <p>The number of characters of padding to be prefixed to each description line.</p>
 * </blockquote></li>
 * <li>"{@link #getHelpCommandName helpCommandName}"
 * <blockquote>Property of type {@code java.lang.String}.
 * <p>The name of the command used to request help.</p>
 * </blockquote></li>
 * <li>"{@link #getLeftPad leftPad}"
 * <blockquote>Property of type {@code int}.
 * <p>The number of characters of padding to be prefixed to each line.</p>
 * </blockquote></li>
 * <li>"{@link #getWidth width}"
 * <blockquote>Property of type {@code int}.
 * <p>The number of characters per line for the usage statement.</p>
 * </blockquote></li>
 * </ul></p>
 * <p><b>Dependencies</b><ul>
 * <li>"{@link #getCommands Commands}"<blockquote>
 * Dependency on {@code org.jomc.cli.Command} at specification level 1.0-alpha-21-SNAPSHOT.</blockquote></li>
 * <li>"{@link #getDebugOption DebugOption}"<blockquote>
 * Dependency on {@code org.apache.commons.cli.Option} bound to an instance.</blockquote></li>
 * <li>"{@link #getFailOnWarningsOption FailOnWarningsOption}"<blockquote>
 * Dependency on {@code org.apache.commons.cli.Option} bound to an instance.</blockquote></li>
 * <li>"{@link #getLocale Locale}"<blockquote>
 * Dependency on {@code java.util.Locale} at specification level 1.1 bound to an instance.</blockquote></li>
 * <li>"{@link #getVerboseOption VerboseOption}"<blockquote>
 * Dependency on {@code org.apache.commons.cli.Option} bound to an instance.</blockquote></li>
 * </ul></p>
 * <p><b>Messages</b><ul>
 * <li>"{@link #getCommandLineInfoMessage commandLineInfo}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>Command line:
 * {0}</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Kommandozeile:
 * {0}</pre></td></tr>
 * </table>
 * <li>"{@link #getIllegalArgumentsMessage illegalArguments}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>Illegal arguments. Type &raquo;jomc {0} {1}&laquo; for further information.</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Ung&uuml;ltige Argumente. Geben Sie &raquo;jomc {0} {1}&laquo; f&uuml;r weitere Informationen ein.</pre></td></tr>
 * </table>
 * <li>"{@link #getUsageMessage usage}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>Type &raquo;jomc &lt;command&gt; {0}&laquo; for further information.</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Geben Sie &raquo;jomc &lt;Befehl&gt; {0}&laquo; f&uuml;r weitere Informationen ein.</pre></td></tr>
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
                             comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-21-SNAPSHOT/jomc-tools" )
// </editor-fold>
// SECTION-END
public class Jomc
{
    // SECTION-START[Jomc]

    /**
     * Log level events are logged at by default.
     * @see #getDefaultLogLevel()
     */
    private static final Level DEFAULT_LOG_LEVEL = Level.WARNING;

    /** Default log level. */
    private static volatile Level defaultLogLevel;

    /** Print writer of the instance. */
    private PrintWriter printWriter;

    /** Log level of the instance. */
    private Level logLevel;

    /** Greatest severity logged by the command. */
    private Level severity = Level.ALL;

    /**
     * Gets the print writer of the instance.
     *
     * @return The print writer of the instance.
     */
    public PrintWriter getPrintWriter()
    {
        if ( this.printWriter == null )
        {
            this.printWriter = new PrintWriter( System.out, true );
        }

        return this.printWriter;
    }

    /**
     * Sets the print writer of the instance.
     *
     * @param value The new print writer of the instance or {@code null}.
     */
    public void setPrintWriter( final PrintWriter value )
    {
        this.printWriter = value;
    }

    /**
     * Gets the default log level events are logged at.
     * <p>The default log level is controlled by system property {@code org.jomc.cli.Jomc.defaultLogLevel} holding the
     * log level to log events at by default. If that property is not set, the {@code WARNING} default is returned.</p>
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
                "org.jomc.cli.Jomc.defaultLogLevel", DEFAULT_LOG_LEVEL.getName() ) );

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
    public boolean isLoggable( final Level level )
    {
        if ( level == null )
        {
            throw new NullPointerException( "level" );
        }

        return level.intValue() >= this.getLogLevel().intValue();
    }

    /**
     * Processes the given arguments and executes the corresponding command.
     *
     * @param args Arguments to process.
     *
     * @return Status code.
     *
     * @see Command#STATUS_SUCCESS
     * @see Command#STATUS_FAILURE
     */
    public int jomc( final String[] args )
    {
        Command cmd = null;
        this.severity = Level.ALL;

        try
        {
            DefaultModelProvider.setDefaultModuleLocation( "META-INF/jomc-cli.xml" );
            DefaultModelProcessor.setDefaultTransformerLocation( "META-INF/jomc-cli.xsl" );
            DefaultSchemaProvider.setDefaultSchemaLocation( "META-INF/jomc-schemas.xml" );
            DefaultServiceProvider.setDefaultServiceLocation( "META-INF/jomc-services.xml" );

            final StringBuilder commandInfo = new StringBuilder();

            for ( Command c : this.getCommands() )
            {
                if ( cmd == null && args != null && args.length > 0 &&
                     ( args[0].equals( c.getName() ) || args[0].equals( c.getAbbreviatedName() ) ) )
                {
                    cmd = c;
                }

                commandInfo.append( StringUtils.rightPad( c.getName(), 25 ) ).append( " : " ).
                    append( c.getShortDescription( this.getLocale() ) ).append( " (" ).append( c.getAbbreviatedName() ).
                    append( ")" ).append( System.getProperty( "line.separator" ) );

            }

            if ( cmd == null )
            {
                this.getPrintWriter().println( this.getUsageMessage( this.getLocale(), this.getHelpCommandName() ) );
                this.getPrintWriter().println();
                this.getPrintWriter().println( commandInfo.toString() );
                return Command.STATUS_FAILURE;
            }

            final String[] commandArguments = new String[ args.length - 1 ];
            System.arraycopy( args, 1, commandArguments, 0, commandArguments.length );

            final Options options = cmd.getOptions();
            options.addOption( this.getDebugOption() );
            options.addOption( this.getVerboseOption() );
            options.addOption( this.getFailOnWarningsOption() );

            if ( commandArguments.length > 0 && this.getHelpCommandName().equals( commandArguments[0] ) )
            {
                final StringWriter usage = new StringWriter();
                final StringWriter opts = new StringWriter();
                final HelpFormatter formatter = new HelpFormatter();

                PrintWriter pw = new PrintWriter( usage );
                formatter.printUsage( pw, this.getWidth(), cmd.getName(), options );
                pw.close();

                pw = new PrintWriter( opts );
                formatter.printOptions( pw, this.getWidth(), options, this.getLeftPad(), this.getDescPad() );
                pw.close();

                this.getPrintWriter().println( cmd.getShortDescription( this.getLocale() ) );
                this.getPrintWriter().println();
                this.getPrintWriter().println( usage.toString() );
                this.getPrintWriter().println( opts.toString() );
                this.getPrintWriter().println();
                this.getPrintWriter().println( cmd.getLongDescription( this.getLocale() ) );
                this.getPrintWriter().println();
                return Command.STATUS_SUCCESS;
            }

            cmd.getListeners().add( new Command.Listener()
            {

                public void onLog( final Level level, final String message, final Throwable t )
                {
                    log( level, message, t );
                }

            } );

            DefaultModelProvider.setDefaultModuleLocation( null );
            DefaultModelProcessor.setDefaultTransformerLocation( null );
            DefaultSchemaProvider.setDefaultSchemaLocation( null );
            DefaultServiceProvider.setDefaultServiceLocation( null );

            final CommandLine commandLine = new GnuParser().parse( options, commandArguments );
            final boolean debug = commandLine.hasOption( this.getDebugOption().getOpt() );
            final boolean verbose = commandLine.hasOption( this.getVerboseOption().getOpt() );
            Level debugLevel = Level.ALL;

            if ( debug )
            {
                final String debugOption = commandLine.getOptionValue( this.getDebugOption().getOpt() );
                if ( debugOption != null )
                {
                    debugLevel = Level.parse( debugOption );
                }
            }

            if ( debug || verbose )
            {
                this.setLogLevel( debug ? debugLevel : Level.INFO );
            }

            cmd.setLogLevel( this.getLogLevel() );

            if ( this.isLoggable( Level.FINE ) )
            {
                final StringBuilder argumentInfo = new StringBuilder();

                for ( int i = 0; i < args.length; i++ )
                {
                    argumentInfo.append( "\t[" ).append( i ).append( "]='" ).append( args[i] ).append( "'" ).
                        append( System.getProperty( "line.separator" ) );

                }

                this.log( Level.FINE, this.getCommandLineInfoMessage(
                    this.getLocale(), argumentInfo.toString() ), null );

            }

            final boolean failOnWarnings = commandLine.hasOption( this.getFailOnWarningsOption().getOpt() );

            final int status = cmd.execute( commandLine );
            if ( status == Command.STATUS_SUCCESS && failOnWarnings &&
                 this.severity.intValue() >= Level.WARNING.intValue() )
            {
                return Command.STATUS_FAILURE;
            }

            return status;
        }
        catch ( final ParseException e )
        {
            this.log( Level.SEVERE, this.getIllegalArgumentsMessage(
                this.getLocale(), cmd.getName(), this.getHelpCommandName() ), e );

            return Command.STATUS_FAILURE;
        }
        catch ( final Throwable t )
        {
            this.log( Level.SEVERE, t.getMessage(), t );
            return Command.STATUS_FAILURE;
        }
        finally
        {
            DefaultModelProvider.setDefaultModuleLocation( null );
            DefaultModelProcessor.setDefaultTransformerLocation( null );
            DefaultSchemaProvider.setDefaultSchemaLocation( null );
            DefaultServiceProvider.setDefaultServiceLocation( null );
            this.getPrintWriter().flush();
        }
    }

    /**
     * Main entry point.
     *
     * @param args The application arguments.
     */
    public static void main( final String[] args )
    {
        System.exit( run( args ) );
    }

    /**
     * Main entry point without exiting the VM.
     *
     * @param args The application arguments.
     *
     * @return Status code.
     *
     * @see Command#STATUS_SUCCESS
     * @see Command#STATUS_FAILURE
     */
    public static int run( final String[] args )
    {
        return new Jomc().jomc( args );
    }

    /**
     * Logs to the print writer of the instance.
     *
     * @param level The level of the event.
     * @param message The message of the event or {@code null}.
     * @param throwable The throwable of the event {@code null}.
     *
     * @throws NullPointerException if {@code level} is {@code null}.
     */
    protected void log( final Level level, final String message, final Throwable throwable )
    {
        if ( level == null )
        {
            throw new NullPointerException( "level" );
        }

        if ( this.severity.intValue() < level.intValue() )
        {
            this.severity = level;
        }

        if ( this.isLoggable( level ) )
        {
            if ( message != null )
            {
                this.getPrintWriter().print( this.formatLogLines( level, "" ) );
                this.getPrintWriter().print( this.formatLogLines( level, message ) );
            }

            if ( throwable != null )
            {
                this.getPrintWriter().print( this.formatLogLines( level, "" ) );
                final String m = this.getMessage( throwable );

                if ( m != null )
                {
                    this.getPrintWriter().print( this.formatLogLines( level, m ) );
                }
                else
                {
                    this.getPrintWriter().print( this.formatLogLines( level, throwable.toString() ) );
                }

                if ( this.getLogLevel().intValue() < Level.INFO.intValue() )
                {
                    final StringWriter stackTrace = new StringWriter();
                    final PrintWriter pw = new PrintWriter( stackTrace );
                    throwable.printStackTrace( pw );
                    pw.flush();
                    this.getPrintWriter().print( this.formatLogLines( level, stackTrace.toString() ) );
                }
            }
        }

        this.getPrintWriter().flush();
    }

    private String formatLogLines( final Level level, final String text )
    {
        try
        {
            final StringBuilder lines = new StringBuilder();
            final BufferedReader reader = new BufferedReader( new StringReader( text ) );

            String line;
            while ( ( line = reader.readLine() ) != null )
            {
                lines.append( "[" ).append( level.getLocalizedName() ).append( "] " );
                lines.append( line ).append( System.getProperty( "line.separator" ) );
            }

            return lines.toString();
        }
        catch ( final IOException e )
        {
            throw new AssertionError( e );
        }
    }

    private String getMessage( final Throwable throwable )
    {
        if ( throwable != null )
        {
            if ( throwable.getMessage() != null )
            {
                return throwable.getMessage();
            }

            return this.getMessage( throwable.getCause() );
        }

        return null;
    }

    // SECTION-END
    // SECTION-START[Constructors]
    // <editor-fold defaultstate="collapsed" desc=" Generated Constructors ">

    /** Creates a new {@code Jomc} instance. */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-21-SNAPSHOT/jomc-tools" )
    public Jomc()
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
     * Gets the {@code Commands} dependency.
     * <p>This method returns any available object of the {@code org.jomc.cli.Command} specification at specification level 1.0-alpha-21-SNAPSHOT.</p>
     * <p>That specification does not apply to any scope. A new object is returned whenever requested.</p>
     * @return The {@code Commands} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-21-SNAPSHOT/jomc-tools" )
    private org.jomc.cli.Command[] getCommands()
    {
        final org.jomc.cli.Command[] _d = (org.jomc.cli.Command[]) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "Commands" );
        assert _d != null : "'Commands' dependency not found.";
        return _d;
    }

    /**
     * Gets the {@code DebugOption} dependency.
     * <p>This method returns the "{@code JOMC CLI Debug Option}" object of the {@code org.apache.commons.cli.Option} specification.</p>
     * <p>That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.</p>
     * @return The {@code DebugOption} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-21-SNAPSHOT/jomc-tools" )
    private org.apache.commons.cli.Option getDebugOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "DebugOption" );
        assert _d != null : "'DebugOption' dependency not found.";
        return _d;
    }

    /**
     * Gets the {@code FailOnWarningsOption} dependency.
     * <p>This method returns the "{@code JOMC CLI Fail-On-Warnings Option}" object of the {@code org.apache.commons.cli.Option} specification.</p>
     * <p>That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.</p>
     * @return The {@code FailOnWarningsOption} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-21-SNAPSHOT/jomc-tools" )
    private org.apache.commons.cli.Option getFailOnWarningsOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "FailOnWarningsOption" );
        assert _d != null : "'FailOnWarningsOption' dependency not found.";
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
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-21-SNAPSHOT/jomc-tools" )
    private java.util.Locale getLocale()
    {
        final java.util.Locale _d = (java.util.Locale) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "Locale" );
        assert _d != null : "'Locale' dependency not found.";
        return _d;
    }

    /**
     * Gets the {@code VerboseOption} dependency.
     * <p>This method returns the "{@code JOMC CLI Verbose Option}" object of the {@code org.apache.commons.cli.Option} specification.</p>
     * <p>That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.</p>
     * @return The {@code VerboseOption} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-21-SNAPSHOT/jomc-tools" )
    private org.apache.commons.cli.Option getVerboseOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "VerboseOption" );
        assert _d != null : "'VerboseOption' dependency not found.";
        return _d;
    }
    // </editor-fold>
    // SECTION-END
    // SECTION-START[Properties]
    // <editor-fold defaultstate="collapsed" desc=" Generated Properties ">

    /**
     * Gets the value of the {@code descPad} property.
     * @return The number of characters of padding to be prefixed to each description line.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-21-SNAPSHOT/jomc-tools" )
    private int getDescPad()
    {
        final java.lang.Integer _p = (java.lang.Integer) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getProperty( this, "descPad" );
        assert _p != null : "'descPad' property not found.";
        return _p.intValue();
    }

    /**
     * Gets the value of the {@code helpCommandName} property.
     * @return The name of the command used to request help.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-21-SNAPSHOT/jomc-tools" )
    private java.lang.String getHelpCommandName()
    {
        final java.lang.String _p = (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getProperty( this, "helpCommandName" );
        assert _p != null : "'helpCommandName' property not found.";
        return _p;
    }

    /**
     * Gets the value of the {@code leftPad} property.
     * @return The number of characters of padding to be prefixed to each line.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-21-SNAPSHOT/jomc-tools" )
    private int getLeftPad()
    {
        final java.lang.Integer _p = (java.lang.Integer) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getProperty( this, "leftPad" );
        assert _p != null : "'leftPad' property not found.";
        return _p.intValue();
    }

    /**
     * Gets the value of the {@code width} property.
     * @return The number of characters per line for the usage statement.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-21-SNAPSHOT/jomc-tools" )
    private int getWidth()
    {
        final java.lang.Integer _p = (java.lang.Integer) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getProperty( this, "width" );
        assert _p != null : "'width' property not found.";
        return _p.intValue();
    }
    // </editor-fold>
    // SECTION-END
    // SECTION-START[Messages]
    // <editor-fold defaultstate="collapsed" desc=" Generated Messages ">

    /**
     * Gets the text of the {@code commandLineInfo} message.
     * <p><b>Templates</b><br/><table>
     * <tr><td valign="top">English:</td><td valign="top"><pre>Command line:
     * {0}</pre></td></tr>
     * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Kommandozeile:
     * {0}</pre></td></tr>
     * </table></p>
     * @param locale The locale of the message to return.
     * @param commandLine Format argument.
     * @return The text of the {@code commandLineInfo} message.
     *
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-21-SNAPSHOT/jomc-tools" )
    private String getCommandLineInfoMessage( final java.util.Locale locale, final java.lang.String commandLine )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "commandLineInfo", locale, commandLine );
        assert _m != null : "'commandLineInfo' message not found.";
        return _m;
    }

    /**
     * Gets the text of the {@code illegalArguments} message.
     * <p><b>Templates</b><br/><table>
     * <tr><td valign="top">English:</td><td valign="top"><pre>Illegal arguments. Type &raquo;jomc {0} {1}&laquo; for further information.</pre></td></tr>
     * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Ung&uuml;ltige Argumente. Geben Sie &raquo;jomc {0} {1}&laquo; f&uuml;r weitere Informationen ein.</pre></td></tr>
     * </table></p>
     * @param locale The locale of the message to return.
     * @param command Format argument.
     * @param helpCommandName Format argument.
     * @return The text of the {@code illegalArguments} message.
     *
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-21-SNAPSHOT/jomc-tools" )
    private String getIllegalArgumentsMessage( final java.util.Locale locale, final java.lang.String command, final java.lang.String helpCommandName )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "illegalArguments", locale, command, helpCommandName );
        assert _m != null : "'illegalArguments' message not found.";
        return _m;
    }

    /**
     * Gets the text of the {@code usage} message.
     * <p><b>Templates</b><br/><table>
     * <tr><td valign="top">English:</td><td valign="top"><pre>Type &raquo;jomc &lt;command&gt; {0}&laquo; for further information.</pre></td></tr>
     * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Geben Sie &raquo;jomc &lt;Befehl&gt; {0}&laquo; f&uuml;r weitere Informationen ein.</pre></td></tr>
     * </table></p>
     * @param locale The locale of the message to return.
     * @param helpCommandName Format argument.
     * @return The text of the {@code usage} message.
     *
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-21-SNAPSHOT/jomc-tools" )
    private String getUsageMessage( final java.util.Locale locale, final java.lang.String helpCommandName )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "usage", locale, helpCommandName );
        assert _m != null : "'usage' message not found.";
        return _m;
    }
    // </editor-fold>
    // SECTION-END
}
