/*
 *  JOMC Tools
 *  Copyright (c) 2005 Christian Schulte <cs@schulte.it>
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 */
package org.jomc.tools.cli;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.xml.bind.Marshaller;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.MissingArgumentException;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.jomc.model.ModelException;
import org.jomc.model.ModelResolver;
import org.jomc.tools.JavaBundles;
import org.jomc.tools.JavaSources;
import org.jomc.tools.ModuleAssembler;

/**
 * Container tools command line interface.
 *
 * @author <a href="mailto:cs@schulte.it">Christian Schulte</a>
 * @version $Id$
 */
public class JomcToolsCli
{

    /** Option taking a tool to execute. */
    private static final Option TOOL_OPTION =
        new Option( "t", "tool", true, getMessage( "tool", null ) );

    private static final String BUNDLES_TOOL = "bundles";

    private static final String SOURCES_TOOL = "sources";

    private static final String MERGE_TOOL = "merge";

    /** Option taking a module name to process. */
    private static final Option MODULE_OPTION = new Option( "m", "module", true, getMessage( "module", null ) );

    /** Option taking a language to use when processing. */
    private static final Option LANGUAGE_OPTION =
        new Option( "l", "language", true, getMessage( "language", new Object[]
        {
            Locale.getDefault().getDisplayLanguage()
        } ) );

    /** Option taking an encoding to use when processing. */
    private static final Option ENCODING_OPTION = new Option( "e", "encoding", true, getMessage( "encoding", null ) );

    /** Option taking a directory name to write source files to. */
    private static final Option SOURCES_OPTION = new Option( "s", "source-dir", true, getMessage( "sources", null ) );

    /** Option taking a directory name to write resource files to. */
    private static final Option RESOURCES_OPTION =
        new Option( "r", "resource-dir", true, getMessage( "resources", null ) );

    /** Option taking a directory name to write build files to. */
    private static final Option BUILD_OPTION = new Option( "b", "build-dir", true, getMessage( "build", null ) );

    /** Option to request the tool's help. */
    private static final Option HELP_OPTION = new Option( "h", "help", false, getMessage( "help", null ) );

    /** Option indicating verbose output. */
    private static final Option VERBOSE_OPTION = new Option( "v", "verbose", false, getMessage( "verbose", null ) );

    /** Option to resolve the classpath module.  */
    private static final Option CLASSPATH_OPTION =
        new Option( "cp", "classpath", false, getMessage( "classpath", null ) );

    /** Option to select templates.  */
    private static final Option PROFILE_OPTION = new Option( "p", "profile", true, getMessage( "profile", new Object[]
        {
            "default"
        } ) );

    /** Option taking a file name to write any document to.  */
    private static final Option DOCUMENT_OPTION =
        new Option( "d", "document", true, getMessage( "document", null ) );

    /** Options supported by the command line interface. */
    private static final Options OPTIONS = new Options();



    static
    {
        OPTIONS.addOption( TOOL_OPTION );
        OPTIONS.addOption( MODULE_OPTION );
        OPTIONS.addOption( LANGUAGE_OPTION );
        OPTIONS.addOption( ENCODING_OPTION );
        OPTIONS.addOption( VERBOSE_OPTION );
        OPTIONS.addOption( SOURCES_OPTION );
        OPTIONS.addOption( RESOURCES_OPTION );
        OPTIONS.addOption( BUILD_OPTION );
        OPTIONS.addOption( HELP_OPTION );
        OPTIONS.addOption( CLASSPATH_OPTION );
        OPTIONS.addOption( PROFILE_OPTION );
        OPTIONS.addOption( DOCUMENT_OPTION );

        TOOL_OPTION.setRequired( true );
    }

    public static void main( final String[] args )
    {
        System.out.println( getMessage( "license", null ) );

        boolean verbose = false;
        boolean classpath = false;
        int exitCode = 0;

        try
        {
            final CommandLineParser cli = new GnuParser();
            final CommandLine cmd = cli.parse( OPTIONS, args );

            verbose = cmd.hasOption( VERBOSE_OPTION.getOpt() );
            classpath = cmd.hasOption( CLASSPATH_OPTION.getOpt() );

            if ( cmd.hasOption( HELP_OPTION.getOpt() ) || !cmd.hasOption( TOOL_OPTION.getOpt() ) )
            {
                printHelp();
            }
            else
            {
                final String module = cmd.getOptionValue( MODULE_OPTION.getOpt() );

                if ( BUNDLES_TOOL.equals( cmd.getOptionValue( TOOL_OPTION.getOpt() ) ) )
                {
                    assertOption( MODULE_OPTION, cmd );
                    assertOption( SOURCES_OPTION, cmd );
                    assertOption( RESOURCES_OPTION, cmd );

                    final JavaBundles command = new JavaBundles( getClassLoader() );
                    command.getModelManager().setClasspathAware( classpath );
                    command.getModelManager().setValidating( true );
                    command.setModuleName( module );

                    if ( cmd.hasOption( LANGUAGE_OPTION.getOpt() ) )
                    {
                        command.setDefaultLocale( new Locale( cmd.getOptionValue( LANGUAGE_OPTION.getOpt() ) ) );
                    }
                    if ( cmd.hasOption( ENCODING_OPTION.getOpt() ) )
                    {
                        command.setEncoding( cmd.getOptionValue( ENCODING_OPTION.getOpt() ) );
                    }
                    if ( cmd.hasOption( BUILD_OPTION.getOpt() ) )
                    {
                        command.setBuildDirectory( new File( cmd.getOptionValue( BUILD_OPTION.getOpt() ) ) );
                    }
                    if ( cmd.hasOption( PROFILE_OPTION.getOpt() ) )
                    {
                        command.setProfile( cmd.getOptionValue( PROFILE_OPTION.getOpt() ) );
                    }

                    command.writeModuleBundles( new File( cmd.getOptionValue( SOURCES_OPTION.getOpt() ) ),
                                                new File( cmd.getOptionValue( RESOURCES_OPTION.getOpt() ) ) );

                }
                else if ( SOURCES_TOOL.equals( cmd.getOptionValue( TOOL_OPTION.getOpt() ) ) )
                {
                    assertOption( MODULE_OPTION, cmd );
                    assertOption( SOURCES_OPTION, cmd );

                    final JavaSources command = new JavaSources( getClassLoader() );
                    command.getModelManager().setClasspathAware( classpath );
                    command.getModelManager().setValidating( true );
                    command.setModuleName( module );

                    if ( cmd.hasOption( ENCODING_OPTION.getOpt() ) )
                    {
                        command.setEncoding( cmd.getOptionValue( ENCODING_OPTION.getOpt() ) );
                    }
                    if ( cmd.hasOption( PROFILE_OPTION.getOpt() ) )
                    {
                        command.setProfile( cmd.getOptionValue( PROFILE_OPTION.getOpt() ) );
                    }

                    command.editModuleSources( new File( cmd.getOptionValue( SOURCES_OPTION.getOpt() ) ) );
                }
                else if ( MERGE_TOOL.equals( cmd.getOptionValue( TOOL_OPTION.getOpt() ) ) )
                {
                    assertOption( DOCUMENT_OPTION, cmd );

                    final ModuleAssembler command = new ModuleAssembler( getClassLoader() );
                    command.getModelManager().setClasspathAware( classpath );
                    command.getModelManager().setValidating( true );

                    File mergeDirectory = null;
                    if ( cmd.hasOption( RESOURCES_OPTION.getOpt() ) )
                    {
                        mergeDirectory = new File( cmd.getOptionValue( RESOURCES_OPTION.getOpt() ) );
                    }

                    command.assembleModules( new File( cmd.getOptionValue( DOCUMENT_OPTION.getOpt() ) ),
                                             mergeDirectory, classpath );

                }
            }
        }
        catch ( ModelException e )
        {
            if ( verbose )
            {
                e.printStackTrace( System.err );
            }
            else
            {
                System.err.println( e.getMessage() );
            }

            final ModelException me = (ModelException) e;
            if ( me.getElement() != null && verbose )
            {
                try
                {
                    final Writer writer = new StringWriter();
                    final Marshaller m = new ModelResolver( getClassLoader() ).getMarshaller( false, true );
                    m.marshal( me.getElement(), writer );
                    System.err.println( writer.toString() );
                }
                catch ( Exception ex )
                {
                    if ( verbose )
                    {
                        ex.printStackTrace( System.err );
                    }
                    else
                    {
                        System.err.println( ex.getMessage() );
                    }
                }
            }

            exitCode = 1;
        }
        catch ( MissingArgumentException e )
        {
            printHelp();

            if ( verbose )
            {
                e.printStackTrace( System.err );
            }
            else
            {
                System.err.println( e.getMessage() );
            }

            exitCode = 1;
        }
        catch ( Throwable t )
        {
            if ( verbose )
            {
                t.printStackTrace( System.err );
            }
            else
            {
                System.err.println( t.getMessage() );
            }

            exitCode = 1;
        }

        System.exit( exitCode );
    }

    private static void assertOption( final Option option,
                                      final CommandLine cmd )
        throws MissingArgumentException
    {
        if ( !cmd.hasOption( option.getOpt() ) )
        {
            throw new MissingArgumentException( getMessage( "missing", new Object[]
                {
                    option.getLongOpt()
                } ) );

        }
    }

    private static void printHelp()
    {
        final StringWriter usage = new StringWriter();
        final StringWriter opts = new StringWriter();

        final HelpFormatter formatter = new HelpFormatter();

        PrintWriter pw = new PrintWriter( usage );
        formatter.printUsage( pw, 72, "jomc-tools", OPTIONS );
        pw.close();

        pw = new PrintWriter( opts );
        formatter.printOptions( pw, 72, OPTIONS, 5, 5 );
        pw.close();

        System.out.println( usage.toString() + "\n" );
        System.out.println( opts.toString() + "\n" );
    }

    private static String getMessage( final String key, final Object args )
    {
        final ResourceBundle b = ResourceBundle.getBundle( "org/jomc/tools/cli/JomcToolsCli" );
        final MessageFormat f = new MessageFormat( b.getString( key ) );
        return f.format( args );
    }

    private static ClassLoader getClassLoader()
    {
        ClassLoader cl = JomcToolsCli.class.getClassLoader();
        if ( cl == null )
        {
            cl = ClassLoader.getSystemClassLoader();
        }

        return cl;
    }

}
