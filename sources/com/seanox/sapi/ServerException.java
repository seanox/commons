/**
 *  LIZENZBEDINGUNGEN - Seanox Software Solutions ist ein Open-Source-Projekt,
 *  im Folgenden Seanox Software Solutions oder kurz Seanox genannt.
 *  Diese Software unterliegt der Version 2 der GNU General Public License.
 *
 *  Seanox Commons, Advanced Programming Interface
 *  Copyright (C) 2017 Seanox Software Solutions
 *
 *  This program is free software; you can redistribute it and/or modify it
 *  under the terms of version 2 of the GNU General Public License as published
 *  by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT
 *  ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 *  FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 *  more details.
 *
 *  You should have received a copy of the GNU General Public License along
 *  with this program; if not, write to the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package com.seanox.sapi;

/**
 *  ServerException, a general exception a server can throw when it encounters
 *  difficulty.<br>
 *  <br>
 *  ServerException 1.0 20171210<br>
 *  Copyright (C) 2017 Seanox Software Solutions<br>
 *  All rights reserved.
 *
 *  @author  Seanox Software Solutions
 *  @version 1.0 20171210
 */
public class ServerException extends Exception {
    
    /** version for serialization */
    private static final long serialVersionUID = -2434861060488241347L;

    /** Constructs a new server exception. */
    public ServerException() {
        super();
    }

    /**
     *  Constructs a new server exception with the specified message.
     *  The message can be written to the server log and/or displayed for the
     *  user.
     *  @param message text of the exception message
     */
    public ServerException(String message) {
        super(message);
    }

    /**
     *  Constructs a new exception with the specified detail message and cause.
     *  Note that the detail message associated with cause is not automatically
     *  incorporated in this exception's detail message.
     *  @param message detail message
     *  @param cause original {@link Throwable} that caused this exception
     */
    public ServerException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     *  Constructs a new exception with the specified cause.
     *  @param cause original {@link Throwable} that caused this exception
     */
    public ServerException(Throwable cause) {
        super(cause);
    }
}