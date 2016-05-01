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

import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import org.jomc.jls.JavaTypeName;
import org.jomc.model.Dependencies;
import org.jomc.model.Implementation;
import org.jomc.model.Messages;
import org.jomc.model.ModelObjectException;
import org.jomc.model.Module;
import org.jomc.model.Modules;
import org.jomc.model.Properties;
import org.jomc.model.Specification;
import org.jomc.model.Specifications;
import org.jomc.model.modlet.ModelHelper;
import org.jomc.modlet.Model;
import org.jomc.modlet.ModelContext;
import org.jomc.modlet.ModelException;
import org.jomc.modlet.ModelProcessor;
import org.jomc.tools.model.SourceFileType;
import org.jomc.tools.model.SourceFilesType;
import org.jomc.tools.model.SourceSectionType;
import org.jomc.tools.model.SourceSectionsType;
import static org.jomc.tools.modlet.ToolsModletConstants.ANNOTATIONS_SECTION_NAME;
import static org.jomc.tools.modlet.ToolsModletConstants.CONSTRUCTORS_HEAD_TEMPLATE;
import static org.jomc.tools.modlet.ToolsModletConstants.CONSTRUCTORS_SECTION_NAME;
import static org.jomc.tools.modlet.ToolsModletConstants.CONSTRUCTORS_TAIL_TEMPLATE;
import static org.jomc.tools.modlet.ToolsModletConstants.DEFAULT_CONSTRUCTOR_SECTION_NAME;
import static org.jomc.tools.modlet.ToolsModletConstants.DEFAULT_CONSTRUCTOR_TEMPLATE;
import static org.jomc.tools.modlet.ToolsModletConstants.DEPENDENCIES_SECTION_NAME;
import static org.jomc.tools.modlet.ToolsModletConstants.DEPENDENCIES_TEMPLATE;
import static org.jomc.tools.modlet.ToolsModletConstants.DOCUMENTATION_SECTION_NAME;
import static org.jomc.tools.modlet.ToolsModletConstants.IMPLEMENTATION_ANNOTATIONS_TEMPLATE;
import static org.jomc.tools.modlet.ToolsModletConstants.IMPLEMENTATION_DOCUMENTATION_TEMPLATE;
import static org.jomc.tools.modlet.ToolsModletConstants.IMPLEMENTATION_LICENSE_TEMPLATE;
import static org.jomc.tools.modlet.ToolsModletConstants.IMPLEMENTATION_TEMPLATE;
import static org.jomc.tools.modlet.ToolsModletConstants.LICENSE_SECTION_NAME;
import static org.jomc.tools.modlet.ToolsModletConstants.MESSAGES_SECTION_NAME;
import static org.jomc.tools.modlet.ToolsModletConstants.MESSAGES_TEMPLATE;
import static org.jomc.tools.modlet.ToolsModletConstants.PROPERTIES_SECTION_NAME;
import static org.jomc.tools.modlet.ToolsModletConstants.PROPERTIES_TEMPLATE;
import static org.jomc.tools.modlet.ToolsModletConstants.SPECIFICATION_ANNOTATIONS_TEMPLATE;
import static org.jomc.tools.modlet.ToolsModletConstants.SPECIFICATION_DOCUMENTATION_TEMPLATE;
import static org.jomc.tools.modlet.ToolsModletConstants.SPECIFICATION_LICENSE_TEMPLATE;
import static org.jomc.tools.modlet.ToolsModletConstants.SPECIFICATION_TEMPLATE;

/**
 * Object management and configuration tools {@code ModelProcessor} implementation.
 *
 * @author <a href="mailto:cs@schulte.it">Christian Schulte</a>
 * @version $JOMC$
 * @see ModelContext#processModel(org.jomc.modlet.Model)
 * @since 1.2
 */
public class ToolsModelProcessor implements ModelProcessor
{

    /**
     * Constant for the name of the model context attribute backing property {@code enabled}.
     *
     * @see #processModel(org.jomc.modlet.ModelContext, org.jomc.modlet.Model)
     * @see ModelContext#getAttribute(java.lang.String)
     */
    public static final String ENABLED_ATTRIBUTE_NAME = "org.jomc.tools.modlet.ToolsModelProcessor.enabledAttribute";

    /**
     * Constant for the name of the system property controlling property {@code defaultEnabled}.
     *
     * @see #isDefaultEnabled()
     */
    private static final String DEFAULT_ENABLED_PROPERTY_NAME =
        "org.jomc.tools.modlet.ToolsModelProcessor.defaultEnabled";

    /**
     * Default value of the flag indicating the processor is enabled by default.
     *
     * @see #isDefaultEnabled()
     */
    private static final Boolean DEFAULT_ENABLED = Boolean.TRUE;

    /**
     * Flag indicating the processor is enabled by default.
     */
    private static volatile Boolean defaultEnabled;

    /**
     * Flag indicating the processor is enabled.
     */
    private Boolean enabled;

    /**
     * Constant for the name of the model context attribute backing property
     * {@code modelObjectClasspathResolutionEnabled}.
     *
     * @see #processModel(org.jomc.modlet.ModelContext, org.jomc.modlet.Model)
     * @see ModelContext#getAttribute(java.lang.String)
     */
    public static final String MODEL_OBJECT_CLASSPATH_RESOLUTION_ENABLED_ATTRIBUTE_NAME =
        "org.jomc.tools.modlet.ToolsModelProcessor.modelObjectClasspathResolutionEnabledAttribute";

    /**
     * Constant for the name of the system property controlling property
     * {@code defaultModelObjectClasspathResolutionEnabled}.
     *
     * @see #isDefaultModelObjectClasspathResolutionEnabled()
     */
    private static final String DEFAULT_MODEL_OBJECT_CLASSPATH_RESOLUTION_ENABLED_PROPERTY_NAME =
        "org.jomc.tools.modlet.ToolsModelProcessor.defaultModelObjectClasspathResolutionEnabled";

    /**
     * Default value of the flag indicating model object class path resolution is enabled by default.
     *
     * @see #isDefaultModelObjectClasspathResolutionEnabled()
     */
    private static final Boolean DEFAULT_MODEL_OBJECT_CLASSPATH_RESOLUTION_ENABLED = Boolean.TRUE;

    /**
     * Flag indicating model object class path resolution is enabled by default.
     */
    private static volatile Boolean defaultModelObjectClasspathResolutionEnabled;

    /**
     * Flag indicating model object class path resolution is enabled.
     */
    private Boolean modelObjectClasspathResolutionEnabled;

    /**
     * Constant for the name of the model context attribute backing property {@code headComment}.
     *
     * @see #processModel(org.jomc.modlet.ModelContext, org.jomc.modlet.Model)
     * @see ModelContext#getAttribute(java.lang.String)
     * @since 1.6
     */
    public static final String HEAD_COMMENT_ATTRIBUTE_NAME =
        "org.jomc.tools.modlet.ToolsModelProcessor.headCommentAttribute";

    /**
     * Constant for the name of the system property controlling property {@code defaultHeadComment}.
     *
     * @see #getDefaultHeadComment()
     * @since 1.6
     */
    private static final String DEFAULT_HEAD_COMMENT_PROPERTY_NAME =
        "org.jomc.tools.modlet.ToolsModelProcessor.defaultHeadComment";

    /**
     * Default head comment the processor is applying by default.
     *
     * @see #getDefaultHeadComment()
     * @since 1.6
     */
    private static final String DEFAULT_HEAD_COMMENT = "//";

    /**
     * Head comment the processor is applying by default.
     *
     * @since 1.6
     */
    private static volatile String defaultHeadComment;

    /**
     * Head comment the processor is applying.
     *
     * @since 1.6
     */
    private String headComment;

    /**
     * Constant for the name of the model context attribute backing property {@code tailComment}.
     *
     * @see #processModel(org.jomc.modlet.ModelContext, org.jomc.modlet.Model)
     * @see ModelContext#getAttribute(java.lang.String)
     * @since 1.6
     */
    public static final String TAIL_COMMENT_ATTRIBUTE_NAME =
        "org.jomc.tools.modlet.ToolsModelProcessor.tailCommentAttribute";

    /**
     * Constant for the name of the system property controlling property {@code defaultTailComment}.
     *
     * @see #getDefaultTailComment()
     * @since 1.6
     */
    private static final String DEFAULT_TAIL_COMMENT_PROPERTY_NAME =
        "org.jomc.tools.modlet.ToolsModelProcessor.defaultTailComment";

    /**
     * Default tail comment the processor is applying by default.
     *
     * @see #getDefaultTailComment()
     * @since 1.6
     */
    private static final String DEFAULT_TAIL_COMMENT = null;

    /**
     * Tail comment the processor is applying by default.
     *
     * @since 1.6
     */
    private static volatile String defaultTailComment;

    /**
     * Tail comment the processor is applying.
     *
     * @since 1.6
     */
    private String tailComment;

    /**
     * Creates a new {@code ToolsModelProcessor} instance.
     */
    public ToolsModelProcessor()
    {
        super();
    }

    /**
     * Gets a flag indicating the processor is enabled by default.
     * <p>
     * The default enabled flag is controlled by system property
     * {@code org.jomc.tools.modlet.ToolsModelProcessor.defaultEnabled} holding a value indicating the processor is
     * enabled by default. If that property is not set, the {@code true} default is returned.
     * </p>
     *
     * @return {@code true}, if the processor is enabled by default; {@code false}, if the processor is disabled by
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
     * Sets the flag indicating the processor is enabled by default.
     *
     * @param value The new value of the flag indicating the processor is enabled by default or {@code null}.
     *
     * @see #isDefaultEnabled()
     */
    public static void setDefaultEnabled( final Boolean value )
    {
        defaultEnabled = value;
    }

    /**
     * Gets a flag indicating the processor is enabled.
     *
     * @return {@code true}, if the processor is enabled; {@code false}, if the processor is disabled.
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
     * Sets the flag indicating the processor is enabled.
     *
     * @param value The new value of the flag indicating the processor is enabled or {@code null}.
     *
     * @see #isEnabled()
     */
    public final void setEnabled( final Boolean value )
    {
        this.enabled = value;
    }

    /**
     * Gets a flag indicating model object class path resolution is enabled by default.
     * <p>
     * The model object class path resolution default enabled flag is controlled by system property
     * {@code org.jomc.tools.modlet.ToolsModelProcessor.defaultModelObjectClasspathResolutionEnabled} holding a value
     * indicating model object class path resolution is enabled by default. If that property is not set, the
     * {@code true} default is returned.
     * </p>
     *
     * @return {@code true}, if model object class path resolution is enabled by default; {@code false}, if model object
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
     * @return {@code true}, if model object class path resolution is enabled; {@code false}, if model object class path
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
     * Gets the head comment the processor is applying by default.
     * <p>
     * The default head comment is controlled by system property
     * {@code org.jomc.tools.modlet.ToolsModelProcessor.defaultHeadComment} holding the head comment the processor is
     * applying by default. If that property is not set, the {@code //} default is returned.
     * </p>
     *
     * @return The head comment the processor is applying by default or {@code null}.
     *
     * @see #setDefaultHeadComment(java.lang.String)
     * @since 1.6
     */
    public static String getDefaultHeadComment()
    {
        if ( defaultHeadComment == null )
        {
            defaultHeadComment = System.getProperty( DEFAULT_HEAD_COMMENT_PROPERTY_NAME, DEFAULT_HEAD_COMMENT );
        }

        return defaultHeadComment;
    }

    /**
     * Sets the head comment the processor is applying by default.
     *
     * @param value The new head comment the processor is applying by default or {@code null}.
     *
     * @see #getDefaultHeadComment()
     * @since 1.6
     */
    public static void setDefaultHeadComment( final String value )
    {
        defaultHeadComment = value;
    }

    /**
     * Gets the head comment the processor is applying.
     *
     * @return The head comment the processor is applying or {@code null}.
     *
     * @see #getDefaultHeadComment()
     * @see #setDefaultHeadComment(java.lang.String)
     * @since 1.6
     */
    public final String getHeadComment()
    {
        if ( this.headComment == null )
        {
            this.headComment = getDefaultHeadComment();
        }

        return this.headComment;
    }

    /**
     * Sets the head comment the processor is applying.
     *
     * @param value The new head comment the processor is applying or {@code null}.
     *
     * @see #getHeadComment()
     * @since 1.6
     */
    public final void setHeadComment( final String value )
    {
        this.headComment = value;
    }

    /**
     * Gets the tail comment the processor is applying by default.
     * <p>
     * The default tail comment is controlled by system property
     * {@code org.jomc.tools.modlet.ToolsModelProcessor.defaultTailComment} holding the tail comment the processor is
     * applying by default. If that property is not set, the {@code null} default is returned.
     * </p>
     *
     * @return The tail comment the processor is applying by default or {@code null}.
     *
     * @see #setDefaultTailComment(java.lang.String)
     * @since 1.6
     */
    public static String getDefaultTailComment()
    {
        if ( defaultTailComment == null )
        {
            defaultTailComment = System.getProperty( DEFAULT_TAIL_COMMENT_PROPERTY_NAME, DEFAULT_TAIL_COMMENT );
        }

        return defaultTailComment;
    }

    /**
     * Sets the tail comment the processor is applying by default.
     *
     * @param value The new tail comment the processor is applying by default or {@code null}.
     *
     * @see #getDefaultTailComment()
     * @since 1.6
     */
    public static void setDefaultTailComment( final String value )
    {
        defaultTailComment = value;
    }

    /**
     * Gets the tail comment the processor is applying.
     *
     * @return The tail comment the processor is applying or {@code null}.
     *
     * @see #getDefaultTailComment()
     * @see #setDefaultTailComment(java.lang.String)
     * @since 1.6
     */
    public final String getTailComment()
    {
        if ( this.tailComment == null )
        {
            this.tailComment = getDefaultTailComment();
        }

        return this.tailComment;
    }

    /**
     * Sets the tail comment the processor is applying.
     *
     * @param value The new tail comment the processor is applying or {@code null}.
     *
     * @see #getTailComment()
     * @since 1.6
     */
    public final void setTailComment( final String value )
    {
        this.tailComment = value;
    }

    /**
     * {@inheritDoc}
     *
     * @see #isEnabled()
     * @see #isModelObjectClasspathResolutionEnabled()
     * @see #getHeadComment()
     * @see #getTailComment()
     * @see #ENABLED_ATTRIBUTE_NAME
     * @see #MODEL_OBJECT_CLASSPATH_RESOLUTION_ENABLED_ATTRIBUTE_NAME
     * @see #HEAD_COMMENT_ATTRIBUTE_NAME
     * @see #TAIL_COMMENT_ATTRIBUTE_NAME
     */
    public Model processModel( final ModelContext context, final Model model ) throws ModelException
    {
        if ( context == null )
        {
            throw new NullPointerException( "context" );
        }
        if ( model == null )
        {
            throw new NullPointerException( "model" );
        }

        Model processed = model;

        boolean contextEnabled = this.isEnabled();
        if ( DEFAULT_ENABLED == contextEnabled && context.getAttribute( ENABLED_ATTRIBUTE_NAME ) instanceof Boolean )
        {
            contextEnabled = (Boolean) context.getAttribute( ENABLED_ATTRIBUTE_NAME );
        }

        boolean contextModelObjectClasspathResolutionEnabled = this.isModelObjectClasspathResolutionEnabled();
        if ( contextModelObjectClasspathResolutionEnabled == DEFAULT_MODEL_OBJECT_CLASSPATH_RESOLUTION_ENABLED
                 && context.getAttribute( MODEL_OBJECT_CLASSPATH_RESOLUTION_ENABLED_ATTRIBUTE_NAME ) instanceof Boolean )
        {
            contextModelObjectClasspathResolutionEnabled =
                (Boolean) context.getAttribute( MODEL_OBJECT_CLASSPATH_RESOLUTION_ENABLED_ATTRIBUTE_NAME );

        }

        if ( contextEnabled )
        {
            processed = model.clone();
            final Modules modules = ModelHelper.getModules( processed );

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

                if ( modules.getSpecifications() != null )
                {
                    for ( int i = 0, s0 = modules.getSpecifications().getSpecification().size(); i < s0; i++ )
                    {
                        final Specification specification = modules.getSpecifications().getSpecification().get( i );
                        final SourceFilesType sourceFilesType = specification.getAnyObject( SourceFilesType.class );

                        if ( sourceFilesType != null )
                        {
                            this.applyDefaults( context, modules, specification, sourceFilesType );
                        }
                    }
                }

                if ( modules.getImplementations() != null )
                {
                    for ( int i = 0, s0 = modules.getImplementations().getImplementation().size(); i < s0; i++ )
                    {
                        final Implementation implementation = modules.getImplementations().getImplementation().get( i );
                        final SourceFilesType sourceFilesType = implementation.getAnyObject( SourceFilesType.class );

                        if ( sourceFilesType != null )
                        {
                            this.applyDefaults( context, modules, implementation, sourceFilesType );
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

        return processed;
    }

    /**
     * Gets the default source code file location for a given specification.
     * <p>
     * If the specification provides a Java type name, this method returns a Java source code file location based on
     * that Java type name.
     * </p>
     *
     * @param context The context to get the default location with.
     * @param modules The model to get the default location with.
     * @param specification The specification to get the default location for.
     *
     * @return The default location for {@code specification} or {@code null}.
     *
     * @throws NullPointerException if {@code context}, {@code modules} or {@code specification} is {@code null}.
     *
     * @see SourceFileType#getLocation()
     * @see Specification#getJavaTypeName()
     * @since 1.6
     */
    protected String getDefaultSourceFileLocation( final ModelContext context, final Modules modules,
                                                   final Specification specification )
    {
        if ( context == null )
        {
            throw new NullPointerException( "context" );
        }
        if ( modules == null )
        {
            throw new NullPointerException( "modules" );
        }
        if ( specification == null )
        {
            throw new NullPointerException( "specification" );
        }

        String location = null;

        try
        {
            if ( specification.getJavaTypeName() != null )
            {
                location = specification.getJavaTypeName().getQualifiedName().replace( '.', '/' ) + ".java";
            }
        }
        catch ( final ModelObjectException e )
        {
            context.log( Level.WARNING, getMessage( e ), null );
        }

        return location;
    }

    /**
     * Gets the default source code file location for a given implementation.
     * <p>
     * If the implementation provides a Java type name, this method returns a Java source code file location based on
     * that Java type name.
     * </p>
     *
     * @param context The context to get the default location with.
     * @param modules The model to get the default location with.
     * @param implementation The implementation to get the default location for.
     *
     * @return The default location for {@code implementation} or {@code null}.
     *
     * @throws NullPointerException if {@code context}, {@code modules} or {@code implementation} is {@code null}.
     *
     * @see SourceFileType#getLocation()
     * @see Implementation#getJavaTypeName()
     * @since 1.6
     */
    protected String getDefaultSourceFileLocation( final ModelContext context, final Modules modules,
                                                   final Implementation implementation )
    {
        if ( context == null )
        {
            throw new NullPointerException( "context" );
        }
        if ( modules == null )
        {
            throw new NullPointerException( "modules" );
        }
        if ( implementation == null )
        {
            throw new NullPointerException( "implementation" );
        }

        String location = null;

        try
        {
            if ( implementation.getJavaTypeName() != null )
            {
                location = implementation.getJavaTypeName().getQualifiedName().replace( '.', '/' ) + ".java";
            }
        }
        catch ( final ModelObjectException e )
        {
            context.log( Level.WARNING, getMessage( e ), null );
        }

        return location;
    }

    /**
     * Gets the default source section name for a given specification.
     * <p>
     * If the specification provides a Java type name, this method returns a section name based on that Java type
     * name.
     * </p>
     *
     * @param context The context to get the default section name with.
     * @param modules The model to get the default section name with.
     * @param specification The specification to get the default section name for.
     *
     * @return The default source section name for {@code specification} or {@code null}.
     *
     * @throws NullPointerException if {@code context}, {@code modules} or {@code specification} is {@code null}.
     *
     * @see SourceSectionType#getName()
     * @see Specification#getJavaTypeName()
     * @since 1.6
     */
    protected String getDefaultSourceSectionName( final ModelContext context, final Modules modules,
                                                  final Specification specification )
    {
        if ( context == null )
        {
            throw new NullPointerException( "context" );
        }
        if ( modules == null )
        {
            throw new NullPointerException( "modules" );
        }
        if ( specification == null )
        {
            throw new NullPointerException( "specification" );
        }

        String sectionName = null;

        try
        {
            final JavaTypeName javaTypeName = specification.getJavaTypeName();

            if ( javaTypeName != null )
            {
                sectionName = javaTypeName.getName( false );
            }
        }
        catch ( final ModelObjectException e )
        {
            context.log( Level.WARNING, getMessage( e ), null );
        }

        return sectionName;
    }

    /**
     * Gets the default source section name for a given implementation.
     * <p>
     * If the implementation provides a Java type name, this method returns a section name based that Java type
     * name.
     * </p>
     *
     * @param context The context to get the default section name with.
     * @param modules The model to get the default section name with.
     * @param implementation The implementation to get the default section name for.
     *
     * @return The default source section name for {@code implementation} or {@code null}.
     *
     * @throws NullPointerException if {@code context}, {@code modules} or {@code implementation} is {@code null}.
     *
     * @see SourceSectionType#getName()
     * @see Implementation#getJavaTypeName()
     * @since 1.6
     */
    protected String getDefaultSourceSectionName( final ModelContext context, final Modules modules,
                                                  final Implementation implementation )
    {
        if ( context == null )
        {
            throw new NullPointerException( "context" );
        }
        if ( modules == null )
        {
            throw new NullPointerException( "modules" );
        }
        if ( implementation == null )
        {
            throw new NullPointerException( "implementation" );
        }

        String sectionName = null;

        try
        {
            final JavaTypeName javaTypeName = implementation.getJavaTypeName();

            if ( javaTypeName != null )
            {
                sectionName = javaTypeName.getName( false );
            }
        }
        catch ( final ModelObjectException e )
        {
            context.log( Level.WARNING, getMessage( e ), null );
        }

        return sectionName;
    }

    /**
     * Updates any optional attributes to default values.
     *
     * @param context The context to apply defaults with.
     * @param modules The model to to apply defaults with.
     * @param specification The specification corresponding to {@code sourceFilesType}.
     * @param sourceFilesType The model to update.
     *
     * @throws NullPointerException if {@code context}, {@code modules}, {@code specification} or
     * {@code sourceFilesType} is {@code null}.
     */
    private void applyDefaults( final ModelContext context, final Modules modules, final Specification specification,
                                final SourceFilesType sourceFilesType )
    {
        if ( context == null )
        {
            throw new NullPointerException( "context" );
        }
        if ( modules == null )
        {
            throw new NullPointerException( "modules" );
        }
        if ( specification == null )
        {
            throw new NullPointerException( "specification" );
        }
        if ( sourceFilesType == null )
        {
            throw new NullPointerException( "sourceFilesType" );
        }

        String contextHeadComment = this.getHeadComment();
        if ( ( DEFAULT_HEAD_COMMENT != null
               ? DEFAULT_HEAD_COMMENT.equals( contextHeadComment )
               : contextHeadComment == null )
                 && context.getAttribute( HEAD_COMMENT_ATTRIBUTE_NAME ) instanceof String )
        {
            contextHeadComment = (String) context.getAttribute( HEAD_COMMENT_ATTRIBUTE_NAME );
        }

        if ( contextHeadComment != null && contextHeadComment.length() == 0 )
        {
            contextHeadComment = null;
        }

        String contextTailComment = this.getTailComment();
        if ( ( DEFAULT_TAIL_COMMENT != null
               ? DEFAULT_TAIL_COMMENT.equals( contextTailComment )
               : contextTailComment == null )
                 && context.getAttribute( TAIL_COMMENT_ATTRIBUTE_NAME ) instanceof String )
        {
            contextTailComment = (String) context.getAttribute( TAIL_COMMENT_ATTRIBUTE_NAME );
        }

        if ( contextTailComment != null && contextTailComment.length() == 0 )
        {
            contextTailComment = null;
        }

        for ( int i = 0, s0 = sourceFilesType.getSourceFile().size(); i < s0; i++ )
        {
            final SourceFileType s = sourceFilesType.getSourceFile().get( i );

            if ( s.getTemplate() == null )
            {
                s.setTemplate( SPECIFICATION_TEMPLATE );
            }
            if ( s.getLocation() == null )
            {
                s.setLocation( this.getDefaultSourceFileLocation( context, modules, specification ) );
            }
            if ( s.getHeadComment() == null )
            {
                s.setHeadComment( contextHeadComment );
            }
            if ( s.getTailComment() == null )
            {
                s.setTailComment( contextTailComment );
            }

            this.applyDefaults( context, modules, specification, s.getSourceSections() );
        }
    }

    /**
     * Updates any optional attributes to default values.
     *
     * @param context The context to apply defaults with.
     * @param modules The model to to apply defaults with.
     * @param specification The specification corresponding to {@code sourceSectionsType}.
     * @param sourceSectionsType The model to update or {@code null}.
     *
     * @throws NullPointerException if {@code context}, {@code modules} or {@code specification} is {@code null}.
     */
    private void applyDefaults( final ModelContext context, final Modules modules, final Specification specification,
                                final SourceSectionsType sourceSectionsType )
    {
        if ( context == null )
        {
            throw new NullPointerException( "context" );
        }
        if ( modules == null )
        {
            throw new NullPointerException( "modules" );
        }
        if ( specification == null )
        {
            throw new NullPointerException( "specification" );
        }

        try
        {
            if ( sourceSectionsType != null )
            {
                for ( int i = 0, s0 = sourceSectionsType.getSourceSection().size(); i < s0; i++ )
                {
                    final SourceSectionType s = sourceSectionsType.getSourceSection().get( i );

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

                    final String sectionName = this.getDefaultSourceSectionName( context, modules, specification );

                    if ( sectionName != null && sectionName.equals( s.getName() ) )
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

                    this.applyDefaults( context, modules, specification, s.getSourceSections() );
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
     * @param context The context to apply defaults with.
     * @param modules The model to to apply defaults with.
     * @param implementation The implementation corresponding to {@code sourceFilesType}.
     * @param sourceFilesType The model to update.
     *
     * @throws NullPointerException if {@code context}, {@code modules}, {@code implementation} or
     * {@code sourceFilesType} is {@code null}.
     */
    private void applyDefaults( final ModelContext context, final Modules modules, final Implementation implementation,
                                final SourceFilesType sourceFilesType )
    {
        if ( context == null )
        {
            throw new NullPointerException( "context" );
        }
        if ( modules == null )
        {
            throw new NullPointerException( "modules" );
        }
        if ( implementation == null )
        {
            throw new NullPointerException( "implementation" );
        }
        if ( sourceFilesType == null )
        {
            throw new NullPointerException( "sourceFilesType" );
        }

        String contextHeadComment = this.getHeadComment();
        if ( ( DEFAULT_HEAD_COMMENT != null
               ? DEFAULT_HEAD_COMMENT.equals( contextHeadComment )
               : contextHeadComment == null )
                 && context.getAttribute( HEAD_COMMENT_ATTRIBUTE_NAME ) instanceof String )
        {
            contextHeadComment = (String) context.getAttribute( HEAD_COMMENT_ATTRIBUTE_NAME );
        }

        if ( contextHeadComment != null && contextHeadComment.length() == 0 )
        {
            contextHeadComment = null;
        }

        String contextTailComment = this.getTailComment();
        if ( ( DEFAULT_TAIL_COMMENT != null
               ? DEFAULT_TAIL_COMMENT.equals( contextTailComment )
               : contextTailComment == null )
                 && context.getAttribute( TAIL_COMMENT_ATTRIBUTE_NAME ) instanceof String )
        {
            contextTailComment = (String) context.getAttribute( TAIL_COMMENT_ATTRIBUTE_NAME );
        }

        if ( contextTailComment != null && contextTailComment.length() == 0 )
        {
            contextTailComment = null;
        }

        for ( int i = 0, s0 = sourceFilesType.getSourceFile().size(); i < s0; i++ )
        {
            final SourceFileType s = sourceFilesType.getSourceFile().get( i );

            if ( s.getTemplate() == null )
            {
                s.setTemplate( IMPLEMENTATION_TEMPLATE );
            }
            if ( s.getLocation() == null )
            {
                s.setLocation( this.getDefaultSourceFileLocation( context, modules, implementation ) );
            }
            if ( s.getHeadComment() == null )
            {
                s.setHeadComment( contextHeadComment );
            }
            if ( s.getTailComment() == null )
            {
                s.setTailComment( contextTailComment );
            }

            this.applyDefaults( context, modules, implementation, s.getSourceSections() );
        }
    }

    /**
     * Updates any optional attributes to default values.
     *
     * @param context The context to apply defaults with.
     * @param modules The model to to apply defaults with.
     * @param implementation The implementation corresponding to {@code sourceSectionsType}.
     * @param sourceSectionsType The model to update or {@code null}.
     *
     * @throws NullPointerException if {@code context}, {@code modules} or {@code implementation} is {@code null}.
     */
    private void applyDefaults( final ModelContext context, final Modules modules, final Implementation implementation,
                                final SourceSectionsType sourceSectionsType )
    {
        if ( context == null )
        {
            throw new NullPointerException( "context" );
        }
        if ( modules == null )
        {
            throw new NullPointerException( "modules" );
        }
        if ( implementation == null )
        {
            throw new NullPointerException( "implementation" );
        }

        final Specifications specifications = modules.getSpecifications( implementation.getIdentifier() );
        final Dependencies dependencies = modules.getDependencies( implementation.getIdentifier() );
        final Messages messages = modules.getMessages( implementation.getIdentifier() );
        final Properties properties = modules.getProperties( implementation.getIdentifier() );

        try
        {
            if ( sourceSectionsType != null )
            {
                for ( int i = 0, s0 = sourceSectionsType.getSourceSection().size(); i < s0; i++ )
                {
                    final SourceSectionType s = sourceSectionsType.getSourceSection().get( i );

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

                    if ( specifications != null )
                    {
                        for ( final Specification specification : specifications.getSpecification() )
                        {
                            final String sectionName =
                                this.getDefaultSourceSectionName( context, modules, specification );

                            if ( sectionName != null && sectionName.equals( s.getName() ) )
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
                    }

                    final String sectionName = this.getDefaultSourceSectionName( context, modules, implementation );

                    if ( sectionName != null && sectionName.equals( s.getName() ) )
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

                    this.applyDefaults( context, modules, implementation, s.getSourceSections() );
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

    private static String getMessage( final Throwable t )
    {
        return t != null
                   ? t.getMessage() != null && t.getMessage().trim().length() > 0
                         ? t.getMessage()
                         : getMessage( t.getCause() )
                   : null;

    }

    private static String getMessage( final String key, final Object... args )
    {
        return MessageFormat.format( ResourceBundle.getBundle(
            ToolsModelProcessor.class.getName().replace( '.', '/' ), Locale.getDefault() ).getString( key ), args );

    }

}
