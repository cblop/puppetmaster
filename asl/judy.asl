// Judy agent
{ include("movement.asl") }  
{ include("emotions.asl") }  
{ include("dialogue.asl") }  

/* Initial beliefs and rules */

name(judy).
direction(left).
skit(free).

health(5).

valence(1).
arousal(1).
dominance(-1).

!changeMood.

+currentScene(judy)
  <- !sayHello.

+!sayHello
  <- !moveTo(stageCentre);
     .wait(2000);
     say(hello);
     .wait(2000);
     say(seepunch);
     .wait("+audienceYes", 5000);
     say(seepunchResponse);
     .wait(2000);
     !callPunch.

-!sayHello
  <- say(seepunchNoResponse).

+!callPunch
  <- move(turn);
     say(callpunch);
     .wait(2000);
     -+skit(kiss).

  


/* Plans */


