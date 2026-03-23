package io.github.nguyennhatquang.fashion.Identity.domain.handler;

import io.github.nguyennhatquang.fashion.Identity.usecase.RegisterUseCase;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import io.github.nguyennhatquang.fashion.Identity.delivery.dto.register.RegisterRequest;
import io.github.nguyennhatquang.fashion.Identity.domain.entity.UserProfile;
import io.github.nguyennhatquang.fashion.Identity.usecase.IRegisterUseCase;
import io.github.nguyennhatquang.fashion.common.response.Result;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class RegisterHandler {
    private final IRegisterUseCase registerService;
}
