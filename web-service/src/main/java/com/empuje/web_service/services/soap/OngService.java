package com.empuje.web_service.services.soap;

import com.empuje.web_service.dto.OngDTO;
import com.empuje.web_service.dto.PresidentDTO;

import java.util.List;

public interface OngService {

    List<PresidentDTO> getPresidentList(List<String> ids);
    List<OngDTO> getOngList(List<String> ids);
}
