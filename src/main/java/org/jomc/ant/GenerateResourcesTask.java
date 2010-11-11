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
package org.jomc.ant;

import java.io.File;
import java.text.MessageFormat;
import java.util.ResourceBundle;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.util.JAXBSource;
import javax.xml.transform.Source;
import org.jomc.model.Implementation;
import org.jomc.model.Module;
import org.jomc.model.Specification;
import org.jomc.modlet.Model;
import org.jomc.modlet.ModelContext;
import org.jomc.modlet.ModelException;
import org.jomc.modlet.ModelValidationReport;
import org.jomc.modlet.ObjectFactory;
import org.jomc.tools.ResourceFileProcessor;

/**
 * Task for generating resource files.
 *
 * @author <a href="mailto:schulte2005@users.sourceforge.net">Christian Schulte</a>
 * @version $Id$
 */
public final class GenerateResourcesTask extends ResourceFileProcessorTask
{

    /** The directory to generate resource files to. */
    private File resourcesDirectory;

    /** Creates a new {@code GenerateResourcesTask} instance. */
    public GenerateResourcesTask()
    {
        super();
    }

    /**
     * Gets the directory to generate resource files to.
     *
     * @return The directory to generate resource files to or {@code null}.
     *
     * @see #setResourcesDirectory(java.io.File)
     */
    public File getResourcesDirectory()
    {
        return this.resourcesDirectory;
    }

    /**
     * Sets the directory to generate resource files to.
     *
     * @param value The new directory to generate resource files to or {@code null}.
     *
     * @see #getResourcesDirectory()
     */
    public void setResourcesDirectory( final File value )
    {
        this.resourcesDirectory = value;
    }

    /**
     * Generates resource files.
     *
     * @throws Exception if generating resource files fails.
     */
    @Override
    public void processResourceFiles() throws Exception
    {
        assertNotNull( "resourcesDirectory", this.getResourcesDirectory() );
        assertDirectory( this.getResourcesDirectory() );

        this.log( getMessage( "generatingResources", this.getModel() ) );

        final ProjectClassLoader classLoader = this.newProjectClassLoader();
        final ModelContext context = this.newModelContext( classLoader );
        final ResourceFileProcessor tool = this.newResourceFileProcessor();
        final JAXBContext jaxbContext = context.createContext( this.getModel() );
        final Model model = this.getModel( context );
        final Source source = new JAXBSource( jaxbContext, new ObjectFactory().createModel( model ) );
        final ModelValidationReport validationReport = context.validateModel( this.getModel(), source );

        this.logValidationReport( context, validationReport );
        tool.setModel( model );

        if ( validationReport.isModelValid() )
        {
            final Specification s = this.getSpecification( model );
            final Implementation i = this.getImplementation( model );
            final Module m = this.getModule( model );

            if ( s != null )
            {
                tool.writeResourceBundleResourceFiles( s, this.getResourcesDirectory() );
            }

            if ( i != null )
            {
                tool.writeResourceBundleResourceFiles( i, this.getResourcesDirectory() );
            }

            if ( m != null )
            {
                tool.writeResourceBundleResourceFiles( m, this.getResourcesDirectory() );
            }

            if ( this.isModulesProcessingRequested() )
            {
                tool.writeResourceBundleResourceFiles( this.getResourcesDirectory() );
            }
        }
        else
        {
            throw new ModelException( getMessage( "invalidModel", this.getModel() ) );
        }
    }

    private static String getMessage( final String key, final Object... args )
    {
        return MessageFormat.format( ResourceBundle.getBundle(
            GenerateResourcesTask.class.getName().replace( '.', '/' ) ).getString( key ), args );

    }

}
