package com.example.Smart_Parking.Controller;

import com.example.Smart_Parking.DTO.PaymentDTO;
import com.example.Smart_Parking.Model.*;
import com.example.Smart_Parking.Repository.PaymentRepository;
import com.example.Smart_Parking.Repository.ReserveRepository;
import com.example.Smart_Parking.Repository.UserRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;

@Controller
@RequestMapping("/Payment")
@CrossOrigin(origins = "*")
public class PaymentController {

    @Autowired private PaymentRepository paymentRepo;
    @Autowired private UserRepository userRepo;
    @Autowired private ReserveRepository reserveRepo;

    @Value("${stripe.success.url}")
    private String successUrl;

    @Value("${stripe.cancel.url}")
    private String cancelUrl;

    // ----------------------------
    // SHOW PAYMENT PAGE (YOUR FORM)
    // ----------------------------
    @GetMapping
    public String showPaymentForm(@RequestParam("userId") Long userId,
                                  @RequestParam("reserveId") Long reserveId,
                                  Model model) {

        Optional<Reserve> reserveOpt = reserveRepo.findById(reserveId);
        if (reserveOpt.isEmpty()) {
            model.addAttribute("error", "Invalid Reservation.");
            return "error";
        }

        Reserve reserve = reserveOpt.get();
        int duration = reserve.getDurationHours();
        double amount = duration * 100.0;  // ₹100 per hour

        PaymentDTO payment = new PaymentDTO();
        payment.setUserId(userId);
        payment.setReserveId(reserveId);
        payment.setAmount(amount);

        model.addAttribute("payment", payment);
        return "Payment";  // Payment.html
    }

    // ----------------------------
    // CREATE STRIPE CHECKOUT SESSION
    // ----------------------------
    @PostMapping("/make")
    public String redirectStripe(@ModelAttribute PaymentDTO dto,
                                 Model model,
                                 HttpSession session) {   // <-- ADD THIS
        try {
            // STORE IDs IN SESSION BEFORE REDIRECTING
            session.setAttribute("reserveId", dto.getReserveId());
            session.setAttribute("userId", dto.getUserId());

            SessionCreateParams params =
                    SessionCreateParams.builder()
                            .setMode(SessionCreateParams.Mode.PAYMENT)
                            .setSuccessUrl(successUrl + "?session_id={CHECKOUT_SESSION_ID}")
                            .setCancelUrl(cancelUrl)
                            .addLineItem(
                                    SessionCreateParams.LineItem.builder()
                                            .setQuantity(1L)
                                            .setPriceData(
                                                    SessionCreateParams.LineItem.PriceData.builder()
                                                            .setCurrency("inr")
                                                            .setUnitAmount((long) (dto.getAmount() * 100))
                                                            .setProductData(
                                                                    SessionCreateParams.LineItem.PriceData.ProductData
                                                                            .builder()
                                                                            .setName("Smart Parking Fee")
                                                                            .build()
                                                            )
                                                            .build()
                                            )
                                            .build()
                            )
                            .build();

            Session stripeSession = Session.create(params);

            return "redirect:" + stripeSession.getUrl();

        } catch (Exception e) {
            model.addAttribute("error", "Stripe error: " + e.getMessage());
            model.addAttribute("payment", dto);  // prevents Thymeleaf crash
            return "Payment";
        }
    }


    // ----------------------------
    // STRIPE SUCCESS REDIRECT HANDLER
    // ----------------------------
    @GetMapping("/success")
    public String paymentSuccess(@RequestParam("session_id") String sessionId, HttpSession session) throws StripeException {

        Session sessionObj = Session.retrieve(sessionId);

        Long reserveId = (Long) session.getAttribute("reserveId");
        Long userId = (Long) session.getAttribute("userId");

        Optional<User> user = userRepo.findById(userId);
        Optional<Reserve> reserve = reserveRepo.findById(reserveId);

        Payment payment = new Payment();
        payment.setUser(user.get());
        payment.setReservation(reserve.get());
        payment.setAmount((double) sessionObj.getAmountTotal() / 100);
        payment.setMethod(PaymentMethod.CARD);
        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setPaymentDate(LocalDateTime.now());

        Payment saved = paymentRepo.save(payment);

        return "redirect:/Payment/success/" + saved.getPayment_id();
    }

}
