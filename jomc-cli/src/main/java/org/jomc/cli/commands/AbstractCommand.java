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
package org.jomc.cli.commands;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import org.apache.commons.cli.CommandLine;
import org.jomc.cli.Command;

/**
 * Base {@code Command} implementation.
 *
 * @author <a href="mailto:cs@schulte.it">Christian Schulte</a>
 */
public abstract class AbstractCommand implements Command
{

    /**
     * Default log level.
     */
    private static volatile Level defaultLogLevel;

    /**
     * Log level of the instance.
     */
    private volatile Level logLevel;

    /**
     * The listeners of the instance.
     */
    private volatile List<Listener> listeners = new CopyOnWriteArrayList<Listener>();

    /**
     * The {@code ExecutorService} of the command.
     *
     * @since 1.10
     */
    private volatile ExecutorService executorService;

    /**
     * Creates a new {@code AbstractCommand} instance.
     */
    public AbstractCommand()
    {
        super();
    }

    /**
     * Gets the default log level events are logged at.
     * <p>
     * The default log level is controlled by system property
     * {@code org.jomc.cli.commands.AbstractCommand.defaultLogLevel} holding the log level to log events at by
     * default. If that property is not set, the {@code WARNING} default is returned.
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
                "org.jomc.cli.commands.AbstractCommand.defaultLogLevel", Level.WARNING.getName() ) );

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
    public final Level getLogLevel()
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
    public final void setLogLevel( final Level value )
    {
        this.logLevel = value;
    }

    /**
     * Gets the list of registered listeners.
     * <p>
     * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make
     * to the returned list will be present inside the object. This is why there is no {@code set} method for the
     * listeners property.
     * </p>
     *
     * @return The list of registered listeners.
     *
     * @see #log(java.util.logging.Level, java.lang.String, java.lang.Throwable)
     */
    public final List<Listener> getListeners()
    {
        return this.listeners;
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
    protected boolean isLoggable( final Level level )
    {
        if ( level == null )
        {
            throw new NullPointerException( "level" );
        }

        return level.intValue() >= this.getLogLevel().intValue();
    }

    /**
     * Notifies registered listeners.
     *
     * @param level The level of the event.
     * @param message The message of the event or {@code null}.
     * @param throwable The throwable of the event {@code null}.
     *
     * @throws NullPointerException if {@code level} is {@code null}.
     *
     * @see #getListeners()
     * @see #isLoggable(java.util.logging.Level)
     */
    protected void log( final Level level, final String message, final Throwable throwable )
    {
        if ( level == null )
        {
            throw new NullPointerException( "level" );
        }

        if ( this.isLoggable( level ) )
        {
            for ( final Listener l : this.getListeners() )
            {
                l.onLog( level, message, throwable );
            }
        }
    }

    @Override
    public org.apache.commons.cli.Options getOptions()
    {
        final org.apache.commons.cli.Options options = new org.apache.commons.cli.Options();
        options.addOption( Options.THREADS_OPTION );
        return options;
    }

    /**
     * Gets the {@code ExecutorService} used to run tasks in parallel.
     *
     * @param commandLine The {@code CommandLine} to use for setting up an executor service when not already created.
     *
     * @return The {@code ExecutorService} used to run tasks in parallel or {@code null}.
     *
     * @since 1.10
     */
    protected final ExecutorService getExecutorService( final CommandLine commandLine )
    {
        if ( this.executorService == null )
        {
            final String formular =
                commandLine.hasOption( Options.THREADS_OPTION.getOpt() )
                    ? commandLine.getOptionValue( Options.THREADS_OPTION.getOpt() ).toLowerCase( new Locale( "" ) )
                    : "1.0c";

            final Double parallelism =
                formular.contains( "c" )
                    ? Double.valueOf( formular.replace( "c", "" ) ) * Runtime.getRuntime().availableProcessors()
                    : Double.valueOf( formular );

            if ( parallelism.intValue() > 1 )
            {
                this.executorService = Executors.newFixedThreadPool(
                    parallelism.intValue(), new ThreadFactory()
                {

                    private final ThreadGroup group;

                    private final AtomicInteger threadNumber = new AtomicInteger( 1 );


                    {
                        final SecurityManager s = System.getSecurityManager();
                        this.group = s != null
                                         ? s.getThreadGroup()
                                         : Thread.currentThread().getThreadGroup();

                    }

                    @Override
                    public Thread newThread( final Runnable r )
                    {
                        final Thread t =
                            new Thread( this.group, r, "jomc-cli-" + this.threadNumber.getAndIncrement(), 0 );

                        if ( t.isDaemon() )
                        {
                            t.setDaemon( false );
                        }
                        if ( t.getPriority() != Thread.NORM_PRIORITY )
                        {
                            t.setPriority( Thread.NORM_PRIORITY );
                        }

                        return t;
                    }

                } );
            }
        }

        return this.executorService;
    }

    @Override
    public final int execute( final CommandLine commandLine )
    {
        if ( commandLine == null )
        {
            throw new NullPointerException( "commandLine" );
        }

        int status = STATUS_FAILURE;

        try
        {
            if ( this.isLoggable( Level.INFO ) )
            {
                this.log( Level.INFO, Messages.getMessage( "separator" ), null );
                this.log( Level.INFO, Messages.getMessage( "applicationTitle" ), null );
                this.log( Level.INFO, Messages.getMessage( "separator" ), null );
                this.log( Level.INFO, Messages.getMessage( "commandInfo", this.getName() ), null );
            }

            this.preExecuteCommand( commandLine );
            this.executeCommand( commandLine );
            status = STATUS_SUCCESS;
        }
        catch ( final Throwable t )
        {
            this.log( Level.SEVERE, null, t );
            status = STATUS_FAILURE;
        }
        finally
        {
            try
            {
                this.postExecuteCommand( commandLine );
            }
            catch ( final Throwable t )
            {
                this.log( Level.SEVERE, null, t );
                status = STATUS_FAILURE;
            }
            finally
            {
                if ( this.executorService != null )
                {
                    this.executorService.shutdown();
                    this.executorService = null;
                }
            }
        }

        if ( this.isLoggable( Level.INFO ) )
        {
            if ( status == STATUS_SUCCESS )
            {
                this.log( Level.INFO, Messages.getMessage( "commandSuccess", this.getName() ), null );
            }
            else if ( status == STATUS_FAILURE )
            {
                this.log( Level.INFO, Messages.getMessage( "commandFailure", this.getName() ), null );
            }

            this.log( Level.INFO, Messages.getMessage( "separator" ), null );
        }

        return status;
    }

    /**
     * Called by the {@code execute} method prior to the {@code executeCommand} method.
     *
     * @param commandLine The command line to execute.
     *
     * @throws NullPointerException if {@code commandLine} is {@code null}.
     * @throws CommandExecutionException if executing the command fails.
     *
     * @see #execute(org.apache.commons.cli.CommandLine)
     */
    protected void preExecuteCommand( final CommandLine commandLine ) throws CommandExecutionException
    {
        if ( commandLine == null )
        {
            throw new NullPointerException( "commandLine" );
        }
    }

    /**
     * Called by the {@code execute} method prior to the {@code postExecuteCommand} method.
     *
     * @param commandLine The command line to execute.
     *
     * @throws CommandExecutionException if executing the command fails.
     *
     * @see #execute(org.apache.commons.cli.CommandLine)
     */
    protected abstract void executeCommand( final CommandLine commandLine ) throws CommandExecutionException;

    /**
     * Called by the {@code execute} method after the {@code preExecuteCommand}/{@code executeCommand} methods even if
     * those methods threw an exception.
     *
     * @param commandLine The command line to execute.
     *
     * @throws NullPointerException if {@code commandLine} is {@code null}.
     * @throws CommandExecutionException if executing the command fails.
     *
     * @see #execute(org.apache.commons.cli.CommandLine)
     */
    protected void postExecuteCommand( final CommandLine commandLine ) throws CommandExecutionException
    {
        if ( commandLine == null )
        {
            throw new NullPointerException( "commandLine" );
        }
    }

}
