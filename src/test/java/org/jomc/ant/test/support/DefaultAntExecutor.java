/*
 *   Copyright (C) 2005 Christian Schulte <schulte2005@users.sourceforge.net>
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
package org.jomc.ant.test.support;

import java.io.PrintStream;
import java.io.StringWriter;
import org.apache.commons.io.output.WriterOutputStream;
import org.apache.tools.ant.BuildEvent;
import org.apache.tools.ant.BuildListener;

/**
 * Default {@code AntExecutor} implementation.
 *
 * @author <a href="mailto:schulte2005@users.sourceforge.net">Christian Schulte</a>
 * @version $JOMC$
 */
public class DefaultAntExecutor implements AntExecutor
{

    /** Creates a new {@code DefaultAntExecutor}. */
    public DefaultAntExecutor()
    {
        super();
    }

    public AntExecutionResult executeAnt( final AntExecutionRequest request )
    {
        if ( request == null )
        {
            throw new NullPointerException( "request" );
        }
        if ( request.getProject() == null )
        {
            throw new NullPointerException( "project" );
        }
        if ( request.getTarget() == null )
        {
            throw new NullPointerException( "target" );
        }

        final StringWriter out = new StringWriter();
        final StringWriter err = new StringWriter();
        final PrintStream writerOut = new PrintStream( new WriterOutputStream( out ) );
        final PrintStream writerErr = new PrintStream( new WriterOutputStream( err ) );
        final PrintStream systemOut = System.out;
        final PrintStream systemErr = System.err;
        final AntExecutionResult result = new AntExecutionResult();
        final BuildListener buildListener = new BuildListener()
        {

            public void buildStarted( final BuildEvent event )
            {
                result.getBuildStartedEvents().add( event );
            }

            public void buildFinished( final BuildEvent event )
            {
                result.getBuildFinishedEvents().add( event );
            }

            public void targetStarted( final BuildEvent event )
            {
                result.getTargetStartedEvents().add( event );
            }

            public void targetFinished( final BuildEvent event )
            {
                result.getTaskFinishedEvents().add( event );
            }

            public void taskStarted( final BuildEvent event )
            {
                result.getTaskStartedEvents().add( event );
            }

            public void taskFinished( final BuildEvent event )
            {
                result.getTaskFinishedEvents().add( event );
            }

            public void messageLogged( final BuildEvent event )
            {
                result.getMessageLoggedEvents().add( event );
            }

        };

        try
        {
            systemOut.flush();
            systemErr.flush();
            System.setOut( writerOut );
            System.setErr( writerErr );

            request.getProject().addBuildListener( buildListener );
            request.getProject().executeTarget( request.getTarget() );
        }
        catch ( final Throwable t )
        {
            result.setThrowable( t );
        }
        finally
        {
            request.getProject().removeBuildListener( buildListener );
            System.setOut( systemOut );
            System.setErr( systemErr );
            writerOut.close();
            writerErr.close();
            result.setSystemError( err.toString() );
            result.setSystemOutput( out.toString() );
        }

        return result;
    }

}
