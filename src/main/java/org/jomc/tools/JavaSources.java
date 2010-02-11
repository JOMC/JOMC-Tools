/*
 *   Copyright (c) 2009 The JOMC Project
 *   Copyright (c) 2005 Christian Schulte <cs@jomc.org>
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
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import javax.xml.bind.JAXBElement;
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
 * Manages Java source code.
 *
 * <p><b>Use cases</b><br/><ul>
 * <li>{@link #manageSources(java.io.File) }</li>
 * <li>{@link #manageSources(org.jomc.model.Module, java.io.File) }</li>
 * <li>{@link #manageSources(org.jomc.model.Specification, java.io.File) }</li>
 * <li>{@link #manageSources(org.jomc.model.Implementation, java.io.File) }</li>
 * </ul></p>
 *
 * @author <a href="mailto:cs@jomc.org">Christian Schulte</a>
 * @version $Id$
 *
 * @see #getModules()
 */
public class JavaSources extends JomcTool
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

    /** Name of the generator. */
    private static final String GENERATOR_NAME = JavaSources.class.getName();

    /** Constant for the version of the generator. */
    private static final String GENERATOR_VERSION = "1.0";

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

    /** Number of whitespace characters per indentation level. */
    private Integer whitespacesPerIndent;

    /** Indentation character. */
    private Character indentationCharacter;

    /** Source files model. */
    private SourceFilesType sourceFilesType;

    /** Creates a new {@code JavaSources} instance. */
    public JavaSources()
    {
        super();
    }

    /**
     * Creates a new {@code JavaSources} instance taking a {@code JavaSources} instance to initialize the instance with.
     *
     * @param tool The instance to initialize the new instance with,
     *
     * @throws ToolException if copying {@code tool} fails.
     */
    public JavaSources( final JavaSources tool ) throws ToolException
    {
        super( tool );
        this.setIndentationCharacter( tool.getIndentationCharacter() );
        this.setWhitespacesPerIndent( tool.getWhitespacesPerIndent() );
        this.sourceFilesType = new SourceFilesType( tool.getSourceFilesType() );
    }

    /**
     * Gets the number of whitespace characters per indentation level.
     *
     * @return The number of whitespace characters per indentation level.
     */
    public int getWhitespacesPerIndent()
    {
        if ( this.whitespacesPerIndent == null )
        {
            this.whitespacesPerIndent = 4;
        }

        return this.whitespacesPerIndent;
    }

    /**
     * Sets the number of whitespace characters per indentation level.
     *
     * @param value The new number of whitespace characters per indentation level.
     */
    public void setWhitespacesPerIndent( final int value )
    {
        this.whitespacesPerIndent = value;
    }

    /**
     * Gets the indentation character.
     *
     * @return The indentation character.
     */
    public char getIndentationCharacter()
    {
        if ( this.indentationCharacter == null )
        {
            this.indentationCharacter = ' ';
        }

        return this.indentationCharacter;
    }

    /**
     * Sets the indentation character.
     *
     * @param value The new indentation character.
     */
    public void setIndentationCharacter( final char value )
    {
        this.indentationCharacter = value;
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
     */
    public SourceFilesType getSourceFilesType()
    {
        if ( this.sourceFilesType == null )
        {
            this.sourceFilesType = new SourceFilesType();
        }

        return this.sourceFilesType;
    }

    /**
     * Gets the model of a specification source file.
     *
     * @param specification The specification to get a source file model for.
     *
     * @return The source file model for {@code specification}.
     *
     * @throws NullPointerException if {@code specification} is {@code null}.
     *
     * @see #getSourceFilesType()
     */
    public SourceFileType getSourceFileType( final Specification specification )
    {
        if ( specification == null )
        {
            throw new NullPointerException( "specification" );
        }

        SourceFileType sourceFileType = this.getSourceFilesType().getSourceFile( specification.getIdentifier() );

        if ( sourceFileType == null )
        {
            for ( Object any : specification.getAny() )
            {
                if ( any instanceof JAXBElement )
                {
                    any = ( (JAXBElement) any ).getValue();
                }

                if ( any instanceof SourceFileType )
                {
                    sourceFileType = (SourceFileType) any;
                    break;
                }
            }
        }

        if ( sourceFileType == null )
        {
            sourceFileType = new SourceFileType();
            sourceFileType.setIdentifier( specification.getIdentifier() );
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
     * Gets the model of an implementation source file.
     *
     * @param implementation The implementation to get a source file model for.
     *
     * @return The source file model for {@code implementation}.
     *
     * @throws NullPointerException if {@code implementation} is {@code null}.
     *
     * @see #getSourceFilesType()
     */
    public SourceFileType getSourceFileType( final Implementation implementation )
    {
        if ( implementation == null )
        {
            throw new NullPointerException( "implementation" );
        }

        SourceFileType sourceFileType = this.getSourceFilesType().getSourceFile( implementation.getIdentifier() );

        if ( sourceFileType == null )
        {
            for ( Object any : implementation.getAny() )
            {
                if ( any instanceof JAXBElement )
                {
                    any = ( (JAXBElement) any ).getValue();
                }

                if ( any instanceof SourceFileType )
                {
                    sourceFileType = (SourceFileType) any;
                    break;
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
            s.setOptional( specifications == null ||
                           ( specifications.getSpecification().isEmpty() && specifications.getReference().isEmpty() ) );

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
     * Manages the source code of the modules of the instance.
     *
     * @param sourcesDirectory The directory holding the sources to manage.
     *
     * @throws NullPointerException if {@code sourcesDirectory} is {@code null}.
     * @throws ToolException if managing sources fails.
     *
     * @see #manageSources(org.jomc.model.Module, java.io.File)
     */
    public void manageSources( final File sourcesDirectory ) throws ToolException
    {
        if ( sourcesDirectory == null )
        {
            throw new NullPointerException( "sourcesDirectory" );
        }

        for ( Module m : this.getModules().getModule() )
        {
            this.manageSources( m, sourcesDirectory );
        }
    }

    /**
     * Manages the source code of a given module of the modules of the instance.
     *
     * @param module The module to process.
     * @param sourcesDirectory The directory holding the sources to manage.
     *
     * @throws NullPointerException if {@code module} or {@code sourcesDirectory} is {@code null}.
     * @throws ToolException if managing sources fails.
     *
     * @see #manageSources(org.jomc.model.Specification, java.io.File)
     * @see #manageSources(org.jomc.model.Implementation, java.io.File)
     */
    public void manageSources( final Module module, final File sourcesDirectory ) throws ToolException
    {
        if ( module == null )
        {
            throw new NullPointerException( "module" );
        }
        if ( sourcesDirectory == null )
        {
            throw new NullPointerException( "sourcesDirectory" );
        }

        if ( module.getSpecifications() != null )
        {
            for ( Specification s : module.getSpecifications().getSpecification() )
            {
                this.manageSources( s, sourcesDirectory );
            }
        }
        if ( module.getImplementations() != null )
        {
            for ( Implementation i : module.getImplementations().getImplementation() )
            {
                this.manageSources( i, sourcesDirectory );
            }
        }
    }

    /**
     * Manages the source code of a given specification of the modules of the instance.
     *
     * @param specification The specification to process.
     * @param sourcesDirectory The directory holding the sources to manage.
     *
     * @throws NullPointerException if {@code specification} or {@code sourcesDirectory} is {@code null}.
     * @throws ToolException if managing sources fails.
     *
     * @see #getSourceCodeEditor(org.jomc.model.Specification)
     */
    public void manageSources( final Specification specification, final File sourcesDirectory ) throws ToolException
    {
        if ( specification == null )
        {
            throw new NullPointerException( "specification" );
        }
        if ( sourcesDirectory == null )
        {
            throw new NullPointerException( "sourcesDirectory" );
        }

        try
        {
            final Implementation i = this.getModules().getImplementation( specification.getIdentifier() );

            if ( i != null && i.isClassDeclaration() )
            {
                this.manageSources( i, sourcesDirectory );
            }
            else if ( specification.isClassDeclaration() )
            {
                this.editFile( new File( sourcesDirectory, specification.getClazz().replace( '.', '/' ) + ".java" ),
                               this.getSourceCodeEditor( specification ) );

            }
        }
        catch ( final IOException e )
        {
            throw new ToolException( e.getMessage(), e );
        }
    }

    /**
     * Manages the source code of a given implementation of the modules of the instance.
     *
     * @param implementation The implementation to process.
     * @param sourcesDirectory The directory holding the sources to manage.
     *
     * @throws NullPointerException if {@code implementation} or {@code sourcesDirectory} is {@code null}.
     * @throws ToolException if managing sources fails.
     *
     * @see #getSourceCodeEditor(org.jomc.model.Implementation)
     */
    public void manageSources( final Implementation implementation, final File sourcesDirectory ) throws ToolException
    {
        if ( implementation == null )
        {
            throw new NullPointerException( "implementation" );
        }
        if ( sourcesDirectory == null )
        {
            throw new NullPointerException( "sourcesDirectory" );
        }

        try
        {
            if ( implementation.isClassDeclaration() )
            {
                this.editFile( new File( sourcesDirectory, implementation.getClazz().replace( '.', '/' ) + ".java" ),
                               this.getSourceCodeEditor( implementation ) );

            }
        }
        catch ( final IOException e )
        {
            throw new ToolException( e.getMessage(), e );
        }
    }

    /**
     * Gets a new editor for editing specification source code.
     *
     * @param specification The specification to edit source code of.
     *
     * @return A new editor for editing the source code of {@code specification}.
     *
     * @throws NullPointerException if {@code specification} is {@code null}.
     */
    public SourceCodeEditor getSourceCodeEditor( final Specification specification )
    {
        if ( specification == null )
        {
            throw new NullPointerException( "specification" );
        }

        return new SourceCodeEditor( specification, new TrailingWhitespaceEditor() );
    }

    /**
     * Gets a new editor for editing implementation source code.
     *
     * @param implementation The implementation to edit source code of.
     *
     * @return A new editor for editing the source code of {@code implementation}.
     *
     * @throws NullPointerException if {@code implementation} is {@code null}.
     */
    public SourceCodeEditor getSourceCodeEditor( final Implementation implementation )
    {
        if ( implementation == null )
        {
            throw new NullPointerException( "implementation" );
        }

        return new SourceCodeEditor( implementation, new TrailingWhitespaceEditor() );
    }

    /**
     * Gets the velocity context used for merging templates.
     *
     * @return The velocity context used for merging templates.
     */
    @Override
    public VelocityContext getVelocityContext()
    {
        final VelocityContext ctx = super.getVelocityContext();
        ctx.put( "generatorName", GENERATOR_NAME );
        ctx.put( "generatorVersion", GENERATOR_VERSION );
        return ctx;
    }

    private void editFile( final File f, final SourceCodeEditor editor ) throws IOException, ToolException
    {
        String content = null;

        if ( !f.exists() )
        {
            final SourceFileType sourceFileType = editor.getSourceFileType();

            if ( sourceFileType != null && sourceFileType.getTemplate() != null )
            {
                final StringWriter writer = new StringWriter();
                final Template template = this.getVelocityTemplate( sourceFileType.getTemplate() );
                final VelocityContext ctx = editor.getVelocityContext();
                ctx.put( "template", template );
                template.merge( ctx, writer );
                writer.close();
                content = writer.toString();
            }
        }
        else
        {
            content = FileUtils.readFileToString( f, this.getInputEncoding() );
        }

        if ( content != null )
        {
            String edited = null;

            try
            {
                edited = editor.edit( content );
            }
            catch ( final IOException e )
            {
                throw new ToolException( getMessage( "failedEditing", f.getCanonicalPath(), e.getMessage() ), e );
            }

            if ( this.isLoggable( Level.FINE ) )
            {
                this.logAddedSections( f, editor.getAddedSections() );
            }

            if ( this.isLoggable( Level.WARNING ) )
            {
                this.logUnknownSections( f, editor.getUnknownSections() );
            }

            if ( !edited.equals( content ) )
            {
                if ( !f.getParentFile().exists() && !f.getParentFile().mkdirs() )
                {
                    throw new ToolException( getMessage( "failedCreatingDirectory",
                                                         f.getParentFile().getAbsolutePath() ) );

                }

                if ( this.isLoggable( Level.INFO ) )
                {
                    this.log( Level.INFO, getMessage( "editing", f.getCanonicalPath() ), null );
                }

                FileUtils.writeStringToFile( f, edited, this.getOutputEncoding() );
            }
        }
    }

    private void logAddedSections( final File f, final List<Section> sections ) throws IOException
    {
        for ( Section s : sections )
        {
            this.log( Level.FINE, getMessage( "addedSection", f.getCanonicalPath(), s.getName() ), null );
        }
    }

    private void logUnknownSections( final File f, final List<Section> sections ) throws IOException
    {
        for ( Section s : sections )
        {
            this.log( Level.WARNING, getMessage( "unknownSection", f.getCanonicalPath(), s.getName() ), null );
        }
    }

    private static String getMessage( final String key, final Object... arguments )
    {
        if ( key == null )
        {
            throw new NullPointerException( "key" );
        }

        return MessageFormat.format( ResourceBundle.getBundle( JavaSources.class.getName().replace( '.', '/' ) ).
            getString( key ), arguments );

    }

    /**
     * Extension to {@code SectionEditor} adding support for editing source code files.
     *
     * @author <a href="mailto:cs@jomc.org">Christian Schulte</a>
     * @version $Id$
     */
    public class SourceCodeEditor extends SectionEditor
    {

        /** {@code Specification} of the instance or {@code null}. */
        private final Specification specification;

        /** {@code Implementation} of the instance or {@code null}. */
        private final Implementation implementation;

        /** List of sections added to the input. */
        private List<Section> addedSections;

        /** List of sections without corresponding model entry. */
        private List<Section> unknownSections;

        /**
         * Creates a new {@code SourceCodeEditor} taking a {@code Specification} to edit source code of.
         *
         * @param specification The specification to edit source code of.
         */
        public SourceCodeEditor( final Specification specification )
        {
            super();
            this.specification = specification;
            this.implementation = null;
        }

        /**
         * Creates a new {@code SourceCodeEditor} taking a {@code Specification} to edit source code of and an editor to
         * chain.
         *
         * @param specification The specification backing the editor.
         * @param lineEditor The editor to chain.
         */
        public SourceCodeEditor( final Specification specification, final LineEditor lineEditor )
        {
            super( lineEditor );
            this.specification = specification;
            this.implementation = null;
        }

        /**
         * Creates a new {@code SourceCodeEditor} taking an {@code Implementation} to edit source code of.
         *
         * @param implementation The implementation to edit source code of.
         */
        public SourceCodeEditor( final Implementation implementation )
        {
            super();
            this.implementation = implementation;
            this.specification = null;
        }

        /**
         * Creates a new {@code SourceCodeEditor} taking an {@code Implementation} to edit source code of and an editor
         * to chain.
         *
         * @param implementation The implementation to edit source code of.
         * @param lineEditor The editor to chain.
         */
        public SourceCodeEditor( final Implementation implementation, final LineEditor lineEditor )
        {
            super( lineEditor );
            this.implementation = implementation;
            this.specification = null;
        }

        /**
         * Gets a list of sections added to the input.
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
            if ( this.specification != null )
            {
                return JavaSources.this.getSourceFileType( this.specification );
            }

            if ( this.implementation != null )
            {
                return JavaSources.this.getSourceFileType( this.implementation );
            }

            return null;
        }

        /**
         * Gets the velocity context used for merging templates.
         *
         * @return The velocity context used for merging templates.
         */
        protected VelocityContext getVelocityContext()
        {
            final VelocityContext ctx = JavaSources.this.getVelocityContext();

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
         * @see #getSourceFileType()
         */
        @Override
        protected String getOutput( final Section section ) throws IOException
        {
            this.getAddedSections().clear();
            this.getUnknownSections().clear();

            final SourceFileType sourceFileType = this.getSourceFileType();

            if ( sourceFileType != null )
            {
                this.createSections( sourceFileType.getSourceSections(), section );
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
            try
            {
                super.editSection( s );

                final SourceFileType sourceFileType = this.getSourceFileType();

                if ( s.getName() != null && sourceFileType != null && sourceFileType.getSourceSections() != null )
                {
                    final SourceSectionType sourceSectionType =
                        sourceFileType.getSourceSections().getSourceSection( s.getName() );

                    if ( sourceSectionType != null )
                    {
                        if ( sourceSectionType.getHeadTemplate() != null &&
                             ( !sourceSectionType.isEditable() || s.getHeadContent().toString().trim().length() == 0 ) )
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

                        if ( sourceSectionType.getTailTemplate() != null &&
                             ( !sourceSectionType.isEditable() || s.getTailContent().toString().trim().length() == 0 ) )
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
            catch ( final ToolException e )
            {
                throw (IOException) new IOException( e.getMessage() ).initCause( e );
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
                        final char[] indent =
                            new char[ getWhitespacesPerIndent() * sourceSectionType.getIndentationLevel() ];

                        Arrays.fill( indent, getIndentationCharacter() );

                        childSection = new Section();
                        childSection.setName( sourceSectionType.getName() );
                        childSection.setStartingLine(
                            String.valueOf( indent ) + "// SECTION-START[" + sourceSectionType.getName() + "]" );

                        childSection.setEndingLine( String.valueOf( indent ) + "// SECTION-END" );
                        section.getSections().add( childSection );

                        this.getAddedSections().add( childSection );
                    }

                    this.createSections( sourceSectionType.getSourceSections(), childSection );
                }
            }
        }

    }

}
