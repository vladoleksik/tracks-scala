import PuzzleReaderWriter.{fileClose, getNumPuzzles, getPuzzle, initialize, putNumPuzzles, putSolution}

import scala.collection.mutable.Queue
import scala.collection.mutable

//object PuzzleSolver extends App {
@main def PuzzleSolver(inFile: String, outFile: String): Unit =

  def solve(puzzle: Puzzle, strategy: (Puzzle => Option[Puzzle])) : Puzzle = {
    strategy(puzzle).get
  }

  //This layer of abstraction is useful since it allows for the use of custom inference rules and validation constraints
  def customInferenceBacktrackingStrategy(infer: ((Puzzle, (Puzzle => Boolean)) => Puzzle), validate: (Puzzle => Boolean), plan: (Int,((Int=>(Puzzle=>List[Puzzle])),(Int=>Int)))) : (Puzzle => Option[Puzzle]) = {
    (puzzle: Puzzle) => inferenceBacktrackingStrategy(puzzle, infer, validate, plan)
  }

  def inferenceBacktrackingStrategy(puzzle: Puzzle, infer: ((Puzzle, (Puzzle=>Boolean)) => Puzzle), validate: (Puzzle=>Boolean),  plan: (Int,((Int=>(Puzzle=>List[Puzzle])),(Int=>Int)))) : Option[Puzzle] = {
    for(stage<-0 until plan(0)) {
      val t0 = System.currentTimeMillis()
      var t1 = System.currentTimeMillis()
      var stack: List[Puzzle] = List(puzzle)
      while((t1-t0)/1000<plan(1)(1)(stage)) {
        val currentPuzzle = stack.head
        stack = stack.tail
        currentPuzzle := infer(currentPuzzle, validate)
        if (validate(currentPuzzle)) {
          if (currentPuzzle.complete) {
            return Some(currentPuzzle)
          }
          else {
            stack = plan(1)(0)(stage)(currentPuzzle) ::: stack
          }
        }
        t1 = System.currentTimeMillis()
      }
    }
    None
  }

  //This abstraction allows the creation of an inference system with any set of rules/intermediary validation
  def customTrackInferrer(inferenceRules: List[InferenceRule]) : ((Puzzle, (Puzzle) => (Boolean)) => Puzzle) = {
    (puzzle: Puzzle, validation: (Puzzle) => (Boolean)) => tracksInference(puzzle, inferenceRules, validation)
  }

  def tracksInference(puzzle: Puzzle, inferenceRules: List[InferenceRule], validation: (Puzzle)=>Boolean) : Puzzle = {
    var newPuzzle = Puzzle(puzzle)
    while (inferenceRules.exists(rule => ({
          newPuzzle = rule(newPuzzle)
          newPuzzle.hasChanged && !newPuzzle.wasOverwritten
        })) && validation(newPuzzle))
    {
    newPuzzle.commit()
    }
    newPuzzle
  }

  //This abstraction allows the creation of a validation system from any set of constraints
  def customTracksValidator(constraints: List[Constraint]) : (Puzzle => Boolean) = {
    (puzzle: Puzzle) => tracksValidate(puzzle, constraints)
  }

  def tracksValidate(puzzle: Puzzle, constraints: List[Constraint]) : Boolean = {
    constraints.forall((c: Constraint)=>c(puzzle))
  }

  def findCellSomewhere(puzzle: Puzzle, start: Int = 0) : Cell = {
    val tries = List(0xd, 0x15, 0x19, 0x25, 0x29, 0x31)
    var minOptions: Int = 10
    var bestCell: Cell = puzzle(0,0)
    puzzle.state.foreach((r: Array[Cell])=>(
      r.foreach((c:Cell)=>{
        if(c.isTrackPossible && !c.isTrackKnown) {
          var options: Int = 0
          val cx: Cell = Cell(0,c.row,c.col)
          tries.foreach((t: Int)=>{
            cx.state = t
            var pos: Boolean = true
            if (cx.goesUp && (cx.row <= 0 || !puzzle.state(cx.row - 1)(cx.col).isTrackPossible || (puzzle.state(cx.row - 1)(cx.col).isTrackKnown && !puzzle.state(cx.row - 1)(cx.col).goesDown))) {
              pos = false
            }
            if (cx.goesDown && (cx.row >= puzzle.num_rows - 1 || !puzzle.state(cx.row + 1)(cx.col).isTrackPossible || (puzzle.state(cx.row + 1)(cx.col).isTrackKnown && !puzzle.state(cx.row + 1)(cx.col).goesUp))) {
              pos = false
            }
            if (cx.goesLeft && (cx.col <= 0 || !puzzle.state(cx.row)(cx.col - 1).isTrackPossible || (puzzle.state(cx.row)(cx.col - 1).isTrackKnown && !puzzle.state(cx.row)(cx.col - 1).goesRight))) {
              pos = false
            }
            if (cx.goesRight && (cx.col >= puzzle.num_cols - 1 || !puzzle.state(cx.row)(cx.col + 1).isTrackPossible || (puzzle.state(cx.row)(cx.col + 1).isTrackKnown && !puzzle.state(cx.row)(cx.col + 1).goesLeft))) {
              pos = false
            }
            if(pos) {
              options = options + 1
            }
          })
          if (!c.isTrackCertain) {
            options = options+1
          }
          if(options<minOptions) {
            minOptions = options
            bestCell = c
          }
        }
      }
      )
    ))
    bestCell
  }

  def findCell(puzzle: Puzzle, start: Int = 0) : Cell = {
    val next : Queue[(Int, Int)] = Queue(puzzle.endpoints(start))
    var vis : List[(Int, Int)] = List(puzzle.endpoints(start))
    while(next.nonEmpty) {
      val cur = next.dequeue()
      if(puzzle(cur(0), cur(1)).row > 0 && puzzle(cur(0), cur(1)).goesUp) {
        if(!vis.contains(cur(0)-1, cur(1))) {
          if (puzzle(cur(0) - 1, cur(1)).goesDown) {
            vis = vis :+ (cur(0)-1,cur(1))
            next.enqueue((cur(0)-1,cur(1)))
          }
          else {
            return puzzle(cur(0)-1,cur(1))
          }
        }
      }
      if (puzzle(cur(0), cur(1)).row < puzzle.num_rows-1 && puzzle(cur(0), cur(1)).goesDown) {
        if (!vis.contains(cur(0) + 1, cur(1))) {
          if (puzzle(cur(0) + 1, cur(1)).goesUp) {
            vis = vis :+ (cur(0) + 1, cur(1))
            next.enqueue((cur(0) + 1, cur(1)))
          }
          else {
            return puzzle(cur(0) + 1, cur(1))
          }
        }
      }
      if (puzzle(cur(0), cur(1)).col > 0 && puzzle(cur(0), cur(1)).goesLeft) {
        if (!vis.contains(cur(0), cur(1) - 1)) {
          if (puzzle(cur(0), cur(1) - 1 ).goesRight) {
            vis = vis :+ (cur(0), cur(1) - 1)
            next.enqueue((cur(0), cur(1) - 1))
          }
          else {
            return puzzle(cur(0), cur(1) - 1)
          }
        }
      }
      if (puzzle(cur(0), cur(1)).col < puzzle.num_cols - 1 && puzzle(cur(0), cur(1)).goesRight) {
        if (!vis.contains(cur(0), cur(1) + 1)) {
          if (puzzle(cur(0), cur(1) + 1).goesLeft) {
            vis = vis :+ (cur(0), cur(1) + 1)
            next.enqueue((cur(0), cur(1) + 1))
          }
          else {
            return puzzle(cur(0), cur(1) + 1)
          }
        }
      }
    }
    puzzle.state.head.head
  }

  def enumPossibilities(cell: Cell, order: Int = 1) : List[Cell] = {
    val keys = order match
      case 1 => List(0xd, 0x15, 0x19, 0x25, 0x29, 0x31) //with fwd
      case 2 => List(0x31, 0x19, 0x25, 0x29, 0xd, 0x15)
      //case 3 => List(0xd, 0x31, 0x29, 0x25, 0x15, 0x19) //with fwd
      //case 4 => List(0x25, 0x29, 0x15, 0x19, 0xd, 0x31) //with reverse
      //case 5 => List(0x15, 0x29, 0x25, 0x31, 0x19, 0xd)
      //case 6 => List(0x25, 0x15, 0xd, 0x19, 0x29, 0x31)
    //val keys = List(0x31, 0x19, 0x25, 0x29, 0xd, 0x15) //with reverse, (almost) fails on 63, 78, fails on 49, 66, 43, 58, 72, 64, 57
    var list :List[Cell] = keys.foldLeft(List[Cell]())((acc,v)=>(
      acc:+Cell(v,cell.row,cell.col)
    ))
    if(!cell.isTrackCertain) {
      list = list ::: List(Cell(0x3,cell.row,cell.col))
    }
    list
  }

  def generateFromStart(puzzle: Puzzle) : List[Puzzle] = {
    var list : List[Puzzle] = Nil
    enumPossibilities(findCell(puzzle)).foldLeft(List[Puzzle]())((acc, cell)=>(
      {
        val newPuzzle = Puzzle(puzzle)
        newPuzzle.state(cell.row)(cell.col).state = cell.state
        acc :+ newPuzzle
      }
    ))
  }

  def generateFromEnd(puzzle: Puzzle): List[Puzzle] = {
    var list: List[Puzzle] = Nil
    enumPossibilities(findCell(puzzle,1),2).foldLeft(List[Puzzle]())((acc, cell) => (
      {
        val newPuzzle = Puzzle(puzzle)
        newPuzzle.state(cell.row)(cell.col).state = cell.state
        acc :+ newPuzzle
      }
      ))
  }

  def generateSomewhere(puzzle: Puzzle): List[Puzzle] = {
    var list: List[Puzzle] = Nil
    enumPossibilities(findCellSomewhere(puzzle),2).foldLeft(List[Puzzle]())((acc, cell) => (
      {
        val newPuzzle = Puzzle(puzzle)
        newPuzzle.state(cell.row)(cell.col).state = cell.state
        acc :+ newPuzzle
      }
      ))
  }

  def customPlan(stages: List[(Int, (Puzzle => List[Puzzle]))]) : (Int, ((Int =>(Puzzle => List[Puzzle])), (Int => Int))) = {
    (stages.length,((stage: Int) => stages(stage)._2, (stage: Int) => stages(stage)._1))
  }


  PuzzleReaderWriter initialize (inFile, outFile)

  val n = getNumPuzzles()

  PuzzleReaderWriter putNumPuzzles n

  //Here, the rules are added to the backbone of our algorithm and the solving strategy is defined
  val strategy = customInferenceBacktrackingStrategy(
    customTrackInferrer(
      List(
        AdjacencyRule,
        NoTracksRemainingRule,
        OnlyTracksRemainingRule,
        LessThanTwoNeighboursRule,
        TwoGoodNeighboursRule,
        TwoGoodConnectingNeighboursRule,
        BottleneckRule,
        RemoteAreaRule
      )
    ),
    customTracksValidator(
      List(
        NoContradictionConstraint,
        MoreTracksThanPossibleConstraint,
        TooLittleRemainingTracksConstraint,
        AdjacencyConstraint,
        NoLoopsConstraint,
        NoThreesomesConstraint,
        OffByOneConstraint,
        NonIsolationConstraint
      )
    ),
    customPlan(
      List(
        (40, generateFromStart),
        (60,generateFromEnd),
        (200, generateSomewhere)
      )
    )
  )

  for(i <- 0 until n) {
      val puzzle = PuzzleReaderWriter getPuzzle i

      val solution = solve(puzzle, strategy)

      PuzzleReaderWriter putSolution solution
  }

  fileClose()
//}
