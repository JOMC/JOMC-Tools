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
package org.jomc.tools.maven;

import java.util.logging.Level;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.jomc.modlet.ModelContext;
import org.jomc.modlet.ModelValidationReport;

/**
 * Validates a project's test model.
 *
 * @author <a href="mailto:cs@schulte.it">Christian Schulte</a>
 * @version $JOMC$
 */
@Mojo( name = "validate-test-model",
       defaultPhase = LifecyclePhase.PROCESS_TEST_CLASSES,
       requiresDependencyResolution = ResolutionScope.TEST,
       threadSafe = true )
public final class TestModelValidateMojo extends AbstractJomcMojo
{

    /**
     * Constant for the name of the tool backing the mojo.
     */
    private static final String TOOLNAME = "ModelValidator";

    /**
     * Execution strategy of the goal ({@code always} or {@code once-per-session}).
     *
     * @since 1.1
     */
    @Parameter( name = "validateTestModelExecutionStrategy",
                property = "jomc.validateTestModelExecutionStrategy",
                defaultValue = "once-per-session" )
    private String validateTestModelExecutionStrategy;

    /**
     * Creates a new {@code TestModelValidateMojo} instance.
     */
    public TestModelValidateMojo()
    {
        super();
    }

    @Override
    protected void executeTool() throws Exception
    {
        this.logSeparator();
        this.logProcessingModel( TOOLNAME, this.getModel() );

        final ModelContext context = this.createModelContext( this.getTestClassLoader() );
        final ModelValidationReport validationReport = context.validateModel( this.getModel( context ) );

        this.log( context, validationReport.isModelValid() ? Level.INFO : Level.SEVERE, validationReport );

        if ( !validationReport.isModelValid() )
        {
            throw new MojoExecutionException( Messages.getMessage( "failedValidatingTestModel" ) );
        }

        this.logToolSuccess( TOOLNAME );
    }

    @Override
    protected String getGoal() throws MojoExecutionException
    {
        return "validate-test-model";
    }

    @Override
    protected String getExecutionStrategy() throws MojoExecutionException
    {
        return this.validateTestModelExecutionStrategy;
    }

}
