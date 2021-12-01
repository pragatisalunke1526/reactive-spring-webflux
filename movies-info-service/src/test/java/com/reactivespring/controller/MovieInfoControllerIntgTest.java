package com.reactivespring.controller;

import com.reactivespring.domain.MovieInfo;
import com.reactivespring.repository.MovieInfoRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
class MovieInfoControllerIntgTest {

    @Autowired
    MovieInfoRepository movieInfoRepository;

    @Autowired
    WebTestClient webTestClient;

    static String MOVIE_INFO_URL="/api//movieinfos";

    @BeforeEach
    void setUp() {
        var movieinfos = List.of(new MovieInfo(null, "Batman Begins",
                        2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15")),
                new MovieInfo(null, "The Dark Knight",
                        2008, List.of("Christian Bale", "HeathLedger"), LocalDate.parse("2008-07-18")),
                new MovieInfo("abc", "Dark Knight Rises",
                        2012, List.of("Christian Bale", "Tom Hardy"), LocalDate.parse("2012-07-20")));

        movieInfoRepository.saveAll(movieinfos)
                .blockLast();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void addMovieInfo() {
 var movieInfo = new MovieInfo("10", "Sherrsha",
                 2005, List.of("Shidhart Malhotra", "Kiara advani"), LocalDate.parse("2021-06-15"));

        webTestClient
                .post()
                .uri(MOVIE_INFO_URL)
                .bodyValue(movieInfo)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(MovieInfo.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    var savedMovieInfo= movieInfoEntityExchangeResult.getResponseBody();
                    assert savedMovieInfo != null;
                    assert savedMovieInfo.getMovieInfoId() !=null;

                });
    }

    @Test
    void getAllMovieInfo() {
        webTestClient
                .get()
                .uri(MOVIE_INFO_URL)
                .exchange()
                .expectStatus()
                .isFound()
                .expectBodyList(MovieInfo.class)
                .hasSize(3);
    }

    @Test
    void getByIdMovieInfo() {

        var MovieInoId = "abc";
        webTestClient
                .get()
                .uri(MOVIE_INFO_URL+"/{id}",MovieInoId)
                .exchange()
                .expectStatus()
                .isFound()
                .expectBody()
                .jsonPath("$.name").isEqualTo("Dark Knight Rises");
//                .expectBody(MovieInfo.class)
//                .consumeWith(movieInfoEntityExchangeResult -> {
//                    var movieInfo=movieInfoEntityExchangeResult.getResponseBody();
//                    assertNotNull(movieInfo);
        //        });
    }

    @Test
    void updateMovieInfo() {

        var MovieInoId = "abc";

        var movieInfo = new MovieInfo(null, "Sherrsha",
                2005, List.of("Shidhart Malhotra", "Kiara advani"), LocalDate.parse("2021-06-15"));

        webTestClient
                .put()
                .uri(MOVIE_INFO_URL+"/{id}",MovieInoId)
                .bodyValue(movieInfo)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(MovieInfo.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    var updatedMovieInfo= movieInfoEntityExchangeResult.getResponseBody();
                    assert updatedMovieInfo != null;
                    assert updatedMovieInfo.getMovieInfoId() !=null;
                    assertEquals("Sherrsha",updatedMovieInfo.getName());
                });
    }


    @Test
    void deleteByIdMovieInfo(){
        var MovieInoId = "abc";

        webTestClient
                .delete()
                .uri(MOVIE_INFO_URL+"/{id}",MovieInoId)
                .exchange()
                .expectStatus()
                .isOk();

    }
}