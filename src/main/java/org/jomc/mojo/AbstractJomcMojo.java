/*
 *   Copyright (C) 2009 The JOMC Project
 *   Copyright (C) 2005 Christian Schulte <schulte2005@users.sourceforge.net>
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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.ArtifactUtils;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.descriptor.MojoDescriptor;
import org.apache.maven.project.MavenProject;
import org.jomc.model.Module;
import org.jomc.model.Modules;
import org.jomc.model.modlet.DefaultModelProcessor;
import org.jomc.model.modlet.DefaultModelProvider;
import org.jomc.model.modlet.ModelHelper;
import org.jomc.modlet.DefaultModelContext;
import org.jomc.modlet.DefaultModletProvider;
import org.jomc.modlet.Model;
import org.jomc.modlet.ModelContext;
import org.jomc.modlet.ModelException;
import org.jomc.modlet.ModelValidationReport;
import org.jomc.tools.ClassFileProcessor;
import org.jomc.tools.JomcTool;
import org.jomc.tools.ResourceFileProcessor;
import org.jomc.tools.SourceFileProcessor;
import org.jomc.tools.modlet.ToolsModelProcessor;
import org.jomc.tools.modlet.ToolsModelProvider;

/**
 * Base class for executing {@code JomcTool}s.
 *
 * @author <a href="mailto:schulte2005@users.sourceforge.net">Christian Schulte</a>
 * @version $Id$
 */
public abstract class AbstractJomcMojo extends AbstractMojo
{

    /**
     * The encoding to use for reading and writing files.
     *
     * @parameter default-value="${project.build.sourceEncoding}" expression="${jomc.sourceEncoding}"
     */
    private String sourceEncoding;

    /**
     * The encoding to use for reading templates.
     *
     * @parameter expression="${jomc.templateEncoding}"
     */
    private String templateEncoding;

    /**
     * Location to search for templates in addition to searching the class path of the plugin.
     * <p>First an attempt is made to parse the location value to an URL. On successful parsing, that URL is used.
     * Otherwise the location value is interpreted as a directory name relative to the base directory of the project.
     * If that directory exists, that directory is used. If nothing is found at the given location, a warning message is
     * logged.</p>
     *
     * @parameter expression="${jomc.templateLocation}"
     * @since 1.2
     */
    private String templateLocation;

    /**
     * The template profile to use when accessing templates.
     *
     * @parameter expression="${jomc.templateProfile}"
     */
    private String templateProfile;

    /**
     * The default template profile to use when accessing templates.
     *
     * @parameter expression="${jomc.defaultTemplateProfile}"
     */
    private String defaultTemplateProfile;

    /**
     * The location to search for providers.
     *
     * @parameter expression="${jomc.providerLocation}"
     */
    private String providerLocation;

    /**
     * The location to search for platform providers.
     *
     * @parameter expression="${jomc.platformProviderLocation}"
     */
    private String platformProviderLocation;

    /**
     * The identifier of the model to process.
     *
     * @parameter default-value="http://jomc.org/model" expression="${jomc.model}"
     */
    private String model;

    /**
     * The name of the {@code ModelContext} implementation class backing the task.
     *
     * @parameter expression="${jomc.modelContextClassName}"
     * @since 1.2
     */
    private String modelContextClassName;

    /**
     * The location to search for modlets.
     *
     * @parameter expression="${jomc.modletLocation}"
     */
    private String modletLocation;

    /**
     * The {@code http://jomc.org/modlet} namespace schema system id.
     *
     * @parameter expression="${jomc.modletSchemaSystemId}"
     * @since 1.2
     */
    private String modletSchemaSystemId;

    /**
     * The location to search for modules.
     *
     * @parameter expression="${jomc.moduleLocation}"
     */
    private String moduleLocation;

    /**
     * The location to search for transformers.
     *
     * @parameter expression="${jomc.transformerLocation}"
     */
    private String transformerLocation;

    /**
     * The indentation string ('\t' for tab).
     *
     * @parameter expression="${jomc.indentation}"
     */
    private String indentation;

    /**
     * The line separator ('\r\n' for DOS, '\r' for Mac, '\n' for Unix).
     *
     * @parameter expression="${jomc.lineSeparator}"
     */
    private String lineSeparator;

    /**
     * The locale.
     * <pre>
     * &lt;locale>
     *   &lt;language>Lowercase two-letter ISO-639 code.&lt;/language>
     *   &lt;country>Uppercase two-letter ISO-3166 code.&lt;/country>
     *   &lt;variant>Vendor and browser specific code.&lt;/variant>
     * &lt;/locale>
     * </pre>
     *
     * @parameter
     * @since 1.2
     * @see Locale
     */
    private LocaleType locale;

    /**
     * Controls verbosity of the plugin.
     *
     * @parameter expression="${jomc.verbose}" default-value="false"
     */
    private boolean verbose;

    /**
     * Controls processing of source code files.
     *
     * @parameter expression="${jomc.sourceProcessing}" default-value="true"
     */
    private boolean sourceProcessingEnabled;

    /**
     * Controls processing of resource files.
     *
     * @parameter expression="${jomc.resourceProcessing}" default-value="true"
     */
    private boolean resourceProcessingEnabled;

    /**
     * Controls processing of class files.
     *
     * @parameter expression="${jomc.classProcessing}" default-value="true"
     */
    private boolean classProcessingEnabled;

    /**
     * Controls processing of models.
     *
     * @parameter expression="${jomc.modelProcessing}" default-value="true"
     */
    private boolean modelProcessingEnabled;

    /**
     * Controls model object classpath resolution.
     *
     * @parameter expression="${jomc.modelObjectClasspathResolution}" default-value="true"
     */
    private boolean modelObjectClasspathResolutionEnabled;

    /**
     * Name of the module to process.
     *
     * @parameter default-value="${project.name}" expression="${jomc.moduleName}"
     */
    private String moduleName;

    /**
     * Name of the test module to process.
     *
     * @parameter default-value="${project.name} Tests" expression="${jomc.testModuleName}"
     */
    private String testModuleName;

    /**
     * Directory holding the compiled class files of the project.
     * <p><strong>Deprecated:</strong> As of JOMC 1.1, please use the 'outputDirectory' parameter. This parameter will
     * be removed in version 2.0.</p>
     *
     * @parameter
     */
    @Deprecated
    private String classesDirectory;

    /**
     * Directory holding the compiled test class files of the project.
     * <p><strong>Deprecated:</strong> As of JOMC 1.1, please use the 'testOutputDirectory' parameter. This parameter
     * will be removed in version 2.0.</p>
     *
     * @parameter
     */
    @Deprecated
    private String testClassesDirectory;

    /**
     * Output directory of the project.
     *
     * @parameter default-value="${project.build.outputDirectory}" expression="${jomc.outputDirectory}"
     * @since 1.1
     */
    private String outputDirectory;

    /**
     * Test output directory of the project.
     *
     * @parameter default-value="${project.build.testOutputDirectory}" expression="${jomc.testOutputDirectory}"
     * @since 1.1
     */
    private String testOutputDirectory;

    /**
     * Directory holding the source files of the project.
     *
     * @parameter default-value="${project.build.sourceDirectory}" expression="${jomc.sourceDirectory}"
     * @since 1.1
     */
    private String sourceDirectory;

    /**
     * Directory holding the test source files of the project.
     *
     * @parameter default-value="${project.build.testSourceDirectory}" expression="${jomc.testSourceDirectory}"
     * @since 1.1
     */
    private String testSourceDirectory;

    /**
     * Directory holding the session related files of the project.
     *
     * @parameter default-value="${project.build.directory}/jomc-sessions" expression="${jomc.sessionDirectory}"
     * @since 1.1
     */
    private String sessionDirectory;

    /**
     * Directory holding the reports of the project.
     *
     * @parameter default-value="${project.reporting.outputDirectory}" expression="${jomc.reportOutputDirectory}"
     * @since 1.1
     */
    private String reportOutputDirectory;

    /**
     * Velocity runtime properties.
     *
     * @parameter
     * @since 1.2
     */
    private Map<String, Object> velocityProperties;

    /**
     * Velocity runtime property resources.
     * <pre>
     * &lt;velocityPropertyResources>
     *   &lt;velocityPropertyResource>
     *     &lt;location>The location of the properties resource.&lt;/location>
     *     &lt;optional>Flag indicating the properties resource is optional.&lt;/optional>
     *     &lt;format>The format of the properties resource.&lt;/format>
     *     &lt;connectTimeout>Timeout value, in milliseconds.&lt;/connectTimeout>
     *     &lt;readTimeout>Timeout value, in milliseconds.&lt;/readTimeout>
     *   &lt;/velocityPropertyResource>
     * &lt;/velocityPropertyResources>
     * </pre>
     * <p>The location value is used to first search the class path of the plugin. If a class path resource is found,
     * that resource is used. If no class path resource is found, an attempt is made to parse the location value to an
     * URL. On successful parsing, that URL is used. Otherwise the location value is interpreted as a file name relative
     * to the base directory of the project. If that file exists, that file is used. If nothing is found at the given
     * location, depending on the optional flag, a warning message is logged or a build failure is produced.</p>
     * <p>The optional flag is used to flag the resource optional. When an optional resource is not found, a warning
     * message is logged instead of producing a build failure.<br/><b>Default value is:</b> false</p>
     * <p>The format value is used to specify the format of the properties resource. Supported values are {@code plain}
     * and {@code xml}.<br/><b>Default value is:</b> plain</p>
     * <p>The connectTimeout value is used to specify the timeout, in milliseconds, to be used when opening
     * communications links to the resource. A timeout of zero is interpreted as an infinite timeout.<br/>
     * <b>Default value is:</b> 60000</p>
     * <p>The readTimeout value is used to specify the timeout, in milliseconds, to be used when reading the resource.
     * A timeout of zero is interpreted as an infinite timeout.<br/>
     * <b>Default value is:</b> 60000</p>
     *
     * @parameter
     * @since 1.2
     */
    private List<VelocityPropertyResource> velocityPropertyResources;

    /**
     * Template parameters.
     *
     * @parameter
     * @since 1.2
     */
    private Map<String, Object> templateParameters;

    /**
     * Template parameter resources.
     * <pre>
     * &lt;templateParameterResources>
     *   &lt;templateParameterResource>
     *     &lt;location>The location of the properties resource.&lt;/location>
     *     &lt;optional>Flag indicating the properties resource is optional.&lt;/optional>
     *     &lt;format>The format of the properties resource.&lt;/format>
     *     &lt;connectTimeout>Timeout value, in milliseconds.&lt;/connectTimeout>
     *     &lt;readTimeout>Timeout value, in milliseconds.&lt;/readTimeout>
     *   &lt;/templateParameterResource>
     * &lt;/templateParameterResources>
     * </pre>
     * <p>The location value is used to first search the class path of the plugin. If a class path resource is found,
     * that resource is used. If no class path resource is found, an attempt is made to parse the location value to an
     * URL. On successful parsing, that URL is used. Otherwise the location value is interpreted as a file name relative
     * to the base directory of the project. If that file exists, that file is used. If nothing is found at the given
     * location, depending on the optional flag, a warning message is logged or a build failure is produced.</p>
     * <p>The optional flag is used to flag the resource optional. When an optional resource is not found, a warning
     * message is logged instead of producing a build failure.<br/><b>Default value is:</b> false</p>
     * <p>The format value is used to specify the format of the properties resource. Supported values are {@code plain}
     * and {@code xml}.<br/><b>Default value is:</b> plain</p>
     * <p>The connectTimeout value is used to specify the timeout, in milliseconds, to be used when opening
     * communications links to the resource. A timeout of zero is interpreted as an infinite timeout.<br/>
     * <b>Default value is:</b> 60000</p>
     * <p>The readTimeout value is used to specify the timeout, in milliseconds, to be used when reading the resource.
     * A timeout of zero is interpreted as an infinite timeout.<br/>
     * <b>Default value is:</b> 60000</p>
     *
     * @parameter
     * @since 1.2
     */
    private List<TemplateParameterResource> templateParameterResources;

    /**
     * Global transformation parameters.
     *
     * @parameter
     * @since 1.2
     */
    private Map<String, Object> transformationParameters;

    /**
     * Global transformation parameter resources.
     * <pre>
     * &lt;transformationParameterResources>
     *   &lt;transformationParameterResource>
     *     &lt;location>The location of the properties resource.&lt;/location>
     *     &lt;optional>Flag indicating the properties resource is optional.&lt;/optional>
     *     &lt;format>The format of the properties resource.&lt;/format>
     *     &lt;connectTimeout>Timeout value, in milliseconds.&lt;/connectTimeout>
     *     &lt;readTimeout>Timeout value, in milliseconds.&lt;/readTimeout>
     *   &lt;/transformationParameterResource>
     * &lt;/transformationParameterResources>
     * </pre>
     * <p>The location value is used to first search the class path of the plugin. If a class path resource is found,
     * that resource is used. If no class path resource is found, an attempt is made to parse the location value to an
     * URL. On successful parsing, that URL is used. Otherwise the location value is interpreted as a file name relative
     * to the base directory of the project. If that file exists, that file is used. If nothing is found at the given
     * location, depending on the optional flag, a warning message is logged or a build failure is produced.</p>
     * <p>The optional flag is used to flag the resource optional. When an optional resource is not found, a warning
     * message is logged instead of producing a build failure.<br/><b>Default value is:</b> false</p>
     * <p>The format value is used to specify the format of the properties resource. Supported values are {@code plain}
     * and {@code xml}.<br/><b>Default value is:</b> plain</p>
     * <p>The connectTimeout value is used to specify the timeout, in milliseconds, to be used when opening
     * communications links to the resource. A timeout of zero is interpreted as an infinite timeout.<br/>
     * <b>Default value is:</b> 60000</p>
     * <p>The readTimeout value is used to specify the timeout, in milliseconds, to be used when reading the resource.
     * A timeout of zero is interpreted as an infinite timeout.<br/>
     * <b>Default value is:</b> 60000</p>
     *
     * @parameter
     * @since 1.2
     */
    private List<TransformationParameterResource> transformationParameterResources;

    /**
     * Class name of the {@code ClassFileProcessor} backing the goal.
     *
     * @parameter default-value="org.jomc.tools.ClassFileProcessor" expression="${jomc.classFileProcessorClassName}"
     * @since 1.2
     */
    private String classFileProcessorClassName;

    /**
     * Class name of the {@code ResourceFileProcessor} backing the goal.
     *
     * @parameter default-value="org.jomc.tools.ResourceFileProcessor"
     *            expression="${jomc.resourceFileProcessorClassName}"
     * @since 1.2
     */
    private String resourceFileProcessorClassName;

    /**
     * Class name of the {@code SourceFileProcessor} backing the goal.
     *
     * @parameter default-value="org.jomc.tools.SourceFileProcessor" expression="${jomc.sourceFileProcessorClassName}"
     * @since 1.2
     */
    private String sourceFileProcessorClassName;

    /**
     * {@code ModelContext} attributes.
     *
     * @parameter
     * @since 1.2
     */
    private Map<String, Object> modelContextAttributes;

    /**
     * Flag controlling JAXP schema validation of model resources.
     *
     * @parameter default-value="true" expression="${jomc.modelResourceValidationEnabled}"
     *
     * @since 1.2
     */
    private boolean modelResourceValidationEnabled;

    /**
     * Flag controlling JAXP schema validation of modlet resources.
     *
     * @parameter default-value="true" expression="${jomc.modletResourceValidationEnabled}"
     *
     * @since 1.2
     */
    private boolean modletResourceValidationEnabled;

    /**
     * The Maven project of the instance.
     *
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject mavenProject;

    /**
     * List of plugin artifacts.
     *
     * @parameter expression="${plugin.artifacts}"
     * @required
     * @readonly
     */
    private List<Artifact> pluginArtifacts;

    /**
     * The Maven session of the instance.
     *
     * @parameter expression="${session}"
     * @required
     * @readonly
     * @since 1.1
     */
    private MavenSession mavenSession;

    /** Creates a new {@code AbstractJomcMojo} instance. */
    public AbstractJomcMojo()
    {
        super();
    }

    /**
     * {@inheritDoc}
     * @see #assertValidParameters()
     * @see #isExecutionPermitted()
     * @see #executeTool()
     */
    public void execute() throws MojoExecutionException, MojoFailureException
    {
        this.assertValidParameters();

        try
        {
            JomcTool.setDefaultTemplateProfile( this.defaultTemplateProfile );

            this.logSeparator();

            if ( this.isLoggable( Level.INFO ) )
            {
                this.log( Level.INFO, Messages.getMessage( "title" ), null );
            }

            if ( this.isExecutionPermitted() )
            {
                this.executeTool();
            }
            else if ( this.isLoggable( Level.INFO ) )
            {
                this.log( Level.INFO, Messages.getMessage( "executionSuppressed", this.getExecutionStrategy() ), null );
            }
        }
        catch ( final Exception e )
        {
            throw new MojoExecutionException( Messages.getMessage( e ), e );
        }
        finally
        {
            JomcTool.setDefaultTemplateProfile( null );
            this.logSeparator();
        }
    }

    /**
     * Validates the parameters of the goal.
     *
     * @throws MojoFailureException if illegal parameter values are detected.
     *
     * @see #assertValidResources(java.util.Collection)
     * @since 1.2
     */
    protected void assertValidParameters() throws MojoFailureException
    {
        this.assertValidResources( this.templateParameterResources );
        this.assertValidResources( this.transformationParameterResources );
        this.assertValidResources( this.velocityPropertyResources );
    }

    /**
     * Validates a given resource collection.
     *
     * @param resources The resource collection to validate or {@code null}.
     *
     * @throws MojoFailureException if a location property of a given resource holds a {@code null} value or a given
     * {@code PropertiesResourceType} holds an illegal format.
     *
     * @see #assertValidParameters()
     * @see PropertiesResourceType#isFormatSupported(java.lang.String)
     * @since 1.2
     */
    protected final void assertValidResources( final Collection<? extends ResourceType> resources )
        throws MojoFailureException
    {
        if ( resources != null )
        {
            for ( ResourceType r : resources )
            {
                if ( r.getLocation() == null )
                {
                    throw new MojoFailureException( Messages.getMessage( "mandatoryParameter", "location" ) );
                }

                if ( r instanceof PropertiesResourceType )
                {
                    final PropertiesResourceType p = (PropertiesResourceType) r;

                    if ( !PropertiesResourceType.isFormatSupported( p.getFormat() ) )
                    {
                        throw new MojoFailureException( Messages.getMessage(
                            "illegalPropertiesFormat", p.getFormat(),
                            StringUtils.join( PropertiesResourceType.FORMAT_VALUES, ',' ) ) );

                    }
                }
            }
        }
    }

    /**
     * Executes this tool.
     *
     * @throws Exception if execution of this tool fais.
     */
    protected abstract void executeTool() throws Exception;

    /**
     * Gets the goal of the instance.
     *
     * @return The goal of the instance.
     *
     * @throws MojoExecutionException if getting the goal of the instance fails.
     * @since 1.1
     */
    protected abstract String getGoal() throws MojoExecutionException;

    /**
     * Gets the execution strategy of the instance.
     *
     * @return The execution strategy of the instance.
     *
     * @throws MojoExecutionException if getting the execution strategy of the instance fails.
     * @since 1.1
     */
    protected abstract String getExecutionStrategy() throws MojoExecutionException;

    /**
     * Gets a flag indicating the current execution is permitted.
     *
     * @return {@code true} if the current execution is permitted; {@code false} if the current execution is suppressed.
     *
     * @throws MojoExecutionException if getting the flag fails.
     *
     * @since 1.1
     * @see #getGoal()
     * @see #getExecutionStrategy()
     */
    protected boolean isExecutionPermitted() throws MojoExecutionException
    {
        try
        {
            boolean permitted = true;

            if ( MojoDescriptor.SINGLE_PASS_EXEC_STRATEGY.equals( this.getExecutionStrategy() ) )
            {
                final File flagFile =
                    new File( this.getSessionDirectory(),
                              ArtifactUtils.versionlessKey( this.getMavenProject().getArtifact() ).hashCode()
                              + "-" + this.getGoal()
                              + "-" + this.getMavenSession().getStartTime().getTime() + ".flg" );

                if ( !this.getSessionDirectory().exists() && !this.getSessionDirectory().mkdirs() )
                {
                    throw new MojoExecutionException( Messages.getMessage(
                        "failedCreatingDirectory", this.getSessionDirectory().getAbsolutePath() ) );

                }

                permitted = flagFile.createNewFile();
            }

            return permitted;
        }
        catch ( final IOException e )
        {
            throw new MojoExecutionException( Messages.getMessage( e ), e );
        }
    }

    /**
     * Gets the Maven project of the instance.
     *
     * @return The Maven project of the instance.
     *
     * @throws MojoExecutionException if getting the Maven project of the instance fails.
     */
    protected MavenProject getMavenProject() throws MojoExecutionException
    {
        return this.mavenProject;
    }

    /**
     * Gets the Maven session of the instance.
     *
     * @return The Maven session of the instance.
     *
     * @throws MojoExecutionException if getting the Maven session of the instance fails.
     *
     * @since 1.1
     */
    protected MavenSession getMavenSession() throws MojoExecutionException
    {
        return this.mavenSession;
    }

    /**
     * Gets an absolute {@code File} instance for a given name.
     * <p>This method constructs a new {@code File} instance using the given name. If the resulting file is not
     * absolute, the value of the {@code basedir} property of the current Maven project is prepended.</p>
     *
     * @param name The name to get an absolute {@code File} instance for.
     *
     * @return An absolute {@code File} instance constructed from {@code name}.
     *
     * @throws MojoExecutionException if getting an absolute {@code File} instance for {@code name} fails.
     * @throws NullPointerException if {@code name} is {@code null}.
     *
     * @since 1.1
     */
    protected File getAbsoluteFile( final String name ) throws MojoExecutionException
    {
        if ( name == null )
        {
            throw new NullPointerException( "name" );
        }

        File file = new File( name );
        if ( !file.isAbsolute() )
        {
            file = new File( this.getMavenProject().getBasedir(), name );
        }

        return file;
    }

    /**
     * Gets the directory holding the compiled class files of the project.
     *
     * @return The directory holding the compiled class files of the project.
     *
     * @throws MojoExecutionException if getting the directory fails.
     *
     * @since 1.1
     */
    protected File getOutputDirectory() throws MojoExecutionException
    {
        if ( this.classesDirectory != null )
        {
            if ( this.isLoggable( Level.WARNING ) )
            {
                this.log( Level.WARNING, Messages.getMessage(
                    "deprecationWarning", "classesDirectory", "outputDirectory" ), null );

            }

            if ( !this.classesDirectory.equals( this.outputDirectory ) )
            {
                if ( this.isLoggable( Level.WARNING ) )
                {
                    this.log( Level.WARNING, Messages.getMessage( "ignoringParameter", "outputDirectory" ), null );
                }

                this.outputDirectory = this.classesDirectory;
            }

            this.classesDirectory = null;
        }

        final File dir = this.getAbsoluteFile( this.outputDirectory );
        if ( !dir.exists() && !dir.mkdirs() )
        {
            throw new MojoExecutionException( Messages.getMessage( "failedCreatingDirectory", dir.getAbsolutePath() ) );
        }

        return dir;
    }

    /**
     * Gets the directory holding the compiled test class files of the project.
     *
     * @return The directory holding the compiled test class files of the project.
     *
     * @throws MojoExecutionException if getting the directory fails.
     *
     * @since 1.1
     */
    protected File getTestOutputDirectory() throws MojoExecutionException
    {
        if ( this.testClassesDirectory != null )
        {
            if ( this.isLoggable( Level.WARNING ) )
            {
                this.log( Level.WARNING, Messages.getMessage(
                    "deprecationWarning", "testClassesDirectory", "testOutputDirectory" ), null );

            }

            if ( !this.testClassesDirectory.equals( this.testOutputDirectory ) )
            {
                if ( this.isLoggable( Level.WARNING ) )
                {
                    this.log( Level.WARNING, Messages.getMessage( "ignoringParameter", "testOutputDirectory" ), null );
                }

                this.testOutputDirectory = this.testClassesDirectory;
            }

            this.testClassesDirectory = null;
        }

        final File dir = this.getAbsoluteFile( this.testOutputDirectory );
        if ( !dir.exists() && !dir.mkdirs() )
        {
            throw new MojoExecutionException( Messages.getMessage( "failedCreatingDirectory", dir.getAbsolutePath() ) );
        }

        return dir;
    }

    /**
     * Gets the directory holding the source files of the project.
     *
     * @return The directory holding the source files of the project.
     *
     * @throws MojoExecutionException if getting the directory fails.
     *
     * @since 1.1
     */
    protected File getSourceDirectory() throws MojoExecutionException
    {
        return this.getAbsoluteFile( this.sourceDirectory );
    }

    /**
     * Gets the directory holding the test source files of the project.
     *
     * @return The directory holding the test source files of the project.
     *
     * @throws MojoExecutionException if getting the directory fails.
     *
     * @since 1.1
     */
    protected File getTestSourceDirectory() throws MojoExecutionException
    {
        return this.getAbsoluteFile( this.testSourceDirectory );
    }

    /**
     * Gets the directory holding the session related files of the project.
     *
     * @return The directory holding the session related files of the project.
     *
     * @throws MojoExecutionException if getting the directory fails.
     *
     * @since 1.1
     */
    protected File getSessionDirectory() throws MojoExecutionException
    {
        return this.getAbsoluteFile( this.sessionDirectory );
    }

    /**
     * Gets the directory holding the reports of the project.
     *
     * @return The directory holding the reports of the project.
     *
     * @throws MojoExecutionException if getting the directory fails.
     *
     * @since 1.1
     */
    protected File getReportOutputDirectory() throws MojoExecutionException
    {
        return this.getAbsoluteFile( this.reportOutputDirectory );
    }

    /**
     * Gets the project's runtime class loader of the instance.
     *
     * @return The project's runtime class loader of the instance.
     *
     * @throws MojoExecutionException if getting the class loader fails.
     */
    protected ClassLoader getMainClassLoader() throws MojoExecutionException
    {
        try
        {
            final Set<String> mainClasspathElements = this.getMainClasspathElements();
            final Set<URI> uris = new HashSet<URI>( mainClasspathElements.size() );

            for ( String element : mainClasspathElements )
            {
                final URI uri = new File( element ).toURI();
                if ( !uris.contains( uri ) )
                {
                    uris.add( uri );
                }
            }

            if ( this.isLoggable( Level.FINEST ) )
            {
                this.log( Level.FINEST, Messages.getMessage( "mainClasspathInfo" ), null );
            }

            int i = 0;
            final URL[] urls = new URL[ uris.size() ];
            for ( URI uri : uris )
            {
                urls[i++] = uri.toURL();

                if ( this.isLoggable( Level.FINEST ) )
                {
                    this.log( Level.FINEST, "\t" + urls[i - 1].toExternalForm(), null );
                }
            }

            return new URLClassLoader( urls, Thread.currentThread().getContextClassLoader() );
        }
        catch ( final IOException e )
        {
            throw new MojoExecutionException( Messages.getMessage( e ), e );
        }
    }

    /**
     * Gets the project's test class loader of the instance.
     *
     * @return The project's test class loader of the instance.
     *
     * @throws MojoExecutionException if getting the class loader fails.
     */
    protected ClassLoader getTestClassLoader() throws MojoExecutionException
    {
        try
        {
            final Set<String> testClasspathElements = this.getTestClasspathElements();
            final Set<URI> uris = new HashSet<URI>( testClasspathElements.size() );

            for ( String element : testClasspathElements )
            {
                final URI uri = new File( element ).toURI();
                if ( !uris.contains( uri ) )
                {
                    uris.add( uri );
                }
            }

            if ( this.isLoggable( Level.FINEST ) )
            {
                this.log( Level.FINEST, Messages.getMessage( "testClasspathInfo" ), null );
            }

            int i = 0;
            final URL[] urls = new URL[ uris.size() ];
            for ( URI uri : uris )
            {
                urls[i++] = uri.toURL();

                if ( this.isLoggable( Level.FINEST ) )
                {
                    this.log( Level.FINEST, "\t" + urls[i - 1].toExternalForm(), null );
                }
            }

            return new URLClassLoader( urls, Thread.currentThread().getContextClassLoader() );
        }
        catch ( final IOException e )
        {
            throw new MojoExecutionException( Messages.getMessage( e ), e );
        }
    }

    /**
     * Gets the project's runtime classpath elements.
     *
     * @return A set of classpath element strings.
     *
     * @throws MojoExecutionException if getting the classpath elements fails.
     */
    protected Set<String> getMainClasspathElements() throws MojoExecutionException
    {
        final List<?> runtimeArtifacts = this.getMavenProject().getRuntimeArtifacts();
        final List<?> compileArtifacts = this.getMavenProject().getCompileArtifacts();
        final Set<String> elements = new HashSet<String>( runtimeArtifacts.size() + compileArtifacts.size() + 1 );
        elements.add( this.getOutputDirectory().getAbsolutePath() );

        for ( final Iterator<?> it = runtimeArtifacts.iterator(); it.hasNext(); )
        {
            final Artifact a = (Artifact) it.next();
            final Artifact pluginArtifact = this.getPluginArtifact( a );

            if ( a.getFile() == null )
            {
                if ( this.isLoggable( Level.WARNING ) )
                {
                    this.log( Level.WARNING, Messages.getMessage( "ignoringArtifact", a.toString() ), null );
                }

                continue;
            }

            if ( pluginArtifact != null )
            {
                if ( this.isLoggable( Level.FINER ) )
                {
                    this.log( Level.FINER, Messages.getMessage(
                        "ignoringPluginArtifact", a.toString(), pluginArtifact.toString() ), null );

                }

                continue;
            }

            final String element = a.getFile().getAbsolutePath();
            elements.add( element );
        }

        for ( final Iterator<?> it = compileArtifacts.iterator(); it.hasNext(); )
        {
            final Artifact a = (Artifact) it.next();
            final Artifact pluginArtifact = this.getPluginArtifact( a );

            if ( a.getFile() == null )
            {
                if ( this.isLoggable( Level.WARNING ) )
                {
                    this.log( Level.WARNING, Messages.getMessage( "ignoringArtifact", a.toString() ), null );
                }

                continue;
            }

            if ( pluginArtifact != null )
            {
                if ( this.isLoggable( Level.FINER ) )
                {
                    this.log( Level.FINER, Messages.getMessage(
                        "ignoringPluginArtifact", a.toString(), pluginArtifact.toString() ), null );

                }

                continue;
            }

            final String element = a.getFile().getAbsolutePath();
            elements.add( element );
        }

        return elements;
    }

    /**
     * Gets the project's test classpath elements.
     *
     * @return A set of classpath element strings.
     *
     * @throws MojoExecutionException if getting the classpath elements fails.
     */
    protected Set<String> getTestClasspathElements() throws MojoExecutionException
    {
        final List<?> testArtifacts = this.getMavenProject().getTestArtifacts();
        final Set<String> elements = new HashSet<String>( testArtifacts.size() + 2 );
        elements.add( this.getOutputDirectory().getAbsolutePath() );
        elements.add( this.getTestOutputDirectory().getAbsolutePath() );

        for ( final Iterator<?> it = testArtifacts.iterator(); it.hasNext(); )
        {
            final Artifact a = (Artifact) it.next();
            final Artifact pluginArtifact = this.getPluginArtifact( a );

            if ( a.getFile() == null )
            {
                if ( this.isLoggable( Level.WARNING ) )
                {
                    this.log( Level.WARNING, Messages.getMessage( "ignoringArtifact", a.toString() ), null );
                }

                continue;
            }

            if ( pluginArtifact != null )
            {
                if ( this.isLoggable( Level.FINER ) )
                {
                    this.log( Level.FINER, Messages.getMessage(
                        "ignoringPluginArtifact", a.toString(), pluginArtifact.toString() ), null );

                }

                continue;
            }

            final String element = a.getFile().getAbsolutePath();
            elements.add( element );
        }

        return elements;
    }

    /**
     * Gets a flag indicating verbose output is enabled.
     *
     * @return {@code true} if verbose output is enabled; {@code false} if information messages are suppressed.
     *
     * @throws MojoExecutionException if getting the flag fails.
     *
     * @since 1.1
     */
    protected final boolean isVerbose() throws MojoExecutionException
    {
        return this.verbose;
    }

    /**
     * Gets a flag indicating verbose output is enabled.
     *
     * @param value {@code true} if verbose output is enabled; {@code false} if information messages are suppressed.
     *
     * @throws MojoExecutionException if setting the flag fails.
     *
     * @since 1.1
     */
    protected final void setVerbose( final boolean value ) throws MojoExecutionException
    {
        this.verbose = value;
    }

    /**
     * Gets a flag indicating the processing of sources is enabled.
     *
     * @return {@code true} if processing of sources is enabled; {@code false} else.
     *
     * @throws MojoExecutionException if getting the flag fails.
     */
    protected final boolean isSourceProcessingEnabled() throws MojoExecutionException
    {
        return this.sourceProcessingEnabled;
    }

    /**
     * Sets the flag indicating the processing of sources is enabled.
     *
     * @param value {@code true} to enable processing of sources; {@code false} to disable processing of sources.
     *
     * @throws MojoExecutionException if setting the flag fails.
     *
     * @since 1.1
     */
    protected final void setSourceProcessingEnabled( final boolean value ) throws MojoExecutionException
    {
        this.sourceProcessingEnabled = value;
    }

    /**
     * Gets a flag indicating the processing of resources is enabled.
     *
     * @return {@code true} if processing of resources is enabled; {@code false} else.
     *
     * @throws MojoExecutionException if getting the flag fails.
     */
    protected final boolean isResourceProcessingEnabled() throws MojoExecutionException
    {
        return this.resourceProcessingEnabled;
    }

    /**
     * Sets the flag indicating the processing of resources is enabled.
     *
     * @param value {@code true} to enable processing of resources; {@code false} to disable processing of resources.
     *
     * @throws MojoExecutionException if setting the flag fails.
     *
     * @since 1.1
     */
    protected final void setResourceProcessingEnabled( final boolean value ) throws MojoExecutionException
    {
        this.resourceProcessingEnabled = value;
    }

    /**
     * Gets a flag indicating the processing of classes is enabled.
     *
     * @return {@code true} if processing of classes is enabled; {@code false} else.
     *
     * @throws MojoExecutionException if getting the flag fails.
     */
    protected final boolean isClassProcessingEnabled() throws MojoExecutionException
    {
        return this.classProcessingEnabled;
    }

    /**
     * Sets the flag indicating the processing of classes is enabled.
     *
     * @param value {@code true} to enable processing of classes; {@code false} to disable processing of classes.
     *
     * @throws MojoExecutionException if setting the flag fails.
     *
     * @since 1.1
     */
    protected final void setClassProcessingEnabled( final boolean value ) throws MojoExecutionException
    {
        this.classProcessingEnabled = value;
    }

    /**
     * Gets a flag indicating the processing of models is enabled.
     *
     * @return {@code true} if processing of models is enabled; {@code false} else.
     *
     * @throws MojoExecutionException if getting the flag fails.
     */
    protected final boolean isModelProcessingEnabled() throws MojoExecutionException
    {
        return this.modelProcessingEnabled;
    }

    /**
     * Sets the flag indicating the processing of models is enabled.
     *
     * @param value {@code true} to enable processing of models; {@code false} to disable processing of models.
     *
     * @throws MojoExecutionException if setting the flag fails.
     *
     * @since 1.1
     */
    protected final void setModelProcessingEnabled( final boolean value ) throws MojoExecutionException
    {
        this.modelProcessingEnabled = value;
    }

    /**
     * Gets a flag indicating model object class path resolution is enabled.
     *
     * @return {@code true} if model object class path resolution is enabled; {@code false} else.
     *
     * @throws MojoExecutionException if getting the flag fails.
     */
    protected final boolean isModelObjectClasspathResolutionEnabled() throws MojoExecutionException
    {
        return this.modelObjectClasspathResolutionEnabled;
    }

    /**
     * Sets the flag indicating model object class path resolution is enabled.
     *
     * @param value {@code true} to enable model object class path resolution; {@code false} to disable model object
     * class path resolution.
     *
     * @throws MojoExecutionException if setting the flag fails.
     *
     * @since 1.1
     */
    protected final void setModelObjectClasspathResolutionEnabled( final boolean value ) throws MojoExecutionException
    {
        this.modelObjectClasspathResolutionEnabled = value;
    }

    /**
     * Gets the identifier of the model to process.
     *
     * @return The identifier of the model to process.
     *
     * @throws MojoExecutionException if getting the identifier fails.
     */
    protected String getModel() throws MojoExecutionException
    {
        return this.model;
    }

    /**
     * Gets the name of the module to process.
     *
     * @return The name of the module to process.
     *
     * @throws MojoExecutionException if getting the name of the module fails.
     */
    protected String getModuleName() throws MojoExecutionException
    {
        return this.moduleName;
    }

    /**
     * Gets the name of the test module to process.
     *
     * @return The name of the test module to process.
     *
     * @throws MojoExecutionException if getting the name of the test module fails.
     */
    protected String getTestModuleName() throws MojoExecutionException
    {
        return this.testModuleName;
    }

    /**
     * Gets the model to process.
     *
     * @param context The model context to get the model to process with.
     *
     * @return The model to process.
     *
     * @throws NullPointerException if {@code context} is {@code null}.
     * @throws MojoExecutionException if getting the model fails.
     */
    protected Model getModel( final ModelContext context ) throws MojoExecutionException
    {
        if ( context == null )
        {
            throw new NullPointerException( "context" );
        }

        try
        {
            Model m = context.findModel( this.getModel() );
            final Modules modules = ModelHelper.getModules( m );

            if ( modules != null && this.isModelObjectClasspathResolutionEnabled() )
            {
                final Module classpathModule =
                    modules.getClasspathModule( Modules.getDefaultClasspathModuleName(), context.getClassLoader() );

                if ( classpathModule != null )
                {
                    modules.getModule().add( classpathModule );
                }
            }

            if ( this.isModelProcessingEnabled() )
            {
                m = context.processModel( m );
            }

            return m;
        }
        catch ( final ModelException e )
        {
            throw new MojoExecutionException( Messages.getMessage( e ), e );
        }
    }

    /**
     * Creates a new model context instance for a given class loader.
     *
     * @param classLoader The class loader to use for creating the context.
     *
     * @return A new model context instance for {@code classLoader}.
     *
     * @throws MojoExecutionException if creating the model context fails.
     *
     * @see #setupModelContext(org.jomc.modlet.ModelContext)
     */
    protected ModelContext createModelContext( final ClassLoader classLoader ) throws MojoExecutionException
    {
        try
        {
            final ModelContext context = this.modelContextClassName == null
                                         ? ModelContext.createModelContext( classLoader )
                                         : ModelContext.createModelContext( this.modelContextClassName, classLoader );

            this.setupModelContext( context );
            return context;
        }
        catch ( final ModelException e )
        {
            throw new MojoExecutionException( Messages.getMessage( e ), e );
        }
    }

    /**
     * Creates a new tool instance for managing source files.
     *
     * @param context The context of the tool.
     *
     * @return A new tool instance for managing source files.
     *
     * @throws NullPointerException if {@code context} is {@code null}.
     * @throws MojoExecutionException if getting the tool of the instance fails.
     *
     * @see #createJomcTool(org.jomc.modlet.ModelContext, java.lang.String, java.lang.Class)
     */
    protected SourceFileProcessor createSourceFileProcessor( final ModelContext context ) throws MojoExecutionException
    {
        if ( context == null )
        {
            throw new NullPointerException( "context" );
        }

        return this.createJomcTool( context, this.sourceFileProcessorClassName, SourceFileProcessor.class );
    }

    /**
     * Creates a new tool instance for managing resource files.
     *
     * @param context The context of the tool.
     *
     * @return A new tool instance for managing resource files.
     *
     * @throws NullPointerException if {@code context} is {@code null}.
     * @throws MojoExecutionException if getting the tool of the instance fails.
     *
     * @see #createJomcTool(org.jomc.modlet.ModelContext, java.lang.String, java.lang.Class)
     */
    protected ResourceFileProcessor createResourceFileProcessor( final ModelContext context )
        throws MojoExecutionException
    {
        if ( context == null )
        {
            throw new NullPointerException( "context" );
        }

        return this.createJomcTool( context, this.resourceFileProcessorClassName, ResourceFileProcessor.class );
    }

    /**
     * Creates a new tool instance for managing class files.
     *
     * @param context The context of the tool.
     *
     * @return A new tool instance for managing class files.
     *
     * @throws NullPointerException if {@code context} is {@code null}.
     * @throws MojoExecutionException if getting the tool of the instance fails.
     *
     * @see #createJomcTool(org.jomc.modlet.ModelContext, java.lang.String, java.lang.Class)
     */
    protected ClassFileProcessor createClassFileProcessor( final ModelContext context ) throws MojoExecutionException
    {
        if ( context == null )
        {
            throw new NullPointerException( "context" );
        }

        return this.createJomcTool( context, this.classFileProcessorClassName, ClassFileProcessor.class );
    }

    /**
     * Creates a new {@code JomcTool} object for a given class name and type.
     *
     * @param context The context of the tool.
     * @param className The name of the class to create an object of.
     * @param type The class of the type of object to create.
     * @param <T> The type of the object to create.
     *
     * @return A new instance of the class with name {@code className}.
     *
     * @throws NullPointerException if {@code context}, {@code className} or {@code type} is {@code null}.
     * @throws MojoExecutionException if creating a new {@code JomcTool} object fails.
     *
     * @see #createObject(java.lang.String, java.lang.Class)
     * @see #setupJomcTool(org.jomc.modlet.ModelContext, org.jomc.tools.JomcTool)
     *
     * @since 1.2
     */
    protected <T extends JomcTool> T createJomcTool( final ModelContext context, final String className,
                                                     final Class<T> type ) throws MojoExecutionException
    {
        if ( context == null )
        {
            throw new NullPointerException( "context" );
        }
        if ( className == null )
        {
            throw new NullPointerException( "className" );
        }
        if ( type == null )
        {
            throw new NullPointerException( "type" );
        }

        final T tool = this.createObject( className, type );
        this.setupJomcTool( context, tool );
        return tool;
    }

    /**
     * Creates a new object for a given class name and type.
     *
     * @param className The name of the class to create an object of.
     * @param type The class of the type of object to create.
     * @param <T> The type of the object to create.
     *
     * @return A new instance of the class with name {@code className}.
     *
     * @throws NullPointerException if {@code className} or {@code type} is {@code null}.
     * @throws MojoExecutionException if creating a new object fails.
     *
     * @since 1.2
     */
    protected <T> T createObject( final String className, final Class<T> type ) throws MojoExecutionException
    {
        if ( className == null )
        {
            throw new NullPointerException( "className" );
        }
        if ( type == null )
        {
            throw new NullPointerException( "type" );
        }

        try
        {
            return Class.forName( className ).asSubclass( type ).newInstance();
        }
        catch ( final InstantiationException e )
        {
            throw new MojoExecutionException( Messages.getMessage( "failedCreatingObject", className ), e );
        }
        catch ( final IllegalAccessException e )
        {
            throw new MojoExecutionException( Messages.getMessage( "failedCreatingObject", className ), e );
        }
        catch ( final ClassNotFoundException e )
        {
            throw new MojoExecutionException( Messages.getMessage( "failedCreatingObject", className ), e );
        }
        catch ( final ClassCastException e )
        {
            throw new MojoExecutionException( Messages.getMessage( "failedCreatingObject", className ), e );
        }
    }

    /**
     * Creates an {@code URL} for a given resource location.
     * <p>This method first searches the class path of the plugin for a single resource matching {@code location}. If
     * such a resource is found, the URL of that resource is returned. If no such resource is found, an attempt is made
     * to parse the given location to an URL. On successful parsing, that URL is returned. Failing that, the given
     * location is interpreted as a file name relative to the project's base directory. If that file is found, the URL
     * of that file is returned. Otherwise {@code null} is returned.</p>
     *
     * @param location The location to create an {@code URL} from.
     *
     * @return An {@code URL} for {@code location} or {@code null} if parsing {@code location} to an URL fails and
     * {@code location} points to a non-existent resource.
     *
     * @throws NullPointerException if {@code location} is {@code null}.
     * @throws MojoExecutionException if creating an URL fails.
     *
     * @since 1.2
     */
    protected URL getResource( final String location ) throws MojoExecutionException
    {
        if ( location == null )
        {
            throw new NullPointerException( "location" );
        }

        try
        {
            String absolute = location;
            if ( !absolute.startsWith( "/" ) )
            {
                absolute = "/" + location;
            }

            URL resource = this.getClass().getResource( absolute );
            if ( resource == null )
            {
                try
                {
                    resource = new URL( location );
                }
                catch ( final MalformedURLException e )
                {
                    if ( this.isLoggable( Level.FINEST ) )
                    {
                        this.log( Level.FINEST, Messages.getMessage( e ), e );
                    }

                    resource = null;
                }
            }

            if ( resource == null )
            {
                final File f = this.getAbsoluteFile( location );

                if ( f.isFile() )
                {
                    resource = f.toURI().toURL();
                }
            }

            return resource;
        }
        catch ( final MalformedURLException e )
        {
            String m = Messages.getMessage( e );
            m = m == null ? "" : " " + m;

            throw new MojoExecutionException( Messages.getMessage( "malformedLocation", location, m ), e );
        }
    }

    /**
     * Creates an {@code URL} for a given directory location.
     * <p>This method first attempts to parse the given location to an URL. On successful parsing, that URL is returned.
     * Failing that, the given location is interpreted as a directory name relative to the project's base directory.
     * If that directory is found, the URL of that directory is returned. Otherwise {@code null} is returned.</p>
     *
     * @param location The directory location to create an {@code URL} from.
     *
     * @return An {@code URL} for {@code location} or {@code null} if parsing {@code location} to an URL fails and
     * {@code location} points to a non-existent directory.
     *
     * @throws NullPointerException if {@code location} is {@code null}.
     * @throws MojoExecutionException if creating an URL fails.
     *
     * @since 1.2
     */
    protected URL getDirectory( final String location ) throws MojoExecutionException
    {
        if ( location == null )
        {
            throw new NullPointerException( "location" );
        }

        try
        {
            URL resource = null;

            try
            {
                resource = new URL( location );
            }
            catch ( final MalformedURLException e )
            {
                if ( this.isLoggable( Level.FINEST ) )
                {
                    this.log( Level.FINEST, Messages.getMessage( e ), e );
                }

                resource = null;
            }

            if ( resource == null )
            {
                final File f = this.getAbsoluteFile( location );

                if ( f.isDirectory() )
                {
                    resource = f.toURI().toURL();
                }
            }

            return resource;
        }
        catch ( final MalformedURLException e )
        {
            String m = Messages.getMessage( e );
            m = m == null ? "" : " " + m;

            throw new MojoExecutionException( Messages.getMessage( "malformedLocation", location, m ), e );
        }
    }

    /**
     * Creates a new {@code Transformer} from a given {@code TransformerResourceType}.
     *
     * @param resource The resource to initialise the transformer with.
     *
     * @return A {@code Transformer} for {@code resource} or {@code null} if {@code resource} is not found and flagged
     * optional.
     *
     * @throws NullPointerException if {@code resource} is {@code null}.
     * @throws MojoExecutionException if creating a transformer fails.
     *
     * @see #getResource(java.lang.String)
     * @since 1.2
     */
    protected Transformer getTransformer( final TransformerResourceType resource ) throws MojoExecutionException
    {
        if ( resource == null )
        {
            throw new NullPointerException( "resource" );
        }

        InputStream in = null;
        final URL url = this.getResource( resource.getLocation() );
        final ErrorListener errorListener = new ErrorListener()
        {

            public void warning( final TransformerException exception ) throws TransformerException
            {
                try
                {
                    log( Level.WARNING, Messages.getMessage( exception ), exception );
                }
                catch ( final MojoExecutionException e )
                {
                    getLog().warn( exception );
                    getLog().error( e );
                }
            }

            public void error( final TransformerException exception ) throws TransformerException
            {
                try
                {
                    log( Level.SEVERE, Messages.getMessage( exception ), exception );
                }
                catch ( final MojoExecutionException e )
                {
                    getLog().error( exception );
                    getLog().error( e );
                }

                throw exception;
            }

            public void fatalError( final TransformerException exception ) throws TransformerException
            {
                try
                {
                    log( Level.SEVERE, Messages.getMessage( exception ), exception );
                }
                catch ( final MojoExecutionException e )
                {
                    getLog().error( exception );
                    getLog().error( e );
                }

                throw exception;
            }

        };

        try
        {
            if ( url != null )
            {
                if ( this.isLoggable( Level.FINER ) )
                {
                    this.log( Level.FINER, Messages.getMessage( "loadingTransformer", url.toExternalForm() ), null );
                }

                final URLConnection con = url.openConnection();
                con.setConnectTimeout( resource.getConnectTimeout() );
                con.setReadTimeout( resource.getReadTimeout() );
                con.connect();
                in = con.getInputStream();

                final TransformerFactory transformerFactory = TransformerFactory.newInstance();
                transformerFactory.setErrorListener( errorListener );
                final Transformer transformer =
                    transformerFactory.newTransformer( new StreamSource( in, url.toURI().toASCIIString() ) );

                transformer.setErrorListener( errorListener );

                for ( Map.Entry<Object, Object> e : System.getProperties().entrySet() )
                {
                    transformer.setParameter( e.getKey().toString(), e.getValue() );
                }

                if ( this.getMavenProject().getProperties() != null )
                {
                    for ( Map.Entry<Object, Object> e : this.getMavenProject().getProperties().entrySet() )
                    {
                        transformer.setParameter( e.getKey().toString(), e.getValue() );
                    }
                }

                if ( this.transformationParameterResources != null )
                {
                    for ( int i = 0, s0 = this.transformationParameterResources.size(); i < s0; i++ )
                    {
                        for ( Map.Entry<Object, Object> e :
                              this.getProperties( this.transformationParameterResources.get( i ) ).entrySet() )
                        {
                            transformer.setParameter( e.getKey().toString(), e.getValue() );
                        }
                    }
                }

                if ( this.transformationParameters != null )
                {
                    for ( Map.Entry<String, Object> e : this.transformationParameters.entrySet() )
                    {
                        transformer.setParameter( e.getKey(), e.getValue() );
                    }
                }

                for ( int i = 0, s0 = resource.getTransformationParameterResources().size(); i < s0; i++ )
                {
                    for ( Map.Entry<Object, Object> e :
                          this.getProperties( resource.getTransformationParameterResources().get( i ) ).entrySet() )
                    {
                        transformer.setParameter( e.getKey().toString(), e.getValue() );
                    }
                }

                for ( Map.Entry<String, Object> e : resource.getTransformationParameters().entrySet() )
                {
                    transformer.setParameter( e.getKey(), e.getValue() );
                }

                for ( Map.Entry<String, String> e : resource.getTransformationOutputProperties().entrySet() )
                {
                    transformer.setOutputProperty( e.getKey(), e.getValue() );
                }

                return transformer;
            }
            else if ( resource.isOptional() )
            {
                if ( this.isLoggable( Level.WARNING ) )
                {
                    this.log( Level.WARNING, Messages.getMessage(
                        "transformerNotFound", resource.getLocation() ), null );

                }
            }
            else
            {
                throw new MojoExecutionException( Messages.getMessage(
                    "transformerNotFound", resource.getLocation() ) );

            }
        }
        catch ( final URISyntaxException e )
        {
            throw new MojoExecutionException( Messages.getMessage( e ), e );
        }
        catch ( final TransformerConfigurationException e )
        {
            String m = Messages.getMessage( e );
            if ( m == null )
            {
                m = Messages.getMessage( e.getException() );
            }

            m = m == null ? "" : " " + m;

            throw new MojoExecutionException( Messages.getMessage(
                "failedCreatingTransformer", resource.getLocation(), m ), e );

        }
        catch ( final SocketTimeoutException e )
        {
            String m = Messages.getMessage( e );
            m = m == null ? "" : " " + m;

            if ( resource.isOptional() )
            {
                if ( this.isLoggable( Level.WARNING ) )
                {
                    this.log( Level.WARNING, Messages.getMessage(
                        "failedLoadingTransformer", url.toExternalForm(), m ), e );

                }
            }
            else
            {
                throw new MojoExecutionException( Messages.getMessage(
                    "failedLoadingTransformer", url.toExternalForm(), m ), e );

            }
        }
        catch ( final IOException e )
        {
            String m = Messages.getMessage( e );
            m = m == null ? "" : " " + m;

            if ( resource.isOptional() )
            {
                if ( this.isLoggable( Level.WARNING ) )
                {
                    this.log( Level.WARNING, Messages.getMessage(
                        "failedLoadingTransformer", url.toExternalForm(), m ), e );

                }
            }
            else
            {
                throw new MojoExecutionException( Messages.getMessage(
                    "failedLoadingTransformer", url.toExternalForm(), m ), e );

            }
        }
        finally
        {
            IOUtils.closeQuietly( in );
        }

        return null;
    }

    /**
     * Creates a new {@code Properties} instance from a {@code PropertiesResourceType}.
     *
     * @param propertiesResourceType The {@code PropertiesResourceType} specifying the properties to create.
     *
     * @return The properties for {@code propertiesResourceType}.
     *
     * @throws NullPointerException if {@code propertiesResourceType} is {@code null}.
     * @throws MojoExecutionException if loading properties fails.
     *
     * @see #getResource(java.lang.String)
     * @since 1.2
     */
    protected Properties getProperties( final PropertiesResourceType propertiesResourceType )
        throws MojoExecutionException
    {
        if ( propertiesResourceType == null )
        {
            throw new NullPointerException( "propertiesResourceType" );
        }

        InputStream in = null;
        final URL url = this.getResource( propertiesResourceType.getLocation() );
        final Properties properties = new Properties();

        try
        {
            if ( url != null )
            {
                if ( this.isLoggable( Level.FINER ) )
                {
                    this.log( Level.FINER, Messages.getMessage( "loadingProperties", url.toExternalForm() ), null );
                }

                final URLConnection con = url.openConnection();
                con.setConnectTimeout( propertiesResourceType.getConnectTimeout() );
                con.setReadTimeout( propertiesResourceType.getReadTimeout() );
                con.connect();

                in = con.getInputStream();

                if ( PropertiesResourceType.PLAIN_FORMAT.equalsIgnoreCase( propertiesResourceType.getFormat() ) )
                {
                    properties.load( in );
                }
                else if ( PropertiesResourceType.XML_FORMAT.equalsIgnoreCase( propertiesResourceType.getFormat() ) )
                {
                    properties.loadFromXML( in );
                }
            }
            else if ( propertiesResourceType.isOptional() )
            {
                if ( this.isLoggable( Level.WARNING ) )
                {
                    this.log( Level.WARNING, Messages.getMessage(
                        "propertiesNotFound", propertiesResourceType.getLocation() ), null );

                }
            }
            else
            {
                throw new MojoExecutionException( Messages.getMessage(
                    "propertiesNotFound", propertiesResourceType.getLocation() ) );

            }
        }
        catch ( final SocketTimeoutException e )
        {
            String m = Messages.getMessage( e );
            m = m == null ? "" : " " + m;

            if ( propertiesResourceType.isOptional() )
            {
                if ( this.isLoggable( Level.WARNING ) )
                {
                    this.log( Level.WARNING, Messages.getMessage(
                        "failedLoadingProperties", url.toExternalForm(), m ), e );

                }
            }
            else
            {
                throw new MojoExecutionException( Messages.getMessage(
                    "failedLoadingProperties", url.toExternalForm(), m ), e );

            }
        }
        catch ( final IOException e )
        {
            String m = Messages.getMessage( e );
            m = m == null ? "" : " " + m;

            if ( propertiesResourceType.isOptional() )
            {
                if ( this.isLoggable( Level.WARNING ) )
                {
                    this.log( Level.WARNING, Messages.getMessage(
                        "failedLoadingProperties", url.toExternalForm(), m ), e );

                }
            }
            else
            {
                throw new MojoExecutionException( Messages.getMessage(
                    "failedLoadingProperties", url.toExternalForm(), m ), e );

            }
        }
        finally
        {
            IOUtils.closeQuietly( in );
        }

        return properties;
    }

    /**
     * Tests if messages at a given level are logged.
     *
     * @param level The level to test.
     *
     * @return {@code true} if messages at {@code level} are logged; {@code false} if messages at {@code level} are
     * suppressed.
     *
     * @throws NullPointerException if {@code level} is {@code null}.
     * @throws MojoExecutionException if testing the level fails.
     *
     * @see #isVerbose()
     * @since 1.2
     */
    protected boolean isLoggable( final Level level ) throws MojoExecutionException
    {
        if ( level == null )
        {
            throw new NullPointerException( "level" );
        }

        boolean loggable = false;

        if ( level.intValue() <= Level.CONFIG.intValue() )
        {
            loggable = this.getLog().isDebugEnabled();
        }
        else if ( level.intValue() <= Level.INFO.intValue() )
        {
            loggable = this.getLog().isInfoEnabled() && this.isVerbose();
        }
        else if ( level.intValue() <= Level.WARNING.intValue() )
        {
            loggable = this.getLog().isWarnEnabled();
        }
        else if ( level.intValue() <= Level.SEVERE.intValue() )
        {
            loggable = this.getLog().isErrorEnabled();
        }

        return loggable;
    }

    /**
     * Logs a separator at a given level.
     *
     * @param level The level to log a separator at.
     *
     * @throws MojoExecutionException if logging fails.
     *
     * @deprecated As of JOMC 1.1, please use method {@link #logSeparator()}. This method will be removed in version
     * 2.0.
     */
    @Deprecated
    protected void logSeparator( final Level level ) throws MojoExecutionException
    {
        this.logSeparator();
    }

    /**
     * Logs a separator.
     *
     * @throws MojoExecutionException if logging fails.
     *
     * @since 1.1
     */
    protected void logSeparator() throws MojoExecutionException
    {
        if ( this.isLoggable( Level.INFO ) )
        {
            this.log( Level.INFO, Messages.getMessage( "separator" ), null );
        }
    }

    /**
     * Logs a message stating a tool is starting to process a module.
     *
     * @param toolName The tool starting execution.
     * @param module The module getting processed.
     *
     * @throws MojoExecutionException if logging fails.
     */
    protected void logProcessingModule( final String toolName, final String module ) throws MojoExecutionException
    {
        if ( this.isLoggable( Level.INFO ) )
        {
            this.log( Level.INFO, Messages.getMessage( "processingModule", toolName, module ), null );
        }
    }

    /**
     * Logs a message stating a tool is starting to process a model.
     *
     * @param toolName The tool starting execution.
     * @param model The model getting processed.
     *
     * @throws MojoExecutionException if logging fails.
     *
     * @since 1.1
     */
    protected void logProcessingModel( final String toolName, final String model ) throws MojoExecutionException
    {
        if ( this.isLoggable( Level.INFO ) )
        {
            this.log( Level.INFO, Messages.getMessage( "processingModel", toolName, model ), null );
        }
    }

    /**
     * Logs a message stating that a module has not been found.
     *
     * @param module The module which has not been found.
     *
     * @throws MojoExecutionException if logging fails.
     */
    protected void logMissingModule( final String module ) throws MojoExecutionException
    {
        if ( this.isLoggable( Level.WARNING ) )
        {
            this.log( Level.WARNING, Messages.getMessage( "missingModule", module ), null );
        }
    }

    /**
     * Logs a message stating that a tool successfully completed execution.
     *
     * @param toolName The name of the tool.
     *
     * @throws MojoExecutionException if logging fails.
     */
    protected void logToolSuccess( final String toolName ) throws MojoExecutionException
    {
        if ( this.isLoggable( Level.INFO ) )
        {
            this.log( Level.INFO, Messages.getMessage( "toolSuccess", toolName ), null );
        }
    }

    /**
     * Logs a {@code ModelValidationReport}.
     *
     * @param context The context to use when marshalling detail elements of the report.
     * @param level The level to log at.
     * @param report The report to log.
     *
     * @throws MojoExecutionException if logging {@code report} fails.
     */
    protected void log( final ModelContext context, final Level level, final ModelValidationReport report )
        throws MojoExecutionException
    {
        try
        {
            if ( !report.getDetails().isEmpty() )
            {
                this.logSeparator();
                Marshaller marshaller = null;

                for ( ModelValidationReport.Detail detail : report.getDetails() )
                {
                    this.log( detail.getLevel(), "o " + detail.getMessage(), null );

                    if ( detail.getElement() != null && this.isLoggable( Level.FINEST ) )
                    {
                        if ( marshaller == null )
                        {
                            marshaller = context.createMarshaller( this.getModel() );
                            marshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
                        }

                        final StringWriter stringWriter = new StringWriter();
                        marshaller.marshal( detail.getElement(), stringWriter );
                        this.log( Level.FINEST, stringWriter.toString(), null );
                    }
                }
            }
        }
        catch ( final ModelException e )
        {
            throw new MojoExecutionException( Messages.getMessage( e ), e );
        }
        catch ( final JAXBException e )
        {
            String message = Messages.getMessage( e );
            if ( message == null && e.getLinkedException() != null )
            {
                message = Messages.getMessage( e.getLinkedException() );
            }

            throw new MojoExecutionException( message, e );
        }
    }

    /**
     * Logs a message and throwable at a given level.
     *
     * @param level The level to log at.
     * @param message The message to log or {@code null}.
     * @param throwable The throwable to log or {@code null}.
     *
     * @throws MojoExecutionException if logging fails.
     */
    protected void log( final Level level, final String message, final Throwable throwable )
        throws MojoExecutionException
    {
        try
        {
            if ( this.isLoggable( level ) )
            {
                String line;
                final BufferedReader reader = new BufferedReader( new StringReader( message == null ? "" : message ) );
                boolean throwableLogged = false;

                while ( ( line = reader.readLine() ) != null )
                {
                    final String mojoMessage =
                        Messages.getMessage( this.getLog().isDebugEnabled() ? "debugMessage" : "logMessage", line,
                                             Thread.currentThread().getName(), new Date( System.currentTimeMillis() ) );

                    if ( level.intValue() <= Level.CONFIG.intValue() )
                    {
                        this.getLog().debug( mojoMessage, throwableLogged ? null : throwable );
                    }
                    else if ( level.intValue() <= Level.INFO.intValue() )
                    {
                        this.getLog().info( mojoMessage, throwableLogged ? null : throwable );
                    }
                    else if ( level.intValue() <= Level.WARNING.intValue() )
                    {
                        this.getLog().warn( mojoMessage, throwableLogged ? null : throwable );
                    }
                    else if ( level.intValue() <= Level.SEVERE.intValue() )
                    {
                        this.getLog().error( mojoMessage, throwableLogged ? null : throwable );
                    }

                    throwableLogged = true;
                }
            }
        }
        catch ( final IOException e )
        {
            this.getLog().error( e );
            throw new AssertionError( e );
        }
    }

    /**
     * Configures a {@code ModelContext} instance.
     *
     * @param context The model context to configure.
     *
     * @throws NullPointerException if {@code context} is {@code null}.
     * @throws MojoExecutionException if configuring {@code context} fails.
     */
    protected void setupModelContext( final ModelContext context ) throws MojoExecutionException
    {
        if ( context == null )
        {
            throw new NullPointerException( "context" );
        }

        if ( this.isVerbose() || this.getLog().isDebugEnabled() )
        {
            context.setLogLevel( this.getLog().isDebugEnabled() ? Level.ALL : Level.INFO );
        }

        context.setModletSchemaSystemId( this.modletSchemaSystemId );
        context.getListeners().add( new ModelContext.Listener()
        {

            @Override
            public void onLog( final Level level, final String message, final Throwable t )
            {
                super.onLog( level, message, t );

                try
                {
                    log( level, message, t );
                }
                catch ( final MojoExecutionException e )
                {
                    getLog().error( e );
                }
            }

        } );

        if ( this.providerLocation != null )
        {
            context.setAttribute( DefaultModelContext.PROVIDER_LOCATION_ATTRIBUTE_NAME, this.providerLocation );
        }

        if ( this.platformProviderLocation != null )
        {
            context.setAttribute( DefaultModelContext.PLATFORM_PROVIDER_LOCATION_ATTRIBUTE_NAME,
                                  this.platformProviderLocation );

        }

        if ( this.modletLocation != null )
        {
            context.setAttribute( DefaultModletProvider.MODLET_LOCATION_ATTRIBUTE_NAME, this.modletLocation );
        }

        if ( this.transformerLocation != null )
        {
            context.setAttribute( DefaultModelProcessor.TRANSFORMER_LOCATION_ATTRIBUTE_NAME, this.transformerLocation );
        }

        if ( this.moduleLocation != null )
        {
            context.setAttribute( DefaultModelProvider.MODULE_LOCATION_ATTRIBUTE_NAME, this.moduleLocation );
        }

        context.setAttribute( ToolsModelProvider.MODEL_OBJECT_CLASSPATH_RESOLUTION_ENABLED_ATTRIBUTE_NAME,
                              this.modelObjectClasspathResolutionEnabled );

        context.setAttribute( ToolsModelProcessor.MODEL_OBJECT_CLASSPATH_RESOLUTION_ENABLED_ATTRIBUTE_NAME,
                              this.modelObjectClasspathResolutionEnabled );

        context.setAttribute( DefaultModletProvider.VALIDATING_ATTRIBUTE_NAME, this.modletResourceValidationEnabled );
        context.setAttribute( DefaultModelProvider.VALIDATING_ATTRIBUTE_NAME, this.modelResourceValidationEnabled );

        if ( this.modelContextAttributes != null )
        {
            for ( Map.Entry<String, Object> e : this.modelContextAttributes.entrySet() )
            {
                if ( e.getValue() != null )
                {
                    context.setAttribute( e.getKey(), e.getValue() );
                }
                else
                {
                    context.clearAttribute( e.getKey() );
                }
            }
        }
    }

    /**
     * Configures a {@code JomcTool} instance.
     *
     * @param context The model context to use for configuring {@code tool}.
     * @param tool The tool to configure.
     *
     * @throws NullPointerException if {@code context} of {@code tool} is {@code null}.
     * @throws MojoExecutionException if configuring {@code tool} fails.
     */
    protected void setupJomcTool( final ModelContext context, final JomcTool tool ) throws MojoExecutionException
    {
        if ( context == null )
        {
            throw new NullPointerException( "context" );
        }
        if ( tool == null )
        {
            throw new NullPointerException( "tool" );
        }

        try
        {
            if ( this.isVerbose() || this.getLog().isDebugEnabled() )
            {
                tool.setLogLevel( this.getLog().isDebugEnabled() ? Level.ALL : Level.INFO );
            }

            tool.getListeners().add( new JomcTool.Listener()
            {

                @Override
                public void onLog( final Level level, final String message, final Throwable t )
                {
                    super.onLog( level, message, t );

                    try
                    {
                        log( level, message, t );
                    }
                    catch ( final MojoExecutionException e )
                    {
                        getLog().error( e );
                    }
                }

            } );

            tool.setTemplateEncoding( this.templateEncoding );
            tool.setInputEncoding( this.sourceEncoding );
            tool.setOutputEncoding( this.sourceEncoding );
            tool.setTemplateProfile( this.templateProfile );
            tool.setModel( this.getModel( context ) );

            if ( this.indentation != null )
            {
                tool.setIndentation( StringEscapeUtils.unescapeJava( this.indentation ) );
            }

            if ( this.lineSeparator != null )
            {
                tool.setLineSeparator( StringEscapeUtils.unescapeJava( this.lineSeparator ) );
            }

            if ( this.locale != null )
            {
                tool.setLocale( new Locale( StringUtils.defaultString( this.locale.getLanguage() ),
                                            StringUtils.defaultString( this.locale.getCountry() ),
                                            StringUtils.defaultString( this.locale.getVariant() ) ) );

            }

            if ( this.velocityPropertyResources != null )
            {
                for ( int i = 0, s0 = this.velocityPropertyResources.size(); i < s0; i++ )
                {
                    for ( Map.Entry<Object, Object> e :
                          this.getProperties( this.velocityPropertyResources.get( i ) ).entrySet() )
                    {
                        if ( e.getValue() != null )
                        {
                            tool.getVelocityEngine().setProperty( e.getKey().toString(), e );
                        }
                        else
                        {
                            tool.getVelocityEngine().clearProperty( e.getKey().toString() );
                        }
                    }
                }
            }

            if ( this.velocityProperties != null )
            {
                for ( Map.Entry<String, Object> e : this.velocityProperties.entrySet() )
                {
                    if ( e.getValue() != null )
                    {
                        tool.getVelocityEngine().setProperty( e.getKey(), e.getValue() );
                    }
                    else
                    {
                        tool.getVelocityEngine().clearProperty( e.getKey() );
                    }
                }
            }

            for ( Map.Entry<Object, Object> e : System.getProperties().entrySet() )
            {
                tool.getTemplateParameters().put( e.getKey().toString(), e.getValue() );
            }

            if ( this.getMavenProject().getProperties() != null )
            {
                for ( Map.Entry<Object, Object> e : System.getProperties().entrySet() )
                {
                    tool.getTemplateParameters().put( e.getKey().toString(), e.getValue() );
                }
            }

            if ( this.templateParameterResources != null )
            {
                for ( int i = 0, s0 = this.templateParameterResources.size(); i < s0; i++ )
                {
                    for ( Map.Entry<Object, Object> e :
                          this.getProperties( this.templateParameterResources.get( i ) ).entrySet() )
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
            }

            if ( this.templateParameters != null )
            {
                for ( Map.Entry<String, Object> e : this.templateParameters.entrySet() )
                {
                    if ( e.getValue() != null )
                    {
                        tool.getTemplateParameters().put( e.getKey(), e.getValue() );
                    }
                    else
                    {
                        tool.getTemplateParameters().remove( e.getKey() );
                    }
                }
            }

            if ( this.templateLocation != null )
            {
                final URL url = this.getDirectory( this.templateLocation );
                tool.setTemplateLocation( url );

                if ( url == null && this.isLoggable( Level.WARNING ) )
                {
                    this.log( Level.WARNING, Messages.getMessage( "locationNotFound", this.templateLocation ), null );
                }
            }
        }
        catch ( final IOException e )
        {
            throw new MojoExecutionException( Messages.getMessage( e ), e );
        }
    }

    private Artifact getPluginArtifact( final Artifact a )
    {
        for ( int i = 0, s0 = this.pluginArtifacts.size(); i < s0; i++ )
        {
            final Artifact pluginArtifact = this.pluginArtifacts.get( i );

            if ( pluginArtifact.getGroupId().equals( a.getGroupId() )
                 && pluginArtifact.getArtifactId().equals( a.getArtifactId() )
                 && ( pluginArtifact.hasClassifier()
                      ? pluginArtifact.getClassifier().equals( a.getClassifier() )
                      : !a.hasClassifier() ) )
            {
                return pluginArtifact;
            }
        }

        return null;
    }

}
