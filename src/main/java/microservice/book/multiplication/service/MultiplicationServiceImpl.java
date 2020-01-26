package microservice.book.multiplication.service;

import microservice.book.multiplication.domain.Multiplication;
import microservice.book.multiplication.domain.MultiplicationResultAttempt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
public class MultiplicationServiceImpl implements MultiplicationService {

	private	RandomGeneratorService randomGeneratorService;

	@Autowired
	public MultiplicationServiceImpl(RandomGeneratorService randomGeneratorService) {
		this.randomGeneratorService = randomGeneratorService;
	}

	@Override
	public Multiplication createRandomMultiplication() {
		int factorA = randomGeneratorService.generateRandomFactor();
		int factorB = randomGeneratorService.generateRandomFactor();
		return new Multiplication(factorA, factorB);
	}

	@Override
	public boolean checkAttempt(MultiplicationResultAttempt attempt) {
		// 답안을 채점
		Multiplication multiplication = attempt.getMultiplication();
		boolean correct = multiplication.getFactorA() * multiplication.getFactorB() == attempt.getResultAttempt();

		// 조작된 답안을 방지. request body에 correct 필드가 유입되는 경우에 true가 담겨있을 수 있음(사용자의 조작 행위)
		Assert.isTrue(!attempt.isCorrect(), "채점한 상태로 보낼 수 없습니다!!"); // IllegalArgumentException 발생

		// 복사본을 만들고 correct 필드를 상황에 맞게 설정
		MultiplicationResultAttempt checkedAttempt = new MultiplicationResultAttempt(
			attempt.getUser(), attempt.getMultiplication(), attempt.getResultAttempt(), correct);

		// 결과를 반환
		return correct;
	}
}
