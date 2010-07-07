/*
 *   Copyright (c) 2009 The JOMC Project
 *   Copyright (c) 2005 Christian Schulte <schulte2005@users.sourceforge.net>
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
 *   THIS SOFTWARE IS PROVIDED BY THE JOMC PROJECT AND CONTRIBUTORS "AS IS"
 *   AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 *   THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 *   PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE JOMC PROJECT OR
 *   CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 *   EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 *   PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 *   OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 *   WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 *   OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 *   ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *   $Id$
 *
 */
package org.jomc.mojo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.ResourceBundle;
import java.util.logging.Level;
import javax.xml.bind.Marshaller;
import org.apache.maven.plugin.MojoExecutionException;
import org.jomc.modlet.Model;
import org.jomc.modlet.ModelContext;
import org.jomc.modlet.ObjectFactory;

/**
 * Base class for displaying and dumping model objects.
 *
 * @author <a href="mailto:schulte2005@users.sourceforge.net">Christian Schulte</a>
 * @version $Id$
 * @since 1.1
 */
public abstract class AbstractModelShowMojo extends AbstractJomcMojo
{

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

    /** Creates a new {@code AbstractModelShowMojo} instance. */
    public AbstractModelShowMojo()
    {
        super();
    }

    @Override
    protected final void executeTool() throws Exception
    {
        final ModelContext modelContext = this.createModelContext( this.getDisplayClassLoader() );
        final Marshaller m = modelContext.createMarshaller( this.getModel() );
        final Model displayModel = this.getDisplayModel( modelContext );
        m.setSchema( modelContext.createSchema( this.getModel() ) );
        m.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );

        if ( displayModel != null )
        {
            if ( this.document == null )
            {
                final StringWriter stringWriter = new StringWriter();
                m.marshal( new ObjectFactory().createModel( displayModel ), stringWriter );

                final boolean verbose = this.isVerbose();
                try
                {
                    this.setVerbose( true );
                    this.log( Level.INFO, "", null );
                    this.log( Level.INFO, stringWriter.toString(), null );
                    this.log( Level.INFO, "", null );
                }
                finally
                {
                    this.setVerbose( verbose );
                }
            }
            else
            {
                if ( this.document.exists() && !this.document.delete() )
                {
                    this.log( Level.WARNING, getMessage( "failedDeleting", this.document.getAbsolutePath() ), null );
                }

                this.log( Level.INFO, getMessage( "writing", this.document.getAbsolutePath() ), null );

                final OutputStream out = new FileOutputStream( this.document );
                m.setProperty( Marshaller.JAXB_ENCODING, this.documentEncoding );
                m.marshal( new ObjectFactory().createModel( displayModel ), out );
                out.close();
            }
        }
        else
        {
            this.log( Level.WARNING, getMessage( "modelObjectNotFound" ), null );
        }
    }

    /**
     * Gets the model to display or dump.
     *
     * @param modelContext The model context to use for getting the model.
     *
     * @return The model to display or dump.
     *
     * @throws MojoExecutionException if getting the model fails.
     */
    protected abstract Model getDisplayModel( ModelContext modelContext ) throws MojoExecutionException;

    /**
     * Gets the class loader to use for displaying or dumping model objects.
     *
     * @return The class loader to use for displaying or dumping model objects.
     *
     * @throws MojoExecutionException if getting the class loader fails.
     */
    protected abstract ClassLoader getDisplayClassLoader() throws MojoExecutionException;

    private static String getMessage( final String key, final Object... args )
    {
        return MessageFormat.format( ResourceBundle.getBundle(
            AbstractModelShowMojo.class.getName().replace( '.', '/' ) ).getString( key ), args );

    }

}
