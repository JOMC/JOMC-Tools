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
package org.jomc.tools;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import org.jomc.model.Dependencies;
import org.jomc.model.Dependency;
import org.jomc.model.Implementation;
import org.jomc.model.Implementations;
import org.jomc.model.Message;
import org.jomc.model.MessageReference;
import org.jomc.model.Messages;
import org.jomc.model.ModelException;
import org.jomc.model.Module;
import org.jomc.model.Modules;
import org.jomc.model.Properties;
import org.jomc.model.Property;
import org.jomc.model.PropertyReference;
import org.jomc.model.Specification;
import org.jomc.model.SpecificationReference;
import org.jomc.model.Specifications;
import org.jomc.model.Text;
import org.jomc.model.Texts;
import org.xml.sax.SAXException;

/**
 * Assembles modules.
 *
 * @author <a href="mailto:schulte2005@users.sourceforge.net">Christian Schulte</a>
 * @version $Id$
 */
public class ModuleAssembler extends JomcTool
{

    /** Relocation map of the instance. */
    private Map<String, String> relocationMap;

    /** Relocation exclusions of the instance. */
    private Set<String> relocationExclusions;

    /** Creates a new {@code ModuleAssembler} instance. */
    public ModuleAssembler()
    {
        super();
    }

    /**
     * Creates a new {@code ModuleAssembler} instance taking a {@code JomcTool} instance to initialize the instance with.
     *
     * @param tool The instance to initialize the new instance with,
     */
    public ModuleAssembler( final JomcTool tool )
    {
        super( tool );
    }

    /**
     * Gets a map of class name relocations.
     *
     * @return A map of class name relocations.
     */
    public Map<String, String> getRelocationMap()
    {
        if ( this.relocationMap == null )
        {
            this.relocationMap = new HashMap<String, String>();
        }

        return this.relocationMap;
    }

    /**
     * Gets a set of class name relocation exclusions.
     *
     * @return A set of class name relocation exclusions.
     */
    public Set<String> getRelocationExclusions()
    {
        if ( this.relocationExclusions == null )
        {
            this.relocationExclusions = new HashSet<String>();
        }

        return this.relocationExclusions;
    }

    /**
     * Assembles the modules of the instance by merging any documents from a given directory.
     *
     * @param moduleFile The file to write the assembled modules to.
     * @param moduleName The name of the merged module.
     * @param moduleVersion The version of the merged module.
     * @param moduleVendor The vendor of the merged module.
     * @param mergeDirectory The directory to scan for documents to merge.
     * @param classLoader The class loader to search for classes.
     *
     * @throws NullPointerException if {@code moduleFile}, {@code moduleName} or {@code classLoader} is {@code null}.
     * @throws IOException if assembling modules fails.
     * @throws ModelException if the assembled modules are invalid.
     */
    public void assembleModules( final File moduleFile, final String moduleName, final String moduleVersion,
                                 final String moduleVendor, final File mergeDirectory, final ClassLoader classLoader )
        throws IOException, ModelException
    {
        if ( moduleFile == null )
        {
            throw new NullPointerException( "moduleFile" );
        }
        if ( moduleName == null )
        {
            throw new NullPointerException( "moduleName" );
        }
        if ( classLoader == null )
        {
            throw new NullPointerException( "classLoader" );
        }

        try
        {
            Module mergedModule = new Module();
            final Modules modules = new Modules( this.getModules() );

            mergedModule.setName( moduleName );
            mergedModule.setVersion( moduleVersion );
            mergedModule.setVendor( moduleVendor );

            if ( mergeDirectory != null && mergeDirectory.exists() && mergeDirectory.isDirectory() )
            {
                final String[] mergeDocuments = mergeDirectory.list( new FilenameFilter()
                {

                    public boolean accept( final File dir, final String name )
                    {
                        return name.endsWith( ".xml" );
                    }

                } );

                if ( mergeDocuments != null )
                {
                    for ( String name : mergeDocuments )
                    {
                        final File document = new File( mergeDirectory, name );
                        if ( document.isFile() )
                        {
                            this.mergeDocument( modules, document );
                        }
                    }
                }
            }

            for ( Module module : modules.getModule() )
            {
                final Set<String> referencedMessages = new HashSet<String>();
                final Set<String> referencedProperties = new HashSet<String>();

                if ( module.getImplementations() != null )
                {
                    for ( Implementation i : module.getImplementations().getImplementation() )
                    {
                        if ( mergedModule.getImplementations() == null )
                        {
                            mergedModule.setImplementations( new Implementations() );
                        }

                        if ( i.getMessages() != null && !i.getMessages().getReference().isEmpty() )
                        {
                            for ( Iterator<MessageReference> it = i.getMessages().getReference().iterator();
                                  it.hasNext(); )
                            {
                                final String messageName = it.next().getName();
                                i.getMessages().getMessage().add( module.getMessages().getMessage( messageName ) );
                                referencedMessages.add( messageName );
                                it.remove();
                            }
                        }

                        if ( i.getProperties() != null && !i.getProperties().getReference().isEmpty() )
                        {
                            for ( Iterator<PropertyReference> it = i.getProperties().getReference().iterator(); it.
                                hasNext(); )
                            {
                                final String propertyName = it.next().getName();
                                i.getProperties().getProperty().add(
                                    module.getProperties().getProperty( propertyName ) );

                                referencedProperties.add( propertyName );
                                it.remove();
                            }
                        }

                        mergedModule.getImplementations().getImplementation().add( i );
                    }
                }

                if ( module.getSpecifications() != null )
                {
                    if ( mergedModule.getSpecifications() == null )
                    {
                        mergedModule.setSpecifications( new Specifications() );
                    }

                    for ( Specification s : module.getSpecifications().getSpecification() )
                    {
                        if ( s.getProperties() != null && !s.getProperties().getReference().isEmpty() )
                        {
                            for ( Iterator<PropertyReference> it = s.getProperties().getReference().iterator();
                                  it.hasNext(); )
                            {
                                final String propertyName = it.next().getName();
                                s.getProperties().getProperty().add(
                                    module.getProperties().getProperty( propertyName ) );

                                referencedProperties.add( propertyName );
                                it.remove();
                            }
                        }

                        mergedModule.getSpecifications().getSpecification().add( s );
                    }
                }

                for ( String messageName : referencedMessages )
                {
                    for ( Iterator<Message> it = module.getMessages().getMessage().iterator(); it.hasNext(); )
                    {
                        if ( messageName.equals( it.next().getName() ) )
                        {
                            it.remove();
                            break;
                        }
                    }
                }

                for ( String propertyName : referencedProperties )
                {
                    for ( Iterator<Property> it = module.getProperties().getProperty().iterator(); it.hasNext(); )
                    {
                        if ( propertyName.equals( it.next().getName() ) )
                        {
                            it.remove();
                            break;
                        }
                    }
                }

                if ( module.getProperties() != null && !module.getProperties().getProperty().isEmpty() )
                {
                    if ( mergedModule.getProperties() == null )
                    {
                        mergedModule.setProperties( new Properties() );
                    }

                    mergedModule.getProperties().getProperty().addAll( module.getProperties().getProperty() );
                }

                if ( module.getMessages() != null && !module.getMessages().getMessage().isEmpty() )
                {
                    if ( mergedModule.getMessages() == null )
                    {
                        mergedModule.setMessages( new Messages() );
                    }

                    mergedModule.getMessages().getMessage().addAll( module.getMessages().getMessage() );
                }
            }

            if ( !this.getRelocationMap().isEmpty() )
            {
                mergedModule = this.relocate( mergedModule );
            }

            if ( !moduleFile.getParentFile().exists() )
            {
                moduleFile.getParentFile().mkdirs();
            }

            if ( moduleFile.exists() && !moduleFile.delete() )
            {
                this.log( Level.WARNING, this.getMessage( "couldNotDelete", new Object[]
                    {
                        moduleFile.getCanonicalPath()
                    } ), null );

            }

            final JAXBElement<Module> moduleElement =
                this.getModelManager().getObjectFactory().createModule( mergedModule );

            this.getModelManager().validateModelObject( moduleElement );

            this.log( Level.INFO, this.getMessage( "writing", new Object[]
                {
                    moduleFile.getCanonicalPath()
                } ), null );

            this.getModelManager().getMarshaller( false, true ).marshal( moduleElement, moduleFile );
        }
        catch ( SAXException e )
        {
            throw (IOException) new IOException( e.getMessage() ).initCause( e );
        }
        catch ( JAXBException e )
        {
            throw (IOException) new IOException( e.getMessage() ).initCause( e );
        }
    }

    /**
     * Merges the modules of the instance with a given document.
     *
     * @param modules The modules to merge.
     * @param document The document to merge.
     *
     * @throws NullPointerException if {@code document} is {@code null}.
     * @throws IOException if merging {@code document} fails.
     */
    public void mergeDocument( final Modules modules, final File document ) throws IOException
    {
        this.log( Level.INFO, this.getMessage( "merging", new Object[]
            {
                document.getCanonicalPath()
            } ), null );

        try
        {
            Object content = this.getModelManager().getUnmarshaller( false ).unmarshal( document );
            if ( content instanceof JAXBElement )
            {
                content = ( (JAXBElement) content ).getValue();
            }

            if ( content instanceof Specifications )
            {
                for ( Specification s : ( (Specifications) content ).getSpecification() )
                {
                    this.mergeSpecification( modules, s );
                }
            }
            else if ( content instanceof Specification )
            {
                this.mergeSpecification( modules, (Specification) content );
            }
            else if ( content instanceof Implementations )
            {
                for ( Implementation i : ( (Implementations) content ).getImplementation() )
                {
                    this.mergeImplementation( modules, i );
                }
            }
            else if ( content instanceof Implementation )
            {
                this.mergeImplementation( modules, (Implementation) content );
            }
        }
        catch ( SAXException e )
        {
            throw (IOException) new IOException( e.getMessage() ).initCause( e );
        }
        catch ( JAXBException e )
        {
            throw (IOException) new IOException( e.getMessage() ).initCause( e );
        }
    }

    /**
     * Merges the modules of the instance with a given specification.
     *
     * @param modules The modules to merge.
     * @param specification The specification to merge.
     *
     * @throws NullPointerException if {@code modules} or {@code specification} is {@code null}.
     * @throws Exception if merging {@code specification} fails.
     */
    public void mergeSpecification( final Modules modules, final Specification specification )
    {
        if ( modules == null )
        {
            throw new NullPointerException( "modules" );
        }
        if ( specification == null )
        {
            throw new NullPointerException( "specification" );
        }

        if ( specification.getProperties() != null )
        {
            final Specification s = modules.getSpecification( specification.getIdentifier() );

            if ( s != null )
            {
                if ( s.getProperties() == null )
                {
                    s.setProperties( specification.getProperties() );
                }
                else
                {
                    this.mergeProperties( specification.getProperties(), s.getProperties() );
                }
            }
            else
            {
                this.log( Level.WARNING, this.getMessage( "missingSpecification", new Object[]
                    {
                        specification.getIdentifier()
                    } ), null );

            }
        }
    }

    /**
     * Merges the modules of the instance with a given implementation.
     *
     * @param modules The modules to merge.
     * @param implementation The implementation to merge.
     *
     * @throws NullPointerException if {@code modules} or {@code implementation} is {@code null}.
     * @throws Exception if merging {@code implementation} fails.
     */
    public void mergeImplementation( final Modules modules, final Implementation implementation )
    {
        if ( modules == null )
        {
            throw new NullPointerException( "modules" );
        }
        if ( implementation == null )
        {
            throw new NullPointerException( "implementation" );
        }

        final Implementation i = modules.getImplementation( implementation.getIdentifier() );

        if ( i != null )
        {
            if ( implementation.getProperties() != null )
            {
                if ( i.getProperties() == null )
                {
                    i.setProperties( implementation.getProperties() );
                }
                else
                {
                    this.mergeProperties( implementation.getProperties(), i.getProperties() );
                }
            }

            if ( implementation.getDependencies() != null )
            {
                if ( i.getDependencies() == null )
                {
                    i.setDependencies( implementation.getDependencies() );
                }
                else
                {
                    this.mergeDependencies( implementation.getDependencies(), i.getDependencies() );
                }
            }

            if ( implementation.getMessages() != null )
            {
                if ( i.getMessages() == null )
                {
                    i.setMessages( implementation.getMessages() );
                }
                else
                {
                    this.mergeMessages( implementation.getMessages(), i.getMessages() );
                }
            }
        }
        else
        {
            this.log( Level.WARNING, this.getMessage( "missingImplementation", new Object[]
                {
                    implementation.getIdentifier()
                } ), null );

        }
    }

    private void mergeProperties( final Properties source, final Properties target )
    {
        for ( Property p : source.getProperty() )
        {
            final Property tp = target.getProperty( p.getName() );
            if ( tp != null )
            {
                tp.setValue( p.getValue() );
                tp.setAny( p.getAny() );
            }
            else
            {
                target.getProperty().add( p );
            }
        }
    }

    private void mergeDependencies( final Dependencies source, final Dependencies target )
    {
        for ( Dependency d : source.getDependency() )
        {
            final Dependency td = target.getDependency( d.getName() );
            if ( td != null )
            {
                if ( d.isBound() )
                {
                    td.setBound( d.isBound() );
                }
                if ( d.getImplementationName() != null && td.getImplementationName() != null )
                {
                    td.setImplementationName( d.getImplementationName() );
                }
            }
            else
            {
                target.getDependency().add( d );
            }
        }
    }

    private void mergeMessages( final Messages source, final Messages target )
    {
        for ( Message m : source.getMessage() )
        {
            final Message tm = target.getMessage( m.getName() );
            if ( tm != null )
            {
                this.mergeTexts( m.getTemplate(), tm.getTemplate() );
            }
            else
            {
                target.getMessage().add( m );
            }
        }
    }

    private void mergeTexts( final Texts source, final Texts target )
    {
        for ( Text t : source.getText() )
        {
            final Text tt = target.getText( t.getLanguage() );
            if ( tt != null )
            {
                tt.setValue( t.getValue() );
            }
            else
            {
                target.getText().add( t );
            }
        }
    }

    private Module relocate( final Module module )
    {
        final Module m = new Module( module );

        if ( m.getImplementations() != null )
        {
            for ( Implementation i : m.getImplementations().getImplementation() )
            {
                this.relocate( i );
            }
        }
        if ( m.getSpecifications() != null )
        {
            for ( Specification s : m.getSpecifications().getSpecification() )
            {
                this.relocate( s );
            }
        }

        return m;
    }

    private void relocate( final Implementation implementation )
    {
        if ( implementation.getClazz() != null )
        {
            final Map.Entry<String, String> relocation = this.getRelocation( implementation.getClazz() );
            if ( relocation != null )
            {
                implementation.setClazz( implementation.getClazz().replace(
                    relocation.getKey(), relocation.getValue() ) );

            }
        }
        if ( implementation.getIdentifier() != null )
        {
            final Map.Entry<String, String> relocation = this.getRelocation( implementation.getIdentifier() );
            if ( relocation != null )
            {
                implementation.setIdentifier( implementation.getIdentifier().replace(
                    relocation.getKey(), relocation.getValue() ) );

            }
        }
        if ( implementation.getParent() != null )
        {
            final Map.Entry<String, String> relocation = this.getRelocation( implementation.getParent() );
            if ( relocation != null )
            {
                implementation.setParent( implementation.getParent().replace(
                    relocation.getKey(), relocation.getValue() ) );

            }
        }
        if ( implementation.getSpecifications() != null )
        {
            for ( SpecificationReference ref : implementation.getSpecifications().getReference() )
            {
                this.relocate( ref );
            }
        }
        if ( implementation.getDependencies() != null )
        {
            for ( Dependency d : implementation.getDependencies().getDependency() )
            {
                this.relocate( d );
            }
        }
    }

    private void relocate( final Specification specification )
    {
        if ( specification.getIdentifier() != null )
        {
            final Map.Entry<String, String> relocation = this.getRelocation( specification.getIdentifier() );
            if ( relocation != null )
            {
                specification.setIdentifier( specification.getIdentifier().replace(
                    relocation.getKey(), relocation.getValue() ) );

            }
        }
    }

    private void relocate( final SpecificationReference specification )
    {
        if ( specification.getIdentifier() != null )
        {
            final Map.Entry<String, String> relocation = this.getRelocation( specification.getIdentifier() );
            if ( relocation != null )
            {
                specification.setIdentifier( specification.getIdentifier().replace(
                    relocation.getKey(), relocation.getValue() ) );

            }
        }
    }

    private Map.Entry<String, String> getRelocation( final String source )
    {
        Map.Entry<String, String> relocation = null;

        for ( Map.Entry<String, String> e : this.getRelocationMap().entrySet() )
        {
            if ( source.startsWith( e.getKey() ) )
            {
                if ( relocation == null || relocation.getKey().length() < e.getKey().length() )
                {
                    relocation = e;
                }
            }
        }

        if ( relocation != null )
        {
            for ( String exlusion : this.getRelocationExclusions() )
            {
                if ( source.startsWith( exlusion ) )
                {
                    relocation = null;
                    break;
                }
            }
        }

        return relocation;
    }

    private String getMessage( final String key, final Object args )
    {
        final ResourceBundle b = ResourceBundle.getBundle( ModuleAssembler.class.getName().replace( '.', '/' ) );
        final MessageFormat f = new MessageFormat( b.getString( key ) );
        return f.format( args );
    }

}
