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
package org.jomc.cli.test;

import java.io.File;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import junit.framework.TestCase;
import org.apache.commons.io.FileUtils;
import org.jomc.ObjectManagerFactory;
import org.jomc.cli.Command;
import org.jomc.cli.Jomc;
import org.jomc.model.ModelObject;
import org.jomc.model.Module;
import org.jomc.modlet.ModelContext;
import org.jomc.modlet.Modlet;
import org.jomc.modlet.ModletObject;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;

// SECTION-START[Documentation]
// <editor-fold defaultstate="collapsed" desc=" Generated Documentation ">
/**
 * Tests the {@code Jomc} CLI class.
 * <p><b>Properties</b><ul>
 * <li>"{@link #getClassesDirectory classesDirectory}"
 * <blockquote>Property of type {@code java.lang.String}.
 * </blockquote></li>
 * <li>"{@link #getTestClassesDirectory testClassesDirectory}"
 * <blockquote>Property of type {@code java.lang.String}.
 * </blockquote></li>
 * <li>"{@link #getTestIllegalSourceFilesModel testIllegalSourceFilesModel}"
 * <blockquote>Property of type {@code java.lang.String}.
 * </blockquote></li>
 * <li>"{@link #getTestLegalSourceFilesModel testLegalSourceFilesModel}"
 * <blockquote>Property of type {@code java.lang.String}.
 * </blockquote></li>
 * <li>"{@link #getTestModelDocument testModelDocument}"
 * <blockquote>Property of type {@code java.lang.String}.
 * </blockquote></li>
 * <li>"{@link #getTestModelDocumentIllegal testModelDocumentIllegal}"
 * <blockquote>Property of type {@code java.lang.String}.
 * </blockquote></li>
 * <li>"{@link #getTestModelOutputDocument testModelOutputDocument}"
 * <blockquote>Property of type {@code java.lang.String}.
 * </blockquote></li>
 * <li>"{@link #getTestModelStylesheet testModelStylesheet}"
 * <blockquote>Property of type {@code java.lang.String}.
 * </blockquote></li>
 * <li>"{@link #getTestModletName testModletName}"
 * <blockquote>Property of type {@code java.lang.String}.
 * </blockquote></li>
 * <li>"{@link #getTestModletOutputDocument testModletOutputDocument}"
 * <blockquote>Property of type {@code java.lang.String}.
 * </blockquote></li>
 * <li>"{@link #getTestModletStylesheet testModletStylesheet}"
 * <blockquote>Property of type {@code java.lang.String}.
 * </blockquote></li>
 * <li>"{@link #getTestModuleName testModuleName}"
 * <blockquote>Property of type {@code java.lang.String}.
 * </blockquote></li>
 * <li>"{@link #getTestResourcesDirectory testResourcesDirectory}"
 * <blockquote>Property of type {@code java.lang.String}.
 * </blockquote></li>
 * <li>"{@link #getTestShowInstanceOutputDocument testShowInstanceOutputDocument}"
 * <blockquote>Property of type {@code java.lang.String}.
 * </blockquote></li>
 * <li>"{@link #getTestShowModelOutputDocument testShowModelOutputDocument}"
 * <blockquote>Property of type {@code java.lang.String}.
 * </blockquote></li>
 * <li>"{@link #getTestShowSpecificationAndInstanceOutputDocument testShowSpecificationAndInstanceOutputDocument}"
 * <blockquote>Property of type {@code java.lang.String}.
 * </blockquote></li>
 * <li>"{@link #getTestShowSpecificationOutputDocument testShowSpecificationOutputDocument}"
 * <blockquote>Property of type {@code java.lang.String}.
 * </blockquote></li>
 * <li>"{@link #getTestSourcesDirectory testSourcesDirectory}"
 * <blockquote>Property of type {@code java.lang.String}.
 * </blockquote></li>
 * </ul></p>
 *
 * @author <a href="mailto:schulte2005@users.sourceforge.net">Christian Schulte</a> 1.0
 * @version $Id$
 */
// </editor-fold>
// SECTION-END
// SECTION-START[Annotations]
// <editor-fold defaultstate="collapsed" desc=" Generated Annotations ">
@javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.1-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.1.x/jomc-tools" )
// </editor-fold>
// SECTION-END
public class JomcTest extends TestCase
{
    // SECTION-START[JomcTest]

    public void testNoArguments() throws Exception
    {
        assertEquals( Command.STATUS_FAILURE, Jomc.run( new String[ 0 ] ) );
    }

    public void testGenerateResources() throws Exception
    {
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
        assertEquals( Command.STATUS_SUCCESS, Jomc.run( args ) );
        assertEquals( Command.STATUS_FAILURE, Jomc.run( unsupportedOption ) );
        assertEquals( Command.STATUS_FAILURE, Jomc.run( failOnWarnings ) );
    }

    public void testManageSources() throws Exception
    {
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
        assertEquals( Command.STATUS_SUCCESS, Jomc.run( args ) );
        assertEquals( Command.STATUS_FAILURE, Jomc.run( unsupportedOption ) );
        assertEquals( Command.STATUS_FAILURE, Jomc.run( failOnWarnings ) );
    }

    public void testCommitValidateClasses() throws Exception
    {
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
            '"' + this.getTestClassesDirectory() + '"', "-mn", '"' + this.getTestModuleName() + '"', "-D"
        };

        final String[] validateArgs = new String[]
        {
            "validate-classes", "-df", '"' + this.getTestModelDocument() + '"', "-cp",
            '"' + this.getTestClassesDirectory() + '"', "-D"
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
            '"' + this.getTestClassesDirectory() + '"', "-mn", "DOES_NOT_EXIST", "--fail-on-warnings", "-D"
        };

        final String[] validateFailOnWarnings = new String[]
        {
            "validate-classes", "-df", '"' + this.getTestModelDocument() + '"', "-cp",
            '"' + this.getTestClassesDirectory() + '"', "-mn", "DOES_NOT_EXIST", "--fail-on-warnings", "-D"
        };

        FileUtils.copyDirectory( new File( this.getClassesDirectory() ), new File( this.getTestClassesDirectory() ) );

        assertEquals( Command.STATUS_SUCCESS, Jomc.run( commitHelp ) );
        assertEquals( Command.STATUS_SUCCESS, Jomc.run( validateHelp ) );
        assertEquals( Command.STATUS_SUCCESS, Jomc.run( commitArgs ) );
        assertEquals( Command.STATUS_SUCCESS, Jomc.run( validateArgs ) );
        assertEquals( Command.STATUS_FAILURE, Jomc.run( commitUnsupportedOption ) );
        assertEquals( Command.STATUS_FAILURE, Jomc.run( validateUnsupportedOption ) );
        assertEquals( Command.STATUS_FAILURE, Jomc.run( commitFailOnWarnings ) );
        assertEquals( Command.STATUS_FAILURE, Jomc.run( validateFailOnWarnings ) );
    }

    public void testMergeModules() throws Exception
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

    public void testValidateModel() throws Exception
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

    public void testMergeModlets() throws Exception
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
        assertFalse( merged.getSchemas().getSchema().isEmpty() );
        assertFalse( merged.getServices().getService().isEmpty() );

        assertEquals( Command.STATUS_SUCCESS, Jomc.run( includeArgs ) );
        merged = unmarshaller.unmarshal(
            new StreamSource( new File( this.getTestModletOutputDocument() ) ), Modlet.class ).getValue();

        assertNotNull( merged );
        assertEquals( this.getTestModletName(), merged.getName() );
        assertNotNull( merged.getSchemas() );
        assertNotNull( merged.getServices() );
        assertFalse( merged.getSchemas().getSchema().isEmpty() );
        assertFalse( merged.getServices().getService().isEmpty() );

        assertEquals( Command.STATUS_SUCCESS, Jomc.run( excludeArgs ) );
        merged = unmarshaller.unmarshal(
            new StreamSource( new File( this.getTestModletOutputDocument() ) ), Modlet.class ).getValue();

        assertNotNull( merged );
        assertEquals( this.getTestModletName(), merged.getName() );
        assertNull( merged.getSchemas() );
        assertNull( merged.getServices() );
    }

    public void testShowModel() throws Exception
    {
        final String[] help = new String[]
        {
            "show-model", "help"
        };

        final String[] showModel = new String[]
        {
            "show-model", "-cp", '"' + this.getClassesDirectory() + '"'
        };

        final String[] writeModel = new String[]
        {
            "show-model", "-cp", '"' + this.getClassesDirectory() + '"', "-d",
            '"' + this.getTestShowModelOutputDocument() + '"'
        };

        final String[] showSpecification = new String[]
        {
            "show-model", "-cp", '"' + this.getClassesDirectory() + '"', "-spec", "JOMC CLI Command"
        };

        final String[] writeSpecification = new String[]
        {
            "show-model", "-cp", '"' + this.getClassesDirectory() + '"', "-spec", "JOMC CLI Command", "-d",
            '"' + this.getTestShowSpecificationOutputDocument() + '"'
        };

        final String[] showInstance = new String[]
        {
            "show-model", "-cp", '"' + this.getClassesDirectory() + '"', "-impl", "JOMC CLI show-model Command"
        };

        final String[] writeInstance = new String[]
        {
            "show-model", "-cp", '"' + this.getClassesDirectory() + '"', "-impl", "JOMC CLI show-model Command", "-d",
            '"' + this.getTestShowInstanceOutputDocument() + '"'
        };

        final String[] showSpecificationAndInstance = new String[]
        {
            "show-model", "-cp", '"' + this.getClassesDirectory() + '"', "-spec", "JOMC CLI Command", "-impl",
            "JOMC CLI show-model Command"
        };

        final String[] writeSpecificationAndInstance = new String[]
        {
            "show-model", "-cp", '"' + this.getClassesDirectory() + '"', "-spec", "JOMC CLI Command", "-impl",
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

    @Override
    protected void setUp()
    {
        // Ensures the singleton is initialized prior to Jomc switching resource locations.
        ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() );
    }

    // SECTION-END
    // SECTION-START[Constructors]
    // <editor-fold defaultstate="collapsed" desc=" Generated Constructors ">

    /** Creates a new {@code JomcTest} instance. */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.1-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.1.x/jomc-tools" )
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
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.1-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.1.x/jomc-tools" )
    private java.lang.String getClassesDirectory()
    {
        final java.lang.String _p = (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getProperty( this, "classesDirectory" );
        assert _p != null : "'classesDirectory' property not found.";
        return _p;
    }

    /**
     * Gets the value of the {@code testClassesDirectory} property.
     * @return The value of the {@code testClassesDirectory} property.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.1-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.1.x/jomc-tools" )
    private java.lang.String getTestClassesDirectory()
    {
        final java.lang.String _p = (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getProperty( this, "testClassesDirectory" );
        assert _p != null : "'testClassesDirectory' property not found.";
        return _p;
    }

    /**
     * Gets the value of the {@code testIllegalSourceFilesModel} property.
     * @return The value of the {@code testIllegalSourceFilesModel} property.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.1-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.1.x/jomc-tools" )
    private java.lang.String getTestIllegalSourceFilesModel()
    {
        final java.lang.String _p = (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getProperty( this, "testIllegalSourceFilesModel" );
        assert _p != null : "'testIllegalSourceFilesModel' property not found.";
        return _p;
    }

    /**
     * Gets the value of the {@code testLegalSourceFilesModel} property.
     * @return The value of the {@code testLegalSourceFilesModel} property.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.1-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.1.x/jomc-tools" )
    private java.lang.String getTestLegalSourceFilesModel()
    {
        final java.lang.String _p = (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getProperty( this, "testLegalSourceFilesModel" );
        assert _p != null : "'testLegalSourceFilesModel' property not found.";
        return _p;
    }

    /**
     * Gets the value of the {@code testModelDocument} property.
     * @return The value of the {@code testModelDocument} property.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.1-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.1.x/jomc-tools" )
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
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.1-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.1.x/jomc-tools" )
    private java.lang.String getTestModelDocumentIllegal()
    {
        final java.lang.String _p = (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getProperty( this, "testModelDocumentIllegal" );
        assert _p != null : "'testModelDocumentIllegal' property not found.";
        return _p;
    }

    /**
     * Gets the value of the {@code testModelOutputDocument} property.
     * @return The value of the {@code testModelOutputDocument} property.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.1-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.1.x/jomc-tools" )
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
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.1-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.1.x/jomc-tools" )
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
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.1-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.1.x/jomc-tools" )
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
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.1-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.1.x/jomc-tools" )
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
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.1-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.1.x/jomc-tools" )
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
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.1-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.1.x/jomc-tools" )
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
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.1-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.1.x/jomc-tools" )
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
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.1-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.1.x/jomc-tools" )
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
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.1-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.1.x/jomc-tools" )
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
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.1-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.1.x/jomc-tools" )
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
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.1-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.1.x/jomc-tools" )
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
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.1-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.1.x/jomc-tools" )
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
