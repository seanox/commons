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
package com.seanox.common;

import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * TestSuite for {@link com.seanox.common.Initialize}.<br>
 * <br>
 * InitializeTest 5.2.0 20200516<br>
 * Copyright (C) 2020 Seanox Software Solutions<br>
 * All rights reserved.
 *
 * @author  Seanox Software Solutions
 * @version 5.2.0 20200516
 */
@RunWith(JUnitPlatform.class)
@Suite.SuiteClasses({
    InitializeTest_Contains.class,
    InitializeTest_Get.class,
    InitializeTest_Parse.class,
    InitializeTest_Set.class,
    InitializeTest_ToString.class
})
public class InitializeTest {
}