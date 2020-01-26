package microservice.book.multiplication.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import microservice.book.multiplication.domain.Multiplication;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@RunWith(SpringRunner.class)
@WebMvcTest(MultiplicationController.class) // 스프링 웹 애플리케이션(MVC만) 컨텍스트를 초기화 한다. MockMvc bean도 불러온다.
public class MultiplicationControllerTest {

	@MockBean // mock 객체를 생성하여 Spring ApplicationContext에 bean을 등록한다.
	private MultiplicationService multiplicationService;

	@Autowired
	private MockMvc mvc;

	// 이 객체는 JacksonTester.initFields(testObject, objectMapper) 메서드를 이용해 자동으로 초기화한다.
	private JacksonTester<Multiplication> json;

	@Before
	public void setUp() {
		JacksonTester.initFields(this, new ObjectMapper());
	}

	@Test
	public void getRandomMultiplicationTest() throws Exception {
		// given
		given(multiplicationService.createRandomMultiplication())
			.willReturn(new Multiplication(70, 20));

		// when
		MockHttpServletResponse response = mvc
			.perform(
				get("/multiplications/random")
					.accept(MediaType.APPLICATION_JSON_UTF8)
			)
			.andDo(print())
			.andReturn()
			.getResponse();

		// then
		assertThat(response.getStatus())
			.isEqualTo(HttpStatus.OK.value());
		assertThat(response.getContentAsString())
			.isEqualTo(
				json.write(new Multiplication(70, 20)).getJson()
			);
	}
}