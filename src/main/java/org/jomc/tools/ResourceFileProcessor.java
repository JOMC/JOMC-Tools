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
import org.apache.velocity.VelocityContext;
import org.jomc.model.Implementation;
import org.jomc.model.Message;
import org.jomc.model.Messages;
import org.jomc.model.Module;
import org.jomc.model.Specification;
import org.jomc.model.Text;

/**
 * Processes resource files.
 *
 * <p><b>Use cases</b><br/><ul>
 * <li>{@link #writeResourceBundleResourceFiles(File) }</li>
 * <li>{@link #writeResourceBundleResourceFiles(Module, File) }</li>
 * <li>{@link #writeResourceBundleResourceFiles(Specification, File) }</li>
 * <li>{@link #writeResourceBundleResourceFiles(Implementation, File) }</li>
 * </ul></p>
 *
 * @author <a href="mailto:schulte2005@users.sourceforge.net">Christian Schulte</a>
 * @version $Id$
 *
 * @see #getModules()
 */
public class ResourceFileProcessor extends JomcTool
{

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
     * @throws IOException if copying {@code tool} fails.
     */
    public ResourceFileProcessor( final ResourceFileProcessor tool ) throws IOException
    {
        super( tool );
        this.resourceBundleDefaultLocale = tool.resourceBundleDefaultLocale;
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
     * @throws IOException if writing resource bundle resource files fails.
     *
     * @see #writeResourceBundleResourceFiles(org.jomc.model.Module, java.io.File)
     */
    public void writeResourceBundleResourceFiles( final File resourcesDirectory ) throws IOException
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
     * @throws IOException if writing resource bundle resource files fails.
     *
     * @see #writeResourceBundleResourceFiles(org.jomc.model.Specification, java.io.File)
     * @see #writeResourceBundleResourceFiles(org.jomc.model.Implementation, java.io.File)
     */
    public void writeResourceBundleResourceFiles( final Module module, final File resourcesDirectory )
        throws IOException
    {
        if ( module == null )
        {
            throw new NullPointerException( "module" );
        }
        if ( resourcesDirectory == null )
        {
            throw new NullPointerException( "resourcesDirectory" );
        }

        if ( module.getSpecifications() != null )
        {
            for ( Specification s : module.getSpecifications().getSpecification() )
            {
                this.writeResourceBundleResourceFiles( s, resourcesDirectory );
            }
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
     * Writes resource bundle resource files of a given specification from the modules of the instance to a directory.
     *
     * @param specification The specification to process.
     * @param resourcesDirectory The directory to write resource bundle resource files to.
     *
     * @throws NullPointerException if {@code specification} or {@code resourcesDirectory} is {@code null}.
     * @throws IOException if writing resource bundle resource files fails.
     *
     * @see #getResourceBundleResources(org.jomc.model.Specification)
     */
    public void writeResourceBundleResourceFiles( final Specification specification, final File resourcesDirectory )
        throws IOException
    {
        if ( specification == null )
        {
            throw new NullPointerException( "implementation" );
        }
        if ( resourcesDirectory == null )
        {
            throw new NullPointerException( "resourcesDirectory" );
        }

        if ( specification.isClassDeclaration() )
        {
            this.assertValidTemplates( specification );

            final String bundlePath =
                this.getJavaTypeName( specification, true ).replace( '.', File.separatorChar );

            this.writeResourceBundleResourceFiles(
                this.getResourceBundleResources( specification ), resourcesDirectory, bundlePath );

        }
    }

    /**
     * Writes resource bundle resource files of a given implementation from the modules of the instance to a directory.
     *
     * @param implementation The implementation to process.
     * @param resourcesDirectory The directory to write resource bundle resource files to.
     *
     * @throws NullPointerException if {@code implementation} or {@code resourcesDirectory} is {@code null}.
     * @throws IOException if writing resource bundle resource files fails.
     *
     * @see #getResourceBundleResources(org.jomc.model.Implementation)
     */
    public void writeResourceBundleResourceFiles( final Implementation implementation, final File resourcesDirectory )
        throws IOException
    {
        if ( implementation == null )
        {
            throw new NullPointerException( "implementation" );
        }
        if ( resourcesDirectory == null )
        {
            throw new NullPointerException( "resourcesDirectory" );
        }

        if ( implementation.isClassDeclaration() )
        {
            this.assertValidTemplates( implementation );

            final String bundlePath =
                this.getJavaTypeName( implementation, true ).replace( '.', File.separatorChar );

            this.writeResourceBundleResourceFiles(
                this.getResourceBundleResources( implementation ), resourcesDirectory, bundlePath );

        }
    }

    /**
     * Gets resource bundle properties resources of a given specification.
     *
     * @param specification The specification to get resource bundle properties resources of.
     *
     * @return Resource bundle properties resources of {@code specification}.
     *
     * @throws NullPointerException if {@code specification} is {@code null}.
     * @throws IOException if getting the resource bundle properties resources fails.
     */
    public Map<Locale, Properties> getResourceBundleResources( final Specification specification )
        throws IOException
    {
        if ( specification == null )
        {
            throw new NullPointerException( "specification" );
        }

        return new HashMap<Locale, java.util.Properties>();
    }

    /**
     * Gets resource bundle properties resources of a given implementation.
     *
     * @param implementation The implementation to get resource bundle properties resources of.
     *
     * @return Resource bundle properties resources of {@code implementation}.
     *
     * @throws NullPointerException if {@code implementation} is {@code null}.
     * @throws IOException if getting the resource bundle properties resources fails.
     */
    public Map<Locale, Properties> getResourceBundleResources( final Implementation implementation )
        throws IOException
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

    private void writeResourceBundleResourceFiles( final Map<Locale, Properties> resources,
                                                   final File resourcesDirectory, final String bundlePath )
        throws IOException
    {
        if ( resources == null )
        {
            throw new NullPointerException( "resources" );
        }
        if ( resourcesDirectory == null )
        {
            throw new NullPointerException( "resourcesDirectory" );
        }
        if ( bundlePath == null )
        {
            throw new NullPointerException( "bundlePath" );
        }

        Properties defProperties = null;
        Properties fallbackProperties = null;

        final VelocityContext ctx = this.getVelocityContext();
        final String toolName = ctx.get( "toolName" ).toString();
        final String toolVersion = ctx.get( "toolVersion" ).toString();
        final String toolUrl = ctx.get( "toolUrl" ).toString();

        for ( Map.Entry<Locale, Properties> e : resources.entrySet() )
        {
            final String language = e.getKey().getLanguage().toLowerCase();
            final Properties p = e.getValue();
            final File file = new File( resourcesDirectory, bundlePath + "_" + language + ".properties" );

            if ( !file.getParentFile().exists() && !file.getParentFile().mkdirs() )
            {
                throw new IOException( getMessage( "failedCreatingDirectory",
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
                p.store( out, toolName + ' ' + toolVersion + " - See " + toolUrl );
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
                throw new IOException( getMessage( "failedCreatingDirectory",
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
                defProperties.store( out, toolName + ' ' + toolVersion + " - See " + toolUrl );
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

    private void assertValidTemplates( final Specification specification )
    {
        if ( specification == null )
        {
            throw new NullPointerException( "specification" );
        }
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
