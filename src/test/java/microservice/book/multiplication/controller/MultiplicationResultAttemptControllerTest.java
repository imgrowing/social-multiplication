package microservice.book.multiplication.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import microservice.book.multiplication.controller.MultiplicationResultAttemptController.ResultResponse;
import microservice.book.multiplication.domain.Multiplication;
import microservice.book.multiplication.domain.MultiplicationResultAttempt;
import microservice.book.multiplication.domain.User;
import microservice.book.multiplication.service.MultiplicationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@RunWith(SpringRunner.class)
@WebMvcTest(MultiplicationResultAttemptController.class)
public class MultiplicationResultAttemptControllerTest {

	@MockBean
	private MultiplicationService multiplicationService;

	@Autowired
	private MockMvc mvc;

	private JacksonTester<MultiplicationResultAttempt> requestJson;
	private JacksonTester<ResultResponse> responseJson;

	@Before
	public void setUp() {
		JacksonTester.initFields(this, new ObjectMapper());
	}

	@Test
	public void postResultReturnCorrect() throws Exception {
		boolean correct = true;
		genericParameterizedTest(correct);
	}

	@Test
	public void postResultReturnNotCorrect() throws Exception {
		boolean correct = false;
		genericParameterizedTest(correct);
	}

	private void genericParameterizedTest(boolean correct) throws Exception {
		// given
		// 여기(MVC test)에서는 service 클래스를 테스트하는 것이 아니기 때문에 입력은 무시하고, 결과값만 mocking 한다.
		given(multiplicationService.checkAttempt(any(MultiplicationResultAttempt.class))).willReturn(correct);

		User user = new User("John_doe");
		Multiplication multiplication = new Multiplication(50, 70);
		MultiplicationResultAttempt resultAttempt = new MultiplicationResultAttempt(user, multiplication, 3500);

		// when
		MockHttpServletResponse response = mvc
			.perform(
				post("/results")
					.contentType(MediaType.APPLICATION_JSON_UTF8)		// post JSON으로 보낼 때에는 content type을 반드시 지정해야 한다. 415 Unsupported Media Type 가 발생함.
					.content(requestJson.write(resultAttempt).getJson())
					.accept(MediaType.APPLICATION_JSON_UTF8)
			)
			.andDo(print())
			.andReturn()
			.getResponse();

		// assert
		assertThat(response.getStatus())
			.isEqualTo(HttpStatus.OK.value());
		assertThat(response.getContentAsString())
			.isEqualTo(responseJson.write(new ResultResponse(correct)).getJson());
	}

}