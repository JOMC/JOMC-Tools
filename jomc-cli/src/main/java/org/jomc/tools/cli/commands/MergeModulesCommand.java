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
import java.net.URL;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.util.JAXBResult;
import javax.xml.bind.util.JAXBSource;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamSource;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.jomc.model.Module;
import org.jomc.model.Modules;
import org.jomc.model.ObjectFactory;
import org.jomc.model.modlet.DefaultModelProvider;
import org.jomc.modlet.ModelContext;
import org.jomc.modlet.ModelException;
import org.jomc.modlet.ModelValidationReport;

/**
 * {@code merge-modules} command implementation.
 *
 * @author <a href="mailto:cs@schulte.it">Christian Schulte</a>
 */
public final class MergeModulesCommand extends AbstractModelCommand
{

    /**
     * Creates a new {@code MergeModulesCommand} instance.
     */
    public MergeModulesCommand()
    {
        super();
    }

    @Override
    public org.apache.commons.cli.Options getOptions()
    {
        final org.apache.commons.cli.Options options = super.getOptions();
        Option option = (Option) Options.DOCUMENT_OPTION.clone();
        option.setRequired( true );
        options.addOption( option );

        options.addOption( Options.DOCUMENT_ENCODING_OPTION );
        options.addOption( Options.STYLESHEET_OPTION );

        option = (Option) Options.MODULE_OPTION.clone();
        option.setRequired( true );
        options.addOption( option );

        options.addOption( Options.MODULE_VERSION_OPTION );
        options.addOption( Options.MODULE_VENDOR_OPTION );
        options.addOption( Options.MODULE_INCLUDES_OPTION );
        options.addOption( Options.MODULE_EXCLUDES_OPTION );
        options.addOption( Options.RESOURCES_OPTION );
        return options;
    }

    public String getName()
    {
        return "merge-modules";
    }

    public String getAbbreviatedName()
    {
        return "mm";
    }

    public String getShortDescription( final Locale locale )
    {
        return Messages.getMessage( "mergeModulesShortDescription" );
    }

    public String getLongDescription( final Locale locale )
    {
        return null;
    }

    protected void executeCommand( final CommandLine commandLine ) throws CommandExecutionException
    {
        if ( commandLine == null )
        {
            throw new NullPointerException( "commandLine" );
        }

        CommandLineClassLoader classLoader = null;

        try
        {
            classLoader = new CommandLineClassLoader( commandLine );
            final Modules modules = new Modules();
            final ModelContext context = this.createModelContext( commandLine, classLoader );
            final String model = this.getModel( commandLine );
            final Marshaller marshaller = context.createMarshaller( model );
            final Unmarshaller unmarshaller = context.createUnmarshaller( model );

            if ( !commandLine.hasOption( Options.NO_MODLET_RESOURCE_VALIDATION_OPTION.getOpt() ) )
            {
                unmarshaller.setSchema( context.createSchema( model ) );
            }

            File stylesheetFile = null;
            if ( commandLine.hasOption( Options.STYLESHEET_OPTION.getOpt() ) )
            {
                stylesheetFile = new File( commandLine.getOptionValue( Options.STYLESHEET_OPTION.getOpt() ) );
            }

            String moduleVersion = null;
            if ( commandLine.hasOption( Options.MODULE_VERSION_OPTION.getOpt() ) )
            {
                moduleVersion = commandLine.getOptionValue( Options.MODULE_VERSION_OPTION.getOpt() );
            }

            String moduleVendor = null;
            if ( commandLine.hasOption( Options.MODULE_VENDOR_OPTION.getOpt() ) )
            {
                moduleVendor = commandLine.getOptionValue( Options.MODULE_VENDOR_OPTION.getOpt() );
            }

            if ( commandLine.hasOption( Options.DOCUMENTS_OPTION.getOpt() ) )
            {
                for ( final File f : this.getDocumentFiles( commandLine ) )
                {
                    if ( this.isLoggable( Level.FINEST ) )
                    {
                        this.log( Level.FINEST, Messages.getMessage( "readingResource", f.getAbsolutePath() ), null );
                    }

                    Object o = unmarshaller.unmarshal( f );
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

            if ( commandLine.hasOption( Options.CLASSPATH_OPTION.getOpt() ) )
            {
                String[] resourceNames = null;

                if ( commandLine.hasOption( Options.RESOURCES_OPTION.getOpt() ) )
                {
                    resourceNames = commandLine.getOptionValues( Options.RESOURCES_OPTION.getOpt() );
                }

                if ( resourceNames == null )
                {
                    resourceNames = new String[]
                    {
                        DefaultModelProvider.getDefaultModuleLocation()
                    };
                }

                for ( final String resource : resourceNames )
                {
                    for ( final Enumeration<URL> e = classLoader.getResources( resource ); e.hasMoreElements(); )
                    {
                        final URL url = e.nextElement();

                        if ( this.isLoggable( Level.FINEST ) )
                        {
                            this.log( Level.FINEST,
                                      Messages.getMessage( "readingResource", url.toExternalForm() ),
                                      null );

                        }

                        Object o = unmarshaller.unmarshal( url );
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
                                      Messages.getMessage( "failureProcessing", url.toExternalForm(), o.toString() ),
                                      null );

                        }
                    }
                }
            }

            if ( commandLine.hasOption( Options.MODULE_INCLUDES_OPTION.getOpt() ) )
            {
                final String[] values = commandLine.getOptionValues( Options.MODULE_INCLUDES_OPTION.getOpt() );

                if ( values != null )
                {
                    final List<String> includes = Arrays.asList( values );

                    for ( final Iterator<Module> it = modules.getModule().iterator(); it.hasNext(); )
                    {
                        final Module m = it.next();
                        if ( !includes.contains( m.getName() ) )
                        {
                            this.log( Level.INFO,
                                      Messages.getMessage( "moduleExclusionInfo", m.getName() ),
                                      null );

                            it.remove();
                        }
                        else
                        {
                            this.log( Level.INFO,
                                      Messages.getMessage( "moduleInclusionInfo", m.getName() ),
                                      null );

                        }
                    }
                }
            }

            if ( commandLine.hasOption( Options.MODULE_EXCLUDES_OPTION.getOpt() ) )
            {
                final String[] values = commandLine.getOptionValues( Options.MODULE_EXCLUDES_OPTION.getOpt() );

                if ( values != null )
                {
                    for ( final String exclude : values )
                    {
                        final Module m = modules.getModule( exclude );

                        if ( m != null )
                        {
                            this.log( Level.INFO,
                                      Messages.getMessage( "moduleExclusionInfo", m.getName() ),
                                      null );

                            modules.getModule().remove( m );
                        }
                    }
                }
            }

            Module classpathModule = null;
            if ( !commandLine.hasOption( Options.NO_CLASSPATH_RESOLUTION_OPTION.getOpt() ) )
            {
                classpathModule = modules.getClasspathModule( Modules.getDefaultClasspathModuleName(), classLoader );
                if ( classpathModule != null && modules.getModule( Modules.getDefaultClasspathModuleName() ) == null )
                {
                    modules.getModule().add( classpathModule );
                }
                else
                {
                    classpathModule = null;
                }
            }

            final ModelValidationReport validationReport = context.validateModel(
                model, new JAXBSource( marshaller, new ObjectFactory().createModules( modules ) ) );

            this.log( validationReport, marshaller );

            if ( !validationReport.isModelValid() )
            {
                throw new CommandExecutionException( Messages.getMessage( "invalidModel", model ) );
            }

            if ( classpathModule != null )
            {
                modules.getModule().remove( classpathModule );
            }

            Module mergedModule =
                modules.getMergedModule( commandLine.getOptionValue( Options.MODULE_OPTION.getOpt() ) );

            mergedModule.setVersion( moduleVersion );
            mergedModule.setVendor( moduleVendor );

            final File moduleFile = new File( commandLine.getOptionValue( Options.DOCUMENT_OPTION.getOpt() ) );

            if ( stylesheetFile != null )
            {
                final Transformer transformer = this.createTransformer( new StreamSource( stylesheetFile ) );
                final JAXBSource source =
                    new JAXBSource( marshaller, new ObjectFactory().createModule( mergedModule ) );

                final JAXBResult result = new JAXBResult( unmarshaller );
                unmarshaller.setSchema( null );
                transformer.transform( source, result );

                if ( result.getResult() instanceof JAXBElement<?>
                         && ( (JAXBElement<?>) result.getResult() ).getValue() instanceof Module )
                {
                    mergedModule = (Module) ( (JAXBElement<?>) result.getResult() ).getValue();
                }
                else
                {
                    throw new CommandExecutionException( Messages.getMessage( "illegalTransformationResultError",
                                                                              stylesheetFile.getAbsolutePath() ) );

                }
            }

            marshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );

            if ( commandLine.hasOption( Options.DOCUMENT_ENCODING_OPTION.getOpt() ) )
            {
                marshaller.setProperty( Marshaller.JAXB_ENCODING,
                                        commandLine.getOptionValue( Options.DOCUMENT_ENCODING_OPTION.getOpt() ) );

            }

            marshaller.setSchema( context.createSchema( model ) );
            marshaller.marshal( new ObjectFactory().createModule( mergedModule ), moduleFile );

            if ( this.isLoggable( Level.INFO ) )
            {
                this.log( Level.INFO, Messages.getMessage( "writingResource", moduleFile.getAbsolutePath() ), null );
            }

            classLoader.close();
            classLoader = null;
        }
        catch ( final IOException e )
        {
            throw new CommandExecutionException( Messages.getMessage( e ), e );
        }
        catch ( final TransformerException e )
        {
            String message = Messages.getMessage( e );
            if ( message == null )
            {
                message = Messages.getMessage( e.getException() );
            }

            throw new CommandExecutionException( message, e );
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
