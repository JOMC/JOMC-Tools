/*
 *   Copyright (C) Christian Schulte, 2005-206
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
package org.jomc.tools.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Locale;
import java.util.Properties;
import java.util.logging.Level;
import org.apache.commons.io.FileUtils;
import org.jomc.model.Argument;
import org.jomc.model.Dependency;
import org.jomc.model.Implementation;
import org.jomc.model.JavaIdentifier;
import org.jomc.model.Message;
import org.jomc.model.ModelObject;
import org.jomc.model.Module;
import org.jomc.model.Modules;
import org.jomc.model.Property;
import org.jomc.model.Specification;
import org.jomc.model.SpecificationReference;
import org.jomc.model.Text;
import org.jomc.model.Texts;
import org.jomc.model.modlet.DefaultModelProvider;
import org.jomc.model.modlet.ModelHelper;
import org.jomc.modlet.Model;
import org.jomc.modlet.ModelContext;
import org.jomc.modlet.ModelContextFactory;
import org.jomc.modlet.ModelException;
import org.jomc.modlet.ModelValidationReport;
import org.jomc.tools.JomcTool;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Test cases for class {@code org.jomc.tools.JomcTool}.
 *
 * @author <a href="mailto:cs@schulte.it">Christian Schulte</a>
 * @version $JOMC$
 */
public class JomcToolTest
{

    /** Constant for the name of the system property holding the name of the encoding of resources backing the test. */
    private static final String RESOURCE_ENCODING_PROPERTY_NAME = "jomc.test.resourceEncoding";

    /** Constant for the name of the system property holding the output directory for the test. */
    private static final String OUTPUT_DIRECTORY_PROPERTY_NAME = "jomc.test.outputDirectory";

    /** The {@code JomcTool} instance tests are performed with. */
    private JomcTool jomcTool;

    /** The {@code ModelContext} of the instance. */
    private ModelContext modelContext;

    /** The {@code Model} of the instance. */
    private Model model;

    /** The name of the encoding to use when reading or writing resources. */
    private String resourceEncoding;

    /** The output directory of the instance. */
    private File outputDirectory;

    /** Serial number of next output directories. */
    private volatile int outputDirectoryId;

    /** Creates a new {@code JomcToolTest} instance. */
    public JomcToolTest()
    {
        super();
    }

    /**
     * Gets the name of the encoding used when reading resources.
     *
     * @return The name of the encoding used when reading resources.
     *
     * @see #setResourceEncoding(java.lang.String)
     */
    public final String getResourceEncoding()
    {
        if ( this.resourceEncoding == null )
        {
            this.resourceEncoding = System.getProperty( RESOURCE_ENCODING_PROPERTY_NAME );
            assertNotNull( "Expected '" + RESOURCE_ENCODING_PROPERTY_NAME + "' system property not found.",
                           this.resourceEncoding );

        }

        return this.resourceEncoding;
    }

    /**
     * Sets the name of the encoding to use when reading resources.
     *
     * @param value The new name of the encoding to use when reading resources or {@code null}.
     *
     * @see #getResourceEncoding()
     */
    public final void setResourceEncoding( final String value )
    {
        this.resourceEncoding = value;
    }

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
            this.outputDirectory = new File( new File( name ), this.getClass().getSimpleName() );
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

    /**
     * Gets the next output directory of the instance.
     *
     * @return The next output directory of the instance.
     */
    public final File getNextOutputDirectory()
    {
        try
        {
            final File nextOutputDirectory =
                new File( this.getOutputDirectory(), Integer.toString( this.outputDirectoryId++ ) );

            assertTrue( nextOutputDirectory.isAbsolute() );
            if ( nextOutputDirectory.exists() )
            {
                FileUtils.deleteDirectory( nextOutputDirectory );
            }

            return nextOutputDirectory;
        }
        catch ( final IOException e )
        {
            throw new AssertionError( e );
        }
    }

    /**
     * Gets the {@code JomcTool} instance tests are performed with.
     *
     * @return The {@code JomcTool} instance tests are performed with.
     *
     * @see #newJomcTool()
     */
    public JomcTool getJomcTool()
    {
        if ( this.jomcTool == null )
        {
            this.jomcTool = this.newJomcTool();
            this.jomcTool.setModel( this.getModel() );
            this.jomcTool.getListeners().add( new JomcTool.Listener()
            {

                @Override
                public void onLog( final Level level, final String message, final Throwable throwable )
                {
                    super.onLog( level, message, throwable );
                    System.out.println( "[" + level.getLocalizedName() + "] " + message );

                    if ( throwable != null )
                    {
                        throwable.printStackTrace( System.out );
                    }
                }

            } );

        }

        return this.jomcTool;
    }

    /**
     * Creates a new {@code JomcTool} instance to test.
     *
     * @return A new {@code JomcTool} instance to test.
     *
     * @see #getJomcTool()
     */
    protected JomcTool newJomcTool()
    {
        return new JomcTool();
    }

    /**
     * Gets the {@code ModelContext} instance backing the test.
     *
     * @return The {@code ModelContext} instance backing the test.
     *
     * @see #newModelContext()
     */
    public ModelContext getModelContext()
    {
        if ( this.modelContext == null )
        {
            this.modelContext = this.newModelContext();
            this.modelContext.getListeners().add( new ModelContext.Listener()
            {

                @Override
                public void onLog( final Level level, String message, Throwable t )
                {
                    super.onLog( level, message, t );
                    System.out.println( "[" + level.getLocalizedName() + "] " + message );

                    if ( t != null )
                    {
                        t.printStackTrace( System.out );
                    }
                }

            } );

        }

        return this.modelContext;
    }

    /**
     * Creates a new {@code ModelContext} instance backing the test.
     *
     * @return A new {@code ModelContext} instance backing the test.
     *
     * @see #getModelContext()
     */
    protected ModelContext newModelContext()
    {
        return ModelContextFactory.newInstance().newModelContext();
    }

    /**
     * Gets the {@code Model} instance backing the test.
     *
     * @return The {@code Model} instance backing the test.
     *
     * @see #newModel()
     */
    public Model getModel()
    {
        if ( this.model == null )
        {
            this.model = this.newModel();
        }

        return this.model;
    }

    /**
     * Creates a new {@code Model} instance backing the test.
     *
     * @return A new {@code Model} instance backing the test.
     *
     * @see #getModel()
     */
    protected Model newModel()
    {
        try
        {
            DefaultModelProvider.setDefaultModuleLocation( this.getClass().getPackage().getName().replace( '.', '/' )
                                                           + "/jomc.xml" );

            Model m = this.getModelContext().findModel( ModelObject.MODEL_PUBLIC_ID );

            if ( m != null )
            {
                final Modules modules = ModelHelper.getModules( m );

                if ( modules != null )
                {
                    final Module cp = modules.getClasspathModule( Modules.getDefaultClasspathModuleName(),
                                                                  this.getClass().getClassLoader() );

                    if ( cp != null )
                    {
                        modules.getModule().add( cp );
                    }
                }

                m = this.getModelContext().processModel( m );

                if ( m != null )
                {
                    final ModelValidationReport validationReport = this.getModelContext().validateModel( m );

                    for ( int i = 0, s0 = validationReport.getDetails().size(); i < s0; i++ )
                    {
                        System.out.println( validationReport.getDetails().get( i ) );
                    }

                    assertTrue( "Unexpected invalid '" + m.getIdentifier() + "' model.",
                                validationReport.isModelValid() );

                }
            }

            return m;
        }
        catch ( final ModelException e )
        {
            throw new AssertionError( e );
        }
        finally
        {
            DefaultModelProvider.setDefaultModuleLocation( null );
        }
    }

    @Test
    @SuppressWarnings( "deprecation" )
    public final void testJomcToolNullPointerException() throws Exception
    {
        assertNotNull( this.getJomcTool() );

        try
        {
            this.getJomcTool().getDisplayLanguage( null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getJomcTool().getImplementedJavaTypeNames( null, false );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getJomcTool().getJavaClasspathLocation( (Implementation) null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getJomcTool().getJavaClasspathLocation( (Specification) null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getJomcTool().getJavaGetterMethodName( (Dependency) null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getJomcTool().getJavaGetterMethodName( (Message) null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getJomcTool().getJavaGetterMethodName( (Property) null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getJomcTool().getJavaMethodParameterName( (Argument) null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getJomcTool().getJavaMethodParameterName( (Dependency) null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getJomcTool().getJavaMethodParameterName( (Message) null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getJomcTool().getJavaMethodParameterName( (Property) null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getJomcTool().getJavaModifierName( null, (Dependency) null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }
        try
        {
            this.getJomcTool().getJavaModifierName( new Implementation(), (Dependency) null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getJomcTool().getJavaModifierName( null, (Message) null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }
        try
        {
            this.getJomcTool().getJavaModifierName( new Implementation(), (Message) null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getJomcTool().getJavaModifierName( null, (Property) null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }
        try
        {
            this.getJomcTool().getJavaModifierName( new Implementation(), (Property) null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getJomcTool().getJavaPackageName( (Implementation) null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getJomcTool().getJavaPackageName( (Specification) null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getJomcTool().getJavaPackageName( (SpecificationReference) null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getJomcTool().getJavaSetterMethodName( (Dependency) null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getJomcTool().getJavaSetterMethodName( (Message) null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getJomcTool().getJavaSetterMethodName( (Property) null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getJomcTool().getJavaTypeName( (Argument) null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getJomcTool().getJavaTypeName( (Dependency) null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getJomcTool().getJavaTypeName( (Implementation) null, true );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getJomcTool().getJavaTypeName( (Property) null, true );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getJomcTool().getJavaTypeName( (Specification) null, true );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getJomcTool().getJavaTypeName( (SpecificationReference) null, true );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getJomcTool().getJavadocComment( (Text) null, 0, "\n" );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }
        try
        {
            this.getJomcTool().getJavadocComment( new Text(), 0, null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }
        try
        {
            this.getJomcTool().getJavadocComment( new Text(), Integer.MIN_VALUE, "\n" );
            fail( "Expected IllegalArgumentException not thrown." );
        }
        catch ( final IllegalArgumentException e )
        {
            assertIllegalArgumentException( e );
        }

        try
        {
            this.getJomcTool().getJavadocComment( (Texts) null, 0, "\n" );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }
        try
        {
            this.getJomcTool().getJavadocComment( new Texts(), 0, null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }
        try
        {
            this.getJomcTool().getJavadocComment( new Texts(), Integer.MIN_VALUE, "\n" );
            fail( "Expected IllegalArgumentException not thrown." );
        }
        catch ( final IllegalArgumentException e )
        {
            assertIllegalArgumentException( e );
        }

        try
        {
            this.getJomcTool().getLongDate( null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getJomcTool().getLongDateTime( null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getJomcTool().getLongTime( null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getJomcTool().getMediumDate( null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getJomcTool().getMediumDateTime( null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getJomcTool().getMediumTime( null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getJomcTool().getShortDate( null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getJomcTool().getShortDateTime( null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getJomcTool().getShortTime( null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getJomcTool().getVelocityTemplate( null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getJomcTool().getYears( null, Calendar.getInstance() );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getJomcTool().getYears( Calendar.getInstance(), null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getJomcTool().isJavaDefaultPackage( (Implementation) null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getJomcTool().isJavaDefaultPackage( (Specification) null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getJomcTool().isJavaPrimitiveType( null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getJomcTool().isLoggable( null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getJomcTool().getIsoDate( null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getJomcTool().getIsoTime( null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getJomcTool().getIsoDateTime( null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getJomcTool().getTemplateEncoding( null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getJomcTool().getParentTemplateProfile( null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }
    }

    @Test
    @SuppressWarnings( "deprecation" )
    public final void testJomcToolNotNull() throws Exception
    {
        final Specification specification = new Specification();
        specification.setClazz( "java.lang.Object" );
        specification.setIdentifier( "java.lang.Object" );

        final Specification defaultPackageSpecification = new Specification();
        defaultPackageSpecification.setClazz( "Object" );
        defaultPackageSpecification.setIdentifier( "Object" );

        final Implementation implementation = new Implementation();
        implementation.setIdentifier( "java.lang.Object" );
        implementation.setName( "java.lang.Object" );
        implementation.setClazz( "java.lang.Object" );

        final Implementation defaultPackageImplementation = new Implementation();
        defaultPackageImplementation.setIdentifier( "Object" );
        defaultPackageImplementation.setName( "Object" );
        defaultPackageImplementation.setClazz( "Object" );

        final Dependency d = new Dependency();
        d.setIdentifier( "java.util.Locale" );
        d.setName( "locale" );
        d.setImplementationName( "default" );

        final Property p = new Property();
        p.setName( "property" );
        p.setValue( "Test" );

        final Message m = new Message();
        m.setName( "message" );

        final Calendar now = Calendar.getInstance();
        final Calendar nextYear = Calendar.getInstance();
        nextYear.set( Calendar.YEAR, nextYear.get( Calendar.YEAR ) + 1 );

        assertNotNull( this.getJomcTool().getListeners() );
        assertNotNull( this.getJomcTool().getInputEncoding() );
        assertNotNull( this.getJomcTool().getModel() );
        assertNotNull( this.getJomcTool().getModules() );
        assertNotNull( this.getJomcTool().getOutputEncoding() );
        assertNotNull( this.getJomcTool().getTemplateProfile() );
        assertNotNull( this.getJomcTool().getDefaultTemplateEncoding() );
        assertNotNull( this.getJomcTool().getTemplateParameters() );
        assertNotNull( this.getJomcTool().getIndentation() );
        assertNotNull( this.getJomcTool().getLineSeparator() );
        assertNotNull( this.getJomcTool().getVelocityContext() );
        assertNotNull( this.getJomcTool().getVelocityEngine() );
        assertNotNull( JomcTool.getDefaultLogLevel() );
        assertNotNull( this.getJomcTool().getLongDate( now ) );
        assertNotNull( this.getJomcTool().getLongDateTime( now ) );
        assertNotNull( this.getJomcTool().getLongTime( now ) );
        assertNotNull( this.getJomcTool().getMediumDate( now ) );
        assertNotNull( this.getJomcTool().getMediumDateTime( now ) );
        assertNotNull( this.getJomcTool().getMediumTime( now ) );
        assertNotNull( this.getJomcTool().getShortDate( now ) );
        assertNotNull( this.getJomcTool().getShortDateTime( now ) );
        assertNotNull( this.getJomcTool().getShortTime( now ) );
        assertNotNull( this.getJomcTool().getIsoDate( now ) );
        assertNotNull( this.getJomcTool().getIsoDateTime( now ) );
        assertNotNull( this.getJomcTool().getIsoTime( now ) );
        assertNotNull( this.getJomcTool().getYears( now, now ) );
        assertNotNull( this.getJomcTool().getYears( now, nextYear ) );
        assertNotNull( this.getJomcTool().getYears( nextYear, now ) );
        assertNotNull( this.getJomcTool().getDisplayLanguage( "en" ) );

        assertEquals( this.getJomcTool().getYears( now, nextYear ), this.getJomcTool().getYears( nextYear, now ) );
        assertEquals( Locale.getDefault().getDisplayLanguage(),
                      this.getJomcTool().getDisplayLanguage( Locale.getDefault().getLanguage() ) );

        assertEquals( "java/lang/Object", this.getJomcTool().getJavaClasspathLocation( implementation ) );
        assertEquals( "Object", this.getJomcTool().getJavaClasspathLocation( defaultPackageImplementation ) );
        assertEquals( "java/lang/Object", this.getJomcTool().getJavaClasspathLocation( specification ) );
        assertEquals( "Object", this.getJomcTool().getJavaClasspathLocation( defaultPackageSpecification ) );
        assertEquals( "getLocale", this.getJomcTool().getJavaGetterMethodName( d ) );
        assertEquals( "getMessage", this.getJomcTool().getJavaGetterMethodName( m ) );
        assertEquals( "getProperty", this.getJomcTool().getJavaGetterMethodName( p ) );
        assertEquals( 0, this.getJomcTool().getImplementedJavaTypeNames( implementation, true ).size() );
        assertEquals( "private", this.getJomcTool().getJavaModifierName( implementation, d ) );
        assertEquals( "private", this.getJomcTool().getJavaModifierName( implementation, m ) );
        assertEquals( "private", this.getJomcTool().getJavaModifierName( implementation, p ) );
        assertEquals( "java.lang", this.getJomcTool().getJavaPackageName( implementation ) );
        assertEquals( "", this.getJomcTool().getJavaPackageName( defaultPackageImplementation ) );
        assertEquals( "java.lang", this.getJomcTool().getJavaPackageName( specification ) );
        assertEquals( "", this.getJomcTool().getJavaPackageName( defaultPackageSpecification ) );
        assertEquals( "java.util", this.getJomcTool().getJavaPackageName( d ) );
        assertEquals( "", this.getJomcTool().getJavaString( "" ) );
        assertEquals( this.getJomcTool().getIndentation(), this.getJomcTool().getIndentation( 1 ) );
        assertEquals( this.getJomcTool().getDefaultTemplateEncoding(),
                      this.getJomcTool().getTemplateEncoding( "DOES_NOT_EXIST" ) );

        assertEquals( this.getJomcTool().getDefaultTemplateProfile(),
                      this.getJomcTool().getParentTemplateProfile( "DOES_NOT_EXIST" ) );

    }

    @Test
    public final void testVelocityTemplates() throws Exception
    {
        assertNotNull( this.getJomcTool().getVelocityTemplate( "Implementation.java.vm" ) );
        this.getJomcTool().setTemplateProfile( "DOES_NOT_EXIST" );
        assertNotNull( this.getJomcTool().getVelocityTemplate( "Implementation.java.vm" ) );
        this.getJomcTool().setTemplateProfile( null );

        try
        {
            this.getJomcTool().getVelocityTemplate( "DOES_NOT_EXIST" );
            fail( "Expected FileNotFoundException not thrown." );
        }
        catch ( final FileNotFoundException e )
        {
            assertNotNull( e.getMessage() );
            System.out.println( e.toString() );
        }

        try
        {
            this.getJomcTool().setTemplateProfile( "DOES_NOT_EXIST" );
            this.getJomcTool().getVelocityTemplate( "DOES_NOT_EXIST" );
            fail( "Expected FileNotFoundException not thrown." );
        }
        catch ( final FileNotFoundException e )
        {
            assertNotNull( e.getMessage() );
            System.out.println( e.toString() );
        }
    }

    @Test
    public final void testDefaultLogLevel() throws Exception
    {
        final String testLogLevel = System.getProperty( "org.jomc.tools.JomcTool.defaultLogLevel" );

        assertNotNull( JomcTool.getDefaultLogLevel() );
        JomcTool.setDefaultLogLevel( null );
        System.setProperty( "org.jomc.tools.JomcTool.defaultLogLevel", "OFF" );
        assertEquals( Level.OFF, JomcTool.getDefaultLogLevel() );

        if ( testLogLevel != null )
        {
            System.setProperty( "org.jomc.tools.JomcTool.defaultLogLevel", testLogLevel );
        }
        else
        {
            System.clearProperty( "org.jomc.tools.JomcTool.defaultLogLevel" );
        }

        JomcTool.setDefaultLogLevel( null );
    }

    @Test
    public final void testLogLevel() throws Exception
    {
        JomcTool.setDefaultLogLevel( null );
        this.getJomcTool().setLogLevel( null );
        assertNotNull( this.getJomcTool().getLogLevel() );

        JomcTool.setDefaultLogLevel( Level.OFF );
        this.getJomcTool().setLogLevel( null );
        assertEquals( Level.OFF, this.getJomcTool().getLogLevel() );

        JomcTool.setDefaultLogLevel( null );
        this.getJomcTool().setLogLevel( null );
    }

    @Test
    public final void testDefaultTemplateProfile() throws Exception
    {
        this.getJomcTool().setDefaultTemplateProfile( null );
        assertNotNull( this.getJomcTool().getDefaultTemplateProfile() );
        this.getJomcTool().setDefaultTemplateProfile( null );
    }

    @Test
    public final void testTemplateProfile() throws Exception
    {
        this.getJomcTool().setDefaultTemplateProfile( null );
        this.getJomcTool().setTemplateProfile( null );
        assertNotNull( this.getJomcTool().getTemplateProfile() );

        this.getJomcTool().setDefaultTemplateProfile( "TEST" );
        this.getJomcTool().setTemplateProfile( null );
        assertEquals( "TEST", this.getJomcTool().getTemplateProfile() );

        this.getJomcTool().setDefaultTemplateProfile( null );
        this.getJomcTool().setTemplateProfile( null );
    }

    @Test
    public final void testIndentation() throws Exception
    {
        assertEquals( "", this.getJomcTool().getIndentation( 0 ) );
        assertEquals( this.getJomcTool().getIndentation(), this.getJomcTool().getIndentation( 1 ) );

        try
        {
            this.getJomcTool().getIndentation( Integer.MIN_VALUE );
            fail( "Expected IllegalArgumentException not thrown." );
        }
        catch ( final IllegalArgumentException e )
        {
            assertIllegalArgumentException( e );
        }

        this.getJomcTool().setIndentation( "    TEST    " );
        assertEquals( "    TEST    ", this.getJomcTool().getIndentation() );
        assertEquals( "    TEST    ", this.getJomcTool().getIndentation( 1 ) );
        this.getJomcTool().setIndentation( null );
    }

    @Test
    public final void testModel() throws Exception
    {
        final Model m = this.getJomcTool().getModel();
        this.getJomcTool().setModel( null );
        assertNotNull( this.getJomcTool().getModel() );
        this.getJomcTool().setModel( m );
    }

    @Test
    public final void testVelocityEngine() throws Exception
    {
        this.getJomcTool().setVelocityEngine( null );
        assertNotNull( this.getJomcTool().getVelocityEngine() );
        this.getJomcTool().setVelocityEngine( null );
    }

    @Test
    public final void testVelocityContext() throws Exception
    {
        assertNotNull( this.getJomcTool().getVelocityContext() );
        this.getJomcTool().setTemplateProfile( "test" );
        assertNotNull( this.getJomcTool().getVelocityContext() );
        assertNotNull( this.getJomcTool().getVelocityContext().get( "test-object" ) );
        assertTrue( this.getJomcTool().getVelocityContext().get( "test-object" ) instanceof JomcTool );
        assertNotNull( this.getJomcTool().getVelocityContext().get( "test-url" ) );
        assertTrue( this.getJomcTool().getVelocityContext().get( "test-url" ) instanceof URL );
        assertEquals( new URL( "file:///tmp" ), this.getJomcTool().getVelocityContext().get( "test-url" ) );
        assertNotNull( this.getJomcTool().getVelocityContext().get( "test-string" ) );
        assertTrue( this.getJomcTool().getVelocityContext().get( "test-string" ) instanceof String );
        assertEquals( "Test", this.getJomcTool().getVelocityContext().get( "test-string" ) );
        this.getJomcTool().setTemplateProfile( null );
    }

    @Test
    public final void testDefaultTemplateEncoding() throws Exception
    {
        this.getJomcTool().setDefaultTemplateEncoding( null );
        assertNotNull( this.getJomcTool().getDefaultTemplateEncoding() );
        this.getJomcTool().setDefaultTemplateEncoding( null );
    }

    @Test
    public final void testTemplateEncoding() throws Exception
    {
        final File templateLocation = this.getNextOutputDirectory();
        File templatesDir = new File( templateLocation, "org" );
        templatesDir = new File( templatesDir, "jomc" );
        templatesDir = new File( templatesDir, "tools" );
        templatesDir = new File( templatesDir, "templates" );
        templatesDir = new File( templatesDir, "tmp" );

        assertTrue( templatesDir.mkdirs() );

        final Properties p = new Properties();
        p.setProperty( "template-encoding", "ISO-8859-1" );

        final OutputStream profileProperties = new FileOutputStream( new File( templatesDir, "profile.properties" ) );
        p.store( profileProperties, this.getClass().getName() );
        profileProperties.close();

        this.getJomcTool().setDefaultTemplateEncoding( null );
        this.getJomcTool().setTemplateLocation( templateLocation.toURI().toURL() );

        assertEquals( "ISO-8859-1", this.getJomcTool().getTemplateEncoding( "tmp" ) );
        assertEquals( "US-ASCII", this.getJomcTool().getTemplateEncoding( "test" ) );
        assertEquals( this.getJomcTool().getDefaultTemplateEncoding(),
                      this.getJomcTool().getTemplateEncoding( "jomc-java-bundles" ) );

        this.getJomcTool().setTemplateLocation( null );
        this.getJomcTool().setDefaultTemplateEncoding( null );
    }

    @Test
    public final void testInputEncoding() throws Exception
    {
        this.getJomcTool().setInputEncoding( null );
        assertNotNull( this.getJomcTool().getInputEncoding() );
        this.getJomcTool().setInputEncoding( null );
    }

    @Test
    public final void testOutputEncoding() throws Exception
    {
        this.getJomcTool().setOutputEncoding( null );
        assertNotNull( this.getJomcTool().getOutputEncoding() );
        this.getJomcTool().setOutputEncoding( null );
    }

    @Test
    public final void testLineSeparator() throws Exception
    {
        this.getJomcTool().setLineSeparator( null );
        assertNotNull( this.getJomcTool().getLineSeparator() );
        this.getJomcTool().setLineSeparator( null );
    }

    @Test
    @SuppressWarnings( "deprecation" )
    public final void testJomcToolModelObjectsNotFound() throws Exception
    {
        final SpecificationReference ref = new SpecificationReference();
        ref.setIdentifier( "DOES_NOT_EXIST" );

        final Implementation i = new Implementation();
        i.setIdentifier( "DOES_NOT_EXSIST" );

        final Dependency d = new Dependency();
        d.setIdentifier( "DOES_NOT_EXIST" );

        final Property p = new Property();
        p.setName( "DOES_NOT_EXIST" );

        assertNull( this.getJomcTool().getJavaPackageName( ref ) );
        assertNull( this.getJomcTool().getJavaTypeName( ref, false ) );
        assertNull( this.getJomcTool().getJavaTypeName( d ) );

        final Model oldModel = this.getJomcTool().getModel();
        this.getJomcTool().setModel( null );
        assertTrue( this.getJomcTool().getImplementedJavaTypeNames( i, true ).isEmpty() );
        assertEquals( "private", this.getJomcTool().getJavaModifierName( i, p ) );
        this.getJomcTool().setModel( oldModel );
    }

    @Test
    @SuppressWarnings( "deprecation" )
    public final void testJavaIdentifier() throws Exception
    {
        assertEquals( "", this.getJomcTool().getJavaIdentifier( "", true ) );
        assertEquals( "", this.getJomcTool().getJavaIdentifier( "", false ) );
        assertEquals( "", this.getJomcTool().getJavaIdentifier( "  ", true ) );
        assertEquals( "", this.getJomcTool().getJavaIdentifier( "  ", false ) );
        assertEquals( "testTestTest", this.getJomcTool().getJavaIdentifier( "  test test test  ", false ) );
        assertEquals( "testTestTest", this.getJomcTool().getJavaIdentifier( "  test  test  test  ", false ) );
        assertEquals( "TestTestTest", this.getJomcTool().getJavaIdentifier( "  test test test  ", true ) );
        assertEquals( "TestTestTest", this.getJomcTool().getJavaIdentifier( "  test  test  test  ", true ) );
        assertEquals( "testTestTest", this.getJomcTool().getJavaIdentifier( "  Test test test  ", false ) );
        assertEquals( "testTestTest", this.getJomcTool().getJavaIdentifier( "  Test  test  test  ", false ) );
        assertEquals( "TestTestTest", this.getJomcTool().getJavaIdentifier( "  test test test  ", true ) );
        assertEquals( "TestTestTest", this.getJomcTool().getJavaIdentifier( "  test  test  test  ", true ) );
    }

    @Test
    @SuppressWarnings( "deprecation" )
    public final void testJavaConstantName() throws Exception
    {
        assertEquals( "", this.getJomcTool().getJavaConstantName( "" ) );
        assertEquals( "", this.getJomcTool().getJavaConstantName( "  " ) );
        assertEquals( "TEST_TEST_TEST", this.getJomcTool().getJavaConstantName( "  test test test  " ) );
        assertEquals( "TEST_TEST_TEST", this.getJomcTool().getJavaConstantName( "  test  test  test  " ) );
        assertEquals( "TEST_T_EST_TE_ST_TES_T",
                      this.getJomcTool().getJavaConstantName( "  Test   tEst   teSt   tesT  " ) );

    }

    @Test
    @SuppressWarnings( "deprecation" )
    public final void testJavaFieldName() throws Exception
    {
        assertEquals( "", this.getJomcTool().getJavaFieldName( "" ) );
        assertEquals( "", this.getJomcTool().getJavaFieldName( "  " ) );
        assertEquals( "testTestTest", this.getJomcTool().getJavaFieldName( "  test test test  " ) );
        assertEquals( "testTestTest", this.getJomcTool().getJavaFieldName( "  test  test  test  " ) );
        assertEquals( "testTEstTeStTesT", this.getJomcTool().getJavaFieldName( "  Test tEst teSt tesT  " ) );
        assertEquals( "testTEstTeStTesT", this.getJomcTool().getJavaFieldName( "  Test  tEst  teSt  tesT  " ) );
        assertEquals( "_package", this.getJomcTool().getJavaFieldName( "  Package " ) );
        assertEquals( "_new", this.getJomcTool().getJavaFieldName( "  New " ) );
    }

    @Test
    @SuppressWarnings( "deprecation" )
    public final void testJavaMethodParameterName() throws Exception
    {
        assertEquals( "", this.getJomcTool().getJavaMethodParameterName( "" ) );
        assertEquals( "", this.getJomcTool().getJavaMethodParameterName( "  " ) );
        assertEquals( "testTestTest", this.getJomcTool().getJavaMethodParameterName( "  test test test  " ) );
        assertEquals( "testTEstTeStTesT", this.getJomcTool().getJavaMethodParameterName( "  Test tEst teSt tesT  " ) );
        assertEquals( "testTEstTeStTesT",
                      this.getJomcTool().getJavaMethodParameterName( "  Test  tEst  teSt  tesT  " ) );

        assertEquals( "_package", this.getJomcTool().getJavaMethodParameterName( "  Package " ) );
        assertEquals( "_new", this.getJomcTool().getJavaMethodParameterName( "  New " ) );
    }

    @Test
    public final void testToJavaConstantName() throws Exception
    {
        try
        {
            this.getJomcTool().toJavaConstantName( "" );
            fail( "Expected 'ParseException' not thrown." );
        }
        catch ( final ParseException e )
        {
            System.out.println( e.toString() );
            assertNotNull( e.getMessage() );
        }
        try
        {
            this.getJomcTool().toJavaConstantName( "  " );
            fail( "Expected 'ParseException' not thrown." );
        }
        catch ( final ParseException e )
        {
            System.out.println( e.toString() );
            assertNotNull( e.getMessage() );
        }

        assertEquals( JavaIdentifier.valueOf( "TEST_TEST_TEST" ),
                      this.getJomcTool().toJavaConstantName( "  test test test  " ) );

        assertEquals( JavaIdentifier.valueOf( "TEST_TEST_TEST_TEST" ),
                      this.getJomcTool().toJavaConstantName( "  Test tEst teSt tesT  " ) );

        assertEquals( JavaIdentifier.valueOf( "TEST_TEST_TEST_TEST" ),
                      this.getJomcTool().toJavaConstantName( "  Test  tEst  teSt  tesT  " ) );

        assertEquals( JavaIdentifier.valueOf( "PACKAGE" ), this.getJomcTool().toJavaConstantName( "  Package " ) );
        assertEquals( JavaIdentifier.valueOf( "NEW" ), this.getJomcTool().toJavaConstantName( "  New " ) );
    }

    @Test
    public final void testToJavaMethodName() throws Exception
    {
        try
        {
            this.getJomcTool().toJavaMethodName( "" );
            fail( "Expected 'ParseException' not thrown." );
        }
        catch ( final ParseException e )
        {
            System.out.println( e.toString() );
            assertNotNull( e.getMessage() );
        }
        try
        {
            this.getJomcTool().toJavaMethodName( "  " );
            fail( "Expected 'ParseException' not thrown." );
        }
        catch ( final ParseException e )
        {
            System.out.println( e.toString() );
            assertNotNull( e.getMessage() );
        }

        assertEquals( JavaIdentifier.valueOf( "testTestTest" ),
                      this.getJomcTool().toJavaMethodName( "  test test test  " ) );

        assertEquals( JavaIdentifier.valueOf( "testTestTestTest" ),
                      this.getJomcTool().toJavaMethodName( "  Test tEst teSt tesT  " ) );

        assertEquals( JavaIdentifier.valueOf( "testTestTestTest" ),
                      this.getJomcTool().toJavaMethodName( "  Test  tEst  teSt  tesT  " ) );

        assertEquals( JavaIdentifier.valueOf( "_package" ), this.getJomcTool().toJavaMethodName( "  Package " ) );
        assertEquals( JavaIdentifier.valueOf( "_new" ), this.getJomcTool().toJavaMethodName( "  New " ) );
    }

    @Test
    public final void testToJavaVariableName() throws Exception
    {
        try
        {
            this.getJomcTool().toJavaVariableName( "" );
            fail( "Expected 'ParseException' not thrown." );
        }
        catch ( final ParseException e )
        {
            System.out.println( e.toString() );
            assertNotNull( e.getMessage() );
        }
        try
        {
            this.getJomcTool().toJavaVariableName( "  " );
            fail( "Expected 'ParseException' not thrown." );
        }
        catch ( final ParseException e )
        {
            System.out.println( e.toString() );
            assertNotNull( e.getMessage() );
        }

        assertEquals( JavaIdentifier.valueOf( "testTestTest" ),
                      this.getJomcTool().toJavaVariableName( "  test test test  " ) );

        assertEquals( JavaIdentifier.valueOf( "testTestTestTest" ),
                      this.getJomcTool().toJavaVariableName( "  Test tEst teSt tesT  " ) );

        assertEquals( JavaIdentifier.valueOf( "testTestTestTest" ),
                      this.getJomcTool().toJavaVariableName( "  Test  tEst  teSt  tesT  " ) );

        assertEquals( JavaIdentifier.valueOf( "_package" ), this.getJomcTool().toJavaVariableName( "  Package " ) );
        assertEquals( JavaIdentifier.valueOf( "_new" ), this.getJomcTool().toJavaVariableName( "  New " ) );
    }

    @Test
    public final void testParentTemplateProfile() throws Exception
    {
        final File templateLocation = this.getNextOutputDirectory();
        File templatesDir = new File( templateLocation, "org" );
        templatesDir = new File( templatesDir, "jomc" );
        templatesDir = new File( templatesDir, "tools" );
        templatesDir = new File( templatesDir, "templates" );
        templatesDir = new File( templatesDir, "tmp" );

        assertTrue( templatesDir.mkdirs() );

        final Properties p = new Properties();
        p.setProperty( "parent-template-profile", "test" );

        final OutputStream profileProperties = new FileOutputStream( new File( templatesDir, "profile.properties" ) );
        p.store( profileProperties, this.getClass().getName() );
        profileProperties.close();

        this.getJomcTool().setDefaultTemplateProfile( null );
        this.getJomcTool().setTemplateLocation( templateLocation.toURI().toURL() );

        assertEquals( "test", this.getJomcTool().getParentTemplateProfile( "tmp" ) );
        assertEquals( "jomc-java-bundles", this.getJomcTool().getParentTemplateProfile( "test" ) );
        assertEquals( this.getJomcTool().getDefaultTemplateProfile(),
                      this.getJomcTool().getParentTemplateProfile( "jomc-java-bundles" ) );

        assertNull( this.getJomcTool().getParentTemplateProfile( this.getJomcTool().getDefaultTemplateProfile() ) );
        this.getJomcTool().setTemplateLocation( null );
        this.getJomcTool().setDefaultTemplateEncoding( null );
    }

    public static void assertNullPointerException( final NullPointerException e )
    {
        assertNotNull( e );
        assertNotNull( e.getMessage() );
        System.out.println( e.toString() );
    }

    public static void assertIllegalArgumentException( final IllegalArgumentException e )
    {
        assertNotNull( e );
        assertNotNull( e.getMessage() );
        System.out.println( e.toString() );
    }

}
