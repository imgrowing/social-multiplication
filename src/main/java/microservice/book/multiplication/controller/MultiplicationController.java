package microservice.book.multiplication.controller;

import microservice.book.multiplication.domain.Multiplication;
import microservice.book.multiplication.service.MultiplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 곱셈 애플리케이션의 REST API를 구현한 클래스
 */
@RestController // = (@Controller + @ResponseBody). 메소드의 반환값이 응답 본문 자체가 된다.
@RequestMapping("/multiplications")
final class MultiplicationController {

	private final MultiplicationService multiplicationService;

	@Autowired
	public MultiplicationController(final MultiplicationService multiplicationService) {
		this.multiplicationService = multiplicationService;
	}

	@GetMapping("/random")
	Multiplication getRandomMultiplication() {
		return multiplicationService.createRandomMultiplication();
	}
}
