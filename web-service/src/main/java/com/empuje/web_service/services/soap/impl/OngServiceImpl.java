package com.empuje.web_service.services.soap.impl;


import com.empuje.web_service.dto.OngDTO;
import com.empuje.web_service.dto.PresidentDTO;
import com.empuje.web_service.services.soap.OngService;
import org.empuje.web_service.wsdl.Application;
import org.empuje.web_service.wsdl.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ws.client.core.WebServiceTemplate;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OngServiceImpl implements OngService {

    private final Application soapClient;

    public OngServiceImpl(Application soapClient) {
        this.soapClient = soapClient;
    }

    public List<PresidentDTO> getPresidentList(List<String> orgIds) {
        // Crear el array de IDs
        StringArray ids = new StringArray();
        ids.getString().addAll(orgIds);

        PresidentTypeArray presidents = soapClient.listPresidents(ids);

        return presidents.getPresidentType()
                .stream().map(p -> new PresidentDTO(
                        p.getId().getValue().longValue(),
                        p.getName().getValue(),
                        p.getAddress().getValue(),
                        p.getPhone().getValue(),
                        p.getOrganizationId().getValue().longValue())
                )
                .toList();

    }

    public List<OngDTO> getOngList(List<String> orgIds) {
        // Crear el array de IDs
        StringArray ids = new StringArray();
        ids.getString().addAll(orgIds);

        OrganizationTypeArray orgs = soapClient.listAssociations(ids);

        return orgs.getOrganizationType()
                .stream().map(o -> new OngDTO(
                        o.getId().getValue().longValue(),
                        o.getName().getValue(),
                        o.getAddress().getValue(),
                        o.getPhone().getValue()
                ))
                .toList();

    }

}
