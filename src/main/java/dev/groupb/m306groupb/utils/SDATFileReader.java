package dev.groupb.m306groupb.utils;

import dev.groupb.m306groupb.enums.FileType;
import dev.groupb.m306groupb.enums.MeasureUnit;
import dev.groupb.m306groupb.enums.Unit;
import dev.groupb.m306groupb.model.FileDate;
import dev.groupb.m306groupb.model.Resolution;
import dev.groupb.m306groupb.model.SDATFile.Observation;
import dev.groupb.m306groupb.model.SDATFile.SDATFile;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.SortedSet;

public class SDATFileReader implements FileReader<SDATFile> {
    @Override
    public FileDate getFileDate(File file) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(GlobalStuff.XML_DATE_FORMAT);

        try {
            FileDate fileDate = new FileDate();

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(file);

            doc.getDocumentElement().normalize();

            NodeList interval = doc.getElementsByTagName("rsm:Interval");
            for (int i = 0; i < interval.getLength(); i++) {
                Node item = interval.item(i);
                item.normalize();

                NodeList children = item.getChildNodes();
                for (int j = 0; j < children.getLength(); j++) {
                    Node child = children.item(j);
                    child.normalize();

                    if (child.getNodeName().equals("rsm:StartDateTime")) {
                        fileDate.setStartDate(dateFormat.parse(child.getTextContent()));
                    } else if (child.getNodeName().equals("rsm:EndDateTime")) {
                        fileDate.setEndDate(dateFormat.parse(child.getTextContent()));
                    }
                }
            }

            NodeList instanceDocument = doc.getElementsByTagName("rsm:InstanceDocument");
            for (int i = 0; i < instanceDocument.getLength(); i++) {
                Node item = instanceDocument.item(i);
                item.normalize();

                NodeList children = item.getChildNodes();
                for (int j = 0; j < children.getLength(); j++) {
                    Node child = children.item(j);
                    child.normalize();

                    if (child.getNodeName().equals("rsm:Creation")) {
                        fileDate.setFileCreationDate(dateFormat.parse(child.getTextContent()));
                    }
                }
            }

            return fileDate;
        } catch (ParserConfigurationException | IOException | SAXException | ParseException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public SDATFile parseFile(File file) {
        return SDATFile.builder()
                .fileName(file.getName())
                .filePath(file.getAbsolutePath())
                .fileType(findFileType(file))
                .resolution(findResolution(file))
                .measureUnit(findMeasureUnit(file))
                .observations(findObservations(file))
                .build();
    }

    private SortedSet<Observation> findObservations(File file) {
        return null;
    }

    private MeasureUnit findMeasureUnit(File file) {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(file);

            doc.getDocumentElement().normalize();

            NodeList productNode = doc.getElementsByTagName("rsm:Product");
            if (productNode.getLength() == 0)
                return null;
            for (int i = 0; i < productNode.getLength(); i++) {
                Node item = productNode.item(i);
                item.normalize();

                NodeList children = item.getChildNodes();
                for (int j = 0; j < children.getLength(); j++) {
                    Node child = children.item(j);
                    child.normalize();

                    if (child.getNodeName().equals("rsm:MeasureUnit")) {
                        return MeasureUnit.fromString(child.getTextContent());
                    }
                }
            }

            return null;
        } catch (ParserConfigurationException | IOException | SAXException e) {
            throw new RuntimeException(e);
        }
    }

    private Resolution findResolution(File file) {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(file);

            doc.getDocumentElement().normalize();

            NodeList resolutionNode = doc.getElementsByTagName("rsm:Resolution");
            if (resolutionNode.getLength() == 0)
                return null;
            Resolution resolution = new Resolution();
            for (int i = 0; i < resolutionNode.getLength(); i++) {
                Node item = resolutionNode.item(i);
                item.normalize();

                NodeList children = item.getChildNodes();
                for (int j = 0; j < children.getLength(); j++) {
                    Node child = children.item(j);
                    child.normalize();

                    if (child.getNodeName().equals("rsm:Resolution")) {
                        resolution.setResolution(Integer.parseInt(child.getTextContent()));
                    } else if (child.getNodeName().equals("rsm:Unit")) {
                        resolution.setTimeUnit(Unit.fromString(child.getTextContent()));
                    }
                }
            }

            return resolution;
        } catch (ParserConfigurationException | IOException | SAXException e) {
            throw new RuntimeException(e);
        }
    }

    private FileType findFileType(File file) {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(file);

            doc.getDocumentElement().normalize();

            NodeList consumption = doc.getElementsByTagName("rsm:ConsumptionMeteringPoint");
            NodeList production = doc.getElementsByTagName("rsm:ProductionMeteringPoint");

            if (consumption.getLength() > 0)
                return FileType.Consumption;
            else if (production.getLength() > 0)
                return FileType.Production;

            throw new RuntimeException("File type not found");
        } catch (ParserConfigurationException | IOException | SAXException e) {
            throw new RuntimeException(e);
        }
    }
}
