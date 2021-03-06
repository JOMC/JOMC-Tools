/*
 *   Copyright (C) 2005 Christian Schulte <cs@schulte.it>
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
package org.jomc.tools.ant;

import java.io.IOException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.util.JAXBSource;
import javax.xml.transform.Source;
import org.apache.tools.ant.BuildException;
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
 * Task for validating class path model objects.
 *
 * @author <a href="mailto:cs@schulte.it">Christian Schulte</a>
 * @version $JOMC$
 */
public final class ValidateClasspathTask extends ClassFileProcessorTask
{

    /**
     * Creates a new {@code ValidateClasspathTask} instance.
     */
    public ValidateClasspathTask()
    {
        super();
    }

    /**
     * Validates class file model objects.
     *
     * @throws BuildException if validating class file model objects fails.
     */
    @Override
    public void processClassFiles() throws BuildException
    {
        this.log( Messages.getMessage( "validatingClasspath", this.getModel() ) );

        try ( final ProjectClassLoader classLoader = this.newProjectClassLoader() )
        {
            final ModelContext context = this.newModelContext( classLoader );
            final ClassFileProcessor tool = this.newClassFileProcessor();
            final JAXBContext jaxbContext = context.createContext( this.getModel() );
            final Model model = this.getModel( context );
            final Source source = new JAXBSource( jaxbContext, new ObjectFactory().createModel( model ) );
            ModelValidationReport validationReport = context.validateModel( this.getModel(), source );

            this.logValidationReport( context, validationReport );
            tool.setModel( model );

            if ( validationReport.isModelValid() )
            {
                final Specification s = this.getSpecification( model );
                final Implementation i = this.getImplementation( model );
                final Module m = this.getModule( model );

                if ( s != null )
                {
                    validationReport = tool.validateModelObjects( s, context );

                    if ( validationReport != null )
                    {
                        this.logValidationReport( context, validationReport );

                        if ( !validationReport.isModelValid() )
                        {
                            throw new ModelException( Messages.getMessage( "invalidModel", this.getModel() ) );
                        }
                    }
                }

                if ( i != null )
                {
                    validationReport = tool.validateModelObjects( i, context );

                    if ( validationReport != null )
                    {
                        this.logValidationReport( context, validationReport );

                        if ( !validationReport.isModelValid() )
                        {
                            throw new ModelException( Messages.getMessage( "invalidModel", this.getModel() ) );
                        }
                    }
                }

                if ( m != null )
                {
                    validationReport = tool.validateModelObjects( m, context );

                    if ( validationReport != null )
                    {
                        this.logValidationReport( context, validationReport );

                        if ( !validationReport.isModelValid() )
                        {
                            throw new ModelException( Messages.getMessage( "invalidModel", this.getModel() ) );
                        }
                    }
                }

                if ( this.isModulesProcessingRequested() )
                {
                    validationReport = tool.validateModelObjects( context );

                    if ( validationReport != null )
                    {
                        this.logValidationReport( context, validationReport );

                        if ( !validationReport.isModelValid() )
                        {
                            throw new ModelException( Messages.getMessage( "invalidModel", this.getModel() ) );
                        }
                    }
                }
            }
            else
            {
                throw new ModelException( Messages.getMessage( "invalidModel", this.getModel() ) );
            }
        }
        catch ( final IOException | JAXBException | ModelException e )
        {
            throw new ClassProcessingException( Messages.getMessage( e ), e, this.getLocation() );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ValidateClasspathTask clone()
    {
        return (ValidateClasspathTask) super.clone();
    }

}
