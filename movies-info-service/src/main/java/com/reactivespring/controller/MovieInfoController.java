package com.reactivespring.controller;


import com.reactivespring.domain.MovieInfo;

import com.reactivespring.service.MovieInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RestController
@Slf4j
@RequestMapping("/v1")
public class MovieInfoController {

    private MovieInfoService movieInfoService;

    public MovieInfoController(MovieInfoService movieInfoService){
        this.movieInfoService=movieInfoService;
    }

@PostMapping("/movieinfos")
@ResponseStatus(HttpStatus.CREATED)
public Mono<MovieInfo> addMovieInfo(@Valid @RequestBody MovieInfo movieInfo){
System.out.println(movieInfo);
        return movieInfoService.addMovieInfo(movieInfo).log();

}


@GetMapping("/movieinfos")
@ResponseStatus(HttpStatus.FOUND)
    public Flux<MovieInfo> getAllMovieInfo(){
        return movieInfoService.getAllMovieInfo().log();

}

@GetMapping("/movieinfos/{id}")
@ResponseStatus(HttpStatus.FOUND)
public Mono<ResponseEntity<MovieInfo>> getByIdMovieInfo(@PathVariable String id){
        //return movieInfoService.getByIdMovieInfo(id);
    return movieInfoService.getByIdMovieInfo(id)
            .map(movieInfo1 -> ResponseEntity.ok()
                    .body(movieInfo1))
            .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()))
            .log();


}


@PutMapping("/movieinfos/{id}")
@ResponseStatus(HttpStatus.CREATED)
public Mono<ResponseEntity<MovieInfo>> updateMovieInfo(@PathVariable String id
        , @Valid @RequestBody MovieInfo updatedMovieInfo){

    var updatedMovieInfoMono = movieInfoService.updateMovieInfo(id,updatedMovieInfo);

   return updatedMovieInfoMono.map(movieInfo1 -> ResponseEntity.ok().body(movieInfo1))
            .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
}

@DeleteMapping("/movieinfos/{id}")
@ResponseStatus(HttpStatus.OK)
public  Mono<Void> deleteByIdMovieInfo(@PathVariable String id){

        return movieInfoService.deleteByIdMovieInfo(id).log();
}


}
