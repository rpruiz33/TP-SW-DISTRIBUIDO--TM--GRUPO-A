package com.empuje.web_service.controllers.restsoap;

import com.empuje.web_service.dto.OngDTO;
import com.empuje.web_service.dto.PresidentDTO;
import com.empuje.web_service.services.soap.impl.OngServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ong")
@RequiredArgsConstructor
public class AsociationController {

    private final OngServiceImpl ongService;


    @PostMapping("/presidents")
    public List<PresidentDTO> getPresidentList(@RequestBody List<String> orgIds) {

        return ongService.getPresidentList(orgIds);
    }

    @PostMapping("/ongs")
    public List<OngDTO> getOngList(@RequestBody List<String> orgIds) {

        return ongService.getOngList(orgIds);
    }
}
