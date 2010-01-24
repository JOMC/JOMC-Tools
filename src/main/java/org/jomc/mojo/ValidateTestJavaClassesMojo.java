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
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.util.JAXBSource;
import javax.xml.validation.Schema;
import org.apache.maven.plugin.MojoExecutionException;
import org.jomc.model.ModelContext;
import org.jomc.model.ModelValidationReport;
import org.jomc.model.Module;
import org.jomc.model.ObjectFactory;
import org.jomc.tools.JavaClasses;

/**
 * Validates a projects' test java classes.
 *
 * @author <a href="mailto:cs@jomc.org">Christian Schulte</a>
 * @version $Id$
 *
 * @phase process-test-classes
 * @goal validate-test-java-classes
 * @requiresDependencyResolution test
 */
public class ValidateTestJavaClassesMojo extends AbstractJomcMojo
{

    /** Constant for the name of the tool backing the mojo. */
    private static final String TOOLNAME = "JavaClasses";

    /** Creates a new {@code ValidateTestJavaClassesMojo} instance. */
    public ValidateTestJavaClassesMojo()
    {
        super();
    }

    @Override
    protected void executeTool() throws Exception
    {
        if ( this.isClassProcessingEnabled() )
        {
            File classesDirectory = new File( this.getMavenProject().getBuild().getTestOutputDirectory() );
            if ( !classesDirectory.isAbsolute() )
            {
                classesDirectory = new File( this.getMavenProject().getBasedir(),
                                             this.getMavenProject().getBuild().getTestOutputDirectory() );

            }

            final ModelContext context = this.getModelContext( this.getTestClassLoader() );
            final JavaClasses tool = this.getJavaClassesTool( context );
            final JAXBContext jaxbContext = context.createContext();
            final Unmarshaller unmarshaller = context.createUnmarshaller();
            final Schema schema = context.createSchema();

            unmarshaller.setSchema( schema );

            final ModelValidationReport validationReport = context.validateModel( new JAXBSource(
                jaxbContext, new ObjectFactory().createModules( tool.getModules() ) ) );

            this.log( context, validationReport.isModelValid() ? Level.INFO : Level.SEVERE, validationReport );

            if ( validationReport.isModelValid() )
            {
                this.logSeparator( Level.INFO );
                final Module module = tool.getModules().getModule( this.getJomcTestModuleName() );

                if ( module != null )
                {
                    this.logProcessingModule( TOOLNAME, module );
                    tool.validateClasses( module, unmarshaller, classesDirectory );
                    this.logToolSuccess( TOOLNAME );
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
        return ResourceBundle.getBundle( ValidateTestJavaClassesMojo.class.getName().replace( '.', '/' ) ).
            getString( key );

    }

}
