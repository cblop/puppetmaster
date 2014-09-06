+!changeMood : valence(X) & arousal(Y)
	<- ?feeling(X, Y, Z, T);
	   -+speed(T);
     emotion(Z);
	   -+emotion(Z).
	   

+valence(X) : true
	<- !changeMood.

+arousal(X) : true
	<- !changeMood.

+!increaseValence(R) : R <= 0.2 & valence(X) & X < 1
	<- -+valence(X + 1).

+!increaseValence(R) : valence(X) & X >= 1
	<- pass.

+!increaseValence(R) : R > 0.2
	<- pass.

+!decreaseValence(R) : R <= 0.2 & valence(X) & X > -1
	<- -+valence(X - 1).

+!decreaseValence(R) : valence(X) & X <= -1
	<- pass.

+!decreaseValence(R) : R > 0.2
	<- pass.

+!increaseArousal(R) : R <= 0.2 & arousal(X) & X < 1
	<- -+arousal(X + 1).

+!decreaseArousal(R) : R <= 0.2 & arousal(X) & X > -1
	<- -+arousal(X - 1).

+!increaseArousal(R) : arousal(X) & X >= 1
	<- pass.

+!increaseArousal(R) : R > 0.2
	<- pass.

+!decreaseArousal(R) : arousal(X) & X <= -1
	<- pass.

+!decreaseArousal(R) : R > 0.2
	<- pass.
