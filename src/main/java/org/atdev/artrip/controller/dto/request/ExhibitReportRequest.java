package org.atdev.artrip.controller.dto.request;


import org.atdev.artrip.service.dto.command.ExhibitReportCommand;

public record ExhibitReportRequest(
        String title,
        String country
) {

    public ExhibitReportCommand toCommand() {
        return new ExhibitReportCommand(title, country);
    }
}
