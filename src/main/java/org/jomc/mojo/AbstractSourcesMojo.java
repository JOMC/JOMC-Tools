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
import java.net.URL;
import java.text.MessageFormat;
import java.util.ResourceBundle;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import org.apache.maven.plugin.MojoExecutionException;
import org.jomc.tools.model.SourceFilesType;
import org.jomc.tools.model.ToolsModel;
import org.xml.sax.SAXException;

/**
 * Base mojo class for managing source code.
 *
 * @author <a href="mailto:cs@jomc.org">Christian Schulte</a>
 * @version $Id$
 */
public abstract class AbstractSourcesMojo extends AbstractJomcMojo
{

    /**
     * The number of whitespace characters per indentation level.
     *
     * @parameter default-value="4"
     */
    private int whitespacesPerIndent;

    /**
     * The indentation character.
     *
     * @parameter default-value=" "
     */
    private char indentationCharacter;

    /**
     * Source files model to use for managing source code.
     *
     * @parameter
     */
    private String sourceFilesModel;

    /**
     * Gets the source files model to use for managing source code.
     *
     * @param classLoader The class loader to use for loading a source files model classpath resource.
     *
     * @return The source files model to use for managing source code.
     *
     * @throws NullPointerException if {@code classLoader} is {@code null}.
     * @throws MojoExecutionException if getting the source files model fails.
     */
    protected SourceFilesType getSourceFilesType( final ClassLoader classLoader ) throws MojoExecutionException
    {
        if ( classLoader == null )
        {
            throw new NullPointerException( "classLoader" );
        }

        try
        {
            if ( this.sourceFilesModel != null )
            {
                final Unmarshaller unmarshaller = ToolsModel.createUnmarshaller();
                unmarshaller.setSchema( ToolsModel.createSchema() );

                File f = new File( this.sourceFilesModel );
                if ( !f.isAbsolute() )
                {
                    f = new File( this.getMavenProject().getBasedir(), this.sourceFilesModel );
                }

                JAXBElement e = null;

                if ( f.exists() )
                {
                    e = (JAXBElement) unmarshaller.unmarshal( f );
                }

                if ( e == null )
                {
                    final URL rsrc = classLoader.getResource( this.sourceFilesModel );

                    if ( rsrc != null )
                    {
                        e = (JAXBElement) unmarshaller.unmarshal( rsrc );
                    }
                }

                if ( e == null )
                {
                    throw new MojoExecutionException( getMessage( "sourceFilesModelNotFound", this.sourceFilesModel ) );
                }

                if ( !ToolsModel.TOOLS_NS_URI.equals( e.getName().getNamespaceURI() ) ||
                     !e.getName().getLocalPart().equals( "source-files" ) )
                {
                    throw new MojoExecutionException( getMessage( "illegalSourceFilesModel", e.getName().toString() ) );
                }

                return ( (JAXBElement<SourceFilesType>) e ).getValue();
            }

            return null;
        }
        catch ( final JAXBException e )
        {
            throw new MojoExecutionException( e.getMessage(), e );
        }
        catch ( final SAXException e )
        {
            throw new MojoExecutionException( e.getMessage(), e );
        }
    }

    /**
     * Gets the number of whitespace characters per indentation level.
     *
     * @return The number of whitespace characters per indentation level.
     */
    protected int getWhitespacesPerIndent()
    {
        return this.whitespacesPerIndent;
    }

    /**
     * Gets the indentation character.
     *
     * @return The indentation character.
     */
    protected char getIndentationCharacter()
    {
        return this.indentationCharacter;
    }

    private static String getMessage( final String key, final Object... args )
    {
        return MessageFormat.format( ResourceBundle.getBundle(
            AbstractSourcesMojo.class.getName().replace( '.', '/' ) ).getString( key ), args );

    }

}
