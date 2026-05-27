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

The wilderness is the high-risk geographic space defined by IMYVMWorldGeo. Players use Community scopes as their home base and venture out into wilderness scopes. The wilderness stays open to players at all times; output rhythm follows the day's Minecraft server moon phase 0–7, switching at midnight Asia/Shanghai. Full moon (phase 0) carries weight 1.0, near-full (phases 1 and 7) 0.7, half moon (2 and 6) 0.4, crescent and waning crescent (3 and 5) 0.2, new moon (4) 0.1.

When a player crosses a scope boundary into the wilderness, Adventure opens an action session automatically. The scope bulletin shows the day's moon phase, the four indices (output forecast, anomaly pressure, death risk, mission failure) and the open state of phase-exclusive actions; vanilla effects inside the scope switch to the template matched to the day's moon phase.

Full and near-full moon days open the full scoring for the P1 probing and P5 aerial branches. Reading probes, sampling blocks and entities, and brushing suspicious blocks belong to probing; HappyGhast-mounted aerial strikes and aerial cargo lifts belong to aerial transport. On other moon-phase days these two action classes remain executable and the events still enter field evidence for research submission and insurance records, but the operation score posts as zero.

P2 combat, P3 puzzle, P4 container, P5 ground transport, and P6 trade post under the day's `phase_weight` across all moon-phase days. Killing mobs near pressure points, triggering redstone / sculk / copper-bulb puzzles, opening chests and trial vaults, leashed cargo / minecart / boat / camel ground transport, and trading with research NPCs or villagers all score instantly through the event listener once an action matches an event tuple. Action allowance R1 enters the player wallet immediately, operation score R2 enters the candidate pool to wait for weekly settlement, direct equipment drop R3 enters the inventory at the moment of container opening, and research progress R4 enters the community treasury at the moment of sample submission to a research NPC.

Players decide their own evacuation timing. Carrying sample crates, leashed cargo, or vehicles toward configured evacuation points or a Community scope counts as evacuation; drop accidents and deaths along the way reduce the `integrity` of sample evidence. When a player reaches an evacuation point or crosses back into a Community scope, every field objective and operation-score entry of the session locks in its integrity and the session record closes.

Death triggers R6 insurance payout at the policy's tier. Unevacuated samples convert to insurance event records under their damage-rate-adjusted integrity. Death forfeitures follow the fund-flow table. Heartbeat-sampled disconnect timeouts are treated as a failed evacuation death.

The community shares market runs on a weekly cadence. Monday at midnight Asia/Shanghai opens issuance with the pricing range and accepts orders; Saturday 18:00 locks the price; Sunday 18:00 weekly settlement pays out against the realized index value.

Sunday 18:00 triggers weekly settlement. All operation-score entries of the player's sessions in the week are weighted-summed by action class into `OperationScoreRaw`, then truncated by the player-side CES `Cap_week` to yield `OperationScore`; the R2 residue converts to wallet credit through the index realization pool, with portions exceeding `ScopeWeeklyCap` burned at the configured burn ratio. R5 share settlement and R6 renewal payouts run in the same sequence, and R7 competition prizes pay out under season rules. Settlement also emits a JSONL full archive and a Markdown macro report.

## Mechanics

The mechanics are organized along the seven return channels. Each channel specifies its source actor, trigger moment, receiving wallet, computation with caps, and burn rule. The five indices, moon-phase days, weekly settlement, and macro evaluation are listed separately as supporting mechanics.

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

R1 is an Adventure-system subsidy paid into the player wallet at the moment an action matches an event tuple. The amount is determined by `baseScore`, the action class coefficient, and the day's `phase_weight`. R1 does not enter the candidate pool, the CES weekly conversion, or the burn pipeline.

### R2 Operation-Score Conversion

R2 is the core return channel. All operation-score entries of the player's sessions in the week are weighted-summed by action class into `OperationScoreRaw`.

The weekly earning cap uses a CES function.

```
Cap_week = A · ( w_M · M^ρ + w_G · G^ρ + w_T · T^ρ )^(η/ρ)
```

`M` is money input, `G` is item input converted through the item-basket coefficients, and `T` is effective playtime. Player and community sides use independent parameters; `ρ < 0` produces a semi-complementary curve and `η < 1` produces decreasing returns to scale. The break-even line `A_be` is set to `0.6 · A` on the player side and `0.85 · A` on the community side.

After truncation by `Cap_week_player`, the result becomes `OperationScore`, which R2 converts into the player wallet; the index realization portion is further truncated by the scope weekly index realization cap `ScopeWeeklyCap = α · sqrt(scope_area_chunks) · A_community · (1 + β · ProductionIndex_norm)`, with the excess burned at 50% and any unrealized index residue at week-end burned at 100%.

R2 carries two layers of anti-manipulation hard constraints: a per-player per-scope weekly operation-score cap `per_scope_player_cap`, and the spatial-temporal floor's player heat deduction. Behavior that fixates on a single point, uses closed mob-farm structures, lacks movement trajectory, lacks probe-reading changes, or lacks pressure-point interaction does not enter the Adventure ledger and falls back to vanilla yields.

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

Multi-material crafting goes through the research center, with recipes in the `craft_recipe` section of `loot-windows.json`. Craft cost is shaped by both research-tier discount and scope drop heat: `craft_cost_eff = base · (1 - research_discount) · (1 + α · scope_direct_value_norm)`, default `α = 0.30`. When a scope's weekly drop value runs hot, same-scope same-archetype craft cost can rise by up to 30%; when drops cool, craft cost falls back to baseline. The research center also operates a disassembly station — players feed in R3 drops and receive basic / research / advanced materials by rarity (low 0.70, mid 0.60, high 0.50), letting overflowed low-tier stock flow into the research material pool.

The `sky_ghast` template declares independent parameters for the aerial branch, clamping `phase_weight` to a floor of 0.40 so that new-moon-day aerial containers still enjoy a half-moon-equivalent probability window, reflecting the scarcity premium that HappyGhast husbandry costs imply.

### R4 Research Progress

R4 has two sources: community treasury funding and player sample submission. The research center credits `research_progress` instantly upon receiving samples or research funds.

```
research_progress_delta = funding · tier_efficiency
research_delta          = sample_value · tier_factor
```

Research carries three feedback levels. When a player submits samples, Adventure issues an instant micro-allowance to the player; when the community's cumulative sample count and quality reach a threshold, a certification tier unlocks and grants index and equipment discounts to the community; when total research investment reaches the facility upgrade threshold, research discount, share-house commission relief, and insurance discounts apply across the community. Certification tiers 1–5 correspond to research discounts 0 / 10 / 20 / 30 / 30%.

### R5 Share Settlement

R5 is a community-treasury-to-community-treasury fund channel. Communities subscribe to shares between Monday and Saturday; Sunday 18:00 weekly settlement pays out at the scope's realized end-of-week index value. Once subscribed, a contract is held to settlement — no closing-out or hedging.

Each contract binds to one index `index_kind ∈ {production, pressure, death_risk, mission_fail}`. A community may run all four index contracts on the same scope simultaneously. The three risk indices (pressure / death_risk / mission_fail) carry higher variance and pay a 0.03 house-fee premium on top of the base (production at 0.05, the others at 0.08).

Two contract forms exist. Trend shares bind to a long or short direction and settle linearly against the index difference:

```
subscription_cost = shares · price_issue · margin_ratio
gross_payout      = shares · direction_sign · (Index_settle − price_issue) · (1 − house_rate)
net_payout        = max(0, gross_payout + subscription_cost) − subscription_cost
```

Default `margin_ratio = 1.00` (full collateral, no leverage). On profit, the difference (minus house fee) returns to the treasury; on loss, the principal is already debited and the maximum loss equals the subscription cost — the treasury does not top up. `direction_sign(long) = +1, direction_sign(short) = −1`.

Range shares use five fixed bands published at the Monday open (crash / low / middle / high / surge), with boundaries from the P10 / P30 / P70 / P90 quantiles of the prior 12-week `empirical_cdf`. Communities pick one of the five bands; custom ranges are not allowed.

```
payout_rate = (1 − house_rate) / max(P_hit_estimate, P_hit_min)
payout      = shares · payout_rate · I[Index_settle ∈ band]
```

Default `P_hit_min = 0.05` prevents extreme bands from blowing up the payout rate. The issue price uses the prior week's index EMA. The share-house fee burns 50%.

Position caps come in three layers: per scope per community (`index_position_per_community_cap`), per community across scopes (`index_position_total_cap`), and scope-wide across all communities (`ScopeTotalPositionCap = base_scope_cap · (1 + β · ProductionIndex_norm)`, default `base_scope_cap = 1500000, β = 0.5`). The first two scale with community development; the third scales with scope output index, preventing a single scope from being mass-bet into an unstable payout. Scopes created mid-week skip that week's share market and join the following week.

### R6 Death Insurance

R6 is sold by Adventure to players, underwritten by the community treasury. Policies cover death, equipment loss, and evacuation failure.

The premium is determined by `base_rate`, the death risk and anomaly pressure indices, the player's 4-week death rate, the coverage ratio, and community development.

```
premium = base_rate · (1 + γ_risk · DeathRisk + γ_pressure · Pressure)
                  · (1 + γ_history · player_death_rate_4w)
                  · coverage_ratio
                  · A_community
```

The coverage ratio is determined by tier: basic 0.5, standard 0.8, enhanced 1.0.

Payout is computed from equipment monetary valuation and the damage-rate-adjusted unevacuated sample value.

```
payout = coverage_ratio · ( equipment_loss + sample_loss · sample_value )
       − deductible
```

Premiums credit 100% to the community treasury; the policy fee burns 30%. Death forfeitures burn 70% and credit 30% to the community treasury. Policies do not cover voluntary self-fall, PVP death, or timeout death after leaving the scope. Community-treasury total underwriting exposure is gated by the development-driven cap, with admission validation rejecting over-cap underwriting.

### R7 Competition Pool

R7 opens at season nodes. A competition covers one or more scopes, a fixed duration, and an explicit ruleset. The ruleset spans scope subset, player or community dimension, scoring formula, prize-pool source, and payout method. The prize pool draws from player entry fees, community sponsorship grants, and Adventure-system subsidies. At settlement, payouts distribute to player wallets and community treasuries under the competition rules.

### Weekly Settlement and Macro Evaluation

Sunday 18:00 starts the settlement sequence: session freeze, community development snapshot, scope index final values, share market settlement, player and community `Cap_week` computation, operation-score conversion posting, research milestone and insurance renewal processing, weekly log archival, and cycle rollover. Each stage runs in a transaction and rolls back to the stage-start snapshot on failure.

Macro evaluation uses a flow–stock dual-table model plus rolling indicators. The flow table records inflows, burns, and transfers of the week; the stock table records week-end values of player wallet total `M2_player`, community treasury total `M2_community`, and the converted value of in-circulation items `item_stock`. Rolling indicators compute at week-end.

| Indicator | Formula |
| --- | --- |
| CRR | `M2_community / (M2_player + M2_community)` |
| Velocity | `(paid_out_player + paid_out_community) / avg M2` |

The CRR target is 0.6. The CRR range carries five alert levels: below 0.4 red community liquidity low, 0.4–0.5 yellow warning, 0.5–0.7 green target band, 0.7–0.85 yellow player-side tightening, above 0.85 red player-side depletion. Alerts write into the weekly Markdown report; administrators read the report, decide on manual parameter adjustments, and the adjustment actions write into the operation log. Adventure does not run automatic feedback control.

Weekly logs land in three layers: SQLite primary store, weekly JSONL archive, and weekly Markdown report. JSONL and Markdown retain long-term.

## Code Architecture

After the Fabric entrypoint launches, the Adventure server process binds six externally observable runtime modules.

| Module | Responsibility |
| --- | --- |
| Index Engine | Synthesize the five indices, maintain the spatial-temporal floor, write scope index snapshots |
| Operation Ledger | Listen to action events, compute `baseScore` and `integrity`, write to the operation-score candidate pool |
| Share Market | Accept community treasury subscriptions, settle by contract form |
| Insurance System | Issue policies, compute premiums, pay out on death events |
| Research Facility System | Receive samples and research funds, maintain tier progress, apply research discounts |
| Weekly Settlement | Trigger the settlement sequence at Asia/Shanghai Sunday 18:00, emit the macro report |

External commands:

| Command | Purpose |
| --- | --- |
| `/adventure` | Command entry |
| `/adventure about` | Show mod version and dependency info |
| `/adventure reload` | Hot-reload configuration |
| `/adventure debug context` | Output current cycle, scope, and player context |
| `/adventure log query <filter>` | Query weekly logs |
| `/adventure log export <range> <format>` | Export weekly logs |

The administrator-hot-reloadable configuration surface has two faces. Parameter weights live in TOML files covering economic parameters, index weights, research thresholds, insurance parameters, and settlement parameters. Output content lives in JSON files covering item-basket conversion coefficients, sample whitelists, container direct-drop and puzzle configurations, probe tiers, and scope effect templates. Mathematical formulas, treated as a non-hot-reloadable code-layer concern, are concentrated inside the Index Engine and Weekly Settlement modules.

Adventure persists all derived tables on its own, including cycles, scope index snapshots, action sessions, operation-score ledger, field objectives, share positions, policies, research progress, competitions, fund flows, macro indicators, and parameter-adjustment logs. Community treasury deposits and withdrawals execute instantly through `CommunityApi.deposit` and `CommunityApi.withdraw`; cross-repository fund operations use Adventure-generated idempotent IDs to self-check against duplicate submission.
