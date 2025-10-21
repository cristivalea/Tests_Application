package com.example.testsapi.entity;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Objectiv implements Comparable<Objectiv>{
    protected String nume;
    protected String id;
    protected boolean mainObiectiv;
    protected List<Objectiv> listaSubobiective;

    public Objectiv(String nume, String id, boolean mainObiectiv, List<Objectiv> listaSubobiective) {
        this.nume = nume;
        this.id = id;
        this.mainObiectiv = mainObiectiv;
        this.listaSubobiective = listaSubobiective;
    }

    public String getNume() {
        return nume;
    }

    public void setNume(String nume) {
        this.nume = nume;
    }

    public boolean isMainObiectiv() {
        return mainObiectiv;
    }

    public void setMainObiectiv(boolean mainObiectiv) {
        this.mainObiectiv = mainObiectiv;
    }

    public List<Objectiv> getListaSubobiective() {
        return listaSubobiective;
    }

    public void setListaSubobiective(List<Objectiv> listaSubobiective) {
        this.listaSubobiective = listaSubobiective;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public static List<Objectiv> readObjectiv(String path) throws ParserConfigurationException, SAXException, IOException {
        File fileInput = new File(path);
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document document = db.parse(fileInput);
        document.getDocumentElement().normalize();

        NodeList nl1 = document.getElementsByTagName("obiectiv");
        List<Objectiv> obiective = new ArrayList<>();

        for(int i = 0; i < nl1.getLength(); i++){
            Node op = nl1.item(i);
            if(op.getNodeType() == Node.ELEMENT_NODE){
                Element e = (Element) op;
                if(op.getParentNode().getNodeName().equals("obiective")) {
                    String idOP = e.getAttribute("id");
                    String numeOP = e.getElementsByTagName("name").item(0).getTextContent().trim();
                    //System.out.println("Obiectiv principal: " + numeOP);
                    e.getElementsByTagName("label");
                    List<Objectiv> subobiective = new ArrayList<>();
                    NodeList subObLista = e.getElementsByTagName("subobiective");
                    if(subObLista.getLength() > 0) {
                        Element subEl = (Element) subObLista.item(0);
                        NodeList n2 = subEl.getElementsByTagName("obiectiv");
                        for (int j = 0; j < n2.getLength(); j++) {
                            Node os = n2.item(j);
                            //System.out.print("Obiectiv Secundar: ");
                            if (os.getNodeType() == Node.ELEMENT_NODE) {
                                Element e2 = (Element) os;
                                String idOS = e2.getAttribute("id");
                                String numeOS = e2.getElementsByTagName("name").item(0).getTextContent().trim();
                                if (numeOS == null) {
                                    System.err.println("Obiectivul secundar " + j + " nu are nume");
                                }
                                //System.out.println(numeOS);
                                subobiective.add(new Objectiv(numeOS,idOS, false, null));
                            }
                        }
                    }
                    obiective.add(new Objectiv(numeOP, idOP, true, subobiective));
                }
            }
        }
        return obiective;
    }

    @Override
    public String toString() {
        return "Objectiv{" +
                "nume='" + nume + '\'' +
                ", id='" + id + '\'' +
                ", mainObiectiv=" + mainObiectiv +
                ", listaSubobiective=" + listaSubobiective +
                '}';
    }

    @Override
    public int compareTo(Objectiv o) {
        return this.getId().compareTo(o.getId());
    }

}
