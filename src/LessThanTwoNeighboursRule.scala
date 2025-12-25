object LessThanTwoNeighboursRule extends InferenceRule {

  //This rule marks any cell with not sufficient neighbours to have a track as impossible.

  def apply(puzzle: Puzzle): Puzzle = {
    val newPuzzle = Puzzle(puzzle)
    var changed: Boolean = true
    while(changed) {
      changed = false
      newPuzzle.state.foreach((row: Array[Cell]) => (row.foreach((cell: Cell) => (
        if (cell.isTrackPossible) {
          var ct: Int = 0
          if (cell.row > 0) {
            if (newPuzzle(cell.row - 1, cell.col).isTrackPossible) {
              if (!newPuzzle(cell.row - 1, cell.col).isTrackKnown) {
                ct += 1
              }
              else {
                if (newPuzzle(cell.row - 1, cell.col).goesDown) {
                  ct += 1
                }
              }
            }
          }
          if (cell.row < puzzle.num_rows - 1) {
            if (newPuzzle(cell.row + 1, cell.col).isTrackPossible) {
              if (!newPuzzle(cell.row + 1, cell.col).isTrackKnown) {
                ct += 1
              }
              else {
                if (newPuzzle(cell.row + 1, cell.col).goesUp) {
                  ct += 1
                }
              }
            }
          }
          if (cell.col > 0) {
            if (newPuzzle(cell.row, cell.col - 1).isTrackPossible) {
              if (!newPuzzle(cell.row, cell.col - 1).isTrackKnown) {
                ct += 1
              }
              else {
                if (newPuzzle(cell.row, cell.col - 1).goesRight) {
                  ct += 1
                }
              }
            }
          }
          if (cell.col < puzzle.num_cols - 1) {
            if (newPuzzle(cell.row, cell.col + 1).isTrackPossible) {
              if (!newPuzzle(cell.row, cell.col + 1).isTrackKnown) {
                ct += 1
              }
              else {
                if (newPuzzle(cell.row, cell.col + 1).goesLeft) {
                  ct += 1
                }
              }
            }
          }
          if (!newPuzzle.endpoints.contains((cell.row, cell.col))) {
            if (ct < 2) {
              if (cell.isTrackCertain) {
                //print(cell.row, cell.col)
                newPuzzle.wasOverwritten = true
              }
              newPuzzle.state(cell.row)(cell.col).state = 3
              newPuzzle.touch()
              changed = true
            }
          }
        }
        ))))
    }
    newPuzzle
  }
}
