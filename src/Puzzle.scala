import scala.annotation.targetName

class Puzzle(puzzle: List[String]) {

  //Here, the structure of a Tracks puzzle is defined. Data is represented by the array of cells, the hints, the dimensions,
  //the endpoints, and flags about its consistency.

  var hasChanged: Boolean = false
  var wasOverwritten: Boolean = false

  private val dimensions = puzzle.head.split(' ').last.split('x')

  var num_rows: Int = dimensions.last.toInt
  var num_cols: Int = dimensions.head.toInt

  var col: Array[Int] = puzzle(1).split(' ').foldLeft(Array[Int]())((ac, x)=>ac :+ x.toInt)

  private val grid = puzzle.slice(2,puzzle.length)

  var line: Array[Int] = grid.foldLeft(Array[Int]())((ac,s)=>ac :+ s.split(' ').last.toInt)
  private val tracks = grid.foldLeft(Array[Array[String]]())((ac,s)=>ac :+ s.split(' ').slice(0,s.split(' ').length-1))

  var state: Array[Array[Cell]] = tracks.foldLeft(Array[Array[Cell]]())((ac1,x)=>ac1 :+ x.foldLeft(Array[Cell]())((ac2,y)=>
    ac2 :+ (y match
      case "_" => Cell(0x0,ac1.length,ac2.length)
      case "║" => Cell(0xd,ac1.length,ac2.length)
      case "╔" => Cell(0x15,ac1.length,ac2.length)
      case "╚" => Cell(0x19,ac1.length,ac2.length)
      case "╗" => Cell(0x25,ac1.length,ac2.length)
      case "╝" => Cell(0x29,ac1.length,ac2.length)
      case "═" => Cell(0x31,ac1.length,ac2.length)
      case _ => throw new Exception("Invalid character in puzzle.")
      )
  ))

  var endpoints : Array[(Int, Int)] = Array[(Int, Int)]()
  this.state.foreach((r: Array[Cell]) => r.foreach((c: Cell)=>(
    if((c.row == 0 && c.goesUp) || (c.row==this.num_rows-1 && c.goesDown) || (c.col==0 && c.goesLeft) || (c.col==this.num_cols-1 && c.goesRight)) {
      endpoints = endpoints :+ c.position
    }
  )))

  //A copy constructor featuring deep-copy.
  def this(p: Puzzle) = {
    this(p.serialization)
    p.state.foreach((r: Array[Cell])=>(
      r.foreach((c: Cell)=>(
        this.state(c.row)(c.col) = Cell(c.state,c.row,c.col)
      ))
    ))
    this.wasOverwritten = p.wasOverwritten
    this.hasChanged = p.hasChanged
    this.endpoints = p.endpoints
  }

  //A serialization method allowing the puzzle to be printed or copied.

  def serialization: List[String] = {
    s"size ${num_cols}x${num_rows}" :: col.mkString(" ") :: Nil ::: state.foldLeft(List[String]())((ac, x) =>
      var s: String = ""
      x.foreach((k: Cell) =>
        s = s + (
          k.state match
            case 0xd => "║"
            case 0x15 => "╔"
            case 0x19 => "╚"
            case 0x25 => "╗"
            case 0x29 => "╝"
            case 0x31 => "═"
            case _ => "_"
          ) + " "
      )
      ac :+ s
    ).zip(line).foldLeft(List[String]())((acc, s) => acc :+ (s(0) + s(1).toString))
  }

  //A function to return the 'writable' version of a solution
  def asSolution : List[String] = {
    s"size ${num_cols}x${num_rows}" :: col.mkString(" ") :: Nil ::: state.foldLeft(List[String]())((ac,x)=>
      var s: String = ""
      x.foreach((k: Cell) =>
        s = s + (
          k.state match
          case 0xd  => "║"
          case 0x15 => "╔"
          case 0x19 => "╚"
          case 0x25 => "╗"
          case 0x29 => "╝"
          case 0x31 => "═"
          case _    => " "
          ) + " "
      )
      ac :+ s
    ).zip(line).foldLeft(List[String]())((acc, s) => acc:+(s(0)+s(1).toString))
  }

  def print() : Unit = {
    this.serialization.foreach((s: String) => (println(s)))
  }

  //This performs a shallow-copy of the cells in a puzzle.
  @targetName("assign")
  def := (puzzle: Puzzle): Puzzle = {
    this.state = puzzle.state
    this.line = puzzle.line
    this.col = puzzle.col
    this.hasChanged = puzzle.hasChanged
    this.wasOverwritten = puzzle.wasOverwritten
    this.num_rows = puzzle.num_rows
    this.num_cols = puzzle.num_cols
    this
  }

  //Obtaining the cell at a certain position in a puzzle
  def apply(i: Int, j: Int) : Cell = {
    if(i>=0 && i<num_rows && j>=0 && j<num_cols)
      state(i)(j)
    else
      throw new IndexOutOfBoundsException("No such cell exists.")
  }

  def row(i: Int) : Array[Cell] = {
    this.state(i)
  }

  def column(j: Int) : Array[Cell] = {
    this.state.foldLeft(Array[Cell]())((acc,r)=>(acc:+r(j)))
  }

  def commit() : Puzzle = {
    hasChanged = false
    this
  }

  def touch() : Puzzle = {
    hasChanged = true
    this
  }

  def complete : Boolean = {
    this.state.forall((r: Array[Cell])=>(r.forall((c: Cell)=>(c.isTrackKnown || !c.isTrackPossible))))
  }
}
