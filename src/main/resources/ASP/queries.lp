% workflow is connected (only one head)
workflowStart(Process) :- workflow(Process, _).
workflowEnd(Process) :- workflow(_, Process).
workflowHead(Process) :- workflow(Process, _), not workflowEnd(Process).
workflowTail(Process) :- not workflowStart(Process), workflow(_, Process).
:- not 1 <= #count {Process : workflowHead(Process)} <= 1.
:- not 1 <= #count {Process : workflowTail(Process)} <= 1.


% duplicate validations. Construct bypass* by adding "bypass" to edgeLabels
% A.bypass*.B
starEdgeLabels("bypass").

%:- adgEdge(ContainerS, LabelS, "update", ContainerIS, LabeIS), adgEdge(ContainerIS, LabeIS, c("bypass", "*"), ContainerIE, LabelIE), adgEdge(ContainerIE, LabelIE, "update", ContainerE, LabelE).
duplicate(ContainerS, LabelS, ContainerE, LabelE) :- adgEdge(ContainerS, LabelS, "update", ContainerIS, LabeIS), adgEdge(ContainerIS, LabeIS, c("bypass", "*"), ContainerIE, LabelIE), adgEdge(ContainerIE, LabelIE, "update", ContainerE, LabelE).
#show duplicate/4.

% invalid validation
reverseEdgeLabels("read").
starEdgeLabels("bypass").

:- adgEdge(ContainerS, DataLabelS, c("read", "-1"), ContainerIS, DataLabelE), adgEdge(ContainerIS, DataLabelE, c("bypass", "*"), ContainerIE, DataLabelE), adgEdge(ContainerIE, DataLabelE, "update", ContainerE, DataLabelE).


% unused inputs
starEdgeLabels("bypass").
usedInput(DataLabel) :- adgEdge(ContainerS, DataLabel, c("bypass", "*"), ContainerE, DataLabel), adgEdge(ContainerE, DataLabel, "update", _, _), node(ContainerS, DataLabel, "original").
usedInput(DataLabel) :- adgEdge(ContainerS, DataLabel, c("bypass", "*"), ContainerE, DataLabel), adgEdge(ContainerE, DataLabel, "read", _, _), node(ContainerS, DataLabel, "original").

:- inputData(DataLabel), not usedInput(DataLabel).
