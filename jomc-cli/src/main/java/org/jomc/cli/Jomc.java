/*
 * Copyright (C) 2009 Christian Schulte <cs@schulte.it>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   o Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   o Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in
 *     the documentation and/or other materials provided with the
 *     distribution.
 *
 * THIS SOFTWARE IS PROVIDED "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY
 * AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * $JOMC$
 *
 */
package org.jomc.cli;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang.StringUtils;
import org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement;

/**
 * JOMC command line interface.
 *
 * @author <a href="mailto:cs@schulte.it">Christian Schulte</a>
 */
public final class Jomc
{

    /**
     * Command line option.
     */
    private static final Option DEBUG_OPTION;

    /**
     * Command line option.
     */
    private static final Option VERBOSE_OPTION;

    /**
     * Command line option.
     */
    private static final Option FAIL_ON_WARNINGS_OPTION;

    static
    {
        DEBUG_OPTION = new Option( "D", Messages.getMessage( "debugOptionDescription" ) );
        DEBUG_OPTION.setLongOpt( "debug" );
        DEBUG_OPTION.setArgs( 1 );
        DEBUG_OPTION.setOptionalArg( true );
        DEBUG_OPTION.setArgName( Messages.getMessage( "debugOptionArgumentDescription" ) );

        VERBOSE_OPTION = new Option( "v", Messages.getMessage( "verboseOptionDescription" ) );
        VERBOSE_OPTION.setLongOpt( "verbose" );

        FAIL_ON_WARNINGS_OPTION = new Option( "fw", Messages.getMessage( "failOnWarningsOptionDescription" ) );
        FAIL_ON_WARNINGS_OPTION.setLongOpt( "fail-on-warnings" );
    }

    /**
     * Log level events are logged at by default.
     *
     * @see #getDefaultLogLevel()
     */
    private static final Level DEFAULT_LOG_LEVEL = Level.WARNING;

    /**
     * Default log level.
     */
    private static volatile Level defaultLogLevel;

    /**
     * Print writer of the instance.
     */
    private PrintWriter printWriter;

    /**
     * Log level of the instance.
     */
    private Level logLevel;

    /**
     * Greatest severity logged by the command.
     */
    private Level severity = Level.ALL;

    /**
     * Creates a new {@code Jomc} instance.
     */
    public Jomc()
    {
        super();
    }

    /**
     * Gets the print writer of the instance.
     *
     * @return The print writer of the instance.
     *
     * @see #setPrintWriter(java.io.PrintWriter)
     */
    @IgnoreJRERequirement
    public PrintWriter getPrintWriter()
    {
        if ( this.printWriter == null )
        {
            try
            {
                // As of Java 6, "System.console()", if any.
                Class.forName( "java.io.Console" );
                this.printWriter = System.console() != null
                                       ? System.console().writer()
                                       : new PrintWriter( System.out, true );

            }
            catch ( final ClassNotFoundException e )
            {
                if ( this.isLoggable( Level.FINEST ) )
                {
                    this.log( Level.FINEST, Messages.getMessage( e ), e );
                }

                this.printWriter = new PrintWriter( System.out, true );
            }
        }

        return this.printWriter;
    }

    /**
     * Sets the print writer of the instance.
     *
     * @param value The new print writer of the instance or {@code null}.
     *
     * @see #getPrintWriter()
     */
    public void setPrintWriter( final PrintWriter value )
    {
        this.printWriter = value;
    }

    /**
     * Gets the default log level events are logged at.
     * <p>
     * The default log level is controlled by system property {@code org.jomc.cli.Jomc.defaultLogLevel} holding
     * the log level to log events at by default. If that property is not set, the {@code WARNING} default is returned.
     * </p>
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

            if ( this.isLoggable( Level.CONFIG ) )
            {
                this.log( Level.CONFIG, Messages.getMessage( "defaultLogLevelInfo", this.logLevel.getLocalizedName() ),
                          null );

            }
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
     * @return {@code true}, if messages at {@code level} are provided to the listeners of the instance;
     * {@code false}, if messages at {@code level} are not provided to the listeners of the instance.
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
            final StringBuilder commandInfo = new StringBuilder();

            for ( final Command c : this.getCommands() )
            {
                if ( cmd == null && args != null && args.length > 0
                         && ( args[0].equals( c.getName() ) || args[0].equals( c.getAbbreviatedName() ) ) )
                {
                    cmd = c;
                }

                commandInfo.append( StringUtils.rightPad( c.getName(), 25 ) ).
                    append( " : " ).
                    append( c.getShortDescription( Locale.getDefault() ) ).
                    append( " (" ).
                    append( c.getAbbreviatedName() ).
                    append( ")" ).
                    append( System.getProperty( "line.separator", "\n" ) );

            }

            if ( cmd == null )
            {
                this.getPrintWriter().println( Messages.getMessage( "usage", "help" ) );
                this.getPrintWriter().println();
                this.getPrintWriter().println( commandInfo.toString() );
                return Command.STATUS_FAILURE;
            }

            final String[] commandArguments = new String[ args.length - 1 ];
            System.arraycopy( args, 1, commandArguments, 0, commandArguments.length );

            final Options options = cmd.getOptions();
            options.addOption( DEBUG_OPTION );
            options.addOption( VERBOSE_OPTION );
            options.addOption( FAIL_ON_WARNINGS_OPTION );

            if ( commandArguments.length > 0 && "help".equals( commandArguments[0] ) )
            {
                final StringWriter usage = new StringWriter();
                final StringWriter opts = new StringWriter();
                final HelpFormatter formatter = new HelpFormatter();

                PrintWriter pw = new PrintWriter( usage );
                formatter.printUsage( pw, 80, cmd.getName(), options );
                pw.close();
                assert !pw.checkError() : "Unexpected error printing usage.";

                pw = new PrintWriter( opts );
                formatter.printOptions( pw, 80, options, 2, 2 );
                pw.close();
                assert !pw.checkError() : "Unexpected error printing options.";

                this.getPrintWriter().println( cmd.getShortDescription( Locale.getDefault() ) );
                this.getPrintWriter().println();
                this.getPrintWriter().println( usage.toString() );
                this.getPrintWriter().println( opts.toString() );
                this.getPrintWriter().println();

                if ( cmd.getLongDescription( Locale.getDefault() ) != null )
                {
                    this.getPrintWriter().println( cmd.getLongDescription( Locale.getDefault() ) );
                    this.getPrintWriter().println();
                }

                return Command.STATUS_SUCCESS;
            }

            cmd.getListeners().add( new Command.Listener()
            {

                public void onLog( final Level level, final String message, final Throwable t )
                {
                    log( level, message, t );
                }

            } );

            // https://issues.apache.org/jira/browse/CLI-255
            final CommandLine commandLine = new GnuParser().parse( options, commandArguments );
            final boolean debug = commandLine.hasOption( DEBUG_OPTION.getOpt() );
            final boolean verbose = commandLine.hasOption( VERBOSE_OPTION.getOpt() );
            Level debugLevel = Level.ALL;

            if ( debug )
            {
                final String debugOption = commandLine.getOptionValue( DEBUG_OPTION.getOpt() );

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

            if ( this.isLoggable( Level.FINER ) )
            {
                for ( int i = 0; i < args.length; i++ )
                {
                    this.log( Level.FINER, new StringBuilder().append( "[" ).append( i ).append( "] -> '" ).
                              append( args[i] ).append( "'" ).append( System.getProperty( "line.separator", "\n" ) ).
                              toString(), null );

                }
            }

            final boolean failOnWarnings = commandLine.hasOption( FAIL_ON_WARNINGS_OPTION.getOpt() );

            final int status = cmd.execute( commandLine );

            if ( status == Command.STATUS_SUCCESS && failOnWarnings
                     && this.severity.intValue() >= Level.WARNING.intValue() )
            {
                return Command.STATUS_FAILURE;
            }

            return status;
        }
        catch ( final ParseException e )
        {
            this.log( Level.SEVERE, Messages.getMessage( "illegalArgumentsInformation", cmd.getName(), "help" ), e );
            return Command.STATUS_FAILURE;
        }
        catch ( final Throwable t )
        {
            this.log( Level.SEVERE, null, t );
            return Command.STATUS_FAILURE;
        }
        finally
        {
            this.getPrintWriter().flush();
            this.severity = Level.ALL;
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
    private void log( final Level level, final String message, final Throwable throwable )
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
                final String m = Messages.getMessage( throwable );

                if ( m != null && m.length() > 0 )
                {
                    this.getPrintWriter().print( this.formatLogLines( level, m ) );
                }
                else
                {
                    this.getPrintWriter().print( this.formatLogLines(
                        level, Messages.getMessage( "defaultExceptionMessage" ) ) );

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
        BufferedReader reader = null;

        try
        {
            final StringBuilder lines = new StringBuilder( text.length() );
            reader = new BufferedReader( new StringReader( text ) );

            for ( String line = reader.readLine(); line != null; line = reader.readLine() )
            {
                final boolean debug = this.getLogLevel().intValue() < Level.INFO.intValue();
                lines.append( "[" ).append( level.getLocalizedName() );

                if ( debug )
                {
                    lines.append( "|" ).append( Thread.currentThread().getName() ).append( "|" ).
                        append( Messages.getMessage( "timePattern", new Date( System.currentTimeMillis() ) ) );

                }

                lines.append( "] " ).append( line ).append( System.getProperty( "line.separator", "\n" ) );
            }

            reader.close();
            reader = null;

            return lines.toString();
        }
        catch ( final IOException e )
        {
            throw new AssertionError( e );
        }
        finally
        {
            try
            {
                if ( reader != null )
                {
                    reader.close();
                }
            }
            catch ( final IOException e )
            {
                this.log( Level.SEVERE, Messages.getMessage( e ), e );
            }
        }
    }

    /**
     * Gets the {@code Command}s of the instance.
     *
     * @return The {@code Command}s of the instance.
     *
     * @throws IOException if discovering {@code Command} implementations fails.
     */
    private List<Command> getCommands() throws IOException
    {
        final List<Command> commands = new ArrayList<Command>();

        final Enumeration<URL> serviceResources =
            this.getClass().getClassLoader().getResources( "META-INF/services/org.jomc.cli.Command" );

        if ( serviceResources != null )
        {
            for ( final URL serviceResource : Collections.list( serviceResources ) )
            {
                BufferedReader reader = null;
                try
                {
                    reader = new BufferedReader( new InputStreamReader( serviceResource.openStream(), "UTF-8" ) );

                    for ( String line = reader.readLine(); line != null; line = reader.readLine() )
                    {
                        if ( !line.contains( "#" ) )
                        {
                            commands.add( Class.forName( line.trim() ).asSubclass( Command.class ).newInstance() );
                        }
                    }
                }
                catch ( final ClassNotFoundException e )
                {
                    throw new AssertionError( e );
                }
                catch ( final InstantiationException e )
                {
                    throw new AssertionError( e );
                }
                catch ( final IllegalAccessException e )
                {
                    throw new AssertionError( e );
                }
                finally
                {
                    try
                    {
                        if ( reader != null )
                        {
                            reader.close();
                        }
                    }
                    catch ( final IOException e )
                    {
                        this.log( Level.WARNING, Messages.getMessage( e ), e );
                    }
                }
            }
        }

        return Collections.unmodifiableList( commands );
    }

}
