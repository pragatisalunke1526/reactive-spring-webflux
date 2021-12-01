package com.reactivespring.controller;

import com.reactivespring.domain.MovieInfo;
import com.reactivespring.service.MovieInfoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.isA;
import static org.mockito.Mockito.when;

@WebFluxTest(MovieInfoController.class)
@AutoConfigureWebTestClient
class MovieInfoControllerUnitTest {


    @Autowired
    private WebTestClient webTestClient;


    @MockBean
    private MovieInfoService movieInfoServiceMock;

    static String MOVIE_INFO_URL="/api//movieinfos";


    @Test
    public void getAllMovieInfo(){
        var movieinfos = List.of(new MovieInfo(null, "Batman Begins",
                        2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15")),
                new MovieInfo(null, "The Dark Knight",
                        2008, List.of("Christian Bale", "HeathLedger"), LocalDate.parse("2008-07-18")));


        when(movieInfoServiceMock.getAllMovieInfo()).thenReturn(Flux.fromIterable(movieinfos));

        webTestClient
                .get()
                .uri(MOVIE_INFO_URL)
                .exchange()
                .expectStatus()
                .isFound()
                .expectBodyList(MovieInfo.class)
                .hasSize(2);


    }


    @Test
    public void getByIdMovieInfo(){


        var movieInoId = "abc";
        when(movieInfoServiceMock.getByIdMovieInfo(isA(String.class))).thenReturn(Mono.just(new MovieInfo("abc", "Dark Knight Rises",
                2012, List.of("Christian Bale", "Tom Hardy"), LocalDate.parse("2012-07-20"))));


        webTestClient
                .get()
                .uri(MOVIE_INFO_URL+"/{id}",movieInoId)
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
    public void addMovieInfo() {

        var movieInfo = new MovieInfo("10", "Sherrsha",
                2005, List.of("Shidhart Malhotra", "Kiara advani"), LocalDate.parse("2021-06-15"));

        when(movieInfoServiceMock.addMovieInfo(isA(MovieInfo.class))).thenReturn(Mono.just(new MovieInfo("mockId", "Sherrsha",
                2005, List.of("Shidhart Malhotra", "Kiara advani"), LocalDate.parse("2021-06-15"))));


        webTestClient
                .post()
                .uri(MOVIE_INFO_URL)
                .bodyValue(movieInfo)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(MovieInfo.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    var savedMovieInfo = movieInfoEntityExchangeResult.getResponseBody();
                    assert savedMovieInfo != null;
                    assert savedMovieInfo.getMovieInfoId() != null;
                    assertEquals("mockId",savedMovieInfo.getMovieInfoId());
                });
    }



    @Test
    void updateMovieInfo() {

        var id = "abc";
        var updatedMovieInfo = new MovieInfo("abc", "Dark Knight Rises 1",
                2013, List.of("Christian Bale1", "Tom Hardy1"), LocalDate.parse("2012-07-20"));

        when(movieInfoServiceMock.updateMovieInfo(isA(String.class),isA(MovieInfo.class)))
                .thenReturn(Mono.just(updatedMovieInfo));

        webTestClient
                .put()
                .uri(MOVIE_INFO_URL + "/{id}", id)
                .bodyValue(updatedMovieInfo)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(MovieInfo.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    var movieInfo = movieInfoEntityExchangeResult.getResponseBody();
                    assert movieInfo != null;
                    assertEquals("Dark Knight Rises 1", movieInfo.getName());
                });
    }




    @Test
    void deleteByIdMovieInfo() {
        var id = "abc";

        when(movieInfoServiceMock.deleteByIdMovieInfo(isA(String.class)))
                .thenReturn(Mono.empty());

        webTestClient
                .delete()
                .uri(MOVIE_INFO_URL + "/{id}", id)
                .exchange()
                .expectStatus()
                .isOk();
    }

    @Test
    void addNewMovieInfo_validation() {

        var movieInfo = new MovieInfo(null, "",
                -2005, List.of(""), LocalDate.parse("2005-06-15"));
        webTestClient
                .post()
                .uri(MOVIE_INFO_URL)
                .bodyValue(movieInfo)
                .exchange()
                .expectStatus()
                .isBadRequest()
                /*.expectBody(String.class)
                .consumeWith(entityExchangeResult -> {
                    var errorMessage = entityExchangeResult.getResponseBody();
                    System.out.println("errorMessage : " + errorMessage);
                    assert errorMessage!=null;
                });*/
                /*.expectBody()
                .jsonPath("$.error").isEqualTo("Bad Request");*/

                .expectBody(String.class)
                .consumeWith(stringEntityExchangeResult -> {
                    var responseBody=stringEntityExchangeResult.getResponseBody();
                    System.out.println("responseBody:"+responseBody);

                    var expectedErrorMessage="movieInfo.cast must be present,movieInfo.name must be present,movieInfo.year must be a Positive Value,must not be empty";
                    assert responseBody!=null;
                    assertEquals(expectedErrorMessage,responseBody );
                });
//                .consumeWith(result -> {
//                    var error = result.getResponseBody();
//                    assert  error!=null;
//                    String expectedErrorMessage = "movieInfo.cast must be present,movieInfo.name must be present,movieInfo.year must be a Positive Value";

//                    assertEquals(expectedErrorMessage, error);
//
//                });
    }


}