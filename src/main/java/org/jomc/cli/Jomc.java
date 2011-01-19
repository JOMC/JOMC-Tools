// SECTION-START[License Header]
// <editor-fold defaultstate="collapsed" desc=" Generated License ">
/*
 *   Copyright (c) 2009 - 2011 The JOMC Project
 *   Copyright (c) 2005 - 2011 Christian Schulte <schulte2005@users.sourceforge.net>
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
import java.util.Date;
import java.util.logging.Level;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang.StringUtils;
import org.jomc.model.modlet.DefaultModelProcessor;
import org.jomc.model.modlet.DefaultModelProvider;
import org.jomc.modlet.DefaultModletProvider;

// SECTION-START[Documentation]
// <editor-fold defaultstate="collapsed" desc=" Generated Documentation ">
/**
 * JOMC command line interface.
 * <p>
 *   <table border="1" width="100%" cellpadding="3" cellspacing="0">
 *     <caption class="TableCaption">Properties</caption>
 *     <tr>
 *       <th align="left" class="TableHeader" scope="col" nowrap>Name</th>
 *       <th align="left" class="TableHeader" scope="col" nowrap>Type</th>
 *       <th align="left" class="TableHeader" scope="col" nowrap>Documentation</th>
 *     </tr>
 *     <tr class="TableRowColor">
 *       <td align="left" nowrap>{@link #getDescPad descPad}</td>
 *       <td align="left" nowrap>{@code int}</td>
 *       <td align="left" valign="top">The number of characters of padding to be prefixed to each description line.</td>
 *     </tr>
 *     <tr class="TableRowColor">
 *       <td align="left" nowrap>{@link #getHelpCommandName helpCommandName}</td>
 *       <td align="left" nowrap>{@code java.lang.String}</td>
 *       <td align="left" valign="top">The name of the command used to request help.</td>
 *     </tr>
 *     <tr class="TableRowColor">
 *       <td align="left" nowrap>{@link #getLeftPad leftPad}</td>
 *       <td align="left" nowrap>{@code int}</td>
 *       <td align="left" valign="top">The number of characters of padding to be prefixed to each line.</td>
 *     </tr>
 *     <tr class="TableRowColor">
 *       <td align="left" nowrap>{@link #getWidth width}</td>
 *       <td align="left" nowrap>{@code int}</td>
 *       <td align="left" valign="top">The number of characters per line for the usage statement.</td>
 *     </tr>
 *   </table>
 * </p>
 * <p>
 *   <table border="1" width="100%" cellpadding="3" cellspacing="0">
 *     <caption class="TableCaption">Dependencies</caption>
 *       <tr>
 *         <th align="left" class="TableHeader" scope="col" nowrap>Name</th>
 *         <th align="left" class="TableHeader" scope="col" nowrap>Description</th>
 *       </tr>
 *       <tr class="TableRowColor">
 *         <td align="left" nowrap>{@link #getCommandLineParser CommandLineParser}</td>
 *         <td align="left">Dependency on {@code 'org.apache.commons.cli.CommandLineParser'} {@code (org.apache.commons.cli.CommandLineParser)}.</td>
 *       </tr>
 *       <tr class="TableRowColor">
 *         <td align="left" nowrap>{@link #getCommands Commands}</td>
 *         <td align="left">Dependency on {@code 'JOMC CLI Command'} {@code (org.jomc.cli.Command)} at specification level 1.0.</td>
 *       </tr>
 *       <tr class="TableRowColor">
 *         <td align="left" nowrap>{@link #getDebugOption DebugOption}</td>
 *         <td align="left">Dependency on {@code 'JOMC CLI Application Option'} {@code (org.apache.commons.cli.Option)} bound to an instance.</td>
 *       </tr>
 *       <tr class="TableRowColor">
 *         <td align="left" nowrap>{@link #getFailOnWarningsOption FailOnWarningsOption}</td>
 *         <td align="left">Dependency on {@code 'JOMC CLI Application Option'} {@code (org.apache.commons.cli.Option)} bound to an instance.</td>
 *       </tr>
 *       <tr class="TableRowColor">
 *         <td align="left" nowrap>{@link #getLocale Locale}</td>
 *         <td align="left">Dependency on {@code 'java.util.Locale'} {@code (java.util.Locale)} at specification level 1.1 bound to an instance.</td>
 *       </tr>
 *       <tr class="TableRowColor">
 *         <td align="left" nowrap>{@link #getVerboseOption VerboseOption}</td>
 *         <td align="left">Dependency on {@code 'JOMC CLI Application Option'} {@code (org.apache.commons.cli.Option)} bound to an instance.</td>
 *       </tr>
 *   </table>
 * </p>
 * <p>
 *   <table border="1" width="100%" cellpadding="3" cellspacing="0">
 *     <caption class="TableCaption">Messages</caption>
 *     <tr>
 *       <th align="left" class="TableHeader" scope="col" nowrap>Name</th>
 *       <th align="left" class="TableHeader" scope="col" nowrap>Languages</th>
 *       <th align="left" class="TableHeader" scope="col" nowrap>Default Template</th>
 *     </tr>
 *     <tr class="TableRowColor">
 *       <td align="left" valign="top" nowrap>{@link #getDefaultExceptionMessage defaultExceptionMessage}</td>
 *       <td align="left" valign="top" nowrap>English (default),&nbsp;Deutsch</td>
 *       <td align="left" valign="top" nowrap><pre><code>No details available.</code></pre></td>
 *     </tr>
 *     <tr class="TableRowColor">
 *       <td align="left" valign="top" nowrap>{@link #getDefaultLogLevelInfo defaultLogLevelInfo}</td>
 *       <td align="left" valign="top" nowrap>English (default),&nbsp;Deutsch</td>
 *       <td align="left" valign="top" nowrap><pre><code>Default log level: ''{0}''</code></pre></td>
 *     </tr>
 *     <tr class="TableRowColor">
 *       <td align="left" valign="top" nowrap>{@link #getIllegalArgumentsInfo illegalArgumentsInfo}</td>
 *       <td align="left" valign="top" nowrap>English (default),&nbsp;Deutsch</td>
 *       <td align="left" valign="top" nowrap><pre><code>Illegal arguments. Type &raquo;jomc {0} {1}&laquo; for further information.</code></pre></td>
 *     </tr>
 *     <tr class="TableRowColor">
 *       <td align="left" valign="top" nowrap>{@link #getLogMessage logMessage}</td>
 *       <td align="left" valign="top" nowrap>English (default),&nbsp;Deutsch</td>
 *       <td align="left" valign="top" nowrap><pre><code>[{0}] {1}</code></pre></td>
 *     </tr>
 *     <tr class="TableRowColor">
 *       <td align="left" valign="top" nowrap>{@link #getTimeInfo timeInfo}</td>
 *       <td align="left" valign="top" nowrap>English (default),&nbsp;Deutsch</td>
 *       <td align="left" valign="top" nowrap><pre><code>{0,time,HH:mm:ss.SSS}</code></pre></td>
 *     </tr>
 *     <tr class="TableRowColor">
 *       <td align="left" valign="top" nowrap>{@link #getUsage usage}</td>
 *       <td align="left" valign="top" nowrap>English (default),&nbsp;Deutsch</td>
 *       <td align="left" valign="top" nowrap><pre><code>Type &raquo;jomc &lt;command&gt; {0}&laquo; for further information.</code></pre></td>
 *     </tr>
 *   </table>
 * </p>
 *
 * @author <a href="mailto:schulte2005@users.sourceforge.net">Christian Schulte</a> 1.0
 * @version $Id$
 */
// </editor-fold>
// SECTION-END
// SECTION-START[Annotations]
// <editor-fold defaultstate="collapsed" desc=" Generated Annotations ">
@javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.2-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.2/jomc-tools-1.2-SNAPSHOT" )
// </editor-fold>
// SECTION-END
public final class Jomc
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

            if ( this.isLoggable( Level.CONFIG ) )
            {
                this.log( Level.CONFIG,
                          this.getDefaultLogLevelInfo( this.getLocale(), this.logLevel.getLocalizedName() ), null );

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
            DefaultModletProvider.setDefaultModletLocation( "META-INF/jomc-modlet.xml" );

            final StringBuilder commandInfo = new StringBuilder();

            for ( Command c : this.getCommands() )
            {
                if ( cmd == null && args != null && args.length > 0
                     && ( args[0].equals( c.getName() ) || args[0].equals( c.getAbbreviatedName() ) ) )
                {
                    cmd = c;
                }

                commandInfo.append( StringUtils.rightPad( c.getName(), 25 ) ).append( " : " ).
                    append( c.getShortDescription( this.getLocale() ) ).append( " (" ).append( c.getAbbreviatedName() ).
                    append( ")" ).append( System.getProperty( "line.separator", "\n" ) );

            }

            if ( cmd == null )
            {
                this.getPrintWriter().println( this.getUsage( this.getLocale(), this.getHelpCommandName() ) );
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
            DefaultModletProvider.setDefaultModletLocation( null );

            final CommandLine commandLine = this.getCommandLineParser().parse( options, commandArguments );
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

            if ( this.isLoggable( Level.FINER ) )
            {
                for ( int i = 0; i < args.length; i++ )
                {
                    this.log( Level.FINER, new StringBuilder().append( "[" ).append( i ).append( "] -> '" ).
                        append( args[i] ).append( "'" ).append( System.getProperty( "line.separator" ) ).
                        toString(), null );

                }
            }

            final boolean failOnWarnings = commandLine.hasOption( this.getFailOnWarningsOption().getOpt() );

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
            this.log( Level.SEVERE, this.getIllegalArgumentsInfo(
                this.getLocale(), cmd.getName(), this.getHelpCommandName() ), e );

            return Command.STATUS_FAILURE;
        }
        catch ( final Throwable t )
        {
            this.log( Level.SEVERE, null, t );
            return Command.STATUS_FAILURE;
        }
        finally
        {
            DefaultModelProvider.setDefaultModuleLocation( null );
            DefaultModelProcessor.setDefaultTransformerLocation( null );
            DefaultModletProvider.setDefaultModletLocation( null );
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
                final String m = getMessage( throwable );

                if ( m != null && m.length() > 0 )
                {
                    this.getPrintWriter().print( this.formatLogLines( level, m ) );
                }
                else
                {
                    this.getPrintWriter().print( this.formatLogLines(
                        level, this.getDefaultExceptionMessage( this.getLocale() ) ) );

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
            final StringBuilder lines = new StringBuilder( text.length() );
            final BufferedReader reader = new BufferedReader( new StringReader( text ) );

            String line;
            while ( ( line = reader.readLine() ) != null )
            {
                final boolean debug = this.getLogLevel().intValue() < Level.INFO.intValue();
                lines.append( "[" ).append( level.getLocalizedName() );

                if ( debug )
                {
                    lines.append( "|" ).append( Thread.currentThread().getName() ).append( "|" ).
                        append( this.getTimeInfo( this.getLocale(), new Date( System.currentTimeMillis() ) ) );

                }

                lines.append( "] " ).append( line ).append( System.getProperty( "line.separator", "\n" ) );
            }

            return lines.toString();
        }
        catch ( final IOException e )
        {
            throw new AssertionError( e );
        }
    }

    private static String getMessage( final Throwable t )
    {
        return t != null ? t.getMessage() != null ? t.getMessage() : getMessage( t.getCause() ) : null;
    }

    // SECTION-END
    // SECTION-START[Constructors]
    // <editor-fold defaultstate="collapsed" desc=" Generated Constructors ">

    /** Creates a new {@code Jomc} instance. */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.2-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.2/jomc-tools-1.2-SNAPSHOT" )
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
     * Gets the {@code CommandLineParser} dependency.
     * <p>This method returns the {@code 'Commons CLI - GNU Command Line Parser'} object of the {@code 'org.apache.commons.cli.CommandLineParser'} {@code (org.apache.commons.cli.CommandLineParser)} specification.</p>
     * <p>That specification does not apply to any scope. A new object is returned whenever requested.</p>
     * @return The {@code CommandLineParser} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.2-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.2/jomc-tools-1.2-SNAPSHOT" )
    private org.apache.commons.cli.CommandLineParser getCommandLineParser()
    {
        final org.apache.commons.cli.CommandLineParser _d = (org.apache.commons.cli.CommandLineParser) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "CommandLineParser" );
        assert _d != null : "'CommandLineParser' dependency not found.";
        return _d;
    }

    /**
     * Gets the {@code Commands} dependency.
     * <p>This method returns any available object of the {@code 'JOMC CLI Command'} {@code (org.jomc.cli.Command)} specification at specification level 1.0.</p>
     * <p>That specification does not apply to any scope. A new object is returned whenever requested.</p>
     * @return The {@code Commands} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.2-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.2/jomc-tools-1.2-SNAPSHOT" )
    private org.jomc.cli.Command[] getCommands()
    {
        final org.jomc.cli.Command[] _d = (org.jomc.cli.Command[]) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "Commands" );
        assert _d != null : "'Commands' dependency not found.";
        return _d;
    }

    /**
     * Gets the {@code DebugOption} dependency.
     * <p>This method returns the {@code 'JOMC CLI Debug Option'} object of the {@code 'JOMC CLI Application Option'} {@code (org.apache.commons.cli.Option)} specification.</p>
     * <p>That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.</p>
     * @return The {@code DebugOption} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.2-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.2/jomc-tools-1.2-SNAPSHOT" )
    private org.apache.commons.cli.Option getDebugOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "DebugOption" );
        assert _d != null : "'DebugOption' dependency not found.";
        return _d;
    }

    /**
     * Gets the {@code FailOnWarningsOption} dependency.
     * <p>This method returns the {@code 'JOMC CLI Fail-On-Warnings Option'} object of the {@code 'JOMC CLI Application Option'} {@code (org.apache.commons.cli.Option)} specification.</p>
     * <p>That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.</p>
     * @return The {@code FailOnWarningsOption} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.2-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.2/jomc-tools-1.2-SNAPSHOT" )
    private org.apache.commons.cli.Option getFailOnWarningsOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "FailOnWarningsOption" );
        assert _d != null : "'FailOnWarningsOption' dependency not found.";
        return _d;
    }

    /**
     * Gets the {@code Locale} dependency.
     * <p>This method returns the {@code 'default'} object of the {@code 'java.util.Locale'} {@code (java.util.Locale)} specification at specification level 1.1.</p>
     * <p>That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.</p>
     * @return The {@code Locale} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.2-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.2/jomc-tools-1.2-SNAPSHOT" )
    private java.util.Locale getLocale()
    {
        final java.util.Locale _d = (java.util.Locale) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "Locale" );
        assert _d != null : "'Locale' dependency not found.";
        return _d;
    }

    /**
     * Gets the {@code VerboseOption} dependency.
     * <p>This method returns the {@code 'JOMC CLI Verbose Option'} object of the {@code 'JOMC CLI Application Option'} {@code (org.apache.commons.cli.Option)} specification.</p>
     * <p>That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.</p>
     * @return The {@code VerboseOption} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.2-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.2/jomc-tools-1.2-SNAPSHOT" )
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
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.2-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.2/jomc-tools-1.2-SNAPSHOT" )
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
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.2-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.2/jomc-tools-1.2-SNAPSHOT" )
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
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.2-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.2/jomc-tools-1.2-SNAPSHOT" )
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
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.2-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.2/jomc-tools-1.2-SNAPSHOT" )
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
     * Gets the text of the {@code defaultExceptionMessage} message.
     * <p><strong>Templates:</strong>
     *   <table border="1" width="100%" cellpadding="3" cellspacing="0">
     *     <tr class="TableSubHeadingColor">
     *       <th align="left" class="TableHeader" scope="col" nowrap>Language</th>
     *       <th align="left" class="TableHeader" scope="col" nowrap>Template</th>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>English (default)</td>
     *       <td align="left" valign="top" nowrap><pre><code>No details available.</code></pre></td>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>Deutsch</td>
     *       <td align="left" valign="top" nowrap><pre><code>Keine Details verf&uuml;gbar.</code></pre></td>
     *     </tr>
     *   </table>
     * </p>
     *
     * @param locale The locale of the message to return.
     * @return The text of the {@code defaultExceptionMessage} message for {@code locale}.
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.2-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.2/jomc-tools-1.2-SNAPSHOT" )
    private String getDefaultExceptionMessage( final java.util.Locale locale )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "defaultExceptionMessage", locale );
        assert _m != null : "'defaultExceptionMessage' message not found.";
        return _m;
    }

    /**
     * Gets the text of the {@code defaultLogLevelInfo} message.
     * <p><strong>Templates:</strong>
     *   <table border="1" width="100%" cellpadding="3" cellspacing="0">
     *     <tr class="TableSubHeadingColor">
     *       <th align="left" class="TableHeader" scope="col" nowrap>Language</th>
     *       <th align="left" class="TableHeader" scope="col" nowrap>Template</th>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>English (default)</td>
     *       <td align="left" valign="top" nowrap><pre><code>Default log level: ''{0}''</code></pre></td>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>Deutsch</td>
     *       <td align="left" valign="top" nowrap><pre><code>Standard Protokollierungsstufe: ''{0}''</code></pre></td>
     *     </tr>
     *   </table>
     * </p>
     *
     * @param locale The locale of the message to return.
     * @param defaultLogLevel Format argument.
     * @return The text of the {@code defaultLogLevelInfo} message for {@code locale}.
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.2-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.2/jomc-tools-1.2-SNAPSHOT" )
    private String getDefaultLogLevelInfo( final java.util.Locale locale, final java.lang.String defaultLogLevel )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "defaultLogLevelInfo", locale, defaultLogLevel );
        assert _m != null : "'defaultLogLevelInfo' message not found.";
        return _m;
    }

    /**
     * Gets the text of the {@code illegalArgumentsInfo} message.
     * <p><strong>Templates:</strong>
     *   <table border="1" width="100%" cellpadding="3" cellspacing="0">
     *     <tr class="TableSubHeadingColor">
     *       <th align="left" class="TableHeader" scope="col" nowrap>Language</th>
     *       <th align="left" class="TableHeader" scope="col" nowrap>Template</th>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>English (default)</td>
     *       <td align="left" valign="top" nowrap><pre><code>Illegal arguments. Type &raquo;jomc {0} {1}&laquo; for further information.</code></pre></td>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>Deutsch</td>
     *       <td align="left" valign="top" nowrap><pre><code>Ung&uuml;ltige Argumente. Geben Sie &raquo;jomc {0} {1}&laquo; f&uuml;r weitere Informationen ein.</code></pre></td>
     *     </tr>
     *   </table>
     * </p>
     *
     * @param locale The locale of the message to return.
     * @param command Format argument.
     * @param helpCommandName Format argument.
     * @return The text of the {@code illegalArgumentsInfo} message for {@code locale}.
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.2-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.2/jomc-tools-1.2-SNAPSHOT" )
    private String getIllegalArgumentsInfo( final java.util.Locale locale, final java.lang.String command, final java.lang.String helpCommandName )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "illegalArgumentsInfo", locale, command, helpCommandName );
        assert _m != null : "'illegalArgumentsInfo' message not found.";
        return _m;
    }

    /**
     * Gets the text of the {@code logMessage} message.
     * <p><strong>Templates:</strong>
     *   <table border="1" width="100%" cellpadding="3" cellspacing="0">
     *     <tr class="TableSubHeadingColor">
     *       <th align="left" class="TableHeader" scope="col" nowrap>Language</th>
     *       <th align="left" class="TableHeader" scope="col" nowrap>Template</th>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>English (default)</td>
     *       <td align="left" valign="top" nowrap><pre><code>[{0}] {1}</code></pre></td>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>Deutsch</td>
     *       <td align="left" valign="top" nowrap><pre><code>[{0}] {1}</code></pre></td>
     *     </tr>
     *   </table>
     * </p>
     *
     * @param locale The locale of the message to return.
     * @param level Format argument.
     * @param message Format argument.
     * @return The text of the {@code logMessage} message for {@code locale}.
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.2-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.2/jomc-tools-1.2-SNAPSHOT" )
    private String getLogMessage( final java.util.Locale locale, final java.lang.String level, final java.lang.String message )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "logMessage", locale, level, message );
        assert _m != null : "'logMessage' message not found.";
        return _m;
    }

    /**
     * Gets the text of the {@code timeInfo} message.
     * <p><strong>Templates:</strong>
     *   <table border="1" width="100%" cellpadding="3" cellspacing="0">
     *     <tr class="TableSubHeadingColor">
     *       <th align="left" class="TableHeader" scope="col" nowrap>Language</th>
     *       <th align="left" class="TableHeader" scope="col" nowrap>Template</th>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>English (default)</td>
     *       <td align="left" valign="top" nowrap><pre><code>{0,time,HH:mm:ss.SSS}</code></pre></td>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>Deutsch</td>
     *       <td align="left" valign="top" nowrap><pre><code>{0,time,HH:mm:ss.SSS}</code></pre></td>
     *     </tr>
     *   </table>
     * </p>
     *
     * @param locale The locale of the message to return.
     * @param time Format argument.
     * @return The text of the {@code timeInfo} message for {@code locale}.
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.2-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.2/jomc-tools-1.2-SNAPSHOT" )
    private String getTimeInfo( final java.util.Locale locale, final java.util.Date time )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "timeInfo", locale, time );
        assert _m != null : "'timeInfo' message not found.";
        return _m;
    }

    /**
     * Gets the text of the {@code usage} message.
     * <p><strong>Templates:</strong>
     *   <table border="1" width="100%" cellpadding="3" cellspacing="0">
     *     <tr class="TableSubHeadingColor">
     *       <th align="left" class="TableHeader" scope="col" nowrap>Language</th>
     *       <th align="left" class="TableHeader" scope="col" nowrap>Template</th>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>English (default)</td>
     *       <td align="left" valign="top" nowrap><pre><code>Type &raquo;jomc &lt;command&gt; {0}&laquo; for further information.</code></pre></td>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>Deutsch</td>
     *       <td align="left" valign="top" nowrap><pre><code>Geben Sie &raquo;jomc &lt;Befehl&gt; {0}&laquo; f&uuml;r weitere Informationen ein.</code></pre></td>
     *     </tr>
     *   </table>
     * </p>
     *
     * @param locale The locale of the message to return.
     * @param helpCommandName Format argument.
     * @return The text of the {@code usage} message for {@code locale}.
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.2-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.2/jomc-tools-1.2-SNAPSHOT" )
    private String getUsage( final java.util.Locale locale, final java.lang.String helpCommandName )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "usage", locale, helpCommandName );
        assert _m != null : "'usage' message not found.";
        return _m;
    }
    // </editor-fold>
    // SECTION-END
}
