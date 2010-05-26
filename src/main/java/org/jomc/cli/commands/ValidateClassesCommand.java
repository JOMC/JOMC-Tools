// SECTION-START[License Header]
// <editor-fold defaultstate="collapsed" desc=" Generated License ">
/*
 *   Copyright (c) 2010 The JOMC Project
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
// </editor-fold>
// SECTION-END
package org.jomc.cli.commands;

import java.util.logging.Level;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.util.JAXBSource;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.jomc.model.ModelContext;
import org.jomc.model.ModelValidationReport;
import org.jomc.model.Module;
import org.jomc.model.Modules;
import org.jomc.model.ObjectFactory;
import org.jomc.tools.ClassFileProcessor;

// SECTION-START[Documentation]
// <editor-fold defaultstate="collapsed" desc=" Generated Documentation ">
/**
 * Command line interface for validating model objects of class files with the {@code org.jomc.tools.ClassFileProcessor} tool.
 * <p><b>Specifications</b><ul>
 * <li>{@code org.jomc.cli.Command} {@code 1.0} {@code Multiton}</li>
 * </ul></p>
 * <p><b>Properties</b><ul>
 * <li>"{@link #getAbbreviatedCommandName abbreviatedCommandName}"
 * <blockquote>Property of type {@code java.lang.String}.
 * <p>Abbreviated name of the command.</p>
 * </blockquote></li>
 * <li>"{@link #getCommandName commandName}"
 * <blockquote>Property of type {@code java.lang.String}.
 * <p>Name of the command.</p>
 * </blockquote></li>
 * <li>"{@link #getProviderExcludes providerExcludes}"
 * <blockquote>Property of type {@code java.lang.String}.
 * <p>List of providers to exclude from any {@code META-INF/services} file separated by {@code :}.</p>
 * </blockquote></li>
 * <li>"{@link #getSchemaExcludes schemaExcludes}"
 * <blockquote>Property of type {@code java.lang.String}.
 * <p>List of schema context-ids to exclude from any {@code META-INF/jomc-schemas.xml} file separated by {@code :}.</p>
 * </blockquote></li>
 * <li>"{@link #getServiceExcludes serviceExcludes}"
 * <blockquote>Property of type {@code java.lang.String}.
 * <p>List of service classes to exclude from any {@code META-INF/jomc-services.xml} file separated by {@code :}.</p>
 * </blockquote></li>
 * </ul></p>
 * <p><b>Dependencies</b><ul>
 * <li>"{@link #getClasspathOption ClasspathOption}"<blockquote>
 * Dependency on {@code org.apache.commons.cli.Option} bound to an instance.</blockquote></li>
 * <li>"{@link #getDocumentsOption DocumentsOption}"<blockquote>
 * Dependency on {@code org.apache.commons.cli.Option} bound to an instance.</blockquote></li>
 * <li>"{@link #getLocale Locale}"<blockquote>
 * Dependency on {@code java.util.Locale} at specification level 1.1 bound to an instance.</blockquote></li>
 * <li>"{@link #getModuleLocationOption ModuleLocationOption}"<blockquote>
 * Dependency on {@code org.apache.commons.cli.Option} bound to an instance.</blockquote></li>
 * <li>"{@link #getModuleNameOption ModuleNameOption}"<blockquote>
 * Dependency on {@code org.apache.commons.cli.Option} bound to an instance.</blockquote></li>
 * <li>"{@link #getNoClasspathResolutionOption NoClasspathResolutionOption}"<blockquote>
 * Dependency on {@code org.apache.commons.cli.Option} bound to an instance.</blockquote></li>
 * <li>"{@link #getNoModelProcessingOption NoModelProcessingOption}"<blockquote>
 * Dependency on {@code org.apache.commons.cli.Option} bound to an instance.</blockquote></li>
 * <li>"{@link #getPlatformProviderLocationOption PlatformProviderLocationOption}"<blockquote>
 * Dependency on {@code org.apache.commons.cli.Option} bound to an instance.</blockquote></li>
 * <li>"{@link #getProviderLocationOption ProviderLocationOption}"<blockquote>
 * Dependency on {@code org.apache.commons.cli.Option} bound to an instance.</blockquote></li>
 * <li>"{@link #getSchemaLocationOption SchemaLocationOption}"<blockquote>
 * Dependency on {@code org.apache.commons.cli.Option} bound to an instance.</blockquote></li>
 * <li>"{@link #getServiceLocationOption ServiceLocationOption}"<blockquote>
 * Dependency on {@code org.apache.commons.cli.Option} bound to an instance.</blockquote></li>
 * <li>"{@link #getTransformerLocationOption TransformerLocationOption}"<blockquote>
 * Dependency on {@code org.apache.commons.cli.Option} bound to an instance.</blockquote></li>
 * </ul></p>
 * <p><b>Messages</b><ul>
 * <li>"{@link #getApplicationTitle applicationTitle}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>JOMC Version 1.0-beta-4-SNAPSHOT Build 2010-05-26T15:16:59+0200</pre></td></tr>
 * </table>
 * <li>"{@link #getCannotProcessMessage cannotProcessMessage}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>Cannot process ''{0}'': {1}</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Kann ''{0}'' nicht verarbeiten: {1}</pre></td></tr>
 * </table>
 * <li>"{@link #getClasspathElementInfo classpathElementInfo}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>Classpath element: ''{0}''</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Klassenpfad-Element: ''{0}''</pre></td></tr>
 * </table>
 * <li>"{@link #getClasspathElementNotFoundWarning classpathElementNotFoundWarning}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>Classpath element ''{0}'' ignored. File not found.</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Klassenpfad-Element ''{0}'' ignoriert. Datei nicht gefunden.</pre></td></tr>
 * </table>
 * <li>"{@link #getDefaultLogLevelInfo defaultLogLevelInfo}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>Default log level: ''{0}''</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Standard-Protokollierungsstufe: ''{0}''</pre></td></tr>
 * </table>
 * <li>"{@link #getDocumentFileInfo documentFileInfo}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>Document file: ''{0}''</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Dokument-Datei: ''{0}''</pre></td></tr>
 * </table>
 * <li>"{@link #getDocumentFileNotFoundWarning documentFileNotFoundWarning}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>Document file ''{0}'' ignored. File not found.</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Dokument-Datei ''{0}'' ignoriert. Datei nicht gefunden.</pre></td></tr>
 * </table>
 * <li>"{@link #getExcludedModuleFromClasspathInfo excludedModuleFromClasspathInfo}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>Module ''{0}'' from class path ignored. Module with identical name already loaded.</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Modul ''{0}'' aus Klassenpfad ignoriert. Modul mit identischem Namen bereits geladen.</pre></td></tr>
 * </table>
 * <li>"{@link #getExcludedProviderInfo excludedProviderInfo}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>Provider ''{1}'' from class path resource ''{0}'' ignored.</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Provider ''{1}'' aus Klassenpfad-Ressource ''{0}'' ignoriert.</pre></td></tr>
 * </table>
 * <li>"{@link #getExcludedSchemaInfo excludedSchemaInfo}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>Context ''{1}'' from class path resource ''{0}'' ignored.</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Kontext ''{1}'' aus Klassenpfad-Ressource ''{0}'' ignoriert.</pre></td></tr>
 * </table>
 * <li>"{@link #getExcludedServiceInfo excludedServiceInfo}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>Service ''{1}'' from class path resource ''{0}'' ignored.</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Service ''{1}'' aus Klassenpfad-Ressource ''{0}'' ignoriert.</pre></td></tr>
 * </table>
 * <li>"{@link #getInvalidModelMessage invalidModelMessage}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>Invalid model.</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Ung&uuml;ltiges Modell.</pre></td></tr>
 * </table>
 * <li>"{@link #getLongDescriptionMessage longDescriptionMessage}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>Example:
 *   jomc validate-classes -cp examples/lib/commons-cli-1.2.jar:target/classes \
 *                         -df examples/xml/jomc-cli.xml \
 *                         -v</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Beispiel:
 *   jomc validate-classes -cp examples/lib/commons-cli-1.2.jar:target/classes \
 *                         -df examples/xml/jomc-cli.xml \
 *                         -v</pre></td></tr>
 * </table>
 * <li>"{@link #getMissingModuleMessage missingModuleMessage}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>Module ''{0}'' not found.</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Modul ''{0}'' nicht gefunden.</pre></td></tr>
 * </table>
 * <li>"{@link #getModulesReport modulesReport}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>Modules</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Module</pre></td></tr>
 * </table>
 * <li>"{@link #getSeparator separator}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>--------------------------------------------------------------------------------</pre></td></tr>
 * </table>
 * <li>"{@link #getShortDescriptionMessage shortDescriptionMessage}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>Validates model objects of class files.</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Pr&uuml;ft Klassendateien.</pre></td></tr>
 * </table>
 * <li>"{@link #getStartingModuleProcessingMessage startingModuleProcessingMessage}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>Executing command {0} with module ''{1}'' ...</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>F&uuml;hrt Befehl {0} mit Modul ''{1}'' aus ... </pre></td></tr>
 * </table>
 * <li>"{@link #getStartingProcessingMessage startingProcessingMessage}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>Executing command {0} ...</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>F&uuml;hrt Befehl {0} aus ... </pre></td></tr>
 * </table>
 * <li>"{@link #getToolFailureMessage toolFailureMessage}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>{0} failure.</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>{0} fehlgeschlagen.</pre></td></tr>
 * </table>
 * <li>"{@link #getToolSuccessMessage toolSuccessMessage}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>{0} successful.</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>{0} erfolgreich.</pre></td></tr>
 * </table>
 * </ul></p>
 *
 * @author <a href="mailto:schulte2005@users.sourceforge.net">Christian Schulte</a> 1.0
 * @version $Id$
 */
// </editor-fold>
// SECTION-END
// SECTION-START[Annotations]
// <editor-fold defaultstate="collapsed" desc=" Generated Annotations ">
@javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor", comments = "See http://jomc.sourceforge.net/jomc/1.0-beta-4-SNAPSHOT/jomc-tools" )
// </editor-fold>
// SECTION-END
public final class ValidateClassesCommand extends AbstractJomcCommand
{
    // SECTION-START[Command]

    /** Options of the instance. */
    private Options options;

    @Override
    public Options getOptions()
    {
        if ( this.options == null )
        {
            this.options = super.getOptions();
        }

        return this.options;
    }

    public int executeCommand( final CommandLine commandLine ) throws Exception
    {
        final ClassLoader classLoader = new CommandLineClassLoader( commandLine );
        final ModelContext context = this.createModelContext( classLoader );
        final Modules modules = this.getModules( context, commandLine );
        final JAXBContext jaxbContext = context.createContext();
        final Marshaller marshaller = context.createMarshaller();
        final Unmarshaller unmarshaller = context.createUnmarshaller();
        final ModelValidationReport validationReport =
            context.validateModel( new JAXBSource( jaxbContext, new ObjectFactory().createModules( modules ) ) );

        this.log( validationReport, marshaller );

        if ( validationReport.isModelValid() )
        {
            final ClassFileProcessor tool = this.createClassFileProcessor();
            tool.setModules( modules );

            if ( commandLine.hasOption( this.getModuleNameOption().getOpt() ) )
            {
                final String moduleName = commandLine.getOptionValue( this.getModuleNameOption().getOpt() );
                final Module module = tool.getModules().getModule( moduleName );

                if ( module != null )
                {
                    if ( this.isLoggable( Level.INFO ) )
                    {
                        this.log( Level.INFO, this.getStartingModuleProcessingMessage(
                            this.getLocale(), this.getCommandName(), module.getName() ), null );

                    }

                    tool.validateModelObjects( module, unmarshaller, classLoader );
                }
                else if ( this.isLoggable( Level.WARNING ) )
                {
                    this.log( Level.WARNING, this.getMissingModuleMessage( this.getLocale(), moduleName ), null );
                }
            }
            else
            {
                if ( this.isLoggable( Level.INFO ) )
                {
                    this.log( Level.INFO, this.getStartingProcessingMessage(
                        this.getLocale(), this.getCommandName() ), null );

                }

                tool.validateModelObjects( unmarshaller, classLoader );
            }

            return STATUS_SUCCESS;
        }

        return STATUS_FAILURE;
    }

    // SECTION-END
    // SECTION-START[ValidateClassesCommand]
    // SECTION-END
    // SECTION-START[Constructors]
    // <editor-fold defaultstate="collapsed" desc=" Generated Constructors ">

    /** Creates a new {@code ValidateClassesCommand} instance. */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor", comments = "See http://jomc.sourceforge.net/jomc/1.0-beta-4-SNAPSHOT/jomc-tools" )
    public ValidateClassesCommand()
    {
        // SECTION-START[Default Constructor]
        super();
        // SECTION-END
    }
    // </editor-fold>
    // SECTION-END
    // SECTION-START[Dependencies]
    // <editor-fold defaultstate="collapsed" desc=" Generated Dependencies ">

    /**
     * Gets the {@code ClasspathOption} dependency.
     * <p>This method returns the "{@code JOMC CLI Classpath Option}" object of the {@code org.apache.commons.cli.Option} specification.</p>
     * <p>That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.</p>
     * @return The {@code ClasspathOption} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor", comments = "See http://jomc.sourceforge.net/jomc/1.0-beta-4-SNAPSHOT/jomc-tools" )
    private org.apache.commons.cli.Option getClasspathOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "ClasspathOption" );
        assert _d != null : "'ClasspathOption' dependency not found.";
        return _d;
    }

    /**
     * Gets the {@code DocumentsOption} dependency.
     * <p>This method returns the "{@code JOMC CLI Documents Option}" object of the {@code org.apache.commons.cli.Option} specification.</p>
     * <p>That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.</p>
     * @return The {@code DocumentsOption} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor", comments = "See http://jomc.sourceforge.net/jomc/1.0-beta-4-SNAPSHOT/jomc-tools" )
    private org.apache.commons.cli.Option getDocumentsOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "DocumentsOption" );
        assert _d != null : "'DocumentsOption' dependency not found.";
        return _d;
    }

    /**
     * Gets the {@code Locale} dependency.
     * <p>This method returns the "{@code default}" object of the {@code java.util.Locale} specification at specification level 1.1.</p>
     * <p>That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.</p>
     * @return The {@code Locale} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor", comments = "See http://jomc.sourceforge.net/jomc/1.0-beta-4-SNAPSHOT/jomc-tools" )
    private java.util.Locale getLocale()
    {
        final java.util.Locale _d = (java.util.Locale) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "Locale" );
        assert _d != null : "'Locale' dependency not found.";
        return _d;
    }

    /**
     * Gets the {@code ModuleLocationOption} dependency.
     * <p>This method returns the "{@code JOMC CLI Module Location Option}" object of the {@code org.apache.commons.cli.Option} specification.</p>
     * <p>That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.</p>
     * @return The {@code ModuleLocationOption} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor", comments = "See http://jomc.sourceforge.net/jomc/1.0-beta-4-SNAPSHOT/jomc-tools" )
    private org.apache.commons.cli.Option getModuleLocationOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "ModuleLocationOption" );
        assert _d != null : "'ModuleLocationOption' dependency not found.";
        return _d;
    }

    /**
     * Gets the {@code ModuleNameOption} dependency.
     * <p>This method returns the "{@code JOMC CLI Module Name Option}" object of the {@code org.apache.commons.cli.Option} specification.</p>
     * <p>That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.</p>
     * @return The {@code ModuleNameOption} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor", comments = "See http://jomc.sourceforge.net/jomc/1.0-beta-4-SNAPSHOT/jomc-tools" )
    private org.apache.commons.cli.Option getModuleNameOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "ModuleNameOption" );
        assert _d != null : "'ModuleNameOption' dependency not found.";
        return _d;
    }

    /**
     * Gets the {@code NoClasspathResolutionOption} dependency.
     * <p>This method returns the "{@code JOMC CLI No Classpath Resolution Option}" object of the {@code org.apache.commons.cli.Option} specification.</p>
     * <p>That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.</p>
     * @return The {@code NoClasspathResolutionOption} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor", comments = "See http://jomc.sourceforge.net/jomc/1.0-beta-4-SNAPSHOT/jomc-tools" )
    private org.apache.commons.cli.Option getNoClasspathResolutionOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "NoClasspathResolutionOption" );
        assert _d != null : "'NoClasspathResolutionOption' dependency not found.";
        return _d;
    }

    /**
     * Gets the {@code NoModelProcessingOption} dependency.
     * <p>This method returns the "{@code JOMC CLI No Model Processing Option}" object of the {@code org.apache.commons.cli.Option} specification.</p>
     * <p>That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.</p>
     * @return The {@code NoModelProcessingOption} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor", comments = "See http://jomc.sourceforge.net/jomc/1.0-beta-4-SNAPSHOT/jomc-tools" )
    private org.apache.commons.cli.Option getNoModelProcessingOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "NoModelProcessingOption" );
        assert _d != null : "'NoModelProcessingOption' dependency not found.";
        return _d;
    }

    /**
     * Gets the {@code PlatformProviderLocationOption} dependency.
     * <p>This method returns the "{@code JOMC CLI Platform Provider Location Option}" object of the {@code org.apache.commons.cli.Option} specification.</p>
     * <p>That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.</p>
     * @return The {@code PlatformProviderLocationOption} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor", comments = "See http://jomc.sourceforge.net/jomc/1.0-beta-4-SNAPSHOT/jomc-tools" )
    private org.apache.commons.cli.Option getPlatformProviderLocationOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "PlatformProviderLocationOption" );
        assert _d != null : "'PlatformProviderLocationOption' dependency not found.";
        return _d;
    }

    /**
     * Gets the {@code ProviderLocationOption} dependency.
     * <p>This method returns the "{@code JOMC CLI Provider Location Option}" object of the {@code org.apache.commons.cli.Option} specification.</p>
     * <p>That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.</p>
     * @return The {@code ProviderLocationOption} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor", comments = "See http://jomc.sourceforge.net/jomc/1.0-beta-4-SNAPSHOT/jomc-tools" )
    private org.apache.commons.cli.Option getProviderLocationOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "ProviderLocationOption" );
        assert _d != null : "'ProviderLocationOption' dependency not found.";
        return _d;
    }

    /**
     * Gets the {@code SchemaLocationOption} dependency.
     * <p>This method returns the "{@code JOMC CLI Schema Location Option}" object of the {@code org.apache.commons.cli.Option} specification.</p>
     * <p>That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.</p>
     * @return The {@code SchemaLocationOption} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor", comments = "See http://jomc.sourceforge.net/jomc/1.0-beta-4-SNAPSHOT/jomc-tools" )
    private org.apache.commons.cli.Option getSchemaLocationOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "SchemaLocationOption" );
        assert _d != null : "'SchemaLocationOption' dependency not found.";
        return _d;
    }

    /**
     * Gets the {@code ServiceLocationOption} dependency.
     * <p>This method returns the "{@code JOMC CLI Service Location Option}" object of the {@code org.apache.commons.cli.Option} specification.</p>
     * <p>That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.</p>
     * @return The {@code ServiceLocationOption} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor", comments = "See http://jomc.sourceforge.net/jomc/1.0-beta-4-SNAPSHOT/jomc-tools" )
    private org.apache.commons.cli.Option getServiceLocationOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "ServiceLocationOption" );
        assert _d != null : "'ServiceLocationOption' dependency not found.";
        return _d;
    }

    /**
     * Gets the {@code TransformerLocationOption} dependency.
     * <p>This method returns the "{@code JOMC CLI Transformer Location Option}" object of the {@code org.apache.commons.cli.Option} specification.</p>
     * <p>That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.</p>
     * @return The {@code TransformerLocationOption} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor", comments = "See http://jomc.sourceforge.net/jomc/1.0-beta-4-SNAPSHOT/jomc-tools" )
    private org.apache.commons.cli.Option getTransformerLocationOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "TransformerLocationOption" );
        assert _d != null : "'TransformerLocationOption' dependency not found.";
        return _d;
    }
    // </editor-fold>
    // SECTION-END
    // SECTION-START[Properties]
    // <editor-fold defaultstate="collapsed" desc=" Generated Properties ">

    /**
     * Gets the value of the {@code abbreviatedCommandName} property.
     * @return Abbreviated name of the command.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor", comments = "See http://jomc.sourceforge.net/jomc/1.0-beta-4-SNAPSHOT/jomc-tools" )
    private java.lang.String getAbbreviatedCommandName()
    {
        final java.lang.String _p = (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getProperty( this, "abbreviatedCommandName" );
        assert _p != null : "'abbreviatedCommandName' property not found.";
        return _p;
    }

    /**
     * Gets the value of the {@code commandName} property.
     * @return Name of the command.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor", comments = "See http://jomc.sourceforge.net/jomc/1.0-beta-4-SNAPSHOT/jomc-tools" )
    private java.lang.String getCommandName()
    {
        final java.lang.String _p = (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getProperty( this, "commandName" );
        assert _p != null : "'commandName' property not found.";
        return _p;
    }

    /**
     * Gets the value of the {@code providerExcludes} property.
     * @return List of providers to exclude from any {@code META-INF/services} file separated by {@code :}.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor", comments = "See http://jomc.sourceforge.net/jomc/1.0-beta-4-SNAPSHOT/jomc-tools" )
    private java.lang.String getProviderExcludes()
    {
        final java.lang.String _p = (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getProperty( this, "providerExcludes" );
        assert _p != null : "'providerExcludes' property not found.";
        return _p;
    }

    /**
     * Gets the value of the {@code schemaExcludes} property.
     * @return List of schema context-ids to exclude from any {@code META-INF/jomc-schemas.xml} file separated by {@code :}.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor", comments = "See http://jomc.sourceforge.net/jomc/1.0-beta-4-SNAPSHOT/jomc-tools" )
    private java.lang.String getSchemaExcludes()
    {
        final java.lang.String _p = (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getProperty( this, "schemaExcludes" );
        assert _p != null : "'schemaExcludes' property not found.";
        return _p;
    }

    /**
     * Gets the value of the {@code serviceExcludes} property.
     * @return List of service classes to exclude from any {@code META-INF/jomc-services.xml} file separated by {@code :}.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor", comments = "See http://jomc.sourceforge.net/jomc/1.0-beta-4-SNAPSHOT/jomc-tools" )
    private java.lang.String getServiceExcludes()
    {
        final java.lang.String _p = (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getProperty( this, "serviceExcludes" );
        assert _p != null : "'serviceExcludes' property not found.";
        return _p;
    }
    // </editor-fold>
    // SECTION-END
    // SECTION-START[Messages]
    // <editor-fold defaultstate="collapsed" desc=" Generated Messages ">

    /**
     * Gets the text of the {@code applicationTitle} message.
     * <p><b>Templates</b><br/><table>
     * <tr><td valign="top">English:</td><td valign="top"><pre>JOMC Version 1.0-beta-4-SNAPSHOT Build 2010-05-26T15:16:59+0200</pre></td></tr>
     * </table></p>
     * @param locale The locale of the message to return.
     * @return The text of the {@code applicationTitle} message.
     *
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor", comments = "See http://jomc.sourceforge.net/jomc/1.0-beta-4-SNAPSHOT/jomc-tools" )
    private String getApplicationTitle( final java.util.Locale locale )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "applicationTitle", locale );
        assert _m != null : "'applicationTitle' message not found.";
        return _m;
    }

    /**
     * Gets the text of the {@code cannotProcessMessage} message.
     * <p><b>Templates</b><br/><table>
     * <tr><td valign="top">English:</td><td valign="top"><pre>Cannot process ''{0}'': {1}</pre></td></tr>
     * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Kann ''{0}'' nicht verarbeiten: {1}</pre></td></tr>
     * </table></p>
     * @param locale The locale of the message to return.
     * @param itemInfo Format argument.
     * @param detailMessage Format argument.
     * @return The text of the {@code cannotProcessMessage} message.
     *
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor", comments = "See http://jomc.sourceforge.net/jomc/1.0-beta-4-SNAPSHOT/jomc-tools" )
    private String getCannotProcessMessage( final java.util.Locale locale, final java.lang.String itemInfo, final java.lang.String detailMessage )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "cannotProcessMessage", locale, itemInfo, detailMessage );
        assert _m != null : "'cannotProcessMessage' message not found.";
        return _m;
    }

    /**
     * Gets the text of the {@code classpathElementInfo} message.
     * <p><b>Templates</b><br/><table>
     * <tr><td valign="top">English:</td><td valign="top"><pre>Classpath element: ''{0}''</pre></td></tr>
     * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Klassenpfad-Element: ''{0}''</pre></td></tr>
     * </table></p>
     * @param locale The locale of the message to return.
     * @param classpathElement Format argument.
     * @return The text of the {@code classpathElementInfo} message.
     *
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor", comments = "See http://jomc.sourceforge.net/jomc/1.0-beta-4-SNAPSHOT/jomc-tools" )
    private String getClasspathElementInfo( final java.util.Locale locale, final java.lang.String classpathElement )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "classpathElementInfo", locale, classpathElement );
        assert _m != null : "'classpathElementInfo' message not found.";
        return _m;
    }

    /**
     * Gets the text of the {@code classpathElementNotFoundWarning} message.
     * <p><b>Templates</b><br/><table>
     * <tr><td valign="top">English:</td><td valign="top"><pre>Classpath element ''{0}'' ignored. File not found.</pre></td></tr>
     * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Klassenpfad-Element ''{0}'' ignoriert. Datei nicht gefunden.</pre></td></tr>
     * </table></p>
     * @param locale The locale of the message to return.
     * @param fileName Format argument.
     * @return The text of the {@code classpathElementNotFoundWarning} message.
     *
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor", comments = "See http://jomc.sourceforge.net/jomc/1.0-beta-4-SNAPSHOT/jomc-tools" )
    private String getClasspathElementNotFoundWarning( final java.util.Locale locale, final java.lang.String fileName )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "classpathElementNotFoundWarning", locale, fileName );
        assert _m != null : "'classpathElementNotFoundWarning' message not found.";
        return _m;
    }

    /**
     * Gets the text of the {@code defaultLogLevelInfo} message.
     * <p><b>Templates</b><br/><table>
     * <tr><td valign="top">English:</td><td valign="top"><pre>Default log level: ''{0}''</pre></td></tr>
     * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Standard-Protokollierungsstufe: ''{0}''</pre></td></tr>
     * </table></p>
     * @param locale The locale of the message to return.
     * @param defaultLogLevel Format argument.
     * @return The text of the {@code defaultLogLevelInfo} message.
     *
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor", comments = "See http://jomc.sourceforge.net/jomc/1.0-beta-4-SNAPSHOT/jomc-tools" )
    private String getDefaultLogLevelInfo( final java.util.Locale locale, final java.lang.String defaultLogLevel )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "defaultLogLevelInfo", locale, defaultLogLevel );
        assert _m != null : "'defaultLogLevelInfo' message not found.";
        return _m;
    }

    /**
     * Gets the text of the {@code documentFileInfo} message.
     * <p><b>Templates</b><br/><table>
     * <tr><td valign="top">English:</td><td valign="top"><pre>Document file: ''{0}''</pre></td></tr>
     * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Dokument-Datei: ''{0}''</pre></td></tr>
     * </table></p>
     * @param locale The locale of the message to return.
     * @param documentFile Format argument.
     * @return The text of the {@code documentFileInfo} message.
     *
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor", comments = "See http://jomc.sourceforge.net/jomc/1.0-beta-4-SNAPSHOT/jomc-tools" )
    private String getDocumentFileInfo( final java.util.Locale locale, final java.lang.String documentFile )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "documentFileInfo", locale, documentFile );
        assert _m != null : "'documentFileInfo' message not found.";
        return _m;
    }

    /**
     * Gets the text of the {@code documentFileNotFoundWarning} message.
     * <p><b>Templates</b><br/><table>
     * <tr><td valign="top">English:</td><td valign="top"><pre>Document file ''{0}'' ignored. File not found.</pre></td></tr>
     * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Dokument-Datei ''{0}'' ignoriert. Datei nicht gefunden.</pre></td></tr>
     * </table></p>
     * @param locale The locale of the message to return.
     * @param fileName Format argument.
     * @return The text of the {@code documentFileNotFoundWarning} message.
     *
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor", comments = "See http://jomc.sourceforge.net/jomc/1.0-beta-4-SNAPSHOT/jomc-tools" )
    private String getDocumentFileNotFoundWarning( final java.util.Locale locale, final java.lang.String fileName )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "documentFileNotFoundWarning", locale, fileName );
        assert _m != null : "'documentFileNotFoundWarning' message not found.";
        return _m;
    }

    /**
     * Gets the text of the {@code excludedModuleFromClasspathInfo} message.
     * <p><b>Templates</b><br/><table>
     * <tr><td valign="top">English:</td><td valign="top"><pre>Module ''{0}'' from class path ignored. Module with identical name already loaded.</pre></td></tr>
     * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Modul ''{0}'' aus Klassenpfad ignoriert. Modul mit identischem Namen bereits geladen.</pre></td></tr>
     * </table></p>
     * @param locale The locale of the message to return.
     * @param moduleName Format argument.
     * @return The text of the {@code excludedModuleFromClasspathInfo} message.
     *
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor", comments = "See http://jomc.sourceforge.net/jomc/1.0-beta-4-SNAPSHOT/jomc-tools" )
    private String getExcludedModuleFromClasspathInfo( final java.util.Locale locale, final java.lang.String moduleName )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "excludedModuleFromClasspathInfo", locale, moduleName );
        assert _m != null : "'excludedModuleFromClasspathInfo' message not found.";
        return _m;
    }

    /**
     * Gets the text of the {@code excludedProviderInfo} message.
     * <p><b>Templates</b><br/><table>
     * <tr><td valign="top">English:</td><td valign="top"><pre>Provider ''{1}'' from class path resource ''{0}'' ignored.</pre></td></tr>
     * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Provider ''{1}'' aus Klassenpfad-Ressource ''{0}'' ignoriert.</pre></td></tr>
     * </table></p>
     * @param locale The locale of the message to return.
     * @param resourceName Format argument.
     * @param providerName Format argument.
     * @return The text of the {@code excludedProviderInfo} message.
     *
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor", comments = "See http://jomc.sourceforge.net/jomc/1.0-beta-4-SNAPSHOT/jomc-tools" )
    private String getExcludedProviderInfo( final java.util.Locale locale, final java.lang.String resourceName, final java.lang.String providerName )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "excludedProviderInfo", locale, resourceName, providerName );
        assert _m != null : "'excludedProviderInfo' message not found.";
        return _m;
    }

    /**
     * Gets the text of the {@code excludedSchemaInfo} message.
     * <p><b>Templates</b><br/><table>
     * <tr><td valign="top">English:</td><td valign="top"><pre>Context ''{1}'' from class path resource ''{0}'' ignored.</pre></td></tr>
     * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Kontext ''{1}'' aus Klassenpfad-Ressource ''{0}'' ignoriert.</pre></td></tr>
     * </table></p>
     * @param locale The locale of the message to return.
     * @param resourceName Format argument.
     * @param contextId Format argument.
     * @return The text of the {@code excludedSchemaInfo} message.
     *
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor", comments = "See http://jomc.sourceforge.net/jomc/1.0-beta-4-SNAPSHOT/jomc-tools" )
    private String getExcludedSchemaInfo( final java.util.Locale locale, final java.lang.String resourceName, final java.lang.String contextId )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "excludedSchemaInfo", locale, resourceName, contextId );
        assert _m != null : "'excludedSchemaInfo' message not found.";
        return _m;
    }

    /**
     * Gets the text of the {@code excludedServiceInfo} message.
     * <p><b>Templates</b><br/><table>
     * <tr><td valign="top">English:</td><td valign="top"><pre>Service ''{1}'' from class path resource ''{0}'' ignored.</pre></td></tr>
     * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Service ''{1}'' aus Klassenpfad-Ressource ''{0}'' ignoriert.</pre></td></tr>
     * </table></p>
     * @param locale The locale of the message to return.
     * @param resourceName Format argument.
     * @param serviceName Format argument.
     * @return The text of the {@code excludedServiceInfo} message.
     *
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor", comments = "See http://jomc.sourceforge.net/jomc/1.0-beta-4-SNAPSHOT/jomc-tools" )
    private String getExcludedServiceInfo( final java.util.Locale locale, final java.lang.String resourceName, final java.lang.String serviceName )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "excludedServiceInfo", locale, resourceName, serviceName );
        assert _m != null : "'excludedServiceInfo' message not found.";
        return _m;
    }

    /**
     * Gets the text of the {@code invalidModelMessage} message.
     * <p><b>Templates</b><br/><table>
     * <tr><td valign="top">English:</td><td valign="top"><pre>Invalid model.</pre></td></tr>
     * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Ung&uuml;ltiges Modell.</pre></td></tr>
     * </table></p>
     * @param locale The locale of the message to return.
     * @return The text of the {@code invalidModelMessage} message.
     *
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor", comments = "See http://jomc.sourceforge.net/jomc/1.0-beta-4-SNAPSHOT/jomc-tools" )
    private String getInvalidModelMessage( final java.util.Locale locale )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "invalidModelMessage", locale );
        assert _m != null : "'invalidModelMessage' message not found.";
        return _m;
    }

    /**
     * Gets the text of the {@code longDescriptionMessage} message.
     * <p><b>Templates</b><br/><table>
     * <tr><td valign="top">English:</td><td valign="top"><pre>Example:
     *   jomc validate-classes -cp examples/lib/commons-cli-1.2.jar:target/classes \
     *                         -df examples/xml/jomc-cli.xml \
     *                         -v</pre></td></tr>
     * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Beispiel:
     *   jomc validate-classes -cp examples/lib/commons-cli-1.2.jar:target/classes \
     *                         -df examples/xml/jomc-cli.xml \
     *                         -v</pre></td></tr>
     * </table></p>
     * @param locale The locale of the message to return.
     * @return The text of the {@code longDescriptionMessage} message.
     *
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor", comments = "See http://jomc.sourceforge.net/jomc/1.0-beta-4-SNAPSHOT/jomc-tools" )
    private String getLongDescriptionMessage( final java.util.Locale locale )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "longDescriptionMessage", locale );
        assert _m != null : "'longDescriptionMessage' message not found.";
        return _m;
    }

    /**
     * Gets the text of the {@code missingModuleMessage} message.
     * <p><b>Templates</b><br/><table>
     * <tr><td valign="top">English:</td><td valign="top"><pre>Module ''{0}'' not found.</pre></td></tr>
     * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Modul ''{0}'' nicht gefunden.</pre></td></tr>
     * </table></p>
     * @param locale The locale of the message to return.
     * @param moduleName Format argument.
     * @return The text of the {@code missingModuleMessage} message.
     *
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor", comments = "See http://jomc.sourceforge.net/jomc/1.0-beta-4-SNAPSHOT/jomc-tools" )
    private String getMissingModuleMessage( final java.util.Locale locale, final java.lang.String moduleName )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "missingModuleMessage", locale, moduleName );
        assert _m != null : "'missingModuleMessage' message not found.";
        return _m;
    }

    /**
     * Gets the text of the {@code modulesReport} message.
     * <p><b>Templates</b><br/><table>
     * <tr><td valign="top">English:</td><td valign="top"><pre>Modules</pre></td></tr>
     * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Module</pre></td></tr>
     * </table></p>
     * @param locale The locale of the message to return.
     * @return The text of the {@code modulesReport} message.
     *
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor", comments = "See http://jomc.sourceforge.net/jomc/1.0-beta-4-SNAPSHOT/jomc-tools" )
    private String getModulesReport( final java.util.Locale locale )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "modulesReport", locale );
        assert _m != null : "'modulesReport' message not found.";
        return _m;
    }

    /**
     * Gets the text of the {@code separator} message.
     * <p><b>Templates</b><br/><table>
     * <tr><td valign="top">English:</td><td valign="top"><pre>--------------------------------------------------------------------------------</pre></td></tr>
     * </table></p>
     * @param locale The locale of the message to return.
     * @return The text of the {@code separator} message.
     *
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor", comments = "See http://jomc.sourceforge.net/jomc/1.0-beta-4-SNAPSHOT/jomc-tools" )
    private String getSeparator( final java.util.Locale locale )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "separator", locale );
        assert _m != null : "'separator' message not found.";
        return _m;
    }

    /**
     * Gets the text of the {@code shortDescriptionMessage} message.
     * <p><b>Templates</b><br/><table>
     * <tr><td valign="top">English:</td><td valign="top"><pre>Validates model objects of class files.</pre></td></tr>
     * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Pr&uuml;ft Klassendateien.</pre></td></tr>
     * </table></p>
     * @param locale The locale of the message to return.
     * @return The text of the {@code shortDescriptionMessage} message.
     *
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor", comments = "See http://jomc.sourceforge.net/jomc/1.0-beta-4-SNAPSHOT/jomc-tools" )
    private String getShortDescriptionMessage( final java.util.Locale locale )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "shortDescriptionMessage", locale );
        assert _m != null : "'shortDescriptionMessage' message not found.";
        return _m;
    }

    /**
     * Gets the text of the {@code startingModuleProcessingMessage} message.
     * <p><b>Templates</b><br/><table>
     * <tr><td valign="top">English:</td><td valign="top"><pre>Executing command {0} with module ''{1}'' ...</pre></td></tr>
     * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>F&uuml;hrt Befehl {0} mit Modul ''{1}'' aus ... </pre></td></tr>
     * </table></p>
     * @param locale The locale of the message to return.
     * @param toolName Format argument.
     * @param moduleName Format argument.
     * @return The text of the {@code startingModuleProcessingMessage} message.
     *
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor", comments = "See http://jomc.sourceforge.net/jomc/1.0-beta-4-SNAPSHOT/jomc-tools" )
    private String getStartingModuleProcessingMessage( final java.util.Locale locale, final java.lang.String toolName, final java.lang.String moduleName )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "startingModuleProcessingMessage", locale, toolName, moduleName );
        assert _m != null : "'startingModuleProcessingMessage' message not found.";
        return _m;
    }

    /**
     * Gets the text of the {@code startingProcessingMessage} message.
     * <p><b>Templates</b><br/><table>
     * <tr><td valign="top">English:</td><td valign="top"><pre>Executing command {0} ...</pre></td></tr>
     * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>F&uuml;hrt Befehl {0} aus ... </pre></td></tr>
     * </table></p>
     * @param locale The locale of the message to return.
     * @param toolName Format argument.
     * @return The text of the {@code startingProcessingMessage} message.
     *
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor", comments = "See http://jomc.sourceforge.net/jomc/1.0-beta-4-SNAPSHOT/jomc-tools" )
    private String getStartingProcessingMessage( final java.util.Locale locale, final java.lang.String toolName )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "startingProcessingMessage", locale, toolName );
        assert _m != null : "'startingProcessingMessage' message not found.";
        return _m;
    }

    /**
     * Gets the text of the {@code toolFailureMessage} message.
     * <p><b>Templates</b><br/><table>
     * <tr><td valign="top">English:</td><td valign="top"><pre>{0} failure.</pre></td></tr>
     * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>{0} fehlgeschlagen.</pre></td></tr>
     * </table></p>
     * @param locale The locale of the message to return.
     * @param toolName Format argument.
     * @return The text of the {@code toolFailureMessage} message.
     *
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor", comments = "See http://jomc.sourceforge.net/jomc/1.0-beta-4-SNAPSHOT/jomc-tools" )
    private String getToolFailureMessage( final java.util.Locale locale, final java.lang.String toolName )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "toolFailureMessage", locale, toolName );
        assert _m != null : "'toolFailureMessage' message not found.";
        return _m;
    }

    /**
     * Gets the text of the {@code toolSuccessMessage} message.
     * <p><b>Templates</b><br/><table>
     * <tr><td valign="top">English:</td><td valign="top"><pre>{0} successful.</pre></td></tr>
     * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>{0} erfolgreich.</pre></td></tr>
     * </table></p>
     * @param locale The locale of the message to return.
     * @param toolName Format argument.
     * @return The text of the {@code toolSuccessMessage} message.
     *
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor", comments = "See http://jomc.sourceforge.net/jomc/1.0-beta-4-SNAPSHOT/jomc-tools" )
    private String getToolSuccessMessage( final java.util.Locale locale, final java.lang.String toolName )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "toolSuccessMessage", locale, toolName );
        assert _m != null : "'toolSuccessMessage' message not found.";
        return _m;
    }
    // </editor-fold>
    // SECTION-END
}
