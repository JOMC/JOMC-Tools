/*
 *  JOMC Tools
 *  Copyright (c) 2005 Christian Schulte <cs@schulte.it>
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 */
package org.jomc.tools;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Iterator;
import javax.xml.bind.JAXBElement;
import org.jomc.model.DefaultModelManager;
import org.jomc.model.Dependencies;
import org.jomc.model.Dependency;
import org.jomc.model.Implementation;
import org.jomc.model.Implementations;
import org.jomc.model.Message;
import org.jomc.model.Messages;
import org.jomc.model.Module;
import org.jomc.model.Modules;
import org.jomc.model.Properties;
import org.jomc.model.Property;
import org.jomc.model.Specification;
import org.jomc.model.Specifications;
import org.jomc.model.Text;
import org.jomc.model.Texts;

/**
 * Assembles modules.
 *
 * @author <a href="mailto:cs@schulte.it">Christian Schulte</a>
 * @version $Id$
 */
public class ModuleAssembler extends JomcTool
{

    /** Creates a new {@code ModuleAssembler} instance. */
    public ModuleAssembler()
    {
        super();
    }

    /**
     * Creates a new {@code ModuleAssembler} instance taking a classloader.
     *
     * @param classLoader The classlaoder of the instance.
     */
    public ModuleAssembler( final ClassLoader classLoader )
    {
        super( classLoader );
    }

    /**
     * Assembles the modules of the instance by merging any documents from a given directory.
     *
     * @param modulesFile The file to write the assembled modules to.
     * @param mergeDirectory The directory to scan for documents to merge.
     * @param includeClasspathModule {@code true} to preserve any entities resolved from the tools classpath;
     * {@code false} to remove any entities resolved from the tools classpath.
     *
     * @throws NullPointerException if {@code modulesFile} is {@code null}.
     * @throws Exception if assembling modules fails.
     */
    public void assembleModules( final File modulesFile, final File mergeDirectory,
                                 final boolean includeClasspathModule ) throws Exception
    {
        if ( modulesFile == null )
        {
            throw new NullPointerException( "modulesFile" );
        }

        final Modules modules = this.getModelManager().getModules();

        if ( !includeClasspathModule )
        {
            for ( Iterator<Module> it = modules.getModule().iterator(); it.hasNext(); )
            {
                if ( DefaultModelManager.CLASSPATH_MODULE_NAME.equals( it.next().getName() ) )
                {
                    it.remove();
                    break;
                }
            }
        }

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
                        this.mergeDocument( document );
                    }
                }
            }
        }

        if ( !modulesFile.getParentFile().exists() )
        {
            modulesFile.getParentFile().mkdirs();
        }

        this.getModelResolver().getMarshaller( false, true ).
            marshal( this.getModelResolver().getObjectFactory().createModules( modules ), modulesFile );

    }

    /**
     * Merges the modules of the instance with a given document.
     *
     * @param document The document to merge.
     *
     * @throws NullPointerException if {@code document} is {@code null}.
     * @throws Exception if merging {@code document} fails.
     */
    public void mergeDocument( final File document ) throws Exception
    {
        Object content = this.getModelResolver().getUnmarshaller( false ).unmarshal( document );
        if ( content instanceof JAXBElement )
        {
            content = ( (JAXBElement) content ).getValue();
        }

        if ( content instanceof Specifications )
        {
            for ( Specification s : ( (Specifications) content ).getSpecification() )
            {
                this.mergeSpecification( s );
            }
        }
        else if ( content instanceof Specification )
        {
            this.mergeSpecification( (Specification) content );
        }
        else if ( content instanceof Implementations )
        {
            for ( Implementation i : ( (Implementations) content ).getImplementation() )
            {
                this.mergeImplementation( i );
            }
        }
        else if ( content instanceof Implementation )
        {
            this.mergeImplementation( (Implementation) content );
        }
    }

    /**
     * Merges the modules of the instance with a given specification.
     *
     * @param specification The specification to merge.
     *
     * @throws NullPointerException if {@code specification} is {@code null}.
     * @throws Exception if merging {@code specification} fails.
     */
    public void mergeSpecification( final Specification specification ) throws Exception
    {
        if ( specification == null )
        {
            throw new NullPointerException( "specification" );
        }

        if ( specification.getProperties() != null )
        {
            final Specification s = this.getModelManager().getSpecification( specification.getIdentifier() );

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
        }
    }

    /**
     * Merges the modules of the instance with a given implementation.
     *
     * @param implementation The implementation to merge.
     *
     * @throws NullPointerException if {@code implementation} is {@code null}.
     * @throws Exception if merging {@code implementation} fails.
     */
    public void mergeImplementation( final Implementation implementation ) throws Exception
    {
        if ( implementation == null )
        {
            throw new NullPointerException( "implementation" );
        }

        final Implementation i = this.getModelManager().getImplementation( implementation.getIdentifier() );

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

}
