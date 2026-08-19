package org.workswap.statistic.consumers;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.workswap.listing.datasource.model.Listing;
import org.workswap.listing.datasource.repository.ListingRepository;
import org.workswap.shared.events.review.ReviewCreatedEvent;
import org.workswap.statistic.services.StatisticCommandService;
import org.workswap.user.datasource.model.User;
import org.workswap.user.datasource.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewConsumer {
    
    private final ListingRepository listingRepository;
    private final UserRepository userRepository;
    private final StatisticCommandService statisticCommandService;

    @RabbitListener(queues = "reviewsQueue")
    public void reviewsQueue(ReviewCreatedEvent event) {
        Listing listing = listingRepository.findById(event.listingId()).orElse(null);
        User profile = userRepository.findById(event.profileId()).orElse(null);

        if (listing != null) {
            statisticCommandService.updateRatingForListing(listing);
        } else if (profile != null) {
            statisticCommandService.updateRatingForUser(profile);
        }
    }
}
