package jm.tp.jpashop.pt.model;

import lombok.*;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;
import static lombok.AccessLevel.*;

@Entity
@AllArgsConstructor(access = PRIVATE)
@NoArgsConstructor(access = PROTECTED)
@Getter @Setter(PRIVATE) @Builder
public class Delivery {

    @Id @GeneratedValue
    @Column(name = "delivery_id")
    private Long id;

    @OneToOne(mappedBy = "delivery", fetch = LAZY)
    private Order order;

    @Embedded
    private Address address;

    @Enumerated(EnumType.STRING)
    private DeliveryStatus deliveryStatus;

    public void readyToDelivery() {
        setDeliveryStatus(DeliveryStatus.READY);
    }

    public void complete() {
        setDeliveryStatus(DeliveryStatus.COMP);
    }

    public void startDelivery(Order order) {
        setOrder(order);
    }
}
