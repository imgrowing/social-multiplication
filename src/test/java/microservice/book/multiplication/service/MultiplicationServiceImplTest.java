package microservice.book.multiplication.service;

import microservice.book.multiplication.domain.Multiplication;
import microservice.book.multiplication.domain.MultiplicationResultAttempt;
import microservice.book.multiplication.domain.User;
import microservice.book.multiplication.repository.MultiplicationRepository;
import microservice.book.multiplication.repository.MultiplicationResultAttemptRepository;
import microservice.book.multiplication.repository.UserRepository;
import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

// SpringBootTest를 사용하지 않는 테스트
public class MultiplicationServiceImplTest {

	private MultiplicationServiceImpl multiplicationServiceImpl;

	@Mock
	private RandomGeneratorService randomGeneratorService;

	@Mock
	private MultiplicationResultAttemptRepository attemptRepository;

	@Mock
	private MultiplicationRepository multiplicationRepository;

	@Mock
	private UserRepository userRepository;

	@Before
	public void setUp() {
		//  이 테스트 클래스에서 @Mock annotation 붙은 필드를 mock 객체로 초기화 합니다.
		//  annotation : @org.mockito.Mock, @Spy, @Captor, @InjectMocks
		MockitoAnnotations.initMocks(this); // randomGeneratorService 가 mock 객체로 초기화 됨
		multiplicationServiceImpl = new MultiplicationServiceImpl(randomGeneratorService, attemptRepository, multiplicationRepository, userRepository);
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
		MultiplicationResultAttempt attempt = new MultiplicationResultAttempt(user, multiplication, 3000, false); // 파라미터로 전달되는 attempt 객체의 correct는 무조건 false 이다.
		MultiplicationResultAttempt verifiedAttempt = new MultiplicationResultAttempt(user, multiplication, 3000, true);
		given(userRepository.findByAlias("John_doe")).willReturn(Optional.empty());
		given(multiplicationRepository.findByFactorAAndFactorB(50, 60)).willReturn(Optional.empty());

		// when
		boolean attemptResult = multiplicationServiceImpl.checkAttempt(attempt);

		// assert
		assertThat(attemptResult).isTrue();
		verify(attemptRepository).save(verifiedAttempt);
	}

	@Test
	public void checkWrongAttemptTest() {
		// given
		Multiplication multiplication = new Multiplication(50, 60);
		User user = new User("John_doe");
		MultiplicationResultAttempt attempt = new MultiplicationResultAttempt(user, multiplication, 3010, false);
		given(userRepository.findByAlias("John_doe")).willReturn(Optional.empty());
		given(multiplicationRepository.findByFactorAAndFactorB(50, 60)).willReturn(Optional.empty());

		// when
		boolean attemptResult = multiplicationServiceImpl.checkAttempt(attempt);

		// assert
		assertThat(attemptResult).isFalse();
		verify(attemptRepository).save(attempt);
	}

	@Test
	public void retrieveStatsTest() {
		// given
		Multiplication multiplication = new Multiplication(50, 60);
		User user = new User("John_doe");
		MultiplicationResultAttempt attempt1 = new MultiplicationResultAttempt(user, multiplication, 3010, false);
		MultiplicationResultAttempt attempt2 = new MultiplicationResultAttempt(user, multiplication, 3051, false);
		List<MultiplicationResultAttempt> latestAttempts = Lists.newArrayList(attempt1, attempt2);
		given(userRepository.findByAlias("John_doe")).willReturn(Optional.empty());
		given(attemptRepository.findTop5ByUserAliasOrderByIdDesc("John_doe")).willReturn(latestAttempts);

		// when
		List<MultiplicationResultAttempt> latestAttemptResult = multiplicationServiceImpl.getStatsForUser("John_doe");

		// then
		assertThat(latestAttemptResult).isEqualTo(latestAttemptResult);
	}
}