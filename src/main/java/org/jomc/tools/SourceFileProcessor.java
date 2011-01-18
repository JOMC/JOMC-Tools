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
package org.jomc.tools;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.exception.VelocityException;
import org.jomc.model.Dependencies;
import org.jomc.model.Implementation;
import org.jomc.model.Messages;
import org.jomc.model.Module;
import org.jomc.model.Properties;
import org.jomc.model.Specification;
import org.jomc.model.Specifications;
import org.jomc.tools.model.SourceFileType;
import org.jomc.tools.model.SourceFilesType;
import org.jomc.tools.model.SourceSectionType;
import org.jomc.tools.model.SourceSectionsType;
import org.jomc.util.LineEditor;
import org.jomc.util.Section;
import org.jomc.util.SectionEditor;
import org.jomc.util.TrailingWhitespaceEditor;

/**
 * Processes source code files.
 *
 * <p><b>Use cases</b><br/><ul>
 * <li>{@link #manageSourceFiles(File) }</li>
 * <li>{@link #manageSourceFiles(Module, File) }</li>
 * <li>{@link #manageSourceFiles(Specification, File) }</li>
 * <li>{@link #manageSourceFiles(Implementation, File) }</li>
 * </ul></p>
 *
 * @author <a href="mailto:schulte2005@users.sourceforge.net">Christian Schulte</a>
 * @version $Id$
 */
public class SourceFileProcessor extends JomcTool
{

    /** Constant for the name of the constructors source code section. */
    private static final String CONSTRUCTORS_SECTION_NAME = "Constructors";

    /** Constant for the name of the default constructor source code section. */
    private static final String DEFAULT_CONSTRUCTOR_SECTION_NAME = "Default Constructor";

    /** Constant for the name of the dependencies source code section. */
    private static final String DEPENDENCIES_SECTION_NAME = "Dependencies";

    /** Constant for the name of the properties source code section. */
    private static final String PROPERTIES_SECTION_NAME = "Properties";

    /** Constant for the name of the messages source code section. */
    private static final String MESSAGES_SECTION_NAME = "Messages";

    /** Constant for the name of the license source code section. */
    private static final String LICENSE_SECTION_NAME = "License Header";

    /** Constant for the name of the documentation source code section. */
    private static final String DOCUMENTATION_SECTION_NAME = "Documentation";

    /** Constant for the name of the implementation annotations source code section. */
    private static final String ANNOTATIONS_SECTION_NAME = "Annotations";

    /** Name of the {@code implementation-constructors-head.vm} template. */
    private static final String CONSTRUCTORS_HEAD_TEMPLATE = "implementation-constructors-head.vm";

    /** Name of the {@code implementation-constructors-tail.vm} template. */
    private static final String CONSTRUCTORS_TAIL_TEMPLATE = "implementation-constructors-tail.vm";

    /** Name of the {@code implementation-default-constructor.vm} template. */
    private static final String DEFAULT_CONSTRUCTOR_TEMPLATE = "implementation-default-constructor.vm";

    /** Name of the {@code implementation-dependencies.vm} template. */
    private static final String DEPENDENCIES_TEMPLATE = "implementation-dependencies.vm";

    /** Name of the {@code implementation-properties.vm} template. */
    private static final String PROPERTIES_TEMPLATE = "implementation-properties.vm";

    /** Name of the {@code implementation-messages.vm} template. */
    private static final String MESSAGES_TEMPLATE = "implementation-messages.vm";

    /** Name of the {@code specification-license.vm} template. */
    private static final String SPECIFICATION_LICENSE_TEMPLATE = "specification-license.vm";

    /** Name of the {@code implementation-license.vm} template. */
    private static final String IMPLEMENTATION_LICENSE_TEMPLATE = "implementation-license.vm";

    /** Name of the {@code specification-documentation.vm} template. */
    private static final String SPECIFICATION_DOCUMENTATION_TEMPLATE = "specification-documentation.vm";

    /** Name of the {@code implementation-documentation.vm} template. */
    private static final String IMPLEMENTATION_DOCUMENTATION_TEMPLATE = "implementation-documentation.vm";

    /** Name of the {@code Implementation.java.vm} template. */
    private static final String IMPLEMENTATION_TEMPLATE = "Implementation.java.vm";

    /** Name of the {@code Specification.java.vm} template. */
    private static final String SPECIFICATION_TEMPLATE = "Specification.java.vm";

    /** Name of the {@code specification-annotations.vm} template. */
    private static final String SPECIFICATION_ANNOTATIONS_TEMPLATE = "specification-annotations.vm";

    /** Name of the {@code implementation-annotations.vm} template. */
    private static final String IMPLEMENTATION_ANNOTATIONS_TEMPLATE = "implementation-annotations.vm";

    /** Source files model. */
    @Deprecated
    private SourceFilesType sourceFilesType;

    /** Creates a new {@code SourceFileProcessor} instance. */
    public SourceFileProcessor()
    {
        super();
    }

    /**
     * Creates a new {@code SourceFileProcessor} instance taking a {@code SourceFileProcessor} instance to initialize
     * the instance with.
     *
     * @param tool The instance to initialize the new instance with,
     *
     * @throws NullPointerException if {@code tool} is {@code null}.
     * @throws IOException if copying {@code tool} fails.
     */
    public SourceFileProcessor( final SourceFileProcessor tool ) throws IOException
    {
        super( tool );
        this.sourceFilesType = tool.sourceFilesType != null ? new SourceFilesType( tool.sourceFilesType ) : null;
    }

    /**
     * Gets the source files model of the instance.
     * <p>This accessor method returns a reference to the live object, not a snapshot. Therefore any modification you
     * make to the returned object will be present inside the object. This is why there is no {@code set} method.</p>
     *
     * @return The source files model of the instance.
     *
     * @see #getSourceFileType(org.jomc.model.Specification)
     * @see #getSourceFileType(org.jomc.model.Implementation)
     *
     * @deprecated As of JOMC 1.2, please add source file models to {@code Specification}s and {@code Implementation}s directly.
     */
    @Deprecated
    public SourceFilesType getSourceFilesType()
    {
        if ( this.sourceFilesType == null )
        {
            this.sourceFilesType = new SourceFilesType();
        }

        return this.sourceFilesType;
    }

    /**
     * Gets the model of a specification source file of the modules of the instance.
     *
     * @param specification The specification to get a source file model for.
     *
     * @return The source file model for {@code specification}.
     *
     * @throws NullPointerException if {@code specification} is {@code null}.
     *
     * @deprecated As of JOMC 1.2, please use method {@link #getSourceFilesType(org.jomc.model.Specification)}.
     */
    @Deprecated
    public SourceFileType getSourceFileType( final Specification specification )
    {
        if ( specification == null )
        {
            throw new NullPointerException( "specification" );
        }

        assert this.getModules().getSpecification( specification.getIdentifier() ) != null :
            "Specification '" + specification.getIdentifier() + "' not found.";

        SourceFileType sourceFileType = this.getSourceFilesType().getSourceFile( specification.getIdentifier() );

        if ( sourceFileType == null )
        {
            sourceFileType = specification.getAnyObject( SourceFileType.class );

            if ( sourceFileType != null )
            {
                if ( sourceFileType.getLocation() == null )
                {
                    // As of version 1.2, the 'location' attribute got updated from 'required' to 'optional'.
                    sourceFileType.setLocation( specification.getClazz().replace( '.', '/' ) + ".java" );
                }

                if ( sourceFileType.getHeadComment() == null )
                {
                    // The 'head-comment' and 'tail-comment' attributes got introduced in version 1.2.
                    sourceFileType.setHeadComment( "//" );
                }
            }
        }

        if ( sourceFileType == null )
        {
            sourceFileType = new SourceFileType();
            sourceFileType.setIdentifier( specification.getIdentifier() );
            sourceFileType.setLocation( specification.getClazz().replace( '.', '/' ) + ".java" );
            sourceFileType.setTemplate( SPECIFICATION_TEMPLATE );
            sourceFileType.setHeadComment( "//" );
            sourceFileType.setSourceSections( new SourceSectionsType() );

            SourceSectionType s = new SourceSectionType();
            s.setName( LICENSE_SECTION_NAME );
            s.setHeadTemplate( SPECIFICATION_LICENSE_TEMPLATE );
            s.setOptional( true );
            sourceFileType.getSourceSections().getSourceSection().add( s );

            s = new SourceSectionType();
            s.setName( ANNOTATIONS_SECTION_NAME );
            s.setHeadTemplate( SPECIFICATION_ANNOTATIONS_TEMPLATE );
            sourceFileType.getSourceSections().getSourceSection().add( s );

            s = new SourceSectionType();
            s.setName( DOCUMENTATION_SECTION_NAME );
            s.setHeadTemplate( SPECIFICATION_DOCUMENTATION_TEMPLATE );
            s.setOptional( true );
            sourceFileType.getSourceSections().getSourceSection().add( s );

            final String javaTypeName = this.getJavaTypeName( specification, false );
            if ( javaTypeName != null )
            {
                s = new SourceSectionType();
                s.setName( javaTypeName );
                s.setIndentationLevel( 1 );
                s.setEditable( true );
                sourceFileType.getSourceSections().getSourceSection().add( s );
            }
        }

        return sourceFileType;
    }

    /**
     * Gets the source files model of a specification of the modules of the instance.
     *
     * @param specification The specification to get a source files model for.
     *
     * @return The source files model for {@code specification}.
     *
     * @throws NullPointerException if {@code specification} is {@code null}.
     *
     * @since 1.2
     */
    public SourceFilesType getSourceFilesType( final Specification specification )
    {
        if ( specification == null )
        {
            throw new NullPointerException( "specification" );
        }

        assert this.getModules().getSpecification( specification.getIdentifier() ) != null :
            "Specification '" + specification.getIdentifier() + "' not found.";

        SourceFilesType model = specification.getAnyObject( SourceFilesType.class );

        if ( model == null )
        {
            model = new SourceFilesType();
            model.getSourceFile().add( this.getSourceFileType( specification ) );
        }
        else
        {
            model = this.applyDefaults( specification, model );
        }

        return model;
    }

    /**
     * Gets the model of an implementation source file of the modules of the instance.
     *
     * @param implementation The implementation to get a source file model for.
     *
     * @return The source file model for {@code implementation}.
     *
     * @throws NullPointerException if {@code implementation} is {@code null}.
     *
     * @deprecated As of JOMC 1.2, please use method {@link #getSourceFilesType(org.jomc.model.Implementation)}.
     */
    @Deprecated
    public SourceFileType getSourceFileType( final Implementation implementation )
    {
        if ( implementation == null )
        {
            throw new NullPointerException( "implementation" );
        }

        assert this.getModules().getImplementation( implementation.getIdentifier() ) != null :
            "Implementation '" + implementation.getIdentifier() + "' not found.";

        SourceFileType sourceFileType = this.getSourceFilesType().getSourceFile( implementation.getIdentifier() );

        if ( sourceFileType == null )
        {
            sourceFileType = implementation.getAnyObject( SourceFileType.class );

            if ( sourceFileType != null )
            {
                if ( sourceFileType.getLocation() == null )
                {
                    // As of version 1.2, the 'location' attribute got updated from 'required' to 'optional'.
                    sourceFileType.setLocation( implementation.getClazz().replace( '.', '/' ) + ".java" );
                }

                if ( sourceFileType.getHeadComment() == null )
                {
                    // The 'head-comment' and 'tail-comment' attributes got introduced in version 1.2.
                    sourceFileType.setHeadComment( "//" );
                }
            }
        }

        if ( sourceFileType == null )
        {
            final Specifications specifications = this.getModules().getSpecifications( implementation.getIdentifier() );
            final Dependencies dependencies = this.getModules().getDependencies( implementation.getIdentifier() );
            final Messages messages = this.getModules().getMessages( implementation.getIdentifier() );
            final Properties properties = this.getModules().getProperties( implementation.getIdentifier() );

            sourceFileType = new SourceFileType();
            sourceFileType.setIdentifier( implementation.getIdentifier() );
            sourceFileType.setLocation( implementation.getClazz().replace( '.', '/' ) + ".java" );
            sourceFileType.setTemplate( IMPLEMENTATION_TEMPLATE );
            sourceFileType.setHeadComment( "//" );
            sourceFileType.setSourceSections( new SourceSectionsType() );

            SourceSectionType s = new SourceSectionType();
            s.setName( LICENSE_SECTION_NAME );
            s.setHeadTemplate( IMPLEMENTATION_LICENSE_TEMPLATE );
            s.setOptional( true );
            sourceFileType.getSourceSections().getSourceSection().add( s );

            s = new SourceSectionType();
            s.setName( ANNOTATIONS_SECTION_NAME );
            s.setHeadTemplate( IMPLEMENTATION_ANNOTATIONS_TEMPLATE );
            sourceFileType.getSourceSections().getSourceSection().add( s );

            s = new SourceSectionType();
            s.setName( DOCUMENTATION_SECTION_NAME );
            s.setHeadTemplate( IMPLEMENTATION_DOCUMENTATION_TEMPLATE );
            s.setOptional( true );
            sourceFileType.getSourceSections().getSourceSection().add( s );

            for ( String interfaceName : this.getImplementedJavaTypeNames( implementation, false ) )
            {
                s = new SourceSectionType();
                s.setName( interfaceName );
                s.setIndentationLevel( 1 );
                s.setEditable( true );
                sourceFileType.getSourceSections().getSourceSection().add( s );
            }

            final String javaTypeName = this.getJavaTypeName( implementation, false );
            if ( javaTypeName != null )
            {
                s = new SourceSectionType();
                s.setName( javaTypeName );
                s.setIndentationLevel( 1 );
                s.setEditable( true );
                sourceFileType.getSourceSections().getSourceSection().add( s );
            }

            s = new SourceSectionType();
            s.setName( CONSTRUCTORS_SECTION_NAME );
            s.setIndentationLevel( 1 );
            s.setHeadTemplate( CONSTRUCTORS_HEAD_TEMPLATE );
            s.setTailTemplate( CONSTRUCTORS_TAIL_TEMPLATE );
            s.setOptional( specifications == null || ( specifications.getSpecification().isEmpty()
                                                       && specifications.getReference().isEmpty() ) );

            s.setSourceSections( new SourceSectionsType() );
            sourceFileType.getSourceSections().getSourceSection().add( s );

            final SourceSectionType defaultCtor = new SourceSectionType();
            defaultCtor.setName( DEFAULT_CONSTRUCTOR_SECTION_NAME );
            defaultCtor.setIndentationLevel( 2 );
            defaultCtor.setHeadTemplate( DEFAULT_CONSTRUCTOR_TEMPLATE );
            defaultCtor.setEditable( true );
            s.getSourceSections().getSourceSection().add( defaultCtor );

            s = new SourceSectionType();
            s.setName( DEPENDENCIES_SECTION_NAME );
            s.setIndentationLevel( 1 );
            s.setHeadTemplate( DEPENDENCIES_TEMPLATE );
            s.setOptional( dependencies == null || dependencies.getDependency().isEmpty() );
            sourceFileType.getSourceSections().getSourceSection().add( s );

            s = new SourceSectionType();
            s.setName( PROPERTIES_SECTION_NAME );
            s.setIndentationLevel( 1 );
            s.setHeadTemplate( PROPERTIES_TEMPLATE );
            s.setOptional( properties == null || properties.getProperty().isEmpty() );
            sourceFileType.getSourceSections().getSourceSection().add( s );

            s = new SourceSectionType();
            s.setName( MESSAGES_SECTION_NAME );
            s.setIndentationLevel( 1 );
            s.setHeadTemplate( MESSAGES_TEMPLATE );
            s.setOptional( messages == null || messages.getMessage().isEmpty() );
            sourceFileType.getSourceSections().getSourceSection().add( s );
        }

        return sourceFileType;
    }

    /**
     * Gets the source files model of an implementation of the modules of the instance.
     *
     * @param implementation The implementation to get a source files model for.
     *
     * @return The source files model for {@code implementation}.
     *
     * @throws NullPointerException if {@code implementation} is {@code null}.
     *
     * @since 1.2
     */
    public SourceFilesType getSourceFilesType( final Implementation implementation )
    {
        if ( implementation == null )
        {
            throw new NullPointerException( "implementation" );
        }

        assert this.getModules().getImplementation( implementation.getIdentifier() ) != null :
            "Implementation '" + implementation.getIdentifier() + "' not found.";

        SourceFilesType model = implementation.getAnyObject( SourceFilesType.class );

        if ( model == null )
        {
            model = new SourceFilesType();
            model.getSourceFile().add( this.getSourceFileType( implementation ) );
        }
        else
        {
            model = this.applyDefaults( implementation, model );
        }

        return model;
    }

    /**
     * Gets a new editor for editing source code files.
     *
     * @return A new editor for editing source code files.
     *
     * @since 1.2
     */
    public SourceFileEditor getSourceFileEditor()
    {
        return new SourceFileEditor( new TrailingWhitespaceEditor( this.getLineSeparator() ), this.getLineSeparator() );
    }

    /**
     * Gets a new editor for editing the source file of a given specification of the modules of the instance.
     *
     * @param specification The specification whose source file to edit.
     *
     * @return A new editor for editing the source file of {@code specification}.
     *
     * @throws NullPointerException if {@code specification} is {@code null}.
     *
     * @deprecated As of JOMC 1.2, please use method {@link #getSourceFileEditor()}.
     */
    @Deprecated
    public SourceFileEditor getSourceFileEditor( final Specification specification )
    {
        if ( specification == null )
        {
            throw new NullPointerException( "specification" );
        }

        assert this.getModules().getSpecification( specification.getIdentifier() ) != null :
            "Specification '" + specification.getIdentifier() + "' not found.";

        return this.getSourceFileEditor();
    }

    /**
     * Gets a new editor for editing the source file of a given implementation of the modules of the instance.
     *
     * @param implementation The implementation whose source file to edit.
     *
     * @return A new editor for editing the source file of {@code implementation}.
     *
     * @throws NullPointerException if {@code implementation} is {@code null}.
     *
     * @deprecated As of JOMC 1.2, please use method {@link #getSourceFileEditor()}.
     */
    @Deprecated
    public SourceFileEditor getSourceFileEditor( final Implementation implementation )
    {
        if ( implementation == null )
        {
            throw new NullPointerException( "implementation" );
        }

        assert this.getModules().getImplementation( implementation.getIdentifier() ) != null :
            "Implementation '" + implementation.getIdentifier() + "' not found.";

        return this.getSourceFileEditor();
    }

    /**
     * Manages the source files of the modules of the instance.
     *
     * @param sourcesDirectory The directory holding the source files to manage.
     *
     * @throws NullPointerException if {@code sourcesDirectory} is {@code null}.
     * @throws IOException if managing source files fails.
     *
     * @see #manageSourceFiles(org.jomc.model.Module, java.io.File)
     */
    public void manageSourceFiles( final File sourcesDirectory ) throws IOException
    {
        if ( sourcesDirectory == null )
        {
            throw new NullPointerException( "sourcesDirectory" );
        }

        for ( Module m : this.getModules().getModule() )
        {
            this.manageSourceFiles( m, sourcesDirectory );
        }
    }

    /**
     * Manages the source files of a given module of the modules of the instance.
     *
     * @param module The module to process.
     * @param sourcesDirectory The directory holding the source files to manage.
     *
     * @throws NullPointerException if {@code module} or {@code sourcesDirectory} is {@code null}.
     * @throws IOException if managing source files fails.
     *
     * @see #manageSourceFiles(org.jomc.model.Specification, java.io.File)
     * @see #manageSourceFiles(org.jomc.model.Implementation, java.io.File)
     */
    public void manageSourceFiles( final Module module, final File sourcesDirectory ) throws IOException
    {
        if ( module == null )
        {
            throw new NullPointerException( "module" );
        }
        if ( sourcesDirectory == null )
        {
            throw new NullPointerException( "sourcesDirectory" );
        }

        assert this.getModules().getModule( module.getName() ) != null : "Module '" + module.getName() + "' not found.";

        if ( module.getSpecifications() != null )
        {
            for ( Specification s : module.getSpecifications().getSpecification() )
            {
                this.manageSourceFiles( s, sourcesDirectory );
            }
        }
        if ( module.getImplementations() != null )
        {
            for ( Implementation i : module.getImplementations().getImplementation() )
            {
                this.manageSourceFiles( i, sourcesDirectory );
            }
        }
    }

    /**
     * Manages the source files of a given specification of the modules of the instance.
     *
     * @param specification The specification to process.
     * @param sourcesDirectory The directory holding the source files to manage.
     *
     * @throws NullPointerException if {@code specification} or {@code sourcesDirectory} is {@code null}.
     * @throws IOException if managing source files fails.
     *
     * @see #getSourceFileEditor()
     * @see #getSourceFilesType(org.jomc.model.Specification)
     */
    public void manageSourceFiles( final Specification specification, final File sourcesDirectory ) throws IOException
    {
        if ( specification == null )
        {
            throw new NullPointerException( "specification" );
        }
        if ( sourcesDirectory == null )
        {
            throw new NullPointerException( "sourcesDirectory" );
        }

        assert this.getModules().getSpecification( specification.getIdentifier() ) != null :
            "Specification '" + specification.getIdentifier() + "' not found.";

        final Implementation i = this.getModules().getImplementation( specification.getIdentifier() );

        if ( i != null && i.isClassDeclaration() )
        {
            this.manageSourceFiles( i, sourcesDirectory );
        }
        else if ( specification.isClassDeclaration() )
        {
            final SourceFileEditor editor = this.getSourceFileEditor( specification );
            final SourceFilesType model = this.getSourceFilesType( specification );

            if ( editor != null && model != null )
            {
                for ( SourceFileType sourceFileType : model.getSourceFile() )
                {
                    editor.edit( specification, sourceFileType, sourcesDirectory );
                }
            }
        }
    }

    /**
     * Manages the source files of a given implementation of the modules of the instance.
     *
     * @param implementation The implementation to process.
     * @param sourcesDirectory The directory holding the source files to manage.
     *
     * @throws NullPointerException if {@code implementation} or {@code sourcesDirectory} is {@code null}.
     * @throws IOException if managing source files fails.
     *
     * @see #getSourceFileEditor()
     * @see #getSourceFilesType(org.jomc.model.Implementation)
     */
    public void manageSourceFiles( final Implementation implementation, final File sourcesDirectory )
        throws IOException
    {
        if ( implementation == null )
        {
            throw new NullPointerException( "implementation" );
        }
        if ( sourcesDirectory == null )
        {
            throw new NullPointerException( "sourcesDirectory" );
        }

        assert this.getModules().getImplementation( implementation.getIdentifier() ) != null :
            "Implementation '" + implementation.getIdentifier() + "' not found.";

        if ( implementation.isClassDeclaration() )
        {
            final SourceFileEditor editor = this.getSourceFileEditor( implementation );
            final SourceFilesType model = this.getSourceFilesType( implementation );

            if ( editor != null && model != null )
            {
                for ( SourceFileType sourceFileType : model.getSourceFile() )
                {
                    editor.edit( implementation, sourceFileType, sourcesDirectory );
                }
            }
        }
    }

    /**
     * Updates any optional attributes to default values.
     *
     * @param specification The specification corresponding to {@code sourceFilesType}.
     * @param sourceFilesType The model to update.
     *
     * @return A copy of {@code sourceFilesType} with optional attributes updated to default values.
     */
    private SourceFilesType applyDefaults( final Specification specification, final SourceFilesType sourceFilesType )
    {
        final SourceFilesType types = new SourceFilesType( sourceFilesType );

        for ( SourceFileType s : types.getSourceFile() )
        {
            if ( s.getTemplate() == null )
            {
                s.setTemplate( SPECIFICATION_TEMPLATE );
            }
            if ( s.getLocation() == null )
            {
                s.setLocation( specification.getClazz().replace( '.', '/' ) + ".java" );
            }
            if ( s.getHeadComment() == null )
            {
                s.setHeadComment( "//" );
            }

            this.applyDefaults( specification, s.getSourceSections() );
        }

        return types;
    }

    /**
     * Updates any optional attributes to default values.
     *
     * @param specification The specification corresponding to {@code sourceSectionsType}.
     * @param sourceSectionsType The model to update or {@code null}.
     */
    private void applyDefaults( final Specification specification, final SourceSectionsType sourceSectionsType )
    {
        try
        {
            if ( sourceSectionsType != null )
            {
                for ( SourceSectionType s : sourceSectionsType.getSourceSection() )
                {
                    if ( LICENSE_SECTION_NAME.equals( s.getName() ) )
                    {
                        if ( !isFieldSet( s, "optional" ) )
                        {
                            s.setOptional( true );
                        }
                        if ( s.getHeadTemplate() == null )
                        {
                            s.setHeadTemplate( SPECIFICATION_LICENSE_TEMPLATE );
                        }
                    }

                    if ( ANNOTATIONS_SECTION_NAME.equals( s.getName() ) )
                    {
                        if ( s.getHeadTemplate() == null )
                        {
                            s.setHeadTemplate( SPECIFICATION_ANNOTATIONS_TEMPLATE );
                        }
                    }

                    if ( DOCUMENTATION_SECTION_NAME.equals( s.getName() ) )
                    {
                        if ( !isFieldSet( s, "optional" ) )
                        {
                            s.setOptional( true );
                        }
                        if ( s.getHeadTemplate() == null )
                        {
                            s.setHeadTemplate( SPECIFICATION_DOCUMENTATION_TEMPLATE );
                        }
                    }

                    final String javaTypeName = this.getJavaTypeName( specification, false );
                    if ( javaTypeName != null )
                    {
                        if ( javaTypeName.equals( s.getName() ) )
                        {
                            if ( !isFieldSet( s, "editable" ) )
                            {
                                s.setEditable( true );
                            }
                            if ( !isFieldSet( s, "indentationLevel" ) )
                            {
                                s.setIndentationLevel( 1 );
                            }
                        }
                    }

                    this.applyDefaults( specification, s.getSourceSections() );
                }
            }
        }
        catch ( final NoSuchFieldException e )
        {
            throw new AssertionError( e );
        }
    }

    /**
     * Updates any optional attributes to default values.
     *
     * @param implementation The implementation corresponding to {@code sourceFilesType}.
     * @param sourceFilesType The model to update.
     *
     * @return A copy of {@code sourceFilesType} with optional attributes updated to default values.
     */
    private SourceFilesType applyDefaults( final Implementation implementation, final SourceFilesType sourceFilesType )
    {
        final SourceFilesType types = new SourceFilesType( sourceFilesType );

        for ( SourceFileType s : types.getSourceFile() )
        {
            if ( s.getTemplate() == null )
            {
                s.setTemplate( IMPLEMENTATION_TEMPLATE );
            }
            if ( s.getLocation() == null )
            {
                s.setLocation( implementation.getClazz().replace( '.', '/' ) + ".java" );
            }
            if ( s.getHeadComment() == null )
            {
                s.setHeadComment( "//" );
            }

            this.applyDefaults( implementation, s.getSourceSections() );
        }

        return types;
    }

    /**
     * Updates any optional attributes to default values.
     *
     * @param implementation The implementation corresponding to {@code sourceSectionsType}.
     * @param sourceSectionsType The model to update or {@code null}.
     */
    private void applyDefaults( final Implementation implementation, final SourceSectionsType sourceSectionsType )
    {
        try
        {
            if ( sourceSectionsType != null )
            {
                for ( SourceSectionType s : sourceSectionsType.getSourceSection() )
                {
                    if ( LICENSE_SECTION_NAME.equals( s.getName() ) )
                    {
                        if ( !isFieldSet( s, "optional" ) )
                        {
                            s.setOptional( true );
                        }
                        if ( s.getHeadTemplate() == null )
                        {
                            s.setHeadTemplate( IMPLEMENTATION_LICENSE_TEMPLATE );
                        }
                    }

                    if ( ANNOTATIONS_SECTION_NAME.equals( s.getName() ) )
                    {
                        if ( s.getHeadTemplate() == null )
                        {
                            s.setHeadTemplate( IMPLEMENTATION_ANNOTATIONS_TEMPLATE );
                        }
                    }

                    if ( DOCUMENTATION_SECTION_NAME.equals( s.getName() ) )
                    {
                        if ( !isFieldSet( s, "optional" ) )
                        {
                            s.setOptional( true );
                        }
                        if ( s.getHeadTemplate() == null )
                        {
                            s.setHeadTemplate( IMPLEMENTATION_DOCUMENTATION_TEMPLATE );
                        }
                    }

                    if ( CONSTRUCTORS_SECTION_NAME.equals( s.getName() ) )
                    {
                        if ( !isFieldSet( s, "indentationLevel" ) )
                        {
                            s.setIndentationLevel( 1 );
                        }
                        if ( s.getHeadTemplate() == null )
                        {
                            s.setHeadTemplate( CONSTRUCTORS_HEAD_TEMPLATE );
                        }
                        if ( s.getTailTemplate() == null )
                        {
                            s.setTailTemplate( CONSTRUCTORS_TAIL_TEMPLATE );
                        }
                        if ( !isFieldSet( s, "optional" ) )
                        {
                            final Specifications specifications =
                                this.getModules().getSpecifications( implementation.getIdentifier() );

                            s.setOptional( specifications == null || ( specifications.getSpecification().isEmpty()
                                                                       && specifications.getReference().isEmpty() ) );

                        }
                    }

                    if ( DEFAULT_CONSTRUCTOR_SECTION_NAME.equals( s.getName() ) )
                    {
                        if ( !isFieldSet( s, "editable" ) )
                        {
                            s.setEditable( true );
                        }
                        if ( !isFieldSet( s, "indentationLevel" ) )
                        {
                            s.setIndentationLevel( 2 );
                        }
                        if ( s.getHeadTemplate() == null )
                        {
                            s.setHeadTemplate( DEFAULT_CONSTRUCTOR_TEMPLATE );
                        }
                    }

                    if ( DEPENDENCIES_SECTION_NAME.equals( s.getName() ) )
                    {
                        if ( !isFieldSet( s, "optional" ) )
                        {
                            final Dependencies dependencies =
                                this.getModules().getDependencies( implementation.getIdentifier() );

                            s.setOptional( dependencies == null || dependencies.getDependency().isEmpty() );
                        }
                        if ( !isFieldSet( s, "indentationLevel" ) )
                        {
                            s.setIndentationLevel( 1 );
                        }
                        if ( s.getHeadTemplate() == null )
                        {
                            s.setHeadTemplate( DEPENDENCIES_TEMPLATE );
                        }
                    }

                    if ( PROPERTIES_SECTION_NAME.equals( s.getName() ) )
                    {
                        if ( !isFieldSet( s, "optional" ) )
                        {
                            final Properties properties =
                                this.getModules().getProperties( implementation.getIdentifier() );

                            s.setOptional( properties == null || properties.getProperty().isEmpty() );
                        }
                        if ( !isFieldSet( s, "indentationLevel" ) )
                        {
                            s.setIndentationLevel( 1 );
                        }
                        if ( s.getHeadTemplate() == null )
                        {
                            s.setHeadTemplate( PROPERTIES_TEMPLATE );
                        }
                    }

                    if ( MESSAGES_SECTION_NAME.equals( s.getName() ) )
                    {
                        if ( !isFieldSet( s, "optional" ) )
                        {
                            final Messages messages = this.getModules().getMessages( implementation.getIdentifier() );
                            s.setOptional( messages == null || messages.getMessage().isEmpty() );
                        }
                        if ( !isFieldSet( s, "indentationLevel" ) )
                        {
                            s.setIndentationLevel( 1 );
                        }
                        if ( s.getHeadTemplate() == null )
                        {
                            s.setHeadTemplate( MESSAGES_TEMPLATE );
                        }
                    }

                    for ( String interfaceName : this.getImplementedJavaTypeNames( implementation, false ) )
                    {
                        if ( interfaceName.equals( s.getName() ) )
                        {
                            if ( !isFieldSet( s, "editable" ) )
                            {
                                s.setEditable( true );
                            }
                            if ( !isFieldSet( s, "indentationLevel" ) )
                            {
                                s.setIndentationLevel( 1 );
                            }
                        }
                    }

                    final String javaTypeName = this.getJavaTypeName( implementation, false );
                    if ( javaTypeName != null )
                    {
                        if ( javaTypeName.equals( s.getName() ) )
                        {
                            if ( !isFieldSet( s, "editable" ) )
                            {
                                s.setEditable( true );
                            }
                            if ( !isFieldSet( s, "indentationLevel" ) )
                            {
                                s.setIndentationLevel( 1 );
                            }
                        }
                    }

                    this.applyDefaults( implementation, s.getSourceSections() );
                }
            }
        }
        catch ( final NoSuchFieldException e )
        {
            throw new AssertionError( e );
        }
    }

    private static boolean isFieldSet( final Object object, final String fieldName ) throws NoSuchFieldException
    {
        final Field field = object.getClass().getDeclaredField( fieldName );
        final boolean accessible = field.isAccessible();

        try
        {
            field.setAccessible( true );
            return field.get( object ) != null;
        }
        catch ( final IllegalAccessException e )
        {
            throw new AssertionError( e );
        }
        finally
        {
            field.setAccessible( accessible );
        }
    }

    private static String getMessage( final String key, final Object... arguments )
    {
        if ( key == null )
        {
            throw new NullPointerException( "key" );
        }

        return MessageFormat.format( ResourceBundle.getBundle(
            SourceFileProcessor.class.getName().replace( '.', '/' ) ).getString( key ), arguments );

    }

    private static String getMessage( final Throwable t )
    {
        return t != null ? t.getMessage() != null ? t.getMessage() : getMessage( t.getCause() ) : null;
    }

    /**
     * Extension to {@code SectionEditor} adding support for editing source code files.
     *
     * @author <a href="mailto:schulte2005@users.sourceforge.net">Christian Schulte</a>
     * @version $Id$
     *
     * @see #edit(org.jomc.model.Specification, org.jomc.tools.model.SourceFileType, java.io.File)
     * @see #edit(org.jomc.model.Implementation, org.jomc.tools.model.SourceFileType, java.io.File)
     */
    public class SourceFileEditor extends SectionEditor
    {

        /** {@code Specification} of the instance or {@code null}. */
        private Specification specification;

        /** {@code Implementation} of the instance or {@code null}. */
        private Implementation implementation;

        /** The source code file to edit. */
        private SourceFileType sourceFileType;

        /** List of sections added to the input. */
        @Deprecated
        private List<Section> addedSections;

        /** List of sections without corresponding model entry. */
        @Deprecated
        private List<Section> unknownSections;

        /**
         * Creates a new {@code SourceFileEditor} instance.
         *
         * @since 1.2
         */
        public SourceFileEditor()
        {
            this( (LineEditor) null, (String) null );
        }

        /**
         * Creates a new {@code SourceFileEditor} instance taking a string to use for separating lines.
         *
         * @param lineSeparator String to use for separating lines.
         *
         * @since 1.2
         */
        public SourceFileEditor( final String lineSeparator )
        {
            this( (LineEditor) null, lineSeparator );
        }

        /**
         * Creates a new {@code SourceFileEditor} instance taking an editor to chain.
         *
         * @param editor The editor to chain.
         *
         * @since 1.2
         */
        public SourceFileEditor( final LineEditor editor )
        {
            this( editor, null );
        }

        /**
         * Creates a new {@code SourceFileEditor} instance taking an editor to chain and a string to use for separating
         * lines.
         *
         * @param editor The editor to chain.
         * @param lineSeparator String to use for separating lines.
         *
         * @since 1.2
         */
        public SourceFileEditor( final LineEditor editor, final String lineSeparator )
        {
            super( editor, lineSeparator );
        }

        /**
         * Creates a new {@code SourceFileEditor} taking a {@code Specification} to edit source code of.
         *
         * @param specification The specification to edit source code of.
         *
         * @deprecated As of JOMC 1.2, please use method {@link #edit(org.jomc.model.Specification, org.jomc.tools.model.SourceFileType, java.io.File)}.
         */
        @Deprecated
        public SourceFileEditor( final Specification specification )
        {
            this( specification, null, null );
        }

        /**
         * Creates a new {@code SourceFileEditor} taking a {@code Specification} to edit source code of and a line
         * separator.
         *
         * @param specification The specification to edit source code of.
         * @param lineSeparator The line separator of the editor.
         *
         * @deprecated As of JOMC 1.2, please use method {@link #edit(org.jomc.model.Specification, org.jomc.tools.model.SourceFileType, java.io.File)}.
         */
        @Deprecated
        public SourceFileEditor( final Specification specification, final String lineSeparator )
        {
            this( specification, null, lineSeparator );
        }

        /**
         * Creates a new {@code SourceFileEditor} taking a {@code Specification} to edit source code of and an editor to
         * chain.
         *
         * @param specification The specification backing the editor.
         * @param lineEditor The editor to chain.
         *
         * @deprecated As of JOMC 1.2, please use method {@link #edit(org.jomc.model.Specification, org.jomc.tools.model.SourceFileType, java.io.File)}.
         */
        @Deprecated
        public SourceFileEditor( final Specification specification, final LineEditor lineEditor )
        {
            this( specification, lineEditor, null );
        }

        /**
         * Creates a new {@code SourceFileEditor} taking a {@code Specification} to edit source code of, an editor to
         * chain and a line separator.
         *
         * @param specification The specification backing the editor.
         * @param lineEditor The editor to chain.
         * @param lineSeparator The line separator of the editor.
         *
         * @deprecated As of JOMC 1.2, please use method {@link #edit(org.jomc.model.Specification, org.jomc.tools.model.SourceFileType, java.io.File)}.
         */
        @Deprecated
        public SourceFileEditor( final Specification specification, final LineEditor lineEditor,
                                 final String lineSeparator )
        {
            super( lineEditor, lineSeparator );
            this.specification = specification;
            this.implementation = null;

            assert getModules().getSpecification( specification.getIdentifier() ) != null :
                "Specification '" + specification.getIdentifier() + "' not found.";

        }

        /**
         * Creates a new {@code SourceFileEditor} taking an {@code Implementation} to edit source code of.
         *
         * @param implementation The implementation to edit source code of.
         *
         * @deprecated As of JOMC 1.2, please use method {@link #edit(org.jomc.model.Implementation, org.jomc.tools.model.SourceFileType, java.io.File)}.
         */
        @Deprecated
        public SourceFileEditor( final Implementation implementation )
        {
            this( implementation, null, null );
        }

        /**
         * Creates a new {@code SourceFileEditor} taking an {@code Implementation} to edit source code of and a line
         * separator.
         *
         * @param implementation The implementation to edit source code of.
         * @param lineSeparator The line separator of the editor.
         *
         * @deprecated As of JOMC 1.2, please use method {@link #edit(org.jomc.model.Implementation, org.jomc.tools.model.SourceFileType, java.io.File)}.
         */
        @Deprecated
        public SourceFileEditor( final Implementation implementation, final String lineSeparator )
        {
            this( implementation, null, lineSeparator );
        }

        /**
         * Creates a new {@code SourceFileEditor} taking an {@code Implementation} to edit source code of and an editor
         * to chain.
         *
         * @param implementation The implementation to edit source code of.
         * @param lineEditor The editor to chain.
         *
         * @deprecated As of JOMC 1.2, please use method {@link #edit(org.jomc.model.Implementation, org.jomc.tools.model.SourceFileType, java.io.File)}.
         */
        @Deprecated
        public SourceFileEditor( final Implementation implementation, final LineEditor lineEditor )
        {
            this( implementation, lineEditor, null );
        }

        /**
         * Creates a new {@code SourceFileEditor} taking an {@code Implementation} to edit source code of, an editor
         * to chain and a line separator.
         *
         * @param implementation The implementation to edit source code of.
         * @param lineEditor The editor to chain.
         * @param lineSeparator The line separator of the editor.
         *
         * @deprecated As of JOMC 1.2, please use method {@link #edit(org.jomc.model.Implementation, org.jomc.tools.model.SourceFileType, java.io.File)}.
         */
        @Deprecated
        public SourceFileEditor( final Implementation implementation, final LineEditor lineEditor,
                                 final String lineSeparator )
        {
            super( lineEditor, lineSeparator );
            this.implementation = implementation;
            this.specification = null;

            assert getModules().getImplementation( implementation.getIdentifier() ) != null :
                "Implementation '" + implementation.getIdentifier() + "' not found.";

        }

        /**
         * Edits the source code of a given specification.
         *
         * @param specification The specification to edit source code of.
         * @param sourceFileType The source code file to edit.
         * @param sourcesDirectory The directory holding the source code file to edit.
         *
         * @throws NullPointerException if {@code specification}, {@code sourceFileType} or {@code sourcesDirectory} is
         * {@code null}.
         * @throws IOException if editing fails.
         *
         * @since 1.2
         */
        public final void edit( final Specification specification, final SourceFileType sourceFileType,
                                final File sourcesDirectory ) throws IOException
        {
            if ( specification == null )
            {
                throw new NullPointerException( "specification" );
            }
            if ( sourceFileType == null )
            {
                throw new NullPointerException( "sourceFileType" );
            }
            if ( sourcesDirectory == null )
            {
                throw new NullPointerException( "sourcesDirectory" );
            }

            assert getModules().getSpecification( specification.getIdentifier() ) != null :
                "Specification '" + specification.getIdentifier() + "' not found.";

            this.specification = specification;
            this.sourceFileType = sourceFileType;
            this.editSourceFile( sourcesDirectory );
        }

        /**
         * Edits the source code of a given implementation.
         *
         * @param implementation The implementation to edit source code of.
         * @param sourceFileType The source code file model.
         * @param sourcesDirectory The directory holding the source code file to edit.
         *
         * @throws NullPointerException if {@code implementation}, {@code sourceFileType} or {@code sourcesDirectory} is
         * {@code null}.
         * @throws IOException if editing fails.
         *
         * @since 1.2
         */
        public final void edit( final Implementation implementation, final SourceFileType sourceFileType,
                                final File sourcesDirectory ) throws IOException
        {
            if ( implementation == null )
            {
                throw new NullPointerException( "implementation" );
            }
            if ( sourceFileType == null )
            {
                throw new NullPointerException( "sourceFileType" );
            }
            if ( sourcesDirectory == null )
            {
                throw new NullPointerException( "sourcesDirectory" );
            }

            assert getModules().getImplementation( implementation.getIdentifier() ) != null :
                "Implementation '" + implementation.getIdentifier() + "' not found.";

            this.implementation = implementation;
            this.sourceFileType = sourceFileType;
            this.editSourceFile( sourcesDirectory );
        }

        /**
         * Gets a list of sections added to the input.
         * <p>This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you
         * make to the returned list will be present inside the object. This is why there is no {@code set} method
         * for the added sections property.</p>
         *
         * @return A list of sections added to the input.
         *
         * @deprecated As of JOMC 1.2, removed without replacement.
         */
        @Deprecated
        public List<Section> getAddedSections()
        {
            if ( this.addedSections == null )
            {
                this.addedSections = new LinkedList<Section>();
            }

            return this.addedSections;
        }

        /**
         * Gets a list of sections without corresponding model entry.
         * <p>This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you
         * make to the returned list will be present inside the object. This is why there is no {@code set} method
         * for the unknown sections property.</p>
         *
         * @return A list of sections without corresponding model entry.
         *
         * @deprecated As of JOMC 1.2, removed without replacement.
         */
        @Deprecated
        public List<Section> getUnknownSections()
        {
            if ( this.unknownSections == null )
            {
                this.unknownSections = new LinkedList<Section>();
            }

            return this.unknownSections;
        }

        /**
         * Gets the currently edited source code file.
         *
         * @return The currently edited source code file.
         */
        protected SourceFileType getSourceFileType()
        {
            if ( this.sourceFileType == null )
            {
                if ( this.specification != null )
                {
                    return SourceFileProcessor.this.getSourceFileType( this.specification );
                }

                if ( this.implementation != null )
                {
                    return SourceFileProcessor.this.getSourceFileType( this.implementation );
                }
            }

            return this.sourceFileType;
        }

        /**
         * Gets a new velocity context used for merging templates.
         *
         * @return A new velocity context used for merging templates.
         */
        protected VelocityContext getVelocityContext()
        {
            final VelocityContext ctx = SourceFileProcessor.this.getVelocityContext();

            if ( this.specification != null )
            {
                ctx.put( "specification", this.specification );
            }

            if ( this.implementation != null )
            {
                ctx.put( "implementation", this.implementation );
            }

            return ctx;
        }

        /**
         * {@inheritDoc}
         * <p>This method creates any sections declared in the model of the editor as returned by method
         * {@code getSourceFileType} prior to rendering the output of the editor.</p>
         *
         * @param section The section to start rendering the editor's output with.
         *
         * @see #createSection(java.lang.String, java.lang.String, org.jomc.tools.model.SourceSectionType)
         */
        @Override
        protected String getOutput( final Section section ) throws IOException
        {
            this.getAddedSections().clear();
            this.getUnknownSections().clear();

            final SourceFileType model = this.getSourceFileType();

            if ( model != null )
            {
                this.createSections( model, model.getSourceSections(), section );
            }

            return super.getOutput( section );
        }

        /**
         * {@inheritDoc}
         * <p>This method searches the model of the editor for a section matching {@code s} and updates properties
         * {@code headContent} and {@code tailContent} of {@code s} according to the templates declared in the model
         * as returned by method {@code getSourceFileType}.</p>
         *
         * @param s The section to edit.
         */
        @Override
        protected void editSection( final Section s ) throws IOException
        {
            try
            {
                super.editSection( s );

                final SourceFileType model = this.getSourceFileType();

                if ( s.getName() != null && model != null && model.getSourceSections() != null )
                {
                    final SourceSectionType sourceSectionType =
                        model.getSourceSections().getSourceSection( s.getName() );

                    if ( sourceSectionType != null )
                    {
                        if ( s.getStartingLine() != null )
                        {
                            s.setStartingLine( getIndentation( sourceSectionType.getIndentationLevel() )
                                               + s.getStartingLine().trim() );

                        }
                        if ( s.getEndingLine() != null )
                        {
                            s.setEndingLine( getIndentation( sourceSectionType.getIndentationLevel() )
                                             + s.getEndingLine().trim() );

                        }

                        if ( sourceSectionType.getHeadTemplate() != null
                             && ( !sourceSectionType.isEditable()
                                  || s.getHeadContent().toString().trim().length() == 0 ) )
                        {
                            final StringWriter writer = new StringWriter();
                            final Template template = getVelocityTemplate( sourceSectionType.getHeadTemplate() );
                            final VelocityContext ctx = getVelocityContext();
                            ctx.put( "template", template );
                            template.merge( ctx, writer );
                            writer.close();
                            s.getHeadContent().setLength( 0 );
                            s.getHeadContent().append( writer.toString() );
                        }

                        if ( sourceSectionType.getTailTemplate() != null
                             && ( !sourceSectionType.isEditable()
                                  || s.getTailContent().toString().trim().length() == 0 ) )
                        {
                            final StringWriter writer = new StringWriter();
                            final Template template = getVelocityTemplate( sourceSectionType.getTailTemplate() );
                            final VelocityContext ctx = getVelocityContext();
                            ctx.put( "template", template );
                            template.merge( ctx, writer );
                            writer.close();
                            s.getTailContent().setLength( 0 );
                            s.getTailContent().append( writer.toString() );
                        }
                    }
                    else
                    {
                        if ( isLoggable( Level.WARNING ) )
                        {
                            log( Level.WARNING, getMessage(
                                "unknownSection", model.getIdentifier(), s.getName() ), null );

                        }

                        this.getUnknownSections().add( s );
                    }
                }
            }
            catch ( final VelocityException e )
            {
                throw (IOException) new IOException( getMessage( e ) ).initCause( e );
            }
        }

        private void createSections( final SourceFileType sourceFileType, final SourceSectionsType sourceSectionsType,
                                     final Section section ) throws IOException
        {
            if ( sourceSectionsType != null && section != null )
            {
                for ( SourceSectionType sourceSectionType : sourceSectionsType.getSourceSection() )
                {
                    Section childSection = section.getSection( sourceSectionType.getName() );

                    if ( childSection == null && !sourceSectionType.isOptional() )
                    {
                        childSection = this.createSection( StringUtils.defaultString( sourceFileType.getHeadComment() ),
                                                           StringUtils.defaultString( sourceFileType.getTailComment() ),
                                                           sourceSectionType );

                        section.getSections().add( childSection );

                        if ( isLoggable( Level.FINE ) )
                        {
                            log( Level.FINE, getMessage(
                                "addedSection", sourceFileType.getIdentifier(), childSection.getName() ), null );

                        }

                        this.getAddedSections().add( childSection );
                    }

                    this.createSections( sourceFileType, sourceSectionType.getSourceSections(), childSection );
                }
            }
        }

        /**
         * Creates a new {@code Section} instance for a given {@code SourceSectionType}.
         *
         * @param headComment Characters to use to start a comment in the source file.
         * @param tailComment Characters to use to end a comment in the source file.
         * @param sourceSectionType The {@code SourceSectionType} to create a new {@code Section} instance for.
         *
         * @return A new {@code Section} instance for {@code sourceSectionType}.
         *
         * @throws NullPointerException if {@code headComment}, {@code tailComment} or {@code sourceSectionType} is
         * {@code null}.
         * @throws IOException if creating a new {@code Section} instance fails.
         *
         * @since 1.2
         */
        private Section createSection( final String headComment, final String tailComment,
                                       final SourceSectionType sourceSectionType ) throws IOException
        {
            if ( headComment == null )
            {
                throw new NullPointerException( "headComment" );
            }
            if ( tailComment == null )
            {
                throw new NullPointerException( "tailComment" );
            }
            if ( sourceSectionType == null )
            {
                throw new NullPointerException( "sourceSectionType" );
            }

            final Section s = new Section();
            s.setName( sourceSectionType.getName() );

            final StringBuilder head = new StringBuilder( 255 );
            head.append( getIndentation( sourceSectionType.getIndentationLevel() ) ).append( headComment );

            s.setStartingLine( head + " SECTION-START[" + sourceSectionType.getName() + ']' + tailComment );
            s.setEndingLine( head + " SECTION-END" + tailComment );

            return s;
        }

        private void editSourceFile( final File sourcesDirectory ) throws IOException
        {
            if ( sourcesDirectory == null )
            {
                throw new NullPointerException( "sourcesDirectory" );
            }
            if ( !sourcesDirectory.isDirectory() )
            {
                throw new IOException( getMessage( "directoryNotFound", sourcesDirectory.getAbsolutePath() ) );
            }

            final SourceFileType model = this.getSourceFileType();
            final File f = new File( sourcesDirectory, model.getLocation() );

            try
            {
                if ( model != null )
                {
                    String content = "";
                    String edited = null;
                    boolean creating = false;

                    if ( !f.exists() )
                    {
                        if ( model.getTemplate() != null )
                        {
                            final StringWriter writer = new StringWriter();
                            final Template template = getVelocityTemplate( model.getTemplate() );
                            final VelocityContext ctx = this.getVelocityContext();
                            ctx.put( "template", template );
                            template.merge( ctx, writer );
                            writer.close();
                            content = writer.toString();
                            creating = true;
                        }
                    }
                    else
                    {
                        if ( isLoggable( Level.FINER ) )
                        {
                            log( Level.FINER, getMessage( "reading", f.getAbsolutePath() ), null );
                        }

                        content = FileUtils.readFileToString( f, getInputEncoding() );
                    }

                    try
                    {
                        edited = super.edit( content );
                    }
                    catch ( final IOException e )
                    {
                        throw (IOException) new IOException( getMessage(
                            "failedEditing", f.getAbsolutePath(), getMessage( e ) ) ).initCause( e );

                    }

                    if ( !edited.equals( content ) || edited.length() == 0 )
                    {
                        if ( !f.getParentFile().exists() && !f.getParentFile().mkdirs() )
                        {
                            throw new IOException( getMessage(
                                "failedCreatingDirectory", f.getParentFile().getAbsolutePath() ) );

                        }

                        if ( isLoggable( Level.INFO ) )
                        {
                            log( Level.INFO, getMessage(
                                creating ? "creating" : "editing", f.getAbsolutePath() ), null );

                        }

                        FileUtils.writeStringToFile( f, edited, getOutputEncoding() );
                    }
                    else if ( isLoggable( Level.FINER ) )
                    {
                        log( Level.FINER, getMessage( "unchanged", f.getAbsolutePath() ), null );
                    }
                }
            }
            catch ( final VelocityException e )
            {
                throw (IOException) new IOException( getMessage(
                    "failedEditing", f.getAbsolutePath(), getMessage( e ) ) ).initCause( e );

            }
        }

    }
}
