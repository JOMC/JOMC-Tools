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
package org.jomc.ant;

import org.apache.tools.ant.BuildException;
import org.jomc.model.Module;
import org.jomc.model.Modules;
import org.jomc.model.modlet.DefaultModelProcessor;
import org.jomc.model.modlet.DefaultModelProvider;
import org.jomc.model.modlet.ModelHelper;
import org.jomc.modlet.Model;
import org.jomc.modlet.ModelContext;
import org.jomc.modlet.ModelException;

/**
 * Base class for executing model based tasks.
 *
 * @author <a href="mailto:schulte2005@users.sourceforge.net">Christian Schulte</a>
 * @version $Id$
 */
public class JomcModelTask extends JomcTask
{

    /** Controls model object classpath resolution. */
    private boolean modelObjectClasspathResolutionEnabled = true;

    /** The location to search for modules. */
    private String moduleLocation;

    /** The location to search for transformers. */
    private String transformerLocation;

    /** Creates a new {@code JomcModelTask} instance. */
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
     * @return {@code true} if model object class path resolution is enabled; {@code false} else.
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
     * @param value {@code true} to enable model object class path resolution; {@code false} to disable model object
     * class path resolution.
     *
     * @see #isModelObjectClasspathResolutionEnabled()
     */
    public final void setModelObjectClasspathResolutionEnabled( final boolean value )
    {
        this.modelObjectClasspathResolutionEnabled = value;
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

        Model model = context.findModel( this.getModel() );

        if ( model != null )
        {
            if ( this.isModelObjectClasspathResolutionEnabled() )
            {
                final Modules modules = ModelHelper.getModules( model );

                if ( modules != null )
                {
                    final Module classpathModule =
                        modules.getClasspathModule( Modules.getDefaultClasspathModuleName(), context.getClassLoader() );

                    if ( classpathModule != null )
                    {
                        modules.getModule().add( classpathModule );
                    }
                }
            }

            if ( this.isModelProcessingEnabled() )
            {
                model = context.processModel( model );
            }
        }

        return model;
    }

    /** {@inheritDoc} */
    @Override
    public void preExecuteTask() throws BuildException
    {
        super.preExecuteTask();

        DefaultModelProvider.setDefaultModuleLocation( this.getModuleLocation() );
        DefaultModelProcessor.setDefaultTransformerLocation( this.getTransformerLocation() );
    }

    /** {@inheritDoc} */
    @Override
    public void postExecuteTask() throws BuildException
    {
        DefaultModelProvider.setDefaultModuleLocation( null );
        DefaultModelProcessor.setDefaultTransformerLocation( null );

        super.postExecuteTask();
    }

    /** {@inheritDoc} */
    @Override
    public JomcModelTask clone()
    {
        return (JomcModelTask) super.clone();
    }

}
