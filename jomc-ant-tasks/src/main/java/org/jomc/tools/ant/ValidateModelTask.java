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
import org.apache.tools.ant.BuildException;
import org.jomc.modlet.Model;
import org.jomc.modlet.ModelContext;
import org.jomc.modlet.ModelException;
import org.jomc.modlet.ModelValidationReport;

/**
 * Task for validating model objects.
 *
 * @author <a href="mailto:cs@schulte.it">Christian Schulte</a>
 * @version $JOMC$
 */
public final class ValidateModelTask extends JomcModelTask
{

    /**
     * Creates a new {@code ValidateModelTask} instance.
     */
    public ValidateModelTask()
    {
        super();
    }

    /**
     * Validates a model.
     *
     * @throws BuildException if validating a model fails.
     */
    @Override
    public void executeTask() throws BuildException
    {
        this.log( Messages.getMessage( "validatingModel", this.getModel() ) );

        try ( final ProjectClassLoader classLoader = this.newProjectClassLoader() )
        {
            final ModelContext context = this.newModelContext( classLoader );
            final Model model = context.findModel( this.getModel() );
            final ModelValidationReport report = context.validateModel( model );
            this.logValidationReport( context, report );

            if ( !report.isModelValid() )
            {
                throw new BuildException( Messages.getMessage( "modelValidationFailure" ), this.getLocation() );
            }

            this.log( Messages.getMessage( "modelValidationSuccess" ) );
        }
        catch ( final IOException | ModelException e )
        {
            throw new BuildException( Messages.getMessage( e ), e, this.getLocation() );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ValidateModelTask clone()
    {
        return (ValidateModelTask) super.clone();
    }

}
