package ewm.subscription.model;

import ewm.subscription.SubscriptionStatus;
import ewm.user.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Table(name = "subscription_requests")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subscription_request_id")
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "requester_id")
    private User requester;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_to_follow_id")
    private User userToFollow;

    @Column(name = "created_on")
    private LocalDateTime createdOn;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private SubscriptionStatus status;

}
