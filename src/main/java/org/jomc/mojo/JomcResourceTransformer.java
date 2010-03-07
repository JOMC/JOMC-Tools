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
import org.jomc.model.ModelContext;
import org.jomc.model.ModelException;
import org.jomc.model.Module;
import org.jomc.model.Modules;
import org.jomc.model.bootstrap.BootstrapContext;
import org.jomc.model.bootstrap.BootstrapException;
import org.jomc.model.bootstrap.Schema;
import org.jomc.model.bootstrap.Schemas;
import org.jomc.model.bootstrap.Service;
import org.jomc.model.bootstrap.Services;

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
 *   &lt;schemasResource&gt;META-INF/jomc-something-else-schemas.xml&lt;/schemasResource&gt;
 *   &lt;schemaResources&gt;
 *     &lt;schemaResource&gt;META-INF/jomc-schemas.xml&lt;/schemaResource&gt;
 *   &lt;/schemaResources&gt;
 *   &lt;servicesResource&gt;META-INF/jomc-something-else-services.xml&lt;/servicesResource&gt;
 *   &lt;serviceResources&gt;
 *     &lt;serviceResource&gt;META-INF/jomc-services.xml&lt;/serviceResource&gt;
 *   &lt;/serviceResources&gt;
 *   &lt;modelObjectStylesheet&gt;Filename of a style sheet to use for transforming the merged model document.&lt;/modelObjectStylesheet&gt;
 *   &lt;bootstrapObjectStylesheet&gt;Filename of a style sheet to use for transforming the merged bootstrap documents.&lt;/bootstrapObjectStylesheet&gt;
 * &lt;/transformer&gt;
 * </pre></p>
 *
 * @author <a href="mailto:schulte2005@users.sourceforge.net">Christian Schulte</a>
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

    /** The resource name of the assembled schema resources. */
    private String schemasResource = "META-INF/jomc-schemas.xml";

    /** Names of schema resources to process. */
    private String[] schemaResources =
    {
        "META-INF/jomc-schemas.xml"
    };

    /** The resource name of the assembled service resources. */
    private String servicesResource = "META-INF/jomc-services.xml";

    /** Names of service resources to process. */
    private String[] serviceResources =
    {
        "META-INF/jomc-services.xml"
    };

    /** Model object style sheet to apply. */
    private File modelObjectStylesheet;

    /** Bootstrap object style sheet to apply. */
    private File bootstrapObjectStylesheet;

    /** Included modules. */
    private List<String> moduleIncludes;

    /** Excluded modules. */
    private List<String> moduleExcludes;

    /** Schemas resources. */
    private final Schemas schemas = new Schemas();

    /** Services resources. */
    private final Services services = new Services();

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
     * @throws ModelException if getting the context fails.
     */
    protected JAXBContext getJomcContext() throws ModelException
    {
        if ( this.jomcContext == null )
        {
            this.jomcContext = ModelContext.createModelContext( this.getClass().getClassLoader() ).createContext();
        }

        return this.jomcContext;
    }

    /**
     * Gets the JOMC JAXB marshaller of the instance.
     *
     * @return The JOMC JAXB marshaller of the instance.
     *
     * @throws ModelException if getting the context fails.
     */
    protected Marshaller getJomcMarshaller() throws ModelException
    {
        if ( this.jomcMarshaller == null )
        {
            this.jomcMarshaller =
                ModelContext.createModelContext( this.getClass().getClassLoader() ).createMarshaller();

        }

        return this.jomcMarshaller;
    }

    /**
     * Gets the JOMC JAXB unmarshaller of the instance.
     *
     * @return The JOMC JAXB unmarshaller of the instance.
     *
     * @throws ModelException if getting the unmarshaller fails.
     */
    protected Unmarshaller getJomcUnmarshaller() throws ModelException
    {
        if ( this.jomcUnmarshaller == null )
        {
            this.jomcUnmarshaller =
                ModelContext.createModelContext( this.getClass().getClassLoader() ).createUnmarshaller();

        }

        return this.jomcUnmarshaller;
    }

    /**
     * Gets the JOMC JAXP schema of the instance.
     *
     * @return The JOMC JAXP schema of the instance.
     *
     * @throws ModelException if getting the schema fails.
     */
    protected javax.xml.validation.Schema getJomcSchema() throws ModelException
    {
        if ( this.jomcSchema == null )
        {
            this.jomcSchema = ModelContext.createModelContext( this.getClass().getClassLoader() ).createSchema();
        }

        return this.jomcSchema;
    }

    /**
     * Gets the bootstrap JAXB context of the instance.
     *
     * @return The bootstrap JAXB context of the instance.
     *
     * @throws BootstrapException if creating a context fails.
     */
    protected JAXBContext getBootstrapContext() throws BootstrapException
    {
        if ( this.bootstrapContext == null )
        {
            this.bootstrapContext =
                BootstrapContext.createBootstrapContext( this.getClass().getClassLoader() ).createContext();

        }

        return this.bootstrapContext;
    }

    /**
     * Gets the bootstrap JAXB marshaller of the instance.
     *
     * @return The bootstrap JAXB marshaller of the instance.
     *
     * @throws BootstrapException if creating a marshaller fails.
     */
    protected Marshaller getBootstrapMarshaller() throws BootstrapException
    {
        if ( this.bootstrapMarshaller == null )
        {
            this.bootstrapMarshaller =
                BootstrapContext.createBootstrapContext( this.getClass().getClassLoader() ).createMarshaller();

        }

        return this.bootstrapMarshaller;
    }

    /**
     * Gets the bootstrap JAXB unmarshaller of the instance.
     *
     * @return The bootstrap JAXB unmarshaller of the instance.
     *
     * @throws BootstrapException if creating an unmarshaller fails.
     */
    protected Unmarshaller getBootstrapUnmarshaller() throws BootstrapException
    {
        if ( this.bootstrapUnmarshaller == null )
        {
            this.bootstrapUnmarshaller =
                BootstrapContext.createBootstrapContext( this.getClass().getClassLoader() ).createUnmarshaller();

        }

        return this.bootstrapUnmarshaller;
    }

    /**
     * Gets the bootstrap JAXP schema of the instance.
     *
     * @return The bootstrap JAXP schema of the instance.
     *
     * @throws BootstrapException if parsing schema resources fails.
     */
    protected javax.xml.validation.Schema getBootstrapSchema() throws BootstrapException
    {
        if ( this.bootstrapSchema == null )
        {
            this.bootstrapSchema =
                BootstrapContext.createBootstrapContext( this.getClass().getClassLoader() ).createSchema();

        }

        return this.bootstrapSchema;
    }

    public boolean canTransformResource( final String arg )
    {
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
        if ( this.schemaResources != null )
        {
            for ( String r : this.schemaResources )
            {
                if ( arg.endsWith( r ) )
                {
                    this.currentResourceType = ResourceType.BOOTSTRAP_OBJECT_RESOURCE;
                    return true;
                }
            }
        }
        if ( this.serviceResources != null )
        {
            for ( String r : this.serviceResources )
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
                        this.modules.getModule().addAll( ( (Modules) modelObject ).getModule() );
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
                        this.schemas.getSchema().addAll( ( (Schemas) bootstrapObject ).getSchema() );
                    }
                    if ( bootstrapObject instanceof Schema )
                    {
                        this.schemas.getSchema().add( (Schema) bootstrapObject );
                    }
                    if ( bootstrapObject instanceof Services )
                    {
                        this.services.getService().addAll( ( (Services) bootstrapObject ).getService() );
                    }
                    if ( bootstrapObject instanceof Service )
                    {
                        this.services.getService().add( (Service) bootstrapObject );
                    }

                    break;

                default:
                    throw new AssertionError( "" + this.currentResourceType );

            }
        }
        catch ( final JAXBException e )
        {
            throw new IOException( e.getMessage(), e );
        }
        catch ( final BootstrapException e )
        {
            throw new IOException( e.getMessage(), e );
        }
        catch ( final ModelException e )
        {
            throw new IOException( e.getMessage(), e );
        }
    }

    public void processResource( final String name, final InputStream in, final List relocators ) throws IOException
    {
        this.processResource( in );
    }

    public boolean hasTransformedResource()
    {
        return !( this.modules.getModule().isEmpty() && this.schemas.getSchema().isEmpty() &&
                  this.services.getService().isEmpty() );

    }

    public void modifyOutputStream( final JarOutputStream out ) throws IOException
    {
        try
        {
            if ( !this.modules.getModule().isEmpty() )
            {
                if ( this.moduleIncludes != null )
                {
                    for ( final Iterator<Module> it = this.modules.getModule().iterator(); it.hasNext(); )
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

                out.putNextEntry( new JarEntry( this.schemasResource ) );

                final Marshaller m = this.getBootstrapMarshaller();
                m.setSchema( this.getBootstrapSchema() );
                m.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
                m.marshal( bootstrapObjectFactory.createSchemas( copy ), out );
            }

            if ( !this.services.getService().isEmpty() )
            {
                final org.jomc.model.bootstrap.ObjectFactory bootstrapObjectFactory =
                    new org.jomc.model.bootstrap.ObjectFactory();

                Services copy = new Services( this.services );

                if ( this.bootstrapObjectStylesheet != null )
                {
                    final Transformer transformer = TransformerFactory.newInstance().newTransformer(
                        new StreamSource( this.bootstrapObjectStylesheet ) );

                    final JAXBSource source =
                        new JAXBSource( this.getBootstrapMarshaller(), bootstrapObjectFactory.createServices( copy ) );

                    final JAXBResult result = new JAXBResult( this.getBootstrapUnmarshaller() );
                    transformer.transform( source, result );
                    copy = ( (JAXBElement<Services>) result.getResult() ).getValue();
                }

                out.putNextEntry( new JarEntry( this.servicesResource ) );

                final Marshaller m = this.getBootstrapMarshaller();
                m.setSchema( this.getBootstrapSchema() );
                m.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
                m.marshal( bootstrapObjectFactory.createServices( copy ), out );
            }
        }
        catch ( final TransformerConfigurationException e )
        {
            throw new IOException( e.getMessage(), e );
        }
        catch ( final TransformerException e )
        {
            throw new IOException( e.getMessage(), e );
        }
        catch ( final JAXBException e )
        {
            throw new IOException( e.getMessage(), e );
        }
        catch ( final BootstrapException e )
        {
            throw new IOException( e.getMessage(), e );
        }
        catch ( final ModelException e )
        {
            throw new IOException( e.getMessage(), e );
        }
    }

}
