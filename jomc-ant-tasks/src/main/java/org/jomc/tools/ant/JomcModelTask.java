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
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.jomc.model.Module;
import org.jomc.model.Modules;
import org.jomc.model.modlet.DefaultModelProcessor;
import org.jomc.model.modlet.DefaultModelProvider;
import org.jomc.model.modlet.DefaultModelValidator;
import org.jomc.model.modlet.ModelHelper;
import org.jomc.modlet.Model;
import org.jomc.modlet.ModelContext;
import org.jomc.modlet.ModelException;
import org.jomc.tools.ant.types.KeyValueType;
import org.jomc.tools.ant.types.ModuleResourceType;
import org.jomc.tools.ant.types.ResourceType;
import org.jomc.tools.modlet.ToolsModelProcessor;
import org.jomc.tools.modlet.ToolsModelProvider;

/**
 * Base class for executing model based tasks.
 *
 * @author <a href="mailto:cs@schulte.it">Christian Schulte</a>
 * @version $JOMC$
 */
public class JomcModelTask extends JomcTask
{

    /**
     * Controls model object class path resolution.
     */
    private boolean modelObjectClasspathResolutionEnabled = true;

    /**
     * The location to search for modules.
     */
    private String moduleLocation;

    /**
     * The location to search for transformers.
     */
    private String transformerLocation;

    /**
     * Module resources.
     */
    private Set<ModuleResourceType> moduleResources;

    /**
     * The flag indicating JAXP schema validation of model resources is enabled.
     */
    private boolean modelResourceValidationEnabled = true;

    /**
     * The flag indicating Java validation is enabled.
     */
    private boolean javaValidationEnabled = true;

    /**
     * Creates a new {@code JomcModelTask} instance.
     */
    public JomcModelTask()
    {
        super();
    }

    /**
     * Gets the location searched for modules.
     *
     * @return The location searched for modules or {@code null}.
     *
     * @see #setModuleLocation(java.lang.String)
     */
    public final String getModuleLocation()
    {
        return this.moduleLocation;
    }

    /**
     * Sets the location to search for modules.
     *
     * @param value The new location to search for modules or {@code null}.
     *
     * @see #getModuleLocation()
     */
    public final void setModuleLocation( final String value )
    {
        this.moduleLocation = value;
    }

    /**
     * Gets the location searched for transformers.
     *
     * @return The location searched for transformers or {@code null}.
     *
     * @see #setTransformerLocation(java.lang.String)
     */
    public final String getTransformerLocation()
    {
        return this.transformerLocation;
    }

    /**
     * Sets the location to search for transformers.
     *
     * @param value The new location to search for transformers or {@code null}.
     *
     * @see #getTransformerLocation()
     */
    public final void setTransformerLocation( final String value )
    {
        this.transformerLocation = value;
    }

    /**
     * Gets a flag indicating model object class path resolution is enabled.
     *
     * @return {@code true}, if model object class path resolution is enabled; {@code false}, else.
     *
     * @see #setModelObjectClasspathResolutionEnabled(boolean)
     */
    public final boolean isModelObjectClasspathResolutionEnabled()
    {
        return this.modelObjectClasspathResolutionEnabled;
    }

    /**
     * Sets the flag indicating model object class path resolution is enabled.
     *
     * @param value {@code true}, to enable model object class path resolution; {@code false}, to disable model object
     * class path resolution.
     *
     * @see #isModelObjectClasspathResolutionEnabled()
     */
    public final void setModelObjectClasspathResolutionEnabled( final boolean value )
    {
        this.modelObjectClasspathResolutionEnabled = value;
    }

    /**
     * Gets a set of module resources.
     * <p>
     * This accessor method returns a reference to the live set, not a snapshot. Therefore any modification you make
     * to the returned set will be present inside the object. This is why there is no {@code set} method for the
     * module resources property.
     * </p>
     *
     * @return A set of module resources.
     *
     * @see #createModuleResource()
     */
    public Set<ModuleResourceType> getModuleResources()
    {
        if ( this.moduleResources == null )
        {
            this.moduleResources = new HashSet<ModuleResourceType>();
        }

        return this.moduleResources;
    }

    /**
     * Creates a new {@code moduleResource} element instance.
     *
     * @return A new {@code moduleResource} element instance.
     *
     * @see #getModuleResources()
     */
    public ModuleResourceType createModuleResource()
    {
        final ModuleResourceType moduleResource = new ModuleResourceType();
        this.getModuleResources().add( moduleResource );
        return moduleResource;
    }

    /**
     * Gets a flag indicating JAXP schema validation of model resources is enabled.
     *
     * @return {@code true}, if JAXP schema validation of model resources is enabled; {@code false}, else.
     *
     * @see #setModelResourceValidationEnabled(boolean)
     */
    public final boolean isModelResourceValidationEnabled()
    {
        return this.modelResourceValidationEnabled;
    }

    /**
     * Sets the flag indicating JAXP schema validation of model resources is enabled.
     *
     * @param value {@code true}, to enable JAXP schema validation of model resources; {@code false}, to disable JAXP
     * schema validation of model resources.
     *
     * @see #isModelResourceValidationEnabled()
     */
    public final void setModelResourceValidationEnabled( final boolean value )
    {
        this.modelResourceValidationEnabled = value;
    }

    /**
     * Gets a flag indicating Java validation is enabled.
     *
     * @return {@code true}, if Java validation is enabled; {@code false}, else.
     *
     * @see #setJavaValidationEnabled(boolean)
     *
     * @since 1.4
     */
    public final boolean isJavaValidationEnabled()
    {
        return this.javaValidationEnabled;
    }

    /**
     * Sets the flag indicating Java validation is enabled.
     *
     * @param value {@code true}, to enable Java validation; {@code false}, to disable Java validation.
     *
     * @see #isJavaValidationEnabled()
     *
     * @since 1.4
     */
    public final void setJavaValidationEnabled( final boolean value )
    {
        this.javaValidationEnabled = value;
    }

    /**
     * Gets a {@code Model} from a given {@code ModelContext}.
     *
     * @param context The context to get a {@code Model} from.
     *
     * @return The {@code Model} from {@code context}.
     *
     * @throws NullPointerException if {@code contexŧ} is {@code null}.
     * @throws BuildException if no model is found.
     * @throws ModelException if getting the model fails.
     *
     * @see #getModel()
     * @see #isModelObjectClasspathResolutionEnabled()
     * @see #isModelProcessingEnabled()
     */
    @Override
    public Model getModel( final ModelContext context ) throws BuildException, ModelException
    {
        if ( context == null )
        {
            throw new NullPointerException( "context" );
        }

        Model model = new Model();
        model.setIdentifier( this.getModel() );
        Modules modules = new Modules();
        ModelHelper.setModules( model, modules );
        Unmarshaller unmarshaller = null;

        for ( final ResourceType resource : this.getModuleResources() )
        {
            final URL[] urls = this.getResources( context, resource.getLocation() );

            if ( urls.length == 0 )
            {
                if ( resource.isOptional() )
                {
                    this.logMessage( Level.WARNING, Messages.getMessage( "moduleResourceNotFound",
                                                                         resource.getLocation() ) );

                }
                else
                {
                    throw new BuildException( Messages.getMessage( "moduleResourceNotFound", resource.getLocation() ),
                                              this.getLocation() );

                }
            }

            for ( int i = urls.length - 1; i >= 0; i-- )
            {
                URLConnection con = null;
                InputStream in = null;

                try
                {
                    this.logMessage( Level.FINEST, Messages.getMessage( "reading", urls[i].toExternalForm() ) );

                    con = urls[i].openConnection();
                    con.setConnectTimeout( resource.getConnectTimeout() );
                    con.setReadTimeout( resource.getReadTimeout() );
                    con.connect();
                    in = con.getInputStream();

                    final Source source = new StreamSource( in, urls[i].toURI().toASCIIString() );

                    if ( unmarshaller == null )
                    {
                        unmarshaller = context.createUnmarshaller( this.getModel() );
                        if ( this.isModelResourceValidationEnabled() )
                        {
                            unmarshaller.setSchema( context.createSchema( this.getModel() ) );
                        }
                    }

                    Object o = unmarshaller.unmarshal( source );
                    if ( o instanceof JAXBElement<?> )
                    {
                        o = ( (JAXBElement<?>) o ).getValue();
                    }

                    if ( o instanceof Module )
                    {
                        modules.getModule().add( (Module) o );
                    }
                    else
                    {
                        this.log( Messages.getMessage( "unsupportedModuleResource", urls[i].toExternalForm() ),
                                  Project.MSG_WARN );

                    }

                    in.close();
                    in = null;
                }
                catch ( final SocketTimeoutException e )
                {
                    String message = Messages.getMessage( e );
                    message = Messages.getMessage( "resourceTimeout", message != null ? " " + message : "" );

                    if ( resource.isOptional() )
                    {
                        this.getProject().log( message, e, Project.MSG_WARN );
                    }
                    else
                    {
                        throw new BuildException( message, e, this.getLocation() );
                    }
                }
                catch ( final IOException e )
                {
                    String message = Messages.getMessage( e );
                    message = Messages.getMessage( "resourceFailure", message != null ? " " + message : "" );

                    if ( resource.isOptional() )
                    {
                        this.getProject().log( message, e, Project.MSG_WARN );
                    }
                    else
                    {
                        throw new BuildException( message, e, this.getLocation() );
                    }
                }
                catch ( final URISyntaxException e )
                {
                    throw new BuildException( Messages.getMessage( e ), e, this.getLocation() );
                }
                catch ( final JAXBException e )
                {
                    String message = Messages.getMessage( e );
                    if ( message == null )
                    {
                        message = Messages.getMessage( e.getLinkedException() );
                    }

                    throw new BuildException( message, e, this.getLocation() );
                }
                finally
                {
                    try
                    {
                        if ( in != null )
                        {
                            in.close();
                        }
                    }
                    catch ( final IOException e )
                    {
                        this.logMessage( Level.SEVERE, Messages.getMessage( e ), e );
                    }
                    finally
                    {
                        if ( con instanceof HttpURLConnection )
                        {
                            ( (HttpURLConnection) con ).disconnect();
                        }
                    }
                }
            }
        }

        model = context.findModel( model );
        modules = ModelHelper.getModules( model );

        if ( modules != null && this.isModelObjectClasspathResolutionEnabled() )
        {
            final Module classpathModule =
                modules.getClasspathModule( Modules.getDefaultClasspathModuleName(), context.getClassLoader() );

            if ( classpathModule != null && modules.getModule( Modules.getDefaultClasspathModuleName() ) == null )
            {
                modules.getModule().add( classpathModule );
            }
        }

        if ( this.isModelProcessingEnabled() )
        {
            model = context.processModel( model );
        }

        return model;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void preExecuteTask() throws BuildException
    {
        super.preExecuteTask();
        this.assertLocationsNotNull( this.getModuleResources() );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ModelContext newModelContext( final ClassLoader classLoader ) throws ModelException
    {
        final ModelContext modelContext = super.newModelContext( classLoader );

        if ( this.getTransformerLocation() != null )
        {
            modelContext.setAttribute( DefaultModelProcessor.TRANSFORMER_LOCATION_ATTRIBUTE_NAME,
                                       this.getTransformerLocation() );

        }

        if ( this.getModuleLocation() != null )
        {
            modelContext.setAttribute( DefaultModelProvider.MODULE_LOCATION_ATTRIBUTE_NAME, this.getModuleLocation() );
        }

        modelContext.setAttribute( ToolsModelProvider.MODEL_OBJECT_CLASSPATH_RESOLUTION_ENABLED_ATTRIBUTE_NAME,
                                   this.isModelObjectClasspathResolutionEnabled() );

        modelContext.setAttribute( ToolsModelProcessor.MODEL_OBJECT_CLASSPATH_RESOLUTION_ENABLED_ATTRIBUTE_NAME,
                                   this.isModelObjectClasspathResolutionEnabled() );

        modelContext.setAttribute( DefaultModelProvider.VALIDATING_ATTRIBUTE_NAME,
                                   this.isModelResourceValidationEnabled() );

        modelContext.setAttribute( DefaultModelValidator.VALIDATE_JAVA_ATTRIBUTE_NAME, this.isJavaValidationEnabled() );

        for ( int i = 0, s0 = this.getModelContextAttributes().size(); i < s0; i++ )
        {
            final KeyValueType kv = this.getModelContextAttributes().get( i );
            final Object object = kv.getObject( this.getLocation() );

            if ( object != null )
            {
                modelContext.setAttribute( kv.getKey(), object );
            }
            else
            {
                modelContext.clearAttribute( kv.getKey() );
            }
        }

        return modelContext;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JomcModelTask clone()
    {
        final JomcModelTask clone = (JomcModelTask) super.clone();

        if ( this.moduleResources != null )
        {
            clone.moduleResources = new HashSet<ModuleResourceType>( this.moduleResources.size() );
            for ( final ModuleResourceType e : this.moduleResources )
            {
                clone.moduleResources.add( e.clone() );
            }
        }

        return clone;
    }

}
