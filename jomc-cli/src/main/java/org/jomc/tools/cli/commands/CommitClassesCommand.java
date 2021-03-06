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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.util.JAXBSource;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.stream.StreamSource;
import org.apache.commons.cli.CommandLine;
import org.jomc.model.Implementation;
import org.jomc.model.Module;
import org.jomc.model.Specification;
import org.jomc.modlet.Model;
import org.jomc.modlet.ModelContext;
import org.jomc.modlet.ModelException;
import org.jomc.modlet.ModelValidationReport;
import org.jomc.modlet.ObjectFactory;
import org.jomc.tools.ClassFileProcessor;

/**
 * {@code commit-classes} command implementation.
 *
 * @author <a href="mailto:cs@schulte.it">Christian Schulte</a>
 */
public final class CommitClassesCommand extends AbstractClassFileProcessorCommand
{

    /**
     * Creates a new {@code CommitClassesCommand} instance.
     */
    public CommitClassesCommand()
    {
        super();
    }

    @Override
    public org.apache.commons.cli.Options getOptions()
    {
        final org.apache.commons.cli.Options options = super.getOptions();
        options.addOption( Options.CLASSES_DIRECTORY_OPTION );
        options.addOption( Options.STYLESHEET_OPTION );
        return options;
    }

    @Override
    public String getName()
    {
        return "commit-classes";
    }

    @Override
    public String getAbbreviatedName()
    {
        return "cc";
    }

    @Override
    public String getShortDescription( final Locale locale )
    {
        return Messages.getMessage( "commitClassesShortDescription" );
    }

    @Override
    public String getLongDescription( final Locale locale )
    {
        return null;
    }

    @Override
    protected void processClassFiles( final CommandLine commandLine ) throws CommandExecutionException
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
            this.log( validationReport, marshaller );

            if ( !validationReport.isModelValid() )
            {
                throw new CommandExecutionException( Messages.getMessage( "invalidModel",
                                                                          this.getModel( commandLine ) ) );

            }

            final ClassFileProcessor tool = this.createClassFileProcessor( commandLine );
            tool.setModel( model );

            final File classesDirectory =
                new File( commandLine.getOptionValue( Options.CLASSES_DIRECTORY_OPTION.getOpt() ) );

            final List<Transformer> transformers = new ArrayList<>( 1 );

            if ( commandLine.hasOption( Options.STYLESHEET_OPTION.getOpt() ) )
            {
                final File stylesheetFile =
                    new File( commandLine.getOptionValue( Options.STYLESHEET_OPTION.getOpt() ) );

                transformers.add( this.createTransformer( new StreamSource( stylesheetFile ) ) );
            }

            final Specification specification = this.getSpecification( commandLine, model );
            final Implementation implementation = this.getImplementation( commandLine, model );
            final Module module = this.getModule( commandLine, model );

            if ( specification != null )
            {
                tool.commitModelObjects( specification, context, classesDirectory );

                if ( !transformers.isEmpty() )
                {
                    tool.transformModelObjects( specification, context, classesDirectory, transformers );
                }
            }

            if ( implementation != null )
            {
                tool.commitModelObjects( implementation, context, classesDirectory );

                if ( !transformers.isEmpty() )
                {
                    tool.transformModelObjects( implementation, context, classesDirectory, transformers );
                }
            }

            if ( module != null )
            {
                tool.commitModelObjects( module, context, classesDirectory );

                if ( !transformers.isEmpty() )
                {
                    tool.transformModelObjects( module, context, classesDirectory, transformers );
                }
            }

            if ( this.isModulesProcessingRequested( commandLine ) )
            {
                tool.commitModelObjects( context, classesDirectory );

                if ( !transformers.isEmpty() )
                {
                    tool.transformModelObjects( context, classesDirectory, transformers );
                }
            }
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
        catch ( final ModelException | IOException e )
        {
            throw new CommandExecutionException( Messages.getMessage( e ), e );
        }
    }

}
