// Punch agent

{ include("movement.asl") }  
{ include("emotions.asl") }  
{ include("dialogue.asl") }  

/* Initial beliefs and rules */

// remember:
// sulky == annoyed, angry == furious, alert == vigilant == excited, vicious == malicious

name(punch).
direction(right).
skit(free).


/*
locations(offstageLeft, stageLeft, stageCentre, stageRight, offstageRight).

neighbours(X, Y) :- locations(X, Y, _, _, _) | locations(_, X, Y, _, _)
	| locations(_, _, X, Y, _) | locations(_, _, _, X, Y).
	
immLeft(X, Y) :- neighbours(X, Y).
immRight(X, Y) :- neighbours(Y, X).

leftOf(X, Y) :- immLeft(X, Y) |
				locations(X, _, _, _, _, _) | locations(_, X, _, Y, _) |
				locations(_, _, _, _, _, Y).
*/

skit(chase) :- feeling(furious | angry) & other(judy).

feeling(0, -1, annoyed, slow).
feeling(0, 0, alert, slow).
feeling(0, 1, vigilant, medium).
feeling(-1, -1, sulky, medium).
feeling(-1, 0, angry, fast).
feeling(-1, 1, furious, fast).
feeling(1, -1, vicious, fast).
feeling(1, 0, malicious, fast).
feeling(1, 1, excited, medium).

emotion(alert).

speed(medium).

waitTime(slow, 3000).
waitTime(medium, 2000).
waitTime(fast, 1000).

energy(5).

interruption.

pos(offStageLeft).
otherPos(offStageRight).


valence(0).
arousal(0).
dominance(1).


/* Initial goals */
// none, they are scene dependant

/* Plans */

// change: different moods

/*
+otherPos(X) : true
	<- ?leftOf(Y, Z).
*/

+scene(X) : _
	<- -+currentScene(X).
	
+!resetScene : true
	<- -+valence(0);
     .print("Punch reset");
	   -+arousal(0);
	   -+dominance(1);
	   -+skit(free);
     -+direction(right);
	   anim(rest);
	   !changeMood.

+currentScene(judy)
	<- !resetScene;
	   !moveTo(stageLeft);
	   !say_hi;
	   !dominate.

+currentScene(police)
	<- !resetScene;
		-+skit(hide).

+skit(hide)
	<- !hide.
		
+otherPos(_) : skit(hide)
	<- !hide.

+!hide : otherPos(stageLeft)
	<- !moveTo(offstageRight).

+!hide : otherPos(stageRight)
	<- !moveTo(offstageLeft).

+!hide : otherPos(stageCentre)
	<- !moveTo(stageCentre).

+!hide
  <- .print("Not hiding").

	   
-currentScene(_) : _
	<- !moveTo(offstageLeft);
		.wait(2000);
		nextScene(next).

+!boast : true
	<- .print("Punch is boasting");
	   say(happy).

+!dominate : otherPos(offstageRight)
	<- ?speed(X);
		anim(X);
		anim(rest);
		?waitTime(X, Y);
		.wait(Y);
	    !boast;
	   .wait(Y);
	   .random(R);
	   !increaseValence(R);
	   .print("Punch has achieved his goal");
	   .wait(2000);
	   -currentScene(_).

+!dominate : not otherPos(offstageRight)
	<- ?speed(X);
		anim(X);
		?waitTime(X, Y);
	   .wait(Y);
	   !silenceOther;
	   .random(R);
	   !decreaseValence(R);
	   !dominate.
	
+!silenceOther : emotion(sulky) | emotion(annoyed)
	<- !changeDirection; // want to do this with a probability
	   //say(sulky).
	   !speak(annoyed).

+!silenceOther : emotion(angry) | emotion(furious)
	<- !chase; // chase
	   !speak(angry).

+!silenceOther : emotion(alert) | emotion(vigilant) | emotion(excited)
	<- .random(R);
	   !pace(R); // probability
	   //say(excited).
	   !speak(happy).

+!silenceOther : emotion(vicious) | emotion(malicious)
	<- .random(R);
	   !pace(R);
	   //say(vicious).
	   !speak(angry).
	   
	   
+!pace(R) : R >= 0.5
	<- !changeDirection.

+!pace(R) : R < 0.5
	<- !moveForward; // randomly
	   .random(S);
	   !increaseArousal(S). // randomly
	   
+!chase : pos(X) & otherPos(Y) & not (X == Y)
	<- .random(R);
		anim(rest);
		!increaseArousal(R);
		!moveTowardsOther.

+!chase : pos(X) & otherPos(Y) & X == Y
	<- .random(R);
		!increaseArousal(R);
		!hitOther.

+!chase : pos(X) & otherPos(Y) & neighbour(X, Y)
	<- !hitOther.

// check the other isn't dead
+!hitOther : true
	<- anim(hit).

+!say_hi : true
	<- !speak(greeting);
		?speed(X);
		?waitTime(X, Y);
	   .wait(Y).

+!speak(X) : speaking
	<- .wait(300);
		//.print("Judy is speaking");
		!speak(X).

+!speak(X) : not speaking
	<- say(X).
	
