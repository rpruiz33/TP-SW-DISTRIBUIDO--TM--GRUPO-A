package com.empuje.web_service.utils;

import java.util.List;

import org.apache.cxf.headers.Header;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.ws.BindingProvider;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;


import org.empuje.web_service.wsdl.*;

@Configuration
public class SoapConfig {

    @Bean
    Application applicationClient() {
        //Obtenemos la semilla de la fábrica de clientes proxy:
        JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();

        //La configuramos:
        factory.setServiceClass(Application.class); //Definimos tipo de cliente a generar.
        factory.setAddress("https://soap-app-latest.onrender.com/"); //Definimos a qué servidor consulta.

        //Instanciamos el cliente SOAP:
        Application client = (Application) factory.create();

        //Configuramos el header SOAP:
        try {
            //Definimos nuestro grupo y clave:
            AuthHeader auth = new AuthHeader();
            auth.setGrupo("GrupoA-TM");
            auth.setClave("clave-tm-a");

            //Convertimos el AuthHeader a XML:
            JAXBContext context = JAXBContext.newInstance(AuthHeader.class);
            Marshaller marshaller = context.createMarshaller();

            //Carga el XML en un documento vacío:
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            marshaller.marshal(auth, doc);

            //Se obtiene el elemento de auth del documento y se crea un Header de CXF usando ese elemento y el namespace "auth.headers":
            Element authElement = doc.getDocumentElement();
            Header soapHeader = new Header(new QName("auth.headers", "Auth"), authElement);

            //Se accede al contexto del cliente SOAP y se agrega el header recién creado a la lista de headers que se enviarán en la próxima llamada:
            ((BindingProvider) client).getRequestContext().put(Header.HEADER_LIST, List.of(soapHeader));

        } catch (Exception e) {
            throw new RuntimeException("Error al configurar el header SOAP", e);
        }

        return client;
    }
}
