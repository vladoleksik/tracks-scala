object MoreTracksThanPossibleConstraint extends Constraint {

  //This constraint declares invalid any puzzle with more tracks on a row/column than the hint allows.

  def countOnRow(puzzle: Puzzle, index: Int) : Int = {
    puzzle.row(index).count((c: Cell)=>c.isTrackCertain)
  }

  def countOnColumn(puzzle: Puzzle, index: Int) : Int = {
    puzzle.column(index).count((c: Cell)=>c.isTrackCertain)
  }

  def apply(puzzle: Puzzle) : Boolean = {
    puzzle.line.zip(Array.range(0,puzzle.num_rows)).forall((number: Int, index: Int) => countOnRow(puzzle,index)<=number)
      && puzzle.col.zip(Array.range(0,puzzle.num_cols)).forall((number: Int, index: Int) => countOnColumn(puzzle, index)<=number)
  }
}
