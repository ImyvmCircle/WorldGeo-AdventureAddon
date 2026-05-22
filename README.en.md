# WorldGeo-AdventureAddon

WorldGeo-AdventureAddon is the wilderness market layer above IMYVMWorldGeo and WorldGeo-CommunityAddon. WorldGeo supplies Region and GeoScope facts. Community supplies treasury, membership, development, and agreement data. Adventure uses upstream inputs to price wilderness risk, research, concessions, and insurance.

## Runtime Direction

Wilderness is an independent exploration field and anomaly-pressure field. Community enters the wilderness market through time-limited concessions, treasury deposits, research projects, insurance underwriting, and weekly competition rewards. Players leave samples, readings, cleanup records, rescue records, and failure records in wilderness scopes. Community converts those records into research credit, insurance rates, concession returns, and treasury competition scores.

The core market is driven by five indices. The wilderness output forecast index prices samples and research debt. The anomaly pressure index prices cleanup rights and pressure-reduction rewards. The casualty risk index prices rescue reserves and death insurance. The mission failure index prices failure insurance and concession deposits. The Community development index adjusts treasury capacity, competition ranking, and institutional credit.

Prices follow volatility, spread, forecast error, and risk change. Weekly issuance enters the reward pool when valid player operations and Community treasury commitments meet settlement conditions. Remaining issuance expires at settlement. Part of concession, insurance, research debt, and transfer fees is burned.

## Mandatory Dependencies

IMYVMWorldGeo carries the geography layer. Adventure reads scope metadata, entry and exit events, statistics snapshots, and adjacency data for wilderness pricing and anomaly windows.

WorldGeo-CommunityAddon carries the institutional layer. Adventure reads treasury accounts, Community development statistics, time-limited concession records, and weekly competition settlement records.

Hoki carries configuration and translation resource loading. Translation capacity is expanded through the resource-file mechanism.

## Code Surface

The bootstrap layer loads configuration and binds services. The command layer provides `/adventure`, `/adventure about`, `/adventure reload`, and `/adventure debug context`. The configuration layer keeps `Adventure.conf` and `AdventureGameplay.conf`. The persistence layer stores schema state. The WorldGeo bridge layer provides read-only Region and GeoScope lookup.

The wilderness market is carried by the index engine, operation ledger, concession market, insurance system, research system, and weekly settlement system.
