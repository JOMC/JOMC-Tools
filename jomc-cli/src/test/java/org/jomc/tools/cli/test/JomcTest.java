/*
 * Copyright 2009 (C) Christian Schulte <cs@schulte.it>
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
package org.jomc.tools.cli.test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import org.apache.commons.io.FileUtils;
import org.jomc.model.ModelObject;
import org.jomc.model.Module;
import org.jomc.modlet.ModelContext;
import org.jomc.modlet.ModelContextFactory;
import org.jomc.modlet.Modlet;
import org.jomc.modlet.ModletObject;
import org.jomc.tools.cli.Command;
import org.jomc.tools.cli.Jomc;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests the {@code Jomc} CLI class.
 *
 * @author <a href="mailto:cs@schulte.it">Christian Schulte</a>
 */
public class JomcTest
{

    /**
     * Constant to prefix relative resource names with.
     */
    private static final String ABSOLUTE_RESOURCE_NAME_PREFIX =
        "/" + JomcTest.class.getPackage().getName().replace( '.', '/' ) + "/";

    /**
     * Constant for the name of the system property holding the output directory for the test.
     */
    private static final String OUTPUT_DIRECTORY_PROPERTY_NAME = "jomc.test.outputDirectory";

    /**
     * Test resources to copy to the resources directory.
     */
    private static final String[] TEST_RESOURCE_NAMES =
    {
        "model-relocations.xsl",
        "modlet-relocations.xsl",
        "jomc.xml",
        "illegal-module.xml",
        "illegal-module-document.xml",
        "module-nonexistent-classes.xml"
    };

    /**
     * The output directory of the instance.
     */
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
        final File resourcesDirectory = new File( this.getOutputDirectory(), "resources" );
        final File testResourcesDirectory = new File( this.getOutputDirectory(), "generated-resources" );
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
            "generate-resources", "-rd", '"' + testResourcesDirectory.getAbsolutePath() + '"', "-df",
            '"' + new File( resourcesDirectory, "jomc.xml" ).getAbsolutePath() + '"', "-D"
        };

        final String[] unsupportedOption = new String[]
        {
            "generate-resources", "--unsupported-option"
        };

        final String[] failOnWarnings = new String[]
        {
            "generate-resources", "-rd", '"' + testResourcesDirectory.getAbsolutePath() + '"', "-df",
            '"' + new File( resourcesDirectory, "jomc.xml" ).getAbsolutePath() + '"', "-mn", "DOES_NOT_EXIST",
            "--fail-on-warnings", "-D"
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
        final File resourcesDirectory = new File( this.getOutputDirectory(), "resources" );
        final File testSourcesDirectory = new File( this.getOutputDirectory(), "generated-sources" );
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
            "manage-sources", "-sd", '"' + testSourcesDirectory.getAbsolutePath() + '"', "-df",
            '"' + new File( resourcesDirectory, "jomc.xml" ).getAbsolutePath() + '"', "-mn",
            "\"JOMC Tools ⁑ CLI\"", "-D", "-ls", "\r\n", "-idt", "\t"
        };

        final String[] unsupportedOption = new String[]
        {
            "manage-sources", "--unsupported-option"
        };

        final String[] failOnWarnings = new String[]
        {
            "manage-sources", "-sd", '"' + testSourcesDirectory.getAbsolutePath() + '"', "-df",
            '"' + new File( resourcesDirectory, "jomc.xml" ).getAbsolutePath() + '"', "-mn", "DOES_NOT_EXIST",
            "--fail-on-warnings", "-D"
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
        final File resourcesDirectory = new File( this.getOutputDirectory(), "resources" );
        final File classesDirectory = new File( this.getOutputDirectory(), "classes" );
        final File nonExistentDirectory = new File( this.getOutputDirectory(), "does-not-exist" );
        assertTrue( nonExistentDirectory.isAbsolute() );

        if ( nonExistentDirectory.exists() )
        {
            FileUtils.deleteDirectory( nonExistentDirectory );
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
            "commit-classes", "-df", '"' + new File( resourcesDirectory, "jomc.xml" ).getAbsolutePath() + '"', "-cd",
            '"' + classesDirectory.getAbsolutePath() + '"', "-mn", "\"JOMC Tools ⁑ CLI\"", "-D"
        };

        final String[] commitArgsNoDirectory = new String[]
        {
            "commit-classes", "-df", '"' + new File( resourcesDirectory, "jomc.xml" ).getAbsolutePath() + '"', "-cd",
            '"' + nonExistentDirectory.getAbsolutePath() + '"', "-mn", "\"JOMC Tools ⁑ CLI\"", "-D"
        };

        final String[] validateArgs = new String[]
        {
            "validate-classes", "-cp", '"' + classesDirectory.getAbsolutePath() + '"', "-D"
        };

        final String[] validateArgsNonExistentClasses = new String[]
        {
            "validate-classes", "-df", '"' + new File( resourcesDirectory, "module-nonexistent-classes.xml" ).
            getAbsolutePath() + '"', "-cp", '"' + classesDirectory.getAbsolutePath() + '"', "-D"
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
            "commit-classes", "-df", '"' + new File( resourcesDirectory, "jomc.xml" ).getAbsolutePath() + '"', "-cd",
            '"' + classesDirectory.getAbsolutePath() + '"', "-mn", "DOES_NOT_EXIST", "--fail-on-warnings",
            "-D"
        };

        final String[] validateFailOnWarnings = new String[]
        {
            "validate-classes", "-df", '"' + new File( resourcesDirectory, "jomc.xml" ).getAbsolutePath() + '"', "-cp",
            '"' + classesDirectory.getAbsolutePath() + '"', "-mn", "DOES_NOT_EXIST", "--fail-on-warnings",
            "-D"
        };

        final String[] commitWithStylesheet = new String[]
        {
            "commit-classes", "-df", '"' + new File( resourcesDirectory, "jomc.xml" ).getAbsolutePath() + '"', "-cd",
            '"' + classesDirectory.getAbsolutePath() + '"', "-mn", "\"JOMC Tools ⁑ CLI\"",
            "-D", "-stylesheet", '"' + new File( resourcesDirectory, "model-relocations.xsl" ).getAbsolutePath() + '"'
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
        final File resourcesDirectory = new File( this.getOutputDirectory(), "resources" );
        final File targetDocument = new File( this.getOutputDirectory(), "transformed-model.xml" );
        final ModelContext context = ModelContextFactory.newInstance().newModelContext();
        final Unmarshaller unmarshaller = context.createUnmarshaller( ModelObject.MODEL_PUBLIC_ID );
        final Schema schema = context.createSchema( ModelObject.MODEL_PUBLIC_ID );
        unmarshaller.setSchema( schema );

        final String[] help = new String[]
        {
            "merge-modules", "help"
        };

        final String[] args = new String[]
        {
            "merge-modules", "-df", '"' + new File( resourcesDirectory, "jomc.xml" ).getAbsolutePath() + '"', "-xs",
            '"' + new File( resourcesDirectory, "model-relocations.xsl" ).getAbsolutePath() + '"', "-mn",
            "\"JOMC Tools ⁑ CLI\"", "-d", '"' + targetDocument.getAbsolutePath() + '"', "-D"
        };

        final String[] includesArg = new String[]
        {
            "merge-modules", "-df", '"' + new File( resourcesDirectory, "jomc.xml" ).getAbsolutePath() + '"', "-xs",
            '"' + new File( resourcesDirectory, "model-relocations.xsl" ).getAbsolutePath() + '"', "-mn",
            "\"JOMC Tools ⁑ CLI\"", "-d", '"' + targetDocument.getAbsolutePath() + '"', "-minc", "\"JOMC Tools ⁑ CLI\"",
            "-D"
        };

        final String[] excludesArg = new String[]
        {
            "merge-modules", "-df", '"' + new File( resourcesDirectory, "jomc.xml" ).getAbsolutePath() + '"', "-xs",
            '"' + new File( resourcesDirectory, "model-relocations.xsl" ).getAbsolutePath() + '"', "-mn",
            "\"JOMC Tools ⁑ CLI\"", "-d", '"' + targetDocument.getAbsolutePath() + '"', "-mexc", "\"JOMC Tools ⁑ CLI\"",
            "-D"
        };

        final String[] unsupportedOption = new String[]
        {
            "merge-modules", "--unsupported-option"
        };

        final String[] illegalDoc = new String[]
        {
            "merge-modules", "-df", '"' + new File( resourcesDirectory, "illegal-module-document.xml" ).
            getAbsolutePath() + '"', "-xs", '"' + new File( resourcesDirectory, "model-relocations.xsl" ).
            getAbsolutePath() + '"', "-mn", "\"JOMC Tools ⁑ CLI\"", "-d", '"' + targetDocument.getAbsolutePath() + '"',
            "-D"
        };

        assertEquals( Command.STATUS_SUCCESS, Jomc.run( help ) );
        assertEquals( Command.STATUS_SUCCESS, Jomc.run( args ) );

        unmarshaller.unmarshal( new StreamSource( targetDocument ), Module.class );

        assertEquals( Command.STATUS_SUCCESS, Jomc.run( includesArg ) );

        final JAXBElement<Module> includedModule =
            unmarshaller.unmarshal( new StreamSource( targetDocument ), Module.class );

        assertNotNull( "Merged module does not contain any included specifications.",
                       includedModule.getValue().getSpecifications() );

        assertNotNull( "Merged module does not contain included 'org.jomc.tools.cli.Command' specification.",
                       includedModule.getValue().getSpecifications().getSpecification( Command.class ) );

        assertEquals( Command.STATUS_SUCCESS, Jomc.run( excludesArg ) );

        final JAXBElement<Module> excludedModule =
            unmarshaller.unmarshal( new StreamSource( targetDocument ), Module.class );

        assertNull( "Merged module contains excluded specifications.",
                    excludedModule.getValue().getSpecifications() );

        assertEquals( Command.STATUS_FAILURE, Jomc.run( unsupportedOption ) );
        assertEquals( Command.STATUS_FAILURE, Jomc.run( illegalDoc ) );
    }

    @Test
    public final void testValidateModel() throws Exception
    {
        final File resourcesDirectory = new File( this.getOutputDirectory(), "resources" );
        final String[] help = new String[]
        {
            "validate-model", "help"
        };

        final String[] args = new String[]
        {
            "validate-model", "-df", '"' + new File( resourcesDirectory, "jomc.xml" ).getAbsolutePath() + '"', "-D"
        };

        final String[] unsupportedOption = new String[]
        {
            "validate-model", "--unsupported-option"
        };

        final String[] illegalDoc = new String[]
        {
            "validate-model", "-df", '"' + new File( resourcesDirectory, "illegal-module.xml" ).getAbsolutePath() + '"',
            "-D"
        };

        assertEquals( Command.STATUS_SUCCESS, Jomc.run( help ) );
        assertEquals( Command.STATUS_SUCCESS, Jomc.run( args ) );
        assertEquals( Command.STATUS_FAILURE, Jomc.run( unsupportedOption ) );
        assertEquals( Command.STATUS_FAILURE, Jomc.run( illegalDoc ) );
    }

    @Test
    public final void testMergeModlets() throws Exception
    {
        final File resourcesDirectory = new File( this.getOutputDirectory(), "resources" );
        final File targetDocument = new File( this.getOutputDirectory(), "transformed-modlet.xml" );
        final ModelContext context = ModelContextFactory.newInstance().newModelContext();
        final Unmarshaller unmarshaller = context.createUnmarshaller( ModletObject.MODEL_PUBLIC_ID );
        final Schema schema = context.createSchema( ModletObject.MODEL_PUBLIC_ID );
        unmarshaller.setSchema( schema );

        final String[] help = new String[]
        {
            "merge-modlets", "help"
        };

        final String[] args = new String[]
        {
            "merge-modlets", "-xs", '"' + new File( resourcesDirectory, "modlet-relocations.xsl" ).
            getAbsolutePath() + '"', "-mdn", "\"JOMC Tools ⁑ CLI ⁑ Tests\"", "-d",
            '"' + targetDocument.getAbsolutePath() + '"', "-cp", "."
        };

        final String[] includeArgs = new String[]
        {
            "merge-modlets", "-xs", '"' + new File( resourcesDirectory, "modlet-relocations.xsl" ).
            getAbsolutePath() + '"', "-mdn", "\"JOMC Tools ⁑ CLI ⁑ Tests\"", "-d",
            '"' + targetDocument.getAbsolutePath() + '"', "-mdinc", "\"JOMC ⁑ Model\"", "-cp", "."
        };

        final String[] excludeArgs = new String[]
        {
            "merge-modlets", "-xs", '"' + new File( resourcesDirectory, "modlet-relocations.xsl" ).
            getAbsolutePath() + '"', "-mdn", "\"JOMC Tools ⁑ CLI ⁑ Tests\"", "-d",
            '"' + targetDocument.getAbsolutePath() + '"',
            "-mdexc", "\"JOMC ⁑ Model" + File.pathSeparatorChar + "JOMC Tools ⁑ Modlet" + File.pathSeparatorChar
                      + "JOMC ⁑ Modlet\"", "-cp", "."
        };

        final String[] unsupportedOption = new String[]
        {
            "merge-modlets", "--unsupported-option"
        };

        assertEquals( Command.STATUS_SUCCESS, Jomc.run( help ) );
        assertEquals( Command.STATUS_FAILURE, Jomc.run( unsupportedOption ) );

        assertEquals( Command.STATUS_SUCCESS, Jomc.run( args ) );
        Modlet merged = unmarshaller.unmarshal( new StreamSource( targetDocument ), Modlet.class ).getValue();

        assertNotNull( merged );
        assertEquals( "JOMC Tools ⁑ CLI ⁑ Tests", merged.getName() );
        assertNotNull( merged.getSchemas() );
        assertNotNull( merged.getServices() );
        assertEquals( 2, merged.getSchemas().getSchema().size() );
        assertEquals( 6, merged.getServices().getService().size() );
        assertNotNull( merged.getSchemas().getSchemaByPublicId( "http://jomc.org/model" ) );
        assertNotNull( merged.getSchemas().getSchemaByPublicId( "http://jomc.org/tools/model" ) );
        assertEquals( 2, merged.getServices().getServices( "org.jomc.modlet.ModelProvider" ).size() );
        assertEquals( 2, merged.getServices().getServices( "org.jomc.modlet.ModelProcessor" ).size() );
        assertEquals( 2, merged.getServices().getServices( "org.jomc.modlet.ModelValidator" ).size() );

        assertEquals( Command.STATUS_SUCCESS, Jomc.run( includeArgs ) );
        merged = unmarshaller.unmarshal( new StreamSource( targetDocument ), Modlet.class ).getValue();

        assertNotNull( merged );
        assertEquals( "JOMC Tools ⁑ CLI ⁑ Tests", merged.getName() );
        assertNotNull( merged.getSchemas() );
        assertNotNull( merged.getServices() );
        assertEquals( 1, merged.getSchemas().getSchema().size() );
        assertNotNull( merged.getSchemas().getSchemaByPublicId( "http://jomc.org/model" ) );
        assertEquals( 3, merged.getServices().getService().size() );
        assertEquals( 1, merged.getServices().getServices( "org.jomc.modlet.ModelProvider" ).size() );
        assertEquals( 1, merged.getServices().getServices( "org.jomc.modlet.ModelProcessor" ).size() );
        assertEquals( 1, merged.getServices().getServices( "org.jomc.modlet.ModelValidator" ).size() );

        assertEquals( Command.STATUS_SUCCESS, Jomc.run( excludeArgs ) );
        merged = unmarshaller.unmarshal( new StreamSource( targetDocument ), Modlet.class ).getValue();

        assertNotNull( merged );
        assertEquals( "JOMC Tools ⁑ CLI ⁑ Tests", merged.getName() );
        assertNull( merged.getSchemas() );
        assertNull( merged.getServices() );
    }

    @Test
    public final void testShowModel() throws Exception
    {
        final File classesDirectory = new File( this.getOutputDirectory(), "jomc-test-classes" );
        final File targetDocument = new File( this.getOutputDirectory(), "model.xml" );

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
            '"' + targetDocument.getAbsolutePath() + '"'
        };

        final String[] showSpecification = new String[]
        {
            "show-model", "-cp", '"' + classesDirectory.getAbsolutePath() + '"', "-spec",
            "\"JOMC Tools ⁑ CLI ⁑ Command\""
        };

        final String[] writeSpecification = new String[]
        {
            "show-model", "-cp", '"' + classesDirectory.getAbsolutePath() + '"', "-spec",
            "\"JOMC Tools ⁑ CLI ⁑ Command\"", "-d", '"' + targetDocument.getAbsolutePath() + '"'
        };

        final String[] showInstance = new String[]
        {
            "show-model", "-cp", '"' + classesDirectory.getAbsolutePath() + '"',
            "-impl", "\"JOMC Tools ⁑ CLI ⁑ Default show-model Command\""
        };

        final String[] writeInstance = new String[]
        {
            "show-model", "-cp", '"' + classesDirectory.getAbsolutePath() + '"',
            "-impl", "\"JOMC Tools ⁑ CLI ⁑ Default show-model Command\"",
            "-d", '"' + targetDocument.getAbsolutePath() + '"'
        };

        final String[] showSpecificationAndInstance = new String[]
        {
            "show-model", "-cp", '"' + classesDirectory.getAbsolutePath() + '"', "-spec",
            "\"JOMC Tools ⁑ CLI ⁑ Command\"", "-impl", "\"JOMC Tools ⁑ CLI ⁑ Default show-model Command\""
        };

        final String[] writeSpecificationAndInstance = new String[]
        {
            "show-model", "-cp", '"' + classesDirectory.getAbsolutePath() + '"',
            "-spec", "\"JOMC Tools ⁑ CLI ⁑ Command\"",
            "-impl", "\"JOMC Tools ⁑ CLI ⁑ Default show-model Command\"", "-d",
            '"' + targetDocument.getAbsolutePath() + '"'
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
        final File f = this.getOutputDirectory();
        assertTrue( f.exists() || f.mkdirs() );

        final File resourcesDirectory = new File( this.getOutputDirectory(), "resources" );
        assertTrue( resourcesDirectory.isAbsolute() );
        FileUtils.deleteDirectory( resourcesDirectory );
        assertTrue( resourcesDirectory.mkdirs() );

        for ( final String testResourceName : TEST_RESOURCE_NAMES )
        {
            final URL rsrc = this.getClass().getResource( ABSOLUTE_RESOURCE_NAME_PREFIX + testResourceName );
            assertNotNull( rsrc );

            try ( final InputStream in = rsrc.openStream() )
            {
                Files.copy( in, new File( resourcesDirectory, testResourceName ).toPath(),
                            StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES );

            }
        }

        final File classesDirectory = new File( this.getOutputDirectory(), "classes" );
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

        try ( final ZipInputStream in = new ZipInputStream( resource.openStream() ) )
        {
            for ( ZipEntry e = in.getNextEntry(); e != null; e = in.getNextEntry() )
            {
                if ( e.isDirectory() )
                {
                    continue;
                }

                final File dest = new File( targetDirectory, e.getName() );
                assertTrue( dest.isAbsolute() );

                Files.copy( in, dest.toPath(), StandardCopyOption.REPLACE_EXISTING,
                            StandardCopyOption.COPY_ATTRIBUTES );

                in.closeEntry();
            }
        }
    }

}
