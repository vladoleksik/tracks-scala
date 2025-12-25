object NoTracksRemainingRule extends InferenceRule {

  //This rule marks all the cells on a row/column impossible once all the tracks have been found

  def apply(puzzle: Puzzle) : Puzzle = {
    val newPuzzle = Puzzle(puzzle)

    //Go through each row and, if it has enough tracks, mark all the others as impossible
    newPuzzle.line.zip(Array.range(0,puzzle.num_rows)).foreach((total: Int, index: Int) => {
      if(newPuzzle.row(index).count((c: Cell) => c.isTrackCertain) == total) {
        newPuzzle.row(index).foreach((c: Cell) => (
          if (!c.isTrackCertain) {
            if (c.isTrackPossible) {
              newPuzzle.touch()
            }
            newPuzzle.state(c.row)(c.col).state = 3
          }
          ))
      }
    })

    //Perform the same operation for columns
    newPuzzle.col.zip(Array.range(0,puzzle.num_cols)).foreach((total: Int, index: Int) => {
      if(newPuzzle.column(index).count((c: Cell) => c.isTrackCertain) == total) {
        newPuzzle.column(index).foreach((c: Cell) => (
          if(!c.isTrackCertain) {
            if(c.isTrackPossible) {
              newPuzzle.touch()
            }
            newPuzzle.state(c.row)(c.col).state = 3
          }
        ))
      }
    })

    newPuzzle
  }
}
