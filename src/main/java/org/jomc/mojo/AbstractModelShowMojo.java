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
     * @parameter default-value="UTF-8" expression="${jomc.documentEncoding}"
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
                stringWriter.append( System.getProperty( "line.separator" ) );
                stringWriter.append( System.getProperty( "line.separator" ) );
                m.marshal( new ObjectFactory().createModel( displayModel ), stringWriter );

                this.getLog().info( "" );
                this.getLog().info( stringWriter.toString() );
                this.getLog().info( "" );
            }
            else
            {
                if ( this.document.exists() && !this.document.delete() )
                {
                    throw new MojoExecutionException( getMessage( "failedDeleting", this.document.getAbsolutePath() ) );
                }

                this.getLog().info( getMessage( "writing", this.document.getAbsolutePath() ) );

                final OutputStream out = new FileOutputStream( this.document );
                m.setProperty( Marshaller.JAXB_ENCODING, this.documentEncoding );
                m.marshal( new ObjectFactory().createModel( displayModel ), out );
                out.close();
            }
        }
        else if ( this.getLog().isWarnEnabled() )
        {
            this.getLog().warn( getMessage( "modelObjectNotFound" ) );
        }
    }

    protected abstract Model getDisplayModel( ModelContext modelContext ) throws MojoExecutionException;

    protected abstract ClassLoader getDisplayClassLoader() throws MojoExecutionException;

    private static String getMessage( final String key, final Object... args )
    {
        return MessageFormat.format( ResourceBundle.getBundle(
            AbstractModelShowMojo.class.getName().replace( '.', '/' ) ).getString( key ), args );

    }

}
