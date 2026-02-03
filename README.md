<p align="center">
  <img src="squire_square-modified.png" alt="Squire VR" width="120" />
</p>

<p align="center">
  <a href="https://github.com/kyy1-cyy/Squire-VR/releases">
    <img alt="Downloads" src="https://img.shields.io/github/downloads/kyy1-cyy/Squire-VR/total?color=00C853&label=Downloads&logo=github">
  </a>
  <a href="https://hits.seeyoufarm.com">
    <img alt="Views" src="https://hits.seeyoufarm.com/api/count/incr/badge.svg?url=https%3A%2F%2Fgithub.com%2Fkyy1-cyy%2FSquire-VR&count_bg=%2300C853&title_bg=%23333333&icon=github.svg&icon_color=%23FFFFFF&title=Views&edge_flat=false"/>
  </a>
</p>

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
