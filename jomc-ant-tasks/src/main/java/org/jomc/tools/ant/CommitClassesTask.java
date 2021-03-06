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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
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
import org.jomc.tools.ant.types.TransformerResourceType;

/**
 * Task for committing model objects to class files.
 *
 * @author <a href="mailto:cs@schulte.it">Christian Schulte</a>
 * @version $JOMC$
 */
public final class CommitClassesTask extends ClassFileProcessorTask
{

    /**
     * The directory holding the class files to commit model objects to.
     */
    private File classesDirectory;

    /**
     * XSLT documents to use for transforming model objects.
     */
    private List<TransformerResourceType> modelObjectStylesheetResources;

    /**
     * Creates a new {@code CommitClassesTask} instance.
     */
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
     * Gets the XSLT documents to use for transforming model objects.
     * <p>
     * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make
     * to the returned list will be present inside the object. This is why there is no {@code set} method for the
     * model object stylesheet resources property.
     * </p>
     *
     * @return The XSLT documents to use for transforming model objects.
     *
     * @see #createModelObjectStylesheetResource()
     */
    public List<TransformerResourceType> getModelObjectStylesheetResources()
    {
        if ( this.modelObjectStylesheetResources == null )
        {
            this.modelObjectStylesheetResources = new LinkedList<TransformerResourceType>();
        }

        return this.modelObjectStylesheetResources;
    }

    /**
     * Creates a new {@code modelObjectStylesheetResource} element instance.
     *
     * @return A new {@code modelObjectStylesheetResource} element instance.
     *
     * @see #getModelObjectStylesheetResources()
     */
    public TransformerResourceType createModelObjectStylesheetResource()
    {
        final TransformerResourceType modelObjectStylesheetResource = new TransformerResourceType();
        this.getModelObjectStylesheetResources().add( modelObjectStylesheetResource );
        return modelObjectStylesheetResource;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void preExecuteTask() throws BuildException
    {
        super.preExecuteTask();

        this.assertNotNull( "classesDirectory", this.getClassesDirectory() );
        this.assertLocationsNotNull( this.getModelObjectStylesheetResources() );
    }

    /**
     * Commits model objects to class files.
     *
     * @throws BuildException if committing model objects fails.
     */
    @Override
    public void processClassFiles() throws BuildException
    {
        this.log( Messages.getMessage( "committingModelObjects", this.getModel() ) );

        try ( final ProjectClassLoader classLoader = this.newProjectClassLoader() )
        {
            final ModelContext context = this.newModelContext( classLoader );
            final ClassFileProcessor tool = this.newClassFileProcessor();
            final JAXBContext jaxbContext = context.createContext( this.getModel() );
            final Model model = this.getModel( context );
            final Source source = new JAXBSource( jaxbContext, new ObjectFactory().createModel( model ) );
            final ModelValidationReport validationReport = context.validateModel( this.getModel(), source );

            this.logValidationReport( context, validationReport );
            tool.setModel( model );

            final List<Transformer> transformers =
                new ArrayList<Transformer>( this.getModelObjectStylesheetResources().size() );

            for ( int i = 0, s0 = this.getModelObjectStylesheetResources().size(); i < s0; i++ )
            {
                final Transformer transformer =
                    this.getTransformer( this.getModelObjectStylesheetResources().get( i ) );

                if ( transformer != null )
                {
                    transformers.add( transformer );
                }
            }

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
                throw new ModelException( Messages.getMessage( "invalidModel", this.getModel() ) );
            }
        }
        catch ( final IOException | JAXBException | TransformerConfigurationException | ModelException e )
        {
            throw new ClassProcessingException( Messages.getMessage( e ), e, this.getLocation() );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CommitClassesTask clone()
    {
        final CommitClassesTask clone = (CommitClassesTask) super.clone();
        clone.classesDirectory =
            this.classesDirectory != null ? new File( this.classesDirectory.getAbsolutePath() ) : null;

        if ( this.modelObjectStylesheetResources != null )
        {
            clone.modelObjectStylesheetResources =
                new ArrayList<TransformerResourceType>( this.modelObjectStylesheetResources.size() );

            for ( final TransformerResourceType e : this.modelObjectStylesheetResources )
            {
                clone.modelObjectStylesheetResources.add( e.clone() );
            }
        }

        return clone;
    }

}
