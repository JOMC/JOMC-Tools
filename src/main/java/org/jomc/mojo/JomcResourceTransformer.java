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
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
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
import org.jomc.model.ModelObject;
import org.jomc.model.Module;
import org.jomc.model.Modules;
import org.jomc.model.bootstrap.BootstrapContext;
import org.jomc.model.bootstrap.BootstrapException;
import org.jomc.model.bootstrap.BootstrapObject;
import org.jomc.model.bootstrap.DefaultBootstrapContext;
import org.jomc.model.bootstrap.DefaultSchemaProvider;
import org.jomc.model.bootstrap.DefaultServiceProvider;
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
 *   &lt;moduleResource&gt;META-INF/custom-jomc.xml&lt;/moduleResource&gt;
 *   &lt;moduleResources&gt;
 *     &lt;moduleResource&gt;META-INF/jomc.xml&lt;/moduleResource&gt;
 *   &lt;/moduleResources&gt;
 *   &lt;moduleIncludes&gt;
 *     &lt;moduleInclude&gt;module name&lt;/moduleInclude&gt;
 *   &lt;/moduleIncludes&gt;
 *   &lt;moduleExcludes&gt;
 *     &lt;moduleExclude&gt;module name&lt;/moduleExclude&gt;
 *   &lt;/moduleExcludes&gt;
 *   &lt;schemasResource&gt;META-INF/custom-jomc-schemas.xml&lt;/schemasResource&gt;
 *   &lt;schemaResources&gt;
 *     &lt;schemaResource&gt;META-INF/jomc-schemas.xml&lt;/schemaResource&gt;
 *   &lt;/schemaResources&gt;
 *   &lt;servicesResource&gt;META-INF/custom-jomc-services.xml&lt;/servicesResource&gt;
 *   &lt;serviceResources&gt;
 *     &lt;serviceResource&gt;META-INF/jomc-services.xml&lt;/serviceResource&gt;
 *   &lt;/serviceResources&gt;
 *   &lt;modelObjectStylesheet&gt;Filename of a style sheet to use for transforming the merged model document.&lt;/modelObjectStylesheet&gt;
 *   &lt;bootstrapObjectStylesheet&gt;Filename of a style sheet to use for transforming the merged bootstrap document.&lt;/bootstrapObjectStylesheet&gt;
 *   &lt;providerLocation&gt;META-INF/custom-services&lt;/providerLocation&gt;
 *   &lt;platformProviderLocation&gt;${java.home}/jre/lib/custom-jomc.properties&lt;/platformProviderLocation&gt;
 *   &lt;serviceLocation&gt;META-INF/custom-jomc-services.xml&lt;/serviceLocation&gt;
 *   &lt;schemaLocation&gt;META-INF/custom-jomc-schemas.xml&lt;/schemaLocation&gt;
 *   &lt;bootstrapSchemaSystemId&gt;http://custom.host.tld/custom/path/jomc-bootstrap-1.0.xsd&lt;/bootstrapSchemaSystemId&gt;
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

    /** The location to search for providers. */
    private String providerLocation;

    /** The location to search for platform providers. */
    private String platformProviderLocation;

    /** The system id of the bootstrap schema. */
    private String bootstrapSchemaSystemId;

    /** The location to search for services. */
    private String serviceLocation;

    /** The location to search for schemas. */
    private String schemaLocation;

    /** Schemas resources. */
    private final Schemas schemas = new Schemas();

    /** Services resources. */
    private final Services services = new Services();

    /** Model resources. */
    private final Modules modules = new Modules();

    /** Type of the currently processed resource. */
    private ResourceType currentResourceType = ResourceType.UNKNOWN_RESOURCE;

    /** The JOMC JAXB marshaller of the instance. */
    private Marshaller jomcMarshaller;

    /** The JOMC JAXB unmarshaller of the instance. */
    private Unmarshaller jomcUnmarshaller;

    /** The bootstrap JAXB marshaller of the instance. */
    private Marshaller bootstrapMarshaller;

    /** The bootstrap JAXB unmarshaller of the instance. */
    private Unmarshaller bootstrapUnmarshaller;

    /** Creates a new {@code JomcResourceTransformer} instance. */
    public JomcResourceTransformer()
    {
        super();
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
                    Object modelObject = this.unmarshalModelObject( in );

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
                    Object bootstrapObject = this.unmarshalBootstrapObject( in );

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
            throw (IOException) new IOException( e.getMessage() ).initCause( e );
        }
        catch ( final BootstrapException e )
        {
            throw (IOException) new IOException( e.getMessage() ).initCause( e );
        }
        catch ( final ModelException e )
        {
            throw (IOException) new IOException( e.getMessage() ).initCause( e );
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

                final Module mergedModule = this.modules.getMergedModule();
                mergedModule.setName( this.moduleName );
                mergedModule.setVersion( this.moduleVersion );
                mergedModule.setVendor( this.moduleVendor );

                final JAXBElement<Module> transformedModule = this.transformModelObject(
                    new org.jomc.model.ObjectFactory().createModule( mergedModule ) );

                out.putNextEntry( new JarEntry( this.moduleResource ) );
                this.marshalModelObject( transformedModule, out );
            }

            if ( !this.schemas.getSchema().isEmpty() )
            {
                final JAXBElement<Schemas> transformedSchemas = this.transformBootstrapObject(
                    new org.jomc.model.bootstrap.ObjectFactory().createSchemas( new Schemas( this.schemas ) ) );

                out.putNextEntry( new JarEntry( this.schemasResource ) );
                this.marshalBootstrapObject( transformedSchemas, out );
            }

            if ( !this.services.getService().isEmpty() )
            {
                final JAXBElement<Services> transformedServices = this.transformBootstrapObject(
                    new org.jomc.model.bootstrap.ObjectFactory().createServices( new Services( this.services ) ) );

                out.putNextEntry( new JarEntry( this.servicesResource ) );
                this.marshalBootstrapObject( transformedServices, out );
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
        catch ( final JAXBException e )
        {
            throw (IOException) new IOException( e.getMessage() ).initCause( e );
        }
        catch ( final BootstrapException e )
        {
            throw (IOException) new IOException( e.getMessage() ).initCause( e );
        }
        catch ( final ModelException e )
        {
            throw (IOException) new IOException( e.getMessage() ).initCause( e );
        }
    }

    private void setupJomc()
    {
        DefaultBootstrapContext.setDefaultBootstrapSchemaSystemId( this.bootstrapSchemaSystemId );
        DefaultBootstrapContext.setDefaultPlatformProviderLocation( this.platformProviderLocation );
        DefaultBootstrapContext.setDefaultProviderLocation( this.providerLocation );
        DefaultSchemaProvider.setDefaultSchemaLocation( this.schemaLocation );
        DefaultServiceProvider.setDefaultServiceLocation( this.serviceLocation );
    }

    private void resetJomc()
    {
        DefaultBootstrapContext.setDefaultBootstrapSchemaSystemId( null );
        DefaultBootstrapContext.setDefaultPlatformProviderLocation( null );
        DefaultBootstrapContext.setDefaultProviderLocation( null );
        DefaultSchemaProvider.setDefaultSchemaLocation( null );
        DefaultServiceProvider.setDefaultServiceLocation( null );
    }

    private Object unmarshalModelObject( final InputStream in ) throws ModelException, JAXBException
    {
        if ( in == null )
        {
            throw new NullPointerException( "in" );
        }

        if ( this.jomcUnmarshaller == null )
        {
            try
            {
                this.setupJomc();
                final ModelContext modelContext = ModelContext.createModelContext( this.getClass().getClassLoader() );
                this.jomcUnmarshaller = modelContext.createUnmarshaller();
                this.jomcUnmarshaller.setSchema( modelContext.createSchema() );
            }
            finally
            {
                this.resetJomc();
            }
        }

        return this.jomcUnmarshaller.unmarshal( in );
    }

    private void marshalModelObject( final JAXBElement<? extends ModelObject> element, final OutputStream out )
        throws ModelException, JAXBException
    {
        if ( element == null )
        {
            throw new NullPointerException( "element" );
        }
        if ( out == null )
        {
            throw new NullPointerException( "out" );
        }

        if ( this.jomcMarshaller == null )
        {
            try
            {
                this.setupJomc();
                final ModelContext modelContext = ModelContext.createModelContext( this.getClass().getClassLoader() );
                this.jomcMarshaller = modelContext.createMarshaller();
                this.jomcMarshaller.setSchema( modelContext.createSchema() );
                this.jomcMarshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
            }
            finally
            {
                this.resetJomc();
            }
        }

        this.jomcMarshaller.marshal( element, out );
    }

    private <T> JAXBElement<T> transformModelObject( final JAXBElement<T> element )
        throws TransformerException, JAXBException, ModelException
    {
        if ( element == null )
        {
            throw new NullPointerException( "element" );
        }

        JAXBElement<T> transformed = element;

        if ( this.modelObjectStylesheet != null )
        {
            try
            {
                this.setupJomc();
                final Transformer transformer = TransformerFactory.newInstance().newTransformer(
                    new StreamSource( this.modelObjectStylesheet ) );

                final ModelContext modelContext = ModelContext.createModelContext( this.getClass().getClassLoader() );
                final Marshaller marshaller = modelContext.createMarshaller();
                final Unmarshaller unmarshaller = modelContext.createUnmarshaller();
                final javax.xml.validation.Schema schema = modelContext.createSchema();
                marshaller.setSchema( schema );
                marshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
                unmarshaller.setSchema( schema );

                final JAXBSource source = new JAXBSource( marshaller, element );
                final JAXBResult result = new JAXBResult( unmarshaller );
                transformer.transform( source, result );
                transformed = (JAXBElement<T>) result.getResult();
            }
            finally
            {
                this.resetJomc();
            }
        }

        return transformed;
    }

    private Object unmarshalBootstrapObject( final InputStream in ) throws BootstrapException, JAXBException
    {
        if ( in == null )
        {
            throw new NullPointerException( "in" );
        }

        if ( this.bootstrapUnmarshaller == null )
        {
            try
            {
                this.setupJomc();

                final BootstrapContext bootstrapContext =
                    BootstrapContext.createBootstrapContext( this.getClass().getClassLoader() );

                this.bootstrapUnmarshaller = bootstrapContext.createUnmarshaller();
                this.bootstrapUnmarshaller.setSchema( bootstrapContext.createSchema() );
            }
            finally
            {
                this.resetJomc();
            }
        }

        return this.bootstrapUnmarshaller.unmarshal( in );
    }

    private void marshalBootstrapObject( final JAXBElement<? extends BootstrapObject> element, final OutputStream out )
        throws BootstrapException, JAXBException
    {
        if ( element == null )
        {
            throw new NullPointerException( "element" );
        }
        if ( out == null )
        {
            throw new NullPointerException( "out" );
        }

        if ( this.bootstrapMarshaller == null )
        {
            try
            {
                this.setupJomc();

                final BootstrapContext bootstrapContext =
                    BootstrapContext.createBootstrapContext( this.getClass().getClassLoader() );

                this.bootstrapMarshaller = bootstrapContext.createMarshaller();
                this.bootstrapMarshaller.setSchema( bootstrapContext.createSchema() );
                this.bootstrapMarshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
            }
            finally
            {
                this.resetJomc();
            }
        }

        this.bootstrapMarshaller.marshal( element, out );
    }

    private <T> JAXBElement<T> transformBootstrapObject( final JAXBElement<T> element )
        throws TransformerException, JAXBException, BootstrapException
    {
        if ( element == null )
        {
            throw new NullPointerException( "element" );
        }

        JAXBElement<T> transformed = element;

        if ( this.bootstrapObjectStylesheet != null )
        {
            try
            {
                this.setupJomc();
                final Transformer transformer = TransformerFactory.newInstance().newTransformer(
                    new StreamSource( this.bootstrapObjectStylesheet ) );

                final BootstrapContext bootstrapContext =
                    BootstrapContext.createBootstrapContext( this.getClass().getClassLoader() );

                final Marshaller marshaller = bootstrapContext.createMarshaller();
                final Unmarshaller unmarshaller = bootstrapContext.createUnmarshaller();
                final javax.xml.validation.Schema schema = bootstrapContext.createSchema();
                marshaller.setSchema( schema );
                marshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
                unmarshaller.setSchema( schema );

                final JAXBSource source = new JAXBSource( marshaller, element );
                final JAXBResult result = new JAXBResult( unmarshaller );
                transformer.transform( source, result );
                transformed = (JAXBElement<T>) result.getResult();
            }
            finally
            {
                this.resetJomc();
            }
        }

        return transformed;
    }

}
