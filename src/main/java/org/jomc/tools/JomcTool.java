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
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.log.LogChute;
import org.jomc.model.Argument;
import org.jomc.model.ArgumentType;
import org.jomc.model.DefaultModelManager;
import org.jomc.model.Dependency;
import org.jomc.model.Implementation;
import org.jomc.model.Message;
import org.jomc.model.ModelManager;
import org.jomc.model.Module;
import org.jomc.model.Modules;
import org.jomc.model.Multiplicity;
import org.jomc.model.Properties;
import org.jomc.model.Property;
import org.jomc.model.PropertyType;
import org.jomc.model.Specification;
import org.jomc.model.Text;

/**
 * Base tool class.
 *
 * @author <a href="mailto:cs@schulte.it">Christian Schulte</a>
 * @version $Id$
 */
public abstract class JomcTool
{

    /** Listener interface. */
    public static abstract class Listener
    {

        /**
         * Get called on logging.
         *
         * @param level The level of the event.
         * @param message The message of the event or {@code null}.
         * @param throwable The throwable of the event or {@code null}.
         */
        public abstract void onLog( Level level, String message, Throwable throwable );

    }

    /** The prefix of the template location. */
    private static final String TEMPLATE_PREFIX = "org/jomc/tools/templates/";

    /** Name of the velocity classpath resource loader implementation. */
    private static final String VELOCITY_RESOURCE_LOADER =
        "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader";

    /** The modules of the instance. */
    private Modules modules;

    /** The model manager of the instance. */
    private ModelManager modelManager;

    /** {@code VelocityEngine} of the generator. */
    private VelocityEngine velocityEngine;

    /** The name of the module to process. */
    private String moduleName;

    /** The encoding to use for reading and writing text files. */
    private String encoding;

    /** The profile of the instance. */
    private String profile;

    /** The build directory of the instance. */
    private File buildDirectory;

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
                this.setBuildDirectory( tool.getBuildDirectory() );
                this.setEncoding( tool.getEncoding() );
                this.setModelManager( tool.getModelManager() );
                this.setModuleName( tool.getModuleName() );
                this.setModules( tool.getModules() );
                this.setProfile( tool.getProfile() );
                this.setVelocityEngine( tool.getVelocityEngine() );
                this.getListeners().add( new Listener()
                {

                    @Override
                    public void onLog( final Level level, final String message, final Throwable throwable )
                    {
                        tool.log( level, message, throwable );
                    }

                } );
            }
            catch ( Exception e )
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

        return specification.getIdentifier().substring( 0, specification.getIdentifier().lastIndexOf( '.' ) );
    }

    /**
     * Gets the Java type name of a specification.
     *
     * @param specification The specification to get the Java type name of.
     *
     * @return The Java type name of {@code specification}.
     *
     * @throws NullPointerException if {@code specification} is {@code null}.
     */
    public String getJavaTypeName( final Specification specification )
    {
        if ( specification == null )
        {
            throw new NullPointerException( "specification" );
        }

        return specification.getIdentifier().substring( specification.getIdentifier().lastIndexOf( '.' ) + 1 );
    }

    /**
     * Gets the Java classpath location of a specification.
     *
     * @return specification The specification to return the Java classpath location of.
     *
     * @return the Java classpath location of {@code specification}.
     *
     * @throws NullPointerException if {@code specification} is {@code null}.
     */
    public String getJavaClasspathLocation( final Specification specification )
    {
        if ( specification == null )
        {
            throw new NullPointerException( "specification" );
        }

        return ( this.getJavaPackageName( specification ) + '.' +
                 this.getJavaTypeName( specification ) ).replace( '.', '/' );

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

        return implementation.getClazz().substring( 0, implementation.getClazz().lastIndexOf( '.' ) );
    }

    /**
     * Gets the Java type name of an implementation.
     *
     * @param implementation The implementation to get the Java type name of.
     *
     * @return The Java type name of {@code implementation}.
     *
     * @throws NullPointerException if {@code implementation} is {@code null}.
     */
    public String getJavaTypeName( final Implementation implementation )
    {
        if ( implementation == null )
        {
            throw new NullPointerException( "implementation" );
        }

        return implementation.getClazz().substring( implementation.getClazz().lastIndexOf( '.' ) + 1 );
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
     *
     * @return The Java type name of {@code property}.
     *
     * @throws NullPointerException if {@code property} is {@code null}.
     */
    public String getJavaTypeName( final Property property )
    {
        if ( property == null )
        {
            throw new NullPointerException( "property" );
        }

        switch ( property.getType() )
        {
            case BOOLEAN:
                return Boolean.class.getName();
            case BYTE:
                return Byte.class.getName();
            case CHAR:
                return Character.class.getName();
            case DOUBLE:
                return Double.class.getName();
            case FLOAT:
                return Float.class.getName();
            case INT:
                return Integer.class.getName();
            case LONG:
                return Long.class.getName();
            case SHORT:
                return Short.class.getName();
            default:
                return property.getType().value();

        }
    }

    /**
     * Gets the Java classpath location of an implementation.
     *
     * @return implementation The implementation to return the Java classpath location of.
     *
     * @return the Java classpath location of {@code implementation}.
     *
     * @throws NullPointerException if {@code implementation} is {@code null}.
     */
    public String getJavaClasspathLocation( final Implementation implementation )
    {
        if ( implementation == null )
        {
            throw new NullPointerException( "implementation" );
        }

        return ( this.getJavaPackageName( implementation ) + '.' +
                 this.getJavaTypeName( implementation ) ).replace( '.', '/' );

    }

    /**
     * Formats a text to a Javadoc comment.
     *
     * @param text The text to format to a Javadoc comment.
     * @param linebreak The text to replace linebreaks with.
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
        return normalized;
    }

    /**
     * Formats a string to a Java string with unicode escapes.
     *
     * @param string The string to format to a Java string.
     *
     * @return {@code string} formatted as a Java string.
     *
     * @throws NullPointerException if {@code string} is {@code null}.
     */
    public String getJavaString( final String string )
    {
        if ( string == null )
        {
            throw new NullPointerException( "string" );
        }

        final StringBuffer buf = new StringBuffer( string.length() );
        final int len = string.length();

        for ( int i = 0; i < len; i++ )
        {
            final char c = string.charAt( i );
            if ( ( c > 61 ) && ( c < 127 ) )
            {
                if ( c == '\\' )
                {
                    buf.append( '\\' ).append( '\\' );
                }

                buf.append( c );
                continue;
            }

            switch ( c )
            {
                case '\t':
                    buf.append( '\\' ).append( 't' );
                    break;
                case '\n':
                    buf.append( '\\' ).append( 'n' );
                    break;
                case '\r':
                    buf.append( '\\' ).append( 'r' );
                    break;
                case '\f':
                    buf.append( '\\' ).append( 'f' );
                    break;
                case '"':
                    buf.append( '\\' ).append( c );
                    break;
                default:
                    if ( c < 0x0020 || c > 0x007e )
                    {
                        buf.append( '\\' );
                        buf.append( 'u' );
                        buf.append( toHex( ( c >> 12 ) & 0xF ) );
                        buf.append( toHex( ( c >> 8 ) & 0xF ) );
                        buf.append( toHex( ( c >> 4 ) & 0xF ) );
                        buf.append( toHex( c & 0xF ) );
                    }
                    else
                    {
                        buf.append( c );
                    }

            }
        }

        return buf.toString();
    }

    /**
     * Gets the name of a Java modifier of a dependency of a given implementation.
     *
     * @param implementation The implementation to get a dependency Java modifier name of.
     *
     * @return The Java modifier name of a dependency of {@code implementation}.
     *
     * @throws NullPointerException if {@code implementation} is {@code null}.
     */
    public String getDependencyJavaModifier( final Implementation implementation )
    {
        if ( implementation == null )
        {
            throw new NullPointerException( "implementation" );
        }

        return "private";
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
    public String getDependencyJavaType( final Dependency dependency )
    {
        if ( dependency == null )
        {
            throw new NullPointerException( "dependency" );
        }

        String typeName = dependency.getIdentifier();
        final Specification s = this.getModules().getSpecification( dependency.getIdentifier() );

        if ( s != null && s.getMultiplicity() == Multiplicity.MANY && dependency.getImplementationName() == null )
        {
            typeName += "[]";
        }

        return typeName;
    }

    /**
     * Gets the name of a Java modifier of a message of a given implementation.
     *
     * @param implementation The implementation to get a message Java modifier name of.
     *
     * @return The Java modifier name of a message of {@code implementation}.
     *
     * @throws NullPointerException if {@code implementation} is {@code null}.
     */
    public String getMessageJavaModifier( final Implementation implementation )
    {
        if ( implementation == null )
        {
            throw new NullPointerException( "implementation" );
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
    public String getPropertyJavaModifier( final Implementation implementation, final Property property )
    {
        if ( property == null )
        {
            throw new NullPointerException( "property" );
        }
        if ( implementation == null )
        {
            throw new NullPointerException( "implementation" );
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
     * Gets the name of a Java accessor method of a given dependency.
     *
     * @param dependency The dependency to get a Java accessor method name of.
     *
     * @return The Java accessor method name of {@code dependency}.
     *
     * @throws NullPointerException if {@code dependency} is {@code null}.
     */
    public String getDependencyGetterMethodName( final Dependency dependency )
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
    public String getMessageGetterMethodName( final Message message )
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
     * Gets the name of a Java accessor method of a given property.
     *
     * @param property The property to get a Java accessor method name of.
     *
     * @return The Java accessor method name of {@code property}.
     *
     * @throws NullPointerException if {@code property} is {@code null}.
     */
    public String getPropertyGetterMethodName( final Property property )
    {
        if ( property == null )
        {
            throw new NullPointerException( "property" );
        }

        final char[] name = property.getName().toCharArray();
        name[0] = Character.toUpperCase( name[0] );
        String prefix = "get";
        if ( property.getType() == PropertyType.BOOLEAN || property.getType() == PropertyType.JAVA_LANG_BOOLEAN )
        {
            prefix = "is";
        }

        return prefix + String.valueOf( name );
    }

    /**
     * Gets the display language of a given language code.
     *
     * @param language The language code to get the display language of.
     *
     * @return The display language of {@code language}.
     */
    public String getDisplayLanguage( final String language )
    {
        final Locale locale = new Locale( language );
        return locale.getDisplayLanguage( locale );
    }

    /**
     * Gets the modules of the instance.
     *
     * @return The modules of the instance.
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
     */
    public void setModules( final Modules value )
    {
        this.modules = value;
    }

    /**
     * Gets the model manager of the instance.
     *
     * @return The model manager of the instance.
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
     * @throws Exception if initializing a new velocity engine fails.
     */
    public VelocityEngine getVelocityEngine() throws Exception
    {
        if ( this.velocityEngine == null )
        {
            final java.util.Properties props = new java.util.Properties();
            props.put( "resource.loader", "class" );
            props.put( "class.resource.loader.class", VELOCITY_RESOURCE_LOADER );

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

        return this.velocityEngine;
    }

    /**
     * Sets the {@code VelocityEngine} of the instance.
     *
     * @param value The new {@code VelocityEngine} of the instance.
     */
    public void setVelocityEngine( final VelocityEngine value ) throws Exception
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
        final VelocityContext ctx = new VelocityContext();
        ctx.put( "modules", this.getModules() );
        ctx.put( "module", this.getModule() );
        ctx.put( "tool", this );
        ctx.put( "now", new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss.SSSZ" ).format( new Date() ) );
        return ctx;
    }

    /**
     * Gets the module to process.
     *
     * @return The module to process or {@code null}.
     */
    public Module getModule()
    {
        return this.getModules().getModule( this.getModuleName() );
    }

    /**
     * Gets the name of the module to process.
     *
     * @return The name of the module to process or {@code null}.
     */
    public String getModuleName()
    {
        return this.moduleName;
    }

    /**
     * Sets the name of the module to process.
     *
     * @param value The new name of the module to process or {@code null}.
     */
    public void setModuleName( final String value )
    {
        this.moduleName = value;
    }

    /**
     * Gets the encoding to use for reading and writing text files.
     *
     * @return The encoding to use for reading and writing text files.
     */
    public String getEncoding()
    {
        if ( this.encoding == null )
        {
            this.encoding = "UTF-8";
            this.log( Level.INFO, this.getMessage( "defaultEncoding", new Object[]
                {
                    this.encoding
                } ), null );

        }

        return this.encoding;
    }

    /**
     * Sets the encoding to use for reading and writing text files.
     *
     * @param value The encoding to use for reading and writing text files.
     */
    public void setEncoding( final String value )
    {
        this.encoding = value;
    }

    /**
     * Gets the profile of the instance.
     *
     * @return The profile of the instance.
     */
    public String getProfile()
    {
        if ( this.profile == null )
        {
            this.profile = "default";
            this.log( Level.INFO, this.getMessage( "defaultProfile", new Object[]
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
     */
    public void setProfile( final String value )
    {
        this.profile = value;
    }

    /**
     * Gets the location of the given template.
     *
     * @param template The template to get the location of.
     *
     * @return The location of the template.
     */
    public String getTemplateLocation( final String template )
    {
        return TEMPLATE_PREFIX + this.getProfile() + "/" + template;
    }

    /**
     * Gets the build directory of the instance.
     *
     * @return The build directory of the instance.
     */
    public File getBuildDirectory()
    {
        if ( this.buildDirectory == null )
        {
            this.buildDirectory = new File( System.getProperty( "java.io.tmpdir" ) );
            this.log( Level.INFO, this.getMessage( "defaultBuildDirectory", new Object[]
                {
                    this.buildDirectory.getAbsolutePath()
                } ), null );

        }

        return this.buildDirectory;
    }

    /**
     * Sets the build directory of the instance.
     *
     * @param value The build directory of the instance.
     */
    public void setBuildDirectory( final File value )
    {
        this.buildDirectory = value;
    }

    /**
     * Notifies registered listeners.
     *
     * @param level The level of the event.
     * @param message The message of the event.
     * @param throwable The throwable of the event.
     */
    protected void log( final Level level, final String message, final Throwable throwable )
    {
        for ( Listener l : this.getListeners() )
        {
            l.onLog( level, message, throwable );
        }
    }

    /**
     * Converts a nibble to a hex character.
     *
     * @param nibble The nibble to convert.
     */
    private static char toHex( final int nibble )
    {
        return hexDigit[( nibble & 0xF )];
    }

    /** A table of hex digits */
    private static final char[] hexDigit =
    {
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
    };

    private String getMessage( final String key, final Object args )
    {
        final ResourceBundle b = ResourceBundle.getBundle( "org/jomc/tools/JomcTool" );
        final MessageFormat f = new MessageFormat( b.getString( key ) );
        return f.format( args );
    }

}
