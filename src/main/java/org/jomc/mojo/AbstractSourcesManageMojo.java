/*
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
package org.jomc.mojo;

import java.io.File;
import java.util.logging.Level;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.util.JAXBSource;
import javax.xml.transform.Source;
import org.apache.maven.plugin.MojoExecutionException;
import org.jomc.model.Module;
import org.jomc.modlet.ModelContext;
import org.jomc.modlet.ModelValidationReport;
import org.jomc.modlet.ObjectFactory;
import org.jomc.tools.SourceFileProcessor;

/**
 * Base class for managing source code files.
 *
 * @author <a href="mailto:cs@schulte.it">Christian Schulte</a>
 * @version $JOMC$
 */
public abstract class AbstractSourcesManageMojo extends AbstractJomcMojo
{

    /** Constant for the name of the tool backing the class. */
    private static final String TOOLNAME = "SourceFileProcessor";

    /** Creates a new {@code AbstractSourcesManageMojo} instance. */
    public AbstractSourcesManageMojo()
    {
        super();
    }

    @Override
    protected final void executeTool() throws Exception
    {
        this.logSeparator();

        if ( this.isSourceProcessingEnabled() )
        {
            this.logProcessingModule( TOOLNAME, this.getSourcesModuleName() );

            final ModelContext context = this.createModelContext( this.getSourcesClassLoader() );
            final SourceFileProcessor tool = this.createSourceFileProcessor( context );
            final JAXBContext jaxbContext = context.createContext( this.getModel() );
            final Source source = new JAXBSource( jaxbContext, new ObjectFactory().createModel( tool.getModel() ) );
            final ModelValidationReport validationReport = context.validateModel( this.getModel(), source );

            this.log( context, validationReport.isModelValid() ? Level.INFO : Level.SEVERE, validationReport );

            if ( validationReport.isModelValid() )
            {
                final Module module =
                    tool.getModules() != null ? tool.getModules().getModule( this.getSourcesModuleName() ) : null;

                if ( module != null )
                {
                    tool.manageSourceFiles( module, this.getSourcesDirectory() );
                    this.logToolSuccess( TOOLNAME );
                }
                else
                {
                    this.logMissingModule( this.getSourcesModuleName() );
                }
            }
            else
            {
                throw new MojoExecutionException( Messages.getMessage( "sourceProcessingFailure" ) );
            }
        }
        else if ( this.isLoggable( Level.INFO ) )
        {
            this.log( Level.INFO, Messages.getMessage( "sourceProcessingDisabled" ), null );
        }
    }

    /**
     * Gets the name of the module to manage source code files of.
     *
     * @return The name of the module to manage source code files of.
     *
     * @throws MojoExecutionException if getting the name fails.
     */
    protected abstract String getSourcesModuleName() throws MojoExecutionException;

    /**
     * Gets the class loader to use for managing source code files.
     *
     * @return The class loader to use for managing source code files.
     *
     * @throws MojoExecutionException if getting the class loader fails.
     */
    protected abstract ClassLoader getSourcesClassLoader() throws MojoExecutionException;

    /**
     * Gets the directory holding the source code files to manage.
     *
     * @return The directory holding the source code files to manage.
     *
     * @throws MojoExecutionException if getting the directory fails.
     */
    protected abstract File getSourcesDirectory() throws MojoExecutionException;

}
