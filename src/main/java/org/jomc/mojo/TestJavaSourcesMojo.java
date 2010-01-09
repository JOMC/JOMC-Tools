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

import java.io.File;
import java.util.ResourceBundle;
import java.util.logging.Level;
import org.apache.maven.plugin.MojoExecutionException;
import org.jomc.model.ModelValidationReport;
import org.jomc.model.Module;
import org.jomc.tools.JavaSources;

/**
 * Manages a projects' test java sources.
 *
 * @author <a href="mailto:cs@jomc.org">Christian Schulte</a>
 * @version $Id$
 *
 * @phase process-test-resources
 * @goal test-java-sources
 * @requiresDependencyResolution test
 */
public final class TestJavaSourcesMojo extends AbstractJomcMojo
{

    /** Creates a new {@code TestJavaSourcesMojo} instance. */
    public TestJavaSourcesMojo()
    {
        super();
    }

    @Override
    protected String getToolName()
    {
        return "JavaSources";
    }

    @Override
    protected ClassLoader getToolClassLoader() throws MojoExecutionException
    {
        return this.getTestClassLoader();
    }

    @Override
    protected void executeTool() throws Exception
    {
        if ( this.isJavaSourceProcessingEnabled() )
        {
            File testSourceDirectory = new File( this.getMavenProject().getBuild().getTestSourceDirectory() );

            if ( !testSourceDirectory.isAbsolute() )
            {
                testSourceDirectory = new File( this.getMavenProject().getBasedir(),
                                                this.getMavenProject().getBuild().getTestSourceDirectory() );

            }

            final JavaSources tool = this.getJavaSourcesTool();
            final ModelValidationReport validationReport =
                this.getModelContext().validateModelObject( tool.getModules() );

            this.log( validationReport.isModelValid() ? Level.INFO : Level.SEVERE, validationReport );

            if ( validationReport.isModelValid() )
            {
                this.logSeparator( Level.INFO );
                final Module module = tool.getModules().getModule( this.getJomcTestModuleName() );
                if ( module != null )
                {
                    this.logProcessingModule( module );
                    tool.manageSources( module, testSourceDirectory );
                    this.logToolSuccess();
                }
                else
                {
                    this.logMissingModule( this.getJomcTestModuleName() );
                }
                this.logSeparator( Level.INFO );
            }
            else
            {
                throw new MojoExecutionException( this.getMessage( "failed" ) );
            }
        }
        else
        {
            this.logSeparator( Level.INFO );
            this.log( Level.INFO, this.getMessage( "disabled" ), null );
            this.logSeparator( Level.INFO );
        }
    }

    private String getMessage( final String key )
    {
        return ResourceBundle.getBundle( TestJavaSourcesMojo.class.getName().replace( '.', '/' ) ).getString( key );
    }

}
