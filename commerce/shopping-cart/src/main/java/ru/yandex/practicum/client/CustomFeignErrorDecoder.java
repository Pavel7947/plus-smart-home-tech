package ru.yandex.practicum.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
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
        if (response.status() == 404) {
            log.debug("Получен статус 404 от сервиса склада с телом {}", body);
            return new NotFoundException(body.message());
        } else if (response.status() == 400) {
            log.debug("Получен статус 400 от сервиса склада с телом {}", body);
            return new LowQuantityException(body.message());
        }
        return defaultDecoder.decode(methodKey, response);
    }


}
