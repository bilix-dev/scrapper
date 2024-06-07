package cl.bilix.scrapper.controller;

import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.bilix.scrapper.dto.Input;
import cl.bilix.scrapper.helpers.Lock;
import cl.bilix.scrapper.helpers.Locker;
import cl.bilix.scrapper.helpers.WebScrapperException;
import cl.bilix.scrapper.helpers.WebScrapperMessage;
import cl.bilix.scrapper.helpers.WebScrapperResult;
import cl.bilix.scrapper.properties.Properties;
import cl.bilix.scrapper.service.Execute;

@RestController
@RequestMapping("api")
public class ApiController {

    // Properties
    private final List<Properties> properties;
    // Mutex para la sección critica, singleton
    private final Locker locker = Locker.getInstance();

    public ApiController(List<Properties> properties, SimpMessagingTemplate template) {
        this.properties = properties;
        // Inicializa los mutex correspondientes
        properties.forEach(property -> locker.add(property.getMap(), template));
    }

    @PostMapping("set")
    public ResponseEntity<Object> setData(@RequestBody Input input) {
        Optional<Properties> query = properties.stream().filter(x -> x.getMap().equals(input.getTerminal()))
                .findFirst();

        if (query.isEmpty())
            return new ResponseEntity<Object>(new WebScrapperResult(WebScrapperMessage.UNINMPLEMENTED),
                    HttpStatus.BAD_REQUEST);

        Properties properties = query.get();
        if (locker.isLockedId(properties.getMap(), input.getPayload().getId())) {
            return new ResponseEntity<Object>(new WebScrapperResult(WebScrapperMessage.PROCCESSING),
                    HttpStatus.BAD_REQUEST);
        }

        try (Lock lockedResource = locker.lock(properties.getMap(), input.getPayload().getId())) {
            // Injectar Propiedades a input
            input.setUrl(properties.getUrl());
            input.setTimeout(properties.getTimeout());
            Execute.apply(input);
            return new ResponseEntity<Object>(new WebScrapperResult(WebScrapperMessage.SUCCESS),
                    HttpStatus.CREATED);
        } catch (WebScrapperException e) {
            return new ResponseEntity<Object>(e.getErrorResult(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<Object>(
                    new WebScrapperResult(WebScrapperMessage.ERROR, e),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
