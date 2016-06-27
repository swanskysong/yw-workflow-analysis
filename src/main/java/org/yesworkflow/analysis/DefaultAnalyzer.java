package org.yesworkflow.analysis;

//import it.unical.mat.dlv.program.*;
//import it.unical.mat.dlv.program.Program;
//import it.unical.mat.wrapper.*;
//import it.unical.mat.wrapper.Model;
import org.yesworkflow.cli.ASPRule;
import org.yesworkflow.model.*;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Created by thsong on 5/17/16.
 */
public class DefaultAnalyzer {

    Program workflow;

    public DefaultAnalyzer model(Model model) {
        if (model == null) throw new IllegalArgumentException("analyzer: Null model passed to DefaultAnalyzer.");
        if (model.program == null) throw new IllegalArgumentException("analyzer: Model with null program passed to DefaultAnalyzer.");
        workflow = model.program;
        return this;
    }

    public void analyze(){
//        System.out.println("workflow.name = " + workflow.name);
        Program[] programs  = workflow.programs;
        String prevProcessName = null;
        WorkflowSpec workflowSpec = new WorkflowSpec();


        for (Program program : programs){

            String processName = program.name.replace("main.", "");
            workflowSpec.addProcess(processName);
            System.out.println("processName = " + processName);

            if (prevProcessName != null) workflowSpec.addWorkflowEdge(prevProcessName, processName);
            else {
                workflowSpec.setHeadProcess(processName);
                for (Port outPort : program.outPorts) {
                    workflowSpec.addInputData(outPort.data.name.toString().split(":")[0]);
                }
            }


            for (Port inPort : program.inPorts) {

//                System.out.println("inPort.toString() = " + inPort.data.toString());

                for (Port outPort : program.outPorts) {
//                    System.out.println("outPort.toString() = " + outPort.data.toString());
                    System.out.println("inPort.data.name.toString() = " + inPort.data.name.toString());
                    System.out.println("outPort.data.name.toString() = " + outPort.data.name.toString());

                    String validationType = "update";
                    if (Objects.equals(inPort.flowAnnotation.tag.toString(), "PARAM")) validationType = "read";
                    workflowSpec.addProcessConfig(processName, inPort.data.name.toString().split(":")[0], outPort.data.name.toString().split(":")[0], validationType);
                }
            }

            prevProcessName = processName;
        }

        workflowSpec.setTailProcess(prevProcessName);

//        workflowSpec.addInputData("SciName").addInputData("EventDate").addInputData("RepCon"); //.addInputData("Unused");


//        System.out.println("workflowSpec.generateASPRule() = " + workflowSpec.generateASPRule());
        URL url = getClass().getClassLoader().getResource("ASP");

        String path = "/home/thsong/data/yw-workflow-analysis/src/main/resources/ASP/";


//        System.out.println("url = " + url.getPath());

//        Path file = Paths.get(url.getPath() + "/facts.lp");
//        try {
//            Files.write(file, workflowSpec.generateASPRule().getBytes());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


        try {
            File file = new File(path + "test.lp");
            System.out.println("file.exists() = " + file.exists());
            if (!file.exists()) file.createNewFile();
            PrintWriter writer = new PrintWriter(file);
            writer.println(workflowSpec.generateASPRule());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

//        try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path + "/facts.lp"), "utf-8"))) {
//            writer.write(workflowSpec.generateASPRule());
//        }catch (IOException e) {
//            e.printStackTrace();
//        }

        // execute ASP


        FilenameFilter fileFilter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                if (name.endsWith(".lp")) return true;
                else return false;
            }
        };
        File[] files = new File(path).listFiles(fileFilter);


        StringBuilder commandSb = new StringBuilder();
        for(File file : files) commandSb.append(file + " ");
//        String command = "pwd";
        String command = "clingo " + commandSb.toString();
        System.out.println("command = " + command);

        StringBuffer output = new StringBuffer();
        ArrayList<String> adgFactsLine = new ArrayList<>();
        Process p;
        try {
            p = Runtime.getRuntime().exec(command);
            p.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line = "";
            while ((line = reader.readLine())!= null) {

                if(line.contains("adgEdge")){
                    String[] facts = line.split(" ");
                    System.out.println("facts.length = " + facts.length);
                    for (String fact : facts){
                        output.append(fact + "\n");
                        if(fact.contains("adgEdge")) adgFactsLine.add(fact);
                    }
                }
                else output.append(line + "\n");
            }


        } catch (Exception e) {
            e.printStackTrace();
        }


        // adgegde to got graph
        try {
            File file = new File(path + "output.gv");
            System.out.println("file.exists() = " + file.exists());
            if (!file.exists()) file.createNewFile();
            PrintWriter writer = new PrintWriter(file);
            writer.println("digraph adgGraph {");


            for (String fact : adgFactsLine) {
                String predicate = fact.substring(0, fact.length() - 1).replace("adgEdge(", "").replace("\"", "");
                String[] variables = predicate.split(",");
                ArrayList<String> mergedVars = new ArrayList<>();
                int i = 0;
                while(i < variables.length){
                    String curr = variables[i];
                    if(curr.contains("c(")) {
                        mergedVars.add(curr + "," + variables[i+1]);
                        i++;
                    }else mergedVars.add(curr);
                    i++;
                }
                System.out.println("mergedVars = " + mergedVars.size());

                String startNodeName = "\"" + mergedVars.get(0) + ":" + mergedVars.get(1) + "\"";
                String endNodeName = "\"" + mergedVars.get(3) + ":" + mergedVars.get(4) + "\"";
                String edgeLabel = "\"" + mergedVars.get(2) + "\"";
                if (!edgeLabel.contains("c(")) writer.println(startNodeName + " -> " + endNodeName + " [label=" + edgeLabel + "];");
            }

            writer.println("}");
            writer.close();

            Runtime.getRuntime().exec("dot -Tpdf output.gv -o output.pdf");

        } catch (IOException e) {
            e.printStackTrace();
        }



        // final output
        if(output.toString().contains("UNSATISFIABLE")) System.out.println("Have issues.");
        else if(output.toString().contains("SATISFIABLE")) System.out.println("dont have issues.");
        else System.out.println("sth wrong");

        System.out.println(output.toString());


        // not finished, convert Gringo output to dot graph
//        ASPRule test = new ASPRule().ParseFromString("adgEdge(\"A\", \"B\", \"C\")");
//        System.out.println("Test = " + test.toString());
//
//
//        try {
//            File file = new File(path + "viz.gv");
//            System.out.println("file.exists() = " + file.exists());
//            if (!file.exists()) file.createNewFile();
//            PrintWriter writer = new PrintWriter(file);
//
//            writer.println("digraph adgGraph {\n");
//            for (String item : adgFactsLine) {
//                ASPRule adgFact = new ASPRule().ParseFromString(item);
//                ArrayList<String> literals = adgFact.getLiterals();
//                System.out.println("literals = " + literals.get(0));
//                String nodeFormat = "\"%s|%s\"";
//                String startNode = String.format(nodeFormat, literals.get(0), literals.get(1));
//                String endNode = String.format(nodeFormat, literals.get(3), literals.get(4));
//                writer.println(startNode + " -> " + endNode);
////                System.out.println("endNode = " + startNode + " -> " + endNode);
//            }
//            writer.println("}");
//            writer.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


    }





//
//    public void invoke() {
//
//        DLVInvocation invocation=DLVWrapper.getInstance().createInvocation("/home/thsong/software/dlv");
//
//        DLVInputProgram inputProgram = new DLVInputProgramImpl();
//
//        Program program=new Program();
//        program.add(new Rule("a(1)."));
//        program.add(new Rule("b(X) :- a(X)."));
//        inputProgram.includeProgram(program);
//
//        Program adg_program = new Program();
//        ArrayList<String> fileList = new ArrayList<>();
//        fileList.add("/home/thsong/data/Workflow-Analysis-ASP/main/facts.lp");
//        fileList.add("/home/thsong/data/Workflow-Analysis-ASP/main/dependency_graph.lp");
//        fileList.add("/home/thsong/data/Workflow-Analysis-ASP/main/parallel.lp");
//        fileList.add("/home/thsong/data/Workflow-Analysis-ASP/main/queries.lp");
//        fileList.add("/home/thsong/data/Workflow-Analysis-ASP/main/rpq2.lp");
//
//        for (String fileName : fileList) {
//            try {
//                BufferedReader bf = new BufferedReader(new FileReader(fileName));
//
//                String line;
//
//                while ((line = bf.readLine()) != null) {
//
//                    if (line.length() == 0 || line.trim().charAt(0) == '%') continue;
////                    System.out.println(line);
//                    adg_program.add(new Rule(line));
//                }
//
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//
////        inputProgram.includeProgram(adg_program);
//
//        Program test=new Program();
//        test.add(new Rule("c(2)."));
//        test.add(new Rule("d(X) :- c(X)."));
//        test.add(new Rule(":- 2 < #count {X : c(X)}."));
//        inputProgram.includeProgram(test);
//
//
//        ModelBufferedHandler modelBufferedHandler=new ModelBufferedHandler(invocation);
//
//        try {
//            invocation.setInputProgram(inputProgram);
//            invocation.run();
//            invocation.waitUntilExecutionFinishes();
//        } catch (DLVInvocationException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        while(modelBufferedHandler.hasMoreModels()) {
//            Model model = modelBufferedHandler.nextModel();
//            System.out.println("model.hasMorePredicates() = " + model.hasMorePredicates());
//            System.out.println("model.nextPredicate() = " + model.toString());
//            while (model.hasMorePredicates()) {
//                Predicate predicate = model.nextPredicate();
//                System.out.println("predicate.toString() = " + predicate.toString());
//            }
//        }
//        System.out.println(invocation.getState());
//    }

    /**
     * Created by thsong on 5/18/16.
     */

}
