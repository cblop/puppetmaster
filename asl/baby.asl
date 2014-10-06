
{ include("movement.asl") }  
{ include("emotions.asl") }  
{ include("dialogue.asl") }  

name(baby).
direction(left).
skit(free).

health(5).

punchPos(stageLeft).

+skit(babysit)
  <- say(ga);
     !appearAt(stageRight);
     !avoidPunch.

+!avoidPunch : not alive(no)
  <- !run;
     .wait(1000);
     !avoidPunch.

+!avoidPunch : alive(no)
  <- true.

+otherAction(hit)
  <- !takeDamage.

+!takeDamage : health(X) & X <= 0
  <- !die.

+!takeDamage : health(X) & X > 0
  <- -+health(X - 1).

+!die
  <- -+alive(no);
     !moveTo(offstageRight);
     nextSkit(killjudy).


