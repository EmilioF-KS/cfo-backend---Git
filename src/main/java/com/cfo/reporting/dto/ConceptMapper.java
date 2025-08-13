package com.cfo.reporting.dto;

import com.cfo.reporting.model.Concept;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ConceptMapper {
    ConceptMapper INSTANCIA = Mappers.getMapper(ConceptMapper.class);
    ConceptDTO personaToPersonaDto(Concept concept);
}
