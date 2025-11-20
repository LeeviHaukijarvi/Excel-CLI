# Spreadsheet Application – Use Cases

## Actors
- **User** – interacts with the spreadsheet via command-line interface (CLI).
- **File System** – handles reading and writing spreadsheet files (S2V format).
- **Formula Engine** – processes and evaluates formulas and dependencies.

---

## Use Case 1: Create New Spreadsheet
**Actor:**
- User

**Description:**
The user creates a new, empty spreadsheet. The system initializes all cell structures and clears previous data or dependencies.

**Includes:**
- Initialize cell grid
- Reset formula dependencies

**Preconditions:**
- Application is running.

**Postconditions:**
- A new empty spreadsheet exists in memory.

**Exceptions:**
- Memory allocation or initialization error.

---

## Use Case 2: Load Spreadsheet from File
**Actors:**
- User
- File System

**Description:**
The user loads a spreadsheet stored in an S2V file. The File System retrieves the file, and the Formula Engine processes formulas and computes cell values.

**Includes:**
- Retrieve spreadsheet from S2V file
- Parse and calculate formulas
- Calculate cell dependencies

**Preconditions:**
- The file exists and follows the S2V format.

**Postconditions:**
- Spreadsheet loaded into memory with all cell values computed.

**Exceptions:**
- File not found or invalid format.
- Circular dependency or formula syntax error detected.

---

## Use Case 3: Save Spreadsheet to File
**Actors:**
- User
- File System

**Description:**
The user saves the current spreadsheet to disk in S2V format.

**Includes:**
- Convert spreadsheet contents to S2V text
- Write file to disk

**Preconditions:**
- A spreadsheet exists in memory.

**Postconditions:**
- File saved successfully to the specified path.

**Exceptions:**
- Write permission denied or disk full.

---

## Use Case 4: View Spreadsheet Contents
**Actor:**
- User

**Description:**
The user views the spreadsheet in textual format, displaying current cell contents and evaluated values.

**Preconditions:**
- Spreadsheet loaded or created.

**Postconditions:**
- Spreadsheet displayed to the user.

---

## Use Case 5: Edit Cell Content
**Actors:**
- User
- Formula Engine

**Description:**
The user modifies the content of a cell (text, number, or formula). The Formula Engine validates and processes formulas and updates dependent cells.

**Includes:**
- Parse and calculate formulas
- Automatically update dependent cells

**Preconditions:**
- A spreadsheet is active in memory.

**Postconditions:**
- Cell content updated.
- Dependent cells recalculated.

**Exceptions:**
- Syntax error or circular dependency detected.

---

## Use Case 6: Retrieve Cell Value
**Actors:**
- User
- Formula Engine

**Description:**
The user requests the value of a specific cell. The Formula Engine evaluates the cell and returns its current numeric or text value.

**Preconditions:**
- Spreadsheet is active.

**Postconditions:**
- Cell value returned in correct format.

**Exceptions:**
- Invalid cell reference.

---

## Use Case 7: Apply Built-in Functions
**Actors:**
- User
- Formula Engine

**Description:**
The user applies supported functions (SUMA, MIN, MAX, PROMEDIO) within a formula.

**Includes:**
- Handle cell ranges (e.g., A1:B3)
- Evaluate nested or combined functions

**Preconditions:**
- Spreadsheet and referenced cells exist.

**Postconditions:**
- Formula result computed and stored.

**Exceptions:**
- Invalid range or function syntax.

---

## Use Case 8: Detect and Correct Syntax Errors in Formulas
**Actors:**
- User
- Formula Engine

**Description:**
When entering a formula, the system detects syntax errors and informs the user for correction.

**Preconditions:**
- Formula input provided by the user.

**Postconditions:**
- Error reported or formula accepted and evaluated.

---

## Use Case 9: Exit Program
**Actor:**
- User

**Description:**
The user terminates the program through the CLI menu.

**Preconditions:**
- Application running.

**Postconditions:**
- Program exits gracefully, optionally prompting to save unsaved work.

---

## Internal Use Cases (System Responsibilities)

These are not user-initiated but occur internally:

- **Initialize cell grid** – sets up empty spreadsheet data structure.
- **Reset formula dependencies** – clears dependency graph.
- **Parse and calculate formulas** – processes and computes formula values.
- **Calculate cell dependencies** – determines relationships between cells.
- **Automatically update dependent cells** – recalculates affected cells when one changes.
- **Handle cell references (A1, B2, A1:B3)** – interprets cell and range addresses.
- **Convert spreadsheet to/from S2V** – formats file storage and parsing.
