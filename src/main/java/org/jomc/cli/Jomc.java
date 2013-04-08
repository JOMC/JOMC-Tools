// SECTION-START[License Header]
// <editor-fold defaultstate="collapsed" desc=" Generated License ">
/*
 *   Java Object Management and Configuration
 *   Copyright (C) Christian Schulte, 2005-206
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
 *   THIS SOFTWARE IS PROVIDED "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 *   INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY
 *   AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 *   THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY DIRECT, INDIRECT,
 *   INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 *   NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 *   DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 *   THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *   (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 *   THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *   $JOMC$
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
 *
 * <dl>
 *   <dt><b>Identifier:</b></dt><dd>JOMC ⁑ CLI ⁑ Application</dd>
 *   <dt><b>Name:</b></dt><dd>JOMC ⁑ CLI ⁑ Application</dd>
 *   <dt><b>Abstract:</b></dt><dd>No</dd>
 *   <dt><b>Final:</b></dt><dd>No</dd>
 *   <dt><b>Stateless:</b></dt><dd>No</dd>
 * </dl>
 *
 * @author <a href="mailto:cs@schulte.it">Christian Schulte</a> 1.0
 * @version 2.0-SNAPSHOT
 */
// </editor-fold>
// SECTION-END
// SECTION-START[Annotations]
// <editor-fold defaultstate="collapsed" desc=" Generated Annotations ">
@javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://www.jomc.org/jomc/2.0/jomc-tools-2.0-SNAPSHOT" )
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
     *
     * @see #setPrintWriter(java.io.PrintWriter)
     */
    public PrintWriter getPrintWriter()
    {
        if ( this.printWriter == null )
        {
            // JDK: As of JDK 6, "this.printWriter = System.console().writer()".
            this.printWriter = new PrintWriter( System.out, true );
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
                assert !pw.checkError() : "Unexpected error printing usage.";

                pw = new PrintWriter( opts );
                formatter.printOptions( pw, this.getWidth(), options, this.getLeftPad(), this.getDescPad() );
                pw.close();
                assert !pw.checkError() : "Unexpected error printing options.";

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
                        append( args[i] ).append( "'" ).append( System.getProperty( "line.separator", "\n" ) ).
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
        BufferedReader reader = null;
        boolean suppressExceptionOnClose = true;

        try
        {
            final StringBuilder lines = new StringBuilder( text.length() );
            reader = new BufferedReader( new StringReader( text ) );

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

            suppressExceptionOnClose = false;
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
                if ( suppressExceptionOnClose )
                {
                    this.log( Level.SEVERE, getMessage( e ), e );
                }
                else
                {
                    throw new AssertionError( e );
                }
            }
        }
    }

    private static String getMessage( final Throwable t )
    {
        return t != null
               ? t.getMessage() != null && t.getMessage().trim().length() > 0
                 ? t.getMessage()
                 : getMessage( t.getCause() )
               : null;

    }

    // SECTION-END
    // SECTION-START[Constructors]
    // <editor-fold defaultstate="collapsed" desc=" Generated Constructors ">
    /** Creates a new {@code Jomc} instance. */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://www.jomc.org/jomc/2.0/jomc-tools-2.0-SNAPSHOT" )
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
     * Gets the {@code <Command Line Parser>} dependency.
     * <p>
     *   This method returns the {@code <Commons CLI - GNU Command Line Parser>} object of the {@code <org.apache.commons.cli.CommandLineParser>} specification at any specification level.
     *   That specification does not apply to any scope. A new object is returned whenever requested.
     * </p>
     * <dl>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl>
     * @return The {@code <Command Line Parser>} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @SuppressWarnings("unused")
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://www.jomc.org/jomc/2.0/jomc-tools-2.0-SNAPSHOT" )
    private org.apache.commons.cli.CommandLineParser getCommandLineParser()
    {
        final org.apache.commons.cli.CommandLineParser _d = (org.apache.commons.cli.CommandLineParser) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "Command Line Parser" );
        assert _d != null : "'Command Line Parser' dependency not found.";
        return _d;
    }
    /**
     * Gets the {@code <Commands>} dependency.
     * <p>
     *   This method returns any available object of the {@code <JOMC ⁑ CLI ⁑ Command>} specification at specification level 1.0.
     *   That specification does not apply to any scope. A new object is returned whenever requested.
     * </p>
     * <dl>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl>
     * @return The {@code <Commands>} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @SuppressWarnings("unused")
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://www.jomc.org/jomc/2.0/jomc-tools-2.0-SNAPSHOT" )
    private org.jomc.cli.Command[] getCommands()
    {
        final org.jomc.cli.Command[] _d = (org.jomc.cli.Command[]) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "Commands" );
        assert _d != null : "'Commands' dependency not found.";
        return _d;
    }
    /**
     * Gets the {@code <Debug Option>} dependency.
     * <p>
     *   This method returns the {@code <JOMC ⁑ CLI ⁑ Debug Option>} object of the {@code <JOMC ⁑ CLI ⁑ Application Option>} specification at specification level 1.2.
     *   That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.
     * </p>
     * <dl>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl>
     * @return The {@code <Debug Option>} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @SuppressWarnings("unused")
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://www.jomc.org/jomc/2.0/jomc-tools-2.0-SNAPSHOT" )
    private org.apache.commons.cli.Option getDebugOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "Debug Option" );
        assert _d != null : "'Debug Option' dependency not found.";
        return _d;
    }
    /**
     * Gets the {@code <Fail On Warnings Option>} dependency.
     * <p>
     *   This method returns the {@code <JOMC ⁑ CLI ⁑ Fail-On-Warnings Option>} object of the {@code <JOMC ⁑ CLI ⁑ Application Option>} specification at specification level 1.2.
     *   That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.
     * </p>
     * <dl>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl>
     * @return The {@code <Fail On Warnings Option>} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @SuppressWarnings("unused")
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://www.jomc.org/jomc/2.0/jomc-tools-2.0-SNAPSHOT" )
    private org.apache.commons.cli.Option getFailOnWarningsOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "Fail On Warnings Option" );
        assert _d != null : "'Fail On Warnings Option' dependency not found.";
        return _d;
    }
    /**
     * Gets the {@code <Locale>} dependency.
     * <p>
     *   This method returns the {@code <default>} object of the {@code <java.util.Locale>} specification at specification level 1.1.
     *   That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.
     * </p>
     * <dl>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl>
     * @return The {@code <Locale>} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @SuppressWarnings("unused")
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://www.jomc.org/jomc/2.0/jomc-tools-2.0-SNAPSHOT" )
    private java.util.Locale getLocale()
    {
        final java.util.Locale _d = (java.util.Locale) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "Locale" );
        assert _d != null : "'Locale' dependency not found.";
        return _d;
    }
    /**
     * Gets the {@code <Verbose Option>} dependency.
     * <p>
     *   This method returns the {@code <JOMC ⁑ CLI ⁑ Verbose Option>} object of the {@code <JOMC ⁑ CLI ⁑ Application Option>} specification at specification level 1.2.
     *   That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.
     * </p>
     * <dl>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl>
     * @return The {@code <Verbose Option>} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @SuppressWarnings("unused")
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://www.jomc.org/jomc/2.0/jomc-tools-2.0-SNAPSHOT" )
    private org.apache.commons.cli.Option getVerboseOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "Verbose Option" );
        assert _d != null : "'Verbose Option' dependency not found.";
        return _d;
    }
    // </editor-fold>
    // SECTION-END
    // SECTION-START[Properties]
    // <editor-fold defaultstate="collapsed" desc=" Generated Properties ">
    /**
     * Gets the value of the {@code <Desc Pad>} property.
     * <p><dl>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl></p>
     * @return The number of characters of padding to be prefixed to each description line.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @SuppressWarnings("unused")
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://www.jomc.org/jomc/2.0/jomc-tools-2.0-SNAPSHOT" )
    private int getDescPad()
    {
        final java.lang.Integer _p = (java.lang.Integer) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getProperty( this, "Desc Pad" );
        assert _p != null : "'Desc Pad' property not found.";
        return _p.intValue();
    }
    /**
     * Gets the value of the {@code <Help Command Name>} property.
     * <p><dl>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl></p>
     * @return The name of the command used to request help.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @SuppressWarnings("unused")
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://www.jomc.org/jomc/2.0/jomc-tools-2.0-SNAPSHOT" )
    private java.lang.String getHelpCommandName()
    {
        final java.lang.String _p = (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getProperty( this, "Help Command Name" );
        assert _p != null : "'Help Command Name' property not found.";
        return _p;
    }
    /**
     * Gets the value of the {@code <Left Pad>} property.
     * <p><dl>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl></p>
     * @return The number of characters of padding to be prefixed to each line.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @SuppressWarnings("unused")
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://www.jomc.org/jomc/2.0/jomc-tools-2.0-SNAPSHOT" )
    private int getLeftPad()
    {
        final java.lang.Integer _p = (java.lang.Integer) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getProperty( this, "Left Pad" );
        assert _p != null : "'Left Pad' property not found.";
        return _p.intValue();
    }
    /**
     * Gets the value of the {@code <width>} property.
     * <p><dl>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl></p>
     * @return The number of characters per line for the usage statement.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @SuppressWarnings("unused")
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://www.jomc.org/jomc/2.0/jomc-tools-2.0-SNAPSHOT" )
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
     * Gets the text of the {@code <Default Exception Message>} message.
     * <p><dl>
     *   <dt><b>Languages:</b></dt>
     *     <dd>English (default)</dd>
     *     <dd>Deutsch</dd>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl></p>
     * @param locale The locale of the message to return.
     * @return The text of the {@code <Default Exception Message>} message for {@code locale}.
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @SuppressWarnings("unused")
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://www.jomc.org/jomc/2.0/jomc-tools-2.0-SNAPSHOT" )
    private String getDefaultExceptionMessage( final java.util.Locale locale )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "Default Exception Message", locale );
        assert _m != null : "'Default Exception Message' message not found.";
        return _m;
    }
    /**
     * Gets the text of the {@code <Default Log Level Info>} message.
     * <p><dl>
     *   <dt><b>Languages:</b></dt>
     *     <dd>English (default)</dd>
     *     <dd>Deutsch</dd>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl></p>
     * @param locale The locale of the message to return.
     * @param defaultLogLevel Format argument.
     * @return The text of the {@code <Default Log Level Info>} message for {@code locale}.
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @SuppressWarnings("unused")
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://www.jomc.org/jomc/2.0/jomc-tools-2.0-SNAPSHOT" )
    private String getDefaultLogLevelInfo( final java.util.Locale locale, final java.lang.String defaultLogLevel )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "Default Log Level Info", locale, defaultLogLevel );
        assert _m != null : "'Default Log Level Info' message not found.";
        return _m;
    }
    /**
     * Gets the text of the {@code <Illegal Arguments Info>} message.
     * <p><dl>
     *   <dt><b>Languages:</b></dt>
     *     <dd>English (default)</dd>
     *     <dd>Deutsch</dd>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl></p>
     * @param locale The locale of the message to return.
     * @param command Format argument.
     * @param helpCommandName Format argument.
     * @return The text of the {@code <Illegal Arguments Info>} message for {@code locale}.
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @SuppressWarnings("unused")
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://www.jomc.org/jomc/2.0/jomc-tools-2.0-SNAPSHOT" )
    private String getIllegalArgumentsInfo( final java.util.Locale locale, final java.lang.String command, final java.lang.String helpCommandName )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "Illegal Arguments Info", locale, command, helpCommandName );
        assert _m != null : "'Illegal Arguments Info' message not found.";
        return _m;
    }
    /**
     * Gets the text of the {@code <Time Info>} message.
     * <p><dl>
     *   <dt><b>Languages:</b></dt>
     *     <dd>English (default)</dd>
     *     <dd>Deutsch</dd>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl></p>
     * @param locale The locale of the message to return.
     * @param time Format argument.
     * @return The text of the {@code <Time Info>} message for {@code locale}.
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @SuppressWarnings("unused")
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://www.jomc.org/jomc/2.0/jomc-tools-2.0-SNAPSHOT" )
    private String getTimeInfo( final java.util.Locale locale, final java.util.Date time )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "Time Info", locale, time );
        assert _m != null : "'Time Info' message not found.";
        return _m;
    }
    /**
     * Gets the text of the {@code <Usage>} message.
     * <p><dl>
     *   <dt><b>Languages:</b></dt>
     *     <dd>English (default)</dd>
     *     <dd>Deutsch</dd>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl></p>
     * @param locale The locale of the message to return.
     * @param helpCommandName Format argument.
     * @return The text of the {@code <Usage>} message for {@code locale}.
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @SuppressWarnings("unused")
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://www.jomc.org/jomc/2.0/jomc-tools-2.0-SNAPSHOT" )
    private String getUsage( final java.util.Locale locale, final java.lang.String helpCommandName )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "Usage", locale, helpCommandName );
        assert _m != null : "'Usage' message not found.";
        return _m;
    }
    // </editor-fold>
    // SECTION-END
}
