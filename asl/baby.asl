
{ include("movement.asl") }  
{ include("emotions.asl") }  
{ include("dialogue.asl") }  

name(baby).
direction(left).
skit(free).

punchPos(stageLeft).

+skit(babysit)
  <- !appearAt(stageRight).

