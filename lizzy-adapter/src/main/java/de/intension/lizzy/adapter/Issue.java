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
package de.intension.lizzy.adapter;

/**
 * Wrapper object for issues from requirement management systems.
 * 
 * @author <a href="mailto:ikuba@intension.de">Ingo Kuba</a>
 */
public class Issue
{

    private String key;
    private String title;
    private String description;

    /**
     * Unique identifier of the issue.
     */
    public String getKey()
    {
        return key;
    }

    public Issue setKey(String key)
    {
        this.key = key;
        return this;
    }

    /**
     * Summarizing title of the issue.
     */
    public String getTitle()
    {
        return title;
    }

    public Issue setTitle(String title)
    {
        this.title = title;
        return this;
    }

    /**
     * Content of the description text.
     */
    public String getDescription()
    {
        return description;
    }

    public Issue setDescription(String description)
    {
        this.description = description;
        return this;
    }
}
