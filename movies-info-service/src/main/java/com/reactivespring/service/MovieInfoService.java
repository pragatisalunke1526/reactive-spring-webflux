package com.reactivespring.service;

import com.reactivespring.domain.MovieInfo;
import com.reactivespring.repository.MovieInfoRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class MovieInfoService {

    private MovieInfoRepository movieInfoRepository;

    public MovieInfoService(MovieInfoRepository movieInfoRepository){
        this.movieInfoRepository=movieInfoRepository;
    }

    public Mono<MovieInfo> addMovieInfo(MovieInfo movieInfo) {

        System.out.println(movieInfo);
        return movieInfoRepository.save(movieInfo).log();

    }

    public Flux<MovieInfo> getAllMovieInfo() {
        return movieInfoRepository.findAll();
    }

    public Mono<MovieInfo> getByIdMovieInfo(String id) {
        return movieInfoRepository.findById(id);
    }

    public Mono<MovieInfo> updateMovieInfo(String id, MovieInfo updatedMovieInfo){
      //  return movieInfoRepository.save(updatedMovieInfo);
       return movieInfoRepository
               .findById(id)
                .flatMap(movieInfo -> {
                    movieInfo.setCast(updatedMovieInfo.getCast());
                    movieInfo.setName(updatedMovieInfo.getName());
                    movieInfo.setRelease_date(updatedMovieInfo.getRelease_date());
                    movieInfo.setYear(updatedMovieInfo.getYear());

                    return movieInfoRepository.save(movieInfo);

                });

        
    }

    public Mono<Void> deleteByIdMovieInfo(String id) {
      return    movieInfoRepository.deleteById(id).log();

    }
}
