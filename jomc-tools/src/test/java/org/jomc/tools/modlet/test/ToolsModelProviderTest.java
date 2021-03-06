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
package org.jomc.tools.modlet.test;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import org.jomc.model.Implementation;
import org.jomc.model.Implementations;
import org.jomc.model.ModelObject;
import org.jomc.model.Module;
import org.jomc.model.Modules;
import org.jomc.model.Specification;
import org.jomc.model.SpecificationReference;
import org.jomc.model.Specifications;
import org.jomc.model.modlet.ModelHelper;
import org.jomc.modlet.Model;
import org.jomc.modlet.ModelContext;
import org.jomc.modlet.ModelContextFactory;
import org.jomc.tools.model.SourceFileType;
import org.jomc.tools.model.SourceFilesType;
import org.jomc.tools.model.SourceSectionType;
import org.jomc.tools.model.SourceSectionsType;
import org.jomc.tools.modlet.ToolsModelProvider;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Test cases for class {@code org.jomc.tools.modlet.ToolsModelProvider}.
 *
 * @author <a href="mailto:cs@schulte.it">Christian Schulte</a> 1.0
 * @version $JOMC$
 */
public class ToolsModelProviderTest
{

    /**
     * The {@code ToolsModelProvider} instance tests are performed with.
     */
    private ToolsModelProvider toolsModelProvider;

    /**
     * Creates a new {@code ToolsModelProviderTest} instance.
     */
    public ToolsModelProviderTest()
    {
        super();
    }

    /**
     * Gets the {@code ToolsModelProvider} instance tests are performed with.
     *
     * @return The {@code ToolsModelProvider} instance tests are performed with.
     *
     * @see #newModelProvider()
     */
    public ToolsModelProvider getModelProvider()
    {
        if ( this.toolsModelProvider == null )
        {
            this.toolsModelProvider = this.newModelProvider();
        }

        return this.toolsModelProvider;
    }

    /**
     * Creates a new {@code ToolsModelProvider} instance to test.
     *
     * @return A new {@code ToolsModelProvider} instance to test.
     *
     * @see #getModelProvider()
     */
    protected ToolsModelProvider newModelProvider()
    {
        return new ToolsModelProvider();
    }

    @Test
    public final void testFindModel() throws Exception
    {
        final ModelContext context = ModelContextFactory.newInstance().newModelContext();
        context.setLogLevel( Level.ALL );
        context.getListeners().add( new ModelContext.Listener()
        {

            @Override
            public void onLog( final Level level, final String message, final Throwable t )
            {
                super.onLog( level, message, t );
                System.out.println( "[" + level.getLocalizedName() + "] " + message );

                if ( t != null )
                {
                    t.printStackTrace( System.out );
                }
            }

        } );

        Model model = new Model();
        model.setIdentifier( ModelObject.MODEL_PUBLIC_ID );

        Modules modules = new Modules();
        Module module = new Module();
        module.setName( this.getClass().getName() );
        module.setSpecifications( new Specifications() );
        module.setImplementations( new Implementations() );

        Specification specification = new Specification();
        specification.setClassDeclaration( true );
        specification.setClazz( "specification.Documentation" );
        specification.setIdentifier( this.getClass().getName() + " Specification" );

        Implementation implementation = new Implementation();
        implementation.setClassDeclaration( true );
        implementation.setClazz( "implementation.Documentation" );
        implementation.setIdentifier( this.getClass().getName() + " Implementation" );
        implementation.setName( this.getClass().getName() + " Implementation" );
        implementation.setSpecifications( new Specifications() );
        implementation.getSpecifications().getReference().add( new SpecificationReference() );
        implementation.getSpecifications().getReference().get( 0 ).setIdentifier( specification.getIdentifier() );

        module.getSpecifications().getSpecification().add( specification );
        module.getImplementations().getImplementation().add( implementation );
        modules.getModule().add( module );

        ModelHelper.setModules( model, modules );

        try
        {
            this.getModelProvider().findModel( null, model );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNotNull( e.getMessage() );
            System.out.println( e.toString() );
        }

        try
        {
            this.getModelProvider().findModel( context, null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNotNull( e.getMessage() );
            System.out.println( e.toString() );
        }

        Model found = this.getModelProvider().findModel( context, model );
        assertNotNull( found );

        modules = ModelHelper.getModules( found );
        assertNotNull( modules );

        specification = modules.getSpecification( this.getClass().getName() + " Specification" );
        assertNotNull( specification );

        implementation = modules.getImplementation( this.getClass().getName() + " Implementation" );
        assertNotNull( implementation );

        final SourceFilesType specificationSourceFiles = specification.getAnyObject( SourceFilesType.class );
        final SourceFilesType implementationSourceFiles = implementation.getAnyObject( SourceFilesType.class );

        assertNotNull( specificationSourceFiles );
        assertNotNull( implementationSourceFiles );

        final SourceFileType specificationSourceFile = specificationSourceFiles.getSourceFile( "Default" );
        final SourceFileType implementationSourceFile = implementationSourceFiles.getSourceFile( "Default" );

        assertNotNull( specificationSourceFile );
        assertSectionNameUniqueness( specificationSourceFile.getSourceSections() );
        assertNotNull( implementationSourceFile );
        assertSectionNameUniqueness( implementationSourceFile.getSourceSections() );

        this.getModelProvider().setEnabled( false );

        found = this.getModelProvider().findModel( context, model );
        assertNull( found );

        this.getModelProvider().setEnabled( true );
    }

    @Test
    public final void testDefaultEnabled() throws Exception
    {
        System.clearProperty( "org.jomc.tools.modlet.ToolsModelProvider.defaultEnabled" );
        ToolsModelProvider.setDefaultEnabled( null );
        assertTrue( ToolsModelProvider.isDefaultEnabled() );

        System.setProperty( "org.jomc.tools.modlet.ToolsModelProvider.defaultEnabled", Boolean.toString( false ) );
        ToolsModelProvider.setDefaultEnabled( null );
        assertFalse( ToolsModelProvider.isDefaultEnabled() );
        System.clearProperty( "org.jomc.tools.modlet.ToolsModelProvider.defaultEnabled" );
        ToolsModelProvider.setDefaultEnabled( null );
        assertTrue( ToolsModelProvider.isDefaultEnabled() );

        System.setProperty( "org.jomc.tools.modlet.ToolsModelProvider.defaultEnabled", Boolean.toString( true ) );
        ToolsModelProvider.setDefaultEnabled( null );
        assertTrue( ToolsModelProvider.isDefaultEnabled() );
        System.clearProperty( "org.jomc.tools.modlet.ToolsModelProvider.defaultEnabled" );
        ToolsModelProvider.setDefaultEnabled( null );
        assertTrue( ToolsModelProvider.isDefaultEnabled() );
    }

    @Test
    public final void testEnabled() throws Exception
    {
        final Model model = new Model();
        model.setIdentifier( ModelObject.MODEL_PUBLIC_ID );

        ToolsModelProvider.setDefaultEnabled( null );
        this.getModelProvider().setEnabled( null );
        assertTrue( this.getModelProvider().isEnabled() );

        this.getModelProvider().findModel( ModelContextFactory.newInstance().newModelContext(), model );
        ToolsModelProvider.setDefaultEnabled( false );
        this.getModelProvider().setEnabled( null );
        assertFalse( this.getModelProvider().isEnabled() );

        this.getModelProvider().findModel( ModelContextFactory.newInstance().newModelContext(), model );
        ToolsModelProvider.setDefaultEnabled( null );
        this.getModelProvider().setEnabled( null );
    }

    @Test
    public final void testDefaultHeadComment() throws Exception
    {
        System.clearProperty( "org.jomc.tools.modlet.ToolsModelProvider.defaultHeadComment" );
        ToolsModelProvider.setDefaultHeadComment( null );
        assertEquals( "//", ToolsModelProvider.getDefaultHeadComment() );

        System.setProperty( "org.jomc.tools.modlet.ToolsModelProvider.defaultHeadComment", "/*" );
        ToolsModelProvider.setDefaultHeadComment( null );
        assertEquals( "/*", ToolsModelProvider.getDefaultHeadComment() );
        System.clearProperty( "org.jomc.tools.modlet.ToolsModelProvider.defaultHeadComment" );
        ToolsModelProvider.setDefaultHeadComment( null );
        assertEquals( "//", ToolsModelProvider.getDefaultHeadComment() );
    }

    @Test
    public final void testHeadComment() throws Exception
    {
        ToolsModelProvider.setDefaultHeadComment( null );
        this.getModelProvider().setHeadComment( null );
        assertEquals( "//", this.getModelProvider().getHeadComment() );

        ToolsModelProvider.setDefaultHeadComment( "/*" );
        this.getModelProvider().setHeadComment( null );
        assertEquals( "/*", this.getModelProvider().getHeadComment() );

        ToolsModelProvider.setDefaultHeadComment( null );
        this.getModelProvider().setHeadComment( null );
        assertEquals( "//", this.getModelProvider().getHeadComment() );
    }

    @Test
    public final void testDefaultTailComment() throws Exception
    {
        System.clearProperty( "org.jomc.tools.modlet.ToolsModelProvider.defaultTailComment" );
        ToolsModelProvider.setDefaultTailComment( null );
        assertNull( ToolsModelProvider.getDefaultTailComment() );

        System.setProperty( "org.jomc.tools.modlet.ToolsModelProvider.defaultTailComment", "*/" );
        ToolsModelProvider.setDefaultTailComment( null );
        assertEquals( "*/", ToolsModelProvider.getDefaultTailComment() );

        System.clearProperty( "org.jomc.tools.modlet.ToolsModelProvider.defaultTailComment" );
        ToolsModelProvider.setDefaultTailComment( null );
        assertNull( ToolsModelProvider.getDefaultTailComment() );
    }

    @Test
    public final void testTailComment() throws Exception
    {
        ToolsModelProvider.setDefaultTailComment( null );
        this.getModelProvider().setTailComment( null );
        assertNull( this.getModelProvider().getTailComment() );

        ToolsModelProvider.setDefaultTailComment( "*/" );
        this.getModelProvider().setTailComment( null );
        assertEquals( "*/", this.getModelProvider().getTailComment() );

        ToolsModelProvider.setDefaultTailComment( null );
        this.getModelProvider().setTailComment( null );
        assertNull( this.getModelProvider().getTailComment() );
    }

    private static void assertSectionNameUniqueness( final SourceSectionsType sections )
    {
        if ( sections != null )
        {
            final Set<String> sectionNames = new HashSet<String>( sections.getSourceSection().size() );

            for ( int i = 0, s0 = sections.getSourceSection().size(); i < s0; i++ )
            {
                final SourceSectionType section = sections.getSourceSection().get( i );
                assertTrue( "Multiple '" + section.getName() + "' sections.", sectionNames.add( section.getName() ) );
                assertSectionNameUniqueness( section.getSourceSections() );
            }
        }
    }

}
