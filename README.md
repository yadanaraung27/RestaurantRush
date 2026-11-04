# RestaurantRush

RestaurantRush is a turn-based restaurant management game written in Java for the SC2002 Object-Oriented Design & Programming assignment. The player manages the restaurant staff over 18 turns while customers arrive, queue for tables, order food, wait for the kitchen, eat, and pay. The game ends in victory or defeat based on paid customers, revenue, and average paid-customer satisfaction.

## Table of Contents

- [Project Stages](#project-stages)
- [Requirements](#requirements)
- [Setup and run](#setup-and-run)
  - [Quick start (macOS/Linux/WSL/Git Bash)](#quick-start-macoslinuxwslgit-bash)
  - [Manual build and run](#manual-build-and-run)
  - [Windows PowerShell](#windows-powershell)
  - [What the commands do](#what-the-commands-do)
- [Stage 1 - Basic operation](#stage-1---basic-operation)
  - [Features](#features)
  - [How to play](#how-to-play)
  - [Test notes](#test-notes)
- [Stage 2 - Customer behaviour](#stage-2---customer-behaviour)
  - [Features](#features-1)
  - [How to play](#how-to-play-1)
  - [Test notes](#test-notes-1)
- [Stage 3 - New role and menu policy](#stage-3---new-role-and-menu-policy)
  - [Features](#features-2)
  - [How to play](#how-to-play-2)
  - [Test notes](#test-notes-2)
- [Stage transitions](#stage-transitions)
  - [Stage 1 to Stage 2](#stage-1-to-stage-2)
  - [Stage 2 to Stage 3](#stage-2-to-stage-3)
- [Design Diagrams](#design-diagrams)

## Project Stages

Each stage is a separate runnable version tagged in GitHub, and each builds on the one before.

| Stage | Git tag | What it adds |
|---|---|---|
| 1 | `stage-1` | Regular customers, an a-la-carte menu, Waiter/Chef/Cashier, the full turn loop, and victory/defeat. |
| 2 | `stage-2` | Replaces two of the regular arrivals with a VIP and a Critic. The VIP loses 5 satisfaction per waiting turn and pays 10% less; the Critic loses 12 per turn and chooses the most expensive item on the menu. Adds `ArrivalSchedule` and `PricingService`. |
| 3 | `stage-3` | Adds the Host (one extra seating per turn), three ComboMeals (90% of their components' combined price, with preparation units summed), and Happy Hour (once per game, two turns, 20% off orders, plus a 15-point satisfaction recovery). Adds `MenuItem`, `FrontStaff`, and `HappyHourPolicy`; `PricingService` applies the VIP discount first, then Happy Hour. |

## Requirements

- JDK 16 or newer (tested with JDK 17).
- macOS, Linux, WSL, or Git Bash for `run.sh`.
- Windows PowerShell users can use the manual commands below.

Check your Java installation:

```bash
java -version
javac -version
```

## Setup and run

### Quick start (macOS/Linux/WSL/Git Bash)

From the repository root:

```bash
chmod +x run.sh
./run.sh
```

`run.sh` cleans `out/`, compiles every `.java` file under `src/`, and starts `src.Main`. It can also be called from another directory.

### Manual build and run

```bash
rm -rf out
mkdir -p out
javac -Xlint:all -d out $(find src -name '*.java' -print)
java -cp out src.Main
```

### Windows PowerShell

```powershell
Remove-Item -Recurse -Force out -ErrorAction SilentlyContinue
New-Item -ItemType Directory -Force out | Out-Null
$sources = Get-ChildItem -Recurse -Filter *.java src | ForEach-Object { $_.FullName }
javac -Xlint:all -d out $sources
java -cp out src.Main
```

### What the commands do

- `rm -rf out` removes old compiled classes so stale `.class` files cannot affect the run.
- `mkdir -p out` creates the build directory.
- `javac -Xlint:all -d out ...` compiles all source files and writes `.class` files into `out/`, preserving package folders.
- `java -cp out src.Main` runs the class `src.Main`; `out` is the classpath and `src.Main` is the fully qualified class name (package `src`, class `Main`).

The compiled layout is:

```text
out/
  src/
    Main.class
    boundary/...
    control/...
    entity/...
    exception/...
    staff/...
```

## Stage 1 - Basic operation

### Features

- 18-turn game loop with 2 tables and 3 staff: Waiter, Chef, and Cashier.
- Three a-la-carte items: Salad ($9.00, 1 prep unit), Burger ($15.00, 1 prep unit), and Pasta ($18.00, 2 prep units).
- Player-chosen priority: you name the customer the Waiter seats, takes an order from, or serves, and the order the Chef prepares.
- Waiting queue, seating, ordering, preparation, serving, eating, and automatic payment.
- Customers lose satisfaction while waiting; unhappy customers abandon the restaurant and free their table.
- The Cashier pays the oldest eligible customer automatically.
- Victory requires at least 4 paid customers, at least $45.00 in revenue, and average paid satisfaction of at least 60.0.

### How to play

Each turn:

1. Choose one Waiter action: `seat C1`, `order C1`, `serve C1`, or `wait`.
2. Choose one Chef action: `prepare C1`, or `wait`.
3. The Cashier automatically handles payment of the oldest eligible customer.

Each prompt lists only the commands you can use right now. Type the command, type its number, or press Enter to wait:

```text
  1. Waiter action (1 per turn)
     [1] seat C1
     [2] wait
     > 1
  >> Seated C1 at table 1.

  2. Chef action (1 per turn)
     [1] wait
     > 
  >> Chef waited.

  1. Waiter action (1 per turn)
     [1] order C1
     [2] wait
     > order C1
  >> C1 chose Salad ($9.00, 1 prep unit).
```

An invalid choice explains why and asks again without using up the staff task:

```text
  1. Waiter action (1 per turn)
     [1] order C1
     [2] wait
     > 5
  >> [Error] Choose a number from 1 to 2, or type the command, for example 'order C1'.
```

Customers choose their meal using a seeded random choice derived from their ID, so the same customer always makes the same choice across runs. Customers arrive on turns 1, 4, 7, 10, 13, and 16 and are named `C1`, `C4`, `C7`, `C10`, `C13`, and `C16` from their arrival turn. That ID is the seed. Type a command or its menu number exactly as shown at the prompt.

### Test notes

Both runs use piped input, so they need no manual typing and produce identical results on every execution. The scripts type the text commands rather than menu numbers, so the sequence can be read without running it.

#### Victory run

```bash
for id in C1 C4 C7 C10 C13 C16; do
  echo "seat $id"
  echo "wait"
  echo "order $id"
  echo "prepare $id"
  echo "serve $id"
  echo "wait"
done | ./run.sh
```

Each customer gets six commands across three turns: `seat`, `wait` (Chef), `order`, `prepare`, `serve`, `wait` (Chef). The loop repeats them for all six arrivals in arrival order, so the run is reproducible; any eligible customer can be chosen instead.

Expected final result:

```text
============================================================
                         GAME OVER
============================================================

                       FINAL RESULTS
------------------------------------------------------------
  Served Customers                6
  Unhappy Departures              0
  Paid Customers                  5
  Total Revenue              $57.00
  Avg. Paid Satisfaction       84.0
  Turns Used                     18

============================================================
                      VICTORY TARGETS
============================================================
  Objective                    Actual     Target   Status
------------------------------------------------------------
  Paid Customers                    5          4   [PASS]
  Total Revenue                $57.00     $45.00   [PASS]
  Avg. Paid Satisfaction         84.0       60.0   [PASS]
------------------------------------------------------------

                 *** VICTORY ACHIEVED! ***

                All 3 victory targets met.
============================================================
```

#### Defeat run

```bash
for turn in $(seq 1 18); do
  echo wait  # Waiter
  echo wait  # Chef
done | ./run.sh
```

Every prompt is answered with `wait`: two per turn (one for the Waiter, one for the Chef) over all 18 turns. The Cashier works automatically, so it has no prompt. No customer is ever seated or served, so only the two earliest arrivals lose enough satisfaction to leave before the game ends.

Expected final result:

```text
============================================================
                         GAME OVER
============================================================

                       FINAL RESULTS
------------------------------------------------------------
  Served Customers                0
  Unhappy Departures              2
  Paid Customers                  0
  Total Revenue               $0.00
  Avg. Paid Satisfaction        N/A
  Turns Used                     18

============================================================
                      VICTORY TARGETS
============================================================
  Objective                    Actual     Target   Status
------------------------------------------------------------
  Paid Customers                    0          4   [FAIL]
  Total Revenue                 $0.00     $45.00   [FAIL]
  Avg. Paid Satisfaction          N/A       60.0   [FAIL]
------------------------------------------------------------

                       *** DEFEAT ***

                0 of 3 victory targets met.
============================================================
```

## Stage 2 - Customer behaviour

Stage 2 adds VIP and Critic customers while keeping the same turn flow and victory conditions. It is independently playable with the same build and `run.sh`, and it contains no Stage 3 features (Host, ComboMeal, or Happy Hour).

### Features

- Three customer types: Regular, VIP, and Critic.
- VIP customers lose 5 satisfaction per waiting turn and pay 10% less.
- Critic customers lose 12 satisfaction per waiting turn and always order the most expensive item on the menu. Ties keep menu order; with the current menu that is Pasta ($18.00, 2 prep units).
- Six scheduled arrivals: C1 Regular, C4 VIP, C7 Regular, C10 Critic, C13 Regular, C16 Regular.
- The turn table shows each customer's type, and the order message shows the locked price after any discount.
- The design uses an abstract `Customer` with `RegularCustomer`, `VIPCustomer`, and `CriticCustomer` subclasses. `ArrivalSchedule` decides who arrives, and `PricingService` applies the discount.

### How to play

The commands and turn order are unchanged:

1. Choose one Waiter action: `seat C1`, `order C1`, `serve C1`, or `wait`.
2. Choose one Chef action: `prepare C1`, or `wait`.
3. The Cashier automatically handles payment of the oldest eligible customer.

The differences are the customer mix:

- C4 is a VIP: the bill is discounted by 10% when the order is taken, and the order message shows the locked price.
- C10 is a Critic: it orders Pasta, which needs two preparation units. The Chef must prepare it twice before the Waiter can serve it.

### Test notes

Both runs use piped input and produce identical results on every execution.

#### Victory run

```bash
./run.sh <<'EOF'
seat C1
wait
order C1
prepare C1
serve C1
wait
seat C4
wait
order C4
prepare C4
serve C4
wait
seat C7
wait
order C7
prepare C7
serve C7
wait
seat C10
wait
order C10
prepare C10
wait
prepare C10
serve C10
wait
seat C13
wait
order C13
prepare C13
serve C13
wait
wait
wait
wait
wait
EOF
```

The run serves C1, C4, C7, C10, and C13. C16 arrives on turn 16 and cannot finish paying within 18 turns, so at most five customers can pay. C4's VIP discount makes the Burger $13.50, and C10's Critic order is Pasta ($18.00, 2 prep units), so the Chef spends two turns on it.

Expected final result:

```text
============================================================
                         GAME OVER
============================================================

                       FINAL RESULTS
------------------------------------------------------------
  Served Customers                5
  Unhappy Departures              0
  Paid Customers                  5
  Total Revenue              $64.50
  Avg. Paid Satisfaction       79.6
  Turns Used                     18

============================================================
                      VICTORY TARGETS
============================================================
  Objective                    Actual     Target   Status
------------------------------------------------------------
  Paid Customers                    5          4   [PASS]
  Total Revenue                $64.50     $45.00   [PASS]
  Avg. Paid Satisfaction         79.6       60.0   [PASS]
------------------------------------------------------------

                 *** VICTORY ACHIEVED! ***

                All 3 victory targets met.
============================================================
```

#### Defeat run

```bash
for turn in $(seq 1 18); do
  echo wait  # Waiter
  echo wait  # Chef
done | ./run.sh
```

Every prompt is answered with `wait`. C1 and C10 are the two customers who lose enough satisfaction to leave before the game ends.

Expected final result:

```text
============================================================
                         GAME OVER
============================================================

                       FINAL RESULTS
------------------------------------------------------------
  Served Customers                0
  Unhappy Departures              2
  Paid Customers                  0
  Total Revenue               $0.00
  Avg. Paid Satisfaction        N/A
  Turns Used                     18

============================================================
                      VICTORY TARGETS
============================================================
  Objective                    Actual     Target   Status
------------------------------------------------------------
  Paid Customers                    0          4   [FAIL]
  Total Revenue                 $0.00     $45.00   [FAIL]
  Avg. Paid Satisfaction          N/A       60.0   [FAIL]
------------------------------------------------------------

                       *** DEFEAT ***

                0 of 3 victory targets met.
============================================================
```

## Stage 3 - New role and menu policy

Stage 3 adds the Host, combo meals, and Happy Hour. It is independently playable with the same build and `run.sh`, and it uses the same victory and defeat targets.

### Features

- **Host**: seats at most one queued customer per turn, in addition to the Waiter. The Waiter can still seat.
- **ComboMeal**: the menu includes three combos alongside the a-la-carte items:
  - Salad + Pasta — $24.30, 3 prep units.
  - Salad + Burger — $21.60, 2 prep units.
  - Pasta + Burger — $29.70, 3 prep units.
  - Each combo costs 90% of its components' combined price; preparation units are the sum.
- **Customer combo rule**: the menu holds `MenuItem`s. Regular and VIP customers use the seeded menu index; Critics choose the most expensive item. With the current menu, C10 chooses Pasta + Burger, C13 chooses Salad + Burger, and C16 chooses Salad + Pasta.
- **Happy Hour**:
  - can be activated once per game, at the start of a turn;
  - lasts for the activation turn and the next;
  - gives 20% off orders taken while active;
  - recovers 15 satisfaction for customers still waiting for a table or food at turn start (maximum 100);
  - applies the VIP discount first, then Happy Hour;
  - locks the price when the order is taken;
  - shows `available`, `active (N turns)`, or `used` in the turn display.

Current menu choices:

| Customer | Type | Order | Price | Prep units |
|---|---|---|---|---|
| C1 | Regular | Salad | $9.00 | 1 |
| C4 | VIP | Pasta | $12.96 during Happy Hour ($16.20 otherwise) | 2 |
| C7 | Regular | Salad | $9.00 | 1 |
| C10 | Critic | Pasta + Burger | $29.70 | 3 |
| C13 | Regular | Salad + Burger | $21.60 | 2 |
| C16 | Regular | Salad + Pasta | $24.30 | 3 |

### How to play

Each turn:

1. Answer the Happy Hour prompt, if it appears: `y` to activate, `n` to decline.
2. Choose one Host action: `seat C1` or `wait`.
3. Choose one Waiter action: `seat C1`, `order C1`, `serve C1`, or `wait`.
4. Choose one Chef action: `prepare C1` or `wait`.
5. The Cashier automatically handles payment of the oldest eligible customer.

The Host and Waiter can both seat, so two customers can be seated in one turn when two tables are free. The Waiter can also use that turn to take an order or serve.

Example from the recording run:

```text
  Activate Happy Hour for this turn and the next? (y/n): y

  [*] Happy Hour: Activated for this turn and the next.
  [+] VIP C4 has arrived.
  [*] Happy Hour: Waiting customers will recover 15 satisfaction

  1. Host action (1 per turn)
     [1] seat C4
     [2] wait
     > seat C4
  >> Seated C4 at table 2.

  2. Waiter action (1 per turn)
     [1] order C4
     [2] wait
     > order C4
  >> C4 (VIP) chose Pasta ($12.96, 2 prep units).
```

The recording run activates Happy Hour on **turn 4**, when C4 arrives. The Host seats C4, and the Waiter takes the order in the same turn. C4 ends turn 4 at 95; the turn-5 recovery brings C4 back to 100. The VIP discount applies first, then Happy Hour: $18 × 0.9 × 0.8 = $12.96.

### Test notes

Both runs use piped input and produce identical results on every execution.

#### Victory run

```bash
./run.sh <<'EOF'
n
seat C1
wait
wait
n
wait
order C1
prepare C1
n
wait
serve C1
wait
y
seat C4
order C4
prepare C4
wait
wait
prepare C4
wait
serve C4
wait
seat C7
wait
wait
wait
order C7
prepare C7
wait
serve C7
wait
seat C10
wait
wait
wait
order C10
prepare C10
wait
wait
prepare C10
seat C13
order C13
prepare C10
wait
serve C10
prepare C13
wait
wait
prepare C13
wait
serve C13
wait
seat C16
wait
wait
wait
wait
wait
EOF
```

The first three `n` values decline Happy Hour on turns 1–3; the `y` activates it on turn 4. The run serves C1, C4, C7, C10, and C13. C16 arrives on turn 16 and cannot finish paying within 18 turns.

Expected final result:

```text
============================================================
                         GAME OVER
============================================================

                       FINAL RESULTS
------------------------------------------------------------
  Served Customers                5
  Unhappy Departures              0
  Paid Customers                  5
  Total Revenue              $82.26
  Avg. Paid Satisfaction       78.2
  Turns Used                     18

============================================================
                      VICTORY TARGETS
============================================================
  Objective                    Actual     Target   Status
------------------------------------------------------------
  Paid Customers                    5          4   [PASS]
  Total Revenue                $82.26     $45.00   [PASS]
  Avg. Paid Satisfaction         78.2       60.0   [PASS]
------------------------------------------------------------

                 *** VICTORY ACHIEVED! ***

                All 3 victory targets met.
============================================================
```

#### Defeat run

```bash
for turn in $(seq 1 18); do
  echo n        # Happy Hour
  echo wait     # Host
  echo wait     # Waiter
  echo wait     # Chef
done | ./run.sh
```

Every prompt is declined or answered with `wait`. C1 and C10 are the two customers who lose enough satisfaction to leave before the game ends.

Expected final result:

```text
============================================================
                         GAME OVER
============================================================

                       FINAL RESULTS
------------------------------------------------------------
  Served Customers                0
  Unhappy Departures              2
  Paid Customers                  0
  Total Revenue               $0.00
  Avg. Paid Satisfaction        N/A
  Turns Used                     18

============================================================
                      VICTORY TARGETS
============================================================
  Objective                    Actual     Target   Status
------------------------------------------------------------
  Paid Customers                    0          4   [FAIL]
  Total Revenue                 $0.00     $45.00   [FAIL]
  Avg. Paid Satisfaction          N/A       60.0   [FAIL]
------------------------------------------------------------

                       *** DEFEAT ***

                0 of 3 victory targets met.
============================================================
```

## Stage transitions

Each transition records what changed between tags, so the diff can be reviewed without reading every file.

### Stage 1 to Stage 2

- **New files:**
  - `RegularCustomer`, `VIPCustomer`, `CriticCustomer` — the three customer types, each with its own loss rate, discount, and menu choice.
  - `ArrivalSchedule` — maps each turn to its customer type, replacing the Stage 1 `(turn - 1) % 3` rule.
  - `PricingService` — applies the customer's discount and returns the price to lock on the order.
- **Changed files:**
  - `Customer` — now abstract; stores the loss and discount rates supplied by each subclass; `chooseItem` and `getTypeName` are polymorphic.
  - `Order` — `orderPrice` became `lockedPrice`, passed in when the order is taken.
  - `Money` — added `multiply(double)` for discounts.
  - `Waiter` — receives `PricingService`; `takeOrder` locks the price and shows the customer type.
  - `Cashier` — charges `getLockedPrice()`.
  - `GameController` — receives `ArrivalSchedule`; `processArrivals` asks it for the turn's customers.
  - `GameCLI` — shows the customer type and explains the VIP/Critic rules in the welcome.
  - `Main` — wires `PricingService` and `ArrivalSchedule`.
- **Deleted files:** none.
- **What still works:**
  - The Stage 1 turn order, seating, ordering, serving, eating/payment timing, abandonment, and victory/defeat rules are unchanged.
  - The Stage 2 victory and defeat runs above pass.
  - The Stage 1 defeat script still produces a defeat. The Stage 1 victory script is not reused verbatim because C10 now orders 2-unit Pasta; the Stage 2 victory run replaces it.
- **Design effect:**
  - Customer behaviour is polymorphic through `chooseItem`, `getDiscountRate`, and `getTypeName`; the loss rate is supplied by the subclass through the constructor. `GameController` has no customer type checks.
  - Arrival scheduling and pricing are separate responsibilities, so the controller only orchestrates the turn.
  - `Order.lockedPrice` prevents later menu or pricing changes from altering an existing order.

### Stage 2 to Stage 3

- **New files:**
  - `MenuItem` — interface shared by a-la-carte items and combos.
  - `ComboMeal` — a `MenuItem` built from two components; price is 90% of the sum, preparation units are summed.
  - `FrontStaff` — shared seating for the Waiter and Host.
  - `Host` — seats at most one queued customer per turn, in addition to the Waiter.
  - `HappyHourPolicy` — once-per-game activation, two-turn duration, 20% discount, and the 15-point satisfaction recovery.
- **Changed files:**
  - `AlaCarteItem` — implements `MenuItem`.
  - `Menu` — stores `List<MenuItem>` so combos and a-la-carte items share one menu.
  - `Order` — stores a `MenuItem` and keeps the locked price.
  - `Customer` — adds `boostSatisfaction`, `isWaitingForTableOrFood`, and `chooseMenuItem`; the subclasses return `MenuItem`.
  - `PricingService` — receives `HappyHourPolicy`; applies the customer discount first, then Happy Hour.
  - `Waiter` — extends `FrontStaff`; `takeOrder` uses `MenuItem`.
  - `GameController` — prompts the Host, offers Happy Hour, applies the recovery before the turn display, and advances its duration.
  - `GameCLI` — Host/Waiter/Chef/Cashier prompts, Happy Hour status and prompt, `[+]` arrivals, and `[*]` Happy Hour messages.
  - `Main` — builds the three combos, adds the Host, and wires `HappyHourPolicy`.
  - `README.md`, `docs/stage3_mermaid`, `docs/Stage3_ClassDiagram.png`, `docs/stage3_sequence_mermaid.txt` — Stage 3 documentation.
- **Deleted files:** none.
- **What still works:**
  - The Stage 1/2 turn order, seating, ordering, serving, eating/payment timing, abandonment, and victory/defeat rules are unchanged.
  - The Stage 3 victory and defeat runs above pass; `javac -Xlint:all` is clean.
  - The Stage 2 victory script is not reused because the menu now includes combos and the seeded choices changed; the Stage 3 victory run replaces it. The Stage 2 defeat script needs an `n` for the Happy Hour prompt; the Stage 3 defeat run includes it.
- **Design effect:**
  - `MenuItem` gives a composite/LSP design: `ComboMeal` is made of two `MenuItem`s, so `Menu`, `Order`, and `PricingService` treat combos and a-la-carte items uniformly.
  - `FrontStaff` shares seating without forcing the Host to implement order/serve; the Host adds a second seating action per turn.
  - `HappyHourPolicy` separates timing, discount, and recovery from the controller; `PricingService` composes the VIP and Happy Hour discounts.
  - `Order.lockedPrice` keeps placed orders unaffected by later Happy Hour changes.

## Design Diagrams

The Stage 1, Stage 2 and Stage 3 class diagrams are available as images.

| Stage | Diagram |
|---|---|
| 1 | [Stage1_ClassDiagram.png](docs/Stage1_ClassDiagram.png) |
| 2 | [Stage2_ClassDiagram.png](docs/Stage2_ClassDiagram.png) |
| 3 | [Stage3_ClassDiagram.png](docs/Stage3_ClassDiagram.png) |

Sequence diagram: [Stage 3 sequence diagram](docs/Stage3_SequenceDiagram.png) — follows C4 through payment and ends with the end-of-turn result. Staff waits and other customers' actions are noted but omitted from the diagram.