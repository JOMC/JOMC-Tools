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
package org.jomc.tools.modlet;

import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import org.jomc.model.Dependencies;
import org.jomc.model.Implementation;
import org.jomc.model.Messages;
import org.jomc.model.Module;
import org.jomc.model.Modules;
import org.jomc.model.Properties;
import org.jomc.model.Specification;
import org.jomc.model.Specifications;
import org.jomc.model.modlet.ModelHelper;
import org.jomc.modlet.Model;
import org.jomc.modlet.ModelContext;
import org.jomc.modlet.ModelException;
import org.jomc.modlet.ModelProvider;
import org.jomc.tools.JomcTool;
import org.jomc.tools.model.ObjectFactory;
import org.jomc.tools.model.SourceFileType;
import org.jomc.tools.model.SourceFilesType;
import org.jomc.tools.model.SourceSectionType;
import org.jomc.tools.model.SourceSectionsType;
import static org.jomc.tools.modlet.ToolsModletConstants.*;

/**
 * Object management and configuration tools {@code ModelProvider} implementation.
 *
 * @author <a href="mailto:schulte2005@users.sourceforge.net">Christian Schulte</a>
 * @version $Id$
 * @see ModelContext#findModel(java.lang.String)
 * @since 1.2
 */
public class ToolsModelProvider implements ModelProvider
{

    /**
     * Constant for the name of the attribute backing property {@code enabled}.
     * @see #findModel(org.jomc.modlet.ModelContext, org.jomc.modlet.Model)
     * @see ModelContext#getAttribute(java.lang.String)
     * @see ModelContext#getAttribute(java.lang.String, java.lang.Object)
     * @see ModelContext#setAttribute(java.lang.String, java.lang.Object)
     * @see ModelContext#clearAttribute(java.lang.String)
     */
    public static final String ENABLED_ATTRIBUTE_NAME = "org.jomc.tools.modlet.ToolsModelProvider.enabledAttribute";

    /**
     * Constant for the name of the model context attribute backing property
     * {@code modelObjectClasspathResolutionEnabled}.
     *
     * @see #findModel(org.jomc.modlet.ModelContext, org.jomc.modlet.Model)
     * @see ModelContext#getAttribute(java.lang.String)
     * @see ModelContext#getAttribute(java.lang.String, java.lang.Object)
     * @see ModelContext#setAttribute(java.lang.String, java.lang.Object)
     * @see ModelContext#clearAttribute(java.lang.String)
     */
    public static final String MODEL_OBJECT_CLASSPATH_RESOLUTION_ENABLED_ATTRIBUTE_NAME =
        "org.jomc.tools.modlet.ToolsModelProvider.modelObjectClasspathResolutionEnabledAttribute";

    /**
     * Constant for the name of the system property controlling property {@code defaultEnabled}.
     * @see #isDefaultEnabled()
     */
    private static final String DEFAULT_ENABLED_PROPERTY_NAME =
        "org.jomc.tools.modlet.ToolsModelProvider.defaultEnabled";

    /**
     * Constant for the name of the system property controlling property
     * {@code defaultModelObjectClasspathResolutionEnabled}.
     * @see #isDefaultModelObjectClasspathResolutionEnabled()
     */
    private static final String DEFAULT_MODEL_OBJECT_CLASSPATH_RESOLUTION_ENABLED_PROPERTY_NAME =
        "org.jomc.tools.modlet.ToolsModelProvider.defaultModelObjectClasspathResolutionEnabled";

    /**
     * Default value of the flag indicating the provider is enabled by default.
     * @see #isDefaultEnabled()
     */
    private static final Boolean DEFAULT_ENABLED = Boolean.TRUE;

    /**
     * Default value of the flag indicating model object class path resolution is enabled by default.
     * @see #isDefaultModelObjectClasspathResolutionEnabled()
     */
    private static final Boolean DEFAULT_MODEL_OBJECT_CLASSPATH_RESOLUTION_ENABLED = Boolean.TRUE;

    /** Flag indicating the provider is enabled by default. */
    private static volatile Boolean defaultEnabled;

    /** Flag indicating model object class path resolution is enabled by default. */
    private static volatile Boolean defaultModelObjectClasspathResolutionEnabled;

    /** Flag indicating the provider is enabled. */
    private Boolean enabled;

    /** Flag indicating model object class path resolution is enabled. */
    private Boolean modelObjectClasspathResolutionEnabled;

    /** Creates a new {@code ToolsModelProvider} instance. */
    public ToolsModelProvider()
    {
        super();
    }

    /**
     * Gets a flag indicating the provider is enabled by default.
     * <p>The default enabled flag is controlled by system property
     * {@code org.jomc.tools.modlet.ToolsModelProvider.defaultEnabled} holding a value indicating the provider is
     * enabled by default. If that property is not set, the {@code true} default is returned.</p>
     *
     * @return {@code true} if the provider is enabled by default; {@code false} if the provider is disabled by
     * default.
     *
     * @see #setDefaultEnabled(java.lang.Boolean)
     */
    public static boolean isDefaultEnabled()
    {
        if ( defaultEnabled == null )
        {
            defaultEnabled = Boolean.valueOf( System.getProperty( DEFAULT_ENABLED_PROPERTY_NAME,
                                                                  Boolean.toString( DEFAULT_ENABLED ) ) );

        }

        return defaultEnabled;
    }

    /**
     * Sets the flag indicating the provider is enabled by default.
     *
     * @param value The new value of the flag indicating the provider is enabled by default or {@code null}.
     *
     * @see #isDefaultEnabled()
     */
    public static void setDefaultEnabled( final Boolean value )
    {
        defaultEnabled = value;
    }

    /**
     * Gets a flag indicating the provider is enabled.
     *
     * @return {@code true} if the provider is enabled; {@code false} if the provider is disabled.
     *
     * @see #isDefaultEnabled()
     * @see #setEnabled(java.lang.Boolean)
     */
    public final boolean isEnabled()
    {
        if ( this.enabled == null )
        {
            this.enabled = isDefaultEnabled();
        }

        return this.enabled;
    }

    /**
     * Sets the flag indicating the provider is enabled.
     *
     * @param value The new value of the flag indicating the provider is enabled or {@code null}.
     *
     * @see #isEnabled()
     */
    public final void setEnabled( final Boolean value )
    {
        this.enabled = value;
    }

    /**
     * Gets a flag indicating model object class path resolution is enabled by default.
     * <p>The model object class path resolution default enabled flag is controlled by system property
     * {@code org.jomc.tools.modlet.ToolsModelProvider.defaultModelObjectClasspathResolutionEnabled} holding a value
     * indicating model object class path resolution is enabled by default. If that property is not set, the
     * {@code true} default is returned.</p>
     *
     * @return {@code true} if model object class path resolution is enabled by default; {@code false} if model object
     * class path resolution is disabled by default.
     *
     * @see #setDefaultModelObjectClasspathResolutionEnabled(java.lang.Boolean)
     */
    public static boolean isDefaultModelObjectClasspathResolutionEnabled()
    {
        if ( defaultModelObjectClasspathResolutionEnabled == null )
        {
            defaultModelObjectClasspathResolutionEnabled = Boolean.valueOf( System.getProperty(
                DEFAULT_MODEL_OBJECT_CLASSPATH_RESOLUTION_ENABLED_PROPERTY_NAME,
                Boolean.toString( DEFAULT_MODEL_OBJECT_CLASSPATH_RESOLUTION_ENABLED ) ) );

        }

        return defaultModelObjectClasspathResolutionEnabled;
    }

    /**
     * Sets the flag indicating model object class path resolution is enabled by default.
     *
     * @param value The new value of the flag indicating model object class path resolution is enabled by default or
     * {@code null}.
     *
     * @see #isDefaultModelObjectClasspathResolutionEnabled()
     */
    public static void setDefaultModelObjectClasspathResolutionEnabled( final Boolean value )
    {
        defaultModelObjectClasspathResolutionEnabled = value;
    }

    /**
     * Gets a flag indicating model object class path resolution is enabled.
     *
     * @return {@code true} if model object class path resolution is enabled; {@code false} if model object class path
     * resolution is disabled.
     *
     * @see #isDefaultModelObjectClasspathResolutionEnabled()
     * @see #setModelObjectClasspathResolutionEnabled(java.lang.Boolean)
     */
    public final boolean isModelObjectClasspathResolutionEnabled()
    {
        if ( this.modelObjectClasspathResolutionEnabled == null )
        {
            this.modelObjectClasspathResolutionEnabled = isDefaultModelObjectClasspathResolutionEnabled();
        }

        return this.modelObjectClasspathResolutionEnabled;
    }

    /**
     * Sets the flag indicating model object class path resolution is is enabled.
     *
     * @param value The new value of the flag indicating model object class path resolution is enabled or {@code null}.
     *
     * @see #isModelObjectClasspathResolutionEnabled()
     */
    public final void setModelObjectClasspathResolutionEnabled( final Boolean value )
    {
        this.modelObjectClasspathResolutionEnabled = value;
    }

    /**
     * {@inheritDoc}
     *
     * @see #isEnabled()
     * @see #isModelObjectClasspathResolutionEnabled()
     * @see #ENABLED_ATTRIBUTE_NAME
     * @see #MODEL_OBJECT_CLASSPATH_RESOLUTION_ENABLED_ATTRIBUTE_NAME
     */
    public Model findModel( final ModelContext context, final Model model ) throws ModelException
    {
        if ( context == null )
        {
            throw new NullPointerException( "context" );
        }
        if ( model == null )
        {
            throw new NullPointerException( "model" );
        }

        Model provided = null;

        boolean contextEnabled = this.isEnabled();
        if ( DEFAULT_ENABLED == contextEnabled && context.getAttribute( ENABLED_ATTRIBUTE_NAME ) != null )
        {
            contextEnabled = (Boolean) context.getAttribute( ENABLED_ATTRIBUTE_NAME );
        }

        boolean contextModelObjectClasspathResolutionEnabled = this.isModelObjectClasspathResolutionEnabled();
        if ( contextModelObjectClasspathResolutionEnabled == DEFAULT_MODEL_OBJECT_CLASSPATH_RESOLUTION_ENABLED
             && context.getAttribute( MODEL_OBJECT_CLASSPATH_RESOLUTION_ENABLED_ATTRIBUTE_NAME ) != null )
        {
            contextModelObjectClasspathResolutionEnabled =
                (Boolean) context.getAttribute( MODEL_OBJECT_CLASSPATH_RESOLUTION_ENABLED_ATTRIBUTE_NAME );

        }

        if ( contextEnabled )
        {
            provided = new Model( model );
            final Modules modules = ModelHelper.getModules( provided );

            if ( modules != null )
            {
                Module classpathModule = null;
                if ( contextModelObjectClasspathResolutionEnabled )
                {
                    classpathModule = modules.getClasspathModule( Modules.getDefaultClasspathModuleName(),
                                                                  context.getClassLoader() );

                    if ( classpathModule != null
                         && modules.getModule( Modules.getDefaultClasspathModuleName() ) == null )
                    {
                        modules.getModule().add( classpathModule );
                    }
                    else
                    {
                        classpathModule = null;
                    }
                }

                final JomcTool tool = new JomcTool();
                tool.setModel( provided );

                if ( modules.getSpecifications() != null )
                {
                    for ( int i = 0, s0 = modules.getSpecifications().getSpecification().size(); i < s0; i++ )
                    {
                        final Specification specification = modules.getSpecifications().getSpecification().get( i );
                        final SourceFileType sourceFileType = specification.getAnyObject( SourceFileType.class );
                        final SourceFilesType sourceFilesType = specification.getAnyObject( SourceFilesType.class );

                        if ( sourceFileType == null && sourceFilesType == null && specification.isClassDeclaration() )
                        {
                            specification.getAny().add( new ObjectFactory().createSourceFiles(
                                this.getDefaultSourceFilesType( tool, specification ) ) );

                        }
                    }
                }

                if ( modules.getImplementations() != null )
                {
                    for ( int i = 0, s0 = modules.getImplementations().getImplementation().size(); i < s0; i++ )
                    {
                        final Implementation implementation = modules.getImplementations().getImplementation().get( i );
                        final SourceFileType sourceFileType = implementation.getAnyObject( SourceFileType.class );
                        final SourceFilesType sourceFilesType = implementation.getAnyObject( SourceFilesType.class );

                        if ( sourceFileType == null && sourceFilesType == null && implementation.isClassDeclaration() )
                        {
                            implementation.getAny().add( new ObjectFactory().createSourceFiles(
                                this.getDefaultSourceFilesType( tool, implementation ) ) );

                        }
                    }
                }

                if ( classpathModule != null )
                {
                    modules.getModule().remove( classpathModule );
                }
            }
        }
        else if ( context.isLoggable( Level.FINER ) )
        {
            context.log( Level.FINER, getMessage( "disabled", this.getClass().getSimpleName(),
                                                  model.getIdentifier() ), null );

        }

        return provided;
    }

    /**
     * Creates a new default source files model for a given specification.
     *
     * @param tool The tool to use for generating type names.
     * @param specification The specification to create a new default source files model for.
     *
     * @return A new default source files model for {@code specification}.
     *
     * @throws NullPointerExeption if {@code tool} or {@code specification} is {@code null}.
     */
    private SourceFilesType getDefaultSourceFilesType( final JomcTool tool, final Specification specification )
    {
        if ( tool == null )
        {
            throw new NullPointerException( "tool" );
        }
        if ( specification == null )
        {
            throw new NullPointerException( "specification" );
        }

        final SourceFilesType sourceFilesType = new SourceFilesType();
        final SourceFileType sourceFileType = new SourceFileType();
        sourceFilesType.getSourceFile().add( sourceFileType );

        sourceFileType.setIdentifier( specification.getIdentifier() );

        if ( specification.getClazz() != null )
        {
            sourceFileType.setLocation( new StringBuilder( specification.getClazz().length() + 5 ).append(
                specification.getClazz().replace( '.', '/' ) ).append( ".java" ).toString() );

        }

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

        final String javaTypeName = tool.getJavaTypeName( specification, false );
        if ( javaTypeName != null )
        {
            s = new SourceSectionType();
            s.setName( javaTypeName );
            s.setIndentationLevel( 1 );
            s.setEditable( true );
            sourceFileType.getSourceSections().getSourceSection().add( s );
        }

        return sourceFilesType;
    }

    /**
     * Creates a new default source files model for a given implementation.
     *
     * @param tool The tool to use for generating type names.
     * @param implementation The implementation to create a new default source files model for.
     *
     * @return A new default source files model for {@code implementation}.
     *
     * @throws NullPointerExeption if {@code tool} or {@code implementation} is {@code null}.
     */
    private SourceFilesType getDefaultSourceFilesType( final JomcTool tool, final Implementation implementation )
    {
        if ( tool == null )
        {
            throw new NullPointerException( "tool" );
        }
        if ( implementation == null )
        {
            throw new NullPointerException( "implementation" );
        }

        final SourceFilesType sourceFilesType = new SourceFilesType();
        final SourceFileType sourceFileType = new SourceFileType();
        sourceFilesType.getSourceFile().add( sourceFileType );

        final Specifications specifications = tool.getModules().getSpecifications( implementation.getIdentifier() );
        final Dependencies dependencies = tool.getModules().getDependencies( implementation.getIdentifier() );
        final Messages messages = tool.getModules().getMessages( implementation.getIdentifier() );
        final Properties properties = tool.getModules().getProperties( implementation.getIdentifier() );

        sourceFileType.setIdentifier( implementation.getIdentifier() );

        if ( implementation.getClazz() != null )
        {
            sourceFileType.setLocation( new StringBuilder( implementation.getClazz().length() + 5 ).append(
                implementation.getClazz().replace( '.', '/' ) ).append( ".java" ).toString() );

        }

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

        final List<String> implementedJavaTypeNames = tool.getImplementedJavaTypeNames( implementation, false );
        for ( int i = 0, s0 = implementedJavaTypeNames.size(); i < s0; i++ )
        {
            s = new SourceSectionType();
            s.setName( implementedJavaTypeNames.get( i ) );
            s.setIndentationLevel( 1 );
            s.setEditable( true );
            sourceFileType.getSourceSections().getSourceSection().add( s );
        }

        final String javaTypeName = tool.getJavaTypeName( implementation, false );
        if ( javaTypeName != null && !implementedJavaTypeNames.contains( javaTypeName ) )
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

        return sourceFilesType;
    }

    private static String getMessage( final String key, final Object... args )
    {
        return MessageFormat.format( ResourceBundle.getBundle(
            ToolsModelProvider.class.getName().replace( '.', '/' ), Locale.getDefault() ).getString( key ), args );

    }

}
