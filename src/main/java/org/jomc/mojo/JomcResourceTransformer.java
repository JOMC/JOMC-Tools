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
import org.jomc.model.ModelObject;
import org.jomc.model.Module;
import org.jomc.model.Modules;
import org.jomc.modlet.DefaultModelContext;
import org.jomc.modlet.DefaultModletProvider;
import org.jomc.modlet.ModelContext;
import org.jomc.modlet.ModelException;
import org.jomc.modlet.Modlet;
import org.jomc.modlet.ModletObject;
import org.jomc.modlet.Modlets;

/**
 * Maven Shade Plugin {@code ResourceTransformer} implementation for assembling JOMC resources.
 * <p><b>Usage</b><pre>
 * &lt;transformer implementation="org.jomc.mojo.JomcResourceTransformer"&gt;
 *   &lt;model&gt;http://jomc.org/model&lt;/model&gt;
 *   &lt;moduleEncoding&gt;${project.build.sourceEncoding}&lt;/moduleEncoding&gt;
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
 *   &lt;modletEncoding&gt;${project.build.sourceEncoding}&lt;/modletEncoding&gt;
 *   &lt;modletName&gt;${project.name}&lt;/modletName&gt;
 *   &lt;modletVersion&gt;${project.version}&lt;/modletVersion&gt;
 *   &lt;modletVendor&gt;${project.organization.name}&lt;/modletVendor&gt;
 *   &lt;modletResource&gt;META-INF/custom-jomc-modlet.xml&lt;/modletResource&gt;
 *   &lt;modletResources&gt;
 *     &lt;modletResource&gt;META-INF/jomc-modlet.xml&lt;/modletResource&gt;
 *   &lt;/modletResources&gt;
 *   &lt;modletIncludes&gt;
 *     &lt;modletInclude&gt;modlet name&lt;/modletInclude&gt;
 *   &lt;/modletIncludes&gt;
 *   &lt;modletExcludes&gt;
 *     &lt;modletExclude&gt;modlet name&lt;/modletExclude&gt;
 *   &lt;/modletExcludes&gt;
 *   &lt;modelObjectStylesheet&gt;Filename of a style sheet to use for transforming the merged model document.&lt;/modelObjectStylesheet&gt;
 *   &lt;modletObjectStylesheet&gt;Filename of a style sheet to use for transforming the merged modlet document.&lt;/modletObjectStylesheet&gt;
 *   &lt;providerLocation&gt;META-INF/custom-services&lt;/providerLocation&gt;
 *   &lt;platformProviderLocation&gt;${java.home}/jre/lib/custom-jomc.properties&lt;/platformProviderLocation&gt;
 *   &lt;modletLocation&gt;META-INF/custom-jomc-modlet.xml&lt;/modletLocation&gt;
 *   &lt;modletSchemaSystemId&gt;http://custom.host.tld/custom/path/jomc-modlet-1.0.xsd&lt;/modletSchemaSystemId&gt;
 * &lt;/transformer&gt;
 * </pre></p>
 *
 * @author <a href="mailto:schulte2005@users.sourceforge.net">Christian Schulte</a>
 * @version $Id$
 */
public class JomcResourceTransformer implements ResourceTransformer
{

    /** Type of a resource. */
    private enum ResourceType
    {

        MODEL_OBJECT_RESOURCE,
        MODLET_OBJECT_RESOURCE,
        UNKNOWN_RESOURCE

    }

    /** The identifier of the model to process. */
    private String model = ModelObject.MODEL_PUBLIC_ID;

    /** The encoding of the assembled module. */
    private String moduleEncoding;

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

    /** Included modules. */
    private List<String> moduleIncludes;

    /** Excluded modules. */
    private List<String> moduleExcludes;

    /** The encoding of the assembled modlet. */
    private String modletEncoding;

    /** The name of the assembled modlet. */
    private String modletName;

    /** The version of the assembled modlet. */
    private String modletVersion;

    /** The vendor of the assembled modlet. */
    private String modletVendor;

    /** The resource name of the assembled modlet resources. */
    private String modletResource = "META-INF/jomc-modlet.xml";

    /** Names of modet resources to process. */
    private String[] modletResources =
    {
        "META-INF/jomc-modlet.xml"
    };

    /** Included modlets. */
    private List<String> modletIncludes;

    /** Excluded modlets. */
    private List<String> modletExcludes;

    /** Model object style sheet to apply. */
    private File modelObjectStylesheet;

    /** Bootstrap object style sheet to apply. */
    private File modletObjectStylesheet;

    /** The location to search for providers. */
    private String providerLocation;

    /** The location to search for platform providers. */
    private String platformProviderLocation;

    /** The system id of the modlet schema. */
    private String modletSchemaSystemId;

    /** The location to search for modlets. */
    private String modletLocation;

    /** Modlet resources. */
    private final Modlets modlets = new Modlets();

    /** Model resources. */
    private final Modules modules = new Modules();

    /** Type of the currently processed resource. */
    private ResourceType currentResourceType = ResourceType.UNKNOWN_RESOURCE;

    /** The JOMC JAXB marshaller of the instance. */
    private Marshaller jomcMarshaller;

    /** The JOMC JAXB unmarshaller of the instance. */
    private Unmarshaller jomcUnmarshaller;

    /** The modlet JAXB marshaller of the instance. */
    private Marshaller modletMarshaller;

    /** The modlet JAXB unmarshaller of the instance. */
    private Unmarshaller modletUnmarshaller;

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
        if ( this.modletResources != null )
        {
            for ( String r : this.modletResources )
            {
                if ( arg.endsWith( r ) )
                {
                    this.currentResourceType = ResourceType.MODLET_OBJECT_RESOURCE;
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

                case MODLET_OBJECT_RESOURCE:
                    Object modletObject = this.unmarshalModletObject( in );

                    if ( modletObject instanceof JAXBElement )
                    {
                        modletObject = ( (JAXBElement) modletObject ).getValue();
                    }
                    if ( modletObject instanceof Modlets )
                    {
                        this.modlets.getModlet().addAll( ( (Modlets) modletObject ).getModlet() );
                    }
                    if ( modletObject instanceof Modlet )
                    {
                        this.modlets.getModlet().add( (Modlet) modletObject );
                    }

                    break;

                default:
                    throw new AssertionError( "" + this.currentResourceType );

            }
        }
        catch ( final JAXBException e )
        {
            String message = e.getMessage();
            if ( message == null && e.getLinkedException() != null )
            {
                message = e.getLinkedException().getMessage();
            }

            throw (IOException) new IOException( message ).initCause( e );
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
        return !( this.modules.getModule().isEmpty() && this.modlets.getModlet().isEmpty() );
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

                final Module mergedModule = this.modules.getMergedModule( this.moduleName );
                mergedModule.setVersion( this.moduleVersion );
                mergedModule.setVendor( this.moduleVendor );

                final JAXBElement<Module> transformedModule = this.transformModelObject(
                    new org.jomc.model.ObjectFactory().createModule( mergedModule ) );

                out.putNextEntry( new JarEntry( this.moduleResource ) );
                this.marshalModelObject( transformedModule, out );
            }

            if ( !this.modlets.getModlet().isEmpty() )
            {
                if ( this.modletIncludes != null )
                {
                    for ( final Iterator<Modlet> it = this.modlets.getModlet().iterator(); it.hasNext(); )
                    {
                        if ( !this.modletIncludes.contains( it.next().getName() ) )
                        {
                            it.remove();
                        }
                    }
                }

                if ( this.modletExcludes != null )
                {
                    for ( String exclude : this.modletExcludes )
                    {
                        final Modlet excluded = this.modlets.getModlet( exclude );
                        if ( excluded != null )
                        {
                            this.modlets.getModlet().remove( excluded );
                        }
                    }
                }

                final Modlet mergedModlet = this.modlets.getMergedModlet( this.modletName, this.model );
                mergedModlet.setVendor( this.modletVendor );
                mergedModlet.setVersion( this.modletVersion );

                final JAXBElement<Modlet> transformedModlet = this.transformModletObject(
                    new org.jomc.modlet.ObjectFactory().createModlet( mergedModlet ) );

                out.putNextEntry( new JarEntry( this.modletResource ) );
                this.marshalModletObject( transformedModlet, out );
            }
        }
        catch ( final TransformerConfigurationException e )
        {
            String message = e.getMessage();
            if ( message == null && e.getException() != null )
            {
                message = e.getException().getMessage();
            }

            throw (IOException) new IOException( message ).initCause( e );
        }
        catch ( final TransformerException e )
        {
            String message = e.getMessage();
            if ( message == null && e.getException() != null )
            {
                message = e.getException().getMessage();
            }

            throw (IOException) new IOException( message ).initCause( e );
        }
        catch ( final JAXBException e )
        {
            String message = e.getMessage();
            if ( message == null && e.getLinkedException() != null )
            {
                message = e.getLinkedException().getMessage();
            }

            throw (IOException) new IOException( message ).initCause( e );
        }
        catch ( final ModelException e )
        {
            throw (IOException) new IOException( e.getMessage() ).initCause( e );
        }
    }

    private void setupJomc()
    {
        ModelContext.setDefaultModletSchemaSystemId( this.modletSchemaSystemId );
        DefaultModelContext.setDefaultPlatformProviderLocation( this.platformProviderLocation );
        DefaultModelContext.setDefaultProviderLocation( this.providerLocation );
        DefaultModletProvider.setDefaultModletLocation( this.modletLocation );
    }

    private void resetJomc()
    {
        ModelContext.setDefaultModletSchemaSystemId( null );
        DefaultModelContext.setDefaultPlatformProviderLocation( null );
        DefaultModelContext.setDefaultProviderLocation( null );
        DefaultModletProvider.setDefaultModletLocation( null );
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
                this.jomcUnmarshaller = modelContext.createUnmarshaller( this.model );
                this.jomcUnmarshaller.setSchema( modelContext.createSchema( this.model ) );
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
                this.jomcMarshaller = modelContext.createMarshaller( this.model );
                this.jomcMarshaller.setSchema( modelContext.createSchema( this.model ) );
                this.jomcMarshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );

                if ( this.moduleEncoding != null )
                {
                    this.jomcMarshaller.setProperty( Marshaller.JAXB_ENCODING, this.moduleEncoding );
                }
            }
            finally
            {
                this.resetJomc();
            }
        }

        this.jomcMarshaller.marshal( element, out );
    }

    private <T> JAXBElement<T> transformModelObject( final JAXBElement<T> element )
        throws ModelException, TransformerException, JAXBException
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
                final Marshaller marshaller = modelContext.createMarshaller( this.model );
                final Unmarshaller unmarshaller = modelContext.createUnmarshaller( this.model );
                final javax.xml.validation.Schema schema = modelContext.createSchema( this.model );
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

    private Object unmarshalModletObject( final InputStream in ) throws ModelException, JAXBException
    {
        if ( in == null )
        {
            throw new NullPointerException( "in" );
        }

        if ( this.modletUnmarshaller == null )
        {
            try
            {
                this.setupJomc();

                final ModelContext modletContext =
                    ModelContext.createModelContext( this.getClass().getClassLoader() );

                this.modletUnmarshaller = modletContext.createUnmarshaller( ModletObject.MODEL_PUBLIC_ID );
                this.modletUnmarshaller.setSchema( modletContext.createSchema( ModletObject.MODEL_PUBLIC_ID ) );
            }
            finally
            {
                this.resetJomc();
            }
        }

        return this.modletUnmarshaller.unmarshal( in );
    }

    private void marshalModletObject( final JAXBElement<? extends ModletObject> element, final OutputStream out )
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

        if ( this.modletMarshaller == null )
        {
            try
            {
                this.setupJomc();

                final ModelContext modletContext =
                    ModelContext.createModelContext( this.getClass().getClassLoader() );

                this.modletMarshaller = modletContext.createMarshaller( ModletObject.MODEL_PUBLIC_ID );
                this.modletMarshaller.setSchema( modletContext.createSchema( ModletObject.MODEL_PUBLIC_ID ) );
                this.modletMarshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );

                if ( this.modletEncoding != null )
                {
                    this.modletMarshaller.setProperty( Marshaller.JAXB_ENCODING, this.modletEncoding );
                }
            }
            finally
            {
                this.resetJomc();
            }
        }

        this.modletMarshaller.marshal( element, out );
    }

    private <T> JAXBElement<T> transformModletObject( final JAXBElement<T> element )
        throws ModelException, TransformerException, JAXBException
    {
        if ( element == null )
        {
            throw new NullPointerException( "element" );
        }

        JAXBElement<T> transformed = element;

        if ( this.modletObjectStylesheet != null )
        {
            try
            {
                this.setupJomc();
                final Transformer transformer = TransformerFactory.newInstance().newTransformer(
                    new StreamSource( this.modletObjectStylesheet ) );

                final ModelContext modletContext =
                    ModelContext.createModelContext( this.getClass().getClassLoader() );

                final Marshaller marshaller = modletContext.createMarshaller( ModletObject.MODEL_PUBLIC_ID );
                final Unmarshaller unmarshaller = modletContext.createUnmarshaller( ModletObject.MODEL_PUBLIC_ID );
                final javax.xml.validation.Schema schema = modletContext.createSchema( ModletObject.MODEL_PUBLIC_ID );
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
