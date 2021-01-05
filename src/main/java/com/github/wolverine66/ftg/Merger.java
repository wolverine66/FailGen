package com.github.wolverine66.ftg;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Merger {
    public static void main(String[] args) throws JDOMException, IOException {
        int ITotal,IPassed,IFailed,ISkipped,IIgnored;
        int RTotal,RPassed,RFailed,RSkipped,RIgnored;

        SAXBuilder builder = new SAXBuilder();
        System.out.println("################## Summary of Processed Result file #################");
        File xmlfile1 = new File(args[0]);
        Document doc = builder.build(xmlfile1);
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

        builder = new SAXBuilder();
        System.out.println("################## Summary of Processed Result file #################");
        File xmlfile2 = new File(args[0]);
        Document doc1 = builder.build(xmlfile2);
        Element rootNode1 = doc1.getRootElement();
        RTotal = Integer.parseInt(rootNode1.getAttributeValue("passed"))
                +Integer.parseInt(rootNode1.getAttributeValue("failed"))
                +Integer.parseInt(rootNode1.getAttributeValue("skipped"));
        RPassed = Integer.parseInt(rootNode1.getAttributeValue("passed"));
        RFailed = Integer.parseInt(rootNode1.getAttributeValue("failed"));
        RSkipped = Integer.parseInt(rootNode1.getAttributeValue("skipped"));
        RIgnored = Integer.parseInt(rootNode1.getAttributeValue("ignored"));
        System.out.println("################# Rerun results ################");
        System.out.println("Total Tests " + RTotal);
        System.out.println("Passed " + RPassed);
        System.out.println("Failed " + RFailed);
        System.out.println("Skipped" + RSkipped);

        if(RPassed !=0) {
            builder = new SAXBuilder();
            Document doc3 = (Document) builder.build(xmlfile1);
            Element rootNode2 = doc3.getRootElement();
            rootNode2.getAttribute("passed").setValue(Integer.toString(IPassed + RPassed));
            rootNode2.getAttribute("failed").setValue(Integer.toString(RFailed));
            rootNode2.getAttribute("skipped").setValue(Integer.toString(RSkipped));
            rootNode2.removeChild("reporter-output");
            rootNode2.removeChild("suite");
            rootNode2.addContent("Consolidated result count of initial and rerun");
            XMLOutputter xmlOutput = new XMLOutputter();
            //display
            xmlOutput.setFormat(Format.getPrettyFormat());
            xmlOutput.output(doc3, new FileWriter(args[2]));
            System.out.println("############## Consolidated results #############");
            System.out.println("Total Tests " + ITotal);
            System.out.println("Passed " + (IPassed + RPassed));
            System.out.println("Failed " + RFailed);
            System.out.println("Skipped" + RSkipped);
            System.out.println("Results updated in " + args[2]);
        }else{
            System.out.println("Zero pass rate in rerun so file not updated");
        }
    }
}
