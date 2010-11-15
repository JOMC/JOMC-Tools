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

import org.junit.Test;
import java.io.OutputStream;
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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

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

    /** Properties backing the instance. */
    private Properties testProperties;

    /** {@inheritDoc} */
    @Override
    public SourceFileProcessor getJomcTool()
    {
        return (SourceFileProcessor) super.getJomcTool();
    }

    /** {@inheritDoc} */
    @Override
    protected SourceFileProcessor newJomcTool()
    {
        return new SourceFileProcessor();
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

    @Test
    public final void testSourceFileProcessorNullPointerException() throws Exception
    {
        try
        {
            this.getJomcTool().getSourceFileType( (Specification) null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getJomcTool().getSourceFileType( (Implementation) null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getJomcTool().getSourceFileEditor( (Specification) null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getJomcTool().getSourceFileEditor( (Implementation) null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getJomcTool().manageSourceFiles( null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getJomcTool().manageSourceFiles( (Implementation) null, new File( "/" ) );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getJomcTool().manageSourceFiles( new Implementation(), null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getJomcTool().manageSourceFiles( (Module) null, new File( "/" ) );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }


        try
        {
            this.getJomcTool().manageSourceFiles( new Module(), null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getJomcTool().manageSourceFiles( (Specification) null, new File( "/" ) );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getJomcTool().manageSourceFiles( new Specification(), null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }
    }

    @Test
    public final void testSourceFileProcessorNotNull() throws Exception
    {
        final Implementation i = new Implementation();
        i.setIdentifier( "TEST" );
        i.setClazz( "TEST" );

        final Specification s = new Specification();
        s.setIdentifier( "TEST" );
        s.setClazz( "TEST" );

        assertNotNull( this.getJomcTool().getSourceFileType(
            this.getJomcTool().getModules().getImplementation( "Implementation" ) ) );

        assertNotNull( this.getJomcTool().getSourceFileType(
            this.getJomcTool().getModules().getSpecification( "Specification" ) ) );

        assertNotNull( this.getJomcTool().getSourceFileEditor(
            this.getJomcTool().getModules().getImplementation( "Implementation" ) ) );

        assertNotNull( this.getJomcTool().getSourceFileEditor(
            this.getJomcTool().getModules().getSpecification( "Specification" ) ) );

    }

    @Test
    public final void testManageSources() throws Exception
    {
        this.getJomcTool().setInputEncoding( this.getTestProperty( "resourceEncoding" ) );
        this.getJomcTool().setOutputEncoding( this.getTestProperty( "resourceEncoding" ) );

        final File nonExistingDirectory = this.getTestSourcesDirectory();
        if ( nonExistingDirectory.exists() )
        {
            FileUtils.deleteDirectory( nonExistingDirectory );
        }

        try
        {
            this.getJomcTool().manageSourceFiles( nonExistingDirectory );
            fail( "Expected IOException not found." );
        }
        catch ( final IOException e )
        {
            assertNotNull( e.getMessage() );
            System.out.println( e );
        }

        try
        {
            this.getJomcTool().manageSourceFiles( this.getJomcTool().getModules().getModule( "Module" ),
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
            this.getJomcTool().manageSourceFiles( this.getJomcTool().getModules().getImplementation( "Implementation" ),
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
            this.getJomcTool().manageSourceFiles( this.getJomcTool().getModules().getSpecification( "Specification" ),
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
        this.getJomcTool().manageSourceFiles( sourcesDirectory );

        sourcesDirectory = this.getTestSourcesDirectory();
        assertTrue( sourcesDirectory.mkdirs() );
        this.getJomcTool().manageSourceFiles( this.getJomcTool().getModules().getModule( "Module" ),
                                              sourcesDirectory );

        final File implementationDirectory = this.getTestSourcesDirectory();
        assertTrue( implementationDirectory.mkdirs() );
        this.getJomcTool().manageSourceFiles( this.getJomcTool().getModules().getImplementation( "Implementation" ),
                                              implementationDirectory );

        this.getJomcTool().manageSourceFiles( this.getJomcTool().getModules().getImplementation( "Implementation" ),
                                              implementationDirectory );

        final File specificationDirectory = this.getTestSourcesDirectory();
        assertTrue( specificationDirectory.mkdirs() );
        this.getJomcTool().manageSourceFiles( this.getJomcTool().getModules().getSpecification( "Specification" ),
                                              specificationDirectory );

        this.getJomcTool().manageSourceFiles( this.getJomcTool().getModules().getSpecification( "Specification" ),
                                              specificationDirectory );

        this.copyResource( "IllegalImplementationSource.java.txt",
                           new File( implementationDirectory, "Implementation.java" ) );

        this.copyResource( "IllegalSpecificationSource.java.txt",
                           new File( specificationDirectory, "Specification.java" ) );

        try
        {
            this.getJomcTool().manageSourceFiles( this.getJomcTool().getModules().getImplementation( "Implementation" ),
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
            this.getJomcTool().manageSourceFiles( this.getJomcTool().getModules().getSpecification( "Specification" ),
                                                  specificationDirectory );

            fail( "Expected IOException not thrown." );
        }
        catch ( final IOException e )
        {
            assertNotNull( e.getMessage() );
            System.out.println( e.toString() );
        }

        this.getJomcTool().setTemplateProfile( "DOES_NOT_EXIST" );

        sourcesDirectory = this.getTestSourcesDirectory();
        assertTrue( sourcesDirectory.mkdirs() );
        this.getJomcTool().manageSourceFiles( sourcesDirectory );

        sourcesDirectory = this.getTestSourcesDirectory();
        assertTrue( sourcesDirectory.mkdirs() );
        this.getJomcTool().manageSourceFiles( this.getJomcTool().getModules().getModule( "Module" ), sourcesDirectory );

        sourcesDirectory = this.getTestSourcesDirectory();
        assertTrue( sourcesDirectory.mkdirs() );
        this.getJomcTool().manageSourceFiles( this.getJomcTool().getModules().getImplementation( "Implementation" ),
                                              sourcesDirectory );

        sourcesDirectory = this.getTestSourcesDirectory();
        assertTrue( sourcesDirectory.mkdirs() );
        this.getJomcTool().manageSourceFiles( this.getJomcTool().getModules().getSpecification( "Specification" ),
                                              sourcesDirectory );

        this.getJomcTool().setInputEncoding( null );
        this.getJomcTool().setOutputEncoding( null );
    }

    @Test
    public final void testMandatorySections() throws Exception
    {
        final SectionEditor editor = new SectionEditor();
        final File specificationDirectory = this.getTestSourcesDirectory();
        final File implementationDirectory = this.getTestSourcesDirectory();

        assertTrue( specificationDirectory.mkdirs() );
        assertTrue( implementationDirectory.mkdirs() );

        this.getJomcTool().setInputEncoding( this.getTestProperty( "resourceEncoding" ) );
        this.getJomcTool().setOutputEncoding( this.getTestProperty( "resourceEncoding" ) );

        File f = new File( implementationDirectory, "Implementation.java" );
        this.copyResource( "ImplementationWithoutAnnotationsSection.java.txt", f );
        this.getJomcTool().manageSourceFiles( this.getJomcTool().getModules().getImplementation( "Implementation" ),
                                              implementationDirectory );

        String edited = this.toString( f );
        editor.edit( edited );
        assertTrue( editor.isSectionPresent( "Annotations" ) );

        f = new File( implementationDirectory, "Implementation.java" );
        this.copyResource( "ImplementationWithoutDependenciesSection.java.txt", f );
        this.getJomcTool().manageSourceFiles( this.getJomcTool().getModules().getImplementation( "Implementation" ),
                                              implementationDirectory );

        edited = this.toString( f );
        editor.edit( edited );
        assertTrue( editor.isSectionPresent( "Dependencies" ) );

        f = new File( implementationDirectory, "Implementation.java" );
        this.copyResource( "ImplementationWithoutMessagesSection.java.txt", f );
        this.getJomcTool().manageSourceFiles( this.getJomcTool().getModules().getImplementation( "Implementation" ),
                                              implementationDirectory );

        edited = this.toString( f );
        editor.edit( edited );
        assertTrue( editor.isSectionPresent( "Messages" ) );

        f = new File( implementationDirectory, "Implementation.java" );
        this.copyResource( "ImplementationWithoutPropertiesSection.java.txt", f );
        this.getJomcTool().manageSourceFiles( this.getJomcTool().getModules().getImplementation( "Implementation" ),
                                              implementationDirectory );

        edited = this.toString( f );
        editor.edit( edited );
        assertTrue( editor.isSectionPresent( "Properties" ) );

        f = new File( implementationDirectory, "ImplementationOfSpecification.java" );
        this.copyResource( "ImplementationOfSpecificationWithoutConstructorsSection.java.txt", f );
        this.getJomcTool().manageSourceFiles(
            this.getJomcTool().getModules().getImplementation( "ImplementationOfSpecification" ),
            implementationDirectory );

        edited = this.toString( f );
        editor.edit( edited );
        assertTrue( editor.isSectionPresent( "Constructors" ) );

        f = new File( specificationDirectory, "Specification.java" );
        this.copyResource( "SpecificationWithoutAnnotationsSection.java.txt", f );
        this.getJomcTool().manageSourceFiles( this.getJomcTool().getModules().getSpecification( "Specification" ),
                                              specificationDirectory );

        edited = this.toString( f );
        editor.edit( edited );
        assertTrue( editor.isSectionPresent( "Annotations" ) );

        this.getJomcTool().setInputEncoding( null );
        this.getJomcTool().setOutputEncoding( null );
    }

    @Test
    public final void testOptionalSections() throws Exception
    {
        final SectionEditor editor = new SectionEditor();
        final File implementationDirectory = this.getTestSourcesDirectory();
        final File specificationDirectory = this.getTestSourcesDirectory();

        assertTrue( specificationDirectory.mkdirs() );
        assertTrue( implementationDirectory.mkdirs() );

        this.getJomcTool().setInputEncoding( this.getTestProperty( "resourceEncoding" ) );
        this.getJomcTool().setOutputEncoding( this.getTestProperty( "resourceEncoding" ) );

        File f = new File( implementationDirectory, "Implementation.java" );
        this.copyResource( "ImplementationWithoutConstructorsSection.java.txt", f );
        this.getJomcTool().manageSourceFiles( this.getJomcTool().getModules().getImplementation( "Implementation" ),
                                              implementationDirectory );

        String edited = this.toString( f );
        editor.edit( edited );
        assertFalse( editor.isSectionPresent( "Constructors" ) );
        this.copyResource( "ImplementationWithoutDefaultConstructorSection.java.txt", f );
        this.getJomcTool().manageSourceFiles( this.getJomcTool().getModules().getImplementation( "Implementation" ),
                                              implementationDirectory );

        edited = this.toString( f );
        editor.edit( edited );
        assertTrue( editor.isSectionPresent( "Constructors" ) );
        assertTrue( editor.isSectionPresent( "Default Constructor" ) );
        this.copyResource( "ImplementationWithoutDocumentationSection.java.txt", f );
        this.getJomcTool().manageSourceFiles( this.getJomcTool().getModules().getImplementation( "Implementation" ),
                                              implementationDirectory );

        edited = this.toString( f );
        editor.edit( edited );
        assertFalse( editor.isSectionPresent( "Documentation" ) );
        this.copyResource( "ImplementationWithoutLicenseSection.java.txt", f );
        this.getJomcTool().manageSourceFiles( this.getJomcTool().getModules().getImplementation( "Implementation" ),
                                              implementationDirectory );

        edited = this.toString( f );
        editor.edit( edited );
        assertFalse( editor.isSectionPresent( "License Header" ) );

        f = new File( specificationDirectory, "Specification.java" );
        this.copyResource( "SpecificationWithoutDocumentationSection.java.txt", f );
        this.getJomcTool().manageSourceFiles( this.getJomcTool().getModules().getSpecification( "Specification" ),
                                              specificationDirectory );

        edited = this.toString( f );
        editor.edit( edited );
        assertFalse( editor.isSectionPresent( "Documentation" ) );
        this.copyResource( "SpecificationWithoutLicenseSection.java.txt", f );
        this.getJomcTool().manageSourceFiles( this.getJomcTool().getModules().getSpecification( "Specification" ),
                                              specificationDirectory );

        edited = this.toString( f );
        editor.edit( edited );
        assertFalse( editor.isSectionPresent( "License Header" ) );

        this.getJomcTool().setInputEncoding( null );
        this.getJomcTool().setOutputEncoding( null );
    }

    @Test
    public final void testCopyConstructor() throws Exception
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

        new SourceFileProcessor( this.getJomcTool() );
    }

    private void copyResource( final String resourceName, final File file ) throws IOException
    {
        InputStream in = null;
        OutputStream out = null;

        try
        {
            in = this.getClass().getResourceAsStream( resourceName );
            assertNotNull( "Resource '" + resourceName + "' not found.", in );

            out = new FileOutputStream( file );
            IOUtils.copy( in, out );
        }
        finally
        {
            IOUtils.closeQuietly( in );
            IOUtils.closeQuietly( out );
        }
    }

    private String toString( final File f ) throws IOException
    {
        InputStream in = null;

        try
        {
            in = new FileInputStream( f );
            return IOUtils.toString( in, this.getTestProperty( "resourceEncoding" ) );
        }
        finally
        {
            IOUtils.closeQuietly( in );
        }
    }

}
