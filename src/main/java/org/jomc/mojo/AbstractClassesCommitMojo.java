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
import java.net.URISyntaxException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.util.JAXBSource;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
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
     * XSLT document to use for transforming model objects.
     * <p>The value of the parameter is a location to search a XSLT document at. First the value is used to search the
     * class path of the plugin. If a class path resource is found, a XSLT document is loaded from that resource. If no
     * class path resource is found, an attempt is made to parse the value to an URL. Succeeding that, an XSLT document
     * is loaded from that URL (since version 1.2). Failing that, the value is interpreted as a file name of a XSLT
     * document to load relative to the base directory of the project. If that file exists, a XSLT document is loaded
     * from that file. If no XSLT document is found at the given location, a build failure is produced.</p>
     * <p><b>Note:</b> When upgrading to version 1.2, any project dependencies holding XSLT documents referenced by this
     * parameter need to be added to the plugins' dependencies.</p>
     *
     * @parameter
     * @deprecated As of JOMC 1.2, please use parameter 'modelObjectStylesheetResources'.
     */
    @Deprecated
    private String modelObjectStylesheet;

    /**
     * XSLT documents to use for transforming model objects.
     * <pre>
     * &lt;modelObjectStylesheetResources>
     *   &lt;modelObjectStylesheetResource>
     *     &lt;location>The location of the XSLT document.&lt;/location>
     *     &lt;optional>Flag indicating the XSLT document is optional.&lt;/optional>
     *     &lt;connectTimeout>Timeout value, in milliseconds.&lt;/connectTimeout>
     *     &lt;readTimeout>Timeout value, in milliseconds.&lt;/readTimeout>
     *   &lt;/modelObjectStylesheetResource>
     * &lt;/modelObjectStylesheetResources>
     * </pre>
     * <p>The location value is used to first search the class path of the plugin. If a class path resource is found,
     * that resource is used. If no class path resource is found, an attempt is made to parse the location value to an
     * URL. On successful parsing, that URL is used. Otherwise the location value is interpreted as a file name relative
     * to the base directory of the project. If that file exists, that file is used. If nothing is found at the given
     * location, depending on the optional flag, a warning message is logged or a build failure is produced.</p>
     * <p>The optional flag is used to flag the resource optional. When an optional resource is not found, a warning
     * message is logged instead of producing a build failure.<br/><b>Default value is:</b> false</p>
     * <p>The optional connectTimeout value is used to specify the timeout, in milliseconds, to be used when opening
     * communications links to the resource. A timeout of zero is interpreted as an infinite timeout.<br/>
     * <b>Default value is:</b> 60000</p>
     * <p>The optional readTimeout value is used to specify the timeout, in milliseconds, to be used when reading the
     * resource. A timeout of zero is interpreted as an infinite timeout.<br/>
     * <b>Default value is:</b> 60000</p>
     *
     * @parameter
     * @since 1.2
     */
    private List<ModelObjectStylesheetResource> modelObjectStylesheetResources;

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
     *
     * @deprecated As of JOMC 1.2, the dependencies of the project are no longer searched for XSLT documents. Please
     * use method {@link #getTransformers()}.
     */
    @Deprecated
    protected List<Transformer> getTransformers( final ClassLoader classLoader ) throws MojoExecutionException
    {
        if ( classLoader == null )
        {
            throw new NullPointerException( "classLoader" );
        }

        return this.getTransformers();
    }

    /**
     * Gets transformers to use for transforming model objects.
     *
     * @return A list of transformers to use for transforming model objects.
     *
     * @throws NullPointerException if {@code classLoader} is {@code null}.
     * @throws MojoExecutionException if getting the transformers fails.
     *
     * @since 1.2
     */
    protected List<Transformer> getTransformers() throws MojoExecutionException
    {
        final List<Transformer> transformers = new ArrayList<Transformer>(
            this.modelObjectStylesheetResources != null ? this.modelObjectStylesheetResources.size() + 1 : 1 );

        try
        {
            if ( this.modelObjectStylesheet != null )
            {
                final URL url = this.getResource( this.modelObjectStylesheet );

                if ( url != null )
                {
                    transformers.add( this.getTransformer( new StreamSource( url.toURI().toASCIIString() ) ) );
                }
                else
                {
                    throw new MojoExecutionException(
                        getMessage( "modelObjectStylesheetNotFound", this.modelObjectStylesheet ) );

                }
            }

            if ( this.modelObjectStylesheetResources != null )
            {
                for ( ModelObjectStylesheetResource r : this.modelObjectStylesheetResources )
                {
                    final URL url = this.getResource( r.getLocation() );

                    if ( url != null )
                    {
                        transformers.add( this.getTransformer( new StreamSource( url.toURI().toASCIIString() ) ) );
                    }
                    else if ( r.isOptional() )
                    {
                        if ( this.isLoggable( Level.WARNING ) )
                        {
                            this.log( Level.WARNING,
                                      getMessage( "modelObjectStylesheetNotFound", r.getLocation() ), null );

                        }
                    }
                    else
                    {
                        throw new MojoExecutionException(
                            getMessage( "modelObjectStylesheetNotFound", r.getLocation() ) );

                    }
                }
            }

            return transformers;
        }
        catch ( final URISyntaxException e )
        {
            throw new MojoExecutionException( getMessage( e ), e );
        }
    }

    @Override
    protected final void executeTool() throws Exception
    {
        this.logSeparator();

        if ( this.isClassProcessingEnabled() )
        {
            this.logProcessingModule( TOOLNAME, this.getClassesModuleName() );

            final ClassLoader classLoader = this.getClassesClassLoader();
            final ModelContext context = this.createModelContext( classLoader );
            final ClassFileProcessor tool = this.createClassFileProcessor( context );
            final JAXBContext jaxbContext = context.createContext( this.getModel() );
            final List<Transformer> transformers = this.getTransformers();
            final Source source = new JAXBSource( jaxbContext, new ObjectFactory().createModel( tool.getModel() ) );
            final ModelValidationReport validationReport = context.validateModel( this.getModel(), source );

            this.log( context, validationReport.isModelValid() ? Level.INFO : Level.SEVERE, validationReport );

            if ( validationReport.isModelValid() )
            {
                final Module module = tool.getModules().getModule( this.getClassesModuleName() );

                if ( module != null )
                {
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
            }
            else
            {
                throw new MojoExecutionException( getMessage( "failed" ) );
            }
        }
        else if ( this.isLoggable( Level.INFO ) )
        {
            this.log( Level.INFO, getMessage( "disabled" ), null );
        }
    }

    /**
     * Gets the name of the module to commit class file model objects of.
     *
     * @return The name of the module to commit class file model objects of.
     *
     * @throws MojoExecutionException if getting the name fails.
     */
    protected abstract String getClassesModuleName() throws MojoExecutionException;

    /**
     * Gets the class loader to use for committing class file model objects.
     *
     * @return The class loader to use for committing class file model objects.
     *
     * @throws MojoExecutionException if getting the class loader fails.
     */
    protected abstract ClassLoader getClassesClassLoader() throws MojoExecutionException;

    /**
     * Gets the directory holding the class files to commit model objects to.
     *
     * @return The directory holding the class files to commit model objects to.
     *
     * @throws MojoExecutionException if getting the directory fails.
     */
    protected abstract File getClassesDirectory() throws MojoExecutionException;

    private static String getMessage( final String key, final Object... args )
    {
        return MessageFormat.format( ResourceBundle.getBundle(
            AbstractClassesCommitMojo.class.getName().replace( '.', '/' ) ).getString( key ), args );

    }

    private static String getMessage( final Throwable t )
    {
        return t != null ? t.getMessage() != null ? t.getMessage() : getMessage( t.getCause() ) : null;
    }

}
