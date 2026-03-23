package io.github.nguyennhatquang.fashion.Identity.usecase;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;



import io.github.nguyennhatquang.fashion.Identity.delivery.dto.login.LoginRequest;
import io.github.nguyennhatquang.fashion.Identity.usecase.strategyLogin.LoginStrategyFactory;
import io.github.nguyennhatquang.fashion.common.infrastructure.auth.AuthResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LoginUseCase {

    private final LoginStrategyFactory loginFactory;

    public AuthResponse execute(LoginRequest.Command request) {
        // 1. Lấy đúng Strategy dựa trên request.loginType()
        ILoginStrategy strategy = loginFactory.getStrategy(request.loginType());

        // 2. Thực thi đăng nhập
        return strategy.authenticate(request);
    }

}
