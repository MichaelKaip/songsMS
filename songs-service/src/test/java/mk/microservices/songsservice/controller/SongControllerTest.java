package mk.microservices.songsservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import mk.microservices.songsservice.dao.SongDAO;
import mk.microservices.songsservice.model.Song;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.assertj.core.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SongControllerTest {

    private MockMvc mockMvc;
    private SongDAO songDAO;
    private Song song1_valid;
    private Song song2_valid;
    private Song song3_noTitle;
    private List<Song> listOfSongs_valid;

    @BeforeEach
    public void setUp() {
        songDAO = Mockito.mock(SongDAO.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new SongController(songDAO)).build();

        song1_valid = Song.builder()
                .id(1)
                .title("Test Title 1")
                .artist("Test Artist 1")
                .label("Test Label 1")
                .released(1910)
                .build();

        song2_valid = Song.builder()
                .id(2)
                .title("Test Title 2")
                .artist("Test artist 2")
                .label("Test Label 2")
                .released(1920)
                .build();

        song3_noTitle = Song.builder()
                .id(3)
                .title(" ")
                .artist("Test artist 2")
                .label("Test Label 2")
                .released(1920)
                .build();

        listOfSongs_valid = List.of(song1_valid, song2_valid);
    }

    @Test
    void getSongJSON_success() {
        when(songDAO.getSongById(1)).thenReturn(song1_valid);
        try {
            mockMvc.perform(get("/songs/1").header("Authorization","randomstring")).andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.title").value("Test Title 1"))
                    .andExpect(jsonPath("$.label").value("Test Label 1"))
                    .andExpect(jsonPath("$.artist").value("Test Artist 1"))
                    .andExpect(jsonPath("$.released").value(1910));
        } catch (Exception e) {
            e.printStackTrace();
            fail("Unexpected Exception", e);
        }
    }

    @Test
    void getSongJSON_Not_Existing(){
        when(songDAO.getSongById(anyInt())).thenReturn(null);
        try {
            mockMvc.perform(get("/songs/1").header("Authorization","randomstring")).andExpect(status().isNotFound());
        }catch(Exception e){
            e.printStackTrace();
            fail("Unexpected Exception", e);
        }
    }

    @Test
    void getSongJSON_Wrong_Id_Format(){
        when(songDAO.getSongById(anyInt())).thenReturn(null);
        try {
            mockMvc.perform(get("/songs/aaaa").header("Authorization","randomstring")).andExpect(status().isBadRequest());
        }catch(Exception e){
            e.printStackTrace();
            fail("Unexpected Exception", e);
        }
    }


    @Test
    void getSongXML_success() {
        when(songDAO.getSongById(1)).thenReturn(song1_valid);
        XmlMapper xmlMapper=new XmlMapper();
        try {
            mockMvc.perform(get("/songs/1").accept(MediaType.APPLICATION_XML).header("Authorization","randomstring")).andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_XML))
                    .andExpect(content().xml(xmlMapper.writeValueAsString(song1_valid)));
        }catch(Exception e){
            e.printStackTrace();
            fail("Unexpected Exception", e);
        }
    }

    @Test
    void getSongXML_Not_Existing() {
        when(songDAO.getSongById(anyInt())).thenReturn(null);
        try {
            mockMvc.perform(get("/songs/1").accept(MediaType.APPLICATION_XML).header("Authorization","randomstring")).andExpect(status().isNotFound());
        }catch(Exception e){
            e.printStackTrace();
            fail("Unexpected Exception", e);
        }
    }

    @Test
    void getSongXML_Wrong_Id_Format(){
        when(songDAO.getSongById(anyInt())).thenReturn(null);
        try {
            mockMvc.perform(get("/songs/aaaa").accept(MediaType.APPLICATION_XML).header("Authorization","randomstring")).andExpect(status().isBadRequest());
        }catch(Exception e){
            e.printStackTrace();
            fail("Unexpected Exception", e);
        }
    }

    //Problem <List12> vs <List>
    @Test
    void getAllSongsXML_success(){
        when(songDAO.getAllSongs()).thenReturn(listOfSongs_valid);
        try{
            XmlMapper xmlMapper = new XmlMapper();
            String expectedString = xmlMapper.writeValueAsString(listOfSongs_valid);
            String expected = expectedString.replaceAll("12","");
            mockMvc.perform(get("/songs").accept(MediaType.APPLICATION_XML).header("Authorization","randomstring")).andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_XML))
                    .andExpect(content().string(expected));
            verify(songDAO).getAllSongs();
        }catch(Exception e){
            e.printStackTrace();
            fail("Unexpected Exception", e);
        }
    }

    @Test
    void getAllSongsXML_Wrong_Url(){
        try{
            mockMvc.perform(get("/songsojcpwoej").accept(MediaType.APPLICATION_XML).header("Authorization","randomstring")).andExpect(status().isNotFound());
            verify(songDAO, Mockito.never()).getAllSongs();
        }catch(Exception e){
            e.printStackTrace();
            fail("Unexpected Exception", e);
        }
    }

    @Test
    void getAllSongsJSON_success() {
        when(songDAO.getAllSongs()).thenReturn(listOfSongs_valid);
        try{
            ObjectMapper objectMapper=new ObjectMapper();
            String expected=objectMapper.writeValueAsString(listOfSongs_valid);
            mockMvc.perform(get("/songs").accept(MediaType.APPLICATION_JSON).header("Authorization","randomstring")).andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(content().json(expected));
            verify(songDAO).getAllSongs();
        }catch(Exception e){
            e.printStackTrace();
            fail("Unexpected Exception", e);
        }
    }

    @Test
    void postSong_success() {
        when(songDAO.addSong(any(Song.class))).thenReturn(1);
        try{
            ObjectMapper objectMapper=new ObjectMapper();
            String songJson = objectMapper.writeValueAsString(song1_valid);
            mockMvc.perform(post("/songs").content(songJson).contentType(MediaType.APPLICATION_JSON).header("Authorization", "randomstring"))
                    .andExpect(header().string("Location", "/localhost:8080/rest/songs/1"))
                    .andExpect(status().isCreated());
        }catch(Exception e){
            e.printStackTrace();
            fail("Unexpected Exception", e);
        }
    }

    @Test
    void postSong_says_JSON_but_is_XML_should_return_400error(){
        when(songDAO.addSong(any(Song.class))).thenReturn(1);
        try{
            ObjectMapper objectMapper = new XmlMapper();
            String songXml = objectMapper.writeValueAsString(song1_valid);
            mockMvc.perform(post("/songs").content(songXml).contentType(MediaType.APPLICATION_JSON).header("Authorization","randomstring"))
                    .andExpect(status().isBadRequest());
            verify(songDAO, Mockito.never()).addSong(any(Song.class));
        }catch(Exception e){
            e.printStackTrace();
            fail("Unexpected Exception", e);
        }
    }

    @Test
    void postSong_empty_title_not_accepted(){
        when(songDAO.updateSong(any(Song.class))).thenReturn(true);
        try{
            ObjectMapper objectMapper=new ObjectMapper();
            String songJson = objectMapper.writeValueAsString(song3_noTitle);
            mockMvc.perform(post("/songs").content(songJson).contentType(MediaType.APPLICATION_JSON).header("Authorization","randomstring"))
                    .andExpect(status().isBadRequest());
            verify(songDAO, Mockito.never()).addSong(any(Song.class));
        }catch(Exception e){
            e.printStackTrace();
            fail("Unexpected Exception", e);
        }
    }

    @Test
    void postSong_says_XML_should_return_415error(){
        when(songDAO.addSong(any(Song.class))).thenReturn(1);
        try{
            ObjectMapper objectMapper = new XmlMapper();
            String songXml = objectMapper.writeValueAsString(song1_valid);
            mockMvc.perform(post("/songs").content(songXml).contentType(MediaType.APPLICATION_XML).header("Authorization","randomstring"))
                    .andExpect(status().isUnsupportedMediaType());
            verify(songDAO, Mockito.never()).addSong(any(Song.class));
        }catch(Exception e){
            e.printStackTrace();
            fail("Unexpected Exception", e);
        }
    }


    @Test
    void updateSong_success() {
        when(songDAO.updateSong(any(Song.class))).thenReturn(true);
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            String songJson = objectMapper.writeValueAsString(song1_valid);
            mockMvc.perform(put("/songs/1").content(songJson).contentType(MediaType.APPLICATION_JSON).header("Authorization","randomstring"))
                    .andExpect(status().isNoContent());
            verify(songDAO).updateSong(any(Song.class));
        }catch(Exception e){
            e.printStackTrace();
            fail("Unexpected Exception", e);
        }
    }

    @Test
    void updateSong_wrong_content_should_return_error(){
        when(songDAO.updateSong(any(Song.class))).thenReturn(true);
        try{
            ObjectMapper objectMapper = new XmlMapper();
            String songXml = objectMapper.writeValueAsString(song1_valid);
            mockMvc.perform(put("/songs/1").content(songXml).contentType(MediaType.APPLICATION_JSON).header("Authorization","randomstring"))
                    .andExpect(status().isBadRequest());
            verify(songDAO, Mockito.never()).updateSong(any(Song.class));
        }catch(Exception e){
            e.printStackTrace();
            fail("Unexpected Exception", e);
        }
    }

    @Test
    void updateSong_empty_title_denied(){
        when(songDAO.updateSong(any(Song.class))).thenReturn(true);
        try{
            ObjectMapper objectMapper=new ObjectMapper();
            String songJson = objectMapper.writeValueAsString(song3_noTitle);
            mockMvc.perform(put("/songs/3").content(songJson).contentType(MediaType.APPLICATION_JSON).header("Authorization","randomstring"))
                    .andExpect(status().isNotAcceptable());
            verify(songDAO, Mockito.never()).updateSong(any(Song.class));
        }catch(Exception e){
            e.printStackTrace();
            fail("Unexpected Exception", e);
        }
    }

    @Test
    void updateSong_different_Ids_should_return_error(){
        when(songDAO.updateSong(any(Song.class))).thenReturn(true);
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            String songJson = objectMapper.writeValueAsString(song1_valid);
            mockMvc.perform(put("/songs/3").content(songJson).contentType(MediaType.APPLICATION_JSON).header("Authorization","randomstring"))
                    .andExpect(status().isNotAcceptable());
            verify(songDAO, Mockito.never()).updateSong(any(Song.class));
        }catch(Exception e){
            e.printStackTrace();
            fail("Unexpected Exception", e);
        }
    }

    @Test
    void deleteSong_success() {
        when(songDAO.deleteSong(anyInt())).thenReturn(true);
        try{
            mockMvc.perform(delete("/songs/1").header("Authorization","randomstring"))
                    .andExpect(status().isNoContent());
        }catch(Exception e){
            e.printStackTrace();
            fail("Unexpected Exception", e);
        }
    }

    @Test
    void deleteSong_Song_doenst_exist_should_return_error(){
        when(songDAO.deleteSong(anyInt())).thenReturn(false);
        try{
            mockMvc.perform(delete("/songs/100").header("Authorization","randomstring"))
                    .andExpect(status().isBadRequest());
        }catch(Exception e){
            e.printStackTrace();
            fail("Unexpected Exception", e);
        }
    }
}