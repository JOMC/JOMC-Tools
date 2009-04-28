/*
 *  JOMC Maven Plugin
 *  Copyright (c) 2005 Christian Schulte <cs@schulte.it>
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 */
package org.jomc.tools.mojo;

import java.io.File;

/**
 * Manages a projects' main java classes.
 *
 * @author <a href="mailto:cs@schulte.it">Christian Schulte</a>
 * @version $Id$
 *
 * @phase process-classes
 * @goal main-java-classes
 * @requiresDependencyResolution runtime
 */
public final class MainJavaClassesMojo extends AbstractJomcMojo
{

    @Override
    public void executeTool() throws Exception
    {
        File classesDirectory = new File( this.getMavenProject().getBuild().getOutputDirectory() );
        if ( !classesDirectory.isAbsolute() )
        {
            classesDirectory = new File( this.getMavenProject().getBasedir(),
                                         this.getMavenProject().getBuild().getOutputDirectory() );

        }

        this.getMainJavaClassesTool().commitModuleClasses( classesDirectory );
    }

}
