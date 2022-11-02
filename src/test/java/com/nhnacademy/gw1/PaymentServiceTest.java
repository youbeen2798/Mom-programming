package com.nhnacademy.gw1;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class PaymentServiceTest {
    // SUT
    PaymentService service;
    // DOC
    CustomerRepository repository;

    @BeforeEach
    void setUp() {
        repository = mock(CustomerRepository.class);

        service = new PaymentService(repository);
    }

    @Test
    void pay_notFoundCustomer_thenThrowCustomerNotFoundException() {
        long amount = 10_000L;
        Long customerId = 3423432L;

        when(repository.findById(customerId)).thenReturn(null);

        assertThatThrownBy(() -> service.pay(amount, customerId))
                .isInstanceOf(CustomerNotFoundException.class)
                .hasMessageContaining("Not found customer", customerId.toString());
    }

    @Test
    void pay_invalidAmount_thenThrowInvalidAmountException() {
        // invalidAmount : 음수의 금액
        // 음수의 금액이 들어갔을 때, service에서 throwInvalidAmoundException
        long amount = -10L;
        Long customerId = 3423432L;
        Customer customer = new Customer(customerId);
        when(repository.findById(customerId)).thenReturn(customer);

        assertThatThrownBy(() -> service.pay(amount, customerId))
                .isInstanceOf(InvalidAmountException.class)
                .hasMessageContaining("Invalid", customerId.toString());
    }


    @Test
    void pay_validAmount() {
        // 유효한 결제금액 결제 시
        long amount = 10_000L;
        Long customerId = 3423432L;
        Customer customer = new Customer(customerId);

        when(repository.findById(customerId)).thenReturn(customer);

        assertThatCode(() -> service.pay(amount, customerId)).doesNotThrowAnyException();
    }

    @Test
    void pay_success_receipt_addPoint() {
        //할인율이 영수증에 제대로 들어갔는지
        long amount = 10_000L;
        Long customerId = 3423432L;

        Customer customer = new Customer(customerId);

        when(repository.findById(customerId)).thenReturn(customer);

        // 결제 시 amount에서 적립률만큼 customer의 point에 쌓임.
        Receipt receipt = service.pay(amount, customerId);

        //receipt 속의 전체 금액 == amount * 할인율 곱한게 같은지
        assertThat(amount *service.getPointRate() == receipt.getPoint());
    }

    @Test
    public void pay_success_check_pointRate(){
        // 금액에 따라 적립금이 차등 적용되는지
        long amount1 = 10_000L;
        Long customerId1 = 3423432L;

        Customer customer1 = new Customer(customerId1);
        when(repository.findById(customerId1)).thenReturn(customer1);

        Receipt receipt1 = service.pay(amount1, customerId1);
        //amount에 따라서 할인율을 다르게 적용 setPointRate를 통해서

        long amount2 = 50_000;
        Long customerId2 = 123123L;
        Customer customer2 = new Customer(customerId2);
        when(repository.findById(customerId2)).thenReturn(customer2);

        Receipt receipt2 = service.pay(amount2, customerId2);


        assertAll("test",
            () -> assertEquals(0.1, receipt1.getPointRate()),
            () -> assertEquals(0.5, receipt2.getPointRate())
        );
    }

    @Test
    public void pay_success_customer_addPoint_oneTime(){
        //적립금이 고객의 계정에 제대로 들어갔는지
        long amount = 10_000L;
        Long customerId = 3423432L;

        Customer customer = new Customer(customerId);
        when(repository.findById(customerId)).thenReturn(customer);

        Receipt receipt = service.pay(amount, customerId);
        assertThat(customer.getPoint()).isEqualTo((long) (amount * service.getPointRate()));

        //고객의 영수증에 point를 넣어줌
        //총 적립금을 제대로 계산이 됬는지(0원 적립도 체크)
        //addPoint를 했을 때 현재 적립금이 제대로 들어갔는지 확인
    }

    @Test
    public void pay_success_customer_addPoint_twoTime(){
        // 다중 결제시 적립금이 고객의 계정에 제대로 들어갔는지
        long amount = 10_000L;
        Long customerId = 3423432L;

        Customer customer = new Customer(customerId);
        when(repository.findById(customerId)).thenReturn(customer);

        Receipt receipt1 = service.pay(amount, customerId);
        Receipt receipt2 = service.pay(amount, customerId);

        assertThat(customer.getPoint()).isEqualTo(receipt1.getPoint() + receipt2.getPoint());
    }

    @Test
    void pay_fail_if_overAmount() {
        Long customerId = 3423432L;

        Customer customer = new Customer(customerId);
        customer.
    }
}