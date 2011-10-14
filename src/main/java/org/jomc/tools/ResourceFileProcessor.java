/*
 *   Copyright (C) Christian Schulte, 2005-206
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
 * <p><b>Use Cases:</b><br/><ul>
 * <li>{@link #writeResourceBundleResourceFiles(File) }</li>
 * <li>{@link #writeResourceBundleResourceFiles(Module, File) }</li>
 * <li>{@link #writeResourceBundleResourceFiles(Specification, File) }</li>
 * <li>{@link #writeResourceBundleResourceFiles(Implementation, File) }</li>
 * </ul></p>
 *
 * @author <a href="mailto:schulte2005@users.sourceforge.net">Christian Schulte</a>
 * @version $JOMC$
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
    public final Locale getResourceBundleDefaultLocale()
    {
        if ( this.resourceBundleDefaultLocale == null )
        {
            this.resourceBundleDefaultLocale = Locale.ENGLISH;

            if ( this.isLoggable( Level.CONFIG ) )
            {
                this.log( Level.CONFIG,
                          getMessage( "defaultResourceBundleDefaultLocale", this.resourceBundleDefaultLocale ), null );

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
    public final void setResourceBundleDefaultLocale( final Locale value )
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

        for ( int i = 0, s0 = this.getModules().getModule().size(); i < s0; i++ )
        {
            this.writeResourceBundleResourceFiles( this.getModules().getModule().get( i ), resourcesDirectory );
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

        assert this.getModules().getModule( module.getName() ) != null : "Module '" + module.getName() + "' not found.";

        if ( module.getSpecifications() != null )
        {
            for ( int i = 0, s0 = module.getSpecifications().getSpecification().size(); i < s0; i++ )
            {
                this.writeResourceBundleResourceFiles( module.getSpecifications().getSpecification().get( i ),
                                                       resourcesDirectory );

            }
        }

        if ( module.getImplementations() != null )
        {
            for ( int i = 0, s0 = module.getImplementations().getImplementation().size(); i < s0; i++ )
            {
                this.writeResourceBundleResourceFiles( module.getImplementations().getImplementation().get( i ),
                                                       resourcesDirectory );

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

        assert this.getModules().getSpecification( specification.getIdentifier() ) != null :
            "Specification '" + specification.getIdentifier() + "' not found.";

        if ( specification.isClassDeclaration() )
        {
            if ( !resourcesDirectory.isDirectory() )
            {
                throw new IOException( getMessage( "directoryNotFound", resourcesDirectory.getAbsolutePath() ) );
            }

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

        assert this.getModules().getImplementation( implementation.getIdentifier() ) != null :
            "Implementation '" + implementation.getIdentifier() + "' not found.";

        if ( implementation.isClassDeclaration() )
        {
            if ( !resourcesDirectory.isDirectory() )
            {
                throw new IOException( getMessage( "directoryNotFound", resourcesDirectory.getAbsolutePath() ) );
            }

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

        assert this.getModules().getSpecification( specification.getIdentifier() ) != null :
            "Specification '" + specification.getIdentifier() + "' not found.";

        return new HashMap<Locale, Properties>();
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

        assert this.getModules().getImplementation( implementation.getIdentifier() ) != null :
            "Implementation '" + implementation.getIdentifier() + "' not found.";

        final Map<Locale, java.util.Properties> properties = new HashMap<Locale, java.util.Properties>( 10 );
        final Messages messages = this.getModules().getMessages( implementation.getIdentifier() );

        if ( messages != null )
        {
            for ( int i = 0, s0 = messages.getMessage().size(); i < s0; i++ )
            {
                final Message message = messages.getMessage().get( i );

                if ( message.getTemplate() != null )
                {
                    for ( int j = 0, s1 = message.getTemplate().getText().size(); j < s1; j++ )
                    {
                        final Text text = message.getTemplate().getText().get( j );
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

            if ( this.getResourceBundleDefaultLocale().getLanguage().equalsIgnoreCase( language ) )
            {
                defProperties = p;
            }

            fallbackProperties = p;

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
            boolean suppressExceptionOnClose = true;
            try
            {
                out = new FileOutputStream( file );
                p.store( out, toolName + ' ' + toolVersion + " - See " + toolUrl );
                suppressExceptionOnClose = false;
            }
            finally
            {
                try
                {
                    if ( out != null )
                    {
                        out.close();
                    }
                }
                catch ( final IOException ex )
                {
                    if ( suppressExceptionOnClose )
                    {
                        this.log( Level.SEVERE, getMessage( ex ), ex );
                    }
                    else
                    {
                        throw ex;
                    }
                }
            }
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
            boolean suppressExceptionOnClose = true;
            try
            {
                out = new FileOutputStream( file );
                defProperties.store( out, toolName + ' ' + toolVersion + " - See " + toolUrl );
                suppressExceptionOnClose = false;
            }
            finally
            {
                try
                {
                    if ( out != null )
                    {
                        out.close();
                    }
                }
                catch ( final IOException e )
                {
                    if ( suppressExceptionOnClose )
                    {
                        this.log( Level.SEVERE, getMessage( e ), e );
                    }
                    else
                    {
                        throw e;
                    }
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
            for ( int i = messages.getMessage().size() - 1; i >= 0; i-- )
            {
                final Message m = messages.getMessage().get( i );

                if ( m.getTemplate() != null )
                {
                    for ( int j = m.getTemplate().getText().size() - 1; j >= 0; j-- )
                    {
                        new MessageFormat( m.getTemplate().getText().get( j ).getValue() );
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

    private static String getMessage( final Throwable t )
    {
        return t != null ? t.getMessage() != null ? t.getMessage() : getMessage( t.getCause() ) : null;
    }

}
