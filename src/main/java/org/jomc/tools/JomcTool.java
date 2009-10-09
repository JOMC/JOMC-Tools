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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.Format;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.log.LogChute;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.jomc.model.Argument;
import org.jomc.model.ArgumentType;
import org.jomc.model.DefaultModelManager;
import org.jomc.model.Dependency;
import org.jomc.model.Implementation;
import org.jomc.model.Message;
import org.jomc.model.ModelManager;
import org.jomc.model.Modules;
import org.jomc.model.Multiplicity;
import org.jomc.model.Properties;
import org.jomc.model.Property;
import org.jomc.model.Specification;
import org.jomc.model.SpecificationReference;
import org.jomc.model.Specifications;
import org.jomc.model.Text;

/**
 * Base tool class.
 *
 * @author <a href="mailto:cs@jomc.org">Christian Schulte</a>
 * @version $Id$
 */
public abstract class JomcTool
{

    /** Listener interface. */
    public interface Listener
    {

        /**
         * Get called on logging.
         *
         * @param level The level of the event.
         * @param message The message of the event or {@code null}.
         * @param throwable The throwable of the event or {@code null}.
         */
        void onLog( Level level, String message, Throwable throwable );

    }

    /** Empty byte array. */
    private static final byte[] NO_BYTES =
    {
    };

    /** The prefix of the template location. */
    private static final String TEMPLATE_PREFIX =
        JomcTool.class.getPackage().getName().replace( '.', '/' ) + "/templates/";

    /** Name of the velocity classpath resource loader implementation. */
    private static final String VELOCITY_RESOURCE_LOADER = ClasspathResourceLoader.class.getName();

    /** The modules of the instance. */
    private Modules modules;

    /** The model manager of the instance. */
    private ModelManager modelManager;

    /** {@code VelocityEngine} of the generator. */
    private VelocityEngine velocityEngine;

    /** The encoding to use for reading templates. */
    private String templateEncoding;

    /** The encoding to use for reading files. */
    private String inputEncoding;

    /** The encoding to use for writing files. */
    private String outputEncoding;

    /** The profile of the instance. */
    private String profile;

    /** The listeners of the instance. */
    private List<Listener> listeners;

    /** Creates a new {@code JomcTool} instance. */
    public JomcTool()
    {
        super();
    }

    /**
     * Creates a new {@code JomcTool} instance taking a {@code JomcTool} instance to initialize the new instance with.
     *
     * @param tool The instance to initialize the new instance with.
     */
    public JomcTool( final JomcTool tool )
    {
        this();
        if ( tool != null )
        {
            try
            {
                this.setTemplateEncoding( tool.getTemplateEncoding() );
                this.setInputEncoding( tool.getInputEncoding() );
                this.setOutputEncoding( tool.getOutputEncoding() );
                this.setModelManager( tool.getModelManager() );
                this.setModules( tool.getModules() );
                this.setProfile( tool.getProfile() );
                this.setVelocityEngine( tool.getVelocityEngine() );
                this.getListeners().addAll( tool.getListeners() );
            }
            catch ( final Exception e )
            {
                this.log( Level.SEVERE, e.getMessage(), e );
            }
        }
    }

    /**
     * Gets the list of registered listeners.
     *
     * @return The list of registered listeners.
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
     * Gets the Java package name of a specification.
     *
     * @param specification The specification to get the Java package name of.
     *
     * @return The Java package name of {@code specification}.
     *
     * @throws NullPointerException if {@code specification} is {@code null}.
     */
    public String getJavaPackageName( final Specification specification )
    {
        if ( specification == null )
        {
            throw new NullPointerException( "specification" );
        }

        return this.getJavaPackageName( specification.getClazz() );
    }

    /**
     * Gets the Java type name of a specification.
     *
     * @param specification The specification to get the Java type name of.
     * @param qualified {@code true} to return the fully qualified type name (with package name prepended);
     * {@code false} to return the short type name (without package name prepended).
     *
     * @return The Java type name of {@code specification}.
     *
     * @throws NullPointerException if {@code specification} is {@code null}.
     */
    public String getJavaTypeName( final Specification specification, final boolean qualified )
    {
        if ( specification == null )
        {
            throw new NullPointerException( "specification" );
        }

        final StringBuilder typeName = new StringBuilder();
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

    /**
     * Gets the Java class path location of a specification.
     *
     * @param specification The specification to return the Java class path location of.
     *
     * @return the Java class path location of {@code specification}.
     *
     * @throws NullPointerException if {@code specification} is {@code null}.
     */
    public String getJavaClasspathLocation( final Specification specification )
    {
        if ( specification == null )
        {
            throw new NullPointerException( "specification" );
        }

        return ( this.getJavaTypeName( specification, true ) ).replace( '.', '/' );
    }

    /**
     * Gets the Java package name of a specification reference.
     *
     * @param reference The specification reference to get the Java package name of.
     *
     * @return The Java package name of {@code reference}.
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
        return this.getJavaPackageName( s );
    }

    /**
     * Gets the name of a Java type of a given specification reference.
     *
     * @param reference The specification reference to get a Java type name of.
     * @param qualified {@code true} to return the fully qualified type name (with package name prepended);
     * {@code false} to return the short type name (without package name prepended).
     *
     * @return The Java type name of {@code reference}.
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
        return this.getJavaTypeName( s, qualified );
    }

    /**
     * Gets the Java package name of an implementation.
     *
     * @param implementation The implementation to get the Java package name of.
     *
     * @return The Java package name of {@code implementation}.
     *
     * @throws NullPointerException if {@code implementation} is {@code null}.
     */
    public String getJavaPackageName( final Implementation implementation )
    {
        if ( implementation == null )
        {
            throw new NullPointerException( "implementation" );
        }

        return this.getJavaPackageName( implementation.getClazz() );
    }

    /**
     * Gets the Java type name of an implementation.
     *
     * @param implementation The implementation to get the Java type name of.
     * @param qualified {@code true} to return the fully qualified type name (with package name prepended);
     * {@code false} to return the short type name (without package name prepended).
     *
     * @return The Java type name of {@code implementation}.
     *
     * @throws NullPointerException if {@code implementation} is {@code null}.
     */
    public String getJavaTypeName( final Implementation implementation, final boolean qualified )
    {
        if ( implementation == null )
        {
            throw new NullPointerException( "implementation" );
        }

        final StringBuilder typeName = new StringBuilder();
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

    /**
     * Gets the Java class path location of an implementation.
     *
     * @param implementation The implementation to return the Java class path location of.
     *
     * @return The Java class path location of {@code implementation}.
     *
     * @throws NullPointerException if {@code implementation} is {@code null}.
     */
    public String getJavaClasspathLocation( final Implementation implementation )
    {
        if ( implementation == null )
        {
            throw new NullPointerException( "implementation" );
        }

        return ( this.getJavaTypeName( implementation, true ) ).replace( '.', '/' );
    }

    /**
     * Gets all Java interfaces an implementation implements.
     *
     * @param implementation The implementation to get all implemented Java interfaces of.
     * @param qualified {@code true} to return the fully qualified type names (with package name prepended);
     * {@code false} to return the short type names (without package name prepended).
     *
     * @return All interfaces implemented by {@code implementation}.
     *
     * @throws NullPointerException if {@code implementation} is {@code null}.
     */
    public List<String> getJavaInterfaceNames( final Implementation implementation, final boolean qualified )
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
                final String typeName = this.getJavaTypeName( s, qualified );
                if ( !col.contains( typeName ) )
                {
                    col.add( typeName );
                }
            }
        }

        return col;
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

        if ( argument.getType() == ArgumentType.DATE || argument.getType() == ArgumentType.TIME )
        {
            return "java.util.Date";
        }
        else if ( argument.getType() == ArgumentType.NUMBER )
        {
            return "java.lang.Number";
        }
        else if ( argument.getType() == ArgumentType.TEXT )
        {
            return "java.lang.String";
        }
        else
        {
            throw new IllegalArgumentException( argument.getType().value() );
        }
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

        if ( property.getAny() != null )
        {
            return Object.class.getName();
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

        return String.class.getName();
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
     * Gets the name of a Java accessor method of a given property.
     *
     * @param property The property to get a Java accessor method name of.
     *
     * @return The Java accessor method name of {@code property}.
     *
     * @throws NullPointerException if {@code property} is {@code null}.
     */
    public String getJavaGetterMethodName( final Property property )
    {
        if ( property == null )
        {
            throw new NullPointerException( "property" );
        }

        final char[] name = property.getName().toCharArray();
        name[0] = Character.toUpperCase( name[0] );
        String prefix = "get";

        final String javaTypeName = this.getJavaTypeName( property, true );
        if ( Boolean.class.getName().equals( javaTypeName ) )
        {
            prefix = "is";
        }

        return prefix + String.valueOf( name );
    }

    /**
     * Gets the name of a Java type of a given dependency.
     *
     * @param dependency The dependency to get a dependency Java type name of.
     *
     * @return The Java type name of {@code dependency}.
     *
     * @throws NullPointerException if {@code dependency} is {@code null}.
     */
    public String getJavaTypeName( final Dependency dependency )
    {
        if ( dependency == null )
        {
            throw new NullPointerException( "dependency" );
        }

        final StringBuilder typeName = new StringBuilder();
        typeName.append( this.getJavaTypeName( (SpecificationReference) dependency, true ) );

        final Specification s = this.getModules().getSpecification( dependency.getIdentifier() );
        if ( s != null && s.getMultiplicity() == Multiplicity.MANY && dependency.getImplementationName() == null )
        {
            typeName.append( "[]" );
        }

        return typeName.toString();
    }

    /**
     * Gets the name of a Java accessor method of a given dependency.
     *
     * @param dependency The dependency to get a Java accessor method name of.
     *
     * @return The Java accessor method name of {@code dependency}.
     *
     * @throws NullPointerException if {@code dependency} is {@code null}.
     */
    public String getJavaGetterMethodName( final Dependency dependency )
    {
        if ( dependency == null )
        {
            throw new NullPointerException( "dependency" );
        }

        final char[] name = dependency.getName().toCharArray();
        name[0] = Character.toUpperCase( name[0] );
        return "get" + String.valueOf( name );
    }

    /**
     * Gets the name of a Java accessor method of a given message.
     *
     * @param message The message to get a Java accessor method name of.
     *
     * @return The Java accessor method name of {@code message}.
     *
     * @throws NullPointerException if {@code message} is {@code null}.
     */
    public String getJavaGetterMethodName( final Message message )
    {
        if ( message == null )
        {
            throw new NullPointerException( "message" );
        }

        final char[] name = message.getName().toCharArray();
        name[0] = Character.toUpperCase( name[0] );
        return "get" + String.valueOf( name ) + "Message";
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
     * @param linebreak The text to replace line breaks with.
     *
     * @return {@code text} formatted as a Javadoc comment.
     *
     * @throws NullPointerException if {@code text} or {@code linebreak} is {@code null}.
     */
    public String getJavadocComment( final Text text, final String linebreak )
    {
        if ( text == null )
        {
            throw new NullPointerException( "text" );
        }
        if ( linebreak == null )
        {
            throw new NullPointerException( "linebreak" );
        }

        String normalized = text.getValue();
        normalized = normalized.replaceAll( "\\/\\*\\*", "/*" );
        normalized = normalized.replaceAll( "\\*/", "/" );
        normalized = normalized.replaceAll( "\n", "\n" + linebreak );
        return StringEscapeUtils.escapeHtml( normalized );
    }

    /**
     * Formats a string to a Java string with unicode escapes.
     *
     * @param str The string to format to a Java string or {@code null}.
     *
     * @return {@code str} formatted as a Java string or {@code null}.
     */
    public String getJavaString( final String str )
    {
        return StringEscapeUtils.escapeJava( str );
    }

    /**
     * Gets a flag indicating if a given specification declares a Java class.
     *
     * @param specification The specification to test.
     *
     * @return {@code true} if {@code specification} is declaring the Java class with name
     * {@code specification.getClazz()}; {@code false} if {@code specification} does not declare that class.
     *
     * @throws NullPointerException if {@code specification} is {@code null}.
     */
    public boolean isJavaClassDeclaration( final Specification specification )
    {
        if ( specification == null )
        {
            throw new NullPointerException( "specification" );
        }

        return specification.getClazz() != null && specification.getClazz().equals( specification.getIdentifier() );
    }

    /**
     * Gets a flag indicating if a given implementation declares a Java class.
     *
     * @param implementation The implementation to test.
     *
     * @return {@code true} if {@code implementation} is declaring the Java class with name
     * {@code implementation.getClazz()}; {@code false} if {@code implementation.getClazz()} is {@code null} or
     * {@code implementation} does not declare that class.
     *
     * @throws NullPointerException if {@code implementation} is {@code null}.
     */
    public boolean isJavaClassDeclaration( final Implementation implementation )
    {
        if ( implementation == null )
        {
            throw new NullPointerException( "implementation" );
        }

        return implementation.getClazz() != null && implementation.getClazz().equals( implementation.getIdentifier() );
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

        return this.getJavaPackageName( specification ).length() == 0;
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

        return this.getJavaPackageName( implementation ).length() == 0;
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

        final Locale locale = new Locale( language );
        return locale.getDisplayLanguage( locale );
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

        return DateFormat.getDateInstance( DateFormat.SHORT ).format( calendar.getTime() );
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

        return DateFormat.getDateInstance( DateFormat.LONG ).format( calendar.getTime() );
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

        return DateFormat.getTimeInstance( DateFormat.SHORT ).format( calendar.getTime() );
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

        return DateFormat.getTimeInstance( DateFormat.LONG ).format( calendar.getTime() );
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

        return DateFormat.getDateTimeInstance( DateFormat.SHORT, DateFormat.SHORT ).format( calendar.getTime() );
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

        return DateFormat.getDateTimeInstance( DateFormat.LONG, DateFormat.LONG ).format( calendar.getTime() );
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

        final Format yearFormat = new SimpleDateFormat( "yyyy" );
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
     * Gets the modules of the instance.
     *
     * @return The modules of the instance.
     *
     * @see #setModules(org.jomc.model.Modules)
     */
    public Modules getModules()
    {
        if ( this.modules == null )
        {
            this.modules = new Modules();
        }

        return this.modules;
    }

    /**
     * Sets the modules of the instance.
     *
     * @param value The new modules of the instance.
     *
     * @see #getModules()
     */
    public void setModules( final Modules value )
    {
        this.modules = value;
    }

    /**
     * Gets the model manager of the instance.
     *
     * @return The model manager of the instance.
     *
     * @see #setModelManager(org.jomc.model.ModelManager)
     */
    public ModelManager getModelManager()
    {
        if ( this.modelManager == null )
        {
            this.modelManager = new DefaultModelManager();
        }

        return this.modelManager;
    }

    /**
     * Sets the model manager of the instance.
     *
     * @param value The new model manager of the instance.
     *
     * @see #getModelManager()
     */
    public void setModelManager( final ModelManager value )
    {
        this.modelManager = value;
    }

    /**
     * Gets the {@code VelocityEngine} used for generating source code.
     *
     * @return The {@code VelocityEngine} used for generating source code.
     *
     * @throws IllegalStateException if initializing a new velocity engine fails.
     *
     * @see #setVelocityEngine(org.apache.velocity.app.VelocityEngine)
     */
    public VelocityEngine getVelocityEngine()
    {
        if ( this.velocityEngine == null )
        {
            try
            {
                final java.util.Properties props = new java.util.Properties();
                props.put( "resource.loader", "class" );
                props.put( "class.resource.loader.class", VELOCITY_RESOURCE_LOADER );
                props.put( "runtime.references.strict", Boolean.TRUE.toString() );

                final VelocityEngine engine = new VelocityEngine();
                engine.setProperty( RuntimeConstants.RUNTIME_LOG_LOGSYSTEM, new LogChute()
                {

                    public void init( final RuntimeServices runtimeServices ) throws Exception
                    {
                    }

                    public void log( final int level, final String message )
                    {
                        this.log( level, message, null );
                    }

                    public void log( final int level, final String message, final Throwable throwable )
                    {
                        JomcTool.this.log( this.toLevel( level ), message, throwable );
                    }

                    public boolean isLevelEnabled( final int level )
                    {
                        return true;
                    }

                    private Level toLevel( final int logChuteLevel )
                    {
                        switch ( logChuteLevel )
                        {
                            case LogChute.DEBUG_ID:
                                return Level.FINE;

                            case LogChute.ERROR_ID:
                                return Level.SEVERE;

                            case LogChute.INFO_ID:
                                return Level.INFO;

                            case LogChute.TRACE_ID:
                                return Level.FINER;

                            case LogChute.WARN_ID:
                                return Level.WARNING;

                            default:
                                return Level.FINEST;

                        }
                    }

                } );

                engine.init( props );
                this.velocityEngine = engine;
            }
            catch ( final Exception e )
            {
                throw new IllegalStateException( e );
            }
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
     * Gets the velocity context used for merging templates.
     *
     * @return The velocity context used for merging templates.
     */
    public VelocityContext getVelocityContext()
    {
        final Date now = new Date();
        final VelocityContext ctx = new VelocityContext();
        ctx.put( "modules", this.getModules() );
        ctx.put( "tool", this );
        ctx.put( "calendar", Calendar.getInstance() );
        ctx.put( "now", new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss.SSSZ" ).format( now ) );
        ctx.put( "year", new SimpleDateFormat( "yyyy" ).format( now ) );
        ctx.put( "month", new SimpleDateFormat( "MM" ).format( now ) );
        ctx.put( "day", new SimpleDateFormat( "dd" ).format( now ) );
        ctx.put( "hour", new SimpleDateFormat( "HH" ).format( now ) );
        ctx.put( "minute", new SimpleDateFormat( "mm" ).format( now ) );
        ctx.put( "second", new SimpleDateFormat( "ss" ).format( now ) );
        ctx.put( "timezone", new SimpleDateFormat( "Z" ).format( now ) );
        return ctx;
    }

    /**
     * Gets the encoding to use for reading templates.
     *
     * @return The encoding to use for reading templates.
     *
     * @see #setTemplateEncoding(java.lang.String)
     */
    public String getTemplateEncoding()
    {
        if ( this.templateEncoding == null )
        {
            this.templateEncoding = this.getMessage( "buildSourceEncoding", null );
        }

        return this.templateEncoding;
    }

    /**
     * Sets the encoding to use for reading templates.
     *
     * @param value The encoding to use for reading templates.
     *
     * @see #getTemplateEncoding()
     */
    public void setTemplateEncoding( final String value )
    {
        this.templateEncoding = value;
    }

    /**
     * Gets the encoding to use for reading files.
     *
     * @return The encoding to use for reading files.
     *
     * @see #setInputEncoding(java.lang.String)
     */
    public String getInputEncoding()
    {
        if ( this.inputEncoding == null )
        {
            this.inputEncoding = new InputStreamReader( new ByteArrayInputStream( NO_BYTES ) ).getEncoding();
            this.log( Level.FINE, this.getMessage( "defaultInputEncoding", new Object[]
                {
                    this.inputEncoding
                } ), null );

        }

        return this.inputEncoding;
    }

    /**
     * Sets the encoding to use for reading files.
     *
     * @param value The encoding to use for reading files.
     *
     * @see #getInputEncoding()
     */
    public void setInputEncoding( final String value )
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
    public String getOutputEncoding()
    {
        if ( this.outputEncoding == null )
        {
            this.outputEncoding = new OutputStreamWriter( new ByteArrayOutputStream() ).getEncoding();
            this.log( Level.FINE, this.getMessage( "defaultOutputEncoding", new Object[]
                {
                    this.outputEncoding
                } ), null );

        }

        return this.outputEncoding;
    }

    /**
     * Sets the encoding to use for writing files.
     *
     * @param value The encoding to use for writing files.
     *
     * @see #getOutputEncoding()
     */
    public void setOutputEncoding( final String value )
    {
        this.outputEncoding = value;
    }

    /**
     * Gets the profile of the instance.
     *
     * @return The profile of the instance.
     *
     * @see #setProfile(java.lang.String)
     */
    public String getProfile()
    {
        if ( this.profile == null )
        {
            this.profile = "default";
            this.log( Level.FINE, this.getMessage( "defaultProfile", new Object[]
                {
                    this.profile
                } ), null );

        }

        return this.profile;
    }

    /**
     * Sets the profile of the instance.
     *
     * @param value The profile of the instance.
     *
     * @see #getProfile()
     */
    public void setProfile( final String value )
    {
        this.profile = value;
    }

    /**
     * Gets a velocity template for a given name.
     * <p>This method returns the template corresponding to the profile of the instance. If that template is not found,
     * the template corresponding to the default profile is returned so that only templates differing from the default
     * templates need to be provided when exchanging templates.</p>
     *
     * @param templateName The name of the template to get.
     *
     * @return The template matching {@code templateName}.
     *
     * @throws NullPointerException if {@code templateName} is {@code null}.
     * @throws IOException if getting the template fails.
     *
     * @see #getProfile()
     * @see #getTemplateEncoding()
     */
    public Template getVelocityTemplate( final String templateName ) throws IOException
    {
        if ( templateName == null )
        {
            throw new NullPointerException( "templateName" );
        }

        try
        {
            return this.getVelocityEngine().getTemplate( TEMPLATE_PREFIX + this.getProfile() + "/" + templateName,
                                                         this.getTemplateEncoding() );

        }
        catch ( final ResourceNotFoundException e )
        {
            this.log( Level.CONFIG, this.getMessage( "templateNotFound", new Object[]
                {
                    templateName, TEMPLATE_PREFIX + this.getProfile() + "/" + templateName
                } ), e );

            try
            {
                return this.getVelocityEngine().getTemplate( TEMPLATE_PREFIX + "default/" + templateName,
                                                             this.getTemplateEncoding() );

            }
            catch ( final Exception e2 )
            {
                throw (IOException) new IOException( e2.getMessage() ).initCause( e2 );
            }
        }
        catch ( final Exception e )
        {
            throw (IOException) new IOException( e.getMessage() ).initCause( e );
        }
    }

    /**
     * Notifies registered listeners.
     *
     * @param level The level of the event.
     * @param message The message of the event.
     * @param throwable The throwable of the event.
     *
     * @see #getListeners()
     */
    protected void log( final Level level, final String message, final Throwable throwable )
    {
        for ( Listener l : this.getListeners() )
        {
            l.onLog( level, message, throwable );
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

    private String getMessage( final String key, final Object args )
    {
        if ( key == null )
        {
            throw new NullPointerException( "key" );
        }

        final ResourceBundle b = ResourceBundle.getBundle( JomcTool.class.getName().replace( '.', '/' ) );
        return args == null ? b.getString( key ) : new MessageFormat( b.getString( key ) ).format( args );
    }

}
