package main;

import java.io.File;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class AutoChangerXML {

    public static void main(String argv[]) {

        int index = 1;

        try {
            String filepath = "./input/algorithmConfig.xml";
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(filepath);

            // Get the root element
            Node algorithm = doc.getFirstChild();

            // loop the staff child node
            NodeList list = algorithm.getChildNodes();

            String prob2 = "";

            for (int i = 0; i < list.getLength(); i++) {

                Node node = list.item(i);

                // get the iterations element, and update the value
                if ("iterations".equals(node.getNodeName())) {
                    //node.setTextContent("1024");
                    node.setTextContent(argv[index++]);
                }

                if("construction".equals(node.getNodeName())){
                    NodeList subList = node.getChildNodes();

                    for (int j = 0; j < subList.getLength(); j++) {

                        Node subNode = subList.item(j);

                        // get the insertion element, and update the value
                        if ("insertion".equals(subNode.getNodeName())) {
                            NamedNodeMap attr = subNode.getAttributes();
                            Node nodeAttr = attr.getNamedItem("name");
                            nodeAttr.setTextContent("bestInsertion");
                        }

                    }
                }

                if("strategy".equals(node.getNodeName())){

                    NodeList subList = node.getChildNodes();

                    for (int j = 0; j < subList.getLength(); j++) {

                        Node subNode = subList.item(j);

                        // get the insertion element, and update the value
                        if ("searchStrategies".equals(subNode.getNodeName())) {
                            NodeList subSubList =subNode.getChildNodes();

                            int counter = 0;

                            for (int k = 0; k < subSubList.getLength(); k++) {

                                Node subSubNode = subSubList.item(k);

                                // get the insertion element, and update the value
                                if ("searchStrategy".equals(subSubNode.getNodeName()) && counter == 0) {

                                    NamedNodeMap attr = subSubNode.getAttributes();
                                    Node nodeAttr = attr.getNamedItem("name");
                                    nodeAttr.setTextContent("randomRuinAndRecreate");

                                    NodeList subSubSubList = subSubNode.getChildNodes();

                                    for (int l = 0; l < subSubSubList.getLength(); l++) {

                                        Node subSubSubNode = subSubSubList.item(l);

                                        if ("selector".equals(subSubSubNode.getNodeName())) {
                                            NamedNodeMap attr2 = subSubSubNode.getAttributes();
                                            Node nodeAttr2 = attr2.getNamedItem("name");
                                            //nodeAttr2.setTextContent("selectBest");
                                            nodeAttr2.setTextContent(argv[index++]);
                                        }

                                        if ("acceptor".equals(subSubSubNode.getNodeName())) {
                                            NamedNodeMap attr2 = subSubSubNode.getAttributes();
                                            Node nodeAttr2 = attr2.getNamedItem("name");
                                            //nodeAttr2.setTextContent("acceptNewRemoveWorst");
                                            nodeAttr2.setTextContent(argv[index++]);
                                        }

                                        if ("modules".equals(subSubSubNode.getNodeName())){

                                            NodeList subSubSubSubList = subSubSubNode.getChildNodes();

                                            for (int m = 0; m < subSubSubSubList.getLength(); m++) {

                                                Node subSubSubSubNode = subSubSubSubList.item(m);

                                                if ("module".equals(subSubSubSubNode.getNodeName())) {

                                                    NamedNodeMap attr2 = subSubSubSubNode.getAttributes();
                                                    Node nodeAttr2 = attr2.getNamedItem("name");
                                                    nodeAttr2.setTextContent("ruin_and_recreate");

                                                    NodeList subSubSubSubSubList = subSubSubSubNode.getChildNodes();

                                                    for (int n = 0; n < subSubSubSubSubList.getLength(); n++) {

                                                        Node subSubSubSubSubNode = subSubSubSubSubList.item(n);

                                                        if ("ruin".equals(subSubSubSubSubNode.getNodeName())) {
                                                            NamedNodeMap attr3 = subSubSubSubSubNode.getAttributes();
                                                            Node nodeAttr3 = attr3.getNamedItem("name");
                                                            nodeAttr3.setTextContent("randomRuin");

                                                            NodeList subSubSubSubSubSubList = subSubSubSubSubNode.getChildNodes();

                                                            for (int o = 0; o < subSubSubSubSubSubList.getLength(); o++) {
                                                                Node subSubSubSubSubSubNode = subSubSubSubSubSubList.item(o);

                                                                if ("share".equals(subSubSubSubSubSubNode.getNodeName())) {
                                                                    //subSubSubSubSubSubNode.setTextContent("0.5");
                                                                    subSubSubSubSubSubNode.setTextContent(argv[index++]);
                                                                }

                                                            }
                                                        }

                                                        if ("insertion".equals(subSubSubSubSubNode.getNodeName())) {
                                                            NamedNodeMap attr3 = subSubSubSubSubNode.getAttributes();
                                                            Node nodeAttr3 = attr3.getNamedItem("name");
                                                            //nodeAttr3.setTextContent("bestInsertion");
                                                            nodeAttr3.setTextContent(argv[index++]);
                                                        }
                                                    }
                                                }
                                            }
                                        }

                                        if ("probability".equals(subSubSubNode.getNodeName())){
                                            //subSubSubNode.setTextContent("0.5");

                                            String prob = argv[index++];
                                            prob2 = ""+(1 - Double.parseDouble(prob))+"";
                                            subSubSubNode.setTextContent(prob);
                                        }



                                    }

                                    counter++;
                                } else if ("searchStrategy".equals(subSubNode.getNodeName()) && counter == 1) {

                                    NamedNodeMap attr = subSubNode.getAttributes();
                                    Node nodeAttr = attr.getNamedItem("name");
                                    nodeAttr.setTextContent("radialRuinAndRecreate");

                                    NodeList subSubSubList = subSubNode.getChildNodes();

                                    for (int l = 0; l < subSubSubList.getLength(); l++) {

                                        Node subSubSubNode = subSubSubList.item(l);

                                        if ("selector".equals(subSubSubNode.getNodeName())) {
                                            NamedNodeMap attr2 = subSubSubNode.getAttributes();
                                            Node nodeAttr2 = attr2.getNamedItem("name");
                                            //nodeAttr2.setTextContent("selectBest");
                                            nodeAttr2.setTextContent(argv[index++]);
                                        }

                                        if ("acceptor".equals(subSubSubNode.getNodeName())) {
                                            NamedNodeMap attr2 = subSubSubNode.getAttributes();
                                            Node nodeAttr2 = attr2.getNamedItem("name");
                                            //nodeAttr2.setTextContent("acceptNewRemoveWorst");
                                            nodeAttr2.setTextContent(argv[index++]);
                                        }

                                        if ("modules".equals(subSubSubNode.getNodeName())){

                                            NodeList subSubSubSubList = subSubSubNode.getChildNodes();

                                            for (int m = 0; m < subSubSubSubList.getLength(); m++) {

                                                Node subSubSubSubNode = subSubSubSubList.item(m);

                                                if ("module".equals(subSubSubSubNode.getNodeName())) {

                                                    NamedNodeMap attr2 = subSubSubSubNode.getAttributes();
                                                    Node nodeAttr2 = attr2.getNamedItem("name");
                                                    nodeAttr2.setTextContent("ruin_and_recreate");

                                                    NodeList subSubSubSubSubList = subSubSubSubNode.getChildNodes();

                                                    for (int n = 0; n < subSubSubSubSubList.getLength(); n++) {

                                                        Node subSubSubSubSubNode = subSubSubSubSubList.item(n);

                                                        if ("ruin".equals(subSubSubSubSubNode.getNodeName())) {
                                                            NamedNodeMap attr3 = subSubSubSubSubNode.getAttributes();
                                                            Node nodeAttr3 = attr3.getNamedItem("name");
                                                            nodeAttr3.setTextContent("radialRuin");

                                                            NodeList subSubSubSubSubSubList = subSubSubSubSubNode.getChildNodes();

                                                            for (int o = 0; o < subSubSubSubSubSubList.getLength(); o++) {
                                                                Node subSubSubSubSubSubNode = subSubSubSubSubSubList.item(o);

                                                                if ("share".equals(subSubSubSubSubSubNode.getNodeName())) {
                                                                    //subSubSubSubSubSubNode.setTextContent("0.3");
                                                                    subSubSubSubSubSubNode.setTextContent(argv[index++]);
                                                                }

                                                            }
                                                        }

                                                        if ("insertion".equals(subSubSubSubSubNode.getNodeName())) {
                                                            NamedNodeMap attr3 = subSubSubSubSubNode.getAttributes();
                                                            Node nodeAttr3 = attr3.getNamedItem("name");
                                                            //nodeAttr3.setTextContent("bestInsertion");
                                                            nodeAttr3.setTextContent(argv[index++]);
                                                        }
                                                    }
                                                }
                                            }
                                        }

                                        if ("probability".equals(subSubSubNode.getNodeName())){
                                            subSubSubNode.setTextContent(prob2);
                                        }

                                    }

                                    counter++;
                                }

                            }
                        }

                    }
                }

            }

            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(filepath));
            transformer.transform(source, result);

            //System.out.println("Done");

        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (TransformerException tfe) {
            tfe.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (SAXException sae) {
            sae.printStackTrace();
        }
    }
}
