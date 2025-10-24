package com.empuje.web_service.services;

import java.io.ByteArrayInputStream;

public interface OperationDonationExcelService {
    ByteArrayInputStream generateExcelReport(boolean isExternal);
}
