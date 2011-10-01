/*
 *   Copyright (C) 2009 The JOMC Project
 *   Copyright (C) 2005 Christian Schulte <schulte2005@users.sourceforge.net>
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
package org.jomc.tools.modlet;

import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import org.jomc.model.Dependencies;
import org.jomc.model.Dependency;
import org.jomc.model.Implementation;
import org.jomc.model.Message;
import org.jomc.model.Messages;
import org.jomc.model.Module;
import org.jomc.model.Modules;
import org.jomc.model.Specification;
import org.jomc.model.modlet.ModelHelper;
import org.jomc.modlet.Model;
import org.jomc.modlet.ModelContext;
import org.jomc.modlet.ModelException;
import org.jomc.modlet.ModelValidationReport;
import org.jomc.modlet.ModelValidator;
import org.jomc.tools.model.ObjectFactory;
import org.jomc.tools.model.SourceFileType;
import org.jomc.tools.model.SourceFilesType;
import org.jomc.tools.model.SourceSectionType;
import org.jomc.tools.model.SourceSectionsType;

/**
 * Object management and configuration tools {@code ModelValidator} implementation.
 *
 * @author <a href="mailto:schulte2005@users.sourceforge.net">Christian Schulte</a>
 * @version $JOMC$
 * @see ModelContext#validateModel(org.jomc.modlet.Model)
 * @since 1.2
 */
public class ToolsModelValidator implements ModelValidator
{

    /** Creates a new {@code ToolsModelValidator} instance. */
    public ToolsModelValidator()
    {
        super();
    }

    public ModelValidationReport validateModel( final ModelContext context, final Model model ) throws ModelException
    {
        if ( context == null )
        {
            throw new NullPointerException( "context" );
        }
        if ( model == null )
        {
            throw new NullPointerException( "model" );
        }

        final ModelValidationReport report = new ModelValidationReport();
        this.assertValidToolsTypes( model, report );
        return report;
    }

    private void assertValidToolsTypes( final Model model, final ModelValidationReport report )
    {
        final List<SourceFileType> sourceFileType = model.getAnyObjects( SourceFileType.class );
        final List<SourceFilesType> sourceFilesType = model.getAnyObjects( SourceFilesType.class );
        final List<SourceSectionType> sourceSectionType = model.getAnyObjects( SourceSectionType.class );
        final List<SourceSectionsType> sourceSectionsType = model.getAnyObjects( SourceSectionsType.class );

        if ( sourceFileType != null )
        {
            for ( SourceFileType s : sourceFileType )
            {
                report.getDetails().add( new ModelValidationReport.Detail(
                    "MODEL_SOURCE_FILE_CONSTRAINT", Level.SEVERE, getMessage(
                    "modelSourceFileConstraint", model.getIdentifier(), s.getIdentifier() ),
                    new ObjectFactory().createSourceFile( s ) ) );


            }
        }

        if ( sourceFilesType != null )
        {
            for ( SourceFilesType files : sourceFilesType )
            {
                for ( SourceFileType s : files.getSourceFile() )
                {
                    report.getDetails().add( new ModelValidationReport.Detail(
                        "MODEL_SOURCE_FILE_CONSTRAINT", Level.SEVERE, getMessage(
                        "modelSourceFileConstraint", model.getIdentifier(), s.getIdentifier() ),
                        new ObjectFactory().createSourceFile( s ) ) );

                }

                if ( files.getSourceFile().isEmpty() )
                {
                    report.getDetails().add( new ModelValidationReport.Detail(
                        "MODEL_SOURCE_FILES_CONSTRAINT", Level.SEVERE, getMessage(
                        "modelSourceFilesConstraint", model.getIdentifier() ),
                        new ObjectFactory().createSourceFiles( files ) ) );

                }
            }
        }

        if ( sourceSectionType != null )
        {
            for ( SourceSectionType s : sourceSectionType )
            {
                report.getDetails().add( new ModelValidationReport.Detail(
                    "MODEL_SOURCE_SECTION_CONSTRAINT", Level.SEVERE, getMessage(
                    "modelSourceSectionConstraint", model.getIdentifier(), s.getName() ),
                    new ObjectFactory().createSourceSection( s ) ) );

            }
        }

        if ( sourceSectionsType != null )
        {
            for ( SourceSectionsType sections : sourceSectionsType )
            {
                for ( SourceSectionType s : sections.getSourceSection() )
                {
                    report.getDetails().add( new ModelValidationReport.Detail(
                        "MODEL_SOURCE_SECTION_CONSTRAINT", Level.SEVERE, getMessage(
                        "modelSourceSectionConstraint", model.getIdentifier(), s.getName() ),
                        new ObjectFactory().createSourceSection( s ) ) );

                }

                if ( sections.getSourceSection().isEmpty() )
                {
                    report.getDetails().add( new ModelValidationReport.Detail(
                        "MODEL_SOURCE_SECTIONS_CONSTRAINT", Level.SEVERE, getMessage(
                        "modelSourceSectionsConstraint", model.getIdentifier() ),
                        new ObjectFactory().createSourceSections( sections ) ) );

                }
            }
        }

        final Modules modules = ModelHelper.getModules( model );

        if ( modules != null )
        {
            this.assertValidToolsTypes( modules, report );
        }
    }

    private void assertValidToolsTypes( final Modules modules, final ModelValidationReport report )
    {
        for ( int i = 0, s0 = modules.getModule().size(); i < s0; i++ )
        {
            this.assertValidToolsTypes( modules.getModule().get( i ), report );
        }
    }

    private void assertValidToolsTypes( final Module module, final ModelValidationReport report )
    {
        final List<SourceFileType> sourceFileType = module.getAnyObjects( SourceFileType.class );
        final List<SourceFilesType> sourceFilesType = module.getAnyObjects( SourceFilesType.class );
        final List<SourceSectionType> sourceSectionType = module.getAnyObjects( SourceSectionType.class );
        final List<SourceSectionsType> sourceSectionsType = module.getAnyObjects( SourceSectionsType.class );

        if ( sourceFileType != null )
        {
            for ( SourceFileType s : sourceFileType )
            {
                report.getDetails().add( new ModelValidationReport.Detail(
                    "MODULE_SOURCE_FILE_CONSTRAINT", Level.SEVERE, getMessage(
                    "moduleSourceFileConstraint", module.getName(), s.getIdentifier() ),
                    new ObjectFactory().createSourceFile( s ) ) );


            }
        }

        if ( sourceFilesType != null )
        {
            for ( SourceFilesType files : sourceFilesType )
            {
                for ( SourceFileType s : files.getSourceFile() )
                {
                    report.getDetails().add( new ModelValidationReport.Detail(
                        "MODULE_SOURCE_FILE_CONSTRAINT", Level.SEVERE, getMessage(
                        "moduleSourceFileConstraint", module.getName(), s.getIdentifier() ),
                        new ObjectFactory().createSourceFile( s ) ) );

                }

                if ( files.getSourceFile().isEmpty() )
                {
                    report.getDetails().add( new ModelValidationReport.Detail(
                        "MODULE_SOURCE_FILES_CONSTRAINT", Level.SEVERE, getMessage(
                        "moduleSourceFilesConstraint", module.getName() ),
                        new ObjectFactory().createSourceFiles( files ) ) );

                }
            }
        }

        if ( sourceSectionType != null )
        {
            for ( SourceSectionType s : sourceSectionType )
            {
                report.getDetails().add( new ModelValidationReport.Detail(
                    "MODULE_SOURCE_SECTION_CONSTRAINT", Level.SEVERE, getMessage(
                    "moduleSourceSectionConstraint", module.getName(), s.getName() ),
                    new ObjectFactory().createSourceSection( s ) ) );

            }
        }

        if ( sourceSectionsType != null )
        {
            for ( SourceSectionsType sections : sourceSectionsType )
            {
                for ( SourceSectionType s : sections.getSourceSection() )
                {
                    report.getDetails().add( new ModelValidationReport.Detail(
                        "MODULE_SOURCE_SECTION_CONSTRAINT", Level.SEVERE, getMessage(
                        "moduleSourceSectionConstraint", module.getName(), s.getName() ),
                        new ObjectFactory().createSourceSection( s ) ) );

                }

                if ( sections.getSourceSection().isEmpty() )
                {
                    report.getDetails().add( new ModelValidationReport.Detail(
                        "MODULE_SOURCE_SECTIONS_CONSTRAINT", Level.SEVERE, getMessage(
                        "moduleSourceSectionsConstraint", module.getName() ),
                        new ObjectFactory().createSourceSections( sections ) ) );

                }
            }
        }

        if ( module.getImplementations() != null )
        {
            for ( int i = 0, s0 = module.getImplementations().getImplementation().size(); i < s0; i++ )
            {
                this.assertValidToolsTypes( module.getImplementations().getImplementation().get( i ), report );
            }
        }

        if ( module.getSpecifications() != null )
        {
            for ( int i = 0, s0 = module.getSpecifications().getSpecification().size(); i < s0; i++ )
            {
                this.assertValidToolsTypes( module.getSpecifications().getSpecification().get( i ), report );
            }
        }
    }

    private void assertValidToolsTypes( final Implementation implementation, final ModelValidationReport report )
    {
        final List<SourceFileType> sourceFileType = implementation.getAnyObjects( SourceFileType.class );
        final List<SourceFilesType> sourceFilesType = implementation.getAnyObjects( SourceFilesType.class );
        final List<SourceSectionType> sourceSectionType = implementation.getAnyObjects( SourceSectionType.class );
        final List<SourceSectionsType> sourceSectionsType = implementation.getAnyObjects( SourceSectionsType.class );

        if ( sourceFileType != null )
        {
            if ( sourceFileType.size() > 1 )
            {
                report.getDetails().add( new ModelValidationReport.Detail(
                    "IMPLEMENTATION_SOURCE_FILE_MULTIPLICITY_CONSTRAINT", Level.SEVERE, getMessage(
                    "implementationSourceFileMultiplicityConstraint", implementation.getIdentifier(),
                    sourceFileType.size() ),
                    new org.jomc.model.ObjectFactory().createImplementation( implementation ) ) );

            }
            else if ( sourceFileType.size() == 1 )
            {
                report.getDetails().add( new ModelValidationReport.Detail(
                    "IMPLEMENTATION_SOURCE_FILE_INFORMATION", Level.INFO, getMessage(
                    "implementationSourceFileInfo", implementation.getIdentifier(),
                    sourceFileType.get( 0 ).getIdentifier() ),
                    new org.jomc.model.ObjectFactory().createImplementation( implementation ) ) );

            }
        }

        if ( sourceFilesType != null )
        {
            if ( sourceFilesType.size() > 1 )
            {
                report.getDetails().add( new ModelValidationReport.Detail(
                    "IMPLEMENTATION_SOURCE_FILES_MULTIPLICITY_CONSTRAINT", Level.SEVERE, getMessage(
                    "implementationSourceFilesMultiplicityConstraint", implementation.getIdentifier(),
                    sourceFilesType.size() ),
                    new org.jomc.model.ObjectFactory().createImplementation( implementation ) ) );

            }
        }

        if ( sourceSectionType != null )
        {
            for ( SourceSectionType s : sourceSectionType )
            {
                report.getDetails().add( new ModelValidationReport.Detail(
                    "IMPLEMENTATION_SOURCE_SECTION_CONSTRAINT", Level.SEVERE, getMessage(
                    "implementationSourceSectionConstraint", implementation.getIdentifier(), s.getName() ),
                    new org.jomc.model.ObjectFactory().createImplementation( implementation ) ) );

            }
        }

        if ( sourceSectionsType != null )
        {
            for ( SourceSectionsType sections : sourceSectionsType )
            {
                for ( SourceSectionType s : sections.getSourceSection() )
                {
                    report.getDetails().add( new ModelValidationReport.Detail(
                        "IMPLEMENTATION_SOURCE_SECTION_CONSTRAINT", Level.SEVERE, getMessage(
                        "implementationSourceSectionConstraint", implementation.getIdentifier(), s.getName() ),
                        new org.jomc.model.ObjectFactory().createImplementation( implementation ) ) );

                }

                if ( sections.getSourceSection().isEmpty() )
                {
                    report.getDetails().add( new ModelValidationReport.Detail(
                        "IMPLEMENTATION_SOURCE_SECTIONS_CONSTRAINT", Level.SEVERE, getMessage(
                        "implementationSourceSectionsConstraint", implementation.getIdentifier() ),
                        new org.jomc.model.ObjectFactory().createImplementation( implementation ) ) );

                }
            }
        }

        if ( implementation.getDependencies() != null )
        {
            this.assertValidToolsTypes( implementation, implementation.getDependencies(), report );
        }

        if ( implementation.getMessages() != null )
        {
            this.assertValidToolsTypes( implementation, implementation.getMessages(), report );
        }
    }

    private void assertValidToolsTypes( final Implementation implementation, final Dependencies dependencies,
                                        final ModelValidationReport report )
    {
        for ( Dependency d : dependencies.getDependency() )
        {
            final List<SourceFileType> sourceFileType = d.getAnyObjects( SourceFileType.class );
            final List<SourceFilesType> sourceFilesType = d.getAnyObjects( SourceFilesType.class );
            final List<SourceSectionType> sourceSectionType = d.getAnyObjects( SourceSectionType.class );
            final List<SourceSectionsType> sourceSectionsType = d.getAnyObjects( SourceSectionsType.class );

            if ( sourceFileType != null )
            {
                for ( SourceFileType s : sourceFileType )
                {
                    report.getDetails().add( new ModelValidationReport.Detail(
                        "IMPLEMENTATION_DEPENDENCY_SOURCE_FILE_CONSTRAINT", Level.SEVERE, getMessage(
                        "dependencySourceFileConstraint", implementation.getIdentifier(), d.getName(),
                        s.getIdentifier() ),
                        new org.jomc.model.ObjectFactory().createImplementation( implementation ) ) );


                }
            }

            if ( sourceFilesType != null )
            {
                for ( SourceFilesType files : sourceFilesType )
                {
                    for ( SourceFileType s : files.getSourceFile() )
                    {
                        report.getDetails().add( new ModelValidationReport.Detail(
                            "IMPLEMENTATION_DEPENDENCY_SOURCE_FILE_CONSTRAINT", Level.SEVERE, getMessage(
                            "dependencySourceFileConstraint", implementation.getIdentifier(), d.getName(),
                            s.getIdentifier() ),
                            new org.jomc.model.ObjectFactory().createImplementation( implementation ) ) );

                    }

                    if ( files.getSourceFile().isEmpty() )
                    {
                        report.getDetails().add( new ModelValidationReport.Detail(
                            "IMPLEMENTATION_DEPENDENCY_SOURCE_FILES_CONSTRAINT", Level.SEVERE, getMessage(
                            "dependencySourceFilesConstraint", implementation.getIdentifier(), d.getName() ),
                            new org.jomc.model.ObjectFactory().createImplementation( implementation ) ) );

                    }
                }
            }

            if ( sourceSectionType != null )
            {
                for ( SourceSectionType s : sourceSectionType )
                {
                    report.getDetails().add( new ModelValidationReport.Detail(
                        "IMPLEMENTATION_DEPENDENCY_SOURCE_SECTION_CONSTRAINT", Level.SEVERE, getMessage(
                        "dependencySourceSectionConstraint", implementation.getIdentifier(), d.getName(), s.getName() ),
                        new org.jomc.model.ObjectFactory().createImplementation( implementation ) ) );

                }
            }

            if ( sourceSectionsType != null )
            {
                for ( SourceSectionsType sections : sourceSectionsType )
                {
                    for ( SourceSectionType s : sections.getSourceSection() )
                    {
                        report.getDetails().add( new ModelValidationReport.Detail(
                            "IMPLEMENTATION_DEPENDENCY_SOURCE_SECTION_CONSTRAINT", Level.SEVERE, getMessage(
                            "dependencySourceSectionConstraint", implementation.getIdentifier(), d.getName(),
                            s.getName() ),
                            new org.jomc.model.ObjectFactory().createImplementation( implementation ) ) );

                    }

                    if ( sections.getSourceSection().isEmpty() )
                    {
                        report.getDetails().add( new ModelValidationReport.Detail(
                            "IMPLEMENTATION_DEPENDENCY_SOURCE_SECTIONS_CONSTRAINT", Level.SEVERE, getMessage(
                            "dependencySourceSectionsConstraint", implementation.getIdentifier(), d.getName() ),
                            new org.jomc.model.ObjectFactory().createImplementation( implementation ) ) );

                    }
                }
            }
        }
    }

    private void assertValidToolsTypes( final Implementation implementation, final Messages messages,
                                        final ModelValidationReport report )
    {
        for ( Message m : messages.getMessage() )
        {
            final List<SourceFileType> sourceFileType = m.getAnyObjects( SourceFileType.class );
            final List<SourceFilesType> sourceFilesType = m.getAnyObjects( SourceFilesType.class );
            final List<SourceSectionType> sourceSectionType = m.getAnyObjects( SourceSectionType.class );
            final List<SourceSectionsType> sourceSectionsType = m.getAnyObjects( SourceSectionsType.class );

            if ( sourceFileType != null )
            {
                for ( SourceFileType s : sourceFileType )
                {
                    report.getDetails().add( new ModelValidationReport.Detail(
                        "IMPLEMENTATION_MESSAGE_SOURCE_FILE_CONSTRAINT", Level.SEVERE, getMessage(
                        "messageSourceFileConstraint", implementation.getIdentifier(), m.getName(),
                        s.getIdentifier() ),
                        new org.jomc.model.ObjectFactory().createImplementation( implementation ) ) );


                }
            }

            if ( sourceFilesType != null )
            {
                for ( SourceFilesType files : sourceFilesType )
                {
                    for ( SourceFileType s : files.getSourceFile() )
                    {
                        report.getDetails().add( new ModelValidationReport.Detail(
                            "IMPLEMENTATION_MESSAGE_SOURCE_FILE_CONSTRAINT", Level.SEVERE, getMessage(
                            "messageSourceFileConstraint", implementation.getIdentifier(), m.getName(),
                            s.getIdentifier() ),
                            new org.jomc.model.ObjectFactory().createImplementation( implementation ) ) );

                    }

                    if ( files.getSourceFile().isEmpty() )
                    {
                        report.getDetails().add( new ModelValidationReport.Detail(
                            "IMPLEMENTATION_MESSAGE_SOURCE_FILES_CONSTRAINT", Level.SEVERE, getMessage(
                            "messageSourceFilesConstraint", implementation.getIdentifier(), m.getName() ),
                            new org.jomc.model.ObjectFactory().createImplementation( implementation ) ) );

                    }
                }
            }

            if ( sourceSectionType != null )
            {
                for ( SourceSectionType s : sourceSectionType )
                {
                    report.getDetails().add( new ModelValidationReport.Detail(
                        "IMPLEMENTATION_MESSAGE_SOURCE_SECTION_CONSTRAINT", Level.SEVERE, getMessage(
                        "messageSourceSectionConstraint", implementation.getIdentifier(), m.getName(), s.getName() ),
                        new org.jomc.model.ObjectFactory().createImplementation( implementation ) ) );

                }
            }

            if ( sourceSectionsType != null )
            {
                for ( SourceSectionsType sections : sourceSectionsType )
                {
                    for ( SourceSectionType s : sections.getSourceSection() )
                    {
                        report.getDetails().add( new ModelValidationReport.Detail(
                            "IMPLEMENTATION_MESSAGE_SOURCE_SECTION_CONSTRAINT", Level.SEVERE, getMessage(
                            "messageSourceSectionConstraint", implementation.getIdentifier(), m.getName(),
                            s.getName() ),
                            new org.jomc.model.ObjectFactory().createImplementation( implementation ) ) );

                    }

                    if ( sections.getSourceSection().isEmpty() )
                    {
                        report.getDetails().add( new ModelValidationReport.Detail(
                            "IMPLEMENTATION_MESSAGE_SOURCE_SECTIONS_CONSTRAINT", Level.SEVERE, getMessage(
                            "messageSourceSectionsConstraint", implementation.getIdentifier(), m.getName() ),
                            new org.jomc.model.ObjectFactory().createImplementation( implementation ) ) );

                    }
                }
            }
        }
    }

    private void assertValidToolsTypes( final Specification specification, final ModelValidationReport report )
    {
        final List<SourceFileType> sourceFileType = specification.getAnyObjects( SourceFileType.class );
        final List<SourceFilesType> sourceFilesType = specification.getAnyObjects( SourceFilesType.class );
        final List<SourceSectionType> sourceSectionType = specification.getAnyObjects( SourceSectionType.class );
        final List<SourceSectionsType> sourceSectionsType = specification.getAnyObjects( SourceSectionsType.class );

        if ( sourceFileType != null )
        {
            if ( sourceFileType.size() > 1 )
            {
                report.getDetails().add( new ModelValidationReport.Detail(
                    "SPECIFICATION_SOURCE_FILE_MULTIPLICITY_CONSTRAINT", Level.SEVERE, getMessage(
                    "specificationSourceFileMultiplicityConstraint", specification.getIdentifier(),
                    sourceFileType.size() ),
                    new org.jomc.model.ObjectFactory().createSpecification( specification ) ) );

            }
            else if ( sourceFileType.size() == 1 )
            {
                report.getDetails().add( new ModelValidationReport.Detail(
                    "SPECIFICATION_SOURCE_FILE_INFORMATION", Level.INFO, getMessage(
                    "specificationSourceFileInfo", specification.getIdentifier(),
                    sourceFileType.get( 0 ).getIdentifier() ),
                    new org.jomc.model.ObjectFactory().createSpecification( specification ) ) );

            }
        }

        if ( sourceFilesType != null )
        {
            if ( sourceFilesType.size() > 1 )
            {
                report.getDetails().add( new ModelValidationReport.Detail(
                    "SPECIFICATION_SOURCE_FILES_MULTIPLICITY_CONSTRAINT", Level.SEVERE, getMessage(
                    "specificationSourceFilesMultiplicityConstraint", specification.getIdentifier(),
                    sourceFilesType.size() ),
                    new org.jomc.model.ObjectFactory().createSpecification( specification ) ) );

            }
        }

        if ( sourceSectionType != null )
        {
            for ( SourceSectionType s : sourceSectionType )
            {
                report.getDetails().add( new ModelValidationReport.Detail(
                    "SPECIFICATION_SOURCE_SECTION_CONSTRAINT", Level.SEVERE, getMessage(
                    "specificationSourceSectionConstraint", specification.getIdentifier(), s.getName() ),
                    new org.jomc.model.ObjectFactory().createSpecification( specification ) ) );

            }
        }

        if ( sourceSectionsType != null )
        {
            for ( SourceSectionsType sections : sourceSectionsType )
            {
                for ( SourceSectionType s : sections.getSourceSection() )
                {
                    report.getDetails().add( new ModelValidationReport.Detail(
                        "SPECIFICATION_SOURCE_SECTION_CONSTRAINT", Level.SEVERE, getMessage(
                        "specificationSourceSectionConstraint", specification.getIdentifier(), s.getName() ),
                        new org.jomc.model.ObjectFactory().createSpecification( specification ) ) );

                }

                if ( sections.getSourceSection().isEmpty() )
                {
                    report.getDetails().add( new ModelValidationReport.Detail(
                        "SPECIFICATION_SOURCE_SECTIONS_CONSTRAINT", Level.SEVERE, getMessage(
                        "specificationSourceSectionsConstraint", specification.getIdentifier() ),
                        new org.jomc.model.ObjectFactory().createSpecification( specification ) ) );

                }
            }
        }
    }

    private static String getMessage( final String key, final Object... args )
    {
        return MessageFormat.format( ResourceBundle.getBundle(
            ToolsModelValidator.class.getName().replace( '.', '/' ), Locale.getDefault() ).getString( key ), args );

    }

}
