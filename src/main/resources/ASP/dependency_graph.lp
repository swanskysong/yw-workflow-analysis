% Construct container graph 
workflowEnd(Actor) :- workflow(_, Actor).
workflowStart(Actor) :- workflow(Actor, _).

dcgEdge("sourceContainer", ProcessA, ProcessLabel, c(ProcessA,ProcessB)) :- workflow(ProcessA, ProcessB), not workflowEnd(ProcessA), process(ProcessA, Type), processLabel(Type, ProcessLabel).

dcgEdge(c(ProcessA,ProcessB), ProcessB, ProcessLabel, c(ProcessB,ProcessC)) :- workflow(ProcessA, ProcessB), workflow(ProcessB, ProcessC), process(ProcessB, Type), processLabel(Type, ProcessLabel).

dcgEdge(c(ProcessA,ProcessB), ProcessB, ProcessLabel, "sinkContainer") :- workflow(ProcessA, ProcessB), not workflowStart(ProcessB), process(ProcessB, Type), processLabel(Type, ProcessLabel).

% source
% construct, the source node, edges from source node and the nodes those edges points to
node("sourceContainer", "source", "null").
adgEdge("sourceContainer", "source", "input", Container, Label) :- dcgEdge("sourceContainer", _, _, Container), inputData(Label).
node(Container, Label, "original") :- dcgEdge("sourceContainer", _, _, Container), inputData(Label).

% construct edges
adgEdge(ContainerS, LabelS, DepType, ContainerE, LabelE) :- dcgEdge(ContainerS, Actor, _, ContainerE), processConfig(Actor, LabelS, LabelE, DepType).

%adgEdge(ContainerS, LabelS, "read", ContainerE, LabelE) :- dcgEdge(ContainerS, Actor, _, ContainerE), actorReference(LabelS, Actor, LabelE).

%actorRead(Label, Actor) :- actorValidation(Label, Actor, _).
actorRead(Label, Actor) :- processConfig(Actor, Label, _, "read").
% don't construct the source and sink node edges
adgEdge(ContainerS, LabelS, "bypass", ContainerE, LabelS) :- node(ContainerS, LabelS, _), dcgEdge(ContainerS, Actor, _, ContainerE), not actorRead(LabelS, Actor), ContainerS != "sourceContainer", ContainerE != "sinkContainer".

% construct the node from edges
node(Container, Label, "validated") :- adgEdge(_, _, "update", Container, Label).
node(ContainerB, LabelB, Status) :- adgEdge(ContainerA, LabelA, "bypass", ContainerB, LabelB), node(ContainerA, LabelA, Status).

% sink
node("sinkContainer", "sink", "null").
adgEdge(Container, Label, "output", "sinkContainer", "sink") :- dcgEdge(Container, _, _, "sinkContainer"), node(Container, Label, _).

