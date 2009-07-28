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
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.logging.Level;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import org.jomc.model.DefaultModelManager;
import org.jomc.model.Dependencies;
import org.jomc.model.Dependency;
import org.jomc.model.Implementation;
import org.jomc.model.Implementations;
import org.jomc.model.Message;
import org.jomc.model.Messages;
import org.jomc.model.ModelException;
import org.jomc.model.Module;
import org.jomc.model.Modules;
import org.jomc.model.Properties;
import org.jomc.model.Property;
import org.jomc.model.Specification;
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
     * Assembles the modules of the instance by merging any documents from a given directory.
     *
     * @param modulesFile The file to write the assembled modules to.
     * @param mergeDirectory The directory to scan for documents to merge.
     * @param classLoader The classloader to search for classes.
     * @param includeClasspathModule {@code true} to preserve any entities resolved from the tool's classpath;
     * {@code false} to remove any entities resolved from the tool's classpath.
     *
     * @throws NullPointerException if {@code modulesFile} is {@code null}.
     * @throws IOException if assembling modules fails.
     * @throws ModelException if the assembled modules are invalid.
     */
    public void assembleModules( final File modulesFile, final File mergeDirectory,
                                 final ClassLoader classLoader, final boolean includeClasspathModule )
        throws IOException, ModelException
    {
        if ( modulesFile == null )
        {
            throw new NullPointerException( "modulesFile" );
        }

        try
        {
            final Modules m = new Modules( this.getModules() );

            if ( !includeClasspathModule )
            {
                for ( Iterator<Module> it = m.getModule().iterator(); it.hasNext(); )
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
                            this.mergeDocument( m, document );
                        }
                    }
                }
            }

            if ( !modulesFile.getParentFile().exists() )
            {
                modulesFile.getParentFile().mkdirs();
            }

            if ( modulesFile.exists() && !modulesFile.delete() )
            {
                this.log( Level.WARNING, this.getMessage( "couldNotDelete", new Object[]
                    {
                        modulesFile.getCanonicalPath()
                    } ), null );

            }

            this.getModelManager().validateModules( m );

            final JavaClasses javaClasses = new JavaClasses( this );
            javaClasses.validateModules( classLoader );

            this.log( Level.INFO, this.getMessage( "writing", new Object[]
                {
                    modulesFile.getCanonicalPath()
                } ), null );

            this.getModelManager().getMarshaller( false, true ).marshal(
                this.getModelManager().getObjectFactory().createModules( m ), modulesFile );

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

    private String getMessage( final String key, final Object args )
    {
        final ResourceBundle b = ResourceBundle.getBundle( ModuleAssembler.class.getName().replace( '.', '/' ) );
        final MessageFormat f = new MessageFormat( b.getString( key ) );
        return f.format( args );
    }

}
