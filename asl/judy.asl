// Judy agent

/* Initial beliefs and rules */

//pos(stageRight).
//health(5).
//worry(0).
//sceneStart.
//speed(10).
//speed goes down with health. So why not just have health?
~speaking.

/* Initial goals */
// Taunt Punch. Must first greet him and ask questions.
!greet(punch).
!question(punch).
//!makeConfess(punch).

/* Plans */

+sceneStart
	<- ?startPos(X);
	   ?startHealth(Y);
	   move(X);
	   +pos(X);
	   +health(Y);
	   !question(punch);
	   -sceneStart.

+pos(P) : true
	<- .print("Judy is at ", P);
		move(P).


+!question(punch) : health(X) & X <= 0
	<- !die.
	

+sceneEnd : true
	<- move(offstageRight).
	
+!die
	<- .print("Judy is dead.");
	+dead;
	die("judy").
	//.send(narrative, achieve, endScene).

/*
+!makeConfess(X)
	<- !question(X).

*/

/*
+!question(punch)
	<- .print("Judy -> Punch question").
*/

+!speak(X) : speaking
	<- .wait(100);
		!speak(X).

+!speak(X) : ~speaking
	<- say(X).
	
+!question(punch) : health(X) & X > 0
	<- .print("Judy asks Punch a question.");
	.send(punch, achieve, question(judy));
	!speak(happy);
	.wait(3000);
	!question(punch).
	
+!greet(punch)
	<- .print("Hi, Punch");
	.send(punch, tell, greeting(judy));
	.wait(3000);
	!speak(greeting).
	

+!take_damage : health(X) & X <= 0
	<- !die.

+!take_damage
	<- ?health(X);
	.print("Judy's health is ", X, ".");
	.send(punch, tell, ouch(judy));
	-health(X);
	+health(X - 1).
	
	



	
