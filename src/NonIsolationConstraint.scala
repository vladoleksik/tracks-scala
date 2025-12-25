import scala.collection.mutable
import scala.collection.mutable.Queue

object NonIsolationConstraint extends Constraint {

  //This rule deems the puzzle invalid if a track area gets isolated from the rest of the puzzle

  def floodfill(puzzle: Puzzle) : (Array[Array[Int]], Int) = {
    val matrix = Array.ofDim[Int](puzzle.num_rows, puzzle.num_cols)
    var ct: Int = 0
    puzzle.state.foreach((r: Array[Cell]) =>(
      r.foreach((c: Cell) => {
        if (!c.isTrackKnown && matrix(c.row)(c.col) == 0) {
          ct = ct + 1
          val stack = mutable.Stack[(Int, Int)]((c.row, c.col))
          while (stack.nonEmpty) {
            val (x, y) = stack.pop()
            if (x >= 0 && x < puzzle.num_rows && y >= 0 && y < puzzle.num_cols && matrix(x)(y) == 0) {
              if (!puzzle.state(x)(y).isTrackKnown) {
                matrix(x)(y) = ct
                stack.push((x - 1, y))
                stack.push((x + 1, y))
                stack.push((x, y - 1))
                stack.push((x, y + 1))
              }
            }
          }
        }
      })
    ))
    (matrix, ct)
  }

  def followTracks(puzzle: Puzzle, matrix: Array[Array[Int]]) : (Array[Array[Int]], Int) = {
    var ct2: Int = 0
    val newMatrix = matrix.clone()
    puzzle.state.foreach((r: Array[Cell]) =>(
      r.foreach((c: Cell) => {
        if (newMatrix(c.row)(c.col) == 0) {
          ct2 = ct2 - 1
          var next = Queue((c.row, c.col))
          var vis = List((c.row, c.col))
          while (next.nonEmpty) {
            val cur = next.dequeue()
            newMatrix(cur(0))(cur(1)) = ct2
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
        }
      })
      ))
    (newMatrix, ct2)
  }

  def getAreasConnected(puzzle: Puzzle, matrix: Array[Array[Int]], noOfTrackPortions: Int) : Array[mutable.Set[Int]] = {
    val components = Array.ofDim[mutable.Set[Int]](noOfTrackPortions + 1)
    for (i <- 0 to noOfTrackPortions) {
      components(i) = mutable.Set[Int]()
    }
    puzzle.state.foreach((r: Array[Cell]) => (
      r.foreach((cell: Cell) => (
        if (cell.isTrackKnown) {
          var ct3 = 0
          var comp: Int = 0
          if (cell.row > 0) {
            if (puzzle(cell.row, cell.col).goesUp) {
              if (puzzle(cell.row - 1, cell.col).isTrackKnown) {
                ct3 += 1
              }
              else {
                comp = matrix(cell.row - 1)(cell.col)
              }
            }
          }
          if (cell.row < puzzle.num_rows - 1) {
            if (puzzle(cell.row, cell.col).goesDown) {
              if (puzzle(cell.row + 1, cell.col).isTrackKnown) {
                ct3 += 1
              }
              else {
                comp = matrix(cell.row + 1)(cell.col)
              }
            }
          }
          if (cell.col > 0) {
            if (puzzle(cell.row, cell.col).goesLeft) {
              if (puzzle(cell.row, cell.col - 1).isTrackKnown) {
                ct3 += 1
              }
              else {
                comp = matrix(cell.row)(cell.col - 1)
              }
            }
          }
          if (cell.col < puzzle.num_cols - 1) {
            if (puzzle(cell.row, cell.col).goesRight) {
              if (puzzle(cell.row, cell.col + 1).isTrackKnown) {
                ct3 += 1
              }
              else {
                comp = matrix(cell.row)(cell.col + 1)
              }
            }
          }
          if (ct3 <= 1) {
            //println(cell.row + " " + cell.col + " " + -matrix(cell.row)(cell.col)+" "+comp)
            components(-matrix(cell.row)(cell.col)).add(comp)
          }
          if (puzzle.endpoints.contains((cell.row, cell.col))) {
            components(-matrix(cell.row)(cell.col)).add(0)
          }
        }
        ))
      ))
    components
  }

  def Kruskal(components: Array[mutable.Set[Int]], noOfAreas: Int) : Array[Int] = {
    val corresp = Array.ofDim[Int](noOfAreas + 1)
    for (i <- 0 to noOfAreas) {
      corresp(i) = i
    }
    components.filter((s: mutable.Set[Int]) => s.size == 2).foreach((s: mutable.Set[Int]) => ({
      val a = s.head
      val b = s.last
      val ca = corresp(a)
      val cb = corresp(b)
      if (ca < cb) {
        for (i <- 0 to noOfAreas) {
          if (corresp(i) == cb) {
            corresp(i) = ca
          }
        }
      }
      else {
        for (i <- 0 to noOfAreas) {
          if (corresp(i) == ca) {
            corresp(i) = cb
          }
        }
      }
    }))
    corresp
  }

  def apply(puzzle: Puzzle) : Boolean = {

    //Perform floodfill to identify the separate areas in the puzzle
    var res = floodfill(puzzle)
    var matrix  = res(0)
    val noOfAreas = res(1)

    //Perform BFS to identify the unique track portions
    res = followTracks(puzzle, matrix)
    matrix = res(0)
    val noOfTrackPortions = -res(1)

    //For each track portion, find the areas it connects
    val components = getAreasConnected(puzzle, matrix, noOfTrackPortions)

    //Perform Kruskal to see if there are areas isolated from each other (see if the graph/puzzle is connected)
    val corresp = Kruskal(components, noOfAreas)

    components.forall((s: mutable.Set[Int])=>s.forall((x: Int)=>(corresp(x)==0)))
  }
}
