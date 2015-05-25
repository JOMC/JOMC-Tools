/*
 *   Copyright (C) Christian Schulte, 2005-206
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
package org.jomc.tools.modlet.test;

import org.jomc.model.Implementation;
import org.jomc.model.Implementations;
import org.jomc.model.ModelObject;
import org.jomc.model.Module;
import org.jomc.model.Modules;
import org.jomc.model.Specification;
import org.jomc.model.Specifications;
import org.jomc.model.modlet.ModelHelper;
import org.jomc.modlet.Model;
import org.jomc.modlet.ModelContext;
import org.jomc.modlet.ModelContextFactory;
import org.jomc.tools.model.SourceFileType;
import org.jomc.tools.model.SourceFilesType;
import org.jomc.tools.model.SourceSectionType;
import org.jomc.tools.model.SourceSectionsType;
import org.jomc.tools.modlet.ToolsModelProcessor;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Test cases for class {@code org.jomc.tools.modlet.ToolsModelProcessor}.
 *
 * @author <a href="mailto:cs@schulte.it">Christian Schulte</a> 1.0
 * @version $JOMC$
 */
public class ToolsModelProcessorTest
{

    /**
     * The {@code ToolsModelProcessor} instance tests are performed with.
     */
    private ToolsModelProcessor toolsModelProcessor;

    /**
     * Creates a new {@code ToolsModelProcessorTest} instance.
     */
    public ToolsModelProcessorTest()
    {
        super();
    }

    /**
     * Gets the {@code ToolsModelProcessor} instance tests are performed with.
     *
     * @return The {@code ToolsModelProcessor} instance tests are performed with.
     *
     * @see #newModelProcessor()
     */
    public ToolsModelProcessor getModelProcessor()
    {
        if ( this.toolsModelProcessor == null )
        {
            this.toolsModelProcessor = this.newModelProcessor();
        }

        return this.toolsModelProcessor;
    }

    /**
     * Creates a new {@code ToolsModelProcessor} instance to test.
     *
     * @return A new {@code ToolsModelProcessor} instance to test.
     *
     * @see #getModelProcessor()
     */
    protected ToolsModelProcessor newModelProcessor()
    {
        return new ToolsModelProcessor();
    }

    @Test
    public final void testProcessModel() throws Exception
    {
        final ModelContext context = ModelContextFactory.newInstance().newModelContext();
        Model model = new Model();
        model.setIdentifier( ModelObject.MODEL_PUBLIC_ID );

        Modules modules = new Modules();
        Module module = new Module();
        module.setName( this.getClass().getName() );
        module.setSpecifications( new Specifications() );
        module.setImplementations( new Implementations() );

        Specification specification = new Specification();
        specification.setClassDeclaration( true );
        specification.setClazz( this.getClass().getName() );
        specification.setIdentifier( this.getClass().getName() + " Specification" );

        Implementation implementation = new Implementation();
        implementation.setClassDeclaration( true );
        implementation.setClazz( this.getClass().getName() );
        implementation.setIdentifier( this.getClass().getName() + " Implementation" );
        implementation.setName( this.getClass().getName() + " Implementation" );

        module.getSpecifications().getSpecification().add( specification );
        module.getImplementations().getImplementation().add( implementation );
        modules.getModule().add( module );

        ModelHelper.setModules( model, modules );

        try
        {
            this.getModelProcessor().processModel( null, model );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNotNull( e.getMessage() );
            System.out.println( e.toString() );
        }

        try
        {
            this.getModelProcessor().processModel( context, null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNotNull( e.getMessage() );
            System.out.println( e.toString() );
        }

        Model processed = this.getModelProcessor().processModel( context, model );
        assertNotNull( processed );

        modules = ModelHelper.getModules( processed );
        assertNotNull( modules );

        specification = modules.getSpecification( this.getClass().getName() + " Specification" );
        assertNotNull( specification );

        implementation = modules.getImplementation( this.getClass().getName() + " Implementation" );
        assertNotNull( implementation );

        SourceFileType ss = specification.getAnyObject( SourceFileType.class );
        assertNull( ss );

        SourceFileType is = implementation.getAnyObject( SourceFileType.class );
        assertNull( is );

        SourceFilesType specificationSourceFiles = new SourceFilesType();
        ss = new SourceFileType();
        ss.setIdentifier( this.getClass().getSimpleName() );
        ss.setSourceSections( new SourceSectionsType() );
        specificationSourceFiles.getSourceFile().add( ss );
        specification.getAny().add( specificationSourceFiles );

        SourceFilesType implementationSourceFiles = new SourceFilesType();
        is = new SourceFileType();
        is.setIdentifier( this.getClass().getSimpleName() );
        is.setSourceSections( new SourceSectionsType() );
        implementationSourceFiles.getSourceFile().add( is );
        implementation.getAny().add( implementationSourceFiles );

        SourceSectionType sourceSection = new SourceSectionType();
        sourceSection.setName( "License Header" );

        ss.getSourceSections().getSourceSection().add( sourceSection );
        is.getSourceSections().getSourceSection().add( sourceSection );

        sourceSection = new SourceSectionType();
        sourceSection.setName( "Annotations" );

        ss.getSourceSections().getSourceSection().add( sourceSection );
        is.getSourceSections().getSourceSection().add( sourceSection );

        sourceSection = new SourceSectionType();
        sourceSection.setName( "Documentation" );

        ss.getSourceSections().getSourceSection().add( sourceSection );
        is.getSourceSections().getSourceSection().add( sourceSection );

        sourceSection = new SourceSectionType();
        sourceSection.setName( this.getClass().getSimpleName() );

        ss.getSourceSections().getSourceSection().add( sourceSection );
        is.getSourceSections().getSourceSection().add( sourceSection );

        sourceSection = new SourceSectionType();
        sourceSection.setName( "Constructors" );

        is.getSourceSections().getSourceSection().add( sourceSection );

        sourceSection = new SourceSectionType();
        sourceSection.setName( "Default Constructor" );

        is.getSourceSections().getSourceSection().add( sourceSection );

        sourceSection = new SourceSectionType();
        sourceSection.setName( "Dependencies" );

        is.getSourceSections().getSourceSection().add( sourceSection );

        sourceSection = new SourceSectionType();
        sourceSection.setName( "Properties" );

        is.getSourceSections().getSourceSection().add( sourceSection );

        sourceSection = new SourceSectionType();
        sourceSection.setName( "Messages" );

        is.getSourceSections().getSourceSection().add( sourceSection );

        processed = this.getModelProcessor().processModel( context, processed );
        assertNotNull( processed );

        modules = ModelHelper.getModules( processed );
        assertNotNull( modules );

        specification = modules.getSpecification( this.getClass().getName() + " Specification" );
        assertNotNull( specification );

        implementation = modules.getImplementation( this.getClass().getName() + " Implementation" );
        assertNotNull( implementation );

        specificationSourceFiles = specification.getAnyObject( SourceFilesType.class );
        assertNotNull( specificationSourceFiles );

        ss = specificationSourceFiles.getSourceFile( this.getClass().getSimpleName() );
        assertNotNull( ss );
        assertNotNull( ss.getHeadComment() );
        assertNotNull( ss.getLocation() );
        assertNotNull( ss.getTemplate() );

        implementationSourceFiles = implementation.getAnyObject( SourceFilesType.class );
        assertNotNull( implementationSourceFiles );
        is = implementationSourceFiles.getSourceFile( this.getClass().getSimpleName() );
        assertNotNull( is );
        assertNotNull( is.getHeadComment() );
        assertNotNull( is.getLocation() );
        assertNotNull( is.getTemplate() );

        sourceSection = ss.getSourceSections().getSourceSection( "License Header" );
        assertNotNull( sourceSection );
        assertTrue( sourceSection.isOptional() );
        assertNotNull( sourceSection.getHeadTemplate() );

        sourceSection = is.getSourceSections().getSourceSection( "License Header" );
        assertNotNull( sourceSection );
        assertTrue( sourceSection.isOptional() );
        assertNotNull( sourceSection.getHeadTemplate() );

        sourceSection = ss.getSourceSections().getSourceSection( "Annotations" );
        assertNotNull( sourceSection );
        assertNotNull( sourceSection.getHeadTemplate() );

        sourceSection = is.getSourceSections().getSourceSection( "Annotations" );
        assertNotNull( sourceSection );
        assertNotNull( sourceSection.getHeadTemplate() );

        sourceSection = ss.getSourceSections().getSourceSection( "Documentation" );
        assertNotNull( sourceSection );
        assertTrue( sourceSection.isOptional() );
        assertNotNull( sourceSection.getHeadTemplate() );

        sourceSection = is.getSourceSections().getSourceSection( "Documentation" );
        assertNotNull( sourceSection );
        assertTrue( sourceSection.isOptional() );
        assertNotNull( sourceSection.getHeadTemplate() );

        sourceSection = ss.getSourceSections().getSourceSection( this.getClass().getSimpleName() );
        assertNotNull( sourceSection );
        assertTrue( sourceSection.isEditable() );
        assertEquals( 1, sourceSection.getIndentationLevel() );

        sourceSection = is.getSourceSections().getSourceSection( this.getClass().getSimpleName() );
        assertNotNull( sourceSection );
        assertTrue( sourceSection.isEditable() );
        assertEquals( 1, sourceSection.getIndentationLevel() );

        sourceSection = is.getSourceSections().getSourceSection( "Constructors" );
        assertNotNull( sourceSection );
        assertNotNull( sourceSection.getHeadTemplate() );
        assertNotNull( sourceSection.getTailTemplate() );
        assertEquals( 1, sourceSection.getIndentationLevel() );
        assertTrue( sourceSection.isOptional() );

        sourceSection = is.getSourceSections().getSourceSection( "Default Constructor" );
        assertNotNull( sourceSection );
        assertNotNull( sourceSection.getHeadTemplate() );
        assertEquals( 2, sourceSection.getIndentationLevel() );
        assertTrue( sourceSection.isEditable() );

        sourceSection = is.getSourceSections().getSourceSection( "Dependencies" );
        assertNotNull( sourceSection );
        assertNotNull( sourceSection.getHeadTemplate() );
        assertEquals( 1, sourceSection.getIndentationLevel() );
        assertTrue( sourceSection.isOptional() );

        sourceSection = is.getSourceSections().getSourceSection( "Properties" );
        assertNotNull( sourceSection );
        assertNotNull( sourceSection.getHeadTemplate() );
        assertEquals( 1, sourceSection.getIndentationLevel() );
        assertTrue( sourceSection.isOptional() );

        sourceSection = is.getSourceSections().getSourceSection( "Messages" );
        assertNotNull( sourceSection );
        assertNotNull( sourceSection.getHeadTemplate() );
        assertEquals( 1, sourceSection.getIndentationLevel() );
        assertTrue( sourceSection.isOptional() );

        sourceSection = is.getSourceSections().getSourceSection( this.getClass().getSimpleName() );
        assertNotNull( sourceSection );
        assertEquals( 1, sourceSection.getIndentationLevel() );
        assertTrue( sourceSection.isEditable() );
    }

    @Test
    public final void testDefaultEnabled() throws Exception
    {
        System.clearProperty( "org.jomc.tools.modlet.ToolsModelProcessor.defaultEnabled" );
        ToolsModelProcessor.setDefaultEnabled( null );
        assertTrue( ToolsModelProcessor.isDefaultEnabled() );

        System.setProperty( "org.jomc.tools.modlet.ToolsModelProcessor.defaultEnabled", Boolean.toString( false ) );
        ToolsModelProcessor.setDefaultEnabled( null );
        assertFalse( ToolsModelProcessor.isDefaultEnabled() );
        System.clearProperty( "org.jomc.tools.modlet.ToolsModelProcessor.defaultEnabled" );
        ToolsModelProcessor.setDefaultEnabled( null );
        assertTrue( ToolsModelProcessor.isDefaultEnabled() );

        System.setProperty( "org.jomc.tools.modlet.ToolsModelProcessor.defaultEnabled", Boolean.toString( true ) );
        ToolsModelProcessor.setDefaultEnabled( null );
        assertTrue( ToolsModelProcessor.isDefaultEnabled() );
        System.clearProperty( "org.jomc.tools.modlet.ToolsModelProcessor.defaultEnabled" );
        ToolsModelProcessor.setDefaultEnabled( null );
        assertTrue( ToolsModelProcessor.isDefaultEnabled() );
    }

    @Test
    public final void testEnabled() throws Exception
    {
        final Model model = new Model();
        model.setIdentifier( ModelObject.MODEL_PUBLIC_ID );

        ToolsModelProcessor.setDefaultEnabled( null );
        this.getModelProcessor().setEnabled( null );
        assertTrue( this.getModelProcessor().isEnabled() );

        this.getModelProcessor().processModel( ModelContextFactory.newInstance().newModelContext(), model );
        ToolsModelProcessor.setDefaultEnabled( false );
        this.getModelProcessor().setEnabled( null );
        assertFalse( this.getModelProcessor().isEnabled() );

        this.getModelProcessor().processModel( ModelContextFactory.newInstance().newModelContext(), model );
        ToolsModelProcessor.setDefaultEnabled( null );
        this.getModelProcessor().setEnabled( null );
    }

    @Test
    public final void testDefaultHeadComment() throws Exception
    {
        System.clearProperty( "org.jomc.tools.modlet.ToolsModelProcessor.defaultHeadComment" );
        ToolsModelProcessor.setDefaultHeadComment( null );
        assertEquals( "//", ToolsModelProcessor.getDefaultHeadComment() );

        System.setProperty( "org.jomc.tools.modlet.ToolsModelProcessor.defaultHeadComment", "/*" );
        ToolsModelProcessor.setDefaultHeadComment( null );
        assertEquals( "/*", ToolsModelProcessor.getDefaultHeadComment() );
        System.clearProperty( "org.jomc.tools.modlet.ToolsModelProcessor.defaultHeadComment" );
        ToolsModelProcessor.setDefaultHeadComment( null );
        assertEquals( "//", ToolsModelProcessor.getDefaultHeadComment() );
    }

    @Test
    public final void testHeadComment() throws Exception
    {
        ToolsModelProcessor.setDefaultHeadComment( null );
        this.getModelProcessor().setHeadComment( null );
        assertEquals( "//", this.getModelProcessor().getHeadComment() );

        ToolsModelProcessor.setDefaultHeadComment( "/*" );
        this.getModelProcessor().setHeadComment( null );
        assertEquals( "/*", this.getModelProcessor().getHeadComment() );

        ToolsModelProcessor.setDefaultHeadComment( null );
        this.getModelProcessor().setHeadComment( null );
        assertEquals( "//", this.getModelProcessor().getHeadComment() );
    }

    @Test
    public final void testDefaultTailComment() throws Exception
    {
        System.clearProperty( "org.jomc.tools.modlet.ToolsModelProcessor.defaultTailComment" );
        ToolsModelProcessor.setDefaultTailComment( null );
        assertNull( ToolsModelProcessor.getDefaultTailComment() );

        System.setProperty( "org.jomc.tools.modlet.ToolsModelProcessor.defaultTailComment", "*/" );
        ToolsModelProcessor.setDefaultTailComment( null );
        assertEquals( "*/", ToolsModelProcessor.getDefaultTailComment() );

        System.clearProperty( "org.jomc.tools.modlet.ToolsModelProcessor.defaultTailComment" );
        ToolsModelProcessor.setDefaultTailComment( null );
        assertNull( ToolsModelProcessor.getDefaultTailComment() );
    }

    @Test
    public final void testTailComment() throws Exception
    {
        ToolsModelProcessor.setDefaultTailComment( null );
        this.getModelProcessor().setTailComment( null );
        assertNull( this.getModelProcessor().getTailComment() );

        ToolsModelProcessor.setDefaultTailComment( "*/" );
        this.getModelProcessor().setTailComment( null );
        assertEquals( "*/", this.getModelProcessor().getTailComment() );

        ToolsModelProcessor.setDefaultTailComment( null );
        this.getModelProcessor().setTailComment( null );
        assertNull( this.getModelProcessor().getTailComment() );
    }

}
