# WorldGeo-AdventureAddon

WorldGeo-AdventureAddon 是 IMYVMWorldGeo 与 WorldGeo-CommunityAddon 之上的野区玩法层。Adventure 把地理事实、社区金库与玩家的现场行为接成一个投入-产出环路。玩家在 GeoScope 内的探测、战斗、解谜、容器、运输、交易六类行为产生操作分；操作分与现场产出进入行动津贴、冒险收益、装备掉落、社区研究、指数份额、阵亡保险六类机制；社区把资金投回研究、份额与保险，反作用于下周区域的产出节奏与玩家选择。野区产出节奏跟随 Minecraft 月相日波动，满月与近满月日开放探测与空中运输的全权重计分，其余月相日仅记现场证据。

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

野区是 IMYVMWorldGeo 划定的高风险地理空间。玩家以 Community 区域为大本营，向野区出击。野区始终对外开放，产出与风险随当日月相起伏，满月与近满月为高产高风险的窗口，新月趋于平淡。

进入野区即开启一段冒险。区域公告展示当日月相、产出、压力、风险与任务失败四项指数，并标明当日开放的专属行为。野区氛围与效果随月相切换。

野区行为分为探测、采样、战斗、解谜与容器、空中分支、地面运输与贸易六类。探测与空中分支仅在满月与近满月日记入收益，其他月相日仅留存现场记录。其余四类在所有月相日均计入收益。每个动作即时入账小额行动津贴，并按当日月相强弱与近期热度折算为操作分。容器开启直接掉落装备，向研究 NPC 提交样本推进社区研究进度。

玩家自主选择撤离时机。携带样本、拴绳货物或载具返回撤离点或 Community 区域即完成本段冒险，途中掉落与死亡会损耗样本完整度，抵达终点后本段记录锁定。死亡触发阵亡保险，按保单等级赔付玩家损失，未撤离样本按损耗折入保险事故。

社区层面，Community 金库承保玩家保单、出资认购野区指数份额、出资推进研究进度。份额市场按周运行，周一开盘下单，周六锁价，周日按指数结果赔付。研究进度跨越阈值后，整个 Community 在野区行动获得成本折扣与收益加成，并获得一次性金钱与装备包奖励。

每周日傍晚触发周结算。本周冒险所得按动作类别汇总入账，与指数兑现一并折算进玩家钱包；阵亡保险与份额市场依序结算。结算同时生成本周存档与宏观周报。

## 机制完整说明

机制按六类玩家可感知的收益机制展开。每类机制说明来源主体、触发时点、入账主体、计算与上限、销毁规则。五指数、月相日、周结算与宏观评估作为支撑机制单列。

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

### 行动津贴

行动津贴由 Adventure 系统补贴，在玩家命中事件元组时即时入玩家钱包。

```
ActionAllowance(event) = α_allowance[class] · baseScore[event.type] · w_class[class] · phase_weight · (1 − heat_penalty)
```

`α_allowance` 按动作类别配置，物流贸易给 0.30 反映“小、快、即时落袋”，解谜与宝库给 0.20 反映高风险窗口，战斗与空中分支给 0.10，采样给 0.05，探测给 0.10。行动津贴与冒险收益结算共用反操纵筛子：行动段命中段级硬判定时行动津贴一并归 0；事件 5 分钟滚动窗口内同 chunk 同事件类型超过 `heat_threshold_kills_per_minute = 8` 时按 `heat_penalty = 1 − 0.5^(excess/threshold)` 软衰减。行动津贴在事件即时入钱包，不参与冒险收益的双轨兑现、不参与周结算 CES 折算、不销毁。

### 冒险收益结算

冒险收益结算是核心收益机制。事件操作分按动作类别累加：

```
opScore(event)        = baseScore[event.type] · w_class[class] · phase_weight · integrity · (1 − heat_penalty)
session.opScore       = Σ_event opScore(event) · (1 − antimanip_kill[session])
PlayerScopeWeekScore  = clip( Σ_session session.opScore, 0, per_scope_player_cap )
OperationScoreRaw     = Σ_scope PlayerScopeWeekScore
OperationScore        = min( OperationScoreRaw, Cap_week_player )
```

12 种事件按主要交互形态归入 6 大动作类别：`read` 进探测；`sample_block / sample_entity / brush` 进采样；`combat` 进战斗；`puzzle / vault / chest` 进解谜与宝库；`air_hit / air_haul` 进空中分支；`logistics / trade` 进物流贸易。`baseScore` 在 `economy.toml [operation_score]` 配置，`w_class` 在 `[operation_score.class_weight]` 配置（探测=0.6 / 采样=0.8 / 战斗=1.0 / 解谜与宝库=1.2 / 空中分支=1.3 / 物流贸易=0.5），冒险收益结算把高风险窗口的回报溢价推到解谜与宝库、空中分支。

周获利上限采用 CES 函数。

```
Cap_week = A · ( w_M · M^ρ + w_G · G^ρ + w_T · T^ρ )^(η/ρ)
```

`M` 是金钱投入，`G` 是物品投入并按物品篮折算系数计，`T` 是有效时长。玩家版与社区版采用独立参数，`ρ < 0` 给出半互补，`η < 1` 给出规模报酬递减。回本线 `A_be` 玩家版取 `0.6 · A`，社区版取 `0.85 · A`。

`OperationScore` 走双轨兑现路径。即时部分按事件 tick 折算入钱包，周末部分进 scope 指数兑现池：

```
immediate_cash(event)    = immediate_cash_ratio · opScore(event) · realization_rate(scope, t)
deferred_score(p, s)     = (1 − immediate_cash_ratio) · PlayerScopeWeekScore(p, s)
realization_rate(s, t)   = base_rate · (1 + γ_index · ProductionIndex_norm(s, t))
deferred_cash(p, s)      = min( deferred_score · realization_rate(s, t_end), ScopeWeeklyCapRemaining(s) )
burn_overflow(p, s)      = (deferred_score · realization_rate − deferred_cash) · overflow_burn_ratio
burn_unredeemed(s)       = pool_residue_at_week_end(s)
```

默认 `immediate_cash_ratio = 0.40`、`base_rate = 0.05` 元/分、`γ_index = 0.40`、`overflow_burn_ratio = 0.50`。兑现率在 `0.05–0.07` 元/分之间随 scope 产出热度浮动。指数兑现池受 scope 周指数兑现总上限 `ScopeWeeklyCap = α · sqrt(scope_area_chunks) · A_community · (1 + β · ProductionIndex_norm)` 截断，超出部分销毁 50%，周末未兑现指数余量销毁 100%。

冒险收益结算还设两层反操纵硬约束：段级判定与事件级热力衰减。段级硬判定四条独立信号——单点高频（5 分钟窗口内 80% 击杀点落在 4 方块半径立方内）、封闭刷怪（移动凸包面积 / 段时长 < 30 m²/min）、无读数变化（探测与采样类别下读数增量同时为 0）、段长 AFK（事件密度 < 0.5 事件/分钟 且段时长 ≥ 10 分钟）——命中任意一条整段操作分置 0、回退原版掉落、不进 Adventure 资金流。事件级热力衰减按 chunk × 事件类型滚动统计，超出阈值后该类型在该 chunk 内的操作分按几何级数衰减。固定单点高频击杀、封闭刷怪结构、AFK 段沿用原版收益。

### 装备掉落

装备掉落在试炼宝库与配置标记的箱子开启时即时计算。直出概率由产出预估指数、当日月相 `phase_weight` 与 scope×模板 archetype 匹配系数 `af` 共同决定。

```
af       = archetype_match_matrix[tpl.template_archetype][scope.archetype]
P_direct = clip(p_base + k · ProductionIndex_norm,
                p_min · phase_weight · af,
                p_max · phase_weight · af)
```

默认 `p_base = 0.02, k = 0.10, p_min = 0.005, p_max = 0.15`。`af` 把 scope 形态（`desert / aquatic / aerial / underground / forest / plains`）与模板玩法分支（`combat / puzzle / vault / aerial / logistics / trade`）匹配到 0.3–1.5 的系数：对角线匹配抬升直出概率（aerial 模板 × aerial scope = 1.5），远离匹配压制（aerial 模板 × desert scope = 0.4），错配场景仍保留基础概率不归零。矩阵均值约 0.85，不放大全局期望。直出物品在 `loot-windows.json` 的 `direct_equipment` 段配置，条目标注 `rarity` 与 `min_norm`，抽奖时按 `ProductionIndex_norm ≥ min_norm` 过滤后再按 `weight` 加权抽取。低档物品（`min_norm = 0`）在所有月相日可抽，中档（`min_norm = 0.35`）在半月以上开放，高档（`min_norm = 0.70`）在近满月以上开放。

单玩家本周装备产出价值受 `value_per_player_weekly_cap` 限制。装备掉落与研究中心 craft 产出的装备共享同一封顶，按 `item-basket` 折算金额累加；装备掉落触发封顶时容器回退到原版战利品，研究中心 craft 触发封顶时拒绝下单。本周走运抽到高价值直出后 craft 配额自动收紧；反之 craft 配额宽松，让运气派与规划派共享一个产出节奏。

多材料兑换走研究中心，配方在 `loot-windows.json` 的 `craft_recipe` 段。craft 成本同时受研究 tier 折扣与 scope 直出热度影响：`craft_cost_eff = recipe.base_materials · (1 - cost_discount[tier]) · (1 + α · scope_direct_value_norm)`，默认 `α = 0.30`。scope 本周直出火热时同 scope 同 archetype 装备的 craft 成本最多上浮 30%；scope 直出冷清时 craft 成本回落到基线。研究中心同时开放拆解工位，玩家投入直出装备按 rarity 退回基础/研究/高级材料（低档 0.70、中档 0.60、高档 0.50），把溢出的低档存货流入研究材料池。

`sky_ghast` 模板对空中分支声明独立参数，把 `phase_weight` 钳到 0.40 下限，让新月日的空中容器仍享受半月等效的概率窗口，反映 HappyGhast 养护成本带来的稀缺加成。

### 社区研究

社区研究走单轴累加器。社区金库出资与玩家向研究 NPC 提交样本共用同一进度：

```
research_progress(c) += funding + sample_value
```

`sample_value` 在 `sample-whitelist.json` 单一字段折算，funding 即金库出资金额。两条入口同权重计入，没有双轴分流。

社区建成研究中心默认获得 tier 1，tier 2–5 由 `research_progress` 越过阈值在周日 18:00 结算时升档，阈值默认 `[0, 0, 10000, 35000, 90000, 200000]`，并随 `A_community` 放缩。

tier 给两个系数：成本折扣 `cost_discount` 与收益加成 `yield_bonus`。

| 系数 | tier 1 | tier 2 | tier 3 | tier 4 | tier 5 |
| --- | --- | --- | --- | --- | --- |
| `cost_discount` | 0% | 8% | 16% | 24% | 32% |
| `yield_bonus` | 0% | 5% | 10% | 15% | 20% |

`cost_discount` 作用于探针购置、采样器购置、研究中心提交手续费、份额房费率、保险保费、研究中心 craft 配方材料；`yield_bonus` 作用于冒险收益结算系数、装备掉落概率 `P_direct` 中心值、样本提交即时津贴。两类系数对所有社区成员同步生效。

研究有两类反馈节拍。玩家提交合格样本时即时入账小津贴 `bounty = clip(sample_value · k_bounty, 50, 1500) · (1 + yield_bonus[tier])`，默认 `k_bounty = 0.30`。社区 `research_progress` 越过下一档阈值时在周日 18:00 一次性发放升级奖励：金额 `cash_bonus = α · tier · A_community^β`（默认 `α = 5000, β = 0.60`）入社区金库，同时按 `loot-windows.json` 的 `tier_unlock_bundle.tier{k}` 段发放一袋装备到社区共享库存。同一周内同时越过多档阈值按档数顺序逐档结算。

### 指数份额

指数份额是社区金库到社区金库的资金通道。社区在周一至周六下单认购份额，周日 18:00 周结算时按 scope 当周指数终值赔付。合约下单后保持到结算，不开平仓与对冲。

每张合约绑定一个指数 `index_kind ∈ {production, pressure, death_risk, mission_fail}`。同一 scope 同一社区可同时开四种指数的合约。风险类指数（pressure / death_risk / mission_fail）方差更高，房费率上浮 0.03（产出预估 0.05，其余三类 0.08）。

合约形态分两类。趋势份额绑定看涨或看跌方向，按结算指数与发行价之差线性结算：

```
subscription_cost = shares · price_issue · margin_ratio
gross_payout      = shares · direction_sign · (Index(s, t_end) − price_issue) · (1 − house_rate)
net_payout        = max(0, gross_payout + subscription_cost) − subscription_cost
```

默认 `margin_ratio = 1.00`（满保证金，无杠杆）。盈利时差额扣房费返还金库，亏损时本金已扣相当于亏掉认购成本即止损，金库无需补差。`direction_sign(long) = +1, direction_sign(short) = −1`。

区间份额采用周开盘时预设的五档区间（重挫 / 偏低 / 中间 / 偏高 / 暴涨），边界取近 12 周指数 `empirical_cdf` 的 P10 / P30 / P70 / P90 分位点。社区在五档里选一档下单，不可自定义区间。

```
payout_rate = (1 − house_rate) / max(P_hit_estimate, P_hit_min)
payout      = shares · payout_rate · I[Index(s, t_end) ∈ band]
```

默认 `P_hit_min = 0.05` 防止极端档赔率爆炸。发行价取上周指数 EMA。份额房费按 50% 销毁。

持仓上限分三层：单 scope 单社区受 `index_position_per_community_cap` 限制，单社区跨 scope 受 `index_position_total_cap` 限制，同 scope 全社区合计受 `ScopeTotalPositionCap = base_scope_cap · (1 + β · ProductionIndex_norm)` 限制（默认 `base_scope_cap = 1500000, β = 0.5`）。前两层按社区发展度放缩，第三层按 scope 产出指数放缩，防止一片 scope 被群体押注冲击周结算赔付侧。本周开盘后新建的 scope 跳过本周份额市场，下周一并入。

### 阵亡保险

阵亡保险由 Adventure 销售给玩家，承保人是社区金库。保单覆盖死亡时丢失的装备价值与未撤离的探索样本价值。同一玩家在同 scope 一周限购 1 张保单，保单生效至本周日 18:00 周结算时点，未理赔随结算到期作废，保费不退。

保单分三档：

| 档位 | 覆盖比例 | 保费乘数 | 免赔率 |
| --- | --- | --- | --- |
| 基础 basic    | 0.50 | 1.0 | 0.30 |
| 标准 standard | 0.80 | 1.6 | 0.15 |
| 加强 premium  | 1.00 | 2.4 | 0.05 |

保费乘数凸增让加强档保费溢价超过覆盖比例线性放缩，玩家在风险偏好下作取舍而不无脑选最高档。

保费由阵亡风险指数、scope 异变压力、玩家近 4 周阵亡率、档位与社区发展度共同决定：

```
premium = base_rate · coverage_premium_multiplier[tier]
                   · (1 + γ_risk · DeathRiskIndex_norm + γ_pressure · PressureIndex_norm)
                   · (1 + γ_history · player_death_rate_4w)
                   · A_community
```

赔付按 `PlayerDeathEvent` 即时触发：

```
gross_payout = coverage_ratio[tier] · ( equipment_loss_value + sample_loss_value )
payout       = max(0, gross_payout · (1 − deductible_ratio[tier])) · prorate_factor
```

装备价值与样本价值取 `item-basket` 即时折算金额。拒赔不消耗保单额度且不退保费，包含三种情形：PVP 死亡、主动跳崖（FALL 死亡且坠落高度 ≥ `voluntary_fall_height_threshold = 30` 方块且 `voluntary_fall_damage_window_seconds = 60` 秒内无敌对实体伤害）、scope 外超时死亡（脱离 scope 边界超过 `scope_exit_grace_seconds = 600` 秒后阵亡）。

社区金库承保受三层上限约束：单保单赔付额上限 5000 元，同 scope 同社区累计承保 `≤ base_underwriting_cap · A_community · (1 − δ · PressureIndex_norm)`（base = 200000 元，δ = 0.40），社区全局承保 `≤ 0.60 · TreasuryBalance`。金库余额低于 50000 元时该社区停售新保单。

单 scope 单周累计赔付熔断：`ScopePayoutCapWeekly = base_scope_payout_cap · A_community_aggregate · (1 + ε · ProductionIndex_norm)`（base = 80000 元，ε = 0.30）。超过该上限后剩余索赔按 `prorate_factor` 比例赔付，周日 18:00 周结算时按 prorate 重新对账，超付差额下周从相关社区金库扣回。熔断对账在指数份额结算前执行。

保费 100% 入社区金库，手续费按 30% 销毁。死亡罚没按 70% 销毁、30% 入社区金库。

### 周结算与宏观评估

周日 18:00 启动结算时序，依次完成行动段冻结、社区发展度快照、scope 指数终值、阵亡保险熔断对账与 prorate 扣回、指数份额市场结算、玩家与社区 `Cap_week` 计算、宏观反馈控制器、操作分折算入账、研究里程碑与保险续期处理、周日志归档、cycle 切换。每阶段在事务内完成，任一阶段失败回滚到阶段起点的快照。

宏观评估采用流量-存量两表加滚动指标。流量表记录本周新增、销毁、流转；存量表记录本周末的玩家钱包总额 M2_player、社区金库总额 M2_community、在线物品折算总值 item_stock。滚动指标按周末计算。

| 指标 | 公式 |
| --- | --- |
| CRR | `M2_community / (M2_player + M2_community)` |
| Velocity | `(paid_out_player + paid_out_community) / 平均 M2` |

CRR 目标值 0.6。CRR 区间设五级告警：低于 0.4 红色社区流动性偏低、0.4–0.5 黄色预警、0.5–0.7 绿色目标区、0.7–0.85 黄色玩家端紧缩、高于 0.85 红色玩家端枯竭。告警等级作为三层反馈环的输入。

宏观反馈环分三层。第一层公式内反向调节，PressureIndex、DeathRiskIndex、CRR 直接出现在冒险收益、指数份额、阵亡保险的当周公式中，事件 tick 即生效：冒险收益兑现率乘 `(1 − μ_pressure · PressureIndex_norm)`、指数份额区间宽度按 `(1 + ξ_band · |CRR − 0.6|)` 扩张、阵亡保险保费按 PressureIndex 与 DeathRiskIndex 上浮且承保上限按 PressureIndex 收紧。默认 `μ_pressure = 0.30`、`ξ_band = 0.50`，系数集中在 `economy.toml [macro_feedback]`。

第二层自动微调器（autopilot）按 `settlement.toml [autopilot]` 段开关，默认关闭。开启后周日 18:00 `compute_macro_feedback` 子阶段算出 CRR / PressureIndex / DeathRiskIndex 全局误差，按 `Δratio = gain · error` 调节三个白名单参数：`realization.base_rate`（CRR 反馈）、`shares.base_pos_per_scope`（PressureIndex 反馈）、`insurance.base_rate`（DeathRiskIndex 反馈）。单参数单周变化封顶 ±2%，硬边界 `[初值·0.5, 初值·2.0]`，越界冻结并红色告警。所有 autopilot 调整写 `economy_adjust(source="autopilot")` 表，24 小时内可由管理员命令回滚。autopilot 关闭时仍把"建议参数包"写入周报与 `tune_suggestion` 表，等待人工应用。

第三层管理员游戏内命令是最高权限层，详见下文 `/adventure tune` 命令族。

周日志走 SQLite 主存、周 JSONL 归档、周 Markdown 报告三层。JSONL 与 Markdown 长期保留。Markdown 报告新增 `autopilot_adjustments` 与 `tune_suggestions` 两节，记录本周自动调整明细与下周建议参数包。

## 代码实现架构

Adventure 服务端进程在 Fabric 入口启动后绑定七个对外可观察的运行模块。

| 模块 | 职责 |
| --- | --- |
| 指数引擎 | 合成五指数、维护空间-时间底、写入 scope 指数快照 |
| 操作量账本 | 监听动作事件、计算 baseScore 与 integrity、写入 `operation_ledger` 并按双轨即时与 deferred 分流落账 |
| 份额市场 | 接受社区金库认购、按合约形态结算 |
| 保险系统 | 签发保单、计算保费、按死亡事件赔付 |
| 研究设施系统 | 接受样本与研究金、维护 tier 进度、应用研究折扣 |
| 周结算系统 | 在 Asia/Shanghai 周日 18:00 触发结算时序、生成宏观报告 |
| 宏观反馈控制器 | 在 `compute_macro_feedback` 子阶段计算 CRR / Pressure / DeathRisk 误差、写 `economy_adjust` 与 `tune_suggestion`、响应 `/adventure tune` 命令族 |

外部命令如下。

| 命令 | 用途 |
| --- | --- |
| `/adventure` | 命令入口 |
| `/adventure about` | 显示模组版本与依赖信息 |
| `/adventure reload` | 热加载配置 |
| `/adventure debug context` | 输出当前 cycle、scope、玩家上下文 |
| `/adventure log query <筛选>` | 查询周日志 |
| `/adventure log export <范围> <格式>` | 导出周日志 |
| `/adventure tune list [last N]` | 列出最近 N 条调参记录 |
| `/adventure tune get <param.path>` | 读取参数当前生效值与初始值 |
| `/adventure tune set <param.path> <value> [reason]` | 立即改写参数到指定值，下一事件 tick 起生效 |
| `/adventure tune apply <suggestion_id>` | 应用本周周报中编号 `suggestion_id` 的建议参数包 |
| `/adventure tune rollback <adjust_id>` | 回滚指定 `adjustId` 到 `old_value`，限本 cycle 与 24 小时窗口 |
| `/adventure tune autopilot on\|off` | 切换自动微调器开关，下周日 18:00 起生效 |
| `/adventure tune dryrun` | 输出本周 autopilot 开启时拟写入的 Δratio，不执行 |

`/adventure tune` 命令族需 OP 等级 ≥ 4，所有执行结果写入 `economy_adjust` 操作日志，`source` 字段区分 `autopilot / manual / rollback / suggestion_apply`。命令前对白名单参数与硬边界做校验，越界拒绝并红色提示。

管理员可热更的配置面分两类。参数权重以 toml 文件维护，覆盖经济参数、指数权重、研究阈值、保险参数与结算参数。产出内容以 json 文件维护，覆盖物品篮折算系数、样本白名单、容器装备直出与解谜配置、探针档位、scope 效果模板。形态属于代码层不可热更的数学公式集中在指数引擎、周结算系统与宏观反馈控制器内部。

Adventure 自身持久化所有派生表，包括 cycle、scope 指数快照、行动段、操作分账本、现场目标、份额持仓、保单、研究进度、资金流向、宏观指标、调参日志与建议参数包。社区金库的出入账通过 `CommunityApi.deposit` 与 `CommunityApi.withdraw` 即时执行，跨仓库资金动作使用 Adventure 生成的幂等 ID 自查避免重发。
