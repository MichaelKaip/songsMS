package mk.microservices.songsinfoservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import mk.microservices.songsinfoservice.domain.SongInfo;
import mk.microservices.songsinfoservice.services.SongInfoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SongsInfoController.class)
class SongsInfoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    private SongInfoService songInfoService;

    @Test
    void getSongInfoById_success() throws Exception {
        when(songInfoService.getSongInfoBySongId(anyInt())).thenReturn(getValidSongInfo());

        mockMvc.perform(get("/songsinfo/1"))
                .andDo(print())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.songId").value(1))
                .andExpect(jsonPath("$.songName").value("TestSongValid"))
                .andExpect(jsonPath("$.description").value("TestSongValid_desc"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }


    @Test
    void getSongInfoById_does_not_exist() throws Exception {
        when(songInfoService.getSongInfoBySongId(anyInt())).thenReturn(null);
        mockMvc.perform(get("/xyz"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllSongInfos_success() throws Exception {
        List<SongInfo> listOfValidSongInfo = getListOfValidSongInfo();
        when(songInfoService.getAllSongInfos()).thenReturn(listOfValidSongInfo);
        String expectedValues = objectMapper.writeValueAsString(listOfValidSongInfo);
        mockMvc.perform(get("/songsinfo"))
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedValues))
                .andExpect(status().isOk());
    }

    @Test
    void postNewSongInfo_success() throws Exception {
        when(songInfoService.saveNewSongInfo(any(SongInfo.class))).thenReturn(1);
        String songInfoJson = objectMapper. writeValueAsString(getValidSongInfo());
        mockMvc.perform(post("/songsinfo")
                .content(songInfoJson)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(header().string("Location","localhost:8083/songsinfo/1"))
                .andExpect(status().isOk());
    }

    @Test
    void postNewSongInfo_empty_songName_is_BadRequest() throws Exception {
        when(songInfoService.saveNewSongInfo(any(SongInfo.class))).thenReturn(1);
        String songInfoJson = objectMapper. writeValueAsString(getInvalidSongInfo());
        mockMvc.perform(post("/songsinfo")
                .content(songInfoJson)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
        verify(songInfoService, never()).saveNewSongInfo(any(SongInfo.class));
    }

    @Test
    void updateSongInfoById_success() throws Exception {
        when(songInfoService.getSongInfoBySongId(anyInt())).thenReturn(getValidSongInfo());
        when(songInfoService.updateSongInfoBySongId(anyInt(), any(SongInfo.class))).thenReturn(1);
        String songInfoJson = objectMapper.writeValueAsString(getValidSongInfo());
        mockMvc.perform(put("/songsinfo/1")
                .content(songInfoJson)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void updateSongInfoById_contentIsXml_should_return_BadRequest() throws Exception {
        when(songInfoService.updateSongInfoBySongId(anyInt(), any(SongInfo.class))).thenReturn(1);
        String songInfoJson = objectMapper.writeValueAsString(getInvalidSongInfo());
        mockMvc.perform(put("/songsinfo/1")
                .content(songInfoJson)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
        verify(songInfoService, never()).updateSongInfoBySongId(anyInt(), any(SongInfo.class));
    }

    @Test
    void deleteSongById_success() throws Exception {
        when(songInfoService.deleteSongInfoBySongId(anyInt())).thenReturn(1);
        mockMvc.perform(delete("/songsinfo/1"))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteSongInfo_doesntExist_should_return_BadRequest() throws Exception {
        when(songInfoService.deleteSongInfoBySongId(anyInt())).thenReturn(-1);
        mockMvc.perform(delete("/songsinfo/1"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }


    private SongInfo getValidSongInfo() {
        return SongInfo.builder()
                .id("1")
                .songId(1)
                .songName("TestSongValid")
                .description("TestSongValid_desc")
                .build();
    }

    private SongInfo getInvalidSongInfo() {
        return SongInfo.builder()
                .id("1")
                .songId(1)
                .description("TestSongValid_desc")
                .build();
    }

    private List<SongInfo> getListOfValidSongInfo() {
        return List.of(
                SongInfo.builder()
                        .id("1")
                        .songId(1)
                        .songName("TestSongValid_1")
                        .description("TestSongValid_desc_1")
                        .build(),
                SongInfo.builder()
                        .id("1")
                        .songId(2)
                        .songName("TestSongValid_2")
                        .description("TestSongValid_desc_1")
                        .build()
        );
    }

}