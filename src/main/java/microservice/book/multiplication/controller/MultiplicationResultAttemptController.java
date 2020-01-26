package microservice.book.multiplication.controller;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import microservice.book.multiplication.domain.MultiplicationResultAttempt;
import microservice.book.multiplication.service.MultiplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/results")
final class MultiplicationResultAttemptController {

	private final MultiplicationService multiplicationService;

	@Autowired
	MultiplicationResultAttemptController(final MultiplicationService multiplicationService) {
		this.multiplicationService = multiplicationService;
	}

	@PostMapping
	ResponseEntity<ResultResponse> postResult(@RequestBody MultiplicationResultAttempt multiplicationResultAttempt) {
		return ResponseEntity.ok(
			new ResultResponse(multiplicationService.checkAttempt(multiplicationResultAttempt))
		);
	}


	@RequiredArgsConstructor
	@NoArgsConstructor(force = true) // force true : 모든 final 필드를 초기화(0 / null / false) 한다.
	@Getter
	static final class ResultResponse {
		private final boolean correct;
	}
}
