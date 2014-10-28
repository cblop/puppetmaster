locations(offstageLeft, stageLeft, stageCentre, stageRight, offstageRight).

immLeft(X, Y) :- locations(X, Y, _, _, _) | locations(_, X, Y, _, _) | locations(_, _, X, Y, _) | locations(_, _, _, X, Y).
immRight(X, Y) :- locations(_, Y, X, _, _) | locations(_, _, Y, X, _) | locations(Y, X, _, _, _) | locations(_, _, _, Y, X).
immLeft(X, Y) :- locations(X, _, _, _, _) & locations(Y, _, _, _, _).
immRight(X, Y) :- locations(_, _, _, _, Y) & locations(Y, _, _, _, Y).


neighbour(X, Y) :- immLeft(X, Y) | immRight(X, Y).

//leftOf(X, Y) :- immLeft (X, Y) | locations(_, X, _, Y, _) | locations(_, _, _, _, Y) | locations(X, _, _, _, _).
leftOf(X, Y) :- immLeft (X, Y) | locations(_, X, _, Y, _).
//rightOf(X, Y) :- immRight (X, Y) | locations(_, Y, _, X, _) | locations(_, _, _, _, X) | locations(Y, _, _, _ ,_).
rightOf(X, Y) :- immRight (X, Y) | locations(_, Y, _, X, _).

opposite(X) :- pos(stageLeft) & locations(_, _, _, X, _).
opposite(X) :- pos(stageRight) & locations(_, X, _, _, _).
opposite(X) :- pos(stageCentre) & direction(right) & locations(_, _, _, X, _).
opposite(X) :- pos(stageCentre) & direction(left) & locations(_, X, _, _, _).

isNextTo(X, Y) :- immLeft(X, Y) & direction(right).
isNextTo(X, Y) :- immRight(X, Y) & direction(left).

oppositeOf(X, Y) :- locations(_, Y, _, X, _) | locations(_, X, _, Y, _) | locations(X, _, _, _, Y) | locations(Y, _, _, _, X) | locations(_, _, X, Y, _).
				
at(X, Y) :- X == Y.

rightOfOther :- pos(X) & otherPos(Y) & rightOf(X, Y).
leftOfOther :- pos(X) & otherPos(Y) & leftOf(X, Y).

otherBehind :- (rightOfOther & direction(right)) | (leftOfOther & direction(left)).

canSeeOther :- scene(X) & not otherBehind & not otherPos(offstageLeft) & not otherPos(offstageRight). 

+obl(move(X, Y), D, V) : name(N) & X == N
  <- .print("Agent ", X, " moveObl: ", Y);
  	 !moveTo(Y).

+!run : punchPos(X) & pos(Y) & not (immLeft(X, Y) | immRight(X, Y) | X == Y)
  <- .wait(2000).

+!run : punchPos(X) & pos(Y) & (immLeft(X, Y) | immRight(X, Y) | X == Y)
  <- ?oppositeOf(Y, Z);
     !moveTo(Z).

+!run
  <- .print("Running failed").



/*
+otherSpeaking(S) : true
	<- +speaking;
	   ?name(X).
	   //.print(X, ": Speaking percept added").

-otherSpeaking(_) : true
	<- -speaking;
	   ?name(X).
	   //.print(X, ": Speaking percept removed").
	   * 
	   */
	   

+moved(X, Y) : X == punch
	<- -+punchPos(Y).

+moved(X, Y) 
	<- -+otherPos(Y).

+!appearAt(X)
  <- -+pos(X);
     appear(X).

+!moveTo(X) : alive(no)
	<- -+pos(X);
	   move(X).

+!moveTo(X) : pos(Y) & Y == X
  <- !changeDirection.

+!moveTo(X)
	<- //anim(rest);
		-+pos(X);
	   move(X).

+!moveForward : direction(left) & (pos(offstageLeft) | pos(stageLeft))
	<- !changeDirection.

+!moveForward : direction(right) & (pos(offstageRight) | pos(stageRight))
	<- !changeDirection.

+!moveForward : direction(left) & not(pos(offstageLeft) | pos(stageLeft))
	<-  ?immLeft(Y, X);
	   !moveTo(Y).

+!moveForward : direction(right) & not(pos(offstageRight) | pos(stageRight))
	<- ?immRight(Y, X);
	   !moveTo(Y).

+!moveForward
  <- true.
	   
+!changeDirection : direction(right)
	<- anim(turn);
	   -+direction(left).

+!changeDirection : direction(left)
	<- anim(turn);
	   -+direction(right).
	
+!moveTowardsOther : otherBehind
	<- !changeDirection;
	   !moveForward.
	
+!moveTowardsOther : not otherBehind
	<- !moveForward.

+!moveNextTo(X) : pos(Z) & rightOf(X, Z)
  <- ?immLeft(Y, X);
     !moveTo(Y).

+!moveNextTo(X) : pos(Z) & leftOf(X, Z)
  <- ?immRight(Y, X);
     !moveTo(Y).

+!moveNextTo(X)
  <- ?immLeft(Y, X);
     !moveTo(Y).

-!moveNextTo(X)
  <- ?immLeft(Y, X);
     ?immRight(Y, Z);
    .print("MoveNextTo failed: ", X, ", ", Y, ", ", Z).

