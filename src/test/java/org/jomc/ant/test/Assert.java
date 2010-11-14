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
package org.jomc.ant.test;

import org.apache.tools.ant.BuildEvent;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Provides static methods for testing various assertions related to Ant executions.
 *
 * @author <a href="mailto:schulte2005@users.sourceforge.net">Christian Schulte</a>
 * @version $Id$
 */
public class Assert
{

    /**
     * Tests an Ant execution to have thrown an exception of a given type.
     *
     * @param result The result to test.
     * @param exception The class of the expected exception.
     */
    public static void assertException( final AntExecutionResult result, final Class<? extends Throwable> exception )
    {
        assertNotNull( result );
        assertNotNull( exception );
        assertNotNull( result.getThrowable() );
        assertTrue( result.getThrowable().getClass() == exception );
    }

    /**
     * Tests an Ant execution to have thrown an exception of a given type taking a reason.
     *
     * @param result The result to test.
     * @param exception The class of the expected exception.
     * @param reason The reason describing why {@code exception} should have been thrown.
     */
    public static void assertException( final AntExecutionResult result, final Class<? extends Throwable> exception,
                                        final CharSequence reason )
    {
        assertNotNull( result );
        assertNotNull( exception );
        assertNotNull( reason );
        assertNotNull( reason.toString(), result.getThrowable() );
        assertTrue( reason.toString(), result.getThrowable().getClass() == exception );
    }

    /**
     * Tests an Ant execution to have thrown an instance of a given exception.
     *
     * @param result The result to test.
     * @param exception The class the expected exception should be an instance of.
     */
    public static void assertExceptionInstance( final AntExecutionResult result,
                                                final Class<? extends Throwable> exception )
    {
        assertNotNull( result );
        assertNotNull( exception );
        assertNotNull( result.getThrowable() );
        assertTrue( result.getThrowable().getClass().isAssignableFrom( exception ) );
    }

    /**
     * Tests an Ant execution to have thrown an instance of a given exception taking a reason.
     *
     * @param result The result to test.
     * @param exception The class the expected exception should be an instance of.
     * @param reason The reason describing why {@code exception} should have been thrown.
     */
    public static void assertExceptionInstance( final AntExecutionResult result,
                                                final Class<? extends Throwable> exception, final CharSequence reason )
    {
        assertNotNull( result );
        assertNotNull( exception );
        assertNotNull( reason.toString(), result.getThrowable() );
        assertTrue( reason.toString(), result.getThrowable().getClass().isAssignableFrom( exception ) );
    }

    /**
     * Tests an Ant execution to have thrown an exception with a given message.
     *
     * @param result The result to test.
     * @param message The message of the expected exception.
     */
    public static void assertExceptionMessage( final AntExecutionResult result, final CharSequence message )
    {
        assertNotNull( result );
        assertNotNull( message );
        assertNotNull( result.getThrowable() );
        assertEquals( message, result.getThrowable().getMessage() );
    }

    /**
     * Tests an Ant execution to have thrown an exception with a given message taking a reason.
     *
     * @param result The result to test.
     * @param message The message of the expected exception.
     * @param reason The reason describing why {@code exception} should have been thrown.
     */
    public static void assertExceptionMessage( final AntExecutionResult result, final CharSequence message,
                                               final CharSequence reason )
    {
        assertNotNull( result );
        assertNotNull( message );
        assertNotNull( reason );
        assertNotNull( reason.toString(), result.getThrowable() );
        assertEquals( reason.toString(), message, result.getThrowable().getMessage() );
    }

    /**
     * Tests an Ant execution to have thrown an exception with a message containing a given string.
     *
     * @param result The result to test.
     * @param needle The text the message of the expected exception should contain.
     */
    public static void assertExceptionMessageContaining( final AntExecutionResult result, final CharSequence needle )
    {
        assertNotNull( result );
        assertNotNull( needle );
        assertNotNull( result.getThrowable() );
        assertTrue( result.getThrowable().getMessage().contains( needle ) );
    }

    /**
     * Tests an Ant execution to have thrown an exception with a message containing a given string taking a reason.
     *
     * @param result The result to test.
     * @param needle The text the message of the expected exception should contain.
     * @param reason The reason describing why {@code exception} should have been thrown.
     */
    public static void assertExceptionMessageContaining( final AntExecutionResult result, final CharSequence needle,
                                                         final CharSequence reason )
    {
        assertNotNull( result );
        assertNotNull( needle );
        assertNotNull( reason );
        assertNotNull( reason.toString(), result.getThrowable() );
        assertTrue( reason.toString(), result.getThrowable().getMessage().contains( needle ) );
    }

    /**
     * Tests an Ant execution to not have thrown an exception.
     *
     * @param result The result to test.
     */
    public static void assertNoException( final AntExecutionResult result )
    {
        assertNotNull( result );
        assertNull( result.getThrowable() );
    }

    /**
     * Tests an Ant execution to not have thrown an exception taking a reason.
     *
     * @param result The result to test.
     * @param reason The reason describing why the execution should not have thrown an exception.
     */
    public static void assertNoException( final AntExecutionResult result, final CharSequence reason )
    {
        assertNotNull( result );
        assertNotNull( reason );
        assertNull( reason.toString(), result.getThrowable() );
    }

    /**
     * Tests an Ant execution to have written a given text to the system output stream.
     *
     * @param result The result to test.
     * @param needle The text which should have been written to the system output stream.
     */
    public static void assertSystemOutputContaining( final AntExecutionResult result, final CharSequence needle )
    {
        assertNotNull( result );
        assertNotNull( needle );
        assertNotNull( result.getSystemOutput() );
        assertTrue( result.getSystemOutput().contains( needle ) );
    }

    /**
     * Tests an Ant execution to have written a given text to the system output stream taking a reason.
     *
     * @param result The result to test.
     * @param needle The text which should have been written to the system output stream.
     * @param reason The reason describing why {@code needle} should have been written to the system output stream.
     */
    public static void assertSystemOutputContaining( final AntExecutionResult result, final CharSequence needle,
                                                     final CharSequence reason )
    {
        assertNotNull( result );
        assertNotNull( needle );
        assertNotNull( reason );
        assertNotNull( reason.toString(), result.getSystemOutput() );
        assertTrue( reason.toString(), result.getSystemOutput().contains( needle ) );
    }

    /**
     * Tests an Ant execution to have written a given text to the system error stream.
     *
     * @param result The result to test.
     * @param needle The text which should have been written to the system error stream.
     */
    public static void assertSystemErrorContaining( final AntExecutionResult result, final CharSequence needle )
    {
        assertNotNull( result );
        assertNotNull( needle );
        assertNotNull( result.getSystemError() );
        assertTrue( result.getSystemError().contains( needle ) );
    }

    /**
     * Tests an Ant execution to have written a given text to the system error stream taking a reason.
     *
     * @param result The result to test.
     * @param needle The text which should have been written to the system error stream.
     * @param reason The reason describing why {@code needle} should have been written to the system error stream.
     */
    public static void assertSystemErrorContaining( final AntExecutionResult result, final CharSequence needle,
                                                    final CharSequence reason )
    {
        assertNotNull( result );
        assertNotNull( needle );
        assertNotNull( reason );
        assertNotNull( reason.toString(), result.getSystemError() );
        assertTrue( reason.toString(), result.getSystemError().contains( needle ) );
    }

    /**
     * Tests an Ant execution to have fired a {@code messageLogged} event holding a given message.
     *
     * @param result The result to test.
     * @param message The message which should have been logged.
     */
    public static void assertMessageLogged( final AntExecutionResult result, final CharSequence message )
    {
        assertNotNull( result );
        assertNotNull( message );

        BuildEvent messageLoggedEvent = null;

        for ( BuildEvent e : result.getMessageLoggedEvents() )
        {
            if ( message.equals( e.getMessage() ) )
            {
                messageLoggedEvent = e;
                break;
            }
        }

        assertNotNull( messageLoggedEvent );
    }

    /**
     * Tests an Ant execution to have fired a {@code messageLogged} event holding a given message taking a reason.
     *
     * @param result The result to test.
     * @param message The message which should have been logged.
     * @param reason The reason describing why {@code message} should have been logged.
     */
    public static void assertMessageLogged( final AntExecutionResult result, final CharSequence message,
                                            final CharSequence reason )
    {
        assertNotNull( result );
        assertNotNull( message );
        assertNotNull( reason );

        BuildEvent messageLoggedEvent = null;

        for ( BuildEvent e : result.getMessageLoggedEvents() )
        {
            if ( message.equals( e.getMessage() ) )
            {
                messageLoggedEvent = e;
                break;
            }
        }

        assertNotNull( reason.toString(), messageLoggedEvent );
    }

    /**
     * Tests an Ant execution to have fired a {@code messageLogged} event holding a given message with a given priority.
     *
     * @param result The result to test.
     * @param message The message which should have been logged.
     * @param priority The priority the message should have been logged with.
     */
    public static void assertMessageLogged( final AntExecutionResult result, final CharSequence message,
                                            final int priority )
    {
        assertNotNull( result );
        assertNotNull( message );

        BuildEvent messageLoggedEvent = null;

        for ( BuildEvent e : result.getMessageLoggedEvents() )
        {
            if ( message.equals( e.getMessage() ) )
            {
                messageLoggedEvent = e;
                break;
            }
        }

        assertNotNull( messageLoggedEvent );
        assertEquals( priority, messageLoggedEvent.getPriority() );
    }

    /**
     * Tests an Ant execution to have fired a {@code messageLogged} event holding a given message with a given priority
     * taking a reason.
     *
     * @param result The result to test.
     * @param message The message which should have been logged.
     * @param priority The priority the message should have been logged with.
     * @param reason The reason describing why {@code message} should have been logged.
     */
    public static void assertMessageLogged( final AntExecutionResult result, final CharSequence message,
                                            final int priority, final CharSequence reason )
    {
        assertNotNull( result );
        assertNotNull( message );
        assertNotNull( reason );

        BuildEvent messageLoggedEvent = null;

        for ( BuildEvent e : result.getMessageLoggedEvents() )
        {
            if ( message.equals( e.getMessage() ) )
            {
                messageLoggedEvent = e;
                break;
            }
        }

        assertNotNull( reason.toString(), messageLoggedEvent );
        assertEquals( reason.toString(), priority, messageLoggedEvent.getPriority() );
    }

    /**
     * Tests an Ant execution to have fired a {@code messageLogged} event holding a message containing a given text.
     *
     * @param result The result to test.
     * @param needle The text contained in a message which should have been logged.
     */
    public static void assertMessageLoggedContaining( final AntExecutionResult result, final CharSequence needle )
    {
        assertNotNull( result );
        assertNotNull( needle );

        BuildEvent messageLoggedEvent = null;

        for ( BuildEvent e : result.getMessageLoggedEvents() )
        {
            if ( e.getMessage() != null && e.getMessage().contains( needle ) )
            {
                messageLoggedEvent = e;
                break;
            }
        }

        assertNotNull( messageLoggedEvent );
    }

    /**
     * Tests an Ant execution to have fired a {@code messageLogged} event holding a message containing a given text
     * taking a reason.
     *
     * @param result The result to test.
     * @param needle The text contained in a message which should have been logged.
     * @param reason The reason describing why {@code needle} should have been logged.
     */
    public static void assertMessageLoggedContaining( final AntExecutionResult result, final CharSequence needle,
                                                      final CharSequence reason )
    {
        assertNotNull( result );
        assertNotNull( needle );
        assertNotNull( reason );

        BuildEvent messageLoggedEvent = null;

        for ( BuildEvent e : result.getMessageLoggedEvents() )
        {
            if ( e.getMessage() != null && e.getMessage().contains( needle ) )
            {
                messageLoggedEvent = e;
                break;
            }
        }

        assertNotNull( reason.toString(), messageLoggedEvent );
    }

    /**
     * Tests an Ant execution to have fired a {@code messageLogged} event holding a message containing a given text with
     * a given priority.
     *
     * @param result The result to test.
     * @param needle The text contained in a message which should have been logged.
     * @param priority The priority the message should have been logged with.
     */
    public static void assertMessageLoggedContaining( final AntExecutionResult result, final CharSequence needle,
                                                      final int priority )
    {
        assertNotNull( result );
        assertNotNull( needle );

        BuildEvent messageLoggedEvent = null;

        for ( BuildEvent e : result.getMessageLoggedEvents() )
        {
            if ( e.getMessage() != null && e.getMessage().contains( needle ) )
            {
                messageLoggedEvent = e;
                break;
            }
        }

        assertNotNull( messageLoggedEvent );
        assertEquals( priority, messageLoggedEvent.getPriority() );
    }

    /**
     * Tests an Ant execution to have fired a {@code messageLogged} event holding a message containing a given text with
     * a given priority taking a reason.
     *
     * @param result The result to test.
     * @param needle The text contained in a message which should have been logged.
     * @param priority The priority the message should have been logged with.
     * @param reason The reason describing why {@code message} should have been logged.
     */
    public static void assertMessageLoggedContaining( final AntExecutionResult result, final CharSequence needle,
                                                      final int priority, final CharSequence reason )
    {
        assertNotNull( result );
        assertNotNull( needle );
        assertNotNull( reason );

        BuildEvent messageLoggedEvent = null;

        for ( BuildEvent e : result.getMessageLoggedEvents() )
        {
            if ( e.getMessage() != null && e.getMessage().contains( needle ) )
            {
                messageLoggedEvent = e;
                break;
            }
        }

        assertNotNull( reason.toString(), messageLoggedEvent );
        assertEquals( reason.toString(), priority, messageLoggedEvent.getPriority() );
    }

    /**
     * Tests an Ant execution to not have fired a {@code messageLogged} event holding a given message.
     *
     * @param result The result to test.
     * @param message The message which should not have been logged.
     */
    public static void assertMessageNotLogged( final AntExecutionResult result, final CharSequence message )
    {
        assertNotNull( result );
        assertNotNull( message );

        BuildEvent messageLoggedEvent = null;

        for ( BuildEvent e : result.getMessageLoggedEvents() )
        {
            if ( message.equals( e.getMessage() ) )
            {
                messageLoggedEvent = e;
                break;
            }
        }

        assertNull( messageLoggedEvent );
    }

    /**
     * Tests an Ant execution to not have fired a {@code messageLogged} event holding a given message taking a reason.
     *
     * @param result The result to test.
     * @param message The message which should not have been logged.
     * @param reason The reason describing why {@code message} should not have been logged.
     */
    public static void assertMessageNotLogged( final AntExecutionResult result, final CharSequence message,
                                               final CharSequence reason )
    {
        assertNotNull( result );
        assertNotNull( message );
        assertNotNull( reason );

        BuildEvent messageLoggedEvent = null;

        for ( BuildEvent e : result.getMessageLoggedEvents() )
        {
            if ( message.equals( e.getMessage() ) )
            {
                messageLoggedEvent = e;
                break;
            }
        }

        assertNull( reason.toString(), messageLoggedEvent );
    }

    /**
     * Tests an Ant execution to not have fired a {@code messageLogged} event holding a message containing a given text.
     *
     * @param result The result to test.
     * @param needle The text contained in a message which should not have been logged.
     */
    public static void assertMessageNotLoggedContaining( final AntExecutionResult result, final CharSequence needle )
    {
        assertNotNull( result );
        assertNotNull( needle );

        BuildEvent messageLoggedEvent = null;

        for ( BuildEvent e : result.getMessageLoggedEvents() )
        {
            if ( e.getMessage() != null && e.getMessage().contains( needle ) )
            {
                messageLoggedEvent = e;
                break;
            }
        }

        assertNull( messageLoggedEvent );
    }

    /**
     * Tests an Ant execution to not have fired a {@code messageLogged} event holding a message containing a given text
     * taking a reason.
     *
     * @param result The result to test.
     * @param needle The text contained in a message which should not have been logged.
     * @param reason The reason describing why {@code message} should not have been logged.
     */
    public static void assertMessageNotLoggedContaining( final AntExecutionResult result, final CharSequence needle,
                                                         final CharSequence reason )
    {
        assertNotNull( result );
        assertNotNull( needle );
        assertNotNull( reason );

        BuildEvent messageLoggedEvent = null;

        for ( BuildEvent e : result.getMessageLoggedEvents() )
        {
            if ( e.getMessage() != null && e.getMessage().contains( needle ) )
            {
                messageLoggedEvent = e;
                break;
            }
        }

        assertNull( reason.toString(), messageLoggedEvent );
    }

}
