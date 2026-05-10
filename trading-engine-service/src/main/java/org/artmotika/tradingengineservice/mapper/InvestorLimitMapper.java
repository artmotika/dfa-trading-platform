package org.artmotika.tradingengineservice.mapper;

import org.artmotika.common.dto.InvestorLimitDto;
import org.artmotika.tradingengineservice.model.InvestorLimit;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface InvestorLimitMapper {
    InvestorLimitDto toDto(InvestorLimit limit);
    InvestorLimit toEntity(InvestorLimitDto dto);
}
