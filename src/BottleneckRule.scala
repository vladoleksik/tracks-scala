import scala.collection.mutable.Queue

object BottleneckRule extends InferenceRule {

  //This rule declares the impossibility of having tracks in cells that are separated from the endpoints by a 'full' row or column.

  def filterCellCertain(c: Cell): Boolean = {
    c.isTrackCertain
  }

  def countCertainCell(c: Cell) : Int = {
    if(c.isTrackKnown) {
      1
    }
    else {
      0
    }
  }

  def followPath(puzzle: Puzzle, startPoint: (Int, Int), endPoint: (Int, Int)) : Puzzle = {
    var newPuzzle = Puzzle(puzzle)
    var next: Queue[(Int, Int)] = Queue(startPoint)
    var vis: List[(Int, Int)] = List(startPoint)
    while (next.nonEmpty) {
      val cur = next.dequeue()
      if(vis.count((p: (Int, Int))=>(p(0)==cur(0)))==puzzle.line(cur(0))) {
        newPuzzle.state.foreach((r: Array[Cell]) => if ((cur(0) - r(0).row).sign == (endPoint(0) - cur(0)).sign) {
          r.foreach((c: Cell) => if (!c.isTrackCertain) {
            if (c.isTrackPossible) {
              newPuzzle.state(c.row)(c.col).state = 0x3
              newPuzzle.touch()
            }
          })
        })
      }
      if(vis.count((p: (Int, Int))=>(p(1)==cur(1)))==puzzle.col(cur(1))) {
        newPuzzle.state.foreach((r: Array[Cell]) => (
          r.foreach((c: Cell) => (
            if ((cur(1) - endPoint(1)).sign == (c.col - cur(1)).sign) {
              if (!c.isTrackCertain) {
                if (c.isTrackPossible) {
                  newPuzzle.state(c.row)(c.col).state = 0x3
                  newPuzzle.touch()
                }
              }
            }
            ))
          ))
      }
      //count rows
      //newPuzzle = function(newPuzzle, cur, endPoint)
      //print(cur(0),cur(1))
      //println()
      if (puzzle(cur(0), cur(1)).row > 0 && puzzle(cur(0), cur(1)).goesUp) {
        if (!vis.contains(cur(0) - 1, cur(1))) {
          if (puzzle(cur(0) - 1, cur(1)).goesDown) {
            vis = vis :+ (cur(0) - 1, cur(1))
            next.enqueue((cur(0) - 1, cur(1)))
          }
        }
      }
      if (puzzle(cur(0), cur(1)).row < puzzle.num_rows - 1 && puzzle(cur(0), cur(1)).goesDown) {
        if (!vis.contains(cur(0) + 1, cur(1))) {
          if (puzzle(cur(0) + 1, cur(1)).goesUp) {
            vis = vis :+ (cur(0) + 1, cur(1))
            next.enqueue((cur(0) + 1, cur(1)))
          }
        }
      }
      if (puzzle(cur(0), cur(1)).col > 0 && puzzle(cur(0), cur(1)).goesLeft) {
        if (!vis.contains(cur(0), cur(1) - 1)) {
          if (puzzle(cur(0), cur(1) - 1).goesRight) {
            vis = vis :+ (cur(0), cur(1) - 1)
            next.enqueue((cur(0), cur(1) - 1))
          }
        }
      }
      if (puzzle(cur(0), cur(1)).col < puzzle.num_cols - 1 && puzzle(cur(0), cur(1)).goesRight) {
        if (!vis.contains(cur(0), cur(1) + 1)) {
          if (puzzle(cur(0), cur(1) + 1).goesLeft) {
            vis = vis :+ (cur(0), cur(1) + 1)
            next.enqueue((cur(0), cur(1) + 1))
          }
        }
      }
    }
    newPuzzle
  }

  def apply(puzzle: Puzzle) : Puzzle = {
    var newPuzzle = Puzzle(puzzle)
    newPuzzle = followPath(newPuzzle,newPuzzle.endpoints(0),newPuzzle.endpoints(1))
    newPuzzle = followPath(newPuzzle,newPuzzle.endpoints(1),newPuzzle.endpoints(0))
    newPuzzle
  }
}
