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
package org.jomc.tools.test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import org.jomc.model.Implementation;
import org.jomc.model.Module;
import org.jomc.model.Specification;
import org.jomc.modlet.Model;
import org.jomc.tools.SourceFileProcessor;
import org.jomc.util.SectionEditor;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Test cases for class {@code org.jomc.tools.SourceFileProcessor} implementations.
 *
 * @author <a href="mailto:cs@schulte.it">Christian Schulte</a>
 * @version $JOMC$
 */
public class SourceFileProcessorTest extends JomcToolTest
{

    /**
     * Constant to prefix relative resource names with.
     */
    private static final String ABSOLUTE_RESOURCE_NAME_PREFIX =
        "/" + SourceFileProcessorTest.class.getPackage().getName().replace( '.', '/' ) + "/";

    /**
     * Creates a new {@code SourceFileProcessorTest} instance.
     */
    public SourceFileProcessorTest()
    {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceFileProcessor getJomcTool()
    {
        return (SourceFileProcessor) super.getJomcTool();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected SourceFileProcessor newJomcTool()
    {
        return new SourceFileProcessor();
    }

    @Test
    public final void testSourceFileProcessorNullPointerException() throws Exception
    {
        try
        {
            this.getJomcTool().getSourceFilesType( (Specification) null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getJomcTool().getSourceFilesType( (Implementation) null );
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
        assertNotNull( this.getJomcTool().getSourceFilesType(
            this.getJomcTool().getModules().getImplementation( "Implementation" ) ) );

        assertNotNull( this.getJomcTool().getSourceFilesType(
            this.getJomcTool().getModules().getSpecification( "Specification" ) ) );

        assertNotNull( this.getJomcTool().getSourceFileEditor() );
    }

    @Test
    public final void testManageSources() throws Exception
    {
        this.getJomcTool().setInputEncoding( this.getResourceEncoding() );
        this.getJomcTool().setOutputEncoding( this.getResourceEncoding() );

        final File nonExistingDirectory = this.getNextOutputDirectory();

        try
        {
            this.getJomcTool().manageSourceFiles( nonExistingDirectory );
            fail( "Expected IOException not thrown." );
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

            fail( "Expected IOException not thrown." );
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

            fail( "Expected IOException not thrown." );
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

            fail( "Expected IOException not thrown." );
        }
        catch ( final IOException e )
        {
            assertNotNull( e.getMessage() );
            System.out.println( e );
        }

        File sourcesDirectory = this.getNextOutputDirectory();
        assertTrue( sourcesDirectory.mkdirs() );
        this.getJomcTool().manageSourceFiles( sourcesDirectory );
        this.getJomcTool().manageSourceFiles( sourcesDirectory );

        sourcesDirectory = this.getNextOutputDirectory();
        assertTrue( sourcesDirectory.mkdirs() );
        this.getJomcTool().manageSourceFiles( this.getJomcTool().getModules().getModule( "Module" ),
                                              sourcesDirectory );

        this.getJomcTool().manageSourceFiles( this.getJomcTool().getModules().getModule( "Module" ),
                                              sourcesDirectory );

        final File implementationDirectory = this.getNextOutputDirectory();
        final File implementationSourceFile = new File( implementationDirectory, "Implementation.java" );
        assertTrue( implementationDirectory.mkdirs() );
        long implementationSourceFileLength;

        this.getJomcTool().manageSourceFiles( this.getJomcTool().getModules().getImplementation( "Implementation" ),
                                              implementationDirectory );

        implementationSourceFileLength = implementationSourceFile.length();
        assertTrue( implementationSourceFile.exists() );

        this.getJomcTool().manageSourceFiles( this.getJomcTool().getModules().getImplementation( "Implementation" ),
                                              implementationDirectory );

        assertTrue( implementationSourceFile.exists() );
        assertEquals( implementationSourceFileLength, implementationSourceFile.length() );

        this.getJomcTool().getTemplateParameters().put( "with-assertions", Boolean.FALSE );
        this.getJomcTool().getTemplateParameters().put( "with-author-copyright", Boolean.FALSE );
        this.getJomcTool().getTemplateParameters().put( "with-editor-fold", Boolean.FALSE );
        this.getJomcTool().getTemplateParameters().put( "with-javadoc", Boolean.FALSE );
        this.getJomcTool().getTemplateParameters().put( "with-javadoc-author", Boolean.FALSE );
        this.getJomcTool().getTemplateParameters().put( "with-javadoc-version", Boolean.FALSE );
        this.getJomcTool().getTemplateParameters().put( "with-jsr-250", Boolean.FALSE );
        this.getJomcTool().getTemplateParameters().put( "with-project-name", null );
        this.getJomcTool().getTemplateParameters().put( "with-revision-keyword", null );
        this.getJomcTool().getTemplateParameters().put( "with-suppress-warnings", null );
        this.getJomcTool().getTemplateParameters().put( "with-vendor-copyright", null );

        this.getJomcTool().manageSourceFiles( this.getJomcTool().getModules().getImplementation( "Implementation" ),
                                              implementationDirectory );

        assertTrue( implementationSourceFile.exists() );
        assertTrue( implementationSourceFile.length() < implementationSourceFileLength );

        this.getJomcTool().getTemplateParameters().clear();

        this.getJomcTool().setTemplateProfile( "jomc-java-bundles" );

        this.getJomcTool().manageSourceFiles( this.getJomcTool().getModules().getImplementation( "Implementation" ),
                                              implementationDirectory );

        this.getJomcTool().getTemplateParameters().put( "with-assertions", Boolean.FALSE );
        this.getJomcTool().getTemplateParameters().put( "with-author-copyright", Boolean.FALSE );
        this.getJomcTool().getTemplateParameters().put( "with-editor-fold", Boolean.FALSE );
        this.getJomcTool().getTemplateParameters().put( "with-javadoc", Boolean.FALSE );
        this.getJomcTool().getTemplateParameters().put( "with-javadoc-author", Boolean.FALSE );
        this.getJomcTool().getTemplateParameters().put( "with-javadoc-version", Boolean.FALSE );
        this.getJomcTool().getTemplateParameters().put( "with-jsr-250", Boolean.FALSE );
        this.getJomcTool().getTemplateParameters().put( "with-project-name", null );
        this.getJomcTool().getTemplateParameters().put( "with-revision-keyword", null );
        this.getJomcTool().getTemplateParameters().put( "with-suppress-warnings", null );
        this.getJomcTool().getTemplateParameters().put( "with-vendor-copyright", null );

        this.getJomcTool().manageSourceFiles( this.getJomcTool().getModules().getImplementation( "Implementation" ),
                                              implementationDirectory );

        this.getJomcTool().getTemplateParameters().clear();

        this.getJomcTool().setTemplateProfile( null );

        this.getJomcTool().manageSourceFiles(
            this.getJomcTool().getModules().getImplementation( "ImplementationWithSourceFilesModel" ),
            implementationDirectory );

        this.getJomcTool().manageSourceFiles(
            this.getJomcTool().getModules().getImplementation( "ImplementationWithSourceFilesModel" ),
            implementationDirectory );

        final File specificationDirectory = this.getNextOutputDirectory();
        assertTrue( specificationDirectory.mkdirs() );
        this.getJomcTool().manageSourceFiles( this.getJomcTool().getModules().getSpecification( "Specification" ),
                                              specificationDirectory );

        this.getJomcTool().getTemplateParameters().put( "with-assertions", Boolean.FALSE );
        this.getJomcTool().getTemplateParameters().put( "with-author-copyright", Boolean.FALSE );
        this.getJomcTool().getTemplateParameters().put( "with-editor-fold", Boolean.FALSE );
        this.getJomcTool().getTemplateParameters().put( "with-javadoc", Boolean.FALSE );
        this.getJomcTool().getTemplateParameters().put( "with-javadoc-author", Boolean.FALSE );
        this.getJomcTool().getTemplateParameters().put( "with-javadoc-version", Boolean.FALSE );
        this.getJomcTool().getTemplateParameters().put( "with-jsr-250", Boolean.FALSE );
        this.getJomcTool().getTemplateParameters().put( "with-project-name", null );
        this.getJomcTool().getTemplateParameters().put( "with-revision-keyword", null );
        this.getJomcTool().getTemplateParameters().put( "with-suppress-warnings", null );
        this.getJomcTool().getTemplateParameters().put( "with-vendor-copyright", null );

        this.getJomcTool().manageSourceFiles( this.getJomcTool().getModules().getSpecification( "Specification" ),
                                              specificationDirectory );

        this.getJomcTool().getTemplateParameters().clear();

        this.getJomcTool().manageSourceFiles(
            this.getJomcTool().getModules().getSpecification( "SpecificationWithSourceFilesModel" ),
            specificationDirectory );

        this.getJomcTool().manageSourceFiles(
            this.getJomcTool().getModules().getSpecification( "SpecificationWithSourceFilesModel" ),
            specificationDirectory );

        this.copyResource( ABSOLUTE_RESOURCE_NAME_PREFIX + "IllegalImplementationSource.java.txt",
                           new File( implementationDirectory, "Implementation.java" ) );

        this.copyResource( ABSOLUTE_RESOURCE_NAME_PREFIX + "IllegalSpecificationSource.java.txt",
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

        this.copyResource( ABSOLUTE_RESOURCE_NAME_PREFIX + "EmptyImplementationSource.java.txt",
                           new File( implementationDirectory, "Implementation.java" ) );

        this.copyResource( ABSOLUTE_RESOURCE_NAME_PREFIX + "EmptySpecificationSource.java.txt",
                           new File( specificationDirectory, "Specification.java" ) );

        this.getJomcTool().manageSourceFiles( this.getJomcTool().getModules().getImplementation( "Implementation" ),
                                              implementationDirectory );

        this.getJomcTool().manageSourceFiles( this.getJomcTool().getModules().getSpecification( "Specification" ),
                                              specificationDirectory );

        this.getJomcTool().setTemplateProfile( "DOES_NOT_EXIST" );

        sourcesDirectory = this.getNextOutputDirectory();
        assertTrue( sourcesDirectory.mkdirs() );
        this.getJomcTool().manageSourceFiles( sourcesDirectory );

        sourcesDirectory = this.getNextOutputDirectory();
        assertTrue( sourcesDirectory.mkdirs() );
        this.getJomcTool().manageSourceFiles( this.getJomcTool().getModules().getModule( "Module" ), sourcesDirectory );

        sourcesDirectory = this.getNextOutputDirectory();
        assertTrue( sourcesDirectory.mkdirs() );
        this.getJomcTool().manageSourceFiles( this.getJomcTool().getModules().getImplementation( "Implementation" ),
                                              sourcesDirectory );

        sourcesDirectory = this.getNextOutputDirectory();
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
        final File specificationDirectory = this.getNextOutputDirectory();
        final File implementationDirectory = this.getNextOutputDirectory();

        assertTrue( specificationDirectory.mkdirs() );
        assertTrue( implementationDirectory.mkdirs() );

        this.getJomcTool().setInputEncoding( this.getResourceEncoding() );
        this.getJomcTool().setOutputEncoding( this.getResourceEncoding() );

        File f = new File( implementationDirectory, "Implementation.java" );
        this.copyResource( ABSOLUTE_RESOURCE_NAME_PREFIX + "ImplementationWithoutAnnotationsSection.java.txt", f );
        this.getJomcTool().manageSourceFiles( this.getJomcTool().getModules().getImplementation( "Implementation" ),
                                              implementationDirectory );

        String edited = this.toString( f );
        editor.edit( edited );
        assertTrue( editor.isSectionPresent( "Annotations" ) );

        f = new File( implementationDirectory, "Implementation.java" );
        this.copyResource( ABSOLUTE_RESOURCE_NAME_PREFIX + "ImplementationWithoutDependenciesSection.java.txt", f );
        this.getJomcTool().manageSourceFiles( this.getJomcTool().getModules().getImplementation( "Implementation" ),
                                              implementationDirectory );

        edited = this.toString( f );
        editor.edit( edited );
        assertTrue( editor.isSectionPresent( "Dependencies" ) );

        f = new File( implementationDirectory, "Implementation.java" );
        this.copyResource( ABSOLUTE_RESOURCE_NAME_PREFIX + "ImplementationWithoutMessagesSection.java.txt", f );
        this.getJomcTool().manageSourceFiles( this.getJomcTool().getModules().getImplementation( "Implementation" ),
                                              implementationDirectory );

        edited = this.toString( f );
        editor.edit( edited );
        assertTrue( editor.isSectionPresent( "Messages" ) );

        f = new File( implementationDirectory, "Implementation.java" );
        this.copyResource( ABSOLUTE_RESOURCE_NAME_PREFIX + "ImplementationWithoutPropertiesSection.java.txt", f );
        this.getJomcTool().manageSourceFiles( this.getJomcTool().getModules().getImplementation( "Implementation" ),
                                              implementationDirectory );

        edited = this.toString( f );
        editor.edit( edited );
        assertTrue( editor.isSectionPresent( "Properties" ) );

        f = new File( implementationDirectory, "ImplementationOfSpecification.java" );
        this.copyResource( ABSOLUTE_RESOURCE_NAME_PREFIX
                               + "ImplementationOfSpecificationWithoutConstructorsSection.java.txt", f );

        this.getJomcTool().manageSourceFiles(
            this.getJomcTool().getModules().getImplementation( "ImplementationOfSpecification" ),
            implementationDirectory );

        edited = this.toString( f );
        editor.edit( edited );
        assertTrue( editor.isSectionPresent( "Constructors" ) );

        f = new File( specificationDirectory, "Specification.java" );
        this.copyResource( ABSOLUTE_RESOURCE_NAME_PREFIX + "SpecificationWithoutAnnotationsSection.java.txt", f );
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
        final File implementationDirectory = this.getNextOutputDirectory();
        final File specificationDirectory = this.getNextOutputDirectory();

        assertTrue( specificationDirectory.mkdirs() );
        assertTrue( implementationDirectory.mkdirs() );

        this.getJomcTool().setInputEncoding( this.getResourceEncoding() );
        this.getJomcTool().setOutputEncoding( this.getResourceEncoding() );

        File f = new File( implementationDirectory, "Implementation.java" );
        this.copyResource( ABSOLUTE_RESOURCE_NAME_PREFIX + "ImplementationWithoutConstructorsSection.java.txt", f );
        this.getJomcTool().manageSourceFiles( this.getJomcTool().getModules().getImplementation( "Implementation" ),
                                              implementationDirectory );

        String edited = this.toString( f );
        editor.edit( edited );
        assertFalse( editor.isSectionPresent( "Constructors" ) );
        this.copyResource( ABSOLUTE_RESOURCE_NAME_PREFIX + "ImplementationWithoutDefaultConstructorSection.java.txt",
                           f );

        this.getJomcTool().manageSourceFiles( this.getJomcTool().getModules().getImplementation( "Implementation" ),
                                              implementationDirectory );

        edited = this.toString( f );
        editor.edit( edited );
        assertTrue( editor.isSectionPresent( "Constructors" ) );
        assertTrue( editor.isSectionPresent( "Default Constructor" ) );
        this.copyResource( ABSOLUTE_RESOURCE_NAME_PREFIX + "ImplementationWithoutDocumentationSection.java.txt", f );
        this.getJomcTool().manageSourceFiles( this.getJomcTool().getModules().getImplementation( "Implementation" ),
                                              implementationDirectory );

        edited = this.toString( f );
        editor.edit( edited );
        assertFalse( editor.isSectionPresent( "Documentation" ) );
        this.copyResource( ABSOLUTE_RESOURCE_NAME_PREFIX + "ImplementationWithoutLicenseSection.java.txt", f );
        this.getJomcTool().manageSourceFiles( this.getJomcTool().getModules().getImplementation( "Implementation" ),
                                              implementationDirectory );

        edited = this.toString( f );
        editor.edit( edited );
        assertFalse( editor.isSectionPresent( "License Header" ) );

        f = new File( specificationDirectory, "Specification.java" );
        this.copyResource( ABSOLUTE_RESOURCE_NAME_PREFIX + "SpecificationWithoutDocumentationSection.java.txt", f );
        this.getJomcTool().manageSourceFiles( this.getJomcTool().getModules().getSpecification( "Specification" ),
                                              specificationDirectory );

        edited = this.toString( f );
        editor.edit( edited );
        assertFalse( editor.isSectionPresent( "Documentation" ) );
        this.copyResource( ABSOLUTE_RESOURCE_NAME_PREFIX + "SpecificationWithoutLicenseSection.java.txt", f );
        this.getJomcTool().manageSourceFiles( this.getJomcTool().getModules().getSpecification( "Specification" ),
                                              specificationDirectory );

        edited = this.toString( f );
        editor.edit( edited );
        assertFalse( editor.isSectionPresent( "License Header" ) );

        this.getJomcTool().setInputEncoding( null );
        this.getJomcTool().setOutputEncoding( null );
    }

    @Test
    public final void testSourceFileEditor() throws Exception
    {
        assertNotNull( this.getJomcTool().getSourceFileEditor() );
        this.getJomcTool().setSourceFileEditor( null );
        assertNotNull( this.getJomcTool().getSourceFileEditor() );
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

    @Test
    public final void testSourceFileProcessorModelObjectsNotFound() throws Exception
    {
        final File tmpDir = new File( System.getProperty( "java.io.tmpdir", "/tmp" ) );
        final Module m = new Module();
        m.setName( "DOES_NOT_EXIST" );

        final Specification s = new Specification();
        s.setIdentifier( "DOES_NOT_EXIST)" );

        final Implementation i = new Implementation();
        i.setIdentifier( "DOES_NOT_EXIST" );

        final Model oldModel = this.getJomcTool().getModel();
        this.getJomcTool().setModel( null );

        assertNull( this.getJomcTool().getSourceFilesType( s ) );
        assertNull( this.getJomcTool().getSourceFilesType( i ) );

        this.getJomcTool().manageSourceFiles( tmpDir );
        this.getJomcTool().manageSourceFiles( m, tmpDir );
        this.getJomcTool().manageSourceFiles( s, tmpDir );
        this.getJomcTool().manageSourceFiles( i, tmpDir );

        this.getJomcTool().setModel( oldModel );
    }

    private void copyResource( final String resourceName, final File file ) throws IOException
    {
        assertTrue( resourceName.startsWith( "/" ) );

        try ( final InputStream in = this.getClass().getResourceAsStream( resourceName ) )
        {
            Files.copy( in, file.toPath(), StandardCopyOption.REPLACE_EXISTING );
        }
    }

    private String toString( final File f ) throws IOException
    {
        final List<String> lines = Files.readAllLines( f.toPath(), Charset.forName( this.getResourceEncoding() ) );

        try ( final StringWriter stringWriter = new StringWriter();
              final BufferedWriter bufferedWriter = new BufferedWriter( stringWriter ) )
        {
            for ( final String line : lines )
            {
                bufferedWriter.append( line );
                bufferedWriter.newLine();
            }

            bufferedWriter.flush();
            return stringWriter.toString();
        }
    }

}
