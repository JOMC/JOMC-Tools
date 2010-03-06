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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;
import org.jomc.model.Implementation;
import org.jomc.model.Message;
import org.jomc.model.Messages;
import org.jomc.model.Module;
import org.jomc.model.Text;

/**
 * Manages resource files.
 *
 * <p><b>Use cases</b><br/><ul>
 * <li>{@link #writeResourceBundleResourceFiles(java.io.File) }</li>
 * <li>{@link #writeResourceBundleResourceFiles(org.jomc.model.Module, java.io.File) }</li>
 * <li>{@link #writeResourceBundleResourceFiles(org.jomc.model.Implementation, java.io.File) }</li>
 * </ul></p>
 *
 * @author <a href="mailto:schulte2005@users.sourceforge.net">Christian Schulte</a>
 * @version $Id$
 *
 * @see #getModules()
 */
public class ResourceFileProcessor extends JomcTool
{

    /** Name of the generator. */
    private static final String GENERATOR_NAME = ResourceFileProcessor.class.getName();

    /** Constant for the version of the generator. */
    private static final String GENERATOR_VERSION = "1.0";

    /** The language of the default language properties file of generated resource bundle resources. */
    private Locale resourceBundleDefaultLocale;

    /** Creates a new {@code ResourceFileProcessor} instance. */
    public ResourceFileProcessor()
    {
        super();
    }

    /**
     * Creates a new {@code ResourceFileProcessor} instance taking a {@code ResourceFileProcessor} instance to
     * initialize the instance with.
     *
     * @param tool The instance to initialize the new instance with.
     *
     * @throws NullPointerException if {@code tool} is {@code null}.
     * @throws ToolException if copying {@code tool} fails.
     */
    public ResourceFileProcessor( final ResourceFileProcessor tool ) throws ToolException
    {
        super( tool );
        this.setResourceBundleDefaultLocale( tool.getResourceBundleDefaultLocale() );
    }

    /**
     * Gets the language of the default language properties file of generated resource bundle resource files.
     *
     * @return The language of the default language properties file of generated resource bundle resource files.
     *
     * @see #setResourceBundleDefaultLocale(java.util.Locale)
     */
    public Locale getResourceBundleDefaultLocale()
    {
        if ( this.resourceBundleDefaultLocale == null )
        {
            this.resourceBundleDefaultLocale = Locale.getDefault();

            if ( this.isLoggable( Level.CONFIG ) )
            {
                this.log( Level.CONFIG, getMessage( "resourceBundleDefaultLocale", this.resourceBundleDefaultLocale ),
                          null );

            }
        }

        return this.resourceBundleDefaultLocale;
    }

    /**
     * Sets the language of the default language properties file of generated resource bundle resource files.
     *
     * @param value The language of the default language properties file of generated resource bundle resource files.
     *
     * @see #getResourceBundleDefaultLocale()
     */
    public void setResourceBundleDefaultLocale( final Locale value )
    {
        this.resourceBundleDefaultLocale = value;
    }

    /**
     * Writes resource bundle resource files of the modules of the instance to a given directory.
     *
     * @param resourcesDirectory The directory to write resource bundle resource files to.
     *
     * @throws NullPointerException if {@code resourcesDirectory} is {@code null}.
     * @throws ToolException if writing resource bundle resource files fails.
     *
     * @see #writeResourceBundleResourceFiles(org.jomc.model.Module, java.io.File)
     */
    public void writeResourceBundleResourceFiles( final File resourcesDirectory ) throws ToolException
    {
        if ( resourcesDirectory == null )
        {
            throw new NullPointerException( "resourcesDirectory" );
        }

        for ( Module m : this.getModules().getModule() )
        {
            this.writeResourceBundleResourceFiles( m, resourcesDirectory );
        }
    }

    /**
     * Writes resource bundle resource files of a given module from the modules of the instance to a given directory.
     *
     * @param module The module to process.
     * @param resourcesDirectory The directory to write resource bundle resource files to.
     *
     * @throws NullPointerException if {@code module} or {@code resourcesDirectory} is {@code null}.
     * @throws ToolException if writing resource bundle resource files fails.
     *
     * @see #writeResourceBundleResourceFiles(org.jomc.model.Implementation, java.io.File)
     */
    public void writeResourceBundleResourceFiles( final Module module, final File resourcesDirectory )
        throws ToolException
    {
        if ( module == null )
        {
            throw new NullPointerException( "module" );
        }
        if ( resourcesDirectory == null )
        {
            throw new NullPointerException( "resourcesDirectory" );
        }

        if ( module.getImplementations() != null )
        {
            for ( Implementation i : module.getImplementations().getImplementation() )
            {
                this.writeResourceBundleResourceFiles( i, resourcesDirectory );
            }
        }
    }

    /**
     * Writes resource bundle resource files of a given implementation from the modules of the instance to a directory.
     *
     * @param implementation The implementation to process.
     * @param resourcesDirectory The directory to write resource bundle resource files to.
     *
     * @throws NullPointerException if {@code implementation} or {@code resourcesDirectory} is {@code null}.
     * @throws ToolException if writing resource bundle resource files fails.
     *
     * @see #getResourceBundleResources(org.jomc.model.Implementation)
     */
    public void writeResourceBundleResourceFiles( final Implementation implementation, final File resourcesDirectory )
        throws ToolException
    {
        if ( implementation == null )
        {
            throw new NullPointerException( "implementation" );
        }
        if ( resourcesDirectory == null )
        {
            throw new NullPointerException( "resourcesDirectory" );
        }

        try
        {
            if ( implementation.isClassDeclaration() )
            {
                this.assertValidTemplates( implementation );

                final String bundlePath =
                    this.getJavaTypeName( implementation, true ).replace( '.', File.separatorChar );

                Properties defProperties = null;
                Properties fallbackProperties = null;

                for ( Map.Entry<Locale, Properties> e : this.getResourceBundleResources( implementation ).entrySet() )
                {
                    final String language = e.getKey().getLanguage().toLowerCase();
                    final java.util.Properties p = e.getValue();
                    final File file = new File( resourcesDirectory, bundlePath + "_" + language + ".properties" );

                    if ( !file.getParentFile().exists() && !file.getParentFile().mkdirs() )
                    {
                        throw new ToolException( getMessage( "failedCreatingDirectory",
                                                             file.getParentFile().getAbsolutePath() ) );

                    }

                    if ( this.isLoggable( Level.INFO ) )
                    {
                        this.log( Level.INFO, getMessage( "writing", file.getCanonicalPath() ), null );
                    }

                    OutputStream out = null;
                    try
                    {
                        out = new FileOutputStream( file );
                        p.store( out, GENERATOR_NAME + ' ' + GENERATOR_VERSION );
                    }
                    finally
                    {
                        if ( out != null )
                        {
                            out.close();
                        }
                    }

                    if ( this.getResourceBundleDefaultLocale().getLanguage().equalsIgnoreCase( language ) )
                    {
                        defProperties = p;
                    }

                    fallbackProperties = p;
                }

                if ( defProperties == null )
                {
                    defProperties = fallbackProperties;
                }

                if ( defProperties != null )
                {
                    final File file = new File( resourcesDirectory, bundlePath + ".properties" );
                    if ( !file.getParentFile().exists() && !file.getParentFile().mkdirs() )
                    {
                        throw new ToolException( getMessage( "failedCreatingDirectory",
                                                             file.getParentFile().getAbsolutePath() ) );

                    }

                    if ( this.isLoggable( Level.INFO ) )
                    {
                        this.log( Level.INFO, getMessage( "writing", file.getCanonicalPath() ), null );
                    }

                    OutputStream out = null;
                    try
                    {
                        out = new FileOutputStream( file );
                        defProperties.store( out, GENERATOR_NAME + ' ' + GENERATOR_VERSION );
                    }
                    finally
                    {
                        if ( out != null )
                        {
                            out.close();
                        }
                    }
                }
            }
        }
        catch ( final IOException e )
        {
            throw new ToolException( e.getMessage(), e );
        }
    }

    /**
     * Gets resource bundle properties resources of a given implementation.
     *
     * @param implementation The implementation to get resource bundle properties resources of.
     *
     * @return Resource bundle properties resources of {@code implementation}.
     *
     * @throws NullPointerException if {@code implementation} is {@code null}.
     * @throws ToolException if getting the resource bundle properties resources fails.
     */
    public Map<Locale, Properties> getResourceBundleResources( final Implementation implementation )
        throws ToolException
    {
        if ( implementation == null )
        {
            throw new NullPointerException( "implementation" );
        }

        final Map<Locale, java.util.Properties> properties = new HashMap<Locale, java.util.Properties>( 10 );
        final Messages messages = this.getModules().getMessages( implementation.getIdentifier() );

        if ( messages != null )
        {
            for ( Message message : messages.getMessage() )
            {
                if ( message.getTemplate() != null )
                {
                    for ( Text text : message.getTemplate().getText() )
                    {
                        final Locale locale = new Locale( text.getLanguage().toLowerCase() );
                        Properties bundleProperties = properties.get( locale );

                        if ( bundleProperties == null )
                        {
                            bundleProperties = new Properties();
                            properties.put( locale, bundleProperties );
                        }

                        bundleProperties.setProperty( message.getName(), text.getValue() );
                    }
                }
            }
        }

        return properties;
    }

    private void assertValidTemplates( final Implementation implementation )
    {
        if ( implementation == null )
        {
            throw new NullPointerException( "implementation" );
        }

        final Messages messages = this.getModules().getMessages( implementation.getIdentifier() );

        if ( messages != null )
        {
            for ( Message m : messages.getMessage() )
            {
                if ( m.getTemplate() != null )
                {
                    for ( Text t : m.getTemplate().getText() )
                    {
                        new MessageFormat( t.getValue() );
                    }
                }
            }
        }
    }

    private static String getMessage( final String key, final Object... arguments )
    {
        if ( key == null )
        {
            throw new NullPointerException( "key" );
        }

        return MessageFormat.format( ResourceBundle.getBundle(
            ResourceFileProcessor.class.getName().replace( '.', '/' ) ).getString( key ), arguments );

    }

}
