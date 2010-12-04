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
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.util.JAXBSource;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import org.apache.tools.ant.BuildException;
import org.jomc.model.Implementation;
import org.jomc.model.Module;
import org.jomc.model.Specification;
import org.jomc.modlet.Model;
import org.jomc.modlet.ModelContext;
import org.jomc.modlet.ModelException;
import org.jomc.modlet.ModelValidationReport;
import org.jomc.modlet.ObjectFactory;
import org.jomc.tools.ClassFileProcessor;

/**
 * Task for committing model objects to class files.
 *
 * @author <a href="mailto:schulte2005@users.sourceforge.net">Christian Schulte</a>
 * @version $Id$
 */
public final class CommitClassesTask extends ClassFileProcessorTask
{

    /** The directory holding the class files to commit model objects to. */
    private File classesDirectory;

    /** Style sheet to use for transforming model objects. */
    private String modelObjectStylesheet;

    /** Creates a new {@code CommitClassesTask} instance. */
    public CommitClassesTask()
    {
        super();
    }

    /**
     * Gets the directory holding the class files to commit model objects to.
     *
     * @return The directory holding the class files to commit model objects to or {@code null}.
     *
     * @see #setClassesDirectory(java.io.File)
     */
    public File getClassesDirectory()
    {
        return this.classesDirectory;
    }

    /**
     * Sets the directory holding the class files to commit model objects to.
     *
     * @param value The new directory holding the class files to commit model objects to or {@code null}.
     *
     * @see #getClassesDirectory()
     */
    public void setClassesDirectory( final File value )
    {
        this.classesDirectory = value;
    }

    /**
     * Gets the location of a style sheet used for transforming model objects.
     *
     * @return The style sheet used for transforming model objects or {@code null}.
     *
     * @see #setModelObjectStylesheet(java.lang.String)
     */
    public String getModelObjectStylesheet()
    {
        return this.modelObjectStylesheet;
    }

    /**
     * Sets the location of a style sheet to use for transforming model objects.
     *
     * @param value The new location of a style sheet to use for transforming model objects or {@code null}.
     *
     * @see #getModelObjectStylesheet()
     */
    public void setModelObjectStylesheet( final String value )
    {
        this.modelObjectStylesheet = value;
    }

    /** {@inheritDoc} */
    @Override
    public void preExecuteTask() throws BuildException
    {
        super.preExecuteTask();

        this.assertNotNull( "classesDirectory", this.getClassesDirectory() );
    }

    /**
     * Commits model objects to class files.
     *
     * @throws BuildException if committing model objects fails.
     */
    @Override
    public void processClassFiles() throws BuildException
    {
        try
        {
            this.log( getMessage( "committingModelObjects", this.getModel() ) );

            final ProjectClassLoader classLoader = this.newProjectClassLoader();
            final ModelContext context = this.newModelContext( classLoader );
            final ClassFileProcessor tool = this.newClassFileProcessor();
            final JAXBContext jaxbContext = context.createContext( this.getModel() );
            final Model model = this.getModel( context );
            final Source source = new JAXBSource( jaxbContext, new ObjectFactory().createModel( model ) );
            final ModelValidationReport validationReport = context.validateModel( this.getModel(), source );
            final List<Transformer> transformers = new ArrayList<Transformer>( 1 );

            if ( this.getModelObjectStylesheet() != null )
            {
                transformers.add( this.newTransformer( this.getModelObjectStylesheet(), context.getClassLoader() ) );
            }

            this.logValidationReport( context, validationReport );
            tool.setModel( model );

            if ( validationReport.isModelValid() )
            {
                final Specification s = this.getSpecification( model );
                final Implementation i = this.getImplementation( model );
                final Module m = this.getModule( model );

                if ( s != null )
                {
                    tool.commitModelObjects( s, context, this.getClassesDirectory() );

                    if ( !transformers.isEmpty() )
                    {
                        tool.transformModelObjects( s, context, this.getClassesDirectory(), transformers );
                    }
                }


                if ( i != null )
                {
                    tool.commitModelObjects( i, context, this.getClassesDirectory() );

                    if ( !transformers.isEmpty() )
                    {
                        tool.transformModelObjects( i, context, this.getClassesDirectory(), transformers );
                    }
                }

                if ( m != null )
                {
                    tool.commitModelObjects( m, context, this.getClassesDirectory() );

                    if ( !transformers.isEmpty() )
                    {
                        tool.transformModelObjects( m, context, this.getClassesDirectory(), transformers );
                    }
                }

                if ( this.isModulesProcessingRequested() )
                {
                    tool.commitModelObjects( context, this.getClassesDirectory() );

                    if ( !transformers.isEmpty() )
                    {
                        tool.transformModelObjects( context, this.getClassesDirectory(), transformers );
                    }
                }
            }
            else
            {
                throw new ModelException( getMessage( "invalidModel", this.getModel() ) );
            }
        }
        catch ( final IOException e )
        {
            throw new ClassProcessingException( getMessage( e ), e, this.getLocation() );
        }
        catch ( final URISyntaxException e )
        {
            throw new ClassProcessingException( getMessage( e ), e, this.getLocation() );
        }
        catch ( final JAXBException e )
        {
            throw new ClassProcessingException( getMessage( e ), e, this.getLocation() );
        }
        catch ( final TransformerConfigurationException e )
        {
            throw new ClassProcessingException( getMessage( e ), e, this.getLocation() );
        }
        catch ( final ModelException e )
        {
            throw new ClassProcessingException( getMessage( e ), e, this.getLocation() );
        }
    }

    /** {@inheritDoc} */
    @Override
    public CommitClassesTask clone()
    {
        final CommitClassesTask clone = (CommitClassesTask) super.clone();
        clone.classesDirectory =
            this.classesDirectory != null ? new File( this.classesDirectory.getAbsolutePath() ) : null;

        return clone;
    }

    private static String getMessage( final String key, final Object... args )
    {
        return MessageFormat.format( ResourceBundle.getBundle(
            CommitClassesTask.class.getName().replace( '.', '/' ) ).getString( key ), args );

    }

    private static String getMessage( final Throwable t )
    {
        return t != null ? t.getMessage() != null ? t.getMessage() : getMessage( t.getCause() ) : null;
    }

}
