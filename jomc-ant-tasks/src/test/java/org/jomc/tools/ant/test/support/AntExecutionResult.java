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
package org.jomc.tools.ant.test.support;

import java.util.LinkedList;
import java.util.List;
import org.apache.tools.ant.BuildEvent;

/**
 * Result of an execution of an Ant target.
 *
 * @author <a href="mailto:cs@schulte.it">Christian Schulte</a>
 * @version $JOMC$
 */
public class AntExecutionResult
{

    /**
     * The characters written to the system output stream during execution of the target.
     */
    private String systemOutput;

    /**
     * The characters written to the system error stream during execution of the target.
     */
    private String systemError;

    /**
     * List of {@code buildStarted} events fired during execution of the target.
     */
    private List<BuildEvent> buildStartedEvents;

    /**
     * List of {@code buildFinished} events fired during execution of the target.
     */
    private List<BuildEvent> buildFinishedEvents;

    /**
     * List of {@code targetStarted} events fired during execution of the target.
     */
    private List<BuildEvent> targetStartedEvents;

    /**
     * List of {@code targetFinished} events fired during execution of the target.
     */
    private List<BuildEvent> targetFinishedEvents;

    /**
     * List of {@code taskStarted} events fired during execution of the target.
     */
    private List<BuildEvent> taskStartedEvents;

    /**
     * List of {@code taskFinished} events fired during execution of the target.
     */
    private List<BuildEvent> taskFinishedEvents;

    /**
     * List of {@code messageLogged} events fired during execution of the target.
     */
    private List<BuildEvent> messageLoggedEvents;

    /**
     * The throwable thrown by the execution of the target.
     */
    private Throwable throwable;

    /**
     * Creates a new {@code AntExecutionResult}.
     */
    public AntExecutionResult()
    {
        super();
    }

    /**
     * Gets the characters written to the system output stream during execution of the target.
     *
     * @return The characters written to the system output stream during execution of the target or {@code null}.
     */
    public final String getSystemOutput()
    {
        return this.systemOutput;
    }

    /**
     * Set the characters written to the system output stream during execution of the target.
     *
     * @param value The new characters written to the system output stream during execution of the target or
     * {@code null}.
     */
    public final void setSystemOutput( final String value )
    {
        this.systemOutput = value;
    }

    /**
     * Gets the characters written to the system error stream during execution of the target.
     *
     * @return The characters written to the system error stream during execution of the target or {@code null}.
     */
    public final String getSystemError()
    {
        return this.systemError;
    }

    /**
     * Set the characters written to the system error stream during execution of the target.
     *
     * @param value The new characters written to the system error stream during execution of the target or
     * {@code null}.
     */
    public final void setSystemError( final String value )
    {
        this.systemError = value;
    }

    /**
     * Gets the throwable thrown by the execution of the target.
     *
     * @return The throwable thrown by the execution of the target or {@code null}.
     */
    public final Throwable getThrowable()
    {
        return this.throwable;
    }

    /**
     * Sets the throwable thrown by the execution of the target.
     *
     * @param value The new throwable thrown by the execution of the target or {@code null}.
     */
    public final void setThrowable( final Throwable value )
    {
        this.throwable = value;
    }

    /**
     * Gets the list of {@code buildStarted} events fired during execution of the target.
     * <p>
     * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make
     * to the returned list will be present inside the object. This is why there is no {@code set} method for the
     * build started events property.
     * </p>
     *
     * @return The list of {@code buildStarted} events fired during execution of the target.
     */
    public final List<BuildEvent> getBuildStartedEvents()
    {
        if ( this.buildStartedEvents == null )
        {
            this.buildStartedEvents = new LinkedList<BuildEvent>();
        }

        return this.buildStartedEvents;
    }

    /**
     * Gets the list of {@code buildFinished} events fired during execution of the target.
     * <p>
     * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make
     * to the returned list will be present inside the object. This is why there is no {@code set} method for the
     * build finished events property.
     * </p>
     *
     * @return The list of {@code buildFinished} events fired during execution of the target.
     */
    public final List<BuildEvent> getBuildFinishedEvents()
    {
        if ( this.buildFinishedEvents == null )
        {
            this.buildFinishedEvents = new LinkedList<BuildEvent>();
        }

        return this.buildFinishedEvents;
    }

    /**
     * Gets the list of {@code targetStarted} events fired during execution of the target.
     * <p>
     * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make
     * to the returned list will be present inside the object. This is why there is no {@code set} method for the
     * target started events property.
     * </p>
     *
     * @return The list of {@code targetStarted} events fired during execution of the target.
     */
    public final List<BuildEvent> getTargetStartedEvents()
    {
        if ( this.targetStartedEvents == null )
        {
            this.targetStartedEvents = new LinkedList<BuildEvent>();
        }

        return this.targetStartedEvents;
    }

    /**
     * Gets the list of {@code targetFinished} events fired during execution of the target.
     * <p>
     * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make
     * to the returned list will be present inside the object. This is why there is no {@code set} method for the
     * target finished events property.
     * </p>
     *
     * @return The list of {@code targetFinished} events fired during execution of the target.
     */
    public final List<BuildEvent> getTargetFinishedEvents()
    {
        if ( this.targetFinishedEvents == null )
        {
            this.targetFinishedEvents = new LinkedList<BuildEvent>();
        }

        return this.targetFinishedEvents;
    }

    /**
     * Gets the list of {@code taskStarted} events fired during execution of the target.
     * <p>
     * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make
     * to the returned list will be present inside the object. This is why there is no {@code set} method for the
     * task started events property.
     * </p>
     *
     * @return The list of {@code taskStarted} events fired during execution of the target.
     */
    public final List<BuildEvent> getTaskStartedEvents()
    {
        if ( this.taskStartedEvents == null )
        {
            this.taskStartedEvents = new LinkedList<BuildEvent>();
        }

        return this.taskStartedEvents;
    }

    /**
     * Gets the list of {@code taskFinished} events fired during execution of the target.
     * <p>
     * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make
     * to the returned list will be present inside the object. This is why there is no {@code set} method for the
     * task finished events property.
     * </p>
     *
     * @return The list of {@code taskFinished} events fired during execution of the target.
     */
    public final List<BuildEvent> getTaskFinishedEvents()
    {
        if ( this.taskFinishedEvents == null )
        {
            this.taskFinishedEvents = new LinkedList<BuildEvent>();
        }

        return this.taskFinishedEvents;
    }

    /**
     * Gets the list of {@code messageLogged} events fired during execution of the target.
     * <p>
     * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make
     * to the returned list will be present inside the object. This is why there is no {@code set} method for the
     * message logged events property.
     * </p>
     *
     * @return The list of {@code messageLogger} events fired during execution of the target.
     */
    public final List<BuildEvent> getMessageLoggedEvents()
    {
        if ( this.messageLoggedEvents == null )
        {
            this.messageLoggedEvents = new LinkedList<BuildEvent>();
        }

        return this.messageLoggedEvents;
    }

}
