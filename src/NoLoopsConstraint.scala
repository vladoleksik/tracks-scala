import scala.collection.mutable

object NoLoopsConstraint extends Constraint {

  //This constraint declares a puzzle invalid once it contains a loop.

  def apply(puzzle: Puzzle) : Boolean = {
    var vis: List[(Int, Int)] = Nil
    puzzle.state.foreach((r: Array[Cell])=>(
      r.foreach((c: Cell)=>(
        if(c.isTrackKnown && !vis.contains((c.row,c.col))) {
          //If it has not been visited before, a cell is checked to not be a part of a loop.
          var next : mutable.Queue[(Int, Int)] = mutable.Queue((c.row,c.col))
          var chk = 0
          while(next.nonEmpty) {
            val cur = next.dequeue()
            if(cur == puzzle.endpoints(0)) {
              chk^=0x1
            }
            if(cur == puzzle.endpoints(1)) {
              chk^=0x2
            }
            vis = vis :+ cur
            var ct: Int = 0
            if (puzzle(cur(0), cur(1)).row > 0 && puzzle(cur(0), cur(1)).goesUp) {
              if (puzzle(cur(0) - 1, cur(1)).goesDown) {
                if (!vis.contains(cur(0) - 1, cur(1))) {
                  next.enqueue((cur(0) - 1, cur(1)))
                }
                else {
                  ct+=1
                }
              }
            }
            if (puzzle(cur(0), cur(1)).row < puzzle.num_rows - 1 && puzzle(cur(0), cur(1)).goesDown) {
              if (puzzle(cur(0) + 1, cur(1)).goesUp) {
                if (!vis.contains(cur(0) + 1, cur(1))) {
                  next.enqueue((cur(0) + 1, cur(1)))
                }
                else {
                  ct+=1
                }
              }
            }
            if (puzzle(cur(0), cur(1)).col > 0 && puzzle(cur(0), cur(1)).goesLeft) {
              if (puzzle(cur(0), cur(1) - 1).goesRight) {
                if (!vis.contains(cur(0), cur(1) - 1)) {
                  next.enqueue((cur(0), cur(1) - 1))
                }
                else {
                  ct+=1
                }
              }
            }
            if (puzzle(cur(0), cur(1)).col < puzzle.num_cols - 1 && puzzle(cur(0), cur(1)).goesRight) {
              if (puzzle(cur(0), cur(1) + 1).goesLeft) {
                if (!vis.contains(cur(0), cur(1) + 1)) {
                  next.enqueue((cur(0), cur(1) + 1))
                }
                else {
                  ct+=1
                }
              }
            }
            if(ct==2) {
              return false
            }
          }
          if(chk==0x3 && !puzzle.complete) {
            return false
          }
        }
      ))
    ))
    true
  }
}
