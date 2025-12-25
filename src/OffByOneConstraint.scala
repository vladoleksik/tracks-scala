object OffByOneConstraint extends Constraint {

  //This constraint declares a puzzle invalid if a row/column lacks one track, but it is not possible to add only one.

  def columnCanBeFilled(puzzle: Puzzle, index: Int, total: Int) : Boolean = {
    if (puzzle.column(index).count((c: Cell) => c.isTrackKnown) == total - 1) {
      if (!puzzle.column(index).exists((c: Cell) =>
        (c.row > 0 && c.goesUp && !puzzle(c.row - 1, c.col).isTrackKnown) || (c.row < puzzle.num_rows - 1 && c.goesDown && !puzzle(c.row + 1, c.col).isTrackKnown)
      )
        &&
        !puzzle.column(index).exists((c: Cell) =>
          !c.isTrackKnown && (c.col > 0 && puzzle(c.row, c.col - 1).isTrackPossible && (!puzzle(c.row, c.col - 1).isTrackKnown || puzzle(c.row, c.col - 1).goesRight)) || (c.col < puzzle.num_cols - 1 && puzzle(c.row, c.col + 1).isTrackPossible && (!puzzle(c.row, c.col + 1).isTrackKnown || puzzle(c.row, c.col + 1).goesLeft))
        )
      ) {
        return false
      }
    }
    true
  }

  def rowCanBeFilled(puzzle: Puzzle, index: Int, total: Int) : Boolean = {
    if (puzzle.row(index).count((c: Cell) => c.isTrackKnown) == total - 1) {
      if (!puzzle.row(index).exists((c: Cell) =>
        (c.col > 0 && c.goesLeft && !puzzle(c.row, c.col - 1).isTrackKnown) || (c.col < puzzle.num_cols - 1 && c.goesRight && !puzzle(c.row, c.col + 1).isTrackKnown)
      )
        &&
        !puzzle.row(index).exists((c: Cell) =>
          !c.isTrackKnown && (c.row > 0 && puzzle(c.row - 1, c.col).isTrackPossible && (!puzzle(c.row - 1, c.col).isTrackKnown || puzzle(c.row - 1, c.col).goesDown)) || (c.row < puzzle.num_rows - 1 && puzzle(c.row + 1, c.col).isTrackPossible && (!puzzle(c.row + 1, c.col).isTrackKnown || puzzle(c.row + 1, c.col).goesUp))
        )
      ) {
        return false
      }
    }
    true
  }

  def apply(puzzle: Puzzle) : Boolean = {

    puzzle.line.zip(Array.range(0,puzzle.num_rows)).forall((total: Int, index: Int) => rowCanBeFilled(puzzle, index, total))
      && puzzle.col.zip(Array.range(0,puzzle.num_cols)).forall((total: Int, index: Int) => columnCanBeFilled(puzzle, index, total))
  }
}
