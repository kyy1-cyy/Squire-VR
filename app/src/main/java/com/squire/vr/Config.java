package com.squire.vr;

public class Config {
    // API & Sync URLs
    public static final String VRP_CONFIG_URL = "https://vrpirates.wiki/downloads/vrp-public.json";
    public static final String MIRROR_BASEURI_URL = "https://there-is-a.vrpmonkey.help/";
    
    // Default Secrets (Base64 is preferred, these are fallbacks)
    public static final String VRP_DEFAULT_PASSWORD = "gL59VfgPxoHR";
    
    // Broadcast Intent Actions
    public static final String ACTION_PROGRESS = "com.squire.vr.DOWNLOAD_PROGRESS";
}
