/*
 *   Copyright (C) 2005 Christian Schulte <cs@schulte.it>
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
 *   THIS SOFTWARE IS PROVIDED "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 *   INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY
 *   AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 *   THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY DIRECT, INDIRECT,
 *   INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 *   NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 *   DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 *   THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *   (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 *   THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *   $JOMC$
 *
 */
package org.jomc.tools.ant;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.jomc.model.Implementation;
import org.jomc.model.Module;
import org.jomc.model.Modules;
import org.jomc.model.Specification;
import org.jomc.model.modlet.ModelHelper;
import org.jomc.modlet.Model;
import org.jomc.tools.JomcTool;
import org.jomc.tools.ant.types.KeyValueType;
import org.jomc.tools.ant.types.LocaleType;
import org.jomc.tools.ant.types.PropertiesResourceType;

/**
 * Base class for executing tool based tasks.
 *
 * @author <a href="mailto:cs@schulte.it">Christian Schulte</a>
 * @version $JOMC$
 */
public class JomcToolTask extends JomcModelTask
{

    /**
     * The default encoding to use for reading templates.
     */
    private String defaultTemplateEncoding;

    /**
     * The default template profile to use when accessing templates.
     */
    private String defaultTemplateProfile;

    /**
     * The encoding to use for reading files.
     */
    private String inputEncoding;

    /**
     * The encoding to use for writing files.
     */
    private String outputEncoding;

    /**
     * Additional location to search for templates.
     */
    private String templateLocation;

    /**
     * The template profile to use when accessing templates.
     */
    private String templateProfile;

    /**
     * The indentation string ('\t' for tab).
     */
    private String indentation;

    /**
     * The line separator ('\r\n' for DOS, '\r' for Mac, '\n' for Unix).
     */
    private String lineSeparator;

    /**
     * The locale.
     */
    private LocaleType locale;

    /**
     * The identifier of a specification to process.
     */
    private String specification;

    /**
     * The identifier of an implementation to process.
     */
    private String implementation;

    /**
     * The name of a module to process.
     */
    private String module;

    /**
     * The Velocity runtime properties.
     */
    private List<KeyValueType> velocityProperties;

    /**
     * The Velocity runtime property resources.
     */
    private List<PropertiesResourceType> velocityPropertyResources;

    /**
     * The template parameters.
     */
    private List<KeyValueType> templateParameters;

    /**
     * The template parameter resources.
     */
    private List<PropertiesResourceType> templateParameterResources;

    /**
     * Creates a new {@code JomcToolTask} instance.
     */
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
     * @see #setDefaultTemplateEncoding(java.lang.String)
     *
     * @since 1.3
     */
    public final String getDefaultTemplateEncoding()
    {
        return this.defaultTemplateEncoding;
    }

    /**
     * Sets the encoding to use for reading templates.
     *
     * @param value The new encoding to use for reading templates or {@code null}.
     *
     * @see #getDefaultTemplateEncoding()
     *
     * @since 1.3
     */
    public final void setDefaultTemplateEncoding( final String value )
    {
        this.defaultTemplateEncoding = value;
    }

    /**
     * Gets the location to search for templates in addition to searching the class path of the task.
     *
     * @return The location to search for templates in addition to searching the class path of the task or {@code null}.
     *
     * @see #setTemplateLocation(java.lang.String)
     */
    public final String getTemplateLocation()
    {
        return this.templateLocation;
    }

    /**
     * Sets the location to search for templates in addition to searching the class path of the task.
     *
     * @param value The new location to search for templates in addition to searching the class path of the task or
     * {@code null}.
     *
     * @see #getTemplateLocation()
     */
    public final void setTemplateLocation( final String value )
    {
        this.templateLocation = value;
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
     * Gets the locale.
     *
     * @return The locale or {@code null}.
     *
     * @see #createLocale()
     */
    public final LocaleType getLocale()
    {
        return this.locale;
    }

    /**
     * Creates a new {@code locale} element instance.
     *
     * @return A new {@code locale} element instance.
     *
     * @throws BuildException if a value already has been created.
     *
     * @see #getLocale()
     */
    public LocaleType createLocale()
    {
        if ( this.locale != null )
        {
            throw new BuildException( Messages.getMessage( "multipleElements", "locale" ), this.getLocation() );
        }

        this.locale = new LocaleType();
        return this.locale;
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
                this.log( Messages.getMessage( "specificationNotFound", this.getSpecification() ), Project.MSG_WARN );
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
                this.log( Messages.getMessage( "implementationNotFound", this.getImplementation() ), Project.MSG_WARN );
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
                this.log( Messages.getMessage( "moduleNotFound", this.getModule() ), Project.MSG_WARN );
            }
        }

        return m;
    }

    /**
     * Gets a flag indicating all modules are requested to be processed.
     *
     * @return {@code true}, if processing of all modules is requested; {@code false}, else.
     *
     * @see #getSpecification()
     * @see #getImplementation()
     * @see #getModule()
     */
    public boolean isModulesProcessingRequested()
    {
        return this.getSpecification() == null && this.getImplementation() == null && this.getModule() == null;
    }

    /**
     * Gets the Velocity runtime properties to apply.
     * <p>
     * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make
     * to the returned list will be present inside the object. This is why there is no {@code set} method for the
     * velocity properties property.
     * </p>
     *
     * @return The Velocity runtime properties to apply.
     *
     * @see #createVelocityProperty()
     */
    public final List<KeyValueType> getVelocityProperties()
    {
        if ( this.velocityProperties == null )
        {
            this.velocityProperties = new LinkedList<KeyValueType>();
        }

        return this.velocityProperties;
    }

    /**
     * Creates a new {@code velocityProperty} element instance.
     *
     * @return A new {@code velocityProperty} element instance.
     *
     * @see #getVelocityProperties()
     */
    public KeyValueType createVelocityProperty()
    {
        final KeyValueType velocityProperty = new KeyValueType();
        this.getVelocityProperties().add( velocityProperty );
        return velocityProperty;
    }

    /**
     * Gets the Velocity runtime property resources to apply.
     * <p>
     * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make
     * to the returned list will be present inside the object. This is why there is no {@code set} method for the
     * velocity property resources property.
     * </p>
     *
     * @return The Velocity runtime property resources to apply.
     *
     * @see #createVelocityPropertyResource()
     */
    public final List<PropertiesResourceType> getVelocityPropertyResources()
    {
        if ( this.velocityPropertyResources == null )
        {
            this.velocityPropertyResources = new LinkedList<PropertiesResourceType>();
        }

        return this.velocityPropertyResources;
    }

    /**
     * Creates a new {@code velocityPropertyResource} element instance.
     *
     * @return A new {@code velocityPropertyResource} element instance.
     *
     * @see #getVelocityPropertyResources()
     */
    public PropertiesResourceType createVelocityPropertyResource()
    {
        final PropertiesResourceType velocityPropertyResource = new PropertiesResourceType();
        this.getVelocityPropertyResources().add( velocityPropertyResource );
        return velocityPropertyResource;
    }

    /**
     * Gets the template parameters to apply.
     * <p>
     * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make
     * to the returned list will be present inside the object. This is why there is no {@code set} method for the
     * template parameters property.
     * </p>
     *
     * @return The template parameters to apply.
     *
     * @see #createTemplateParameter()
     */
    public final List<KeyValueType> getTemplateParameters()
    {
        if ( this.templateParameters == null )
        {
            this.templateParameters = new LinkedList<KeyValueType>();
        }

        return this.templateParameters;
    }

    /**
     * Creates a new {@code templateParameter} element instance.
     *
     * @return A new {@code templateParameter} element instance.
     *
     * @see #getTemplateParameters()
     */
    public KeyValueType createTemplateParameter()
    {
        final KeyValueType templateParameter = new KeyValueType();
        this.getTemplateParameters().add( templateParameter );
        return templateParameter;
    }

    /**
     * Gets the template parameter resources to apply.
     * <p>
     * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make
     * to the returned list will be present inside the object. This is why there is no {@code set} method for the
     * template parameter resources property.
     * </p>
     *
     * @return The template parameter resources to apply.
     *
     * @see #createTemplateParameterResource()
     */
    public final List<PropertiesResourceType> getTemplateParameterResources()
    {
        if ( this.templateParameterResources == null )
        {
            this.templateParameterResources = new LinkedList<PropertiesResourceType>();
        }

        return this.templateParameterResources;
    }

    /**
     * Creates a new {@code templateParameterResource} element instance.
     *
     * @return A new {@code templateParameterResource} element instance.
     *
     * @see #getTemplateParameterResources()
     */
    public PropertiesResourceType createTemplateParameterResource()
    {
        final PropertiesResourceType templateParameterResource = new PropertiesResourceType();
        this.getTemplateParameterResources().add( templateParameterResource );
        return templateParameterResource;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void preExecuteTask() throws BuildException
    {
        super.preExecuteTask();

        this.assertKeysNotNull( this.getVelocityProperties() );
        this.assertKeysNotNull( this.getTemplateParameters() );
        this.assertLocationsNotNull( this.getTemplateParameterResources() );
        this.assertLocationsNotNull( this.getVelocityPropertyResources() );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void postExecuteTask() throws BuildException
    {
        super.postExecuteTask();
    }

    /**
     * Configures a given {@code JomcTool} instance using the properties of the instance.
     *
     * @param tool The tool to configure.
     *
     * @throws NullPointerException if {@code tool} is {@code null}.
     * @throws BuildException if configuring {@code tool} fails.
     */
    public void configureJomcTool( final JomcTool tool ) throws BuildException
    {
        if ( tool == null )
        {
            throw new NullPointerException( "tool" );
        }

        try
        {
            tool.setLogLevel( Level.ALL );
            tool.setIndentation( StringEscapeUtils.unescapeJava( this.getIndentation() ) );
            tool.setInputEncoding( this.getInputEncoding() );
            tool.setLineSeparator( StringEscapeUtils.unescapeJava( this.getLineSeparator() ) );
            tool.setOutputEncoding( this.getOutputEncoding() );
            tool.setDefaultTemplateEncoding( this.getDefaultTemplateEncoding() );
            tool.setDefaultTemplateProfile( this.getDefaultTemplateProfile() );
            tool.setTemplateProfile( this.getTemplateProfile() );
            tool.getListeners().add( new JomcTool.Listener()
            {

                @Override
                public void onLog( final Level level, final String message, final Throwable throwable )
                {
                    super.onLog( level, message, throwable );

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

            for ( int i = 0, s0 = this.getVelocityPropertyResources().size(); i < s0; i++ )
            {
                for ( final Map.Entry<Object, Object> e
                          : this.getProperties( this.getVelocityPropertyResources().get( i ) ).entrySet() )
                {
                    if ( e.getValue() != null )
                    {
                        tool.getVelocityEngine().setProperty( e.getKey().toString(), e.getValue() );
                    }
                    else
                    {
                        tool.getVelocityEngine().clearProperty( e.getKey().toString() );
                    }
                }
            }

            for ( int i = 0, s0 = this.getVelocityProperties().size(); i < s0; i++ )
            {
                final KeyValueType p = this.getVelocityProperties().get( i );
                final Object object = p.getObject( this.getLocation() );

                if ( object != null )
                {
                    tool.getVelocityEngine().setProperty( p.getKey(), object );
                }
                else
                {
                    tool.getVelocityEngine().clearProperty( p.getKey() );
                }
            }

            for ( final Map.Entry<Object, Object> e : System.getProperties().entrySet() )
            {
                tool.getTemplateParameters().put( e.getKey().toString(), e.getValue() );
            }

            for ( final Iterator<Map.Entry<String, Object>> it = this.getProject().getProperties().entrySet().
                iterator(); it.hasNext(); )
            {
                final Map.Entry<String, Object> e = it.next();
                tool.getTemplateParameters().put( e.getKey(), e.getValue() );
            }

            for ( int i = 0, s0 = this.getTemplateParameterResources().size(); i < s0; i++ )
            {
                for ( final Map.Entry<Object, Object> e
                          : this.getProperties( this.getTemplateParameterResources().get( i ) ).entrySet() )
                {
                    if ( e.getValue() != null )
                    {
                        tool.getTemplateParameters().put( e.getKey().toString(), e.getValue() );
                    }
                    else
                    {
                        tool.getTemplateParameters().remove( e.getKey().toString() );
                    }
                }
            }

            for ( int i = 0, s0 = this.getTemplateParameters().size(); i < s0; i++ )
            {
                final KeyValueType p = this.getTemplateParameters().get( i );
                final Object object = p.getObject( this.getLocation() );

                if ( object != null )
                {
                    tool.getTemplateParameters().put( p.getKey(), object );
                }
                else
                {
                    tool.getTemplateParameters().remove( p.getKey() );
                }
            }

            if ( this.getTemplateLocation() != null )
            {
                final URL url = this.getDirectory( this.getTemplateLocation() );
                tool.setTemplateLocation( url );

                if ( url == null )
                {
                    this.log( Messages.getMessage( "templateLocationNotFound", this.getTemplateLocation() ),
                              Project.MSG_WARN );

                }
            }

            if ( this.getLocale() != null )
            {
                tool.setLocale( new Locale( StringUtils.defaultString( this.getLocale().getLanguage() ),
                                            StringUtils.defaultString( this.getLocale().getCountry() ),
                                            StringUtils.defaultString( this.getLocale().getVariant() ) ) );

            }
        }
        catch ( final IOException e )
        {
            throw new BuildException( Messages.getMessage( e ), e, this.getLocation() );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JomcToolTask clone()
    {
        final JomcToolTask clone = (JomcToolTask) super.clone();

        if ( this.locale != null )
        {
            clone.locale = this.locale.clone();
        }

        if ( this.velocityPropertyResources != null )
        {
            clone.velocityPropertyResources =
                new ArrayList<PropertiesResourceType>( this.velocityPropertyResources.size() );

            for ( final PropertiesResourceType e : this.velocityPropertyResources )
            {
                clone.velocityPropertyResources.add( e.clone() );
            }
        }

        if ( this.velocityProperties != null )
        {
            clone.velocityProperties = new ArrayList<KeyValueType>( this.velocityProperties.size() );

            for ( final KeyValueType e : this.velocityProperties )
            {
                clone.velocityProperties.add( e.clone() );
            }
        }

        if ( this.velocityPropertyResources != null )
        {
            clone.velocityPropertyResources =
                new ArrayList<PropertiesResourceType>( this.velocityPropertyResources.size() );

            for ( final PropertiesResourceType e : this.velocityPropertyResources )
            {
                clone.velocityPropertyResources.add( e.clone() );
            }
        }

        if ( this.templateParameters != null )
        {
            clone.templateParameters = new ArrayList<KeyValueType>( this.templateParameters.size() );

            for ( final KeyValueType e : this.templateParameters )
            {
                clone.templateParameters.add( e.clone() );
            }
        }

        if ( this.templateParameterResources != null )
        {
            clone.templateParameterResources =
                new ArrayList<PropertiesResourceType>( this.templateParameterResources.size() );

            for ( final PropertiesResourceType e : this.templateParameterResources )
            {
                clone.templateParameterResources.add( e.clone() );
            }
        }

        return clone;
    }

}
