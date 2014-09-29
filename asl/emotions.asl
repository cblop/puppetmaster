myEmotion(annoyed) :- valence(0) & arousal(-1).
myEmotion(alert) :- valence(0) & arousal(0).
myEmotion(vigilant) :- valence(0) & arousal(1).
myEmotion(sulky) :- valence(-1) & arousal(-1).
myEmotion(angry) :- valence(-1) & arousal(0).
myEmotion(furious) :- valence(-1) & arousal(1).
myEmotion(vicious) :- valence(1) & arousal(-1).
myEmotion(vicious) :- valence(1) & arousal(0).
myEmotion(malicious) :- valence(1) & arousal(1).

/*
+!changeMood : valence(X) & arousal(Y)
	<- ?feeling(X, Y, Z, T);
	   -+speed(T);
     emotion(Z);
	   -+emotion(Z).
*/

+!changeMood
  <- ?valence(X);
     ?arousal(Y);
     ?myEmotion(Z);
     emotion(Z);
     -+emotion(Z).

-!changeMood
  <- ?valence(X);
     ?arousal(Y);
    .print("changeMood failed!", X, " ", Y).

+valence(X) : true
	<- !changeMood.

+arousal(X) : true
	<- !changeMood.

+!increaseValence(R) : R <= 0.7 & valence(X) & X < 1
	<- -+valence(X + 1).

+!increaseValence(R) : valence(X) & X >= 1
	<- pass.

+!increaseValence(R) : R > 0.7
	<- pass.

+!decreaseValence(R) : R <= 0.7 & valence(X) & X > -1
	<- -+valence(X - 1).

+!decreaseValence(R) : valence(X) & X <= -1
	<- pass.

+!decreaseValence(R) : R > 0.7
	<- pass.

+!increaseArousal(R) : R <= 0.7 & arousal(X) & X < 1
	<- -+arousal(X + 1).

+!decreaseArousal(R) : R <= 0.7 & arousal(X) & X > -1
	<- -+arousal(X - 1).

+!increaseArousal(R) : arousal(X) & X >= 1
	<- pass.

+!increaseArousal(R) : R > 0.7
	<- pass.

+!decreaseArousal(R) : arousal(X) & X <= -1
	<- pass.

+!decreaseArousal(R) : R > 0.7
	<- pass.
