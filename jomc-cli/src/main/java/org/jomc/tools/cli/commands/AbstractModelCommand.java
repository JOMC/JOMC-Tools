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
package org.jomc.tools.cli.commands;

import java.io.File;
import java.util.logging.Level;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import org.apache.commons.cli.CommandLine;
import org.jomc.model.Module;
import org.jomc.model.Modules;
import org.jomc.model.modlet.DefaultModelProcessor;
import org.jomc.model.modlet.DefaultModelProvider;
import org.jomc.model.modlet.DefaultModelValidator;
import org.jomc.model.modlet.ModelHelper;
import org.jomc.modlet.Model;
import org.jomc.modlet.ModelContext;
import org.jomc.modlet.ModelException;
import org.jomc.tools.modlet.ToolsModelProcessor;
import org.jomc.tools.modlet.ToolsModelProvider;

/**
 * {@code Model} based command implementation.
 *
 * @author <a href="mailto:cs@schulte.it">Christian Schulte</a>
 */
public abstract class AbstractModelCommand extends AbstractModletCommand
{

    /**
     * Creates a new {@code AbstractModelCommand} instance.
     */
    public AbstractModelCommand()
    {
        super();
    }

    @Override
    public org.apache.commons.cli.Options getOptions()
    {
        final org.apache.commons.cli.Options options = super.getOptions();
        options.addOption( Options.MODULE_LOCATION_OPTION );
        options.addOption( Options.TRANSFORMER_LOCATION_OPTION );
        options.addOption( Options.NO_CLASSPATH_RESOLUTION_OPTION );
        options.addOption( Options.NO_MODEL_PROCESSING_OPTION );
        options.addOption( Options.NO_MODEL_RESOURCE_VALIDATION_OPTION );
        options.addOption( Options.NO_JAVA_VALIDATION_OPTION );
        return options;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ModelContext createModelContext( final CommandLine commandLine, final ClassLoader classLoader )
        throws CommandExecutionException
    {
        if ( commandLine == null )
        {
            throw new NullPointerException( "commandLine" );
        }

        final ModelContext modelContext = super.createModelContext( commandLine, classLoader );

        if ( commandLine.hasOption( Options.TRANSFORMER_LOCATION_OPTION.getOpt() ) )
        {
            modelContext.setAttribute( DefaultModelProcessor.TRANSFORMER_LOCATION_ATTRIBUTE_NAME,
                                       commandLine.getOptionValue( Options.TRANSFORMER_LOCATION_OPTION.getOpt() ) );

        }

        if ( commandLine.hasOption( Options.MODULE_LOCATION_OPTION.getOpt() ) )
        {
            modelContext.setAttribute( DefaultModelProvider.MODULE_LOCATION_ATTRIBUTE_NAME,
                                       commandLine.getOptionValue( Options.MODULE_LOCATION_OPTION.getOpt() ) );

        }

        modelContext.setAttribute( ToolsModelProvider.MODEL_OBJECT_CLASSPATH_RESOLUTION_ENABLED_ATTRIBUTE_NAME,
                                   !commandLine.hasOption( Options.NO_CLASSPATH_RESOLUTION_OPTION.getOpt() ) );

        modelContext.setAttribute( ToolsModelProcessor.MODEL_OBJECT_CLASSPATH_RESOLUTION_ENABLED_ATTRIBUTE_NAME,
                                   !commandLine.hasOption( Options.NO_CLASSPATH_RESOLUTION_OPTION.getOpt() ) );

        modelContext.setAttribute( DefaultModelProvider.VALIDATING_ATTRIBUTE_NAME,
                                   !commandLine.hasOption( Options.NO_MODEL_RESOURCE_VALIDATION_OPTION.getOpt() ) );

        modelContext.setAttribute( DefaultModelValidator.VALIDATE_JAVA_ATTRIBUTE_NAME,
                                   !commandLine.hasOption( Options.NO_JAVA_VALIDATION_OPTION.getOpt() ) );

        return modelContext;
    }

    /**
     * Gets the model as specified by a given command line.
     *
     * @param context The context to use for getting the model.
     * @param commandLine The command line specifying the model to get.
     *
     * @return The model as specified by {@code commandLine}.
     *
     * @throws CommandExecutionException if getting the model fails.
     */
    protected Model getModel( final ModelContext context, final CommandLine commandLine )
        throws CommandExecutionException
    {
        try
        {
            Model model = new Model();
            model.setIdentifier( this.getModel( commandLine ) );
            Modules modules = new Modules();
            ModelHelper.setModules( model, modules );

            if ( commandLine.hasOption( Options.DOCUMENTS_OPTION.getOpt() ) )
            {
                final Unmarshaller u = context.createUnmarshaller( model.getIdentifier() );

                if ( !commandLine.hasOption( Options.NO_MODEL_RESOURCE_VALIDATION_OPTION.getOpt() ) )
                {
                    u.setSchema( context.createSchema( model.getIdentifier() ) );
                }

                for ( final File f : this.getDocumentFiles( commandLine ) )
                {
                    if ( this.isLoggable( Level.FINEST ) )
                    {
                        this.log( Level.FINEST,
                                  Messages.getMessage( "readingResource", f.getAbsolutePath() ),
                                  null );

                    }

                    Object o = u.unmarshal( f );
                    if ( o instanceof JAXBElement<?> )
                    {
                        o = ( (JAXBElement<?>) o ).getValue();
                    }

                    if ( o instanceof Module )
                    {
                        modules.getModule().add( (Module) o );
                    }
                    else if ( o instanceof Modules )
                    {
                        modules.getModule().addAll( ( (Modules) o ).getModule() );
                    }
                    else if ( this.isLoggable( Level.WARNING ) )
                    {
                        this.log( Level.WARNING,
                                  Messages.getMessage( "failureProcessing", f.getAbsolutePath(), o.toString() ),
                                  null );

                    }
                }
            }

            model = context.findModel( model );
            modules = ModelHelper.getModules( model );

            if ( modules != null && !commandLine.hasOption( Options.NO_CLASSPATH_RESOLUTION_OPTION.getOpt() ) )
            {
                final Module classpathModule = modules.getClasspathModule(
                    Modules.getDefaultClasspathModuleName(), context.getClassLoader() );

                if ( classpathModule != null && modules.getModule( classpathModule.getName() ) == null )
                {
                    modules.getModule().add( classpathModule );
                }
            }

            if ( !commandLine.hasOption( Options.NO_MODEL_PROCESSING_OPTION.getOpt() ) )
            {
                model = context.processModel( model );
                modules = ModelHelper.getModules( model );
            }

            assert modules != null : "Modules '" + this.getModel( commandLine ) + "' not found.";
            return model;
        }
        catch ( final ModelException e )
        {
            throw new CommandExecutionException( Messages.getMessage( e ), e );
        }
        catch ( final JAXBException e )
        {
            String message = Messages.getMessage( e );
            if ( message == null )
            {
                message = Messages.getMessage( e.getLinkedException() );
            }

            throw new CommandExecutionException( message, e );
        }
    }

}
