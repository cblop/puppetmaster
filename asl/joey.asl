// Joey agent
{ include("movement.asl") }  
{ include("emotions.asl") }  
{ include("dialogue.asl") }  

/* Initial beliefs and rules */

name(joey).
direction(left).
skit(free).

valence(1).
arousal(1).
dominance(1).

!changeMood.

+currentScene(intro)
  <- !introduceShow.

+!introduceShow
  <- .wait(5000);
     !moveTo(offstageRight).

/*
+!introduceShow
  <- !moveTo(stageCentre);
     .wait(2000);
     say(start);
     .wait("+audienceYes", 5000);
     say(response);
     .wait(2000);
     !moveTo(offstageRight);
     .wait(2000);
     nextSceneInQueue(next).

-!introduceShow : not emotion(sulky)
  <- say(noresponse);
     .random(R);
     .random(S);
     !decreaseArousal(R);
     !decreaseValence(S);
     !introduceShow.

-!introduceShow
  <- say(giveup);
     .wait(2000);
     !moveTo(offstageRight).

*/


