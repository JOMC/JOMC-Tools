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

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;
import java.util.logging.Level;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.jomc.model.Implementation;
import org.jomc.model.Module;
import org.jomc.model.Modules;
import org.jomc.model.Specification;
import org.jomc.model.modlet.ModelHelper;
import org.jomc.modlet.Model;
import org.jomc.tools.JomcTool;

/**
 * {@code JomcTool} based command implementation.
 *
 * @author <a href="mailto:cs@schulte.it">Christian Schulte</a>
 */
public abstract class AbstractJomcToolCommand extends AbstractModelCommand
{

    /**
     * Creates a new {@code AbstractJomcToolCommand} instance.
     */
    public AbstractJomcToolCommand()
    {
        super();
    }

    @Override
    public org.apache.commons.cli.Options getOptions()
    {
        final org.apache.commons.cli.Options options = super.getOptions();
        options.addOption( Options.TEMPLATE_PROFILE_OPTION );
        options.addOption( Options.DEFAULT_TEMPLATE_PROFILE_OPTION );
        options.addOption( Options.DEFAULT_TEMPLATE_ENCODING_OPTION );
        options.addOption( Options.TEMPLATE_LOCATION_OPTION );
        options.addOption( Options.OUTPUT_ENCODING_OPTION );
        options.addOption( Options.INPUT_ENCODING_OPTION );
        options.addOption( Options.INDENTATION_STRING_OPTION );
        options.addOption( Options.LINE_SEPARATOR_OPTION );
        options.addOption( Options.LANGUAGE_OPTION );
        options.addOption( Options.COUNTRY_OPTION );
        options.addOption( Options.LOCALE_VARIANT_OPTION );
        options.addOption( Options.IMPLEMENTATION_OPTION );
        options.addOption( Options.MODULE_OPTION );
        options.addOption( Options.SPECIFICATION_OPTION );
        return options;
    }

    /**
     * Creates a new object for a given class name and type.
     *
     * @param className The name of the class to create an object of.
     * @param type The class of the type of object to create.
     * @param <T> The type of the object to create.
     *
     * @return A new instance of the class with name {@code className}.
     *
     * @throws NullPointerException if {@code className} or {@code type} is {@code null}.
     * @throws CommandExecutionException if creating a new object fails.
     */
    protected <T> T createObject( final String className, final Class<T> type ) throws CommandExecutionException
    {
        if ( className == null )
        {
            throw new NullPointerException( "className" );
        }
        if ( type == null )
        {
            throw new NullPointerException( "type" );
        }

        try
        {
            return Class.forName( className ).asSubclass( type ).newInstance();
        }
        catch ( final InstantiationException e )
        {
            throw new CommandExecutionException( Messages.getMessage( "objectCreationFailure", className ), e );
        }
        catch ( final IllegalAccessException e )
        {
            throw new CommandExecutionException( Messages.getMessage( "objectCreationFailure", className ), e );
        }
        catch ( final ClassNotFoundException e )
        {
            throw new CommandExecutionException( Messages.getMessage( "objectCreationFailure", className ), e );
        }
        catch ( final ClassCastException e )
        {
            throw new CommandExecutionException( Messages.getMessage( "objectCreationFailure", className ), e );
        }
    }

    /**
     * Creates a new {@code JomcTool} object for a given class name and type.
     *
     * @param commandLine The {@code CommandLine} to configure the new {@code JomcTool} object with.
     * @param className The name of the class to create an object of.
     * @param type The class of the type of object to create.
     * @param <T> The type of the object to create.
     *
     * @return A new instance of the class with name {@code className} configured using {@code commandLine}.
     *
     * @throws NullPointerException if {@code commandLine}, {@code className} or {@code type} is {@code null}.
     * @throws CommandExecutionException if creating a new object fails.
     *
     * @see #createObject(java.lang.String, java.lang.Class)
     */
    protected <T extends JomcTool> T createJomcTool( final String className, final Class<T> type,
                                                     final CommandLine commandLine ) throws CommandExecutionException
    {
        if ( commandLine == null )
        {
            throw new NullPointerException( "commandLine" );
        }
        if ( className == null )
        {
            throw new NullPointerException( "className" );
        }
        if ( type == null )
        {
            throw new NullPointerException( "type" );
        }

        final T tool = this.createObject( className, type );
        tool.setLogLevel( this.getLogLevel() );
        tool.setExecutorService( this.getExecutorService( commandLine ) );
        tool.setLocale( this.getLocale( commandLine ) );
        tool.getListeners().add( new JomcTool.Listener()
        {

            @Override
            public void onLog( final Level level, final String message, final Throwable throwable )
            {
                super.onLog( level, message, throwable );
                log( level, message, throwable );
            }

        } );

        if ( commandLine.hasOption( Options.TEMPLATE_ENCODING_OPTION.getOpt() ) )
        {
            this.log( Level.WARNING, Messages.getMessage( "deprecatedOptionMessage",
                                                          Options.TEMPLATE_ENCODING_OPTION.getLongOpt(),
                                                          Options.DEFAULT_TEMPLATE_ENCODING_OPTION.getLongOpt() ),
                      null );

            tool.setDefaultTemplateEncoding( commandLine.getOptionValue( Options.TEMPLATE_ENCODING_OPTION.getOpt() ) );
        }
        else if ( commandLine.hasOption( Options.DEFAULT_TEMPLATE_ENCODING_OPTION.getOpt() ) )
        {
            tool.setDefaultTemplateEncoding(
                commandLine.getOptionValue( Options.DEFAULT_TEMPLATE_ENCODING_OPTION.getOpt() ) );

        }
        if ( commandLine.hasOption( Options.DEFAULT_TEMPLATE_PROFILE_OPTION.getOpt() ) )
        {
            tool.setDefaultTemplateProfile(
                commandLine.getOptionValue( Options.DEFAULT_TEMPLATE_PROFILE_OPTION.getOpt() ) );

        }
        if ( commandLine.hasOption( Options.TEMPLATE_PROFILE_OPTION.getOpt() ) )
        {
            tool.setTemplateProfile( commandLine.getOptionValue( Options.TEMPLATE_PROFILE_OPTION.getOpt() ) );
        }
        if ( commandLine.hasOption( Options.TEMPLATE_LOCATION_OPTION.getOpt() ) )
        {
            try
            {
                tool.setTemplateLocation( new URL(
                    commandLine.getOptionValue( Options.TEMPLATE_LOCATION_OPTION.getOpt() ) ) );

            }
            catch ( final MalformedURLException e )
            {
                this.log( Level.FINER, null, e );

                try
                {
                    tool.setTemplateLocation( new File(
                        commandLine.getOptionValue( Options.TEMPLATE_LOCATION_OPTION.getOpt() ) ).toURI().toURL() );

                }
                catch ( final MalformedURLException e2 )
                {
                    throw new CommandExecutionException( Messages.getMessage( e2 ), e2 );
                }
            }
        }
        if ( commandLine.hasOption( Options.INPUT_ENCODING_OPTION.getOpt() ) )
        {
            tool.setInputEncoding( commandLine.getOptionValue( Options.INPUT_ENCODING_OPTION.getOpt() ) );
        }
        if ( commandLine.hasOption( Options.OUTPUT_ENCODING_OPTION.getOpt() ) )
        {
            tool.setOutputEncoding( commandLine.getOptionValue( Options.OUTPUT_ENCODING_OPTION.getOpt() ) );
        }
        if ( commandLine.hasOption( Options.INDENTATION_STRING_OPTION.getOpt() ) )
        {
            tool.setIndentation( StringEscapeUtils.unescapeJava(
                commandLine.getOptionValue( Options.INDENTATION_STRING_OPTION.getOpt() ) ) );

        }
        if ( commandLine.hasOption( Options.LINE_SEPARATOR_OPTION.getOpt() ) )
        {
            tool.setLineSeparator( StringEscapeUtils.unescapeJava(
                commandLine.getOptionValue( Options.LINE_SEPARATOR_OPTION.getOpt() ) ) );

        }

        return tool;
    }

    /**
     * Gets the specification to process from a given model.
     *
     * @param commandLine The command line specifying the specification to process.
     * @param model The model to get the specification to process from.
     *
     * @return The specification to process or {@code null}.
     *
     * @throws NullPointerException if {@code commandLine} or {@code model} is {@code null}.
     */
    protected final Specification getSpecification( final CommandLine commandLine, final Model model )
    {
        if ( commandLine == null )
        {
            throw new NullPointerException( "commandLine" );
        }
        if ( model == null )
        {
            throw new NullPointerException( "model" );
        }

        Specification s = null;

        if ( commandLine.hasOption( Options.SPECIFICATION_OPTION.getOpt() ) )
        {
            final String identifier = commandLine.getOptionValue( Options.SPECIFICATION_OPTION.getOpt() );
            final Modules modules = ModelHelper.getModules( model );

            if ( modules != null )
            {
                s = modules.getSpecification( identifier );
            }

            if ( s == null )
            {
                this.log( Level.WARNING, Messages.getMessage( "specificationNotFoundWarning", identifier ), null );
            }
        }

        return s;
    }

    /**
     * Gets the implementation to process from a given model.
     *
     * @param commandLine The command line specifying the implementation to process.
     * @param model The model to get the implementation to process from.
     *
     * @return The implementation to process or {@code null}.
     *
     * @throws NullPointerException if {@code commandLine} or {@code model} is {@code null}.
     */
    protected final Implementation getImplementation( final CommandLine commandLine, final Model model )
    {
        if ( commandLine == null )
        {
            throw new NullPointerException( "commandLine" );
        }
        if ( model == null )
        {
            throw new NullPointerException( "model" );
        }

        Implementation i = null;

        if ( commandLine.hasOption( Options.IMPLEMENTATION_OPTION.getOpt() ) )
        {
            final String identifier = commandLine.getOptionValue( Options.IMPLEMENTATION_OPTION.getOpt() );
            final Modules modules = ModelHelper.getModules( model );

            if ( modules != null )
            {
                i = modules.getImplementation( identifier );
            }

            if ( i == null )
            {
                this.log( Level.WARNING, Messages.getMessage( "implementationNotFoundWarning", identifier ), null );
            }
        }

        return i;
    }

    /**
     * Gets the module to process from a given model.
     *
     * @param commandLine The command line specifying the implementation to process.
     * @param model The model to get the module to process from.
     *
     * @return The module to process or {@code null}.
     *
     * @throws NullPointerException if {@code model} is {@code null}.
     */
    protected final Module getModule( final CommandLine commandLine, final Model model )
    {
        if ( commandLine == null )
        {
            throw new NullPointerException( "commandLine" );
        }
        if ( model == null )
        {
            throw new NullPointerException( "model" );
        }

        Module m = null;

        if ( commandLine.hasOption( Options.MODULE_OPTION.getOpt() ) )
        {
            final String name = commandLine.getOptionValue( Options.MODULE_OPTION.getOpt() );
            final Modules modules = ModelHelper.getModules( model );

            if ( modules != null )
            {
                m = modules.getModule( name );
            }

            if ( m == null )
            {
                this.log( Level.WARNING, Messages.getMessage( "moduleNotFoundWarning", name ), null );
            }
        }

        return m;
    }

    /**
     * Gets a flag indicating that all modules are requested to be processed.
     *
     * @param commandLine The command line to process.
     *
     * @return {@code true}, if processing of all modules is requested; {@code false}, else.
     *
     * @throws NullPointerException if {@code commandLine} is {@code null}.
     *
     * @see #getSpecification(org.apache.commons.cli.CommandLine, org.jomc.modlet.Model)
     * @see #getImplementation(org.apache.commons.cli.CommandLine, org.jomc.modlet.Model)
     * @see #getModule(org.apache.commons.cli.CommandLine, org.jomc.modlet.Model)
     */
    protected final boolean isModulesProcessingRequested( final CommandLine commandLine )
    {
        if ( commandLine == null )
        {
            throw new NullPointerException( "commandLine" );
        }

        return !( commandLine.hasOption( Options.SPECIFICATION_OPTION.getOpt() )
                  || commandLine.hasOption( Options.IMPLEMENTATION_OPTION.getOpt() )
                  || commandLine.hasOption( Options.MODULE_OPTION.getOpt() ) );

    }

    /**
     * Gets a locale from a command line.
     *
     * @param commandLine The command line to get a locale from.
     *
     * @return The locale from {@code commandLine} or {@code null}, if {@code commandLine} does not hold options
     * specifying a locale.
     */
    protected final Locale getLocale( final CommandLine commandLine )
    {
        if ( commandLine == null )
        {
            throw new NullPointerException( "commandLine" );
        }

        Locale locale = null;

        final String lc = commandLine.hasOption( Options.LANGUAGE_OPTION.getOpt() )
                              ? commandLine.getOptionValue( Options.LANGUAGE_OPTION.getOpt() )
                              : null;

        final String cc = commandLine.hasOption( Options.COUNTRY_OPTION.getOpt() )
                              ? commandLine.getOptionValue( Options.COUNTRY_OPTION.getOpt() )
                              : null;

        final String lv = commandLine.hasOption( Options.LOCALE_VARIANT_OPTION.getOpt() )
                              ? commandLine.getOptionValue( Options.LOCALE_VARIANT_OPTION.getOpt() )
                              : null;

        if ( lc != null || cc != null || lv != null )
        {
            locale = new Locale( StringUtils.defaultString( lc ),
                                 StringUtils.defaultString( cc ),
                                 StringUtils.defaultString( lv ) );

        }

        return locale;
    }

}
