package microservice.book.multiplication.service;

import microservice.book.multiplication.domain.Multiplication;
import microservice.book.multiplication.domain.MultiplicationResultAttempt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
	public boolean checkAttempt(MultiplicationResultAttempt resultAttempt) {
		Multiplication multiplication = resultAttempt.getMultiplication();
		return multiplication.getFactorA() * multiplication.getFactorB() == resultAttempt.getResultAttempt();
	}
}
