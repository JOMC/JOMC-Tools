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
import java.io.IOException;
import java.io.StringWriter;
import java.util.Locale;
import java.util.logging.Level;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.util.JAXBSource;
import javax.xml.transform.Source;
import org.apache.commons.cli.CommandLine;
import org.jomc.model.Instance;
import org.jomc.model.Module;
import org.jomc.model.Modules;
import org.jomc.model.Specification;
import org.jomc.model.modlet.ModelHelper;
import org.jomc.modlet.Model;
import org.jomc.modlet.ModelContext;
import org.jomc.modlet.ModelException;
import org.jomc.modlet.ModelValidationReport;
import org.jomc.modlet.ObjectFactory;
import org.jomc.tools.cli.commands.AbstractModletCommand.CommandLineClassLoader;

/**
 * {@code show-model} command implementation.
 *
 * @author <a href="mailto:cs@schulte.it">Christian Schulte</a>
 */
public final class ShowModelCommand extends AbstractModelCommand
{

    /**
     * Creates a new {@code ShowModelCommand} instance.
     */
    public ShowModelCommand()
    {
        super();
    }

    @Override
    public org.apache.commons.cli.Options getOptions()
    {
        final org.apache.commons.cli.Options options = super.getOptions();
        options.addOption( Options.DOCUMENT_OPTION );
        options.addOption( Options.DOCUMENT_ENCODING_OPTION );
        options.addOption( Options.IMPLEMENTATION_OPTION );
        options.addOption( Options.MODULE_OPTION );
        options.addOption( Options.SPECIFICATION_OPTION );
        return options;
    }

    @Override
    public String getName()
    {
        return "show-model";
    }

    @Override
    public String getAbbreviatedName()
    {
        return "sm";
    }

    @Override
    public String getShortDescription( final Locale locale )
    {
        return Messages.getMessage( "showModelShortDescription" );
    }

    @Override
    public String getLongDescription( final Locale locale )
    {
        return null;
    }

    @Override
    protected void executeCommand( final CommandLine commandLine ) throws CommandExecutionException
    {
        if ( commandLine == null )
        {
            throw new NullPointerException( "commandLine" );
        }

        try ( final CommandLineClassLoader classLoader = new CommandLineClassLoader( commandLine ) )
        {
            final ModelContext context = this.createModelContext( commandLine, classLoader );
            final Model model = this.getModel( context, commandLine );
            final JAXBContext jaxbContext = context.createContext( model.getIdentifier() );
            final Marshaller marshaller = context.createMarshaller( model.getIdentifier() );
            final Source source = new JAXBSource( jaxbContext, new ObjectFactory().createModel( model ) );
            final ModelValidationReport validationReport = context.validateModel( model.getIdentifier(), source );
            final Modules modules = ModelHelper.getModules( model );
            this.log( validationReport, marshaller );

            if ( !validationReport.isModelValid() )
            {
                throw new CommandExecutionException( Messages.getMessage( "invalidModel",
                                                                          this.getModel( commandLine ) ) );

            }

            final Model displayModel = new Model();
            displayModel.setIdentifier( model.getIdentifier() );

            boolean displayModules = true;

            if ( commandLine.hasOption( Options.IMPLEMENTATION_OPTION.getOpt() ) )
            {
                final String identifier = commandLine.getOptionValue( Options.IMPLEMENTATION_OPTION.getOpt() );
                final Instance instance = modules != null ? modules.getInstance( identifier ) : null;
                displayModules = false;

                if ( instance != null )
                {
                    displayModel.getAny().add( new org.jomc.model.ObjectFactory().createInstance( instance ) );
                }
                else if ( this.isLoggable( Level.WARNING ) )
                {
                    this.log( Level.WARNING,
                              Messages.getMessage( "implementationNotFoundWarning", identifier ),
                              null );

                }
            }

            if ( commandLine.hasOption( Options.SPECIFICATION_OPTION.getOpt() ) )
            {
                final String identifier = commandLine.getOptionValue( Options.SPECIFICATION_OPTION.getOpt() );
                final Specification specification = modules != null ? modules.getSpecification( identifier ) : null;
                displayModules = false;

                if ( specification != null )
                {
                    displayModel.getAny().add(
                        new org.jomc.model.ObjectFactory().createSpecification( specification ) );

                }
                else if ( this.isLoggable( Level.WARNING ) )
                {
                    this.log( Level.WARNING,
                              Messages.getMessage( "specificationNotFoundWarning", identifier ),
                              null );

                }
            }

            if ( commandLine.hasOption( Options.MODULE_OPTION.getOpt() ) )
            {
                final String moduleName = commandLine.getOptionValue( Options.MODULE_OPTION.getOpt() );
                final Module m = modules != null ? modules.getModule( moduleName ) : null;
                displayModules = false;

                if ( m != null )
                {
                    displayModel.getAny().add( new org.jomc.model.ObjectFactory().createModule( m ) );
                }
                else if ( this.isLoggable( Level.WARNING ) )
                {
                    this.log( Level.WARNING,
                              Messages.getMessage( "moduleNotFoundWarning", moduleName ),
                              null );

                }
            }

            if ( displayModules )
            {
                ModelHelper.setModules( displayModel, modules );
            }

            marshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );

            if ( commandLine.hasOption( Options.DOCUMENT_ENCODING_OPTION.getOpt() ) )
            {
                marshaller.setProperty( Marshaller.JAXB_ENCODING,
                                        commandLine.getOptionValue( Options.DOCUMENT_ENCODING_OPTION.getOpt() ) );

            }

            if ( commandLine.hasOption( Options.DOCUMENT_OPTION.getOpt() ) )
            {
                final File documentFile = new File( commandLine.getOptionValue( Options.DOCUMENT_OPTION.getOpt() ) );

                if ( this.isLoggable( Level.INFO ) )
                {
                    this.log( Level.INFO,
                              Messages.getMessage( "writingResource", documentFile.getAbsolutePath() ),
                              null );

                }

                marshaller.marshal( new ObjectFactory().createModel( displayModel ), documentFile );
            }
            else if ( this.isLoggable( Level.INFO ) )
            {
                final StringWriter stringWriter = new StringWriter();
                marshaller.marshal( new ObjectFactory().createModel( displayModel ), stringWriter );
                this.log( Level.INFO, stringWriter.toString(), null );
            }
        }
        catch ( final IOException | ModelException e )
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
