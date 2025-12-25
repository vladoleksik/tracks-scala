object NoContradictionConstraint  extends Constraint {

  //This constraint declares any puzzle for which a cell can be proven in two different states (any inconsistent puzzle)
  //invalid.

  def apply(puzzle: Puzzle) : Boolean = {
    !puzzle.wasOverwritten
  }
}
