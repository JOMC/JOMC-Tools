/*
 * Copyright (C) 2009 Christian Schulte <cs@schulte.it>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   o Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   o Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in
 *     the documentation and/or other materials provided with the
 *     distribution.
 *
 * THIS SOFTWARE IS PROVIDED "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY
 * AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * $JOMC$
 *
 */
package org.jomc.tools.cli.commands;

/**
 * Command execution exception.
 *
 * @author <a href="mailto:cs@schulte.it">Christian Schulte</a>
 */
public class CommandExecutionException extends Exception
{

    /**
     * Serial version UID for backwards compatibility with 2.x object streams.
     */
    private static final long serialVersionUID = 77128788446670777L;

    /**
     * Creates a new {@code CommandExecutionException} instance.
     */
    public CommandExecutionException()
    {
        super();
    }

    /**
     * Creates a new {@code CommandExecutionException} taking a message.
     *
     * @param message A message describing the exception.
     */
    public CommandExecutionException( final String message )
    {
        this( message, null );
    }

    /**
     * Creates a new {@code CommandExecutionException} taking a causing throwable.
     *
     * @param throwable A throwable causing the exception.
     */
    public CommandExecutionException( final Throwable throwable )
    {
        this( null, throwable );
    }

    /**
     * Creates a new {@code CommandExecutionException} taking a message and a causing throwable.
     *
     * @param message A message describing the exception.
     * @param throwable A throwable causing the exception.
     */
    public CommandExecutionException( final String message, final Throwable throwable )
    {
        super( message, throwable );
    }

}
