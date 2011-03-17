/*
 *   Copyright (C) 2009 The JOMC Project
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
import org.jomc.modlet.Model;
import org.jomc.modlet.ModelContext;
import org.jomc.modlet.ModelException;
import org.jomc.modlet.ModelValidationReport;

/**
 * Task for validating model objects.
 *
 * @author <a href="mailto:schulte2005@users.sourceforge.net">Christian Schulte</a>
 * @version $Id$
 */
public final class ValidateModelTask extends JomcModelTask
{

    /** Creates a new {@code ValidateModelTask} instance. */
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
        try
        {
            this.log( Messages.getMessage( "validatingModel", this.getModel() ) );

            final ClassLoader classLoader = this.newProjectClassLoader();
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
        catch ( final ModelException e )
        {
            throw new BuildException( Messages.getMessage( e ), e, this.getLocation() );
        }
    }

    /** {@inheritDoc} */
    @Override
    public ValidateModelTask clone()
    {
        return (ValidateModelTask) super.clone();
    }

}
