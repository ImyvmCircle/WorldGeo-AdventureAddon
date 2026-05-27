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

R3 在试炼宝库与配置标记的箱子开启时即时计算。直出概率由产出预估指数、当日月相 `phase_weight` 与 scope×模板 archetype 匹配系数 `af` 共同决定。

```
af       = archetype_match_matrix[tpl.template_archetype][scope.archetype]
P_direct = clip(p_base + k · ProductionIndex_norm,
                p_min · phase_weight · af,
                p_max · phase_weight · af)
```

默认 `p_base = 0.02, k = 0.10, p_min = 0.005, p_max = 0.15`。`af` 把 scope 形态（`desert / aquatic / aerial / underground / forest / plains`）与模板玩法分支（`combat / puzzle / vault / aerial / logistics / trade`）匹配到 0.3–1.5 的系数：对角线匹配抬升直出概率（aerial 模板 × aerial scope = 1.5），远离匹配压制（aerial 模板 × desert scope = 0.4），错配场景仍保留基础概率不归零。矩阵均值约 0.85，不放大全局期望。直出物品在 `loot-windows.json` 的 `direct_equipment` 段配置，条目标注 `rarity` 与 `min_norm`，抽奖时按 `ProductionIndex_norm ≥ min_norm` 过滤后再按 `weight` 加权抽取。低档物品（`min_norm = 0`）在所有月相日可抽，中档（`min_norm = 0.35`）在半月以上开放，高档（`min_norm = 0.70`）在近满月以上开放。

单玩家本周装备产出价值受 `value_per_player_weekly_cap` 限制。R3 直出与 R4 craft 产出的装备共享同一封顶，按 `item-basket` 折算金额累加；R3 触发封顶时容器回退到原版战利品，R4 craft 触发封顶时拒绝下单。本周走运抽到高价值直出后 craft 配额自动收紧；反之 craft 配额宽松，让运气派与规划派共享一个产出节奏。

多材料兑换走研究中心，配方在 `loot-windows.json` 的 `craft_recipe` 段。craft 成本同时受研究 tier 折扣与 scope 直出热度影响：`craft_cost_eff = base · (1 - research_discount) · (1 + α · scope_direct_value_norm)`，默认 `α = 0.30`。scope 本周直出火热时同 scope 同 archetype 装备的 craft 成本最多上浮 30%；scope 直出冷清时 craft 成本回落到基线。研究中心同时开放拆解工位，玩家投入 R3 直出件按 rarity 退回基础/研究/高级材料（低档 0.70、中档 0.60、高档 0.50），把溢出的低档存货流入研究材料池。

`sky_ghast` 模板对空中分支声明独立参数，把 `phase_weight` 钳到 0.40 下限，让新月日的空中容器仍享受半月等效的概率窗口，反映 HappyGhast 养护成本带来的稀缺加成。

### R4 研究进度

R4 由社区金库出资与玩家提交样本两种来源构成。研究中心收到样本或研究金时即时计入 `research_progress`。

```
research_progress_delta = funding · tier_efficiency
research_delta          = sample_value · tier_factor
```

研究有三级反馈。玩家提交样本时由 Adventure 给玩家发即时小津贴；社区累计样本数与品质达成阈值时解锁认证 tier，给社区发指数与装备折扣；研究投入合计达到设施升级阈值时在全社区范围应用研究折扣、份额佣金减免与保险折扣。认证 tier 1–5 对应研究折扣 0/10/20/30/30%。

### R5 份额结算

R5 是社区金库到社区金库的资金通道。社区在周一至周六下单认购份额，周日 18:00 周结算时按 scope 当周指数终值赔付。合约下单后保持到结算，不开平仓与对冲。

每张合约绑定一个指数 `index_kind ∈ {production, pressure, death_risk, mission_fail}`。同一 scope 同一社区可同时开四种指数的合约。风险类指数（pressure / death_risk / mission_fail）方差更高，房费率上浮 0.03（产出预估 0.05，其余三类 0.08）。

合约形态分两类。趋势份额绑定看涨或看跌方向，按结算指数与发行价之差线性结算：

```
subscription_cost = shares · price_issue · margin_ratio
gross_payout      = shares · direction_sign · (Index_settle − price_issue) · (1 − house_rate)
net_payout        = max(0, gross_payout + subscription_cost) − subscription_cost
```

默认 `margin_ratio = 1.00`（满保证金，无杠杆）。盈利时差额扣房费返还金库，亏损时本金已扣相当于亏掉认购成本即止损，金库无需补差。`direction_sign(long) = +1, direction_sign(short) = −1`。

区间份额采用周开盘时预设的五档区间（重挫 / 偏低 / 中间 / 偏高 / 暴涨），边界取近 12 周指数 `empirical_cdf` 的 P10 / P30 / P70 / P90 分位点。社区在五档里选一档下单，不可自定义区间。

```
payout_rate = (1 − house_rate) / max(P_hit_estimate, P_hit_min)
payout      = shares · payout_rate · I[Index_settle ∈ band]
```

默认 `P_hit_min = 0.05` 防止极端档赔率爆炸。发行价取上周指数 EMA。份额房费按 50% 销毁。

持仓上限分三层：单 scope 单社区受 `index_position_per_community_cap` 限制，单社区跨 scope 受 `index_position_total_cap` 限制，同 scope 全社区合计受 `ScopeTotalPositionCap = base_scope_cap · (1 + β · ProductionIndex_norm)` 限制（默认 `base_scope_cap = 1500000, β = 0.5`）。前两层按社区发展度放缩，第三层按 scope 产出指数放缩，防止一片 scope 被群体押注冲击周结算赔付侧。本周开盘后新建的 scope 跳过本周份额市场，下周一并入。

### R6 阵亡保险

R6 由 Adventure 销售给玩家，承保人是社区金库。保单覆盖死亡时丢失的装备价值与未撤离的探索样本价值。同一玩家在同 scope 一周限购 1 张保单，保单生效至本周日 18:00 周结算时点，未理赔随结算到期作废，保费不退。

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

装备价值与样本价值取 `item-basket` 即时折算金额。拒赔不消耗保单额度且不退保费，包含三种情形：PVP 死亡、主动跳崖（FALL 死亡且坠落高度 ≥ 30 方块且 60 秒内无敌对实体伤害）、scope 外超时死亡（脱离 scope 边界超过 600 秒后阵亡）。

社区金库承保受三层上限约束：单保单赔付额上限 5000 元，同 scope 同社区累计承保 `≤ base_underwriting_cap · A_community · (1 − δ · PressureIndex_norm)`（base = 200000 元，δ = 0.40），社区全局承保 `≤ 0.60 · TreasuryBalance`。金库余额低于 50000 元时该社区停售新保单。

单 scope 单周累计赔付熔断：`ScopePayoutCapWeekly = base_scope_payout_cap · A_community_aggregate · (1 + ε · ProductionIndex_norm)`（base = 80000 元，ε = 0.30）。超过该上限后剩余索赔按 `prorate_factor` 比例赔付，周日 18:00 周结算时按 prorate 重新对账，超付差额下周从相关社区金库扣回。熔断对账在 R5 份额结算前执行。

保费 100% 入社区金库，手续费按 30% 销毁。死亡罚没按 70% 销毁、30% 入社区金库。

### R7 竞赛奖池

R7 在赛季节点开放。每场竞赛覆盖一个或多个 scope、一段固定时长、一组明示规则，规则在赛事配置里独立声明。

奖池由三处合成：

```
PrizePool = entry_fee_sum + sponsor_grant_sum
          + base_subsidy · (1 + θ_subsidy · (1 − ParticipationRate_norm))
```

玩家报名费受 `entry_fee_min = 50` 与 `entry_fee_max = 2000` 元约束，50% 进奖池、50% 销毁。社区赞助拨款分三档（small 10000 元 / medium 30000 元 / large 80000 元），单社区单赛季赞助总额受 `100000 · A_community` 限制。系统补贴基线 50000 元，冷门赛季按 `θ_subsidy = 0.50` 上浮，热门趋于基线。

玩家得分按竞赛权重向量加权四种 metric：操作分、指数兑现额、装备直出件数、撤离完整度。每项按竞赛窗口的 95 分位点归一化，低于 `min_count` 阈值的 metric 计 0 分防刷。社区维度赛事按 `mean / sum / top_k_mean` 三选一聚合玩家成绩。

奖金分配三选一：线性 top-N（前 N 名等差衰减）、指数阶梯（前 N 名 1/2 几何衰减）、按分数比例。社区维度赛事 30% 奖金回入社区金库、70% 给参与玩家按 Score 二次分摊。

冷场熔断：参与人数低于 `min_participants = 5` 时赛事作废，报名费全额退款，赞助拨款全额退款，系统补贴全额销毁。

奖金在竞赛 `end_time` 所在周的周日 18:00 周结算时统一发放，时序在 R6 熔断对账与 R5 份额结算之后。跨周赛事在每周周日 18:00 走中间快照，`end_time` 所在周才执行最终发放。

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
