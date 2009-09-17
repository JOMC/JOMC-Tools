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
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
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
 *   &lt;moduleName&gt;${pom.name}&lt;/moduleName&gt;
 *   &lt;moduleVersion&gt;${pom.version}&lt;/moduleVersion&gt;
 *   &lt;moduleVendor&gt;${pom.organization.name}&lt;/moduleVendor&gt;
 *   &lt;moduleResource&gt;META-INF/jomc-something-else.xml&lt;/moduleResource&gt;
 *   &lt;moduleResources&gt;
 *     &lt;moduleResource&gt;META-INF/jomc.xml&lt;/moduleResource&gt;
 *   &lt;/moduleResources&gt;
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
        BOOTSTRAP_OBJECT_RESOURCE

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

    /** Bootstrap resources. */
    private final Schemas schemas = new Schemas();

    /** Model resources. */
    private final Modules modules = new Modules();

    /** Type of the currently processed resource. */
    private ResourceType currentResourceType;

    /** The model manager of the instance. */
    private DefaultModelManager modelManager;

    /**
     * Gets the {@code ModelManager} of the instance.
     *
     * @return The {@code ModelManager} of the instance.
     */
    public DefaultModelManager getModelManager()
    {
        if ( this.modelManager == null )
        {
            this.modelManager = new DefaultModelManager();
        }

        return this.modelManager;
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

        this.currentResourceType = null;
        return false;
    }

    public void processResource( final InputStream in ) throws IOException
    {
        try
        {
            switch ( this.currentResourceType )
            {
                case MODEL_OBJECT_RESOURCE:
                    Object modelObject = this.getModelManager().getUnmarshaller( true ).unmarshal( in );
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
                    Object bootstrapObject = this.getModelManager().getBootstrapUnmarshaller( true ).unmarshal( in );
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
        catch ( SAXException e )
        {
            throw (IOException) new IOException( e.getMessage() ).initCause( e );
        }
        catch ( JAXBException e )
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
                Module mergedModule = this.modules.getMergedModule();
                mergedModule.setName( this.moduleName );
                mergedModule.setVersion( this.moduleVersion );
                mergedModule.setVendor( this.moduleVendor );

                if ( this.modelObjectStylesheet != null )
                {
                    final Transformer transformer = TransformerFactory.newInstance().newTransformer(
                        new StreamSource( this.modelObjectStylesheet ) );

                    mergedModule = this.getModelManager().transformModelObject(
                        this.getModelManager().getObjectFactory().createModule( mergedModule ), transformer );

                }

                out.putNextEntry( new JarEntry( this.moduleResource ) );
                this.getModelManager().getMarshaller( true, true ).marshal(
                    this.getModelManager().getObjectFactory().createModule( mergedModule ), out );

            }

            if ( !this.schemas.getSchema().isEmpty() )
            {
                Schemas copy = new Schemas( this.schemas );

                if ( this.bootstrapObjectStylesheet != null )
                {
                    final Transformer transformer = TransformerFactory.newInstance().newTransformer(
                        new StreamSource( this.bootstrapObjectStylesheet ) );

                    copy = this.getModelManager().transformBootstrapObject(
                        this.getModelManager().getBootstrapObjectFactory().createSchemas( copy ), transformer );

                }

                out.putNextEntry( new JarEntry( this.bootstrapResource ) );
                this.getModelManager().getBootstrapMarshaller( true, true ).marshal(
                    this.getModelManager().getBootstrapObjectFactory().createSchemas( copy ), out );

            }
        }
        catch ( TransformerConfigurationException e )
        {
            throw (IOException) new IOException( e.getMessage() ).initCause( e );
        }
        catch ( TransformerException e )
        {
            throw (IOException) new IOException( e.getMessage() ).initCause( e );
        }
        catch ( SAXException e )
        {
            throw (IOException) new IOException( e.getMessage() ).initCause( e );
        }
        catch ( JAXBException e )
        {
            throw (IOException) new IOException( e.getMessage() ).initCause( e );
        }
    }

}
