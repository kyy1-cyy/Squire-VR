# 🛡️ Squire VR - Technical Roadmap & Project Ledger

This document tracks the architectural evolution of **Squire VR**, an independent, native sideloading client for the Meta Quest ecosystem.

---

## 💎 Completed Milestones (v2.2.0 Stable)

| Milestone | Technical Impact |
| :--- | :--- |
| **SQL Persistence** | Migrated from flat `.txt` to **SQLite (GameDao)** for high-speed caching and data integrity. |
| **Native Sync Engine** | Integrated `rclone` and `7zip` Native .so files for direct mirror-to-HMD streaming . |
| **Intelligent Updates** | Version-comparison engine cross-referencing mirror metadata vs. local `PackageInfo`. |
| **Pre-flight Logic** | `StatFs` validation ensures **2x game size** available before extraction. |
| **Dynamic UI** | Real-time `List Observers` for tab counts. |
| **Self-Healing** | Auto-calculates `MD5` hashes from release names if server metadata is missing. |
| **Automation** | `InstallReceiver` handles post-download `/Android/obb` moves and APK execution. |
| **UI State** | Managed button states (Grey-pill) to prevent redundant downloads. |
| **In App Updates** | Updates now happen in the app for a faster and better experence |
| **Search/Indexing** | High-performance `SearchView` with `Comparator` logic for Date/Size sorting. |
| **Resume/Pause** | Have added resume and pause to downloads. |
---

## 🛠️ Development Pipeline

> [!Tip]
> ### 🟡 Priority 1: Visual Polish & VR UX
> **Goal:** Native Meta Store aesthetic.
> * **High-Density Grid:** Vertical "Poster Mode" box-art for better VR browsing.
> * **Skeleton Loading:** Shimmer effects in `GameAdapter` during high-volume SQL fetches.

> [!NOTE]
> ### 🔵 Priority 2: Scalability & Community
> **Goal:** Long-term support
