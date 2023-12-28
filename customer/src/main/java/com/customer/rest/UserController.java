package com.customer.rest;

import java.lang.reflect.Type;
import java.util.List;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.customer.config.IChannel;
import com.customer.model.User;
import com.customer.model.UserPointDTO;
import com.customer.service.UserService;
import com.customer.model.ResultVO;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.customer.model.ChannelRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/*
 * springdoc-openapi
 * https://springdoc.org/#migrating-from-springfox
 */

@Tag(name="Customer service API", description="Customer service API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {
	
	@Autowired
	private final UserService userService;

	@GetMapping("/userbyid/{userId}")
	@Operation(summary="아이디로 사용자 정보 가져오기 ")
	@Parameters({
		@Parameter(name="userId", in=ParameterIn.PATH, description="", required=true, allowEmptyValue=false) 
	})
	public ResponseEntity<User> getUserById(@PathVariable (name="userId", required = true) String userId) {
		return userService.getUserById(userId);
	}
	
	@GetMapping("/users/{userId}")
	@Operation(summary="아이디로 사용자 정보 가져오기 ")
	@Parameters({
		@Parameter(name="userId", in=ParameterIn.PATH, description="", required=true, allowEmptyValue=false) 
	})
	public ResponseEntity<ResultVO<User>> get(@PathVariable (name="userId", required = true) String userId) {
		return userService.get(userId);
	}
	
	@PostMapping("/users")
	@Operation(summary="사용자 정보 등록하기 ")
	public ResponseEntity <String > setUserInsert(
			@RequestBody User user
			) throws Exception { 

		return userService.setUserInsert(user);
	}
	@GetMapping(value="/users", produces = "application/json")	
	@Operation(operationId="users", summary="사용자 정보 가져오기", description="사용자 정보를 제공합니다.")
	public ResponseEntity <List<User>> getUserList() { 
		return userService.getUserList();
	}	
	@PutMapping("/users/{userId}")
	@Operation(summary="사용자 정보 변경하기 ")	
	public ResponseEntity <String > setUserUpdate(
			@PathVariable(name="userId",required = true ) String userId, 
			@RequestBody User user
			) throws Exception { 

		return userService.setUserUpdate(userId, user);
	}
	@DeleteMapping("/users/{userId}")
	@Operation(summary="사용자 정보 삭제하기 ")
	public ResponseEntity <String > setUserDelete(
			@PathVariable(name="userId",required = true ) String userId
			) throws Exception { 

		return userService.setUserDelete(userId);
	}
}
