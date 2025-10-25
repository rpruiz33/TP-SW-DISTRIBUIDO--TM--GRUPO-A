package com.empuje.web_service.utils;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Setter;

@Setter
@XmlRootElement(name = "Auth", namespace = "auth.headers")
public class AuthHeader {

    private String grupo;
    private String clave;

    @XmlElement(name = "Grupo", namespace = "auth.headers")
    public String getGrupo() {
        return grupo;
    }

    @XmlElement(name = "Clave", namespace = "auth.headers")
    public String getClave() {
        return clave;
    }

}