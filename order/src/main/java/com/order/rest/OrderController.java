package com.order.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.order.model.*;
import com.order.config.IChannel;
import com.order.service.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Tag(name="Order service API", description="Order service API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class OrderController {
	private final Logger log = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private OrderService orderService;

	@Autowired
	private OrderManager orderManager;

	@Bean
	Queue responseQueue() {
		return new Queue(IChannel.CH_ORDER_RESPONSE, false);
	}

	@GetMapping("/orders/{orderId}")
	@Operation(summary="주문정보 조회")
	@Parameters({
		@Parameter(name="orderId", in=ParameterIn.PATH, description="", required=true, allowEmptyValue=false) 
	})
	public Mono<ResultVO> getState(@PathVariable (name="orderId", required = true) String orderId) {
		return orderService.getState(orderId);
	}

	@PostMapping("/orders")
	@Operation(summary="상품을 주문합니다")
	public Mono<ResultVO<ResponseOrderDTO>> order(@RequestBody RequestOrderDTO order) {
		return orderService.order(order);
	}

	/**
	 * payment, delivery 서비스 응답을 수신한다.
	 * 
	 * @param consumerRecord
	 */
	@RabbitListener(queues = IChannel.CH_ORDER_RESPONSE)
	public void receiveResponse(String payload) {
		log.info("[@.@ ORDER RECEIVED] " + payload.toString());

		Gson gson = new Gson();
		JsonObject jsonObj = gson.fromJson(payload, JsonElement.class).getAsJsonObject();
		String trxId = jsonObj.get("trxId").getAsString();
		String type = jsonObj.get("messageType").getAsString();
		
		try {
			ResultListener listener = orderManager.getOrder(trxId);
			
			if ( listener != null ) {
				log.info("#### ["+type+"] Find Result listener object for trxId-"+trxId);
				listener.fireData(jsonObj);
			} else {
				log.info("#### ["+type+"] CAN't find Result listener object for trxId-"+trxId);
			}

		}catch(Exception e) {
			e.printStackTrace();
		}

	}
}
