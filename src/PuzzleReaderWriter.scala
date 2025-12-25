import java.io.FileWriter
import scala.io.*

object PuzzleReaderWriter {
  private var inputFile: String = ""
  private var outputFile: String = ""
  private var content: List[String] = Nil
  private var fw: FileWriter = null
  private var source: scala.io.Source = null

  def initialize(inputfile: String, outputfile: String) : Unit = {
    inputFile = inputfile
    outputFile = outputfile
    source = Source.fromFile(inputFile)
    content = source.getLines().toList
    fw = new FileWriter(outputFile, false)
  }

  def getNumPuzzles() : Int = {
    val countPuzzles = content.head.split(" ").last.toInt
    countPuzzles
  }

  def getPuzzle(index: Int): Puzzle = {
    //val sizeNumbers = content.filter(_.startsWith("size"))(index).split(" ").last.split("x")
    var ct = 0
    val puzzleData = content.foldLeft(List[String]())((accumulator, p)=>
      if(p.startsWith("size")) {
        ct=ct+1
      }
      var s: String = ""
      if(ct==index+1) {
        s = s + p
      }
      accumulator :+ s
    ).filter((p: String)=> p.nonEmpty)
    //new Puzzle(sizeNumbers(0).toInt, sizeNumbers.last.toInt, "")
    new Puzzle(puzzleData)
  }

  def putNumPuzzles(countPuzzles: Int) : Unit = {
    fw.write("puzzles "+countPuzzles.toString+"\n")
  }

  def putSolution(puzzle: Puzzle) : Unit = {
    puzzle.asSolution.foreach((s: String)=>fw.write(s+"\n"))
  }

  def fileClose() : Unit = {
    source.close()
    fw.close()
  }
}
