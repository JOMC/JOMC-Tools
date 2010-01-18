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
package org.jomc.tools.test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;
import junit.framework.Assert;
import org.apache.bcel.classfile.JavaClass;
import org.apache.commons.io.FileUtils;
import org.jomc.model.Implementation;
import org.jomc.model.ModelContext;
import org.jomc.model.ModelException;
import org.jomc.model.Module;
import org.jomc.model.Modules;
import org.jomc.model.Specification;
import org.jomc.tools.JavaClasses;
import org.jomc.tools.ToolException;

/**
 * Tests {@code JavaClasses} implementations.
 *
 * @author <a href="mailto:cs@jomc.org">Christian Schulte</a>
 * @version $Id$
 */
public class JavaClassesTest extends JomcToolTest
{

    /** The test {@code Modules} of the instance. */
    private Modules testModules;

    /** The {@code JavaClasses} instance tests are performed with. */
    private JavaClasses testTool;

    /** Serial number of the test classes directory. */
    private int testClassesId;

    /**
     * Gets the {@code Modules} tests are performed with.
     *
     * @return The {@code Modules} tests are performed with.
     *
     * @throws ToolException if getting the modules fails.
     */
    @Override
    public Modules getTestModules() throws ToolException
    {
        try
        {
            if ( this.testModules == null )
            {
                final ModelContext context = ModelContext.createModelContext( this.getClass().getClassLoader() );
                final Unmarshaller u = context.createUnmarshaller();
                u.setSchema( context.createSchema() );

                final JAXBElement<Module> m =
                    (JAXBElement<Module>) u.unmarshal( this.getClass().getResource( "jomc-tools.xml" ) );

                this.testModules = new Modules();
                this.testModules.getModule().add( m.getValue() );

                final Module cp = this.testModules.getClasspathModule(
                    Modules.getDefaultClasspathModuleName(), this.getClass().getClassLoader() );

                if ( cp != null )
                {
                    this.testModules.getModule().add( cp );
                }
            }

            return this.testModules;
        }
        catch ( final JAXBException e )
        {
            throw new ToolException( e );
        }
        catch ( final ModelException e )
        {
            throw new ToolException( e );
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

        if ( testClassesDirectory.exists() )
        {
            FileUtils.cleanDirectory( testClassesDirectory );
        }

        final File outputDirectory = new File( this.getTestProperty( "projectBuildOutputDirectory" ) );
        FileUtils.copyDirectory( outputDirectory, testClassesDirectory );
        return testClassesDirectory;
    }

    private String getTestProperty( final String key ) throws IOException
    {
        final java.util.Properties p = new java.util.Properties();
        final InputStream in = this.getClass().getResourceAsStream( "JavaClassesTest.properties" );
        p.load( in );
        in.close();

        return p.getProperty( key );
    }

    @Override
    public JavaClasses getTestTool() throws ToolException
    {
        if ( this.testTool == null )
        {
            this.testTool = new JavaClasses();
            this.testTool.setModules( this.getTestModules() );
        }

        return this.testTool;
    }

    @Override
    public void testNullPointerException() throws Exception
    {
        super.testNullPointerException();

        final ModelContext context = ModelContext.createModelContext( this.getClass().getClassLoader() );
        final Marshaller marshaller = context.createMarshaller();
        final Unmarshaller unmarshaller = context.createUnmarshaller();

        try
        {
            this.getTestTool().commitClasses( null, null );
            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }
        try
        {
            this.getTestTool().commitClasses( marshaller, null );
            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().commitClasses( (Implementation) null, null, null );
            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }
        try
        {
            this.getTestTool().commitClasses( new Implementation(), null, null );
            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }
        try
        {
            this.getTestTool().commitClasses( new Implementation(), marshaller, null );
            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().commitClasses( (Module) null, null, null );
            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }
        try
        {
            this.getTestTool().commitClasses( new Module(), null, null );
            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }
        try
        {
            this.getTestTool().commitClasses( new Module(), marshaller, null );
            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().commitClasses( (Specification) null, null, null );
            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }
        try
        {
            this.getTestTool().commitClasses( new Specification(), null, null );
            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }
        try
        {
            this.getTestTool().commitClasses( new Specification(), marshaller, null );
            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().decodeModelObject( null, null, null );
            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }
        try
        {
            this.getTestTool().decodeModelObject( unmarshaller, null, null );
            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }
        try
        {
            this.getTestTool().decodeModelObject( unmarshaller, new byte[ 0 ], null );
            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().encodeModelObject( null, null );
            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }
        try
        {
            this.getTestTool().encodeModelObject( marshaller, null );
            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().getClassfileAttribute( null, null );
            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }
        try
        {
            this.getTestTool().getClassfileAttribute( this.getTestTool().getJavaClass(
                this.getClass().getResource( "java/lang/Object.class" ), "java.lang.Object" ), null );

            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().getJavaClass( null );
            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().getJavaClass( (InputStream) null, null );
            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }
        try
        {
            this.getTestTool().getJavaClass( new ByteArrayInputStream( new byte[ 0 ] ), null );
            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().getJavaClass( (URL) null, null );
            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }
        try
        {
            this.getTestTool().getJavaClass( new File( "/" ).toURI().toURL(), null );
            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().setClassfileAttribute( null, null, null );
            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }


        try
        {
            this.getTestTool().setClassfileAttribute( this.getTestTool().getJavaClass(
                this.getClass().getResource( "java/lang/Object.class" ), "java.lang.Object" ), null, null );

            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }
        try
        {
            this.getTestTool().setClassfileAttribute( this.getTestTool().getJavaClass(
                this.getClass().getResource( "java/lang/Object.class" ), "java.lang.Object" ), "TEST", null );

            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().transformClasses( null, null, null, null );
            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }
        try
        {
            this.getTestTool().transformClasses( marshaller, null, null, null );
            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }
        try
        {
            this.getTestTool().transformClasses( marshaller, unmarshaller, null, null );
            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }
        try
        {
            this.getTestTool().transformClasses( marshaller, unmarshaller, new File( "/" ), null );
            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().transformClasses( (Module) null, null, null, null, null );
            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }
        try
        {
            this.getTestTool().transformClasses( new Module(), null, null, null, null );
            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }
        try
        {
            this.getTestTool().transformClasses( new Module(), marshaller, null, null, null );
            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }
        try
        {
            this.getTestTool().transformClasses( new Module(), marshaller, unmarshaller, null, null );
            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }
        try
        {
            this.getTestTool().transformClasses( new Module(), marshaller, unmarshaller, new File( "/" ), null );
            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().transformClasses( (Specification) null, null, null, null, null );
            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }
        try
        {
            this.getTestTool().transformClasses( new Specification(), null, null, null, null );
            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }
        try
        {
            this.getTestTool().transformClasses( new Specification(), marshaller, null, null, null );
            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }
        try
        {
            this.getTestTool().transformClasses( new Specification(), marshaller, unmarshaller, null, null );
            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }
        try
        {
            this.getTestTool().transformClasses(
                new Specification(), marshaller, unmarshaller,
                this.getTestTool().getJavaClass( this.getClass().getResource( "java/lang/Oject.class" ),
                                                 "java.lang.Object" ), null );

            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().transformClasses( (Implementation) null, null, null, null, null );
            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }
        try
        {
            this.getTestTool().transformClasses( new Implementation(), null, null, null, null );
            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }
        try
        {
            this.getTestTool().transformClasses( new Implementation(), marshaller, null, null, null );
            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }
        try
        {
            this.getTestTool().transformClasses( new Implementation(), marshaller, unmarshaller, null, null );
            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }
        try
        {
            this.getTestTool().transformClasses(
                new Implementation(), marshaller, unmarshaller, this.getTestTool().getJavaClass(
                this.getClass().getResource( "java/lang/Oject.class" ), "java.lang.Object" ), null );

            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().validateClasses( null, (ClassLoader) null );
            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }
        try
        {
            this.getTestTool().validateClasses( unmarshaller, (ClassLoader) null );
            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().validateClasses( null, (File) null );
            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }
        try
        {
            this.getTestTool().validateClasses( unmarshaller, (File) null );
            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().validateClasses( (Module) null, null, (ClassLoader) null );
            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }
        try
        {
            this.getTestTool().validateClasses( new Module(), null, (ClassLoader) null );
            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }
        try
        {
            this.getTestTool().validateClasses( new Module(), unmarshaller, (ClassLoader) null );
            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().validateClasses( (Module) null, null, (File) null );
            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }
        try
        {
            this.getTestTool().validateClasses( new Module(), null, (File) null );
            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }
        try
        {
            this.getTestTool().validateClasses( new Module(), unmarshaller, (File) null );
            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().validateClasses( (Specification) null, null, null );
            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }
        try
        {
            this.getTestTool().validateClasses( new Specification(), null, null );
            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }
        try
        {
            this.getTestTool().validateClasses( new Specification(), unmarshaller, null );
            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().validateClasses( (Implementation) null, null, null );
            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }
        try
        {
            this.getTestTool().validateClasses( new Implementation(), null, null );
            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }
        try
        {
            this.getTestTool().validateClasses( new Implementation(), unmarshaller, null );
            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }
    }

    public void testCommitTransformValidateClasses() throws Exception
    {
        final ModelContext context = ModelContext.createModelContext( this.getClass().getClassLoader() );
        final Marshaller marshaller = context.createMarshaller();
        final Unmarshaller unmarshaller = context.createUnmarshaller();
        final File allClasses = this.getTestClassesDirectory();
        final File moduleClasses = this.getTestClassesDirectory();
        final File implementationClasses = this.getTestClassesDirectory();
        final File specificationClasses = this.getTestClassesDirectory();
        final File uncommittedClasses = this.getTestClassesDirectory();
        final Implementation i = this.getTestTool().getModules().getImplementation( "org.jomc.tools.JavaClasses" );
        final Module m = this.getTestTool().getModules().getModule( this.getTestProperty( "projectName" ) );
        final Specification s = this.getTestTool().getModules().getSpecification( "org.jomc.tools.JavaClasses" );
        final TransformerFactory transformerFactory = TransformerFactory.newInstance();
        final List<Transformer> transformers = Arrays.asList( new Transformer[]
            {
                transformerFactory.newTransformer( new StreamSource(
                this.getClass().getResourceAsStream( "no-op.xsl" ) ) )
            } );

        this.getTestTool().commitClasses( marshaller, allClasses );
        this.getTestTool().commitClasses( m, marshaller, moduleClasses );
        this.getTestTool().commitClasses( s, marshaller, specificationClasses );
        this.getTestTool().commitClasses( i, marshaller, implementationClasses );

        final JavaClass implementationClass = this.getTestTool().getJavaClass(
            new File( implementationClasses, i.getClazz().replace( '.', File.separatorChar ) + ".class" ) );

        final JavaClass specificationClass = this.getTestTool().getJavaClass(
            new File( specificationClasses, s.getClazz().replace( '.', File.separatorChar ) + ".class" ) );

        this.getTestTool().transformClasses( marshaller, unmarshaller, allClasses, transformers );
        this.getTestTool().transformClasses( m, marshaller, unmarshaller, moduleClasses, transformers );
        this.getTestTool().transformClasses( s, marshaller, unmarshaller, specificationClass, transformers );
        this.getTestTool().transformClasses( i, marshaller, unmarshaller, implementationClass, transformers );

        this.getTestTool().validateClasses( unmarshaller, allClasses );
        this.getTestTool().validateClasses( m, unmarshaller, moduleClasses );
        this.getTestTool().validateClasses( s, unmarshaller, specificationClass );
        this.getTestTool().validateClasses( i, unmarshaller, implementationClass );

        this.getTestTool().validateClasses( unmarshaller, uncommittedClasses );
    }

}
