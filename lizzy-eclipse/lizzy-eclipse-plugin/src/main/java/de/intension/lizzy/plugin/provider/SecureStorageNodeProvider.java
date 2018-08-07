package de.intension.lizzy.plugin.provider;

import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.equinox.security.storage.SecurePreferencesFactory;
import org.eclipse.equinox.security.storage.StorageException;

/**
 * Provides access to the Secure Storage of Eclipse.
 */
public class SecureStorageNodeProvider
{

    public static final String PASSWORD             = "password";
    public static final String USER                 = "user";
    public static final String CONTENT_PROVIDER_URL = "contentProviderURL";

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
