class Cell(data: Int, i: Int, j: Int) {

  //This structure holds the data characteristic to a cell state and its position on the board of a puzzle.
  //In order to represent a puzzle in a reliable way, we encoded the information regarding the existence of
  //a track as bits in an integer.
  //The "state" variable has the lowest two bits representing the chances of finding a track in the cell:
  // 0b00 - a track is possible
  // 0b01 - a track is certain, and it is known
  // 0b10 - a track is certain but the precise track is not known
  // 0b11 - no track exists in the cell
  //The next four more significant bits map the track layout as follows:
  // 0bLRABXX
  //  L - The cell has a track portion connecting to its neighbour to the left
  //  R - The cell has a track portion connecting to its neighbour to the left
  //  A - The cell has a track portion connecting to the cell above it
  //  B - The cell has a track portion connecting to the cell below it

  var state : Int = data
  val position : (Int, Int) = (i,j)

  //Specific information about a cell is made available through property-like methods

  def isTrackPossible : Boolean = {
    state%4!=3
  }

  def isTrackCertain : Boolean = {
    state%4==1 || state%4==2
  }

  def isTrackKnown : Boolean = {
    state%4==1
  }

  def goesUp : Boolean = {
    (state&(1<<3))!=0
  }

  def goesDown: Boolean = {
    (state & (1 << 2)) != 0
  }

  def goesLeft: Boolean = {
    (state & (1 << 5)) != 0
  }

  def goesRight: Boolean = {
    (state & (1 << 4)) != 0
  }

  //The rows and columns are made available for ease of access when describing rules

  def row : Int = {
    position(0)
  }
  def col : Int = {
    position(1)
  }
  
}