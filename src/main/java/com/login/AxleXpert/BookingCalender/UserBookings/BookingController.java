package com.login.AxleXpert.BookingCalender.UserBookings;

import java.util.List;

import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    @GetMapping("/all")
    public ResponseEntity<List<BookingDTO>> getDummyBookings(@RequestParam(required = false) Integer count) {
        // attempt to load from resource file first
        List<BookingDTO> fromFile = tryLoadFromResource();
        if (fromFile == null) {
            // resource missing or parse error -> return empty list
            return ResponseEntity.ok(List.of());
        }
        if (count != null) {
            int min = 5;
            int max = 15;
            int n = Math.max(min, Math.min(max, count));
            return ResponseEntity.ok(fromFile.subList(0, Math.min(fromFile.size(), n)));
        }
        return ResponseEntity.ok(fromFile);
    }

        private List<BookingDTO> tryLoadFromResource() {
                try {
                        ClassPathResource res = new ClassPathResource("dummy-bookings.json");
                        if (!res.exists()) return null;
                        ObjectMapper mapper = new ObjectMapper();
                        // configure mapper if needed for Java Time parsing
                        mapper.findAndRegisterModules();
                        return mapper.readValue(res.getInputStream(), new TypeReference<List<BookingDTO>>(){});
                } catch (IOException e) {
                    // ignore and fall back to generated data
                    return null;
                }
        }

    // (DTO types moved to separate files)
}
