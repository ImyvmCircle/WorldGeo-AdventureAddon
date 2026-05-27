# WorldGeo-AdventureAddon

WorldGeo-AdventureAddon is the wilderness gameplay layer on top of IMYVMWorldGeo and WorldGeo-CommunityAddon. Adventure wires geographic facts, community treasuries, and player field actions into a single input–output loop. Six action classes inside a GeoScope — probing, combat, puzzle, container, transport, trade — generate operation scores; the scores flow into player wallets and community treasuries through seven return channels; communities reinvest the funds into research, shares, insurance, and sponsorship, which feed back into the next week's scope output rhythm and player choices. Wilderness output rhythm follows Minecraft moon-phase days: full and near-full days open the full-weight scoring of probing and aerial transport branches, while other moon-phase days only register field evidence.

The server time zone is Asia/Shanghai. The weekly settlement triggers at 18:00 on Sunday.

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

The deployment workflow places the Adventure jar and all required dependency jars into the server's `mods` directory. On first launch, Adventure generates default configuration files and persistent databases. Administrators apply configuration changes through `/adventure reload`.

## Gameplay Overview

The wilderness is the high-risk geographic space defined by IMYVMWorldGeo. Players use Community scopes as their home base and venture out into wilderness scopes. The wilderness stays open at all times, with output and risk shifting by the day's moon phase: full and near-full moon days are the high-yield, high-risk windows, while new moon days lean quiet.

When a player crosses into the wilderness, an action session opens automatically. The scope bulletin shows the day's moon phase, the four indices (output, pressure, risk, mission failure), and which phase-exclusive actions are open. Atmosphere and effects inside the scope switch with the phase.

Wilderness actions fall into six classes: probing, sampling, combat, puzzle and container, aerial branch, and ground transport and trade. Probing and aerial branches score only on full and near-full moon days; on other days they still produce evidence but do not score. The remaining classes score on all moon-phase days. Each action immediately produces a small action allowance and, weighted by phase, integrity, and heat decay, an operation score. Opening containers grants direct equipment drops; submitting samples to research NPCs advances community research.

Players choose their own evacuation timing. Carrying samples, leashed cargo, or vehicles back to an evacuation point or a Community scope completes the session; drops or deaths en route reduce sample evidence integrity, and once the player reaches the endpoint the session record locks. Death triggers the insurance payout at the policy's tier; unevacuated samples convert into insurance event records by their damage-adjusted integrity.

At the community level, the Community treasury can underwrite player policies, subscribe to wilderness index shares, and fund research progress. The shares market runs weekly: Monday opens issuance and orders, Saturday locks the price, Sunday settles against the realized index. When research crosses a tier threshold, the entire Community enjoys a cost discount and yield bonus on wilderness operations, alongside a one-time cash and equipment-pack reward.

Sunday 18:00 Asia/Shanghai triggers weekly settlement. The week's operation scores are aggregated by action class, capped weekly, and converted into player wallets through the index realization pool; insurance and the shares market settle in sequence. Settlement also emits a full archive and a macro weekly report.

## Mechanics

The mechanics are organized along the six return channels. Each channel specifies its source actor, trigger moment, receiving wallet, computation with caps, and burn rule. The five indices, moon-phase days, weekly settlement, and macro evaluation are listed separately as supporting mechanics.

### Five Indices

Four wilderness indices and one community index form Adventure's pricing floor.

| Index | Meaning | Primary downstream use |
| --- | --- | --- |
| Wilderness Output Forecast | Probability and quality of resources available that day | Direct-drop probability, share issue price, operation-score conversion cap |
| Anomaly Pressure | Wilderness danger accumulation | Cleanup payoff, share pricing, insurance premium |
| Death Risk | Player death and equipment loss | Insurance premium, payout formula |
| Mission Failure | Failure probability for cleanup, sampling, maintenance, research submission | Failure insurance, deposit |
| Community Development | Community treasury capacity and institutional credit | Underwriting cap, share position cap, research tier threshold |

The four wilderness indices are synthesized from a spatial-temporal floor plus rolling 7-day event statistics. The spatial-temporal floor is a weighted sum of multi-octave Simplex noise, the moon-phase term, and a 7-day player activity heat deduction; event statistics apply each index's content operator weights. Indices add per-event jitter at each event to prevent identical scoring of identical events.

### Moon-Phase Days

Wilderness output rhythm follows the day's Minecraft server moon phase 0–7, switching at midnight Asia/Shanghai. The day's moon phase determines the operation-score multiplier `phase_weight` and the open state of phase-exclusive actions.

| Phase | Stage | phase_weight | P1 Probing & P5 Aerial | Other four action classes |
| --- | --- | --- | --- | --- |
| 0 | Full moon | 1.0 | Scored | Scored |
| 1, 7 | Near-full | 0.7 | Scored | Scored |
| 2, 6 | Half moon | 0.4 | Field evidence only | Scored |
| 3, 5 | Crescent / waning crescent | 0.2 | Field evidence only | Scored |
| 4 | New moon | 0.1 | Field evidence only | Scored |

At moon-phase rollover Adventure calls WorldGeo's timed effect overlay API to switch the scope's effect template. Effect templates translate the horror-tinged mental-anomaly atmosphere into combinations of SLOWNESS, MINING_FATIGUE, NAUSEA, BLINDNESS, DARKNESS, HUNGER, WEAKNESS, POISON, GLOWING and other vanilla effects, switched by the day's moon phase.

### R1 Action Allowance

R1 is an Adventure-system subsidy paid into the player wallet at the moment an action matches an event tuple.

```
R1(event) = α_R1[class] · baseScore[event.type] · w_class[class] · phase_weight · (1 − heat_penalty)
```

`α_R1` is configured per action class: P6 logistics-and-trade gets 0.30 for the "small, fast, immediate" flow; P4 puzzle-and-vault gets 0.20 for the high-risk premium; P3 combat and P5 aerial branch get 0.10; P2 sampling gets 0.05; P1 probing gets 0.10. R1 and R2 share the same anti-manipulation filter: when a session triggers a session-level hard rule, R1 is zeroed alongside R2; when a chunk × event type exceeds `heat_threshold_kills_per_minute = 8` within a 5-minute rolling window, `heat_penalty = 1 − 0.5^(excess/threshold)` applies geometric softening. R1 lands in the wallet at the event itself and takes no part in the R2 dual-track realization, the CES weekly conversion, or the burn pipeline.

### R2 Operation-Score Conversion

R2 is the core return channel. Event-level operation scores accumulate by action class:

```
opScore(event)        = baseScore[event.type] · w_class[class] · phase_weight · integrity · (1 − heat_penalty)
session.opScore       = Σ_event opScore(event) · (1 − antimanip_kill[session])
PlayerScopeWeekScore  = clip( Σ_session session.opScore, 0, per_scope_player_cap )
OperationScoreRaw     = Σ_scope PlayerScopeWeekScore
OperationScore        = min( OperationScoreRaw, Cap_week_player )
```

Twelve event types map to six action classes by primary interaction shape: `read` to P1 Probing; `sample_block / sample_entity / brush` to P2 Sampling; `combat` to P3 Combat; `puzzle / vault / chest` to P4 Puzzle-and-Vault; `air_hit / air_haul` to P5 Aerial; `logistics / trade` to P6 Logistics-and-Trade. `baseScore` is configured in `economy.toml [operation_score]` and `w_class` in `[operation_score.class_weight]` (P1=0.6 / P2=0.8 / P3=1.0 / P4=1.2 / P5=1.3 / P6=0.5), routing the high-risk-window premium into P4 and P5.

The weekly earning cap uses a CES function.

```
Cap_week = A · ( w_M · M^ρ + w_G · G^ρ + w_T · T^ρ )^(η/ρ)
```

`M` is money input, `G` is item input converted through the item-basket coefficients, and `T` is effective playtime. Player and community sides use independent parameters; `ρ < 0` produces a semi-complementary curve and `η < 1` produces decreasing returns to scale. The break-even line `A_be` is set to `0.6 · A` on the player side and `0.85 · A` on the community side.

`OperationScore` follows a dual-track realization path. The immediate portion converts to wallet credit at event tick; the deferred portion accrues into a scope-level index realization pool:

```
immediate_cash(event)    = immediate_cash_ratio · opScore(event) · realization_rate(scope, t)
deferred_score(p, s)     = (1 − immediate_cash_ratio) · PlayerScopeWeekScore(p, s)
realization_rate(s, t)   = base_rate · (1 + γ_index · ProductionIndex_norm(s, t))
deferred_cash(p, s)      = min( deferred_score · realization_rate(s, t_end), ScopeWeeklyCapRemaining(s) )
burn_overflow(p, s)      = (deferred_score · realization_rate − deferred_cash) · overflow_burn_ratio
burn_unredeemed(s)       = pool_residue_at_week_end(s)
```

Defaults: `immediate_cash_ratio = 0.40`, `base_rate = 0.05 Yuan/score`, `γ_index = 0.40`, `overflow_burn_ratio = 0.50`. The realization rate floats in `0.05–0.07 Yuan/score` with scope production heat. The index realization pool is truncated by the scope weekly cap `ScopeWeeklyCap = α · sqrt(scope_area_chunks) · A_community · (1 + β · ProductionIndex_norm)`, with the excess burned at 50% and any unrealized residue at week-end burned at 100%.

R2 carries two anti-manipulation layers: session-level hard rules and event-level heat softening. Four independent session-level signals — point clustering (≥ 80% of kills inside a 4-block radius cube over a 5-minute window), closed mob farm (movement convex hull / session duration < 30 m²/min), no probe-reading change (P1/P2 reading and sampling index deltas both = 0), and session-long AFK (event density < 0.5 events/minute and session duration ≥ 10 minutes) — zero the entire session's operation score on any hit, fall back to vanilla loot, and bypass the Adventure ledger. Event-level heat softening rolls per chunk × event type and applies geometric decay above the threshold. Point-fixated farming, closed farm structures, and AFK sessions revert to vanilla yields.

### R3 Direct Equipment Drop

R3 is computed at the moment a trial vault or configured chest opens. The drop probability is jointly driven by the output forecast index, the day's moon-phase `phase_weight`, and the scope×template archetype-match coefficient `af`.

```
af       = archetype_match_matrix[tpl.template_archetype][scope.archetype]
P_direct = clip(p_base + k · ProductionIndex_norm,
                p_min · phase_weight · af,
                p_max · phase_weight · af)
```

Defaults are `p_base = 0.02, k = 0.10, p_min = 0.005, p_max = 0.15`. `af` maps scope archetype (`desert / aquatic / aerial / underground / forest / plains`) against template archetype (`combat / puzzle / vault / aerial / logistics / trade`) into a 0.3–1.5 coefficient: diagonal matches lift the probability (aerial template × aerial scope = 1.5), distant mismatches suppress it (aerial template × desert scope = 0.4), while mismatched scopes still retain a baseline probability rather than dropping to zero. Matrix mean is about 0.85, leaving the global P_direct expectation unchanged. Drop items are configured in the `direct_equipment` section of `loot-windows.json`. Each entry carries `rarity` and `min_norm`; the roll first filters entries by `ProductionIndex_norm ≥ min_norm`, then samples within by `weight`. Low-tier items (`min_norm = 0`) are reachable on all moon-phase days, mid-tier (`min_norm = 0.35`) unlocks from half moon upward, and high-tier (`min_norm = 0.70`) unlocks from near-full moon upward.

Per-player weekly equipment-output value is capped by `value_per_player_weekly_cap`. R3 drops and R4 crafts share the same cap, accumulated through the `item-basket` valuation; when R3 hits the cap the container falls back to vanilla loot, when R4 craft hits the cap the order is rejected. A lucky week with high-value drops automatically tightens the remaining craft budget; a quiet week leaves the craft budget generous, so luck-driven and planning-driven players share one output rhythm.

Multi-material crafting goes through the research center, with recipes in the `craft_recipe` section of `loot-windows.json`. Craft cost is shaped by both research-tier discount and scope drop heat: `craft_cost_eff = recipe.base_materials · (1 - cost_discount[tier]) · (1 + α · scope_direct_value_norm)`, default `α = 0.30`. When a scope's weekly drop value runs hot, same-scope same-archetype craft cost can rise by up to 30%; when drops cool, craft cost falls back to baseline. The research center also operates a disassembly station — players feed in R3 drops and receive basic / research / advanced materials by rarity (low 0.70, mid 0.60, high 0.50), letting overflowed low-tier stock flow into the research material pool.

The `sky_ghast` template declares independent parameters for the aerial branch, clamping `phase_weight` to a floor of 0.40 so that new-moon-day aerial containers still enjoy a half-moon-equivalent probability window, reflecting the scarcity premium that HappyGhast husbandry costs imply.

### R4 Research Progress

R4 runs a single-axis accumulator. Community treasury funding (C1 channel) and player sample submission to research NPCs share the same progress counter:

```
research_progress(c) += funding + sample_value
```

`sample_value` is the single value field in `sample-whitelist.json`; `funding` is the treasury outlay amount. Both inlets count under the same weight; there is no dual-axis split.

Building a research center grants tier 1 by default. Tier 2–5 unlock when `research_progress` crosses thresholds at the Sunday 18:00 settlement upgrade sub-stage. Default thresholds are `[0, 0, 10000, 35000, 90000, 200000]`, scaling with `A_community`.

Each tier carries two coefficients: cost discount `cost_discount` and yield bonus `yield_bonus`.

| Coefficient | tier 1 | tier 2 | tier 3 | tier 4 | tier 5 |
| --- | --- | --- | --- | --- | --- |
| `cost_discount` | 0% | 8% | 16% | 24% | 32% |
| `yield_bonus` | 0% | 5% | 10% | 15% | 20% |

`cost_discount` applies to probe purchase, sampler purchase, research-center submission fees, R5 share-house rate, R6 insurance premium, and research-center craft recipe materials. `yield_bonus` applies to the R2 operation-score conversion coefficient, the R3 direct-drop probability `P_direct` central value, and the R4 sample submission instant allowance. Both coefficients take effect uniformly across all community members.

Research carries two feedback cadences. When a player submits a qualified sample, an instant micro-allowance posts immediately: `bounty = clip(sample_value · k_bounty, 50, 1500) · (1 + yield_bonus[tier])`, default `k_bounty = 0.30`. When the community's `research_progress` crosses the next threshold, a one-shot upgrade reward fires at Sunday 18:00: a cash bonus `cash_bonus = α · tier · A_community^β` (default `α = 5000, β = 0.60`) enters the treasury, and an equipment bundle from `loot-windows.json` `tier_unlock_bundle.tier{k}` lands in the community shared storage. When multiple thresholds cross in the same week, each tier settles independently in ascending order.

### R5 Share Settlement

R5 is a community-treasury-to-community-treasury fund channel. Communities subscribe to shares between Monday and Saturday; Sunday 18:00 weekly settlement pays out at the scope's realized end-of-week index value. Once subscribed, a contract is held to settlement — no closing-out or hedging.

Each contract binds to one index `index_kind ∈ {production, pressure, death_risk, mission_fail}`. A community may run all four index contracts on the same scope simultaneously. The three risk indices (pressure / death_risk / mission_fail) carry higher variance and pay a 0.03 house-fee premium on top of the base (production at 0.05, the others at 0.08).

Two contract forms exist. Trend shares bind to a long or short direction and settle linearly against the index difference:

```
subscription_cost = shares · price_issue · margin_ratio
gross_payout      = shares · direction_sign · (Index(s, t_end) − price_issue) · (1 − house_rate)
net_payout        = max(0, gross_payout + subscription_cost) − subscription_cost
```

Default `margin_ratio = 1.00` (full collateral, no leverage). On profit, the difference (minus house fee) returns to the treasury; on loss, the principal is already debited and the maximum loss equals the subscription cost — the treasury does not top up. `direction_sign(long) = +1, direction_sign(short) = −1`.

Range shares use five fixed bands published at the Monday open (crash / low / middle / high / surge), with boundaries from the P10 / P30 / P70 / P90 quantiles of the prior 12-week `empirical_cdf`. Communities pick one of the five bands; custom ranges are not allowed.

```
payout_rate = (1 − house_rate) / max(P_hit_estimate, P_hit_min)
payout      = shares · payout_rate · I[Index(s, t_end) ∈ band]
```

Default `P_hit_min = 0.05` prevents extreme bands from blowing up the payout rate. The issue price uses the prior week's index EMA. The share-house fee burns 50%.

Position caps come in three layers: per scope per community (`index_position_per_community_cap`), per community across scopes (`index_position_total_cap`), and scope-wide across all communities (`ScopeTotalPositionCap = base_scope_cap · (1 + β · ProductionIndex_norm)`, default `base_scope_cap = 1500000, β = 0.5`). The first two scale with community development; the third scales with scope output index, preventing a single scope from being mass-bet into an unstable payout. Scopes created mid-week skip that week's share market and join the following week.

### R6 Death Insurance

R6 is sold by Adventure to players, underwritten by the community treasury. Policies cover the value of equipment lost on death and unevacuated exploration samples. Each player is limited to one policy per scope per week; policies remain in force until Sunday 18:00 weekly settlement, expiring without refund if no claim is filed.

Policies come in three tiers:

| Tier | Coverage Ratio | Premium Multiplier | Deductible Ratio |
| --- | --- | --- | --- |
| basic    | 0.50 | 1.0 | 0.30 |
| standard | 0.80 | 1.6 | 0.15 |
| premium  | 1.00 | 2.4 | 0.05 |

The convex premium multiplier makes the enhanced tier's surcharge exceed the linear scaling of coverage ratio, so players pick by risk preference rather than always defaulting to the highest tier.

The premium follows the death-risk index, scope anomaly pressure, the player's 4-week death rate, tier, and community development:

```
premium = base_rate · coverage_premium_multiplier[tier]
                   · (1 + γ_risk · DeathRiskIndex_norm + γ_pressure · PressureIndex_norm)
                   · (1 + γ_history · player_death_rate_4w)
                   · A_community
```

Payouts fire on `PlayerDeathEvent`:

```
gross_payout = coverage_ratio[tier] · ( equipment_loss_value + sample_loss_value )
payout       = max(0, gross_payout · (1 − deductible_ratio[tier])) · prorate_factor
```

Equipment and sample values use the live `item-basket` conversion. Claim denials neither consume the policy nor refund the premium, and cover three cases: PVP death, voluntary fall (FALL death with fall height ≥ `voluntary_fall_height_threshold = 30` blocks and no hostile-entity damage event within `voluntary_fall_damage_window_seconds = 60` seconds), and out-of-scope timeout death (death more than `scope_exit_grace_seconds = 600` seconds after leaving the scope boundary).

Community-treasury underwriting is gated by three caps: per-policy payout ≤ 5000 Yuan; per scope per community cumulative underwriting `≤ base_underwriting_cap · A_community · (1 − δ · PressureIndex_norm)` (base = 200000 Yuan, δ = 0.40); community-wide underwriting `≤ 0.60 · TreasuryBalance`. When the treasury falls below 50000 Yuan, that community suspends new policy sales.

Per-scope weekly cumulative payout circuit-breaker: `ScopePayoutCapWeekly = base_scope_payout_cap · A_community_aggregate · (1 + ε · ProductionIndex_norm)` (base = 80000 Yuan, ε = 0.30). Once the cap is exceeded in a week, remaining claims pay at `prorate_factor`; Sunday 18:00 settlement re-prorates, and any overpayment clawback happens automatically against the affected community treasuries the following week. Circuit-breaker reconciliation runs before R5 share settlement.

Premiums credit 100% to the community treasury; the policy fee burns 30%. Death forfeitures burn 70% and credit 30% to the community treasury.

### Weekly Settlement and Macro Evaluation

Sunday 18:00 starts the settlement sequence: session freeze, community development snapshot, scope index final values, R6 insurance circuit-breaker reconciliation with prorate clawback, R5 share market settlement, player and community `Cap_week` computation, macro feedback controller, operation-score conversion posting, research milestone and insurance renewal processing, weekly log archival, and cycle rollover. Each stage runs in a transaction and rolls back to the stage-start snapshot on failure.

Macro evaluation uses a flow–stock dual-table model plus rolling indicators. The flow table records inflows, burns, and transfers of the week; the stock table records week-end values of player wallet total `M2_player`, community treasury total `M2_community`, and the converted value of in-circulation items `item_stock`. Rolling indicators compute at week-end.

| Indicator | Formula |
| --- | --- |
| CRR | `M2_community / (M2_player + M2_community)` |
| Velocity | `(paid_out_player + paid_out_community) / avg M2` |

The CRR target is 0.6. Five alert levels: below 0.4 red community liquidity low, 0.4–0.5 yellow warning, 0.5–0.7 green target band, 0.7–0.85 yellow player-side tightening, above 0.85 red player-side depletion. Alert levels feed the three-layer feedback loop below.

The macro feedback loop runs in three layers. Layer one is formula-level reverse adjustment: PressureIndex, DeathRiskIndex, and CRR appear directly in the in-week R2 / R5 / R6 formulas and take effect at event tick. R2 realization rate multiplies by `(1 − μ_pressure · PressureIndex_norm)`; R5 band width scales by `(1 + ξ_band · |CRR − 0.6|)`; R6 premium scales up under PressureIndex and DeathRiskIndex while underwriting caps tighten by PressureIndex. Defaults are `μ_pressure = 0.30`, `ξ_band = 0.50`, configured in `economy.toml [macro_feedback]`.

Layer two is the autopilot cross-week parameter migrator, toggled by `settlement.toml [autopilot]`, defaulting to off. When on, the `compute_macro_feedback` sub-stage at Sunday 18:00 computes global CRR / PressureIndex / DeathRiskIndex errors and adjusts three whitelisted parameters via `Δratio = gain · error`: `realization.base_rate` (CRR feedback), `shares.base_pos_per_scope` (PressureIndex feedback), and `insurance.base_rate` (DeathRiskIndex feedback). A single parameter shifts at most ±2% per week, with hard bounds `[initial · 0.5, initial · 2.0]`; out-of-bound values freeze that parameter and raise a red alert. Every autopilot change writes to `economy_adjust(source="autopilot")` and is rollback-eligible by administrator command within 24 hours. When autopilot is off, the controller still emits a "suggested parameter pack" into the weekly report and `tune_suggestion` table for manual application.

Layer three is the administrator in-game command set, detailed under `/adventure tune` below as the highest-authority intervention layer.

Weekly logs land in three layers: SQLite primary store, weekly JSONL archive, and weekly Markdown report. JSONL and Markdown retain long-term. The Markdown report adds two new sections, `autopilot_adjustments` and `tune_suggestions`, recording this week's automatic changes and next week's suggested parameter pack.

## Code Architecture

After the Fabric entrypoint launches, the Adventure server process binds seven externally observable runtime modules.

| Module | Responsibility |
| --- | --- |
| Index Engine | Synthesize the five indices, maintain the spatial-temporal floor, write scope index snapshots |
| Operation Ledger | Listen to action events, compute `baseScore` and `integrity`, write to `operation_ledger` and route the dual-track immediate vs deferred allocation |
| Share Market | Accept community treasury subscriptions, settle by contract form |
| Insurance System | Issue policies, compute premiums, pay out on death events |
| Research Facility System | Receive samples and research funds, maintain tier progress, apply research discounts |
| Weekly Settlement | Trigger the settlement sequence at Asia/Shanghai Sunday 18:00, emit the macro report |
| Macro Feedback Controller | Compute CRR / Pressure / DeathRisk errors in the `compute_macro_feedback` sub-stage, write `economy_adjust` and `tune_suggestion`, serve the `/adventure tune` command family |

External commands:

| Command | Purpose |
| --- | --- |
| `/adventure` | Command entry |
| `/adventure about` | Show mod version and dependency info |
| `/adventure reload` | Hot-reload configuration |
| `/adventure debug context` | Output current cycle, scope, and player context |
| `/adventure log query <filter>` | Query weekly logs |
| `/adventure log export <range> <format>` | Export weekly logs |
| `/adventure tune list [last N]` | List the most recent N parameter-adjustment records |
| `/adventure tune get <param.path>` | Read a parameter's current effective value and initial value |
| `/adventure tune set <param.path> <value> [reason]` | Overwrite the parameter immediately; takes effect from the next event tick |
| `/adventure tune apply <suggestion_id>` | Apply the suggested parameter pack identified by `suggestion_id` in this week's report |
| `/adventure tune rollback <adjust_id>` | Roll back the change identified by `adjustId` to its `old_value`; restricted to the current cycle and 24-hour window |
| `/adventure tune autopilot on\|off` | Toggle the autopilot flag; effective from next Sunday 18:00 |
| `/adventure tune dryrun` | Output the Δratio set the autopilot would write this week if enabled, without executing |

The `/adventure tune` family requires OP level ≥ 4. Every command execution writes into the `economy_adjust` operation log with a `source` field distinguishing `autopilot / manual / rollback / suggestion_apply`. Before execution each command validates against the whitelist `allowed_params` and hard bounds; out-of-bound calls are rejected with a red notice.

The administrator-hot-reloadable configuration surface has two faces. Parameter weights live in TOML files covering economic parameters, index weights, research thresholds, insurance parameters, and settlement parameters. Output content lives in JSON files covering item-basket conversion coefficients, sample whitelists, container direct-drop and puzzle configurations, probe tiers, and scope effect templates. Mathematical formulas, treated as a non-hot-reloadable code-layer concern, are concentrated inside the Index Engine, Weekly Settlement, and Macro Feedback Controller modules.

Adventure persists all derived tables on its own, including cycles, scope index snapshots, action sessions, operation-score ledger, field objectives, share positions, policies, research progress, fund flows, macro indicators, parameter-adjustment logs, and suggested parameter packs. Community treasury deposits and withdrawals execute instantly through `CommunityApi.deposit` and `CommunityApi.withdraw`; cross-repository fund operations use Adventure-generated idempotent IDs to self-check against duplicate submission.
