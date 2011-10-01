/*
 *   Copyright (C) 2005 Christian Schulte <schulte2005@users.sourceforge.net>
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

import org.apache.maven.plugin.MojoExecutionException;
import org.jomc.model.Instance;
import org.jomc.model.Modules;
import org.jomc.model.ObjectFactory;
import org.jomc.model.modlet.ModelHelper;
import org.jomc.modlet.Model;
import org.jomc.modlet.ModelContext;

/**
 * Displays a project's main instance.
 *
 * @author <a href="mailto:schulte2005@users.sourceforge.net">Christian Schulte</a>
 * @version $JOMC$
 *
 * @goal show-main-instance
 * @threadSafe
 * @requiresDependencyResolution test
 * @since 1.1
 */
public final class MainInstanceShowMojo extends AbstractModelShowMojo
{

    /**
     * Identifier of the instance to show.
     *
     * @parameter expression="${jomc.identifier}"
     * @required
     */
    private String identifier;

    /**
     * Execution strategy of the goal ({@code always} or {@code once-per-session}).
     *
     * @parameter default-value="once-per-session" expression="${jomc.showMainInstanceExecutionStrategy}"
     */
    private String showMainInstanceExecutionStrategy;

    /** Creates a new {@code MainInstanceShowMojo} instance. */
    public MainInstanceShowMojo()
    {
        super();
    }

    @Override
    protected Model getDisplayModel( final ModelContext modelContext ) throws MojoExecutionException
    {
        final Model model = this.getModel( modelContext );
        final Modules modules = ModelHelper.getModules( model );
        final Instance instance = modules != null ? modules.getInstance( this.identifier ) : null;
        Model displayModel = null;

        if ( instance != null )
        {
            displayModel = new Model();
            displayModel.setIdentifier( model.getIdentifier() );
            displayModel.getAny().add( new ObjectFactory().createInstance( instance ) );
        }

        return displayModel;
    }

    @Override
    protected ClassLoader getDisplayClassLoader() throws MojoExecutionException
    {
        return this.getMainClassLoader();
    }

    @Override
    protected String getGoal() throws MojoExecutionException
    {
        return "show-main-instance";
    }

    @Override
    protected String getExecutionStrategy() throws MojoExecutionException
    {
        return this.showMainInstanceExecutionStrategy;
    }

}
