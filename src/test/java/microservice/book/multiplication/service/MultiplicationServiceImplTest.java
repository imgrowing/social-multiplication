package microservice.book.multiplication.service;

import microservice.book.multiplication.domain.Multiplication;
import microservice.book.multiplication.domain.MultiplicationResultAttempt;
import microservice.book.multiplication.domain.User;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

// SpringBootTest를 사용하지 않는 테스트
public class MultiplicationServiceImplTest {

	private MultiplicationServiceImpl multiplicationServiceImpl;

	@Mock
	private RandomGeneratorService randomGeneratorService;

	@Before
	public void setUp() {
		//  이 테스트 클래스에서 @Mock annotation 붙은 필드를 mock 객체로 초기화 합니다.
		//  annotation : @org.mockito.Mock, @Spy, @Captor, @InjectMocks
		MockitoAnnotations.initMocks(this); // randomGeneratorService 가 mock 객체로 초기화 됨
		multiplicationServiceImpl = new MultiplicationServiceImpl(randomGeneratorService);
	}

	@Test
	public void createRandomMultiplicationTest() {
		// given (목 객체가 처음에 50, 나중에 30을 반환하도록 설정)
		given(randomGeneratorService.generateRandomFactor()).willReturn(50, 30);

		// when
		Multiplication multiplication = multiplicationServiceImpl.createRandomMultiplication();

		// assert
		assertThat(multiplication.getFactorA()).isEqualTo(50);
		assertThat(multiplication.getFactorB()).isEqualTo(30);
//		assertThat(multiplication.getResult()).isEqualTo(1500);
	}

	@Test
	public void checkCorrectAttemptTest() {
		// given
		Multiplication multiplication = new Multiplication(50, 60);
		User user = new User("John_doe");
		MultiplicationResultAttempt resultAttempt = new MultiplicationResultAttempt(user, multiplication, 3000);

		// when
		boolean attemptResult = multiplicationServiceImpl.checkAttempt(resultAttempt);

		// assert
		assertThat(attemptResult).isTrue();
	}

	@Test
	public void checkWrongAttemptTest() {
		// given
		Multiplication multiplication = new Multiplication(50, 60);
		User user = new User("John_doe");
		MultiplicationResultAttempt resultAttempt = new MultiplicationResultAttempt(user, multiplication, 3010);

		// when
		boolean attemptResult = multiplicationServiceImpl.checkAttempt(resultAttempt);

		// assert
		assertThat(attemptResult).isFalse();
	}
}