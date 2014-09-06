locations(stageLeft, stageCentre, stageRight).

immLeft(X, Y) :- locations(X, Y, _) | locations(_, X, Y).
immRight(X, Y) :- locations(Y, X, _) | locations(_, Y, X).

neighbour(X, Y) :- immLeft(X, Y) | immRight(X, Y).

leftOf(X, Y) :- immLeft (X, Y) | locations(X, _, Y).
rightOf(X, Y) :- immRight (X, Y) | locations(Y, _, X).

opposite(X) :- pos(stageLeft) & locations(_, _, X).
opposite(X) :- pos(stageRight) & locations(X, _, _).
opposite(X) :- pos(stageCentre) & direction(right) & locations(_, _, X).
opposite(X) :- pos(stageCentre) & direction(left) & locations(X, _, _).
				
at(X, Y) :- X == Y.


rightOfOther :- pos(X) & otherPos(Y) & rightOf(X, Y).
leftOfOther :- pos(X) & otherPos(Y) & leftOf(X, Y).

otherBehind :- rightOfOther & direction(D) & D == right.
otherBehind :- leftOfOther & direction(D) & D == left.

canSeeOther :- pos(X) & otherPos(Y) & rightOf(X, Y) & direction(left).
canSeeOther :- pos(X) & otherPos(Y) & leftOf(X, Y) & direction(right).


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

+!moveTo(X) : pos(Y)
	<- anim(rest);
		-+pos(X);
	   move(X).

+!moveForward : direction(left) & pos(stageLeft)
	<- !changeDirection.

+!moveForward : direction(right) & pos(stageRight)
	<- !changeDirection.

+!moveForward : direction(left) & pos(X) & not (X == stageLeft)
	<-  ?immLeft(Y, X);
	   !moveTo(Y).

+!moveForward : direction(right) & pos(X) & not (X == stageRight)
	<- ?immRight(Y, X);
	   !moveTo(Y).
	   
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