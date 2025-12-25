object TooLittleRemainingTracksConstraint extends Constraint{

  //This rule declares a puzzle invalid if, on any row/column, there are not enough possible cells to complete the line

  def apply(puzzle: Puzzle): Boolean = {
    puzzle.line.zip(Array.range(0,puzzle.num_rows)).forall((total: Int, index: Int) => puzzle.row(index).count((c: Cell) => c.isTrackPossible) >= total)
      && puzzle.col.zip(Array.range(0,puzzle.num_cols)).forall((total: Int, index: Int) => puzzle.column(index).count((c: Cell) => c.isTrackPossible) >= total)
  }
}
