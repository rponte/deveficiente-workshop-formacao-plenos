package workshop.arquitetura.reviews;

public class ReviewController {

    private final ReviewRepository reviewRepository;
    private final ReviewApprovalGenAISystem approvalService; 

    public ReviewController(ReviewRepository repository) {
        this.reviewRepository = repository;
    }

    @PostMapping("/api/products/{productId}/reviews")
    public ReviewResponse newReview(@PathVariable UUID productId,
                                    @Valid @RequestBody NewReviewRequest request) {

        // 1. Persists the review in DynamoDB with status=PENDING
        Review pendingReview = request.toModel(productId);
        reviewRepository.save(pendingReview);

        // 2. Uses a LLM to approve or reject the review
        try {
            ApprovalResponse approval = approvalService.approve(pendingReview);
            if (approval.wasRejected()) {
                throw new ReviewNotApprovedException("Review was rejected by our Advanced GenAI Reviewer System: " + approval.getMessage());
            }

            // 3. If approved, updates the review in the database
            pendingReview.markAsApproved();
            reviewRepository.save(pendingReview);

            return new NewReviewResponse(pendingReview.getId(), pendingReview.getStatus());
            
        } catch(Exception e) {
            // 4. In case of errors, deletes the pending review
            reviewRepository.delete(pendingReview);
        }
        
        // 5. Sorry, your review was rejected
        return new NewReviewResponse(pendingReview.getId(), ReviewStatus.REJECTED); 
    }

}

/**
 * Request and Response payloads
 */

record NewReviewRequest(
    @NotNull UUID userId,
    @Min(1) @Max(5) double rating;
    @NotBlank @Size(max=1024) String comment
) {

    public Review toModel(UUID productId) {
        return new Review(
            productId, 
            this.userId, 
            this.rating,
            this.comment,
            ReviewStatus.PENDING
        );
    }

}

record NewReviewResponse(
    UUID id,
    ReviewStatus status
){}
