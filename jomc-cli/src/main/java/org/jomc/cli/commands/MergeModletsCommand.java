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
import java.net.URL;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.util.JAXBResult;
import javax.xml.bind.util.JAXBSource;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamSource;
import org.apache.commons.cli.CommandLine;
import org.jomc.modlet.DefaultModletProvider;
import org.jomc.modlet.ModelContext;
import org.jomc.modlet.ModelException;
import org.jomc.modlet.ModelValidationReport;
import org.jomc.modlet.Modlet;
import org.jomc.modlet.ModletObject;
import org.jomc.modlet.Modlets;
import org.jomc.modlet.ObjectFactory;

// SECTION-START[Documentation]
// <editor-fold defaultstate="collapsed" desc=" Generated Documentation ">
/**
 * JOMC ⁑ CLI ⁑ {@code merge-modlets} command implementation.
 *
 * <dl>
 *   <dt><b>Identifier:</b></dt><dd>JOMC ⁑ CLI ⁑ Default merge-modlets Command</dd>
 *   <dt><b>Name:</b></dt><dd>JOMC ⁑ CLI ⁑ Default merge-modlets Command</dd>
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
public final class MergeModletsCommand extends AbstractModletCommand
{
    // SECTION-START[Command]
    // SECTION-END
    // SECTION-START[MergeModletsCommand]

    protected void executeCommand( final CommandLine commandLine ) throws CommandExecutionException
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
            final Modlets modlets = new Modlets();
            final ModelContext context = this.createModelContext( commandLine, classLoader );
            final Marshaller marshaller = context.createMarshaller( ModletObject.MODEL_PUBLIC_ID );
            final Unmarshaller unmarshaller = context.createUnmarshaller( ModletObject.MODEL_PUBLIC_ID );

            if ( !commandLine.hasOption( this.getNoModletResourceValidation().getOpt() ) )
            {
                unmarshaller.setSchema( context.createSchema( ModletObject.MODEL_PUBLIC_ID ) );
            }

            File stylesheetFile = null;
            if ( commandLine.hasOption( this.getStylesheetOption().getOpt() ) )
            {
                stylesheetFile = new File( commandLine.getOptionValue( this.getStylesheetOption().getOpt() ) );
            }

            String modletVersion = null;
            if ( commandLine.hasOption( this.getModletVersionOption().getOpt() ) )
            {
                modletVersion = commandLine.getOptionValue( this.getModletVersionOption().getOpt() );
            }

            String modletVendor = null;
            if ( commandLine.hasOption( this.getModletVendorOption().getOpt() ) )
            {
                modletVendor = commandLine.getOptionValue( this.getModletVendorOption().getOpt() );
            }

            if ( commandLine.hasOption( this.getDocumentsOption().getOpt() ) )
            {
                for ( final File f : this.getDocumentFiles( commandLine ) )
                {
                    if ( this.isLoggable( Level.FINEST ) )
                    {
                        this.log( Level.FINEST, this.getReadingMessage( this.getLocale(), f.getAbsolutePath() ), null );
                    }

                    Object o = unmarshaller.unmarshal( f );
                    if ( o instanceof JAXBElement<?> )
                    {
                        o = ( (JAXBElement<?>) o ).getValue();
                    }

                    if ( o instanceof Modlet )
                    {
                        modlets.getModlet().add( (Modlet) o );
                    }
                    else if ( o instanceof Modlets )
                    {
                        modlets.getModlet().addAll( ( (Modlets) o ).getModlet() );
                    }
                    else if ( this.isLoggable( Level.WARNING ) )
                    {
                        this.log( Level.WARNING, this.getCannotProcessMessage(
                                  this.getLocale(), f.getAbsolutePath(), o.toString() ), null );

                    }
                }
            }

            if ( commandLine.hasOption( this.getClasspathOption().getOpt() ) )
            {
                String[] resourceNames = null;

                if ( commandLine.hasOption( this.getResourcesOption().getOpt() ) )
                {
                    resourceNames = commandLine.getOptionValues( this.getResourcesOption().getOpt() );
                }

                if ( resourceNames == null )
                {
                    resourceNames = new String[]
                    {
                        DefaultModletProvider.getDefaultModletLocation()
                    };
                }

                for ( final String resource : resourceNames )
                {
                    for ( final Enumeration<URL> e = classLoader.getResources( resource ); e.hasMoreElements(); )
                    {
                        final URL url = e.nextElement();

                        if ( this.isLoggable( Level.FINEST ) )
                        {
                            this.log( Level.FINEST, this.getReadingMessage( this.getLocale(), url.toExternalForm() ),
                                      null );

                        }

                        Object o = unmarshaller.unmarshal( url );
                        if ( o instanceof JAXBElement<?> )
                        {
                            o = ( (JAXBElement<?>) o ).getValue();
                        }

                        if ( o instanceof Modlet )
                        {
                            modlets.getModlet().add( (Modlet) o );
                        }
                        else if ( o instanceof Modlets )
                        {
                            modlets.getModlet().addAll( ( (Modlets) o ).getModlet() );
                        }
                        else if ( this.isLoggable( Level.WARNING ) )
                        {
                            this.log( Level.WARNING, this.getCannotProcessMessage(
                                      this.getLocale(), url.toExternalForm(), o.toString() ), null );

                        }
                    }
                }
            }

            for ( final Iterator<Modlet> it = modlets.getModlet().iterator(); it.hasNext(); )
            {
                if ( this.getApplicationModlet().equals( it.next().getName() ) )
                {
                    it.remove();
                }
            }

            modlets.getModlet().addAll( classLoader.getExcludedModlets().getModlet() );

            if ( commandLine.hasOption( this.getModletIncludesOption().getOpt() ) )
            {
                final String[] values = commandLine.getOptionValues( this.getModletIncludesOption().getOpt() );

                if ( values != null )
                {
                    final List<String> includes = Arrays.asList( values );

                    for ( final Iterator<Modlet> it = modlets.getModlet().iterator(); it.hasNext(); )
                    {
                        final Modlet m = it.next();
                        if ( !includes.contains( m.getName() ) )
                        {
                            this.log( Level.INFO, this.getExcludingModletInfo( this.getLocale(), m.getName() ), null );
                            it.remove();
                        }
                        else
                        {
                            this.log( Level.INFO, this.getIncludingModletInfo( this.getLocale(), m.getName() ), null );
                        }
                    }
                }
            }

            if ( commandLine.hasOption( this.getModletExcludesOption().getOpt() ) )
            {
                final String[] values = commandLine.getOptionValues( this.getModletExcludesOption().getOpt() );

                if ( values != null )
                {
                    for ( final String exclude : values )
                    {
                        final Modlet m = modlets.getModlet( exclude );

                        if ( m != null )
                        {
                            this.log( Level.INFO, this.getExcludingModletInfo( this.getLocale(), m.getName() ), null );
                            modlets.getModlet().remove( m );
                        }
                    }
                }
            }

            final ModelValidationReport validationReport =
                context.validateModel( ModletObject.MODEL_PUBLIC_ID,
                                       new JAXBSource( marshaller, new ObjectFactory().createModlets( modlets ) ) );

            this.log( validationReport, marshaller );

            if ( !validationReport.isModelValid() )
            {
                throw new CommandExecutionException( this.getInvalidModelMessage(
                    this.getLocale(), ModletObject.MODEL_PUBLIC_ID ) );

            }

            Modlet mergedModlet = modlets.getMergedModlet(
                commandLine.getOptionValue( this.getModletNameOption().getOpt() ), this.getModel( commandLine ) );

            mergedModlet.setVersion( modletVersion );
            mergedModlet.setVendor( modletVendor );

            final File modletFile = new File( commandLine.getOptionValue( this.getDocumentOption().getOpt() ) );

            if ( stylesheetFile != null )
            {
                final Transformer transformer = this.createTransformer( new StreamSource( stylesheetFile ) );
                final JAXBSource source =
                    new JAXBSource( marshaller, new ObjectFactory().createModlet( mergedModlet ) );

                final JAXBResult result = new JAXBResult( unmarshaller );
                unmarshaller.setSchema( null );
                transformer.transform( source, result );

                if ( result.getResult() instanceof JAXBElement<?>
                         && ( (JAXBElement<?>) result.getResult() ).getValue() instanceof Modlet )
                {
                    mergedModlet = (Modlet) ( (JAXBElement<?>) result.getResult() ).getValue();
                }
                else
                {
                    throw new CommandExecutionException( this.getIllegalTransformationResultError(
                        this.getLocale(), stylesheetFile.getAbsolutePath() ) );

                }
            }

            marshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );

            if ( commandLine.hasOption( this.getDocumentEncodingOption().getOpt() ) )
            {
                marshaller.setProperty( Marshaller.JAXB_ENCODING,
                                        commandLine.getOptionValue( this.getDocumentEncodingOption().getOpt() ) );

            }

            marshaller.setSchema( context.createSchema( ModletObject.MODEL_PUBLIC_ID ) );
            marshaller.marshal( new ObjectFactory().createModlet( mergedModlet ), modletFile );

            if ( this.isLoggable( Level.INFO ) )
            {
                this.log( Level.INFO, this.getWriteInfo( this.getLocale(), modletFile.getAbsolutePath() ), null );
            }

            suppressExceptionOnClose = false;
        }
        catch ( final IOException e )
        {
            throw new CommandExecutionException( getExceptionMessage( e ), e );
        }
        catch ( final TransformerException e )
        {
            String message = getExceptionMessage( e );
            if ( message == null )
            {
                message = getExceptionMessage( e.getException() );
            }

            throw new CommandExecutionException( message, e );
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
    /** Creates a new {@code MergeModletsCommand} instance. */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://www.jomc.org/jomc-tools/2.0-SNAPSHOT/jomc-tools-2.0-SNAPSHOT" )
    public MergeModletsCommand()
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
     * Gets the {@code <Document Encoding Option>} dependency.
     * <p>
     *   This method returns the {@code <JOMC ⁑ CLI ⁑ Document Encoding Option>} object of the {@code <JOMC ⁑ CLI ⁑ Command Option>} specification at specification level 1.2.
     *   That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.
     * </p>
     * <dl>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl>
     * @return The {@code <Document Encoding Option>} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @SuppressWarnings({"unchecked", "unused", "PMD.UnnecessaryFullyQualifiedName"})
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://www.jomc.org/jomc-tools/2.0-SNAPSHOT/jomc-tools-2.0-SNAPSHOT" )
    private org.apache.commons.cli.Option getDocumentEncodingOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "Document Encoding Option" );
        assert _d != null : "'Document Encoding Option' dependency not found.";
        return _d;
    }
    /**
     * Gets the {@code <Document Option>} dependency.
     * <p>
     *   This method returns the {@code <JOMC ⁑ CLI ⁑ Document Option>} object of the {@code <JOMC ⁑ CLI ⁑ Command Option>} specification at specification level 1.2.
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
     * @return The {@code <Document Option>} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @SuppressWarnings({"unchecked", "unused", "PMD.UnnecessaryFullyQualifiedName"})
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://www.jomc.org/jomc-tools/2.0-SNAPSHOT/jomc-tools-2.0-SNAPSHOT" )
    private org.apache.commons.cli.Option getDocumentOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "Document Option" );
        assert _d != null : "'Document Option' dependency not found.";
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
     * Gets the {@code <Modlet Excludes Option>} dependency.
     * <p>
     *   This method returns the {@code <JOMC ⁑ CLI ⁑ Modlet Excludes Option>} object of the {@code <JOMC ⁑ CLI ⁑ Command Option>} specification at specification level 1.2.
     *   That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.
     * </p>
     * <dl>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl>
     * @return The {@code <Modlet Excludes Option>} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @SuppressWarnings({"unchecked", "unused", "PMD.UnnecessaryFullyQualifiedName"})
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://www.jomc.org/jomc-tools/2.0-SNAPSHOT/jomc-tools-2.0-SNAPSHOT" )
    private org.apache.commons.cli.Option getModletExcludesOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "Modlet Excludes Option" );
        assert _d != null : "'Modlet Excludes Option' dependency not found.";
        return _d;
    }
    /**
     * Gets the {@code <Modlet Includes Option>} dependency.
     * <p>
     *   This method returns the {@code <JOMC ⁑ CLI ⁑ Modlet Includes Option>} object of the {@code <JOMC ⁑ CLI ⁑ Command Option>} specification at specification level 1.2.
     *   That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.
     * </p>
     * <dl>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl>
     * @return The {@code <Modlet Includes Option>} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @SuppressWarnings({"unchecked", "unused", "PMD.UnnecessaryFullyQualifiedName"})
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://www.jomc.org/jomc-tools/2.0-SNAPSHOT/jomc-tools-2.0-SNAPSHOT" )
    private org.apache.commons.cli.Option getModletIncludesOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "Modlet Includes Option" );
        assert _d != null : "'Modlet Includes Option' dependency not found.";
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
     * Gets the {@code <Modlet Name Option>} dependency.
     * <p>
     *   This method returns the {@code <JOMC ⁑ CLI ⁑ Modlet Name Option>} object of the {@code <JOMC ⁑ CLI ⁑ Command Option>} specification at specification level 1.2.
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
     * @return The {@code <Modlet Name Option>} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @SuppressWarnings({"unchecked", "unused", "PMD.UnnecessaryFullyQualifiedName"})
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://www.jomc.org/jomc-tools/2.0-SNAPSHOT/jomc-tools-2.0-SNAPSHOT" )
    private org.apache.commons.cli.Option getModletNameOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "Modlet Name Option" );
        assert _d != null : "'Modlet Name Option' dependency not found.";
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
     * Gets the {@code <Modlet Vendor Option>} dependency.
     * <p>
     *   This method returns the {@code <JOMC ⁑ CLI ⁑ Modlet Vendor Option>} object of the {@code <JOMC ⁑ CLI ⁑ Command Option>} specification at specification level 1.2.
     *   That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.
     * </p>
     * <dl>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl>
     * @return The {@code <Modlet Vendor Option>} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @SuppressWarnings({"unchecked", "unused", "PMD.UnnecessaryFullyQualifiedName"})
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://www.jomc.org/jomc-tools/2.0-SNAPSHOT/jomc-tools-2.0-SNAPSHOT" )
    private org.apache.commons.cli.Option getModletVendorOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "Modlet Vendor Option" );
        assert _d != null : "'Modlet Vendor Option' dependency not found.";
        return _d;
    }
    /**
     * Gets the {@code <Modlet Version Option>} dependency.
     * <p>
     *   This method returns the {@code <JOMC ⁑ CLI ⁑ Modlet Version Option>} object of the {@code <JOMC ⁑ CLI ⁑ Command Option>} specification at specification level 1.2.
     *   That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.
     * </p>
     * <dl>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl>
     * @return The {@code <Modlet Version Option>} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @SuppressWarnings({"unchecked", "unused", "PMD.UnnecessaryFullyQualifiedName"})
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://www.jomc.org/jomc-tools/2.0-SNAPSHOT/jomc-tools-2.0-SNAPSHOT" )
    private org.apache.commons.cli.Option getModletVersionOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "Modlet Version Option" );
        assert _d != null : "'Modlet Version Option' dependency not found.";
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
     * Gets the {@code <Resources Option>} dependency.
     * <p>
     *   This method returns the {@code <JOMC ⁑ CLI ⁑ Resources Option>} object of the {@code <JOMC ⁑ CLI ⁑ Command Option>} specification at specification level 1.2.
     *   That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.
     * </p>
     * <dl>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl>
     * @return The {@code <Resources Option>} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @SuppressWarnings({"unchecked", "unused", "PMD.UnnecessaryFullyQualifiedName"})
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://www.jomc.org/jomc-tools/2.0-SNAPSHOT/jomc-tools-2.0-SNAPSHOT" )
    private org.apache.commons.cli.Option getResourcesOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "Resources Option" );
        assert _d != null : "'Resources Option' dependency not found.";
        return _d;
    }
    /**
     * Gets the {@code <Stylesheet Option>} dependency.
     * <p>
     *   This method returns the {@code <JOMC ⁑ CLI ⁑ Stylesheet Option>} object of the {@code <JOMC ⁑ CLI ⁑ Command Option>} specification at specification level 1.2.
     *   That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.
     * </p>
     * <dl>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl>
     * @return The {@code <Stylesheet Option>} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @SuppressWarnings({"unchecked", "unused", "PMD.UnnecessaryFullyQualifiedName"})
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://www.jomc.org/jomc-tools/2.0-SNAPSHOT/jomc-tools-2.0-SNAPSHOT" )
    private org.apache.commons.cli.Option getStylesheetOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "Stylesheet Option" );
        assert _d != null : "'Stylesheet Option' dependency not found.";
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
     * Gets the text of the {@code <Excluding Modlet Info>} message.
     * <p><dl>
     *   <dt><b>Languages:</b></dt>
     *     <dd>English (default)</dd>
     *     <dd>Deutsch</dd>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl></p>
     * @param locale The locale of the message to return.
     * @param modletName Format argument.
     * @return The text of the {@code <Excluding Modlet Info>} message for {@code locale}.
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @SuppressWarnings({"unchecked", "unused", "PMD.UnnecessaryFullyQualifiedName"})
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://www.jomc.org/jomc-tools/2.0-SNAPSHOT/jomc-tools-2.0-SNAPSHOT" )
    private String getExcludingModletInfo( final java.util.Locale locale, final java.lang.String modletName )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "Excluding Modlet Info", locale, modletName );
        assert _m != null : "'Excluding Modlet Info' message not found.";
        return _m;
    }
    /**
     * Gets the text of the {@code <Illegal Transformation Result Error>} message.
     * <p><dl>
     *   <dt><b>Languages:</b></dt>
     *     <dd>English (default)</dd>
     *     <dd>Deutsch</dd>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl></p>
     * @param locale The locale of the message to return.
     * @param stylesheetInfo Format argument.
     * @return The text of the {@code <Illegal Transformation Result Error>} message for {@code locale}.
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @SuppressWarnings({"unchecked", "unused", "PMD.UnnecessaryFullyQualifiedName"})
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://www.jomc.org/jomc-tools/2.0-SNAPSHOT/jomc-tools-2.0-SNAPSHOT" )
    private String getIllegalTransformationResultError( final java.util.Locale locale, final java.lang.String stylesheetInfo )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "Illegal Transformation Result Error", locale, stylesheetInfo );
        assert _m != null : "'Illegal Transformation Result Error' message not found.";
        return _m;
    }
    /**
     * Gets the text of the {@code <Including Modlet Info>} message.
     * <p><dl>
     *   <dt><b>Languages:</b></dt>
     *     <dd>English (default)</dd>
     *     <dd>Deutsch</dd>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl></p>
     * @param locale The locale of the message to return.
     * @param modletName Format argument.
     * @return The text of the {@code <Including Modlet Info>} message for {@code locale}.
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @SuppressWarnings({"unchecked", "unused", "PMD.UnnecessaryFullyQualifiedName"})
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://www.jomc.org/jomc-tools/2.0-SNAPSHOT/jomc-tools-2.0-SNAPSHOT" )
    private String getIncludingModletInfo( final java.util.Locale locale, final java.lang.String modletName )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "Including Modlet Info", locale, modletName );
        assert _m != null : "'Including Modlet Info' message not found.";
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
     * Gets the text of the {@code <Write Info>} message.
     * <p><dl>
     *   <dt><b>Languages:</b></dt>
     *     <dd>English (default)</dd>
     *     <dd>Deutsch</dd>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl></p>
     * @param locale The locale of the message to return.
     * @param fileName Format argument.
     * @return The text of the {@code <Write Info>} message for {@code locale}.
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @SuppressWarnings({"unchecked", "unused", "PMD.UnnecessaryFullyQualifiedName"})
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 2.0-SNAPSHOT", comments = "See http://www.jomc.org/jomc-tools/2.0-SNAPSHOT/jomc-tools-2.0-SNAPSHOT" )
    private String getWriteInfo( final java.util.Locale locale, final java.lang.String fileName )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "Write Info", locale, fileName );
        assert _m != null : "'Write Info' message not found.";
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
     *       <td align="left" valign="top" nowrap>JOMC ⁑ CLI ⁑ Document Encoding Option</td>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>JOMC ⁑ CLI ⁑ Command Option {@code (org.apache.commons.cli.Option)} @ 1.2</td>
     *       <td align="left" valign="top" nowrap>JOMC ⁑ CLI ⁑ Document Option</td>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>JOMC ⁑ CLI ⁑ Command Option {@code (org.apache.commons.cli.Option)} @ 1.2</td>
     *       <td align="left" valign="top" nowrap>JOMC ⁑ CLI ⁑ Documents Option</td>
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
     *       <td align="left" valign="top" nowrap>JOMC ⁑ CLI ⁑ Modlet Excludes Option</td>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>JOMC ⁑ CLI ⁑ Command Option {@code (org.apache.commons.cli.Option)} @ 1.2</td>
     *       <td align="left" valign="top" nowrap>JOMC ⁑ CLI ⁑ Modlet Includes Option</td>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>JOMC ⁑ CLI ⁑ Command Option {@code (org.apache.commons.cli.Option)} @ 1.2</td>
     *       <td align="left" valign="top" nowrap>JOMC ⁑ CLI ⁑ Modlet Location Option</td>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>JOMC ⁑ CLI ⁑ Command Option {@code (org.apache.commons.cli.Option)} @ 1.2</td>
     *       <td align="left" valign="top" nowrap>JOMC ⁑ CLI ⁑ Modlet Name Option</td>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>JOMC ⁑ CLI ⁑ Command Option {@code (org.apache.commons.cli.Option)} @ 1.2</td>
     *       <td align="left" valign="top" nowrap>JOMC ⁑ CLI ⁑ Modlet Schema System Id Option</td>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>JOMC ⁑ CLI ⁑ Command Option {@code (org.apache.commons.cli.Option)} @ 1.2</td>
     *       <td align="left" valign="top" nowrap>JOMC ⁑ CLI ⁑ Modlet Vendor Option</td>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>JOMC ⁑ CLI ⁑ Command Option {@code (org.apache.commons.cli.Option)} @ 1.2</td>
     *       <td align="left" valign="top" nowrap>JOMC ⁑ CLI ⁑ Modlet Version Option</td>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>JOMC ⁑ CLI ⁑ Command Option {@code (org.apache.commons.cli.Option)} @ 1.2</td>
     *       <td align="left" valign="top" nowrap>JOMC ⁑ CLI ⁑ No Modlet Resource Validation Option</td>
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
     *       <td align="left" valign="top" nowrap>JOMC ⁑ CLI ⁑ Resources Option</td>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>JOMC ⁑ CLI ⁑ Command Option {@code (org.apache.commons.cli.Option)} @ 1.2</td>
     *       <td align="left" valign="top" nowrap>JOMC ⁑ CLI ⁑ Stylesheet Option</td>
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
        options.addOption( this.getDocumentEncodingOption() );
        options.addOption( this.getDocumentOption() );
        options.addOption( this.getDocumentsOption() );
        options.addOption( this.getModelContextFactoryOption() );
        options.addOption( this.getModelOption() );
        options.addOption( this.getModletExcludesOption() );
        options.addOption( this.getModletIncludesOption() );
        options.addOption( this.getModletLocationOption() );
        options.addOption( this.getModletNameOption() );
        options.addOption( this.getModletSchemaSystemIdOption() );
        options.addOption( this.getModletVendorOption() );
        options.addOption( this.getModletVersionOption() );
        options.addOption( this.getNoModletResourceValidation() );
        options.addOption( this.getPlatformProviderLocationOption() );
        options.addOption( this.getProviderLocationOption() );
        options.addOption( this.getResourcesOption() );
        options.addOption( this.getStylesheetOption() );
        return options;
    }
    // </editor-fold>
    // SECTION-END

}
