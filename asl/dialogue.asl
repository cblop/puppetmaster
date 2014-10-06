+input(_)
	<- -+audienceYes;
		.print("AUDIENCE SAYS YES").

+scene(X)
  <- -+currentScene(X);
     -+other(X).

+nextSkit(X)
  <- -+skit(X).

+action(X, Y)
  <- -+otherAction(Y).

