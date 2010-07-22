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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.jomc.model.Implementation;
import org.jomc.model.Module;
import org.jomc.model.Specification;
import org.jomc.tools.SourceFileProcessor;
import org.jomc.util.SectionEditor;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.fail;

/**
 * Test cases for class {@code org.jomc.tools.SourceFileProcessor} implementations.
 *
 * @author <a href="mailto:schulte2005@users.sourceforge.net">Christian Schulte</a>
 * @version $Id$
 */
public class SourceFileProcessorTest extends JomcToolTest
{

    /** Serial number of the test sources directory. */
    private int testSourcesId;

    /** The {@code SourceFileProcessor} instance tests are performed with. */
    private SourceFileProcessor testTool;

    /** Properties backing the instance. */
    private Properties testProperties;

    @Override
    public SourceFileProcessor getTestTool() throws IOException
    {
        if ( this.testTool == null )
        {
            this.testTool = new SourceFileProcessor();
            this.testTool.setModel( this.getTestModel() );
        }

        return this.testTool;
    }

    /**
     * Gets the directory to write sources to.
     *
     * @return The directory to write sources to.
     *
     * @throws IOException if getting the directory fails.
     */
    public File getTestSourcesDirectory() throws IOException
    {
        final File testSourcesDirectory =
            new File( this.getTestProperty( "testSourcesDirectory" ), Integer.toString( this.testSourcesId++ ) );

        assertTrue( testSourcesDirectory.isAbsolute() );

        if ( testSourcesDirectory.exists() )
        {
            FileUtils.deleteDirectory( testSourcesDirectory );
        }

        return testSourcesDirectory;
    }

    private String getTestProperty( final String key ) throws IOException
    {
        if ( this.testProperties == null )
        {
            this.testProperties = new java.util.Properties();
            final InputStream in = this.getClass().getResourceAsStream( "SourceFileProcessorTest.properties" );
            this.testProperties.load( in );
            in.close();
        }

        final String value = this.testProperties.getProperty( key );
        assertNotNull( value );
        return value;
    }

    @Override
    public void testNullPointerException() throws Exception
    {
        super.testNullPointerException();

        try
        {
            this.getTestTool().getSourceFileType( (Specification) null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().getSourceFileType( (Implementation) null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().getSourceFileEditor( (Specification) null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().getSourceFileEditor( (Implementation) null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().manageSourceFiles( null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().manageSourceFiles( (Implementation) null, new File( "/" ) );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().manageSourceFiles( new Implementation(), null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().manageSourceFiles( (Module) null, new File( "/" ) );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }


        try
        {
            this.getTestTool().manageSourceFiles( new Module(), null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().manageSourceFiles( (Specification) null, new File( "/" ) );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().manageSourceFiles( new Specification(), null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }
    }

    @Override
    public void testNotNull() throws Exception
    {
        super.testNotNull();

        final Implementation i = new Implementation();
        i.setIdentifier( "TEST" );
        i.setClazz( "TEST" );

        final Specification s = new Specification();
        s.setIdentifier( "TEST" );
        s.setClazz( "TEST" );

        assertNotNull( this.getTestTool().getSourceFileType(
            this.getTestTool().getModules().getImplementation( "Implementation" ) ) );

        assertNotNull( this.getTestTool().getSourceFileType(
            this.getTestTool().getModules().getSpecification( "Specification" ) ) );

        assertNotNull( this.getTestTool().getSourceFileEditor(
            this.getTestTool().getModules().getImplementation( "Implementation" ) ) );

        assertNotNull( this.getTestTool().getSourceFileEditor(
            this.getTestTool().getModules().getSpecification( "Specification" ) ) );

    }

    public void testManageSources() throws Exception
    {
        final File nonExistingDirectory = this.getTestSourcesDirectory();
        if ( nonExistingDirectory.exists() )
        {
            FileUtils.deleteDirectory( nonExistingDirectory );
        }

        try
        {
            this.getTestTool().manageSourceFiles( nonExistingDirectory );
            fail( "Expected IOException not found." );
        }
        catch ( final IOException e )
        {
            assertNotNull( e.getMessage() );
            System.out.println( e );
        }

        try
        {
            this.getTestTool().manageSourceFiles( this.getTestTool().getModules().getModule( "Module" ),
                                                  nonExistingDirectory );

            fail( "Expected IOException not found." );
        }
        catch ( final IOException e )
        {
            assertNotNull( e.getMessage() );
            System.out.println( e );
        }

        try
        {
            this.getTestTool().manageSourceFiles( this.getTestTool().getModules().getImplementation( "Implementation" ),
                                                  nonExistingDirectory );

            fail( "Expected IOException not found." );
        }
        catch ( final IOException e )
        {
            assertNotNull( e.getMessage() );
            System.out.println( e );
        }

        try
        {
            this.getTestTool().manageSourceFiles( this.getTestTool().getModules().getSpecification( "Specification" ),
                                                  nonExistingDirectory );

            fail( "Expected IOException not found." );
        }
        catch ( final IOException e )
        {
            assertNotNull( e.getMessage() );
            System.out.println( e );
        }

        File sourcesDirectory = this.getTestSourcesDirectory();
        assertTrue( sourcesDirectory.mkdirs() );
        this.getTestTool().manageSourceFiles( sourcesDirectory );

        sourcesDirectory = this.getTestSourcesDirectory();
        assertTrue( sourcesDirectory.mkdirs() );
        this.getTestTool().manageSourceFiles( this.getTestTool().getModules().getModule( "Module" ),
                                              sourcesDirectory );

        final File implementationDirectory = this.getTestSourcesDirectory();
        assertTrue( implementationDirectory.mkdirs() );
        this.getTestTool().manageSourceFiles( this.getTestTool().getModules().getImplementation( "Implementation" ),
                                              implementationDirectory );

        this.getTestTool().manageSourceFiles( this.getTestTool().getModules().getImplementation( "Implementation" ),
                                              implementationDirectory );

        final File specificationDirectory = this.getTestSourcesDirectory();
        assertTrue( specificationDirectory.mkdirs() );
        this.getTestTool().manageSourceFiles( this.getTestTool().getModules().getSpecification( "Specification" ),
                                              specificationDirectory );

        this.getTestTool().manageSourceFiles( this.getTestTool().getModules().getSpecification( "Specification" ),
                                              specificationDirectory );

        IOUtils.copy( this.getClass().getResourceAsStream( "IllegalImplementationSource.java.txt" ),
                      new FileOutputStream( new File( implementationDirectory, "Implementation.java" ) ) );

        IOUtils.copy( this.getClass().getResourceAsStream( "IllegalSpecificationSource.java.txt" ),
                      new FileOutputStream( new File( specificationDirectory, "Specification.java" ) ) );

        try
        {
            this.getTestTool().manageSourceFiles( this.getTestTool().getModules().getImplementation( "Implementation" ),
                                                  implementationDirectory );

            fail( "Expected IOException not thrown." );
        }
        catch ( final IOException e )
        {
            assertNotNull( e.getMessage() );
            System.out.println( e.toString() );
        }

        try
        {
            this.getTestTool().manageSourceFiles( this.getTestTool().getModules().getSpecification( "Specification" ),
                                                  specificationDirectory );

            fail( "Expected IOException not thrown." );
        }
        catch ( final IOException e )
        {
            assertNotNull( e.getMessage() );
            System.out.println( e.toString() );
        }

        this.getTestTool().setTemplateProfile( "DOES_NOT_EXIST" );

        sourcesDirectory = this.getTestSourcesDirectory();
        assertTrue( sourcesDirectory.mkdirs() );
        this.getTestTool().manageSourceFiles( sourcesDirectory );

        sourcesDirectory = this.getTestSourcesDirectory();
        assertTrue( sourcesDirectory.mkdirs() );
        this.getTestTool().manageSourceFiles( this.getTestTool().getModules().getModule( "Module" ), sourcesDirectory );

        sourcesDirectory = this.getTestSourcesDirectory();
        assertTrue( sourcesDirectory.mkdirs() );
        this.getTestTool().manageSourceFiles( this.getTestTool().getModules().getImplementation( "Implementation" ),
                                              sourcesDirectory );

        sourcesDirectory = this.getTestSourcesDirectory();
        assertTrue( sourcesDirectory.mkdirs() );
        this.getTestTool().manageSourceFiles( this.getTestTool().getModules().getSpecification( "Specification" ),
                                              sourcesDirectory );

    }

    public void testMandatorySections() throws Exception
    {
        final SectionEditor editor = new SectionEditor();
        final File specificationDirectory = this.getTestSourcesDirectory();
        final File implementationDirectory = this.getTestSourcesDirectory();

        assertTrue( specificationDirectory.mkdirs() );
        assertTrue( implementationDirectory.mkdirs() );

        File f = new File( implementationDirectory, "Implementation.java" );
        IOUtils.copy( this.getClass().getResourceAsStream( "ImplementationWithoutAnnotationsSection.java.txt" ),
                      new FileOutputStream( f ) );

        this.getTestTool().manageSourceFiles( this.getTestTool().getModules().getImplementation( "Implementation" ),
                                              implementationDirectory );

        String edited = IOUtils.toString( new FileInputStream( f ) );
        editor.edit( edited );
        assertTrue( editor.isSectionPresent( "Annotations" ) );

        f = new File( implementationDirectory, "Implementation.java" );
        IOUtils.copy( this.getClass().getResourceAsStream( "ImplementationWithoutDependenciesSection.java.txt" ),
                      new FileOutputStream( f ) );

        this.getTestTool().manageSourceFiles( this.getTestTool().getModules().getImplementation( "Implementation" ),
                                              implementationDirectory );

        edited = IOUtils.toString( new FileInputStream( f ) );
        editor.edit( edited );
        assertTrue( editor.isSectionPresent( "Dependencies" ) );

        f = new File( implementationDirectory, "Implementation.java" );
        IOUtils.copy( this.getClass().getResourceAsStream( "ImplementationWithoutMessagesSection.java.txt" ),
                      new FileOutputStream( f ) );

        this.getTestTool().manageSourceFiles( this.getTestTool().getModules().getImplementation( "Implementation" ),
                                              implementationDirectory );

        edited = IOUtils.toString( new FileInputStream( f ) );
        editor.edit( edited );
        assertTrue( editor.isSectionPresent( "Messages" ) );

        f = new File( implementationDirectory, "Implementation.java" );
        IOUtils.copy( this.getClass().getResourceAsStream( "ImplementationWithoutPropertiesSection.java.txt" ),
                      new FileOutputStream( f ) );

        this.getTestTool().manageSourceFiles( this.getTestTool().getModules().getImplementation( "Implementation" ),
                                              implementationDirectory );

        edited = IOUtils.toString( new FileInputStream( f ) );
        editor.edit( edited );
        assertTrue( editor.isSectionPresent( "Properties" ) );

        f = new File( implementationDirectory, "ImplementationOfSpecification.java" );
        IOUtils.copy(
            this.getClass().getResourceAsStream( "ImplementationOfSpecificationWithoutConstructorsSection.java.txt" ),
            new FileOutputStream( f ) );

        this.getTestTool().manageSourceFiles(
            this.getTestTool().getModules().getImplementation( "ImplementationOfSpecification" ),
            implementationDirectory );

        edited = IOUtils.toString( new FileInputStream( f ) );
        editor.edit( edited );
        assertTrue( editor.isSectionPresent( "Constructors" ) );

        f = new File( specificationDirectory, "Specification.java" );
        IOUtils.copy( this.getClass().getResourceAsStream( "SpecificationWithoutAnnotationsSection.java.txt" ),
                      new FileOutputStream( f ) );

        this.getTestTool().manageSourceFiles( this.getTestTool().getModules().getSpecification( "Specification" ),
                                              specificationDirectory );

        edited = IOUtils.toString( new FileInputStream( f ) );
        editor.edit( edited );
        assertTrue( editor.isSectionPresent( "Annotations" ) );
    }

    public void testOptionalSections() throws Exception
    {
        final SectionEditor editor = new SectionEditor();
        final File implementationDirectory = this.getTestSourcesDirectory();
        final File specificationDirectory = this.getTestSourcesDirectory();

        assertTrue( specificationDirectory.mkdirs() );
        assertTrue( implementationDirectory.mkdirs() );

        File f = new File( implementationDirectory, "Implementation.java" );
        IOUtils.copy( this.getClass().getResourceAsStream( "ImplementationWithoutConstructorsSection.java.txt" ),
                      new FileOutputStream( f ) );

        this.getTestTool().manageSourceFiles( this.getTestTool().getModules().getImplementation( "Implementation" ),
                                              implementationDirectory );

        String edited = IOUtils.toString( new FileInputStream( f ) );
        editor.edit( edited );
        assertFalse( editor.isSectionPresent( "Constructors" ) );

        IOUtils.copy( this.getClass().getResourceAsStream( "ImplementationWithoutDefaultConstructorSection.java.txt" ),
                      new FileOutputStream( f ) );

        this.getTestTool().manageSourceFiles( this.getTestTool().getModules().getImplementation( "Implementation" ),
                                              implementationDirectory );

        edited = IOUtils.toString( new FileInputStream( f ) );
        editor.edit( edited );
        assertTrue( editor.isSectionPresent( "Constructors" ) );
        assertTrue( editor.isSectionPresent( "Default Constructor" ) );

        IOUtils.copy( this.getClass().getResourceAsStream( "ImplementationWithoutDocumentationSection.java.txt" ),
                      new FileOutputStream( f ) );

        this.getTestTool().manageSourceFiles( this.getTestTool().getModules().getImplementation( "Implementation" ),
                                              implementationDirectory );

        edited = IOUtils.toString( new FileInputStream( f ) );
        editor.edit( edited );
        assertFalse( editor.isSectionPresent( "Documentation" ) );

        IOUtils.copy( this.getClass().getResourceAsStream( "ImplementationWithoutLicenseSection.java.txt" ),
                      new FileOutputStream( f ) );

        this.getTestTool().manageSourceFiles( this.getTestTool().getModules().getImplementation( "Implementation" ),
                                              implementationDirectory );

        edited = IOUtils.toString( new FileInputStream( f ) );
        editor.edit( edited );
        assertFalse( editor.isSectionPresent( "License Header" ) );

        f = new File( specificationDirectory, "Specification.java" );
        IOUtils.copy( this.getClass().getResourceAsStream( "SpecificationWithoutDocumentationSection.java.txt" ),
                      new FileOutputStream( f ) );

        this.getTestTool().manageSourceFiles( this.getTestTool().getModules().getSpecification( "Specification" ),
                                              specificationDirectory );

        edited = IOUtils.toString( new FileInputStream( f ) );
        editor.edit( edited );
        assertFalse( editor.isSectionPresent( "Documentation" ) );

        IOUtils.copy( this.getClass().getResourceAsStream( "SpecificationWithoutLicenseSection.java.txt" ),
                      new FileOutputStream( f ) );

        this.getTestTool().manageSourceFiles( this.getTestTool().getModules().getSpecification( "Specification" ),
                                              specificationDirectory );

        edited = IOUtils.toString( new FileInputStream( f ) );
        editor.edit( edited );
        assertFalse( editor.isSectionPresent( "License Header" ) );
    }

    public void testCopyConstructor() throws Exception
    {
        try
        {
            new SourceFileProcessor( null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNotNull( e.getMessage() );
            System.out.println( e.toString() );
        }

        new SourceFileProcessor( this.getTestTool() );
    }

}
