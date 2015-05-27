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

import java.util.logging.Level;
import org.apache.maven.plugin.MojoExecutionException;
import org.jomc.modlet.ModelContext;
import org.jomc.modlet.ModelValidationReport;

/**
 * Validates a project's main model.
 *
 * @author <a href="mailto:cs@schulte.it">Christian Schulte</a>
 * @version $JOMC$
 *
 * @phase process-classes
 * @goal validate-main-model
 * @threadSafe
 * @requiresDependencyResolution test
 */
public final class MainModelValidateMojo extends AbstractJomcMojo
{

    /**
     * Constant for the name of the tool backing the mojo.
     */
    private static final String TOOLNAME = "ModelValidator";

    /**
     * Execution strategy of the goal ({@code always} or {@code once-per-session}).
     *
     * @parameter default-value="once-per-session" expression="${jomc.validateMainModelExecutionStrategy}"
     * @since 1.1
     */
    private String validateMainModelExecutionStrategy;

    /**
     * Creates a new {@code MainModelValidateMojo} instance.
     */
    public MainModelValidateMojo()
    {
        super();
    }

    @Override
    protected void executeTool() throws Exception
    {
        this.logSeparator();
        this.logProcessingModel( TOOLNAME, this.getModel() );

        final ModelContext context = this.createModelContext( this.getMainClassLoader() );
        final ModelValidationReport validationReport = context.validateModel( this.getModel( context ) );

        this.log( context, validationReport.isModelValid() ? Level.INFO : Level.SEVERE, validationReport );

        if ( !validationReport.isModelValid() )
        {
            throw new MojoExecutionException( Messages.getMessage( "failedValidatingMainModel" ) );
        }

        this.logToolSuccess( TOOLNAME );
    }

    @Override
    protected String getGoal() throws MojoExecutionException
    {
        return "validate-main-model";
    }

    @Override
    protected String getExecutionStrategy() throws MojoExecutionException
    {
        return this.validateMainModelExecutionStrategy;
    }

}
