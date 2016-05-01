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
package org.jomc.tools;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.text.DateFormat;
import java.text.Format;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
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
import org.jomc.jls.JavaIdentifier;
import org.jomc.model.InheritanceModel;
import org.jomc.model.ModelObject;
import org.jomc.model.Modules;
import org.jomc.model.modlet.ModelHelper;
import org.jomc.modlet.Model;

/**
 * Base tool class.
 *
 * @author <a href="mailto:cs@schulte.it">Christian Schulte</a>
 * @version $JOMC$
 */
public class JomcTool
{

    /**
     * Listener interface.
     */
    public abstract static class Listener
    {

        /**
         * Creates a new {@code Listener} instance.
         */
        public Listener()
        {
            super();
        }

        /**
         * Gets called on logging.
         *
         * @param level The level of the event.
         * @param message The message of the event or {@code null}.
         * @param throwable The throwable of the event or {@code null}.
         *
         * @throws NullPointerException if {@code level} is {@code null}.
         */
        public void onLog( final Level level, final String message, final Throwable throwable )
        {
            if ( level == null )
            {
                throw new NullPointerException( "level" );
            }
        }

    }

    /**
     * Empty byte array.
     */
    private static final byte[] NO_BYTES =
    {
    };

    /**
     * The prefix of the template location.
     */
    private static final String TEMPLATE_PREFIX =
        JomcTool.class.getPackage().getName().replace( '.', '/' ) + "/templates/";

    /**
     * Constant for the name of the template profile property specifying a parent template profile name.
     *
     * @since 1.3
     */
    private static final String PARENT_TEMPLATE_PROFILE_PROPERTY_NAME = "parent-template-profile";

    /**
     * Constant for the name of the template profile property specifying the template encoding.
     *
     * @since 1.3
     */
    private static final String TEMPLATE_ENCODING_PROFILE_PROPERTY_NAME = "template-encoding";

    /**
     * The default encoding to use for reading templates.
     *
     * @since 1.3
     */
    private String defaultTemplateEncoding;

    /**
     * The default template profile.
     */
    private String defaultTemplateProfile;

    /**
     * The log level events are logged at by default.
     *
     * @see #getDefaultLogLevel()
     */
    private static final Level DEFAULT_LOG_LEVEL = Level.WARNING;

    /**
     * The default log level.
     */
    private static volatile Level defaultLogLevel;

    /**
     * The model of the instance.
     */
    private Model model;

    /**
     * The {@code VelocityEngine} of the instance.
     */
    private VelocityEngine velocityEngine;

    /**
     * Flag indicating the default {@code VelocityEngine}.
     *
     * @since 1.2.4
     */
    private boolean defaultVelocityEngine;

    /**
     * The location to search for templates in addition to searching the class path.
     *
     * @since 1.2
     */
    private URL templateLocation;

    /**
     * The encoding to use for reading files.
     */
    private String inputEncoding;

    /**
     * The encoding to use for writing files.
     */
    private String outputEncoding;

    /**
     * The template parameters.
     *
     * @since 1.2
     */
    private Map<String, Object> templateParameters;

    /**
     * The template profile of the instance.
     */
    private String templateProfile;

    /**
     * The indentation string of the instance.
     */
    private String indentation;

    /**
     * The line separator of the instance.
     */
    private String lineSeparator;

    /**
     * The listeners of the instance.
     */
    private List<Listener> listeners;

    /**
     * The log level of the instance.
     */
    private Level logLevel;

    /**
     * The locale of the instance.
     *
     * @since 1.2
     */
    private Locale locale;

    /**
     * Cached indentation strings.
     */
    private volatile Reference<Map<String, String>> indentationCache;

    /**
     * Cached templates.
     *
     * @since 1.3
     */
    private volatile Reference<Map<String, TemplateData>> templateCache;

    /**
     * Cached template profile context properties.
     *
     * @since 1.3
     */
    private volatile Reference<Map<String, java.util.Properties>> templateProfileContextPropertiesCache;

    /**
     * Cached template profile properties.
     *
     * @since 1.3
     */
    private volatile Reference<Map<String, java.util.Properties>> templateProfilePropertiesCache;

    /**
     * Creates a new {@code JomcTool} instance.
     */
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
        this.listeners = tool.listeners != null ? new CopyOnWriteArrayList<Listener>( tool.listeners ) : null;
        this.logLevel = tool.logLevel;
        this.model = tool.model != null ? tool.model.clone() : null;
        this.outputEncoding = tool.outputEncoding;
        this.defaultTemplateEncoding = tool.defaultTemplateEncoding;
        this.defaultTemplateProfile = tool.defaultTemplateProfile;
        this.templateProfile = tool.templateProfile;
        this.velocityEngine = tool.velocityEngine;
        this.defaultVelocityEngine = tool.defaultVelocityEngine;
        this.locale = tool.locale;
        this.templateParameters =
            tool.templateParameters != null
                ? Collections.synchronizedMap( new HashMap<String, Object>( tool.templateParameters ) )
                : null;

        this.templateLocation =
            tool.templateLocation != null ? new URL( tool.templateLocation.toExternalForm() ) : null;

    }

    /**
     * Gets the list of registered listeners.
     * <p>
     * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make
     * to the returned list will be present inside the object. This is why there is no {@code set} method for the
     * listeners property.
     * </p>
     *
     * @return The list of registered listeners.
     *
     * @see #log(java.util.logging.Level, java.lang.String, java.lang.Throwable)
     */
    public List<Listener> getListeners()
    {
        if ( this.listeners == null )
        {
            this.listeners = new CopyOnWriteArrayList<Listener>();
        }

        return this.listeners;
    }

    /**
     * Gets the default log level events are logged at.
     * <p>
     * The default log level is controlled by system property {@code org.jomc.tools.JomcTool.defaultLogLevel} holding
     * the log level to log events at by default. If that property is not set, the {@code WARNING} default is
     * returned.
     * </p>
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
     * @return {@code true}, if messages at {@code level} are provided to the listeners of the instance;
     * {@code false}, if messages at {@code level} are not provided to the listeners of the instance.
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
     * Compiles a string to a Java constant name.
     *
     * @param str The string to compile or {@code null}.
     *
     * @return {@code str} compiled to a {@code JavaIdentifier} or {@code null}, if {@code str} is {@code null}.
     *
     * @throws ParseException if compiling {@code str} to a {@code JavaIdentifier} fails.
     *
     * @since 1.3
     *
     * @see JavaIdentifier#normalize(java.lang.String, org.jomc.util.JavaIdentifier.NormalizationMode)
     * @see org.jomc.util.JavaIdentifier.NormalizationMode#CONSTANT_NAME_CONVENTION
     */
    public JavaIdentifier toJavaConstantName( final String str ) throws ParseException
    {
        JavaIdentifier constantName = null;

        if ( str != null )
        {
            constantName = JavaIdentifier.normalize( str, JavaIdentifier.NormalizationMode.CONSTANT_NAME_CONVENTION );
        }

        return constantName;
    }

    /**
     * Compiles a string to a Java method name.
     *
     * @param str The string to compile or {@code null}.
     *
     * @return {@code str} compiled to a {@code JavaIdentifier} or {@code null}, if {@code str} is {@code null}.
     *
     * @throws ParseException if compiling {@code str} to a {@code JavaIdentifier} fails.
     *
     * @since 1.4
     *
     * @see JavaIdentifier#normalize(java.lang.String, org.jomc.util.JavaIdentifier.NormalizationMode)
     * @see org.jomc.util.JavaIdentifier.NormalizationMode#METHOD_NAME_CONVENTION
     */
    public JavaIdentifier toJavaMethodName( final String str ) throws ParseException
    {
        JavaIdentifier variableName = null;

        if ( str != null )
        {
            variableName =
                JavaIdentifier.normalize( str, JavaIdentifier.NormalizationMode.METHOD_NAME_CONVENTION );

        }

        return variableName;
    }

    /**
     * Compiles a string to a Java variable name.
     *
     * @param str The string to compile or {@code null}.
     *
     * @return {@code str} compiled to a {@code JavaIdentifier} or {@code null}, if {@code str} is {@code null}.
     *
     * @throws ParseException if compiling {@code str} to a {@code JavaIdentifier} fails.
     *
     * @since 1.4
     *
     * @see JavaIdentifier#normalize(java.lang.String, org.jomc.util.JavaIdentifier.NormalizationMode)
     * @see org.jomc.util.JavaIdentifier.NormalizationMode#VARIABLE_NAME_CONVENTION
     */
    public JavaIdentifier toJavaVariableName( final String str ) throws ParseException
    {
        JavaIdentifier variableName = null;

        if ( str != null )
        {
            variableName =
                JavaIdentifier.normalize( str, JavaIdentifier.NormalizationMode.VARIABLE_NAME_CONVENTION );

        }

        return variableName;
    }

    /**
     * Formats a string to a HTML string with HTML entities.
     *
     * @param str The string to format to a HTML string with HTML entities or {@code null}.
     *
     * @return {@code str} formatted to a HTML string with HTML entities or {@code null}.
     *
     * @since 1.2
     */
    public String getHtmlString( final String str )
    {
        return str != null ? str.replace( "&", "&amp;" ).replace( "<", "&lt;" ).replace( ">", "&gt;" ).
            replace( "\"", "&quot;" ).replace( "*", "&lowast;" ) : null;

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
     * Formats a {@code Boolean} to a string.
     *
     * @param b The {@code Boolean} to format to a string or {@code null}.
     *
     * @return {@code b} formatted to a string.
     *
     * @see #getLocale()
     *
     * @since 1.2
     */
    public String getBooleanString( final Boolean b )
    {
        return ResourceBundle.getBundle( JomcTool.class.getName(), this.getLocale() ).
            getString( b ? "booleanStringTrue" : "booleanStringFalse" );

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
     * @param calendar The calendar to format to a string.
     *
     * @return The date of {@code calendar} formatted using a short format style pattern.
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
     * @param calendar The calendar to format to a string.
     *
     * @return The date of {@code calendar} formatted using a medium format style pattern.
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
     * @param calendar The calendar to format to a string.
     *
     * @return The date of {@code calendar} formatted using a long format style pattern.
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
     * @param calendar The calendar to format to a string.
     *
     * @return The date of {@code calendar} formatted using an ISO-8601 format style.
     *
     * @throws NullPointerException if {@code calendar} is {@code null}.
     *
     * @see SimpleDateFormat yyyy-DDD
     *
     * @since 1.2
     */
    public String getIsoDate( final Calendar calendar )
    {
        if ( calendar == null )
        {
            throw new NullPointerException( "calendar" );
        }

        return new SimpleDateFormat( "yyyy-DDD", this.getLocale() ).format( calendar.getTime() );
    }

    /**
     * Formats a calendar instance to a string.
     *
     * @param calendar The calendar to format to a string.
     *
     * @return The time of {@code calendar} formatted using a short format style pattern.
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
     * @param calendar The calendar to format to a string.
     *
     * @return The time of {@code calendar} formatted using a medium format style pattern.
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
     * @param calendar The calendar to format to a string.
     *
     * @return The time of {@code calendar} formatted using a long format style pattern.
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
     * @param calendar The calendar to format to a string.
     *
     * @return The time of {@code calendar} formatted using an ISO-8601 format style.
     *
     * @throws NullPointerException if {@code calendar} is {@code null}.
     *
     * @see SimpleDateFormat HH:mm
     *
     * @since 1.2
     */
    public String getIsoTime( final Calendar calendar )
    {
        if ( calendar == null )
        {
            throw new NullPointerException( "calendar" );
        }

        return new SimpleDateFormat( "HH:mm", this.getLocale() ).format( calendar.getTime() );
    }

    /**
     * Formats a calendar instance to a string.
     *
     * @param calendar The calendar to format to a string.
     *
     * @return The date and time of {@code calendar} formatted using a short format style pattern.
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
     * @param calendar The calendar to format to a string.
     *
     * @return The date and time of {@code calendar} formatted using a medium format style pattern.
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
     * @param calendar The calendar to format to a string.
     *
     * @return The date and time of {@code calendar} formatted using a long format style pattern.
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
     * Formats a calendar instance to a string.
     *
     * @param calendar The calendar to format to a string.
     *
     * @return The date and time of {@code calendar} formatted using a ISO-8601 format style.
     *
     * @throws NullPointerException if {@code calendar} is {@code null}.
     *
     * @see SimpleDateFormat yyyy-MM-dd'T'HH:mm:ssZ
     *
     * @since 1.2
     */
    public String getIsoDateTime( final Calendar calendar )
    {
        if ( calendar == null )
        {
            throw new NullPointerException( "calendar" );
        }

        return Closeable.class.isAssignableFrom( ClassLoader.class )
                   ? new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ssXXX", this.getLocale() ).format( calendar.getTime() )
                   : new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ssZ", this.getLocale() ).format( calendar.getTime() );

    }

    /**
     * Gets a string describing the range of years for given calendars.
     *
     * @param start The start of the range.
     * @param end The end of the range.
     *
     * @return Formatted range of the years of {@code start} and {@code end} (e.g. {@code "start - end"}).
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
    public final Model getModel()
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
     * @param value The new model of the instance or {@code null}.
     *
     * @see #getModel()
     */
    public final void setModel( final Model value )
    {
        this.model = value;
    }

    /**
     * Gets the modules of the model of the instance.
     *
     * @return The modules of the model of the instance or {@code null}, if no modules are found.
     *
     * @see #getModel()
     * @see #setModel(org.jomc.modlet.Model)
     */
    public final Modules getModules()
    {
        return ModelHelper.getModules( this.getModel() );
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
    public final VelocityEngine getVelocityEngine() throws IOException
    {
        if ( this.velocityEngine == null )
        {
            /**
             * {@code LogChute} logging to the listeners of the tool.
             */
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
            engine.setProperty( RuntimeConstants.STRICT_MATH, Boolean.TRUE.toString() );
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

            this.velocityEngine = engine;
            this.defaultVelocityEngine = true;
        }

        return this.velocityEngine;
    }

    /**
     * Sets the {@code VelocityEngine} of the instance.
     *
     * @param value The new {@code VelocityEngine} of the instance or {@code null}.
     *
     * @see #getVelocityEngine()
     */
    public final void setVelocityEngine( final VelocityEngine value )
    {
        this.velocityEngine = value;
        this.defaultVelocityEngine = false;
    }

    /**
     * Gets a new velocity context used for merging templates.
     *
     * @return A new velocity context used for merging templates.
     *
     * @throws IOException if creating a new context instance fails.
     *
     * @see #getTemplateParameters()
     */
    public VelocityContext getVelocityContext() throws IOException
    {
        final Calendar now = Calendar.getInstance();
        final VelocityContext ctx =
            new VelocityContext( new HashMap<String, Object>( this.getTemplateParameters() ) );

        this.mergeTemplateProfileContextProperties( this.getTemplateProfile(), this.getLocale().getLanguage(), ctx );
        this.mergeTemplateProfileContextProperties( this.getTemplateProfile(), null, ctx );

        final Model clonedModel = this.getModel().clone();
        final Modules clonedModules = ModelHelper.getModules( clonedModel );
        assert clonedModules != null : "Unexpected missing modules for model '" + clonedModel.getIdentifier() + "'.";

        ctx.put( "model", clonedModel );
        ctx.put( "modules", clonedModules );
        ctx.put( "imodel", new InheritanceModel( clonedModules ) );
        ctx.put( "tool", this );
        ctx.put( "toolName", this.getClass().getName() );
        ctx.put( "toolVersion", getMessage( "projectVersion" ) );
        ctx.put( "toolUrl", getMessage( "projectUrl" ) );
        ctx.put( "calendar", now.getTime() );
        ctx.put( "now",
                 Closeable.class.isAssignableFrom( ClassLoader.class )
                     ? new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", this.getLocale() ).format( now.getTime() )
                     : new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss.SSSZ", this.getLocale() ).format( now.getTime() ) );

        ctx.put( "year", new SimpleDateFormat( "yyyy", this.getLocale() ).format( now.getTime() ) );
        ctx.put( "month", new SimpleDateFormat( "MM", this.getLocale() ).format( now.getTime() ) );
        ctx.put( "day", new SimpleDateFormat( "dd", this.getLocale() ).format( now.getTime() ) );
        ctx.put( "hour", new SimpleDateFormat( "HH", this.getLocale() ).format( now.getTime() ) );
        ctx.put( "minute", new SimpleDateFormat( "mm", this.getLocale() ).format( now.getTime() ) );
        ctx.put( "second", new SimpleDateFormat( "ss", this.getLocale() ).format( now.getTime() ) );
        ctx.put( "timezone",
                 Closeable.class.isAssignableFrom( ClassLoader.class )
                     ? new SimpleDateFormat( "XXX", this.getLocale() ).format( now.getTime() )
                     : new SimpleDateFormat( "Z", this.getLocale() ).format( now.getTime() ) );

        ctx.put( "shortDate", this.getShortDate( now ) );
        ctx.put( "mediumDate", this.getMediumDate( now ) );
        ctx.put( "longDate", this.getLongDate( now ) );
        ctx.put( "isoDate", this.getIsoDate( now ) );
        ctx.put( "shortTime", this.getShortTime( now ) );
        ctx.put( "mediumTime", this.getMediumTime( now ) );
        ctx.put( "longTime", this.getLongTime( now ) );
        ctx.put( "isoTime", this.getIsoTime( now ) );
        ctx.put( "shortDateTime", this.getShortDateTime( now ) );
        ctx.put( "mediumDateTime", this.getMediumDateTime( now ) );
        ctx.put( "longDateTime", this.getLongDateTime( now ) );
        ctx.put( "isoDateTime", this.getIsoDateTime( now ) );

        return ctx;
    }

    /**
     * Gets the template parameters of the instance.
     * <p>
     * This accessor method returns a reference to the live map, not a snapshot. Therefore any modification you make
     * to the returned map will be present inside the object. This is why there is no {@code set} method for the
     * template parameters property.
     * </p>
     *
     * @return The template parameters of the instance.
     *
     * @see #getVelocityContext()
     *
     * @since 1.2
     */
    public final Map<String, Object> getTemplateParameters()
    {
        if ( this.templateParameters == null )
        {
            this.templateParameters = Collections.synchronizedMap( new HashMap<String, Object>() );
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
        this.templateProfileContextPropertiesCache = null;
        this.templateProfilePropertiesCache = null;

        if ( this.defaultVelocityEngine )
        {
            this.setVelocityEngine( null );
        }
    }

    /**
     * Gets the default encoding used for reading templates.
     *
     * @return The default encoding used for reading templates.
     *
     * @see #setDefaultTemplateEncoding(java.lang.String)
     *
     * @since 1.3
     */
    public final String getDefaultTemplateEncoding()
    {
        if ( this.defaultTemplateEncoding == null )
        {
            this.defaultTemplateEncoding = getMessage( "buildSourceEncoding" );

            if ( this.isLoggable( Level.CONFIG ) )
            {
                this.log( Level.CONFIG, getMessage( "defaultTemplateEncoding", this.defaultTemplateEncoding ), null );
            }
        }

        return this.defaultTemplateEncoding;
    }

    /**
     * Sets the default encoding to use for reading templates.
     *
     * @param value The new default encoding to use for reading templates or {@code null}.
     *
     * @see #getDefaultTemplateEncoding()
     *
     * @since 1.3
     */
    public final void setDefaultTemplateEncoding( final String value )
    {
        this.defaultTemplateEncoding = value;
        this.templateCache = null;
    }

    /**
     * Gets the template encoding of a given template profile.
     *
     * @param tp The template profile to get the template encoding of.
     *
     * @return The template encoding of the template profile identified by {@code tp} or the default template encoding
     * if no such encoding is defined.
     *
     * @throws NullPointerException if {@code tp} is {@code null}.
     *
     * @see #getDefaultTemplateEncoding()
     *
     * @since 1.3
     */
    public final String getTemplateEncoding( final String tp )
    {
        if ( tp == null )
        {
            throw new NullPointerException( "tp" );
        }

        String te = null;

        try
        {
            te = this.getTemplateProfileProperties( tp ).getProperty( TEMPLATE_ENCODING_PROFILE_PROPERTY_NAME );
        }
        catch ( final IOException e )
        {
            if ( this.isLoggable( Level.SEVERE ) )
            {
                this.log( Level.SEVERE, getMessage( e ), e );
            }
        }

        return te != null ? te : this.getDefaultTemplateEncoding();
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
     * <p>
     * The default template profile is the implicit parent profile of any template profile not specifying a parent
     * template profile.
     * </p>
     *
     * @return The default template profile.
     *
     *
     * @see #setDefaultTemplateProfile(java.lang.String)
     */
    public final String getDefaultTemplateProfile()
    {
        if ( this.defaultTemplateProfile == null )
        {
            this.defaultTemplateProfile = "jomc-java";
        }

        return this.defaultTemplateProfile;
    }

    /**
     * Sets the default template profile.
     *
     * @param value The new default template profile or {@code null}.
     *
     * @see #getDefaultTemplateProfile()
     */
    public final void setDefaultTemplateProfile( final String value )
    {
        this.defaultTemplateProfile = value;
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
            this.templateProfile = this.getDefaultTemplateProfile();

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
     * Gets the parent template profile of a given template profile.
     *
     * @param tp The template profile to get the parent template profile of.
     *
     * @return The parent template profile of the template profile identified by {@code tp}; the default template
     * profile, if no such parent template profile is defined; {@code null}, if {@code tp} denotes the default template
     * profile.
     *
     * @throws NullPointerException if {@code tp} is {@code null}.
     *
     * @see #getDefaultTemplateProfile()
     *
     * @since 1.3
     */
    public final String getParentTemplateProfile( final String tp )
    {
        if ( tp == null )
        {
            throw new NullPointerException( "tp" );
        }

        String parentTemplateProfile = null;

        try
        {
            parentTemplateProfile =
                this.getTemplateProfileProperties( tp ).getProperty( PARENT_TEMPLATE_PROFILE_PROPERTY_NAME );

        }
        catch ( final IOException e )
        {
            if ( this.isLoggable( Level.SEVERE ) )
            {
                this.log( Level.SEVERE, getMessage( e ), e );
            }
        }

        return parentTemplateProfile != null ? parentTemplateProfile
                   : tp.equals( this.getDefaultTemplateProfile() ) ? null : this.getDefaultTemplateProfile();

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

        Map<String, String> map = this.indentationCache == null ? null : this.indentationCache.get();

        if ( map == null )
        {
            map = new ConcurrentHashMap<String, String>( 8 );
            this.indentationCache = new SoftReference<Map<String, String>>( map );
        }

        final String key = this.getIndentation() + "|" + level;
        String idt = map.get( key );

        if ( idt == null )
        {
            final StringBuilder b = new StringBuilder( this.getIndentation().length() * level );

            for ( int i = level; i > 0; i-- )
            {
                b.append( this.getIndentation() );
            }

            idt = b.toString();
            map.put( key, idt );
        }

        return idt;
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
        this.indentation = value;
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
     *
     * @since 1.2
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
     *
     * @since 1.2
     */
    public final void setLocale( final Locale value )
    {
        this.locale = value;
    }

    /**
     * Gets a velocity template for a given name.
     * <p>
     * This method searches templates at the following locations recursively in the shown order stopping whenever
     * a matching template is found.
     * <ol>
     * <li><code>org/jomc/tools/templates/{@link #getTemplateProfile() profile}/{@link #getLocale() language}/<i>templateName</i></code></li>
     * <li><code>org/jomc/tools/templates/{@link #getParentTemplateProfile(java.lang.String) parent profile}/{@link #getLocale() language}/<i>templateName</i></code></li>
     * <li><code>org/jomc/tools/templates/{@link #getTemplateProfile() profile}/<i>templateName</i></code></li>
     * <li><code>org/jomc/tools/templates/{@link #getParentTemplateProfile(java.lang.String) parent profile}/{@link #getLocale() language}/<i>templateName</i></code></li>
     * </ol></p>
     *
     * @param templateName The name of the template to get.
     *
     * @return The template matching {@code templateName}.
     *
     * @throws NullPointerException if {@code templateName} is {@code null}.
     * @throws FileNotFoundException if no such template is found.
     * @throws IOException if getting the template fails.
     *
     * @see #getTemplateProfile()
     * @see #getParentTemplateProfile(java.lang.String)
     * @see #getLocale()
     * @see #getTemplateEncoding(java.lang.String)
     * @see #getVelocityEngine()
     */
    public Template getVelocityTemplate( final String templateName ) throws FileNotFoundException, IOException
    {
        if ( templateName == null )
        {
            throw new NullPointerException( "templateName" );
        }

        return this.getVelocityTemplate( this.getTemplateProfile(), templateName );
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
            for ( int i = this.getListeners().size() - 1; i >= 0; i-- )
            {
                this.getListeners().get( i ).onLog( level, message, throwable );
            }
        }
    }

    private Template findVelocityTemplate( final String location, final String encoding ) throws IOException
    {
        try
        {
            return this.getVelocityEngine().getTemplate( location, encoding );
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

            // JDK: As of JDK 6, "new IOException( message, cause )".
            throw (IOException) new IOException( getMessage( "invalidTemplate", location, m ) ).initCause( e );
        }
        catch ( final VelocityException e )
        {
            String m = getMessage( e );
            m = m == null ? "" : " " + m;

            // JDK: As of JDK 6, "new IOException( message, cause )".
            throw (IOException) new IOException( getMessage( "velocityException", location, m ) ).initCause( e );
        }
    }

    private java.util.Properties getTemplateProfileContextProperties( final String profileName, final String language )
        throws IOException
    {
        Map<String, java.util.Properties> map = this.templateProfileContextPropertiesCache == null
                                                    ? null : this.templateProfileContextPropertiesCache.get();

        if ( map == null )
        {
            map = new ConcurrentHashMap<String, java.util.Properties>();
            this.templateProfileContextPropertiesCache = new SoftReference<Map<String, java.util.Properties>>( map );
        }

        final String key = profileName + "|" + language;
        java.util.Properties profileProperties = map.get( key );

        if ( profileProperties == null )
        {
            InputStream in = null;
            URL url = null;
            profileProperties = new java.util.Properties();

            final String resourceName = TEMPLATE_PREFIX + profileName + ( language == null ? "" : "/" + language )
                                            + "/context.properties";

            try
            {
                url = this.getClass().getResource( "/" + resourceName );

                if ( url != null )
                {
                    in = url.openStream();

                    if ( this.isLoggable( Level.CONFIG ) )
                    {
                        this.log( Level.CONFIG, getMessage( "contextPropertiesFound", url.toExternalForm() ), null );
                    }

                    profileProperties.load( in );

                    in.close();
                    in = null;
                }
                else if ( this.getTemplateLocation() != null )
                {
                    if ( this.isLoggable( Level.CONFIG ) )
                    {
                        this.log( Level.CONFIG, getMessage( "contextPropertiesNotFound", resourceName ), null );
                    }

                    url = new URL( this.getTemplateLocation(), resourceName );
                    in = url.openStream();

                    if ( this.isLoggable( Level.CONFIG ) )
                    {
                        this.log( Level.CONFIG, getMessage( "contextPropertiesFound", url.toExternalForm() ), null );
                    }

                    profileProperties.load( in );

                    in.close();
                    in = null;
                }
                else if ( this.isLoggable( Level.CONFIG ) )
                {
                    this.log( Level.CONFIG, getMessage( "contextPropertiesNotFound", resourceName ), null );
                }
            }
            catch ( final FileNotFoundException e )
            {
                if ( this.isLoggable( Level.CONFIG ) )
                {
                    this.log( Level.CONFIG, getMessage( "contextPropertiesNotFound", url.toExternalForm() ), null );
                }
            }
            finally
            {
                map.put( key, profileProperties );

                try
                {
                    if ( in != null )
                    {
                        in.close();
                    }
                }
                catch ( final IOException e )
                {
                    this.log( Level.SEVERE, getMessage( e ), e );
                }
            }
        }

        return profileProperties;
    }

    private void mergeTemplateProfileContextProperties( final String profileName, final String language,
                                                        final VelocityContext velocityContext ) throws IOException
    {
        if ( profileName != null )
        {
            final java.util.Properties templateProfileProperties =
                this.getTemplateProfileContextProperties( profileName, language );

            for ( final Enumeration<?> e = templateProfileProperties.propertyNames(); e.hasMoreElements(); )
            {
                final String name = e.nextElement().toString();
                final String value = templateProfileProperties.getProperty( name );
                final String[] values = value.split( "\\|" );

                if ( !velocityContext.containsKey( name ) )
                {
                    final String className = values[0];

                    try
                    {
                        if ( values.length > 1 )
                        {
                            final Class<?> valueClass = Class.forName( className );
                            velocityContext.put( name,
                                                 valueClass.getConstructor( String.class ).newInstance( values[1] ) );
                        }
                        else if ( value.contains( "|" ) )
                        {
                            velocityContext.put( name, Class.forName( values[0] ).newInstance() );
                        }
                        else
                        {
                            velocityContext.put( name, value );
                        }
                    }
                    catch ( final InstantiationException ex )
                    {
                        // JDK: As of JDK 6, "new IOException( message, cause )".
                        throw (IOException) new IOException( getMessage(
                            "contextPropertiesException", profileName + ( language != null ? ", " + language : "" ) ) ).
                            initCause( ex );

                    }
                    catch ( final IllegalAccessException ex )
                    {
                        // JDK: As of JDK 6, "new IOException( message, cause )".
                        throw (IOException) new IOException( getMessage(
                            "contextPropertiesException", profileName + ( language != null ? ", " + language : "" ) ) ).
                            initCause( ex );

                    }
                    catch ( final InvocationTargetException ex )
                    {
                        // JDK: As of JDK 6, "new IOException( message, cause )".
                        throw (IOException) new IOException( getMessage(
                            "contextPropertiesException", profileName + ( language != null ? ", " + language : "" ) ) ).
                            initCause( ex );

                    }
                    catch ( final NoSuchMethodException ex )
                    {
                        // JDK: As of JDK 6, "new IOException( message, cause )".
                        throw (IOException) new IOException( getMessage(
                            "contextPropertiesException", profileName + ( language != null ? ", " + language : "" ) ) ).
                            initCause( ex );

                    }
                    catch ( final ClassNotFoundException ex )
                    {
                        // JDK: As of JDK 6, "new IOException( message, cause )".
                        throw (IOException) new IOException( getMessage(
                            "contextPropertiesException", profileName + ( language != null ? ", " + language : "" ) ) ).
                            initCause( ex );

                    }
                }
            }

            this.mergeTemplateProfileContextProperties( this.getParentTemplateProfile( profileName ), language,
                                                        velocityContext );

        }
    }

    private java.util.Properties getTemplateProfileProperties( final String profileName ) throws IOException
    {
        Map<String, java.util.Properties> map = this.templateProfilePropertiesCache == null
                                                    ? null : this.templateProfilePropertiesCache.get();

        if ( map == null )
        {
            map = new ConcurrentHashMap<String, java.util.Properties>();
            this.templateProfilePropertiesCache = new SoftReference<Map<String, java.util.Properties>>( map );
        }

        java.util.Properties profileProperties = map.get( profileName );

        if ( profileProperties == null )
        {
            InputStream in = null;
            profileProperties = new java.util.Properties();

            final String resourceName = TEMPLATE_PREFIX + profileName + "/profile.properties";
            URL url = null;

            try
            {
                url = this.getClass().getResource( "/" + resourceName );

                if ( url != null )
                {
                    in = url.openStream();

                    if ( this.isLoggable( Level.CONFIG ) )
                    {
                        this.log( Level.CONFIG, getMessage( "templateProfilePropertiesFound", url.toExternalForm() ),
                                  null );

                    }

                    profileProperties.load( in );

                    in.close();
                    in = null;
                }
                else if ( this.getTemplateLocation() != null )
                {
                    if ( this.isLoggable( Level.CONFIG ) )
                    {
                        this.log( Level.CONFIG, getMessage( "templateProfilePropertiesNotFound", resourceName ), null );
                    }

                    url = new URL( this.getTemplateLocation(), resourceName );
                    in = url.openStream();

                    if ( this.isLoggable( Level.CONFIG ) )
                    {
                        this.log( Level.CONFIG, getMessage( "templateProfilePropertiesFound", url.toExternalForm() ),
                                  null );

                    }

                    profileProperties.load( in );

                    in.close();
                    in = null;
                }
                else if ( this.isLoggable( Level.CONFIG ) )
                {
                    this.log( Level.CONFIG, getMessage( "templateProfilePropertiesNotFound", resourceName ), null );
                }
            }
            catch ( final FileNotFoundException e )
            {
                if ( this.isLoggable( Level.CONFIG ) )
                {
                    this.log( Level.CONFIG, getMessage( "templateProfilePropertiesNotFound", url.toExternalForm() ),
                              null );

                }
            }
            finally
            {
                map.put( profileName, profileProperties );

                try
                {
                    if ( in != null )
                    {
                        in.close();
                    }
                }
                catch ( final IOException e )
                {
                    this.log( Level.SEVERE, getMessage( e ), e );
                }
            }
        }

        return profileProperties;
    }

    private Template getVelocityTemplate( final String tp, final String tn ) throws IOException
    {
        Template template = null;

        if ( tp != null )
        {
            final String key = this.getLocale() + "|" + this.getTemplateProfile() + "|"
                                   + this.getDefaultTemplateProfile() + "|" + tn;

            Map<String, TemplateData> map = this.templateCache == null
                                                ? null : this.templateCache.get();

            if ( map == null )
            {
                map = new ConcurrentHashMap<String, TemplateData>( 32 );
                this.templateCache = new SoftReference<Map<String, TemplateData>>( map );
            }

            TemplateData templateData = map.get( key );

            if ( templateData == null )
            {
                templateData = new TemplateData();

                if ( !StringUtils.EMPTY.equals( this.getLocale().getLanguage() ) )
                {
                    templateData.location = TEMPLATE_PREFIX + tp + "/" + this.getLocale().getLanguage() + "/" + tn;
                    templateData.template =
                        this.findVelocityTemplate( templateData.location, this.getTemplateEncoding( tp ) );

                }

                if ( templateData.template == null )
                {
                    templateData.location = TEMPLATE_PREFIX + tp + "/" + tn;
                    templateData.template =
                        this.findVelocityTemplate( templateData.location, this.getTemplateEncoding( tp ) );

                }

                if ( templateData.template == null )
                {
                    template = this.getVelocityTemplate( this.getParentTemplateProfile( tp ), tn );

                    if ( template == null )
                    {
                        map.put( key, new TemplateData() );
                        throw new FileNotFoundException( getMessage( "noSuchTemplate", tn ) );
                    }
                }
                else
                {
                    if ( this.isLoggable( Level.FINER ) )
                    {
                        this.log( Level.FINER, getMessage( "templateInfo", tn, templateData.location ), null );
                    }

                    template = templateData.template;
                    map.put( key, templateData );
                }
            }
            else if ( templateData.template == null )
            {
                throw new FileNotFoundException( getMessage( "noSuchTemplate", tn ) );
            }
            else
            {
                if ( this.isLoggable( Level.FINER ) )
                {
                    this.log( Level.FINER, getMessage( "templateInfo", tn, templateData.location ), null );
                }

                template = templateData.template;
            }
        }

        return template;
    }

    private static String getMessage( final String key, final Object... arguments )
    {
        return MessageFormat.format( ResourceBundle.getBundle(
            JomcTool.class.getName().replace( '.', '/' ) ).getString( key ), arguments );

    }

    private static String getMessage( final Throwable t )
    {
        return t != null
                   ? t.getMessage() != null && t.getMessage().trim().length() > 0
                         ? t.getMessage()
                         : getMessage( t.getCause() )
                   : null;

    }

    /**
     * @since 1.3
     */
    private static class TemplateData
    {

        private String location;

        private Template template;

    }

}
