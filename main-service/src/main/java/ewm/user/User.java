package ewm.user;

import ewm.subscription.model.Subscription;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Data
@Entity
@Table(name = "users")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
//@NamedEntityGraph(name = "user.subscriptions", attributeNodes = {
//        @NamedAttributeNode("subscriptions"),
//        @NamedAttributeNode("followers")
//})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "email")
    @Size(min = 6, max = 254)
    private String email;

    @Column(name = "name")
    @Size(min = 2, max = 250)
    private String name;

    @OneToOne(mappedBy = "owner", cascade = CascadeType.ALL)
    @JoinColumn(name = "subscription_id")
    private Subscription subscription;


}
