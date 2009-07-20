/*
 *   Copyright (c) 2009 The JOMC Project
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
package org.jomc.tools.cli;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.MissingArgumentException;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.lang.StringUtils;
import org.jomc.model.DefaultModelManager;
import org.jomc.model.ModelException;
import org.jomc.model.Module;
import org.jomc.tools.JavaBundles;
import org.jomc.tools.JavaClasses;
import org.jomc.tools.JavaSources;
import org.jomc.tools.JomcTool;
import org.jomc.tools.ModuleAssembler;
import org.xml.sax.SAXException;

/**
 * Object management and configuration tools command line interface.
 *
 * @author <a href="mailto:schulte2005@users.sourceforge.net">Christian Schulte</a>
 * @version $Id$
 */
public class JomcToolsCli
{

    /** Logger names. */
    private static final String MANAGER_LOGGER = "ModelManager";

    private static final String CLI_LOGGER = "JomcTools";

    /** Option taking a tool to execute. */
    private static final Option TOOL_OPTION = new Option( "t", "tool", true, getMessage( "tool", null ) );

    private static final String BUNDLES_TOOL = "bundles";

    private static final String SOURCES_TOOL = "sources";

    private static final String MERGE_TOOL = "merge";

    private static final String CLASSES_TOOL = "classes";

    /** Option taking a module name to process. */
    private static final Option MODULE_OPTION = new Option( "m", "module", true, getMessage( "module", null ) );

    /** Option taking a language to use when processing. */
    private static final Option LANGUAGE_OPTION =
        new Option( "l", "language", true, getMessage( "language", new Object[]
        {
            Locale.getDefault().getDisplayLanguage()
        } ) );

    /** Option taking an encoding to use for reading templates. */
    private static final Option TEMPLATE_ENCODING_OPTION =
        new Option( "T", "template-encoding", true, getMessage( "templateEncoding", null ) );

    /** Option taking an encoding to use for reading files. */
    private static final Option INPUT_ENCODING_OPTION =
        new Option( "I", "input-encoding", true, getMessage( "inputEncoding", null ) );

    /** Option taking an encoding to use for writing files. */
    private static final Option OUTPUT_ENCODING_OPTION =
        new Option( "O", "output-encoding", true, getMessage( "outputEncoding", null ) );

    /** Option taking a directory name to write source files to. */
    private static final Option SOURCES_OPTION = new Option( "s", "source-dir", true, getMessage( "sources", null ) );

    /** Option taking a directory name to write resource files to. */
    private static final Option RESOURCES_OPTION =
        new Option( "r", "resource-dir", true, getMessage( "resources", null ) );

    /** Option taking a directory name to write build files to. */
    private static final Option BUILD_OPTION = new Option( "b", "build-dir", true, getMessage( "build", null ) );

    /** Option taking a directory name classfiles are stored in. */
    private static final Option CLASSES_OPTION =
        new Option( "c", "classes-dir", true, getMessage( "classes", null ) );

    /** Option to request the tool's help. */
    private static final Option HELP_OPTION = new Option( "h", "help", false, getMessage( "help", null ) );

    /** Option indicating verbose output. */
    private static final Option VERBOSE_OPTION = new Option( "v", "verbose", false, getMessage( "verbose", null ) );

    /** Option indicating debug output. */
    private static final Option DEBUG_OPTION = new Option( "D", "debug", false, getMessage( "debug", null ) );

    /** Option taking a list of classpath elements separated by ':'.  */
    private static final Option CLASSPATH_OPTION =
        new Option( "cp", "classpath", true, getMessage( "classpath", null ) );

    /** Option taking a list of document filenames separated by ':'.  */
    private static final Option MODULES_OPTION =
        new Option( "M", "modules", true, getMessage( "modules", null ) );

    /** Option to select templates.  */
    private static final Option PROFILE_OPTION = new Option( "p", "profile", true, getMessage( "profile", new Object[]
        {
            "default"
        } ) );

    /** Option taking a file name to write any document to.  */
    private static final Option DOCUMENT_OPTION = new Option( "d", "document", true, getMessage( "document", null ) );

    /** Options supported by the command line interface. */
    private static final Options OPTIONS = new Options();

    /** Constant of the exit code indicating success. */
    private static final int EXIT_OK = 0;

    /** Constant of the exit code indicating failure. */
    private static final int EXIT_FAILURE = 1;

    static
    {
        OPTIONS.addOption( TOOL_OPTION );
        OPTIONS.addOption( MODULE_OPTION );
        OPTIONS.addOption( LANGUAGE_OPTION );
        OPTIONS.addOption( TEMPLATE_ENCODING_OPTION );
        OPTIONS.addOption( INPUT_ENCODING_OPTION );
        OPTIONS.addOption( OUTPUT_ENCODING_OPTION );
        OPTIONS.addOption( VERBOSE_OPTION );
        OPTIONS.addOption( DEBUG_OPTION );
        OPTIONS.addOption( SOURCES_OPTION );
        OPTIONS.addOption( RESOURCES_OPTION );
        OPTIONS.addOption( BUILD_OPTION );
        OPTIONS.addOption( CLASSES_OPTION );
        OPTIONS.addOption( HELP_OPTION );
        OPTIONS.addOption( CLASSPATH_OPTION );
        OPTIONS.addOption( PROFILE_OPTION );
        OPTIONS.addOption( DOCUMENT_OPTION );
        OPTIONS.addOption( MODULES_OPTION );
    }

    public static void main( final String[] args )
    {
        int exitCode = EXIT_FAILURE;
        CommandLine cmd = null;

        try
        {
            final CommandLineParser cli = new GnuParser();
            cmd = cli.parse( OPTIONS, args );

            log( cmd, Level.INFO, null, getMessage( "separator", null ), null );
            log( cmd, Level.INFO, null, getMessage( "title", null ), null );
            log( cmd, Level.INFO, null, getMessage( "separator", null ), null );

            if ( cmd.hasOption( HELP_OPTION.getOpt() ) )
            {
                logHelp( cmd );
                exit( EXIT_OK, cmd );
            }

            assertOption( TOOL_OPTION, cmd );

            final ClassLoader classLoader = getClasspathOption( cmd );

            if ( BUNDLES_TOOL.equals( cmd.getOptionValue( TOOL_OPTION.getOpt() ) ) )
            {
                assertOption( MODULE_OPTION, cmd );
                assertOption( SOURCES_OPTION, cmd );
                assertOption( RESOURCES_OPTION, cmd );

                final JavaBundles command = new JavaBundles();
                setupJavaBundles( command, classLoader, cmd );

                command.writeModuleBundles( new File( cmd.getOptionValue( SOURCES_OPTION.getOpt() ) ),
                                            new File( cmd.getOptionValue( RESOURCES_OPTION.getOpt() ) ) );

                exitCode = EXIT_OK;
            }
            else if ( SOURCES_TOOL.equals( cmd.getOptionValue( TOOL_OPTION.getOpt() ) ) )
            {
                assertOption( MODULE_OPTION, cmd );
                assertOption( SOURCES_OPTION, cmd );

                final JavaSources command = new JavaSources();
                setupJavaSources( command, classLoader, cmd );

                command.editModuleSources( new File( cmd.getOptionValue( SOURCES_OPTION.getOpt() ) ) );
                exitCode = EXIT_OK;
            }
            else if ( MERGE_TOOL.equals( cmd.getOptionValue( TOOL_OPTION.getOpt() ) ) )
            {
                assertOption( DOCUMENT_OPTION, cmd );

                final ModuleAssembler command = new ModuleAssembler();
                setupModuleAssembler( command, classLoader, cmd );

                File mergeDirectory = null;
                if ( cmd.hasOption( RESOURCES_OPTION.getOpt() ) )
                {
                    mergeDirectory = new File( cmd.getOptionValue( RESOURCES_OPTION.getOpt() ) );
                }

                command.assembleModules( new File( cmd.getOptionValue( DOCUMENT_OPTION.getOpt() ) ),
                                         mergeDirectory, classLoader, false );

                exitCode = EXIT_OK;
            }
            else if ( CLASSES_TOOL.equals( cmd.getOptionValue( TOOL_OPTION.getOpt() ) ) )
            {
                assertOption( MODULE_OPTION, cmd );
                assertOption( CLASSES_OPTION, cmd );

                final JavaClasses command = new JavaClasses();
                setupJavaClasses( command, classLoader, cmd );

                command.commitModuleClasses( new File( cmd.getOptionValue( CLASSES_OPTION.getOpt() ) ) );
                exitCode = EXIT_OK;
            }
            else
            {
                logHelp( cmd );
                log( cmd, Level.INFO, null, "\n", null );
                log( cmd, Level.SEVERE, null, getMessage( "unsupportedTool", new Object[]
                    {
                        cmd.getOptionValue( TOOL_OPTION.getOpt() )
                    } ), null );

                log( cmd, Level.INFO, null, getMessage( "separator", null ), null );
                exitCode = EXIT_FAILURE;
            }
        }
        catch ( MissingArgumentException e )
        {
            logHelp( cmd );
            log( cmd, Level.INFO, null, "\n", null );
            log( cmd, Level.SEVERE, null, e.getMessage(), e );
            log( cmd, Level.INFO, null, getMessage( "separator", null ), null );
            exitCode = EXIT_FAILURE;
        }
        catch ( ModelException e )
        {
            log( cmd, Level.INFO, null, "\n", null );
            for ( ModelException.Detail detail : e.getDetails() )
            {
                log( cmd, detail.getLevel(), null, detail.getMessage(), null );
            }
            log( cmd, Level.SEVERE, null, e.getMessage(), e );
            log( cmd, Level.INFO, null, getMessage( "separator", null ), null );
            exitCode = EXIT_FAILURE;
        }
        catch ( JAXBException e )
        {
            log( cmd, Level.INFO, null, "\n", null );
            log( cmd, Level.SEVERE, null, e.getMessage(), e );
            if ( e.getLinkedException() != null )
            {
                log( cmd, Level.SEVERE, null, e.getLinkedException().getMessage(), null );
            }
            log( cmd, Level.INFO, null, getMessage( "separator", null ), null );
            exitCode = EXIT_FAILURE;
        }
        catch ( Throwable t )
        {
            log( cmd, Level.INFO, null, "\n", null );
            log( cmd, Level.SEVERE, null, t.getMessage(), t );
            log( cmd, Level.INFO, null, getMessage( "separator", null ), null );
            exitCode = EXIT_FAILURE;
        }

        exit( exitCode, cmd );
    }

    private static Set<File> getModulesOption( final CommandLine commandLine ) throws IOException
    {
        final Set<File> files = new HashSet<File>();
        final String arg = commandLine.getOptionValue( MODULES_OPTION.getOpt() );

        if ( arg.toCharArray()[0] == '@' )
        {
            String line = null;
            final File file = new File( arg.substring( 1 ) );
            log( commandLine, Level.FINE, CLI_LOGGER, getMessage( "processing", new Object[]
                {
                    file.getCanonicalPath()
                } ), null );

            final BufferedReader reader = new BufferedReader( new FileReader( file ) );

            while ( ( line = reader.readLine() ) != null )
            {
                if ( !line.contains( "#" ) )
                {
                    if ( line.contains( ":" ) )
                    {
                        for ( String value : line.split( ":" ) )
                        {
                            files.add( new File( value ) );
                        }
                    }
                    else
                    {
                        files.add( new File( line ) );
                    }
                }
            }

            reader.close();
        }
        else
        {
            for ( String value : arg.split( ":" ) )
            {
                files.add( new File( value ) );
            }
        }

        return files;
    }

    private static ClassLoader getClasspathOption( final CommandLine commandLine )
        throws MalformedURLException, FileNotFoundException, IOException
    {
        final Set<URL> urls = new HashSet<URL>();
        if ( commandLine.hasOption( CLASSPATH_OPTION.getOpt() ) )
        {
            final String arg = commandLine.getOptionValue( CLASSPATH_OPTION.getOpt() );

            if ( arg.toCharArray()[0] == '@' )
            {
                String line = null;
                final File file = new File( arg.substring( 1 ) );
                log( commandLine, Level.FINE, CLI_LOGGER, getMessage( "processing", new Object[]
                    {
                        file.getCanonicalPath()
                    } ), null );

                final BufferedReader reader = new BufferedReader( new FileReader( file ) );

                while ( ( line = reader.readLine() ) != null )
                {
                    if ( !line.contains( "#" ) )
                    {
                        if ( line.contains( ":" ) )
                        {
                            for ( String value : line.split( ":" ) )
                            {
                                final URL url = new File( value ).toURI().toURL();
                                urls.add( url );
                            }
                        }
                        else
                        {
                            final URL url = new File( line ).toURI().toURL();
                            urls.add( url );
                        }
                    }
                }

                reader.close();
            }
            else
            {
                for ( String value : arg.split( ":" ) )
                {
                    final URL url = new File( value ).toURI().toURL();
                    urls.add( url );
                }
            }
        }

        for ( URL url : urls )
        {
            log( commandLine, Level.FINE, CLI_LOGGER, getMessage( "processingClasspathElement", new Object[]
                {
                    url.toExternalForm()
                } ), null );

        }

        return new URLClassLoader( urls.toArray( new URL[ urls.size() ] ) );
    }

    private static void setupJavaBundles( final JavaBundles javaBundles, final ClassLoader classLoader,
                                          final CommandLine commandLine )
        throws IOException, SAXException, JAXBException, ModelException
    {
        setupTool( javaBundles, classLoader, commandLine );
        if ( commandLine.hasOption( LANGUAGE_OPTION.getOpt() ) )
        {
            javaBundles.setDefaultLocale( new Locale( commandLine.getOptionValue( LANGUAGE_OPTION.getOpt() ) ) );
        }
    }

    private static void setupJavaSources( final JavaSources javaSources, final ClassLoader classLoader,
                                          final CommandLine commandLine )
        throws IOException, SAXException, JAXBException, ModelException
    {
        setupTool( javaSources, classLoader, commandLine );
    }

    private static void setupJavaClasses( final JavaClasses javaClasses, final ClassLoader classLoader,
                                          final CommandLine commandLine )
        throws IOException, SAXException, JAXBException, ModelException
    {
        setupTool( javaClasses, classLoader, commandLine );
    }

    private static void setupModuleAssembler( final ModuleAssembler moduleAssembler, final ClassLoader classLoader,
                                              final CommandLine commandLine )
        throws IOException, SAXException, JAXBException, ModelException
    {
        setupTool( moduleAssembler, classLoader, commandLine );
    }

    private static void setupTool( final JomcTool tool, final ClassLoader classLoader, final CommandLine commandLine )
        throws IOException, SAXException, JAXBException, ModelException
    {
        final String toolName =
            tool.getClass().getName().substring( tool.getClass().getPackage().getName().length() + 1 );

        log( commandLine, Level.INFO, null, getMessage( "initializing", new Object[]
            {
                toolName
            } ), null );

        tool.getListeners().add( new JomcTool.Listener()
        {

            public void onLog( final Level level, final String message, final Throwable t )
            {
                log( commandLine, level, toolName, message, t );
            }

        } );

        if ( commandLine.hasOption( TEMPLATE_ENCODING_OPTION.getOpt() ) )
        {
            tool.setTemplateEncoding( commandLine.getOptionValue( TEMPLATE_ENCODING_OPTION.getOpt() ) );
        }
        if ( commandLine.hasOption( INPUT_ENCODING_OPTION.getOpt() ) )
        {
            tool.setInputEncoding( commandLine.getOptionValue( INPUT_ENCODING_OPTION.getOpt() ) );
        }
        if ( commandLine.hasOption( OUTPUT_ENCODING_OPTION.getOpt() ) )
        {
            tool.setOutputEncoding( commandLine.getOptionValue( OUTPUT_ENCODING_OPTION.getOpt() ) );
        }
        if ( commandLine.hasOption( BUILD_OPTION.getOpt() ) )
        {
            tool.setBuildDirectory( new File( commandLine.getOptionValue( BUILD_OPTION.getOpt() ) ) );
        }
        if ( commandLine.hasOption( PROFILE_OPTION.getOpt() ) )
        {
            tool.setProfile( commandLine.getOptionValue( PROFILE_OPTION.getOpt() ) );
        }
        if ( commandLine.hasOption( MODULE_OPTION.getOpt() ) )
        {
            tool.setModuleName( commandLine.getOptionValue( MODULE_OPTION.getOpt() ) );
        }

        if ( tool.getModelManager() instanceof DefaultModelManager )
        {
            final DefaultModelManager defaultModelManager = (DefaultModelManager) tool.getModelManager();
            defaultModelManager.getListeners().add( new DefaultModelManager.Listener()
            {

                public void onLog( final Level level, final String message, final Throwable t )
                {
                    log( commandLine, level, MANAGER_LOGGER, message, t );
                }

            } );

            defaultModelManager.setClassLoader( classLoader );
        }

        if ( commandLine.hasOption( MODULES_OPTION.getOpt() ) )
        {
            final Set<File> files = getModulesOption( commandLine );
            for ( File file : files )
            {
                log( commandLine, Level.FINE, CLI_LOGGER, getMessage( "processing", new Object[]
                    {
                        file.getCanonicalPath()
                    } ), null );

                final Object content = tool.getModelManager().getUnmarshaller( false ).unmarshal( file );

                if ( content instanceof JAXBElement && ( (JAXBElement) content ).getValue() instanceof Module )
                {
                    final Module module = (Module) ( (JAXBElement) content ).getValue();
                    log( commandLine, Level.FINE, CLI_LOGGER, getMessage( "processingModule", new Object[]
                        {
                            module.getName(), file.getCanonicalPath()
                        } ), null );

                    tool.getModules().getModule().add( module );
                }
            }
        }
        else if ( tool.getModelManager() instanceof DefaultModelManager )
        {
            final DefaultModelManager defaultModelManager = (DefaultModelManager) tool.getModelManager();
            tool.setModules( defaultModelManager.getClasspathModules( DefaultModelManager.DEFAULT_DOCUMENT_LOCATION ) );

            final Module classpathModule = defaultModelManager.getClasspathModule( tool.getModules() );
            if ( classpathModule != null )
            {
                tool.getModules().getModule().add( classpathModule );
            }
        }

        log( commandLine, Level.INFO, null, "\n", null );
        log( commandLine, Level.INFO, null, getMessage( "modulesReport", null ), null );

        if ( tool.getModules().getModule().isEmpty() )
        {
            log( commandLine, Level.INFO, null, "\t" + getMessage( "missingModules", null ), null );
        }
        else
        {
            for ( Module m : tool.getModules().getModule() )
            {
                final StringBuffer moduleInfo = new StringBuffer().append( '\t' );
                moduleInfo.append( m.getName() );

                if ( m.getVersion() != null )
                {
                    moduleInfo.append( " - " ).append( m.getVersion() );
                }

                log( commandLine, Level.INFO, null, moduleInfo.toString(), null );
            }
        }
        log( commandLine, Level.INFO, null, "\n", null );

        tool.getModelManager().validateModelObject(
            tool.getModelManager().getObjectFactory().createModules( tool.getModules() ) );

    }

    private static void assertOption( final Option option, final CommandLine cmd ) throws MissingArgumentException
    {
        if ( !cmd.hasOption( option.getOpt() ) )
        {
            throw new MissingArgumentException( getMessage( "missing", new Object[]
                {
                    option.getLongOpt()
                } ) );

        }
    }

    private static void logHelp( final CommandLine commandLine )
    {
        final StringWriter usage = new StringWriter();
        final StringWriter opts = new StringWriter();

        final HelpFormatter formatter = new HelpFormatter();

        PrintWriter pw = new PrintWriter( usage );
        formatter.printUsage( pw, 72, "jomc", OPTIONS );
        pw.close();

        pw = new PrintWriter( opts );
        formatter.printOptions( pw, 72, OPTIONS, 5, 5 );
        pw.close();

        log( commandLine, Level.INFO, null, "\n", null );
        log( commandLine, Level.INFO, null, usage.toString(), null );
        log( commandLine, Level.INFO, null, opts.toString(), null );
        log( commandLine, Level.INFO, null, getMessage( "separator", null ), null );
    }

    private static void exit( final int exitCode, final CommandLine commandLine )
    {
        log( commandLine, Level.INFO, null, getMessage( "separator", null ), null );
        log( commandLine, Level.INFO, null, getMessage( exitCode == EXIT_OK ? "success" : "failure", new Object[]
            {
                exitCode
            } ), null );

        log( commandLine, Level.INFO, null, getMessage( "separator", null ), null );
        System.exit( exitCode );
    }

    private static String getMessage( final String key, final Object args )
    {
        final ResourceBundle b = ResourceBundle.getBundle( "org/jomc/tools/cli/JomcToolsCli" );
        final MessageFormat f = new MessageFormat( b.getString( key ) );
        return f.format( args );
    }

    private static void log( final CommandLine commandLine, final Level level, final String component,
                             final String message, final Throwable throwable )
    {
        final Level cliLevel =
            ( commandLine != null && commandLine.hasOption( VERBOSE_OPTION.getOpt() ) ? Level.FINEST : Level.INFO );

        if ( level.intValue() >= cliLevel.intValue() )
        {
            final PrintWriter printWriter = new PrintWriter( new OutputStreamWriter( System.out ) );

            if ( message != null )
            {
                printWriter.print( getLoglines( commandLine, level, component, message ) );
            }

            if ( throwable != null && commandLine != null && commandLine.hasOption( DEBUG_OPTION.getOpt() ) )
            {
                final StringWriter stackTrace = new StringWriter();
                final PrintWriter pw = new PrintWriter( stackTrace );
                throwable.printStackTrace( pw );
                pw.flush();

                printWriter.print( getLoglines( commandLine, level, component, stackTrace.toString() ) );
            }

            printWriter.flush();
        }
    }

    private static String getLoglines( final CommandLine commandLine, final Level level, final String component,
                                       final String text )
    {
        try
        {
            String line;
            final boolean verbose = commandLine != null && commandLine.hasOption( VERBOSE_OPTION.getOpt() );
            final StringBuffer lines = new StringBuffer();
            final BufferedReader reader = new BufferedReader( new StringReader( text ) );

            while ( ( line = reader.readLine() ) != null )
            {
                if ( verbose )
                {
                    lines.append( "[" ).append( StringUtils.leftPad( level.toString(), 8 ) ).append( "] " );

                    if ( component != null )
                    {
                        lines.append( "[" ).append( StringUtils.leftPad( component, 18 ) ).append( "] " );
                    }
                }

                lines.append( line ).append( "\n" );
            }

            return lines.toString();
        }
        catch ( IOException e )
        {
            throw new RuntimeException( e );
        }
    }

}
