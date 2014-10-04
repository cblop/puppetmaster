// Police agent
{ include("movement.asl") }  
{ include("emotions.asl") }  
{ include("dialogue.asl") }  

/* Initial beliefs and rules */

name(police).
direction(left).
skit(free).

speed(medium).

waitTime(slow, 3000).
waitTime(medium, 2000).
waitTime(fast, 1000).

health(5).
energy(5).

valence(1).
arousal(1).
dominance(1).

alive(yes).


/*
feeling(0, -1, annoyed, slow).
feeling(0, 0, alert, slow).
feeling(0, 1, vigilant, medium).
feeling(-1, -1, sulky, medium).
feeling(-1, 0, angry, fast).
feeling(-1, 1, furious, fast).
feeling(1, -1, vicious, fast).
feeling(1, 0, malicious, fast).
feeling(1, 1, excited, medium).
*/

speech(greeting, greeting).
speech(search, search).
speech(X, happy) :- X == alert | X == vigilant | X == excited.
speech(X, annoyed) :- X == sulky | X == annoyed.
speech(X, angry) :- X == angry | X == furious | X == vicious | X == malicious.

pos(offstageRight).
otherPos(offstageLeft).

/* Initial goals */
// Taunt Punch. Must first greet him and ask questions.
//!makeConfess(punch).

/* Plans */

/*

+!resetScene : true
	<- -+valence(1);
     .print("Police reset");
	   -+arousal(1);
	   -+dominance(-1);
	   -+health(5);
	   -+skit(free);
	   -+direction(left);
	   anim(rest);
	   !changeMood.

+currentScene(police) : _
	<- !resetScene;
        !moveTo(stageCentre);
        .wait(2000);
        -+currentSkit(search).

// could generalise this to X
// check the emotion
+currentSkit(search)
	<- !arrestPunch.
		
-!g[.print("Fail plan triggered")].
*/

/*
+currentSkit(search) : audienceYes
	<-  anim(front);
		!speak(search);
		.wait(2000);
		-audienceYes;
		!lookForPunch.
*/

/*
		
+!noiseDetected
	<- pass.

		
+input(_)
	<- -+audienceYes;
		.print("AUDIENCE SAYS YES").

+!arrestPunch : emotion(angry) | emotion(furious)
  <- ?alive(yes);
     !subduePunch.

+!arrestPunch : health(0)
  <- ?alive(yes);
     !die.

+!arrestPunch
  <- ?alive(yes);
     !lookForPunch;
     !talkToPunch;
     !arrestPunch.

-!arrestPunch
  <- .print("Police should be dead now").

+!talkToPunch
  <- ?emotion(X);
     !speak(X);
     .wait(2000);
     ?otherPos(Y);
     //!moveNextTo(Y);
     !moveTowardsOther.

+!subduePunch : health(0)
 <- !die.

+!subduePunch : otherPos(stageCentre)
  <- .wait(1000);
     !moveTo(stageRight);
     anim(hit).

+!subduePunch
  <- .wait(1000);
     !moveTowardsOther;
     anim(hit).


+!lookForPunch : emotion(angry) | emotion(furious)
 <- !subduePunch.
		
+!lookForPunch : canSeeOther
	<- ?otherPos(X);
     .print("Police can see Punch: ", X).
		
+!lookForPunch : not canSeeOther
	<-  anim(front);
		!speak(search);
		.wait("+audienceYes", 5000);
		anim(rest);
		!speak(search);
		.wait(2000);
		?opposite(X);
		!moveTo(X);
		.wait(2000);
		!changeDirection;
		.wait(3000);
		.random(R);
		.random(S);
		!decreaseValence(R);
		!increaseArousal(S);
		!lookForPunch.

-!lookForPunch : not emotion(sulky) & not canSeeOther
	<- .random(R);
		.random(S);
		!decreaseValence(0.1);
		!decreaseArousal(0.1);
		anim(rest);
		.wait(2000);
		!changeDirection;
		.wait(2000);
		!lookForPunch.
		
-!lookForPunch : emotion(sulky)
	<- !moveTo(offstageRight).


-currentScene(_) : _
	<- !moveTo(offstageRight);
		.wait(2000);
		!resetScene.

+otherPos(X) : alive(yes)
	<- !evade.

+otherPos(X)
  <- pass.
	
+otherAction(X) : X == hit
	<- .print("Police is getting hit");
		!takeDamage.


+!evade : pos(X) & otherPos(Y) & neighbour(X, Y)
	<- !moveForward; // randomly
	   .random(R);
	   !decreaseValence(R);
	   !increaseArousal(R). // randomly

+!evade : pos(X) & otherPos(Y) & at(X, Y)
	<- !moveForward; // randomly
	   .random(R);
	   !decreaseValence(R). // randomly
	   
+!evade : pos(X) & otherPos(Y) & not neighbour(X, Y)
	<- pass.

+!evade : pos(X) & otherPos(Y) & not at(X, Y)
	<- pass.
	

+!question(punch) : health(X) & X <= 0
	<- !die.
	
	
+!die
	<- .print("Police is dead.");
	-+alive(no);
	anim(dead);
	.wait(2000);
  //-+skit(dead);
  //-+scene(dead);
  !moveTo(offstageRight).
	//-currentScene(_).
	//.send(narrative, achieve, endScene).


+!speak(X) : speaking
	<- .wait(300);
		//.print("Punch is speaking");
		!speak(X).

+!speak(X) : not speaking
	<- ?speech(X, Y);
		say(Y).
	
+!question(punch) : health(X) & X > 0
	<- !evade;
	?emotion(E);
	!speak(E);
	?speed(S);
	anim(S);
	?waitTime(S, T);
	.wait(T);
	!question(punch).
	
+!greet(punch)
	<- .print("Hi, Punch");
	.wait(3000);
	!speak(greeting).
	

+!takeDamage : health(X) & X <= 0
	<- !die.

+!takeDamage : health(X) & X > 0
	<- ?health(X);
	.print("Police's health is ", X, ".");
	-+health(X - 1).
	
*/

	
