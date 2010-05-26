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

    /** Name of the generator. */
    private static final String GENERATOR_NAME = SourceFileProcessor.class.getName();

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

    /** Source files model. */
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
        this.sourceFilesType = new SourceFilesType( tool.getSourceFilesType() );
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
            sourceFileType = specification.getAnyObject( SourceFileType.class );
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
     * Manages the source file of a given specification of the modules of the instance.
     *
     * @param specification The specification to process.
     * @param sourcesDirectory The directory holding the source files to manage.
     *
     * @throws NullPointerException if {@code specification} or {@code sourcesDirectory} is {@code null}.
     * @throws IOException if managing source files fails.
     *
     * @see #getSourceFileEditor(org.jomc.model.Specification)
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

        final Implementation i = this.getModules().getImplementation( specification.getIdentifier() );

        if ( i != null && i.isClassDeclaration() )
        {
            this.manageSourceFiles( i, sourcesDirectory );
        }
        else if ( specification.isClassDeclaration() )
        {
            final File sourceFile =
                new File( sourcesDirectory, specification.getClazz().replace( '.', '/' ) + ".java" );

            this.editSourceFile( sourceFile, this.getSourceFileEditor( specification ) );
        }
    }

    /**
     * Manages the source file of a given implementation of the modules of the instance.
     *
     * @param implementation The implementation to process.
     * @param sourcesDirectory The directory holding the source files to manage.
     *
     * @throws NullPointerException if {@code implementation} or {@code sourcesDirectory} is {@code null}.
     * @throws IOException if managing source files fails.
     *
     * @see #getSourceFileEditor(org.jomc.model.Implementation)
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

        if ( implementation.isClassDeclaration() )
        {
            final File sourceFile =
                new File( sourcesDirectory, implementation.getClazz().replace( '.', '/' ) + ".java" );

            this.editSourceFile( sourceFile, this.getSourceFileEditor( implementation ) );
        }
    }

    /**
     * Gets a new editor for editing the source file of a given specification.
     *
     * @param specification The specification whose source file to edit.
     *
     * @return A new editor for editing the source file of {@code specification}.
     *
     * @throws NullPointerException if {@code specification} is {@code null}.
     */
    public SourceFileEditor getSourceFileEditor( final Specification specification )
    {
        if ( specification == null )
        {
            throw new NullPointerException( "specification" );
        }

        return new SourceFileEditor( specification, new TrailingWhitespaceEditor( this.getLineSeparator() ),
                                     this.getLineSeparator() );

    }

    /**
     * Gets a new editor for editing the source file of a given implementation.
     *
     * @param implementation The implementation whose source file to edit.
     *
     * @return A new editor for editing the source file of {@code implementation}.
     *
     * @throws NullPointerException if {@code implementation} is {@code null}.
     */
    public SourceFileEditor getSourceFileEditor( final Implementation implementation )
    {
        if ( implementation == null )
        {
            throw new NullPointerException( "implementation" );
        }

        return new SourceFileEditor( implementation, new TrailingWhitespaceEditor( this.getLineSeparator() ),
                                     this.getLineSeparator() );

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

    /**
     * Edits a given file using a given editor.
     *
     * @param f The file to edit.
     * @param editor The editor to edit {@code f} with.
     *
     * @throws NullPointerException if {@code f} or {@code editor} is {@code null}.
     * @throws IOException if editing fails.
     */
    private void editSourceFile( final File f, final SourceFileEditor editor ) throws IOException
    {
        if ( f == null )
        {
            throw new NullPointerException( "f" );
        }
        if ( editor == null )
        {
            throw new NullPointerException( "editor" );
        }

        String content = "";

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

        String edited = null;

        try
        {
            edited = editor.edit( content );
        }
        catch ( final IOException e )
        {
            throw (IOException) new IOException( getMessage(
                "failedEditing", f.getCanonicalPath(), e.getMessage() ) ).initCause( e );

        }

        if ( this.isLoggable( Level.FINE ) )
        {
            for ( Section s : editor.getAddedSections() )
            {
                this.log( Level.FINE, getMessage( "addedSection", f.getCanonicalPath(), s.getName() ), null );
            }
        }

        if ( this.isLoggable( Level.WARNING ) )
        {
            for ( Section s : editor.getUnknownSections() )
            {
                this.log( Level.WARNING, getMessage( "unknownSection", f.getCanonicalPath(), s.getName() ), null );
            }
        }

        if ( !edited.equals( content ) || edited.length() == 0 )
        {
            if ( !f.getParentFile().exists() && !f.getParentFile().mkdirs() )
            {
                throw new IOException( getMessage( "failedCreatingDirectory", f.getParentFile().getAbsolutePath() ) );
            }

            if ( this.isLoggable( Level.INFO ) )
            {
                this.log( Level.INFO, getMessage( "editing", f.getCanonicalPath() ), null );
            }

            FileUtils.writeStringToFile( f, edited, this.getOutputEncoding() );
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

    /**
     * Extension to {@code SectionEditor} adding support for editing source code files.
     *
     * @author <a href="mailto:schulte2005@users.sourceforge.net">Christian Schulte</a>
     * @version $Id$
     */
    public class SourceFileEditor extends SectionEditor
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
         * Creates a new {@code SourceFileEditor} taking a {@code Specification} to edit source code of.
         *
         * @param specification The specification to edit source code of.
         */
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
         */
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
         */
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
         */
        public SourceFileEditor( final Specification specification, final LineEditor lineEditor,
                                 final String lineSeparator )
        {
            super( lineEditor, lineSeparator );
            this.specification = specification;
            this.implementation = null;
        }

        /**
         * Creates a new {@code SourceFileEditor} taking an {@code Implementation} to edit source code of.
         *
         * @param implementation The implementation to edit source code of.
         */
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
         */
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
         */
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
         */
        public SourceFileEditor( final Implementation implementation, final LineEditor lineEditor,
                                 final String lineSeparator )
        {
            super( lineEditor, lineSeparator );
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
                return SourceFileProcessor.this.getSourceFileType( this.specification );
            }

            if ( this.implementation != null )
            {
                return SourceFileProcessor.this.getSourceFileType( this.implementation );
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
            super.editSection( s );

            final SourceFileType sourceFileType = this.getSourceFileType();

            if ( s.getName() != null && sourceFileType != null && sourceFileType.getSourceSections() != null )
            {
                final SourceSectionType sourceSectionType =
                    sourceFileType.getSourceSections().getSourceSection( s.getName() );

                if ( sourceSectionType != null )
                {
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
