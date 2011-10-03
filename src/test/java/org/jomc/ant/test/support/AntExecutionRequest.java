/*
 *   Copyright (C) Christian Schulte, 2005-07-25
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

import org.apache.tools.ant.Project;

/**
 * Request to execute a target on an Ant project.
 *
 * @author <a href="mailto:schulte2005@users.sourceforge.net">Christian Schulte</a>
 * @version $JOMC$
 */
public class AntExecutionRequest
{

    /** The {@code Project} to execute a target of. */
    private final Project project;

    /** The identifier of the target to execute. */
    private final String target;

    /**
     * Creates a new {@code AntExecutionRequest} taking a project and a target.
     *
     * @param project The {@code Project} to execute {@code target} of.
     * @param target The identifier of the target to execute.
     */
    public AntExecutionRequest( final Project project, final String target )
    {
        super();
        this.project = project;
        this.target = target;
    }

    /**
     * Gets the {@code Project} to execute a target of.
     *
     * @return The {@code Project} to execute a target of or {@code null}.
     */
    public Project getProject()
    {
        return this.project;
    }

    /**
     * Gets the identifier of the target to execute.
     *
     * @return The identifier of the target to execute or {@code null}.
     */
    public String getTarget()
    {
        return this.target;
    }

}
