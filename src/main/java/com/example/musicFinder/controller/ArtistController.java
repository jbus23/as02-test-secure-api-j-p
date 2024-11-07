package com.example.musicFinder;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@RestController
public class ArtistController {

    private final RestTemplate restTemplate = new RestTemplate();

    @GetMapping("/artist/{name}")
    public ResponseEntity<String> getArtistInfo(@PathVariable String name) {
        String url = "https://en.wikipedia.org/api/rest_v1/page/summary/" + name;
        
        try {
            // Send a GET request to the Wikipedia API
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            return ResponseEntity.ok(response.getBody());
        } catch (HttpClientErrorException.NotFound e) {
            // Return a 404 error if the artist is not found
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("{\"error\":\"Artist not found\"}");
        } catch (Exception e) {
            // Handle any other errors that may occur
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\":\"Unable to fetch artist information\"}");
        }
    }
}
