package com.reactivespring.handler;

import com.reactivespring.domain.Review;
import com.reactivespring.exception.ReviewDataException;
import com.reactivespring.repository.ReviewReactiveRepository;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import javax.validation.Validator;
import javax.validation.ConstraintViolation;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ReviewsHandler {


    private ReviewReactiveRepository reviewReactiveRepository;

    public ReviewsHandler(ReviewReactiveRepository reviewReactiveRepository){
        this.reviewReactiveRepository=reviewReactiveRepository;
    }

    @Autowired
    private Validator validator;



    public Mono<ServerResponse> addReview(ServerRequest request){


        return  request.bodyToMono(Review.class)
                .doOnNext(this::validate)
                .flatMap(reviewReactiveRepository::save)
                .flatMap(ServerResponse.status(HttpStatus.CREATED)::bodyValue);
    }

    private void validate(Review review) {
        var constraintViolations = validator.validate(review);
        log.info("constraintViolations : {} ", constraintViolations);
        if (constraintViolations.size() > 0) {
            var errorMessage = constraintViolations
                    .stream()
                    .map(ConstraintViolation::getMessage)
                    .sorted()
                    .collect(Collectors.joining(","));

            throw new ReviewDataException(errorMessage);
        }
    }


    public Mono<ServerResponse> getReviews(ServerRequest serverRequest) {

        var movieInfoId=serverRequest.queryParam("movieInfoId");
        if(movieInfoId.isPresent()){
            var reviewsFlux =reviewReactiveRepository.findReviewsByMovieInfoId(Long.valueOf(movieInfoId.get()));
            return buildReviewResponse(reviewsFlux);
        }
        else {
        var reviewsFlux = reviewReactiveRepository.findAll();
        return buildReviewResponse(reviewsFlux);}
    }

    private Mono<ServerResponse> buildReviewResponse(Flux<Review> reviewsFlux) {
        return ServerResponse.ok().body(reviewsFlux, Review.class);
    }

    public Mono<ServerResponse> updateReview(ServerRequest serverRequest) {
        var reviewId = serverRequest.pathVariable("id");
        var existingReview = reviewReactiveRepository.findById(reviewId)
                .switchIfEmpty(Mono.error(
                        new RuntimeException("Review not found for the given RewiewId"+reviewId)));

        return existingReview.flatMap(review -> serverRequest.bodyToMono(Review.class)
                .map(reqReview -> {
                    review.setComment(reqReview.getComment());
                    review.setRating(reqReview.getRating());
                    return review;
                })
                .flatMap(reviewReactiveRepository::save)
                .flatMap(savedReview ->
                        ServerResponse.status(HttpStatus.OK)
                                .bodyValue(savedReview)))
                .switchIfEmpty(ServerResponse.notFound().build());


    }

    public Mono<ServerResponse> deleteReview(ServerRequest serverRequest) {
        var reviewId = serverRequest.pathVariable("id");
        var existingReview = reviewReactiveRepository.findById(reviewId);
             return existingReview
                      .flatMap(review -> reviewReactiveRepository.deleteById(reviewId)
                .then(ServerResponse.noContent().build()));
    }


}


