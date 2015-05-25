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
import java.io.StringWriter;
import java.util.logging.Level;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import org.apache.maven.plugin.MojoExecutionException;
import org.jomc.modlet.ModelContext;

/**
 * Base class for displaying and dumping model objects.
 *
 * @author <a href="mailto:cs@schulte.it">Christian Schulte</a>
 * @version $JOMC$
 * @since 1.1
 */
public abstract class AbstractModelShowMojo extends AbstractJomcMojo
{

    /**
     * Constant for the name of the tool backing the mojo.
     */
    private static final String TOOLNAME = "ModelProcessor";

    /**
     * File to write the model to.
     *
     * @parameter expression="${jomc.document}"
     */
    private File document;

    /**
     * Encoding of the document to write.
     *
     * @parameter default-value="${project.build.sourceEncoding}" expression="${jomc.documentEncoding}"
     */
    private String documentEncoding;

    /**
     * Creates a new {@code AbstractModelShowMojo} instance.
     */
    public AbstractModelShowMojo()
    {
        super();
    }

    @Override
    protected final void executeTool() throws Exception
    {
        this.logSeparator();
        this.logProcessingModel( TOOLNAME, this.getModel() );

        final ModelContext modelContext = this.createModelContext( this.getDisplayClassLoader() );
        final Marshaller m = modelContext.createMarshaller( this.getModel() );
        final JAXBElement<?> displayModel = this.getDisplayModel( modelContext );
        m.setSchema( modelContext.createSchema( this.getModel() ) );
        m.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );

        if ( displayModel != null )
        {
            if ( this.document == null )
            {
                final StringWriter stringWriter = new StringWriter();
                m.marshal( displayModel, stringWriter );

                final boolean verbose = this.isVerbose();
                try
                {
                    this.setVerbose( true );

                    if ( this.isLoggable( Level.INFO ) )
                    {
                        this.log( Level.INFO, stringWriter.toString(), null );
                    }
                }
                finally
                {
                    this.setVerbose( verbose );
                }
            }
            else
            {
                if ( this.document.exists() && !this.document.delete() && this.isLoggable( Level.WARNING ) )
                {
                    this.log( Level.WARNING, Messages.getMessage(
                              "failedDeletingFile", this.document.getAbsolutePath() ), null );

                }

                if ( this.isLoggable( Level.INFO ) )
                {
                    this.log( Level.INFO, Messages.getMessage(
                              "writingEncoded", this.document.getAbsolutePath(), this.documentEncoding ), null );

                }

                m.setProperty( Marshaller.JAXB_ENCODING, this.documentEncoding );
                m.marshal( displayModel, this.document );
            }

            this.logToolSuccess( TOOLNAME );
        }
        else if ( this.isLoggable( Level.WARNING ) )
        {
            this.log( Level.WARNING, Messages.getMessage( "modelObjectNotFound" ), null );
        }
    }

    /**
     * Gets the model object to display or dump.
     *
     * @param modelContext The model context to use for getting the model.
     *
     * @return The model object to display or dump.
     *
     * @throws MojoExecutionException if getting the model fails.
     */
    protected abstract JAXBElement<?> getDisplayModel( ModelContext modelContext ) throws MojoExecutionException;

    /**
     * Gets the class loader to use for displaying or dumping model objects.
     *
     * @return The class loader to use for displaying or dumping model objects.
     *
     * @throws MojoExecutionException if getting the class loader fails.
     */
    protected abstract ClassLoader getDisplayClassLoader() throws MojoExecutionException;

}
