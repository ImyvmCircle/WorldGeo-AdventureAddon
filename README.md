# WorldGeo-AdventureAddon

WorldGeo-AdventureAddon 是 IMYVMWorldGeo 与 WorldGeo-CommunityAddon 之上的野区市场层。WorldGeo 提供 Region 与 GeoScope 的地理事实，Community 提供国库、成员、发展度和协定数据，Adventure 用上游输入给野区风险、研究、特许权和保险定价。

## 运行方向

野区是独立的探索场和异变压力场。Community 通过限时特许权、国库保证金、研究项目、保险承保和周竞赛奖励进入野区市场。玩家在野区留下样本、读数、清理记录、救援记录和失败记录，Community 把记录集合转成研究信用、保险费率、特许权收益和国库竞赛分。

核心市场由五个指数驱动。野区产出预估指数定价样本和研究债，异变压力指数定价清理权和压力下降奖励，阵亡风险指数定价救援准备金和阵亡保险，任务失败指数定价失败保险和特许保证金，Community 发展度指数调整国库容量、竞赛排名和机构信用。

价格跟随波动、差额、预测误差和风险变化。玩家有效操作与 Community 国库承诺满足结算条件时，周发行额进入奖励池。结算时剩余额度直接失效。特许权、保险、研究债和转让手续费的一部分进入销毁。

## 强制依赖

IMYVMWorldGeo 承担地理层。Adventure 读取 scope 元数据、进出事件、统计快照和邻接数据，用于野区定价和异变窗口。

WorldGeo-CommunityAddon 承担机构层。Adventure 读取国库账户、Community 发展度统计、限时特许权记录和周竞赛结算记录。

Hoki 承担配置和翻译资源加载。翻译能力由资源文件机制扩展。

## 代码面

启动层加载配置并绑定服务。命令层提供 `/adventure`、`/adventure about`、`/adventure reload` 和 `/adventure debug context`。配置层保留 `Adventure.conf` 与 `AdventureGameplay.conf`。持久化层保存 schema 状态。WorldGeo 桥接层提供只读 Region 与 GeoScope 查询。

野区市场由指数引擎、操作量账本、特许权市场、保险系统、研究系统和周结算系统承载。
