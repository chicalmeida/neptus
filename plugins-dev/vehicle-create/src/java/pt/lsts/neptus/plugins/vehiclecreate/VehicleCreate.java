/*
 * Copyright (c) 2004-2022 Universidade do Porto - Faculdade de Engenharia
 * Laboratório de Sistemas e Tecnologia Subaquática (LSTS)
 * All rights reserved.
 * Rua Dr. Roberto Frias s/n, sala I203, 4200-465 Porto, Portugal
 *
 * This file is part of Neptus, Command and Control Framework.
 *
 * Commercial Licence Usage
 * Licencees holding valid commercial Neptus licences may use this file
 * in accordance with the commercial licence agreement provided with the
 * Software or, alternatively, in accordance with the terms contained in a
 * written agreement between you and Universidade do Porto. For licensing
 * terms, conditions, and further information contact lsts@fe.up.pt.
 *
 * Modified European Union Public Licence - EUPL v.1.1 Usage
 * Alternatively, this file may be used under the terms of the Modified EUPL,
 * Version 1.1 only (the "Licence"), appearing in the file LICENCE.md
 * included in the packaging of this file. You may not use this work
 * except in compliance with the Licence. Unless required by applicable
 * law or agreed to in writing, software distributed under the Licence is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF
 * ANY KIND, either express or implied. See the Licence for the specific
 * language governing permissions and limitations at
 * https://github.com/LSTS/neptus/blob/develop/LICENSE.md
 * and http://ec.europa.eu/idabc/eupl.html.
 *
 * For more information please see <http://lsts.fe.up.pt/neptus>.
 *
 */
package pt.lsts.neptus.plugins.vehiclecreate;

import com.google.common.eventbus.Subscribe;
import net.miginfocom.swing.MigLayout;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import pt.lsts.imc.IMCDefinition;
import pt.lsts.imc.IMCMessage;
import pt.lsts.neptus.comm.manager.imc.ImcMsgManager;
import pt.lsts.neptus.comm.manager.imc.ImcSystem;
import pt.lsts.neptus.console.ConsoleLayer;
import pt.lsts.neptus.console.ConsoleLayout;
import pt.lsts.neptus.console.ConsolePanel;
import pt.lsts.imc.Announce;
import pt.lsts.neptus.comm.manager.imc.AnnounceWorker;
import pt.lsts.neptus.plugins.PluginDescription;
import pt.lsts.neptus.plugins.Popup;
import pt.lsts.neptus.types.vehicle.VehicleType;
import pt.lsts.neptus.util.FileUtil;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.LinkedList;

@PluginDescription(name = "Vehicle Create",description="Add vehicle")
@Popup(width = 300, height = 300)
public class VehicleCreate extends ConsolePanel {
    VehicleType vh = new VehicleType();

    public VehicleCreate(ConsoleLayout console) {
        super(console);
    }

    public VehicleCreate(ConsoleLayout console, boolean usedInsideAnotherConsolePanel) {
        super(console, usedInsideAnotherConsolePanel);
    }

    @Override
    public void initSubPanel() {
        Announce an = new Announce();
        JButton btn = new JButton("Test Add Vehicle");
        btn.addActionListener((e) -> {
            start(an);
        });
        add(btn);
    }

    @Override
    public void cleanSubPanel() {

    }

    public void drawInitialMessage(){

        JDialog frame = new JDialog(this.getConsole());
        frame.setTitle("New Vehicle");
        frame.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        frame.setSize(300, 150);

        JPanel panel = new JPanel(new MigLayout());


        JLabel message = new JLabel("New vehicle detected.");
        JLabel l = new JLabel("Do you want to add this vehicle?");
        JButton bt = new JButton("Add new Vehicle");

        bt.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                drawVehicleInfo();
            }
        });

        panel.add(message, "push, align center, wrap");
        panel.add(l, "push, align center, wrap");
        panel.add(bt, "push, align center");

        frame.add(panel, BorderLayout.CENTER);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public void getParams(Announce a){
        vh.setName(a.getSysName());
        vh.setType(a.getSysTypeStr());
        String services = a.getServices();
        System.out.println(services);
    }

    public void drawVehicleInfo(){
        JDialog frame = new JDialog(this.getConsole());
        frame.setTitle("New Vehicle");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(500, 500);

        JPanel panel = new JPanel(new MigLayout());

        //Properties
        JLabel p = new JLabel("Properties");
        panel.add(p, "wrap");
        /*JLabel idLabel = new JLabel("Vehicle ID");
        JTextField idTextField = new JTextField(vh.getId(), 15);*/
        JLabel vehicleName = new JLabel("Vehicle Name");
        JTextField nameTextField = new JTextField(vh.getName(),30);
        JLabel vTypeLabel = new JLabel("Vehicle Type");
        JTextField vehicleType = new JTextField(vh.getType(), 15);
        /*JLabel vModelLabel = new JLabel("Vehicle Model");
        JTextField vehicleModel = new JTextField(vh.getModel(), 15);*/

        /*panel.add(idLabel);
        panel.add(idTextField, "wrap");*/
        panel.add(vehicleName);
        panel.add(nameTextField, "wrap");
        panel.add(vTypeLabel);
        panel.add(vehicleType, "wrap");
        /*panel.add(vModelLabel);
        panel.add(vehicleModel, "wrap");*/

        //Appearance
        /*JLabel a = new JLabel("Appearance");
        panel.add(a, "wrap");
        FileNameExtensionFilter filter = new FileNameExtensionFilter("JPG & PNG Images", "jpg", "png");
        JLabel xsizeL = new JLabel("x-size");
        JTextField xSizeField = new JTextField(8);
        JLabel ysizeL = new JLabel("y-size");
        JTextField ySizeField = new JTextField(8);
        JLabel zsizeL = new JLabel("z-size");
        JTextField zSizeField = new JTextField(8);
        JLabel topimageL = new JLabel("Top Image");
        JFileChooser topImage = new JFileChooser();
        topImage.setFileFilter(filter);
        JLabel sideimageL = new JLabel("Side Image");
        JFileChooser sideImage = new JFileChooser();
        sideImage.setFileFilter(filter);
        JLabel bottomimageL = new JLabel("Back Image");
        JFileChooser bottomImage = new JFileChooser();
        bottomImage.setFileFilter(filter);
        JLabel presentationimageL = new JLabel("Presentation Image");
        JFileChooser presentationImage = new JFileChooser();
        presentationImage.setFileFilter(filter);

        panel.add(xsizeL);
        panel.add(xSizeField);
        panel.add(ysizeL);
        panel.add(ySizeField);
        panel.add(zsizeL);
        panel.add(zSizeField, "wrap");
        panel.add(topimageL);
        panel.add(topImage, "wrap");
        panel.add(sideimageL);
        panel.add(sideImage, "wrap");
        panel.add(bottomimageL);
        panel.add(bottomImage, "wrap");
        panel.add(presentationimageL);
        panel.add(presentationImage, "wrap");

        //Limits
        JLabel l = new JLabel("Limits");
        panel.add(l, "wrap");
        JLabel zunits = new JLabel("Valid z units");
        JTextField zUnitsT = new JTextField(8);
        JLabel minSpeed = new JLabel("Min Speed");
        JTextField minSpeedT = new JTextField(8);
        JLabel maxSpeed = new JLabel("Max Speed");
        JTextField maxSpeedT = new JTextField(8);
        JLabel duration = new JLabel("Maximum Duration Hours");
        JTextField durationT = new JTextField(8);
        JLabel coordSystem = new JLabel("Coordinate System Label");
        JTextField coordS = new JTextField(15);

        panel.add(zunits);
        panel.add(zUnitsT, "wrap");
        panel.add(minSpeed);
        panel.add(minSpeedT, "wrap");
        panel.add(maxSpeed);
        panel.add(maxSpeedT, "wrap");
        panel.add(duration);
        panel.add(durationT, "wrap");
        panel.add(coordSystem);
        panel.add(coordS, "wrap");*/


        JButton bt = new JButton("Confirm");

        panel.add(bt, "push, align center");

        bt.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                writeXML();
                System.out.println("Done creating XML File");
            }
        });

        frame.add(panel, BorderLayout.CENTER);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    // FIXME
    @Subscribe
    public void onAnnounce(Announce msg) {
        // TODO podes fazer algum processamento para ver se o veículo não é cenhecido
        // e depois poder chamar o teu start, ou chamas logo para testar
        //ou atraves do nome do ficheiro ou do owner?
        msg.getOwner();
    }

    public void start(Announce announce){
        getParams(announce);
        drawInitialMessage();
        writeXML();
    }

    public void writeXML(){
        //final String xmlFilePath = "C:\\Users\\Francisca\\Desktop\\UNI\\LSTS\\xmlfile.xml";
        // FIXME Podes fazer assim para não depender de correr só no teu PC
        final String xmlFilePath = "xmlfile.xml"; //Fica no root da pasta onde tens o Neptus

        try {
            DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();

            DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();

            Document document = documentBuilder.newDocument();

            // root element
            Element root = document.createElement("system");
            document.appendChild(root);


            // properties element
            Element properties = document.createElement("properties");
            root.appendChild(properties);

            // set an attribute to staff element
            /*Attr attr = document.createAttribute("id");
            attr.setValue(vh.getId());
            properties.setAttributeNode(attr);*/

            //you can also use staff.setAttribute("id", "1") for this

            // name element
            Element name = document.createElement("name");
            name.appendChild(document.createTextNode(vh.getName()));
            properties.appendChild(name);

            // type element
            Element type = document.createElement("type");
            type.appendChild(document.createTextNode(vh.getType()));
            properties.appendChild(type);

            /*Element model = document.createElement("model");
            type.appendChild(document.createTextNode(vh.getModel()));
            properties.appendChild(model);*/

            Element appearance = document.createElement("appearance");
            properties.appendChild(appearance);


            // department elements
            Element protocol = document.createElement("protocols-supported");
            root.appendChild(protocol);

            //protocol element
            /*Element p = document.createElement("protocol");
            for(String element : vh.getProtocols()){
                p.appendChild(document.createTextNode(element));
            }
            protocol.appendChild(p);*/

            //protocol-args element
            /*Element args = document.createElement("protocols-args");
            protocol.appendChild(args);

            //gsm element
            Element gsm = document.createElement("gsm");
            args.appendChild(gsm);

            //number element
            Element number = document.createElement("number");

            gsm.appendChild(number);

            //iridium element
            Element iridium = document.createElement("iridium");
            args.appendChild(iridium);

            //imei element
            Element imei = document.createElement("imei");
            imei.appendChild(document.createTextNode(this.imei));
            iridium.appendChild(imei);

            Element communication = document.createElement("communication-means");
            root.appendChild(communication);

            Element comMean = document.createElement("comm-mean");
            communication.appendChild(comMean);

            Element cname = document.createElement("name");
            cname.appendChild(document.createTextNode(this.cname));
            comMean.appendChild(cname);

            Element ctype = document.createElement("type");
            ctype.appendChild(document.createTextNode(this.ctype));
            comMean.appendChild(ctype);

            Element host = document.createElement("host-address");
            host.appendChild(document.createTextNode(this.hAddress));
            comMean.appendChild(host);

            Element prts = document.createElement("protocols");
            prts.appendChild(document.createTextNode(this.prts));
            comMean.appendChild(prts);

            Element latency = document.createElement("latency");
            latency.setAttribute("value", this.l_value);
            latency.setAttribute("units", this.l_units);
            comMean.appendChild(latency);

            Element prt_args = document.createElement("protocols-args");
            comMean.appendChild(prt_args);

            Element imc = document.createElement("imc");
            prt_args.appendChild(imc);

            Element port = document.createElement("port");
            port.appendChild(document.createTextNode(this.porta));
            imc.appendChild(port);

            Element port_tcp=document.createElement("port-tcp");
            port_tcp.appendChild(document.createTextNode(this.porttcp));
            imc.appendChild(port_tcp);

            Element udp = document.createElement("udp-on");
            udp.appendChild(document.createTextNode(this.udp));
            imc.appendChild(udp);

            Element tcp = document.createElement("tcp-on");
            tcp.appendChild(document.createTextNode(this.tcp));
            imc.appendChild(tcp);

            Element imc_id = document.createElement("imc-id");
            imc_id.appendChild(document.createTextNode(this.imc_id));
            imc.appendChild(imc_id)*/;


            // create the xml file
            //transform the DOM Object to an XML File
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource domSource = new DOMSource(document);
            StreamResult streamResult = new StreamResult(new File(xmlFilePath));

            // If you use
            // StreamResult result = new StreamResult(System.out);
            // the output will be pushed to the standard output ...
            // You can use that for debugging

            transformer.transform(domSource, streamResult);


        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (TransformerException tfe) {
            tfe.printStackTrace();
        }
    }
}
