package rikka.shizuku;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import rikka.shizuku.Shizuku;

/* loaded from: classes4.dex */
class ShizukuServiceConnections {
    private static final Map<String, ShizukuServiceConnection> CACHE = Collections.synchronizedMap(new HashMap());

    ShizukuServiceConnections() {
    }

    static ShizukuServiceConnection get(Shizuku.UserServiceArgs args) {
        String key = args.tag != null ? args.tag : args.componentName.getClassName();
        ShizukuServiceConnection connection = CACHE.get(key);
        if (connection == null) {
            ShizukuServiceConnection connection2 = new ShizukuServiceConnection(args);
            CACHE.put(key, connection2);
            return connection2;
        }
        return connection;
    }

    static void remove(ShizukuServiceConnection connection) {
        List<String> keys = new ArrayList<>();
        for (Map.Entry<String, ShizukuServiceConnection> entry : CACHE.entrySet()) {
            if (entry.getValue() == connection) {
                keys.add(entry.getKey());
            }
        }
        for (String key : keys) {
            CACHE.remove(key);
        }
    }
}
