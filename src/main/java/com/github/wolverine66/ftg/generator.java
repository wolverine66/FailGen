package com.github.wolverine66.ftg;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.testng.xml.XmlClass;
import org.testng.xml.XmlInclude;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Generator {
    public static void main(String[] args) throws JDOMException, IOException {
        float ITotal,IPassed,IFailed,ISkipped,IIgnored;
        SAXBuilder builder = new SAXBuilder();
        System.out.println("################## Summary of Processed Result file #################");
        File file = new File(args[0]);
        Document doc = builder.build(file);
        Element rootNode = doc.getRootElement();
        ITotal = Integer.parseInt(rootNode.getAttributeValue("passed"))
                +Integer.parseInt(rootNode.getAttributeValue("failed"))
                +Integer.parseInt(rootNode.getAttributeValue("skipped"));
        IPassed = Integer.parseInt(rootNode.getAttributeValue("passed"));
        IFailed = Integer.parseInt(rootNode.getAttributeValue("failed"));
        ISkipped = Integer.parseInt(rootNode.getAttributeValue("skipped"));
        IIgnored = Integer.parseInt(rootNode.getAttributeValue("ignored"));

        System.out.println("################# Initial results ################");
        System.out.println("Total Tests " + ITotal);
        System.out.println("Passed " + IPassed);
        System.out.println("Failed " + IFailed);
        System.out.println("Skipped" + ISkipped);
        System.out.println("Ignored" + IIgnored);

        HashMap<String, String> failureTestList = new LinkedHashMap<>();
        Element appSettings = rootNode.getChild("suite");
        String suiteName = appSettings.getAttributeValue("name");
        System.out.println("Suite "+suiteName);
        List<Element> IElement = appSettings.getChildren("test");

        for(Element e : IElement){
            boolean failureflg = false;
            String testMethod = null;
            String testName = e.getAttributeValue("name");
            testName = "#&&#"+testName;
            Element classE = e.getChild("class");
            List<Element> testE = classE.getChildren("test-method");
            for(Element test : testE){
                if(test.getAttributeValue("status").equalsIgnoreCase("fail") || test.getAttributeValue("status").equalsIgnoreCase("skip")){
                    failureflg = true;
                    break;
                }
            }
            if(failureflg){
                for(Element test : testE){
                    if(test.getAttributeValue("is-config")== null){
                        testMethod = test.getAttributeValue("name")+testName;
                        break;
                    }
                }
                failureTestList.put(testMethod, classE.getAttributeValue("name"));
            }
        }
        String FailureThreshold = args[1];
        System.out.println("Failure Threshold "+FailureThreshold);
        //TestNG XMl generation
        System.out.println("Failure percent "+(((ITotal-IPassed)/ITotal)*100));
        if(IPassed!=ITotal && (((ITotal-IPassed)/ITotal)*100)<=Integer.parseInt(FailureThreshold)){
            XmlSuite rerunSuite = new XmlSuite();
            rerunSuite.setName(suiteName);
            for(Map.Entry<String, String> s : failureTestList.entrySet()) {
                String key = s.getKey();
                String testNames[] = key.split("#&&#");
                XmlTest test = new XmlTest(rerunSuite);
                test.setName(testNames[1]);
                XmlClass testClass = new XmlClass();
                testClass.setName(s.getValue());
                XmlInclude includeMethod = new XmlInclude(testNames[0]);
                List<XmlInclude> listInclude = new ArrayList<>();
                listInclude.add(includeMethod);
                testClass.setIncludedMethods(listInclude);
                ArrayList<XmlClass> classes = new ArrayList<>();
                classes.add(testClass);
                test.setXmlClasses(classes);
            }
            System.out.println("########### Printing Failed TestNG Suite XML");
            System.out.println(rerunSuite.toXml());
            File generated_file = new File(args[2]);
            System.out.println("File "+file);
            FileWriter writer = new FileWriter(generated_file);
            writer.write(rerunSuite.toXml());
            writer.close();}
        else{
            System.out.println("Rerun is not required as either all tests passed or above the failure rerun threshold");
        }
    }
}
