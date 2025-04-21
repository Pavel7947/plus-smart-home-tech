package ru.yandex.practicum.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import ru.yandex.practicum.dto.ErrorResponse;
import ru.yandex.practicum.exception.LowQuantityException;
import ru.yandex.practicum.exception.NotFoundException;

@Slf4j
public class CustomFeignErrorDecoder implements ErrorDecoder {
    private final ErrorDecoder defaultDecoder = new Default();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Exception decode(String methodKey, Response response) {
        ErrorResponse body;
        try {
            body = objectMapper.readValue(response.body().asInputStream().readAllBytes(), ErrorResponse.class);
        } catch (Exception e) {
            log.debug("Получено исключение при декодировании тела ответа {}", e.getClass().getSimpleName(), e);
            return new RuntimeException();
        }
        if (response.status() == HttpStatus.NOT_FOUND.value()) {
            log.debug("Получен статус 404  с телом {}", body);
            return new NotFoundException(body.message());
        } else if (response.status() == HttpStatus.BAD_REQUEST.value()) {
            log.debug("Получен статус 400 с телом {}", body);
            return new LowQuantityException(body.message());
        }
        return defaultDecoder.decode(methodKey, response);
    }


}
