# WorldGeo-AdventureAddon

WorldGeo-AdventureAddon 是 IMYVMWorldGeo 之上的野区模板层，提供可扩展的野区注册、状态管理与基础增删改查接口。它依赖 WorldGeo Core 的 region/scope 体系，并与 WorldGeo-CommunityAddon 隔离：一个 region 不能同时是社区与野区。

## 安装说明

服务端运行环境如下。

| 项 | 版本 |
| --- | --- |
| Minecraft | 26.1 |
| Java | 25 及以上 |
| Fabric Loader | 0.18.5 及以上 |
| Fabric API | 0.144.3+26.1 |
| Fabric Language Kotlin | 1.13.10+kotlin.2.3.20 |

强制依赖模组如下。

| 模组 | 版本 |
| --- | --- |
| Hoki | 1.1.5 |
| IMYVMWorldGeo | 26.1-1.5.1 |
| WorldGeo-CommunityAddon | 26.1-1.1.0 |

部署流程将 Adventure 与全部强制依赖的 jar 文件放入服务端 `mods` 目录，启动服务端，Adventure 在首次启动时生成默认配置文件与持久化数据库。管理员通过 `/adventure reload` 热加载配置变更。

## 命令

| 命令 | 权限 | 用途 |
| --- | --- | --- |
| `/adventure` | 任何人 | 显示模组版本与依赖信息 |
| `/adventure reload` | OP 等级 ≥ 4 | 热加载 `Wilderness.conf` 配置 |
| `/wilderness create <regionNumberId> <name>` | OP 等级 ≥ 4 | 将指定 region 注册为野区（该 region 必须存在且不能是社区） |
| `/wilderness delete <regionNumberId>` | OP 等级 ≥ 4 | 删除指定野区 |
| `/wilderness info <regionNumberId>` | 任何人 | 查看单个野区信息 |
| `/wilderness list [page]` | 任何人 | 分页列出所有野区 |

## 数据持久化

野区数据保存在当前存档目录下的 `iwg_wilderness.db`。数据库采用二进制版本标记、单条记录长度帧、临时文件原子替换与旧版备份，加载损坏时会生成 `.corrupt.<timestamp>` 备份并阻止服务器继续启动，避免带脏状态运行。
