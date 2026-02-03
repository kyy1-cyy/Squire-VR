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
### Feature Index
| Feature | Description |
|--------|-------------|
| Auto Install | Toggle to auto‑install APKs when a download finishes, with OBB moved into /Android/obb automatically. |
| Uninstall Bin | One‑tap uninstall directly from the game card with robust fallbacks; UI updates instantly without restarting. |
| Installed Label | Clear “Installed” indicator on cards to identify installed titles at a glance. |
| Updates View | Shows Installed vs Latest versions with size, making upgrade decisions quick and obvious. |
| Storage Page | Dedicated storage screen showing used/total/free space with percentage and GB breakdown. |
| Popularity | Displays popularity score and supports sorting; shows “N/A” when data isn’t available. |
| Queue System | Clean, centered queue with background status; add multiple downloads safely and cancel pending items. |
| Favorites | Star toggle to mark favorites and a tab to filter them quickly. |
| Config Loading | Loads public VRP config at startup; baseUri/password populated and applied to downloads/streaming. |
| Download/Streaming | Streams remote files via HTTP listing and writes locally, kicking off extraction and install flows. |
| Splash Screen | Smooth fade‑in splash before launching the main UI. |
| Manager Panel | Shizuku manager view to request permissions, check service state, and test privileged file access. |
| Live Refresh | Package change listener refreshes tabs and counts instantly after install/uninstall. |

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
