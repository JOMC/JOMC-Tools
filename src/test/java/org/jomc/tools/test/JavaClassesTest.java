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
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import junit.framework.Assert;
import org.apache.bcel.classfile.JavaClass;
import org.apache.commons.io.FileUtils;
import org.jomc.model.DefaultModelManager;
import org.jomc.model.Implementation;
import org.jomc.model.ModelObject;
import org.jomc.model.Module;
import org.jomc.model.Modules;
import org.jomc.model.Specification;
import org.jomc.tools.JavaClasses;
import org.xml.sax.SAXException;

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
     * @throws IOException if reading schema resources fails.
     * @throws SAXException if parsing schema resources fails.
     * @throws JAXBException if unmarshalling schema resources fails.
     */
    @Override
    public Modules getTestModules() throws IOException, SAXException, JAXBException
    {
        if ( this.testModules == null )
        {
            this.testModules = new Modules();
            final DefaultModelManager defaultModelManager = new DefaultModelManager();
            final Unmarshaller u = defaultModelManager.getUnmarshaller( false );
            final JAXBElement<Module> m =
                (JAXBElement<Module>) u.unmarshal( this.getClass().getResource( "jomc-tools.xml" ) );

            this.testModules.getModule().add( m.getValue() );

            final Module cp = defaultModelManager.getClasspathModule( this.testModules );
            if ( cp != null )
            {
                this.testModules.getModule().add( cp );
            }
        }

        return this.testModules;
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
    public JavaClasses getTestTool() throws IOException, SAXException, JAXBException
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

        try
        {
            this.getTestTool().commitClasses( null );
            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().commitClasses( (Implementation) null, new File( "/" ) );
            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().commitClasses( new Implementation(), null );
            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().commitClasses( (Module) null, new File( "/" ) );
            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().commitClasses( new Module(), null );
            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().commitClasses( (Specification) null, new File( "/" ) );
            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().commitClasses( new Specification(), null );
            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().decodeModelObject( null, ModelObject.class );
            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().decodeModelObject( new byte[ 0 ], null );
            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().encodeModelObject( null );
            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().getClassfileAttribute( null, "" );
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
            this.getTestTool().getJavaClass( (InputStream) null, "" );
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
            this.getTestTool().getJavaClass( (URL) null, "" );
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
            this.getTestTool().setClassfileAttribute( null, "", null );
            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().setClassfileAttribute( null, "", new byte[ 0 ] );
            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().transformClasses( null, null );
            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().transformClasses( new File( "/" ), null );
            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().validateClasses( (ClassLoader) null );
            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().validateClasses( (File) null );
            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().validateClasses( (Implementation) null, null );
            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().validateClasses( new Implementation(), null );
            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().validateClasses( (Specification) null, null );
            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().validateClasses( new Specification(), null );
            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().validateClasses( (Module) null, (ClassLoader) null );
            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().validateClasses( new Module(), (ClassLoader) null );
            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().validateClasses( (Module) null, (File) null );
            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().validateClasses( new Module(), (File) null );
            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().validateClasses( (Specification) null, null );
            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().validateClasses( new Specification(), null );
            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().transformClasses( null, null );
            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().transformClasses( new File( "/" ), null );
            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().transformClasses( (Implementation) null, null, null );
            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().transformClasses( new Implementation(), null, null );
            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().transformClasses( (Module) null, (File) null, null );
            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().transformClasses( new Module(), (File) null, null );
            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().transformClasses( new Module(), new File( "/" ), null );
            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().transformClasses( (Specification) null, null, null );
            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().transformClasses( new Specification(), null, null );
            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }
    }

    public void testCommitValidateClasses() throws Exception
    {
        final File allClasses = this.getTestClassesDirectory();
        final File moduleClasses = this.getTestClassesDirectory();
        final File implementationClasses = this.getTestClassesDirectory();
        final File specificationClasses = this.getTestClassesDirectory();
        final Implementation i = this.getTestTool().getModules().getImplementation( "org.jomc.tools.JavaClasses" );
        final Module m = this.getTestTool().getModules().getModule( this.getTestProperty( "projectName" ) );
        final Specification s = this.getTestTool().getModules().getSpecification( "org.jomc.tools.JavaClasses" );

        this.getTestTool().commitClasses( allClasses );
        this.getTestTool().commitClasses( i, implementationClasses );
        this.getTestTool().commitClasses( m, moduleClasses );
        this.getTestTool().commitClasses( s, specificationClasses );

        this.getTestTool().validateClasses( allClasses );
        this.getTestTool().validateClasses( m, moduleClasses );

        final JavaClass implementationClass = this.getTestTool().getJavaClass(
            new File( implementationClasses, this.getTestTool().getJavaClasspathLocation( i ) + ".class" ) );

        final JavaClass specificationClass = this.getTestTool().getJavaClass(
            new File( specificationClasses, this.getTestTool().getJavaClasspathLocation( s ) + ".class" ) );

        this.getTestTool().validateClasses( i, implementationClass );
        this.getTestTool().validateClasses( s, specificationClass );
    }

}
