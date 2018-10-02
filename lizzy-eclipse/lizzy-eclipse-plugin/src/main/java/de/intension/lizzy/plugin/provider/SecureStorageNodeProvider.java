/*******************************************************************************
 * Copyright 2018 Intension GmbH (https://www.intension.de)
 * and other contributors as indicated by the @author tags.
 * Licensed under the Eclipse Public License - v 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.eclipse.org/legal/epl-2.0/
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package de.intension.lizzy.plugin.provider;

import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.equinox.security.storage.SecurePreferencesFactory;
import org.eclipse.equinox.security.storage.StorageException;

/**
 * Provides access to the Secure Storage of Eclipse.
 * 
 * @author <a href="mailto:ikuba@intension.de">Ingo Kuba</a>
 */
public class SecureStorageNodeProvider
{

    public static final String IDENTIFICATION       = "password";
    public static final String USER                 = "user";
    public static final String CONTENT_PROVIDER_URL = "contentProviderURL";
    public static final String PROJECT              = "project";

    private SecureStorageNodeProvider()
    {
        throw new IllegalStateException("Utility class");
    }

    private static ISecurePreferences getNode()
    {
        return SecurePreferencesFactory.getDefault().node("lizzy");
    }

    public static void put(String id, String password)
        throws StorageException
    {
        getNode().put(id, password, true);
    }

    public static String get(String id)
        throws StorageException
    {
        return getNode().get(id, "");
    }
}
