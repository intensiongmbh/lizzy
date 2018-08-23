/*******************************************************************************
 * Copyright 2018 Intension GmbH (https://www.intension.de)
 * and other contributors as indicated by the @author tags.
 * 
 * Licensed under the Eclipse Public License - v 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *    http://www.eclipse.org/legal/epl-2.0/
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package de.intension.lizzy.converter;

/**
 * @author <a href="mailto:ikuba@intension.de">Ingo Kuba</a>
 */
public enum CaseFormat
{
    /**
     * Camel case format.
     * e.g. 'thisIsCamelCase'
     */
    CAMEL_CASE,
    /**
     * Snake format.
     * e.g. 'this_is_snake_case'
     */
    SNAKE_CASE;
}
