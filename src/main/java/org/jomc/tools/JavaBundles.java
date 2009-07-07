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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
import org.apache.velocity.VelocityContext;
import org.jomc.model.Argument;
import org.jomc.model.Arguments;
import org.jomc.model.Implementation;
import org.jomc.model.Message;
import org.jomc.model.MessageReference;
import org.jomc.model.Module;
import org.jomc.model.Text;
import org.jomc.model.Texts;

/**
 * Generates Java bundles.
 *
 * @author <a href="mailto:schulte2005@users.sourceforge.net">Christian Schulte</a>
 * @version $Id$
 * @see #writeModuleBundles(java.io.File, java.io.File)
 */
public class JavaBundles extends JomcTool
{

    /** Name of the generator. */
    private static final String GENERATOR_NAME = JavaBundles.class.getName();

    /** Constant for the version of the generator. */
    private static final String GENERATOR_VERSION = "1.0";

    /** Constant for the name of a property holding a profile. */
    private static final String PROP_PROFILE = JavaBundles.class.getName() + ".profile";

    /** Location of the {@code Bundle.java.vm} template. */
    private static final String BUNDLE_TEMPLATE = "Bundle.java.vm";

    /** The language of the default language properties file of the bundle. */
    private Locale defaultLocale;

    /** Creates a new {@code JavaBundles} instance. */
    public JavaBundles()
    {
        super();
    }

    /**
     * Creates a new {@code JavaBundles} instance taking a {@code JomcTool} instance to initialize the instance with.
     *
     * @param tool The instance to initialize the new instance with,
     */
    public JavaBundles( final JomcTool tool )
    {
        super( tool );
    }

    /**
     * Gets the language of the default language properties file of the bundle.
     *
     * @return The language of the default language properties file of the bundle.
     */
    public Locale getDefaultLocale()
    {
        if ( this.defaultLocale == null )
        {
            this.defaultLocale = Locale.getDefault();
            this.log( Level.INFO, this.getMessage( "defaultLocale", new Object[]
                {
                    this.defaultLocale.toString()
                } ), null );

        }

        return this.defaultLocale;
    }

    /**
     * Sets the language of the default language properties file of the bundle.
     *
     * @param value The language of the default language properties file of the bundle.
     */
    public void setDefaultLocale( final Locale value )
    {
        this.defaultLocale = value;
    }

    /**
     * Writes Java resource bundle sources of the module of the instance to a given directories.
     *
     * @param sourcesDirectory The directory to write sources to.
     *
     * @throws NullPointerException if {@code sourcesDirectory} is {@code null}.
     * @throws IOException if writing fails.
     */
    public void writeModuleSources( final File sourcesDirectory ) throws IOException
    {
        if ( sourcesDirectory == null )
        {
            throw new NullPointerException( "sourcesDirectory" );
        }

        this.writeModuleBundles( sourcesDirectory, null );
    }

    /**
     * Writes Java resource bundle resources of the module of the instance to a given directories.
     *
     * @param resourcesDirectory The directory to write resources to.
     *
     * @throws NullPointerException if {@code resourcesDirectory} is {@code null}.
     * @throws IOException if writing fails.
     */
    public void writeModuleResources( final File resourcesDirectory ) throws IOException
    {
        if ( resourcesDirectory == null )
        {
            throw new NullPointerException( "resourcesDirectory" );
        }

        this.writeModuleBundles( null, resourcesDirectory );
    }

    /**
     * Writes Java resource bundles of the module of the instance to given directories.
     *
     * @param sourcesDirectory The directory to write sources to or {@code null} to not write any sources.
     * @param resourcesDirectory The directory to write resources to or {@code null} to not write any resources.
     *
     * @throws IOException if writing fails.
     */
    public void writeModuleBundles( final File sourcesDirectory, final File resourcesDirectory ) throws IOException
    {
        if ( this.getModule() != null )
        {
            if ( !this.getBuildDirectory().exists() )
            {
                this.getBuildDirectory().mkdirs();
            }

            this.log( Level.INFO, this.getMessage( "processingModule", new Object[]
                {
                    this.getModule().getName()
                } ), null );

            this.assertValidTemplates( this.getModule() );

            final Properties bundleHashcodes = new Properties();
            final File hashFile = new File( this.getBuildDirectory(), "java-bundles.properties" );

            if ( !hashFile.exists() )
            {
                hashFile.createNewFile();
            }

            final InputStream in = new FileInputStream( hashFile );
            bundleHashcodes.load( in );
            in.close();

            final String lastProfile = bundleHashcodes.getProperty( PROP_PROFILE );
            if ( lastProfile != null && !lastProfile.equals( this.getProfile() ) )
            {
                bundleHashcodes.clear();
                bundleHashcodes.setProperty( PROP_PROFILE, this.getProfile() );
            }

            if ( this.getModule().getImplementations() != null )
            {
                for ( Implementation i : this.getModule().getImplementations().getImplementation() )
                {
                    if ( i.getMessages() != null )
                    {
                        final int bundleHash = this.getHashCode( this.getModule(), i );

                        if ( sourcesDirectory != null )
                        {
                            final String propertyName =
                                i.getIdentifier() + sourcesDirectory.getAbsolutePath().hashCode();

                            final String propertyHash = bundleHashcodes.getProperty( propertyName );

                            if ( propertyHash == null || Integer.valueOf( propertyHash ).intValue() != bundleHash )
                            {
                                bundleHashcodes.setProperty( propertyName, Integer.toString( bundleHash ) );
                                this.writeResourceBundleSource( i, sourcesDirectory );
                            }
                        }

                        if ( resourcesDirectory != null )
                        {
                            final String propertyName =
                                i.getIdentifier() + resourcesDirectory.getAbsolutePath().hashCode();

                            final String propertyHash = bundleHashcodes.getProperty( propertyName );

                            if ( propertyHash == null || Integer.valueOf( propertyHash ).intValue() != bundleHash )
                            {
                                bundleHashcodes.setProperty( propertyName, Integer.toString( bundleHash ) );
                                this.writeResourceBundleProperties( i, resourcesDirectory );
                            }
                        }
                    }
                }
            }

            final OutputStream out = new FileOutputStream( hashFile );
            bundleHashcodes.store( out, GENERATOR_NAME + ' ' + GENERATOR_VERSION );
            out.close();

            this.log( Level.INFO, this.getMessage( "upToDate", null ), null );
        }
        else
        {
            this.log( Level.WARNING, this.getMessage( "missingModule", new Object[]
                {
                    this.getModuleName()
                } ), null );

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
     * @throws IOException if rendering source code fails.
     */
    public String getResourceBundleSource( final Implementation implementation ) throws IOException
    {
        if ( implementation == null )
        {
            throw new NullPointerException( "implementation" );
        }

        try
        {
            final StringWriter writer = new StringWriter();
            final VelocityContext ctx = this.getVelocityContext();
            ctx.put( "implementation", implementation );

            this.getVelocityEngine().mergeTemplate(
                this.getTemplateLocation( BUNDLE_TEMPLATE ), this.getEncoding(), ctx, writer );

            writer.close();
            return writer.toString();
        }
        catch ( Exception e )
        {
            throw (IOException) new IOException( e.getMessage() ).initCause( e );
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
     */
    public Map<Locale, java.util.Properties> getResourceBundleProperties( final Implementation implementation )
    {
        if ( implementation == null )
        {
            throw new NullPointerException( "implementation" );
        }

        final Map<Locale, java.util.Properties> properties = new HashMap<Locale, java.util.Properties>( 10 );

        if ( implementation.getMessages() != null )
        {
            for ( Message message : implementation.getMessages().getMessage() )
            {
                for ( Text text : message.getTemplate().getText() )
                {
                    final String language = text.getLanguage().toLowerCase();

                    java.util.Properties bundleProperties = properties.get( language );

                    if ( bundleProperties == null )
                    {
                        bundleProperties = new java.util.Properties();
                        properties.put( new Locale( language ), bundleProperties );
                    }

                    bundleProperties.setProperty( message.getName(), text.getValue() );
                }
            }

            for ( MessageReference messageReference : implementation.getMessages().getReference() )
            {
                final Message message =
                    this.getModule().getMessages().getMessage( messageReference.getName() );

                for ( Text text : message.getTemplate().getText() )
                {
                    final String language = text.getLanguage().toLowerCase();

                    java.util.Properties bundleProperties = properties.get( language );

                    if ( bundleProperties == null )
                    {
                        bundleProperties = new java.util.Properties();
                        properties.put( new Locale( language ), bundleProperties );
                    }

                    bundleProperties.setProperty( message.getName(), text.getValue() );
                }
            }
        }

        return properties;
    }

    /**
     * Writes the source code of the Java class for accessing the resource bundle of a given implementation to a
     * directory.
     *
     * @param implementation The implementation of the bundle.
     * @param sourceDirectory The directory to write to.
     *
     * @throws NullPointerException if {@code implementation} or {@code sourceDirectory} is {@code null}.
     * @throws IOException if writing fails.
     */
    public void writeResourceBundleSource( final Implementation implementation, final File sourceDirectory )
        throws IOException
    {
        if ( implementation == null )
        {
            throw new NullPointerException( "implementation" );
        }
        if ( sourceDirectory == null )
        {
            throw new NullPointerException( "sourceDirectory" );
        }

        final String bundlePath = ( this.getJavaPackageName( implementation ) + '.' + this.getJavaTypeName(
            implementation ) + "Bundle" ).replace( '.', File.separatorChar );

        final File bundleFile = new File( sourceDirectory, bundlePath + ".java" );

        if ( !bundleFile.getParentFile().exists() )
        {
            bundleFile.getParentFile().mkdirs();
        }

        this.log( Level.INFO, this.getMessage( "writing", new Object[]
            {
                bundleFile.getCanonicalPath()
            } ), null );

        FileUtils.writeStringToFile( bundleFile, this.getResourceBundleSource( implementation ), this.getEncoding() );
    }

    /**
     * Writes the resource bundle properties of a given implementation to a directory.
     *
     * @param implementation The implementation of the bundle.
     * @param resourceDirectory The directory to write to.
     *
     * @throws NullPointerException if {@code implementation} or {@code resourceDirectory} is {@code null}.
     * @throws IOException if writing fails.
     */
    public void writeResourceBundleProperties( final Implementation implementation, final File resourceDirectory )
        throws IOException
    {
        if ( implementation == null )
        {
            throw new NullPointerException( "implementation" );
        }
        if ( resourceDirectory == null )
        {
            throw new NullPointerException( "resourceDirectory" );
        }

        final String bundlePath = ( this.getJavaPackageName( implementation ) + '.' + this.getJavaTypeName(
            implementation ) + "Bundle" ).replace( '.', File.separatorChar );

        Properties defProperties = null;
        Properties fallbackProperties = null;

        for ( Map.Entry<Locale, java.util.Properties> e : this.getResourceBundleProperties( implementation ).entrySet() )
        {
            final String language = e.getKey().getLanguage();
            final java.util.Properties p = e.getValue();
            final File file = new File( resourceDirectory, bundlePath + "_" + language.toLowerCase( Locale.ENGLISH ) +
                                                           ".properties" );

            if ( !file.getParentFile().exists() )
            {
                file.getParentFile().mkdirs();
            }

            this.log( Level.INFO, this.getMessage( "writing", new Object[]
                {
                    file.getCanonicalPath()
                } ), null );

            OutputStream out = new FileOutputStream( file );
            p.store( out, GENERATOR_NAME + ' ' + GENERATOR_VERSION );
            out.close();

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

        final File file = new File( resourceDirectory, bundlePath + ".properties" );
        this.log( Level.INFO, this.getMessage( "writing", new Object[]
            {
                file.getCanonicalPath()
            } ), null );

        final OutputStream out = new FileOutputStream( file );
        defProperties.store( out, GENERATOR_NAME + ' ' + GENERATOR_VERSION );
        out.close();
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
        ctx.put( "classSuffix", "Bundle" );
        ctx.put( "comment", Boolean.TRUE );
        ctx.put( "templateLocation", this.getTemplateLocation( BUNDLE_TEMPLATE ) );
        return ctx;
    }

    /**
     * Computes the hashcode of an implementation.
     *
     * @param module The module to use for resolving references.
     * @param implementation The implementation to compute the hashcode of.
     *
     * @return The hashcode of {@code implementation}.
     *
     * @throws NullPointerException if {@code module} or {@code implementation} is {@code null}.
     */
    private int getHashCode( final Module module, final Implementation implementation )
    {
        if ( module == null )
        {
            throw new NullPointerException( "module" );
        }
        if ( implementation == null )
        {
            throw new NullPointerException( "implementation" );
        }

        int bundleHash = 23;

        if ( implementation.getMessages() != null )
        {
            for ( Message message : implementation.getMessages().getMessage() )
            {
                bundleHash = 37 * bundleHash + this.getHashCode( message );
            }

            for ( MessageReference messageReference : implementation.getMessages().getReference() )
            {
                final Message message = module.getMessages().getMessage( messageReference.getName() );
                bundleHash = 37 * bundleHash + this.getHashCode( message );
            }
        }

        return bundleHash;
    }

    private int getHashCode( final Message message )
    {
        int hash = 23;

        if ( message != null )
        {
            hash = 37 * hash + message.getName().hashCode();
            hash = 37 * hash + this.getHashCode( message.getDocumentation() );
            hash = 37 * hash + this.getHashCode( message.getTemplate() );
            hash = 37 * hash + this.getHashCode( message.getArguments() );
        }

        return hash;
    }

    private int getHashCode( final Texts texts )
    {
        int hash = 23;

        if ( texts != null )
        {
            for ( Text t : texts.getText() )
            {
                hash = 37 * hash + t.getLanguage().hashCode();
                hash = 37 * hash + t.getValue().hashCode();
            }
        }

        return hash;
    }

    private int getHashCode( final Arguments arguments )
    {
        int hash = 23;

        if ( arguments != null )
        {
            for ( Argument a : arguments.getArgument() )
            {
                hash = 37 * hash + this.getHashCode( a.getDocumentation() );
                hash = 37 * hash + a.getName().hashCode();
                hash = 37 * hash + a.getType().toString().hashCode();
            }
        }

        return hash;
    }

    private void assertValidTemplates( final Module module ) throws IllegalArgumentException
    {
        if ( module.getImplementations() != null )
        {
            for ( Implementation i : module.getImplementations().getImplementation() )
            {
                if ( i.getMessages() == null )
                {
                    continue;
                }

                for ( Message m : i.getMessages().getMessage() )
                {
                    this.assertValidMessage( m );
                }
            }
        }

        if ( module.getMessages() != null )
        {
            for ( Message m : module.getMessages().getMessage() )
            {
                this.assertValidMessage( m );
            }
        }
    }

    private void assertValidMessage( final Message message ) throws IllegalArgumentException
    {
        if ( message.getTemplate() != null )
        {
            for ( Text t : message.getTemplate().getText() )
            {
                new MessageFormat( t.getValue() );
            }
        }
    }

    private String getMessage( final String key, final Object args )
    {
        final ResourceBundle b = ResourceBundle.getBundle( "org/jomc/tools/JavaBundles" );
        final MessageFormat f = new MessageFormat( b.getString( key ) );
        return f.format( args );
    }

}
