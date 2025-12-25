object AdjacencyConstraint extends Constraint {

  //This constraint check for the existence of adjacent cells that do not connect to each other in a way that would allow the puzzle to be solved at a later stage. ('De-railings')

  private def check(puzzle: Puzzle, cell: Cell) : Boolean = {
    if(cell.isTrackKnown) {
      if (!puzzle.endpoints.contains(cell.position)) {
        if (cell.goesUp && (cell.row <= 0 || !puzzle.state(cell.row - 1)(cell.col).isTrackPossible || (puzzle.state(cell.row - 1)(cell.col).isTrackKnown && !puzzle.state(cell.row - 1)(cell.col).goesDown))) {
          return false
        }
        if (cell.goesDown && (cell.row >= puzzle.num_rows - 1 || !puzzle.state(cell.row + 1)(cell.col).isTrackPossible || (puzzle.state(cell.row + 1)(cell.col).isTrackKnown && !puzzle.state(cell.row + 1)(cell.col).goesUp))) {
          return false
        }
        if (cell.goesLeft && (cell.col <= 0 || !puzzle.state(cell.row)(cell.col - 1).isTrackPossible || (puzzle.state(cell.row)(cell.col - 1).isTrackKnown && !puzzle.state(cell.row)(cell.col - 1).goesRight))) {
          return false
        }
        if (cell.goesRight && (cell.col >= puzzle.num_cols - 1 || !puzzle.state(cell.row)(cell.col + 1).isTrackPossible || (puzzle.state(cell.row)(cell.col + 1).isTrackKnown && !puzzle.state(cell.row)(cell.col + 1).goesLeft))) {
          return false
        }
      } else {
        if (cell.goesUp && cell.row > 0 && (!puzzle.state(cell.row - 1)(cell.col).isTrackPossible || (puzzle.state(cell.row - 1)(cell.col).isTrackKnown && !puzzle.state(cell.row - 1)(cell.col).goesDown))) {
          return false
        }
        if (cell.goesDown && cell.row < puzzle.num_rows - 1 && (!puzzle.state(cell.row + 1)(cell.col).isTrackPossible || (puzzle.state(cell.row + 1)(cell.col).isTrackKnown && !puzzle.state(cell.row + 1)(cell.col).goesUp))) {
          return false
        }
        if (cell.goesLeft && cell.col > 0 && (!puzzle.state(cell.row)(cell.col - 1).isTrackPossible || (puzzle.state(cell.row)(cell.col - 1).isTrackKnown && !puzzle.state(cell.row)(cell.col - 1).goesRight))) {
          return false
        }
        if (cell.goesRight && cell.col < puzzle.num_cols - 1 && (!puzzle.state(cell.row)(cell.col + 1).isTrackPossible || (puzzle.state(cell.row)(cell.col + 1).isTrackKnown && !puzzle.state(cell.row)(cell.col + 1).goesLeft))) {
          return false
        }
      }
    }
    true
  }

  def apply(puzzle: Puzzle) : Boolean = {
    puzzle.state.forall((row: Array[Cell]) => row.forall((cell: Cell) => check(puzzle, cell)))
  }
}
