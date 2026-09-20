# CampusPass — Android App

The mobile app portion of the CampusPass system — a unified platform for managing campus events, ticketing, and attendance.

## Overview

CampusPass lets students discover campus events, register with QR-based tickets, check in via QR scanning, and give feedback. Club organizers can create events, manage capacity, scan tickets at entry, and export attendance reports.

## Related Repository

**Backend API:** [campuspass-api](https://github.com/katlegomothapo10/campuspass-api)

## Tech Stack

| Layer | Technology |
|-------|------------|
| Language | Kotlin |
| UI | Jetpack Compose + Material 3 |
| Architecture | MVVM |
| Networking | Retrofit 2 + OkHttp |
| Local Storage | DataStore Preferences |
| Authentication | JWT + Google SSO (Credential Manager) |
| Navigation | Navigation Compose |

## Setup Instructions

### Prerequisites

- Android Studio (latest version)
- JDK 17+
- Android SDK 37
- **Running backend API** — see [campuspass-api](https://github.com/katlegomothapo10/campuspass-api)
- **Emulator with Google Play Services** (for Google SSO testing)

### Installation

1. Clone the repository:
```bash
git clone https://github.com/katlegomothapo10/campuspass-app.git