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
import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.util.JAXBSource;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;
import org.apache.maven.plugin.MojoExecutionException;
import org.jomc.model.Module;
import org.jomc.modlet.ModelContext;
import org.jomc.modlet.ModelValidationReport;
import org.jomc.modlet.ObjectFactory;
import org.jomc.tools.ClassFileProcessor;

/**
 * Base class for committing model objects to class files.
 *
 * @author <a href="mailto:schulte2005@users.sourceforge.net">Christian Schulte</a>
 * @version $Id$
 */
public abstract class AbstractClassesCommitMojo extends AbstractJomcMojo
{

    /** Constant for the name of the tool backing the mojo. */
    private static final String TOOLNAME = "ClassFileProcessor";

    /**
     * Style sheet to use for transforming model objects.
     *
     * @parameter
     */
    private String modelObjectStylesheet;

    /** Creates a new {@code AbstractClassesCommitMojo} instance. */
    public AbstractClassesCommitMojo()
    {
        super();
    }

    /**
     * Gets transformers to use for transforming model objects.
     *
     * @param classLoader The class loader to use for loading a transformer classpath resource.
     *
     * @return A list of transformers to use for transforming model objects.
     *
     * @throws NullPointerException if {@code classLoader} is {@code null}.
     * @throws MojoExecutionException if getting the transformers fails.
     */
    protected List<Transformer> getTransformers( final ClassLoader classLoader ) throws MojoExecutionException
    {
        try
        {
            final List<Transformer> transformers = new LinkedList<Transformer>();

            if ( this.modelObjectStylesheet != null )
            {
                File f = new File( this.modelObjectStylesheet );
                if ( !f.isAbsolute() )
                {
                    f = new File( this.getMavenProject().getBasedir(), this.modelObjectStylesheet );
                }

                if ( f.exists() )
                {
                    transformers.add( this.getTransformer( f.toURI().toURL() ) );
                }
                else
                {
                    final URL url = classLoader.getResource( this.modelObjectStylesheet );
                    if ( url != null )
                    {
                        transformers.add( this.getTransformer( url ) );
                    }
                    else
                    {
                        throw new MojoExecutionException( getMessage(
                            "modelObjectStylesheetNotFound", this.modelObjectStylesheet ) );

                    }
                }
            }

            return transformers;
        }
        catch ( final TransformerConfigurationException e )
        {
            String message = e.getMessage();
            if ( message == null && e.getException() != null )
            {
                message = e.getException().getMessage();
            }

            throw new MojoExecutionException( message, e );
        }
        catch ( final IOException e )
        {
            throw new MojoExecutionException( e.getMessage(), e );
        }
    }

    /**
     * Gets a transformer from a given URL.
     *
     * @param url The URL to initialize the transformer with.
     *
     * @return A {@code Transformer} backed by {@code url}.
     *
     * @throws NullPointerException if {@code url} is {@code null}.
     * @throws TransformerConfigurationException if there are errors when parsing {@code url} or creating a
     * {@code Transformer} fails.
     * @throws IOException if reading {@code url} fails.
     */
    private Transformer getTransformer( final URL url ) throws TransformerConfigurationException, IOException
    {
        if ( url == null )
        {
            throw new NullPointerException( "url" );
        }

        final TransformerFactory transformerFactory = TransformerFactory.newInstance();
        transformerFactory.setErrorListener( new ErrorListener()
        {

            public void warning( final TransformerException exception ) throws TransformerException
            {
                log( Level.WARNING, exception.getMessage(), exception );
            }

            public void error( final TransformerException exception ) throws TransformerException
            {
                log( Level.SEVERE, exception.getMessage(), exception );
                throw exception;
            }

            public void fatalError( final TransformerException exception ) throws TransformerException
            {
                log( Level.SEVERE, exception.getMessage(), exception );
                throw exception;
            }

        } );

        return transformerFactory.newTransformer( new StreamSource( url.openStream() ) );
    }

    @Override
    protected final void executeTool() throws Exception
    {
        if ( this.isClassProcessingEnabled() )
        {
            final ClassLoader classLoader = this.getClassesClassLoader();
            final ModelContext context = this.createModelContext( classLoader );
            final ClassFileProcessor tool = this.createClassFileProcessor( context );
            final JAXBContext jaxbContext = context.createContext( this.getModel() );
            final List<Transformer> transformers = this.getTransformers( classLoader );
            final Source source = new JAXBSource( jaxbContext, new ObjectFactory().createModel( tool.getModel() ) );
            final ModelValidationReport validationReport = context.validateModel( this.getModel(), source );

            this.log( context, validationReport.isModelValid() ? Level.INFO : Level.SEVERE, validationReport );

            if ( validationReport.isModelValid() )
            {
                this.logSeparator( Level.INFO );
                final Module module = tool.getModules().getModule( this.getClassesModuleName() );

                if ( module != null )
                {
                    this.logProcessingModule( TOOLNAME, module.getName() );
                    tool.commitModelObjects( module, context, this.getClassesDirectory() );

                    if ( !transformers.isEmpty() )
                    {
                        tool.transformModelObjects( module, context, this.getClassesDirectory(), transformers );
                    }

                    this.logToolSuccess( TOOLNAME );
                }
                else
                {
                    this.logMissingModule( this.getClassesModuleName() );
                }

                this.logSeparator( Level.INFO );
            }
            else
            {
                throw new MojoExecutionException( getMessage( "failed" ) );
            }
        }
        else
        {
            this.logSeparator( Level.INFO );
            this.log( Level.INFO, getMessage( "disabled" ), null );
            this.logSeparator( Level.INFO );
        }
    }

    protected abstract String getClassesModuleName() throws MojoExecutionException;

    protected abstract ClassLoader getClassesClassLoader() throws MojoExecutionException;

    protected abstract File getClassesDirectory() throws MojoExecutionException;

    private static String getMessage( final String key, final Object... args )
    {
        return MessageFormat.format( ResourceBundle.getBundle(
            AbstractClassesCommitMojo.class.getName().replace( '.', '/' ) ).getString( key ), args );

    }

}
