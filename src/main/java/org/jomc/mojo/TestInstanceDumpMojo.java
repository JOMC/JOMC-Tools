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
import org.apache.maven.plugin.MojoFailureException;
import org.jomc.model.Instance;
import org.jomc.model.Modules;
import org.jomc.model.ObjectFactory;
import org.jomc.modlet.ModelContext;

/**
 * Dumps a project's test instance.
 *
 * @author <a href="mailto:schulte2005@users.sourceforge.net">Christian Schulte</a>
 * @version $Id$
 *
 * @goal dump-test-instance
 * @threadSafe
 * @requiresDependencyResolution test
 */
public final class TestInstanceDumpMojo extends AbstractJomcMojo
{

    /**
     * File to dump the instance to. If not set, data will be logged to the console.
     *
     * @parameter expression="${jomc.dumpFile}"
     */
    private File dumpFile;

    /**
     * Identifier of the instance to dump.
     *
     * @parameter expression="${jomc.identifier}"
     * @required
     */
    private String identifier;

    protected void executeTool() throws Exception
    {
        if ( this.identifier == null )
        {
            throw new MojoFailureException( getMessage( "mandatoryParameterMissing", "identifier" ) );
        }

        final ClassLoader classLoader = this.getTestClassLoader();
        final ModelContext modelContext = this.createModelContext( classLoader );
        final Modules modules = this.getToolModules( modelContext );
        final Instance instance = modules.getInstance( this.identifier );

        if ( instance != null )
        {
            final Marshaller m = modelContext.createMarshaller( Modules.MODEL_PUBLIC_ID );
            m.setSchema( modelContext.createSchema( Modules.MODEL_PUBLIC_ID ) );
            m.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );

            if ( this.dumpFile != null )
            {
                if ( this.dumpFile.exists() )
                {
                    this.dumpFile.delete();
                }

                final OutputStream out = new FileOutputStream( this.dumpFile );
                m.marshal( new ObjectFactory().createInstance( instance ), out );
                out.close();

                this.getLog().info( getMessage( "writing", this.dumpFile.getAbsolutePath() ) );
            }
            else
            {
                this.getLog().info( "" );

                final StringWriter stringWriter = new StringWriter();
                stringWriter.append( System.getProperty( "line.separator" ) );
                stringWriter.append( System.getProperty( "line.separator" ) );

                m.marshal( new ObjectFactory().createInstance( instance ), stringWriter );

                this.getLog().info( stringWriter.toString() );
                this.getLog().info( "" );
            }
        }
        else
        {
            throw new MojoExecutionException( getMessage( "instanceNotFound", this.identifier ) );
        }
    }

    private static String getMessage( final String key, final Object... args )
    {
        return MessageFormat.format( ResourceBundle.getBundle(
            TestInstanceDumpMojo.class.getName().replace( '.', '/' ) ).getString( key ), args );

    }

}
