    package com.jg.GPS.service;

    import com.jg.GPS.dto.POIDto;
    import org.springframework.http.ResponseEntity;

    public interface POIService {
        public ResponseEntity insertPOI(POIDto poiDto);
        public ResponseEntity getAllPois();
        public ResponseEntity getAllPoisInRange(double x, double y, double dmax);
    }
