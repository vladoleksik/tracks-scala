object OnlyTracksRemainingRule extends InferenceRule {

  //This rule marks all the cells on a row/column certain once all the other tracks are impossible

  def apply(puzzle: Puzzle) : Puzzle = {
    val newPuzzle = Puzzle(puzzle)

    newPuzzle.line.zip(Array.range(0,puzzle.num_rows)).foreach((total: Int, index: Int) => {
      if(newPuzzle.row(index).count((c: Cell) => c.isTrackPossible) == total) {
        newPuzzle.row(index).foreach((c: Cell) => (
          if(c.isTrackPossible) {
            if(!c.isTrackKnown) {
              if(!c.isTrackCertain) {
                newPuzzle.touch()
              }
              newPuzzle.state(c.row)(c.col).state = 2
            }
          }
          ))
      }
    })

    newPuzzle.col.zip(Array.range(0, puzzle.num_cols)).foreach((total: Int, index: Int) => {
      if (newPuzzle.column(index).count((c: Cell) => c.isTrackPossible) == total) {
        newPuzzle.column(index).foreach((c: Cell) => (
          if(c.isTrackPossible) {
            if(!c.isTrackKnown) {
              if(!c.isTrackCertain) {
                newPuzzle.touch()
              }
              newPuzzle.state(c.row)(c.col).state = 2
            }
          }
          ))
      }
    })

    newPuzzle
  }
}
