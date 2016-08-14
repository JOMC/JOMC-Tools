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
package org.jomc.tools.modlet;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import javax.xml.bind.JAXBElement;
import org.jomc.model.Dependencies;
import org.jomc.model.Dependency;
import org.jomc.model.Implementation;
import org.jomc.model.Message;
import org.jomc.model.Messages;
import org.jomc.model.ModelObjectException;
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
import org.jomc.tools.model.TemplateParameterType;

/**
 * Object management and configuration tools {@code ModelValidator} implementation.
 *
 * @author <a href="mailto:cs@schulte.it">Christian Schulte</a>
 * @version $JOMC$
 * @see ModelContext#validateModel(org.jomc.modlet.Model)
 * @since 1.2
 */
public class ToolsModelValidator implements ModelValidator
{

    /**
     * Constant for the name of the model context attribute backing property {@code validateJava}.
     *
     * @see ModelContext#getAttribute(java.lang.String)
     * @since 1.6
     */
    public static final String VALIDATE_JAVA_ATTRIBUTE_NAME =
        "org.jomc.tools.modlet.ToolsModelValidator.validateJavaAttribute";

    /**
     * Constant for the name of the system property controlling property {@code defaultValidateJava}.
     *
     * @see #isDefaultValidateJava()
     * @since 1.6
     */
    private static final String DEFAULT_VALIDATE_JAVA_PROPERTY_NAME =
        "org.jomc.tools.modlet.ToolsModelValidator.defaultValidateJava";

    /**
     * Default value of the flag indicating the validator is performing Java related validation by default.
     *
     * @see #isDefaultValidateJava()
     * @since 1.6
     */
    private static final Boolean DEFAULT_VALIDATE_JAVA = Boolean.TRUE;

    /**
     * Flag indicating the validator is performing Java related validation by default.
     *
     * @since 1.6
     */
    private static volatile Boolean defaultValidateJava;

    /**
     * Flag indicating the validator is performing Java related validation.
     *
     * @since 1.6
     */
    private Boolean validateJava;

    /**
     * Creates a new {@code ToolsModelValidator} instance.
     */
    public ToolsModelValidator()
    {
        super();
    }

    /**
     * Gets a flag indicating the validator is performing Java related validation by default.
     * <p>
     * The default validate Java flag is controlled by system property
     * {@code org.jomc.tools.modlet.ToolsModelValidator.defaultValidateJava} holding a value indicating the validator
     * is performing Java related validation by default. If that property is not set, the {@code true} default is
     * returned.
     * </p>
     *
     * @return {@code true}, if the validator is performing Java related validation by default; {@code false}, if the
     * validator is not performing Java related validation by default.
     *
     * @see #setDefaultValidateJava(java.lang.Boolean)
     *
     * @since 1.6
     */
    public static boolean isDefaultValidateJava()
    {
        if ( defaultValidateJava == null )
        {
            defaultValidateJava = Boolean.valueOf( System.getProperty( DEFAULT_VALIDATE_JAVA_PROPERTY_NAME,
                                                                       Boolean.toString( DEFAULT_VALIDATE_JAVA ) ) );

        }

        return defaultValidateJava;
    }

    /**
     * Sets the flag indicating the validator is performing Java related validation by default.
     *
     * @param value The new value of the flag indicating the validator is performing Java related validation by default
     * or {@code null}.
     *
     * @see #isDefaultValidateJava()
     *
     * @since 1.6
     */
    public static void setDefaultValidateJava( final Boolean value )
    {
        defaultValidateJava = value;
    }

    /**
     * Gets a flag indicating the validator is performing Java related validation.
     *
     * @return {@code true}, if the validator is performing Java related validation; {@code false}, if the the validator
     * is not performing Java related validation.
     *
     * @see #isDefaultValidateJava()
     * @see #setValidateJava(java.lang.Boolean)
     *
     * @since 1.6
     */
    public final boolean isValidateJava()
    {
        if ( this.validateJava == null )
        {
            this.validateJava = isDefaultValidateJava();
        }

        return this.validateJava;
    }

    /**
     * Sets the flag indicating the validator is performing Java related validation.
     *
     * @param value The new value of the flag indicating the validator is performing Java related validation or
     * {@code null}.
     *
     * @see #isValidateJava()
     *
     * @since 1.6
     */
    public final void setValidateJava( final Boolean value )
    {
        this.validateJava = value;
    }

    @Override
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
        this.assertValidToolsTypes( context, model, report );
        return report;
    }

    private void assertValidToolsTypes( final ModelContext context, final Model model,
                                        final ModelValidationReport report )
    {
        final List<SourceFileType> sourceFileType = model.getAnyObjects( SourceFileType.class );
        final List<SourceFilesType> sourceFilesType = model.getAnyObjects( SourceFilesType.class );
        final List<SourceSectionType> sourceSectionType = model.getAnyObjects( SourceSectionType.class );
        final List<SourceSectionsType> sourceSectionsType = model.getAnyObjects( SourceSectionsType.class );

        if ( sourceFileType != null )
        {
            for ( final SourceFileType s : sourceFileType )
            {
                report.getDetails().add( new ModelValidationReport.Detail(
                    "MODEL_SOURCE_FILE_CONSTRAINT", Level.SEVERE, getMessage(
                        "modelSourceFileConstraint", model.getIdentifier(), s.getIdentifier() ),
                    new ObjectFactory().createSourceFile( s ) ) );

                if ( this.isValidateJava() )
                {
                    for ( final TemplateParameterType p : s.getTemplateParameter() )
                    {
                        try
                        {
                            p.getJavaValue( context.getClassLoader() );
                        }
                        catch ( final ModelObjectException e )
                        {
                            final String message = getMessage( e );

                            if ( context.isLoggable( Level.FINE ) )
                            {
                                context.log( Level.FINE, message, e );
                            }

                            report.getDetails().add( new ModelValidationReport.Detail(
                                "MODEL_SOURCE_FILE_TEMPLATE_PARAMETER_JAVA_VALUE_CONSTRAINT", Level.SEVERE, getMessage(
                                    "modelSourceFileTemplateParameterJavaValueConstraint", model.getIdentifier(),
                                    s.getIdentifier(), p.getName(),
                                    message != null && message.length() > 0 ? " " + message : "" ),
                                new ObjectFactory().createSourceFile( s ) ) );

                        }
                    }
                }

                this.validateTemplateParameters( report, context, s.getSourceSections(),
                                                 "MODEL_SOURCE_FILE_SECTION_TEMPLATE_PARAMETER_JAVA_VALUE_CONSTRAINT",
                                                 new ObjectFactory().createSourceFile( s ),
                                                 "modelSourceFileSectionTemplateParameterJavaValueConstraint",
                                                 model.getIdentifier(), s.getIdentifier() );

            }
        }

        if ( sourceFilesType != null )
        {
            for ( final SourceFilesType files : sourceFilesType )
            {
                for ( final SourceFileType s : files.getSourceFile() )
                {
                    report.getDetails().add( new ModelValidationReport.Detail(
                        "MODEL_SOURCE_FILE_CONSTRAINT", Level.SEVERE, getMessage(
                            "modelSourceFileConstraint", model.getIdentifier(), s.getIdentifier() ),
                        new ObjectFactory().createSourceFile( s ) ) );

                    if ( this.isValidateJava() )
                    {
                        for ( final TemplateParameterType p : s.getTemplateParameter() )
                        {
                            try
                            {
                                p.getJavaValue( context.getClassLoader() );
                            }
                            catch ( final ModelObjectException e )
                            {
                                final String message = getMessage( e );

                                if ( context.isLoggable( Level.FINE ) )
                                {
                                    context.log( Level.FINE, message, e );
                                }

                                report.getDetails().add( new ModelValidationReport.Detail(
                                    "MODEL_SOURCE_FILE_TEMPLATE_PARAMETER_JAVA_VALUE_CONSTRAINT", Level.SEVERE,
                                    getMessage( "modelSourceFileTemplateParameterJavaValueConstraint",
                                                model.getIdentifier(), s.getIdentifier(), p.getName(),
                                                message != null && message.length() > 0 ? " " + message : "" ),
                                    new ObjectFactory().createSourceFile( s ) ) );

                            }
                        }
                    }

                    this.validateTemplateParameters(
                        report, context, s.getSourceSections(),
                        "MODEL_SOURCE_FILE_SECTION_TEMPLATE_PARAMETER_JAVA_VALUE_CONSTRAINT",
                        new ObjectFactory().createSourceFile( s ),
                        "modelSourceFileSectionTemplateParameterJavaValueConstraint",
                        model.getIdentifier(), s.getIdentifier() );

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
            for ( final SourceSectionType s : sourceSectionType )
            {
                report.getDetails().add( new ModelValidationReport.Detail(
                    "MODEL_SOURCE_SECTION_CONSTRAINT", Level.SEVERE, getMessage(
                        "modelSourceSectionConstraint", model.getIdentifier(), s.getName() ),
                    new ObjectFactory().createSourceSection( s ) ) );

                if ( this.isValidateJava() )
                {
                    for ( final TemplateParameterType p : s.getTemplateParameter() )
                    {
                        try
                        {
                            p.getJavaValue( context.getClassLoader() );
                        }
                        catch ( final ModelObjectException e )
                        {
                            final String message = getMessage( e );

                            if ( context.isLoggable( Level.FINE ) )
                            {
                                context.log( Level.FINE, message, e );
                            }

                            report.getDetails().add( new ModelValidationReport.Detail(
                                "MODEL_SOURCE_SECTION_TEMPLATE_PARAMETER_JAVA_VALUE_CONSTRAINT", Level.SEVERE,
                                getMessage( "modelSourceSectionTemplateParameterJavaValueConstraint",
                                            model.getIdentifier(), s.getName(), p.getName(),
                                            message != null && message.length() > 0 ? " " + message : "" ),
                                new ObjectFactory().createSourceSection( s ) ) );

                        }
                    }
                }

                this.validateTemplateParameters( report, context, s.getSourceSections(),
                                                 "MODEL_SOURCE_SECTION_TEMPLATE_PARAMETER_JAVA_VALUE_CONSTRAINT",
                                                 new ObjectFactory().createSourceSection( s ),
                                                 "modelSourceSectionTemplateParameterJavaValueConstraint",
                                                 model.getIdentifier() );

            }
        }

        if ( sourceSectionsType != null )
        {
            for ( final SourceSectionsType sections : sourceSectionsType )
            {
                for ( final SourceSectionType s : sections.getSourceSection() )
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

                this.validateTemplateParameters( report, context, sections,
                                                 "MODEL_SOURCE_SECTION_TEMPLATE_PARAMETER_JAVA_VALUE_CONSTRAINT",
                                                 new ObjectFactory().createSourceSections( sections ),
                                                 "modelSourceSectionTemplateParameterJavaValueConstraint",
                                                 model.getIdentifier() );

            }
        }

        final Modules modules = ModelHelper.getModules( model );

        if ( modules != null )
        {
            this.assertValidToolsTypes( context, modules, report );
        }
    }

    private void assertValidToolsTypes( final ModelContext context, final Modules modules,
                                        final ModelValidationReport report )
    {
        for ( int i = 0, s0 = modules.getModule().size(); i < s0; i++ )
        {
            this.assertValidToolsTypes( context, modules.getModule().get( i ), report );
        }
    }

    private void assertValidToolsTypes( final ModelContext context, final Module module,
                                        final ModelValidationReport report )
    {
        final List<SourceFileType> sourceFileType = module.getAnyObjects( SourceFileType.class );
        final List<SourceFilesType> sourceFilesType = module.getAnyObjects( SourceFilesType.class );
        final List<SourceSectionType> sourceSectionType = module.getAnyObjects( SourceSectionType.class );
        final List<SourceSectionsType> sourceSectionsType = module.getAnyObjects( SourceSectionsType.class );

        if ( sourceFileType != null )
        {
            for ( final SourceFileType s : sourceFileType )
            {
                report.getDetails().add( new ModelValidationReport.Detail(
                    "MODULE_SOURCE_FILE_CONSTRAINT", Level.SEVERE, getMessage(
                        "moduleSourceFileConstraint", module.getName(), s.getIdentifier() ),
                    new ObjectFactory().createSourceFile( s ) ) );

                if ( this.isValidateJava() )
                {
                    for ( final TemplateParameterType p : s.getTemplateParameter() )
                    {
                        try
                        {
                            p.getJavaValue( context.getClassLoader() );
                        }
                        catch ( final ModelObjectException e )
                        {
                            final String message = getMessage( e );

                            if ( context.isLoggable( Level.FINE ) )
                            {
                                context.log( Level.FINE, message, e );
                            }

                            report.getDetails().add( new ModelValidationReport.Detail(
                                "MODULE_SOURCE_FILE_TEMPLATE_PARAMETER_JAVA_VALUE_CONSTRAINT", Level.SEVERE, getMessage(
                                    "moduleSourceFileTemplateParameterJavaValueConstraint", module.getName(),
                                    s.getIdentifier(), p.getName(),
                                    message != null && message.length() > 0 ? " " + message : "" ),
                                new ObjectFactory().createSourceFile( s ) ) );

                        }
                    }
                }

                this.validateTemplateParameters(
                    report, context, s.getSourceSections(),
                    "MODULE_SOURCE_FILE_SECTION_TEMPLATE_PARAMETER_JAVA_VALUE_CONSTRAINT",
                    new ObjectFactory().createSourceFile( s ),
                    "moduleSourceFileSectionTemplateParameterJavaValueConstraint",
                    module.getName(), s.getIdentifier() );

            }
        }

        if ( sourceFilesType != null )
        {
            for ( final SourceFilesType files : sourceFilesType )
            {
                for ( final SourceFileType s : files.getSourceFile() )
                {
                    report.getDetails().add( new ModelValidationReport.Detail(
                        "MODULE_SOURCE_FILE_CONSTRAINT", Level.SEVERE, getMessage(
                            "moduleSourceFileConstraint", module.getName(), s.getIdentifier() ),
                        new ObjectFactory().createSourceFile( s ) ) );

                    if ( this.isValidateJava() )
                    {
                        for ( final TemplateParameterType p : s.getTemplateParameter() )
                        {
                            try
                            {
                                p.getJavaValue( context.getClassLoader() );
                            }
                            catch ( final ModelObjectException e )
                            {
                                final String message = getMessage( e );

                                if ( context.isLoggable( Level.FINE ) )
                                {
                                    context.log( Level.FINE, message, e );
                                }

                                report.getDetails().add( new ModelValidationReport.Detail(
                                    "MODULE_SOURCE_FILE_TEMPLATE_PARAMETER_JAVA_VALUE_CONSTRAINT", Level.SEVERE,
                                    getMessage( "moduleSourceFileTemplateParameterJavaValueConstraint",
                                                module.getName(), s.getIdentifier(), p.getName(),
                                                message != null && message.length() > 0 ? " " + message : "" ),
                                    new ObjectFactory().createSourceFile( s ) ) );

                            }
                        }
                    }

                    this.validateTemplateParameters(
                        report, context, s.getSourceSections(),
                        "MODULE_SOURCE_FILE_SECTION_TEMPLATE_PARAMETER_JAVA_VALUE_CONSTRAINT",
                        new ObjectFactory().createSourceFile( s ),
                        "moduleSourceFileSectionTemplateParameterJavaValueConstraint",
                        module.getName(), s.getIdentifier() );

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
            for ( final SourceSectionType s : sourceSectionType )
            {
                report.getDetails().add( new ModelValidationReport.Detail(
                    "MODULE_SOURCE_SECTION_CONSTRAINT", Level.SEVERE, getMessage(
                        "moduleSourceSectionConstraint", module.getName(), s.getName() ),
                    new ObjectFactory().createSourceSection( s ) ) );

                if ( this.isValidateJava() )
                {
                    for ( final TemplateParameterType p : s.getTemplateParameter() )
                    {
                        try
                        {
                            p.getJavaValue( context.getClassLoader() );
                        }
                        catch ( final ModelObjectException e )
                        {
                            final String message = getMessage( e );

                            if ( context.isLoggable( Level.FINE ) )
                            {
                                context.log( Level.FINE, message, e );
                            }

                            report.getDetails().add( new ModelValidationReport.Detail(
                                "MODULE_SOURCE_SECTION_TEMPLATE_PARAMETER_JAVA_VALUE_CONSTRAINT", Level.SEVERE,
                                getMessage( "moduleSourceSectionTemplateParameterJavaValueConstraint",
                                            module.getName(), s.getName(), p.getName(),
                                            message != null && message.length() > 0 ? " " + message : "" ),
                                new ObjectFactory().createSourceSection( s ) ) );

                        }
                    }
                }

                this.validateTemplateParameters(
                    report, context, s.getSourceSections(),
                    "MODULE_SOURCE_SECTION_TEMPLATE_PARAMETER_JAVA_VALUE_CONSTRAINT",
                    new ObjectFactory().createSourceSection( s ),
                    "moduleSourceSectionTemplateParameterJavaValueConstraint",
                    module.getName(), s.getName() );

            }
        }

        if ( sourceSectionsType != null )
        {
            for ( final SourceSectionsType sections : sourceSectionsType )
            {
                for ( final SourceSectionType s : sections.getSourceSection() )
                {
                    report.getDetails().add( new ModelValidationReport.Detail(
                        "MODULE_SOURCE_SECTION_CONSTRAINT", Level.SEVERE, getMessage(
                            "moduleSourceSectionConstraint", module.getName(), s.getName() ),
                        new ObjectFactory().createSourceSection( s ) ) );

                    if ( this.isValidateJava() )
                    {
                        for ( final TemplateParameterType p : s.getTemplateParameter() )
                        {
                            try
                            {
                                p.getJavaValue( context.getClassLoader() );
                            }
                            catch ( final ModelObjectException e )
                            {
                                final String message = getMessage( e );

                                if ( context.isLoggable( Level.FINE ) )
                                {
                                    context.log( Level.FINE, message, e );
                                }

                                report.getDetails().add( new ModelValidationReport.Detail(
                                    "MODULE_SOURCE_SECTION_TEMPLATE_PARAMETER_JAVA_VALUE_CONSTRAINT", Level.SEVERE,
                                    getMessage( "moduleSourceSectionTemplateParameterJavaValueConstraint",
                                                module.getName(), s.getName(), p.getName(),
                                                message != null && message.length() > 0 ? " " + message : "" ),
                                    new ObjectFactory().createSourceSection( s ) ) );

                            }
                        }
                    }

                    this.validateTemplateParameters( report, context, s.getSourceSections(),
                                                     "MODULE_SOURCE_SECTION_TEMPLATE_PARAMETER_JAVA_VALUE_CONSTRAINT",
                                                     new ObjectFactory().createSourceSection( s ),
                                                     "moduleSourceSectionTemplateParameterJavaValueConstraint",
                                                     module.getName(), s.getName() );

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
                this.assertValidToolsTypes( context, module, module.getImplementations().getImplementation().get( i ),
                                            report );

            }
        }

        if ( module.getSpecifications() != null )
        {
            for ( int i = 0, s0 = module.getSpecifications().getSpecification().size(); i < s0; i++ )
            {
                this.assertValidToolsTypes( context, module, module.getSpecifications().getSpecification().get( i ),
                                            report );

            }
        }
    }

    private void assertValidToolsTypes( final ModelContext context, final Module module,
                                        final Implementation implementation, final ModelValidationReport report )
    {
        final List<SourceFileType> sourceFileType = implementation.getAnyObjects( SourceFileType.class );
        final List<SourceFilesType> sourceFilesType = implementation.getAnyObjects( SourceFilesType.class );
        final List<SourceSectionType> sourceSectionType = implementation.getAnyObjects( SourceSectionType.class );
        final List<SourceSectionsType> sourceSectionsType = implementation.getAnyObjects( SourceSectionsType.class );

        if ( sourceFileType != null )
        {
            for ( final SourceFileType s : sourceFileType )
            {
                if ( this.isValidateJava() )
                {
                    for ( final TemplateParameterType p : s.getTemplateParameter() )
                    {
                        try
                        {
                            p.getJavaValue( context.getClassLoader() );
                        }
                        catch ( final ModelObjectException e )
                        {
                            final String message = getMessage( e );

                            if ( context.isLoggable( Level.FINE ) )
                            {
                                context.log( Level.FINE, message, e );
                            }

                            report.getDetails().add( new ModelValidationReport.Detail(
                                "IMPLEMENTATION_SOURCE_FILE_TEMPLATE_PARAMETER_JAVA_VALUE_CONSTRAINT", Level.SEVERE,
                                getMessage( "implementationSourceFileTemplateParameterJavaValueConstraint",
                                            module.getName(), implementation.getIdentifier(),
                                            s.getIdentifier(), p.getName(),
                                            message != null && message.length() > 0 ? " " + message : "" ),
                                new org.jomc.model.ObjectFactory().createImplementation( implementation ) ) );

                        }
                    }
                }

                this.validateTemplateParameters(
                    report, context, s.getSourceSections(),
                    "IMPLEMENTATION_SOURCE_FILE_SECTION_TEMPLATE_PARAMETER_JAVA_VALUE_CONSTRAINT",
                    new org.jomc.model.ObjectFactory().createImplementation( implementation ),
                    "implementationSourceFileSectionTemplateParameterJavaValueConstraint",
                    module.getName(), implementation.getIdentifier(), s.getIdentifier() );

            }

            if ( sourceFileType.size() > 1 )
            {
                report.getDetails().add( new ModelValidationReport.Detail(
                    "IMPLEMENTATION_SOURCE_FILE_MULTIPLICITY_CONSTRAINT", Level.SEVERE, getMessage(
                        "implementationSourceFileMultiplicityConstraint", module.getName(),
                        implementation.getIdentifier(), sourceFileType.size() ),
                    new org.jomc.model.ObjectFactory().createImplementation( implementation ) ) );

            }
            else if ( sourceFileType.size() == 1 )
            {
                report.getDetails().add( new ModelValidationReport.Detail(
                    "IMPLEMENTATION_SOURCE_FILE_INFORMATION", Level.INFO, getMessage(
                        "implementationSourceFileInfo", module.getName(), implementation.getIdentifier(),
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
                        "implementationSourceFilesMultiplicityConstraint", module.getName(),
                        implementation.getIdentifier(), sourceFilesType.size() ),
                    new org.jomc.model.ObjectFactory().createImplementation( implementation ) ) );

            }

            for ( final SourceFilesType l : sourceFilesType )
            {
                for ( final SourceFileType s : l.getSourceFile() )
                {
                    if ( this.isValidateJava() )
                    {
                        for ( final TemplateParameterType p : s.getTemplateParameter() )
                        {
                            try
                            {
                                p.getJavaValue( context.getClassLoader() );
                            }
                            catch ( final ModelObjectException e )
                            {
                                final String message = getMessage( e );

                                if ( context.isLoggable( Level.FINE ) )
                                {
                                    context.log( Level.FINE, message, e );
                                }

                                report.getDetails().add( new ModelValidationReport.Detail(
                                    "IMPLEMENTATION_SOURCE_FILE_TEMPLATE_PARAMETER_JAVA_VALUE_CONSTRAINT", Level.SEVERE,
                                    getMessage( "implementationSourceFileTemplateParameterJavaValueConstraint",
                                                module.getName(), implementation.getIdentifier(),
                                                s.getIdentifier(), p.getName(),
                                                message != null && message.length() > 0 ? " " + message : "" ),
                                    new org.jomc.model.ObjectFactory().createImplementation( implementation ) ) );

                            }
                        }
                    }

                    this.validateTemplateParameters(
                        report, context, s.getSourceSections(),
                        "IMPLEMENTATION_SOURCE_FILE_SECTION_TEMPLATE_PARAMETER_JAVA_VALUE_CONSTRAINT",
                        new org.jomc.model.ObjectFactory().createImplementation( implementation ),
                        "implementationSourceFileSectionTemplateParameterJavaValueConstraint",
                        module.getName(), implementation.getIdentifier(), s.getIdentifier() );

                }
            }
        }

        if ( sourceSectionType != null )
        {
            for ( final SourceSectionType s : sourceSectionType )
            {
                report.getDetails().add( new ModelValidationReport.Detail(
                    "IMPLEMENTATION_SOURCE_SECTION_CONSTRAINT", Level.SEVERE, getMessage(
                        "implementationSourceSectionConstraint", module.getName(), implementation.getIdentifier(),
                        s.getName() ), new org.jomc.model.ObjectFactory().createImplementation( implementation ) ) );

                if ( this.isValidateJava() )
                {
                    for ( final TemplateParameterType p : s.getTemplateParameter() )
                    {
                        try
                        {
                            p.getJavaValue( context.getClassLoader() );
                        }
                        catch ( final ModelObjectException e )
                        {
                            final String message = getMessage( e );

                            if ( context.isLoggable( Level.FINE ) )
                            {
                                context.log( Level.FINE, message, e );
                            }

                            report.getDetails().add( new ModelValidationReport.Detail(
                                "IMPLEMENTATION_SOURCE_SECTION_TEMPLATE_PARAMETER_JAVA_VALUE_CONSTRAINT", Level.SEVERE,
                                getMessage( "implementationSourceSectionTemplateParameterJavaValueConstraint",
                                            module.getName(), implementation.getIdentifier(),
                                            s.getName(), p.getName(),
                                            message != null && message.length() > 0 ? " " + message : "" ),
                                new org.jomc.model.ObjectFactory().createImplementation( implementation ) ) );

                        }
                    }
                }

                this.validateTemplateParameters(
                    report, context, s.getSourceSections(),
                    "IMPLEMENTATION_SOURCE_SECTION_TEMPLATE_PARAMETER_JAVA_VALUE_CONSTRAINT",
                    new org.jomc.model.ObjectFactory().createImplementation( implementation ),
                    "implementationSourceSectionTemplateParameterJavaValueConstraint",
                    module.getName(), implementation.getIdentifier() );

            }
        }

        if ( sourceSectionsType != null )
        {
            for ( final SourceSectionsType sections : sourceSectionsType )
            {
                for ( final SourceSectionType s : sections.getSourceSection() )
                {
                    report.getDetails().add( new ModelValidationReport.Detail(
                        "IMPLEMENTATION_SOURCE_SECTION_CONSTRAINT", Level.SEVERE, getMessage(
                            "implementationSourceSectionConstraint", module.getName(), implementation.getIdentifier(),
                            s.getName() ),
                        new org.jomc.model.ObjectFactory().createImplementation( implementation ) ) );

                    if ( this.isValidateJava() )
                    {
                        for ( final TemplateParameterType p : s.getTemplateParameter() )
                        {
                            try
                            {
                                p.getJavaValue( context.getClassLoader() );
                            }
                            catch ( final ModelObjectException e )
                            {
                                final String message = getMessage( e );

                                if ( context.isLoggable( Level.FINE ) )
                                {
                                    context.log( Level.FINE, message, e );
                                }

                                report.getDetails().add( new ModelValidationReport.Detail(
                                    "IMPLEMENTATION_SOURCE_SECTION_TEMPLATE_PARAMETER_JAVA_VALUE_CONSTRAINT",
                                    Level.SEVERE, getMessage(
                                        "implementationSourceSectionTemplateParameterJavaValueConstraint",
                                        module.getName(), implementation.getIdentifier(), s.getName(), p.getName(),
                                        message != null && message.length() > 0 ? " " + message : "" ),
                                    new org.jomc.model.ObjectFactory().createImplementation( implementation ) ) );

                            }
                        }
                    }

                    this.validateTemplateParameters(
                        report, context, s.getSourceSections(),
                        "IMPLEMENTATION_SOURCE_SECTION_TEMPLATE_PARAMETER_JAVA_VALUE_CONSTRAINT",
                        new org.jomc.model.ObjectFactory().createImplementation( implementation ),
                        "implementationSourceSectionTemplateParameterJavaValueConstraint",
                        module.getName(), implementation.getIdentifier() );

                }

                if ( sections.getSourceSection().isEmpty() )
                {
                    report.getDetails().add( new ModelValidationReport.Detail(
                        "IMPLEMENTATION_SOURCE_SECTIONS_CONSTRAINT", Level.SEVERE, getMessage(
                            "implementationSourceSectionsConstraint", module.getName(),
                            implementation.getIdentifier() ),
                        new org.jomc.model.ObjectFactory().createImplementation( implementation ) ) );

                }
            }
        }

        if ( implementation.getDependencies() != null )
        {
            this.assertValidToolsTypes( context, module, implementation, implementation.getDependencies(), report );
        }

        if ( implementation.getMessages() != null )
        {
            this.assertValidToolsTypes( context, module, implementation, implementation.getMessages(), report );
        }
    }

    private void assertValidToolsTypes( final ModelContext context, final Module module,
                                        final Implementation implementation, final Dependencies dependencies,
                                        final ModelValidationReport report )
    {
        for ( final Dependency d : dependencies.getDependency() )
        {
            final List<SourceFileType> sourceFileType = d.getAnyObjects( SourceFileType.class );
            final List<SourceFilesType> sourceFilesType = d.getAnyObjects( SourceFilesType.class );
            final List<SourceSectionType> sourceSectionType = d.getAnyObjects( SourceSectionType.class );
            final List<SourceSectionsType> sourceSectionsType = d.getAnyObjects( SourceSectionsType.class );

            if ( sourceFileType != null )
            {
                for ( final SourceFileType s : sourceFileType )
                {
                    report.getDetails().add( new ModelValidationReport.Detail(
                        "IMPLEMENTATION_DEPENDENCY_SOURCE_FILE_CONSTRAINT", Level.SEVERE, getMessage(
                            "dependencySourceFileConstraint", module.getName(), implementation.getIdentifier(),
                            d.getName(), s.getIdentifier() ),
                        new org.jomc.model.ObjectFactory().createImplementation( implementation ) ) );

                    if ( this.isValidateJava() )
                    {
                        for ( final TemplateParameterType p : s.getTemplateParameter() )
                        {
                            try
                            {
                                p.getJavaValue( context.getClassLoader() );
                            }
                            catch ( final ModelObjectException e )
                            {
                                final String message = getMessage( e );

                                if ( context.isLoggable( Level.FINE ) )
                                {
                                    context.log( Level.FINE, message, e );
                                }

                                report.getDetails().add( new ModelValidationReport.Detail(
                                    "IMPLEMENTATION_DEPENDENCY_SOURCE_FILE_TEMPLATE_PARAMETER_JAVA_VALUE_CONSTRAINT",
                                    Level.SEVERE, getMessage(
                                        "dependencySourceFileTemplateParameterJavaValueConstraint",
                                        module.getName(), implementation.getIdentifier(), d.getName(),
                                        s.getIdentifier(), p.getName(),
                                        message != null && message.length() > 0 ? " " + message : "" ),
                                    new org.jomc.model.ObjectFactory().createImplementation( implementation ) ) );

                            }
                        }
                    }

                    this.validateTemplateParameters(
                        report, context, s.getSourceSections(),
                        "IMPLEMENTATION_DEPENDENCY_SOURCE_FILE_SECTION_TEMPLATE_PARAMETER_JAVA_VALUE_CONSTRAINT",
                        new org.jomc.model.ObjectFactory().createImplementation( implementation ),
                        "dependencySourceFileSectionTemplateParameterJavaValueConstraint",
                        module.getName(), implementation.getIdentifier(), d.getName(), s.getIdentifier() );

                }
            }

            if ( sourceFilesType != null )
            {
                for ( final SourceFilesType files : sourceFilesType )
                {
                    for ( final SourceFileType s : files.getSourceFile() )
                    {
                        report.getDetails().add( new ModelValidationReport.Detail(
                            "IMPLEMENTATION_DEPENDENCY_SOURCE_FILE_CONSTRAINT", Level.SEVERE, getMessage(
                                "dependencySourceFileConstraint", module.getName(), implementation.getIdentifier(),
                                d.getName(), s.getIdentifier() ),
                            new org.jomc.model.ObjectFactory().createImplementation( implementation ) ) );

                        if ( this.isValidateJava() )
                        {
                            for ( final TemplateParameterType p : s.getTemplateParameter() )
                            {
                                try
                                {
                                    p.getJavaValue( context.getClassLoader() );
                                }
                                catch ( final ModelObjectException e )
                                {
                                    final String message = getMessage( e );

                                    if ( context.isLoggable( Level.FINE ) )
                                    {
                                        context.log( Level.FINE, message, e );
                                    }

                                    report.getDetails().add( new ModelValidationReport.Detail(
                                        "IMPLEMENTATION_DEPENDENCY_SOURCE_FILE_TEMPLATE_PARAMETER_JAVA_VALUE_CONSTRAINT",
                                        Level.SEVERE, getMessage(
                                            "dependencySourceFileTemplateParameterJavaValueConstraint",
                                            module.getName(), implementation.getIdentifier(), d.getName(),
                                            s.getIdentifier(), p.getName(),
                                            message != null && message.length() > 0 ? " " + message : "" ),
                                        new org.jomc.model.ObjectFactory().createImplementation( implementation ) ) );

                                }
                            }
                        }

                        this.validateTemplateParameters(
                            report, context, s.getSourceSections(),
                            "IMPLEMENTATION_DEPENDENCY_SOURCE_FILE_SECTION_TEMPLATE_PARAMETER_JAVA_VALUE_CONSTRAINT",
                            new org.jomc.model.ObjectFactory().createImplementation( implementation ),
                            "dependencySourceFileSectionTemplateParameterJavaValueConstraint",
                            module.getName(), implementation.getIdentifier(), d.getName(), s.getIdentifier() );

                    }

                    if ( files.getSourceFile().isEmpty() )
                    {
                        report.getDetails().add( new ModelValidationReport.Detail(
                            "IMPLEMENTATION_DEPENDENCY_SOURCE_FILES_CONSTRAINT", Level.SEVERE, getMessage(
                                "dependencySourceFilesConstraint", module.getName(), implementation.getIdentifier(),
                                d.getName() ),
                            new org.jomc.model.ObjectFactory().createImplementation( implementation ) ) );

                    }
                }
            }

            if ( sourceSectionType != null )
            {
                for ( final SourceSectionType s : sourceSectionType )
                {
                    report.getDetails().add( new ModelValidationReport.Detail(
                        "IMPLEMENTATION_DEPENDENCY_SOURCE_SECTION_CONSTRAINT", Level.SEVERE, getMessage(
                            "dependencySourceSectionConstraint", module.getName(), implementation.getIdentifier(),
                            d.getName(), s.getName() ),
                        new org.jomc.model.ObjectFactory().createImplementation( implementation ) ) );

                    if ( this.isValidateJava() )
                    {
                        for ( final TemplateParameterType p : s.getTemplateParameter() )
                        {
                            try
                            {
                                p.getJavaValue( context.getClassLoader() );
                            }
                            catch ( final ModelObjectException e )
                            {
                                final String message = getMessage( e );

                                if ( context.isLoggable( Level.FINE ) )
                                {
                                    context.log( Level.FINE, message, e );
                                }

                                report.getDetails().add( new ModelValidationReport.Detail(
                                    "IMPLEMENTATION_DEPENDENCY_SOURCE_SECTION_TEMPLATE_PARAMETER_JAVA_VALUE_CONSTRAINT",
                                    Level.SEVERE, getMessage(
                                        "dependencySourceSectionTemplateParameterJavaValueConstraint",
                                        module.getName(), implementation.getIdentifier(), d.getName(),
                                        s.getName(), p.getName(),
                                        message != null && message.length() > 0 ? " " + message : "" ),
                                    new org.jomc.model.ObjectFactory().createImplementation( implementation ) ) );

                            }
                        }
                    }

                    this.validateTemplateParameters(
                        report, context, s.getSourceSections(),
                        "IMPLEMENTATION_DEPENDENCY_SOURCE_SECTION_TEMPLATE_PARAMETER_JAVA_VALUE_CONSTRAINT",
                        new org.jomc.model.ObjectFactory().createImplementation( implementation ),
                        "dependencySourceSectionTemplateParameterJavaValueConstraint",
                        module.getName(), implementation.getIdentifier(), d.getName() );

                }
            }

            if ( sourceSectionsType != null )
            {
                for ( final SourceSectionsType sections : sourceSectionsType )
                {
                    for ( final SourceSectionType s : sections.getSourceSection() )
                    {
                        report.getDetails().add( new ModelValidationReport.Detail(
                            "IMPLEMENTATION_DEPENDENCY_SOURCE_SECTION_CONSTRAINT", Level.SEVERE, getMessage(
                                "dependencySourceSectionConstraint", module.getName(), implementation.getIdentifier(),
                                d.getName(), s.getName() ),
                            new org.jomc.model.ObjectFactory().createImplementation( implementation ) ) );

                        if ( this.isValidateJava() )
                        {
                            for ( final TemplateParameterType p : s.getTemplateParameter() )
                            {
                                try
                                {
                                    p.getJavaValue( context.getClassLoader() );
                                }
                                catch ( final ModelObjectException e )
                                {
                                    final String message = getMessage( e );

                                    if ( context.isLoggable( Level.FINE ) )
                                    {
                                        context.log( Level.FINE, message, e );
                                    }

                                    report.getDetails().add( new ModelValidationReport.Detail(
                                        "IMPLEMENTATION_DEPENDENCY_SOURCE_SECTION_TEMPLATE_PARAMETER_JAVA_VALUE_CONSTRAINT",
                                        Level.SEVERE, getMessage(
                                            "dependencySourceSectionTemplateParameterJavaValueConstraint",
                                            module.getName(), implementation.getIdentifier(), d.getName(),
                                            s.getName(), p.getName(),
                                            message != null && message.length() > 0 ? " " + message : "" ),
                                        new org.jomc.model.ObjectFactory().createImplementation( implementation ) ) );

                                }
                            }
                        }

                        this.validateTemplateParameters(
                            report, context, s.getSourceSections(),
                            "IMPLEMENTATION_DEPENDENCY_SOURCE_SECTION_TEMPLATE_PARAMETER_JAVA_VALUE_CONSTRAINT",
                            new org.jomc.model.ObjectFactory().createImplementation( implementation ),
                            "dependencySourceSectionTemplateParameterJavaValueConstraint",
                            module.getName(), implementation.getIdentifier(), d.getName() );

                    }

                    if ( sections.getSourceSection().isEmpty() )
                    {
                        report.getDetails().add( new ModelValidationReport.Detail(
                            "IMPLEMENTATION_DEPENDENCY_SOURCE_SECTIONS_CONSTRAINT", Level.SEVERE, getMessage(
                                "dependencySourceSectionsConstraint", module.getName(), implementation.getIdentifier(),
                                d.getName() ),
                            new org.jomc.model.ObjectFactory().createImplementation( implementation ) ) );

                    }
                }
            }
        }
    }

    private void assertValidToolsTypes( final ModelContext context, final Module module,
                                        final Implementation implementation, final Messages messages,
                                        final ModelValidationReport report )
    {
        for ( final Message m : messages.getMessage() )
        {
            final List<SourceFileType> sourceFileType = m.getAnyObjects( SourceFileType.class );
            final List<SourceFilesType> sourceFilesType = m.getAnyObjects( SourceFilesType.class );
            final List<SourceSectionType> sourceSectionType = m.getAnyObjects( SourceSectionType.class );
            final List<SourceSectionsType> sourceSectionsType = m.getAnyObjects( SourceSectionsType.class );

            if ( sourceFileType != null )
            {
                for ( final SourceFileType s : sourceFileType )
                {
                    report.getDetails().add( new ModelValidationReport.Detail(
                        "IMPLEMENTATION_MESSAGE_SOURCE_FILE_CONSTRAINT", Level.SEVERE, getMessage(
                            "messageSourceFileConstraint", module.getName(), implementation.getIdentifier(),
                            m.getName(), s.getIdentifier() ),
                        new org.jomc.model.ObjectFactory().createImplementation( implementation ) ) );

                    if ( this.isValidateJava() )
                    {
                        for ( final TemplateParameterType p : s.getTemplateParameter() )
                        {
                            try
                            {
                                p.getJavaValue( context.getClassLoader() );
                            }
                            catch ( final ModelObjectException e )
                            {
                                final String message = getMessage( e );

                                if ( context.isLoggable( Level.FINE ) )
                                {
                                    context.log( Level.FINE, message, e );
                                }

                                report.getDetails().add( new ModelValidationReport.Detail(
                                    "IMPLEMENTATION_MESSAGE_SOURCE_FILE_TEMPLATE_PARAMETER_JAVA_VALUE_CONSTRAINT",
                                    Level.SEVERE, getMessage(
                                        "messageSourceFileTemplateParameterJavaValueConstraint",
                                        module.getName(), implementation.getIdentifier(), m.getName(),
                                        s.getIdentifier(), p.getName(),
                                        message != null && message.length() > 0 ? " " + message : "" ),
                                    new org.jomc.model.ObjectFactory().createImplementation( implementation ) ) );

                            }
                        }
                    }

                    this.validateTemplateParameters(
                        report, context, s.getSourceSections(),
                        "IMPLEMENTATION_MESSAGE_SOURCE_FILE_SECTION_TEMPLATE_PARAMETER_JAVA_VALUE_CONSTRAINT",
                        new org.jomc.model.ObjectFactory().createImplementation( implementation ),
                        "messageSourceFileSectionTemplateParameterJavaValueConstraint",
                        module.getName(), implementation.getIdentifier(), m.getName(), s.getIdentifier() );

                }
            }

            if ( sourceFilesType != null )
            {
                for ( final SourceFilesType files : sourceFilesType )
                {
                    for ( final SourceFileType s : files.getSourceFile() )
                    {
                        report.getDetails().add( new ModelValidationReport.Detail(
                            "IMPLEMENTATION_MESSAGE_SOURCE_FILE_CONSTRAINT", Level.SEVERE, getMessage(
                                "messageSourceFileConstraint", module.getName(), implementation.getIdentifier(),
                                m.getName(), s.getIdentifier() ),
                            new org.jomc.model.ObjectFactory().createImplementation( implementation ) ) );

                        if ( this.isValidateJava() )
                        {
                            for ( final TemplateParameterType p : s.getTemplateParameter() )
                            {
                                try
                                {
                                    p.getJavaValue( context.getClassLoader() );
                                }
                                catch ( final ModelObjectException e )
                                {
                                    final String message = getMessage( e );

                                    if ( context.isLoggable( Level.FINE ) )
                                    {
                                        context.log( Level.FINE, message, e );
                                    }

                                    report.getDetails().add( new ModelValidationReport.Detail(
                                        "IMPLEMENTATION_MESSAGE_SOURCE_FILE_TEMPLATE_PARAMETER_JAVA_VALUE_CONSTRAINT",
                                        Level.SEVERE, getMessage(
                                            "messageSourceFileTemplateParameterJavaValueConstraint",
                                            module.getName(), implementation.getIdentifier(), m.getName(),
                                            s.getIdentifier(), p.getName(),
                                            message != null && message.length() > 0 ? " " + message : "" ),
                                        new org.jomc.model.ObjectFactory().createImplementation( implementation ) ) );

                                }
                            }
                        }

                        this.validateTemplateParameters(
                            report, context, s.getSourceSections(),
                            "IMPLEMENTATION_MESSAGE_SOURCE_FILE_SECTION_TEMPLATE_PARAMETER_JAVA_VALUE_CONSTRAINT",
                            new org.jomc.model.ObjectFactory().createImplementation( implementation ),
                            "messageSourceFileSectionTemplateParameterJavaValueConstraint",
                            module.getName(), implementation.getIdentifier(), m.getName(), s.getIdentifier() );

                    }

                    if ( files.getSourceFile().isEmpty() )
                    {
                        report.getDetails().add( new ModelValidationReport.Detail(
                            "IMPLEMENTATION_MESSAGE_SOURCE_FILES_CONSTRAINT", Level.SEVERE, getMessage(
                                "messageSourceFilesConstraint", module.getName(), implementation.getIdentifier(),
                                m.getName() ),
                            new org.jomc.model.ObjectFactory().createImplementation( implementation ) ) );

                    }
                }
            }

            if ( sourceSectionType != null )
            {
                for ( final SourceSectionType s : sourceSectionType )
                {
                    report.getDetails().add( new ModelValidationReport.Detail(
                        "IMPLEMENTATION_MESSAGE_SOURCE_SECTION_CONSTRAINT", Level.SEVERE, getMessage(
                            "messageSourceSectionConstraint", module.getName(), implementation.getIdentifier(),
                            m.getName(), s.getName() ),
                        new org.jomc.model.ObjectFactory().createImplementation( implementation ) ) );

                    if ( this.isValidateJava() )
                    {
                        for ( final TemplateParameterType p : s.getTemplateParameter() )
                        {
                            try
                            {
                                p.getJavaValue( context.getClassLoader() );
                            }
                            catch ( final ModelObjectException e )
                            {
                                final String message = getMessage( e );

                                if ( context.isLoggable( Level.FINE ) )
                                {
                                    context.log( Level.FINE, message, e );
                                }

                                report.getDetails().add( new ModelValidationReport.Detail(
                                    "IMPLEMENTATION_MESSAGE_SOURCE_SECTION_TEMPLATE_PARAMETER_JAVA_VALUE_CONSTRAINT",
                                    Level.SEVERE, getMessage(
                                        "messageSourceSectionTemplateParameterJavaValueConstraint",
                                        module.getName(), implementation.getIdentifier(), m.getName(),
                                        s.getName(), p.getName(),
                                        message != null && message.length() > 0 ? " " + message : "" ),
                                    new org.jomc.model.ObjectFactory().createImplementation( implementation ) ) );

                            }
                        }
                    }

                    this.validateTemplateParameters(
                        report, context, s.getSourceSections(),
                        "IMPLEMENTATION_MESSAGE_SOURCE_SECTION_TEMPLATE_PARAMETER_JAVA_VALUE_CONSTRAINT",
                        new org.jomc.model.ObjectFactory().createImplementation( implementation ),
                        "messageSourceSectionTemplateParameterJavaValueConstraint",
                        module.getName(), implementation.getIdentifier(), m.getName() );

                }
            }

            if ( sourceSectionsType != null )
            {
                for ( final SourceSectionsType sections : sourceSectionsType )
                {
                    for ( final SourceSectionType s : sections.getSourceSection() )
                    {
                        report.getDetails().add( new ModelValidationReport.Detail(
                            "IMPLEMENTATION_MESSAGE_SOURCE_SECTION_CONSTRAINT", Level.SEVERE, getMessage(
                                "messageSourceSectionConstraint", module.getName(), implementation.getIdentifier(),
                                m.getName(), s.getName() ),
                            new org.jomc.model.ObjectFactory().createImplementation( implementation ) ) );

                        if ( this.isValidateJava() )
                        {
                            for ( final TemplateParameterType p : s.getTemplateParameter() )
                            {
                                try
                                {
                                    p.getJavaValue( context.getClassLoader() );
                                }
                                catch ( final ModelObjectException e )
                                {
                                    final String message = getMessage( e );

                                    if ( context.isLoggable( Level.FINE ) )
                                    {
                                        context.log( Level.FINE, message, e );
                                    }

                                    report.getDetails().add( new ModelValidationReport.Detail(
                                        "IMPLEMENTATION_MESSAGE_SOURCE_SECTION_TEMPLATE_PARAMETER_JAVA_VALUE_CONSTRAINT",
                                        Level.SEVERE, getMessage(
                                            "messageSourceSectionTemplateParameterJavaValueConstraint",
                                            module.getName(), implementation.getIdentifier(), m.getName(),
                                            s.getName(), p.getName(),
                                            message != null && message.length() > 0 ? " " + message : "" ),
                                        new org.jomc.model.ObjectFactory().createImplementation( implementation ) ) );

                                }
                            }
                        }

                        this.validateTemplateParameters(
                            report, context, s.getSourceSections(),
                            "IMPLEMENTATION_MESSAGE_SOURCE_SECTION_TEMPLATE_PARAMETER_JAVA_VALUE_CONSTRAINT",
                            new org.jomc.model.ObjectFactory().createImplementation( implementation ),
                            "messageSourceSectionTemplateParameterJavaValueConstraint",
                            module.getName(), implementation.getIdentifier(), m.getName() );

                    }

                    if ( sections.getSourceSection().isEmpty() )
                    {
                        report.getDetails().add( new ModelValidationReport.Detail(
                            "IMPLEMENTATION_MESSAGE_SOURCE_SECTIONS_CONSTRAINT", Level.SEVERE, getMessage(
                                "messageSourceSectionsConstraint", module.getName(), implementation.getIdentifier(),
                                m.getName() ),
                            new org.jomc.model.ObjectFactory().createImplementation( implementation ) ) );

                    }
                }
            }
        }
    }

    private void assertValidToolsTypes( final ModelContext context, final Module module,
                                        final Specification specification, final ModelValidationReport report )
    {
        final List<SourceFileType> sourceFileType = specification.getAnyObjects( SourceFileType.class );
        final List<SourceFilesType> sourceFilesType = specification.getAnyObjects( SourceFilesType.class );
        final List<SourceSectionType> sourceSectionType = specification.getAnyObjects( SourceSectionType.class );
        final List<SourceSectionsType> sourceSectionsType = specification.getAnyObjects( SourceSectionsType.class );

        if ( sourceFileType != null )
        {
            for ( final SourceFileType s : sourceFileType )
            {
                if ( this.isValidateJava() )
                {
                    for ( final TemplateParameterType p : s.getTemplateParameter() )
                    {
                        try
                        {
                            p.getJavaValue( context.getClassLoader() );
                        }
                        catch ( final ModelObjectException e )
                        {
                            final String message = getMessage( e );

                            if ( context.isLoggable( Level.FINE ) )
                            {
                                context.log( Level.FINE, message, e );
                            }

                            report.getDetails().add( new ModelValidationReport.Detail(
                                "SPECIFICATION_SOURCE_FILE_TEMPLATE_PARAMETER_JAVA_VALUE_CONSTRAINT",
                                Level.SEVERE, getMessage(
                                    "specificationSourceFileTemplateParameterJavaValueConstraint",
                                    module.getName(), specification.getIdentifier(), s.getIdentifier(), p.getName(),
                                    message != null && message.length() > 0 ? " " + message : "" ),
                                new org.jomc.model.ObjectFactory().createSpecification( specification ) ) );

                        }
                    }
                }

                this.validateTemplateParameters(
                    report, context, s.getSourceSections(),
                    "SPECIFICATION_SOURCE_FILE_SECTION_TEMPLATE_PARAMETER_JAVA_VALUE_CONSTRAINT",
                    new org.jomc.model.ObjectFactory().createSpecification( specification ),
                    "specificationSourceFileSectionTemplateParameterJavaValueConstraint",
                    module.getName(), specification.getIdentifier(), s.getIdentifier() );

            }

            if ( sourceFileType.size() > 1 )
            {
                report.getDetails().add( new ModelValidationReport.Detail(
                    "SPECIFICATION_SOURCE_FILE_MULTIPLICITY_CONSTRAINT", Level.SEVERE, getMessage(
                        "specificationSourceFileMultiplicityConstraint", module.getName(),
                        specification.getIdentifier(), sourceFileType.size() ),
                    new org.jomc.model.ObjectFactory().createSpecification( specification ) ) );

            }
            else if ( sourceFileType.size() == 1 )
            {
                report.getDetails().add( new ModelValidationReport.Detail(
                    "SPECIFICATION_SOURCE_FILE_INFORMATION", Level.INFO, getMessage(
                        "specificationSourceFileInfo", module.getName(), specification.getIdentifier(),
                        sourceFileType.get( 0 ).getIdentifier() ),
                    new org.jomc.model.ObjectFactory().createSpecification( specification ) ) );

            }
        }

        if ( sourceFilesType != null )
        {
            for ( final SourceFilesType l : sourceFilesType )
            {
                for ( final SourceFileType s : l.getSourceFile() )
                {
                    if ( this.isValidateJava() )
                    {
                        for ( final TemplateParameterType p : s.getTemplateParameter() )
                        {
                            try
                            {
                                p.getJavaValue( context.getClassLoader() );
                            }
                            catch ( final ModelObjectException e )
                            {
                                final String message = getMessage( e );

                                if ( context.isLoggable( Level.FINE ) )
                                {
                                    context.log( Level.FINE, message, e );
                                }

                                report.getDetails().add( new ModelValidationReport.Detail(
                                    "SPECIFICATION_SOURCE_FILE_TEMPLATE_PARAMETER_JAVA_VALUE_CONSTRAINT",
                                    Level.SEVERE, getMessage(
                                        "specificationSourceFileTemplateParameterJavaValueConstraint",
                                        module.getName(), specification.getIdentifier(), s.getIdentifier(), p.getName(),
                                        message != null && message.length() > 0 ? " " + message : "" ),
                                    new org.jomc.model.ObjectFactory().createSpecification( specification ) ) );

                            }
                        }
                    }

                    this.validateTemplateParameters(
                        report, context, s.getSourceSections(),
                        "SPECIFICATION_SOURCE_FILE_SECTION_TEMPLATE_PARAMETER_JAVA_VALUE_CONSTRAINT",
                        new org.jomc.model.ObjectFactory().createSpecification( specification ),
                        "specificationSourceFileSectionTemplateParameterJavaValueConstraint",
                        module.getName(), specification.getIdentifier(), s.getIdentifier() );

                }
            }

            if ( sourceFilesType.size() > 1 )
            {
                report.getDetails().add( new ModelValidationReport.Detail(
                    "SPECIFICATION_SOURCE_FILES_MULTIPLICITY_CONSTRAINT", Level.SEVERE, getMessage(
                        "specificationSourceFilesMultiplicityConstraint", module.getName(),
                        specification.getIdentifier(), sourceFilesType.size() ),
                    new org.jomc.model.ObjectFactory().createSpecification( specification ) ) );

            }
        }

        if ( sourceSectionType != null )
        {
            for ( final SourceSectionType s : sourceSectionType )
            {
                report.getDetails().add( new ModelValidationReport.Detail(
                    "SPECIFICATION_SOURCE_SECTION_CONSTRAINT", Level.SEVERE, getMessage(
                        "specificationSourceSectionConstraint", specification.getIdentifier(), s.getName() ),
                    new org.jomc.model.ObjectFactory().createSpecification( specification ) ) );

                if ( this.isValidateJava() )
                {
                    for ( final TemplateParameterType p : s.getTemplateParameter() )
                    {
                        try
                        {
                            p.getJavaValue( context.getClassLoader() );
                        }
                        catch ( final ModelObjectException e )
                        {
                            final String message = getMessage( e );

                            if ( context.isLoggable( Level.FINE ) )
                            {
                                context.log( Level.FINE, message, e );
                            }

                            report.getDetails().add( new ModelValidationReport.Detail(
                                "SPECIFICATION_SOURCE_SECTION_TEMPLATE_PARAMETER_JAVA_VALUE_CONSTRAINT",
                                Level.SEVERE, getMessage(
                                    "specificationSourceSectionTemplateParameterJavaValueConstraint",
                                    module.getName(), specification.getIdentifier(), s.getName(), p.getName(),
                                    message != null && message.length() > 0 ? " " + message : "" ),
                                new org.jomc.model.ObjectFactory().createSpecification( specification ) ) );

                        }
                    }
                }

                this.validateTemplateParameters(
                    report, context, s.getSourceSections(),
                    "SPECIFICATION_SOURCE_SECTION_TEMPLATE_PARAMETER_JAVA_VALUE_CONSTRAINT",
                    new org.jomc.model.ObjectFactory().createSpecification( specification ),
                    "specificationSourceSectionTemplateParameterJavaValueConstraint",
                    module.getName(), specification.getIdentifier() );

            }
        }

        if ( sourceSectionsType != null )
        {
            for ( final SourceSectionsType sections : sourceSectionsType )
            {
                for ( final SourceSectionType s : sections.getSourceSection() )
                {
                    report.getDetails().add( new ModelValidationReport.Detail(
                        "SPECIFICATION_SOURCE_SECTION_CONSTRAINT", Level.SEVERE, getMessage(
                            "specificationSourceSectionConstraint", specification.getIdentifier(), s.getName() ),
                        new org.jomc.model.ObjectFactory().createSpecification( specification ) ) );

                    if ( this.isValidateJava() )
                    {
                        for ( final TemplateParameterType p : s.getTemplateParameter() )
                        {
                            try
                            {
                                p.getJavaValue( context.getClassLoader() );
                            }
                            catch ( final ModelObjectException e )
                            {
                                final String message = getMessage( e );

                                if ( context.isLoggable( Level.FINE ) )
                                {
                                    context.log( Level.FINE, message, e );
                                }

                                report.getDetails().add( new ModelValidationReport.Detail(
                                    "SPECIFICATION_SOURCE_SECTION_TEMPLATE_PARAMETER_JAVA_VALUE_CONSTRAINT",
                                    Level.SEVERE, getMessage(
                                        "specificationSourceSectionTemplateParameterJavaValueConstraint",
                                        module.getName(), specification.getIdentifier(), s.getName(), p.getName(),
                                        message != null && message.length() > 0 ? " " + message : "" ),
                                    new org.jomc.model.ObjectFactory().createSpecification( specification ) ) );

                            }
                        }
                    }

                    this.validateTemplateParameters(
                        report, context, s.getSourceSections(),
                        "SPECIFICATION_SOURCE_SECTION_TEMPLATE_PARAMETER_JAVA_VALUE_CONSTRAINT",
                        new org.jomc.model.ObjectFactory().createSpecification( specification ),
                        "specificationSourceSectionTemplateParameterJavaValueConstraint",
                        module.getName(), specification.getIdentifier() );

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

    private void validateTemplateParameters( final ModelValidationReport report, final ModelContext context,
                                             final SourceSectionsType sourceSectionsType,
                                             final String detailIdentifier,
                                             final JAXBElement<?> detailElement,
                                             final String messageKey, final Object... messageArguments )
    {
        if ( sourceSectionsType != null )
        {
            if ( this.isValidateJava() )
            {
                for ( final SourceSectionType s : sourceSectionsType.getSourceSection() )
                {
                    for ( final TemplateParameterType p : s.getTemplateParameter() )
                    {
                        try
                        {
                            p.getJavaValue( context.getClassLoader() );
                        }
                        catch ( final ModelObjectException e )
                        {
                            final String message = getMessage( e );

                            if ( context.isLoggable( Level.FINE ) )
                            {
                                context.log( Level.FINE, message, e );
                            }

                            final List<Object> arguments = new ArrayList<>( Arrays.asList( messageArguments ) );
                            arguments.add( s.getName() );
                            arguments.add( p.getName() );
                            arguments.add( message != null && message.length() > 0 ? " " + message : "" );

                            report.getDetails().add( new ModelValidationReport.Detail(
                                detailIdentifier, Level.SEVERE, getMessage(
                                    messageKey, arguments.toArray( new Object[ arguments.size() ] ) ),
                                detailElement ) );

                        }
                    }

                    this.validateTemplateParameters( report, context, s.getSourceSections(), detailIdentifier,
                                                     detailElement, messageKey, messageArguments );

                }
            }
        }
    }

    private static String getMessage( final String key, final Object... args )
    {
        return MessageFormat.format( ResourceBundle.getBundle(
            ToolsModelValidator.class.getName().replace( '.', '/' ), Locale.getDefault() ).getString( key ), args );

    }

    private static String getMessage( final Throwable t )
    {
        return t != null
                   ? t.getMessage() != null && t.getMessage().trim().length() > 0
                         ? t.getMessage()
                         : getMessage( t.getCause() )
                   : null;

    }

}
