% ---- compute optimized workflow with maximal parallelism ----

% computer dependecies between process that must be satisfied
actorDependency(ActorA, ActorB) :- dcgEdge(ContainerS, ActorA, _, _), dcgEdge(_, ActorB, _, ContainerE), adgEdge(ContainerS, DataLabelS, "update", ContainerSI, DataLabelSI), adgEdge(ContainerSI, DataLabelSI, c("bypass", "*"), ContainerEI, DataLabelEI), adgEdge(ContainerEI, DataLabelEI, "read", ContainerE, DataLabelE).

% no partial order between parallel processes
parallelProcess(ProcessA, ProcessB) :- not actorDependency(ProcessA, ProcessB), not actorDependency(ProcessB, ProcessA), process(ProcessA, "actor"), process(ProcessB, "actor"), not sourceProcess(ProcessA), not sourceProcess(ProcessB), not sinkProcess(ProcessA), not sinkProcess(ProcessB), ProcessA != ProcessB.

% construct possible edges as the start point (protential answers)
optimizedWorkflowOrder(ProcessA, ProcessB) | notOptimizedWorkflow(ProcessA, ProcessB) :- process(ProcessA, "actor"), process(ProcessB, "actor"), not parallelProcess(ProcessA, ProcessB), ProcessA != ProcessB.

% eliminate invalid answers:
% 1) all actors are in
optimizedWorkflowProcess(Process) :- optimizedWorkflowOrder(Process, _).
optimizedWorkflowProcess(Process) :- optimizedWorkflowOrder(_, Process).
:- not optimizedWorkflowProcess(Process), process(Process, "actor").

% 2) souce and sink are fixed
:- optimizedWorkflowOrder(Process, _), sinkProcess(Process).
:- optimizedWorkflowOrder(_, Process), sourceProcess(Process).

% 3) only one head and one tail
optimizedWorkflowStart(Process) :- optimizedWorkflowOrder(Process, _).
optimizedWorkflowEnd(Process) :- optimizedWorkflowOrder(_, Process).
optimizedWorkflowHead(Process) :- optimizedWorkflowOrder(Process, _), not optimizedWorkflowEnd(Process).
optimizedWorkflowTail(Process) :- not optimizedWorkflowStart(Process), optimizedWorkflowOrder(_, Process).
:- 2 <= #count {Process : optimizedWorkflowHead(Process)}.
:- 2 <= #count {Process : optimizedWorkflowTail(Process)}.

% 4) anti-symmetric
:- optimizedWorkflowOrder(ProcessA, ProcessB), optimizedWorkflowOrder(ProcessB, ProcessA), ProcessA != ProcessB.

% 5) transitive (for optimization?)
optimizedWorkflowOrder(ProcessA, ProcessB) :- optimizedWorkflowOrder(ProcessA, ProcessI), optimizedWorkflowOrder(ProcessI, ProcessB), ProcessA != ProcessB.

% 6) dependency is satisfied
:- not optimizedWorkflowOrder(ProcessA, ProcessB), actorDependency(ProcessA, ProcessB).


% compute successor relation as the result
optimizedWorkflow(ProcessA, ProcessB) :- optimizedWorkflowOrder(ProcessA, ProcessB), not notOptimizedWorkflow(ProcessA, ProcessB).
notOptimizedWorkflow(ProcessA, ProcessB) :- optimizedWorkflowOrder(ProcessA, ProcessI), optimizedWorkflowOrder(ProcessI, ProcessB), ProcessA != ProcessB.

% insert control components as needed
newWorkflow(Process, c(Process, "splitter")) :- optimizedWorkflow(Process, ProcessA), optimizedWorkflow(Process, ProcessB), ProcessA != ProcessB.
newWorkflow(c(Process, "splitter"), ProcessB) :- optimizedWorkflow(Process, ProcessA), optimizedWorkflow(Process, ProcessB), ProcessA != ProcessB.
newWorkflow(c(Process, "splitter"), ProcessA) :- optimizedWorkflow(Process, ProcessA), optimizedWorkflow(Process, ProcessB), ProcessA != ProcessB.


%#show optimizedWorkflowOrder/2.
%#show optimizedWorkflow/2.
%#show newWorkflow/2.
