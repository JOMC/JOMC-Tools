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
package org.jomc.tools.mojo;

import java.io.File;
import java.util.Arrays;
import org.jomc.model.Module;
import org.jomc.tools.ModelObjectRelocation;
import org.jomc.tools.ModelObjectRelocator;
import org.jomc.tools.ModuleAssembler;

/**
 * Assembles modules.
 *
 * @author <a href="mailto:schulte2005@users.sourceforge.net">Christian Schulte</a>
 * @version $Id$
 *
 * @phase process-resources
 * @goal assemble-modules
 * @requiresDependencyResolution runtime
 */
public final class ModuleAssemblerMojo extends AbstractJomcMojo
{

    /**
     * File to write the assembled module to.
     * @parameter default-value="${project.build.directory}/jomc/META-INF/jomc.xml"
     */
    private File moduleFile;

    /**
     * Name of the merged module.
     * @parameter default-value="${project.name}"
     */
    private String moduleName;

    /**
     * Version of the merged module.
     * @parameter default-value="${project.version}"
     */
    private String moduleVersion;

    /**
     * Vendor of the merged module.
     * @parameter default-value="${project.organization.name}"
     */
    private String moduleVendor;

    /**
     * Directory holding documents to merge.
     * @parameter default-value="src/main/jomc"
     */
    private File mergeDirectory;

    /**
     * Model object relocations.
     * @parameter
     */
    private ModelObjectRelocation[] modelObjectRelocations;

    @Override
    public void executeTool() throws Exception
    {
        final ModuleAssembler assembler = this.getModuleAssemblerTool();
        ModelObjectRelocator relocator = null;

        if ( this.modelObjectRelocations != null )
        {
            relocator = new ModelObjectRelocator();
            relocator.getModelObjectRelocations().addAll( Arrays.asList( this.modelObjectRelocations ) );
        }

        final Module mergedModule = assembler.mergeModules( this.moduleName, this.moduleVersion, this.moduleVendor,
                                                            this.mergeDirectory, relocator );

        assembler.getModelManager().getMarshaller( true, true ).marshal(
            assembler.getModelManager().getObjectFactory().createModule( mergedModule ), this.moduleFile );

    }

}
