<p align="center">
  <img src="squire_square-modified.png" alt="Squire VR" width="300" />
</p>

<p align="center">
  <a href="https://github.com/kyy1-cyy/Squire-VR/releases">
    <img alt="Downloads" src="https://img.shields.io/github/downloads/kyy1-cyy/Squire-VR/total?color=00C853&label=Downloads&logo=github">
  </a>
  <a href="https://visitor-badge.laobi.icu/badge?page_id=kyy1-cyy.Squire-VR">
    <img alt="Views" src="https://visitor-badge.laobi.icu/badge?page_id=kyy1-cyy.Squire-VR&title=Views&color=00C853"/>
  </a>
</p>

>[!IMPORTANT]
Views badge has been reset so please do not take that one as the real life view.

**Jump to:** [Squire VR](#squire-vr) • [Features](#features) • [Getting Started](#getting-started) • 
[How It Works](#how-it-works) • [Release Notes (v2.2.1)](#release-notes-v221) • [HUGE THANKS](#huge-thanks) • [License](#license)

# Squire VR

Standalone tool to browse, download, and manage games from the Rookie server for Meta Quest.

## Features
- Installed label to identify installed games instantly
- One-tap uninstall from the game card with live UI refresh
- Storage page for quick space overview and management
- Popularity display and sorting improvements
- Clean, responsive UI with helpful status overlays

### Component Map
| File | Summary |
|------|--------|
| [AutoInstaller.java](file:///Users/mac/Documents/trae_projects/pp/SquireVR_decompiled/src/main/java/com/squire/vr/AutoInstaller.java) | Extracts archives, coordinates APK install, and moves OBBs to /Android/obb. Emits status callbacks for progress UI and final results. |
| [Config.java](file:///Users/mac/Documents/trae_projects/pp/SquireVR_decompiled/src/main/java/com/squire/vr/Config.java) | Central constants: public config JSON URL, default mirror baseUri, default password, and broadcast action names. |
| [DeviceStorageActivity.java](file:///Users/mac/Documents/trae_projects/pp/SquireVR_decompiled/src/main/java/com/squire/vr/DeviceStorageActivity.java) | Storage view page. Computes total/used/free space with StatFs and shows percent bar and GB breakdown; simple close action. |
| [FadeOutRunnable.java](file:///Users/mac/Documents/trae_projects/pp/SquireVR_decompiled/src/main/java/com/squire/vr/FadeOutRunnable.java) | Utility runnable to hide a view after animations, restoring alpha to 1.0 for reuse. |
| [Game.java](file:///Users/mac/Documents/trae_projects/pp/SquireVR_decompiled/src/main/java/com/squire/vr/Game.java) | Game data model: name, package, versions, popularity, sizes, install markers, and a stableId used by RecyclerView. |
| [GameAdapter.java](file:///Users/mac/Documents/trae_projects/pp/SquireVR_decompiled/src/main/java/com/squire/vr/GameAdapter.java) | Recycler adapter for cards and inline details. Handles favorites, uninstall button visibility, installed indicator, meta text (Installed vs Latest), and “Update/Resume/Download” button states with stable IDs to reduce flicker. |
| [InstallReceiver.java](file:///Users/mac/Documents/trae_projects/pp/SquireVR_decompiled/src/main/java/com/squire/vr/InstallReceiver.java) | Listens for package installer results. Triggers OBB move attempts, sends progress/status broadcasts, and reports success/fail with final obb paths. |
| [MainActivity.java](file:///Users/mac/Documents/trae_projects/pp/SquireVR_decompiled/src/main/java/com/squire/vr/MainActivity.java) | Main UI controller. Fetches VRP config and meta, builds game list, filters tabs (All/Installed/Updates/Favorites), manages queue and progress overlays, handles uninstall flow with fallbacks, and refreshes the installed package cache. |
| [SplashActivity.java](file:///Users/mac/Documents/trae_projects/pp/SquireVR_decompiled/src/main/java/com/squire/vr/SplashActivity.java) | Simple splash screen with fade‑in animation before transitioning to MainActivity. |
| [SquireManagerActivity.java](file:///Users/mac/Documents/trae_projects/pp/SquireVR_decompiled/src/main/java/com/squire/vr/SquireManagerActivity.java) | Shizuku manager panel. Requests permissions, checks binder status, shows connection state, and provides a test to list /Android/obb via Shizuku. |
| [StreamingService.java](file:///Users/mac/Documents/trae_projects/pp/SquireVR_decompiled/src/main/java/com/squire/vr/StreamingService.java) | Background streaming/downloader. Lists remote files via rclone http, streams to local storage, and kicks off extraction using runtime baseUri/password. |
| [VrpConfig.java](file:///Users/mac/Documents/trae_projects/pp/SquireVR_decompiled/src/main/java/com/squire/vr/VrpConfig.java) | Runtime config holder populated from the public JSON: baseUri + password used throughout downloads/streaming. |

## Getting Started
1. Enable “Install unknown apps” and grant file access on your Quest
2. Build or install the APK
3. Launch Squire VR and wait while the catalog config loads
4. Browse, download, and manage games

## How It Works
- The app fetches public VRP config at startup and populates runtime settings (base URI + password)
- Each game entry carries the base URI/password for downloading and streaming
- Uninstall uses a resilient multi-path flow and updates UI instantly

## Release Notes (v2.2.1)
- 🟢 Installed Label on game cards
- 🗑️ Uninstall Bin with instant refresh (Installed tab updates live)
- 🗂️ Storage page for easy space view
- 🔥 Minor popularity fixes and display consistency
- 🧹 Repo hygiene with improved .gitignore and removed unused images

## HUGE THANKS
Huge thanks to the vrpirates team for keeping the servers and app running!

## License
For personal use.
