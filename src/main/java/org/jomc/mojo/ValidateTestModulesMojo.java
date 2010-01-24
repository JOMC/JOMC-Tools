/*
 *   Copyright (c) 2009 The JOMC Project
 *   Copyright (c) 2005 Christian Schulte <cs@jomc.org>
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

import java.util.ResourceBundle;
import java.util.logging.Level;
import org.apache.maven.plugin.MojoExecutionException;
import org.jomc.model.ModelValidationReport;

/**
 * Validates a projects' main modules.
 *
 * @author <a href="mailto:cs@jomc.org">Christian Schulte</a>
 * @version $Id$
 *
 * @phase process-test-classes
 * @goal validate-test-modules
 * @requiresDependencyResolution test
 */
public class ValidateTestModulesMojo extends AbstractJomcMojo
{

    /** Creates a new {@code ValidateTestModulesMojo} instance. */
    public ValidateTestModulesMojo()
    {
        super();
    }

    @Override
    protected String getToolName()
    {
        return "validate-test-modules";
    }

    @Override
    protected ClassLoader getToolClassLoader() throws MojoExecutionException
    {
        return this.getTestClassLoader();
    }

    @Override
    protected void executeTool() throws Exception
    {
        final ModelValidationReport validationReport = this.getModelContext().validateModel( this.getToolModules() );

        this.log( validationReport.isModelValid() ? Level.INFO : Level.SEVERE, validationReport );

        if ( !validationReport.isModelValid() )
        {
            throw new MojoExecutionException( this.getMessage( "failed" ) );
        }

        this.logSeparator( Level.INFO );
        this.logToolSuccess();
        this.logSeparator( Level.INFO );
    }

    private String getMessage( final String key )
    {
        return ResourceBundle.getBundle( ValidateTestModulesMojo.class.getName().replace( '.', '/' ) ).getString( key );
    }

}
