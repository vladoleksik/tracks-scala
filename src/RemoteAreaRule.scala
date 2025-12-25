import scala.collection.mutable

object RemoteAreaRule extends InferenceRule {

  //This rule marks all cells in areas surrounded by impossible cells as impossible.

  def floodfill(puzzle: Puzzle) : (Array[Array[Int]], Int) = {
    val matrix = Array.ofDim[Int](puzzle.num_rows, puzzle.num_cols)
    var ct: Int = 0
    for (i <- 0 until puzzle.num_rows) {
      for (j <- 0 until puzzle.num_cols) {
        if (puzzle.state(i)(j).isTrackPossible && matrix(i)(j) == 0) {
          ct = ct + 1
          //floodfill
          val stack = mutable.Stack[(Int, Int)]((i, j))
          while (stack.nonEmpty) {
            val (x, y) = stack.pop()
            if (x >= 0 && x < puzzle.num_rows && y >= 0 && y < puzzle.num_cols && matrix(x)(y) == 0) {
              if (puzzle.state(x)(y).isTrackPossible) {
                matrix(x)(y) = ct
                if(x > 0 && (!puzzle(x - 1, y).isTrackKnown || puzzle(x - 1, y).goesDown))
                  stack.push((x - 1, y))
                if(x < puzzle.num_rows - 1 && (!puzzle(x + 1, y).isTrackKnown || puzzle(x + 1, y).goesUp))
                  stack.push((x + 1, y))
                if(y > 0 && (!puzzle(x, y - 1).isTrackKnown || puzzle(x, y - 1).goesRight))
                  stack.push((x, y - 1))
                if (y < puzzle.num_cols - 1 && (!puzzle(x, y + 1).isTrackKnown || puzzle(x, y + 1).goesLeft))
                  stack.push((x, y + 1))
              }
            }
          }
        }
      }
    }
    (matrix, ct)
  }

  def apply(puzzle: Puzzle) : Puzzle = {

    val res = floodfill(puzzle)
    val matrix = res(0)
    val noOfAreas = res(1)

    val nonRemoteSet = mutable.Set[Int]()

    nonRemoteSet.add(matrix(puzzle.endpoints(0)(0))(puzzle.endpoints(0)(1)))
    nonRemoteSet.add(matrix(puzzle.endpoints(1)(0))(puzzle.endpoints(1)(1)))

    val newPuzzle = Puzzle(puzzle)
    puzzle.state.foreach((r: Array[Cell])=>(
      r.foreach((cell: Cell)=> {
        val comp = matrix(cell.row)(cell.col)
        if (comp>0 && comp<=noOfAreas && !nonRemoteSet.contains(comp)) {
          if (puzzle(cell.row, cell.col).isTrackCertain) {
            newPuzzle.wasOverwritten = true
          }
          newPuzzle.state(cell.row)(cell.col).state = 0x3
          newPuzzle.touch()
        }
      })
    ))
    newPuzzle
  }
}
