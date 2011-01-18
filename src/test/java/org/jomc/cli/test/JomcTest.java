// SECTION-START[License Header]
// <editor-fold defaultstate="collapsed" desc=" Generated License ">
/*
 *   Copyright (c) 2011 The JOMC Project
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
package org.jomc.cli.test;

import org.apache.commons.io.IOUtils;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.net.URL;
import java.io.IOException;
import org.junit.Before;
import org.junit.Test;
import java.io.File;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import org.apache.commons.io.FileUtils;
import org.jomc.ObjectManagerFactory;
import org.jomc.cli.Command;
import org.jomc.cli.Jomc;
import org.jomc.model.ModelObject;
import org.jomc.model.Module;
import org.jomc.modlet.ModelContext;
import org.jomc.modlet.Modlet;
import org.jomc.modlet.ModletObject;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

// SECTION-START[Documentation]
// <editor-fold defaultstate="collapsed" desc=" Generated Documentation ">
/**
 * Tests the {@code Jomc} CLI class.
 * <p>
 *   <table border="1" width="100%" cellpadding="3" cellspacing="0">
 *     <caption class="TableCaption">Properties</caption>
 *     <tr>
 *       <th align="left" class="TableHeader" scope="col" nowrap>Name</th>
 *       <th align="left" class="TableHeader" scope="col" nowrap>Type</th>
 *       <th align="left" class="TableHeader" scope="col" nowrap>Documentation</th>
 *     </tr>
 *     <tr class="TableRowColor">
 *       <td align="left" nowrap>{@link #getClassesDirectory classesDirectory}</td>
 *       <td align="left" nowrap>{@code java.lang.String}</td>
 *       <td align="left" valign="top"></td>
 *     </tr>
 *     <tr class="TableRowColor">
 *       <td align="left" nowrap>{@link #getResourcesDirectory resourcesDirectory}</td>
 *       <td align="left" nowrap>{@code java.lang.String}</td>
 *       <td align="left" valign="top"></td>
 *     </tr>
 *     <tr class="TableRowColor">
 *       <td align="left" nowrap>{@link #getTestClassesDirectory testClassesDirectory}</td>
 *       <td align="left" nowrap>{@code java.lang.String}</td>
 *       <td align="left" valign="top"></td>
 *     </tr>
 *     <tr class="TableRowColor">
 *       <td align="left" nowrap>{@link #getTestModelDocument testModelDocument}</td>
 *       <td align="left" nowrap>{@code java.lang.String}</td>
 *       <td align="left" valign="top"></td>
 *     </tr>
 *     <tr class="TableRowColor">
 *       <td align="left" nowrap>{@link #getTestModelDocumentIllegal testModelDocumentIllegal}</td>
 *       <td align="left" nowrap>{@code java.lang.String}</td>
 *       <td align="left" valign="top"></td>
 *     </tr>
 *     <tr class="TableRowColor">
 *       <td align="left" nowrap>{@link #getTestModelDocumentNonExistentClasses testModelDocumentNonExistentClasses}</td>
 *       <td align="left" nowrap>{@code java.lang.String}</td>
 *       <td align="left" valign="top"></td>
 *     </tr>
 *     <tr class="TableRowColor">
 *       <td align="left" nowrap>{@link #getTestModelOutputDocument testModelOutputDocument}</td>
 *       <td align="left" nowrap>{@code java.lang.String}</td>
 *       <td align="left" valign="top"></td>
 *     </tr>
 *     <tr class="TableRowColor">
 *       <td align="left" nowrap>{@link #getTestModelStylesheet testModelStylesheet}</td>
 *       <td align="left" nowrap>{@code java.lang.String}</td>
 *       <td align="left" valign="top"></td>
 *     </tr>
 *     <tr class="TableRowColor">
 *       <td align="left" nowrap>{@link #getTestModletName testModletName}</td>
 *       <td align="left" nowrap>{@code java.lang.String}</td>
 *       <td align="left" valign="top"></td>
 *     </tr>
 *     <tr class="TableRowColor">
 *       <td align="left" nowrap>{@link #getTestModletOutputDocument testModletOutputDocument}</td>
 *       <td align="left" nowrap>{@code java.lang.String}</td>
 *       <td align="left" valign="top"></td>
 *     </tr>
 *     <tr class="TableRowColor">
 *       <td align="left" nowrap>{@link #getTestModletStylesheet testModletStylesheet}</td>
 *       <td align="left" nowrap>{@code java.lang.String}</td>
 *       <td align="left" valign="top"></td>
 *     </tr>
 *     <tr class="TableRowColor">
 *       <td align="left" nowrap>{@link #getTestModuleName testModuleName}</td>
 *       <td align="left" nowrap>{@code java.lang.String}</td>
 *       <td align="left" valign="top"></td>
 *     </tr>
 *     <tr class="TableRowColor">
 *       <td align="left" nowrap>{@link #getTestResourcesDirectory testResourcesDirectory}</td>
 *       <td align="left" nowrap>{@code java.lang.String}</td>
 *       <td align="left" valign="top"></td>
 *     </tr>
 *     <tr class="TableRowColor">
 *       <td align="left" nowrap>{@link #getTestShowInstanceOutputDocument testShowInstanceOutputDocument}</td>
 *       <td align="left" nowrap>{@code java.lang.String}</td>
 *       <td align="left" valign="top"></td>
 *     </tr>
 *     <tr class="TableRowColor">
 *       <td align="left" nowrap>{@link #getTestShowModelOutputDocument testShowModelOutputDocument}</td>
 *       <td align="left" nowrap>{@code java.lang.String}</td>
 *       <td align="left" valign="top"></td>
 *     </tr>
 *     <tr class="TableRowColor">
 *       <td align="left" nowrap>{@link #getTestShowSpecificationAndInstanceOutputDocument testShowSpecificationAndInstanceOutputDocument}</td>
 *       <td align="left" nowrap>{@code java.lang.String}</td>
 *       <td align="left" valign="top"></td>
 *     </tr>
 *     <tr class="TableRowColor">
 *       <td align="left" nowrap>{@link #getTestShowSpecificationOutputDocument testShowSpecificationOutputDocument}</td>
 *       <td align="left" nowrap>{@code java.lang.String}</td>
 *       <td align="left" valign="top"></td>
 *     </tr>
 *     <tr class="TableRowColor">
 *       <td align="left" nowrap>{@link #getTestSourcesDirectory testSourcesDirectory}</td>
 *       <td align="left" nowrap>{@code java.lang.String}</td>
 *       <td align="left" valign="top"></td>
 *     </tr>
 *   </table>
 * </p>
 *
 * @author <a href="mailto:schulte2005@users.sourceforge.net">Christian Schulte</a> 1.0
 * @version $Id$
 */
// </editor-fold>
// SECTION-END
// SECTION-START[Annotations]
// <editor-fold defaultstate="collapsed" desc=" Generated Annotations ">
@javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.2-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.2/jomc-tools-1.2-SNAPSHOT" )
// </editor-fold>
// SECTION-END
public class JomcTest
{
    // SECTION-START[JomcTest]

    /** Constant to prefix relative resource names with. */
    private static final String ABSOLUTE_RESOURCE_NAME_PREFIX = "/org/jomc/cli/test/";

    /** Constant for the name of the system property holding the output directory for the test. */
    private static final String OUTPUT_DIRECTORY_PROPERTY_NAME = "jomc.test.outputDirectory";

    /** Test resources to copy to the resources directory. */
    private static final String[] TEST_RESOURCE_NAMES =
    {
        "model-relocations.xsl",
        "modlet-relocations.xsl",
        "jomc.xml",
        "illegal-module.xml",
        "module-nonexistent-classes.xml"
    };

    /** The output directory of the instance. */
    private File outputDirectory;

    /**
     * Gets the output directory of instance.
     *
     * @return The output directory of instance.
     *
     * @see #setOutputDirectory(java.io.File)
     */
    public final File getOutputDirectory()
    {
        if ( this.outputDirectory == null )
        {
            final String name = System.getProperty( OUTPUT_DIRECTORY_PROPERTY_NAME );
            assertNotNull( "Expected '" + OUTPUT_DIRECTORY_PROPERTY_NAME + "' system property not found.", name );
            this.outputDirectory = new File( new File( name ), "JomcTest" );
            assertTrue( "Expected '" + OUTPUT_DIRECTORY_PROPERTY_NAME + "' system property to hold an absolute path.",
                        this.outputDirectory.isAbsolute() );

            if ( !this.outputDirectory.exists() )
            {
                assertTrue( this.outputDirectory.mkdirs() );
            }
        }

        return this.outputDirectory;
    }

    /**
     * Sets the output directory of instance.
     *
     * @param value The new output directory of instance or {@code null}.
     *
     * @see #getOutputDirectory()
     */
    public final void setOutputDirectory( final File value )
    {
        if ( value != null )
        {
            assertTrue( "Expected absolute 'outputDirectory'.", value.isAbsolute() );
        }

        this.outputDirectory = value;
    }

    @Test
    public final void testNoArguments() throws Exception
    {
        assertEquals( Command.STATUS_FAILURE, Jomc.run( new String[ 0 ] ) );
    }

    @Test
    public final void testGenerateResources() throws Exception
    {
        final File testResourcesDirectory = new File( this.getTestResourcesDirectory() );
        assertTrue( testResourcesDirectory.isAbsolute() );

        if ( testResourcesDirectory.exists() )
        {
            FileUtils.deleteDirectory( testResourcesDirectory );
        }

        final String[] help = new String[]
        {
            "generate-resources", "help"
        };

        final String[] args = new String[]
        {
            "generate-resources", "-rd", '"' + this.getTestResourcesDirectory() + '"', "-df",
            '"' + this.getTestModelDocument() + '"', "-D"
        };

        final String[] unsupportedOption = new String[]
        {
            "generate-resources", "--unsupported-option"
        };

        final String[] failOnWarnings = new String[]
        {
            "generate-resources", "-rd", '"' + this.getTestResourcesDirectory() + '"', "-df",
            '"' + this.getTestModelDocument() + '"', "-mn", "DOES_NOT_EXIST", "--fail-on-warnings", "-D"
        };

        assertEquals( Command.STATUS_SUCCESS, Jomc.run( help ) );
        assertEquals( Command.STATUS_FAILURE, Jomc.run( args ) );
        assertEquals( Command.STATUS_FAILURE, Jomc.run( unsupportedOption ) );

        assertTrue( testResourcesDirectory.mkdirs() );
        assertEquals( Command.STATUS_SUCCESS, Jomc.run( args ) );
        assertEquals( Command.STATUS_FAILURE, Jomc.run( failOnWarnings ) );
    }

    @Test
    public final void testManageSources() throws Exception
    {
        final File testSourcesDirectory = new File( this.getTestSourcesDirectory() );
        assertTrue( testSourcesDirectory.isAbsolute() );

        if ( testSourcesDirectory.exists() )
        {
            FileUtils.deleteDirectory( testSourcesDirectory );
        }

        final String[] help = new String[]
        {
            "manage-sources", "help"
        };

        final String[] args = new String[]
        {
            "manage-sources", "-sd", '"' + this.getTestSourcesDirectory() + '"', "-df",
            '"' + this.getTestModelDocument() + '"', "-mn", '"' + this.getTestModuleName() + '"', "-D",
            "-ls", "\r\n", "-idt", "\t"
        };

        final String[] unsupportedOption = new String[]
        {
            "manage-sources", "--unsupported-option"
        };

        final String[] failOnWarnings = new String[]
        {
            "manage-sources", "-sd", '"' + this.getTestSourcesDirectory() + '"', "-df",
            '"' + this.getTestModelDocument() + '"', "-mn", "DOES_NOT_EXIST", "--fail-on-warnings", "-D"
        };

        assertEquals( Command.STATUS_SUCCESS, Jomc.run( help ) );
        assertEquals( Command.STATUS_FAILURE, Jomc.run( args ) );
        assertEquals( Command.STATUS_FAILURE, Jomc.run( unsupportedOption ) );

        assertTrue( testSourcesDirectory.mkdirs() );
        assertEquals( Command.STATUS_SUCCESS, Jomc.run( args ) );
        assertEquals( Command.STATUS_FAILURE, Jomc.run( failOnWarnings ) );
    }

    @Test
    public final void testCommitValidateClasses() throws Exception
    {
        final File testClassesDirectory = new File( this.getTestClassesDirectory() );
        assertTrue( testClassesDirectory.isAbsolute() );

        if ( testClassesDirectory.exists() )
        {
            FileUtils.deleteDirectory( testClassesDirectory );
        }

        final String[] commitHelp = new String[]
        {
            "commit-classes", "help"
        };

        final String[] validateHelp = new String[]
        {
            "validate-classes", "help"
        };

        final String[] commitArgs = new String[]
        {
            "commit-classes", "-df", '"' + this.getTestModelDocument() + '"', "-cd",
            '"' + this.getClassesDirectory() + '"', "-mn",
            '"' + this.getTestModuleName() + '"', "-D"
        };

        final String[] commitArgsNoDirectory = new String[]
        {
            "commit-classes", "-df", '"' + this.getTestModelDocument() + '"', "-cd",
            '"' + this.getTestClassesDirectory() + '"', "-mn", '"' + this.getTestModuleName() + '"', "-D"
        };

        final String[] validateArgs = new String[]
        {
            "validate-classes", "-df", '"' + this.getTestModelDocument() + '"', "-cp",
            '"' + this.getClassesDirectory() + '"', "-D"
        };

        final String[] validateArgsNonExistentClasses = new String[]
        {
            "validate-classes", "-df", '"' + this.getTestModelDocumentNonExistentClasses() + '"', "-cp",
            '"' + this.getClassesDirectory() + '"', "-D"
        };

        final String[] commitUnsupportedOption = new String[]
        {
            "commit-classes", "--unsupported-option"
        };

        final String[] validateUnsupportedOption = new String[]
        {
            "validate-classes", "--unsupported-option"
        };

        final String[] commitFailOnWarnings = new String[]
        {
            "commit-classes", "-df", '"' + this.getTestModelDocument() + '"', "-cd",
            '"' + this.getClassesDirectory() + '"', "-mn",
            "DOES_NOT_EXIST", "--fail-on-warnings", "-D"
        };

        final String[] validateFailOnWarnings = new String[]
        {
            "validate-classes", "-df", '"' + this.getTestModelDocument() + '"', "-cp",
            '"' + this.getClassesDirectory() + '"', "-mn",
            "DOES_NOT_EXIST", "--fail-on-warnings", "-D"
        };

        final String[] commitWithStylesheet = new String[]
        {
            "commit-classes", "-df", '"' + this.getTestModelDocument() + '"', "-cd",
            '"' + this.getClassesDirectory() + '"', "-mn",
            '"' + this.getTestModuleName() + '"', "-D", "-stylesheet", '"' + this.getTestModelStylesheet() + '"'
        };

        assertEquals( Command.STATUS_SUCCESS, Jomc.run( commitHelp ) );
        assertEquals( Command.STATUS_SUCCESS, Jomc.run( validateHelp ) );
        assertEquals( Command.STATUS_FAILURE, Jomc.run( commitArgsNoDirectory ) );
        assertEquals( Command.STATUS_FAILURE, Jomc.run( commitUnsupportedOption ) );
        assertEquals( Command.STATUS_FAILURE, Jomc.run( validateUnsupportedOption ) );

        assertEquals( Command.STATUS_SUCCESS, Jomc.run( commitArgs ) );
        assertEquals( Command.STATUS_SUCCESS, Jomc.run( validateArgs ) );
        assertEquals( Command.STATUS_SUCCESS, Jomc.run( commitWithStylesheet ) );
        assertEquals( Command.STATUS_FAILURE, Jomc.run( commitFailOnWarnings ) );
        assertEquals( Command.STATUS_FAILURE, Jomc.run( validateFailOnWarnings ) );
        assertEquals( Command.STATUS_FAILURE, Jomc.run( validateArgsNonExistentClasses ) );
    }

    @Test
    public final void testMergeModules() throws Exception
    {
        final ModelContext context = ModelContext.createModelContext( this.getClass().getClassLoader() );
        final Unmarshaller unmarshaller = context.createUnmarshaller( ModelObject.MODEL_PUBLIC_ID );
        final Schema schema = context.createSchema( ModelObject.MODEL_PUBLIC_ID );
        unmarshaller.setSchema( schema );

        final String[] help = new String[]
        {
            "merge-modules", "help"
        };

        final String[] args = new String[]
        {
            "merge-modules", "-df", '"' + this.getTestModelDocument() + '"', "-xs",
            '"' + this.getTestModelStylesheet() + '"', "-mn", '"' + this.getTestModuleName() + '"', "-d",
            '"' + this.getTestModelOutputDocument() + '"', "-D"
        };

        final String[] includesArg = new String[]
        {
            "merge-modules", "-df", '"' + this.getTestModelDocument() + '"', "-xs",
            '"' + this.getTestModelStylesheet() + '"', "-mn", '"' + this.getTestModuleName() + '"', "-d",
            '"' + this.getTestModelOutputDocument() + '"', "-minc", "\"JOMC CLI\"", "-D"
        };

        final String[] excludesArg = new String[]
        {
            "merge-modules", "-df", '"' + this.getTestModelDocument() + '"', "-xs",
            '"' + this.getTestModelStylesheet() + '"', "-mn", '"' + this.getTestModuleName() + '"', "-d",
            '"' + this.getTestModelOutputDocument() + '"', "-mexc", "\"JOMC CLI\"", "-D"
        };

        final String[] unsupportedOption = new String[]
        {
            "merge-modules", "--unsupported-option"
        };

        final String[] illegalDoc = new String[]
        {
            "merge-modules", "-df", '"' + this.getTestModelDocumentIllegal() + '"', "-xs",
            '"' + this.getTestModelStylesheet() + '"', "-mn", '"' + this.getTestModuleName() + '"', "-d",
            '"' + this.getTestModelOutputDocument() + '"', "-D"
        };

        assertEquals( Command.STATUS_SUCCESS, Jomc.run( help ) );
        assertEquals( Command.STATUS_SUCCESS, Jomc.run( args ) );

        unmarshaller.unmarshal( new StreamSource( new File( this.getTestModelOutputDocument() ) ), Module.class );

        assertEquals( Command.STATUS_SUCCESS, Jomc.run( includesArg ) );

        final JAXBElement<Module> includedModule =
            unmarshaller.unmarshal( new StreamSource( new File( this.getTestModelOutputDocument() ) ), Module.class );

        assertNotNull( "Merged module does not contain any included specifications.",
                       includedModule.getValue().getSpecifications() );

        assertNotNull( "Merged module does not contain included 'org.jomc.cli.Command' specification.",
                       includedModule.getValue().getSpecifications().getSpecification( Command.class ) );

        assertEquals( Command.STATUS_SUCCESS, Jomc.run( excludesArg ) );

        final JAXBElement<Module> excludedModule =
            unmarshaller.unmarshal( new StreamSource( new File( this.getTestModelOutputDocument() ) ), Module.class );

        assertNull( "Merged module contains excluded specifications.",
                    excludedModule.getValue().getSpecifications() );

        assertEquals( Command.STATUS_FAILURE, Jomc.run( unsupportedOption ) );
        assertEquals( Command.STATUS_FAILURE, Jomc.run( illegalDoc ) );
    }

    @Test
    public final void testValidateModel() throws Exception
    {
        final String[] help = new String[]
        {
            "validate-model", "help"
        };

        final String[] args = new String[]
        {
            "validate-model", "-df", '"' + this.getTestModelDocument() + '"', "-D"
        };

        final String[] unsupportedOption = new String[]
        {
            "validate-model", "--unsupported-option"
        };

        final String[] illegalDoc = new String[]
        {
            "validate-model", "-df", '"' + this.getTestModelDocumentIllegal() + '"', "-D"
        };

        assertEquals( Command.STATUS_SUCCESS, Jomc.run( help ) );
        assertEquals( Command.STATUS_SUCCESS, Jomc.run( args ) );
        assertEquals( Command.STATUS_FAILURE, Jomc.run( unsupportedOption ) );
        assertEquals( Command.STATUS_FAILURE, Jomc.run( illegalDoc ) );
    }

    @Test
    public final void testMergeModlets() throws Exception
    {
        final ModelContext context = ModelContext.createModelContext( this.getClass().getClassLoader() );
        final Unmarshaller unmarshaller = context.createUnmarshaller( ModletObject.MODEL_PUBLIC_ID );
        final Schema schema = context.createSchema( ModletObject.MODEL_PUBLIC_ID );
        unmarshaller.setSchema( schema );

        final String[] help = new String[]
        {
            "merge-modlets", "help"
        };

        final String[] args = new String[]
        {
            "merge-modlets", "-xs", '"' + this.getTestModletStylesheet() + '"', "-mdn",
            '"' + this.getTestModletName() + '"', "-d", '"' + this.getTestModletOutputDocument() + '"'
        };

        final String[] includeArgs = new String[]
        {
            "merge-modlets", "-xs", '"' + this.getTestModletStylesheet() + '"', "-mdn",
            '"' + this.getTestModletName() + '"', "-d", '"' + this.getTestModletOutputDocument() + '"',
            "-mdinc", "JOMC Model"
        };

        final String[] excludeArgs = new String[]
        {
            "merge-modlets", "-xs", '"' + this.getTestModletStylesheet() + '"', "-mdn",
            '"' + this.getTestModletName() + '"', "-d", '"' + this.getTestModletOutputDocument() + '"',
            "-mdexc", "JOMC Model" + File.pathSeparatorChar + "JOMC Tools" + File.pathSeparatorChar + "JOMC Modlet"
        };

        final String[] unsupportedOption = new String[]
        {
            "merge-modlets", "--unsupported-option"
        };

        assertEquals( Command.STATUS_SUCCESS, Jomc.run( help ) );
        assertEquals( Command.STATUS_FAILURE, Jomc.run( unsupportedOption ) );

        assertEquals( Command.STATUS_SUCCESS, Jomc.run( args ) );
        Modlet merged = unmarshaller.unmarshal(
            new StreamSource( new File( this.getTestModletOutputDocument() ) ), Modlet.class ).getValue();

        assertNotNull( merged );
        assertEquals( this.getTestModletName(), merged.getName() );
        assertNotNull( merged.getSchemas() );
        assertNotNull( merged.getServices() );
        assertEquals( 2, merged.getSchemas().getSchema().size() );
        assertEquals( 3, merged.getServices().getService().size() );
        assertNotNull( merged.getSchemas().getSchemaByPublicId( "http://jomc.org/model" ) );
        assertNotNull( merged.getSchemas().getSchemaByPublicId( "http://jomc.org/tools/model" ) );
        assertEquals( 1, merged.getServices().getServices( "org.jomc.modlet.ModelProvider" ).size() );
        assertEquals( 1, merged.getServices().getServices( "org.jomc.modlet.ModelProcessor" ).size() );
        assertEquals( 1, merged.getServices().getServices( "org.jomc.modlet.ModelValidator" ).size() );

        assertEquals( Command.STATUS_SUCCESS, Jomc.run( includeArgs ) );
        merged = unmarshaller.unmarshal(
            new StreamSource( new File( this.getTestModletOutputDocument() ) ), Modlet.class ).getValue();

        assertNotNull( merged );
        assertEquals( this.getTestModletName(), merged.getName() );
        assertNotNull( merged.getSchemas() );
        assertNotNull( merged.getServices() );
        assertEquals( 1, merged.getSchemas().getSchema().size() );
        assertNotNull( merged.getSchemas().getSchemaByPublicId( "http://jomc.org/model" ) );
        assertEquals( 3, merged.getServices().getService().size() );
        assertEquals( 1, merged.getServices().getServices( "org.jomc.modlet.ModelProvider" ).size() );
        assertEquals( 1, merged.getServices().getServices( "org.jomc.modlet.ModelProcessor" ).size() );
        assertEquals( 1, merged.getServices().getServices( "org.jomc.modlet.ModelValidator" ).size() );

        assertEquals( Command.STATUS_SUCCESS, Jomc.run( excludeArgs ) );
        merged = unmarshaller.unmarshal(
            new StreamSource( new File( this.getTestModletOutputDocument() ) ), Modlet.class ).getValue();

        assertNotNull( merged );
        assertEquals( this.getTestModletName(), merged.getName() );
        assertNull( merged.getSchemas() );
        assertNull( merged.getServices() );
    }

    @Test
    public final void testShowModel() throws Exception
    {
        final File classesDirectory = new File( this.getOutputDirectory(), "jomc-test-classes" );

        final String[] help = new String[]
        {
            "show-model", "help"
        };

        final String[] showModel = new String[]
        {
            "show-model", "-cp", '"' + classesDirectory.getAbsolutePath() + '"'
        };

        final String[] writeModel = new String[]
        {
            "show-model", "-cp", '"' + classesDirectory.getAbsolutePath() + '"', "-d",
            '"' + this.getTestShowModelOutputDocument() + '"'
        };

        final String[] showSpecification = new String[]
        {
            "show-model", "-cp", '"' + classesDirectory.getAbsolutePath() + '"', "-spec", "JOMC CLI Command"
        };

        final String[] writeSpecification = new String[]
        {
            "show-model", "-cp", '"' + classesDirectory.getAbsolutePath() + '"', "-spec", "JOMC CLI Command", "-d",
            '"' + this.getTestShowSpecificationOutputDocument() + '"'
        };

        final String[] showInstance = new String[]
        {
            "show-model", "-cp", '"' + classesDirectory.getAbsolutePath() + '"', "-impl", "JOMC CLI show-model Command"
        };

        final String[] writeInstance = new String[]
        {
            "show-model", "-cp", '"' + classesDirectory.getAbsolutePath() + '"', "-impl", "JOMC CLI show-model Command",
            "-d",
            '"' + this.getTestShowInstanceOutputDocument() + '"'
        };

        final String[] showSpecificationAndInstance = new String[]
        {
            "show-model", "-cp", '"' + classesDirectory.getAbsolutePath() + '"', "-spec", "JOMC CLI Command", "-impl",
            "JOMC CLI show-model Command"
        };

        final String[] writeSpecificationAndInstance = new String[]
        {
            "show-model", "-cp", '"' + classesDirectory.getAbsolutePath() + '"', "-spec", "JOMC CLI Command", "-impl",
            "JOMC CLI show-model Command", "-d", '"' + this.getTestShowSpecificationAndInstanceOutputDocument() + '"'
        };

        final String[] unsupportedOption = new String[]
        {
            "show-model", "--unsupported-option"
        };

        assertEquals( Command.STATUS_SUCCESS, Jomc.run( help ) );
        assertEquals( Command.STATUS_SUCCESS, Jomc.run( showModel ) );
        assertEquals( Command.STATUS_SUCCESS, Jomc.run( writeModel ) );
        assertEquals( Command.STATUS_SUCCESS, Jomc.run( showInstance ) );
        assertEquals( Command.STATUS_SUCCESS, Jomc.run( writeInstance ) );
        assertEquals( Command.STATUS_SUCCESS, Jomc.run( showSpecification ) );
        assertEquals( Command.STATUS_SUCCESS, Jomc.run( writeSpecification ) );
        assertEquals( Command.STATUS_SUCCESS, Jomc.run( showSpecificationAndInstance ) );
        assertEquals( Command.STATUS_SUCCESS, Jomc.run( writeSpecificationAndInstance ) );
        assertEquals( Command.STATUS_FAILURE, Jomc.run( unsupportedOption ) );
    }

    @Before
    public void setUp() throws IOException
    {
        // Ensures the singleton is initialized prior to class Jomc switching resource locations.
        ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() );

        final File f = this.getOutputDirectory();
        if ( !f.exists() )
        {
            assertTrue( f.mkdirs() );
        }

        final File resourcesDirectory = new File( this.getResourcesDirectory() );
        assertTrue( resourcesDirectory.isAbsolute() );
        FileUtils.deleteDirectory( resourcesDirectory );
        assertTrue( resourcesDirectory.mkdirs() );

        for ( String testResourceName : TEST_RESOURCE_NAMES )
        {
            final URL rsrc = this.getClass().getResource( ABSOLUTE_RESOURCE_NAME_PREFIX + testResourceName );
            assertNotNull( rsrc );
            FileUtils.copyInputStreamToFile( rsrc.openStream(), new File( resourcesDirectory, testResourceName ) );
        }

        final File classesDirectory = new File( this.getClassesDirectory() );
        this.unzipResource( ABSOLUTE_RESOURCE_NAME_PREFIX + "classfiles.zip", classesDirectory );
    }

    private void unzipResource( final String resourceName, final File targetDirectory ) throws IOException
    {
        assertTrue( resourceName.startsWith( "/" ) );
        final URL resource = this.getClass().getResource( resourceName );
        assertNotNull( "Expected '" + resourceName + "' not found.", resource );

        assertTrue( targetDirectory.isAbsolute() );
        FileUtils.deleteDirectory( targetDirectory );
        assertTrue( targetDirectory.mkdirs() );

        final ZipInputStream in = new ZipInputStream( resource.openStream() );
        ZipEntry e = null;

        while ( ( e = in.getNextEntry() ) != null )
        {
            final File dest = new File( targetDirectory, e.getName() );
            assertTrue( dest.isAbsolute() );

            if ( e.isDirectory() )
            {
                if ( !dest.exists() )
                {
                    assertTrue( dest.mkdirs() );
                }

                continue;
            }

            final OutputStream out = FileUtils.openOutputStream( dest );
            IOUtils.copy( in, out );
            IOUtils.closeQuietly( out );
            in.closeEntry();
        }

        IOUtils.closeQuietly( in );
    }

    // SECTION-END
    // SECTION-START[Constructors]
    // <editor-fold defaultstate="collapsed" desc=" Generated Constructors ">

    /** Creates a new {@code JomcTest} instance. */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.2-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.2/jomc-tools-1.2-SNAPSHOT" )
    public JomcTest()
    {
        // SECTION-START[Default Constructor]
        super();
        // SECTION-END
    }
    // </editor-fold>
    // SECTION-END
    // SECTION-START[Dependencies]
    // SECTION-END
    // SECTION-START[Properties]
    // <editor-fold defaultstate="collapsed" desc=" Generated Properties ">

    /**
     * Gets the value of the {@code classesDirectory} property.
     * @return The value of the {@code classesDirectory} property.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.2-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.2/jomc-tools-1.2-SNAPSHOT" )
    private java.lang.String getClassesDirectory()
    {
        final java.lang.String _p = (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getProperty( this, "classesDirectory" );
        assert _p != null : "'classesDirectory' property not found.";
        return _p;
    }

    /**
     * Gets the value of the {@code resourcesDirectory} property.
     * @return The value of the {@code resourcesDirectory} property.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.2-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.2/jomc-tools-1.2-SNAPSHOT" )
    private java.lang.String getResourcesDirectory()
    {
        final java.lang.String _p = (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getProperty( this, "resourcesDirectory" );
        assert _p != null : "'resourcesDirectory' property not found.";
        return _p;
    }

    /**
     * Gets the value of the {@code testClassesDirectory} property.
     * @return The value of the {@code testClassesDirectory} property.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.2-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.2/jomc-tools-1.2-SNAPSHOT" )
    private java.lang.String getTestClassesDirectory()
    {
        final java.lang.String _p = (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getProperty( this, "testClassesDirectory" );
        assert _p != null : "'testClassesDirectory' property not found.";
        return _p;
    }

    /**
     * Gets the value of the {@code testModelDocument} property.
     * @return The value of the {@code testModelDocument} property.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.2-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.2/jomc-tools-1.2-SNAPSHOT" )
    private java.lang.String getTestModelDocument()
    {
        final java.lang.String _p = (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getProperty( this, "testModelDocument" );
        assert _p != null : "'testModelDocument' property not found.";
        return _p;
    }

    /**
     * Gets the value of the {@code testModelDocumentIllegal} property.
     * @return The value of the {@code testModelDocumentIllegal} property.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.2-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.2/jomc-tools-1.2-SNAPSHOT" )
    private java.lang.String getTestModelDocumentIllegal()
    {
        final java.lang.String _p = (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getProperty( this, "testModelDocumentIllegal" );
        assert _p != null : "'testModelDocumentIllegal' property not found.";
        return _p;
    }

    /**
     * Gets the value of the {@code testModelDocumentNonExistentClasses} property.
     * @return The value of the {@code testModelDocumentNonExistentClasses} property.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.2-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.2/jomc-tools-1.2-SNAPSHOT" )
    private java.lang.String getTestModelDocumentNonExistentClasses()
    {
        final java.lang.String _p = (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getProperty( this, "testModelDocumentNonExistentClasses" );
        assert _p != null : "'testModelDocumentNonExistentClasses' property not found.";
        return _p;
    }

    /**
     * Gets the value of the {@code testModelOutputDocument} property.
     * @return The value of the {@code testModelOutputDocument} property.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.2-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.2/jomc-tools-1.2-SNAPSHOT" )
    private java.lang.String getTestModelOutputDocument()
    {
        final java.lang.String _p = (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getProperty( this, "testModelOutputDocument" );
        assert _p != null : "'testModelOutputDocument' property not found.";
        return _p;
    }

    /**
     * Gets the value of the {@code testModelStylesheet} property.
     * @return The value of the {@code testModelStylesheet} property.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.2-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.2/jomc-tools-1.2-SNAPSHOT" )
    private java.lang.String getTestModelStylesheet()
    {
        final java.lang.String _p = (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getProperty( this, "testModelStylesheet" );
        assert _p != null : "'testModelStylesheet' property not found.";
        return _p;
    }

    /**
     * Gets the value of the {@code testModletName} property.
     * @return The value of the {@code testModletName} property.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.2-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.2/jomc-tools-1.2-SNAPSHOT" )
    private java.lang.String getTestModletName()
    {
        final java.lang.String _p = (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getProperty( this, "testModletName" );
        assert _p != null : "'testModletName' property not found.";
        return _p;
    }

    /**
     * Gets the value of the {@code testModletOutputDocument} property.
     * @return The value of the {@code testModletOutputDocument} property.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.2-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.2/jomc-tools-1.2-SNAPSHOT" )
    private java.lang.String getTestModletOutputDocument()
    {
        final java.lang.String _p = (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getProperty( this, "testModletOutputDocument" );
        assert _p != null : "'testModletOutputDocument' property not found.";
        return _p;
    }

    /**
     * Gets the value of the {@code testModletStylesheet} property.
     * @return The value of the {@code testModletStylesheet} property.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.2-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.2/jomc-tools-1.2-SNAPSHOT" )
    private java.lang.String getTestModletStylesheet()
    {
        final java.lang.String _p = (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getProperty( this, "testModletStylesheet" );
        assert _p != null : "'testModletStylesheet' property not found.";
        return _p;
    }

    /**
     * Gets the value of the {@code testModuleName} property.
     * @return The value of the {@code testModuleName} property.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.2-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.2/jomc-tools-1.2-SNAPSHOT" )
    private java.lang.String getTestModuleName()
    {
        final java.lang.String _p = (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getProperty( this, "testModuleName" );
        assert _p != null : "'testModuleName' property not found.";
        return _p;
    }

    /**
     * Gets the value of the {@code testResourcesDirectory} property.
     * @return The value of the {@code testResourcesDirectory} property.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.2-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.2/jomc-tools-1.2-SNAPSHOT" )
    private java.lang.String getTestResourcesDirectory()
    {
        final java.lang.String _p = (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getProperty( this, "testResourcesDirectory" );
        assert _p != null : "'testResourcesDirectory' property not found.";
        return _p;
    }

    /**
     * Gets the value of the {@code testShowInstanceOutputDocument} property.
     * @return The value of the {@code testShowInstanceOutputDocument} property.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.2-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.2/jomc-tools-1.2-SNAPSHOT" )
    private java.lang.String getTestShowInstanceOutputDocument()
    {
        final java.lang.String _p = (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getProperty( this, "testShowInstanceOutputDocument" );
        assert _p != null : "'testShowInstanceOutputDocument' property not found.";
        return _p;
    }

    /**
     * Gets the value of the {@code testShowModelOutputDocument} property.
     * @return The value of the {@code testShowModelOutputDocument} property.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.2-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.2/jomc-tools-1.2-SNAPSHOT" )
    private java.lang.String getTestShowModelOutputDocument()
    {
        final java.lang.String _p = (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getProperty( this, "testShowModelOutputDocument" );
        assert _p != null : "'testShowModelOutputDocument' property not found.";
        return _p;
    }

    /**
     * Gets the value of the {@code testShowSpecificationAndInstanceOutputDocument} property.
     * @return The value of the {@code testShowSpecificationAndInstanceOutputDocument} property.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.2-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.2/jomc-tools-1.2-SNAPSHOT" )
    private java.lang.String getTestShowSpecificationAndInstanceOutputDocument()
    {
        final java.lang.String _p = (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getProperty( this, "testShowSpecificationAndInstanceOutputDocument" );
        assert _p != null : "'testShowSpecificationAndInstanceOutputDocument' property not found.";
        return _p;
    }

    /**
     * Gets the value of the {@code testShowSpecificationOutputDocument} property.
     * @return The value of the {@code testShowSpecificationOutputDocument} property.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.2-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.2/jomc-tools-1.2-SNAPSHOT" )
    private java.lang.String getTestShowSpecificationOutputDocument()
    {
        final java.lang.String _p = (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getProperty( this, "testShowSpecificationOutputDocument" );
        assert _p != null : "'testShowSpecificationOutputDocument' property not found.";
        return _p;
    }

    /**
     * Gets the value of the {@code testSourcesDirectory} property.
     * @return The value of the {@code testSourcesDirectory} property.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.2-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.2/jomc-tools-1.2-SNAPSHOT" )
    private java.lang.String getTestSourcesDirectory()
    {
        final java.lang.String _p = (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getProperty( this, "testSourcesDirectory" );
        assert _p != null : "'testSourcesDirectory' property not found.";
        return _p;
    }
    // </editor-fold>
    // SECTION-END
    // SECTION-START[Messages]
    // SECTION-END
}
