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
import junit.framework.Assert;
import junit.framework.TestCase;
import org.apache.commons.io.FileUtils;
import org.jomc.ObjectManagerFactory;
import org.jomc.cli.Command;
import org.jomc.cli.Jomc;
import org.jomc.model.ModelContext;
import org.jomc.model.Module;

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
 * <li>"{@link #getTestDocument testDocument}"
 * <blockquote>Property of type {@code java.lang.String}.
 * </blockquote></li>
 * <li>"{@link #getTestDocumentIllegal testDocumentIllegal}"
 * <blockquote>Property of type {@code java.lang.String}.
 * </blockquote></li>
 * <li>"{@link #getTestIllegalSourceFilesModel testIllegalSourceFilesModel}"
 * <blockquote>Property of type {@code java.lang.String}.
 * </blockquote></li>
 * <li>"{@link #getTestLegalSourceFilesModel testLegalSourceFilesModel}"
 * <blockquote>Property of type {@code java.lang.String}.
 * </blockquote></li>
 * <li>"{@link #getTestModuleName testModuleName}"
 * <blockquote>Property of type {@code java.lang.String}.
 * </blockquote></li>
 * <li>"{@link #getTestOutputDocument testOutputDocument}"
 * <blockquote>Property of type {@code java.lang.String}.
 * </blockquote></li>
 * <li>"{@link #getTestResourcesDirectory testResourcesDirectory}"
 * <blockquote>Property of type {@code java.lang.String}.
 * </blockquote></li>
 * <li>"{@link #getTestSourcesDirectory testSourcesDirectory}"
 * <blockquote>Property of type {@code java.lang.String}.
 * </blockquote></li>
 * <li>"{@link #getTestStylesheet testStylesheet}"
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
@javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor", comments = "See http://jomc.sourceforge.net/jomc/1.0-beta-4-SNAPSHOT/jomc-tools" )
// </editor-fold>
// SECTION-END
public class JomcTest extends TestCase
{
    // SECTION-START[JomcTest]

    public void testNoArguments() throws Exception
    {
        Assert.assertEquals( Command.STATUS_FAILURE, Jomc.run( new String[ 0 ] ) );
    }

    public void testGenerateJavaBundles() throws Exception
    {
        final String[] help = new String[]
        {
            "generate-resources", "help"
        };

        final String[] args = new String[]
        {
            "generate-resources", "-rd", '"' + this.getTestResourcesDirectory() + '"', "-df",
            '"' + this.getTestDocument() + '"', "-D"
        };

        final String[] unsupportedOption = new String[]
        {
            "generate-resources", "--unsupported-option"
        };

        final String[] failOnWarnings = new String[]
        {
            "generate-resources", "-rd", '"' + this.getTestResourcesDirectory() + '"', "-df",
            '"' + this.getTestDocument() + '"', "-mn", "DOES_NOT_EXIST", "--fail-on-warnings", "-D"
        };

        Assert.assertEquals( Command.STATUS_SUCCESS, Jomc.run( help ) );
        Assert.assertEquals( Command.STATUS_SUCCESS, Jomc.run( args ) );
        Assert.assertEquals( Command.STATUS_FAILURE, Jomc.run( unsupportedOption ) );
        Assert.assertEquals( Command.STATUS_FAILURE, Jomc.run( failOnWarnings ) );
    }

    public void testManageJavaSources() throws Exception
    {
        final String[] help = new String[]
        {
            "manage-sources", "help"
        };

        final String[] args = new String[]
        {
            "manage-sources", "-sd", '"' + this.getTestSourcesDirectory() + '"', "-df",
            '"' + this.getTestDocument() + '"', "-mn", '"' + this.getTestModuleName() + '"', "-D",
            "-ls", "dos", "-idt", "\t"
        };

        final String[] unsupportedOption = new String[]
        {
            "manage-sources", "--unsupported-option"
        };

        final String[] failOnWarnings = new String[]
        {
            "manage-sources", "-sd", '"' + this.getTestSourcesDirectory() + '"', "-df",
            '"' + this.getTestDocument() + '"', "-mn", "DOES_NOT_EXIST", "--fail-on-warnings", "-D"
        };

        Assert.assertEquals( Command.STATUS_SUCCESS, Jomc.run( help ) );
        Assert.assertEquals( Command.STATUS_SUCCESS, Jomc.run( args ) );
        Assert.assertEquals( Command.STATUS_FAILURE, Jomc.run( unsupportedOption ) );
        Assert.assertEquals( Command.STATUS_FAILURE, Jomc.run( failOnWarnings ) );
    }

    public void testCommitValidateJavaClasses() throws Exception
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
            "commit-classes", "-df", '"' + this.getTestDocument() + '"', "-cd",
            '"' + this.getTestClassesDirectory() + '"', "-mn", '"' + this.getTestModuleName() + '"', "-D"
        };

        final String[] validateArgs = new String[]
        {
            "validate-classes", "-df", '"' + this.getTestDocument() + '"', "-cp",
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
            "commit-classes", "-df", '"' + this.getTestDocument() + '"', "-cd",
            '"' + this.getTestClassesDirectory() + '"', "-mn", "DOES_NOT_EXIST", "--fail-on-warnings", "-D"
        };

        final String[] validateFailOnWarnings = new String[]
        {
            "validate-classes", "-df", '"' + this.getTestDocument() + '"', "-cp",
            '"' + this.getTestClassesDirectory() + '"', "-mn", "DOES_NOT_EXIST", "--fail-on-warnings", "-D"
        };

        FileUtils.copyDirectory( new File( this.getClassesDirectory() ), new File( this.getTestClassesDirectory() ) );

        Assert.assertEquals( Command.STATUS_SUCCESS, Jomc.run( commitHelp ) );
        Assert.assertEquals( Command.STATUS_SUCCESS, Jomc.run( validateHelp ) );
        Assert.assertEquals( Command.STATUS_SUCCESS, Jomc.run( commitArgs ) );
        Assert.assertEquals( Command.STATUS_SUCCESS, Jomc.run( validateArgs ) );
        Assert.assertEquals( Command.STATUS_FAILURE, Jomc.run( commitUnsupportedOption ) );
        Assert.assertEquals( Command.STATUS_FAILURE, Jomc.run( validateUnsupportedOption ) );
        Assert.assertEquals( Command.STATUS_FAILURE, Jomc.run( commitFailOnWarnings ) );
        Assert.assertEquals( Command.STATUS_FAILURE, Jomc.run( validateFailOnWarnings ) );
    }

    public void testMergeModules() throws Exception
    {
        final ModelContext context = ModelContext.createModelContext( this.getClass().getClassLoader() );
        final Unmarshaller unmarshaller = context.createUnmarshaller();
        final Schema schema = context.createSchema();
        unmarshaller.setSchema( schema );

        final String[] help = new String[]
        {
            "merge-modules", "help"
        };

        final String[] args = new String[]
        {
            "merge-modules", "-df", '"' + this.getTestDocument() + '"', "-xs", '"' + this.getTestStylesheet() + '"',
            "-mn", '"' + this.getTestModuleName() + '"', "-d", '"' + this.getTestOutputDocument() + '"', "-D"
        };

        final String[] includesArg = new String[]
        {
            "merge-modules", "-df", '"' + this.getTestDocument() + '"', "-xs", '"' + this.getTestStylesheet() + '"',
            "-mn", '"' + this.getTestModuleName() + '"', "-d", '"' + this.getTestOutputDocument() + '"',
            "-minc", "\"JOMC CLI\"", "-D"
        };

        final String[] excludesArg = new String[]
        {
            "merge-modules", "-df", '"' + this.getTestDocument() + '"', "-xs", '"' + this.getTestStylesheet() + '"',
            "-mn", '"' + this.getTestModuleName() + '"', "-d", '"' + this.getTestOutputDocument() + '"',
            "-mexc", "\"JOMC CLI\"", "-D"
        };

        final String[] unsupportedOption = new String[]
        {
            "merge-modules", "--unsupported-option"
        };

        final String[] illegalDoc = new String[]
        {
            "merge-modules", "-df", '"' + this.getTestDocumentIllegal() + '"', "-xs",
            '"' + this.getTestStylesheet() + '"', "-mn", '"' + this.getTestModuleName() + '"', "-d",
            '"' + this.getTestOutputDocument() + '"', "-D"
        };

        Assert.assertEquals( Command.STATUS_SUCCESS, Jomc.run( help ) );
        Assert.assertEquals( Command.STATUS_SUCCESS, Jomc.run( args ) );

        unmarshaller.unmarshal( new StreamSource( new File( this.getTestOutputDocument() ) ), Module.class );

        Assert.assertEquals( Command.STATUS_SUCCESS, Jomc.run( includesArg ) );

        final JAXBElement<Module> includedModule =
            unmarshaller.unmarshal( new StreamSource( new File( this.getTestOutputDocument() ) ), Module.class );

        Assert.assertNotNull( "Merged module does not contain any included specifications.",
                              includedModule.getValue().getSpecifications() );

        Assert.assertNotNull( "Merged module does not contain included 'org.jomc.cli.Command' specification.",
                              includedModule.getValue().getSpecifications().getSpecification( Command.class ) );

        Assert.assertEquals( Command.STATUS_SUCCESS, Jomc.run( excludesArg ) );

        final JAXBElement<Module> excludedModule =
            unmarshaller.unmarshal( new StreamSource( new File( this.getTestOutputDocument() ) ), Module.class );

        Assert.assertNull( "Merged module contains excluded specifications.",
                           excludedModule.getValue().getSpecifications() );

        Assert.assertEquals( Command.STATUS_FAILURE, Jomc.run( unsupportedOption ) );
        Assert.assertEquals( Command.STATUS_FAILURE, Jomc.run( illegalDoc ) );
    }

    public void testValidateModules() throws Exception
    {
        final String[] help = new String[]
        {
            "validate-modules", "help"
        };

        final String[] args = new String[]
        {
            "validate-modules", "-df", '"' + this.getTestDocument() + '"', "-D"
        };

        final String[] unsupportedOption = new String[]
        {
            "validate-modules", "--unsupported-option"
        };

        final String[] illegalDoc = new String[]
        {
            "validate-modules", "-df", '"' + this.getTestDocumentIllegal() + '"', "-D"
        };

        Assert.assertEquals( Command.STATUS_SUCCESS, Jomc.run( help ) );
        Assert.assertEquals( Command.STATUS_SUCCESS, Jomc.run( args ) );
        Assert.assertEquals( Command.STATUS_FAILURE, Jomc.run( unsupportedOption ) );
        Assert.assertEquals( Command.STATUS_FAILURE, Jomc.run( illegalDoc ) );
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
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor", comments = "See http://jomc.sourceforge.net/jomc/1.0-beta-4-SNAPSHOT/jomc-tools" )
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
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor", comments = "See http://jomc.sourceforge.net/jomc/1.0-beta-4-SNAPSHOT/jomc-tools" )
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
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor", comments = "See http://jomc.sourceforge.net/jomc/1.0-beta-4-SNAPSHOT/jomc-tools" )
    private java.lang.String getTestClassesDirectory()
    {
        final java.lang.String _p = (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getProperty( this, "testClassesDirectory" );
        assert _p != null : "'testClassesDirectory' property not found.";
        return _p;
    }

    /**
     * Gets the value of the {@code testDocument} property.
     * @return The value of the {@code testDocument} property.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor", comments = "See http://jomc.sourceforge.net/jomc/1.0-beta-4-SNAPSHOT/jomc-tools" )
    private java.lang.String getTestDocument()
    {
        final java.lang.String _p = (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getProperty( this, "testDocument" );
        assert _p != null : "'testDocument' property not found.";
        return _p;
    }

    /**
     * Gets the value of the {@code testDocumentIllegal} property.
     * @return The value of the {@code testDocumentIllegal} property.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor", comments = "See http://jomc.sourceforge.net/jomc/1.0-beta-4-SNAPSHOT/jomc-tools" )
    private java.lang.String getTestDocumentIllegal()
    {
        final java.lang.String _p = (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getProperty( this, "testDocumentIllegal" );
        assert _p != null : "'testDocumentIllegal' property not found.";
        return _p;
    }

    /**
     * Gets the value of the {@code testIllegalSourceFilesModel} property.
     * @return The value of the {@code testIllegalSourceFilesModel} property.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor", comments = "See http://jomc.sourceforge.net/jomc/1.0-beta-4-SNAPSHOT/jomc-tools" )
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
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor", comments = "See http://jomc.sourceforge.net/jomc/1.0-beta-4-SNAPSHOT/jomc-tools" )
    private java.lang.String getTestLegalSourceFilesModel()
    {
        final java.lang.String _p = (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getProperty( this, "testLegalSourceFilesModel" );
        assert _p != null : "'testLegalSourceFilesModel' property not found.";
        return _p;
    }

    /**
     * Gets the value of the {@code testModuleName} property.
     * @return The value of the {@code testModuleName} property.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor", comments = "See http://jomc.sourceforge.net/jomc/1.0-beta-4-SNAPSHOT/jomc-tools" )
    private java.lang.String getTestModuleName()
    {
        final java.lang.String _p = (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getProperty( this, "testModuleName" );
        assert _p != null : "'testModuleName' property not found.";
        return _p;
    }

    /**
     * Gets the value of the {@code testOutputDocument} property.
     * @return The value of the {@code testOutputDocument} property.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor", comments = "See http://jomc.sourceforge.net/jomc/1.0-beta-4-SNAPSHOT/jomc-tools" )
    private java.lang.String getTestOutputDocument()
    {
        final java.lang.String _p = (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getProperty( this, "testOutputDocument" );
        assert _p != null : "'testOutputDocument' property not found.";
        return _p;
    }

    /**
     * Gets the value of the {@code testResourcesDirectory} property.
     * @return The value of the {@code testResourcesDirectory} property.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor", comments = "See http://jomc.sourceforge.net/jomc/1.0-beta-4-SNAPSHOT/jomc-tools" )
    private java.lang.String getTestResourcesDirectory()
    {
        final java.lang.String _p = (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getProperty( this, "testResourcesDirectory" );
        assert _p != null : "'testResourcesDirectory' property not found.";
        return _p;
    }

    /**
     * Gets the value of the {@code testSourcesDirectory} property.
     * @return The value of the {@code testSourcesDirectory} property.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor", comments = "See http://jomc.sourceforge.net/jomc/1.0-beta-4-SNAPSHOT/jomc-tools" )
    private java.lang.String getTestSourcesDirectory()
    {
        final java.lang.String _p = (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getProperty( this, "testSourcesDirectory" );
        assert _p != null : "'testSourcesDirectory' property not found.";
        return _p;
    }

    /**
     * Gets the value of the {@code testStylesheet} property.
     * @return The value of the {@code testStylesheet} property.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor", comments = "See http://jomc.sourceforge.net/jomc/1.0-beta-4-SNAPSHOT/jomc-tools" )
    private java.lang.String getTestStylesheet()
    {
        final java.lang.String _p = (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getProperty( this, "testStylesheet" );
        assert _p != null : "'testStylesheet' property not found.";
        return _p;
    }
    // </editor-fold>
    // SECTION-END
    // SECTION-START[Messages]
    // SECTION-END
}
