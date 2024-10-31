package com.example.musicFinder.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.musicFinder.MusicFinderController;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

@ExtendWith(SpringExtension.class)
@WebMvcTest(MusicFinderController.class)

public class SongControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private MusicFinderController musicFinderController;

    @Test
    public void testFetchLyrics_ValidSong() throws Exception {
        String artist = "Adele";
        String song = "Hello";
        String apiUrl = "https://api.lyrics.ovh/v1/" + artist + "/" + song;

        // Mock valid response from Lyrics.ovh API
        String mockLyricsResponse = "{\"lyrics\":\"Hello, it's me\\nI was wondering if after all...\"}";
        when(restTemplate.getForObject(apiUrl, String.class)).thenReturn(mockLyricsResponse);

        mockMvc.perform(get("/song/{artist}/{name}", artist, song))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.song").value(song))
                .andExpect(jsonPath("$.artist").value(artist))
                .andExpect(jsonPath("$.youtubeSearch").value("https://www.youtube.com/results?search_query=Adele+Hello"))
                .andExpect(jsonPath("$.lyrics").value("Hello, it's me\\I was wondering if after all these years you'd like to meet\\To go over everything\\They say that time's supposed to heal ya\\But I ain't done much healing\\Hello, can you hear me?\\I'm in California dreaming about who we used to be\\When we were younger and free\\I've forgotten how it felt before the world fell at our feet\\There's such a difference between us\\And a million miles\\Hello from the other side\\I must've called a thousand times \\To tell you I'm sorry\\For everything that I've done\\But when I call you never \\Seem to be home\\Hello from the outside\\At least I can say that I've tried \\To tell you I'm sorry\\For breaking your heart\\But it don't matter, it clearly \\Doesn't tear you apart anymore\\Hello, how are you?\\It's so typical of me to talk about myself, I'm sorry \\I hope that you're well\\Did you ever make it out of that town\\Where nothing ever happened?\\It's no secret\\That the both of us \\Are running out of time\\So hello from the other side (other side)\\I must've called a thousand times (thousand times)\\To tell you I'm sorry\\For everything that I've done\\But when I call you never \\Seem to be home\\Hello from the outside (outside)\\At least I can say that I've tried (I've tried)\\To tell you I'm sorry\\For breaking your heart\\But it don't matter, it clearly \\Doesn't tear you apart anymore\\Ooooohh, anymore\\Ooooohh, anymore\\Ooooohh, anymore\\Anymore\\Hello from the other side (other side)\\I must've called a thousand times (thousand times)\\To tell you I'm sorry\\For everything that I've done\\But when I call you never \\Seem to be home\\Hello from the outside (outside)\\At least I can say that I've tried (I've tried)\\To tell you I'm sorry\\For breaking your heart\\But it don't matter, it clearly \\Doesn't tear you apart anymore"));
    }

    @Test
    public void testFetchLyrics_InvalidSong() throws Exception {
        String artist = "UnknownArtist";
        String song = "UnknownSong";
        String apiUrl = "https://api.lyrics.ovh/v1/" + artist + "/" + song;

        // Mock invalid response to simulate an exception for an unknown song
        when(restTemplate.getForObject(apiUrl, String.class)).thenThrow(new RuntimeException("Lyrics not found"));

        mockMvc.perform(get("/song/{artist}/{name}", artist, song))
                .andExpect(status().isOk())  // The controller still returns 200 for a not-found scenario
                .andExpect(jsonPath("$.lyrics").value("{\"error\":\"Lyrics not found\"}"))
                .andExpect(jsonPath("$.song").value(song))
                .andExpect(jsonPath("$.artist").value(artist))
                .andExpect(jsonPath("$.youtubeSearch").value("https://www.youtube.com/results?search_query=UnknownArtist+UnknownSong"));
    }
}
