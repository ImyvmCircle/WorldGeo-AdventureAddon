# WorldGeo-AdventureAddon

WorldGeo-AdventureAddon 是 IMYVMWorldGeo 与 WorldGeo-CommunityAddon 之上的野区玩法层。Adventure 把地理事实、社区金库与玩家的现场行为接成一个投入-产出环路。玩家在 GeoScope 内的探测、战斗、解谜、容器、运输、交易六类行为产生操作分；操作分按七个回报通道汇入玩家钱包与社区金库；社区把资金投回研究、份额、保险与赞助，反作用于下周 scope 的产出节奏与玩家选择。野区产出节奏跟随 Minecraft 月相日波动，满月与近满月日开放探测与空中运输的全权重计分，其余月相日仅记现场证据。

服务端时区采用 Asia/Shanghai。周日 18:00 触发周结算。

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

## 玩法简要介绍

野区是 IMYVMWorldGeo 划定的高风险地理空间。玩家以 Community scope 为大本营，向野区 scope 出击。野区始终对玩家开放，产出节奏跟随 Minecraft 服务器世界月相 0–7 的当日相位变化，每日 Asia/Shanghai 零点切档。满月（月相 0）权重 1.0，近满月（月相 1, 7）0.7，半月（2, 6）0.4，蛾眉与残月（3, 5）0.2，新月（4）0.1。

玩家穿过 scope 边界进入野区时，Adventure 自动打开一个行动段。scope 公告显示当日月相、产出预估、异变压力、阵亡风险、任务失败四个指数与当日专属行为开放状态；scope 内的原版效果切换至当日月相对应的模板。

满月与近满月日开放 P1 探测与 P5 空中分支的完整计分。读取探针、采集方块与实体样本、刷掘可疑方块属于探测行为；骑乘 HappyGhast 的空中命中与空中吊运属于空中运输。其他月相日，这两类行为仍可执行，事件入现场证据进入研究提交与保险事故记录，但操作分计 0。

P2 战斗、P3 解谜、P4 容器、P5 地面运输、P6 交易在所有月相日按当日 phase_weight 入账。击杀压力点附近的怪物、触发红石与潜声与铜灯解谜、开启箱子与试炼宝库、地面拴绳货物与矿车与船与骆驼运输、向研究 NPC 与村民交易，每个动作命中事件元组时由 listener 即时打分。动作津贴 R1 即时入玩家钱包，操作分 R2 进入候选池等待周结算，装备直出 R3 在容器开启时即时入背包，研究进度 R4 在向研究 NPC 提交样本时即时进入社区金库。

玩家自主决定撤离时机。携带样本箱、拴绳货物或载具向配置撤离点或 Community scope 移动；移动过程的掉落事故与死亡降低样本的证据完整度 integrity。玩家到达撤离点或跨回 Community scope 时，本段所有现场目标与操作分条目的 integrity 固化，本段记录锁定。

死亡触发 R6 阵亡保险按保单档位赔付。未撤离样本按损坏率折算 integrity 进入保险事故记录。死亡罚没按资金流向表执行。心跳采样判定的掉线超时按死亡撤离失败处理。

社区份额市场按周节奏运行。周一 Asia/Shanghai 零点开盘公布定价区间并接受下单，周六 18:00 锁价，周日 18:00 周结算时按指数实际值赔付。

周日 18:00 触发周结算。玩家本周所有行动段的操作分按动作类别加权求和得 OperationScoreRaw，按玩家版 CES `Cap_week` 截断得 OperationScore；R2 余量按指数兑现池折算入玩家钱包，超 `ScopeWeeklyCap` 部分按销毁比例销毁。R5 份额结算与 R6 续期赔付同步执行，R7 竞赛奖金按赛季规则发放。结算同时写出 JSONL 全量归档与 Markdown 宏观报告。

## 机制完整说明

机制按七个回报通道展开。每个通道说明来源主体、触发时点、入账主体、计算与上限、销毁规则。五指数、月相日、周结算与宏观评估作为支撑机制单列。

### 五指数

野区四指数与社区一指数构成 Adventure 的定价底。

| 指数 | 含义 | 主要去向 |
| --- | --- | --- |
| 野区产出预估 | 当日可获得资源的概率与质量 | 装备直出概率、份额发行价、操作分折算上限 |
| 异变压力 | 野区危险积累 | 清理收益、份额定价、保险保费 |
| 阵亡风险 | 玩家死亡与装备损耗 | 保险保费、赔付公式 |
| 任务失败 | 清理、采样、维护、研究提交失败概率 | 失败保险、保证金 |
| Community 发展度 | 社区金库容量与机构信用 | 承保额上限、份额持仓上限、研究 tier 阈值 |

野区四指数由空间-时间底加近七天事件统计合成。空间-时间底是多倍频 Simplex 噪声、月相相位与玩家近 7 天活动热力反扣的加权和；事件统计按各指数的内容算子加权。指数对单次事件叠加事件级 jitter，避免同形事件得分完全一致。

### 月相日

野区产出节奏跟随 Minecraft 服务器世界月相 0–7 的当日相位，每日 Asia/Shanghai 零点切档。当日月相决定操作分乘子 `phase_weight` 与专属行为开放状态。

| 月相 | 阶段 | phase_weight | P1 探测 与 P5 空中分支 | 其他四类行为 |
| --- | --- | --- | --- | --- |
| 0 | 满月 | 1.0 | 计分 | 计分 |
| 1, 7 | 近满月 | 0.7 | 计分 | 计分 |
| 2, 6 | 半月 | 0.4 | 仅记现场证据 | 计分 |
| 3, 5 | 蛾眉/残月 | 0.2 | 仅记现场证据 | 计分 |
| 4 | 新月 | 0.1 | 仅记现场证据 | 计分 |

月相日切换时 Adventure 调用 WorldGeo 的时间叠加效果接口给 scope 切换效果模板。效果模板把怪谈风格的心智异常翻译成 SLOWNESS、MINING_FATIGUE、NAUSEA、BLINDNESS、DARKNESS、HUNGER、WEAKNESS、POISON、GLOWING 等原版效果的组合，按当日月相切档。

### R1 动作津贴

R1 由 Adventure 系统补贴，在玩家命中事件元组时即时入玩家钱包。金额由 `baseScore`、动作类别系数与当日 `phase_weight` 共同决定，不进入候选池，不参与周结算 CES 折算，不销毁。

### R2 操作分折算

R2 是核心回报通道。玩家本周所有行动段的操作分按动作类别加权求和得 `OperationScoreRaw`。

周获利上限采用 CES 函数。

```
Cap_week = A · ( w_M · M^ρ + w_G · G^ρ + w_T · T^ρ )^(η/ρ)
```

`M` 是金钱投入，`G` 是物品投入并按物品篮折算系数计，`T` 是有效时长。玩家版与社区版采用独立参数，`ρ < 0` 给出半互补，`η < 1` 给出规模报酬递减。回本线 `A_be` 玩家版取 `0.6 · A`，社区版取 `0.85 · A`。

按 `Cap_week_player` 截断后得 `OperationScore`，由 R2 折算入玩家钱包；指数兑现部分按 scope 周指数兑现总上限 `ScopeWeeklyCap = α · sqrt(scope_area_chunks) · A_community · (1 + β · ProductionIndex_norm)` 截断，超出部分销毁 50%，周末未兑现指数余量销毁 100%。

R2 还设两层反操纵硬约束，分别是单玩家单 scope 周操作分上限 `per_scope_player_cap`、空间-时间底层的玩家热力反扣。固定单点高频击杀、封闭刷怪结构、无移动轨迹、无读数变化、无压力点交互的行为不进入 Adventure 账户，沿用原版收益。

### R3 装备直出

R3 在试炼宝库与配置标记的箱子开启时即时计算。直出概率由产出预估指数与当日月相 `phase_weight` 共同决定。

```
P_direct = clip(p_base + k · ProductionIndex_norm,
                p_min · phase_weight,
                p_max · phase_weight)
```

默认 `p_base = 0.02, k = 0.10, p_min = 0.005, p_max = 0.15`。直出物品在 `loot-windows.json` 的 `direct_equipment` 段配置，条目标注 `rarity` 与 `min_norm`，抽奖时按 `ProductionIndex_norm ≥ min_norm` 过滤后再按 `weight` 加权抽取。低档物品（`min_norm = 0`）在所有月相日可抽，中档（`min_norm = 0.35`）在半月以上开放，高档（`min_norm = 0.70`）在近满月以上开放。

单玩家本周直出价值受 `value_per_player_weekly_cap` 限制，按 `item-basket` 折算金额累加；超出封顶后容器回退到原版战利品。多材料兑换走研究中心，配方在 `loot-windows.json` 的 `craft_recipe` 段；研究 tier 折扣降低兑换所需材料数量。

`sky_ghast` 模板对空中分支声明独立参数，把 `phase_weight` 钳到 0.40 下限，让新月日的空中容器仍享受半月等效的概率窗口，反映 HappyGhast 养护成本带来的稀缺加成。

### R4 研究进度

R4 由社区金库出资与玩家提交样本两种来源构成。研究中心收到样本或研究金时即时计入 `research_progress`。

```
research_progress_delta = funding · tier_efficiency
research_delta          = sample_value · tier_factor
```

研究有三级反馈。玩家提交样本时由 Adventure 给玩家发即时小津贴；社区累计样本数与品质达成阈值时解锁认证 tier，给社区发指数与装备折扣；研究投入合计达到设施升级阈值时在全社区范围应用研究折扣、份额佣金减免与保险折扣。认证 tier 1–5 对应研究折扣 0/10/20/30/30%。

### R5 份额结算

R5 是社区金库到社区金库的资金通道。社区在周一至周六下单认购份额，周日 18:00 周结算时按 scope 当周指数终值赔付。

合约形态有两类。趋势份额按结算指数与发行价之差线性结算。

```
payout = shares · (Index_settle − Index_issue)
```

区间份额在估计区间内命中固定赔付，未命中作废。

```
payout = shares · payout_rate · I[Index_settle ∈ range]
payout_rate = 1 / 估计区间命中概率 · (1 − 房费率)
```

发行价取上周指数 EMA。份额房费按 50% 销毁。每周对单 scope 单社区的份额持仓总额受 `index_position_per_community_cap` 限制，单社区跨 scope 的持仓总额受 `index_position_total_cap` 限制；两层上限均按社区发展度线性放缩。

### R6 阵亡保险

R6 由 Adventure 销售给玩家，承保人是社区金库。保单覆盖死亡、装备损耗与撤离失败。

保费由 `base_rate`、阵亡风险与异变压力指数、玩家近 4 周阵亡率、覆盖比例与社区发展度共同决定。

```
premium = base_rate · (1 + γ_risk · DeathRisk + γ_pressure · Pressure)
                  · (1 + γ_history · player_death_rate_4w)
                  · coverage_ratio
                  · A_community
```

覆盖比例 `coverage_ratio` 由档位决定，基础档 0.5、标准档 0.8、加强档 1.0。

赔付按装备金钱估值与未撤离样本损坏率折算。

```
payout = coverage_ratio · ( equipment_loss + sample_loss · sample_value )
       − deductible
```

保费 100% 入社区金库，手续费按 30% 销毁。死亡罚没按 70% 销毁、30% 入社区金库。保单不覆盖玩家主动跳崖、PVP 死亡与离开 scope 后超时死亡。社区金库的总承保敞口按发展度上限做准入校验，超额拒签。

### R7 竞赛奖池

R7 在赛季节点开放。竞赛覆盖一个或多个 scope、一段固定时长、一组明示规则。规则可选项包括 scope 子集、玩家或社区维度、计分公式、奖池来源与赔付方式。奖池来自玩家报名费、社区赞助拨款、Adventure 系统补贴三处。结算时按竞赛规则分发到玩家钱包与社区金库。

### 周结算与宏观评估

周日 18:00 启动结算时序，依次完成行动段冻结、社区发展度快照、scope 指数终值、份额市场结算、玩家与社区 `Cap_week` 计算、操作分折算入账、研究里程碑与保险续期处理、周日志归档、cycle 切换。每阶段在事务内完成，任一阶段失败回滚到阶段起点的快照。

宏观评估采用流量-存量两表加滚动指标。流量表记录本周新增、销毁、流转；存量表记录本周末的玩家钱包总额 M2_player、社区金库总额 M2_community、在线物品折算总值 item_stock。滚动指标按周末计算。

| 指标 | 公式 |
| --- | --- |
| CRR | `M2_community / (M2_player + M2_community)` |
| Velocity | `(paid_out_player + paid_out_community) / 平均 M2` |

CRR 目标值 0.6。CRR 区间设五级告警，低于 0.4 红色社区流动性偏低、0.4–0.5 黄色预警、0.5–0.7 绿色目标区、0.7–0.85 黄色玩家端紧缩、高于 0.85 红色玩家端枯竭。告警写入周 Markdown 报告，由管理员人工读评估并选择手动调参，调参动作写入操作日志，不做自动反馈控制。

周日志走 SQLite 主存、周 JSONL 归档、周 Markdown 报告三层。JSONL 与 Markdown 长期保留。

## 代码实现架构

Adventure 服务端进程在 Fabric 入口启动后绑定六个对外可观察的运行模块。

| 模块 | 职责 |
| --- | --- |
| 指数引擎 | 合成五指数、维护空间-时间底、写入 scope 指数快照 |
| 操作量账本 | 监听动作事件、计算 baseScore 与 integrity、写入操作分候选池 |
| 份额市场 | 接受社区金库认购、按合约形态结算 |
| 保险系统 | 签发保单、计算保费、按死亡事件赔付 |
| 研究设施系统 | 接受样本与研究金、维护 tier 进度、应用研究折扣 |
| 周结算系统 | 在 Asia/Shanghai 周日 18:00 触发结算时序、生成宏观报告 |

外部命令如下。

| 命令 | 用途 |
| --- | --- |
| `/adventure` | 命令入口 |
| `/adventure about` | 显示模组版本与依赖信息 |
| `/adventure reload` | 热加载配置 |
| `/adventure debug context` | 输出当前 cycle、scope、玩家上下文 |
| `/adventure log query <筛选>` | 查询周日志 |
| `/adventure log export <范围> <格式>` | 导出周日志 |

管理员可热更的配置面分两类。参数权重以 toml 文件维护，覆盖经济参数、指数权重、研究阈值、保险参数与结算参数。产出内容以 json 文件维护，覆盖物品篮折算系数、样本白名单、容器装备直出与解谜配置、探针档位、scope 效果模板。形态属于代码层不可热更的数学公式集中在指数引擎与周结算系统内部。

Adventure 自身持久化所有派生表，包括 cycle、scope 指数快照、行动段、操作分账本、现场目标、份额持仓、保单、研究进度、竞赛、资金流向、宏观指标与调参日志。社区金库的出入账通过 `CommunityApi.deposit` 与 `CommunityApi.withdraw` 即时执行，跨仓库资金动作使用 Adventure 生成的幂等 ID 自查避免重发。
