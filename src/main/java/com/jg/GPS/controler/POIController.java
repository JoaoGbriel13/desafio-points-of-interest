package com.jg.GPS.controler;

import com.jg.GPS.dto.POIDto;
import com.jg.GPS.dto.SearchDto;
import com.jg.GPS.service.POIService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class POIController {
    private final POIService poiService;

    public POIController(POIService poiService) {
        this.poiService = poiService;
    }

    @GetMapping("/get-all")
    public ResponseEntity getAllPois(){
        return poiService.getAllPois();
    }
    @GetMapping("/search")
    public ResponseEntity getAllInRange(@RequestBody SearchDto searchDto){
        return poiService.getAllPoisInRange(searchDto.x(), searchDto.y(), searchDto.dmax());
    }
    @PostMapping("insert")
    public ResponseEntity insertPoi(@RequestBody POIDto poiDto){
        return poiService.insertPOI(poiDto);
    }

}
