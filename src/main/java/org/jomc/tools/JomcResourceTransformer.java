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
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
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
 * &lt;transformer implementation="org.jomc.tools.JomcResourceTransformer"&gt;
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
 *   &lt;modelObjectRelocations&gt;
 *     &lt;modelObjectRelocation&gt;
 *       &lt;sourcePattern&gt;some.prefix.to.relocate&lt;/sourcePattern&gt;
 *       &lt;replacementPattern&gt;some.prefix.to.relocate.that.prefix.to&lt;/replacementPattern&gt;
 *       &lt;exclusionPatterns&gt;
 *         &lt;exclusionPattern&gt;some.prefix.to.relocate.but.this&lt;/exclusionPattern&gt;
 *       &lt;/exclusionPatterns&gt;
 *     &lt;/modelObjectRelocation&gt;
 *   &lt;/modelObjectRelocations&gt;
 *   &lt;bootstrapObjectRelocations&gt;
 *     &lt;bootstrapObjectRelocation&gt;
 *       &lt;sourcePattern&gt;some.prefix.to.relocate&lt;/sourcePattern&gt;
 *       &lt;replacementPattern&gt;some.prefix.to.relocate.that.prefix.to&lt;/replacementPattern&gt;
 *       &lt;exclusionPatterns&gt;
 *         &lt;exclusionPattern&gt;some.prefix.to.relocate.but.this&lt;/exclusionPattern&gt;
 *       &lt;/exclusionPatterns&gt;
 *     &lt;/bootstrapObjectRelocation&gt;
 *     &lt;bootstrapObjectRelocation&gt;
 *       &lt;sourcePattern&gt;some/prefix/to/relocate&lt;/sourcePattern&gt;
 *       &lt;replacementPattern&gt;some/prefix/to/relocate/that/prefix/to&lt;/replacementPattern&gt;
 *       &lt;exclusionPatterns&gt;
 *         &lt;exclusionPattern&gt;some/prefix/to/relocate/but/this&lt;/exclusionPattern&gt;
 *       &lt;/exclusionPatterns&gt;
 *     &lt;/bootstrapObjectRelocation&gt;
 *   &lt;/bootstrapObjectRelocations&gt;
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
        BOOTSTRAP_OBJECT_RESOURCE

    }

    /** The name of the assembled module. */
    private String moduleName;

    /** The version of the assembled module. */
    private String moduleVersion;

    /** The vendor of the assembled module. */
    private String moduleVendor;

    /** The resource name of the assembled module. */
    private String moduleResource;

    /** Names of resources to process. */
    private String[] moduleResources;

    /** The resource name of the assembled bootstrap resources. */
    private String bootstrapResource;

    /** Names of bootstrap resources to process. */
    private String[] bootstrapResources;

    /** Directory holding documents to merge. */
    private File mergeDirectory;

    /** Model object relocations to apply. */
    private ModelObjectRelocation[] modelObjectRelocations;

    /** Bootstrap object relocations to apply. */
    private BootstrapObjectRelocation[] bootstrapObjectRelocations;

    /** The {@code ModuleAssembler} of the instance. */
    private ModuleAssembler moduleAssembler = new ModuleAssembler();

    /** The {@code DefaultModelManager} of the instance. */
    private DefaultModelManager defaultModelManager = new DefaultModelManager();

    /** Bootstrap schemas. */
    private Schemas bootstrapSchemas = new Schemas();

    /** Type of the currently processed resource. */
    private ResourceType currentResourceType;

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
                    Object modelObject = this.defaultModelManager.getUnmarshaller( true ).unmarshal( in );
                    if ( modelObject instanceof JAXBElement )
                    {
                        modelObject = ( (JAXBElement) modelObject ).getValue();
                    }
                    if ( modelObject instanceof Modules )
                    {
                        for ( Module m : ( (Modules) modelObject ).getModule() )
                        {
                            this.moduleAssembler.getModules().getModule().add( m );
                        }
                    }
                    if ( modelObject instanceof Module )
                    {
                        this.moduleAssembler.getModules().getModule().add( (Module) modelObject );
                    }
                    break;

                case BOOTSTRAP_OBJECT_RESOURCE:
                    Object bootstrapObject = this.defaultModelManager.getBootstrapUnmarshaller( true ).unmarshal( in );
                    if ( bootstrapObject instanceof JAXBElement )
                    {
                        bootstrapObject = ( (JAXBElement) bootstrapObject ).getValue();
                    }
                    if ( bootstrapObject instanceof Schemas )
                    {
                        for ( Schema s : ( (Schemas) bootstrapObject ).getSchema() )
                        {
                            this.bootstrapSchemas.getSchema().add( s );
                        }
                    }
                    if ( bootstrapObject instanceof Schema )
                    {
                        this.bootstrapSchemas.getSchema().add( (Schema) bootstrapObject );
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
        return !( this.moduleAssembler.getModules().getModule().isEmpty() &&
                  this.bootstrapSchemas.getSchema().isEmpty() );

    }

    public void modifyOutputStream( final JarOutputStream out ) throws IOException
    {
        try
        {
            if ( !this.moduleAssembler.getModules().getModule().isEmpty() )
            {
                ModelObjectRelocator relocator = null;
                if ( this.modelObjectRelocations != null )
                {
                    relocator = new ModelObjectRelocator();
                    relocator.getModelObjectRelocations().addAll( Arrays.asList( this.modelObjectRelocations ) );
                }

                final Module mergedModule = this.moduleAssembler.mergeModules(
                    this.moduleName, this.moduleVersion, this.moduleVendor, this.mergeDirectory, relocator );

                out.putNextEntry( new JarEntry( this.moduleResource ) );
                this.moduleAssembler.getModelManager().getMarshaller( true, true ).marshal(
                    this.moduleAssembler.getModelManager().getObjectFactory().createModule( mergedModule ), out );

            }
            if ( !this.bootstrapSchemas.getSchema().isEmpty() )
            {
                BootstrapObjectRelocator relocator = null;
                if ( this.bootstrapObjectRelocations != null )
                {
                    relocator = new BootstrapObjectRelocator();
                    relocator.getBootstrapObjectRelocations().addAll( Arrays.asList( this.bootstrapObjectRelocations ) );
                    this.bootstrapSchemas = relocator.relocateBootstrapObject( this.bootstrapSchemas, Schemas.class );
                }

                out.putNextEntry( new JarEntry( this.bootstrapResource ) );
                this.defaultModelManager.getBootstrapMarshaller( true, true ).marshal(
                    this.defaultModelManager.getBootstrapObjectFactory().createSchemas( this.bootstrapSchemas ), out );

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

}
