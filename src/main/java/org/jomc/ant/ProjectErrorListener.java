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
package org.jomc.ant;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.TransformerException;
import org.apache.tools.ant.Project;

/**
 * Error listener backed by a project.
 *
 * @author <a href="mailto:schulte2005@users.sourceforge.net">Christian Schulte</a>
 * @version $Id$
 */
public class ProjectErrorListener implements ErrorListener
{

    /** The project messages are logged to. */
    private final Project project;

    /**
     * Creates a new {@code ProjectErrorListener} taking a {@code Project}.
     *
     * @param project The {@code Project} to log messages to.
     */
    public ProjectErrorListener( final Project project )
    {
        super();
        this.project = project;
    }

    /**
     * Gets the {@code Project} messages are logged to.
     *
     * @return The {@code Project} messages are logged to.
     */
    public final Project getProject()
    {
        return this.project;
    }

    /** {@inheritDoc} */
    public void warning( final TransformerException exception ) throws TransformerException
    {
        this.getProject().log( getMessage( exception ), exception, Project.MSG_WARN );
    }

    /** {@inheritDoc} */
    public void error( final TransformerException exception ) throws TransformerException
    {
        throw exception;
    }

    /** {@inheritDoc} */
    public void fatalError( final TransformerException exception ) throws TransformerException
    {
        throw exception;
    }

    private static String getMessage( final Throwable t )
    {
        return t != null ? t.getMessage() != null ? t.getMessage() : getMessage( t.getCause() ) : null;
    }

}
