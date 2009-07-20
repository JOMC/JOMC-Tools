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
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import org.apache.commons.io.FileUtils;
import org.apache.velocity.VelocityContext;
import org.jomc.model.Implementation;
import org.jomc.model.Specification;
import org.jomc.tools.util.LineEditor;
import org.jomc.tools.util.Section;
import org.jomc.tools.util.SectionEditor;
import org.jomc.tools.util.TrailingWhitespaceEditor;

/**
 * Manages Java source code.
 *
 * @author <a href="mailto:schulte2005@users.sourceforge.net">Christian Schulte</a>
 * @version $Id$
 * @see #editModuleSources(java.io.File)
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

    /** Constant for the name of the specification comment source code section. */
    private static final String SPECIFICATION_COMMENT_SECTION_NAME = "Specification Comment";

    /** Constant for the name of the implementation comment source code section. */
    private static final String IMPLEMENTATION_COMMENT_SECTION_NAME = "Implementation Comment";

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

    /** Name of the {@code implementation-dependencies.vm} template. */
    private static final String DEPENDENCIES_TEMPLATE = "implementation-dependencies.vm";

    /** Name of the {@code implementation-properties.vm} template. */
    private static final String PROPERTIES_TEMPLATE = "implementation-properties.vm";

    /** Name of the {@code implementation-messages.vm} template. */
    private static final String MESSAGES_TEMPLATE = "implementation-messages.vm";

    /** Name of the {@code license-header.vm} template. */
    private static final String LICENSE_TEMPLATE = "license-header.vm";

    /** Name of the {@code specification-comment.vm} template. */
    private static final String SPECIFICATION_COMMENT_TEMPLATE = "specification-comment.vm";

    /** Name of the {@code implementation-comment.vm} template. */
    private static final String IMPLEMENTATION_COMMENT_TEMPLATE = "implementation-comment.vm";

    /** Name of the {@code Implementation.java.vm} template. */
    private static final String IMPLEMENTATION_TEMPLATE = "Implementation.java.vm";

    /** Name of the {@code Specification.java.vm} template. */
    private static final String SPECIFICATION_TEMPLATE = "Specification.java.vm";

    /** Name of the {@code specification-annotations.vm} template. */
    private static final String SPECIFICATION_ANNOTATIONS_TEMPLATE = "specification-annotations.vm";

    /** Name of the {@code implementation-annotations.vm} template. */
    private static final String IMPLEMENTATION_ANNOTATIONS_TEMPLATE = "implementation-annotations.vm";

    /** Creates a new {@code JavaSources} instance. */
    public JavaSources()
    {
        super();
    }

    /**
     * Creates a new {@code JavaSources} instance taking a {@code JomcTool} instance to initialize the instance with.
     *
     * @param tool The instance to initialize the new instance with,
     */
    public JavaSources( final JomcTool tool )
    {
        super( tool );
    }

    /**
     * Edits the Java source code of the module of the instance.
     *
     * @param sourceDirectory The directory holding the source files to edit.
     *
     * @throws NullPointerException if {@code sourcesDirectory} is {@code null}.
     * @throws IOException if editing fails.
     */
    public void editModuleSources( final File sourceDirectory ) throws IOException
    {
        if ( sourceDirectory == null )
        {
            throw new NullPointerException( "sourceDirectory" );
        }

        if ( this.getModule() != null )
        {
            this.log( Level.INFO, this.getMessage( "processingModule", new Object[]
                {
                    this.getModule().getName()
                } ), null );

            if ( this.getModule().getSpecifications() != null )
            {
                for ( Specification s : this.getModule().getSpecifications().getSpecification() )
                {
                    if ( this.getModules().getImplementation( s.getIdentifier() ) == null )
                    {
                        this.editSpecificationSource( s, sourceDirectory );
                    }
                }
            }
            if ( this.getModule().getImplementations() != null )
            {
                for ( Implementation i : this.getModule().getImplementations().getImplementation() )
                {
                    if ( i.getIdentifier().equals( i.getClazz() ) )
                    {
                        this.editImplementationSource( i, sourceDirectory );
                    }
                }
            }

            this.log( Level.INFO, this.getMessage( "upToDate", null ), null );
        }
        else
        {
            this.log( Level.WARNING, this.getMessage( "missingModule", new Object[]
                {
                    this.getModuleName()
                } ), null );

        }
    }

    /**
     * Edits the source code of a specification.
     *
     * @param specification The specification to edit.
     * @param sourceDirectory The directory holding the source file to edit.
     *
     * @throws NullPointerException if {@code specification} or {@code sourceDirectory} is {@code null}.
     * @throws IOException if editing the specification fails.
     */
    public void editSpecificationSource( final Specification specification, final File sourceDirectory )
        throws IOException
    {
        if ( specification == null )
        {
            throw new NullPointerException( "specification" );
        }
        if ( sourceDirectory == null )
        {
            throw new NullPointerException( "sourceDirectory" );
        }

        final File f = new File( sourceDirectory, specification.getIdentifier().replace( '.', '/' ) + ".java" );

        String content;
        if ( f.exists() )
        {
            content = FileUtils.readFileToString( f, this.getInputEncoding() );
        }
        else
        {
            if ( !f.getParentFile().exists() )
            {
                f.getParentFile().mkdirs();
            }

            content = this.getSpecificationTemplate( specification );
        }

        final JavaEditor editor = this.newJavaEditor( specification );
        final String edited = editor.edit( content );

        if ( !editor.isAnnotationsSectionEdited() )
        {
            throw new IOException( this.getMessage( "missingSection", new Object[]
                {
                    ANNOTATIONS_SECTION_NAME,
                    specification.getIdentifier()
                } ) );

        }

        if ( !edited.equals( content ) )
        {
            FileUtils.writeStringToFile( f, edited, this.getOutputEncoding() );
            this.log( Level.INFO, this.getMessage( "editing", new Object[]
                {
                    f.getCanonicalPath()
                } ), null );

        }
    }

    /**
     * Edits the source code of an implementation.
     *
     * @param implementation The implementation to edit.
     * @param sourceDirectory The directory holding the source file to edit.
     *
     * @throws NullPointerException if {@code implementation} or {@code sourceDirectory} is {@code null}.
     * @throws IOException if editing the implementation fails.
     */
    public void editImplementationSource( final Implementation implementation, final File sourceDirectory )
        throws IOException
    {
        if ( implementation == null )
        {
            throw new NullPointerException( "implementation" );
        }
        if ( sourceDirectory == null )
        {
            throw new NullPointerException( "sourceDirectory" );
        }

        final File f = new File( sourceDirectory, implementation.getClazz().replace( '.', '/' ) + ".java" );

        String content;
        if ( f.exists() )
        {
            content = FileUtils.readFileToString( f, this.getInputEncoding() );
        }
        else
        {
            if ( !f.getParentFile().exists() )
            {
                f.getParentFile().mkdirs();
            }

            content = this.getImplementationTemplate( implementation );
        }

        final JavaEditor editor = this.newJavaEditor( implementation );
        final String edited = editor.edit( content );

        if ( editor.isConstructorsSectionEdited() && !editor.isDefaultConstructorSectionEdited() )
        {
            throw new IOException( this.getMessage( "missingSection", new Object[]
                {
                    CONSTRUCTORS_SECTION_NAME,
                    implementation.getIdentifier()
                } ) );

        }
        if ( implementation.getSpecifications() != null &&
             !implementation.getSpecifications().getReference().isEmpty() && !editor.isConstructorsSectionEdited() )
        {
            throw new IOException( this.getMessage( "missingSection", new Object[]
                {
                    CONSTRUCTORS_SECTION_NAME,
                    implementation.getIdentifier()
                } ) );

        }
        if ( implementation.getProperties() != null &&
             !implementation.getProperties().getProperty().isEmpty() && !editor.isPropertiesSectionEdited() )
        {
            throw new IOException( this.getMessage( "missingSection", new Object[]
                {
                    PROPERTIES_SECTION_NAME,
                    implementation.getIdentifier()
                } ) );

        }
        if ( implementation.getDependencies() != null &&
             !implementation.getDependencies().getDependency().isEmpty() && !editor.isDependenciesSectionEdited() )
        {
            throw new IOException( this.getMessage( "missingSection", new Object[]
                {
                    DEPENDENCIES_SECTION_NAME,
                    implementation.getIdentifier()
                } ) );

        }
        if ( implementation.getMessages() != null &&
             !( implementation.getMessages().getReference().isEmpty() &&
                implementation.getMessages().getMessage().isEmpty() ) && !editor.isMessagesSectionEdited() )
        {
            throw new IOException( this.getMessage( "missingSection", new Object[]
                {
                    MESSAGES_SECTION_NAME,
                    implementation.getIdentifier()
                } ) );

        }
        if ( !editor.isAnnotationsSectionEdited() )
        {
            throw new IOException( this.getMessage( "missingSection", new Object[]
                {
                    ANNOTATIONS_SECTION_NAME,
                    implementation.getIdentifier()
                } ) );

        }

        if ( !edited.equals( content ) )
        {
            FileUtils.writeStringToFile( f, edited, this.getOutputEncoding() );
            this.log( Level.INFO, this.getMessage( "editing", new Object[]
                {
                    f.getCanonicalPath()
                } ), null );

        }
    }

    /**
     * Gets a new editor for editing an implementation source code file.
     *
     * @param implementation The implementation to create a new editor for.
     *
     * @return A new editor for editing an implementation source code file.
     */
    public JavaEditor newJavaEditor( final Implementation implementation )
    {
        return new JavaEditor( new TrailingWhitespaceEditor(), implementation );
    }

    /**
     * Gets a new editor for editing a specification source code file.
     *
     * @param specification The specification to create a new editor for.
     *
     * @return A new editor for editing a specification source code file.
     */
    public JavaEditor newJavaEditor( final Specification specification )
    {
        return new JavaEditor( new TrailingWhitespaceEditor(), specification );
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
     * Gets the source code of the constructors section head content of an implemenation.
     *
     * @param implementation The implementation to get the constructors section head content of.
     *
     * @throws IOException if getting the source code section fails.
     */
    private String getConstructorsSectionHeadContent( final Implementation implementation ) throws IOException
    {
        try
        {
            final StringWriter writer = new StringWriter();
            final VelocityContext ctx = this.getVelocityContext();
            final String template = this.getTemplateLocation( CONSTRUCTORS_HEAD_TEMPLATE );
            ctx.put( "implementation", implementation );
            ctx.put( "templateLocation", template );
            this.getVelocityEngine().mergeTemplate( template, this.getTemplateEncoding(), ctx, writer );
            return writer.toString();
        }
        catch ( Exception e )
        {
            throw (IOException) new IOException( e.getMessage() ).initCause( e );
        }
    }

    /**
     * Gets the source code of the constructors section tail content of an implemenation.
     *
     * @param implementation The implementation to get the constructors section tail content of.
     *
     * @throws IOException if getting the source code section fails.
     */
    private String getConstructorsSectionTailContent( final Implementation implementation ) throws IOException
    {
        try
        {
            final StringWriter writer = new StringWriter();
            final VelocityContext ctx = this.getVelocityContext();
            final String template = this.getTemplateLocation( CONSTRUCTORS_TAIL_TEMPLATE );
            ctx.put( "implementation", implementation );
            ctx.put( "templateLocation", template );
            this.getVelocityEngine().mergeTemplate( template, this.getTemplateEncoding(), ctx, writer );
            return writer.toString();
        }
        catch ( Exception e )
        {
            throw (IOException) new IOException( e.getMessage() ).initCause( e );
        }
    }

    /**
     * Gets the source code of the dependencies section.
     *
     * @param implementation The implementation to get the source code of the dependencies section of.
     *
     * @throws IOException if getting the source code section fails.
     */
    private String getDependenciesSection( final Implementation implementation ) throws IOException
    {
        try
        {
            final StringWriter writer = new StringWriter();
            final VelocityContext ctx = this.getVelocityContext();
            final String template = this.getTemplateLocation( DEPENDENCIES_TEMPLATE );
            ctx.put( "implementation", implementation );
            ctx.put( "templateLocation", template );
            this.getVelocityEngine().mergeTemplate( template, this.getTemplateEncoding(), ctx, writer );
            return writer.toString();
        }
        catch ( Exception e )
        {
            throw (IOException) new IOException( e.getMessage() ).initCause( e );
        }
    }

    /**
     * Gets the source code of the properties section.
     *
     * @param implementation The implementation to get the source code of the properties section of.
     *
     * @throws IOException if getting the source code section fails.
     */
    private String getPropertiesSection( final Implementation implementation ) throws IOException
    {
        try
        {
            final StringWriter writer = new StringWriter();
            final VelocityContext ctx = this.getVelocityContext();
            final String template = this.getTemplateLocation( PROPERTIES_TEMPLATE );
            ctx.put( "implementation", implementation );
            ctx.put( "templateLocation", template );
            this.getVelocityEngine().mergeTemplate( template, this.getTemplateEncoding(), ctx, writer );
            return writer.toString();
        }
        catch ( Exception e )
        {
            throw (IOException) new IOException( e.getMessage() ).initCause( e );
        }
    }

    /**
     * Gets the source code of the messages section.
     *
     * @param implementation The implementation to get the source code of the messages section of.
     *
     * @throws IOException if getting the source code section fails.
     */
    private String getMessagesSection( final Implementation implementation ) throws IOException
    {
        try
        {
            final StringWriter writer = new StringWriter();
            final VelocityContext ctx = this.getVelocityContext();
            final String template = this.getTemplateLocation( MESSAGES_TEMPLATE );
            ctx.put( "implementation", implementation );
            ctx.put( "templateLocation", template );
            this.getVelocityEngine().mergeTemplate( template, this.getTemplateEncoding(), ctx, writer );
            return writer.toString();
        }
        catch ( Exception e )
        {
            throw (IOException) new IOException( e.getMessage() ).initCause( e );
        }
    }

    /**
     * Gets the source code of the license section.
     *
     * @param implementation The implementation to get the source code of the license section of.
     *
     * @throws IOException if getting the source code section fails.
     */
    private String getLicenseSection( final Implementation implementation ) throws IOException
    {
        try
        {
            final StringWriter writer = new StringWriter();
            final VelocityContext ctx = this.getVelocityContext();
            final String template = this.getTemplateLocation( LICENSE_TEMPLATE );
            ctx.put( "authors", implementation.getAuthors() );
            ctx.put( "templateLocation", template );
            this.getVelocityEngine().mergeTemplate( template, this.getTemplateEncoding(), ctx, writer );
            return writer.toString();
        }
        catch ( Exception e )
        {
            throw (IOException) new IOException( e.getMessage() ).initCause( e );
        }
    }

    /**
     * Gets the source code of the license section.
     *
     * @param specification The specification to get the source code of the license section of.
     *
     * @throws IOException if getting the source code section fails.
     */
    private String getLicenseSection( final Specification specification ) throws IOException
    {
        try
        {
            final StringWriter writer = new StringWriter();
            final VelocityContext ctx = this.getVelocityContext();
            final String template = this.getTemplateLocation( LICENSE_TEMPLATE );
            ctx.put( "authors", specification.getAuthors() );
            ctx.put( "templateLocation", template );
            this.getVelocityEngine().mergeTemplate( template, this.getTemplateEncoding(), ctx, writer );
            return writer.toString();
        }
        catch ( Exception e )
        {
            throw (IOException) new IOException( e.getMessage() ).initCause( e );
        }
    }

    /**
     * Gets the source code of the specification comment section.
     *
     * @param specification The specification to get the source code section of.
     *
     * @throws IOException if getting the source code section fails.
     */
    private String getCommentSection( final Specification specification ) throws IOException
    {
        try
        {
            final StringWriter writer = new StringWriter();
            final VelocityContext ctx = this.getVelocityContext();
            final String template = this.getTemplateLocation( SPECIFICATION_COMMENT_TEMPLATE );
            ctx.put( "specification", specification );
            ctx.put( "templateLocation", template );
            this.getVelocityEngine().mergeTemplate( template, this.getTemplateEncoding(), ctx, writer );
            return writer.toString();
        }
        catch ( Exception e )
        {
            throw (IOException) new IOException( e.getMessage() ).initCause( e );
        }
    }

    /**
     * Gets the source code of the implementation comment section.
     *
     * @param implementation The implementation to get the source code section of.
     *
     * @throws IOException if getting the source code section fails.
     */
    private String getCommentSection( final Implementation implementation ) throws IOException
    {
        try
        {
            final StringWriter writer = new StringWriter();
            final VelocityContext ctx = this.getVelocityContext();
            final String template = this.getTemplateLocation( IMPLEMENTATION_COMMENT_TEMPLATE );
            ctx.put( "implementation", implementation );
            ctx.put( "templateLocation", template );
            this.getVelocityEngine().mergeTemplate( template, this.getTemplateEncoding(), ctx, writer );
            return writer.toString();
        }
        catch ( Exception e )
        {
            throw (IOException) new IOException( e.getMessage() ).initCause( e );
        }
    }

    /**
     * Gets the source code template of an implementation.
     *
     * @param implementation The implementation to get the source code template of.
     *
     * @throws IOException if getting the source code section fails.
     */
    private String getImplementationTemplate( final Implementation implementation ) throws IOException
    {
        try
        {
            final StringWriter writer = new StringWriter();
            final VelocityContext ctx = this.getVelocityContext();
            final String template = this.getTemplateLocation( IMPLEMENTATION_TEMPLATE );
            ctx.put( "implementation", implementation );
            ctx.put( "templateLocation", template );
            this.getVelocityEngine().mergeTemplate( template, this.getTemplateEncoding(), ctx, writer );
            return writer.toString();
        }
        catch ( Exception e )
        {
            throw (IOException) new IOException( e.getMessage() ).initCause( e );
        }
    }

    /**
     * Gets the source code template of a specification.
     *
     * @param specification The specification to get the source code template of.
     *
     * @throws IOException if getting the source code section fails.
     */
    private String getSpecificationTemplate( final Specification specification ) throws IOException
    {
        try
        {
            final StringWriter writer = new StringWriter();
            final VelocityContext ctx = this.getVelocityContext();
            final String template = this.getTemplateLocation( SPECIFICATION_TEMPLATE );
            ctx.put( "specification", specification );
            ctx.put( "templateLocation", template );
            this.getVelocityEngine().mergeTemplate( template, this.getTemplateEncoding(), ctx, writer );
            return writer.toString();
        }
        catch ( Exception e )
        {
            throw (IOException) new IOException( e.getMessage() ).initCause( e );
        }
    }

    /**
     * Gets the source code of the specification annotations section.
     *
     * @param specification The specification to get the source code of the annotations section of.
     *
     * @throws IOException if getting the source code section fails.
     */
    private String getSpecificationAnnotationsTemplate( final Specification specification ) throws IOException
    {
        try
        {
            final StringWriter writer = new StringWriter();
            final VelocityContext ctx = this.getVelocityContext();
            final String template = this.getTemplateLocation( SPECIFICATION_ANNOTATIONS_TEMPLATE );
            ctx.put( "specification", specification );
            ctx.put( "templateLocation", template );
            this.getVelocityEngine().mergeTemplate( template, this.getTemplateEncoding(), ctx, writer );
            return writer.toString();
        }
        catch ( Exception e )
        {
            throw (IOException) new IOException( e.getMessage() ).initCause( e );
        }
    }

    /**
     * Gets the source code of the implementation annotations section.
     *
     * @param implementation The implementation to get the source code of the annotations section of.
     *
     * @throws IOException if getting the source code section fails.
     */
    private String getImplementationAnnotationsTemplate( final Implementation implementation ) throws IOException
    {
        try
        {
            final StringWriter writer = new StringWriter();
            final VelocityContext ctx = this.getVelocityContext();
            final String template = this.getTemplateLocation( IMPLEMENTATION_ANNOTATIONS_TEMPLATE );
            ctx.put( "implementation", implementation );
            ctx.put( "templateLocation", template );
            this.getVelocityEngine().mergeTemplate( template, this.getTemplateEncoding(), ctx, writer );
            return writer.toString();
        }
        catch ( Exception e )
        {
            throw (IOException) new IOException( e.getMessage() ).initCause( e );
        }
    }

    private String getMessage( final String key, final Object args )
    {
        final ResourceBundle b = ResourceBundle.getBundle( this.getClass().getName().replace( '.', '/' ) );
        final MessageFormat f = new MessageFormat( b.getString( key ) );
        return f.format( args );
    }

    public class JavaEditor extends SectionEditor
    {

        private Specification specification;

        private Implementation implementation;

        private boolean licenseSectionEdited;

        private boolean constructorsSectionEdited;

        private boolean defaultConstructorSectionEdited;

        private boolean implementationCommentSectionEdited;

        private boolean specificationCommentSectionEdited;

        private boolean messagesSectionEdited;

        private boolean dependenciesSectionEdited;

        private boolean propertiesSectionEdited;

        private boolean annotationsSectionEdited;

        public JavaEditor( final Specification specification )
        {
            super();
            this.specification = specification;
        }

        public JavaEditor( final Implementation implementation )
        {
            super();
            this.implementation = implementation;
        }

        public JavaEditor( final LineEditor lineEditor, final Specification specification )
        {
            super( lineEditor );
            this.specification = specification;
        }

        public JavaEditor( final LineEditor lineEditor, final Implementation implementation )
        {
            super( lineEditor );
            this.implementation = implementation;
        }

        @Override
        public String getOutput( final Section root )
        {
            try
            {
                Section ctorSection = null;
                List<Section> sections = root.getSections();
                for ( Section s : sections )
                {
                    if ( LICENSE_SECTION_NAME.equals( s.getName() ) )
                    {
                        this.editLicenseSection( s );
                    }
                    if ( this.implementation != null )
                    {
                        if ( CONSTRUCTORS_SECTION_NAME.equals( s.getName() ) )
                        {
                            this.editConstructorsSection( s );
                            ctorSection = s;
                        }
                        else if ( DEFAULT_CONSTRUCTOR_SECTION_NAME.equals( s.getName() ) )
                        {
                            this.editDefaultConstructorSection( s );
                        }
                        else if ( DEPENDENCIES_SECTION_NAME.equals( s.getName() ) )
                        {
                            this.editDependenciesSection( s );
                        }
                        else if ( IMPLEMENTATION_COMMENT_SECTION_NAME.equals( s.getName() ) )
                        {
                            this.editImplementationCommentSection( s );
                        }
                        else if ( MESSAGES_SECTION_NAME.equals( s.getName() ) )
                        {
                            this.editMessagesSection( s );
                        }
                        else if ( PROPERTIES_SECTION_NAME.equals( s.getName() ) )
                        {
                            this.editPropertiesSection( s );
                        }
                        else if ( ANNOTATIONS_SECTION_NAME.equals( s.getName() ) )
                        {
                            this.editImplementationAnnotationsSection( s );
                        }
                    }
                    else if ( this.specification != null )
                    {
                        if ( SPECIFICATION_COMMENT_SECTION_NAME.equals( s.getName() ) )
                        {
                            this.editSpecificationCommentSection( s );
                        }
                        else if ( ANNOTATIONS_SECTION_NAME.equals( s.getName() ) )
                        {
                            this.editSpecificationAnnotationsSection( s );
                        }
                    }
                }

                if ( this.isConstructorsSectionEdited() && !this.isDefaultConstructorSectionEdited() )
                {
                    ctorSection.getHeadContent().append( "        // SECTION-START[Default Constructor]\n" );
                    ctorSection.getHeadContent().append( "        super();\n" );
                    ctorSection.getHeadContent().append( "        // SECTION-END\n" );
                    this.defaultConstructorSectionEdited = true;
                }

                return super.getOutput( root );
            }
            catch ( Exception e )
            {
                throw new RuntimeException( e );
            }
        }

        public void editLicenseSection( final Section s ) throws IOException
        {
            s.getHeadContent().setLength( 0 );
            if ( this.specification != null )
            {
                s.getHeadContent().append( getLicenseSection( this.specification ) );
            }
            if ( this.implementation != null )
            {
                s.getHeadContent().append( getLicenseSection( this.implementation ) );
            }

            this.licenseSectionEdited = true;
        }

        public void editConstructorsSection( final Section s ) throws IOException
        {
            s.getHeadContent().setLength( 0 );
            s.getTailContent().setLength( 0 );
            s.getHeadContent().append( getConstructorsSectionHeadContent( implementation ) );
            s.getTailContent().append( getConstructorsSectionTailContent( implementation ) );
            this.constructorsSectionEdited = true;
        }

        public void editDefaultConstructorSection( final Section s )
        {
            if ( s.getHeadContent().toString().trim().length() == 0 )
            {
                s.getHeadContent().setLength( 0 );
                s.getHeadContent().append( "        super();\n" ).toString();

            }

            this.defaultConstructorSectionEdited = true;
        }

        public void editDependenciesSection( final Section s ) throws IOException
        {
            s.getHeadContent().setLength( 0 );
            s.getHeadContent().append( getDependenciesSection( implementation ) );
            this.dependenciesSectionEdited = true;
        }

        public void editMessagesSection( final Section s ) throws IOException
        {
            s.getHeadContent().setLength( 0 );
            s.getHeadContent().append( getMessagesSection( implementation ) );
            this.messagesSectionEdited = true;
        }

        public void editPropertiesSection( final Section s ) throws IOException
        {
            s.getHeadContent().setLength( 0 );
            s.getHeadContent().append( getPropertiesSection( implementation ) );
            this.propertiesSectionEdited = true;
        }

        public void editImplementationCommentSection( final Section s ) throws IOException
        {
            s.getHeadContent().setLength( 0 );
            s.getHeadContent().append( getCommentSection( implementation ) );
            this.implementationCommentSectionEdited = true;
        }

        public void editImplementationAnnotationsSection( final Section s ) throws IOException
        {
            s.getHeadContent().setLength( 0 );
            s.getHeadContent().append( getImplementationAnnotationsTemplate( implementation ) );
            this.annotationsSectionEdited = true;
        }

        public void editSpecificationCommentSection( final Section s ) throws IOException
        {
            s.getHeadContent().setLength( 0 );
            s.getHeadContent().append( getCommentSection( specification ) );
            this.specificationCommentSectionEdited = true;
        }

        public void editSpecificationAnnotationsSection( final Section s ) throws IOException
        {
            s.getHeadContent().setLength( 0 );
            s.getHeadContent().append( getSpecificationAnnotationsTemplate( specification ) );
            this.annotationsSectionEdited = true;
        }

        public boolean isLicenseSectionEdited()
        {
            return this.licenseSectionEdited;
        }

        public boolean isConstructorsSectionEdited()
        {
            return this.constructorsSectionEdited;
        }

        public boolean isDefaultConstructorSectionEdited()
        {
            return this.defaultConstructorSectionEdited;
        }

        public boolean isImplementationCommentSectionEdited()
        {
            return this.implementationCommentSectionEdited;
        }

        public boolean isSpecificationCommentSectionEdited()
        {
            return this.specificationCommentSectionEdited;
        }

        public boolean isMessagesSectionEdited()
        {
            return this.messagesSectionEdited;
        }

        public boolean isDependenciesSectionEdited()
        {
            return this.dependenciesSectionEdited;
        }

        public boolean isPropertiesSectionEdited()
        {
            return this.propertiesSectionEdited;
        }

        public boolean isAnnotationsSectionEdited()
        {
            return this.annotationsSectionEdited;
        }

    }

}
