package com.jg.GPS;

import com.jg.GPS.dto.POIDto;
import com.jg.GPS.model.POIModel;
import com.jg.GPS.repository.POIModelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class GpsApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(GpsApplication.class, args);
	}
	@Autowired
	public POIModelRepository poiModelRepository;
	@Override
	public void run(String... args) throws Exception {
		List<POIModel> pois = Arrays.asList(
				createPOI("Lanchonete", 27, 12),
				createPOI("Posto", 31, 18),
				createPOI("Joalheria", 15, 12),
				createPOI("Floricultura", 19, 21),
				createPOI("Pub", 12, 8),
				createPOI("Supermercado", 23, 6),
				createPOI("Churrascaria", 28, 2)
		);

		for (POIModel poi : pois) {
			poiModelRepository.save(poi);
		}
	}
	private POIModel createPOI(String name, double x, double y) {
		POIModel poi = new POIModel();
		poi.setName(name);
		poi.setX(x);
		poi.setY(y);
		return poi;
	}
}

