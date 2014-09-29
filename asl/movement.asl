locations(offstageLeft, stageLeft, stageCentre, stageRight, offstageRight).

immLeft(X, Y) :- locations(_, X, Y, _, _) | locations(_, _, X, Y, _).
immRight(X, Y) :- locations(_, Y, X, _, _) | locations(_, _, Y, X, _).

neighbour(X, Y) :- immLeft(X, Y) | immRight(X, Y).

//leftOf(X, Y) :- immLeft (X, Y) | locations(_, X, _, Y, _) | locations(_, _, _, _, Y) | locations(X, _, _, _, _).
leftOf(X, Y) :- immLeft (X, Y) | locations(_, X, _, Y, _).
//rightOf(X, Y) :- immRight (X, Y) | locations(_, Y, _, X, _) | locations(_, _, _, _, X) | locations(Y, _, _, _ ,_).
rightOf(X, Y) :- immRight (X, Y) | locations(_, Y, _, X, _).

opposite(X) :- pos(stageLeft) & locations(_, _, _, X, _).
opposite(X) :- pos(stageRight) & locations(_, X, _, _, _).
opposite(X) :- pos(stageCentre) & direction(right) & locations(_, _, _, X, _).
opposite(X) :- pos(stageCentre) & direction(left) & locations(_, X, _, _, _).

oppositeOf(X, Y) :- locations(_, Y, _, X, _) | locations(_, X, _, Y, _) | locations(X, _, _, _, Y) | locations(Y, _, _, _, X) | locations(_, _, X, Y, _).
				
at(X, Y) :- X == Y.


rightOfOther :- pos(X) & otherPos(Y) & rightOf(X, Y).
leftOfOther :- pos(X) & otherPos(Y) & leftOf(X, Y).

otherBehind :- (rightOfOther & direction(right)) | (leftOfOther & direction(left)).

canSeeOther :- not otherBehind & not otherPos(offstageLeft) & not otherPos(offstageRight). 



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
	   


+otherMoved(X) : _
	<- -+otherPos(X).

+!moveTo(X) : alive(no)
	<- -+pos(X);
	   move(X).

+!moveTo(X)
	<- anim(rest);
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

+!moveNextTo(Y) : direction(right)
  <- ?immLeft(X, Y);
     !moveTo(X).

+!moveNextTo(Y) : direction(left)
  <- ?immRight(X, Y);
     !moveTo(X).

+!moveNextTo(X)
  <-!moveTo(X).

