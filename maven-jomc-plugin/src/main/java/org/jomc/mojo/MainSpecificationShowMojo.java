/*
 *   Copyright (C) Christian Schulte <cs@schulte.it>, 2005-206
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

import javax.xml.bind.JAXBElement;
import org.apache.maven.plugin.MojoExecutionException;
import org.jomc.model.Modules;
import org.jomc.model.Specification;
import org.jomc.model.modlet.ModelHelper;
import org.jomc.modlet.Model;
import org.jomc.modlet.ModelContext;

/**
 * Displays a specification from the project's main model.
 *
 * @author <a href="mailto:cs@schulte.it">Christian Schulte</a>
 * @version $JOMC$
 *
 * @goal show-main-specification
 * @threadSafe
 * @requiresDependencyResolution test
 * @since 1.1
 */
public final class MainSpecificationShowMojo extends AbstractModelShowMojo
{

    /**
     * Identifier of the specification to show.
     *
     * @parameter expression="${jomc.identifier}"
     * @required
     */
    private String identifier;

    /**
     * Execution strategy of the goal ({@code always} or {@code once-per-session}).
     *
     * @parameter default-value="once-per-session" expression="${jomc.showMainSpecificationExecutionStrategy}"
     */
    private String showMainSpecificationExecutionStrategy;

    /**
     * Creates a new {@code MainSpecificationShowMojo} instance.
     */
    public MainSpecificationShowMojo()
    {
        super();
    }

    @Override
    protected JAXBElement<?> getDisplayModel( final ModelContext modelContext ) throws MojoExecutionException
    {
        final Model model = this.getModel( modelContext );
        final Modules modules = ModelHelper.getModules( model );
        final Specification specification = modules != null ? modules.getSpecification( this.identifier ) : null;
        JAXBElement<?> displayModel = null;

        if ( specification != null )
        {
            displayModel = new org.jomc.model.ObjectFactory().createSpecification( specification );
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
        return "show-main-specification";
    }

    @Override
    protected String getExecutionStrategy() throws MojoExecutionException
    {
        return this.showMainSpecificationExecutionStrategy;
    }

}
