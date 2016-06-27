package org.yesworkflow.analysis;

import org.yesworkflow.cli.ASPRule;

import java.util.ArrayList;

/**
 * Created by thsong on 5/22/16.
 */
public class WorkflowSpec {

    ArrayList<ASPRule> processes = new ArrayList<>();
    ArrayList<ASPRule> processConfigs = new ArrayList<>();
    ArrayList<ASPRule> workflowEdges = new ArrayList<>();
    ArrayList<ASPRule> inputData = new ArrayList<>();
    ASPRule sourceProcess;
    ASPRule sinkProcess;
    ArrayList<ASPRule> processLabels = new ArrayList<>();

    public WorkflowSpec (){
        //        processLabel("actor", "next").
        processLabels.add(new ASPRule().setPredicateName("processLabel")
                .addLiterals("actor", "next"));
        processLabels.add(new ASPRule().setPredicateName("processLabel")
                .addLiterals("splitter", "split"));
        processLabels.add(new ASPRule().setPredicateName("processLabel")
                .addLiterals("merger", "merge"));

    }

    public void addProcess(String processName) {
        if (processName == null || processName.isEmpty()) {
            System.out.println("WorkflowSpec: empty process name.");
            return;
        }
        processes.add(new ASPRule().setPredicateName("process")
                .addLiterals(processName, "actor"));
    }

    public void addProcessConfig(String processName, String inputDataLabel, String outDataLabel, String validationType) {
        if (processName == null || processName.isEmpty()) {
            System.out.println("WorkflowSpec: empty process config name.");
            return;
        }
        if (inputDataLabel == null || processName.isEmpty()) {
            System.out.println("WorkflowSpec: empty input Data Label name.");
            return;
        }
        if (outDataLabel == null || processName.isEmpty()) {
            System.out.println("WorkflowSpec: empty out Data Label name.");
            return;
        }
        if (validationType == null || processName.isEmpty()) {
            System.out.println("WorkflowSpec: empty validation Type name.");
            return;
        }
        processConfigs.add(new ASPRule().setPredicateName("processConfig")
                .addLiterals(processName, inputDataLabel, outDataLabel, validationType));
    }

    public void addWorkflowEdge(String startProcessName, String endProcessName) {
        if (startProcessName == null || startProcessName.isEmpty()) {
            System.out.println("WorkflowSpec: empty workflow start edge name.");
            return;
        }
        if (endProcessName == null || endProcessName.isEmpty()) {
            System.out.println("WorkflowSpec: empty workflow end edge name.");
            return;
        }
        workflowEdges.add(new ASPRule().setPredicateName("workflow")
                .addLiterals(startProcessName, endProcessName));
    }
    public WorkflowSpec addInputData (String input) {
        inputData.add(new ASPRule().setPredicateName("inputData").addLiteral(input));
        return this;
    }

    public void setHeadProcess (String sourceProcess) {
        this.sourceProcess = new ASPRule().setPredicateName("sourceProcess").addLiteral(sourceProcess);
    }

    public void setTailProcess (String sinkProcess) {
        this.sinkProcess = new ASPRule().setPredicateName("sinkProcess").addLiteral(sinkProcess);
    }

    public String generateASPRule() {
        StringBuilder sb = new StringBuilder();

        //          process("Reader", "actor").
        for (ASPRule process : processes) sb.append(process.toString() + "\n");

        //          workflow("Reader", "SciNameValidator").
        for (ASPRule workflowEdge : workflowEdges) sb.append(workflowEdge.toString() + "\n");

        //          actorValidation("sciName", "SciNameValidator", "sciName").
        for (ASPRule processConfig : processConfigs) sb.append(processConfig.toString() + "\n");

        for (ASPRule inputDataItem : inputData) sb.append(inputDataItem.toString() + "\n");

        for (ASPRule processLabel : processLabels) sb.append(processLabel.toString() + "\n");

        sb.append(sourceProcess.toString() + "\n");
        sb.append(sinkProcess.toString() + "\n");

        return sb.toString();
    }
}
