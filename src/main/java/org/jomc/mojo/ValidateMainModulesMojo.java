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
package org.jomc.mojo;

import java.util.ResourceBundle;
import java.util.logging.Level;
import org.apache.maven.plugin.MojoExecutionException;
import org.jomc.model.ModelContext;
import org.jomc.model.ModelValidationReport;

/**
 * Validates a projects' main modules.
 *
 * @author <a href="mailto:schulte2005@users.sourceforge.net">Christian Schulte</a>
 * @version $Id$
 *
 * @phase process-classes
 * @goal validate-main-modules
 * @requiresDependencyResolution test
 */
public class ValidateMainModulesMojo extends AbstractJomcMojo
{

    /** Constant for the name of the tool backing the mojo. */
    private static final String TOOLNAME = "ModelValidator";

    /** Creates a new {@code ValidateMainModulesMojo} instance. */
    public ValidateMainModulesMojo()
    {
        super();
    }

    @Override
    protected void executeTool() throws Exception
    {
        final ModelContext context = this.createModelContext( this.getMainClassLoader() );
        final ModelValidationReport validationReport = context.validateModel( this.getToolModules( context ) );

        this.log( context, validationReport.isModelValid() ? Level.INFO : Level.SEVERE, validationReport );

        if ( !validationReport.isModelValid() )
        {
            throw new MojoExecutionException( getMessage( "failed" ) );
        }

        this.logSeparator( Level.INFO );
        this.logToolSuccess( TOOLNAME );
        this.logSeparator( Level.INFO );
    }

    private static String getMessage( final String key )
    {
        return ResourceBundle.getBundle( ValidateMainModulesMojo.class.getName().replace( '.', '/' ) ).getString( key );
    }

}
