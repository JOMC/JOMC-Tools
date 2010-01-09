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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;
import org.apache.commons.io.FileUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.jomc.model.Implementation;
import org.jomc.model.Message;
import org.jomc.model.Messages;
import org.jomc.model.Module;
import org.jomc.model.Text;

/**
 * Generates Java bundles.
 *
 * <p><b>Use cases</b><br/><ul>
 * <li>{@link #writeBundleResources(java.io.File) }</li>
 * <li>{@link #writeBundleResources(org.jomc.model.Module, java.io.File) }</li>
 * <li>{@link #writeBundleResources(org.jomc.model.Implementation, java.io.File) }</li>
 * <li>{@link #writeBundleSources(java.io.File) }</li>
 * <li>{@link #writeBundleSources(org.jomc.model.Module, java.io.File) }</li>
 * <li>{@link #writeBundleSources(org.jomc.model.Implementation, java.io.File) }</li>
 * </ul></p>
 *
 * @author <a href="mailto:cs@jomc.org">Christian Schulte</a>
 * @version $Id$
 *
 * @see #getModules()
 */
public class JavaBundles extends JomcTool
{

    /** Name of the generator. */
    private static final String GENERATOR_NAME = JavaBundles.class.getName();

    /** Constant for the version of the generator. */
    private static final String GENERATOR_VERSION = "1.0";

    /** Location of the {@code Bundle.java.vm} template. */
    private static final String BUNDLE_TEMPLATE = "Bundle.java.vm";

    /** Constant for the suffix appended to implementation identifiers. */
    private static final String BUNDLE_SUFFIX = "Bundle";

    /** The language of the default language properties file of the bundle. */
    private Locale defaultLocale;

    /** Creates a new {@code JavaBundles} instance. */
    public JavaBundles()
    {
        super();
    }

    /**
     * Creates a new {@code JavaBundles} instance taking a {@code JavaBundles} instance to initialize the instance with.
     *
     * @param tool The instance to initialize the new instance with,
     */
    public JavaBundles( final JavaBundles tool )
    {
        super( tool );
        this.setDefaultLocale( tool.getDefaultLocale() );
    }

    /**
     * Gets the language of the default language properties file of the bundle.
     *
     * @return The language of the default language properties file of the bundle.
     *
     * @see #setDefaultLocale(java.util.Locale)
     */
    public Locale getDefaultLocale()
    {
        if ( this.defaultLocale == null )
        {
            this.defaultLocale = Locale.getDefault();
            if ( this.isLoggable( Level.FINE ) )
            {
                this.log( Level.FINE, this.getMessage( "defaultLocale", new Object[]
                    {
                        this.defaultLocale.toString()
                    } ), null );

            }
        }

        return this.defaultLocale;
    }

    /**
     * Sets the language of the default language properties file of the bundle.
     *
     * @param value The language of the default language properties file of the bundle.
     *
     * @see #getDefaultLocale()
     */
    public void setDefaultLocale( final Locale value )
    {
        this.defaultLocale = value;
    }

    /**
     * Writes bundle sources of the modules of the instance to a given directory.
     *
     * @param sourcesDirectory The directory to write sources to.
     *
     * @throws NullPointerException if {@code sourcesDirectory} is {@code null}.
     * @throws ToolException if writing fails.
     *
     * @see #writeBundleSources(org.jomc.model.Module, java.io.File)
     */
    public void writeBundleSources( final File sourcesDirectory ) throws ToolException
    {
        if ( sourcesDirectory == null )
        {
            throw new NullPointerException( "sourcesDirectory" );
        }

        for ( Module m : this.getModules().getModule() )
        {
            this.writeBundleSources( m, sourcesDirectory );
        }
    }

    /**
     * Writes bundle sources of a given module from the modules of the instance to a given directory.
     *
     * @param module The module to process.
     * @param sourcesDirectory The directory to write sources to.
     *
     * @throws NullPointerException if {@code module} or {@code sourcesDirectory} is {@code null}.
     * @throws ToolException if writing fails.
     *
     * @see #writeBundleSources(org.jomc.model.Implementation, java.io.File)
     */
    public void writeBundleSources( final Module module, final File sourcesDirectory ) throws ToolException
    {
        if ( module == null )
        {
            throw new NullPointerException( "module" );
        }
        if ( sourcesDirectory == null )
        {
            throw new NullPointerException( "sourcesDirectory" );
        }

        if ( module.getImplementations() != null )
        {
            for ( Implementation i : module.getImplementations().getImplementation() )
            {
                this.writeBundleSources( i, sourcesDirectory );
            }
        }
    }

    /**
     * Writes bundle sources of a given implementation from the modules of the instance to a given directory.
     *
     * @param implementation The implementation to process.
     * @param sourcesDirectory The directory to write sources to.
     *
     * @throws NullPointerException if {@code implementation} or {@code sourcesDirectory} is {@code null}.
     * @throws ToolException if writing fails.
     *
     * @see #getResourceBundleSources(org.jomc.model.Implementation)
     */
    public void writeBundleSources( final Implementation implementation, final File sourcesDirectory )
        throws ToolException
    {
        if ( implementation == null )
        {
            throw new NullPointerException( "implementation" );
        }
        if ( sourcesDirectory == null )
        {
            throw new NullPointerException( "sourcesDirectory" );
        }

        try
        {
            if ( implementation.isClassDeclaration() )
            {
                this.assertValidTemplates( implementation );

                final String bundlePath =
                    ( this.getJavaTypeName( implementation, true ) + BUNDLE_SUFFIX ).replace( '.', File.separatorChar );

                final File bundleFile = new File( sourcesDirectory, bundlePath + ".java" );

                if ( !bundleFile.getParentFile().exists() && !bundleFile.getParentFile().mkdirs() )
                {
                    throw new ToolException( this.getMessage( "failedCreatingDirectory", new Object[]
                        {
                            bundleFile.getParentFile().getAbsolutePath()
                        } ) );

                }

                if ( this.isLoggable( Level.INFO ) )
                {
                    this.log( Level.INFO, this.getMessage( "writing", new Object[]
                        {
                            bundleFile.getCanonicalPath()
                        } ), null );

                }

                FileUtils.writeStringToFile( bundleFile, this.getResourceBundleSources( implementation ),
                                             this.getOutputEncoding() );

            }
        }
        catch ( final IOException e )
        {
            throw new ToolException( e );
        }
    }

    /**
     * Writes bundle resources of the modules of the instance to a given directory.
     *
     * @param resourcesDirectory The directory to write resources to.
     *
     * @throws NullPointerException if {@code resourcesDirectory} is {@code null}.
     * @throws ToolException if writing fails.
     *
     * @see #writeBundleResources(org.jomc.model.Module, java.io.File)
     */
    public void writeBundleResources( final File resourcesDirectory ) throws ToolException
    {
        if ( resourcesDirectory == null )
        {
            throw new NullPointerException( "resourcesDirectory" );
        }

        for ( Module m : this.getModules().getModule() )
        {
            this.writeBundleResources( m, resourcesDirectory );
        }
    }

    /**
     * Writes bundle resources of a given module from the modules of the instance to a given directory.
     *
     * @param module The module to process.
     * @param resourcesDirectory The directory to write resources to.
     *
     * @throws NullPointerException if {@code module} or {@code resourcesDirectory} is {@code null}.
     * @throws ToolException if writing fails.
     *
     * @see #writeBundleResources(org.jomc.model.Implementation, java.io.File)
     */
    public void writeBundleResources( final Module module, final File resourcesDirectory ) throws ToolException
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
                this.writeBundleResources( i, resourcesDirectory );
            }
        }
    }

    /**
     * Writes the bundle resources of a given implementation from the modules of the instance to a directory.
     *
     * @param implementation The implementation to process.
     * @param resourcesDirectory The directory to write resources to.
     *
     * @throws NullPointerException if {@code implementation} or {@code resourcesDirectory} is {@code null}.
     * @throws ToolException if writing fails.
     *
     * @see #getResourceBundleResources(org.jomc.model.Implementation)
     */
    public void writeBundleResources( final Implementation implementation, final File resourcesDirectory )
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
                    ( this.getJavaTypeName( implementation, true ) + BUNDLE_SUFFIX ).replace( '.', File.separatorChar );

                Properties defProperties = null;
                Properties fallbackProperties = null;

                for ( Map.Entry<Locale, Properties> e : this.getResourceBundleResources( implementation ).entrySet() )
                {
                    final String language = e.getKey().getLanguage().toLowerCase();
                    final java.util.Properties p = e.getValue();
                    final File file = new File( resourcesDirectory, bundlePath + "_" + language + ".properties" );

                    if ( !file.getParentFile().exists() && !file.getParentFile().mkdirs() )
                    {
                        throw new ToolException( this.getMessage( "failedCreatingDirectory", new Object[]
                            {
                                file.getParentFile().getAbsolutePath()
                            } ) );

                    }

                    if ( this.isLoggable( Level.INFO ) )
                    {
                        this.log( Level.INFO, this.getMessage( "writing", new Object[]
                            {
                                file.getCanonicalPath()
                            } ), null );

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

                    if ( this.getDefaultLocale().getLanguage().equalsIgnoreCase( language ) )
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
                        throw new ToolException( this.getMessage( "failedCreatingDirectory", new Object[]
                            {
                                file.getParentFile().getAbsolutePath()
                            } ) );

                    }

                    if ( this.isLoggable( Level.INFO ) )
                    {
                        this.log( Level.INFO, this.getMessage( "writing", new Object[]
                            {
                                file.getCanonicalPath()
                            } ), null );

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
            throw new ToolException( e );
        }
    }

    /**
     * Gets the source code of the Java class for accessing the resource bundle of a given implementation.
     *
     * @param implementation The implementation to get the source code of.
     *
     * @return The source code of the Java class for accessing the resource bundle of {@code implementation}.
     *
     * @throws NullPointerException if {@code implementation} is {@code null}.
     * @throws ToolException if getting the source code fails.
     */
    public String getResourceBundleSources( final Implementation implementation ) throws ToolException
    {
        if ( implementation == null )
        {
            throw new NullPointerException( "implementation" );
        }

        try
        {
            final StringWriter writer = new StringWriter();
            final VelocityContext ctx = this.getVelocityContext();
            final Template template = this.getVelocityTemplate( BUNDLE_TEMPLATE );
            ctx.put( "implementation", implementation );
            ctx.put( "template", template );
            template.merge( ctx, writer );
            writer.close();
            return writer.toString();
        }
        catch ( final IOException e )
        {
            throw new ToolException( e );
        }
    }

    /**
     * Gets the resource bundle properties of a given implementation.
     *
     * @param implementation The implementation to get resource bundle properties of.
     *
     * @return Resource bundle properties of {@code implementation}.
     *
     * @throws NullPointerException if {@code implementation} is {@code null}.
     * @throws ToolException if getting the resources fails.
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

    /**
     * Gets the velocity context used for merging templates.
     *
     * @return The velocity context used for merging templates.
     */
    @Override
    public VelocityContext getVelocityContext()
    {
        final VelocityContext ctx = super.getVelocityContext();
        ctx.put( "classSuffix", BUNDLE_SUFFIX );
        ctx.put( "comment", Boolean.TRUE );
        return ctx;
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

    private String getMessage( final String key, final Object args )
    {
        final ResourceBundle b = ResourceBundle.getBundle( JavaBundles.class.getName().replace( '.', '/' ) );
        return new MessageFormat( b.getString( key ) ).format( args );
    }

}
