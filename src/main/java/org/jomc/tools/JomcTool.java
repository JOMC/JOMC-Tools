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
package org.jomc.tools;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.net.URL;
import java.text.DateFormat;
import java.text.Format;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.exception.VelocityException;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.log.LogChute;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.apache.velocity.runtime.resource.loader.URLResourceLoader;
import org.jomc.model.Argument;
import org.jomc.model.ArgumentType;
import org.jomc.model.Dependency;
import org.jomc.model.Implementation;
import org.jomc.model.Message;
import org.jomc.model.ModelObject;
import org.jomc.model.Modules;
import org.jomc.model.Multiplicity;
import org.jomc.model.Properties;
import org.jomc.model.Property;
import org.jomc.model.Specification;
import org.jomc.model.SpecificationReference;
import org.jomc.model.Specifications;
import org.jomc.model.Text;
import org.jomc.model.Texts;
import org.jomc.model.modlet.ModelHelper;
import org.jomc.modlet.Model;

/**
 * Base tool class.
 *
 * @author <a href="mailto:schulte2005@users.sourceforge.net">Christian Schulte</a>
 * @version $Id$
 */
public class JomcTool
{

    /** Listener interface. */
    public abstract static class Listener
    {

        /**
         * Get called on logging.
         *
         * @param level The level of the event.
         * @param message The message of the event or {@code null}.
         * @param throwable The throwable of the event or {@code null}.
         *
         * @throws NullPointerException if {@code level} is {@code null}.
         */
        public abstract void onLog( Level level, String message, Throwable throwable );

    }

    /** Empty byte array. */
    private static final byte[] NO_BYTES =
    {
    };

    /** The prefix of the template location. */
    private static final String TEMPLATE_PREFIX =
        JomcTool.class.getPackage().getName().replace( '.', '/' ) + "/templates/";

    /** Constant for the default template profile. */
    private static final String DEFAULT_TEMPLATE_PROFILE = "jomc-java";

    /** Default template profile. */
    private static volatile String defaultTemplateProfile;

    /**
     * Log level events are logged at by default.
     * @see #getDefaultLogLevel()
     */
    private static final Level DEFAULT_LOG_LEVEL = Level.WARNING;

    /** Default log level. */
    private static volatile Level defaultLogLevel;

    /** The model of the instance. */
    private Model model;

    /** {@code VelocityEngine} of the generator. */
    private VelocityEngine velocityEngine;

    /** The encoding to use for reading templates. */
    private String templateEncoding;

    /** The location to search for templates in addition to searching the class path. */
    private URL templateLocation;

    /** The encoding to use for reading files. */
    private String inputEncoding;

    /** The encoding to use for writing files. */
    private String outputEncoding;

    /** The template parameters. */
    private Map<String, Object> templateParameters;

    /** The template profile of the instance. */
    private String templateProfile;

    /** The indentation string of the instance. */
    private String indentation;

    /** The line separator of the instance. */
    private String lineSeparator;

    /** The listeners of the instance. */
    private List<Listener> listeners;

    /** Log level of the instance. */
    private Level logLevel;

    /** The locale of the instance. */
    private Locale locale;

    /** Cached indentation strings. */
    private final Map<Integer, String> indentationCache = new HashMap<Integer, String>();

    /** Creates a new {@code JomcTool} instance. */
    public JomcTool()
    {
        super();
    }

    /**
     * Creates a new {@code JomcTool} instance taking a {@code JomcTool} instance to initialize the new instance with.
     *
     * @param tool The instance to initialize the new instance with.
     *
     * @throws NullPointerException if {@code tool} is {@code null}.
     * @throws IOException if copying {@code tool} fails.
     */
    public JomcTool( final JomcTool tool ) throws IOException
    {
        this();

        if ( tool == null )
        {
            throw new NullPointerException( "tool" );
        }

        this.indentation = tool.indentation;
        this.inputEncoding = tool.inputEncoding;
        this.lineSeparator = tool.lineSeparator;
        this.listeners = tool.listeners != null ? new LinkedList<Listener>( tool.listeners ) : null;
        this.logLevel = tool.logLevel;
        this.model = tool.model != null ? new Model( tool.model ) : null;
        this.outputEncoding = tool.outputEncoding;
        this.templateEncoding = tool.templateEncoding;
        this.templateProfile = tool.templateProfile;
        this.velocityEngine = tool.velocityEngine;
        this.locale = tool.locale;
        this.templateParameters =
            tool.templateParameters != null ? new HashMap<String, Object>( tool.templateParameters ) : null;

        this.templateLocation =
            tool.templateLocation != null ? new URL( tool.templateLocation.toExternalForm() ) : null;

    }

    /**
     * Gets the list of registered listeners.
     * <p>This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make
     * to the returned list will be present inside the object. This is why there is no {@code set} method for the
     * listeners property.</p>
     *
     * @return The list of registered listeners.
     *
     * @see #log(java.util.logging.Level, java.lang.String, java.lang.Throwable)
     */
    public List<Listener> getListeners()
    {
        if ( this.listeners == null )
        {
            this.listeners = new LinkedList<Listener>();
        }

        return this.listeners;
    }

    /**
     * Gets the default log level events are logged at.
     * <p>The default log level is controlled by system property {@code org.jomc.tools.JomcTool.defaultLogLevel} holding
     * the log level to log events at by default. If that property is not set, the {@code WARNING} default is
     * returned.</p>
     *
     * @return The log level events are logged at by default.
     *
     * @see #getLogLevel()
     * @see Level#parse(java.lang.String)
     */
    public static Level getDefaultLogLevel()
    {
        if ( defaultLogLevel == null )
        {
            defaultLogLevel = Level.parse( System.getProperty( "org.jomc.tools.JomcTool.defaultLogLevel",
                                                               DEFAULT_LOG_LEVEL.getName() ) );

        }

        return defaultLogLevel;
    }

    /**
     * Sets the default log level events are logged at.
     *
     * @param value The new default level events are logged at or {@code null}.
     *
     * @see #getDefaultLogLevel()
     */
    public static void setDefaultLogLevel( final Level value )
    {
        defaultLogLevel = value;
    }

    /**
     * Gets the log level of the instance.
     *
     * @return The log level of the instance.
     *
     * @see #getDefaultLogLevel()
     * @see #setLogLevel(java.util.logging.Level)
     * @see #isLoggable(java.util.logging.Level)
     */
    public final Level getLogLevel()
    {
        if ( this.logLevel == null )
        {
            this.logLevel = getDefaultLogLevel();

            if ( this.isLoggable( Level.CONFIG ) )
            {
                this.log( Level.CONFIG, getMessage( "defaultLogLevelInfo", this.logLevel.getLocalizedName() ), null );
            }
        }

        return this.logLevel;
    }

    /**
     * Sets the log level of the instance.
     *
     * @param value The new log level of the instance or {@code null}.
     *
     * @see #getLogLevel()
     * @see #isLoggable(java.util.logging.Level)
     */
    public final void setLogLevel( final Level value )
    {
        this.logLevel = value;
    }

    /**
     * Checks if a message at a given level is provided to the listeners of the instance.
     *
     * @param level The level to test.
     *
     * @return {@code true} if messages at {@code level} are provided to the listeners of the instance;
     * {@code false} if messages at {@code level} are not provided to the listeners of the instance.
     *
     * @throws NullPointerException if {@code level} is {@code null}.
     *
     * @see #getLogLevel()
     * @see #setLogLevel(java.util.logging.Level)
     * @see #log(java.util.logging.Level, java.lang.String, java.lang.Throwable)
     */
    public boolean isLoggable( final Level level )
    {
        if ( level == null )
        {
            throw new NullPointerException( "level" );
        }

        return level.intValue() >= this.getLogLevel().intValue();
    }

    /**
     * Gets the Java package name of a specification.
     *
     * @param specification The specification to get the Java package name of.
     *
     * @return The Java package name of {@code specification} or {@code null}.
     *
     * @throws NullPointerException if {@code specification} is {@code null}.
     */
    public String getJavaPackageName( final Specification specification )
    {
        if ( specification == null )
        {
            throw new NullPointerException( "specification" );
        }

        return specification.getClazz() != null ? this.getJavaPackageName( specification.getClazz() ) : null;
    }

    /**
     * Gets the Java type name of a specification.
     *
     * @param specification The specification to get the Java type name of.
     * @param qualified {@code true} to return the fully qualified type name (with package name prepended);
     * {@code false} to return the short type name (without package name prepended).
     *
     * @return The Java type name of {@code specification} or {@code null}.
     *
     * @throws NullPointerException if {@code specification} is {@code null}.
     */
    public String getJavaTypeName( final Specification specification, final boolean qualified )
    {
        if ( specification == null )
        {
            throw new NullPointerException( "specification" );
        }

        if ( specification.getClazz() != null )
        {
            final StringBuilder typeName = new StringBuilder( specification.getClazz().length() );
            final String javaPackageName = this.getJavaPackageName( specification );

            if ( qualified && javaPackageName.length() > 0 )
            {
                typeName.append( javaPackageName ).append( '.' );
            }

            typeName.append( javaPackageName.length() > 0
                             ? specification.getClazz().substring( javaPackageName.length() + 1 )
                             : specification.getClazz() );

            return typeName.toString();
        }

        return null;
    }

    /**
     * Gets the Java class path location of a specification.
     *
     * @param specification The specification to return the Java class path location of.
     *
     * @return The Java class path location of {@code specification} or {@code null}.
     *
     * @throws NullPointerException if {@code specification} is {@code null}.
     */
    public String getJavaClasspathLocation( final Specification specification )
    {
        if ( specification == null )
        {
            throw new NullPointerException( "specification" );
        }

        return specification.getClazz() != null
               ? ( this.getJavaTypeName( specification, true ) ).replace( '.', '/' )
               : null;

    }

    /**
     * Gets the Java package name of a specification reference.
     *
     * @param reference The specification reference to get the Java package name of.
     *
     * @return The Java package name of {@code reference} or {@code null}.
     *
     * @throws NullPointerException if {@code reference} is {@code null}.
     */
    public String getJavaPackageName( final SpecificationReference reference )
    {
        if ( reference == null )
        {
            throw new NullPointerException( "reference" );
        }

        final Specification s = this.getModules().getSpecification( reference.getIdentifier() );
        assert s != null : "Specification '" + reference.getIdentifier() + "' not found.";
        return s.getClazz() != null ? this.getJavaPackageName( s ) : null;
    }

    /**
     * Gets the name of a Java type of a given specification reference.
     *
     * @param reference The specification reference to get a Java type name of.
     * @param qualified {@code true} to return the fully qualified type name (with package name prepended);
     * {@code false} to return the short type name (without package name prepended).
     *
     * @return The Java type name of {@code reference} or {@code null}.
     *
     * @throws NullPointerException if {@code reference} is {@code null}.
     */
    public String getJavaTypeName( final SpecificationReference reference, final boolean qualified )
    {
        if ( reference == null )
        {
            throw new NullPointerException( "reference" );
        }

        final Specification s = this.getModules().getSpecification( reference.getIdentifier() );
        assert s != null : "Specification '" + reference.getIdentifier() + "' not found.";
        return s.getClazz() != null ? this.getJavaTypeName( s, qualified ) : null;
    }

    /**
     * Gets the Java package name of an implementation.
     *
     * @param implementation The implementation to get the Java package name of.
     *
     * @return The Java package name of {@code implementation} or {@code null}.
     *
     * @throws NullPointerException if {@code implementation} is {@code null}.
     */
    public String getJavaPackageName( final Implementation implementation )
    {
        if ( implementation == null )
        {
            throw new NullPointerException( "implementation" );
        }

        return implementation.getClazz() != null ? this.getJavaPackageName( implementation.getClazz() ) : null;
    }

    /**
     * Gets the Java type name of an implementation.
     *
     * @param implementation The implementation to get the Java type name of.
     * @param qualified {@code true} to return the fully qualified type name (with package name prepended);
     * {@code false} to return the short type name (without package name prepended).
     *
     * @return The Java type name of {@code implementation} or {@code null}.
     *
     * @throws NullPointerException if {@code implementation} is {@code null}.
     */
    public String getJavaTypeName( final Implementation implementation, final boolean qualified )
    {
        if ( implementation == null )
        {
            throw new NullPointerException( "implementation" );
        }

        if ( implementation.getClazz() != null )
        {
            final StringBuilder typeName = new StringBuilder( implementation.getClazz().length() );
            final String javaPackageName = this.getJavaPackageName( implementation );

            if ( qualified && javaPackageName.length() > 0 )
            {
                typeName.append( javaPackageName ).append( '.' );
            }

            typeName.append( javaPackageName.length() > 0
                             ? implementation.getClazz().substring( javaPackageName.length() + 1 )
                             : implementation.getClazz() );

            return typeName.toString();
        }

        return null;
    }

    /**
     * Gets the Java class path location of an implementation.
     *
     * @param implementation The implementation to return the Java class path location of.
     *
     * @return The Java class path location of {@code implementation} or {@code null}.
     *
     * @throws NullPointerException if {@code implementation} is {@code null}.
     */
    public String getJavaClasspathLocation( final Implementation implementation )
    {
        if ( implementation == null )
        {
            throw new NullPointerException( "implementation" );
        }

        return implementation.getClazz() != null
               ? ( this.getJavaTypeName( implementation, true ) ).replace( '.', '/' )
               : null;

    }

    /**
     * Gets a list of names of all Java types an implementation implements.
     *
     * @param implementation The implementation to get names of all implemented Java types of.
     * @param qualified {@code true} to return the fully qualified type names (with package name prepended);
     * {@code false} to return the short type names (without package name prepended).
     *
     * @return An unmodifiable list of names of all Java types implemented by {@code implementation}.
     *
     * @throws NullPointerException if {@code implementation} is {@code null}.
     *
     * @deprecated As of JOMC 1.2, replaced by method {@link #getImplementedJavaTypeNames(org.jomc.model.Implementation, boolean)}.
     */
    @Deprecated
    public List<String> getJavaInterfaceNames( final Implementation implementation, final boolean qualified )
    {
        if ( implementation == null )
        {
            throw new NullPointerException( "implementation" );
        }

        return this.getImplementedJavaTypeNames( implementation, qualified );
    }

    /**
     * Gets a list of names of all Java types an implementation implements.
     *
     * @param implementation The implementation to get names of all implemented Java types of.
     * @param qualified {@code true} to return the fully qualified type names (with package name prepended);
     * {@code false} to return the short type names (without package name prepended).
     *
     * @return An unmodifiable list of names of all Java types implemented by {@code implementation}.
     *
     * @throws NullPointerException if {@code implementation} is {@code null}.
     */
    public List<String> getImplementedJavaTypeNames( final Implementation implementation, final boolean qualified )
    {
        if ( implementation == null )
        {
            throw new NullPointerException( "implementation" );
        }

        final Specifications specs = this.getModules().getSpecifications( implementation.getIdentifier() );
        final List<String> col = new ArrayList<String>( specs == null ? 0 : specs.getSpecification().size() );

        if ( specs != null )
        {
            for ( Specification s : specs.getSpecification() )
            {
                if ( s.getClazz() != null )
                {
                    final String typeName = this.getJavaTypeName( s, qualified );
                    if ( !col.contains( typeName ) )
                    {
                        col.add( typeName );
                    }
                }
            }
        }

        return Collections.unmodifiableList( col );
    }

    /**
     * Gets the Java type name of an argument.
     *
     * @param argument The argument to get the Java type name of.
     *
     * @return The Java type name of {@code argument}.
     *
     * @throws NullPointerException if {@code argument} is {@code null}.
     */
    public String getJavaTypeName( final Argument argument )
    {
        if ( argument == null )
        {
            throw new NullPointerException( "argument" );
        }

        String javaTypeName = "java.lang.String";

        if ( argument.getType() == ArgumentType.DATE || argument.getType() == ArgumentType.TIME )
        {
            javaTypeName = "java.util.Date";
        }
        else if ( argument.getType() == ArgumentType.NUMBER )
        {
            javaTypeName = "java.lang.Number";
        }

        return javaTypeName;
    }

    /**
     * Gets a Java method parameter name of an argument.
     *
     * @param argument The argument to get the Java method parameter name of.
     *
     * @return The Java method parameter name of {@code argument}.
     *
     * @throws NullPointerException if {@code argument} is {@code null}.
     *
     * @since 1.2
     */
    public String getJavaMethodParameterName( final Argument argument )
    {
        if ( argument == null )
        {
            throw new NullPointerException( "argument" );
        }

        return this.getJavaIdentifier( argument.getName(), false );
    }

    /**
     * Gets the Java type name of a property.
     *
     * @param property The property to get the Java type name of.
     * @param boxify {@code true} to return the name of the Java wrapper class when the type is a Java primitive type;
     * {@code false} to return the exact binary name (unboxed name) of the Java type.
     *
     * @return The Java type name of {@code property}.
     *
     * @throws NullPointerException if {@code property} is {@code null}.
     */
    public String getJavaTypeName( final Property property, final boolean boxify )
    {
        if ( property == null )
        {
            throw new NullPointerException( "property" );
        }

        if ( property.getType() != null )
        {
            final String typeName = property.getType();

            if ( boxify )
            {
                if ( Boolean.TYPE.getName().equals( typeName ) )
                {
                    return Boolean.class.getName();
                }
                if ( Byte.TYPE.getName().equals( typeName ) )
                {
                    return Byte.class.getName();
                }
                if ( Character.TYPE.getName().equals( typeName ) )
                {
                    return Character.class.getName();
                }
                if ( Double.TYPE.getName().equals( typeName ) )
                {
                    return Double.class.getName();
                }
                if ( Float.TYPE.getName().equals( typeName ) )
                {
                    return Float.class.getName();
                }
                if ( Integer.TYPE.getName().equals( typeName ) )
                {
                    return Integer.class.getName();
                }
                if ( Long.TYPE.getName().equals( typeName ) )
                {
                    return Long.class.getName();
                }
                if ( Short.TYPE.getName().equals( typeName ) )
                {
                    return Short.class.getName();
                }
            }

            return typeName;
        }

        return property.getAny() != null ? Object.class.getName() : String.class.getName();
    }

    /**
     * Gets a flag indicating if the type of a given property is a Java primitive.
     *
     * @param property The property to query.
     *
     * @return {@code true} if the type of {@code property} is a Java primitive; {@code false} if not.
     *
     * @throws NullPointerException if {@code property} is {@code null}.
     */
    public boolean isJavaPrimitiveType( final Property property )
    {
        if ( property == null )
        {
            throw new NullPointerException( "property" );
        }

        return !this.getJavaTypeName( property, false ).equals( this.getJavaTypeName( property, true ) );
    }

    /**
     * Gets the name of a Java getter method of a given property.
     *
     * @param property The property to get a Java getter method name of.
     *
     * @return The Java getter method name of {@code property}.
     *
     * @throws NullPointerException if {@code property} is {@code null}.
     */
    public String getJavaGetterMethodName( final Property property )
    {
        if ( property == null )
        {
            throw new NullPointerException( "property" );
        }

        String prefix = "get";

        final String javaTypeName = this.getJavaTypeName( property, true );
        if ( Boolean.class.getName().equals( javaTypeName ) )
        {
            prefix = "is";
        }

        return prefix + this.getJavaIdentifier( property.getName(), true );
    }

    /**
     * Gets the name of a Java setter method of a given property.
     *
     * @param property The property to get a Java setter method name of.
     *
     * @return The Java setter method name of {@code property}.
     *
     * @throws NullPointerException if {@code property} is {@code null}.
     *
     * @since 1.2
     */
    public String getJavaSetterMethodName( final Property property )
    {
        if ( property == null )
        {
            throw new NullPointerException( "property" );
        }

        return "set" + this.getJavaIdentifier( property.getName(), true );
    }

    /**
     * Gets a Java method parameter name of a property.
     *
     * @param property The property to get the Java method parameter name of.
     *
     * @return The Java method parameter name of {@code property}.
     *
     * @throws NullPointerException if {@code property} is {@code null}.
     *
     * @since 1.2
     */
    public String getJavaMethodParameterName( final Property property )
    {
        if ( property == null )
        {
            throw new NullPointerException( "property" );
        }

        return this.getJavaIdentifier( property.getName(), false );
    }

    /**
     * Gets the name of a Java type of a given dependency.
     *
     * @param dependency The dependency to get a dependency Java type name of.
     *
     * @return The Java type name of {@code dependency} or {@code null}.
     *
     * @throws NullPointerException if {@code dependency} is {@code null}.
     */
    public String getJavaTypeName( final Dependency dependency )
    {
        if ( dependency == null )
        {
            throw new NullPointerException( "dependency" );
        }

        final Specification s = this.getModules().getSpecification( dependency.getIdentifier() );

        if ( s != null && s.getClazz() != null )
        {
            final StringBuilder typeName = new StringBuilder( s.getClazz().length() );
            typeName.append( this.getJavaTypeName( s, true ) );
            if ( s.getMultiplicity() == Multiplicity.MANY && dependency.getImplementationName() == null )
            {
                typeName.append( "[]" );
            }

            return typeName.toString();
        }

        return null;
    }

    /**
     * Gets the name of a Java getter method of a given dependency.
     *
     * @param dependency The dependency to get a Java getter method name of.
     *
     * @return The Java getter method name of {@code dependency}.
     *
     * @throws NullPointerException if {@code dependency} is {@code null}.
     */
    public String getJavaGetterMethodName( final Dependency dependency )
    {
        if ( dependency == null )
        {
            throw new NullPointerException( "dependency" );
        }

        return "get" + this.getJavaIdentifier( dependency.getName(), true );
    }

    /**
     * Gets the name of a Java setter method of a given dependency.
     *
     * @param dependency The dependency to get a Java setter method name of.
     *
     * @return The Java setter method name of {@code dependency}.
     *
     * @throws NullPointerException if {@code dependency} is {@code null}.
     *
     * @since 1.2
     */
    public String getJavaSetterMethodName( final Dependency dependency )
    {
        if ( dependency == null )
        {
            throw new NullPointerException( "dependency" );
        }

        return "set" + this.getJavaIdentifier( dependency.getName(), true );
    }

    /**
     * Gets a Java method parameter name of a dependency.
     *
     * @param dependency The dependency to get the Java method parameter name of.
     *
     * @return The Java method parameter name of {@code dependency}.
     *
     * @throws NullPointerException if {@code dependency} is {@code null}.
     *
     * @since 1.2
     */
    public String getJavaMethodParameterName( final Dependency dependency )
    {
        if ( dependency == null )
        {
            throw new NullPointerException( "dependency" );
        }

        return this.getJavaIdentifier( dependency.getName(), false );
    }

    /**
     * Gets the name of a Java getter method of a given message.
     *
     * @param message The message to get a Java getter method name of.
     *
     * @return The Java getter method name of {@code message}.
     *
     * @throws NullPointerException if {@code message} is {@code null}.
     */
    public String getJavaGetterMethodName( final Message message )
    {
        if ( message == null )
        {
            throw new NullPointerException( "message" );
        }

        return "get" + this.getJavaIdentifier( message.getName(), true );
    }

    /**
     * Gets the name of a Java setter method of a given message.
     *
     * @param message The message to get a Java setter method name of.
     *
     * @return The Java setter method name of {@code message}.
     *
     * @throws NullPointerException if {@code message} is {@code null}.
     *
     * @since 1.2
     */
    public String getJavaSetterMethodName( final Message message )
    {
        if ( message == null )
        {
            throw new NullPointerException( "message" );
        }

        return "set" + this.getJavaIdentifier( message.getName(), true );
    }

    /**
     * Gets a Java method parameter name of a message.
     *
     * @param message The message to get the Java method parameter name of.
     *
     * @return The Java method parameter name of {@code message}.
     *
     * @throws NullPointerException if {@code message} is {@code null}.
     *
     * @since 1.2
     */
    public String getJavaMethodParameterName( final Message message )
    {
        if ( message == null )
        {
            throw new NullPointerException( "message" );
        }

        return this.getJavaIdentifier( message.getName(), false );
    }

    /**
     * Gets the name of a Java modifier of a dependency of a given implementation.
     *
     * @param implementation The implementation to get a dependency Java modifier name of.
     * @param dependency The dependency to get a Java modifier name of.
     *
     * @return The Java modifier name of {@code dependency} of {@code implementation}.
     *
     * @throws NullPointerException if {@code implementation} or {@code dependency} is {@code null}.
     */
    public String getJavaModifierName( final Implementation implementation, final Dependency dependency )
    {
        if ( implementation == null )
        {
            throw new NullPointerException( "implementation" );
        }
        if ( dependency == null )
        {
            throw new NullPointerException( "dependency" );
        }

        return "private";
    }

    /**
     * Gets the name of a Java modifier of a message of a given implementation.
     *
     * @param implementation The implementation to get a message Java modifier name of.
     * @param message The message to get a Java modifier name of.
     *
     * @return The Java modifier name of {@code message} of {@code implementation}.
     *
     * @throws NullPointerException if {@code implementation} or {@code message} is {@code null}.
     */
    public String getJavaModifierName( final Implementation implementation, final Message message )
    {
        if ( implementation == null )
        {
            throw new NullPointerException( "implementation" );
        }
        if ( message == null )
        {
            throw new NullPointerException( "message" );
        }

        return "private";
    }

    /**
     * Gets the name of a Java modifier for a given property of a given implementation.
     *
     * @param implementation The implementation declaring {@code property}.
     * @param property The property to get a Java modifier name for.
     *
     * @return The Java modifier name for {@code property} of {@code implementation}.
     *
     * @throws NullPointerException if {@code implementation} or {@code property} is {@code null}.
     */
    public String getJavaModifierName( final Implementation implementation, final Property property )
    {
        if ( implementation == null )
        {
            throw new NullPointerException( "implementation" );
        }
        if ( property == null )
        {
            throw new NullPointerException( "property" );
        }

        String modifier = "private";
        final Properties specified = this.getModules().getSpecifiedProperties( implementation.getIdentifier() );

        if ( specified != null && specified.getProperty( property.getName() ) != null )
        {
            modifier = "public";
        }

        return modifier;
    }

    /**
     * Formats a text to a Javadoc comment.
     *
     * @param text The text to format to a Javadoc comment.
     * @param indentationLevel The indentation level of the comment.
     * @param linePrefix The text to prepend lines with.
     *
     * @return {@code text} formatted as a Javadoc comment.
     *
     * @throws NullPointerException if {@code text} or {@code linePrefix} is {@code null}.
     * @throws IllegalArgumentException if {@code indentationLevel} is negative.
     */
    public String getJavadocComment( final Text text, final int indentationLevel, final String linePrefix )
    {
        if ( text == null )
        {
            throw new NullPointerException( "text" );
        }
        if ( linePrefix == null )
        {
            throw new NullPointerException( "linePrefix" );
        }
        if ( indentationLevel < 0 )
        {
            throw new IllegalArgumentException( Integer.toString( indentationLevel ) );
        }

        try
        {
            String javadoc = "";

            if ( text.getValue() != null )
            {
                final String indent = this.getIndentation( indentationLevel );
                final BufferedReader reader = new BufferedReader( new StringReader( text.getValue() ) );
                final StringBuilder builder = new StringBuilder( text.getValue().length() );

                String line;
                while ( ( line = reader.readLine() ) != null )
                {
                    builder.append( this.getLineSeparator() ).append( indent ).append( linePrefix ).
                        append( line.replaceAll( "\\/\\*\\*", "/*" ).replaceAll( "\\*/", "/" ) );

                }

                if ( builder.length() > 0 )
                {
                    javadoc =
                        builder.substring( this.getLineSeparator().length() + indent.length() + linePrefix.length() );

                    if ( !new MimeType( text.getType() ).match( "text/html" ) )
                    {
                        javadoc = StringEscapeUtils.escapeHtml( javadoc );
                    }
                }
            }

            return javadoc;
        }
        catch ( final MimeTypeParseException e )
        {
            throw new AssertionError( e );
        }
        catch ( final IOException e )
        {
            throw new AssertionError( e );
        }
    }

    /**
     * Formats a text from a list of texts to a Javadoc comment.
     *
     * @param texts The list of texts to format to a Javadoc comment.
     * @param indentationLevel The indentation level of the comment.
     * @param linePrefix The text to prepend lines with.
     *
     * @return The text corresponding to the locale of the instance from the list of texts formatted to a Javadoc
     * comment.
     *
     * @throws NullPointerException if {@code texts} or {@code linePrefix} is {@code null}.
     * @throws IllegalArgumentException if {@code indentationLevel} is negative.
     *
     * @see #getLocale()
     *
     * @since 1.2
     */
    public String getJavadocComment( final Texts texts, final int indentationLevel, final String linePrefix )
    {
        if ( texts == null )
        {
            throw new NullPointerException( "texts" );
        }
        if ( linePrefix == null )
        {
            throw new NullPointerException( "linePrefix" );
        }
        if ( indentationLevel < 0 )
        {
            throw new IllegalArgumentException( Integer.toString( indentationLevel ) );
        }

        return this.getJavadocComment( texts.getText( this.getLocale().getLanguage() ), indentationLevel, linePrefix );
    }

    /**
     * Formats a string to a Java string with unicode escapes.
     *
     * @param str The string to format to a Java string or {@code null}.
     *
     * @return {@code str} formatted to a Java string or {@code null}.
     *
     * @see StringEscapeUtils#escapeJava(java.lang.String)
     */
    public String getJavaString( final String str )
    {
        return StringEscapeUtils.escapeJava( str );
    }

    /**
     * Formats a string to a Java identifier.
     *
     * @param str The string to format or {@code null}.
     * @param capitalize {@code true} to return an identifier with the first character upper cased; {@code false} to
     * return an identifier with the first character lower cased.
     *
     * @return {@code str} formatted to a Java identifier.
     *
     * @since 1.2
     */
    public String getJavaIdentifier( final String str, final boolean capitalize )
    {
        String identifier = null;

        if ( str != null )
        {
            final int len = str.length();
            final StringBuilder builder = new StringBuilder( len );
            boolean uc = capitalize;

            for ( int i = 0; i < len; i++ )
            {
                final char c = str.charAt( i );

                if ( !( Character.isJavaIdentifierStart( c ) || Character.isJavaIdentifierPart( c ) ) )
                {
                    uc = true;
                }
                else if ( builder.length() == 0
                          ? Character.isJavaIdentifierStart( c )
                          : Character.isJavaIdentifierPart( c ) )
                {
                    builder.append( uc ? Character.toUpperCase( c ) : c );
                    uc = false;
                }
            }

            identifier = builder.toString();
        }

        return identifier;
    }

    /**
     * Gets a flag indicating if the class of a given specification is located in the Java default package.
     *
     * @param specification The specification to test.
     *
     * @return {@code true} if the class of {@code specification} is located in the Java default package; {@code false}
     * if not.
     *
     * @throws NullPointerException if {@code specification} is {@code null}.
     */
    public boolean isJavaDefaultPackage( final Specification specification )
    {
        if ( specification == null )
        {
            throw new NullPointerException( "specification" );
        }

        return specification.getClazz() != null && this.getJavaPackageName( specification ).length() == 0;
    }

    /**
     * Gets a flag indicating if the class of a given implementation is located in the Java default package.
     *
     * @param implementation The implementation to test.
     *
     * @return {@code true} if the class of {@code implementation} is located in the Java default package; {@code false}
     * if not.
     *
     * @throws NullPointerException if {@code implementation} is {@code null}.
     */
    public boolean isJavaDefaultPackage( final Implementation implementation )
    {
        if ( implementation == null )
        {
            throw new NullPointerException( "implementation" );
        }

        return implementation.getClazz() != null && this.getJavaPackageName( implementation ).length() == 0;
    }

    /**
     * Formats a string to a HTML string with HTML entities.
     *
     * @param str The string to format to a HTML string with HTML entities or {@code null}.
     *
     * @return {@code str} formatted to a HTML string with HTML entities or {@code null}.
     *
     * @see StringEscapeUtils#escapeHtml(java.lang.String)
     *
     * @since 1.2
     */
    public String getHtmlString( final String str )
    {
        return StringEscapeUtils.escapeHtml( str );
    }

    /**
     * Formats a string to a XML string with XML entities.
     *
     * @param str The string to format to a XML string with XML entities or {@code null}.
     *
     * @return {@code str} formatted to a XML string with XML entities or {@code null}.
     *
     * @see StringEscapeUtils#escapeXml(java.lang.String)
     *
     * @since 1.2
     */
    public String getXmlString( final String str )
    {
        return StringEscapeUtils.escapeXml( str );
    }

    /**
     * Formats a string to a JavaScript string applying JavaScript string rules.
     *
     * @param str The string to format to a JavaScript string by applying JavaScript string rules or {@code null}.
     *
     * @return {@code str} formatted to a JavaScript string with JavaScript string rules applied or {@code null}.
     *
     * @see StringEscapeUtils#escapeJavaScript(java.lang.String)
     *
     * @since 1.2
     */
    public String getJavaScriptString( final String str )
    {
        return StringEscapeUtils.escapeJavaScript( str );
    }

    /**
     * Formats a string to a SQL string.
     *
     * @param str The string to format to a SQL string or {@code null}.
     *
     * @return {@code str} formatted to a SQL string or {@code null}.
     *
     * @see StringEscapeUtils#escapeSql(java.lang.String)
     *
     * @since 1.2
     */
    public String getSqlString( final String str )
    {
        return StringEscapeUtils.escapeSql( str );
    }

    /**
     * Formats a string to a CSV string.
     *
     * @param str The string to format to a CSV string or {@code null}.
     *
     * @return {@code str} formatted to a CSV string or {@code null}.
     *
     * @see StringEscapeUtils#escapeCsv(java.lang.String)
     *
     * @since 1.2
     */
    public String getCsvString( final String str )
    {
        return StringEscapeUtils.escapeCsv( str );
    }

    /**
     * Gets the display language of a given language code.
     *
     * @param language The language code to get the display language of.
     *
     * @return The display language of {@code language}.
     *
     * @throws NullPointerException if {@code language} is {@code null}.
     */
    public String getDisplayLanguage( final String language )
    {
        if ( language == null )
        {
            throw new NullPointerException( "language" );
        }

        final Locale l = new Locale( language );
        return l.getDisplayLanguage( l );
    }

    /**
     * Formats a calendar instance to a string.
     *
     * @param calendar The calendar to format.
     *
     * @return Date of {@code calendar} formatted using a short format style pattern.
     *
     * @throws NullPointerException if {@code calendar} is {@code null}.
     *
     * @see DateFormat#SHORT
     */
    public String getShortDate( final Calendar calendar )
    {
        if ( calendar == null )
        {
            throw new NullPointerException( "calendar" );
        }

        return DateFormat.getDateInstance( DateFormat.SHORT, this.getLocale() ).format( calendar.getTime() );
    }

    /**
     * Formats a calendar instance to a string.
     *
     * @param calendar The calendar to format.
     *
     * @return Date of {@code calendar} formatted using a medium format style pattern.
     *
     * @throws NullPointerException if {@code calendar} is {@code null}.
     *
     * @see DateFormat#MEDIUM
     *
     * @since 1.2
     */
    public String getMediumDate( final Calendar calendar )
    {
        if ( calendar == null )
        {
            throw new NullPointerException( "calendar" );
        }

        return DateFormat.getDateInstance( DateFormat.MEDIUM, this.getLocale() ).format( calendar.getTime() );
    }

    /**
     * Formats a calendar instance to a string.
     *
     * @param calendar The calendar to format.
     *
     * @return Date of {@code calendar} formatted using a long format style pattern.
     *
     * @throws NullPointerException if {@code calendar} is {@code null}.
     *
     * @see DateFormat#LONG
     */
    public String getLongDate( final Calendar calendar )
    {
        if ( calendar == null )
        {
            throw new NullPointerException( "calendar" );
        }

        return DateFormat.getDateInstance( DateFormat.LONG, this.getLocale() ).format( calendar.getTime() );
    }

    /**
     * Formats a calendar instance to a string.
     *
     * @param calendar The calendar to format.
     *
     * @return Time of {@code calendar} formatted using a short format style pattern.
     *
     * @throws NullPointerException if {@code calendar} is {@code null}.
     *
     * @see DateFormat#SHORT
     */
    public String getShortTime( final Calendar calendar )
    {
        if ( calendar == null )
        {
            throw new NullPointerException( "calendar" );
        }

        return DateFormat.getTimeInstance( DateFormat.SHORT, this.getLocale() ).format( calendar.getTime() );
    }

    /**
     * Formats a calendar instance to a string.
     *
     * @param calendar The calendar to format.
     *
     * @return Time of {@code calendar} formatted using a medium format style pattern.
     *
     * @throws NullPointerException if {@code calendar} is {@code null}.
     *
     * @see DateFormat#MEDIUM
     *
     * @since 1.2
     */
    public String getMediumTime( final Calendar calendar )
    {
        if ( calendar == null )
        {
            throw new NullPointerException( "calendar" );
        }

        return DateFormat.getTimeInstance( DateFormat.MEDIUM, this.getLocale() ).format( calendar.getTime() );
    }

    /**
     * Formats a calendar instance to a string.
     *
     * @param calendar The calendar to format.
     *
     * @return Time of {@code calendar} formatted using a long format style pattern.
     *
     * @throws NullPointerException if {@code calendar} is {@code null}.
     *
     * @see DateFormat#LONG
     */
    public String getLongTime( final Calendar calendar )
    {
        if ( calendar == null )
        {
            throw new NullPointerException( "calendar" );
        }

        return DateFormat.getTimeInstance( DateFormat.LONG, this.getLocale() ).format( calendar.getTime() );
    }

    /**
     * Formats a calendar instance to a string.
     *
     * @param calendar The calendar to format.
     *
     * @return Date and time of {@code calendar} formatted using a short format style pattern.
     *
     * @throws NullPointerException if {@code calendar} is {@code null}.
     *
     * @see DateFormat#SHORT
     */
    public String getShortDateTime( final Calendar calendar )
    {
        if ( calendar == null )
        {
            throw new NullPointerException( "calendar" );
        }

        return DateFormat.getDateTimeInstance( DateFormat.SHORT, DateFormat.SHORT, this.getLocale() ).
            format( calendar.getTime() );

    }

    /**
     * Formats a calendar instance to a string.
     *
     * @param calendar The calendar to format.
     *
     * @return Date and time of {@code calendar} formatted using a medium format style pattern.
     *
     * @throws NullPointerException if {@code calendar} is {@code null}.
     *
     * @see DateFormat#MEDIUM
     *
     * @since 1.2
     */
    public String getMediumDateTime( final Calendar calendar )
    {
        if ( calendar == null )
        {
            throw new NullPointerException( "calendar" );
        }

        return DateFormat.getDateTimeInstance( DateFormat.MEDIUM, DateFormat.MEDIUM, this.getLocale() ).
            format( calendar.getTime() );

    }

    /**
     * Formats a calendar instance to a string.
     *
     * @param calendar The calendar to format.
     *
     * @return Date and time of {@code calendar} formatted using a long format style pattern.
     *
     * @throws NullPointerException if {@code calendar} is {@code null}.
     *
     * @see DateFormat#LONG
     */
    public String getLongDateTime( final Calendar calendar )
    {
        if ( calendar == null )
        {
            throw new NullPointerException( "calendar" );
        }

        return DateFormat.getDateTimeInstance( DateFormat.LONG, DateFormat.LONG, this.getLocale() ).
            format( calendar.getTime() );

    }

    /**
     * Gets a string describing the range of years for given calendars.
     *
     * @param start The start of the range.
     * @param end The end of the range.
     *
     * @return Formatted range of the years of {@code start} and {@code end}.
     *
     * @throws NullPointerException if {@code start} or {@code end} is {@code null}.
     */
    public String getYears( final Calendar start, final Calendar end )
    {
        if ( start == null )
        {
            throw new NullPointerException( "start" );
        }
        if ( end == null )
        {
            throw new NullPointerException( "end" );
        }

        final Format yearFormat = new SimpleDateFormat( "yyyy", this.getLocale() );
        final int s = start.get( Calendar.YEAR );
        final int e = end.get( Calendar.YEAR );
        final StringBuilder years = new StringBuilder();

        if ( s != e )
        {
            if ( s < e )
            {
                years.append( yearFormat.format( start.getTime() ) ).append( " - " ).
                    append( yearFormat.format( end.getTime() ) );

            }
            else
            {
                years.append( yearFormat.format( end.getTime() ) ).append( " - " ).
                    append( yearFormat.format( start.getTime() ) );

            }
        }
        else
        {
            years.append( yearFormat.format( start.getTime() ) );
        }

        return years.toString();
    }

    /**
     * Gets the model of the instance.
     *
     * @return The model of the instance.
     *
     * @see #getModules()
     * @see #setModel(org.jomc.modlet.Model)
     */
    public Model getModel()
    {
        if ( this.model == null )
        {
            this.model = new Model();
            this.model.setIdentifier( ModelObject.MODEL_PUBLIC_ID );
        }

        return this.model;
    }

    /**
     * Sets the model of the instance.
     *
     * @param value The new model of the instance.
     *
     * @see #getModel()
     */
    public void setModel( final Model value )
    {
        this.model = value;
    }

    /**
     * Gets the modules of the instance.
     *
     * @return The modules of the instance.
     *
     * @see #getModel()
     * @see #setModel(org.jomc.modlet.Model)
     */
    public Modules getModules()
    {
        Modules modules = ModelHelper.getModules( this.getModel() );

        if ( modules == null )
        {
            modules = new Modules();
            ModelHelper.setModules( this.getModel(), modules );
        }

        return modules;
    }

    /**
     * Gets the {@code VelocityEngine} of the instance.
     *
     * @return The {@code VelocityEngine} of the instance.
     *
     * @throws IOException if initializing a new velocity engine fails.
     *
     * @see #setVelocityEngine(org.apache.velocity.app.VelocityEngine)
     */
    public VelocityEngine getVelocityEngine() throws IOException
    {
        if ( this.velocityEngine == null )
        {
            /** {@code LogChute} logging to the listeners of the tool. */
            class JomcLogChute implements LogChute
            {

                JomcLogChute()
                {
                    super();
                }

                public void init( final RuntimeServices runtimeServices ) throws Exception
                {
                }

                public void log( final int level, final String message )
                {
                    this.log( level, message, null );
                }

                public void log( final int level, final String message, final Throwable throwable )
                {
                    JomcTool.this.log( Level.FINEST, message, throwable );
                }

                public boolean isLevelEnabled( final int level )
                {
                    return isLoggable( Level.FINEST );
                }

            }

            final VelocityEngine engine = new VelocityEngine();
            engine.setProperty( RuntimeConstants.RUNTIME_REFERENCES_STRICT, Boolean.TRUE.toString() );
            engine.setProperty( RuntimeConstants.VM_ARGUMENTS_STRICT, Boolean.TRUE.toString() );
            engine.setProperty( RuntimeConstants.RUNTIME_LOG_LOGSYSTEM, new JomcLogChute() );

            engine.setProperty( RuntimeConstants.RESOURCE_LOADER, "class" );
            engine.setProperty( "class.resource.loader.class", ClasspathResourceLoader.class.getName() );
            engine.setProperty( "class.resource.loader.cache", Boolean.TRUE.toString() );

            if ( this.getTemplateLocation() != null )
            {
                engine.setProperty( RuntimeConstants.RESOURCE_LOADER, "class,url" );
                engine.setProperty( "url.resource.loader.class", URLResourceLoader.class.getName() );
                engine.setProperty( "url.resource.loader.cache", Boolean.TRUE.toString() );
                engine.setProperty( "url.resource.loader.root", this.getTemplateLocation().toExternalForm() );
                engine.setProperty( "url.resource.loader.timeout", Integer.toString( 60000 ) );
            }

            engine.init();
            this.velocityEngine = engine;
        }

        return this.velocityEngine;
    }

    /**
     * Sets the {@code VelocityEngine} of the instance.
     *
     * @param value The new {@code VelocityEngine} of the instance.
     *
     * @see #getVelocityEngine()
     */
    public void setVelocityEngine( final VelocityEngine value )
    {
        this.velocityEngine = value;
    }

    /**
     * Gets a new velocity context used for merging templates.
     *
     * @return A new velocity context used for merging templates.
     *
     * @see #getTemplateParameters()
     */
    public VelocityContext getVelocityContext()
    {
        final Date now = new Date();
        final VelocityContext ctx = new VelocityContext( Collections.synchronizedMap(
            new HashMap<String, Object>( this.getTemplateParameters() ) ) );

        ctx.put( "model", new Model( this.getModel() ) );
        ctx.put( "modules", new Modules( this.getModules() ) );
        ctx.put( "tool", this );
        ctx.put( "toolName", this.getClass().getName() );
        ctx.put( "toolVersion", getMessage( "projectVersion" ) );
        ctx.put( "toolUrl", getMessage( "projectUrl" ) );
        ctx.put( "calendar", Calendar.getInstance() );
        ctx.put( "now", new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss.SSSZ", this.getLocale() ).format( now ) );
        ctx.put( "year", new SimpleDateFormat( "yyyy", this.getLocale() ).format( now ) );
        ctx.put( "month", new SimpleDateFormat( "MM", this.getLocale() ).format( now ) );
        ctx.put( "day", new SimpleDateFormat( "dd", this.getLocale() ).format( now ) );
        ctx.put( "hour", new SimpleDateFormat( "HH", this.getLocale() ).format( now ) );
        ctx.put( "minute", new SimpleDateFormat( "mm", this.getLocale() ).format( now ) );
        ctx.put( "second", new SimpleDateFormat( "ss", this.getLocale() ).format( now ) );
        ctx.put( "timezone", new SimpleDateFormat( "Z", this.getLocale() ).format( now ) );
        return ctx;
    }

    /**
     * Gets the template parameters of the instance.
     * <p>This accessor method returns a reference to the live map, not a snapshot. Therefore any modification you make
     * to the returned map will be present inside the object. This is why there is no {@code set} method for the
     * template parameters property.</p>
     *
     * @return The template parameters of the instance.
     *
     * @since 1.2
     *
     * @see #getVelocityContext()
     */
    public final Map<String, Object> getTemplateParameters()
    {
        if ( this.templateParameters == null )
        {
            this.templateParameters = new HashMap<String, Object>();
        }

        return this.templateParameters;
    }

    /**
     * Gets the location to search for templates in addition to searching the class path.
     *
     * @return The location to search for templates in addition to searching the class path or {@code null}.
     *
     * @see #setTemplateLocation(java.net.URL)
     *
     * @since 1.2
     */
    public final URL getTemplateLocation()
    {
        return this.templateLocation;
    }

    /**
     * Sets the location to search for templates in addition to searching the class path.
     *
     * @param value The new location to search for templates in addition to searching the class path or {@code null}.
     *
     * @see #getTemplateLocation()
     *
     * @since 1.2
     */
    public final void setTemplateLocation( final URL value )
    {
        this.templateLocation = value;
    }

    /**
     * Gets the encoding to use for reading templates.
     *
     * @return The encoding to use for reading templates.
     *
     * @see #setTemplateEncoding(java.lang.String)
     */
    public final String getTemplateEncoding()
    {
        if ( this.templateEncoding == null )
        {
            this.templateEncoding = getMessage( "buildSourceEncoding" );

            if ( this.isLoggable( Level.CONFIG ) )
            {
                this.log( Level.CONFIG, getMessage( "defaultTemplateEncoding", this.templateEncoding ), null );
            }
        }

        return this.templateEncoding;
    }

    /**
     * Sets the encoding to use for reading templates.
     *
     * @param value The new encoding to use for reading templates or {@code null}.
     *
     * @see #getTemplateEncoding()
     */
    public final void setTemplateEncoding( final String value )
    {
        this.templateEncoding = value;
        this.velocityEngine = null;
    }

    /**
     * Gets the encoding to use for reading files.
     *
     * @return The encoding to use for reading files.
     *
     * @see #setInputEncoding(java.lang.String)
     */
    public final String getInputEncoding()
    {
        if ( this.inputEncoding == null )
        {
            this.inputEncoding = new InputStreamReader( new ByteArrayInputStream( NO_BYTES ) ).getEncoding();

            if ( this.isLoggable( Level.CONFIG ) )
            {
                this.log( Level.CONFIG, getMessage( "defaultInputEncoding", this.inputEncoding ), null );
            }
        }

        return this.inputEncoding;
    }

    /**
     * Sets the encoding to use for reading files.
     *
     * @param value The new encoding to use for reading files or {@code null}.
     *
     * @see #getInputEncoding()
     */
    public final void setInputEncoding( final String value )
    {
        this.inputEncoding = value;
    }

    /**
     * Gets the encoding to use for writing files.
     *
     * @return The encoding to use for writing files.
     *
     * @see #setOutputEncoding(java.lang.String)
     */
    public final String getOutputEncoding()
    {
        if ( this.outputEncoding == null )
        {
            this.outputEncoding = new OutputStreamWriter( new ByteArrayOutputStream() ).getEncoding();

            if ( this.isLoggable( Level.CONFIG ) )
            {
                this.log( Level.CONFIG, getMessage( "defaultOutputEncoding", this.outputEncoding ), null );
            }
        }

        return this.outputEncoding;
    }

    /**
     * Sets the encoding to use for writing files.
     *
     * @param value The encoding to use for writing files or {@code null}.
     *
     * @see #getOutputEncoding()
     */
    public final void setOutputEncoding( final String value )
    {
        this.outputEncoding = value;
    }

    /**
     * Gets the default template profile.
     * <p>The default template profile is controlled by system property
     * {@code org.jomc.tools.JomcTool.defaultTemplateProfile} holding the name of the template profile to use by
     * default. If that property is not set, the {@code jomc-java} default is returned.</p>
     *
     * @return The default template profile.
     *
     * @see #setDefaultTemplateProfile(java.lang.String)
     */
    public static String getDefaultTemplateProfile()
    {
        if ( defaultTemplateProfile == null )
        {
            defaultTemplateProfile = System.getProperty( "org.jomc.tools.JomcTool.defaultTemplateProfile",
                                                         DEFAULT_TEMPLATE_PROFILE );

        }

        return defaultTemplateProfile;
    }

    /**
     * Sets the default template profile.
     *
     * @param value The new default template profile or {@code null}.
     *
     * @see #getDefaultTemplateProfile()
     */
    public static void setDefaultTemplateProfile( final String value )
    {
        defaultTemplateProfile = value;
    }

    /**
     * Gets the template profile of the instance.
     *
     * @return The template profile of the instance.
     *
     * @see #getDefaultTemplateProfile()
     * @see #setTemplateProfile(java.lang.String)
     */
    public final String getTemplateProfile()
    {
        if ( this.templateProfile == null )
        {
            this.templateProfile = getDefaultTemplateProfile();

            if ( this.isLoggable( Level.CONFIG ) )
            {
                this.log( Level.CONFIG, getMessage( "defaultTemplateProfile", this.templateProfile ), null );
            }
        }

        return this.templateProfile;
    }

    /**
     * Sets the template profile of the instance.
     *
     * @param value The new template profile of the instance or {@code null}.
     *
     * @see #getTemplateProfile()
     */
    public final void setTemplateProfile( final String value )
    {
        this.templateProfile = value;
    }

    /**
     * Gets the indentation string of the instance.
     *
     * @return The indentation string of the instance.
     *
     * @see #setIndentation(java.lang.String)
     */
    public final String getIndentation()
    {
        if ( this.indentation == null )
        {
            this.indentation = "    ";

            if ( this.isLoggable( Level.CONFIG ) )
            {
                this.log( Level.CONFIG, getMessage( "defaultIndentation",
                                                    StringEscapeUtils.escapeJava( this.indentation ) ), null );

            }
        }

        return this.indentation;
    }

    /**
     * Gets an indentation string for a given indentation level.
     *
     * @param level The indentation level to get an indentation string for.
     *
     * @return The indentation string for {@code level}.
     *
     * @throws IllegalArgumentException if {@code level} is negative.
     *
     * @see #getIndentation()
     */
    public final String getIndentation( final int level )
    {
        if ( level < 0 )
        {
            throw new IllegalArgumentException( Integer.toString( level ) );
        }

        synchronized ( this.indentationCache )
        {
            String idt = this.indentationCache.get( level );

            if ( idt == null )
            {
                final StringBuilder b = new StringBuilder( this.getIndentation().length() * level );

                for ( int i = level; i > 0; i-- )
                {
                    b.append( this.getIndentation() );
                }

                idt = b.toString();
                this.indentationCache.put( level, idt );
            }

            return idt;
        }
    }

    /**
     * Sets the indentation string of the instance.
     *
     * @param value The new indentation string of the instance or {@code null}.
     *
     * @see #getIndentation()
     */
    public final void setIndentation( final String value )
    {
        synchronized ( this.indentationCache )
        {
            this.indentation = value;
            this.indentationCache.clear();
        }
    }

    /**
     * Gets the line separator of the instance.
     *
     * @return The line separator of the instance.
     *
     * @see #setLineSeparator(java.lang.String)
     */
    public final String getLineSeparator()
    {
        if ( this.lineSeparator == null )
        {
            this.lineSeparator = System.getProperty( "line.separator", "\n" );

            if ( this.isLoggable( Level.CONFIG ) )
            {
                this.log( Level.CONFIG, getMessage( "defaultLineSeparator",
                                                    StringEscapeUtils.escapeJava( this.lineSeparator ) ), null );

            }
        }

        return this.lineSeparator;
    }

    /**
     * Sets the line separator of the instance.
     *
     * @param value The new line separator of the instance or {@code null}.
     *
     * @see #getLineSeparator()
     */
    public final void setLineSeparator( final String value )
    {
        this.lineSeparator = value;
    }

    /**
     * Gets the locale of the instance.
     *
     * @return The locale of the instance.
     *
     * @see #setLocale(java.util.Locale)
     */
    public final Locale getLocale()
    {
        if ( this.locale == null )
        {
            this.locale = Locale.ENGLISH;

            if ( this.isLoggable( Level.CONFIG ) )
            {
                this.log( Level.CONFIG, getMessage( "defaultLocale", this.locale ), null );
            }
        }

        return this.locale;
    }

    /**
     * Sets the locale of the instance.
     *
     * @param value The new locale of the instance or {@code null}.
     *
     * @see #getLocale()
     */
    public final void setLocale( final Locale value )
    {
        this.locale = value;
    }

    /**
     * Gets a velocity template for a given name.
     * <p>This method searches templates at the following locations:
     * <ol>
     *  <li><code>org/jomc/tools/templates/{@link #getTemplateProfile() profile}/{@link #getLocale() language}/<i>templateName</i></code></li>
     *  <li><code>org/jomc/tools/templates/{@link #getTemplateProfile() profile}/<i>templateName</i></code></li>
     *  <li><code>org/jomc/tools/templates/{@link #getDefaultTemplateProfile() default profile}/{@link #getLocale() language}/<i>templateName</i></code></li>
     *  <li><code>org/jomc/tools/templates/{@link #getDefaultTemplateProfile() default profile}/<i>templateName</i></code></li>
     * </ol></p>
     *
     * @param templateName The name of the template to get.
     *
     * @return The template matching {@code templateName}.
     *
     * @throws NullPointerException if {@code templateName} is {@code null}.
     * @throws IOException if getting the template fails.
     *
     * @see #getTemplateProfile()
     * @see #getTemplateEncoding()
     * @see #getVelocityEngine()
     */
    public Template getVelocityTemplate( final String templateName ) throws IOException
    {
        if ( templateName == null )
        {
            throw new NullPointerException( "templateName" );
        }

        String location = null;
        Template template = null;

        if ( !StringUtils.EMPTY.equals( this.getLocale().getLanguage() ) )
        {
            location =
                TEMPLATE_PREFIX + this.getTemplateProfile() + "/" + this.getLocale().getLanguage() + "/" + templateName;

            template = this.findVelocityTemplate( location );
        }

        if ( template == null )
        {
            location = TEMPLATE_PREFIX + this.getTemplateProfile() + "/" + templateName;
            template = this.findVelocityTemplate( location );
        }

        if ( template == null && !StringUtils.EMPTY.equals( this.getLocale().getLanguage() ) )
        {
            location = TEMPLATE_PREFIX + getDefaultTemplateProfile() + "/" + this.getLocale().getLanguage() + "/"
                       + templateName;

            template = this.findVelocityTemplate( location );
        }

        if ( template == null )
        {
            location = TEMPLATE_PREFIX + getDefaultTemplateProfile() + "/" + templateName;
            template = this.findVelocityTemplate( location );
        }

        if ( template == null )
        {
            throw new IOException( getMessage( "noSuchTemplate", templateName ) );
        }

        if ( this.isLoggable( Level.FINER ) )
        {
            this.log( Level.FINER, getMessage( "templateInfo", templateName, location ), null );
        }

        return template;
    }

    /**
     * Notifies registered listeners.
     *
     * @param level The level of the event.
     * @param message The message of the event or {@code null}.
     * @param throwable The throwable of the event or {@code null}.
     *
     * @throws NullPointerException if {@code level} is {@code null}.
     *
     * @see #getListeners()
     * @see #isLoggable(java.util.logging.Level)
     */
    public void log( final Level level, final String message, final Throwable throwable )
    {
        if ( level == null )
        {
            throw new NullPointerException( "level" );
        }

        if ( this.isLoggable( level ) )
        {
            for ( Listener l : this.getListeners() )
            {
                l.onLog( level, message, throwable );
            }
        }
    }

    private String getJavaPackageName( final String identifier )
    {
        if ( identifier == null )
        {
            throw new NullPointerException( "identifier" );
        }

        final int idx = identifier.lastIndexOf( '.' );
        return idx != -1 ? identifier.substring( 0, idx ) : "";
    }

    private Template findVelocityTemplate( final String location ) throws IOException
    {
        try
        {
            return this.getVelocityEngine().getTemplate( location, this.getTemplateEncoding() );
        }
        catch ( final ResourceNotFoundException e )
        {
            if ( this.isLoggable( Level.FINER ) )
            {
                this.log( Level.FINER, getMessage( "templateNotFound", location ), null );
            }

            return null;
        }
        catch ( final ParseErrorException e )
        {
            String m = getMessage( e );
            m = m == null ? "" : " " + m;

            throw (IOException) new IOException( getMessage( "invalidTemplate", location, m ) ).initCause( e );
        }
        catch ( final VelocityException e )
        {
            String m = getMessage( e );
            m = m == null ? "" : " " + m;

            throw (IOException) new IOException( getMessage( "velocityException", location, m ) ).initCause( e );
        }
    }

    private static String getMessage( final String key, final Object... arguments )
    {
        return MessageFormat.format( ResourceBundle.getBundle(
            JomcTool.class.getName().replace( '.', '/' ) ).getString( key ), arguments );

    }

    private static String getMessage( final Throwable t )
    {
        return t != null ? t.getMessage() != null ? t.getMessage() : getMessage( t.getCause() ) : null;
    }

}
