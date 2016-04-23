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
import java.util.Locale;
import java.util.logging.Level;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.util.JAXBSource;
import javax.xml.transform.Source;
import org.apache.commons.cli.CommandLine;
import org.jomc.model.Implementation;
import org.jomc.model.Module;
import org.jomc.model.Specification;
import org.jomc.modlet.Model;
import org.jomc.modlet.ModelContext;
import org.jomc.modlet.ModelException;
import org.jomc.modlet.ModelValidationReport;
import org.jomc.modlet.ObjectFactory;
import org.jomc.tools.ResourceFileProcessor;

// SECTION-START[Documentation]
// <editor-fold defaultstate="collapsed" desc=" Generated Documentation ">
/**
 * {@code generate-resources} command implementation.
 *
 * @author <a href="mailto:cs@schulte.it">Christian Schulte</a>
 */
public final class GenerateResourcesCommand extends AbstractResourceFileProcessorCommand
{

    /**
     * Creates a new {@code GenerateResourcesCommand} instance.
     */
    public GenerateResourcesCommand()
    {
        super();
    }

    public String getName()
    {
        return "generate-resources";
    }

    public String getAbbreviatedName()
    {
        return "gr";
    }

    public String getShortDescription( final Locale locale )
    {
        return Messages.getMessage( "generateResourcesShortDescription" );
    }

    public String getLongDescription( final Locale locale )
    {
        return Messages.getMessage( "generateResourcesLongDescription" );
    }

    protected void processResourceFiles( final CommandLine commandLine ) throws CommandExecutionException
    {
        if ( commandLine == null )
        {
            throw new NullPointerException( "commandLine" );
        }

        CommandLineClassLoader classLoader = null;

        try
        {
            classLoader = new CommandLineClassLoader( commandLine );
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

            final ResourceFileProcessor tool = this.createResourceFileProcessor( commandLine );
            tool.setModel( model );

            final File resourcesDirectory =
                new File( commandLine.getOptionValue( Options.RESOURCE_DIRECTORY_OPTION.getOpt() ) );

            final Specification specification = this.getSpecification( commandLine, model );
            final Implementation implementation = this.getImplementation( commandLine, model );
            final Module module = this.getModule( commandLine, model );

            if ( specification != null )
            {
                tool.writeResourceBundleResourceFiles( specification, resourcesDirectory );
            }

            if ( implementation != null )
            {
                tool.writeResourceBundleResourceFiles( implementation, resourcesDirectory );
            }

            if ( module != null )
            {
                tool.writeResourceBundleResourceFiles( module, resourcesDirectory );
            }

            if ( this.isModulesProcessingRequested( commandLine ) )
            {
                tool.writeResourceBundleResourceFiles( resourcesDirectory );
            }

            classLoader.close();
            classLoader = null;
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
        catch ( final ModelException e )
        {
            throw new CommandExecutionException( Messages.getMessage( e ), e );
        }
        catch ( final IOException e )
        {
            throw new CommandExecutionException( Messages.getMessage( e ), e );
        }
        finally
        {
            try
            {
                if ( classLoader != null )
                {
                    classLoader.close();
                }
            }
            catch ( final IOException e )
            {
                this.log( Level.SEVERE, Messages.getMessage( e ), e );
            }
        }
    }

}
