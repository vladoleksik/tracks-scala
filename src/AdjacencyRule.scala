object AdjacencyRule extends InferenceRule {

  //This rule updates the information about the puzzle so that the cells connecting to known tracks are considered
  //to certainly have a track themselves

  def apply(puzzle: Puzzle) : Puzzle = {
    val newPuzzle = Puzzle(puzzle)
    newPuzzle.state.foreach((row: Array[Cell])=>(row.foreach((cell: Cell)=>(
      if(cell.isTrackKnown) {
        if (cell.row > 0 && cell.goesUp) {
          if(!newPuzzle(cell.row - 1,cell.col).isTrackCertain) {
            if(!newPuzzle(cell.row - 1,cell.col).isTrackPossible) {
              newPuzzle.wasOverwritten=true
            }
            newPuzzle.state(cell.row - 1)(cell.col).state = 2
            newPuzzle.touch()
          }
        }
        if (cell.row < puzzle.num_rows-1 && cell.goesDown) {
          if (!newPuzzle(cell.row + 1, cell.col).isTrackCertain) {
            if (!newPuzzle(cell.row + 1, cell.col).isTrackPossible) {
              newPuzzle.wasOverwritten = true
            }
            newPuzzle.state(cell.row + 1)(cell.col).state = 2
            newPuzzle.touch()
          }
        }
        if (cell.col > 0 && cell.goesLeft) {
          if(!newPuzzle(cell.row, cell.col - 1).isTrackCertain) {
            if(!newPuzzle(cell.row, cell.col - 1).isTrackPossible) {
              newPuzzle.wasOverwritten = true
            }
            newPuzzle.state(cell.row)(cell.col - 1).state = 2
            newPuzzle.touch()
          }
        }
        if (cell.col < puzzle.num_cols-1 && cell.goesRight) {
          if(!newPuzzle(cell.row, cell.col + 1).isTrackCertain) {
            if(!newPuzzle(cell.row, cell.col + 1).isTrackPossible) {
              newPuzzle.wasOverwritten = true
            }
            newPuzzle.state(cell.row)(cell.col + 1).state = 2
            newPuzzle.touch()
          }
        }
      }
    ))))
    newPuzzle
  }
}
