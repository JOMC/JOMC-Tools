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

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
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
import org.jomc.tools.SourceFileProcessor;

/**
 * Task for managing source files.
 *
 * @author <a href="mailto:cs@schulte.it">Christian Schulte</a>
 * @version $JOMC$
 */
public final class ManageSourcesTask extends SourceFileProcessorTask
{

    /**
     * The directory holding the source files to manage.
     */
    private File sourcesDirectory;

    /**
     * Creates a new {@code ManageSourcesTask} instance.
     */
    public ManageSourcesTask()
    {
        super();
    }

    /**
     * Gets the directory holding the source files to manage.
     *
     * @return The directory holding the source files to manage or {@code null}.
     *
     * @see #setSourcesDirectory(java.io.File)
     */
    public File getSourcesDirectory()
    {
        return this.sourcesDirectory;
    }

    /**
     * Sets the directory holding the source files to manage.
     *
     * @param value The new directory holding the source files to manage or {@code null}.
     *
     * @see #getSourcesDirectory()
     */
    public void setSourcesDirectory( final File value )
    {
        this.sourcesDirectory = value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void preExecuteTask() throws BuildException
    {
        super.preExecuteTask();

        this.assertNotNull( "sourcesDirectory", this.getSourcesDirectory() );
    }

    /**
     * Manages source files.
     *
     * @throws BuildException if managing source files fails.
     */
    @Override
    public void processSourceFiles() throws BuildException
    {
        ProjectClassLoader classLoader = null;

        try
        {
            this.log( Messages.getMessage( "managingSources", this.getModel() ) );

            classLoader = this.newProjectClassLoader();
            final ModelContext context = this.newModelContext( classLoader );
            final SourceFileProcessor tool = this.newSourceFileProcessor();
            final JAXBContext jaxbContext = context.createContext( this.getModel() );
            final Model model = this.getModel( context );
            final Source source = new JAXBSource( jaxbContext, new ObjectFactory().createModel( model ) );
            final ModelValidationReport validationReport = context.validateModel( this.getModel(), source );

            this.logValidationReport( context, validationReport );
            tool.setModel( model );

            if ( validationReport.isModelValid() )
            {
                final Specification s = this.getSpecification( model );
                final Implementation i = this.getImplementation( model );
                final Module m = this.getModule( model );

                if ( s != null )
                {
                    tool.manageSourceFiles( s, this.getSourcesDirectory() );
                }

                if ( i != null )
                {
                    tool.manageSourceFiles( i, this.getSourcesDirectory() );
                }

                if ( m != null )
                {
                    tool.manageSourceFiles( m, this.getSourcesDirectory() );
                }

                if ( this.isModulesProcessingRequested() )
                {
                    tool.manageSourceFiles( this.getSourcesDirectory() );
                }

                classLoader.close();
                classLoader = null;
            }
            else
            {
                throw new ModelException( Messages.getMessage( "invalidModel", this.getModel() ) );
            }
        }
        catch ( final IOException e )
        {
            throw new SourceProcessingException( Messages.getMessage( e ), e, this.getLocation() );
        }
        catch ( final JAXBException e )
        {
            throw new SourceProcessingException( Messages.getMessage( e ), e, this.getLocation() );
        }
        catch ( final ModelException e )
        {
            throw new SourceProcessingException( Messages.getMessage( e ), e, this.getLocation() );
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
                this.logMessage( Level.SEVERE, Messages.getMessage( e ), e );
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ManageSourcesTask clone()
    {
        final ManageSourcesTask clone = (ManageSourcesTask) super.clone();
        clone.sourcesDirectory =
            this.sourcesDirectory != null ? new File( this.sourcesDirectory.getAbsolutePath() ) : null;

        return clone;
    }

}
