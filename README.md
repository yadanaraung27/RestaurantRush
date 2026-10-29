# RestaurantRush

RestaurantRush is a turn-based restaurant management game written in Java for the SC2002 Object-Oriented Design & Programming assignment. The player manages the restaurant staff over 18 turns while customers arrive, queue for tables, order food, wait for the kitchen, eat, and pay. The game ends in victory or defeat based on paid customers, revenue, and average customer satisfaction.

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
- [Stage transitions](#stage-transitions)
  - [Stage 1 to Stage 2](#stage-1-to-stage-2)
  - [Stage 2 to Stage 3](#stage-2-to-stage-3)
- [Design documents](#design-documents)

## Project Stages

Each stage is a separate runnable version tagged in GitHub, and each builds on the one before.

| Stage | Git tag | What it adds |
|---|---|---|
| 1 | `stage-1` | Regular customers, an a-la-carte menu, Waiter/Chef/Cashier, the full turn loop, and victory/defeat. |
| 2 | `stage-2` | Replaces two of the regular arrivals with a VIP and a Critic. The VIP loses 5 satisfaction per waiting turn and pays 10% less; the Critic loses 12 per turn and chooses the most expensive item on the menu. Adds scheduled arrivals, `CustomerFactory`, and `PricingService`. |
| 3 | `stage-3` | Adds the Host (one extra seating per turn), ComboMeal (two items priced at 90% of their combined price, with preparation units summed), and Happy Hour (20% off orders taken while active, plus a satisfaction recovery at turn start). `PricingService` applies the VIP discount first, then Happy Hour. |

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

## Stage transitions

Each transition records what changed between tags, so the diff can be reviewed without reading every file.

### Stage 1 to Stage 2

To be completed when `stage-2` is tagged.

- **New files:** —
- **Changed files:** — *(reason)*
- **Deleted files:** —
- **Compare:** —

### Stage 2 to Stage 3

To be completed when `stage-3` is tagged.

- **New files:** —
- **Changed files:** — *(reason)*
- **Deleted files:** —
- **Compare:** —

## Design Diagrams

The Stage 1 class diagram is available as an image. Stage 2 and 3 diagrams will be added with their stages.

| Stage | Diagram |
|---|---|
| 1 | [Stage1_ClassDiagram.png](docs/Stage1_ClassDiagram.png) |
| 2 | |
| 3 | |