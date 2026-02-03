# Squire VR

Standalone tool to browse, download, and manage games from the Rookie server for Meta Quest.

## Features
- Installed label to identify installed games instantly
- One-tap uninstall from the game card with live UI refresh
- Storage page for quick space overview and management
- Popularity display and sorting improvements
- Clean, responsive UI with helpful status overlays

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

## License
For personal use and testing only.
