# MiauDX – Source of the Miau CQ App

![Miau CQ Logo](https://play-lh.googleusercontent.com/Z1_example_logo_url)  
*Android ham radio tool for DX cluster, logbook, and CAT control*

---

## 📡 What is Miau CQ?

**Miau CQ** is an Android app designed for amateur radio operators.  
It combines three essential tools in one place:

- **DX Cluster Client** – connect to popular clusters and view real-time DX spots, WWV reports, and announcements.  
- **Logbook** – record your QSOs with callsign, frequency, mode, RST, and timestamp.  
- **CAT Control** – control supported rigs (currently focused on **Yaesu FT-891**) via USB CAT interface.  

Play Store: [Miau CQ on Google Play](https://play.google.com/store/apps/details?id=com.Runner.CQMiau)

---

## ✨ Features

- 📡 **Real-time DX spots** with custom filters  
- 📓 **Integrated logbook** (ADIF export planned)  
- 🎛 **CAT rig control** (FT-891 tested, other Yaesu rigs in progress)  
- ⏱ **Frequency management** from cluster to rig in one tap  
- 🌙 **Dark/Light themes** (optional)  
- 🔌 **USB connection support** for CAT cables  
- 🔔 **Notifications** when a desired callsign or band appears  

---

## 🛠 Technical Details

- Written in **Java** for Android  
- Core CAT control logic derived from early FT-891 experiments  
- Minimum Android version: 8.0 (Oreo)  
- Current version: 1.06 (Jan 2025)  

---

## 🚀 Getting Started (Developers)

Clone the repo and build with Android Studio:

```bash
git clone https://github.com/dirtybug/MiauDX.git
cd MiauDX
