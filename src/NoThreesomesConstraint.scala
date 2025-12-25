object NoThreesomesConstraint extends Constraint {

  //Any cells that are implied to contain an intersection will deem the puzzle invalid.

  def hasMoreConnections(puzzle: Puzzle, c: Cell) : Boolean = {
    var ct: Int = 0
    if(c.row>0) {
      if(puzzle(c.row-1,c.col).goesDown) {
        ct+=1
      }
    }
    if(c.row<puzzle.num_rows-1) {
      if(puzzle(c.row+1,c.col).goesUp) {
        ct+=1
      }
    }
    if(c.col>0) {
      if(puzzle(c.row,c.col-1).goesRight) {
        ct+=1
      }
    }
    if(c.col<puzzle.num_cols-1) {
      if(puzzle(c.row,c.col+1).goesLeft) {
        ct+=1
      }
    }
    if(ct>2)
      true
    else
      false
  }
  
  def apply(puzzle: Puzzle) : Boolean = {
    !puzzle.state.exists((r: Array[Cell])=>(r.exists((c: Cell)=>hasMoreConnections(puzzle,c))))
  }
}
