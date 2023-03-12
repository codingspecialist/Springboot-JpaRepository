package shop.mtcoding.jpastudy.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shop.mtcoding.jpastudy.dto.ResponseDto;
import shop.mtcoding.jpastudy.model.Customer;
import shop.mtcoding.jpastudy.model.CustomerRepository;

import java.util.List;

/**
 * 1. 요청 DTO는 나중에 배움
 * 2. Service도 나중에 배움 (트랜잭션 처리 commit, rollback)
 * 3. 이번장 목표 : Hibernate
 * 4. 숙제 Controller 테스트 코드, Repository 테스트 코드 작성
 */
@RequiredArgsConstructor
@RestController
public class CustomerController {

    private final CustomerRepository customerRepository;

    @GetMapping("/customers/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id){
        Customer customerPS = customerRepository.findById(id).orElseThrow(
                ()-> new RuntimeException("고객을 찾을 수 없어요")
        );
        ResponseDto<?> dto = new ResponseDto<>("한건 조회 성공", customerPS);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @GetMapping("/customers")
    public ResponseEntity<?> findAll(@RequestParam(defaultValue = "0") int page){
        PageRequest pageRequest = PageRequest.of(page, 2);
        Page<Customer> customerListPS = customerRepository.findAll(pageRequest);
        ResponseDto<?> dto = new ResponseDto<>("전체 조회 성공", customerListPS);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @PostMapping("/customers")
    public ResponseEntity<?> save(@RequestBody Customer customer) {
        customerRepository.save(customer);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping("/customers/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Customer customer){
        // 1. 존재 여부 확인 : 최소한의 트랜잭션 발동 and Setter 만들지 않기
        Customer customerPS = customerRepository.findById(id).orElseThrow(
                ()-> new RuntimeException("고객을 찾을 수 없어요")
        );
        
        // 2. 객체 값 변경
        customerPS.update(customer.getName(), customer.getTel());
        
        // 3. 변경된 객체로 DB 수정
        customerRepository.save(customerPS);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/customers/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id){
        // 1. 존재 여부 확인 : 최소한의 DB 트래픽
        Customer customerPS = customerRepository.findById(id).orElseThrow(
                ()-> new RuntimeException("고객을 찾을 수 없어요")
        );
        
        // 2. 존재하면 삭제
        customerRepository.delete(customerPS);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
