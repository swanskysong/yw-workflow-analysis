%bf(X,Y) | bf(Y,X) :- actor(X), actor(Y), X != Y.

%config("a1", "RepCon", "SciName", "df", "True").
%config("a1", "RepCon", "RepCon", "df", "False").

%adgEdge(In, Out) | notAdgEdge(In, Out) :- config(_, Out, In, _, "True").
%adgEdge(In, Out) :- config(_, Out, In, _, "False").

cdgNode("ConA", "date", "1002", "original", "1").
cdgNode("ConA", "year", "2010", "original", "1").
cdgNode("ConA", "month", "Feb", "orignial", "1").
cdgNode("ConB", "date", "201002", "cureated", "1").
cdgNode("ConB", "year", "2010", "original", "1").
cdgNode("ConB", "month", "02", "cureated", "1").

cdgNode("ConA", "date", "1002", "original", "2").
cdgNode("ConA", "year", "2002", "original", "2").
cdgNode("ConB", "date", "200210", "cureated", "2").
cdgNode("ConB", "year", "2002", "original", "2").
cdgNode("ConB", "month", "10", "cureated", "2").

dcgEdge("ConA", "flowering", "actor", "ConB").


% Find the data items that the values are different in different invocation (order the invocation number to remove duplicate)
changedData(Container, DataLabel, InvocationA, InvocationB) :- 
	cdgNode(Container, DataLabel, InvocationA, DataValueA, _), 
	cdgNode(Container, DataLabel, InvocationB, DataValueB, _), 
	DataValueA != DataValueB, InvocationA < InvocationB.

% Find the container that has more than one data items that have value changed in different invocations, i.e., not useful pair of invocations
notUsefulDataChange(Container, InvocationA, InvocationB) :- 
	changedData(Container, DataLabelA, InvocationA, InvocationB), 
	changedData(Container, DataLabelB, InvocationA, InvocationB), 
	DataLabelA != DataLabelB, InvocationA != InvocationB.

% Eliminate the container and invocations that are not useful
usefulChangedData(Container, DataLabel, InvocationA, InvocationB) :- 
	changedData(Container, DataLabel, InvocationA, InvocationB), 
	not notUsefulDataChange(Container, InvocationA, InvocationB).

% For the pair of container a process reads from and writes to, find all the changed outputs if the input container is useful.
% Exclude the case where the same data item changes in different invocations 
inferredDependency(DataLabelIn, ProcessName, "unknown", DataLabelOut) :-
	usefulChangedData(ContainerIn, DataLabelIn, InvocationA, InvocationB),
	changedData(ContainerOut, DataLabelOut, InvocationA, InvocationB),
	dcgEdge(ContainerIn, ProcessName, _, ContainerOut), DataLabelIn != DataLabelOut.

% If the data items with the same label changed in the same invocation, it is a derived_from
inferredDependency(DataLabel, ProcessName, "derived_from", DataLabel) :-
	cdgNode(ContainerIn, DataLabel, Invocation, DataValueIn, _), 
	cdgNode(ContainerOut, DataLabel, Invocation, DataValueOut, _),
	dcgEdge(ContainerIn, ProcessName, _, ContainerOut), DataValueIn != DataValueOut.

#show inferredDependency/4.



oldInfer(DataLabelIn, ProcessName, DataLabelOut) :-
cdgNode(ContainerIn, DataLabelIn, DataValueInA, _, InvocationA), cdgNode(ContainerIn, DataLabelIn, DataValueInB, _, InvocationB),
DataValueInA != DataValueInB, InvocationA != InvocationB,
cdgNode(ContainerOut, DataLabelOut, DataValueOutA, _, InvocationA), cdgNode(ContainerOut, DataLabelOut, DataValueOutB, _, InvocationB), 
DataValueOutA != DataValueOutB, DataLabelIn != DataLabelOut,
usefulChangedData(ContainerIn, DataLabelIn, InvocationA, InvocationB),
dcgEdge(ContainerIn, ProcessName, _, ContainerOut).




