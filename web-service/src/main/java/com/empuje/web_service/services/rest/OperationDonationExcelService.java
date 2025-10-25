package com.empuje.web_service.services.rest;

import java.io.ByteArrayInputStream;

public interface OperationDonationExcelService {
    ByteArrayInputStream generateExcelReport(boolean isExternal);
}
