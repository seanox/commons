/**
 * LIZENZBEDINGUNGEN - Seanox Software Solutions ist ein Open-Source-Projekt,
 * im Folgenden Seanox Software Solutions oder kurz Seanox genannt.
 * Diese Software unterliegt der Version 2 der Apache License.
 *
 * Seanox Commons, Advanced Programming Interface
 * Copyright (C) 2020 Seanox Software Solutions
 *  
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *  
 * http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

/**
 * Functional gateway, comparable with Wrapper/Facade/Delegate and provides the
 * communication between the elementary XAPI of Seanox Devwex and the enhanced
 * XAPI in Seanox Commons.<br>
 * This approach allows Seanox Commons and Seanox Devwex to be independent.<br>
 * In addition, delegated/cascaded Components/classes binding is supported.
 * Components/classes are bound by delegated/cascaded calls instead of
 * inheritance. This allows e.g. the insertion of additional ClassLoaders.<br>
 * The class does not use a package to simplify the configuration.<br>
 * <pre>
 *   XAPI > package.One > package.Two > package.Target
 * </pre>
 * XAPI 1.0.0 20200624<br>
 * Copyright (C) 2020 Seanox Software Solutions<br>
 * Alle Rechte vorbehalten.
 *
 * @author  Seanox Software Solutions
 * @version 1.0.0 20200624
 */
public class XAPI {

    /**
     * Creates a XAPI gateway for a module that is initiated via the constructor.
     * To configure the gateway, the module configuration are passed.
     * @param  name
     * @param  data
     * @throws Exception
     *     In the case of all unhandled unexpected exceptions
     */
    public XAPI(String name)
            throws Exception {
        // TODO:
    }

    /**
     * Creates a XAPI gateway for a server that is initiated via the constructor.
     * To configure the gateway, the name and a proprietary initialize object
     * are passed.
     * @param  name
     * @param  data
     * @throws Exception
     *     In the case of all unhandled unexpected exceptions
     */
    public XAPI(String name, Object initialize)
            throws Exception {
        // TODO:
    }

    /**
     * This method is optional and delegates the retrieval of general
     * information about the determination that should be set statically or with
     * initialization.
     *     <dir>Format for servers:</dir>
     * {@code PROTOCOL HOST-NAME:PORT} or<br>
     * {@code PROTOCOL HOST-ADRESSE:PORT}
     *     <dir>Format for modules:</dir>
     * {@code PRODUCER-MODULE/VERSION} 
     * @return general information about the determination
     */
    public String explain() {
        // TODO:
        return null;
    }
    
    /**
     * This method delegates the check and, if necessary, the manipulation of
     * the request or processes. If the request is to be answered with a server
     * status only, the filter method can set it and does not have to answer the
     * request itself. In this case, the answer is provided by the initial
     * worker implementation.
     * @param  worker
     * @param  options
     * @throws Exception
     *     In the case of all unhandled unexpected exceptions
     */    
    public void filter(Object worker, String options)
            throws Exception {
        // TODO:
    }

    /**
     * This method delegates the complete processing of the request to be
     * answered. If the request is to be answered with a server status only, the
     * service method can set this status and does not have to answer the
     * request itself. In this case, the answer is provided by the initial
     * worker implementation.
     * @param  worker
     * @param  options
     * @throws Exception
     *     In the case of all unhandled unexpected exceptions
     */
    public void service(Object worker, String options)
            throws Exception {
        // TODO:
    }

    /**
     * The destination (module or server) is requested to close before being
     * unloaded by the Service and ClassLoader. The resources, data streams and
     * processes initiated or used by the module should be closed or terminated
     * with this call.
     * @throws Exception
     *     In the case of all unhandled unexpected exceptions
     */
    public void destroy()
            throws Exception {
        // TODO:
    }
}