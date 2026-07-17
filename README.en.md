# WorldGeo-AdventureAddon

WorldGeo-AdventureAddon is a wilderness template layer on top of IMYVMWorldGeo. It provides an extensible wilderness registry, state management, and basic CRUD interfaces. It relies on the WorldGeo Core region/scope system and is isolated from WorldGeo-CommunityAddon: a region cannot be both a community and a wilderness.

## Installation

Server runtime requirements:

| Item | Version |
| --- | --- |
| Minecraft | 26.1 |
| Java | 25 or above |
| Fabric Loader | 0.18.5 or above |
| Fabric API | 0.144.3+26.1 |
| Fabric Language Kotlin | 1.13.10+kotlin.2.3.20 |

Required dependency mods:

| Mod | Version |
| --- | --- |
| Hoki | 1.1.5 |
| IMYVMWorldGeo | 26.1-1.5.1 |
| WorldGeo-CommunityAddon | 26.1-1.1.0 |

Place the Adventure jar and all required dependency jars into the server's `mods` directory. On first launch, Adventure generates default configuration files and persistent databases. Administrators apply configuration changes through `/adventure reload`.

## Commands

| Command | Permission | Purpose |
| --- | --- | --- |
| `/adventure` | anyone | Show mod version and dependency info |
| `/adventure reload` | OP level ≥ 4 | Hot-reload `Wilderness.conf` configuration |
| `/wilderness create <regionNumberId> <name>` | OP level ≥ 4 | Register the given region as wilderness (region must exist and must not be a community) |
| `/wilderness delete <regionNumberId>` | OP level ≥ 4 | Delete the given wilderness |
| `/wilderness info <regionNumberId>` | anyone | Show a single wilderness entry |
| `/wilderness list [page]` | anyone | List all wildernesses paginated |

## Data Persistence

Wilderness data is saved in the current world directory as `iwg_wilderness.db`. The database uses a binary version marker, length-prefixed records, temp-file atomic replacement, and legacy backups. If the file is corrupted on load, a `.corrupt.<timestamp>` backup is created and the server startup is aborted to avoid running with dirty state.
