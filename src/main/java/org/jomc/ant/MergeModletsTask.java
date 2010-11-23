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
package org.jomc.ant;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.Set;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.util.JAXBResult;
import javax.xml.bind.util.JAXBSource;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.validation.Schema;
import org.apache.tools.ant.BuildException;
import org.jomc.ant.types.NameType;
import org.jomc.modlet.ModelContext;
import org.jomc.modlet.ModelException;
import org.jomc.modlet.Modlet;
import org.jomc.modlet.ModletObject;
import org.jomc.modlet.Modlets;
import org.jomc.modlet.ObjectFactory;

/**
 * Task for merging modlet resources.
 *
 * @author <a href="mailto:schulte2005@users.sourceforge.net">Christian Schulte</a>
 * @version $Id$
 */
public final class MergeModletsTask extends JomcTask
{

    /** The encoding of the modlet resource. */
    private String modletEncoding;

    /** File to write the merged modlet to. */
    private File modletFile;

    /** The name of the merged modlet. */
    private String modletName;

    /** The version of the merged modlet. */
    private String modletVersion;

    /** The vendor of the merged modlet. */
    private String modletVendor;

    /** Included modlets. */
    private Set<NameType> modletIncludes;

    /** Excluded modlets. */
    private Set<NameType> modletExcludes;

    /** Modlet object style sheet to apply. */
    private String modletObjectStylesheet;

    /** Creates a new {@code MergeModletsTask} instance. */
    public MergeModletsTask()
    {
        super();
    }

    /**
     * Gets the file to write the merged modlet to.
     *
     * @return The file to write the merged modlet to or {@code null}.
     *
     * @see #setModletFile(java.io.File)
     */
    public File getModletFile()
    {
        return this.modletFile;
    }

    /**
     * Sets the file to write the merged modlet to.
     *
     * @param value The new file to write the merged modlet to or {@code null}.
     *
     * @see #getModletFile()
     */
    public void setModletFile( final File value )
    {
        this.modletFile = value;
    }

    /**
     * Gets the encoding of the modlet resource.
     *
     * @return The encoding of the modlet resource.
     *
     * @see #setModletEncoding(java.lang.String)
     */
    public String getModletEncoding()
    {
        if ( this.modletEncoding == null )
        {
            this.modletEncoding = new OutputStreamWriter( new ByteArrayOutputStream() ).getEncoding();
        }

        return this.modletEncoding;
    }

    /**
     * Sets the encoding of the modlet resource.
     *
     * @param value The new encoding of the modlet resource or {@code null}.
     *
     * @see #getModletEncoding()
     */
    public void setModletEncoding( final String value )
    {
        this.modletEncoding = value;
    }

    /**
     * Gets the name of the merged modlet.
     *
     * @return The name of the merged modlet or {@code null}.
     *
     * @see #setModletName(java.lang.String)
     */
    public String getModletName()
    {
        return this.modletName;
    }

    /**
     * Sets the name of the merged modlet.
     *
     * @param value The new name of the merged modlet or {@code null}.
     *
     * @see #getModletName()
     */
    public void setModletName( final String value )
    {
        this.modletName = value;
    }

    /**
     * Gets the location of a style sheet to transform the merged modlet with.
     *
     * @return The location of a style sheet to transform the merged modlet with or {@code null}.
     *
     * @see #setModletObjectStylesheet(java.lang.String)
     */
    public String getModletObjectStylesheet()
    {
        return this.modletObjectStylesheet;
    }

    /**
     * Sets the location of a style sheet to transform the merged modlet with.
     *
     * @param value The new location of a style sheet to transform the merged modlet with or {@code null}.
     *
     * @see #getModletObjectStylesheet()
     */
    public void setModletObjectStylesheet( final String value )
    {
        this.modletObjectStylesheet = value;
    }

    /**
     * Gets the version of the merged modlet.
     *
     * @return The version of the merged modlet or {@code null}.
     *
     * @see #setModletVersion(java.lang.String)
     */
    public String getModletVersion()
    {
        return this.modletVersion;
    }

    /**
     * Sets the version of the merged modlet.
     *
     * @param value The new version of the merged modlet or {@code null}.
     *
     * @see #getModletVersion()
     */
    public void setModletVersion( final String value )
    {
        this.modletVersion = value;
    }

    /**
     * Gets the vendor of the merged modlet.
     *
     * @return The vendor of the merge modlet or {@code null}.
     *
     * @see #setModletVendor(java.lang.String)
     */
    public String getModletVendor()
    {
        return this.modletVendor;
    }

    /**
     * Sets the vendor of the merged modlet.
     *
     * @param value The new vendor of the merged modlet or {@code null}.
     *
     * @see #getModletVendor()
     */
    public void setModletVendor( final String value )
    {
        this.modletVendor = value;
    }

    /**
     * Gets a set of modlet names to include.
     * <p>This accessor method returns a reference to the live set, not a snapshot. Therefore any modification you make
     * to the returned set will be present inside the object. This is why there is no {@code set} method for the
     * modlet includes property.</p>
     *
     * @return A set of modlet names to include.
     *
     * @see #createModletInclude()
     */
    public Set<NameType> getModletIncludes()
    {
        if ( this.modletIncludes == null )
        {
            this.modletIncludes = new HashSet<NameType>();
        }

        return this.modletIncludes;
    }

    /**
     * Creates a new {@code modletInclude} element instance.
     *
     * @return A new {@code modletInclude} element instance.
     *
     * @see #getModletIncludes()
     */
    public NameType createModletInclude()
    {
        final NameType modletInclude = new NameType();
        this.getModletIncludes().add( modletInclude );
        return modletInclude;
    }

    /**
     * Gets a set of modlet names to exclude.
     * <p>This accessor method returns a reference to the live set, not a snapshot. Therefore any modification you make
     * to the returned set will be present inside the object. This is why there is no {@code set} method for the
     * modlet excludes property.</p>
     *
     * @return A set of modlet names to exclude.
     *
     * @see #createModletExclude()
     */
    public Set<NameType> getModletExcludes()
    {
        if ( this.modletExcludes == null )
        {
            this.modletExcludes = new HashSet<NameType>();
        }

        return this.modletExcludes;
    }

    /**
     * Creates a new {@code modletExclude} element instance.
     *
     * @return A new {@code modletExclude} element instance.
     *
     * @see #getModletExcludes()
     */
    public NameType createModletExclude()
    {
        final NameType modletExclude = new NameType();
        this.getModletExcludes().add( modletExclude );
        return modletExclude;
    }

    /**
     * Merges modlet resources.
     *
     * @throws BuildException if merging modlet resources fails.
     */
    @Override
    public void executeTask() throws BuildException
    {
        try
        {
            this.assertNotNull( "model", this.getModel() );
            this.assertNotNull( "modletFile", this.getModletFile() );
            this.assertNotNull( "modletName", this.getModletName() );
            this.assertNamesNotNull( this.getModletExcludes() );
            this.assertNamesNotNull( this.getModletIncludes() );

            this.log( getMessage( "mergingModlets", this.getModel() ) );

            final ProjectClassLoader classLoader = this.newProjectClassLoader();
            final ModelContext context = this.newModelContext( classLoader );
            final Modlets modlets = new Modlets( context.getModlets() );

            for ( String defaultExclude : classLoader.getModletExcludes() )
            {
                final Modlet m = modlets.getModlet( defaultExclude );
                if ( m != null )
                {
                    modlets.getModlet().remove( m );
                }
            }

            modlets.getModlet().addAll( classLoader.getExcludedModlets().getModlet() );

            for ( final Iterator<Modlet> it = modlets.getModlet().iterator(); it.hasNext(); )
            {
                final Modlet modlet = it.next();

                if ( !this.isModletIncluded( modlet ) || this.isModletExcluded( modlet ) )
                {
                    it.remove();
                    this.log( getMessage( "excludingModlet", modlet.getName() ) );
                }
                else
                {
                    this.log( getMessage( "includingModlet", modlet.getName() ) );
                }
            }

            Modlet mergedModlet = modlets.getMergedModlet( this.getModletName(), this.getModel() );
            mergedModlet.setVendor( this.getModletVendor() );
            mergedModlet.setVersion( this.getModletVersion() );

            final Marshaller marshaller = context.createMarshaller( ModletObject.MODEL_PUBLIC_ID );
            final Unmarshaller unmarshaller = context.createUnmarshaller( ModletObject.MODEL_PUBLIC_ID );
            final Schema schema = context.createSchema( ModletObject.MODEL_PUBLIC_ID );
            marshaller.setSchema( schema );
            unmarshaller.setSchema( schema );

            if ( this.getModletObjectStylesheet() != null )
            {
                final Transformer transformer = this.newTransformer( this.getModletObjectStylesheet(),
                                                                     context.getClassLoader() );

                final JAXBSource source =
                    new JAXBSource( marshaller, new ObjectFactory().createModlet( mergedModlet ) );

                final JAXBResult result = new JAXBResult( unmarshaller );

                transformer.transform( source, result );

                if ( result.getResult() instanceof JAXBElement<?>
                     && ( (JAXBElement<?>) result.getResult() ).getValue() instanceof Modlet )
                {
                    mergedModlet = (Modlet) ( (JAXBElement<?>) result.getResult() ).getValue();
                }
                else
                {
                    throw new BuildException( getMessage( "illegalTransformationResult",
                                                          this.getModletObjectStylesheet() ), this.getLocation() );

                }
            }

            this.log( getMessage( "writing", this.getModletFile().getAbsolutePath(), this.getModletEncoding() ) );
            marshaller.setProperty( Marshaller.JAXB_ENCODING, this.getModletEncoding() );
            marshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
            marshaller.marshal( new ObjectFactory().createModlet( mergedModlet ), this.getModletFile() );
        }
        catch ( final IOException e )
        {
            throw new BuildException( getMessage( e ), e, this.getLocation() );
        }
        catch ( final URISyntaxException e )
        {
            throw new BuildException( getMessage( e ), e, this.getLocation() );
        }
        catch ( final JAXBException e )
        {
            String message = getMessage( e );
            if ( message == null )
            {
                message = getMessage( e.getLinkedException() );
            }

            throw new BuildException( message, e, this.getLocation() );
        }
        catch ( final TransformerConfigurationException e )
        {
            throw new BuildException( getMessage( e ), e, this.getLocation() );
        }
        catch ( final TransformerException e )
        {
            throw new BuildException( getMessage( e ), e, this.getLocation() );
        }
        catch ( final ModelException e )
        {
            throw new BuildException( getMessage( e ), e, this.getLocation() );
        }
    }

    /**
     * Tests a given modlet to be included based on property {@code modletIncludes}.
     *
     * @param modlet The modlet to test.
     *
     * @return {@code true} if {@code modlet} is included based on property {@code modletIncludes}.
     *
     * @throws NullPointerException if {@code modlet} is {@code null}.
     *
     * @see #getModletIncludes()
     */
    public boolean isModletIncluded( final Modlet modlet )
    {
        if ( modlet == null )
        {
            throw new NullPointerException( "modlet" );
        }

        for ( NameType include : this.getModletIncludes() )
        {
            if ( include.getName().equals( modlet.getName() ) )
            {
                return true;
            }
        }

        return this.getModletIncludes().isEmpty() ? true : false;
    }

    /**
     * Test a given modlet to be excluded based on property {@code modletExcludes}.
     *
     * @param modlet The modlet to test.
     *
     * @return {@code true} if {@code modlet} is excluded based on property {@code modletExcludes}.
     *
     * @throws NullPointerException if {@code modlet} is {@code null}.
     *
     * @see #getModletExcludes()
     */
    public boolean isModletExcluded( final Modlet modlet )
    {
        if ( modlet == null )
        {
            throw new NullPointerException( "modlet" );
        }

        for ( NameType exclude : this.getModletExcludes() )
        {
            if ( exclude.getName().equals( modlet.getName() ) )
            {
                return true;
            }
        }

        return false;
    }

    /** {@inheritDoc} */
    @Override
    public MergeModletsTask clone()
    {
        final MergeModletsTask clone = (MergeModletsTask) super.clone();
        clone.modletFile = this.modletFile != null ? new File( this.modletFile.getAbsolutePath() ) : null;

        if ( this.modletExcludes != null )
        {
            final HashSet<NameType> set = new HashSet<NameType>( this.modletExcludes.size() );
            for ( NameType t : this.modletExcludes )
            {
                set.add( t.clone() );
            }

            clone.modletExcludes = set;
        }

        if ( this.modletIncludes != null )
        {
            final HashSet<NameType> set = new HashSet<NameType>( this.modletIncludes.size() );
            for ( NameType t : this.modletIncludes )
            {
                set.add( t.clone() );
            }

            clone.modletIncludes = set;
        }

        return clone;
    }

    private static String getMessage( final String key, final Object... args )
    {
        return MessageFormat.format( ResourceBundle.getBundle(
            MergeModletsTask.class.getName().replace( '.', '/' ) ).getString( key ), args );

    }

    private static String getMessage( final Throwable t )
    {
        return t != null ? t.getMessage() != null ? t.getMessage() : getMessage( t.getCause() ) : null;
    }

}
