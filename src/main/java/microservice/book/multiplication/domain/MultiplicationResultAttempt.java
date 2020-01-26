package microservice.book.multiplication.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

/**
 * {@link User}가 {@link Multiplication}을 계산한 답안을 정의한 클래스
 */
@RequiredArgsConstructor
@Getter
@ToString
@EqualsAndHashCode
@Entity
public final class MultiplicationResultAttempt {

	@Id
	@GeneratedValue
	private Long id;

	@ManyToOne(cascade = CascadeType.PERSIST) // save()시에 user 객체도 USER 테이블에 함께 저장된다.
	@JoinColumn(name = "USER_ID") // 이 테이블에서 User 테이블의 참조를 가지는 FK 컬럼명을 지정한다.
	private final User user;

	@ManyToOne(cascade = CascadeType.PERSIST) // save() 시에 multiplication 객체도 MULTIPLICATION 테이블에 함께 저장된다.
	@JoinColumn(name = "MULTIPLICATION_ID")  // 이 테이블에서 Multiplication 테이블의 참조를 가지는 FK 컬럼명을 지정한다.
	private final Multiplication multiplication;

	private final int resultAttempt;

	private final boolean correct;

	// JSON/JPA를 위한 빈 생성자
	MultiplicationResultAttempt() {
		user = null;
		multiplication = null;
		resultAttempt = -1;
		correct = false;
	}
}
