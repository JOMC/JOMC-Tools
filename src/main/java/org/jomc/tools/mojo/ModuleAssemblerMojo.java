/*
 *   Copyright (c) 2009 The JOMC Project
 *   Copyright (c) 2005 Christian Schulte <cs@schulte.it>
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

/**
 * Assembles modules.
 *
 * @author <a href="mailto:cs@schulte.it">Christian Schulte</a>
 * @version $Id$
 *
 * @phase process-resources
 * @goal assemble-modules
 * @requiresDependencyResolution runtime
 */
public final class ModuleAssemblerMojo extends AbstractJomcMojo
{

    /**
     * Directory holding documents to merge.
     * @parameter default-value="src/main/jomc"
     * @optional
     */
    private File mergeDirectory;

    /**
     * Flag indicating if entities resolved from the classpath should be preserved in the assembled modules.
     * @parameter default-value="false"
     * @optional
     */
    private boolean includeClasspathModule;

    /**
     * File to write the assembled modules to.
     * @parameter default-value="${project.build.directory}/jomc/META-INF/jomc.xml"
     * @optional
     */
    private File modulesFile;

    @Override
    public void executeTool() throws Exception
    {
        this.getModuleAssemblerTool().assembleModules(
            this.modulesFile, this.mergeDirectory, this.getMainClassLoader(), this.includeClasspathModule );

    }

}
