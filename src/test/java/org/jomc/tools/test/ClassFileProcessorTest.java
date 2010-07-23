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
package org.jomc.tools.test;

import java.util.Properties;
import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;
import org.jomc.model.Dependency;
import org.jomc.model.Message;
import org.jomc.model.Property;
import org.jomc.model.Multiplicity;
import org.jomc.tools.SourceFileProcessor;
import org.jomc.tools.ResourceFileProcessor;
import org.jomc.modlet.ModelContext;
import org.jomc.model.modlet.ModelHelper;
import org.jomc.modlet.Model;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;
import org.apache.commons.io.FileUtils;
import org.jomc.model.Implementation;
import org.jomc.model.ModelObject;
import org.jomc.model.Module;
import org.jomc.model.Modules;
import org.jomc.model.Specification;
import org.jomc.modlet.ModelException;
import org.jomc.tools.ClassFileProcessor;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.fail;

/**
 * Test cases for class {@code org.jomc.tools.ClassFileProcessor}.
 *
 * @author <a href="mailto:schulte2005@users.sourceforge.net">Christian Schulte</a>
 * @version $Id$
 */
public class ClassFileProcessorTest extends JomcToolTest
{

    /** The test {@code Model} of the instance. */
    private Model testModel;

    /** The {@code JavaClasses} instance tests are performed with. */
    private ClassFileProcessor testTool;

    /** Serial number of the test classes directory. */
    private int testClassesId;

    /** Properties backing the instance. */
    private Properties testProperties;

    /**
     * Gets the {@code Model} tests are performed with.
     *
     * @return The {@code Model} tests are performed with.
     *
     * @throws IOException if getting the modules fails.
     */
    @Override
    public Model getTestModel() throws IOException
    {
        try
        {
            if ( this.testModel == null )
            {
                final Unmarshaller u = this.getModelContext().createUnmarshaller( ModelObject.MODEL_PUBLIC_ID );
                u.setSchema( this.getModelContext().createSchema( ModelObject.MODEL_PUBLIC_ID ) );

                final JAXBElement<Module> m =
                    (JAXBElement<Module>) u.unmarshal( this.getClass().getResource( "jomc-tools.xml" ) );

                final Modules modules = new Modules();
                modules.getModule().add( m.getValue() );

                final Module cp = modules.getClasspathModule(
                    Modules.getDefaultClasspathModuleName(), this.getClass().getClassLoader() );

                if ( cp != null )
                {
                    modules.getModule().add( cp );
                }

                this.testModel = new Model();
                this.testModel.setIdentifier( ModelObject.MODEL_PUBLIC_ID );
                ModelHelper.setModules( this.testModel, modules );
            }

            return this.testModel;
        }
        catch ( final JAXBException e )
        {
            String message = getMessage( e );
            if ( message == null && e.getLinkedException() != null )
            {
                message = getMessage( e.getLinkedException() );
            }

            throw (IOException) new IOException( message ).initCause( e );
        }
        catch ( final ModelException e )
        {
            throw (IOException) new IOException( getMessage( e ) ).initCause( e );
        }
    }

    /**
     * Gets the directory holding compiled Java classes.
     *
     * @return The directory holding compiled Java classes.
     *
     * @throws IOException if getting the directory fails.
     */
    public File getTestClassesDirectory() throws IOException
    {
        final File testClassesDirectory = new File( this.getTestProperty( "testClassesDirectory" ),
                                                    Integer.toString( this.testClassesId++ ) );

        assertTrue( testClassesDirectory.isAbsolute() );

        if ( testClassesDirectory.exists() )
        {
            FileUtils.cleanDirectory( testClassesDirectory );
        }

        final File outputDirectory = new File( this.getTestProperty( "projectBuildOutputDirectory" ) );
        assertTrue( outputDirectory.isAbsolute() );

        FileUtils.copyDirectory( outputDirectory, testClassesDirectory );
        return testClassesDirectory;
    }

    private String getTestProperty( final String key ) throws IOException
    {
        if ( this.testProperties == null )
        {
            this.testProperties = new java.util.Properties();
            final InputStream in = this.getClass().getResourceAsStream( "ClassFileProcessorTest.properties" );
            this.testProperties.load( in );
            in.close();
        }

        final String value = this.testProperties.getProperty( key );
        assertNotNull( value );
        return value;
    }

    @Override
    public ClassFileProcessor getTestTool() throws IOException
    {
        if ( this.testTool == null )
        {
            this.testTool = new ClassFileProcessor();
            this.testTool.setModel( this.getTestModel() );
        }

        return this.testTool;
    }

    @Override
    public void testNullPointerException() throws Exception
    {
        super.testNullPointerException();

        final Marshaller marshaller = this.getModelContext().createMarshaller( ModelObject.MODEL_PUBLIC_ID );
        final Unmarshaller unmarshaller = this.getModelContext().createUnmarshaller( ModelObject.MODEL_PUBLIC_ID );
        final URL object = this.getClass().getResource( "/java/lang/Object.class" );
        final InputStream in = object.openStream();
        final JavaClass objectClass = new ClassParser( in, object.toExternalForm() ).parse();
        in.close();

        try
        {
            this.getTestTool().commitModelObjects( null, null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }
        try
        {
            this.getTestTool().commitModelObjects( this.getModelContext(), null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().commitModelObjects( (Implementation) null, (ModelContext) null, null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }
        try
        {
            this.getTestTool().commitModelObjects( new Implementation(), (ModelContext) null, null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }
        try
        {
            this.getTestTool().commitModelObjects( new Implementation(), this.getModelContext(), null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().commitModelObjects( (Implementation) null, (Marshaller) null, null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }
        try
        {
            this.getTestTool().commitModelObjects( new Implementation(), (Marshaller) null, null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }
        try
        {
            this.getTestTool().commitModelObjects( new Implementation(), marshaller, null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().commitModelObjects( (Module) null, null, null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }
        try
        {
            this.getTestTool().commitModelObjects( new Module(), null, null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }
        try
        {
            this.getTestTool().commitModelObjects( new Module(), this.getModelContext(), null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().commitModelObjects( (Specification) null, (ModelContext) null, null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }
        try
        {
            this.getTestTool().commitModelObjects( new Specification(), (ModelContext) null, null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }
        try
        {
            this.getTestTool().commitModelObjects( new Specification(), this.getModelContext(), null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().commitModelObjects( (Specification) null, (Marshaller) null, null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }
        try
        {
            this.getTestTool().commitModelObjects( new Specification(), (Marshaller) null, null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }
        try
        {
            this.getTestTool().commitModelObjects( new Specification(), marshaller, null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().decodeModelObject( null, null, null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }
        try
        {
            this.getTestTool().decodeModelObject( unmarshaller, null, null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }
        try
        {
            this.getTestTool().decodeModelObject( unmarshaller, new byte[ 0 ], null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().encodeModelObject( null, null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }
        try
        {
            this.getTestTool().encodeModelObject( marshaller, null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().getClassfileAttribute( null, null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }
        try
        {
            this.getTestTool().getClassfileAttribute( objectClass, null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().setClassfileAttribute( null, null, null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }
        try
        {
            this.getTestTool().setClassfileAttribute( objectClass, null, null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().transformModelObjects( null, null, null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }
        try
        {
            this.getTestTool().transformModelObjects( this.getModelContext(), null, null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }
        try
        {
            this.getTestTool().transformModelObjects( this.getModelContext(), new File( "/" ), null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().transformModelObjects( (Module) null, null, null, null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }
        try
        {
            this.getTestTool().transformModelObjects( new Module(), null, null, null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }
        try
        {
            this.getTestTool().transformModelObjects( new Module(), this.getModelContext(), null, null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }
        try
        {
            this.getTestTool().transformModelObjects( new Module(), this.getModelContext(), new File( "/" ), null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().transformModelObjects( (Specification) null, null, null, null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }
        try
        {
            this.getTestTool().transformModelObjects( new Specification(), null, null, null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }
        try
        {
            this.getTestTool().transformModelObjects( new Specification(), this.getModelContext(), null, null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }
        try
        {
            this.getTestTool().transformModelObjects(
                new Specification(), this.getModelContext(), new File( "/" ), null );

            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().transformModelObjects( (Implementation) null, null, null, null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }
        try
        {
            this.getTestTool().transformModelObjects( new Implementation(), null, null, null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }
        try
        {
            this.getTestTool().transformModelObjects( new Implementation(), this.getModelContext(), null, null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }
        try
        {
            this.getTestTool().transformModelObjects(
                new Implementation(), this.getModelContext(), new File( "/" ), null );

            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().transformModelObjects( (Specification) null, null, null, null, null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }
        try
        {
            this.getTestTool().transformModelObjects( new Specification(), null, null, null, null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }
        try
        {
            this.getTestTool().transformModelObjects( new Specification(), marshaller, null, null, null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }
        try
        {
            this.getTestTool().transformModelObjects( new Specification(), marshaller, unmarshaller, null, null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }
        try
        {
            this.getTestTool().transformModelObjects( new Specification(), marshaller, unmarshaller, objectClass, null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().transformModelObjects( (Implementation) null, null, null, null, null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }
        try
        {
            this.getTestTool().transformModelObjects( new Implementation(), null, null, null, null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }
        try
        {
            this.getTestTool().transformModelObjects( new Implementation(), marshaller, null, null, null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }
        try
        {
            this.getTestTool().transformModelObjects( new Implementation(), marshaller, unmarshaller, null, null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }
        try
        {
            this.getTestTool().transformModelObjects( new Implementation(), marshaller, unmarshaller, objectClass, null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().validateModelObjects( null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().validateModelObjects( null, (File) null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }
        try
        {
            this.getTestTool().validateModelObjects( this.getModelContext(), (File) null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().validateModelObjects( (Module) null, null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }
        try
        {
            this.getTestTool().validateModelObjects( new Module(), null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().validateModelObjects( (Module) null, null, (File) null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }
        try
        {
            this.getTestTool().validateModelObjects( new Module(), null, (File) null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }
        try
        {
            this.getTestTool().validateModelObjects( new Module(), this.getModelContext(), (File) null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().validateModelObjects( (Specification) null, null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }
        try
        {
            this.getTestTool().validateModelObjects( new Specification(), null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().validateModelObjects( (Specification) null, (ModelContext) null, null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }
        try
        {
            this.getTestTool().validateModelObjects( new Specification(), (ModelContext) null, null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }
        try
        {
            this.getTestTool().validateModelObjects( new Specification(), this.getModelContext(), null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().validateModelObjects( (Implementation) null, null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }
        try
        {
            this.getTestTool().validateModelObjects( new Implementation(), null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().validateModelObjects( (Implementation) null, (ModelContext) null, null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }
        try
        {
            this.getTestTool().validateModelObjects( new Implementation(), (ModelContext) null, null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }
        try
        {
            this.getTestTool().validateModelObjects( new Implementation(), this.getModelContext(), null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().validateModelObjects( (Specification) null, (Unmarshaller) null, null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }
        try
        {
            this.getTestTool().validateModelObjects( new Specification(), (Unmarshaller) null, null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }
        try
        {
            this.getTestTool().validateModelObjects( new Specification(), unmarshaller, null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().validateModelObjects( (Implementation) null, (Unmarshaller) null, null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }
        try
        {
            this.getTestTool().validateModelObjects( new Implementation(), (Unmarshaller) null, null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }
        try
        {
            this.getTestTool().validateModelObjects( new Implementation(), unmarshaller, null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }
    }

    public void testCommitTransformValidateClasses() throws Exception
    {
        final File nonExistentDirectory = new File( this.getTestProperty( "testClassesDirectory" ),
                                                    Integer.toString( this.testClassesId++ ) );

        assertTrue( nonExistentDirectory.isAbsolute() );

        if ( nonExistentDirectory.exists() )
        {
            FileUtils.deleteDirectory( nonExistentDirectory );
        }

        final File emptyDirectory = new File( this.getTestProperty( "testClassesDirectory" ),
                                              Integer.toString( this.testClassesId++ ) );

        assertTrue( emptyDirectory.isAbsolute() );

        if ( emptyDirectory.exists() )
        {
            FileUtils.cleanDirectory( emptyDirectory );
        }
        else
        {
            assertTrue( emptyDirectory.mkdirs() );
        }

        final File allClasses = this.getTestClassesDirectory();
        final ClassLoader allClassesLoader = new URLClassLoader( new URL[]
            {
                allClasses.toURI().toURL()
            } );

        final File moduleClasses = this.getTestClassesDirectory();
        final ClassLoader moduleClassesLoader = new URLClassLoader( new URL[]
            {
                moduleClasses.toURI().toURL()
            } );

        final File implementationClasses = this.getTestClassesDirectory();
        final ClassLoader implementationClassesLoader = new URLClassLoader( new URL[]
            {
                implementationClasses.toURI().toURL()
            } );

        final File specificationClasses = this.getTestClassesDirectory();
        final ClassLoader specificationClassesLoader = new URLClassLoader( new URL[]
            {
                specificationClasses.toURI().toURL()
            } );

        final File uncommittedClasses = this.getTestClassesDirectory();
        final ClassLoader uncommittedClassesLoader = new URLClassLoader( new URL[]
            {
                uncommittedClasses.toURI().toURL()
            } );

        final Module m = this.getTestTool().getModules().getModule( this.getTestProperty( "projectName" ) );
        final Specification s = this.getTestTool().getModules().getSpecification( "org.jomc.tools.ClassFileProcessor" );
        final Implementation i =
            this.getTestTool().getModules().getImplementation( "org.jomc.tools.ClassFileProcessor" );

        final TransformerFactory transformerFactory = TransformerFactory.newInstance();
        final List<Transformer> transformers = Arrays.asList( new Transformer[]
            {
                transformerFactory.newTransformer( new StreamSource(
                this.getClass().getResourceAsStream( "no-op.xsl" ) ) )
            } );

        assertNotNull( m );
        assertNotNull( s );
        assertNotNull( i );

        try
        {
            this.getTestTool().commitModelObjects( this.getModelContext(), nonExistentDirectory );
            fail( "Expected IOException not thrown." );
        }
        catch ( final IOException e )
        {
            assertNotNull( e.getMessage() );
            System.out.println( e );
        }
        try
        {
            this.getTestTool().commitModelObjects( this.getModelContext(), emptyDirectory );
            fail( "Expected IOException not thrown." );
        }
        catch ( final IOException e )
        {
            assertNotNull( e.getMessage() );
            System.out.println( e );
        }

        try
        {
            this.getTestTool().commitModelObjects( m, this.getModelContext(), nonExistentDirectory );
            fail( "Expected IOException not thrown." );
        }
        catch ( final IOException e )
        {
            assertNotNull( e.getMessage() );
            System.out.println( e );
        }
        try
        {
            this.getTestTool().commitModelObjects( m, this.getModelContext(), emptyDirectory );
            fail( "Expected IOException not thrown." );
        }
        catch ( final IOException e )
        {
            assertNotNull( e.getMessage() );
            System.out.println( e );
        }

        try
        {
            this.getTestTool().commitModelObjects( s, this.getModelContext(), nonExistentDirectory );
            fail( "Expected IOException not thrown." );
        }
        catch ( final IOException e )
        {
            assertNotNull( e.getMessage() );
            System.out.println( e );
        }
        try
        {
            this.getTestTool().commitModelObjects( s, this.getModelContext(), emptyDirectory );
            fail( "Expected IOException not thrown." );
        }
        catch ( final IOException e )
        {
            assertNotNull( e.getMessage() );
            System.out.println( e );
        }

        try
        {
            this.getTestTool().commitModelObjects( i, this.getModelContext(), nonExistentDirectory );
            fail( "Expected IOException not thrown." );
        }
        catch ( final IOException e )
        {
            assertNotNull( e.getMessage() );
            System.out.println( e );
        }
        try
        {
            this.getTestTool().commitModelObjects( i, this.getModelContext(), emptyDirectory );
            fail( "Expected IOException not thrown." );
        }
        catch ( final IOException e )
        {
            assertNotNull( e.getMessage() );
            System.out.println( e );
        }

        try
        {
            this.getTestTool().transformModelObjects( this.getModelContext(), nonExistentDirectory, transformers );
            fail( "Expected IOException not thrown." );
        }
        catch ( final IOException e )
        {
            assertNotNull( e.getMessage() );
            System.out.println( e );
        }
        try
        {
            this.getTestTool().transformModelObjects( this.getModelContext(), emptyDirectory, transformers );
            fail( "Expected IOException not thrown." );
        }
        catch ( final IOException e )
        {
            assertNotNull( e.getMessage() );
            System.out.println( e );
        }

        try
        {
            this.getTestTool().transformModelObjects( m, this.getModelContext(), nonExistentDirectory, transformers );
            fail( "Expected IOException not thrown." );
        }
        catch ( final IOException e )
        {
            assertNotNull( e.getMessage() );
            System.out.println( e );
        }
        try
        {
            this.getTestTool().transformModelObjects( m, this.getModelContext(), emptyDirectory, transformers );
            fail( "Expected IOException not thrown." );
        }
        catch ( final IOException e )
        {
            assertNotNull( e.getMessage() );
            System.out.println( e );
        }

        try
        {
            this.getTestTool().transformModelObjects( s, this.getModelContext(), nonExistentDirectory, transformers );
            fail( "Expected IOException not thrown." );
        }
        catch ( final IOException e )
        {
            assertNotNull( e.getMessage() );
            System.out.println( e );
        }
        try
        {
            this.getTestTool().transformModelObjects( s, this.getModelContext(), emptyDirectory, transformers );
            fail( "Expected IOException not thrown." );
        }
        catch ( final IOException e )
        {
            assertNotNull( e.getMessage() );
            System.out.println( e );
        }

        try
        {
            this.getTestTool().transformModelObjects( i, this.getModelContext(), nonExistentDirectory, transformers );
            fail( "Expected IOException not thrown." );
        }
        catch ( final IOException e )
        {
            assertNotNull( e.getMessage() );
            System.out.println( e );
        }
        try
        {
            this.getTestTool().transformModelObjects( i, this.getModelContext(), emptyDirectory, transformers );
            fail( "Expected IOException not thrown." );
        }
        catch ( final IOException e )
        {
            assertNotNull( e.getMessage() );
            System.out.println( e );
        }

        try
        {
            this.getTestTool().validateModelObjects( this.getModelContext(), nonExistentDirectory );
            fail( "Expected IOException not thrown." );
        }
        catch ( final IOException e )
        {
            assertNotNull( e.getMessage() );
            System.out.println( e );
        }
        try
        {
            this.getTestTool().validateModelObjects( this.getModelContext(), emptyDirectory );
            fail( "Expected IOException not thrown." );
        }
        catch ( final IOException e )
        {
            assertNotNull( e.getMessage() );
            System.out.println( e );
        }

        try
        {
            this.getTestTool().validateModelObjects( m, this.getModelContext(), nonExistentDirectory );
            fail( "Expected IOException not thrown." );
        }
        catch ( final IOException e )
        {
            assertNotNull( e.getMessage() );
            System.out.println( e );
        }
        try
        {
            this.getTestTool().validateModelObjects( m, this.getModelContext(), emptyDirectory );
            fail( "Expected IOException not thrown." );
        }
        catch ( final IOException e )
        {
            assertNotNull( e.getMessage() );
            System.out.println( e );
        }

        try
        {
            this.getTestTool().validateModelObjects( s, this.getModelContext(), nonExistentDirectory );
            fail( "Expected IOException not thrown." );
        }
        catch ( final IOException e )
        {
            assertNotNull( e.getMessage() );
            System.out.println( e );
        }
        try
        {
            this.getTestTool().validateModelObjects( s, this.getModelContext(), emptyDirectory );
            fail( "Expected IOException not thrown." );
        }
        catch ( final IOException e )
        {
            assertNotNull( e.getMessage() );
            System.out.println( e );
        }

        try
        {
            this.getTestTool().validateModelObjects( i, this.getModelContext(), nonExistentDirectory );
            fail( "Expected IOException not thrown." );
        }
        catch ( final IOException e )
        {
            assertNotNull( e.getMessage() );
            System.out.println( e );
        }
        try
        {
            this.getTestTool().validateModelObjects( i, this.getModelContext(), emptyDirectory );
            fail( "Expected IOException not thrown." );
        }
        catch ( final IOException e )
        {
            assertNotNull( e.getMessage() );
            System.out.println( e );
        }

        this.getTestTool().commitModelObjects( this.getModelContext(), allClasses );
        this.getTestTool().commitModelObjects( m, this.getModelContext(), moduleClasses );
        this.getTestTool().commitModelObjects( s, this.getModelContext(), specificationClasses );
        this.getTestTool().commitModelObjects( i, this.getModelContext(), implementationClasses );

        this.getTestTool().transformModelObjects( this.getModelContext(), allClasses, transformers );
        this.getTestTool().transformModelObjects( m, this.getModelContext(), moduleClasses, transformers );
        this.getTestTool().transformModelObjects( s, this.getModelContext(), specificationClasses, transformers );
        this.getTestTool().transformModelObjects( i, this.getModelContext(), implementationClasses, transformers );

        this.getTestTool().validateModelObjects( ModelContext.createModelContext( allClassesLoader ) );
        this.getTestTool().validateModelObjects( m, ModelContext.createModelContext( moduleClassesLoader ) );
        this.getTestTool().validateModelObjects( s, ModelContext.createModelContext( specificationClassesLoader ) );
        this.getTestTool().validateModelObjects( i, ModelContext.createModelContext( implementationClassesLoader ) );

        this.getTestTool().validateModelObjects( this.getModelContext(), allClasses );
        this.getTestTool().validateModelObjects( m, this.getModelContext(), moduleClasses );
        this.getTestTool().validateModelObjects( s, this.getModelContext(), specificationClasses );
        this.getTestTool().validateModelObjects( i, this.getModelContext(), implementationClasses );

        this.getTestTool().validateModelObjects( ModelContext.createModelContext( uncommittedClassesLoader ) );
        this.getTestTool().validateModelObjects( this.getModelContext(), uncommittedClasses );

        final Model model = this.getTestTool().getModel();
        final Model copy = new Model( model );
        final Modules modules = ModelHelper.getModules( copy );
        final Module testModule = modules.getModule( this.getTestProperty( "projectName" ) );
        assertNotNull( testModule );

        final Specification classFileProcessor =
            testModule.getSpecifications().getSpecification( ClassFileProcessor.class.getName() );

        final Specification resourceFileProcessor =
            testModule.getSpecifications().getSpecification( ResourceFileProcessor.class.getName() );

        final Specification sourceFileProcessor =
            testModule.getSpecifications().getSpecification( SourceFileProcessor.class.getName() );

        final Implementation classFileProcessorImpl =
            testModule.getImplementations().getImplementation( ClassFileProcessor.class.getName() );

        final Implementation resourceFileProcessorImpl =
            testModule.getImplementations().getImplementation( ResourceFileProcessor.class.getName() );

        final Implementation sourceFileProcessorImpl =
            testModule.getImplementations().getImplementation( SourceFileProcessor.class.getName() );

        assertNotNull( classFileProcessor );
        assertNotNull( resourceFileProcessor );
        assertNotNull( sourceFileProcessor );
        assertNotNull( classFileProcessorImpl );
        assertNotNull( resourceFileProcessorImpl );
        assertNotNull( sourceFileProcessorImpl );

        classFileProcessor.setMultiplicity( Multiplicity.ONE );
        classFileProcessor.setScope( "TEST" );
        resourceFileProcessor.setMultiplicity( Multiplicity.ONE );
        resourceFileProcessor.setScope( "TEST" );
        sourceFileProcessor.setMultiplicity( Multiplicity.ONE );
        sourceFileProcessor.setScope( "TEST" );

        Property p = classFileProcessorImpl.getProperties().getProperty( "TestStringProperty" );
        assertNotNull( p );
        assertNotNull( classFileProcessorImpl.getProperties().getProperty().remove( p ) );

        p = classFileProcessorImpl.getProperties().getProperty( "TestPrimitiveProperty" );
        assertNotNull( p );
        p.setType( null );

        p = resourceFileProcessorImpl.getProperties().getProperty( "TestStringProperty" );
        assertNotNull( p );
        assertNotNull( resourceFileProcessorImpl.getProperties().getProperty().remove( p ) );

        p = resourceFileProcessorImpl.getProperties().getProperty( "TestPrimitiveProperty" );
        assertNotNull( p );
        p.setType( null );

        p = sourceFileProcessorImpl.getProperties().getProperty( "TestStringProperty" );
        assertNotNull( p );
        assertNotNull( sourceFileProcessorImpl.getProperties().getProperty().remove( p ) );

        p = sourceFileProcessorImpl.getProperties().getProperty( "TestPrimitiveProperty" );
        assertNotNull( p );
        p.setType( null );

        Message message = classFileProcessorImpl.getMessages().getMessage( "TestMessage" );
        assertNotNull( message );
        assertNotNull( classFileProcessorImpl.getMessages().getMessage().remove( message ) );

        message = resourceFileProcessorImpl.getMessages().getMessage( "TestMessage" );
        assertNotNull( message );
        assertNotNull( resourceFileProcessorImpl.getMessages().getMessage().remove( message ) );

        message = sourceFileProcessorImpl.getMessages().getMessage( "TestMessage" );
        assertNotNull( message );
        assertNotNull( sourceFileProcessorImpl.getMessages().getMessage().remove( message ) );

        Dependency dependency = classFileProcessorImpl.getDependencies().getDependency( "Locale" );
        assertNotNull( dependency );
        dependency.setImplementationName( null );
        dependency.setVersion( Integer.toString( Integer.MAX_VALUE ) );

        dependency = classFileProcessorImpl.getDependencies().getDependency( "JavaClasses" );
        assertNotNull( dependency );
        assertNotNull( classFileProcessorImpl.getDependencies().getDependency().remove( dependency ) );

        dependency = resourceFileProcessorImpl.getDependencies().getDependency( "Locale" );
        assertNotNull( dependency );
        dependency.setImplementationName( null );
        dependency.setVersion( Integer.toString( Integer.MAX_VALUE ) );

        dependency = resourceFileProcessorImpl.getDependencies().getDependency( "JavaBundles" );
        assertNotNull( dependency );
        assertNotNull( resourceFileProcessorImpl.getDependencies().getDependency().remove( dependency ) );

        dependency = sourceFileProcessorImpl.getDependencies().getDependency( "Locale" );
        assertNotNull( dependency );
        dependency.setImplementationName( null );
        dependency.setVersion( Integer.toString( Integer.MAX_VALUE ) );

        dependency = sourceFileProcessorImpl.getDependencies().getDependency( "JavaSources" );
        assertNotNull( dependency );
        assertNotNull( sourceFileProcessorImpl.getDependencies().getDependency().remove( dependency ) );

        this.getTestTool().setModel( copy );

        this.getTestTool().validateModelObjects( ModelContext.createModelContext( allClassesLoader ) );
        this.getTestTool().validateModelObjects( m, ModelContext.createModelContext( moduleClassesLoader ) );
        this.getTestTool().validateModelObjects( s, ModelContext.createModelContext( specificationClassesLoader ) );
        this.getTestTool().validateModelObjects( i, ModelContext.createModelContext( implementationClassesLoader ) );

        this.getTestTool().validateModelObjects( this.getModelContext(), allClasses );
        this.getTestTool().validateModelObjects( m, this.getModelContext(), moduleClasses );
        this.getTestTool().validateModelObjects( s, this.getModelContext(), specificationClasses );
        this.getTestTool().validateModelObjects( i, this.getModelContext(), implementationClasses );

        this.getTestTool().validateModelObjects( ModelContext.createModelContext( uncommittedClassesLoader ) );
        this.getTestTool().validateModelObjects( this.getModelContext(), uncommittedClasses );

        this.getTestTool().setModel( model );
    }

    public void testCopyConstructor() throws Exception
    {
        try
        {
            new ClassFileProcessor( null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNotNull( e.getMessage() );
            System.out.println( e.toString() );
        }

        new ClassFileProcessor( this.getTestTool() );
    }

    private static String getMessage( final Throwable t )
    {
        return t != null ? t.getMessage() != null ? t.getMessage() : getMessage( t.getCause() ) : null;
    }

}
