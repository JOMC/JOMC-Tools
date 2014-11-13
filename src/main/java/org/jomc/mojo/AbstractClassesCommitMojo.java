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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.util.JAXBSource;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.jomc.model.Module;
import org.jomc.modlet.ModelContext;
import org.jomc.modlet.ModelValidationReport;
import org.jomc.modlet.ObjectFactory;
import org.jomc.tools.ClassFileProcessor;

/**
 * Base class for committing model objects to class files.
 *
 * @author <a href="mailto:cs@schulte.it">Christian Schulte</a>
 * @version $JOMC$
 */
public abstract class AbstractClassesCommitMojo extends AbstractJomcMojo
{

    /** Constant for the name of the tool backing the mojo. */
    private static final String TOOLNAME = "ClassFileProcessor";

    /**
     * XSLT documents to use for transforming model objects.
     * <pre>
     * &lt;modelObjectStylesheetResources>
     *   &lt;modelObjectStylesheetResource>
     *     &lt;location>The location of the XSLT document.&lt;/location>
     *     &lt;optional>Flag indicating the XSLT document is optional.&lt;/optional>
     *     &lt;connectTimeout>Timeout value, in milliseconds.&lt;/connectTimeout>
     *     &lt;readTimeout>Timeout value, in milliseconds.&lt;/readTimeout>
     *     &lt;transformationParameterResources>
     *       &lt;transformationParameterResource>
     *         &lt;location>The location of the properties resource.&lt;/location>
     *         &lt;optional>Flag indicating the properties resource is optional.&lt;/optional>
     *         &lt;format>The format of the properties resource.&lt;/format>
     *         &lt;connectTimeout>Timeout value, in milliseconds.&lt;/connectTimeout>
     *         &lt;readTimeout>Timeout value, in milliseconds.&lt;/readTimeout>
     *       &lt;/transformationParameterResource>
     *     &lt;/transformationParameterResources>
     *     &lt;transformationParameters>
     *       &lt;transformationParameter>
     *         &lt;key>The name of the parameter.&lt;/key>
     *         &lt;value>The value of the parameter.&lt;/value>
     *         &lt;type>The name of the class of the parameter's object.&lt;/type>
     *       &lt;/transformationParameter>
     *     &lt;/transformationParameters>
     *     &lt;transformationOutputProperties>
     *       &lt;transformationOutputProperty>
     *         &lt;key>The name of the property.&lt;/key>
     *         &lt;value>The value of the property.&lt;/value>
     *         &lt;type>The name of the class of the properties object.&lt;/type>
     *       &lt;/transformationOutputProperty>
     *     &lt;/transformationOutputProperties>
     *   &lt;/modelObjectStylesheetResource>
     * &lt;/modelObjectStylesheetResources>
     * </pre>
     * <p>The location value is used to first search the class path of the plugin and the project's main or test class
     * path. If a class path resource is found, that resource is used. If no class path resource is found, an attempt is
     * made to parse the location value to an URL. On successful parsing, that URL is used. Otherwise the location value
     * is interpreted as a file name relative to the base directory of the project. If that file exists, that file is
     * used. If nothing is found at the given location, depending on the optional flag, a warning message is logged or a
     * build failure is produced.</p>
     * <p>The optional flag is used to flag the resource optional. When an optional resource is not found, a warning
     * message is logged instead of producing a build failure.<br/><b>Default value is:</b> false</p>
     * <p>The connectTimeout value is used to specify the timeout, in milliseconds, to be used when opening
     * communications links to the resource. A timeout of zero is interpreted as an infinite timeout.<br/>
     * <b>Default value is:</b> 60000</p>
     * <p>The readTimeout value is used to specify the timeout, in milliseconds, to be used when reading the resource.
     * A timeout of zero is interpreted as an infinite timeout.<br/>
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
     * @param modelContext The model context to search.
     *
     * @return A list of transformers to use for transforming model objects.
     *
     * @throws NullPointerException if {@code modelContext} is {@code null}.
     * @throws MojoExecutionException if getting the transformers fails.
     *
     * @since 1.8
     */
    protected List<Transformer> getTransformers( final ModelContext modelContext ) throws MojoExecutionException
    {
        if ( modelContext == null )
        {
            throw new NullPointerException( "modelContext" );
        }

        final List<Transformer> transformers = new ArrayList<Transformer>(
            this.modelObjectStylesheetResources != null ? this.modelObjectStylesheetResources.size() : 0 );

        if ( this.modelObjectStylesheetResources != null )
        {
            for ( int i = 0, s0 = this.modelObjectStylesheetResources.size(); i < s0; i++ )
            {
                final Transformer transformer =
                    this.getTransformer( modelContext, this.modelObjectStylesheetResources.get( i ) );

                if ( transformer != null )
                {
                    transformers.add( transformer );
                }
            }
        }

        return transformers;
    }

    @Override
    protected void assertValidParameters() throws MojoFailureException
    {
        super.assertValidParameters();
        this.assertValidResources( this.modelObjectStylesheetResources );
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
            final List<Transformer> transformers = this.getTransformers( context );
            final Source source = new JAXBSource( jaxbContext, new ObjectFactory().createModel( tool.getModel() ) );
            final ModelValidationReport validationReport = context.validateModel( this.getModel(), source );

            this.log( context, validationReport.isModelValid() ? Level.INFO : Level.SEVERE, validationReport );

            if ( validationReport.isModelValid() )
            {
                final Module module =
                    tool.getModules() != null ? tool.getModules().getModule( this.getClassesModuleName() ) : null;

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
                throw new MojoExecutionException( Messages.getMessage( "classFileProcessingFailure" ) );
            }
        }
        else if ( this.isLoggable( Level.INFO ) )
        {
            this.log( Level.INFO, Messages.getMessage( "classFileProcessingDisabled" ), null );
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

}
