/*
 *   Copyright (C) Christian Schulte, 2005-07-25
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

import java.util.List;
import org.jomc.model.Text;
import org.jomc.model.Texts;
import javax.xml.bind.util.JAXBSource;
import org.jomc.tools.model.SourceSectionsType;
import org.jomc.tools.model.SourceSectionType;
import org.jomc.model.Message;
import org.jomc.model.Dependency;
import org.jomc.model.Dependencies;
import org.jomc.model.Messages;
import org.jomc.model.Implementation;
import org.jomc.model.Implementations;
import org.jomc.model.Specifications;
import org.jomc.model.Specification;
import org.jomc.model.Module;
import org.jomc.model.Modules;
import org.jomc.tools.model.SourceFilesType;
import org.jomc.tools.model.SourceFileType;
import org.jomc.model.ModelObject;
import org.jomc.model.modlet.ModelHelper;
import org.jomc.modlet.Model;
import org.jomc.modlet.ModelContext;
import org.jomc.modlet.ModelValidationReport;
import org.jomc.tools.modlet.ToolsModelValidator;
import org.junit.Test;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Test cases for class {@code org.jomc.tools.modlet.ToolsModelValidator}.
 *
 * @author <a href="mailto:schulte2005@users.sourceforge.net">Christian Schulte</a> 1.0
 * @version $JOMC$
 */
public class ToolsModelValidatorTest
{

    /** The {@code ToolsModelValidator} instance tests are performed with. */
    private ToolsModelValidator toolsModelValidator;

    /** Creates a new {@code ToolsModelValidatorTest} instance. */
    public ToolsModelValidatorTest()
    {
        super();
    }

    /**
     * Gets the {@code ToolsModelValidator} instance tests are performed with.
     *
     * @return The {@code ToolsModelValidator} instance tests are performed with.
     *
     * @see #newModelValidator()
     */
    public ToolsModelValidator getModelValidator()
    {
        if ( this.toolsModelValidator == null )
        {
            this.toolsModelValidator = this.newModelValidator();
        }

        return this.toolsModelValidator;
    }

    /**
     * Create a new {@code ToolsModelValidator} instance to test.
     *
     * @return A new {@code ToolsModelValidator} instance to test.
     *
     * @see #getModelValidator()
     */
    protected ToolsModelValidator newModelValidator()
    {
        return new ToolsModelValidator();
    }

    @Test
    public final void testValidateModel() throws Exception
    {
        final ModelContext modelContext = ModelContext.createModelContext( this.getClass().getClassLoader() );

        try
        {
            this.getModelValidator().validateModel( modelContext, null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNotNull( e.getMessage() );
            System.out.println( e.toString() );
        }

        try
        {
            this.getModelValidator().validateModel( null, new Model() );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNotNull( e.getMessage() );
            System.out.println( e.toString() );
        }

        ModelValidationReport report = this.getModelValidator().validateModel( modelContext, new Model() );
        assertNotNull( report );
        assertTrue( report.isModelValid() );

        final Model model = new Model();
        model.setIdentifier( ModelObject.MODEL_PUBLIC_ID );

        final SourceFileType sourceFile1 = new SourceFileType();
        sourceFile1.setIdentifier( this.getClass().getSimpleName() + " 1" );

        final SourceFileType sourceFile2 = new SourceFileType();
        sourceFile2.setIdentifier( this.getClass().getSimpleName() + " 2" );

        final SourceFilesType sourceFiles1 = new SourceFilesType();
        sourceFiles1.getSourceFile().add( sourceFile1 );
        sourceFiles1.getSourceFile().add( sourceFile2 );

        final SourceFilesType sourceFiles2 = new SourceFilesType();
        sourceFiles2.getSourceFile().add( sourceFile1 );
        sourceFiles2.getSourceFile().add( sourceFile2 );

        final SourceSectionType sourceSection1 = new SourceSectionType();
        sourceSection1.setName( this.getClass().getSimpleName() + " 1" );

        final SourceSectionType sourceSection2 = new SourceSectionType();
        sourceSection2.setName( this.getClass().getSimpleName() + " 2" );

        final SourceSectionsType sourceSections1 = new SourceSectionsType();
        sourceSections1.getSourceSection().add( sourceSection1 );
        sourceSections1.getSourceSection().add( sourceSection2 );

        final SourceSectionsType sourceSections2 = new SourceSectionsType();
        sourceSections2.getSourceSection().add( sourceSection1 );
        sourceSections2.getSourceSection().add( sourceSection2 );

        model.getAny().add( new org.jomc.tools.model.ObjectFactory().createSourceFile( sourceFile1 ) );
        model.getAny().add( new org.jomc.tools.model.ObjectFactory().createSourceFile( sourceFile2 ) );
        model.getAny().add( new org.jomc.tools.model.ObjectFactory().createSourceFiles( sourceFiles1 ) );
        model.getAny().add( new org.jomc.tools.model.ObjectFactory().createSourceFiles( sourceFiles2 ) );
        model.getAny().add( new org.jomc.tools.model.ObjectFactory().createSourceFiles( new SourceFilesType() ) );
        model.getAny().add( new org.jomc.tools.model.ObjectFactory().createSourceSection( sourceSection1 ) );
        model.getAny().add( new org.jomc.tools.model.ObjectFactory().createSourceSection( sourceSection2 ) );
        model.getAny().add( new org.jomc.tools.model.ObjectFactory().createSourceSections( sourceSections1 ) );
        model.getAny().add( new org.jomc.tools.model.ObjectFactory().createSourceSections( sourceSections2 ) );
        model.getAny().add( new org.jomc.tools.model.ObjectFactory().createSourceSections( new SourceSectionsType() ) );

        final Modules modules = new Modules();
        ModelHelper.setModules( model, modules );

        final Module module = new Module();
        modules.getModule().add( module );
        module.setSpecifications( new Specifications() );
        module.setImplementations( new Implementations() );
        module.setName( this.getClass().getSimpleName() );

        module.getAny().add( new org.jomc.tools.model.ObjectFactory().createSourceFile( sourceFile1 ) );
        module.getAny().add( new org.jomc.tools.model.ObjectFactory().createSourceFile( sourceFile2 ) );
        module.getAny().add( new org.jomc.tools.model.ObjectFactory().createSourceFiles( sourceFiles1 ) );
        module.getAny().add( new org.jomc.tools.model.ObjectFactory().createSourceFiles( sourceFiles2 ) );
        module.getAny().add( new org.jomc.tools.model.ObjectFactory().createSourceFiles( new SourceFilesType() ) );
        module.getAny().add( new org.jomc.tools.model.ObjectFactory().createSourceSection( sourceSection1 ) );
        module.getAny().add( new org.jomc.tools.model.ObjectFactory().createSourceSection( sourceSection2 ) );
        module.getAny().add( new org.jomc.tools.model.ObjectFactory().createSourceSections( sourceSections1 ) );
        module.getAny().add( new org.jomc.tools.model.ObjectFactory().createSourceSections( sourceSections2 ) );
        module.getAny().add( new org.jomc.tools.model.ObjectFactory().createSourceSections( new SourceSectionsType() ) );
        module.setMessages( new Messages() );

        final Specification specification = new Specification();
        specification.setIdentifier( this.getClass().getSimpleName() );
        specification.getAny().add( new org.jomc.tools.model.ObjectFactory().createSourceFile( sourceFile1 ) );
        specification.getAny().add( new org.jomc.tools.model.ObjectFactory().createSourceFile( sourceFile2 ) );
        specification.getAny().add( new org.jomc.tools.model.ObjectFactory().createSourceFiles( sourceFiles1 ) );
        specification.getAny().add( new org.jomc.tools.model.ObjectFactory().createSourceFiles( sourceFiles2 ) );
        specification.getAny().add( new org.jomc.tools.model.ObjectFactory().createSourceFiles( new SourceFilesType() ) );
        specification.getAny().add( new org.jomc.tools.model.ObjectFactory().createSourceSection( sourceSection1 ) );
        specification.getAny().add( new org.jomc.tools.model.ObjectFactory().createSourceSection( sourceSection2 ) );
        specification.getAny().add( new org.jomc.tools.model.ObjectFactory().createSourceSections( sourceSections1 ) );
        specification.getAny().add( new org.jomc.tools.model.ObjectFactory().createSourceSections( sourceSections2 ) );
        specification.getAny().add( new org.jomc.tools.model.ObjectFactory().createSourceSections(
            new SourceSectionsType() ) );

        final Implementation implementation = new Implementation();
        implementation.setIdentifier( this.getClass().getSimpleName() );
        implementation.setName( this.getClass().getSimpleName() );
        implementation.getAny().add( new org.jomc.tools.model.ObjectFactory().createSourceFile( sourceFile1 ) );
        implementation.getAny().add( new org.jomc.tools.model.ObjectFactory().createSourceFile( sourceFile2 ) );
        implementation.getAny().add( new org.jomc.tools.model.ObjectFactory().createSourceFiles( sourceFiles1 ) );
        implementation.getAny().add( new org.jomc.tools.model.ObjectFactory().createSourceFiles( sourceFiles2 ) );
        implementation.getAny().add( new org.jomc.tools.model.ObjectFactory().createSourceFiles( new SourceFilesType() ) );
        implementation.getAny().add( new org.jomc.tools.model.ObjectFactory().createSourceSection( sourceSection1 ) );
        implementation.getAny().add( new org.jomc.tools.model.ObjectFactory().createSourceSection( sourceSection2 ) );
        implementation.getAny().add( new org.jomc.tools.model.ObjectFactory().createSourceSections( sourceSections1 ) );
        implementation.getAny().add( new org.jomc.tools.model.ObjectFactory().createSourceSections( sourceSections2 ) );
        implementation.getAny().add( new org.jomc.tools.model.ObjectFactory().createSourceSections(
            new SourceSectionsType() ) );

        implementation.setDependencies( new Dependencies() );
        implementation.setMessages( new Messages() );

        final Dependency dependency = new Dependency();
        dependency.setName( this.getClass().getSimpleName() );
        dependency.setIdentifier( this.getClass().getSimpleName() );
        dependency.getAny().add( new org.jomc.tools.model.ObjectFactory().createSourceFile( sourceFile1 ) );
        dependency.getAny().add( new org.jomc.tools.model.ObjectFactory().createSourceFile( sourceFile2 ) );
        dependency.getAny().add( new org.jomc.tools.model.ObjectFactory().createSourceFiles( sourceFiles1 ) );
        dependency.getAny().add( new org.jomc.tools.model.ObjectFactory().createSourceFiles( sourceFiles2 ) );
        dependency.getAny().add( new org.jomc.tools.model.ObjectFactory().createSourceFiles( new SourceFilesType() ) );
        dependency.getAny().add( new org.jomc.tools.model.ObjectFactory().createSourceSection( sourceSection1 ) );
        dependency.getAny().add( new org.jomc.tools.model.ObjectFactory().createSourceSection( sourceSection2 ) );
        dependency.getAny().add( new org.jomc.tools.model.ObjectFactory().createSourceSections( sourceSections1 ) );
        dependency.getAny().add( new org.jomc.tools.model.ObjectFactory().createSourceSections( sourceSections2 ) );
        dependency.getAny().add(
            new org.jomc.tools.model.ObjectFactory().createSourceSections( new SourceSectionsType() ) );

        final Message message = new Message();
        message.setName( this.getClass().getSimpleName() );
        message.setTemplate( new Texts() );
        message.getTemplate().setDefaultLanguage( "en" );

        final Text text = new Text();
        text.setLanguage( "en" );
        message.getTemplate().getText().add( text );

        message.getAny().add( new org.jomc.tools.model.ObjectFactory().createSourceFile( sourceFile1 ) );
        message.getAny().add( new org.jomc.tools.model.ObjectFactory().createSourceFile( sourceFile2 ) );
        message.getAny().add( new org.jomc.tools.model.ObjectFactory().createSourceFiles( sourceFiles1 ) );
        message.getAny().add( new org.jomc.tools.model.ObjectFactory().createSourceFiles( sourceFiles2 ) );
        message.getAny().add( new org.jomc.tools.model.ObjectFactory().createSourceFiles( new SourceFilesType() ) );
        message.getAny().add( new org.jomc.tools.model.ObjectFactory().createSourceSection( sourceSection1 ) );
        message.getAny().add( new org.jomc.tools.model.ObjectFactory().createSourceSection( sourceSection2 ) );
        message.getAny().add( new org.jomc.tools.model.ObjectFactory().createSourceSections( sourceSections1 ) );
        message.getAny().add( new org.jomc.tools.model.ObjectFactory().createSourceSections( sourceSections2 ) );
        message.getAny().add( new org.jomc.tools.model.ObjectFactory().createSourceSections( new SourceSectionsType() ) );

        implementation.getDependencies().getDependency().add( dependency );
        implementation.getMessages().getMessage().add( message );

        module.getImplementations().getImplementation().add( implementation );
        module.getMessages().getMessage().add( message );
        module.getSpecifications().getSpecification().add( specification );

        final Specification deprecatedSpecification = new Specification();
        deprecatedSpecification.setIdentifier( this.getClass().getSimpleName() + " 2" );
        deprecatedSpecification.getAny().add( new org.jomc.tools.model.ObjectFactory().createSourceFile( sourceFile1 ) );

        final Implementation deprecatedImplementation = new Implementation();
        deprecatedImplementation.setIdentifier( this.getClass().getSimpleName() + " 2" );
        deprecatedImplementation.setName( this.getClass().getSimpleName() );
        deprecatedImplementation.getAny().add( new org.jomc.tools.model.ObjectFactory().createSourceFile( sourceFile1 ) );

        module.getSpecifications().getSpecification().add( deprecatedSpecification );
        module.getImplementations().getImplementation().add( deprecatedImplementation );

        final JAXBSource jaxbSource = new JAXBSource( modelContext.createMarshaller(
            ModelObject.MODEL_PUBLIC_ID ), new org.jomc.modlet.ObjectFactory().createModel( model ) );

        report = modelContext.validateModel( ModelObject.MODEL_PUBLIC_ID, jaxbSource );
        assertValidModel( report );

        report = this.getModelValidator().validateModel( modelContext, model );
        assertInvalidModel( report );
        assertModelValidationReportDetail( report, "MODEL_SOURCE_FILE_CONSTRAINT", 6 );
        assertModelValidationReportDetail( report, "MODEL_SOURCE_FILES_CONSTRAINT", 1 );
        assertModelValidationReportDetail( report, "MODEL_SOURCE_SECTION_CONSTRAINT", 6 );
        assertModelValidationReportDetail( report, "MODEL_SOURCE_SECTIONS_CONSTRAINT", 1 );
        assertModelValidationReportDetail( report, "MODULE_SOURCE_FILE_CONSTRAINT", 6 );
        assertModelValidationReportDetail( report, "MODULE_SOURCE_FILES_CONSTRAINT", 1 );
        assertModelValidationReportDetail( report, "MODULE_SOURCE_SECTION_CONSTRAINT", 6 );
        assertModelValidationReportDetail( report, "MODULE_SOURCE_SECTIONS_CONSTRAINT", 1 );
        assertModelValidationReportDetail( report, "IMPLEMENTATION_SOURCE_FILE_MULTIPLICITY_CONSTRAINT", 1 );
        assertModelValidationReportDetail( report, "IMPLEMENTATION_SOURCE_FILES_MULTIPLICITY_CONSTRAINT", 1 );
        assertModelValidationReportDetail( report, "IMPLEMENTATION_SOURCE_SECTION_CONSTRAINT", 6 );
        assertModelValidationReportDetail( report, "IMPLEMENTATION_SOURCE_SECTIONS_CONSTRAINT", 1 );
        assertModelValidationReportDetail( report, "IMPLEMENTATION_SOURCE_FILE_INFORMATION", 1 );
        assertModelValidationReportDetail( report, "IMPLEMENTATION_DEPENDENCY_SOURCE_FILE_CONSTRAINT", 6 );
        assertModelValidationReportDetail( report, "IMPLEMENTATION_DEPENDENCY_SOURCE_FILES_CONSTRAINT", 1 );
        assertModelValidationReportDetail( report, "IMPLEMENTATION_DEPENDENCY_SOURCE_SECTION_CONSTRAINT", 6 );
        assertModelValidationReportDetail( report, "IMPLEMENTATION_DEPENDENCY_SOURCE_SECTIONS_CONSTRAINT", 1 );
        assertModelValidationReportDetail( report, "IMPLEMENTATION_MESSAGE_SOURCE_FILE_CONSTRAINT", 6 );
        assertModelValidationReportDetail( report, "IMPLEMENTATION_MESSAGE_SOURCE_FILES_CONSTRAINT", 1 );
        assertModelValidationReportDetail( report, "IMPLEMENTATION_MESSAGE_SOURCE_SECTION_CONSTRAINT", 6 );
        assertModelValidationReportDetail( report, "IMPLEMENTATION_MESSAGE_SOURCE_SECTIONS_CONSTRAINT", 1 );
        assertModelValidationReportDetail( report, "SPECIFICATION_SOURCE_FILE_MULTIPLICITY_CONSTRAINT", 1 );
        assertModelValidationReportDetail( report, "SPECIFICATION_SOURCE_FILES_MULTIPLICITY_CONSTRAINT", 1 );
        assertModelValidationReportDetail( report, "SPECIFICATION_SOURCE_SECTION_CONSTRAINT", 6 );
        assertModelValidationReportDetail( report, "SPECIFICATION_SOURCE_SECTIONS_CONSTRAINT", 1 );
        assertModelValidationReportDetail( report, "SPECIFICATION_SOURCE_FILE_INFORMATION", 1 );
    }

    private static void assertValidModel( final ModelValidationReport report )
    {
        assertNotNull( report );

        if ( !report.isModelValid() )
        {
            System.out.println( ">>>Unexpected invalid model:" );
            logModelValidationReport( report );
            fail( report.toString() );
        }
        else
        {
            System.out.println( ">>>Valid model:" );
            logModelValidationReport( report );
        }
    }

    private static void assertInvalidModel( final ModelValidationReport report )
    {
        assertNotNull( report );

        if ( report.isModelValid() )
        {
            System.out.println( ">>>Unexpected valid model:" );
            logModelValidationReport( report );
            fail( report.toString() );
        }
        else
        {
            System.out.println( ">>>Invalid model:" );
            logModelValidationReport( report );
        }
    }

    private static void assertModelValidationReportDetail( final ModelValidationReport report, final String identifier,
                                                           final Number count )
    {
        final List<ModelValidationReport.Detail> details = report.getDetails( identifier );

        if ( details.size() != count )
        {
            System.out.println( ">>>Unexpected number of '" + identifier + "' details. Expected " + count + " - found "
                                + details.size() + "." );

            logModelValidationReport( report );
            fail( report.toString() );
        }
    }

    private static void logModelValidationReport( final ModelValidationReport report )
    {
        for ( ModelValidationReport.Detail d : report.getDetails() )
        {
            System.out.println( "\t" + d );
        }
    }

}
