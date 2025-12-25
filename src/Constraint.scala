abstract class Constraint {

  //This lays out the structure of a constraint for our solving strategy. The constraints are rules adapted for establishing
  //an equivalence between each current puzzle and an invalid puzzle.
  //Since this system is not complete either, while a 'false' result implies an unsolvable state, the contrary does not
  //hold (there are bound to be invalid states that pass validation).
  //This solving strategy relies on a 'strong' validation step; it is worth pruning any branch of the search tree rather
  //sooner, than later. Since the validation rules are applied in polynomial time, backtracking still represents the most
  //time-consuming step and the one that should be kept at its lightest.

  def apply(puzzle: Puzzle) : Boolean
}
