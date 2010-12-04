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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStreamWriter;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.util.JAXBResult;
import javax.xml.bind.util.JAXBSource;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.validation.Schema;
import org.apache.tools.ant.BuildException;
import org.jomc.ant.types.NameType;
import org.jomc.model.Module;
import org.jomc.model.Modules;
import org.jomc.model.ObjectFactory;
import org.jomc.model.modlet.ModelHelper;
import org.jomc.modlet.Model;
import org.jomc.modlet.ModelContext;
import org.jomc.modlet.ModelException;

/**
 * Task for merging module resources.
 *
 * @author <a href="mailto:schulte2005@users.sourceforge.net">Christian Schulte</a>
 * @version $Id$
 */
public final class MergeModulesTask extends JomcModelTask
{

    /** The encoding of the module resource. */
    private String moduleEncoding;

    /** File to write the merged module to. */
    private File moduleFile;

    /** The name of the merged module. */
    private String moduleName;

    /** The version of the merged module. */
    private String moduleVersion;

    /** The vendor of the merged module. */
    private String moduleVendor;

    /** Included modules. */
    private Set<NameType> moduleIncludes;

    /** Excluded modules. */
    private Set<NameType> moduleExcludes;

    /** Model object style sheet to apply. */
    private String modelObjectStylesheet;

    /** Creates a new {@code MergeModulesTask} instance. */
    public MergeModulesTask()
    {
        super();
    }

    /**
     * Gets the file to write the merged module to.
     *
     * @return The file to write the merged module to or {@code null}.
     *
     * @see #setModuleFile(java.io.File)
     */
    public File getModuleFile()
    {
        return this.moduleFile;
    }

    /**
     * Sets the file to write the merged module to.
     *
     * @param value The new file to write the merged module to or {@code null}.
     *
     * @see #getModuleFile()
     */
    public void setModuleFile( final File value )
    {
        this.moduleFile = value;
    }

    /**
     * Gets the encoding of the module resource.
     *
     * @return The encoding of the module resource.
     *
     * @see #setModuleEncoding(java.lang.String)
     */
    public String getModuleEncoding()
    {
        if ( this.moduleEncoding == null )
        {
            this.moduleEncoding = new OutputStreamWriter( new ByteArrayOutputStream() ).getEncoding();
        }

        return this.moduleEncoding;
    }

    /**
     * Sets the encoding of the module resource.
     *
     * @param value The new encoding of the module resource or {@code null}.
     *
     * @see #getModuleEncoding()
     */
    public void setModuleEncoding( final String value )
    {
        this.moduleEncoding = value;
    }

    /**
     * Gets the name of the merged module.
     *
     * @return The name of the merged module or {@code null}.
     *
     * @see #setModuleName(java.lang.String)
     */
    public String getModuleName()
    {
        return this.moduleName;
    }

    /**
     * Sets the name of the merged module.
     *
     * @param value The new name of the merged module or {@code null}.
     *
     * @see #getModuleName()
     */
    public void setModuleName( final String value )
    {
        this.moduleName = value;
    }

    /**
     * Gets the version of the merged module.
     *
     * @return The version of the merged module or {@code null}.
     *
     * @see #setModuleVersion(java.lang.String)
     */
    public String getModuleVersion()
    {
        return this.moduleVersion;
    }

    /**
     * Sets the version of the merged module.
     *
     * @param value The new version of the merged module or {@code null}.
     *
     * @see #getModuleVersion()
     */
    public void setModuleVersion( final String value )
    {
        this.moduleVersion = value;
    }

    /**
     * Gets the vendor of the merged module.
     *
     * @return The vendor of the merge module or {@code null}.
     *
     * @see #setModuleVendor(java.lang.String)
     */
    public String getModuleVendor()
    {
        return this.moduleVendor;
    }

    /**
     * Sets the vendor of the merged module.
     *
     * @param value The new vendor of the merged module or {@code null}.
     *
     * @see #getModuleVendor()
     */
    public void setModuleVendor( final String value )
    {
        this.moduleVendor = value;
    }

    /**
     * Gets a set of module names to include.
     * <p>This accessor method returns a reference to the live set, not a snapshot. Therefore any modification you make
     * to the returned set will be present inside the object. This is why there is no {@code set} method for the
     * module includes property.</p>
     *
     * @return A set of module names to include.
     *
     * @see #createModuleInclude()
     */
    public Set<NameType> getModuleIncludes()
    {
        if ( this.moduleIncludes == null )
        {
            this.moduleIncludes = new HashSet<NameType>();
        }

        return this.moduleIncludes;
    }

    /**
     * Creates a new {@code moduleInclude} element instance.
     *
     * @return A new {@code moduleInclude} element instance.
     *
     * @see #getModuleIncludes()
     */
    public NameType createModuleInclude()
    {
        final NameType moduleInclude = new NameType();
        this.getModuleIncludes().add( moduleInclude );
        return moduleInclude;
    }

    /**
     * Gets a set of module names to exclude.
     * <p>This accessor method returns a reference to the live set, not a snapshot. Therefore any modification you make
     * to the returned set will be present inside the object. This is why there is no {@code set} method for the
     * module excludes property.</p>
     *
     * @return A set of module names to exclude.
     *
     * @see #createModuleExclude()
     */
    public Set<NameType> getModuleExcludes()
    {
        if ( this.moduleExcludes == null )
        {
            this.moduleExcludes = new HashSet<NameType>();
        }

        return this.moduleExcludes;
    }

    /**
     * Creates a new {@code moduleExclude} element instance.
     *
     * @return A new {@code moduleExclude} element instance.
     *
     * @see #getModuleExcludes()
     */
    public NameType createModuleExclude()
    {
        final NameType moduleExclude = new NameType();
        this.getModuleExcludes().add( moduleExclude );
        return moduleExclude;
    }

    /**
     * Gets the location of a style sheet to transform the merged module with.
     *
     * @return The location of a style sheet to transform the merged module with or {@code null}.
     */
    public String getModelObjectStylesheet()
    {
        return this.modelObjectStylesheet;
    }

    /**
     * Sets the location of a style sheet to transform the merged module with.
     *
     * @param value The new location of a style sheet to transform the merged module with or {@code null}.
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

        this.assertNotNull( "moduleFile", this.getModuleFile() );
        this.assertNotNull( "moduleName", this.getModuleName() );
        this.assertNamesNotNull( this.getModuleExcludes() );
        this.assertNamesNotNull( this.getModuleIncludes() );
    }

    /**
     * Merges module resources.
     *
     * @throws BuildException if merging module resources fails.
     */
    @Override
    public void executeTask() throws BuildException
    {
        try
        {
            this.log( getMessage( "mergingModules", this.getModel() ) );

            final ProjectClassLoader classLoader = this.newProjectClassLoader();
            final ModelContext context = this.newModelContext( classLoader );
            final Model model = context.findModel( this.getModel() );
            final Modules modules =
                ModelHelper.getModules( model ) == null ? new Modules() : ModelHelper.getModules( model );

            for ( final Iterator<Module> it = modules.getModule().iterator(); it.hasNext(); )
            {
                final Module module = it.next();

                if ( !this.isModuleIncluded( module ) || this.isModuleExcluded( module ) )
                {
                    it.remove();
                    this.log( getMessage( "excludingModule", module.getName() ) );
                }
                else
                {
                    this.log( getMessage( "includingModule", module.getName() ) );
                }
            }

            Module mergedModule = modules.getMergedModule( this.getModuleName() );
            mergedModule.setVendor( this.getModuleVendor() );
            mergedModule.setVersion( this.getModuleVersion() );

            final Marshaller marshaller = context.createMarshaller( this.getModel() );
            final Unmarshaller unmarshaller = context.createUnmarshaller( this.getModel() );
            final Schema schema = context.createSchema( this.getModel() );
            marshaller.setSchema( schema );
            unmarshaller.setSchema( schema );

            if ( this.getModelObjectStylesheet() != null )
            {
                final Transformer transformer = this.newTransformer( this.getModelObjectStylesheet(),
                                                                     context.getClassLoader() );

                for ( Map.Entry<Object, Object> e : System.getProperties().entrySet() )
                {
                    transformer.setParameter( e.getKey().toString(), e.getValue() );
                }

                final JAXBSource source =
                    new JAXBSource( marshaller, new ObjectFactory().createModule( mergedModule ) );

                final JAXBResult result = new JAXBResult( unmarshaller );

                transformer.transform( source, result );

                if ( result.getResult() instanceof JAXBElement<?>
                     && ( (JAXBElement<?>) result.getResult() ).getValue() instanceof Module )
                {
                    mergedModule = (Module) ( (JAXBElement<?>) result.getResult() ).getValue();
                }
                else
                {
                    throw new BuildException( getMessage( "illegalTransformationResult",
                                                          this.getModelObjectStylesheet() ), this.getLocation() );

                }
            }

            this.log( getMessage( "writing", this.getModuleFile().getAbsolutePath(), this.getModuleEncoding() ) );
            marshaller.setProperty( Marshaller.JAXB_ENCODING, this.getModuleEncoding() );
            marshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
            marshaller.marshal( new ObjectFactory().createModule( mergedModule ), this.getModuleFile() );
        }
        catch ( final URISyntaxException e )
        {
            throw new BuildException( getMessage( e ), e, this.getLocation() );
        }
        catch ( final JAXBException e )
        {
            String message = getMessage( e );
            if ( message == null )
            {
                message = getMessage( e.getLinkedException() );
            }

            throw new BuildException( message, e, this.getLocation() );
        }
        catch ( final TransformerConfigurationException e )
        {
            throw new BuildException( getMessage( e ), e, this.getLocation() );
        }
        catch ( final TransformerException e )
        {
            throw new BuildException( getMessage( e ), e, this.getLocation() );
        }
        catch ( final ModelException e )
        {
            throw new BuildException( getMessage( e ), e, this.getLocation() );
        }
    }

    /**
     * Tests a given module to be included based on property {@code moduleIncludes}.
     *
     * @param module The module to test.
     *
     * @return {@code true} if {@code module} is included based on property {@code moduleIncludes}.
     *
     * @throws NullPointerException if {@code module} is {@code null}.
     *
     * @see #getModuleIncludes()
     */
    public boolean isModuleIncluded( final Module module )
    {
        if ( module == null )
        {
            throw new NullPointerException( "module" );
        }

        for ( NameType include : this.getModuleIncludes() )
        {
            if ( include.getName().equals( module.getName() ) )
            {
                return true;
            }
        }

        return this.getModuleIncludes().isEmpty() ? true : false;
    }

    /**
     * Test a given module to be excluded based on property {@code moduleExcludes}.
     *
     * @param module The module to test.
     *
     * @return {@code true} if {@code module} is excluded based on property {@code moduleExcludes}.
     *
     * @throws NullPointerException if {@code module} is {@code null}.
     *
     * @see #getModuleExcludes()
     */
    public boolean isModuleExcluded( final Module module )
    {
        if ( module == null )
        {
            throw new NullPointerException( "module" );
        }

        for ( NameType exclude : this.getModuleExcludes() )
        {
            if ( exclude.getName().equals( module.getName() ) )
            {
                return true;
            }
        }

        return false;
    }

    /** {@inheritDoc} */
    @Override
    public MergeModulesTask clone()
    {
        final MergeModulesTask clone = (MergeModulesTask) super.clone();
        clone.moduleFile = this.moduleFile != null ? new File( this.moduleFile.getAbsolutePath() ) : null;

        if ( this.moduleExcludes != null )
        {
            final HashSet<NameType> set = new HashSet<NameType>( this.moduleExcludes.size() );
            for ( NameType t : this.moduleExcludes )
            {
                set.add( t.clone() );
            }

            clone.moduleExcludes = set;
        }

        if ( this.moduleIncludes != null )
        {
            final HashSet<NameType> set = new HashSet<NameType>( this.moduleIncludes.size() );
            for ( NameType t : this.moduleIncludes )
            {
                set.add( t.clone() );
            }

            clone.moduleIncludes = set;
        }

        return clone;
    }

    private static String getMessage( final String key, final Object... args )
    {
        return MessageFormat.format( ResourceBundle.getBundle(
            MergeModulesTask.class.getName().replace( '.', '/' ) ).getString( key ), args );

    }

    private static String getMessage( final Throwable t )
    {
        return t != null ? t.getMessage() != null ? t.getMessage() : getMessage( t.getCause() ) : null;
    }

}
