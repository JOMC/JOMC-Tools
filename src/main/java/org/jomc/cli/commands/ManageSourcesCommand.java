// SECTION-START[License Header]
// <editor-fold defaultstate="collapsed" desc=" Generated License ">
/*
 * Java Object Management and Configuration
 * Copyright (C) Christian Schulte <cs@schulte.it>, 2009-206
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   o Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   o Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in
 *     the documentation and/or other materials provided with the
 *     distribution.
 *
 * THIS SOFTWARE IS PROVIDED "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY
 * AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * $JOMC$
 *
 */
// </editor-fold>
// SECTION-END
package org.jomc.cli.commands;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.util.JAXBSource;
import javax.xml.transform.Source;
import org.apache.commons.cli.CommandLine;
import org.jomc.model.Implementation;
import org.jomc.model.Module;
import org.jomc.model.Specification;
import org.jomc.modlet.Model;
import org.jomc.modlet.ModelContext;
import org.jomc.modlet.ModelException;
import org.jomc.modlet.ModelValidationReport;
import org.jomc.modlet.ObjectFactory;
import org.jomc.tools.SourceFileProcessor;

// SECTION-START[Documentation]
// <editor-fold defaultstate="collapsed" desc=" Generated Documentation ">
/**
 * JOMC ⁑ CLI ⁑ {@code manage-sources} command implementation.
 *
 * <dl>
 *   <dt><b>Identifier:</b></dt><dd>JOMC ⁑ CLI ⁑ Default manage-sources Command</dd>
 *   <dt><b>Name:</b></dt><dd>JOMC ⁑ CLI ⁑ Default manage-sources Command</dd>
 *   <dt><b>Specifications:</b></dt>
 *     <dd>JOMC ⁑ CLI ⁑ Command @ 1.0</dd>
 *   <dt><b>Abstract:</b></dt><dd>No</dd>
 *   <dt><b>Final:</b></dt><dd>No</dd>
 *   <dt><b>Stateless:</b></dt><dd>No</dd>
 * </dl>
 *
 * @author <a href="mailto:cs@schulte.it">Christian Schulte</a> 1.0
 * @version 2.0-SNAPSHOT
 */
// </editor-fold>
// SECTION-END
// SECTION-START[Annotations]
// <editor-fold defaultstate="collapsed" desc=" Generated Annotations ">
@javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://www.jomc.org/jomc-tools/2.0-SNAPSHOT/jomc-tools-2.0-SNAPSHOT" )
// </editor-fold>
// SECTION-END
public final class ManageSourcesCommand extends AbstractSourceFileProcessorCommand
{
    // SECTION-START[Command]
    // SECTION-END
    // SECTION-START[ManageSourcesCommand]

    protected void processSourceFiles( final CommandLine commandLine ) throws CommandExecutionException
    {
        if ( commandLine == null )
        {
            throw new NullPointerException( "commandLine" );
        }

        CommandLineClassLoader classLoader = null;
        boolean suppressExceptionOnClose = true;

        try
        {
            classLoader = new CommandLineClassLoader( commandLine );
            final ModelContext context = this.createModelContext( commandLine, classLoader );
            final Model model = this.getModel( context, commandLine );
            final JAXBContext jaxbContext = context.createContext( model.getIdentifier() );
            final Marshaller marshaller = context.createMarshaller( model.getIdentifier() );
            final Source source = new JAXBSource( jaxbContext, new ObjectFactory().createModel( model ) );
            final ModelValidationReport validationReport = context.validateModel( model.getIdentifier(), source );
            this.log( validationReport, marshaller );

            if ( !validationReport.isModelValid() )
            {
                throw new CommandExecutionException( this.getInvalidModelMessage(
                    this.getLocale(), this.getModel( commandLine ) ) );

            }

            final SourceFileProcessor tool = this.createSourceFileProcessor( commandLine );
            tool.setModel( model );

            final File sourcesDirectory =
                new File( commandLine.getOptionValue( this.getSourceDirectoryOption().getOpt() ) );

            final Specification specification = this.getSpecification( commandLine, model );
            final Implementation implementation = this.getImplementation( commandLine, model );
            final Module module = this.getModule( commandLine, model );

            if ( specification != null )
            {
                tool.manageSourceFiles( specification, sourcesDirectory );
            }

            if ( implementation != null )
            {
                tool.manageSourceFiles( implementation, sourcesDirectory );
            }

            if ( module != null )
            {
                tool.manageSourceFiles( module, sourcesDirectory );
            }

            if ( this.isModulesProcessingRequested( commandLine ) )
            {
                tool.manageSourceFiles( sourcesDirectory );
            }

            suppressExceptionOnClose = false;
        }
        catch ( final JAXBException e )
        {
            String message = getExceptionMessage( e );
            if ( message == null )
            {
                message = getExceptionMessage( e.getLinkedException() );
            }

            throw new CommandExecutionException( message, e );
        }
        catch ( final ModelException e )
        {
            throw new CommandExecutionException( getExceptionMessage( e ), e );
        }
        catch ( final IOException e )
        {
            throw new CommandExecutionException( getExceptionMessage( e ), e );
        }
        finally
        {
            try
            {
                if ( classLoader != null )
                {
                    classLoader.close();
                }
            }
            catch ( final IOException e )
            {
                if ( suppressExceptionOnClose )
                {
                    this.log( Level.SEVERE, getExceptionMessage( e ), e );
                }
                else
                {
                    throw new CommandExecutionException( getExceptionMessage( e ), e );
                }
            }
        }
    }

    // SECTION-END
    // SECTION-START[Constructors]
    // <editor-fold defaultstate="collapsed" desc=" Generated Constructors ">
    /** Creates a new {@code ManageSourcesCommand} instance. */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://www.jomc.org/jomc-tools/2.0-SNAPSHOT/jomc-tools-2.0-SNAPSHOT" )
    public ManageSourcesCommand()
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
     * Gets the {@code <Classpath Option>} dependency.
     * <p>
     *   This method returns the {@code <JOMC ⁑ CLI ⁑ Classpath Option>} object of the {@code <JOMC ⁑ CLI ⁑ Command Option>} specification at specification level 1.2.
     *   That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.
     * </p>
     * <dl>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl>
     * @return The {@code <Classpath Option>} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @SuppressWarnings({"unchecked", "unused", "PMD.UnnecessaryFullyQualifiedName"})
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://www.jomc.org/jomc-tools/2.0-SNAPSHOT/jomc-tools-2.0-SNAPSHOT" )
    private org.apache.commons.cli.Option getClasspathOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "Classpath Option" );
        assert _d != null : "'Classpath Option' dependency not found.";
        return _d;
    }
    /**
     * Gets the {@code <Country Option>} dependency.
     * <p>
     *   This method returns the {@code <JOMC ⁑ CLI ⁑ Country Option>} object of the {@code <JOMC ⁑ CLI ⁑ Command Option>} specification at specification level 1.2.
     *   That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.
     * </p>
     * <dl>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl>
     * @return The {@code <Country Option>} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @SuppressWarnings({"unchecked", "unused", "PMD.UnnecessaryFullyQualifiedName"})
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://www.jomc.org/jomc-tools/2.0-SNAPSHOT/jomc-tools-2.0-SNAPSHOT" )
    private org.apache.commons.cli.Option getCountryOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "Country Option" );
        assert _d != null : "'Country Option' dependency not found.";
        return _d;
    }
    /**
     * Gets the {@code <Default Template Encoding Option>} dependency.
     * <p>
     *   This method returns the {@code <JOMC ⁑ CLI ⁑ Default Template Encoding Option>} object of the {@code <JOMC ⁑ CLI ⁑ Command Option>} specification at specification level 1.2.
     *   That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.
     * </p>
     * <dl>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl>
     * @return The {@code <Default Template Encoding Option>} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @SuppressWarnings({"unchecked", "unused", "PMD.UnnecessaryFullyQualifiedName"})
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://www.jomc.org/jomc-tools/2.0-SNAPSHOT/jomc-tools-2.0-SNAPSHOT" )
    private org.apache.commons.cli.Option getDefaultTemplateEncodingOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "Default Template Encoding Option" );
        assert _d != null : "'Default Template Encoding Option' dependency not found.";
        return _d;
    }
    /**
     * Gets the {@code <Default Template Profile Option>} dependency.
     * <p>
     *   This method returns the {@code <JOMC ⁑ CLI ⁑ Default Template Profile Option>} object of the {@code <JOMC ⁑ CLI ⁑ Command Option>} specification at specification level 1.2.
     *   That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.
     * </p>
     * <dl>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl>
     * @return The {@code <Default Template Profile Option>} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @SuppressWarnings({"unchecked", "unused", "PMD.UnnecessaryFullyQualifiedName"})
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://www.jomc.org/jomc-tools/2.0-SNAPSHOT/jomc-tools-2.0-SNAPSHOT" )
    private org.apache.commons.cli.Option getDefaultTemplateProfileOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "Default Template Profile Option" );
        assert _d != null : "'Default Template Profile Option' dependency not found.";
        return _d;
    }
    /**
     * Gets the {@code <Documents Option>} dependency.
     * <p>
     *   This method returns the {@code <JOMC ⁑ CLI ⁑ Documents Option>} object of the {@code <JOMC ⁑ CLI ⁑ Command Option>} specification at specification level 1.2.
     *   That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.
     * </p>
     * <dl>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl>
     * @return The {@code <Documents Option>} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @SuppressWarnings({"unchecked", "unused", "PMD.UnnecessaryFullyQualifiedName"})
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://www.jomc.org/jomc-tools/2.0-SNAPSHOT/jomc-tools-2.0-SNAPSHOT" )
    private org.apache.commons.cli.Option getDocumentsOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "Documents Option" );
        assert _d != null : "'Documents Option' dependency not found.";
        return _d;
    }
    /**
     * Gets the {@code <Implementation Option>} dependency.
     * <p>
     *   This method returns the {@code <JOMC ⁑ CLI ⁑ Implementation Option>} object of the {@code <JOMC ⁑ CLI ⁑ Command Option>} specification at specification level 1.2.
     *   That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.
     * </p>
     * <dl>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl>
     * @return The {@code <Implementation Option>} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @SuppressWarnings({"unchecked", "unused", "PMD.UnnecessaryFullyQualifiedName"})
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://www.jomc.org/jomc-tools/2.0-SNAPSHOT/jomc-tools-2.0-SNAPSHOT" )
    private org.apache.commons.cli.Option getImplementationOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "Implementation Option" );
        assert _d != null : "'Implementation Option' dependency not found.";
        return _d;
    }
    /**
     * Gets the {@code <Indentation String Option>} dependency.
     * <p>
     *   This method returns the {@code <JOMC ⁑ CLI ⁑ Indentation String Option>} object of the {@code <JOMC ⁑ CLI ⁑ Command Option>} specification at specification level 1.2.
     *   That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.
     * </p>
     * <dl>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl>
     * @return The {@code <Indentation String Option>} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @SuppressWarnings({"unchecked", "unused", "PMD.UnnecessaryFullyQualifiedName"})
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://www.jomc.org/jomc-tools/2.0-SNAPSHOT/jomc-tools-2.0-SNAPSHOT" )
    private org.apache.commons.cli.Option getIndentationStringOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "Indentation String Option" );
        assert _d != null : "'Indentation String Option' dependency not found.";
        return _d;
    }
    /**
     * Gets the {@code <Input Encoding Option>} dependency.
     * <p>
     *   This method returns the {@code <JOMC ⁑ CLI ⁑ Input Encoding Option>} object of the {@code <JOMC ⁑ CLI ⁑ Command Option>} specification at specification level 1.2.
     *   That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.
     * </p>
     * <dl>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl>
     * @return The {@code <Input Encoding Option>} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @SuppressWarnings({"unchecked", "unused", "PMD.UnnecessaryFullyQualifiedName"})
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://www.jomc.org/jomc-tools/2.0-SNAPSHOT/jomc-tools-2.0-SNAPSHOT" )
    private org.apache.commons.cli.Option getInputEncodingOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "Input Encoding Option" );
        assert _d != null : "'Input Encoding Option' dependency not found.";
        return _d;
    }
    /**
     * Gets the {@code <Language Option>} dependency.
     * <p>
     *   This method returns the {@code <JOMC ⁑ CLI ⁑ Language Option>} object of the {@code <JOMC ⁑ CLI ⁑ Command Option>} specification at specification level 1.2.
     *   That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.
     * </p>
     * <dl>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl>
     * @return The {@code <Language Option>} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @SuppressWarnings({"unchecked", "unused", "PMD.UnnecessaryFullyQualifiedName"})
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://www.jomc.org/jomc-tools/2.0-SNAPSHOT/jomc-tools-2.0-SNAPSHOT" )
    private org.apache.commons.cli.Option getLanguageOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "Language Option" );
        assert _d != null : "'Language Option' dependency not found.";
        return _d;
    }
    /**
     * Gets the {@code <Line Separator Option>} dependency.
     * <p>
     *   This method returns the {@code <JOMC ⁑ CLI ⁑ Line Separator Option>} object of the {@code <JOMC ⁑ CLI ⁑ Command Option>} specification at specification level 1.2.
     *   That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.
     * </p>
     * <dl>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl>
     * @return The {@code <Line Separator Option>} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @SuppressWarnings({"unchecked", "unused", "PMD.UnnecessaryFullyQualifiedName"})
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://www.jomc.org/jomc-tools/2.0-SNAPSHOT/jomc-tools-2.0-SNAPSHOT" )
    private org.apache.commons.cli.Option getLineSeparatorOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "Line Separator Option" );
        assert _d != null : "'Line Separator Option' dependency not found.";
        return _d;
    }
    /**
     * Gets the {@code <Locale>} dependency.
     * <p>
     *   This method returns the {@code <default>} object of the {@code <java.util.Locale>} specification at specification level 1.1.
     *   That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.
     * </p>
     * <dl>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl>
     * @return The {@code <Locale>} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @SuppressWarnings({"unchecked", "unused", "PMD.UnnecessaryFullyQualifiedName"})
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://www.jomc.org/jomc-tools/2.0-SNAPSHOT/jomc-tools-2.0-SNAPSHOT" )
    private java.util.Locale getLocale()
    {
        final java.util.Locale _d = (java.util.Locale) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "Locale" );
        assert _d != null : "'Locale' dependency not found.";
        return _d;
    }
    /**
     * Gets the {@code <Locale Variant Option>} dependency.
     * <p>
     *   This method returns the {@code <JOMC ⁑ CLI ⁑ Locale Variant Option>} object of the {@code <JOMC ⁑ CLI ⁑ Command Option>} specification at specification level 1.2.
     *   That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.
     * </p>
     * <dl>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl>
     * @return The {@code <Locale Variant Option>} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @SuppressWarnings({"unchecked", "unused", "PMD.UnnecessaryFullyQualifiedName"})
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://www.jomc.org/jomc-tools/2.0-SNAPSHOT/jomc-tools-2.0-SNAPSHOT" )
    private org.apache.commons.cli.Option getLocaleVariantOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "Locale Variant Option" );
        assert _d != null : "'Locale Variant Option' dependency not found.";
        return _d;
    }
    /**
     * Gets the {@code <Model Context Factory Option>} dependency.
     * <p>
     *   This method returns the {@code <JOMC ⁑ CLI ⁑ ModelContextFactory Class Name Option>} object of the {@code <JOMC ⁑ CLI ⁑ Command Option>} specification at specification level 1.2.
     *   That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.
     * </p>
     * <dl>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl>
     * @return The {@code <Model Context Factory Option>} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @SuppressWarnings({"unchecked", "unused", "PMD.UnnecessaryFullyQualifiedName"})
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://www.jomc.org/jomc-tools/2.0-SNAPSHOT/jomc-tools-2.0-SNAPSHOT" )
    private org.apache.commons.cli.Option getModelContextFactoryOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "Model Context Factory Option" );
        assert _d != null : "'Model Context Factory Option' dependency not found.";
        return _d;
    }
    /**
     * Gets the {@code <Model Option>} dependency.
     * <p>
     *   This method returns the {@code <JOMC ⁑ CLI ⁑ Model Option>} object of the {@code <JOMC ⁑ CLI ⁑ Command Option>} specification at specification level 1.2.
     *   That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.
     * </p>
     * <dl>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl>
     * @return The {@code <Model Option>} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @SuppressWarnings({"unchecked", "unused", "PMD.UnnecessaryFullyQualifiedName"})
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://www.jomc.org/jomc-tools/2.0-SNAPSHOT/jomc-tools-2.0-SNAPSHOT" )
    private org.apache.commons.cli.Option getModelOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "Model Option" );
        assert _d != null : "'Model Option' dependency not found.";
        return _d;
    }
    /**
     * Gets the {@code <Modlet Location Option>} dependency.
     * <p>
     *   This method returns the {@code <JOMC ⁑ CLI ⁑ Modlet Location Option>} object of the {@code <JOMC ⁑ CLI ⁑ Command Option>} specification at specification level 1.2.
     *   That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.
     * </p>
     * <dl>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl>
     * @return The {@code <Modlet Location Option>} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @SuppressWarnings({"unchecked", "unused", "PMD.UnnecessaryFullyQualifiedName"})
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://www.jomc.org/jomc-tools/2.0-SNAPSHOT/jomc-tools-2.0-SNAPSHOT" )
    private org.apache.commons.cli.Option getModletLocationOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "Modlet Location Option" );
        assert _d != null : "'Modlet Location Option' dependency not found.";
        return _d;
    }
    /**
     * Gets the {@code <Modlet Schema System Id Option>} dependency.
     * <p>
     *   This method returns the {@code <JOMC ⁑ CLI ⁑ Modlet Schema System Id Option>} object of the {@code <JOMC ⁑ CLI ⁑ Command Option>} specification at specification level 1.2.
     *   That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.
     * </p>
     * <dl>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl>
     * @return The {@code <Modlet Schema System Id Option>} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @SuppressWarnings({"unchecked", "unused", "PMD.UnnecessaryFullyQualifiedName"})
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://www.jomc.org/jomc-tools/2.0-SNAPSHOT/jomc-tools-2.0-SNAPSHOT" )
    private org.apache.commons.cli.Option getModletSchemaSystemIdOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "Modlet Schema System Id Option" );
        assert _d != null : "'Modlet Schema System Id Option' dependency not found.";
        return _d;
    }
    /**
     * Gets the {@code <Module Location Option>} dependency.
     * <p>
     *   This method returns the {@code <JOMC ⁑ CLI ⁑ Module Location Option>} object of the {@code <JOMC ⁑ CLI ⁑ Command Option>} specification at specification level 1.2.
     *   That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.
     * </p>
     * <dl>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl>
     * @return The {@code <Module Location Option>} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @SuppressWarnings({"unchecked", "unused", "PMD.UnnecessaryFullyQualifiedName"})
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://www.jomc.org/jomc-tools/2.0-SNAPSHOT/jomc-tools-2.0-SNAPSHOT" )
    private org.apache.commons.cli.Option getModuleLocationOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "Module Location Option" );
        assert _d != null : "'Module Location Option' dependency not found.";
        return _d;
    }
    /**
     * Gets the {@code <Module Name Option>} dependency.
     * <p>
     *   This method returns the {@code <JOMC ⁑ CLI ⁑ Module Name Option>} object of the {@code <JOMC ⁑ CLI ⁑ Command Option>} specification at specification level 1.2.
     *   That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.
     * </p>
     * <dl>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl>
     * @return The {@code <Module Name Option>} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @SuppressWarnings({"unchecked", "unused", "PMD.UnnecessaryFullyQualifiedName"})
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://www.jomc.org/jomc-tools/2.0-SNAPSHOT/jomc-tools-2.0-SNAPSHOT" )
    private org.apache.commons.cli.Option getModuleNameOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "Module Name Option" );
        assert _d != null : "'Module Name Option' dependency not found.";
        return _d;
    }
    /**
     * Gets the {@code <No Classpath Resolution Option>} dependency.
     * <p>
     *   This method returns the {@code <JOMC ⁑ CLI ⁑ No Classpath Resolution Option>} object of the {@code <JOMC ⁑ CLI ⁑ Command Option>} specification at specification level 1.2.
     *   That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.
     * </p>
     * <dl>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl>
     * @return The {@code <No Classpath Resolution Option>} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @SuppressWarnings({"unchecked", "unused", "PMD.UnnecessaryFullyQualifiedName"})
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://www.jomc.org/jomc-tools/2.0-SNAPSHOT/jomc-tools-2.0-SNAPSHOT" )
    private org.apache.commons.cli.Option getNoClasspathResolutionOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "No Classpath Resolution Option" );
        assert _d != null : "'No Classpath Resolution Option' dependency not found.";
        return _d;
    }
    /**
     * Gets the {@code <No Java Validation Option>} dependency.
     * <p>
     *   This method returns the {@code <JOMC ⁑ CLI ⁑ No Java Validation Option>} object of the {@code <JOMC ⁑ CLI ⁑ Command Option>} specification at specification level 1.2.
     *   That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.
     * </p>
     * <dl>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl>
     * @return The {@code <No Java Validation Option>} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @SuppressWarnings({"unchecked", "unused", "PMD.UnnecessaryFullyQualifiedName"})
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://www.jomc.org/jomc-tools/2.0-SNAPSHOT/jomc-tools-2.0-SNAPSHOT" )
    private org.apache.commons.cli.Option getNoJavaValidationOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "No Java Validation Option" );
        assert _d != null : "'No Java Validation Option' dependency not found.";
        return _d;
    }
    /**
     * Gets the {@code <No Model Processing Option>} dependency.
     * <p>
     *   This method returns the {@code <JOMC ⁑ CLI ⁑ No Model Processing Option>} object of the {@code <JOMC ⁑ CLI ⁑ Command Option>} specification at specification level 1.2.
     *   That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.
     * </p>
     * <dl>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl>
     * @return The {@code <No Model Processing Option>} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @SuppressWarnings({"unchecked", "unused", "PMD.UnnecessaryFullyQualifiedName"})
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://www.jomc.org/jomc-tools/2.0-SNAPSHOT/jomc-tools-2.0-SNAPSHOT" )
    private org.apache.commons.cli.Option getNoModelProcessingOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "No Model Processing Option" );
        assert _d != null : "'No Model Processing Option' dependency not found.";
        return _d;
    }
    /**
     * Gets the {@code <No Model Resource Validation>} dependency.
     * <p>
     *   This method returns the {@code <JOMC ⁑ CLI ⁑ No Model Resource Validation Option>} object of the {@code <JOMC ⁑ CLI ⁑ Command Option>} specification at specification level 1.2.
     *   That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.
     * </p>
     * <dl>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl>
     * @return The {@code <No Model Resource Validation>} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @SuppressWarnings({"unchecked", "unused", "PMD.UnnecessaryFullyQualifiedName"})
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://www.jomc.org/jomc-tools/2.0-SNAPSHOT/jomc-tools-2.0-SNAPSHOT" )
    private org.apache.commons.cli.Option getNoModelResourceValidation()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "No Model Resource Validation" );
        assert _d != null : "'No Model Resource Validation' dependency not found.";
        return _d;
    }
    /**
     * Gets the {@code <No Modlet Resource Validation>} dependency.
     * <p>
     *   This method returns the {@code <JOMC ⁑ CLI ⁑ No Modlet Resource Validation Option>} object of the {@code <JOMC ⁑ CLI ⁑ Command Option>} specification at specification level 1.2.
     *   That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.
     * </p>
     * <dl>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl>
     * @return The {@code <No Modlet Resource Validation>} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @SuppressWarnings({"unchecked", "unused", "PMD.UnnecessaryFullyQualifiedName"})
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://www.jomc.org/jomc-tools/2.0-SNAPSHOT/jomc-tools-2.0-SNAPSHOT" )
    private org.apache.commons.cli.Option getNoModletResourceValidation()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "No Modlet Resource Validation" );
        assert _d != null : "'No Modlet Resource Validation' dependency not found.";
        return _d;
    }
    /**
     * Gets the {@code <No Source Processing Option>} dependency.
     * <p>
     *   This method returns the {@code <JOMC ⁑ CLI ⁑ No Source File Processing Option>} object of the {@code <JOMC ⁑ CLI ⁑ Command Option>} specification at specification level 1.2.
     *   That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.
     * </p>
     * <dl>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl>
     * @return The {@code <No Source Processing Option>} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @SuppressWarnings({"unchecked", "unused", "PMD.UnnecessaryFullyQualifiedName"})
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://www.jomc.org/jomc-tools/2.0-SNAPSHOT/jomc-tools-2.0-SNAPSHOT" )
    private org.apache.commons.cli.Option getNoSourceProcessingOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "No Source Processing Option" );
        assert _d != null : "'No Source Processing Option' dependency not found.";
        return _d;
    }
    /**
     * Gets the {@code <Output Encoding Option>} dependency.
     * <p>
     *   This method returns the {@code <JOMC ⁑ CLI ⁑ Output Encoding Option>} object of the {@code <JOMC ⁑ CLI ⁑ Command Option>} specification at specification level 1.2.
     *   That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.
     * </p>
     * <dl>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl>
     * @return The {@code <Output Encoding Option>} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @SuppressWarnings({"unchecked", "unused", "PMD.UnnecessaryFullyQualifiedName"})
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://www.jomc.org/jomc-tools/2.0-SNAPSHOT/jomc-tools-2.0-SNAPSHOT" )
    private org.apache.commons.cli.Option getOutputEncodingOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "Output Encoding Option" );
        assert _d != null : "'Output Encoding Option' dependency not found.";
        return _d;
    }
    /**
     * Gets the {@code <Platform Provider Location Option>} dependency.
     * <p>
     *   This method returns the {@code <JOMC ⁑ CLI ⁑ Platform Provider Location Option>} object of the {@code <JOMC ⁑ CLI ⁑ Command Option>} specification at specification level 1.2.
     *   That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.
     * </p>
     * <dl>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl>
     * @return The {@code <Platform Provider Location Option>} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @SuppressWarnings({"unchecked", "unused", "PMD.UnnecessaryFullyQualifiedName"})
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://www.jomc.org/jomc-tools/2.0-SNAPSHOT/jomc-tools-2.0-SNAPSHOT" )
    private org.apache.commons.cli.Option getPlatformProviderLocationOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "Platform Provider Location Option" );
        assert _d != null : "'Platform Provider Location Option' dependency not found.";
        return _d;
    }
    /**
     * Gets the {@code <Provider Location Option>} dependency.
     * <p>
     *   This method returns the {@code <JOMC ⁑ CLI ⁑ Provider Location Option>} object of the {@code <JOMC ⁑ CLI ⁑ Command Option>} specification at specification level 1.2.
     *   That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.
     * </p>
     * <dl>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl>
     * @return The {@code <Provider Location Option>} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @SuppressWarnings({"unchecked", "unused", "PMD.UnnecessaryFullyQualifiedName"})
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://www.jomc.org/jomc-tools/2.0-SNAPSHOT/jomc-tools-2.0-SNAPSHOT" )
    private org.apache.commons.cli.Option getProviderLocationOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "Provider Location Option" );
        assert _d != null : "'Provider Location Option' dependency not found.";
        return _d;
    }
    /**
     * Gets the {@code <Source Directory Option>} dependency.
     * <p>
     *   This method returns the {@code <JOMC ⁑ CLI ⁑ Source Directory Option>} object of the {@code <JOMC ⁑ CLI ⁑ Command Option>} specification at specification level 1.2.
     *   That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.
     * </p>
     * <p><strong>Properties:</strong>
     *   <table border="1" width="100%" cellpadding="3" cellspacing="0">
     *     <tr class="TableSubHeadingColor">
     *       <th align="left" scope="col" nowrap><b>Name</b></th>
     *       <th align="left" scope="col" nowrap><b>Type</b></th>
     *       <th align="left" scope="col" nowrap><b>Documentation</b></th>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>{@code <Required>}</td>
     *       <td align="left" valign="top" nowrap>{@code boolean}</td>
     *       <td align="left" valign="top"></td>
     *     </tr>
     *   </table>
     * </p>
     * <dl>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl>
     * @return The {@code <Source Directory Option>} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @SuppressWarnings({"unchecked", "unused", "PMD.UnnecessaryFullyQualifiedName"})
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://www.jomc.org/jomc-tools/2.0-SNAPSHOT/jomc-tools-2.0-SNAPSHOT" )
    private org.apache.commons.cli.Option getSourceDirectoryOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "Source Directory Option" );
        assert _d != null : "'Source Directory Option' dependency not found.";
        return _d;
    }
    /**
     * Gets the {@code <Source File Processor Option>} dependency.
     * <p>
     *   This method returns the {@code <JOMC ⁑ CLI ⁑ SourceFileProcessor Class Name Option>} object of the {@code <JOMC ⁑ CLI ⁑ Command Option>} specification at specification level 1.2.
     *   That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.
     * </p>
     * <dl>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl>
     * @return The {@code <Source File Processor Option>} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @SuppressWarnings({"unchecked", "unused", "PMD.UnnecessaryFullyQualifiedName"})
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://www.jomc.org/jomc-tools/2.0-SNAPSHOT/jomc-tools-2.0-SNAPSHOT" )
    private org.apache.commons.cli.Option getSourceFileProcessorOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "Source File Processor Option" );
        assert _d != null : "'Source File Processor Option' dependency not found.";
        return _d;
    }
    /**
     * Gets the {@code <Specification Option>} dependency.
     * <p>
     *   This method returns the {@code <JOMC ⁑ CLI ⁑ Specification Option>} object of the {@code <JOMC ⁑ CLI ⁑ Command Option>} specification at specification level 1.2.
     *   That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.
     * </p>
     * <dl>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl>
     * @return The {@code <Specification Option>} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @SuppressWarnings({"unchecked", "unused", "PMD.UnnecessaryFullyQualifiedName"})
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://www.jomc.org/jomc-tools/2.0-SNAPSHOT/jomc-tools-2.0-SNAPSHOT" )
    private org.apache.commons.cli.Option getSpecificationOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "Specification Option" );
        assert _d != null : "'Specification Option' dependency not found.";
        return _d;
    }
    /**
     * Gets the {@code <Template Location Option>} dependency.
     * <p>
     *   This method returns the {@code <JOMC ⁑ CLI ⁑ Template Location Option>} object of the {@code <JOMC ⁑ CLI ⁑ Command Option>} specification at specification level 1.2.
     *   That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.
     * </p>
     * <dl>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl>
     * @return The {@code <Template Location Option>} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @SuppressWarnings({"unchecked", "unused", "PMD.UnnecessaryFullyQualifiedName"})
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://www.jomc.org/jomc-tools/2.0-SNAPSHOT/jomc-tools-2.0-SNAPSHOT" )
    private org.apache.commons.cli.Option getTemplateLocationOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "Template Location Option" );
        assert _d != null : "'Template Location Option' dependency not found.";
        return _d;
    }
    /**
     * Gets the {@code <Template Profile Option>} dependency.
     * <p>
     *   This method returns the {@code <JOMC ⁑ CLI ⁑ Template Profile Option>} object of the {@code <JOMC ⁑ CLI ⁑ Command Option>} specification at specification level 1.2.
     *   That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.
     * </p>
     * <dl>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl>
     * @return The {@code <Template Profile Option>} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @SuppressWarnings({"unchecked", "unused", "PMD.UnnecessaryFullyQualifiedName"})
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://www.jomc.org/jomc-tools/2.0-SNAPSHOT/jomc-tools-2.0-SNAPSHOT" )
    private org.apache.commons.cli.Option getTemplateProfileOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "Template Profile Option" );
        assert _d != null : "'Template Profile Option' dependency not found.";
        return _d;
    }
    /**
     * Gets the {@code <Transformer Location Option>} dependency.
     * <p>
     *   This method returns the {@code <JOMC ⁑ CLI ⁑ Transformer Location Option>} object of the {@code <JOMC ⁑ CLI ⁑ Command Option>} specification at specification level 1.2.
     *   That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.
     * </p>
     * <dl>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl>
     * @return The {@code <Transformer Location Option>} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @SuppressWarnings({"unchecked", "unused", "PMD.UnnecessaryFullyQualifiedName"})
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://www.jomc.org/jomc-tools/2.0-SNAPSHOT/jomc-tools-2.0-SNAPSHOT" )
    private org.apache.commons.cli.Option getTransformerLocationOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "Transformer Location Option" );
        assert _d != null : "'Transformer Location Option' dependency not found.";
        return _d;
    }
    // </editor-fold>
    // SECTION-END
    // SECTION-START[Properties]
    // <editor-fold defaultstate="collapsed" desc=" Generated Properties ">
    /**
     * Gets the value of the {@code <Abbreviated Command Name>} property.
     * <p><dl>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl></p>
     * @return Abbreviated name of the command.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @SuppressWarnings({"unchecked", "unused", "PMD.UnnecessaryFullyQualifiedName"})
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://www.jomc.org/jomc-tools/2.0-SNAPSHOT/jomc-tools-2.0-SNAPSHOT" )
    private java.lang.String getAbbreviatedCommandName()
    {
        final java.lang.String _p = (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getProperty( this, "Abbreviated Command Name" );
        assert _p != null : "'Abbreviated Command Name' property not found.";
        return _p;
    }
    /**
     * Gets the value of the {@code <Application Modlet>} property.
     * <p><dl>
     *   <dt><b>Final:</b></dt><dd>Yes</dd>
     * </dl></p>
     * @return Name of the 'shaded' application modlet.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @SuppressWarnings({"unchecked", "unused", "PMD.UnnecessaryFullyQualifiedName"})
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://www.jomc.org/jomc-tools/2.0-SNAPSHOT/jomc-tools-2.0-SNAPSHOT" )
    private java.lang.String getApplicationModlet()
    {
        final java.lang.String _p = (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getProperty( this, "Application Modlet" );
        assert _p != null : "'Application Modlet' property not found.";
        return _p;
    }
    /**
     * Gets the value of the {@code <Command Name>} property.
     * <p><dl>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl></p>
     * @return Name of the command.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @SuppressWarnings({"unchecked", "unused", "PMD.UnnecessaryFullyQualifiedName"})
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://www.jomc.org/jomc-tools/2.0-SNAPSHOT/jomc-tools-2.0-SNAPSHOT" )
    private java.lang.String getCommandName()
    {
        final java.lang.String _p = (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getProperty( this, "Command Name" );
        assert _p != null : "'Command Name' property not found.";
        return _p;
    }
    /**
     * Gets the value of the {@code <Modlet Excludes>} property.
     * <p><dl>
     *   <dt><b>Final:</b></dt><dd>Yes</dd>
     * </dl></p>
     * @return List of modlet names to exclude from any {@code META-INF/jomc-modlet.xml} files separated by {@code :}.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @SuppressWarnings({"unchecked", "unused", "PMD.UnnecessaryFullyQualifiedName"})
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://www.jomc.org/jomc-tools/2.0-SNAPSHOT/jomc-tools-2.0-SNAPSHOT" )
    private java.lang.String getModletExcludes()
    {
        final java.lang.String _p = (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getProperty( this, "Modlet Excludes" );
        assert _p != null : "'Modlet Excludes' property not found.";
        return _p;
    }
    /**
     * Gets the value of the {@code <Provider Excludes>} property.
     * <p><dl>
     *   <dt><b>Final:</b></dt><dd>Yes</dd>
     * </dl></p>
     * @return List of providers to exclude from any {@code META-INF/services} files separated by {@code :}.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @SuppressWarnings({"unchecked", "unused", "PMD.UnnecessaryFullyQualifiedName"})
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://www.jomc.org/jomc-tools/2.0-SNAPSHOT/jomc-tools-2.0-SNAPSHOT" )
    private java.lang.String getProviderExcludes()
    {
        final java.lang.String _p = (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getProperty( this, "Provider Excludes" );
        assert _p != null : "'Provider Excludes' property not found.";
        return _p;
    }
    /**
     * Gets the value of the {@code <Schema Excludes>} property.
     * <p><dl>
     *   <dt><b>Final:</b></dt><dd>Yes</dd>
     * </dl></p>
     * @return List of schema context-ids to exclude from any {@code META-INF/jomc-modlet.xml} files separated by {@code :}.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @SuppressWarnings({"unchecked", "unused", "PMD.UnnecessaryFullyQualifiedName"})
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://www.jomc.org/jomc-tools/2.0-SNAPSHOT/jomc-tools-2.0-SNAPSHOT" )
    private java.lang.String getSchemaExcludes()
    {
        final java.lang.String _p = (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getProperty( this, "Schema Excludes" );
        assert _p != null : "'Schema Excludes' property not found.";
        return _p;
    }
    /**
     * Gets the value of the {@code <Service Excludes>} property.
     * <p><dl>
     *   <dt><b>Final:</b></dt><dd>Yes</dd>
     * </dl></p>
     * @return List of service classes to exclude from any {@code META-INF/jomc-modlet.xml} files separated by {@code :}.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @SuppressWarnings({"unchecked", "unused", "PMD.UnnecessaryFullyQualifiedName"})
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://www.jomc.org/jomc-tools/2.0-SNAPSHOT/jomc-tools-2.0-SNAPSHOT" )
    private java.lang.String getServiceExcludes()
    {
        final java.lang.String _p = (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getProperty( this, "Service Excludes" );
        assert _p != null : "'Service Excludes' property not found.";
        return _p;
    }
    // </editor-fold>
    // SECTION-END
    // SECTION-START[Messages]
    // <editor-fold defaultstate="collapsed" desc=" Generated Messages ">
    /**
     * Gets the text of the {@code <Application Title>} message.
     * <p><dl>
     *   <dt><b>Languages:</b></dt>
     *     <dd>English (default)</dd>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl></p>
     * @param locale The locale of the message to return.
     * @return The text of the {@code <Application Title>} message for {@code locale}.
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @SuppressWarnings({"unchecked", "unused", "PMD.UnnecessaryFullyQualifiedName"})
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://www.jomc.org/jomc-tools/2.0-SNAPSHOT/jomc-tools-2.0-SNAPSHOT" )
    private String getApplicationTitle( final java.util.Locale locale )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "Application Title", locale );
        assert _m != null : "'Application Title' message not found.";
        return _m;
    }
    /**
     * Gets the text of the {@code <Cannot Process Message>} message.
     * <p><dl>
     *   <dt><b>Languages:</b></dt>
     *     <dd>English (default)</dd>
     *     <dd>Deutsch</dd>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl></p>
     * @param locale The locale of the message to return.
     * @param itemInfo Format argument.
     * @param detailMessage Format argument.
     * @return The text of the {@code <Cannot Process Message>} message for {@code locale}.
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @SuppressWarnings({"unchecked", "unused", "PMD.UnnecessaryFullyQualifiedName"})
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://www.jomc.org/jomc-tools/2.0-SNAPSHOT/jomc-tools-2.0-SNAPSHOT" )
    private String getCannotProcessMessage( final java.util.Locale locale, final java.lang.String itemInfo, final java.lang.String detailMessage )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "Cannot Process Message", locale, itemInfo, detailMessage );
        assert _m != null : "'Cannot Process Message' message not found.";
        return _m;
    }
    /**
     * Gets the text of the {@code <Classpath Element Info>} message.
     * <p><dl>
     *   <dt><b>Languages:</b></dt>
     *     <dd>English (default)</dd>
     *     <dd>Deutsch</dd>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl></p>
     * @param locale The locale of the message to return.
     * @param classpathElement Format argument.
     * @return The text of the {@code <Classpath Element Info>} message for {@code locale}.
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @SuppressWarnings({"unchecked", "unused", "PMD.UnnecessaryFullyQualifiedName"})
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://www.jomc.org/jomc-tools/2.0-SNAPSHOT/jomc-tools-2.0-SNAPSHOT" )
    private String getClasspathElementInfo( final java.util.Locale locale, final java.lang.String classpathElement )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "Classpath Element Info", locale, classpathElement );
        assert _m != null : "'Classpath Element Info' message not found.";
        return _m;
    }
    /**
     * Gets the text of the {@code <Classpath Element Not Found Warning>} message.
     * <p><dl>
     *   <dt><b>Languages:</b></dt>
     *     <dd>English (default)</dd>
     *     <dd>Deutsch</dd>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl></p>
     * @param locale The locale of the message to return.
     * @param fileName Format argument.
     * @return The text of the {@code <Classpath Element Not Found Warning>} message for {@code locale}.
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @SuppressWarnings({"unchecked", "unused", "PMD.UnnecessaryFullyQualifiedName"})
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://www.jomc.org/jomc-tools/2.0-SNAPSHOT/jomc-tools-2.0-SNAPSHOT" )
    private String getClasspathElementNotFoundWarning( final java.util.Locale locale, final java.lang.String fileName )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "Classpath Element Not Found Warning", locale, fileName );
        assert _m != null : "'Classpath Element Not Found Warning' message not found.";
        return _m;
    }
    /**
     * Gets the text of the {@code <Command Failure Message>} message.
     * <p><dl>
     *   <dt><b>Languages:</b></dt>
     *     <dd>English (default)</dd>
     *     <dd>Deutsch</dd>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl></p>
     * @param locale The locale of the message to return.
     * @param toolName Format argument.
     * @return The text of the {@code <Command Failure Message>} message for {@code locale}.
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @SuppressWarnings({"unchecked", "unused", "PMD.UnnecessaryFullyQualifiedName"})
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://www.jomc.org/jomc-tools/2.0-SNAPSHOT/jomc-tools-2.0-SNAPSHOT" )
    private String getCommandFailureMessage( final java.util.Locale locale, final java.lang.String toolName )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "Command Failure Message", locale, toolName );
        assert _m != null : "'Command Failure Message' message not found.";
        return _m;
    }
    /**
     * Gets the text of the {@code <Command Info Message>} message.
     * <p><dl>
     *   <dt><b>Languages:</b></dt>
     *     <dd>English (default)</dd>
     *     <dd>Deutsch</dd>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl></p>
     * @param locale The locale of the message to return.
     * @param toolName Format argument.
     * @return The text of the {@code <Command Info Message>} message for {@code locale}.
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @SuppressWarnings({"unchecked", "unused", "PMD.UnnecessaryFullyQualifiedName"})
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://www.jomc.org/jomc-tools/2.0-SNAPSHOT/jomc-tools-2.0-SNAPSHOT" )
    private String getCommandInfoMessage( final java.util.Locale locale, final java.lang.String toolName )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "Command Info Message", locale, toolName );
        assert _m != null : "'Command Info Message' message not found.";
        return _m;
    }
    /**
     * Gets the text of the {@code <Command Success Message>} message.
     * <p><dl>
     *   <dt><b>Languages:</b></dt>
     *     <dd>English (default)</dd>
     *     <dd>Deutsch</dd>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl></p>
     * @param locale The locale of the message to return.
     * @param toolName Format argument.
     * @return The text of the {@code <Command Success Message>} message for {@code locale}.
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @SuppressWarnings({"unchecked", "unused", "PMD.UnnecessaryFullyQualifiedName"})
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://www.jomc.org/jomc-tools/2.0-SNAPSHOT/jomc-tools-2.0-SNAPSHOT" )
    private String getCommandSuccessMessage( final java.util.Locale locale, final java.lang.String toolName )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "Command Success Message", locale, toolName );
        assert _m != null : "'Command Success Message' message not found.";
        return _m;
    }
    /**
     * Gets the text of the {@code <Default Log Level Info>} message.
     * <p><dl>
     *   <dt><b>Languages:</b></dt>
     *     <dd>English (default)</dd>
     *     <dd>Deutsch</dd>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl></p>
     * @param locale The locale of the message to return.
     * @param defaultLogLevel Format argument.
     * @return The text of the {@code <Default Log Level Info>} message for {@code locale}.
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @SuppressWarnings({"unchecked", "unused", "PMD.UnnecessaryFullyQualifiedName"})
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://www.jomc.org/jomc-tools/2.0-SNAPSHOT/jomc-tools-2.0-SNAPSHOT" )
    private String getDefaultLogLevelInfo( final java.util.Locale locale, final java.lang.String defaultLogLevel )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "Default Log Level Info", locale, defaultLogLevel );
        assert _m != null : "'Default Log Level Info' message not found.";
        return _m;
    }
    /**
     * Gets the text of the {@code <Disabled Message>} message.
     * <p><dl>
     *   <dt><b>Languages:</b></dt>
     *     <dd>English (default)</dd>
     *     <dd>Deutsch</dd>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl></p>
     * @param locale The locale of the message to return.
     * @return The text of the {@code <Disabled Message>} message for {@code locale}.
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @SuppressWarnings({"unchecked", "unused", "PMD.UnnecessaryFullyQualifiedName"})
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://www.jomc.org/jomc-tools/2.0-SNAPSHOT/jomc-tools-2.0-SNAPSHOT" )
    private String getDisabledMessage( final java.util.Locale locale )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "Disabled Message", locale );
        assert _m != null : "'Disabled Message' message not found.";
        return _m;
    }
    /**
     * Gets the text of the {@code <Document File Info>} message.
     * <p><dl>
     *   <dt><b>Languages:</b></dt>
     *     <dd>English (default)</dd>
     *     <dd>Deutsch</dd>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl></p>
     * @param locale The locale of the message to return.
     * @param documentFile Format argument.
     * @return The text of the {@code <Document File Info>} message for {@code locale}.
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @SuppressWarnings({"unchecked", "unused", "PMD.UnnecessaryFullyQualifiedName"})
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://www.jomc.org/jomc-tools/2.0-SNAPSHOT/jomc-tools-2.0-SNAPSHOT" )
    private String getDocumentFileInfo( final java.util.Locale locale, final java.lang.String documentFile )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "Document File Info", locale, documentFile );
        assert _m != null : "'Document File Info' message not found.";
        return _m;
    }
    /**
     * Gets the text of the {@code <Document File Not Found Warning>} message.
     * <p><dl>
     *   <dt><b>Languages:</b></dt>
     *     <dd>English (default)</dd>
     *     <dd>Deutsch</dd>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl></p>
     * @param locale The locale of the message to return.
     * @param fileName Format argument.
     * @return The text of the {@code <Document File Not Found Warning>} message for {@code locale}.
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @SuppressWarnings({"unchecked", "unused", "PMD.UnnecessaryFullyQualifiedName"})
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://www.jomc.org/jomc-tools/2.0-SNAPSHOT/jomc-tools-2.0-SNAPSHOT" )
    private String getDocumentFileNotFoundWarning( final java.util.Locale locale, final java.lang.String fileName )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "Document File Not Found Warning", locale, fileName );
        assert _m != null : "'Document File Not Found Warning' message not found.";
        return _m;
    }
    /**
     * Gets the text of the {@code <Excluded Modlet Info>} message.
     * <p><dl>
     *   <dt><b>Languages:</b></dt>
     *     <dd>English (default)</dd>
     *     <dd>Deutsch</dd>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl></p>
     * @param locale The locale of the message to return.
     * @param resourceName Format argument.
     * @param modletIdentifier Format argument.
     * @return The text of the {@code <Excluded Modlet Info>} message for {@code locale}.
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @SuppressWarnings({"unchecked", "unused", "PMD.UnnecessaryFullyQualifiedName"})
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://www.jomc.org/jomc-tools/2.0-SNAPSHOT/jomc-tools-2.0-SNAPSHOT" )
    private String getExcludedModletInfo( final java.util.Locale locale, final java.lang.String resourceName, final java.lang.String modletIdentifier )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "Excluded Modlet Info", locale, resourceName, modletIdentifier );
        assert _m != null : "'Excluded Modlet Info' message not found.";
        return _m;
    }
    /**
     * Gets the text of the {@code <Excluded Provider Info>} message.
     * <p><dl>
     *   <dt><b>Languages:</b></dt>
     *     <dd>English (default)</dd>
     *     <dd>Deutsch</dd>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl></p>
     * @param locale The locale of the message to return.
     * @param resourceName Format argument.
     * @param providerName Format argument.
     * @return The text of the {@code <Excluded Provider Info>} message for {@code locale}.
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @SuppressWarnings({"unchecked", "unused", "PMD.UnnecessaryFullyQualifiedName"})
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://www.jomc.org/jomc-tools/2.0-SNAPSHOT/jomc-tools-2.0-SNAPSHOT" )
    private String getExcludedProviderInfo( final java.util.Locale locale, final java.lang.String resourceName, final java.lang.String providerName )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "Excluded Provider Info", locale, resourceName, providerName );
        assert _m != null : "'Excluded Provider Info' message not found.";
        return _m;
    }
    /**
     * Gets the text of the {@code <Excluded Schema Info>} message.
     * <p><dl>
     *   <dt><b>Languages:</b></dt>
     *     <dd>English (default)</dd>
     *     <dd>Deutsch</dd>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl></p>
     * @param locale The locale of the message to return.
     * @param resourceName Format argument.
     * @param contextId Format argument.
     * @return The text of the {@code <Excluded Schema Info>} message for {@code locale}.
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @SuppressWarnings({"unchecked", "unused", "PMD.UnnecessaryFullyQualifiedName"})
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://www.jomc.org/jomc-tools/2.0-SNAPSHOT/jomc-tools-2.0-SNAPSHOT" )
    private String getExcludedSchemaInfo( final java.util.Locale locale, final java.lang.String resourceName, final java.lang.String contextId )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "Excluded Schema Info", locale, resourceName, contextId );
        assert _m != null : "'Excluded Schema Info' message not found.";
        return _m;
    }
    /**
     * Gets the text of the {@code <Excluded Service Info>} message.
     * <p><dl>
     *   <dt><b>Languages:</b></dt>
     *     <dd>English (default)</dd>
     *     <dd>Deutsch</dd>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl></p>
     * @param locale The locale of the message to return.
     * @param resourceName Format argument.
     * @param serviceName Format argument.
     * @return The text of the {@code <Excluded Service Info>} message for {@code locale}.
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @SuppressWarnings({"unchecked", "unused", "PMD.UnnecessaryFullyQualifiedName"})
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://www.jomc.org/jomc-tools/2.0-SNAPSHOT/jomc-tools-2.0-SNAPSHOT" )
    private String getExcludedServiceInfo( final java.util.Locale locale, final java.lang.String resourceName, final java.lang.String serviceName )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "Excluded Service Info", locale, resourceName, serviceName );
        assert _m != null : "'Excluded Service Info' message not found.";
        return _m;
    }
    /**
     * Gets the text of the {@code <Failed Creating Object Message>} message.
     * <p><dl>
     *   <dt><b>Languages:</b></dt>
     *     <dd>English (default)</dd>
     *     <dd>Deutsch</dd>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl></p>
     * @param locale The locale of the message to return.
     * @param objectInfo Format argument.
     * @return The text of the {@code <Failed Creating Object Message>} message for {@code locale}.
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @SuppressWarnings({"unchecked", "unused", "PMD.UnnecessaryFullyQualifiedName"})
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://www.jomc.org/jomc-tools/2.0-SNAPSHOT/jomc-tools-2.0-SNAPSHOT" )
    private String getFailedCreatingObjectMessage( final java.util.Locale locale, final java.lang.String objectInfo )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "Failed Creating Object Message", locale, objectInfo );
        assert _m != null : "'Failed Creating Object Message' message not found.";
        return _m;
    }
    /**
     * Gets the text of the {@code <Implementation Not Found Warning>} message.
     * <p><dl>
     *   <dt><b>Languages:</b></dt>
     *     <dd>English (default)</dd>
     *     <dd>Deutsch</dd>
     *   <dt><b>Final:</b></dt><dd>Yes</dd>
     * </dl></p>
     * @param locale The locale of the message to return.
     * @param implementationIdentifier Format argument.
     * @return The text of the {@code <Implementation Not Found Warning>} message for {@code locale}.
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @SuppressWarnings({"unchecked", "unused", "PMD.UnnecessaryFullyQualifiedName"})
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://www.jomc.org/jomc-tools/2.0-SNAPSHOT/jomc-tools-2.0-SNAPSHOT" )
    private String getImplementationNotFoundWarning( final java.util.Locale locale, final java.lang.String implementationIdentifier )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "Implementation Not Found Warning", locale, implementationIdentifier );
        assert _m != null : "'Implementation Not Found Warning' message not found.";
        return _m;
    }
    /**
     * Gets the text of the {@code <Invalid Model Message>} message.
     * <p><dl>
     *   <dt><b>Languages:</b></dt>
     *     <dd>English (default)</dd>
     *     <dd>Deutsch</dd>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl></p>
     * @param locale The locale of the message to return.
     * @param modelIdentifier Format argument.
     * @return The text of the {@code <Invalid Model Message>} message for {@code locale}.
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @SuppressWarnings({"unchecked", "unused", "PMD.UnnecessaryFullyQualifiedName"})
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://www.jomc.org/jomc-tools/2.0-SNAPSHOT/jomc-tools-2.0-SNAPSHOT" )
    private String getInvalidModelMessage( final java.util.Locale locale, final java.lang.String modelIdentifier )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "Invalid Model Message", locale, modelIdentifier );
        assert _m != null : "'Invalid Model Message' message not found.";
        return _m;
    }
    /**
     * Gets the text of the {@code <Long Description Message>} message.
     * <p><dl>
     *   <dt><b>Languages:</b></dt>
     *     <dd>English (default)</dd>
     *     <dd>Deutsch</dd>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl></p>
     * @param locale The locale of the message to return.
     * @return The text of the {@code <Long Description Message>} message for {@code locale}.
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @SuppressWarnings({"unchecked", "unused", "PMD.UnnecessaryFullyQualifiedName"})
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://www.jomc.org/jomc-tools/2.0-SNAPSHOT/jomc-tools-2.0-SNAPSHOT" )
    private String getLongDescriptionMessage( final java.util.Locale locale )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "Long Description Message", locale );
        assert _m != null : "'Long Description Message' message not found.";
        return _m;
    }
    /**
     * Gets the text of the {@code <Module Not Found Warning>} message.
     * <p><dl>
     *   <dt><b>Languages:</b></dt>
     *     <dd>English (default)</dd>
     *     <dd>Deutsch</dd>
     *   <dt><b>Final:</b></dt><dd>Yes</dd>
     * </dl></p>
     * @param locale The locale of the message to return.
     * @param moduleName Format argument.
     * @return The text of the {@code <Module Not Found Warning>} message for {@code locale}.
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @SuppressWarnings({"unchecked", "unused", "PMD.UnnecessaryFullyQualifiedName"})
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://www.jomc.org/jomc-tools/2.0-SNAPSHOT/jomc-tools-2.0-SNAPSHOT" )
    private String getModuleNotFoundWarning( final java.util.Locale locale, final java.lang.String moduleName )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "Module Not Found Warning", locale, moduleName );
        assert _m != null : "'Module Not Found Warning' message not found.";
        return _m;
    }
    /**
     * Gets the text of the {@code <Reading Message>} message.
     * <p><dl>
     *   <dt><b>Languages:</b></dt>
     *     <dd>English (default)</dd>
     *     <dd>Deutsch</dd>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl></p>
     * @param locale The locale of the message to return.
     * @param locationInfo Format argument.
     * @return The text of the {@code <Reading Message>} message for {@code locale}.
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @SuppressWarnings({"unchecked", "unused", "PMD.UnnecessaryFullyQualifiedName"})
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://www.jomc.org/jomc-tools/2.0-SNAPSHOT/jomc-tools-2.0-SNAPSHOT" )
    private String getReadingMessage( final java.util.Locale locale, final java.lang.String locationInfo )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "Reading Message", locale, locationInfo );
        assert _m != null : "'Reading Message' message not found.";
        return _m;
    }
    /**
     * Gets the text of the {@code <Separator>} message.
     * <p><dl>
     *   <dt><b>Languages:</b></dt>
     *     <dd>English (default)</dd>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl></p>
     * @param locale The locale of the message to return.
     * @return The text of the {@code <Separator>} message for {@code locale}.
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @SuppressWarnings({"unchecked", "unused", "PMD.UnnecessaryFullyQualifiedName"})
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://www.jomc.org/jomc-tools/2.0-SNAPSHOT/jomc-tools-2.0-SNAPSHOT" )
    private String getSeparator( final java.util.Locale locale )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "Separator", locale );
        assert _m != null : "'Separator' message not found.";
        return _m;
    }
    /**
     * Gets the text of the {@code <Short Description Message>} message.
     * <p><dl>
     *   <dt><b>Languages:</b></dt>
     *     <dd>English (default)</dd>
     *     <dd>Deutsch</dd>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl></p>
     * @param locale The locale of the message to return.
     * @return The text of the {@code <Short Description Message>} message for {@code locale}.
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @SuppressWarnings({"unchecked", "unused", "PMD.UnnecessaryFullyQualifiedName"})
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://www.jomc.org/jomc-tools/2.0-SNAPSHOT/jomc-tools-2.0-SNAPSHOT" )
    private String getShortDescriptionMessage( final java.util.Locale locale )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "Short Description Message", locale );
        assert _m != null : "'Short Description Message' message not found.";
        return _m;
    }
    /**
     * Gets the text of the {@code <Specification Not Found Warning>} message.
     * <p><dl>
     *   <dt><b>Languages:</b></dt>
     *     <dd>English (default)</dd>
     *     <dd>Deutsch</dd>
     *   <dt><b>Final:</b></dt><dd>Yes</dd>
     * </dl></p>
     * @param locale The locale of the message to return.
     * @param specificationIdentifier Format argument.
     * @return The text of the {@code <Specification Not Found Warning>} message for {@code locale}.
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @SuppressWarnings({"unchecked", "unused", "PMD.UnnecessaryFullyQualifiedName"})
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://www.jomc.org/jomc-tools/2.0-SNAPSHOT/jomc-tools-2.0-SNAPSHOT" )
    private String getSpecificationNotFoundWarning( final java.util.Locale locale, final java.lang.String specificationIdentifier )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "Specification Not Found Warning", locale, specificationIdentifier );
        assert _m != null : "'Specification Not Found Warning' message not found.";
        return _m;
    }
    // </editor-fold>
    // SECTION-END
    // SECTION-START[Generated Command]
    // <editor-fold defaultstate="collapsed" desc=" Generated Options ">
    /**
     * Gets the options of the command.
     * <p><strong>Options:</strong>
     *   <table border="1" width="100%" cellpadding="3" cellspacing="0">
     *     <tr class="TableSubHeadingColor">
     *       <th align="left" scope="col" nowrap><b>Specification</b></th>
     *       <th align="left" scope="col" nowrap><b>Implementation</b></th>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>JOMC ⁑ CLI ⁑ Command Option {@code (org.apache.commons.cli.Option)} @ 1.2</td>
     *       <td align="left" valign="top" nowrap>JOMC ⁑ CLI ⁑ Classpath Option</td>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>JOMC ⁑ CLI ⁑ Command Option {@code (org.apache.commons.cli.Option)} @ 1.2</td>
     *       <td align="left" valign="top" nowrap>JOMC ⁑ CLI ⁑ Country Option</td>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>JOMC ⁑ CLI ⁑ Command Option {@code (org.apache.commons.cli.Option)} @ 1.2</td>
     *       <td align="left" valign="top" nowrap>JOMC ⁑ CLI ⁑ Default Template Encoding Option</td>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>JOMC ⁑ CLI ⁑ Command Option {@code (org.apache.commons.cli.Option)} @ 1.2</td>
     *       <td align="left" valign="top" nowrap>JOMC ⁑ CLI ⁑ Default Template Profile Option</td>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>JOMC ⁑ CLI ⁑ Command Option {@code (org.apache.commons.cli.Option)} @ 1.2</td>
     *       <td align="left" valign="top" nowrap>JOMC ⁑ CLI ⁑ Documents Option</td>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>JOMC ⁑ CLI ⁑ Command Option {@code (org.apache.commons.cli.Option)} @ 1.2</td>
     *       <td align="left" valign="top" nowrap>JOMC ⁑ CLI ⁑ Implementation Option</td>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>JOMC ⁑ CLI ⁑ Command Option {@code (org.apache.commons.cli.Option)} @ 1.2</td>
     *       <td align="left" valign="top" nowrap>JOMC ⁑ CLI ⁑ Indentation String Option</td>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>JOMC ⁑ CLI ⁑ Command Option {@code (org.apache.commons.cli.Option)} @ 1.2</td>
     *       <td align="left" valign="top" nowrap>JOMC ⁑ CLI ⁑ Input Encoding Option</td>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>JOMC ⁑ CLI ⁑ Command Option {@code (org.apache.commons.cli.Option)} @ 1.2</td>
     *       <td align="left" valign="top" nowrap>JOMC ⁑ CLI ⁑ Language Option</td>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>JOMC ⁑ CLI ⁑ Command Option {@code (org.apache.commons.cli.Option)} @ 1.2</td>
     *       <td align="left" valign="top" nowrap>JOMC ⁑ CLI ⁑ Line Separator Option</td>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>JOMC ⁑ CLI ⁑ Command Option {@code (org.apache.commons.cli.Option)} @ 1.2</td>
     *       <td align="left" valign="top" nowrap>JOMC ⁑ CLI ⁑ Locale Variant Option</td>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>JOMC ⁑ CLI ⁑ Command Option {@code (org.apache.commons.cli.Option)} @ 1.2</td>
     *       <td align="left" valign="top" nowrap>JOMC ⁑ CLI ⁑ ModelContextFactory Class Name Option</td>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>JOMC ⁑ CLI ⁑ Command Option {@code (org.apache.commons.cli.Option)} @ 1.2</td>
     *       <td align="left" valign="top" nowrap>JOMC ⁑ CLI ⁑ Model Option</td>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>JOMC ⁑ CLI ⁑ Command Option {@code (org.apache.commons.cli.Option)} @ 1.2</td>
     *       <td align="left" valign="top" nowrap>JOMC ⁑ CLI ⁑ Modlet Location Option</td>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>JOMC ⁑ CLI ⁑ Command Option {@code (org.apache.commons.cli.Option)} @ 1.2</td>
     *       <td align="left" valign="top" nowrap>JOMC ⁑ CLI ⁑ Modlet Schema System Id Option</td>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>JOMC ⁑ CLI ⁑ Command Option {@code (org.apache.commons.cli.Option)} @ 1.2</td>
     *       <td align="left" valign="top" nowrap>JOMC ⁑ CLI ⁑ Module Location Option</td>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>JOMC ⁑ CLI ⁑ Command Option {@code (org.apache.commons.cli.Option)} @ 1.2</td>
     *       <td align="left" valign="top" nowrap>JOMC ⁑ CLI ⁑ Module Name Option</td>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>JOMC ⁑ CLI ⁑ Command Option {@code (org.apache.commons.cli.Option)} @ 1.2</td>
     *       <td align="left" valign="top" nowrap>JOMC ⁑ CLI ⁑ No Classpath Resolution Option</td>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>JOMC ⁑ CLI ⁑ Command Option {@code (org.apache.commons.cli.Option)} @ 1.2</td>
     *       <td align="left" valign="top" nowrap>JOMC ⁑ CLI ⁑ No Java Validation Option</td>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>JOMC ⁑ CLI ⁑ Command Option {@code (org.apache.commons.cli.Option)} @ 1.2</td>
     *       <td align="left" valign="top" nowrap>JOMC ⁑ CLI ⁑ No Model Processing Option</td>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>JOMC ⁑ CLI ⁑ Command Option {@code (org.apache.commons.cli.Option)} @ 1.2</td>
     *       <td align="left" valign="top" nowrap>JOMC ⁑ CLI ⁑ No Model Resource Validation Option</td>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>JOMC ⁑ CLI ⁑ Command Option {@code (org.apache.commons.cli.Option)} @ 1.2</td>
     *       <td align="left" valign="top" nowrap>JOMC ⁑ CLI ⁑ No Modlet Resource Validation Option</td>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>JOMC ⁑ CLI ⁑ Command Option {@code (org.apache.commons.cli.Option)} @ 1.2</td>
     *       <td align="left" valign="top" nowrap>JOMC ⁑ CLI ⁑ No Source File Processing Option</td>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>JOMC ⁑ CLI ⁑ Command Option {@code (org.apache.commons.cli.Option)} @ 1.2</td>
     *       <td align="left" valign="top" nowrap>JOMC ⁑ CLI ⁑ Output Encoding Option</td>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>JOMC ⁑ CLI ⁑ Command Option {@code (org.apache.commons.cli.Option)} @ 1.2</td>
     *       <td align="left" valign="top" nowrap>JOMC ⁑ CLI ⁑ Platform Provider Location Option</td>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>JOMC ⁑ CLI ⁑ Command Option {@code (org.apache.commons.cli.Option)} @ 1.2</td>
     *       <td align="left" valign="top" nowrap>JOMC ⁑ CLI ⁑ Provider Location Option</td>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>JOMC ⁑ CLI ⁑ Command Option {@code (org.apache.commons.cli.Option)} @ 1.2</td>
     *       <td align="left" valign="top" nowrap>JOMC ⁑ CLI ⁑ Source Directory Option</td>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>JOMC ⁑ CLI ⁑ Command Option {@code (org.apache.commons.cli.Option)} @ 1.2</td>
     *       <td align="left" valign="top" nowrap>JOMC ⁑ CLI ⁑ SourceFileProcessor Class Name Option</td>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>JOMC ⁑ CLI ⁑ Command Option {@code (org.apache.commons.cli.Option)} @ 1.2</td>
     *       <td align="left" valign="top" nowrap>JOMC ⁑ CLI ⁑ Specification Option</td>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>JOMC ⁑ CLI ⁑ Command Option {@code (org.apache.commons.cli.Option)} @ 1.2</td>
     *       <td align="left" valign="top" nowrap>JOMC ⁑ CLI ⁑ Template Location Option</td>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>JOMC ⁑ CLI ⁑ Command Option {@code (org.apache.commons.cli.Option)} @ 1.2</td>
     *       <td align="left" valign="top" nowrap>JOMC ⁑ CLI ⁑ Template Profile Option</td>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>JOMC ⁑ CLI ⁑ Command Option {@code (org.apache.commons.cli.Option)} @ 1.2</td>
     *       <td align="left" valign="top" nowrap>JOMC ⁑ CLI ⁑ Transformer Location Option</td>
     *     </tr>
     *   </table>
     * </p>
     * @return The options of the command.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://www.jomc.org/jomc-tools/2.0-SNAPSHOT/jomc-tools-2.0-SNAPSHOT" )
    @Override
    public org.apache.commons.cli.Options getOptions()
    {
        final org.apache.commons.cli.Options options = new org.apache.commons.cli.Options();
        options.addOption( this.getClasspathOption() );
        options.addOption( this.getCountryOption() );
        options.addOption( this.getDefaultTemplateEncodingOption() );
        options.addOption( this.getDefaultTemplateProfileOption() );
        options.addOption( this.getDocumentsOption() );
        options.addOption( this.getImplementationOption() );
        options.addOption( this.getIndentationStringOption() );
        options.addOption( this.getInputEncodingOption() );
        options.addOption( this.getLanguageOption() );
        options.addOption( this.getLineSeparatorOption() );
        options.addOption( this.getLocaleVariantOption() );
        options.addOption( this.getModelContextFactoryOption() );
        options.addOption( this.getModelOption() );
        options.addOption( this.getModletLocationOption() );
        options.addOption( this.getModletSchemaSystemIdOption() );
        options.addOption( this.getModuleLocationOption() );
        options.addOption( this.getModuleNameOption() );
        options.addOption( this.getNoClasspathResolutionOption() );
        options.addOption( this.getNoJavaValidationOption() );
        options.addOption( this.getNoModelProcessingOption() );
        options.addOption( this.getNoModelResourceValidation() );
        options.addOption( this.getNoModletResourceValidation() );
        options.addOption( this.getNoSourceProcessingOption() );
        options.addOption( this.getOutputEncodingOption() );
        options.addOption( this.getPlatformProviderLocationOption() );
        options.addOption( this.getProviderLocationOption() );
        options.addOption( this.getSourceDirectoryOption() );
        options.addOption( this.getSourceFileProcessorOption() );
        options.addOption( this.getSpecificationOption() );
        options.addOption( this.getTemplateLocationOption() );
        options.addOption( this.getTemplateProfileOption() );
        options.addOption( this.getTransformerLocationOption() );
        return options;
    }
    // </editor-fold>
    // SECTION-END

}
