package workshop.arquitetura.reviews;

public class ReviewController {

    private final ReviewRepository reviewRepository;
    private final ReviewApprovalGenAISystem approvalService; 

    ReviewController(ReviewRepository repository) {
        this.reviewRepository = repository;
    }

    @PostMapping("/api/products/{productId}/reviews")
    public ReviewResponse newReview(@PathVariable UUID productId,
                            @Valid @RequestBody NewReviewRequest request) {

        // 1. Persists the review in DynamoDB with status=PENDING
        Review pendingReview = request.toModel(productId);
        var pendingReview = reviewRepository.save(pendingReview);

        // 2. Uses a LLM to approve or reject the review
        try {
            ApprovalResponse approval = approvalService.approve(pendingReview);
            if (approval.wasApproved()) {
                pendingReview.setStatus(ReviewStatus.APPROVED);
                reviewRepository.save(pendingReview);
            } else {
                throw new ReviewNotApprovedException("Review was rejected by our Advanced GenAI Reviewer System: " + approval.getMessage());
            }
        } catch(Exception e) {
            // 3. In case of errors, deletes the pending review
            reviewRepository.delete(pendingReview);
        } finally {
            // 4. Returns the review to frontend
            return new NewReviewResponse(pendingReview.getId(), pendingReview.getStatus());
        }
    }

}

/**
 * Request and Response payloads
 */

record NewReviewRequest(
    UUID userId,
    String reviewText
) {

    public Review toModel(UUID productId) {
        return new Review(
            productId, 
            this.userId, 
            this.reviewText,
            ReviewStatus.PENDING
        );
    }

}

record NewReviewResponse(
    UUID id,
    ReviewStatus status
){}
