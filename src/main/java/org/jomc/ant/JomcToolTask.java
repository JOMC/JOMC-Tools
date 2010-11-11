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
package org.jomc.ant;

import java.text.MessageFormat;
import java.util.ResourceBundle;
import java.util.logging.Level;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.jomc.model.Implementation;
import org.jomc.model.Module;
import org.jomc.model.Modules;
import org.jomc.model.Specification;
import org.jomc.model.modlet.ModelHelper;
import org.jomc.modlet.Model;
import org.jomc.tools.JomcTool;

/**
 * Base class for executing tool based tasks.
 *
 * @author <a href="mailto:schulte2005@users.sourceforge.net">Christian Schulte</a>
 * @version $Id$
 */
public class JomcToolTask extends JomcModelTask
{

    /** The default template profile to use when accessing templates. */
    private String defaultTemplateProfile;

    /** The encoding to use for reading files. */
    private String inputEncoding;

    /** The encoding to use for writing files. */
    private String outputEncoding;

    /** The encoding to use for reading templates. */
    private String templateEncoding;

    /** The template profile to use when accessing templates. */
    private String templateProfile;

    /** The indentation string ('\t' for tab). */
    private String indentation;

    /** The line separator ('\r\n' for DOS, '\r' for Mac, '\n' for Unix). */
    private String lineSeparator;

    /** The identifier of a specification to process. */
    private String specification;

    /** The identifier of an implementation to process. */
    private String implementation;

    /** The name of a module to process. */
    private String module;

    /** Creates a new {@code JomcToolTask} instance. */
    public JomcToolTask()
    {
        super();
    }

    /**
     * Gets the encoding to use for reading files.
     *
     * @return The encoding to use for reading files or {@code null}.
     *
     * @see #setInputEncoding(java.lang.String)
     */
    public final String getInputEncoding()
    {
        return this.inputEncoding;
    }

    /**
     * Sets the encoding to use for reading files.
     *
     * @param value The new encoding to use for reading files or {@code null}.
     *
     * @see #getInputEncoding()
     */
    public final void setInputEncoding( final String value )
    {
        this.inputEncoding = value;
    }

    /**
     * Gets the encoding to use for writing files.
     *
     * @return The encoding to use for writing files or {@code null}.
     *
     * @see #setOutputEncoding(java.lang.String)
     */
    public final String getOutputEncoding()
    {
        return this.outputEncoding;
    }

    /**
     * Sets the encoding to use for writing files.
     *
     * @param value The new encoding to use for writing files or {@code null}.
     *
     * @see #getOutputEncoding()
     */
    public final void setOutputEncoding( final String value )
    {
        this.outputEncoding = value;
    }

    /**
     * Gets the encoding to use for reading templates.
     *
     * @return The encoding to use for reading templates or {@code null}.
     *
     * @see #setTemplateEncoding(java.lang.String)
     */
    public final String getTemplateEncoding()
    {
        return this.templateEncoding;
    }

    /**
     * Sets the encoding to use for reading templates.
     *
     * @param value The new encoding to use for reading templates or {@code null}.
     *
     * @see #getTemplateEncoding()
     */
    public final void setTemplateEncoding( final String value )
    {
        this.templateEncoding = value;
    }

    /**
     * Gets the default template profile to use when accessing templates.
     *
     * @return The default template profile to use when accessing templates or {@code null}.
     *
     * @see #setDefaultTemplateProfile(java.lang.String)
     */
    public final String getDefaultTemplateProfile()
    {
        return this.defaultTemplateProfile;
    }

    /**
     * Sets the default template profile to use when accessing templates.
     *
     * @param value The new default template profile to use when accessing templates or {@code null}.
     *
     * @see #getDefaultTemplateProfile()
     */
    public final void setDefaultTemplateProfile( final String value )
    {
        this.defaultTemplateProfile = value;
    }

    /**
     * Gets the template profile to use when accessing templates.
     *
     * @return The template profile to use when accessing templates or {@code null}.
     *
     * @see #setTemplateProfile(java.lang.String)
     */
    public final String getTemplateProfile()
    {
        return this.templateProfile;
    }

    /**
     * Sets the template profile to use when accessing templates.
     *
     * @param value The new template profile to use when accessing templates or {@code null}.
     *
     * @see #getTemplateProfile()
     */
    public final void setTemplateProfile( final String value )
    {
        this.templateProfile = value;
    }

    /**
     * Gets the indentation string ('\t' for tab).
     *
     * @return The indentation string ('\t' for tab) or {@code null}.
     *
     * @see #setIndentation(java.lang.String)
     */
    public final String getIndentation()
    {
        return this.indentation;
    }

    /**
     * Sets the indentation string ('\t' for tab).
     *
     * @param value The new indentation string ('\t' for tab) or {@code null}.
     *
     * @see #getIndentation()
     */
    public final void setIndentation( final String value )
    {
        this.indentation = value;
    }

    /**
     * Gets the line separator ('\r\n' for DOS, '\r' for Mac, '\n' for Unix).
     *
     * @return The line separator ('\r\n' for DOS, '\r' for Mac, '\n' for Unix) or {@code null}.
     *
     * @see #setLineSeparator(java.lang.String)
     */
    public final String getLineSeparator()
    {
        return this.lineSeparator;
    }

    /**
     * Sets the line separator ('\r\n' for DOS, '\r' for Mac, '\n' for Unix).
     *
     * @param value The new line separator ('\r\n' for DOS, '\r' for Mac, '\n' for Unix) or {@code null}.
     *
     * @see #getLineSeparator()
     */
    public final void setLineSeparator( final String value )
    {
        this.lineSeparator = value;
    }

    /**
     * Gets the identifier of a specification to process.
     *
     * @return The identifier of a specification to process or {@code null}.
     *
     * @see #setSpecification(java.lang.String)
     */
    public final String getSpecification()
    {
        return this.specification;
    }

    /**
     * Sets the identifier of a specification to process.
     *
     * @param value The new identifier of a specification to process or {@code null}.
     *
     * @see #getSpecification()
     */
    public final void setSpecification( final String value )
    {
        this.specification = value;
    }

    /**
     * Gets the specification to process from a given model.
     *
     * @param model The model to get the specification to process from.
     *
     * @return The specification to process or {@code null}.
     *
     * @throws NullPointerException if {@code model} is {@code null}.
     *
     * @see #getSpecification()
     */
    public final Specification getSpecification( final Model model )
    {
        if ( model == null )
        {
            throw new NullPointerException( "model" );
        }

        Specification s = null;

        if ( this.getSpecification() != null )
        {
            final Modules modules = ModelHelper.getModules( model );

            if ( modules != null )
            {
                s = modules.getSpecification( this.getSpecification() );
            }

            if ( s == null )
            {
                this.log( getMessage( "specificationNotFound", this.getSpecification() ), Project.MSG_WARN );
            }
        }

        return s;
    }

    /**
     * Gets the identifier of an implementation to process.
     *
     * @return The identifier of an implementation to process or {@code null}.
     *
     * @see #setImplementation(java.lang.String)
     */
    public final String getImplementation()
    {
        return this.implementation;
    }

    /**
     * Sets the identifier of an implementation to process.
     *
     * @param value The new identifier of an implementation to process or {@code null}.
     *
     * @see #getImplementation()
     */
    public final void setImplementation( final String value )
    {
        this.implementation = value;
    }

    /**
     * Gets the implementation to process from a given model.
     *
     * @param model The model to get the implementation to process from.
     *
     * @return The implementation to process or {@code null}.
     *
     * @throws NullPointerException if {@code model} is {@code null}.
     *
     * @see #getImplementation()
     */
    public final Implementation getImplementation( final Model model )
    {
        if ( model == null )
        {
            throw new NullPointerException( "model" );
        }

        Implementation i = null;

        if ( this.getImplementation() != null )
        {
            final Modules modules = ModelHelper.getModules( model );

            if ( modules != null )
            {
                i = modules.getImplementation( this.getImplementation() );
            }

            if ( i == null )
            {
                this.log( getMessage( "implementationNotFound", this.getImplementation() ), Project.MSG_WARN );
            }
        }

        return i;
    }

    /**
     * Gets the identifier of a module to process.
     *
     * @return The identifier of a module to process or {@code null}.
     *
     * @see #setModule(java.lang.String)
     */
    public final String getModule()
    {
        return this.module;
    }

    /**
     * Sets the identifier of a module to process.
     *
     * @param value The new identifier of a module to process or {@code null}.
     *
     * @see #getModule()
     */
    public final void setModule( final String value )
    {
        this.module = value;
    }

    /**
     * Gets the module to process from a given model.
     *
     * @param model The model to get the module to process from.
     *
     * @return The module to process or {@code null}.
     *
     * @throws NullPointerException if {@code model} is {@code null}.
     *
     * @see #getModule()
     */
    public final Module getModule( final Model model )
    {
        if ( model == null )
        {
            throw new NullPointerException( "model" );
        }

        Module m = null;

        if ( this.getModule() != null )
        {
            final Modules modules = ModelHelper.getModules( model );

            if ( modules != null )
            {
                m = modules.getModule( this.getModule() );
            }

            if ( m == null )
            {
                this.log( getMessage( "moduleNotFound", this.getModule() ), Project.MSG_WARN );
            }
        }

        return m;
    }

    /**
     * Gets a flag indicating that all modules are requested to be processed.
     *
     * @return {@code true} if processing of all modules is requested; {@code false} else.
     *
     * @see #getSpecification()
     * @see #getImplementation()
     * @see #getModule()
     */
    public boolean isModulesProcessingRequested()
    {
        return this.getSpecification() == null && this.getImplementation() == null && this.getModule() == null;
    }

    /** {@inheritDoc} */
    @Override
    public void preExecuteTask() throws BuildException
    {
        super.preExecuteTask();

        JomcTool.setDefaultTemplateProfile( this.getDefaultTemplateProfile() );
    }

    /** {@inheritDoc} */
    @Override
    public void postExecuteTask() throws BuildException
    {
        JomcTool.setDefaultTemplateProfile( null );

        super.postExecuteTask();
    }

    /**
     * Configures a given {@code JomcTool} instance using the properties of the instance.
     *
     * @param tool The tool to configure.
     *
     * @throws NullPointerException if {@code tool} is {@code null}.
     */
    public void configureJomcTool( final JomcTool tool )
    {
        if ( tool == null )
        {
            throw new NullPointerException( "tool" );
        }

        tool.setLogLevel( Level.ALL );
        tool.setIndentation( StringEscapeUtils.unescapeJava( this.getIndentation() ) );
        tool.setInputEncoding( this.getInputEncoding() );
        tool.setLineSeparator( StringEscapeUtils.unescapeJava( this.getLineSeparator() ) );
        tool.setOutputEncoding( this.getOutputEncoding() );
        tool.setTemplateEncoding( this.getTemplateEncoding() );
        tool.setTemplateProfile( this.getTemplateProfile() );
        tool.getListeners().add( new JomcTool.Listener()
        {

            @Override
            public void onLog( final Level level, final String message, final Throwable throwable )
            {
                if ( level.intValue() >= Level.SEVERE.intValue() )
                {
                    log( message, throwable, Project.MSG_ERR );
                }
                else if ( level.intValue() >= Level.WARNING.intValue() )
                {
                    log( message, throwable, Project.MSG_WARN );
                }
                else if ( level.intValue() >= Level.INFO.intValue() )
                {
                    log( message, throwable, Project.MSG_INFO );
                }
                else
                {
                    log( message, throwable, Project.MSG_DEBUG );
                }
            }

        } );

    }

    private static String getMessage( final String key, final Object... args )
    {
        return MessageFormat.format( ResourceBundle.getBundle(
            JomcToolTask.class.getName().replace( '.', '/' ) ).getString( key ), args );

    }

}
