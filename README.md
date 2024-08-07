# Pontos de Interesse por GPS

## Descrição do Projeto

Este projeto é um serviço para a empresa XY Inc., especializada na produção de receptores GPS. A plataforma desenvolvida fornece inteligência ao dispositivo GPS, permitindo a localização de pontos de interesse (POIs) por meio de serviços REST. Os principais recursos incluem cadastro de POIs, listagem de todos os POIs e busca de POIs por proximidade.

## Estrutura do Projeto

O projeto está organizado nas seguintes classes e pacotes:

### Pacote `com.jg.GPS.controller`

#### Classe `POIController`

```java
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

    @PostMapping("/insert")
    public ResponseEntity insertPoi(@RequestBody POIDto poiDto){
        return poiService.insertPOI(poiDto);
    }
}
```

### Pacote `com.jg.GPS.dto`

#### Classe `POIDto`

```java
public record POIDto(String name,
                     double x,
                     double y) {
}
```

#### Classe `SearchDto`

```java
public record SearchDto(double x,
                        double y,
                        double dmax) {
}
```

### Pacote `com.jg.GPS.model`

#### Classe `POIModel`

```java
@Entity
@Table(name = "tb_poi")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class POIModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(unique = true)
    private String name;

    private double x;

    private double y;
}
```

### Pacote `com.jg.GPS.repository`

#### Interface `POIModelRepository`

```java
public interface POIModelRepository extends JpaRepository<POIModel, Long> {
    @Query(nativeQuery = true, value = "SELECT * FROM tb_poi WHERE x = :x AND y = :y")
    Boolean findIfExists(@Param("x") double x, @Param("y") double y);
}
```

### Pacote `com.jg.GPS.service.impl`

#### Classe `PoiServiceImpl`

```java
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
        else if (poiRepository.findIfExists(poi.getX(), poi.getY())){
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
        List<POIModel> poisInRange = calculateDistance(poiRepository.findAll(), x, y, dmax);
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
```

### Pacote `com.jg.GPS.service`

#### Interface `POIService`

```java
public interface POIService {
    ResponseEntity insertPOI(POIDto poiDto);
    ResponseEntity getAllPois();
    ResponseEntity getAllPoisInRange(double x, double y, double dmax);
}
```

## Cálculo da Distância

Para calcular a distância entre dois pontos no plano cartesiano, foi utilizada a fórmula da distância euclidiana:

distância = √((x₂ - x₁)² + (y₂ - y₁)²)

Neste projeto, a função `calculateDistance` implementa essa fórmula para encontrar todos os POIs dentro de um raio especificado a partir de um ponto de referência.

```java
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
```

Esta função percorre todos os POIs armazenados, calcula a distância até o ponto de referência e adiciona à lista de POIs dentro do alcance (dmax) se a distância for menor ou igual ao valor especificado.

## Endpoints Disponíveis

- `GET /get-all`: Retorna todos os POIs cadastrados.
- `GET /search`: Retorna todos os POIs dentro do raio especificado a partir do ponto de referência.
- `POST /insert`: Insere um novo POI.

## Requisitos

- Java 11 ou superior
- Spring Boot 2.5 ou superior
- Banco de dados relacional (por exemplo, MySQL)

## Configuração do Banco de Dados

Configure as propriedades de conexão com o banco de dados no arquivo `application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/seubancodedados
spring.datasource.username=seuusuario
spring.datasource.password=suasenha
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL5Dialect
```

## Executando o Projeto

1. Clone o repositório.
2. Configure as propriedades do banco de dados.
3. Execute a aplicação usando seu IDE ou a linha de comando:

```bash
./mvnw spring-boot:run
```

A aplicação estará disponível em `http://localhost:8080`.

## Dependências Utilizadas

No arquivo `pom.xml`, as seguintes dependências foram utilizadas:

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-devtools</artifactId>
        <scope>runtime</scope>
        <optional>true</optional>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-docker-compose</artifactId>
        <scope>runtime</scope>
        <optional>true</optional>
    </dependency>
    <dependency>
        <groupId>org.postgresql</groupId>
        <artifactId>postgresql</artifactId>
        <scope>runtime</scope>
    </dependency>
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>


    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

### Importância do Spring Docker Compose

O Spring Docker Compose é uma ferramenta poderosa que simplifica o uso de contêineres Docker em aplicações Spring Boot. Ele facilita a definição e execução de ambientes complexos de aplicação com vários serviços dependentes, como bancos de dados, filas de mensagens e caches distribuídos.

#### Principais Benefícios:

1. **Facilidade de Configuração**: Permite a configuração fácil e rápida de ambientes de desenvolvimento, testes e produção usando arquivos `docker-compose.yml`.
2. **Portabilidade**: Garante que o ambiente de desenvolvimento seja idêntico ao de produção, reduzindo problemas de inconsistências entre ambientes.
3. **Isolamento de Serviços**: Cada serviço roda em um contêiner separado, garantindo isolamento e evitando conflitos de dependências.
4. **Escalabilidade**: Facilita a escalabilidade de serviços, permitindo o aumento ou diminuição do número de instâncias de contêineres conforme necessário.
5. **Manutenção Simplificada**: Facilita a manutenção e atualização de serviços individuais sem impactar todo o ambiente.

A integração do Spring Docker Compose no projeto permite uma gestão mais eficiente e robusta do ambiente de execução, essencial para aplicações modernas que exigem alta disponibilidade e escalabilidade.
