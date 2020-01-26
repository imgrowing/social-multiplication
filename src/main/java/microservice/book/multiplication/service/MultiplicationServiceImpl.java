package microservice.book.multiplication.service;

import microservice.book.multiplication.domain.Multiplication;
import microservice.book.multiplication.domain.MultiplicationResultAttempt;
import microservice.book.multiplication.domain.User;
import microservice.book.multiplication.repository.MultiplicationRepository;
import microservice.book.multiplication.repository.MultiplicationResultAttemptRepository;
import microservice.book.multiplication.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Optional;

@Service
public class MultiplicationServiceImpl implements MultiplicationService {

	private	RandomGeneratorService randomGeneratorService;
	private MultiplicationResultAttemptRepository attemptRepository;
	private MultiplicationRepository multiplicationRepository;
	private UserRepository userRepository;

	@Autowired
	public MultiplicationServiceImpl(
		RandomGeneratorService randomGeneratorService,
		MultiplicationResultAttemptRepository attemptRepository,
		MultiplicationRepository multiplicationRepository,
		UserRepository userRepository) {
		this.randomGeneratorService = randomGeneratorService;
		this.attemptRepository = attemptRepository;
		this.multiplicationRepository = multiplicationRepository;
		this.userRepository = userRepository;
	}

	@Override
	public Multiplication createRandomMultiplication() {
		int factorA = randomGeneratorService.generateRandomFactor();
		int factorB = randomGeneratorService.generateRandomFactor();
		return new Multiplication(factorA, factorB);
	}

	@Transactional
	@Override
	public boolean checkAttempt(MultiplicationResultAttempt attempt) {
		// 해당 닉네임의 사용자가 존재하는지 확인
		Optional<User> user = userRepository.findByAlias(attempt.getUser().getAlias());

		// 해당 multiplication 이 존재하는지 확인
		Optional<Multiplication> savedMultiplication = multiplicationRepository.findByFactorAAndFactorB(attempt.getMultiplication().getFactorA(), attempt.getMultiplication().getFactorB());

		// 조작된 답안을 방지. request body에 isCorrect 필드가 유입되는 경우에 true가 담겨있을 수 있음(사용자의 조작 행위)
		Assert.isTrue(!attempt.isCorrect(), "채점한 상태로 보낼 수 없습니다!!"); // IllegalArgumentException 발생

		// 답안을 채점
		Multiplication multiplication = attempt.getMultiplication();
		boolean isCorrect = (multiplication.getFactorA() * multiplication.getFactorB()) == attempt.getResultAttempt();

		// 복사본을 만들고 isCorrect 필드를 상황에 맞게 설정
		MultiplicationResultAttempt checkedAttempt = new MultiplicationResultAttempt(
			user.orElse(attempt.getUser()), // DB에 존재하면 그 user를 사용하고, 존재하지 않으면 파라미터에서 전달받은 user를 USER 테이블에 새로 저장한다.
			savedMultiplication.orElse(attempt.getMultiplication()),
			attempt.getResultAttempt(),
			isCorrect);

		// 답안을 저장
		attemptRepository.save(checkedAttempt);

		// 결과를 반환
		return isCorrect;
	}

	@Override
	public List<MultiplicationResultAttempt> getStatsForUser(String userAlias) {
		return attemptRepository.findTop5ByUserAliasOrderByIdDesc(userAlias);
	}

}
