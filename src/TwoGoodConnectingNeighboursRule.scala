object TwoGoodConnectingNeighboursRule extends InferenceRule {

  //This rule fills the cell in if it has exactly two neighbours that are known to be tracks and are connected to it

  def apply(puzzle: Puzzle): Puzzle = {
    val newPuzzle = Puzzle(puzzle)
    newPuzzle.state.foreach((row: Array[Cell]) => (row.foreach((cell: Cell) => (
      if (cell.isTrackPossible && !cell.isTrackKnown) {
        var ct: Int = 0
        var track: Int = 0
        if (cell.row > 0) {
          if (newPuzzle(cell.row - 1, cell.col).isTrackKnown) {
            if (newPuzzle(cell.row - 1, cell.col).goesDown) {
              ct += 1
              track+=0x2
            }
          }
        }
        if (cell.row < puzzle.num_rows - 1) {
          if (newPuzzle(cell.row + 1, cell.col).isTrackKnown) {
            if (newPuzzle(cell.row + 1, cell.col).goesUp) {
              ct += 1
              track+=0x1
            }
          }
        }
        if (cell.col > 0) {
          if (newPuzzle(cell.row, cell.col - 1).isTrackKnown) {
            if (newPuzzle(cell.row, cell.col - 1).goesRight) {
              ct += 1
              track+=0x8
            }
          }
        }
        if (cell.col < puzzle.num_cols - 1) {
          if (newPuzzle(cell.row, cell.col + 1).isTrackKnown) {
            if (newPuzzle(cell.row, cell.col + 1).goesLeft) {
              ct += 1
              track+=0x4
            }
          }
        }
        if (ct == 2) {
          newPuzzle.state(cell.row)(cell.col).state = (track<<2)+1
          newPuzzle.touch()
        }
      }
      ))))
    newPuzzle
  }
}
