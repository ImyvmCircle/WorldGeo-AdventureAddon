# WorldGeo-AdventureAddon

WorldGeo-AdventureAddon 是 IMYVMWorldGeo 与 WorldGeo-CommunityAddon 之上的野区市场层。WorldGeo 提供 Region 与 GeoScope 的地理事实，Community 提供国库、成员、发展度和协定数据，Adventure 用上游输入给野区风险、研究、指数份额和保险定价。

## 运行方向

野区是独立的探索场和异变压力场。Community 通过指数份额、国库保证金、研究设施、保险承保和周竞赛奖励进入野区市场。玩家在野区留下样本、读数、清理记录、救援记录和失败记录，Community 把记录集合转成设施进度、保险费率、指数收益和国库竞赛分。

核心市场由五个指数驱动。野区产出预估指数定价样本和指数份额，异变压力指数定价清理收益和压力下降奖励，阵亡风险指数定价救援准备金和阵亡保险，任务失败指数定价失败保险和保证金，Community 发展度指数调整国库容量、竞赛排名和机构信用。

价格跟随波动、差额、预测误差和风险变化。玩家有效操作与 Community 国库承诺满足结算条件时，周发行额进入奖励池。结算时剩余额度直接失效。指数交易、保险和转让手续费的一部分进入销毁。

## 强制依赖

IMYVMWorldGeo 承担地理层。Adventure 读取 scope 元数据、进出事件和统计快照，用于野区定价和异变窗口。

WorldGeo-CommunityAddon 承担机构层。Adventure 读取国库账户与 Community 发展度统计，通过 `CommunityApi.deposit` / `withdraw` 即时存取国库。指数持仓、保单、竞赛结算等 Adventure 派生记录由 Adventure 自身持久化。

Hoki 承担配置和翻译资源加载。翻译能力由资源文件机制扩展。

## 代码面

启动层加载配置并绑定服务。命令层提供 `/adventure`、`/adventure about`、`/adventure reload` 和 `/adventure debug context`。配置层保留 `Adventure.conf` 与 `AdventureGameplay.conf`。持久化层保存 schema 状态。WorldGeo 桥接层提供 Region 与 GeoScope 查询。

野区市场由指数引擎、操作量账本、指数份额市场、保险系统、研究设施系统和周结算系统承载。

## 集成契约

Adventure 的市场记录绑定 GeoScope 周期。WorldGeo 提供稳定 `scopeId`、scope 归属历史、统计快照和进出事件。野区开关与风险基线沿用 WorldGeo extension setting key，Adventure 读取有效值后进入指数计算。

Community 通过即时收支参与市场。Adventure 保存指数份额、保险、操作分、周结算、赞助、保单、竞赛结算全部真源；Community 只承担国库存取与发展度计算。所有跨仓库资金动作都使用 Adventure 生成的幂等 ID，由 Adventure 自查避免重发。
