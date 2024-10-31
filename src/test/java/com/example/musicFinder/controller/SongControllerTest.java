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
                .andExpect(jsonPath("$.lyrics").value("<Hello, it's me<br>I was wondering if after all these years you'd like to meet<br>To go over everything<br>They say that time's supposed to heal ya<br>But I ain't done much healing<br>Hello, can you hear me?<br>I'm in California dreaming about who we used to be<br>When we were younger and free<br>I've forgotten how it felt before the world fell at our feet<br>There's such a difference between us<br>And a million miles<br>Hello from the other side<br>I must've called a thousand times <br>To tell you I'm sorry<br>For everything that I've done<br>But when I call you never <br>Seem to be home<br>Hello from the outside<br>At least I can say that I've tried <br>To tell you I'm sorry<br>For breaking your heart<br>But it don't matter, it clearly <br>Doesn't tear you apart anymore<br>Hello, how are you?<br>It's so typical of me to talk about myself, I'm sorry <br>I hope that you're well<br>Did you ever make it out of that town<br>Where nothing ever happened?<br>It's no secret<br>That the both of us <br>Are running out of time<br>So hello from the other side (other side)<br>I must've called a thousand times (thousand times)<br>To tell you I'm sorry<br>For everything that I've done<br>But when I call you never <br>Seem to be home<br>Hello from the outside (outside)<br>At least I can say that I've tried (I've tried)<br>To tell you I'm sorry<br>For breaking your heart<br>But it don't matter, it clearly <br>Doesn't tear you apart anymore<br>Ooooohh, anymore<br>Ooooohh, anymore<br>Ooooohh, anymore<br>Anymore<br>Hello from the other side (other side)<br>I must've called a thousand times (thousand times)<br>To tell you I'm sorry<br>For everything that I've done<br>But when I call you never <br>Seem to be home<br>Hello from the outside (outside)<br>At least I can say that I've tried (I've tried)<br>To tell you I'm sorry<br>For breaking your heart<br>But it don't matter, it clearly <br>Doesn't tear you apart anymore"));
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
