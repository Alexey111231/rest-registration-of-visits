package ru.vk.sladkiipirojok.visits.controller;

import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import ru.vk.sladkiipirojok.visits.service.TimeService;
import ru.vk.sladkiipirojok.visits.service.model.Link;
import ru.vk.sladkiipirojok.visits.service.repository.LinkRepository;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties")
class VisitsControllerIT {
    //TEST CONSTANTS
    private final String FIRST_DOMAIN_IN_DB = "kek.ru";
    private final String SECOND_DOMAIN_IN_DB = "test.ru";
    private final String JSON_STATUS_PATH = "$.status";
    private final String JSON_DOMAIN_PATH = "$.domains";
    private final String OK_STATUS = "ok";
    private final String GET_URL = "/visited_domains?from=%s&to=%s";
    private final String POST_URL = "/visited_links";
    private final String JSON_TWO_ARGS = "{\"links\": [\"%s\",\"%s\"]}";
    private final String FIRST_URL = "first.ru";
    private final String FIRST_SAVE_URL = "firstSave.ru";
    private final String SECOND_URL = "second.ru";
    private final String SECOND_SAVE_URL = "secondSave.ru";
    //END CONSTANTS

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LinkRepository linkRepository;

    @Autowired
    private TimeService timeService;

    @BeforeEach
    public void setRedisData() {
        linkRepository.saveAll(List.of(new Link(FIRST_DOMAIN_IN_DB, 4122L),
                new Link(SECOND_DOMAIN_IN_DB, 412200L)));
    }

    @AfterEach
    public void clearRedis() {
        linkRepository.deleteAll();
    }

    @Test
    @SneakyThrows
    void Get_From4100To420000_ReturnedKekAndTest() {
        mockMvc.perform(get(String.format(GET_URL, 4100, 420000)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath(JSON_STATUS_PATH, is(OK_STATUS)))
                .andExpect(jsonPath(JSON_DOMAIN_PATH).isArray())
                .andExpect(jsonPath(JSON_DOMAIN_PATH, hasSize(2)))
                .andExpect(jsonPath(JSON_DOMAIN_PATH, hasItem(FIRST_DOMAIN_IN_DB)))
                .andExpect(jsonPath(JSON_DOMAIN_PATH, hasItem(SECOND_DOMAIN_IN_DB)));
    }

    @Test
    @SneakyThrows
    void Get_From0To5000_ReturnedKek() {
        mockMvc.perform(get(String.format(GET_URL, 0, 5000)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath(JSON_STATUS_PATH, is(OK_STATUS)))
                .andExpect(jsonPath(JSON_DOMAIN_PATH).isArray())
                .andExpect(jsonPath(JSON_DOMAIN_PATH, hasSize(1)))
                .andExpect(jsonPath(JSON_DOMAIN_PATH, hasItem(FIRST_DOMAIN_IN_DB)));
    }

    @Test
    @SneakyThrows
    void Get_From0To100_ReturnedEmptyList() {
        mockMvc.perform(get(String.format(GET_URL, 0, 100)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath(JSON_STATUS_PATH, is(OK_STATUS)))
                .andExpect(jsonPath(JSON_DOMAIN_PATH).isArray())
                .andExpect(jsonPath(JSON_DOMAIN_PATH, hasSize(0)));
    }

    @Test
    @SneakyThrows
    void GetAndPost_SaveTwoLinksWithEqualDomains_ReturnedOneDomain() {
        long dateBefore = timeService.getTime();

        mockMvc.perform(post(POST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format(JSON_TWO_ARGS, FIRST_DOMAIN_IN_DB, FIRST_DOMAIN_IN_DB + "?q=421")))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath(JSON_STATUS_PATH, is(OK_STATUS)));

        long dateAfter = timeService.getTime();

        mockMvc.perform(get(String.format(GET_URL, dateBefore, dateAfter)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath(JSON_STATUS_PATH, is(OK_STATUS)))
                .andExpect(jsonPath(JSON_DOMAIN_PATH).isArray())
                .andExpect(jsonPath(JSON_DOMAIN_PATH, hasSize(1)))
                .andExpect(jsonPath(JSON_DOMAIN_PATH, hasItem(FIRST_DOMAIN_IN_DB)));
    }

    @Test
    @SneakyThrows
    void GetAndPost_TwoSaveGetTheFirstSaveData_ReturnedFirstSaveData() {
        long dateBefore = timeService.getTime();

        mockMvc.perform(post(POST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format(JSON_TWO_ARGS, FIRST_URL, FIRST_SAVE_URL)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath(JSON_STATUS_PATH, is(OK_STATUS)));

        long dateAfter = timeService.getTime();

        mockMvc.perform(post(POST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format(JSON_TWO_ARGS, SECOND_URL, SECOND_SAVE_URL)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath(JSON_STATUS_PATH, is(OK_STATUS)));

        mockMvc.perform(get(String.format(GET_URL, dateBefore, dateAfter)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath(JSON_STATUS_PATH, is(OK_STATUS)))
                .andExpect(jsonPath(JSON_DOMAIN_PATH).isArray())
                .andExpect(jsonPath(JSON_DOMAIN_PATH, hasSize(2)))
                .andExpect(jsonPath(JSON_DOMAIN_PATH, hasItem(FIRST_URL)))
                .andExpect(jsonPath(JSON_DOMAIN_PATH, hasItem(FIRST_SAVE_URL)));
    }

    @Test
    @SneakyThrows
    void GetAndPost_TwoSaveGetTheFirstSaveData_ReturnedSecondSaveData() {
        mockMvc.perform(post(POST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format(JSON_TWO_ARGS, FIRST_URL, FIRST_SAVE_URL)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath(JSON_STATUS_PATH, is(OK_STATUS)));

        long dateBefore = timeService.getTime();

        mockMvc.perform(post(POST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format(JSON_TWO_ARGS, SECOND_URL, SECOND_SAVE_URL)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath(JSON_STATUS_PATH, is(OK_STATUS)));

        long dateAfter = timeService.getTime();

        mockMvc.perform(get(String.format(GET_URL, dateBefore, dateAfter)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath(JSON_STATUS_PATH, is(OK_STATUS)))
                .andExpect(jsonPath(JSON_DOMAIN_PATH).isArray())
                .andExpect(jsonPath(JSON_DOMAIN_PATH, hasSize(2)))
                .andExpect(jsonPath(JSON_DOMAIN_PATH, hasItem(SECOND_URL)))
                .andExpect(jsonPath(JSON_DOMAIN_PATH, hasItem(SECOND_SAVE_URL)));
    }

    @Test
    @SneakyThrows
    void GetAndPost_TwoSaveGetAllDataFromDB_ReturnedAllDataFromDB() {
        mockMvc.perform(post(POST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format(JSON_TWO_ARGS, FIRST_URL, FIRST_SAVE_URL)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath(JSON_STATUS_PATH, is(OK_STATUS)));

        mockMvc.perform(post(POST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format(JSON_TWO_ARGS, SECOND_URL, SECOND_SAVE_URL)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath(JSON_STATUS_PATH, is(OK_STATUS)));

        long dateAfter = timeService.getTime();

        mockMvc.perform(get(String.format(GET_URL, 0, dateAfter)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath(JSON_STATUS_PATH, is(OK_STATUS)))
                .andExpect(jsonPath(JSON_DOMAIN_PATH).isArray())
                .andExpect(jsonPath(JSON_DOMAIN_PATH, hasSize(6)))
                .andExpect(jsonPath(JSON_DOMAIN_PATH, hasItem(SECOND_URL)))
                .andExpect(jsonPath(JSON_DOMAIN_PATH, hasItem(SECOND_SAVE_URL)))
                .andExpect(jsonPath(JSON_DOMAIN_PATH, hasItem(FIRST_SAVE_URL)))
                .andExpect(jsonPath(JSON_DOMAIN_PATH, hasItem(FIRST_URL)))
                .andExpect(jsonPath(JSON_DOMAIN_PATH, hasItem(FIRST_DOMAIN_IN_DB)))
                .andExpect(jsonPath(JSON_DOMAIN_PATH, hasItem(SECOND_DOMAIN_IN_DB)));
    }

    @Test
    @SneakyThrows
    void PostAndGet_From0To5000_ReturnedKekAndTest() {
        mockMvc.perform(get(String.format(GET_URL, 0, 5000)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath(JSON_STATUS_PATH, is(OK_STATUS)))
                .andExpect(jsonPath(JSON_DOMAIN_PATH).isArray())
                .andExpect(jsonPath(JSON_DOMAIN_PATH, hasSize(1)))
                .andExpect(jsonPath(JSON_DOMAIN_PATH, hasItem(FIRST_DOMAIN_IN_DB)));
    }

    @Test
    @SneakyThrows
    void Post_AddOneLink_IsCreatedAndStatusOk() {
        mockMvc.perform(post(POST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"links\": [\"https://ya.ru\"]}"))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath(JSON_STATUS_PATH, is(OK_STATUS)));
    }

    @Test
    @SneakyThrows
    void Post_AddManyLinkWithSchemaAbsent_IsCreatedAndStatusOk() {
        mockMvc.perform(post(POST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "  \"links\": [\n" +
                        "  \"https://ya.ru\",\n" +
                        "  \"https://ya.ru?q=123\",\n" +
                        "  \"funbox.ru\",\n" +
                        "  \"https://stackoverflow.com/questions/11828270/how-to-exit-the-vim-editor\"\n" +
                        "  ]\n" +
                        "}"))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath(JSON_STATUS_PATH, is(OK_STATUS)));
    }

    @Test
    @SneakyThrows
    void Post_AddZeroLink_IsBadRequestAndStatusErrorJson() {
        mockMvc.perform(post(POST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"links\": []}"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(JSON_STATUS_PATH, is("Error json")));
    }

    @Test
    @SneakyThrows
    void Post_AddErrorLink_IsBadRequestAndStatusErrorJson() {
        mockMvc.perform(post(POST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"links\": [ \"adsasdads\"]}"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(JSON_STATUS_PATH, is("Error json")));
    }

    @Test
    @SneakyThrows
    void Post_EmptyJson_IsBadRequestAndStatusErrorJson() {
        mockMvc.perform(post(POST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(JSON_STATUS_PATH, is("Error json")));
    }

    @Test
    @SneakyThrows
    void Get_FromGreatestTo_IsBadRequestAndStatusStartGreaterEnd() {
        mockMvc.perform(get(String.format(GET_URL, 500, 200)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(JSON_STATUS_PATH, is("Start time is greater than end time")));
    }

    @Test
    @SneakyThrows
    void Get_NegativeParameter_IsBadRequestAndStatusErrorValue() {
        mockMvc.perform(get(String.format(GET_URL, -2, 200)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(JSON_STATUS_PATH, is("Error parameter value")));
    }

    @Test
    @SneakyThrows
    void Get_ParameterIsStringValue_IsBadRequestAndStatusErrorType() {
        mockMvc.perform(get(String.format(GET_URL, "afsafsa", 200)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(JSON_STATUS_PATH, is("Error type parameter")));
    }

    @Test
    @SneakyThrows
    void Get_NoParameter_IsBadRequestAndStatusWrongNumber() {
        mockMvc.perform(get(String.format(GET_URL, "", 200)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(JSON_STATUS_PATH, is("Wrong number of parameters")));
    }
}