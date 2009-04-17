/*
 *  JOMC Tools
 *  Copyright (c) 2005 Christian Schulte <cs@schulte.it>
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 */
package org.jomc.tools;

import java.io.File;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.List;
import java.util.ResourceBundle;
import org.apache.commons.io.FileUtils;
import org.apache.velocity.VelocityContext;
import org.jomc.model.Implementation;
import org.jomc.model.Module;
import org.jomc.model.Specification;
import org.jomc.tools.util.LineEditor;
import org.jomc.tools.util.Section;
import org.jomc.tools.util.SectionEditor;
import org.jomc.tools.util.TrailingWhitespaceEditor;

/**
 * Manages java source code.
 *
 * @author <a href="mailto:cs@schulte.it">Christian Schulte</a>
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

    /** Name of the {@code annotations.vm} template. */
    private static final String ANNOTATIONS_TEMPLATE = "annotations.vm";

    /** Creates a new {@code JavaSources} instance. */
    public JavaSources()
    {
        super();
    }

    /**
     * Creates a new {@code JavaSources} instance taking a classloader.
     *
     * @param classLoader The classlaoder of the instance.
     */
    public JavaSources( final ClassLoader classLoader )
    {
        super( classLoader );
    }

    /**
     * Edits the java source code of the module of the instance.
     *
     * @param sourceDirectory The directory holding the source files to edit.
     *
     * @throws NullPointerException if {@code sourcesDirectory} is {@code null}.
     * @throws Exception if editing fails.
     */
    public void editModuleSources( final File sourceDirectory ) throws Exception
    {
        if ( sourceDirectory == null )
        {
            throw new NullPointerException( "sourceDirectory" );
        }

        if ( this.getModule() != null )
        {
            if ( this.getModule().getSpecifications() != null )
            {
                for ( Specification s : this.getModule().getSpecifications().getSpecification() )
                {
                    if ( this.getModelManager().getImplementation( s.getIdentifier() ) != null )
                    {
                        // Ignore.
                        continue;
                    }

                    final File f = new File( sourceDirectory, s.getIdentifier().replace( '.', '/' ) + ".java" );

                    String content;
                    if ( f.exists() )
                    {
                        content = FileUtils.readFileToString( f, this.getEncoding() );
                    }
                    else
                    {
                        if ( !f.getParentFile().exists() )
                        {
                            f.getParentFile().mkdirs();
                        }

                        content = this.getSpecificationTemplate( s );
                    }

                    final String edited = this.editSpecification( s, content );
                    if ( !edited.equals( content ) )
                    {
                        FileUtils.writeStringToFile( f, edited, this.getEncoding() );
                    }
                }
            }
            if ( this.getModule().getImplementations() != null )
            {
                for ( Implementation i : this.getModule().getImplementations().getImplementation() )
                {
                    if ( !i.getIdentifier().equals( i.getClazz() ) )
                    {
                        // Ignore.
                        continue;
                    }

                    final File f = new File( sourceDirectory, i.getClazz().replace( '.', '/' ) + ".java" );

                    String content;
                    if ( f.exists() )
                    {
                        content = FileUtils.readFileToString( f, this.getEncoding() );
                    }
                    else
                    {
                        if ( !f.getParentFile().exists() )
                        {
                            f.getParentFile().mkdirs();
                        }

                        content = this.getImplementationTemplate( i );
                    }

                    final String edited = this.editImplementation( i, content );

                    if ( !edited.equals( content ) )
                    {
                        FileUtils.writeStringToFile( f, edited, this.getEncoding() );
                    }
                }
            }
        }
    }

    /**
     * Edits the source code of a specification.
     *
     * @param specification The specification to edit.
     * @param source The source code of {@code specification}.
     *
     * @return The edited source code of {@code specification}.
     *
     * @throws NullPointerException if {@code specification} or {@code source} is {@code null}.
     *
     * @throws Exception if editing the specification fails.
     */
    public String editSpecification( final Specification specification, final String source ) throws Exception
    {
        if ( specification == null )
        {
            throw new NullPointerException( "specification" );
        }
        if ( source == null )
        {
            throw new NullPointerException( "source" );
        }

        final JavaEditor editor = this.getJavaEditor( specification );
        final String edited = editor.edit( source );

        if ( !editor.isAnnotationsSectionEdited() )
        {
            throw new Exception( this.getMessage( "missingSection", new Object[]
                {
                    ANNOTATIONS_SECTION_NAME,
                    specification.getIdentifier()
                } ) );

        }

        return edited;
    }

    /**
     * Edits the source code of an implementation.
     *
     * @param implementation The implementation to edit.
     * @param source The source code of {@code implementation}.
     *
     * @return The edited source code of {@code implementation}.
     *
     * @throws NullPointerException if {@code implementation} or {@code source} is {@code null}.
     *
     * @throws Exception if editing the implementation fails.
     */
    public String editImplementation( final Implementation implementation, final String source ) throws Exception
    {
        if ( implementation == null )
        {
            throw new NullPointerException( "implementation" );
        }
        if ( source == null )
        {
            throw new NullPointerException( "source" );
        }

        final JavaEditor editor = this.getJavaEditor( implementation );
        final String edited = editor.edit( source );

        if ( editor.isConstructorsSectionEdited() && !editor.isDefaultConstructorSectionEdited() )
        {
            throw new Exception( this.getMessage( "missingSection", new Object[]
                {
                    CONSTRUCTORS_SECTION_NAME,
                    implementation.getIdentifier()
                } ) );

        }
        if ( implementation.getSpecifications() != null &&
             !implementation.getSpecifications().getReference().isEmpty() && !editor.isConstructorsSectionEdited() )
        {
            throw new Exception( this.getMessage( "missingSection", new Object[]
                {
                    CONSTRUCTORS_SECTION_NAME,
                    implementation.getIdentifier()
                } ) );

        }
        if ( implementation.getProperties() != null &&
             !implementation.getProperties().getProperty().isEmpty() && !editor.isPropertiesSectionEdited() )
        {
            throw new Exception( this.getMessage( "missingSection", new Object[]
                {
                    PROPERTIES_SECTION_NAME,
                    implementation.getIdentifier()
                } ) );

        }
        if ( implementation.getDependencies() != null &&
             !implementation.getDependencies().getDependency().isEmpty() && !editor.isDependenciesSectionEdited() )
        {
            throw new Exception( this.getMessage( "missingSection", new Object[]
                {
                    DEPENDENCIES_SECTION_NAME,
                    implementation.getIdentifier()
                } ) );

        }
        if ( implementation.getMessages() != null &&
             !( implementation.getMessages().getReference().isEmpty() &&
                implementation.getMessages().getMessage().isEmpty() ) && !editor.isMessagesSectionEdited() )
        {
            throw new Exception( this.getMessage( "missingSection", new Object[]
                {
                    MESSAGES_SECTION_NAME,
                    implementation.getIdentifier()
                } ) );

        }
        if ( !editor.isAnnotationsSectionEdited() )
        {
            throw new Exception( this.getMessage( "missingSection", new Object[]
                {
                    ANNOTATIONS_SECTION_NAME,
                    implementation.getIdentifier()
                } ) );

        }

        return edited;
    }

    /**
     * Gets a new editor for editing an implementation source code file.
     *
     * @param implementation The implementation to create a new editor for.
     *
     * @return A new editor for editing an implementation source code file.
     */
    public JavaEditor getJavaEditor( final Implementation implementation )
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
    public JavaEditor getJavaEditor( final Specification specification )
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
     * @throws Exception if getting the source code section fails.
     */
    private String getConstructorsSectionHeadContent( final Implementation implementation ) throws Exception
    {
        final StringWriter writer = new StringWriter();
        final VelocityContext ctx = this.getVelocityContext();
        final String template = this.getTemplateLocation( CONSTRUCTORS_HEAD_TEMPLATE );
        ctx.put( "implementation", implementation );
        ctx.put( "templateLocation", template );
        this.getVelocity().mergeTemplate( template, this.getEncoding(), ctx, writer );
        return writer.toString();
    }

    /**
     * Gets the source code of the constructors section tail content of an implemenation.
     *
     * @param implementation The implementation to get the constructors section tail content of.
     *
     * @throws Exception if getting the source code section fails.
     */
    private String getConstructorsSectionTailContent( final Implementation implementation ) throws Exception
    {
        final StringWriter writer = new StringWriter();
        final VelocityContext ctx = this.getVelocityContext();
        final String template = this.getTemplateLocation( CONSTRUCTORS_TAIL_TEMPLATE );
        ctx.put( "implementation", implementation );
        ctx.put( "templateLocation", template );
        this.getVelocity().mergeTemplate( template, this.getEncoding(), ctx, writer );
        return writer.toString();
    }

    /**
     * Gets the source code of the dependencies section.
     *
     * @param implementation The implementation to get the source code of the dependencies section of.
     *
     * @throws Exception if getting the source code section fails.
     */
    private String getDependenciesSection( final Implementation implementation ) throws Exception
    {
        final StringWriter writer = new StringWriter();
        final VelocityContext ctx = this.getVelocityContext();
        final String template = this.getTemplateLocation( DEPENDENCIES_TEMPLATE );
        ctx.put( "implementation", implementation );
        ctx.put( "templateLocation", template );
        this.getVelocity().mergeTemplate( template, this.getEncoding(), ctx, writer );
        return writer.toString();
    }

    /**
     * Gets the source code of the properties section.
     *
     * @param implementation The implementation to get the source code of the properties section of.
     *
     * @throws Exception if getting the source code section fails.
     */
    private String getPropertiesSection( final Implementation implementation ) throws Exception
    {
        final StringWriter writer = new StringWriter();
        final VelocityContext ctx = this.getVelocityContext();
        final String template = this.getTemplateLocation( PROPERTIES_TEMPLATE );
        ctx.put( "implementation", implementation );
        ctx.put( "templateLocation", template );
        this.getVelocity().mergeTemplate( template, this.getEncoding(), ctx, writer );
        return writer.toString();
    }

    /**
     * Gets the source code of the messages section.
     *
     * @param implementation The implementation to get the source code of the messages section of.
     *
     * @throws Exception if getting the source code section fails.
     */
    private String getMessagesSection( final Implementation implementation ) throws Exception
    {
        final StringWriter writer = new StringWriter();
        final VelocityContext ctx = this.getVelocityContext();
        final String template = this.getTemplateLocation( MESSAGES_TEMPLATE );
        ctx.put( "implementation", implementation );
        ctx.put( "templateLocation", template );
        this.getVelocity().mergeTemplate( template, this.getEncoding(), ctx, writer );
        return writer.toString();
    }

    /**
     * Gets the source code of the license section.
     *
     * @param implementation The implementation to get the source code of the license section of.
     *
     * @throws Exception if getting the source code section fails.
     */
    private String getLicenseSection( final Implementation implementation ) throws Exception
    {
        final StringWriter writer = new StringWriter();
        final VelocityContext ctx = this.getVelocityContext();
        final String template = this.getTemplateLocation( LICENSE_TEMPLATE );
        ctx.put( "implementation", implementation );
        ctx.put( "templateLocation", template );
        this.getVelocity().mergeTemplate( template, this.getEncoding(), ctx, writer );
        return writer.toString();
    }

    /**
     * Gets the source code of the license section.
     *
     * @param specification The specification to get the source code of the license section of.
     *
     * @throws Exception if getting the source code section fails.
     */
    private String getLicenseSection( final Specification specification ) throws Exception
    {
        final StringWriter writer = new StringWriter();
        final VelocityContext ctx = this.getVelocityContext();
        final String template = this.getTemplateLocation( LICENSE_TEMPLATE );
        ctx.put( "specification", specification );
        ctx.put( "templateLocation", template );
        this.getVelocity().mergeTemplate( template, this.getEncoding(), ctx, writer );
        return writer.toString();
    }

    /**
     * Gets the source code of the specification comment section.
     *
     * @param specification The specification to get the source code section of.
     *
     * @throws Exception if getting the source code section fails.
     */
    private String getCommentSection( final Specification specification ) throws Exception
    {
        final StringWriter writer = new StringWriter();
        final VelocityContext ctx = this.getVelocityContext();
        final String template = this.getTemplateLocation( SPECIFICATION_COMMENT_TEMPLATE );
        ctx.put( "specification", specification );
        ctx.put( "templateLocation", template );
        this.getVelocity().mergeTemplate( template, this.getEncoding(), ctx, writer );
        return writer.toString();
    }

    /**
     * Gets the source code of the implementation comment section.
     *
     * @param implementation The implementation to get the source code section of.
     *
     * @throws Exception if getting the source code section fails.
     */
    private String getCommentSection( final Implementation implementation ) throws Exception
    {
        final StringWriter writer = new StringWriter();
        final VelocityContext ctx = this.getVelocityContext();
        final String template = this.getTemplateLocation( IMPLEMENTATION_COMMENT_TEMPLATE );
        ctx.put( "implementation", implementation );
        ctx.put( "templateLocation", template );
        this.getVelocity().mergeTemplate( template, this.getEncoding(), ctx, writer );
        return writer.toString();
    }

    /**
     * Gets the source code template of an implementation.
     *
     * @param implementation The implementation to get the source code template of.
     *
     * @throws Exception if getting the source code section fails.
     */
    private String getImplementationTemplate( final Implementation implementation ) throws Exception
    {
        final StringWriter writer = new StringWriter();
        final VelocityContext ctx = this.getVelocityContext();
        final String template = this.getTemplateLocation( IMPLEMENTATION_TEMPLATE );
        ctx.put( "implementation", implementation );
        ctx.put( "templateLocation", template );
        this.getVelocity().mergeTemplate( template, this.getEncoding(), ctx, writer );
        return writer.toString();
    }

    /**
     * Gets the source code template of a specification.
     *
     * @param specification The specification to get the source code template of.
     *
     * @throws Exception if getting the source code section fails.
     */
    private String getSpecificationTemplate( final Specification specification ) throws Exception
    {
        final StringWriter writer = new StringWriter();
        final VelocityContext ctx = this.getVelocityContext();
        final String template = this.getTemplateLocation( SPECIFICATION_TEMPLATE );
        ctx.put( "specification", specification );
        ctx.put( "templateLocation", template );
        this.getVelocity().mergeTemplate( template, this.getEncoding(), ctx, writer );
        return writer.toString();
    }

    /**
     * Gets the source code of the specification annotations section.
     *
     * @param specification The specification to get the source code of the annotations section of.
     *
     * @throws Exception if getting the source code section fails.
     */
    private String getAnnotationsTemplate( final Specification specification ) throws Exception
    {
        final StringWriter writer = new StringWriter();
        final VelocityContext ctx = this.getVelocityContext();
        final String template = this.getTemplateLocation( ANNOTATIONS_TEMPLATE );
        ctx.put( "value", this.getSerializedSpecification( specification ) );
        ctx.put( "templateLocation", template );
        this.getVelocity().mergeTemplate( template, this.getEncoding(), ctx, writer );
        return writer.toString();
    }

    /**
     * Gets the source code of the implementation annotations section.
     *
     * @param implementation The implementation to get the source code of the annotations section of.
     *
     * @throws Exception if getting the source code section fails.
     */
    private String getAnnotationsTemplate( final Implementation implementation ) throws Exception
    {
        final StringWriter writer = new StringWriter();
        final VelocityContext ctx = this.getVelocityContext();
        final String template = this.getTemplateLocation( ANNOTATIONS_TEMPLATE );
        ctx.put( "value", this.getSerializedImplementation( implementation ) );
        ctx.put( "templateLocation", template );
        this.getVelocity().mergeTemplate( template, this.getEncoding(), ctx, writer );
        return writer.toString();
    }

    /**
     * Serializes a given specification.
     *
     * @param specification The specification to serialize.
     *
     * @return {@code specification} serialized to a byte array.
     *
     * @throws Exception if serialization fails.
     */
    public String getSerializedSpecification( final Specification specification ) throws Exception
    {
        if ( specification == null )
        {
            throw new NullPointerException( "specification" );
        }

        final StringWriter writer = new StringWriter();
        this.getModelResolver().getMarshaller( true, false ).
            marshal( this.getModelResolver().getObjectFactory().createSpecification( specification ), writer );

        return writer.toString();
    }

    /**
     * Serializes a given implementation.
     *
     * @param implementation The implementation to serialize.
     *
     * @return {@code implementation} serialized to a byte array.
     *
     * @throws NullPointerException if {@code implementation} is {@code null}.
     * @throws Exception if serialization fails.
     */
    public String getSerializedImplementation( final Implementation implementation ) throws Exception
    {
        if ( implementation == null )
        {
            throw new NullPointerException( "implementation" );
        }

        final StringWriter writer = new StringWriter();
        final Module m = this.getModelManager().getImplementationModule( implementation.getIdentifier() );
        this.getModelResolver().getMarshaller( true, false ).marshal( this.getModelResolver().getObjectFactory().
            createModule( m ), writer );

        return writer.toString();
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

        public void editLicenseSection( final Section s ) throws Exception
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

        public void editConstructorsSection( final Section s ) throws Exception
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

        public void editDependenciesSection( final Section s ) throws Exception
        {
            s.getHeadContent().setLength( 0 );
            s.getHeadContent().append( getDependenciesSection( implementation ) );
            this.dependenciesSectionEdited = true;
        }

        public void editMessagesSection( final Section s ) throws Exception
        {
            s.getHeadContent().setLength( 0 );
            s.getHeadContent().append( getMessagesSection( implementation ) );
            this.messagesSectionEdited = true;
        }

        public void editPropertiesSection( final Section s ) throws Exception
        {
            s.getHeadContent().setLength( 0 );
            s.getHeadContent().append( getPropertiesSection( implementation ) );
            this.propertiesSectionEdited = true;
        }

        public void editImplementationCommentSection( final Section s ) throws Exception
        {
            s.getHeadContent().setLength( 0 );
            s.getHeadContent().append( getCommentSection( implementation ) );
            this.implementationCommentSectionEdited = true;
        }

        public void editImplementationAnnotationsSection( final Section s ) throws Exception
        {
            s.getHeadContent().setLength( 0 );
            s.getHeadContent().append( getAnnotationsTemplate( implementation ) );
            this.annotationsSectionEdited = true;
        }

        public void editSpecificationCommentSection( final Section s ) throws Exception
        {
            s.getHeadContent().setLength( 0 );
            s.getHeadContent().append( getCommentSection( specification ) );
            this.specificationCommentSectionEdited = true;
        }

        public void editSpecificationAnnotationsSection( final Section s ) throws Exception
        {
            s.getHeadContent().setLength( 0 );
            s.getHeadContent().append( getAnnotationsTemplate( specification ) );
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
