package cl.bilix.scrapper.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.bilix.scrapper.dto.InputDto;
import cl.bilix.scrapper.helpers.Lock;
import cl.bilix.scrapper.helpers.WebScrapperException;
import cl.bilix.scrapper.helpers.WebScrapperMessage;
import cl.bilix.scrapper.helpers.WebScrapperResult;
import cl.bilix.scrapper.properties.Properties;
import cl.bilix.scrapper.service.Execute;

@RestController
@RequestMapping("api")
public class ApiController {

    private final SimpMessagingTemplate template;
    // Properties
    private final List<Properties> properties;
    // Mutex para la sección critica, estatico para cualquier clase lo lea
    private static final HashMap<String, Lock> locker = new HashMap<>();

    public static Map<Object, Object> getLockerMessage() {
        return locker.entrySet()
                .stream()
                .collect(Collectors.toUnmodifiableMap(
                        k -> k.getKey(),
                        v -> v.getValue().isLockedWithId()));
    }

    public ApiController(List<Properties> properties, SimpMessagingTemplate template) {
        this.template = template;
        this.properties = properties;
        // Inicializa los mutex correspondientes
        properties.forEach(property -> locker.put(property.getMap(), new Lock()));
    }

    @PostMapping("set")
    public ResponseEntity<Object> setData(@RequestBody InputDto input) {
        Optional<Properties> query = properties.stream().filter(x -> x.getMap().equals(input.getTerminal()))
                .findFirst();

        Properties properties = query.get();
        Lock lock = locker.get(properties.getMap());

        if (query.isEmpty())
            return new ResponseEntity<Object>("No existe terminal " + input.getTerminal(), HttpStatus.BAD_REQUEST);

        if (lock.getLockedIds().contains(input.getId())) {
            return new ResponseEntity<Object>("Id " + input.getId() + "ya esta siendo procesada", HttpStatus.OK);
        }

        try (Lock lockedResource = lock.open(input.getId())) {
            template.convertAndSend("/topic/notification", getLockerMessage());
            Execute.apply(properties);
            return new ResponseEntity<Object>(new WebScrapperResult(WebScrapperMessage.SUCCESS),
                    HttpStatus.CREATED);
        } catch (WebScrapperException e) {
            return new ResponseEntity<Object>(e.getErrorResult(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<Object>(
                    new WebScrapperResult(WebScrapperMessage.ERROR, e.getCause().getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        } finally {
            template.convertAndSend("/topic/notification", getLockerMessage());
        }
    }
}
