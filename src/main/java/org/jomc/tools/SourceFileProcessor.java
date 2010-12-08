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
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import org.apache.commons.io.FileUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
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
        }

        if ( sourceFileType == null )
        {
            sourceFileType = new SourceFileType();
            sourceFileType.setIdentifier( specification.getIdentifier() );
            sourceFileType.setLocation( specification.getClazz().replace( '.', '/' ) + ".java" );
            sourceFileType.setTemplate( SPECIFICATION_TEMPLATE );
            sourceFileType.setSourceSections( new SourceSectionsType() );

            SourceSectionType s = new SourceSectionType();
            s.setName( LICENSE_SECTION_NAME );
            s.setHeadTemplate( SPECIFICATION_LICENSE_TEMPLATE );
            s.setOptional( true );
            sourceFileType.getSourceSections().getSourceSection().add( s );

            s = new SourceSectionType();
            s.setName( ANNOTATIONS_SECTION_NAME );
            s.setHeadTemplate( SPECIFICATION_ANNOTATIONS_TEMPLATE );
            s.setOptional( false );
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
                s.setOptional( false );
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
            sourceFileType.setSourceSections( new SourceSectionsType() );

            SourceSectionType s = new SourceSectionType();
            s.setName( LICENSE_SECTION_NAME );
            s.setHeadTemplate( IMPLEMENTATION_LICENSE_TEMPLATE );
            s.setOptional( true );
            sourceFileType.getSourceSections().getSourceSection().add( s );

            s = new SourceSectionType();
            s.setName( ANNOTATIONS_SECTION_NAME );
            s.setHeadTemplate( IMPLEMENTATION_ANNOTATIONS_TEMPLATE );
            s.setOptional( false );
            sourceFileType.getSourceSections().getSourceSection().add( s );

            s = new SourceSectionType();
            s.setName( DOCUMENTATION_SECTION_NAME );
            s.setHeadTemplate( IMPLEMENTATION_DOCUMENTATION_TEMPLATE );
            s.setOptional( true );
            sourceFileType.getSourceSections().getSourceSection().add( s );

            for ( String interfaceName : this.getJavaInterfaceNames( implementation, false ) )
            {
                s = new SourceSectionType();
                s.setName( interfaceName );
                s.setIndentationLevel( 1 );
                s.setOptional( false );
                s.setEditable( true );
                sourceFileType.getSourceSections().getSourceSection().add( s );
            }

            s = new SourceSectionType();
            s.setName( this.getJavaTypeName( implementation, false ) );
            s.setIndentationLevel( 1 );
            s.setOptional( false );
            s.setEditable( true );
            sourceFileType.getSourceSections().getSourceSection().add( s );

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
            defaultCtor.setOptional( false );
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

        return model;
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
     * @deprecated As of JOMC 1.2, please use {@link #getSourceFileEditors(org.jomc.model.Specification)}.
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

        return new SourceFileEditor( specification, new TrailingWhitespaceEditor( this.getLineSeparator() ),
                                     this.getLineSeparator() );

    }

    /**
     * Gets a new list of editors for editing the source files of a given specification of the modules of the instance.
     *
     * @param specification The specification whose source files to edit.
     *
     * @return An unmodifiable list of editors for editing the source files of {@code specification}.
     *
     * @throws NullPointerException if {@code specification} is {@code null}.
     *
     * @see #getSourceFilesType(org.jomc.model.Specification)
     *
     * @since 1.2
     */
    public List<? extends SourceFileEditor> getSourceFileEditors( final Specification specification )
    {
        if ( specification == null )
        {
            throw new NullPointerException( "specification" );
        }

        assert this.getModules().getSpecification( specification.getIdentifier() ) != null :
            "Specification '" + specification.getIdentifier() + "' not found.";

        final SourceFilesType model = this.getSourceFilesType( specification );
        final List<SourceFileEditor> editors = new ArrayList<SourceFileEditor>( model.getSourceFile().size() + 1 );

        for ( SourceFileType m : model.getSourceFile() )
        {
            final VelocityContext ctx = this.getVelocityContext();
            ctx.put( "specification", specification );

            editors.add( new SourceFileEditor( m, ctx, new TrailingWhitespaceEditor( this.getLineSeparator() ),
                                               this.getLineSeparator() ) );

        }

        if ( this.getClass() != SourceFileProcessor.class )
        {
            try
            {
                this.getClass().getDeclaredMethod( "getSourceFileEditor", Specification.class );
                editors.add( this.getSourceFileEditor( specification ) );

                if ( this.isLoggable( Level.WARNING ) )
                {
                    this.log( Level.WARNING, getMessage(
                        "deprecationWarning", this.getClass().getName(),
                        "getSourceFileEditor(org.jomc.model.Specification)",
                        "getSourceFileEditors(org.jomc.model.Specification)" ), null );

                }
            }
            catch ( final NoSuchMethodException e )
            {
                if ( this.isLoggable( Level.FINEST ) )
                {
                    this.log( Level.FINEST, getMessage( e ), e );
                }
            }
        }

        return Collections.unmodifiableList( editors );
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
     * @deprecated As of JOMC 1.2, please use {@link #getSourceFileEditors(org.jomc.model.Implementation)}.
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

        return new SourceFileEditor( implementation, new TrailingWhitespaceEditor( this.getLineSeparator() ),
                                     this.getLineSeparator() );

    }

    /**
     * Gets a new list of editors for editing the source files of a given implementation of the modules of the instance.
     *
     * @param implementation The implementation whose source files to edit.
     *
     * @return An unmodifiable list of editors for editing the source files of {@code implementation}.
     *
     * @throws NullPointerException if {@code implementation} is {@code null}.
     *
     * @see #getSourceFilesType(org.jomc.model.Implementation)
     *
     * @since 1.2
     */
    public List<? extends SourceFileEditor> getSourceFileEditors( final Implementation implementation )
    {
        if ( implementation == null )
        {
            throw new NullPointerException( "implementation" );
        }

        assert this.getModules().getImplementation( implementation.getIdentifier() ) != null :
            "Implementation '" + implementation.getIdentifier() + "' not found.";

        final SourceFilesType model = this.getSourceFilesType( implementation );
        final List<SourceFileEditor> editors = new ArrayList<SourceFileEditor>( model.getSourceFile().size() );

        for ( SourceFileType m : model.getSourceFile() )
        {
            final VelocityContext ctx = this.getVelocityContext();
            ctx.put( "implementation", implementation );

            editors.add( new SourceFileEditor( m, ctx, new TrailingWhitespaceEditor( this.getLineSeparator() ),
                                               this.getLineSeparator() ) );

        }

        if ( this.getClass() != SourceFileProcessor.class )
        {
            try
            {
                this.getClass().getDeclaredMethod( "getSourceFileEditor", Implementation.class );
                editors.add( this.getSourceFileEditor( implementation ) );

                if ( this.isLoggable( Level.WARNING ) )
                {
                    this.log( Level.WARNING, getMessage(
                        "deprecationWarning", this.getClass().getName(),
                        "getSourceFileEditor(org.jomc.model.Implementation)",
                        "getSourceFileEditors(org.jomc.model.Implementation)" ), null );

                }
            }
            catch ( final NoSuchMethodException e )
            {
                if ( this.isLoggable( Level.FINEST ) )
                {
                    this.log( Level.FINEST, getMessage( e ), e );
                }
            }
        }

        return Collections.unmodifiableList( editors );
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
     * @see #getSourceFileEditors(org.jomc.model.Specification)
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
            this.editSourceFiles( sourcesDirectory, this.getSourceFileEditors( specification ) );
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
     * @see #getSourceFileEditors(org.jomc.model.Implementation)
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
            this.editSourceFiles( sourcesDirectory, this.getSourceFileEditors( implementation ) );
        }
    }

    /**
     * Edits files using a given list of editors.
     *
     * @param sourcesDirectory The directory holding the source files to edit.
     * @param editors The editors to use for editing source files.
     *
     * @throws NullPointerException if {@code sourcesDirectory} or {@code editors} is {@code null}.
     * @throws IOException if editing fails.
     */
    private void editSourceFiles( final File sourcesDirectory, final List<? extends SourceFileEditor> editors )
        throws IOException
    {
        if ( sourcesDirectory == null )
        {
            throw new NullPointerException( "sourcesDirectory" );
        }
        if ( !sourcesDirectory.isDirectory() )
        {
            throw new IOException( getMessage( "directoryNotFound", sourcesDirectory.getAbsolutePath() ) );
        }
        if ( editors == null )
        {
            throw new NullPointerException( "editors" );
        }

        for ( SourceFileEditor editor : editors )
        {
            final SourceFileType sourceFileType = editor.getSourceFileType();

            if ( sourceFileType != null )
            {
                String content = "";
                String edited = null;
                boolean creating = false;
                final File f = new File( sourcesDirectory, sourceFileType.getLocation() );

                if ( !f.exists() )
                {
                    if ( sourceFileType.getTemplate() != null )
                    {
                        final StringWriter writer = new StringWriter();
                        final Template template = this.getVelocityTemplate( sourceFileType.getTemplate() );
                        final VelocityContext ctx = editor.getVelocityContext();
                        ctx.put( "template", template );
                        template.merge( ctx, writer );
                        writer.close();
                        content = writer.toString();
                        creating = true;
                    }
                }
                else
                {
                    if ( this.isLoggable( Level.FINER ) )
                    {
                        this.log( Level.FINER, getMessage( "reading", f.getAbsolutePath() ), null );
                    }

                    content = FileUtils.readFileToString( f, this.getInputEncoding() );
                }

                try
                {
                    edited = editor.edit( content );
                }
                catch ( final IOException e )
                {
                    throw (IOException) new IOException( getMessage(
                        "failedEditing", f.getAbsolutePath(), getMessage( e ) ) ).initCause( e );

                }

                if ( this.isLoggable( Level.FINE ) )
                {
                    for ( Section s : editor.getAddedSections() )
                    {
                        this.log( Level.FINE, getMessage(
                            "addedSection", f.getAbsolutePath(), s.getName() ), null );

                    }
                }

                if ( this.isLoggable( Level.WARNING ) )
                {
                    for ( Section s : editor.getUnknownSections() )
                    {
                        this.log( Level.WARNING, getMessage(
                            "unknownSection", f.getAbsolutePath(), s.getName() ), null );

                    }
                }

                if ( !edited.equals( content ) || edited.length() == 0 )
                {
                    if ( !f.getParentFile().exists() && !f.getParentFile().mkdirs() )
                    {
                        throw new IOException( getMessage(
                            "failedCreatingDirectory", f.getParentFile().getAbsolutePath() ) );

                    }

                    if ( this.isLoggable( Level.INFO ) )
                    {
                        this.log( Level.INFO, getMessage(
                            creating ? "creating" : "editing", f.getAbsolutePath() ), null );

                    }

                    FileUtils.writeStringToFile( f, edited, this.getOutputEncoding() );
                }
                else if ( this.isLoggable( Level.FINER ) )
                {
                    this.log( Level.FINER, getMessage( "unchanged", f.getAbsolutePath() ), null );
                }
            }
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
     */
    public class SourceFileEditor extends SectionEditor
    {

        /** {@code Specification} of the instance or {@code null}. */
        @Deprecated
        private final Specification specification;

        /** {@code Implementation} of the instance or {@code null}. */
        @Deprecated
        private final Implementation implementation;

        /**
         * The Velocity context used to merge templates.
         * @since 1.2
         */
        private final VelocityContext velocityContext;

        /**
         * The model of the editor.
         * @since 1.2
         */
        private final SourceFileType sourceFileType;

        /** List of sections added to the input. */
        private List<Section> addedSections;

        /** List of sections without corresponding model entry. */
        private List<Section> unknownSections;

        /**
         * Creates a new {@code SourceFileEditor} instance taking a source file model and a Velocity context.
         *
         * @param sourceFileType The source file model backing the editor.
         * @param velocityContext The Velocity context backing the editor.
         *
         * @since 1.2
         */
        public SourceFileEditor( final SourceFileType sourceFileType, final VelocityContext velocityContext )
        {
            this( sourceFileType, velocityContext, null, null );
        }

        /**
         * Creates a new {@code SourceFileEditor} instance taking a source file model, a Velocity context and a line
         * separator.
         *
         * @param sourceFileType The source file model backing the editor.
         * @param velocityContext The Velocity context backing the editor.
         * @param lineSeparator The line separator of the editor.
         *
         * @since 1.2
         */
        public SourceFileEditor( final SourceFileType sourceFileType, final VelocityContext velocityContext,
                                 final String lineSeparator )
        {
            this( sourceFileType, velocityContext, null, lineSeparator );
        }

        /**
         * Creates a new {@code SourceFileEditor} instance taking a source file model, a Velocity context and an editor
         * to chain.
         *
         * @param sourceFileType The source file model backing the editor.
         * @param velocityContext The Velocity context backing the editor.
         * @param lineEditor The editor to chain.
         *
         * @since 1.2
         */
        public SourceFileEditor( final SourceFileType sourceFileType, final VelocityContext velocityContext,
                                 final LineEditor lineEditor )
        {
            this( sourceFileType, velocityContext, lineEditor, null );
        }

        /**
         * Creates a new {@code SourceFileEditor} instance taking a source file model, a Velocity context, an editor
         * to chain and a line separator.
         *
         * @param sourceFileType The source file model backing the editor.
         * @param velocityContext The Velocity context backing the editor.
         * @param lineEditor The editor to chain.
         * @param lineSeparator The line separator of the editor.
         *
         * @since 1.2
         */
        public SourceFileEditor( final SourceFileType sourceFileType, final VelocityContext velocityContext,
                                 final LineEditor lineEditor, final String lineSeparator )
        {
            super( lineEditor, lineSeparator );
            this.specification = null;
            this.implementation = null;
            this.sourceFileType = sourceFileType;
            this.velocityContext = velocityContext;
        }

        /**
         * Creates a new {@code SourceFileEditor} taking a {@code Specification} to edit source code of.
         *
         * @param specification The specification to edit source code of.
         *
         * @deprecated As of JOMC 1.2, please use {@link #SourceFileProcessor.SourceFileEditor(org.jomc.tools.model.SourceFileType, org.apache.velocity.VelocityContext) }.
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
         * @deprecated As of JOMC 1.2, please use {@link #SourceFileProcessor.SourceFileEditor(org.jomc.tools.model.SourceFileType, org.apache.velocity.VelocityContext, java.lang.String) }.
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
         * @deprecated As of JOMC 1.2, please use {@link #SourceFileProcessor.SourceFileEditor(org.jomc.tools.model.SourceFileType, org.apache.velocity.VelocityContext, org.jomc.util.LineEditor) }.
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
         * @deprecated As of JOMC 1.2, please use {@link #SourceFileProcessor.SourceFileEditor(org.jomc.tools.model.SourceFileType, org.apache.velocity.VelocityContext, org.jomc.util.LineEditor, java.lang.String) }.
         */
        @Deprecated
        public SourceFileEditor( final Specification specification, final LineEditor lineEditor,
                                 final String lineSeparator )
        {
            super( lineEditor, lineSeparator );
            this.specification = specification;
            this.implementation = null;
            this.velocityContext = null;
            this.sourceFileType = null;

            assert getModules().getSpecification( specification.getIdentifier() ) != null :
                "Specification '" + specification.getIdentifier() + "' not found.";

        }

        /**
         * Creates a new {@code SourceFileEditor} taking an {@code Implementation} to edit source code of.
         *
         * @param implementation The implementation to edit source code of.
         *
         * @deprecated As of JOMC 1.2, please use {@link #SourceFileProcessor.SourceFileEditor(org.jomc.tools.model.SourceFileType, org.apache.velocity.VelocityContext) }.
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
         * @deprecated As of JOMC 1.2, please use {@link #SourceFileProcessor.SourceFileEditor(org.jomc.tools.model.SourceFileType, org.apache.velocity.VelocityContext, java.lang.String) }.
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
         * @deprecated As of JOMC 1.2, please use {@link #SourceFileProcessor.SourceFileEditor(org.jomc.tools.model.SourceFileType, org.apache.velocity.VelocityContext, org.jomc.util.LineEditor) }.
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
         * @deprecated As of JOMC 1.2, please use {@link #SourceFileProcessor.SourceFileEditor(org.jomc.tools.model.SourceFileType, org.apache.velocity.VelocityContext, org.jomc.util.LineEditor, java.lang.String) }.
         */
        @Deprecated
        public SourceFileEditor( final Implementation implementation, final LineEditor lineEditor,
                                 final String lineSeparator )
        {
            super( lineEditor, lineSeparator );
            this.implementation = implementation;
            this.specification = null;
            this.velocityContext = null;
            this.sourceFileType = null;

            assert getModules().getImplementation( implementation.getIdentifier() ) != null :
                "Implementation '" + implementation.getIdentifier() + "' not found.";

        }

        /**
         * Gets a list of sections added to the input.
         * <p>This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you
         * make to the returned list will be present inside the object. This is why there is no {@code set} method
         * for the added sections property.</p>
         *
         * @return A list of sections added to the input.
         */
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
         */
        public List<Section> getUnknownSections()
        {
            if ( this.unknownSections == null )
            {
                this.unknownSections = new LinkedList<Section>();
            }

            return this.unknownSections;
        }

        /**
         * Gets the model of the editor.
         *
         * @return The model of the editor.
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
         * Gets the velocity context used for merging templates.
         *
         * @return The velocity context used for merging templates.
         */
        protected VelocityContext getVelocityContext()
        {
            if ( this.velocityContext == null )
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

            return this.velocityContext;
        }

        /**
         * {@inheritDoc}
         * <p>This method creates any sections declared in the model of the editor as returned by method
         * {@code getSourceFileType} prior to rendering the output of the editor.</p>
         *
         * @param section The section to start rendering the editor's output with.
         *
         * @see #getSourceFileType()
         */
        @Override
        protected String getOutput( final Section section ) throws IOException
        {
            this.getAddedSections().clear();
            this.getUnknownSections().clear();

            final SourceFileType model = this.getSourceFileType();

            if ( model != null )
            {
                this.createSections( model.getSourceSections(), section );
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
         *
         * @see #getSourceFileType()
         */
        @Override
        protected void editSection( final Section s ) throws IOException
        {
            super.editSection( s );

            final SourceFileType model = this.getSourceFileType();

            if ( s.getName() != null && model != null && model.getSourceSections() != null )
            {
                final SourceSectionType sourceSectionType = model.getSourceSections().getSourceSection( s.getName() );

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
                         && ( !sourceSectionType.isEditable() || s.getHeadContent().toString().trim().length() == 0 ) )
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
                         && ( !sourceSectionType.isEditable() || s.getTailContent().toString().trim().length() == 0 ) )
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
                    this.getUnknownSections().add( s );
                }
            }
        }

        private void createSections( final SourceSectionsType sourceSectionsType, final Section section )
        {
            if ( sourceSectionsType != null && section != null )
            {
                for ( SourceSectionType sourceSectionType : sourceSectionsType.getSourceSection() )
                {
                    Section childSection = section.getSection( sourceSectionType.getName() );

                    if ( childSection == null && !sourceSectionType.isOptional() )
                    {
                        childSection = new Section();
                        childSection.setName( sourceSectionType.getName() );
                        childSection.setStartingLine( getIndentation( sourceSectionType.getIndentationLevel() )
                                                      + "// SECTION-START[" + sourceSectionType.getName() + "]" );

                        childSection.setEndingLine( getIndentation( sourceSectionType.getIndentationLevel() )
                                                    + "// SECTION-END" );

                        section.getSections().add( childSection );
                        this.getAddedSections().add( childSection );
                    }

                    this.createSections( sourceSectionType.getSourceSections(), childSection );
                }
            }
        }

    }
}
