// Judy agent
{ include("movement.asl") }  
{ include("emotions.asl") }  
{ include("dialogue.asl") }  

/* Initial beliefs and rules */

name(judy).
direction(left).
skit(free).

health(5).

valence(0).
arousal(0).
dominance(-1).

punchPos(offstageLeft).

!changeMood.

+currentScene(judy)
  <- !sayHello.

+!sayHello
  <- !moveTo(stageCentre);
     .wait(2000);
     say(hello);
     .wait(2000);
     !askAudience.


+!askAudience
  <- say(seepunch);
     .wait("+audienceYes", 5000);
     say(seepunchResponse);
     .wait(2000);
     !callPunch.

-!askAudience : emotion(sulky)
  <- say(giveUp);
     .wait(2000);
     !moveTo(offstageRight).

-!askAudience
  <- say(seepunchNoResponse);
    !getSad;
    !askAudience.

+!callPunch
  <- !changeDirection;
     say(callpunch);
     .wait(2000);
     nextSkit(kiss).

+skit(baby)
  <- .wait(2000);
     !getBaby.

+!getBaby : emotion(tired) | emotion(doubtful) | emotion(scared) | emotion(unhappy) | emotion(afraid)
  <- say(giveUp);
     !moveTo(offstageRight).

+!getBaby
  <- say(getBaby);
     .wait("+audienceYes", 5000);
     !moveTo(offstageRight);
     .wait(2000);
     anim(babyside);
     !moveTo(stageRight);
     .wait(2000);
     say(lookAfterBaby);
     .wait(2000);
     !moveTo(offStageRight);
     -+nextSkit(babysit).
     

-!getBaby
  <- !getSad;
     !getBaby.
     

+skit(kiss)
  <- !avoidKisses.

+!avoidKisses : emotion(delighted)
  <- true.

+!avoidKisses : not emotion(delighted)
  <- !getHappy;
     !run;
     .wait(1000);
     !avoidKisses.


+!run : punchPos(X) & pos(Y) & not (immLeft(X, Y) | immRight(X, Y) | X == Y)
  <- .wait(2000).

+!run : punchPos(X) & pos(Y) & (immLeft(X, Y) | immRight(X, Y) | X == Y)
  <- ?oppositeOf(Y, Z);
     !moveTo(Z).

+!run
  <- .print("Running failed").
    

  


/* Plans */


