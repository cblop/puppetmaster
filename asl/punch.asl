// Punch agent

{ include("movement.asl") }  
{ include("emotions.asl") }  
{ include("dialogue.asl") }  

/* Initial beliefs and rules */

// remember:
// sulky == annoyed, angry == furious, alert == vigilant == excited, vicious == malicious

name(punch).
direction(right).
//skit(free).

//skit(chase) :- feeling(furious | angry) & other(judy).


//energy(5).

pos(offstageLeft).
otherPos(offstageRight).

valence(0).
arousal(0).
dominance(1).

!changeMood.

// Goals for each skit happen here

+skit(kiss)
  <- ?otherPos(X);
     !moveNextTo(X);
     !kissJudy.

+skit(babysit)
  <- .wait(2000);
     -+other(baby);
     say(comeBaby);
     .wait(2000);
     !getBaby.

+skit(killjudy)
  <- .wait(2000);
     -+other(judy);
     true.

+!getBaby : emotion(furious)
  <- !hitOther.

+!getBaby : not emotion(furious)
  <- !chaseOther;
     .wait(2000);
     !getMad;
     !getBaby.


+!hitOther : otherPos(offstageRight)
  <- true.

+!hitOther : pos(X) & otherPos(Y) & isNextTo(X, Y)
  <- anim(hit);
     .wait(2000);
     !hitOther.

+!hitOther : pos(X) & otherPos(Y) & not isNextTo(X, Y)
  <- .wait(2000);
     anim(hit);
     !moveTo(Y);
     !hitOther.

+!kissJudy
  <- say(askKiss);
     .wait("+audienceYes", 5000);
     // should also ask Judy!
     say(willKiss);
     !chaseOther;
     say(kiss);
     nextSkit(baby).

-!kissJudy
  <- .print("Kissing failed").

// have different outcomes depending on mood and other character. Kiss or kill.

+!chaseOther : pos(X) & otherPos(Z) & X == Z
  <- true.


+!chaseOther : pos(X) & otherPos(Y) & not X == Z
  <- .print("Trying to chase...");
     .wait(2000);
     !moveTo(Y);
     !chaseOther.


-!chaseOther
  <- .print("chasing failed");
     ?pos(P);
     ?otherPos(X);
     .print("Scene, pos, other: ", P, ", ", X);
     .wait(1000);
     !chaseOther.


/* Initial goals */
// none, they are scene dependant

/* Plans */

// change: different moods

/*
+otherPos(X) : true
	<- ?leftOf(Y, Z).
*/

/*

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
	<- !moveTo(offstageRight);
     .wait(2000);
     .random(R);
     .random(S);
     !increaseArousal(R);
     !decreaseValence(S);
     !attackCheck.

+!hide : otherPos(stageRight)
	<- !moveTo(offstageLeft);
     .wait(2000);
     .random(R);
     .random(S);
     !increaseArousal(R);
     !decreaseValence(S);
     !attackCheck.

+!hide : otherPos(stageCentre)
	<- !moveTo(stageCentre);
     .wait(2000);
     .random(R);
     .random(S);
     !increaseArousal(R);
     !increaseValence(S);
     !attackCheck.

+!attackCheck : emotion(furious) | emotion(angry)
  <- .print("Punch decides to attack");
     -+skit(attack).

+!attackCheck
  <- .print("Punch does not want to attack").

+skit(attack)
  <- .print("Attack skit");
      anim(hit);
     !chase.

+otherPos(_) : skit(attack)
  <- .print("Punch: attack");
      anim(hit);
     !chase.

+skit(attack)
  <- .print("PUNCH ATTACK!").

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
	   
+!chase : pos(X) & otherPos(Y) & not (X == Y) & not (X == offstageRight)
	<- .random(R);
		!increaseArousal(R);
		!moveTowardsOther.

+!chase : pos(X) & otherPos(Y) & immLeft(X, Y) & direction(right)
	<- .random(R);
		!increaseArousal(R);
		!hitOther.

+!chase : pos(X) & otherPos(Y) & immRight(X, Y) & direction(left)
	<- .random(R);
		!increaseArousal(R);
		!hitOther.

+!chase : otherPos(offstageRight)
  <- !moveTo(offstageLeft).

+!chase
  <- !moveTowardsOther.

// check the other isn't dead
+!hitOther : true
	<- ?emotion(X);
    say(X); 
    anim(hit).

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

+otherAction(X) : X == hit
  <- !evade.

+!evade
  <- ?otherPos(X);
    ?oppositeOf(X, Y);
    !moveTo(Y).

+otherAction(X) : X == dead
  <- !moveTo(offstageLeft);
     .wait(2000);
     nextScene(next).

*/
	
