/*
 *   Copyright (C) 2005 Christian Schulte <cs@schulte.it>
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
package org.jomc.tools.ant.test;

import org.jomc.tools.ant.ClassFileProcessorTask;

/**
 * Test cases for class {@code org.jomc.tools.ant.ClassFileProcessorTask}.
 *
 * @author <a href="mailto:cs@schulte.it">Christian Schulte</a>
 * @version $JOMC$
 */
public class ClassFileProcessorTaskTest extends JomcToolTaskTest
{

    /**
     * Creates a new {@code ClassFileProcessorTaskTest} instance.
     */
    public ClassFileProcessorTaskTest()
    {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ClassFileProcessorTask getJomcTask()
    {
        return (ClassFileProcessorTask) super.getJomcTask();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ClassFileProcessorTask newJomcTask()
    {
        return new ClassFileProcessorTask();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getBuildFileName()
    {
        return "class-file-processor-task-test.xml";
    }

}
