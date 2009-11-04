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
package org.jomc.mojo;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.util.JAXBResult;
import javax.xml.bind.util.JAXBSource;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;
import org.apache.maven.plugins.shade.resource.ResourceTransformer;
import org.jomc.model.DefaultModelManager;
import org.jomc.model.Module;
import org.jomc.model.Modules;
import org.jomc.model.bootstrap.Schema;
import org.jomc.model.bootstrap.Schemas;
import org.xml.sax.SAXException;

/**
 * Maven Shade Plugin {@code ResourceTransformer} implementation for assembling JOMC resources.
 * <p><b>Usage</b><pre>
 * &lt;transformer implementation="org.jomc.mojo.JomcResourceTransformer"&gt;
 *   &lt;moduleName&gt;${project.name}&lt;/moduleName&gt;
 *   &lt;moduleVersion&gt;${project.version}&lt;/moduleVersion&gt;
 *   &lt;moduleVendor&gt;${project.organization.name}&lt;/moduleVendor&gt;
 *   &lt;moduleResource&gt;META-INF/jomc-something-else.xml&lt;/moduleResource&gt;
 *   &lt;moduleResources&gt;
 *     &lt;moduleResource&gt;META-INF/jomc.xml&lt;/moduleResource&gt;
 *   &lt;/moduleResources&gt;
 *   &lt;moduleIncludes&gt;
 *     &lt;moduleInclude&gt;module name&lt;/moduleInclude&gt;
 *   &lt;/moduleIncludes&gt;
 *   &lt;moduleExcludes&gt;
 *     &lt;moduleExclude&gt;module name&lt;/moduleExclude&gt;
 *   &lt;/moduleExcludes&gt;
 *   &lt;bootstrapResource&gt;META-INF/jomc-something-else-bootstrap.xml&lt;/bootstrapResource&gt;
 *   &lt;bootstrapResources&gt;
 *     &lt;bootstrapResource&gt;META-INF/jomc-bootstrap.xml&lt;/bootstrapResource&gt;
 *   &lt;/bootstrapResources&gt;
 *   &lt;modelObjectStylesheet&gt;Filename of a style sheet to use for transforming the merged model document.&lt;/modelObjectStylesheet&gt;
 *   &lt;bootstrapObjectStylesheet&gt;Filename of a style sheet to use for transforming the merged bootstrap document.&lt;/bootstrapObjectStylesheet&gt;
 * &lt;/transformer&gt;
 * </pre></p>
 *
 * @author <a href="mailto:cs@jomc.org">Christian Schulte</a>
 * @version $Id$
 */
public class JomcResourceTransformer implements ResourceTransformer
{

    /** Type of a resource. */
    enum ResourceType
    {

        MODEL_OBJECT_RESOURCE,
        BOOTSTRAP_OBJECT_RESOURCE,
        UNKNOWN_RESOURCE

    }

    /** The name of the assembled module. */
    private String moduleName;

    /** The version of the assembled module. */
    private String moduleVersion;

    /** The vendor of the assembled module. */
    private String moduleVendor;

    /** The resource name of the assembled module. */
    private String moduleResource = "META-INF/jomc.xml";

    /** Names of resources to process. */
    private String[] moduleResources =
    {
        "META-INF/jomc.xml"
    };

    /** The resource name of the assembled bootstrap resources. */
    private String bootstrapResource = "META-INF/jomc-bootstrap.xml";

    /** Names of bootstrap resources to process. */
    private String[] bootstrapResources =
    {
        "META-INF/jomc-bootstrap.xml"
    };

    /** Model object style sheet to apply. */
    private File modelObjectStylesheet;

    /** Bootstrap object style sheet to apply. */
    private File bootstrapObjectStylesheet;

    /** Included modules. */
    private List<String> moduleIncludes;

    /** Excluded modules. */
    private List<String> moduleExcludes;

    /** Bootstrap resources. */
    private final Schemas schemas = new Schemas();

    /** Model resources. */
    private final Modules modules = new Modules();

    /** Type of the currently processed resource. */
    private ResourceType currentResourceType = ResourceType.UNKNOWN_RESOURCE;

    /** The JOMC JAXB context of the instance. */
    private JAXBContext jomcContext;

    /** The JOMC JAXB marshaller of the instance. */
    private Marshaller jomcMarshaller;

    /** The JOMC JAXB unmarshaller of the instance. */
    private Unmarshaller jomcUnmarshaller;

    /** The JOMC JAXP schema of the instance. */
    private javax.xml.validation.Schema jomcSchema;

    /** The bootstrap JAXB context of the instance. */
    private JAXBContext bootstrapContext;

    /** The bootstrap JAXB marshaller of the instance. */
    private Marshaller bootstrapMarshaller;

    /** The bootstrap JAXB unmarshaller of the instance. */
    private Unmarshaller bootstrapUnmarshaller;

    /** The bootstrap JAXP schema of the instance. */
    private javax.xml.validation.Schema bootstrapSchema;

    /** Creates a new {@code JomcResourceTransformer} instance. */
    public JomcResourceTransformer()
    {
        super();
    }

    /**
     * Gets the JOMC JAXB context of the instance.
     *
     * @return The JOMC JAXB context of the instance.
     *
     * @throws IOException if reading schema resources fails.
     * @throws SAXException if parsing schema resources fails.
     * @throws JAXBException if unmarshalling schema resources or creating a context fails.
     */
    protected JAXBContext getJomcContext() throws IOException, SAXException, JAXBException
    {
        if ( this.jomcContext == null )
        {
            this.jomcContext = new DefaultModelManager().getContext( this.getClass().getClassLoader() );
        }

        return this.jomcContext;
    }

    /**
     * Gets the JOMC JAXB marshaller of the instance.
     *
     * @return The JOMC JAXB marshaller of the instance.
     *
     * @throws IOException if reading schema resources fails.
     * @throws SAXException if parsing schema resources fails.
     * @throws JAXBException if unmarshalling schema resources or creating a marshaller fails.
     */
    protected Marshaller getJomcMarshaller() throws IOException, SAXException, JAXBException
    {
        if ( this.jomcMarshaller == null )
        {
            this.jomcMarshaller = new DefaultModelManager().getMarshaller( this.getClass().getClassLoader() );
        }

        return this.jomcMarshaller;
    }

    /**
     * Gets the JOMC JAXB unmarshaller of the instance.
     *
     * @return The JOMC JAXB unmarshaller of the instance.
     *
     * @throws IOException if reading schema resources fails.
     * @throws SAXException if parsing schema resources fails.
     * @throws JAXBException if unmarshalling schema resources or creating an unmarshaller fails.
     */
    protected Unmarshaller getJomcUnmarshaller() throws IOException, SAXException, JAXBException
    {
        if ( this.jomcUnmarshaller == null )
        {
            this.jomcUnmarshaller = new DefaultModelManager().getUnmarshaller( this.getClass().getClassLoader() );
        }

        return this.jomcUnmarshaller;
    }

    /**
     * Gets the JOMC JAXP schema of the instance.
     *
     * @return The JOMC JAXP schema of the instance.
     *
     * @throws IOException if reading schema resources fails.
     * @throws SAXException if parsing schema resources fails.
     * @throws JAXBException if unmarshalling schema resources or creating a context fails.
     */
    protected javax.xml.validation.Schema getJomcSchema() throws IOException, SAXException, JAXBException
    {
        if ( this.jomcSchema == null )
        {
            this.jomcSchema = new DefaultModelManager().getSchema( this.getClass().getClassLoader() );
        }

        return this.jomcSchema;
    }

    /**
     * Gets the bootstrap JAXB context of the instance.
     *
     * @return The bootstrap JAXB context of the instance.
     *
     * @throws JAXBException if creating a context fails.
     */
    protected JAXBContext getBootstrapContext() throws JAXBException
    {
        if ( this.bootstrapContext == null )
        {
            this.bootstrapContext = new DefaultModelManager().getBootstrapContext();
        }

        return this.bootstrapContext;
    }

    /**
     * Gets the bootstrap JAXB marshaller of the instance.
     *
     * @return The bootstrap JAXB marshaller of the instance.
     *
     * @throws JAXBException if creating a marshaller fails.
     */
    protected Marshaller getBootstrapMarshaller() throws JAXBException
    {
        if ( this.bootstrapMarshaller == null )
        {
            this.bootstrapMarshaller = new DefaultModelManager().getBootstrapMarshaller();
        }

        return this.bootstrapMarshaller;
    }

    /**
     * Gets the bootstrap JAXB unmarshaller of the instance.
     *
     * @return The bootstrap JAXB unmarshaller of the instance.
     *
     * @throws JAXBException if creating an unmarshaller fails.
     */
    protected Unmarshaller getBootstrapUnmarshaller() throws JAXBException
    {
        if ( this.bootstrapUnmarshaller == null )
        {
            this.bootstrapUnmarshaller = new DefaultModelManager().getBootstrapUnmarshaller();
        }

        return this.bootstrapUnmarshaller;
    }

    /**
     * Gets the bootstrap JAXP schema of the instance.
     *
     * @return The bootstrap JAXP schema of the instance.
     *
     * @throws SAXException if parsing schema resources fails.
     */
    protected javax.xml.validation.Schema getBootstrapSchema() throws SAXException
    {
        if ( this.bootstrapSchema == null )
        {
            this.bootstrapSchema = new DefaultModelManager().getBootstrapSchema();
        }

        return this.bootstrapSchema;
    }

    public boolean canTransformResource( final String arg )
    {
        // Relocating model data of committed class files is not supported since class files are not provided to
        // resource transformers and the shade plugin does not expose its remappers.

        if ( this.moduleResources != null )
        {
            for ( String r : this.moduleResources )
            {
                if ( arg.endsWith( r ) )
                {
                    this.currentResourceType = ResourceType.MODEL_OBJECT_RESOURCE;
                    return true;
                }
            }
        }
        if ( this.bootstrapResources != null )
        {
            for ( String r : this.bootstrapResources )
            {
                if ( arg.endsWith( r ) )
                {
                    this.currentResourceType = ResourceType.BOOTSTRAP_OBJECT_RESOURCE;
                    return true;
                }
            }
        }

        this.currentResourceType = ResourceType.UNKNOWN_RESOURCE;
        return false;
    }

    public void processResource( final InputStream in ) throws IOException
    {
        try
        {
            switch ( this.currentResourceType )
            {
                case MODEL_OBJECT_RESOURCE:
                    final Unmarshaller unmarshaller = this.getJomcUnmarshaller();
                    unmarshaller.setSchema( this.getJomcSchema() );

                    Object modelObject = unmarshaller.unmarshal( in );

                    if ( modelObject instanceof JAXBElement )
                    {
                        modelObject = ( (JAXBElement) modelObject ).getValue();
                    }
                    if ( modelObject instanceof Modules )
                    {
                        for ( Module m : ( (Modules) modelObject ).getModule() )
                        {
                            this.modules.getModule().add( m );
                        }
                    }
                    if ( modelObject instanceof Module )
                    {
                        this.modules.getModule().add( (Module) modelObject );
                    }
                    break;

                case BOOTSTRAP_OBJECT_RESOURCE:
                    final Unmarshaller u = this.getBootstrapUnmarshaller();
                    u.setSchema( this.getBootstrapSchema() );

                    Object bootstrapObject = u.unmarshal( in );

                    if ( bootstrapObject instanceof JAXBElement )
                    {
                        bootstrapObject = ( (JAXBElement) bootstrapObject ).getValue();
                    }
                    if ( bootstrapObject instanceof Schemas )
                    {
                        for ( Schema s : ( (Schemas) bootstrapObject ).getSchema() )
                        {
                            this.schemas.getSchema().add( s );
                        }
                    }
                    if ( bootstrapObject instanceof Schema )
                    {
                        this.schemas.getSchema().add( (Schema) bootstrapObject );
                    }
                    break;

                default:
                    throw new AssertionError( "" + this.currentResourceType );

            }
        }
        catch ( final SAXException e )
        {
            throw (IOException) new IOException( e.getMessage() ).initCause( e );
        }
        catch ( final JAXBException e )
        {
            throw (IOException) new IOException( e.getMessage() ).initCause( e );
        }
    }

    public boolean hasTransformedResource()
    {
        return !( this.modules.getModule().isEmpty() && this.schemas.getSchema().isEmpty() );
    }

    public void modifyOutputStream( final JarOutputStream out ) throws IOException
    {
        try
        {
            if ( !this.modules.getModule().isEmpty() )
            {
                if ( this.moduleIncludes != null )
                {
                    for ( Iterator<Module> it = this.modules.getModule().iterator(); it.hasNext(); )
                    {
                        if ( !this.moduleIncludes.contains( it.next().getName() ) )
                        {
                            it.remove();
                        }
                    }
                }

                if ( this.moduleExcludes != null )
                {
                    for ( String exclude : this.moduleExcludes )
                    {
                        final Module excluded = this.modules.getModule( exclude );
                        if ( excluded != null )
                        {
                            this.modules.getModule().remove( excluded );
                        }
                    }
                }

                Module mergedModule = this.modules.getMergedModule();
                mergedModule.setName( this.moduleName );
                mergedModule.setVersion( this.moduleVersion );
                mergedModule.setVendor( this.moduleVendor );

                final org.jomc.model.ObjectFactory modelObjectFactory = new org.jomc.model.ObjectFactory();

                if ( this.modelObjectStylesheet != null )
                {
                    final Transformer transformer = TransformerFactory.newInstance().newTransformer(
                        new StreamSource( this.modelObjectStylesheet ) );

                    final JAXBSource source =
                        new JAXBSource( this.getJomcMarshaller(), modelObjectFactory.createModule( mergedModule ) );

                    final JAXBResult result = new JAXBResult( this.getJomcUnmarshaller() );
                    transformer.transform( source, result );
                    mergedModule = ( (JAXBElement<Module>) result.getResult() ).getValue();
                }

                out.putNextEntry( new JarEntry( this.moduleResource ) );

                final Marshaller marshaller = this.getJomcMarshaller();
                marshaller.setSchema( this.getJomcSchema() );
                marshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
                marshaller.marshal( modelObjectFactory.createModule( mergedModule ), out );
            }

            if ( !this.schemas.getSchema().isEmpty() )
            {
                final org.jomc.model.bootstrap.ObjectFactory bootstrapObjectFactory =
                    new org.jomc.model.bootstrap.ObjectFactory();

                Schemas copy = new Schemas( this.schemas );

                if ( this.bootstrapObjectStylesheet != null )
                {
                    final Transformer transformer = TransformerFactory.newInstance().newTransformer(
                        new StreamSource( this.bootstrapObjectStylesheet ) );

                    final JAXBSource source =
                        new JAXBSource( this.getBootstrapMarshaller(), bootstrapObjectFactory.createSchemas( copy ) );

                    final JAXBResult result = new JAXBResult( this.getBootstrapUnmarshaller() );
                    transformer.transform( source, result );
                    copy = ( (JAXBElement<Schemas>) result.getResult() ).getValue();
                }

                out.putNextEntry( new JarEntry( this.bootstrapResource ) );

                final Marshaller m = this.getBootstrapMarshaller();
                m.setSchema( this.getBootstrapSchema() );
                m.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
                m.marshal( bootstrapObjectFactory.createSchemas( copy ), out );
            }
        }
        catch ( final TransformerConfigurationException e )
        {
            throw (IOException) new IOException( e.getMessage() ).initCause( e );
        }
        catch ( final TransformerException e )
        {
            throw (IOException) new IOException( e.getMessage() ).initCause( e );
        }
        catch ( final SAXException e )
        {
            throw (IOException) new IOException( e.getMessage() ).initCause( e );
        }
        catch ( final JAXBException e )
        {
            throw (IOException) new IOException( e.getMessage() ).initCause( e );
        }
    }

}
