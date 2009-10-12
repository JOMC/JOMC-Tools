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

    /** Creates a new {@code JavaSources} instance. */
    public JavaSources()
    {
        super();
    }

    /**
     * Creates a new {@code JavaSources} instance taking a {@code JavaSources} instance to initialize the instance with.
     *
     * @param tool The instance to initialize the new instance with,
     */
    public JavaSources( final JavaSources tool )
    {
        super( tool );
    }

    /**
     * Manages the source code of the modules of the instance.
     *
     * @param sourcesDirectory The directory holding the sources to manage.
     *
     * @throws NullPointerException if {@code sourcesDirectory} is {@code null}.
     * @throws IOException if managing sources fails.
     *
     * @see #manageSources(org.jomc.model.Module, java.io.File)
     */
    public void manageSources( final File sourcesDirectory ) throws IOException
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
     * @throws IOException if managing sources fails.
     *
     * @see #manageSources(org.jomc.model.Specification, java.io.File)
     * @see #manageSources(org.jomc.model.Implementation, java.io.File)
     */
    public void manageSources( final Module module, final File sourcesDirectory ) throws IOException
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
     * @throws IOException if managing sources fails.
     *
     * @see #getSpecificationEditor(org.jomc.model.Specification)
     */
    public void manageSources( final Specification specification, final File sourcesDirectory ) throws IOException
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
        if ( i != null && this.isJavaClassDeclaration( i ) )
        {
            this.manageSources( i, sourcesDirectory );
        }
        else if ( this.isJavaClassDeclaration( specification ) )
        {
            final File f = new File( sourcesDirectory, specification.getIdentifier().replace( '.', '/' ) + ".java" );
            final String content = f.exists()
                                   ? FileUtils.readFileToString( f, this.getInputEncoding() )
                                   : this.getSpecificationTemplate( specification );

            final JavaSpecificationEditor editor = this.getSpecificationEditor( specification );
            final String edited;
            try
            {
                edited = editor.edit( content );
            }
            catch ( final IOException e )
            {
                throw (IOException) new IOException( this.getMessage( "failedEditing", new Object[]
                    {
                        f.getCanonicalPath(), e.getMessage()
                    } ) ).initCause( e );

            }

            if ( !editor.isLicenseSectionPresent() && this.isLoggable( Level.INFO ) )
            {
                this.log( Level.INFO, this.getMessage( "missingOptionalSection", new Object[]
                    {
                        LICENSE_SECTION_NAME,
                        f.getCanonicalPath()
                    } ), null );

            }

            if ( !editor.isAnnotationsSectionPresent() )
            {
                throw new IOException( this.getMessage( "missingSection", new Object[]
                    {
                        ANNOTATIONS_SECTION_NAME,
                        f.getCanonicalPath()
                    } ) );

            }

            if ( !editor.isDocumentationSectionPresent() && this.isLoggable( Level.INFO ) )
            {
                this.log( Level.INFO, this.getMessage( "missingOptionalSection", new Object[]
                    {
                        DOCUMENTATION_SECTION_NAME,
                        f.getCanonicalPath()
                    } ), null );

            }

            if ( !edited.equals( content ) )
            {
                if ( !f.getParentFile().exists() && !f.getParentFile().mkdirs() )
                {
                    throw new IOException( this.getMessage( "failedCreatingDirectory", new Object[]
                        {
                            f.getParentFile().getAbsolutePath()
                        } ) );

                }

                if ( this.isLoggable( Level.INFO ) )
                {
                    this.log( Level.INFO, this.getMessage( "editing", new Object[]
                        {
                            f.getCanonicalPath()
                        } ), null );

                }

                FileUtils.writeStringToFile( f, edited, this.getOutputEncoding() );
            }
        }
    }

    /**
     * Manages the source code of a given implementation of the modules of the instance.
     *
     * @param implementation The implementation to process.
     * @param sourcesDirectory The directory holding the sources to manage.
     *
     * @throws NullPointerException if {@code implementation} or {@code sourcesDirectory} is {@code null}.
     * @throws IOException if managing sources fails.
     *
     * @see #getImplementationEditor(org.jomc.model.Implementation)
     */
    public void manageSources( final Implementation implementation, final File sourcesDirectory ) throws IOException
    {
        if ( implementation == null )
        {
            throw new NullPointerException( "implementation" );
        }
        if ( sourcesDirectory == null )
        {
            throw new NullPointerException( "sourcesDirectory" );
        }

        if ( this.isJavaClassDeclaration( implementation ) )
        {
            final File f = new File( sourcesDirectory, implementation.getClazz().replace( '.', '/' ) + ".java" );
            final String content = f.exists()
                                   ? FileUtils.readFileToString( f, this.getInputEncoding() )
                                   : this.getImplementationTemplate( implementation );

            final JavaImplementationEditor editor = this.getImplementationEditor( implementation );
            final String edited;
            try
            {
                edited = editor.edit( content );
            }
            catch ( final IOException e )
            {
                throw (IOException) new IOException( this.getMessage( "failedEditing", new Object[]
                    {
                        f.getCanonicalPath(), e.getMessage()
                    } ) ).initCause( e );

            }

            if ( !editor.isLicenseSectionPresent() && this.isLoggable( Level.INFO ) )
            {
                this.log( Level.INFO, this.getMessage( "missingOptionalSection", new Object[]
                    {
                        LICENSE_SECTION_NAME,
                        f.getCanonicalPath()
                    } ), null );

            }

            if ( !editor.isAnnotationsSectionPresent() )
            {
                throw new IOException( this.getMessage( "missingSection", new Object[]
                    {
                        ANNOTATIONS_SECTION_NAME,
                        f.getCanonicalPath()
                    } ) );

            }

            if ( !editor.isDocumentationSectionPresent() && this.isLoggable( Level.INFO ) )
            {
                this.log( Level.INFO, this.getMessage( "missingOptionalSection", new Object[]
                    {
                        DOCUMENTATION_SECTION_NAME,
                        f.getCanonicalPath()
                    } ), null );

            }

            if ( !editor.isConstructorsSectionPresent() )
            {
                final Specifications specifications =
                    this.getModules().getSpecifications( implementation.getIdentifier() );

                if ( specifications != null &&
                     !( specifications.getSpecification().isEmpty() && specifications.getReference().isEmpty() ) )
                {
                    throw new IOException( this.getMessage( "missingSection", new Object[]
                        {
                            CONSTRUCTORS_SECTION_NAME,
                            f.getCanonicalPath()
                        } ) );

                }
                else if ( this.isLoggable( Level.INFO ) )
                {
                    this.log( Level.INFO, this.getMessage( "missingOptionalSection", new Object[]
                        {
                            CONSTRUCTORS_SECTION_NAME,
                            f.getCanonicalPath()
                        } ), null );

                }
            }
            else if ( !editor.isDefaultConstructorSectionPresent() )
            {
                throw new IOException( this.getMessage( "missingSection", new Object[]
                    {
                        DEFAULT_CONSTRUCTOR_SECTION_NAME,
                        f.getCanonicalPath()
                    } ) );

            }

            if ( !editor.isPropertiesSectionPresent() )
            {
                final Properties properties = this.getModules().getProperties( implementation.getIdentifier() );

                if ( properties != null && !properties.getProperty().isEmpty() )
                {
                    throw new IOException( this.getMessage( "missingSection", new Object[]
                        {
                            PROPERTIES_SECTION_NAME,
                            f.getCanonicalPath()
                        } ) );

                }
                else if ( this.isLoggable( Level.INFO ) )
                {
                    this.log( Level.INFO, this.getMessage( "missingOptionalSection", new Object[]
                        {
                            PROPERTIES_SECTION_NAME,
                            f.getCanonicalPath()
                        } ), null );

                }
            }

            if ( !editor.isDependenciesSectionPresent() )
            {
                final Dependencies dependencies = this.getModules().getDependencies( implementation.getIdentifier() );

                if ( dependencies != null && !dependencies.getDependency().isEmpty() )
                {
                    throw new IOException( this.getMessage( "missingSection", new Object[]
                        {
                            DEPENDENCIES_SECTION_NAME,
                            f.getCanonicalPath()
                        } ) );

                }
                else if ( this.isLoggable( Level.INFO ) )
                {
                    this.log( Level.INFO, this.getMessage( "missingOptionalSection", new Object[]
                        {
                            DEPENDENCIES_SECTION_NAME,
                            f.getCanonicalPath()
                        } ), null );

                }
            }

            if ( !editor.isMessagesSectionPresent() )
            {
                final Messages messages = this.getModules().getMessages( implementation.getIdentifier() );

                if ( messages != null && !messages.getMessage().isEmpty() )
                {
                    throw new IOException( this.getMessage( "missingSection", new Object[]
                        {
                            MESSAGES_SECTION_NAME,
                            f.getCanonicalPath()
                        } ) );

                }
                else if ( this.isLoggable( Level.INFO ) )
                {
                    this.log( Level.INFO, this.getMessage( "missingOptionalSection", new Object[]
                        {
                            MESSAGES_SECTION_NAME,
                            f.getCanonicalPath()
                        } ), null );

                }
            }

            if ( !edited.equals( content ) )
            {
                if ( !f.getParentFile().exists() && !f.getParentFile().mkdirs() )
                {
                    throw new IOException( this.getMessage( "failedCreatingDirectory", new Object[]
                        {
                            f.getParentFile().getAbsolutePath()
                        } ) );

                }

                if ( this.isLoggable( Level.INFO ) )
                {
                    this.log( Level.INFO, this.getMessage( "editing", new Object[]
                        {
                            f.getCanonicalPath()
                        } ), null );

                }

                FileUtils.writeStringToFile( f, edited, this.getOutputEncoding() );
            }
        }
    }

    /**
     * Gets a new editor for editing Java specification source code.
     *
     * @param specification The specification to create a new editor for.
     *
     * @return A new editor for editing the source code of {@code specification}.
     *
     * @throws NullPointerException if {@code specification} is {@code null}.
     */
    public JavaSpecificationEditor getSpecificationEditor( final Specification specification )
    {
        if ( specification == null )
        {
            throw new NullPointerException( "specification" );
        }

        return new JavaSpecificationEditor( new TrailingWhitespaceEditor(), specification );
    }

    /**
     * Gets a new editor for editing Java implementation source code.
     *
     * @param implementation The implementation to create a new editor for.
     *
     * @return A new editor for editing the source code of {@code implementation}.
     *
     * @throws NullPointerException if {@code implementation} is {@code null}.
     */
    public JavaImplementationEditor getImplementationEditor( final Implementation implementation )
    {
        if ( implementation == null )
        {
            throw new NullPointerException( "implementation" );
        }

        return new JavaImplementationEditor( new TrailingWhitespaceEditor(), implementation );
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
     * Gets the Java source code template of specification.
     *
     * @param specification The specification to get the source code template of.
     *
     * @throws IOException if getting the source code section fails.
     */
    private String getSpecificationTemplate( final Specification specification ) throws IOException
    {
        final StringWriter writer = new StringWriter();
        final VelocityContext ctx = this.getVelocityContext();
        final Template template = this.getVelocityTemplate( SPECIFICATION_TEMPLATE );
        ctx.put( "specification", specification );
        ctx.put( "template", template );
        template.merge( ctx, writer );
        writer.close();
        return writer.toString();
    }

    /**
     * Gets the Java source code template of an implementation.
     *
     * @param implementation The implementation to get the source code template of.
     *
     * @throws IOException if getting the source code section fails.
     */
    private String getImplementationTemplate( final Implementation implementation ) throws IOException
    {
        final StringWriter writer = new StringWriter();
        final VelocityContext ctx = this.getVelocityContext();
        final Template template = this.getVelocityTemplate( IMPLEMENTATION_TEMPLATE );
        ctx.put( "implementation", implementation );
        ctx.put( "template", template );
        template.merge( ctx, writer );
        writer.close();
        return writer.toString();
    }

    /**
     * Gets the Java source code of the license section of a specification.
     *
     * @param specification The specification to get the source code of the license section of.
     *
     * @throws IOException if getting the source code section fails.
     */
    private String getLicenseSection( final Specification specification ) throws IOException
    {
        final StringWriter writer = new StringWriter();
        final VelocityContext ctx = this.getVelocityContext();
        final Template template = this.getVelocityTemplate( SPECIFICATION_LICENSE_TEMPLATE );
        ctx.put( "specification", specification );
        ctx.put( "template", template );
        template.merge( ctx, writer );
        writer.close();
        return writer.toString();
    }

    /**
     * Gets the Java source code of the license section of an implementation..
     *
     * @param implementation The implementation to get the source code of the license section of.
     *
     * @throws IOException if getting the source code section fails.
     */
    private String getLicenseSection( final Implementation implementation ) throws IOException
    {
        final StringWriter writer = new StringWriter();
        final VelocityContext ctx = this.getVelocityContext();
        final Template template = this.getVelocityTemplate( IMPLEMENTATION_LICENSE_TEMPLATE );
        ctx.put( "implementation", implementation );
        ctx.put( "template", template );
        template.merge( ctx, writer );
        writer.close();
        return writer.toString();
    }

    /**
     * Gets the Java source code of the specification annotations section.
     *
     * @param specification The specification to get the source code of the annotations section of.
     *
     * @throws IOException if getting the source code section fails.
     */
    private String getAnnotationsSection( final Specification specification ) throws IOException
    {
        final StringWriter writer = new StringWriter();
        final VelocityContext ctx = this.getVelocityContext();
        final Template template = this.getVelocityTemplate( SPECIFICATION_ANNOTATIONS_TEMPLATE );
        ctx.put( "specification", specification );
        ctx.put( "template", template );
        template.merge( ctx, writer );
        writer.close();
        return writer.toString();
    }

    /**
     * Gets the Java source code of the implementation annotations section.
     *
     * @param implementation The implementation to get the source code of the annotations section of.
     *
     * @throws IOException if getting the source code section fails.
     */
    private String getAnnotationsSection( final Implementation implementation ) throws IOException
    {
        final StringWriter writer = new StringWriter();
        final VelocityContext ctx = this.getVelocityContext();
        final Template template = this.getVelocityTemplate( IMPLEMENTATION_ANNOTATIONS_TEMPLATE );
        ctx.put( "implementation", implementation );
        ctx.put( "template", template );
        template.merge( ctx, writer );
        writer.close();
        return writer.toString();
    }

    /**
     * Gets the Java source code of the documentation section of a specification.
     *
     * @param specification The specification to get the source code section of.
     *
     * @throws IOException if getting the source code section fails.
     */
    private String getDocumentationSection( final Specification specification ) throws IOException
    {
        final StringWriter writer = new StringWriter();
        final VelocityContext ctx = this.getVelocityContext();
        final Template template = this.getVelocityTemplate( SPECIFICATION_DOCUMENTATION_TEMPLATE );
        ctx.put( "specification", specification );
        ctx.put( "template", template );
        template.merge( ctx, writer );
        writer.close();
        return writer.toString();
    }

    /**
     * Gets the Java source code of the documentation section of an implementation.
     *
     * @param implementation The implementation to get the source code section of.
     *
     * @throws IOException if getting the source code section fails.
     */
    private String getDocumentationSection( final Implementation implementation ) throws IOException
    {
        final StringWriter writer = new StringWriter();
        final VelocityContext ctx = this.getVelocityContext();
        final Template template = this.getVelocityTemplate( IMPLEMENTATION_DOCUMENTATION_TEMPLATE );
        ctx.put( "implementation", implementation );
        ctx.put( "template", template );
        template.merge( ctx, writer );
        writer.close();
        return writer.toString();
    }

    /**
     * Gets the Java source code of the constructors section head content of an implementation.
     *
     * @param implementation The implementation to get the constructors section head content of.
     *
     * @throws IOException if getting the source code section fails.
     */
    private String getConstructorsSectionHeadContent( final Implementation implementation ) throws IOException
    {
        final StringWriter writer = new StringWriter();
        final VelocityContext ctx = this.getVelocityContext();
        final Template template = this.getVelocityTemplate( CONSTRUCTORS_HEAD_TEMPLATE );
        ctx.put( "implementation", implementation );
        ctx.put( "template", template );
        template.merge( ctx, writer );
        writer.close();
        return writer.toString();
    }

    /**
     * Gets the Java source code of the constructors section tail content of an implementation.
     *
     * @param implementation The implementation to get the constructors section tail content of.
     *
     * @throws IOException if getting the source code section fails.
     */
    private String getConstructorsSectionTailContent( final Implementation implementation ) throws IOException
    {
        final StringWriter writer = new StringWriter();
        final VelocityContext ctx = this.getVelocityContext();
        final Template template = this.getVelocityTemplate( CONSTRUCTORS_TAIL_TEMPLATE );
        ctx.put( "implementation", implementation );
        ctx.put( "template", template );
        template.merge( ctx, writer );
        writer.close();
        return writer.toString();
    }

    /**
     * Gets the Java source code of the dependencies section of an implementation.
     *
     * @param implementation The implementation to get the source code of the dependencies section of.
     *
     * @throws IOException if getting the source code section fails.
     */
    private String getDependenciesSection( final Implementation implementation ) throws IOException
    {
        final StringWriter writer = new StringWriter();
        final VelocityContext ctx = this.getVelocityContext();
        final Template template = this.getVelocityTemplate( DEPENDENCIES_TEMPLATE );
        ctx.put( "implementation", implementation );
        ctx.put( "template", template );
        template.merge( ctx, writer );
        writer.close();
        return writer.toString();
    }

    /**
     * Gets the Java source code of the properties section of an implementation.
     *
     * @param implementation The implementation to get the source code of the properties section of.
     *
     * @throws IOException if getting the source code section fails.
     */
    private String getPropertiesSection( final Implementation implementation ) throws IOException
    {
        final StringWriter writer = new StringWriter();
        final VelocityContext ctx = this.getVelocityContext();
        final Template template = this.getVelocityTemplate( PROPERTIES_TEMPLATE );
        ctx.put( "implementation", implementation );
        ctx.put( "template", template );
        template.merge( ctx, writer );
        writer.close();
        return writer.toString();
    }

    /**
     * Gets the Java source code of the messages section of an implementation.
     *
     * @param implementation The implementation to get the source code of the messages section of.
     *
     * @throws IOException if getting the source code section fails.
     */
    private String getMessagesSection( final Implementation implementation ) throws IOException
    {
        final StringWriter writer = new StringWriter();
        final VelocityContext ctx = this.getVelocityContext();
        final Template template = this.getVelocityTemplate( MESSAGES_TEMPLATE );
        ctx.put( "implementation", implementation );
        ctx.put( "template", template );
        template.merge( ctx, writer );
        writer.close();
        return writer.toString();
    }

    private String getMessage( final String key, final Object args )
    {
        final ResourceBundle b = ResourceBundle.getBundle( JavaSources.class.getName().replace( '.', '/' ) );
        final MessageFormat f = new MessageFormat( b.getString( key ) );
        return f.format( args );
    }

    /**
     * Extension to {@code SectionEditor} for editing Java source code.
     *
     * @author <a href="mailto:cs@jomc.org">Christian Schulte</a>
     * @version $Id$
     */
    public abstract class JavaEditor extends SectionEditor
    {

        /** Flag indicating that the source code of the editor contains a license section. */
        private boolean licenseSectionPresent;

        /** Flag indicating that the source code of the editor contains an annotations section. */
        private boolean annotationsSectionPresent;

        /** Flag indicating that the source code of the editor contains a documentation section. */
        private boolean documentationSectionPresent;

        /** Creates a new {@code JavaEditor} instance. */
        public JavaEditor()
        {
            super();
        }

        /**
         * Creates a new {@code JavaEditor} instance taking a {@code LineEditor} to chain.
         *
         * @param lineEditor The editor to chain.
         */
        public JavaEditor( final LineEditor lineEditor )
        {
            super( lineEditor );
        }

        @Override
        public String getOutput( final Section section ) throws IOException
        {
            if ( section == null )
            {
                throw new NullPointerException( "section" );
            }

            this.licenseSectionPresent = false;
            this.annotationsSectionPresent = false;
            this.documentationSectionPresent = false;
            return super.getOutput( section );
        }

        @Override
        public void editSection( final Section section ) throws IOException
        {
            if ( section == null )
            {
                throw new NullPointerException( "section" );
            }

            if ( section.getName() != null )
            {
                if ( LICENSE_SECTION_NAME.equals( section.getName() ) )
                {
                    this.editLicenseSection( section );
                    this.licenseSectionPresent = true;
                }
                if ( ANNOTATIONS_SECTION_NAME.equals( section.getName() ) )
                {
                    this.editAnnotationsSection( section );
                    this.annotationsSectionPresent = true;
                }
                if ( DOCUMENTATION_SECTION_NAME.equals( section.getName() ) )
                {
                    this.editDocumentationSection( section );
                    this.documentationSectionPresent = true;
                }
            }
        }

        /**
         * Edits the license section of the source code of the editor.
         *
         * @param s The section to edit.
         *
         * @throws NullPointerException if {@code s} is {@code null}.
         * @throws IOException if editing {@code s} fails.
         */
        public abstract void editLicenseSection( final Section s ) throws IOException;

        /**
         * Edits the annotations section of the source code of the editor.
         *
         * @param s The section to edit.
         *
         * @throws NullPointerException if {@code s} is {@code null}.
         * @throws IOException if editing {@code s} fails.
         */
        public abstract void editAnnotationsSection( final Section s ) throws IOException;

        /**
         * Edits the documentation section of the source code of the editor.
         *
         * @param s The section to edit.
         *
         * @throws NullPointerException if {@code s} is {@code null}.
         * @throws IOException if editing {@code s} fails.
         */
        public abstract void editDocumentationSection( final Section s ) throws IOException;

        /**
         * Gets a flag indicating that the source code of the editor contains a license section.
         *
         * @return {@code true} if the source code of the editor contains a license section; {@code false} if the
         * source code of the editor does not contain a license section.
         */
        public boolean isLicenseSectionPresent()
        {
            return this.licenseSectionPresent;
        }

        /**
         * Gets a flag indicating that the source code of the editor contains an annotations section.
         *
         * @return {@code true} if the source code of the editor contains an annotations section; {@code false} if the
         * source code of the editor does not contain an annotations section.
         */
        public boolean isAnnotationsSectionPresent()
        {
            return this.annotationsSectionPresent;
        }

        /**
         * Gets a flag indicating that the source code of the editor contains a documentation section.
         *
         * @return {@code true} if the source code of the editor contains a documentation section; {@code false} if the
         * source code of the editor does not contain a documentation section.
         */
        public boolean isDocumentationSectionPresent()
        {
            return this.documentationSectionPresent;
        }

    }

    /**
     * Extension to {@code JavaEditor} for editing specification source code.
     *
     * @author <a href="mailto:cs@jomc.org">Christian Schulte</a>
     * @version $Id$
     */
    public class JavaSpecificationEditor extends JavaEditor
    {

        /** The specification to edit. */
        private Specification specification;

        /**
         * Creates a new {@code JavaSpecificationEditor} instance for editing the source code of a given specification.
         *
         * @param specification The specification to edit.
         */
        public JavaSpecificationEditor( final Specification specification )
        {
            super();
            this.specification = specification;
        }

        /**
         * Creates a new {@code JavaSpecificationEditor} instance for editing the source code of a given specification
         * taking a {@code LineEditor} to chain.
         *
         * @param lineEditor The editor to chain.
         * @param specification The specification to edit.
         */
        public JavaSpecificationEditor( final LineEditor lineEditor, final Specification specification )
        {
            super( lineEditor );
            this.specification = specification;
        }

        /**
         * Edits the license section of the source code of the editor.
         *
         * @param s The section to edit.
         *
         * @throws NullPointerException if {@code s} is {@code null}.
         * @throws IOException if editing {@code s} fails.
         */
        public void editLicenseSection( final Section s ) throws IOException
        {
            if ( s == null )
            {
                throw new NullPointerException( "s" );
            }

            s.getHeadContent().setLength( 0 );
            if ( this.specification != null )
            {
                s.getHeadContent().append( getLicenseSection( this.specification ) );
            }
        }

        /**
         * Edits the annotations section of the source code of the editor.
         *
         * @param s The section to edit.
         *
         * @throws NullPointerException if {@code s} is {@code null}.
         * @throws IOException if editing {@code s} fails.
         */
        public void editAnnotationsSection( final Section s ) throws IOException
        {
            if ( s == null )
            {
                throw new NullPointerException( "s" );
            }

            s.getHeadContent().setLength( 0 );
            if ( this.specification != null )
            {
                s.getHeadContent().append( getAnnotationsSection( this.specification ) );
            }
        }

        /**
         * Edits the documentation section of the source code of the editor.
         *
         * @param s The section to edit.
         *
         * @throws NullPointerException if {@code s} is {@code null}.
         * @throws IOException if editing {@code s} fails.
         */
        public void editDocumentationSection( final Section s ) throws IOException
        {
            if ( s == null )
            {
                throw new NullPointerException( "s" );
            }

            s.getHeadContent().setLength( 0 );
            if ( this.specification != null )
            {
                s.getHeadContent().append( getDocumentationSection( this.specification ) );
            }
        }

    }

    /**
     * Extension to {@code JavaEditor} for editing implementation source code.
     *
     * @author <a href="mailto:cs@jomc.org">Christian Schulte</a>
     * @version $Id$
     */
    public class JavaImplementationEditor extends JavaEditor
    {

        /** The implementation to edit. */
        private Implementation implementation;

        /** Flag indicating that the source code of the editor contains a constructors section. */
        private boolean constructorsSectionPresent;

        /** Flag indicating that the source code of the editor contains a default constructor section. */
        private boolean defaultConstructorSectionPresent;

        /** Flag indicating that the source code of the editor contains a messages section. */
        private boolean messagesSectionPresent;

        /** Flag indicating that the source code of the editor contains a dependencies section. */
        private boolean dependenciesSectionPresent;

        /** Flag indicating that the source code of the editor contains a properties section. */
        private boolean propertiesSectionPresent;

        /**
         * Creates a new {@code JavaImplementationEditor} instance for editing the source code of a given implementation.
         *
         * @param implementation The implementation to edit.
         */
        public JavaImplementationEditor( final Implementation implementation )
        {
            super();
            this.implementation = implementation;
        }

        /**
         * Creates a new {@code JavaImplementationEditor} instance for editing the source code of a given implementation
         * taking a {@code LineEditor} to chain.
         *
         * @param lineEditor The editor to chain.
         * @param implementation The implementation to edit.
         */
        public JavaImplementationEditor( final LineEditor lineEditor, final Implementation implementation )
        {
            super( lineEditor );
            this.implementation = implementation;
        }

        @Override
        public String getOutput( final Section section ) throws IOException
        {
            if ( section == null )
            {
                throw new NullPointerException( "section" );
            }

            this.constructorsSectionPresent = false;
            this.defaultConstructorSectionPresent = false;
            this.messagesSectionPresent = false;
            this.dependenciesSectionPresent = false;
            this.propertiesSectionPresent = false;
            return super.getOutput( section );
        }

        @Override
        public void editSection( final Section section ) throws IOException
        {
            if ( section == null )
            {
                throw new NullPointerException( "section" );
            }

            super.editSection( section );

            if ( section.getName() != null )
            {
                if ( CONSTRUCTORS_SECTION_NAME.equals( section.getName() ) )
                {
                    this.editConstructorsSection( section );
                    this.constructorsSectionPresent = true;
                }
                else if ( DEFAULT_CONSTRUCTOR_SECTION_NAME.equals( section.getName() ) )
                {
                    this.editDefaultConstructorSection( section );
                    this.defaultConstructorSectionPresent = true;
                }
                else if ( DEPENDENCIES_SECTION_NAME.equals( section.getName() ) )
                {
                    this.editDependenciesSection( section );
                    this.dependenciesSectionPresent = true;
                }
                else if ( MESSAGES_SECTION_NAME.equals( section.getName() ) )
                {
                    this.editMessagesSection( section );
                    this.messagesSectionPresent = true;
                }
                else if ( PROPERTIES_SECTION_NAME.equals( section.getName() ) )
                {
                    this.editPropertiesSection( section );
                    this.propertiesSectionPresent = true;
                }
            }
        }

        /**
         * Edits the license section of the source code of the editor.
         *
         * @param s The section to edit.
         *
         * @throws IOException if editing {@code s} fails.
         */
        public void editLicenseSection( final Section s ) throws IOException
        {
            if ( s == null )
            {
                throw new NullPointerException( "s" );
            }

            s.getHeadContent().setLength( 0 );
            if ( this.implementation != null )
            {
                s.getHeadContent().append( getLicenseSection( this.implementation ) );
            }
        }

        /**
         * Edits the annotations section of the source code of the editor.
         *
         * @param s The section to edit.
         *
         * @throws NullPointerException if {@code s} is {@code null}.
         * @throws IOException if editing {@code s} fails.
         */
        public void editAnnotationsSection( final Section s ) throws IOException
        {
            if ( s == null )
            {
                throw new NullPointerException( "s" );
            }

            s.getHeadContent().setLength( 0 );
            if ( this.implementation != null )
            {
                s.getHeadContent().append( getAnnotationsSection( this.implementation ) );
            }
        }

        /**
         * Edits the documentation section of the source code of the editor.
         *
         * @param s The section to edit.
         *
         * @throws NullPointerException if {@code s} is {@code null}.
         * @throws IOException if editing {@code s} fails.
         */
        public void editDocumentationSection( final Section s ) throws IOException
        {
            if ( s == null )
            {
                throw new NullPointerException( "s" );
            }

            s.getHeadContent().setLength( 0 );
            if ( this.implementation != null )
            {
                s.getHeadContent().append( getDocumentationSection( this.implementation ) );
            }
        }

        /**
         * Edits the constructors section of the source code of the editor.
         *
         * @param s The section to edit.
         *
         * @throws NullPointerException if {@code s} is {@code null}.
         * @throws IOException if editing {@code s} fails.
         */
        public void editConstructorsSection( final Section s ) throws IOException
        {
            if ( s == null )
            {
                throw new NullPointerException( "s" );
            }

            s.getHeadContent().setLength( 0 );
            s.getTailContent().setLength( 0 );

            if ( this.implementation != null )
            {
                s.getHeadContent().append( getConstructorsSectionHeadContent( this.implementation ) );
                s.getTailContent().append( getConstructorsSectionTailContent( this.implementation ) );
            }

            for ( Section child : s.getSections() )
            {
                if ( child.getName() != null && DEFAULT_CONSTRUCTOR_SECTION_NAME.equals( child.getName() ) )
                {
                    this.defaultConstructorSectionPresent = true;
                    break;
                }
            }

            if ( !this.defaultConstructorSectionPresent )
            {
                final Section defaultCtor = new Section();
                defaultCtor.setName( DEFAULT_CONSTRUCTOR_SECTION_NAME );
                defaultCtor.setStartingLine( "        // SECTION-START[" + DEFAULT_CONSTRUCTOR_SECTION_NAME + "]" );
                defaultCtor.setEndingLine( "        // SECTION-END" );
                defaultCtor.getHeadContent().append( "        super();" ).append( this.getLineSeparator() );
                s.getSections().add( defaultCtor );
                this.defaultConstructorSectionPresent = true;
            }
        }

        /**
         * Edits the default constructor section of the source code of the editor.
         *
         * @param s The section to edit.
         *
         * @throws NullPointerException if {@code s} is {@code null}.
         * @throws IOException if editing {@code s} fails.
         */
        public void editDefaultConstructorSection( final Section s ) throws IOException
        {
            if ( s == null )
            {
                throw new NullPointerException( "s" );
            }

            if ( s.getHeadContent().toString().trim().length() == 0 )
            {
                s.getHeadContent().setLength( 0 );

                if ( this.implementation != null )
                {
                    s.getHeadContent().append( "        super();" ).append( this.getLineSeparator() );
                }
            }
        }

        /**
         * Edits the dependencies section of the source code of the editor.
         *
         * @param s The section to edit.
         *
         * @throws NullPointerException if {@code s} is {@code null}.
         * @throws IOException if editing {@code s} fails.
         */
        public void editDependenciesSection( final Section s ) throws IOException
        {
            if ( s == null )
            {
                throw new NullPointerException( "s" );
            }

            s.getHeadContent().setLength( 0 );
            if ( this.implementation != null )
            {
                s.getHeadContent().append( getDependenciesSection( this.implementation ) );
            }
        }

        /**
         * Edits the messages section of the source code of the editor.
         *
         * @param s The section to edit.
         *
         * @throws NullPointerException if {@code s} is {@code null}.
         * @throws IOException if editing {@code s} fails.
         */
        public void editMessagesSection( final Section s ) throws IOException
        {
            if ( s == null )
            {
                throw new NullPointerException( "s" );
            }

            s.getHeadContent().setLength( 0 );
            if ( this.implementation != null )
            {
                s.getHeadContent().append( getMessagesSection( this.implementation ) );
            }
        }

        /**
         * Edits the properties section of the source code of the editor.
         *
         * @param s The section to edit.
         *
         * @throws NullPointerException if {@code s} is {@code null}.
         * @throws IOException if editing {@code s} fails.
         */
        public void editPropertiesSection( final Section s ) throws IOException
        {
            if ( s == null )
            {
                throw new NullPointerException( "s" );
            }

            s.getHeadContent().setLength( 0 );
            if ( this.implementation != null )
            {
                s.getHeadContent().append( getPropertiesSection( this.implementation ) );
            }
        }

        /**
         * Gets a flag indicating that the source code of the editor contains a constructors section.
         *
         * @return {@code true} if the source code of the editor contains a constructors section; {@code false} if the
         * source code of the editor does not contain a constructors section.
         */
        public boolean isConstructorsSectionPresent()
        {
            return this.constructorsSectionPresent;
        }

        /**
         * Gets a flag indicating that the source code of the editor contains a default constructor section.
         *
         * @return {@code true} if the source code of the editor contains a default constructor section; {@code false}
         * if the source code of the editor does not contain a default constructor section.
         */
        public boolean isDefaultConstructorSectionPresent()
        {
            return this.defaultConstructorSectionPresent;
        }

        /**
         * Gets a flag indicating that the source code of the editor contains a messages section.
         *
         * @return {@code true} if the source code of the editor contains a messages section; {@code false}
         * if the source code of the editor does not contain a messages section.
         */
        public boolean isMessagesSectionPresent()
        {
            return this.messagesSectionPresent;
        }

        /**
         * Gets a flag indicating that the source code of the editor contains a dependencies section.
         *
         * @return {@code true} if the source code of the editor contains a dependencies section; {@code false}
         * if the source code of the editor does not contain a dependencies section.
         */
        public boolean isDependenciesSectionPresent()
        {
            return this.dependenciesSectionPresent;
        }

        /**
         * Gets a flag indicating that the source code of the editor contains a properties section.
         *
         * @return {@code true} if the source code of the editor contains a properties section; {@code false}
         * if the source code of the editor does not contain a properties section.
         */
        public boolean isPropertiesSectionPresent()
        {
            return this.propertiesSectionPresent;
        }

    }

}
