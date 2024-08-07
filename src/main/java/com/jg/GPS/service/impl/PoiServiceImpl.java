package com.jg.GPS.service.impl;

import com.jg.GPS.dto.POIDto;
import com.jg.GPS.model.POIModel;
import com.jg.GPS.repository.POIModelRepository;
import com.jg.GPS.service.POIService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PoiServiceImpl implements POIService {
    private final POIModelRepository poiRepository;

    public PoiServiceImpl(POIModelRepository poiRepository) {
        this.poiRepository = poiRepository;
    }


    @Override
    public ResponseEntity insertPOI(POIDto poiDto) {
        POIModel poi = new POIModel();
        poi.setName(poiDto.name());
        poi.setX(poiDto.x());
        poi.setY(poiDto.y());
        if (poi.getX() <= 0 || poi.getY() <= 0){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Coordenada não pode ser negativa!");
        }
        else if (poiRepository.findIfExists(poi.getY(), poi.getY())){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Já existe um POI com essas coordenadas");
        }
        poiRepository.save(poi);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body("POI Inserido com sucesso");
    }

    @Override
    public ResponseEntity getAllPois() {
        if (poiRepository.findAll().isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nenhum POI encontrado");
        }
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toDtoList(poiRepository.findAll()));
    }

    @Override
    public ResponseEntity getAllPoisInRange(double x, double y, double dmax) {
        if (poiRepository.findAll().isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nenhum POI encontrado");
        }
        List<POIModel> poisInRange = calculateDistance(poiRepository.findAll(),x,y,dmax);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toDtoList(poisInRange));
    }
    private List<POIDto> toDtoList(List<POIModel> poiModels){
        List<POIDto> dtoList = new ArrayList<>();
        for (POIModel poiModel : poiModels){
            POIDto dto = new POIDto(poiModel.getName(), poiModel.getX(), poiModel.getY());
            dtoList.add(dto);
        }
        return dtoList;
    }

    private List<POIModel> calculateDistance(List<POIModel> poiModels, double x, double y, double dmax){
        List<POIModel> poisInRange = new ArrayList<>();
        for (POIModel poiModel : poiModels){
            double xSquare = Math.pow(x - poiModel.getX(), 2);
            double ySquare = Math.pow(y - poiModel.getY(), 2);
            double distance = Math.sqrt(xSquare + ySquare);
            if (distance <= dmax){
                poisInRange.add(poiModel);
            }
        }
        return poisInRange;
    }
}
